package com.starp.zoo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.vo.AppOpCapVO;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/7.
 */
public interface IApplicationService {

    /**
     * save ApplicationModel
     * @Author David
     * @Date 17:48 2018/12/18
     * @param  applicationModel
     * @return void
     **/
    void save(ApplicationModel applicationModel);


    /**
     * 获取所有的ApplicationModel
     * @Author David
     * @Date 17:49 2018/12/18
     * @param  status
     * @param productType
     * @return java.util.List<com.starp.zoo.entity.zoo.ApplicationModel>
     **/
    List<ApplicationModel> getAll(Integer status, String productType);


    /**
     * 获取ApplicationModel
     * @Author David
     * @Date 17:49 2018/12/18
     * @param appId
     * @return com.starp.zoo.entity.zoo.ApplicationModel
     **/
    ApplicationModel getById(String appId);


    /**
     * 删除OfferTaskModel
     * @Author David
     * @Date 17:51 2018/12/18
     * @param  appId
     * @return void
     **/
    void deleteById(String appId);


    /**
     * 更新ApplicationModel 的状态
     * @Author David
     * @Date 17:51 2018/12/18
     * @param  appId
     * @param status
     * @return void
     **/
    void updateStatus(String appId, Integer status);


    /**
     * 检查ApplicationModel是否存在
     * @Author David
     * @Date 17:52 2018/12/18
     * @param  name
     * @param type
     * @param appId
     * @return boolean
     **/
    boolean checkExists(String name, int type, String appId);

    /**
     * 检查 app 是否存在或者启用
     * @param appId
     * @return
     */
    boolean checkActive(String appId);

    /**
     * 查找list
     * @param page
     * @param limit
     * @param appNames
     * @param packageName
     * @param productType
     * @param status
     * @return
     */
    PageVO getList(int page, int limit, List<String> appNames, String packageName, String productType, Integer status);

    /**
     * 查找所有app name
     * @return
     */
    List<OptionVO> getAllAppNames();

    /**
     * 获取 package names
     * @return
     */
    List<OptionVO> getAllPackageNames();

    /**
     * 添加 app 分組
     * @param appId
     * @param tagId
     * @param saveOffer
     */
    void resetAppTag(String appId, List<String> tagId, boolean saveOffer);

    /**
     * 保存App配置
     * @param applicationModel
     * @param tagIds
     * @param opCapList
     * @param operatorTopOffer
     * @param appSdk
     * @param epmOperators
     */
    void saveConfig(ApplicationModel applicationModel, List<String> tagIds, List<AppOpCapVO> opCapList, JSONArray operatorTopOffer, JSONArray appSdk, JSONArray epmOperators);

    /**
     * 获取app配置
     * @param id
     * @return
     */
    JSONObject getAppById(String id);

    /**

     * 获取所有APP
     * @return
     */
    List<ApplicationModel> getAllAppOptions();

    /**
     * 查询 App offer 关联数量
     * @param appId
     * @return
     */
    Long getAppOfferNum(String appId);

    /**
     * 根据主键查找
     * @param token
     * @return
     */
    ApplicationModel getByIdentification(String token);

    /**
     * 修改日志状态
     * @param id
     * @param status
     */
    void updateLogStatus(String id, int status);

    /**
     * 批量删除
     * @param ids
     */
    void multiDelete(List<String> ids);

    /**
     *
     * 同步app redis
     */
    void importRedis();

    /**
     * 通过AppId , operator 查找符合条件的offer
     * @param appId
     * @param operator
     * @return
     */
    List<JSONObject> getAppOperatorOffers(String appId, String operator);

    /**
     * 从S3去SDK 文件
     * @param appId
     * @param mnc
     * @param model
     * @return
     */
    byte[] getSdkByAppId(String appId, String mnc, String model) ;

    /**
     * epm 浮动告警
     */
    void checkAlarmEpm();

    /**
     * 批量修改日志状态
     * @param ids
     * @param status
     */
    void multiUpdateLogStatus(List<String> ids, int status);

    /**
     * 检查app名称是否重复
     * @param appName
     * @return
     */
    String checkAppName(String appName);

    /**
     * getApp
     * @param appId
     * @return
     */
    ApplicationModel getAppModel(String appId);

    /**
     * 开启App日志
     * @param operators
     * @param appNames
     */
	void openAppLog(List<String> operators, List<String> appNames);

    /**
     * 获取APP日志配置信息
     * @return
     * @param appNames
     * @param operators
     * @param page
     * @param limit
     */
    PageVO getAppLogConfig(List<String> appNames, List<String> operators, Integer page, Integer limit);
}
