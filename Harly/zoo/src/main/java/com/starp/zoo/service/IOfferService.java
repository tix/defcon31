package com.starp.zoo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.SubscribeModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import com.starp.zoo.vo.TagsOptionVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/9.
 */
public interface IOfferService {

    /**
     *
     * 保存offerModel
     * @Author David
     * @Date 16:49 2018/12/18
     * @param offerModel
     * @return
     **/
    void save(OfferModel offerModel);


    /**
     *
     * 获取符合条件的offermodel
     * @Author David
     * @Date 16:48 2018/12/18
     * @param offerName
     * @param country
     * @param operator
     * @param partner
     * @param isNewOffer
     * @return OfferModel
     **/
    List<OfferModel> getAll(String offerName, String country, String operator, String partner, String isNewOffer);


    /**
     *
     * 获取OfferModel
     *  @Author David
     * @Date 16:50 2018/12/18
     * @param id
     * @return OfferModel
     **/
    OfferModel getById(String id);

    /**
     * 通过ID 删除OfferAutoScriptModel
     * @Date 16:51 2018/12/18
     * @param id
     * @return int
     **/
    Integer deleteById(String id);

    /**
     *
     * 复制offerModel
     * @Author David
     * @Date 16:52 2018/12/18
     * @param offerModel
     * @throws Exception
     * @return
     **/
    void copy(OfferModel offerModel)throws Exception;

    /**
     *
     * 获取offename list
     * @Author David
     * @Date 16:52 2018/12/18
     * @param isNewOffer
     * @return offername list
     * @throws Exception
     **/
    List<String> getOfferNameList(String isNewOffer) throws Exception;


    /**
     *
     * 获取offer Partner list
     * @Author David
     * @Date 16:53 2018/12/18
     * @param
     * @return String
     **/
    List<String> getOfferPartnerList();


    /**
     *
     * 获取符合条件的AutoScriptModel identification 跟name返回的Map
     * @Author David
     * @Date 16:54 2018/12/18
     * @param
     * @return Map
     **/
    List<Map<String, String>> getOfferScriptList();


    /**
     *
     * 获取符合条件的Offermodel
     * @Author David
     * @Date 16:55 2018/12/18
     * @param offerIds
     * @return OfferModels
     **/
    List<OfferModel> getAll(List<String> offerIds);


    /**
     * 获取符合条件的Offermodels 倒叙List
     * @param offerIds
     * @return
     */
    List<OfferModel> getAllByCreateTimeDesc(List<String> offerIds);

    /**
     *
     * 检查OfferTaskModel是否在使用
     * @Author David
     * @Date 16:56 2018/12/18
     * @param id
     * @param country
     * @param operator
     * @return ApplicationModel
     **/
    ApplicationModel checkUse(String id, String country, String operator);


    /**
     *
     * 删除OfferTaskModel
     * @Author David
     * @Date 16:58 2018/12/18
     * @param offerId
     * @return
     **/
    void deleteAppRelate(String offerId);

    /**
     *
     * 是否有满足条件的offerModel
     * @Author David
     * @Date 16:58 2018/12/18
     * @param name
     * @param type
     * @param id
     * @return boolean
     **/
    boolean checkExists(String name, int type, String id);

    /**
     *
     * 是否有满足条件的offerModel
     * @Author David
     * @Date 16:58 2018/12/18
     * @param offerId
     * @param type
     * @param id
     * @return boolean
     **/
    boolean checkIdExists(String offerId, int type, String id);


    /**
     *
     * 查找符合条件的OfferModel
     * @Author David
     * @Date 17:00 2018/12/18
     * @param offerId
     * @return OfferModel
     **/
    OfferModel getByOfferId(String offerId);

    /**
     * 获取页面可用的数据
     * @param page
     * @param limit
     * @param ids
     * @param names
     * @param emails
     * @param country
     * @param operator
     * @param partner
     * @param tag
     * @param offerId
     * @param partnerOfferId
     * @param status
     * @param belong
     * @return
     */
    PageVO getPageList(int page, int limit, List<String> ids, List<String> names,List<String> emails, String country,
                       String operator, String partner, String tag, String offerId, String partnerOfferId, Integer status, String belong);

    /**
     * 获取所有的合作商选项
     * @return
     */
    List<OptionVO> getPartnerOptions();

    /**
     * 获取 offer 配置
     * @param id
     * @return
     */
    JSONObject getConfigModel(String id);

    /**
     * 跨过 caffeine 取 offer
     * @param offerId
     * @return com.starp.app.entity.OfferModel
     * @author Curry
     * @date 2023/5/9
     */
    OfferModel getOfferModelByRedis(String offerId);



    /**
     * save all config
     * @param offerModel
     * @param tagsOptionVO
     * @return
     */
    String saveAllConfig(OfferModel offerModel, TagsOptionVO tagsOptionVO);

    /**
     * 删除配置
     * @param id
     */
    void delete(String id);

    /**
     * 获取默认的任务持续时间
     * @param country
     * @param operator
     * @return
     */
    int getDefaultDuration(String country, String operator);

