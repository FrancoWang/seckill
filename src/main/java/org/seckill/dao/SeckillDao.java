package org.seckill.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

/**
 * @author FrancoWang
 */

//Dao对应entity，实现增删查改等相应的功能。
public interface SeckillDao {
	 /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 如果影响行数>1，表示更新库存的记录行数
     */
	int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") Date killTime);
	
	/**
     * 根据id查询秒杀的商品信息
     * @param seckillId
     * @return
     */
	Seckill queryById(long seckillId);
	
	 /**
     * 根据偏移量查询秒杀商品列表
     * @param offet
     * @param limit
     * @return
     */
	List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit);

	/**
	 * 使用存储过程执行秒杀，需要在SeckillDao.xml中进行相应的操作
	 * @param paramMap
	 */
	void killByProcedure(Map<String,Object> paramMap);
}
