package org.seckill.exception;

/**
 * 秒杀关闭异常
 * 时间到了
 * 库存关了
 */

public class SeckillCloseException extends SeckillException{

	public SeckillCloseException(String message, Throwable cause) {
		super(message, cause);
	}

	public SeckillCloseException(String message) {
		super(message);
	}
	
}
