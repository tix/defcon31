package com.starp.zoo.service;


import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.AffClickInfoModel;
import com.starp.zoo.entity.zoo.AffPostBackIeModel;
import com.starp.zoo.entity.zoo.AffPostBackModel;
import com.starp.zoo.entity.zoo.OfferModel;
import org.springframework.scheduling.annotation.Async;

/**
 * @author  Charles, DATE: 2018/9/19.
 */
public interface IAffService {

    /**
     * 获取clickid
     * @param appId
     * @param offerId
     * @return
     */
    String getClickId(String appId, String offerId);

    /**
     * 保存 postBack 数据
     * @param affModel
     */
    void savePostBack(AffPostBackModel affModel);

    /**
     * 保存并向下游发送postback
     * @param postback
     * @return
     */
    void saveAffApkPostBack(JSONObject postback);


    /**
     * 保存点击信息
     * @param ipAddress
     * @param deviceId
     * @param clickId
     * @param offerModel
     * @param userAgent
     * @param appId
     * @param type
     * @param isSave
     */
    void saveClickInfo(String ipAddress, String deviceId, String clickId, OfferModel offerModel, String userAgent, String appId, int type, boolean isSave);

    /**
     * 保存 IE postBack 数据
     * @param affPostBackIEModel
     */
    void save(AffPostBackIeModel affPostBackIEModel);

    /**
     * 查找拉取的次数
     * @param ipAddress
     * @param categoryType
     * @param categoryId
     * @param offerId
     * @return
     */
    long getPullNum(String ipAddress, String categoryType, String categoryId, String offerId);


}
