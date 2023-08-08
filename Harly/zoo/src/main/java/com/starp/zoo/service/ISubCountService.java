package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.OfferModel;

import java.util.Map;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/10.
 */
public interface ISubCountService {




    /**
     * 初始化count
     * @Author David
     * @Date 17:13 2018/12/18
     * @param offerModel
     * @return
     **/
    void initCount(OfferModel offerModel);



    /**
     * isOverCapWithSubscribe
     * @Author David
     * @Date 17:14 2018/12/18
     * @param offerModel
     * @return boolean
     * @throws Exception
     **/
    boolean isOverCapWithSubscribe(OfferModel offerModel) throws Exception;

    /**
     * 判断是否超 cap
     * @param offerModel
     * @param offerCounters
     * @return
     */
    boolean isOverCapFromRedis(OfferModel offerModel, Map<Object, Object> offerCounters);

    /**
     * 判断是否超过 app 最大转化
     * @param offerModel
     * @param offerCounters
     * @return
     */
    boolean isOverAppCapFromRedis(OfferModel offerModel, Map<Object, Object> offerCounters);

    /**
     * 增加 app 转化统计
     * @param offerId
     * @param appId
     */
    void incrAppTransCount(String offerId,String appId);
}
