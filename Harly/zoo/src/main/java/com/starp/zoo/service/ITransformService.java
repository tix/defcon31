package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/11.
 */
public interface ITransformService {

    /**
     * update offerModel subcount
     * @Author David
     * @Date 17:53 2018/12/18
     * @param appId
     * @param  offerId
     * @return void
     **/
    void updateSubCount(String appId, String offerId);


}
