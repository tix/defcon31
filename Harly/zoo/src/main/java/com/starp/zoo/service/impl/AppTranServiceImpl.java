package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.config.aws.sqs.Producer;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.SubscribeModel;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.service.*;
import com.starp.zoo.util.DateUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author david
 */
@Service
public class AppTranServiceImpl implements IAppTranService {

    @Autowired
    Producer producer;

    @Autowired
    private StringRedisTemplate masterRedisTemplate;

    @Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @Autowired
    private ISubscribeService subscribeService;

    @Autowired
    private ISubCountService subCountService;

    @Autowired
    private ITransformService transformService;

    @Autowired
    private IAppUserEventService appUserEventService;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private IGetOfferService getOfferService;

    @Timed
    @Override
    public void sendAppTrans(JSONObject params) {
        producer.sendToQueueOfferAppTrans(new BaseSqsMessage(new Integer[]{}, "AppTrans", JSON.toJSONString(params)));
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    @Override
    public void sendOfferAppTrans(String msgBodyStr) {
        JSONObject params = JSON.parseObject(msgBodyStr);
        String deviceId = params.getString("deviceId");
        String appId = params.getString("appId");
        String offerId = params.getString("offerId");
        String userId = params.getString("userId");
        String status = params.getString("status");
        String packageName = params.getString("packageName");
        String userAgent = params.getString("userAgent");
        String ip = params.getString("ip");
        SubscribeModel subscribeModel = new SubscribeModel();
        subscribeModel.setIp(ip);
        subscribeModel.setOfferId(offerId);
        subscribeModel.setUserId(userId);
        subscribeModel.setStatus(status);
        subscribeModel.setType(ZooConstant.APP);
        subscribeModel.setUserAgent(userAgent);
        subscribeModel.setPackageName(packageName);
        subscribeService.save(subscribeModel);
        //更新转化总数
        if(!StringUtils.isEmpty(status) && Integer.parseInt(status) == 1) {
            transformService.updateSubCount(appId, offerId);
            OfferModel offerModel = getOfferModel(offerId);
            if(offerModel != null){
                subCountService.incrAppTransCount(offerId,appId);
                // 用户offer转化信息
                String userTransKey = CacheNameSpace.ZOO_USER_TRANS_LIST + appId + CacheNameSpace.COLON + deviceId + CacheNameSpace.COLON +  offerModel.getOperator() + CacheNameSpace.COLON + DateUtil.today();
                String stack = offerModel.getStack() == null ? "" : offerModel.getStack();
                masterRedisTemplate.opsForHash().put(userTransKey,  offerId , stack);
                masterRedisTemplate.expire(userTransKey,  NumberEnum.ONE_DAY_MILLISECONDS.getNum(), TimeUnit.MILLISECONDS);
                String protectedId = CacheNameSpace.ZOO_PROTECTED_TAG + CacheNameSpace.COLON + offerModel.getOperator();
                List<String> protectedOfferList = getOfferService.findProtectedTagList(protectedId);
                boolean isProtectedOffer = protectedOfferList != null && protectedOfferList.size() >0 && protectedOfferList.indexOf(offerId) > -1;
                if(isProtectedOffer){
                    saveProtectedReids(ip,userId,offerId);
                }
            }

        }
    }

    private void saveProtectedReids(String ip, String userId, String offerId) {
        String protectIpSevenDayKey = CacheNameSpace.ZOO_PROTECTED_OFFER_SEVEN_DAY + CacheNameSpace.IP + CacheNameSpace.COLON + ip + CacheNameSpace.COLON + offerId;
        String protectUserIdSevenDayKey = CacheNameSpace.ZOO_PROTECTED_OFFER_SEVEN_DAY + CacheNameSpace.USERID + CacheNameSpace.COLON + userId + CacheNameSpace.COLON + offerId;
        Boolean existIpSevenDay = cluster1RedisTemplate.hasKey(protectIpSevenDayKey);
        Boolean existUserIdSeventDay = cluster1RedisTemplate.hasKey(protectUserIdSevenDayKey);
        if(!(existIpSevenDay != null && existIpSevenDay)){
            masterRedisTemplate.opsForList().leftPush(protectIpSevenDayKey, offerId);
            masterRedisTemplate.expire(protectIpSevenDayKey,7, TimeUnit.DAYS);
        }else {
            masterRedisTemplate.opsForList().leftPush(protectIpSevenDayKey, offerId);
        }
        if(!(existUserIdSeventDay != null && existUserIdSeventDay)){
            masterRedisTemplate.opsForList().leftPush(protectUserIdSevenDayKey, offerId);
            masterRedisTemplate.expire(protectIpSevenDayKey,7, TimeUnit.DAYS);
        }else {
            masterRedisTemplate.opsForList().leftPush(protectUserIdSevenDayKey, offerId);
        }
        String protectIpThirtyDayKey = CacheNameSpace.ZOO_PROTECTED_OFFER_THIRTY_DAY + CacheNameSpace.IP + CacheNameSpace.COLON + ip;
        String protectUserIdThirtyDayKey = CacheNameSpace.ZOO_PROTECTED_OFFER_THIRTY_DAY + CacheNameSpace.USERID + CacheNameSpace.COLON + userId;
        Boolean existIpThirtytDay = cluster1RedisTemplate.hasKey(protectIpSevenDayKey);
        Boolean existUserIdThirtyDay = cluster1RedisTemplate.hasKey(protectUserIdSevenDayKey);
        if(!(existIpThirtytDay != null && existIpThirtytDay)){
            masterRedisTemplate.opsForValue().increment(protectIpThirtyDayKey,1L);
            masterRedisTemplate.expire(protectIpThirtyDayKey,30,TimeUnit.DAYS);
        }else {
            masterRedisTemplate.opsForValue().increment(protectIpThirtyDayKey,1L);
        }
        if(!(existUserIdThirtyDay != null && existUserIdThirtyDay)){
            masterRedisTemplate.opsForValue().increment(protectUserIdThirtyDayKey,1L);
            masterRedisTemplate.expire(protectUserIdThirtyDayKey,30,TimeUnit.DAYS);
        }else {
            masterRedisTemplate.opsForValue().increment(protectUserIdThirtyDayKey,1L);
        }
    }


    private OfferModel getOfferModel(String offerId) {
        OfferModel offerModel = null;
        Boolean existOfferRedis = cluster1RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER, offerId);
        if(existOfferRedis != null && existOfferRedis){
            offerModel = JSON.parseObject(String.valueOf(cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId)),OfferModel.class);
        }else {
            offerModel = offerRepo.findByIdentification(offerId);
        }
        return offerModel;
    }
}
