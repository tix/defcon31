package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.OfferAutoScriptModel;
import com.starp.zoo.entity.zoo.ScriptModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/3/4
 * @description :
 */
public interface IScriptService {
    /**
     * 获取选项
     * @param country
     * @return
     */
    List<OptionVO> getOptions(String country);

    /**
     *获取脚本信息
     * @param id
     * @return
     */
    ScriptModel getScript(String id);

    /**
     * 保存脚本
     * @param scriptModel
     * @param offerIds
     * @return
     */
    ScriptModel saveScript(ScriptModel scriptModel, List<String> offerIds);

    /**
     * 获取列表
     * @param page
     * @param limit
     * @param type
     * @param name
     * @param country
     * @param eventType
     * @param scripts
     * @return
     */
    PageVO getScriptList(int page, int limit, String type, String name, String country, String eventType, List<String> scripts);

    /**
     * 删除脚本
     * @param id
     */
    void delete(String id);

    /**
     * 批量删除
     * @param ids
     */
    void multiDelete(List<String> ids);

    /**
     * 模糊查询名称
     * @param query
     * @return
     */
    List<OptionVO> findScriptName(String query);

    /**
     * 查找关联关系
     * @param id
     * @return
     */
    List<OfferAutoScriptModel> getOfferScript(String id);

    /**
     * 根据 offer 主键，类型 获取js 列表
     * @param offerId
     * @return
     */
    List<ScriptModel> findScriptByOfferId(String offerId);

    /**
     * 获取 App 自动匹配的 脚本
     * @param url
     * @param country
     * @return
     */
    ScriptModel getAppScript(String url, String country);

    /**
     * 正则去重
     * @param regular
     * @param country
     * @param identification
     * @param type
     * @param eventType
     * @return
     */
    Map<String,Object> checkExistRegular(String regular, String country,String identification,String type,String eventType);

    /**
     * 查询所有JS 名称
     * @return
     */
    List<OptionVO> findAllNames();

    /**
     * 根据name查询JS
     * @param name
     * @return
     */
    ScriptModel findJsByName(String name);

    /**
     * 查询script内容
     * @param country
     * @param name
     * @param type
     * @param eventType
     * @param scripts
     * @return
     */
    List<OptionVO> fetchScriptsDetail(String country, String name, String type, String eventType, List<String> scripts);

    /**
     * 获取online js
     * @param type
     * @return
     */
    List<ScriptModel> getOnlineJsList(String type);

    /**
     * 获取选框
     * @return
     */
    List<OptionVO> fetchScriptOption();

    /**
     *根据offerNames查询
     * @param offerNames
     * @return
     */
    List<JSONObject> fetchResult(List<String> offerNames);

    /**
     * 获取Script json
     * @param url
     * @param country
     * @return
     */
    JSONObject getAppScriptJson(String url, String country);

    /**
     * 根据国家获取script
     * @param country
     * @return
     */
    List<ScriptModel> findScriptListByCountry(String country);
}
