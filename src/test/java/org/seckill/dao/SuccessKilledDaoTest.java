package org.seckill.dao;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration({"classpath:spring/spring-dao.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SuccessKilledDaoTest {
	
	//注入Dao实现类依赖
	@Resource
	private SuccessKilledDao successKilledDao;
	
	/*
	 * 运行成功，测试台打印出insertCount=1的信息，即我们修改了表中的一条记录，
	 * 这时查看秒杀成功明细表，发现该用户的信息已经被插入。然后再次运行该测试方法，
	 * 程序没有报主键异常的错，是因为我们在编写我们的明细表的时候添加了一个联合主键的字段，
	 * 它保证我们明细表中的seckillId和userPhone不能重复插入，
	 * 另外在SuccessDao.xml中写的插入语句的ignore关键字也保证了这点。
	 * 控制台输出0，表示没有对明细表做插入操作。
	 * 然后进行queryByIdWithSeckill()方法的测试,需要在Dao层的方法中添加@Param注解:
	 * 第一次insertCount是1，第二次是0
	 */
	@Test
	public void testInsertSuccessKilled() throws Exception{
		long id = 1001L;
		long phone = 18700948297L;
		int insertCount = successKilledDao.insertSuccessKilled(id,phone);
		System.out.println("insertCount"+insertCount);
	}
	
	@Test
	public void testQueryByIdWithSeckill() throws Exception{
		long id = 1001L;
		long phone = 18700948297L;
		SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
		System.out.println(successKilled);
		System.out.println(successKilled.getSeckill());
	}
	
}
