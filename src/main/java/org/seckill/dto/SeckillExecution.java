package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;

/**
 * 用来封装秒杀后执行的结果
 * @author FrancoWang
 */

public class SeckillExecution {
	
	//秒杀用户Id
	private long SeckillId;
	
	//秒杀执行结果状态
	private int state;
	
	//秒杀执行结果状态表示
	private String stateInfo;
	
	//秒杀成功对象
	private SuccessKilled successKilled;

	public long getSeckillId() {
		return SeckillId;
	}

	public void setSeckillId(long seckillId) {
		SeckillId = seckillId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public SuccessKilled getSuccessKilled() {
		return successKilled;
	}

	public void setSuccessKilled(SuccessKilled successKilled) {
		this.successKilled = successKilled;
	}

	public SeckillExecution(long seckillId, SeckillStateEnum stateEnum,
			SuccessKilled successKilled) {
		SeckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.successKilled = successKilled;
	}

	public SeckillExecution(long seckillId, SeckillStateEnum stateEnum) {
		SeckillId = seckillId;
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	@Override
	public String toString() {
		return "SeckillExecution [SeckillId=" + SeckillId + ", state=" + state
				+ ", stateInfo=" + stateInfo + ", successKilled="
				+ successKilled + "]";
	}
	
}
