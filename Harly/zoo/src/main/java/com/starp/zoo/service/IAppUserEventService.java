package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.entity.zoo.AppUserEventModel;

import java.util.Map;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/7/3.
 */
public interface IAppUserEventService {

    /**
     * 保存AppUserEventModel
     * @Author David
     * @Date 17:16 2018/12/18
     * @param appUserEventModel
     * @throws Exception
     **/
    void saveModel(AppUserEventModel appUserEventModel);

    /**
     * 保存日志
     * @param params
     */
    void saveLog(JSONObject params);

    /**
     * 保存日志信息
     * @param offerId
     * @param appUserEventModel
     */
    void saveLogInfo(String offerId, AppUserEventModel appUserEventModel);
}
