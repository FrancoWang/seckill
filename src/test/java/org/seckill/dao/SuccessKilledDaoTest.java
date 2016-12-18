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
	
	//ע��Daoʵ��������
	@Resource
	private SuccessKilledDao successKilledDao;
	
	/*
	 * ���гɹ�������̨��ӡ��insertCount=1����Ϣ���������޸��˱��е�һ����¼��
	 * ��ʱ�鿴��ɱ�ɹ���ϸ�����ָ��û�����Ϣ�Ѿ������롣Ȼ���ٴ����иò��Է�����
	 * ����û�б������쳣�Ĵ�����Ϊ�����ڱ�д���ǵ���ϸ���ʱ�������һ�������������ֶΣ�
	 * ����֤������ϸ���е�seckillId��userPhone�����ظ����룬
	 * ������SuccessDao.xml��д�Ĳ�������ignore�ؼ���Ҳ��֤����㡣
	 * ����̨���0����ʾû�ж���ϸ�������������
	 * Ȼ�����queryByIdWithSeckill()�����Ĳ���,��Ҫ��Dao��ķ��������@Paramע��:
	 * ��һ��insertCount��1���ڶ�����0
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
