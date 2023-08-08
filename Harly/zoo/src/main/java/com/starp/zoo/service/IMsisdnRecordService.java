package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.MsisdnRecordModel;

import java.util.List;

/**
 * IMsisdnRecordService.
 *
 * @author magic
 * @date 2021/10/29
 */
public interface IMsisdnRecordService {

    /**
     * 保存记录.
     *
     * @param msisdn
     * @param result
     * @param r1
     * @param r2
     * @param r3
     * @param r4
     * @param r5
     * @param r6
     * @param r7
     */
    void saveRecord(String msisdn, String result, String r1, String r2, String r3, String r4, String r5, String r6, String r7);

    /**
     * 获取一个电话.
     *
     * @param param
     * @return
     */
    String getOne(String param);

    /**
     * 清空当日被拉取数据.
     */
    void initCurrentIsPull();

    /**
     * 新增电话.
     *
     * @param list
     */
    void addMsisdn(List<MsisdnRecordModel> list);

    /**
     * 保存黑名单.
     *
     * @param msisdnArr
     * @param field
     */
    void saveBlack(String[] msisdnArr, String[] field);

    /**
     * 每天同步msisdn
     */
    void syncMsisdn();
}
