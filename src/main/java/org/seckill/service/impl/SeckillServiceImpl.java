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
	//��־
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	//mybatis�е�mapper���г�ʼ���������Ǽ��뵽��������
	//ע��Service����,ʹ��@Autowiredע�ͣ�����һЩ����˵@Resource,@Inject
	//�������Լ�ȥnewһ��ʵ����
	@Autowired
	private SeckillDao seckillDao;

	@Autowired
	private SuccessKilledDao successKilledDao;

	//��redisDaoע�뵽service����
	@Autowired
	private RedisDao redisDao;

	//md5��ֵ�ַ��������ڻ���MD5
	private final String slat = "LD3-2#$!$dWJ'sfj_(*_$%@#w~!@!#*&(*&%#$%@&";

	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	public Seckill getById(long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	//�����ɱ�ӿ�
	public Exposer exportSeckillUrl(long seckillId) {
		/**
		 * get from cache
		 * if null
		 *  get db
		 *   else
		 *    put cache
		 *    	locgoin
		 */
		//�Ż���:�����Ż�,�����ڳ�ʱ�Ļ����Ͻ���һ����ά��
		//1.����redis
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill==null){
			//2.������浱��û�У��ͽ���ڶ������������ݿ�
			seckill = seckillDao.queryById(seckillId);
			//������ݿ⵱��Ҳû�У�����false��ɱ��������
			if(seckill==null){
				return new Exposer(false,seckillId);
			}else{
				//3.������ڣ�����redis
				redisDao.putSeckill(seckill);
			}
		}


		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		//��õ�ǰϵͳ��ʱ��
		Date nowTime = new Date();
		if(nowTime.getTime() < startTime.getTime() || 
				nowTime.getTime() > endTime.getTime()){
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		//ת���ض��ַ����Ĺ��̣�������
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	/**
	 * ʹ��ע��������񷽷����ŵ㣺
	 * 1.�����ŶӴ��һ��Լ������ȷ��ע���񷽷��ı�̷��
	 * 2.��֤���񷽷���ִ��ʱ�価���̣ܶ���Ҫ�������������������
	 * �绺�桢cache��RPC/HTTP������߰��뵽���񷽷���ߡ�
	 * 3.�������еķ�������Ҫ���񣬱���ֻ��һ���޸Ĳ�����
	 * ֻ����������Ҫ������ƣ������������ϵ�������Ҫ��ɡ�
	 */
	@Transactional
	public SeckillExecution executeSeckill(long seckillId, long userPhone,
			String md5) throws SeckillException, RepeatKillException,
			SeckillCloseException {
		//�����ƥ��
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			throw new SeckillException("seckill data rewrite");
		}
		//ִ����ɱ�߼�:1.�����+2.��¼��ɱ��Ϊ
		Date nowTime = new Date();

		try {
			//��¼������Ϊ
			int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
			//Ψһ:seckillId, userPhone
			if(insertCount <= 0){
				//�ظ���ɱ
				throw new RepeatKillException("seckill repeated");
			}else{
				//�����,�ȵ���Ʒ�ľ���,��һ�����漰��mysql���м���
				int upDateCount = seckillDao.reduceNumber(seckillId, nowTime);
				if(upDateCount <= 0){
					//û�и��µ����ݿ�,��ɱ����,rollback
					throw new SeckillCloseException("seckill is closed");
				}else{
					//��ɱ�ɹ� commit
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
					return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,successKilled);
				}
			}
		}catch(SeckillCloseException e1){
			throw e1;
		}catch(RepeatKillException e2){
			throw e2;
		}catch (Exception e) {
			//��ʱ���������ݿ���˵ȵ��쳣��Ҫ������
			logger.error(e.getMessage(),e);
			//���б������쳣ת�����������쳣��һ��try���д�spring���æ�ع�����֤��ɱ��ԭ���ԣ����ͳɹ������¼��
			throw new SeckillException("seckill inner error:"+e.getMessage());
		}
	}

	//Ϊʲô��MD5��������д�����أ���ΪexecuteSeckillҲ��Ҫ����MD5���������ɵ����Ա�
	private String getMD5(long seckillId){
		//ͨ����ֵ�͹���������MD5
		String base = seckillId + "/" + slat; 
		//MD5�����࣬ר����������MD5��ʵ�ּ��ܣ������ǲ�����ģ�����ڲ�֪����ֵ�������
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	public SeckillExecution executeSeckillProcedure(long seckillId,
			long userPhone, String md5){
		// �����洢���̵���ɱ�߼�
		if(md5 == null || !md5.equals(getMD5(seckillId))){
			return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//ִ�д洢���̣�result����ֵ
		try {
			seckillDao.killByProcedure(map);
			//��ȡresult,��Ҫ��pom.xml�н�������
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
