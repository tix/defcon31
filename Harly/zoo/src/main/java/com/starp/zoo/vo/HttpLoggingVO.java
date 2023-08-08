package com.starp.zoo.vo;

import lombok.Data;

import java.util.List;

/**
 * httplogging实体.
 *
 * @author magic
 * @date 2021/7/27
 */
@Data
public class HttpLoggingVO {
    private String createTime;

    private String appId;

    private String offerId;

    private String pid;

    private String userId;

    private String carrier;

    private String key;

    private String recordDate;

    private String stepName;

    private List<SingleStepVO> singleStepList;
}
