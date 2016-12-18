package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
//@Component @Service @Dao @Controller
@Service
public class SeckillServiceImpl implements SeckillService{
	//日志
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	//mybatis中的mapper进行初始化，帮我们加入到容器当中
	//注入Service依赖,使用@Autowired注释，还有一些比如说@Resource,@Inject
	//而不用自己去new一个实现类
	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private SuccessKilledDao successKilledDao;

	//将redisDao注入到service当中
	@Autowired
	private RedisDao redisDao;

	//md5盐值字符串，用于混淆MD5
	private final String slat = "LD3-2#$!$dWJ'sfj_(*_$%@#w~!@!#*&(*&%#$%@&";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	//获得秒杀接口
	public Exposer exportSeckillUrl(long seckillId) {
		/**
		 * get from cache
		 * if null
		 *  get db
		 *   else
		 *    put cache
		 *    	locgoin
		 */
		//优化点:缓存优化,建立在超时的基础上进行一致性维护
		//1.访问redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill==null){
			//2.如果缓存当中没有，就进入第二步，访问数据库
			seckill = seckillDao.queryById(seckillId);
			//如果数据库当中也没有，返回false秒杀但不存在
			if(seckill==null){
				return new Exposer(false,seckillId);
			}else{
				//3.如果存在，放入redis
				redisDao.putSeckill(seckill);
			}
		}


		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//获得当前系统的时间
		Date nowTime = new Date();
		if(nowTime.getTime() < startTime.getTime() || 
				nowTime.getTime() > endTime.getTime()){
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		//转化特定字符串的过程，不可逆
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	/**
	 * 使用注解控制事务方法的优点：
	 * 1.开发团队达成一致约定，明确标注事务方法的编程风格。
	 * 2.保证事务方法的执行时间尽可能短，不要穿插其它的网络操作，
	 * 如缓存、cache等RPC/HTTP请求或者剥离到事务方法外边。
	 * 3.不是所有的方法都需要事务，比如只有一条修改操作，
	 * 只读操作不需要事务控制，当有两条以上的事务需要完成。
	 */
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone,
			String md5) throws SeckillException, RepeatKillException,
			SeckillCloseException {
		//结果不匹配
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite");
		}
		//执行秒杀逻辑:1.减库存+2.记录秒杀行为
		Date nowTime = new Date();

		try {
			//记录购买行为
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			//唯一:seckillId, userPhone
			if(insertCount <= 0){
				//重复秒杀
				throw new RepeatKillException("seckill repeated");
			}else{
				//减库存,热点商品的竞争,这一步才涉及到mysql的行级锁
				int upDateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if(upDateCount <= 0){
					//没有更新到数据库,秒杀结束,rollback
					throw new SeckillCloseException("seckill is closed");
				}else{
					//秒杀成功 commit
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
				}
			}
		}catch(SeckillCloseException e1){
			throw e1;
		}catch(RepeatKillException e2){
			throw e2;
		}catch (Exception e) {
			//超时或者是数据库断了等等异常需要括起来
			logger.error(e.getMessage(),e);
			//所有编译期异常转化成运行期异常，一旦try中有错，spring会帮忙回滚，保证秒杀的原子性（库存和成功购买记录）
			throw new SeckillException("seckill inner error:"+e.getMessage());
		}
	}

	//为什么把MD5方法单独写出来呢，因为executeSeckill也需要调用MD5方法与生成的做对比
	private String getMD5(long seckillId){
		//通过盐值和规则来生成MD5
		String base = seckillId + "/" + slat; 
		//MD5工具类，专门用来生成MD5，实现加密，并且是不可逆的，如果在不知道盐值的情况下
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	public SeckillExecution executeSeckillProcedure(long seckillId,
			long userPhone, String md5){
		// 开发存储过程的秒杀逻辑
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//执行存储过程，result被赋值
		try {
			seckillDao.killByProcedure(map);
			//获取result,需要在pom.xml中进行配置
			int result = MapUtils.getInteger(map,"result",-2);
			if(result==1){
				SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,sk);
			}else{
				return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
		}
	}
}
