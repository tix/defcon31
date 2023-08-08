package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.SubCountModel;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.SubCountRepo;
import com.starp.zoo.repo.zoo.SubscribeRepo;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.service.ISubCountService;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.util.DateUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/10.
 */
@Service
public class SubCountServiceImpl implements ISubCountService {

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private SubCountRepo subCountRepo;

    @Autowired
    private SubscribeRepo subscribeRepo;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;

    @Autowired
    private IOfferService offerService;

    public boolean isOverCap(OfferModel offerModel) {
        int cap = offerModel.getCap();
        //如果要设置每天，则需要offerId+date
        SubCountModel subCountModel = subCountRepo.findFirstByOfferIdOrderByCreateTime(offerModel.getIdentification() + "_" + formatDateByTimeZone(offerModel));
        if(subCountModel != null && subCountModel.getCount() >= cap){
            return true;
        }
        return false;
    }

    /**
     * subCountModel offerId 必须为唯一
     * @param offerModel
     */

    @Override
    public void initCount(OfferModel offerModel) {
            SubCountModel subCountModel = new SubCountModel();
            subCountModel.setOfferId(offerModel.getIdentification() + "_" + formatDateByTimeZone(offerModel));
            subCountModel.setCount(1);
            subCountRepo.save(subCountModel);
    }

    public static String formatDateByTimeZone(OfferModel offerModel){
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if(offerModel != null) {
            if(!StringUtils.isEmpty(offerModel.getResetTimezone())) {
                df.setTimeZone(TimeZone.getTimeZone(offerModel.getResetTimezone()));
            }
            if(!StringUtils.isEmpty(offerModel.getResetTime())){
                String resetTimeStr = offerModel.getResetTime();
                String[] timeArr = resetTimeStr.split(ZooConstant.COLON);
                if(timeArr != null && timeArr.length == NumberEnum.THREE.getNum()){
                    calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArr[0]));
                    calendar.add(Calendar.MINUTE, Integer.parseInt(timeArr[1]));
                    calendar.add(Calendar.SECOND, Integer.parseInt(timeArr[2]));
                }
            }
        }
        return df.format(calendar.getTime());
    }


    @Override
    public boolean isOverCapWithSubscribe(OfferModel offerModel) throws Exception {
        int cap = offerModel.getCap();
        //添加时间、offerId 、status 三个索引
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        if(!StringUtils.isEmpty(offerModel.getResetTimezone())) {
            df.setTimeZone(TimeZone.getTimeZone(offerModel.getResetTimezone()));
        }
        if(!StringUtils.isEmpty(offerModel.getResetTime())){
            String resetTimeStr = offerModel.getResetTime();
            String[] timeArr = resetTimeStr.split(ZooConstant.COLON);
            if(timeArr != null && timeArr.length == NumberEnum.THREE.getNum()){
                calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArr[0]));
                calendar.add(Calendar.MINUTE, Integer.parseInt(timeArr[1]));
                calendar.add(Calendar.SECOND, Integer.parseInt(timeArr[2]));
            }
        }
        String startTime = df.format(calendar.getTime());
        DateFormat dfNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = dfNow.parse(startTime);
        calendar.add(Calendar.DATE, 1);
        String endTime = df.format(calendar.getTime());
        Date endDate = dfNow.parse(endTime);
        int count = subscribeRepo.countQuery(offerModel.getIdentification(), String.valueOf(ZooConstant.STATUS_1), startDate, endDate);
        if(count >= cap){
            return true;
        }
        return false;
    }

    @Override
    public boolean isOverCapFromRedis(OfferModel offerModel, Map<Object, Object> offerTransCounters) {
        if(offerModel != null) {
            int count = Integer.parseInt(String.valueOf(offerTransCounters.get(offerModel.getIdentification())));
            if (count > offerModel.getCap()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isOverAppCapFromRedis(OfferModel offerModel, Map<Object, Object> appTransCounts) {
        if(offerModel != null) {
            int count = Integer.parseInt(String.valueOf(appTransCounts.get(offerModel.getIdentification())));
            if (count > offerModel.getAppCap()) {
                return true;
            }
        }
        return false;
    }

    @Async
    @Override
    @SuppressFBWarnings({"NP_NULL_PARAM_DEREF","NP_NULL_ON_SOME_PATH"})
    public void incrAppTransCount(String offerId,String appId) {
        if(offerRepo.existsById(offerId)){
            OfferModel offerModel = offerService.getOfferModel(offerId);
            if(offerModel != null) {
                String offerTransKey = CacheNameSpace.ZOO_OFFER_TRNS_COUNTER + appId + CacheNameSpace.COLON + offerModel.getOperator() + CacheNameSpace.COLON + DateUtil.today();
                String appTransKey = CacheNameSpace.AFF_APP_OFFER_TRANS_COUNTER + CacheNameSpace.COLON + offerModel.getIdentification() + CacheNameSpace.COLON + formatDateByTimeZone(offerModel);
                Long offerTransCount = stringRedisTemplate.opsForHash().increment(offerTransKey, offerId, 1);
                Long appTransCount = stringRedisTemplate.opsForValue().increment(appTransKey, 1);
                if(offerTransCount == 1){
                    stringRedisTemplate.expire(offerTransKey,NumberEnum.ONE.getNum(),TimeUnit.DAYS);
                }
                if(appTransCount == 1){
                    stringRedisTemplate.expire(appTransKey, NumberEnum.ONE_DAY_MILLISECONDS.getNum(), TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
