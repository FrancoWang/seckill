package org.seckill.exception;
/**
 * �ظ���ɱ�쳣���������쳣�������ֶ�try catch,
 * springҲֻ�����������쳣 
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
