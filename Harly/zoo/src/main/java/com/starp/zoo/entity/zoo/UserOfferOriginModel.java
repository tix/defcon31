package com.starp.zoo.entity.zoo;

import lombok.Data;

/**
 * @author magic
 * @date 2020/12/4
 */
@Data
public class UserOfferOriginModel {
    private String userId;
    private String createTime;
    private String offerName;
    private String eventCode;
    private String url;
    private String seq;
}
