package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;

/**
 * ������װ��ɱ��ִ�еĽ��
 * @author FrancoWang
 */

public class SeckillExecution {
	
	//��ɱ�û�Id
	private long SeckillId;
	
	//��ɱִ�н��״̬
	private int state;
	
	//��ɱִ�н��״̬��ʾ
	private String stateInfo;
	
	//��ɱ�ɹ�����
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