    /**
     * 检查 offerId 是否唯一
     * @param offerId
     * @return
     */
    boolean checkUniqueId(String offerId);

    /**
     * 检查 offerId 是否唯一
     * @param url
     * @return
     */
    String checkUniqueUrl(String url);

    /**
     * 检查
     * @param name
     * @return
     */
    boolean checkUniqueName(String name);

    /**
     * 检查同一国家运营商下 某个合作商的 offerId 唯一
     * @param partner
     * @param country
     * @param operator
     * @param partnerOfferId
     * @return
     */
    Long checkUniquePartnerOfferId(String country, String operator, String partner, String partnerOfferId);

    /**
     * 获取offerName
     * @param query
     * @return
     */
    List<OptionVO> getOfferNameOptions(String query);

    /**
     * 获取所有的 offer
     * @param query
     * @return
     */
    List<JSONObject> getAll(String query);

    /**
     * getAllOptions
     * @param query
     * @return
     */
    List<OptionVO> getAllOptions(String query);

    /**
     * 根据 appid 找到 offer
     * @param type
     * @param categoryId
     * @return
     */
    List<OfferModel> findOffersByCategoryId(int type, String categoryId);

    /**
     * 批量修改
     * @param ids
     * @param status
     */
    void changeStatus(List<String> ids, String status);

    /**
     * 获取 offerIds
     * @param query
     * @return
     */
    List<OptionVO>  getOfferIdOptions(String query);

    /**
     * 获取parentOfferId
     * @param query
     * @return
     */
    List<OptionVO>  getPartnerOfferIdOptions(String query);

    /**
     * 根据国家运营商查找
     * @param country
     * @param operators
     * @return
     */
    List<OfferModel> getOffers(String country, List<String> operators);

    /**
     * 获取zoo offerModel
     * @param offerId
     * @return
     */
    OfferModel findOfferId(String offerId);


    /**
     * 查找offer
     * @param query
     * @return
     */
    List<OptionVO> getZooOfferNameOptions(String query);


    /**
     * 查找相同上游相同流程下提示选择的参数
     * @param partner
     * @param type
     * @return
     */

    Map<String, List<OptionVO>> getParamTips(String partner, String type);

    /**
     * 自动更改状态定时任务接口
     */
    void autoUpdate();

    /**
     * 根据offerName 跟 appName统计JS被执行次数
     * @param offer
     * @param appName
     * @param beginTime
     * @param endTime
     * @param distinct
     * @return
     */
    PageVO countJs(String offer, List<String> appName, long beginTime, long endTime,boolean distinct);

    /**
     * 根据offerName script统计userId
     * @param offerName
     * @param script
     * @param distinctUser
     * @param beginTime
     * @param appNames
     * @param endTime
     * @return
     */
    PageVO getUserList(String offerName,List<String> appNames, String script, boolean distinctUser, long beginTime, long endTime);


    /**
     * 超cap关闭 offer的状态
     * @param payChannel
     * @param operator
     * @param shortCode
     * @param keyword
     */
    void updateStatus(String payChannel, String operator, String shortCode, String keyword);

    /**
     * changeStatus
     * @param identification
     * @param status0
     */
    void changeStatus(String identification, int status0);

    /**
     *  批量修改日志状态
     * @param ids
     * @param status
     */
    void changeLog(List<String> ids, int status);

    /**
     * findClickIdAndMsisdn
     * @param offerId
     * @param num
     * @return
     */
    List<SubscribeModel> findClickIdAndMsisdn(String offerId, int num);

    /**
     * sendOffer到队列
     * @param id
     */
    void sendOfferMaxPullQueue(String id);

    /**
     * update redis
     * @param offerModel
     * @param offerId
     * @param appId
     * @param redisAppCap
     * @param redisOfferCap
     * @param pullCount
     * @param methodName
     */
    void updateOfferRedis(OfferModel offerModel, String offerId, String appId,Integer redisOfferCap,Integer redisAppCap,Integer pullCount,String methodName);




    /**
     * update redis
     */
    void initFilterRedis();

    /**
     * import today redis
     */
    void importTodayRedis();

    /**
     * fetch stack offer
     * @param operator
     * @param country
     * @param closeCount
     * @return
     */
    JSONObject fechStackOffer(String operator, String country, Integer closeCount);

    /**
     * init offer redis
     */
    void initOfferRedis();

    /**
     * check app operator epm
     * @param msgBody
     */
    void checkAlarmEpm(String msgBody);

    /**
     * get test offer
     * @param partnerId
     * @return
     */
    OfferModel getTestOffer(String partnerId);

    /**
     * save test offer
     * @param offerModel
     */
    void saveTestOffer(OfferModel offerModel);


    /**
     * format test
     * @param partnerId
     * @param ipAddress
     * @return
     * @throws Exception
     */
    String formatConfig(String partnerId, String ipAddress) throws Exception;

    /**
     * check offer in Time
     */
    void checkOfferInTime();



    /**
     * find offer model
     * @param offerId
     * @return
     */
    OfferModel getOfferModel(String offerId);

