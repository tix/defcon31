package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.constant.*;
import com.starp.zoo.entity.zoo.*;
import com.starp.zoo.repo.zoo.*;
import com.starp.zoo.service.IAndroidMncService;
import com.starp.zoo.service.IAppUserEventService;
import com.starp.zoo.vo.OptionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/7/3.
 */
@Service
public class AppUserEventServiceImpl implements IAppUserEventService {

    private static final Logger appEventLog = LoggerFactory.getLogger(LogNameEnum.APP_EVENT.getLogName());

    @Autowired
    private AppUserEventRepo appUserEventRepo;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private ApplicationRepo appRepo;

    @Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @Autowired
    private UserMobileInfoRepo userMobileInfoRepo;

    @Autowired
    private IAndroidMncService mncService;

    @Async
    @Override
    public void saveModel(AppUserEventModel appUserEventModel){
        appUserEventRepo.save(appUserEventModel);
    }

    @Override
    public void saveLog(JSONObject params) {
        String deviceId = params.getString(ZooConstant.DEVICE_ID);
        String userId = params.getString(ZooConstant.APP_EVENT_USER_ID);
        String mnc = params.getString(ZooConstant.MNC);
        String operator = mncService.generateOp(mnc);
        // offer主键
        String offerId = params.getString(ZooConstant.OFFER_ID.toLowerCase());
        Integer eventCode = params.getInteger(ZooConstant.APP_EVENT_CODE);
        if (eventCode != null && eventCode.intValue() == ZooConstant.APP_MSISDN_URL_EVENT) {
            String url = params.getString(ZooConstant.APP_EVENT_PARAM_2);
            if(!StringUtils.isEmpty(url)){
                // 从 redis 获取记录
                String msisdnParam = (String) cluster1RedisTemplate.opsForHash().get(CacheNameSpace.MSISDN_PARAMS, operator);
                if (!StringUtils.isEmpty(msisdnParam)) {
                    String[] paramArr = msisdnParam.split(ZooConstant.COMMA);
                    if(paramArr != null && paramArr.length > 0) {
                        for(String param : paramArr) {
                            Pattern pattern = Pattern.compile("(\\?|&+)"+ param +"=([^&]*)");
                            Matcher matcher = pattern.matcher(url);
                            while (matcher.find()){
                                String msisdn = matcher.group(2);
                                // 如果找到则保存用户电话号码
                                if (!StringUtils.isEmpty(msisdn)) {
                                    UserMobileInfoModel userMobileInfoModel = userMobileInfoRepo.findFirstByDeviceIdAndMncOrderByCreateTimeDesc(deviceId, mnc);
                                    if(userMobileInfoModel == null) {
                                        userMobileInfoModel = new UserMobileInfoModel();
                                    }
                                    userMobileInfoModel.setMnc(mnc);
                                    userMobileInfoModel.setDeviceId(deviceId);
                                    userMobileInfoModel.setUserId(userId);
                                    userMobileInfoModel.setMobile(msisdn);
                                    userMobileInfoRepo.save(userMobileInfoModel);
                                }
                            }
                        }
                    }
                }
            }
        }
        AppUserEventModel appUserEventModel = new AppUserEventModel(deviceId, userId, eventCode, params.getTimestamp(ZooConstant.APP_EVENT_TIME),
                params.getString(ZooConstant.APP_EVENT_APP_ID),params.getString(ZooConstant.APP_EVENT_PARAM_1),params.getString(ZooConstant.APP_EVENT_PARAM_2),
                    params.getString(ZooConstant.APP_EVENT_PARAM_3),params.getString(ZooConstant.APP_EVENT_PARAM_4),params.getString(ZooConstant.APP_EVENT_PARAM_5), mnc, operator);
        saveLogInfo(offerId, appUserEventModel);
    }

    @Async
    @Override
    public void saveLogInfo(String offerId, AppUserEventModel appUserEventModel) {
        appUserEventModel.setOfferId(offerId);
        if(!StringUtils.isEmpty(offerId)){
            OfferModel offerModel = offerRepo.findFirstByIdentification(offerId);
            if (offerModel != null) {
                List<TagModel> tagModels = tagRepo.findQuery(offerId);
                StringBuilder stacks = new StringBuilder();
                StringBuilder groups = new StringBuilder();
                StringBuilder otherTags = new StringBuilder();
                if (tagModels != null && tagModels.size() > 0) {
                    for (TagModel tag : tagModels) {
                        if (tag.getTagType() == ZooConstant.TAG_TYPE_STACK) {
                            if (StringUtils.isEmpty(stacks.toString())) {
                                stacks.append(tag.getTagName());
                            } else {
                                stacks.append(ZooConstant.VERTICAL_LINE).append(tag.getTagName());
                            }
                        } else if (tag.getTagType() == ZooConstant.TAG_TYPE_GROUP) {
                            if (StringUtils.isEmpty(groups.toString())) {
                                groups.append(tag.getTagName());
                            } else {
                                groups.append(ZooConstant.VERTICAL_LINE).append(tag.getTagName());
                            }
                        } else {
                            if (StringUtils.isEmpty(otherTags.toString())) {
                                otherTags.append(tag.getTagName());
                            } else {
                                otherTags.append(ZooConstant.VERTICAL_LINE).append(tag.getTagName());
                            }
                        }
                    }
                }
                appUserEventModel.setOfferName(offerModel.getOfferName());
                appUserEventModel.setSystemOfferId(offerModel.getOfferId());
                appUserEventModel.setPartner(offerModel.getPartner());
                appUserEventModel.setPartnerOfferId(offerModel.getPartnerOfferId());
                appUserEventModel.setStacks(stacks.toString());
                appUserEventModel.setGroups(groups.toString());
                appUserEventModel.setOtherTags(otherTags.toString());
                if (StringUtils.isEmpty(appUserEventModel.getOperator())) {
                    appUserEventModel.setOperator(offerModel.getOperator());
                }
            }
        }
        if(!StringUtils.isEmpty(appUserEventModel.getAppId())){
            ApplicationModel applicationModel = appRepo.findFirstByIdentification(appUserEventModel.getAppId());
            if (applicationModel != null) {
                appUserEventModel.setAppName(applicationModel.getAppName());
                appUserEventModel.setAppDescription(applicationModel.getDescription());
            }
        }
        appEventLog.info(appUserEventModel.toString());
        saveModel(appUserEventModel);
    }
}
