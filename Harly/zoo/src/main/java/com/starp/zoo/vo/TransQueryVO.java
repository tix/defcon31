package com.starp.zoo.vo;

import lombok.Data;

/**
 * @author david
 */
@Data
public class TransQueryVO {

    private String country;


    private String time;

    private Integer clickNum;

    /**
     * 收入
     */
    private Double revenue;


    /**
     * 渠道收入
     */
    private Double channelRevenue;

}
