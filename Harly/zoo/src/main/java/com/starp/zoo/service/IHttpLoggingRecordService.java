package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.HttpLoggingModel;
import com.starp.zoo.entity.zoo.HttpLoggingRecordModel;

import java.util.List;

/**
 * IHttpLoggingRecordService.
 *
 * @author magic
 * @date 2021/9/26
 */
public interface IHttpLoggingRecordService {
    /**
     * 保存.
     *
     * @param httpLoggingModel
     */
    void saveRecord(HttpLoggingModel httpLoggingModel);

    /**
     * 查找.
     *
     * @param operator
     * @param appName
     * @param offerId
     * @param userId
     * @param pid
     * @param stepNumber
     * @param begin
     * @param end
     * @param stepName
     * @param page
     * @param limit
     * @return
     */
    List<HttpLoggingRecordModel> findList(String operator, String appName, String offerId, String userId, String pid, String stepNumber, Long begin, Long end, String stepName, Integer page, Integer limit);
}
