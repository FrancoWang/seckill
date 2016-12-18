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
		//redis逻辑操作
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:" + seckillId;
				//并没有实现内部序列化操作，先转化成二进制数组,通过反序列化拿到内部对象
				//get->byte[]->反序列化->Object(seckill)
				//不采用Seckill类实现Seralizable接口的方式，采用Google的protostuff第三方性能超好的序列化方式实现序列化操作
				//采用自定义序列化
				//protostuff : pojo
				byte[] bytes = jedis.get(key.getBytes());
				//缓存中重新获取
				if(bytes != null){
					//new一个空对象
					Seckill seckill = schema.newMessage();
					//这种方法可以压缩到原来JDK自带序列化的十分之一或者五分之一，压缩速度是两个数量级之快
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					//seckill被反序列化
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
		//set Object[seckill]->byte[]->发送给redis,这是一个序列化的过程
		try {
			Jedis jedis = jedisPool.getResource();
			try{
				String key = "seckill:"+seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				//LinkedBuffer是一个缓存器
				//超时缓存
				int timeout = 60*60;//一小时
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
