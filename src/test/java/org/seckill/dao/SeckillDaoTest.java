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
//����Spring��Junit���ϣ�Junit����ʱ����springIOC���� :@RunWith
//spring-test,junit
//����Junit��Spring�����ļ�
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SeckillDaoTest {
	
	//ע��Daoʵ��������
	@Resource
	private SeckillDao seckillDao;
	
	@Test
	public void testQueryById() throws Exception{
		long id =1000l;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill.getName());
		System.out.println(seckill);
	}
	
	/*���и÷��������򱨴�������Ϣ����:
	 *Caused by: org.apache.ibatis.binding.BindingException: 
	 *Parameter 'offset' not found. Available parameters are 
	 *[1, 0, param1, param2]
	 *��˼�����޷����offset�����İ󶨣�
	 *��Ҳ������java������Ե�һ�����⣬
	 *Ҳ����javaû�б����вεļ�¼��
	 *java�����е�ʱ����List<Seckill> queryAll(int offset,int limit);
	 *�еĲ����������:queryAll(int arg0,int arg1),
	 *�������Ǿ�û�а취ȥ���ݶ����������Ҫ��SeckillDao�ӿ����޸ķ���:
     *List<Seckill> queryAll(@Param("offset") int offset,@Param("limit") int limit); 
     *��������ʹ���ǵ�MyBatisʶ��offset��limit����������
     *��Dao�㷽���е�������������xmlӳ���ļ���sql���Ĵ���������ӳ�䡣
     *Ȼ�����²��ԣ����ֲ���ͨ����
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
