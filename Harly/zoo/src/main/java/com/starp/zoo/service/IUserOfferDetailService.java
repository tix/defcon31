package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.UserOfferExcelModel;

import java.util.List;

/**
 * @author magic
 * @date 2020/12/7
 */
public interface IUserOfferDetailService {
    /**
     * 获取用户offer详情信息
     *
     * @param tableName
     * @param app
     * @param operator
     * @param userCount
     * @param page
     * @param createDate
     * @return
     */
    JSONObject getDetail(String tableName, String app, String operator, String userCount, int page, String createDate);

    /**
     * 导出数据
     *
     * @param tableName
     * @param app
     * @param operator
     * @param userCount
     * @param page
     * @param createDate
     */
    void exportData(String tableName, String app, String operator, String userCount, int page, String createDate);

    /**
     * 获取下载列表
     *
     * @return
     */
    List<UserOfferExcelModel> getExcelList();
}
