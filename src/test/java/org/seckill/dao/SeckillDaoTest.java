package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mysql.fabric.xmlrpc.base.Data;

import javax.annotation.Resource;
//配置Spring和Junit整合，Junit启动时加载springIOC容器 :@RunWith
//spring-test,junit
//告诉Junit，Spring配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SeckillDaoTest {
	
	//注入Dao实现类依赖
	@Resource
	private SeckillDao seckillDao;
	
	@Test
	public void testQueryById() throws Exception{
		long id =1000l;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill.getName());
		System.out.println(seckill);
	}
	
	/*运行该方法，程序报错，报错信息如下:
	 *Caused by: org.apache.ibatis.binding.BindingException: 
	 *Parameter 'offset' not found. Available parameters are 
	 *[1, 0, param1, param2]
	 *意思就是无法完成offset参数的绑定，
	 *这也是我们java编程语言的一个问题，
	 *也就是java没有保存行参的记录，
	 *java在运行的时候会把List<Seckill> queryAll(int offset,int limit);
	 *中的参数变成这样:queryAll(int arg0,int arg1),
	 *这样我们就没有办法去传递多个参数。需要在SeckillDao接口中修改方法:
     *List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit); 
     *这样才能使我们的MyBatis识别offset和limit两个参数，
     *将Dao层方法中的这两个参数与xml映射文件中sql语句的传入参数完成映射。
     *然后重新测试，发现测试通过。
     */
	@Test
	public void testQueryAll() throws Exception{
		List<Seckill> seckills = seckillDao.queryAll(0, 100);
		for(Seckill seckill: seckills){
			System.out.println(seckill.getName());
			System.out.println(seckill);
		}
	}
	
	@Test
	public void testReduceNumber() throws Exception{
		Date killTime = new Date();
		int updateCount = seckillDao.reduceNumber(1000l,killTime);
		System.out.println("updateCount"+updateCount);
	}
	
}
