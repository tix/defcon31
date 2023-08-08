package com.starp.zoo.service;


import com.alibaba.fastjson.JSONObject;

/**
 * @author david
 */
public interface IUserAgentService {

    /**
     * GET USERAGENT
     * @param url
     * @param isRedirect
     * @param userId
     * @return
     */
    JSONObject getGlobalHeaders(String url, boolean isRedirect, String userId);
}
