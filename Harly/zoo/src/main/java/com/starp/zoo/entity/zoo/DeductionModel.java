package com.starp.zoo.entity.zoo;

import lombok.Data;

/**
 * @author covey
 */
@Data
public class DeductionModel {

    /**
     * 时间
     */
    private String time;

    /**
     * 国家
     */
    private String country;

    /**
     * 运营商
     */
    private String operator;

    /**
     *上游
     */
    private String partner;

    /**
     * offer
     */
    private String offerName;

    /**
     * app内转化
     */
    private double conversion;

    /**
     * 上游回传
     */
    private double postBack;

    /**
     * 扣量
     */
    private double rate;
}
