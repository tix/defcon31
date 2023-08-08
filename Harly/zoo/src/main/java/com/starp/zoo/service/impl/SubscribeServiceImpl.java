package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.SubscribeModel;
import com.starp.zoo.repo.zoo.*;
import com.starp.zoo.service.ISubscribeService;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.util.DateUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/10.
 */
@Service
@Slf4j
public class SubscribeServiceImpl implements ISubscribeService {

    @Autowired
    private SubscribeRepo subscribeRepo;

    @Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @Autowired
    private OfferRepo offerRepo;

    @Override
    public void save(SubscribeModel subscribeModel) {
        //保存msisdn
        try {
            OfferModel offerModel = offerRepo.findOfferByIdentification(subscribeModel.getOfferId());
            if(offerModel!=null){
                Date oneHour = DateUtil.formatTime(DateUtil.today() + " 01:00:00");
                String clickKeyStart = CacheNameSpace.ZOO_IP_CLICKID + subscribeModel.getIp();
                String clickHashKey = offerModel.getIdentification();
                String clickId = findUserInfoFromRedis(oneHour,clickKeyStart,clickHashKey);
                subscribeModel.setClickId(clickId);
                String msisdnKeyStart = CacheNameSpace.ZOO_OFFER_MSISDN_INFO + offerModel.getIdentification();
                String msisdnHashkey = subscribeModel.getUserId();
                String msisdn = findUserInfoFromRedis(oneHour,msisdnKeyStart,msisdnHashkey);
                subscribeModel.setMsisdn(msisdn);
            }
            subscribeRepo.save(subscribeModel);
        }catch (Exception e){
            log.info("SAVE SUBSCRIBE USER  ERROR:{},  SUBSCRIBE MODEL IS :{}", JSON.toJSONString(e),JSON.toJSONString(subscribeModel));
        }

    }

    private String findUserInfoFromRedis(Date oneHour, String keyStart, String hashKey) {
        String value = "";
        Date date = new Date();
        if(date.before(oneHour)){
            Boolean exsitClickIdKey = cluster1RedisTemplate.opsForHash().hasKey(keyStart + CacheNameSpace.COLON + DateUtil.today(), hashKey);
            if(exsitClickIdKey != null && exsitClickIdKey){
                value = String.valueOf(cluster1RedisTemplate.opsForHash().get(keyStart + CacheNameSpace.COLON + DateUtil.today(), hashKey));
            }else {
                exsitClickIdKey = cluster1RedisTemplate.opsForHash().hasKey(keyStart + CacheNameSpace.COLON + DateUtil.yesterday(),hashKey);
                if(exsitClickIdKey != null && exsitClickIdKey){
                    value = String.valueOf(cluster1RedisTemplate.opsForHash().get(keyStart + CacheNameSpace.COLON + DateUtil.yesterday(), hashKey));
                }
            }
        }else {
            Boolean existClickIdKey = cluster1RedisTemplate.opsForHash().hasKey(keyStart + CacheNameSpace.COLON + DateUtil.today(),hashKey);
            if(existClickIdKey != null && existClickIdKey){
                value = String.valueOf(cluster1RedisTemplate.opsForHash().get(keyStart + CacheNameSpace.COLON + DateUtil.today(),hashKey));
            }
        }
        return value;
    }

    /**
     * 判断24 小时内是否收到该 offer 的转化
     * @param offerModel
     * @param ipAddress
     * @return
     */
    @Override
    public boolean isTransformed(OfferModel offerModel, String ipAddress) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date yesterdayDate = calendar.getTime();
        boolean isExists = subscribeRepo.existsByOfferIdAndStatusAndIpAndCreateTimeAfter(offerModel.getIdentification(),
                String.valueOf(ZooConstant.STATUS_1), ipAddress, yesterdayDate);
        if(isExists){
            return true;
        }
        return false;
    }

    @Override
    public void deleteTestData(String offerId, String ip) {
        if(!StringUtils.isEmpty(ip)) {
            if (StringUtils.isEmpty(offerId)) {
                subscribeRepo.deleteByIp(ip);
            } else {
                subscribeRepo.deleteByOfferIdAndIp(offerId, ip);
            }
        }
    }

    @Timed
    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    @Override
    public boolean isTransformedFromRedis(OfferModel offerModel, String deviceId, Map<Object, Object> userTransListJson) {
        //每一次转化就设置一个 24小时的key
        if(offerModel != null) {
            String userKey = offerModel.getIdentification();
            boolean existTrans = userTransListJson.get(userKey) != null;
            if (existTrans) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void saveSubModel(SubscribeModel subscribeModel) {
        subscribeRepo.save(subscribeModel);
    }
}
