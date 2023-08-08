package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;

/**
 * IAddressMsisdnService.
 *
 * @author magic
 * @data 2022/4/21
 */
public interface IAddressMsisdnService {
    /**
     * 保存addressMsisdn 参数.
     *
     * @param jsonObject
     */
    void save(JSONObject jsonObject);
}
