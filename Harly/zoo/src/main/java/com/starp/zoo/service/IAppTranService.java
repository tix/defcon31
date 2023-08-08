package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author david
 */
public interface IAppTranService {
    /**
     * send app trans
     * @param params
     */
    void sendAppTrans(JSONObject params);

    /**
     * consumer app trans
     * @param msgBodyStr
     */
    void sendOfferAppTrans(String msgBodyStr);
}
