package org.seckill.dao.cache;

import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
/**
 * @author FrancoWang
 */
public class RedisDao {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final JedisPool jedisPool;

	public RedisDao(String ip,int port){
		System.out.println("!!");
		jedisPool = new JedisPool(ip,port);
	}

	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

	public Seckill getSeckill(long seckillId){
		//redis�߼�����
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				//��û��ʵ���ڲ����л���������ת���ɶ���������,ͨ�������л��õ��ڲ�����
				//get->byte[]->�����л�->Object(seckill)
				//������Seckill��ʵ��Seralizable�ӿڵķ�ʽ������Google��protostuff���������ܳ��õ����л���ʽʵ�����л�����
				//�����Զ������л�
				//protostuff : pojo
				byte[] bytes = jedis.get(key.getBytes());
				//���������»�ȡ
				if(bytes != null){
					//newһ���ն���
					Seckill seckill = schema.newMessage();
					//���ַ�������ѹ����ԭ��JDK�Դ����л���ʮ��֮һ�������֮һ��ѹ���ٶ�������������֮��
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					//seckill�������л�
					return seckill;
				}
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	public String putSeckill(Seckill seckill){
		//set Object[seckill]->byte[]->���͸�redis,����һ�����л��Ĺ���
		try {
			Jedis jedis = jedisPool.getResource();
			try{
				String key = "seckill:"+seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				//LinkedBuffer��һ��������
				//��ʱ����
				int timeout = 60*60;//һСʱ
				String result = jedis.setex(key.getBytes(),timeout,bytes);
				return result;
			}
			finally{
				jedis.close();
			}
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

}
