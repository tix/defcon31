package com.starp.zoo.service;

import com.alibaba.fastjson.JSONArray;
import com.starp.zoo.entity.zoo.AppInstallModel;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.Date;
import java.util.List;

/**
 * @author covey
 */
public interface IAppInstallService {
    /**
     * 查询所有的appName
     * @return
     */
    List<OptionVO> findAppNames();

    /**
     * 查询所有
     * @param page
     * @param limit
     * @return
     */
    PageVO findList(Integer page, Integer limit);

    /**
     * 根据条件查询
     * @param page
     * @param limit
     * @param countries
     * @param appNames
     * @param begin
     * @param end
     * @return
     */
    PageVO findByParam(Integer page, Integer limit, JSONArray countries, JSONArray appNames, Date begin,Date end);

    /**
     * 保存
     * @param appInstallModel
     */
    void save(AppInstallModel appInstallModel);

    /**
     * 修改
     * @param appInstallModel
     */
    void update(AppInstallModel appInstallModel);

    /**
     * 删除
     * @param appInstallModel
     */
    void delete(AppInstallModel appInstallModel);

    /**
     * 根据appName查找installNum
     * @param appIds
     * @param times
     * @param country
     * @return
     */
    JSONObject findInstallNumByAppId(String country,List<String> appIds,List<String> times);
}
