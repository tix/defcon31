package com.starp.zoo.vo;

import lombok.Data;

/**
 * @author curry by 2022/4/12
 */
@Data
public class AutoStackInfoVO {

	/**
	 * 实际为 APP 的名字
	 */
	String appId;

	/**
	 * 实际为 APP 的描述
	 */
	String appName;

	String testGroup;

	String operator;

	String offerName;

	Integer initMoNum;

	Integer nowMoNum;

	Integer newMoNum;

	String offerId;

	String createTime;

	public AutoStackInfoVO() {
	}

	public AutoStackInfoVO(String appId, String appName, String testGroup, String operator, String offerName, Integer initMoNum, Integer nowMoNum, Integer newMoNum, String offerId, String createTime) {
		this.appId = appId;
		this.appName = appName;
		this.testGroup = testGroup;
		this.operator = operator;
		this.offerName = offerName;
		this.initMoNum = initMoNum;
		this.nowMoNum = nowMoNum;
		this.newMoNum = newMoNum;
		this.offerId = offerId;
		this.createTime = createTime;
	}
}
