package org.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.impl.SeckillServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//配置service要依赖于dao的配置
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
"classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	//使用注解注入进来就可以了，而不用重新new一个对象。
	@Autowired
	private SeckillService seckillService;

	@Test
	public void testGetSeckillList() throws Exception{
		List<Seckill> list = seckillService.getSeckillList();
		logger.info("list={}",list);
	}

	@Test
	public void testGetById() throws Exception{
		long id = 1000l;
		Seckill seckill = seckillService.getById(id);
		logger.info("seckill={}",seckill);
	}

	/*@Test
	public void testExportSeckillUrl() throws Exception{
		long id = 1000l;
		Exposer exposer = seckillService.exportSeckillUrl(id);
		logger.info("exposer={}",exposer);
	}

	@Test
	public void testExecuteSeckill() throws Exception{
		long id = 1000l;
		long phone = 13820960208l;
		String md5 = "a9b4e3efcca4c020591f0eeb2ac7d18f";
		//当重复秒杀不去向上抛出junit
		try{
			SeckillExecution seckillExecution = seckillService.executeSeckill(id,phone,md5);
			logger.info("result={}",seckillExecution);
		}catch(RepeatKillException e){
			logger.error(e.getMessage());
		}catch(SeckillCloseException e){
			logger.error(e.getMessage());
		}
	}*/

	//将秒杀的两个功能模块合并到一起
	//集成测试代码完整逻辑，注意可重复执行。
	@Test
	public void testSeckillLogic() throws Exception{
		/*long id = 1000l;*/
		long id = 1001l;
		//获得秒杀的接口地址
		Exposer exposer = seckillService.exportSeckillUrl(id);
		if(exposer.isExposed()){
			logger.info("exposer={}",exposer);
			long phone = 13820960208l;
			String md5 = exposer.getMd5();
			//当重复秒杀不去向上抛出junit
			try{
				SeckillExecution seckillExecution = seckillService.executeSeckill(id,phone,md5);
				logger.info("result={}",seckillExecution);
			}catch(RepeatKillException e){
				logger.error(e.getMessage());
			}catch(SeckillCloseException e){
				logger.error(e.getMessage());
			}
		}else{
			//警告就是秒杀未开启
			logger.warn("exposer={}",exposer);
		}
	}
	
	@Test
	public void testExecuteSeckillProcedure(){
		long seckillId = 1001l;
		long phone = 13132410258l;
		Exposer exposer = seckillService.exportSeckillUrl(seckillId);
		if(exposer.isExposed()){
			String md5 = exposer.getMd5();
			SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
			logger.info(execution.getStateInfo());
		}
		
	}
}
