package com.starp.zoo.service;

/**
 * @author Charles
 */
public interface ILogService {

    /**
     * 日志打点
     * @param appId
     * @param offerId
     * @param aff
     * @param pid
     * @param url
     * @param operator
     * @param body
     * @param timestamp
     * @param ipAddress
     * @param status
     * @param userId
     */
    void saveLog(String appId, String offerId, String aff, String pid, String url,
                 String operator, String body, Long timestamp, String ipAddress, Integer status, String userId);
}
