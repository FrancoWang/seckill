package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

/**
 * 业务接口：站在使用者角度设计接口，而不是实现
 * 三个方面：方法定义粒度，比如秒杀类型，肯定有接口叫做执行秒杀
 * 有执行秒杀需要的一些参数，而不应该去关注秒杀应该怎么去减库存，
 * 怎么去添加用户的一些购买行为，这样的信息对我们来说是实现，应该
 * 站在使用者的角度，使用者怎么方便，让使用者友好的调用接口是一个点
 * 参数越简练越直接传递越好。返回类型（return的类型）一定要友好；异常。
 * @Created by FrancoWang 
 */
public interface SeckillService {
	
	/**
	 * 查询所有秒杀记录
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * 查询单个秒杀记录
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);
	
	/**
	 * 秒杀开启时，输出秒杀接口的地址
	 * 否则输出系统时间和秒杀时间
	 * 秒杀开启时，谁也猜不到秒杀的地址
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * 执行秒杀操作，DTO是数据传输层
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
	throws SeckillException,RepeatKillException,SeckillCloseException;
	
	/**
	 * 执行秒杀操作，by存储过程
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);

}
