package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.config.aws.sqs.Producer;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.service.IEpmService;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.service.ISubCountService;
import com.starp.zoo.service.ITransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/11.
 */
@Service
public class TransformServiceImpl implements ITransformService {
    
    @Autowired
    private ISubCountService subCountServic;
    
    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private IEpmService epmService;

    @Lazy
    @Autowired
    private IOfferService offerService;

    @Async
    @Override
    public void updateSubCount(String appId,String offerId){
        OfferModel offerModel = offerService.getOfferModel(offerId);
        if(offerModel != null) {
            if(!StringUtils.isEmpty(appId)) {
                epmService.incrTrans(appId, offerModel, null, false, ZooConstant.CATEGORY_APP);
            }
        }
    }


}
