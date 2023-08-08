package com.starp.zoo.entity.zoo;

import lombok.Data;

import java.util.List;

/**
 * @author magic
 * @date 2020/12/4
 */
@Data
public class UserOfferDetailModel {
    /**
     * 用户id
     */
    private String userId;
    /**
     * offer详情列表
     */
    private List<SequenceDetail> detailList;

    /**
     * list长度
     */
    private int listSize;
}
