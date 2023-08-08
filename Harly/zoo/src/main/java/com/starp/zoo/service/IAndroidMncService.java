package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.MncPermissionModel;

import java.util.List;

/**
 * @author david
 */
public interface IAndroidMncService {
    /**
     * find mnc list
     *
     * @param country
     * @param type
     * @param mnc
     * @return
     */
    List<MncPermissionModel> findMncList(String country, String type, String mnc);

    /**
     * save mnc
     *
     * @param country
     * @param mnc
     * @param type
     * @param regex
     * @param areaCode
     * @param url
     * @param identification
     * @param oldMnc
     */
    void saveMnc(String country, String mnc, String type, String regex, String areaCode, String url, String identification, String oldMnc);

    /**
     * delete mnc
     *
     * @param id
     * @param type
     * @param mnc
     */
    void delete(String id, String type, String mnc);

    /**
     * find result
     *
     * @param param
     * @return
     */
    String findMncResult(String param);

    /**
     * generate operator
     *
     * @param sio
     * @return
     */
    String generateOp(String sio);

    /**
     * 将老逻辑redis中的数据同步到t_mnc_permission表中
     *
     * @return
     */
    void syncMysql();

    /**
     * 将t_mnc_permission表中的数据同步到redis
     *
     * @return
     */
    void syncRedis();

    /**
     * check app log status
     *
     * @param param
     * @return
     */
    JSONObject findAppStatus(String param);

}