    /**
     * handle auto update
     * @param msgBody
     */
    void handleAutoStart(String msgBody);

    /**
     * handleAutoUpdateRedis
     * @param offerModel
     */
    void handleAutoUpdateRedis(OfferModel offerModel);

    /**
     * update current hour epm redis
     */
    void initEpmCurrentHourRedis();

    /**
     * 删除超时时间的epm key
     */
    void deleteTimoutRedis();

    /**
     * 删除对应的key
     * @param key
     */
    void deleteTimoutKey(String key);

    /**
     * 自动更新offer跑量时间
     */
    void autoRunTime();

    /**
     * 根据offer配置自动更新shortcode
     */
    void updateOfferShortCode();

    /**
     * 检查mo cr
     */
    void checkOfferMoCr();

    /**
     * 根据新计算的 ARPU 平均值 更新 offer 的 init_epm 值
     * @param arpuAvgVoList
     */
	void updateInitEpm(List<JSONObject> arpuAvgVoList);

    /**
     * 计算智能栈
     * @param offerNames
     * @return
     */
    JSONObject getAutoStackList(JSONArray offerNames);

    /**
     * 删除智能栈列表中的offer，不影响系统offer信息
     * @param offerId
     */
	void deleteAutoStackOffer(String offerId);

    /**
     * 获取智能栈配置信息
     * @param appNames
     * @param opeAndOfferList
     * @return
     */
    JSONObject getAutoStackConfigInfo(List<String> appNames, List<JSONObject> opeAndOfferList);

    /**
     * 智能栈刷新offer的新增Mo数
     * @param appName
     * @throws Exception
     */
    void refreshOfferMo(String appName) throws Exception;

    /**
     * 释放智能栈指定app中的offer
     * @param appName
     * @return
     */
    boolean freeOfferInApp(String appName);

    /**
     * 获取智能栈列表上次选择内容
     * @return
     */
    List<String> getOldAutoStackOfferNames();

    /**
     * 清空智能栈列表所有数据
     */
	void deleteRedisAutoStackList();

    /**
     * 缓存查找offer
     * @param offerId
     * @return
     */
    OfferModel findOfferModel(String offerId);

    /**
     * 处理offer跑量
     * @param msgBodyStr
     */
    void handleOfferRunTime(String msgBodyStr);

    /**
     * 同步测试offer
     */
    void syncTestOffer();

    /**
     * 删除过期的key
     * @param key
     */
    void deleteAlarmTimeOutKey(String key);

    /**
     * 删除appEvent redis
     */
    void deleteAppEventRedis();

    /**
     * 删除app key
     * @param key
     */
    void deleteEventRedis(String key);

    /**
     * 获取 epmList redis 中的 app 与 offer 关联关系
     * @param appNames
     * @return
     */
    JSONObject getEpmListRedis(List<String> appNames);

    /**
     * 指定时间开启关闭offer
     * @param retryTime
     * @return void
     * @author Curry
     * @date 2022/11/19
     */
    void autoUpdateDate(Integer retryTime);

    /**
     * 指定时间开关offer
     * @param msgBodyStr
     * @param retryTime
     * @return void
     * @author Curry
     * @date 2022/11/19
     */
    void handleAutoStartRetry(String msgBodyStr, Integer retryTime);

    /**
     * 提前释放 offer
     * @param offerId
     * @param appName
     * @return boolean
     * @author Curry
     * @date 2022/12/9
     */
	boolean advanceFreeOffer(String offerId, String appName);

    /**
     * 获取智能栈可用 offer OptionVO
     * @return void
     * @author Curry
     * @date 2022/12/12
     */
    List<OptionVO> getUsableOfferOption();

    /**
     * 获取可用智能栈 测试组
     * @return java.util.List<com.starp.zoo.vo.OptionVO>
     * @author Curry
     * @date 2022/12/12
     */
    List<String> getUsableGroupOption();

    /**
     * 手动选择offer加入智能栈测试组
     * @return boolean
     * @author Curry
     * @date 2022/12/12
     * @param offerIds
     * @param testGroup
     */
    boolean handleOfferAddGroup(List<String> offerIds, String testGroup);

    /**
     *
     * 保存 epmList info
     * @return void
     * @author Curry
     * @date 2023/2/17
     */
	void saveEpmListInfo();

    /**
     * 批量保存测试offer
     * @param jsonArray
     * @author Curry
     * @date 2023/5/17
     */
    void multiSaveOfferAutoTest(JSONArray jsonArray);

    /**
     * 获取测试offer列表
     * @param country
     * @param operator
     * @param partner
     * @param testStatus
     * @param page
     * @param limit
     * @param begin
     * @param end
     * @return void
     * @author Curry
     * @date 2023/5/22
     */
    JSONObject getAutoTestOfferList(List<String> country, List<String> operator, List<String> partner, List<Integer> testStatus, Integer page, Integer limit, String begin, String end);

    /**
     * 删除测试offerRedisID
     * @param id
     * @return void
     * @author Curry
     * @date 2023/5/25
     */
	void deleteTestOfferRedis(String id);
}
