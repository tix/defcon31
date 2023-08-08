package com.starp.zoo.vo;

import lombok.Data;

/**
 * @author curry by 2023/5/23
 */
@Data
public class EpmClickVO {

	private String offerId;

	private Long click;

	private Long appTrans;

	private Long postBack;

	public EpmClickVO(String offerId, Long click, Long appTrans, Long postBack) {
		this.offerId = offerId;
		this.click = click;
		this.appTrans = appTrans;
		this.postBack = postBack;
	}

	public EpmClickVO() {
	}
}
