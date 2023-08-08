package com.starp.zoo.constant;

/**
 * 可选择的系统定义
 * 
 * @author yeahmobi
 *
 */
public enum AvaliableSystem {
	/** 支付转化系统 */
	PAYMENT("支付转化系统"),
	/** 统计系统 */
	STATISTICS("统计系统"),
	/** 管理系统 */
	MANAGER("管理系统"),
	/** 统计系统 */
	ZOO("动物园");

	private String systemName;

	AvaliableSystem(String systemName) {
		this.systemName = systemName;
	}

	public String getSystemName() {
		return systemName;
	}

//	public void setSystemName(String systemName) {
//		this.systemName = systemName;
//	}

}
