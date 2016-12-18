package org.seckill.exception;
/**
 * 重复秒杀异常（运行期异常）不用手动try catch,
 * spring也只接收运行期异常 
 * @author FrancoWang
 */
public class RepeatKillException extends SeckillException{

	public RepeatKillException(String message) {
		super(message);
	}

	public RepeatKillException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
