package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.HttpLoggingModel;
import com.starp.zoo.util.AesUtil;
import com.starp.zoo.util.DesUtil;
import com.starp.zoo.vo.HttpLoggingVO;
import com.starp.zoo.vo.SingleStepVO;

import java.util.List;

/**
 * httpLogging service.
 *
 * @author magic
 * @date 2021/7/26
 */
public interface IHttpLoggingService {

    /**
     * 保存文件到s3服务器.
     *
     * @param httpLoggingModel
     * @param desUtil
     * @param servletPath
     * @param response
     */
    void saveLoggingFileToS3(HttpLoggingModel httpLoggingModel, DesUtil desUtil, String servletPath, String response);

    /**
     * 根据条件从s3查询.
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
    List<HttpLoggingVO> findFormS3(String operator, String appName, String offerId, String userId, String pid, String stepNumber, Long begin, Long end, String stepName, Integer page, Integer limit);

    /**
     * 查询详情列表.
     *
     * @param operator
     * @param appName
     * @param offerId
     * @param userId
     * @param pid
     * @param recordDate
     * @param stepNumber
     * @param stepName
     * @return
     */
    List<SingleStepVO> findDetail(String operator, String appName, String offerId, String userId, String pid, String recordDate, String stepNumber, String stepName);

    /**
     * 删除过期的日志文件.
     */
    void deleteLoggingFile();

    /**
     * saveLoggingFileToS3New
     * @param httpLoggingModel
     * @param desUtil
     * @param servletPath
     * @param result
     * @param encodeType
     * @return void
     * @author Curry
     * @date 2022/11/1
     */
	void saveLoggingFileToS3New(HttpLoggingModel httpLoggingModel, AesUtil desUtil, String servletPath, String result, int encodeType);
}
