package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

/**
 * ҵ��ӿڣ�վ��ʹ���߽Ƕ���ƽӿڣ�������ʵ��
 * �������棺�����������ȣ�������ɱ���ͣ��϶��нӿڽ���ִ����ɱ
 * ��ִ����ɱ��Ҫ��һЩ����������Ӧ��ȥ��ע��ɱӦ����ôȥ����棬
 * ��ôȥ�����û���һЩ������Ϊ����������Ϣ��������˵��ʵ�֣�Ӧ��
 * վ��ʹ���ߵĽǶȣ�ʹ������ô���㣬��ʹ�����Ѻõĵ��ýӿ���һ����
 * ����Խ����Խֱ�Ӵ���Խ�á��������ͣ�return�����ͣ�һ��Ҫ�Ѻã��쳣��
 * @Created by FrancoWang 
 */
public interface SeckillService {
	
	/**
	 * ��ѯ������ɱ��¼
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * ��ѯ������ɱ��¼
	 * @param seckillId
	 * @return
	 */
	Seckill getById(long seckillId);
	
	/**
	 * ��ɱ����ʱ�������ɱ�ӿڵĵ�ַ
	 * �������ϵͳʱ�����ɱʱ��
	 * ��ɱ����ʱ��˭Ҳ�²�����ɱ�ĵ�ַ
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(long seckillId);
	
	/**
	 * ִ����ɱ������DTO�����ݴ����
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(long seckillId,long userPhone,String md5)
	throws SeckillException,RepeatKillException,SeckillCloseException;
	
	/**
	 * ִ����ɱ������by�洢����
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckillProcedure(long seckillId,long userPhone,String md5);

}