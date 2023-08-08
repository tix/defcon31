package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.OfferModel;

import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
public interface IGetOfferService {

    /**get enable
     * @param deviceId
     * @param appId
     * @param ipAddress
     * @param operator
     * @param usedList
     * @param isLog
     * @param isTest
     * @return
     */
    OfferModel getEnableOfferModel(String deviceId, String appId, String ipAddress, String operator, List<String> usedList,Boolean isLog, boolean isTest);

    /**
     * getClickId
     * @param appId
     * @param ipAddress
     * @param offerId
     * @return
     */
    String getClickId(String appId, String ipAddress, String offerId);


    /**
     * 获取 format url
     * @param appId
     * @param clickId
     * @param resultModel
     * @throws Exception
     * @return
     */
    String formatUrl(String appId, String clickId, OfferModel resultModel) throws Exception;


    /**
     * offer 超过最大拉取次数告警
     * @param msgBodyStr
     */
    void sendOfferMaxPullMail(String msgBodyStr);

    /**
     * checkEnbaleOffer
     * @param userOfferEpmIndex
     * @param size
     * @param appEpmOfferList
     * @param preOfferId
     * @param offerFiltersKeys
     * @param filterType
     * @param usedList
     * @param usedTags
     * @param deviceId
     * @param userTransListJson
     * @param offerPulledCounterMap
     * @param resultModel
     * @param appId
     * @param operator
     * @param offerFilterModel
     * @param offerFiltersMap
     * @param loop
     * @param ip
     * @return
     */
    JSONObject checkEnbaleOffer(int userOfferEpmIndex, int size, List<String> appEpmOfferList, String preOfferId, List<String> offerFiltersKeys, String filterType, List<String> usedList, List<String> usedTags, String deviceId, Map<Object, Object> userTransListJson, Map<Object, Object> offerPulledCounterMap,
            OfferModel resultModel, String appId, String operator, OfferModel offerFilterModel, Map<Object, Object> offerFiltersMap, int loop, String ip);

    /**
     * checkCanPullTopOffer
     * @param offerFiltersMap
     * @param protectedOfferList
     * @param ip
     * @param deviceId
     * @param offerFiltersKeys
     * @param usedList
     * @param usedTags
     * @param offerFilterModel
     * @param userTransListJson
     * @param offerPulledCounterMap
     * @param appId
     * @param operator
     * @return
     */
    boolean checkCanPullTopOffer(Map<Object, Object> offerFiltersMap, List<String> protectedOfferList, String ip, String deviceId, List<String> offerFiltersKeys, List<String> usedList, List<String> usedTags, OfferModel offerFilterModel, Map<Object, Object> userTransListJson, Map<Object, Object> offerPulledCounterMap, String appId, String operator);

    /**
     * handleUpdatePullOfferRedis
     * @param appId
     * @param operator
     * @param currOfferId
     * @param resultModel
     * @param loop
     * @param appEpmOfferList
     * @param userOfferEpmIndex
     */
    void handleUpdatePullOfferRedis(String appId, String operator, String currOfferId, OfferModel resultModel, int loop, List<String> appEpmOfferList, int userOfferEpmIndex);

    /**
     * checkProtectedOffer
     * @param ip
     * @param deviceId
     * @param currOfferId
     * @return
     */
    boolean checkProtectedOffer(String ip, String deviceId, String currOfferId);

    /**
     * checkProtectedThirtyOffer
     * @param ip
     * @param deviceId
     * @param currOfferId
     * @return
     */
    boolean checkProtectedThirtyOffer(String ip, String deviceId, String currOfferId);

    /**
     * getOfferPulledCounterListWithOperator
     * @param appId
     * @param operator
     * @return
     */
    Map<Object, Object> getOfferPulledCounterListWithOperator(String appId, String operator);

    /**
     * getUsedStackTags
     *
     * @param appId
     * @param deviceId
     * @param operator
     * @param usedList
     * @return
     */
    List<String> getUsedStackTags(String appId, String deviceId, String operator, List<String> usedList);

    /**
     * getAppEpmOfferList
     * @param appId
     * @param offerFiltersKeys
     * @param operator
     * @return
     */
    List<String> getAppEpmOfferList(String appId, List<String> offerFiltersKeys, String operator);

    /**
     * getUserOfferEpmIndex
     * @param appEpmOfferList
     * @param appId
     * @param operator
     * @return
     */
    int getUserOfferEpmIndex(List<String> appEpmOfferList, String appId, String operator);

    /**
     * findProtectedTagList
     * @param protectedId
     * @return
     */
    List<String> findProtectedTagList(String protectedId);
}
