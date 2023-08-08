package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.SubscribeModel;

import java.util.Map;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/10.
 */
public interface ISubscribeService {

    /**
     * save subscibeModel
     * @Author David
     * @Date 17:47 2018/12/18
     * @param  subscribeModel
     * @return void
     **/
    void save(SubscribeModel subscribeModel);


    /**
     * 判断24 小时内是否收到该 offer 的转化
     * @Author David
     * @Date 17:48 2018/12/18
     * @param  offerModel
     * @param ipAddress
     * @return boolean
     **/
    boolean isTransformed(OfferModel offerModel, String ipAddress);

    /**
     * 清除测试数据
     * @param offerId
     * @param ip
     */
    void deleteTestData(String offerId, String ip);

    /**
     * 判断该用户有无转化过
     * @param offerModel
     * @param ipAddress
     * @param userTransListJson
     * @return
     */
    boolean isTransformedFromRedis(OfferModel offerModel, String ipAddress, Map<Object, Object> userTransListJson);

    /**
     * save 包内转化model
     * @param subscribeModel
     */
    void saveSubModel(SubscribeModel subscribeModel);
}
