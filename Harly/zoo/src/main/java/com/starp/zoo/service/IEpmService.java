package com.starp.zoo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Charles
 * @date 2019/1/22
 * @description :
 */
public interface IEpmService {


    /**
     * 统计点击信息
     *
     * @param appId
     * @param offerId
     * @param type
     */
    void incrClick(String appId, String offerId, int type);

    /**
     * 统计转化信息
     * incrAppTrans
     * incrPostBackTrans
     * incrTotalTrans
     *
     * @param appId
     * @param offerModel
     * @param revenue
     * @param isPostBack
     * @param type
     */
    void incrTrans(String appId, OfferModel offerModel, Double revenue, boolean isPostBack, int type);

    /**
     * epm 计算定时方法
     *
     * @param isRetry
     * @param retryCalendar
     */
    void executeCalculateEpm(boolean isRetry, Calendar retryCalendar);

    /**
     * 获取可用的 app 名称
     *
     * @param country
     * @param operator
     * @param partner
     * @return
     */
    List<OptionVO> getResourceNames(List<String> country, String operator, List<String> partner);


    /**
     * 获取可用的 offer，名称
     *
     * @param country
     * @param operator
     * @param partner
     * @param resourceName
     * @return
     */
    List<OptionVO> getOfferName(List<String> country, String operator, List<String> partner, List<String> resourceName);

    /**
     * 获取可用的 offer，offerId
     *
     * @param country
     * @param operator
     * @param partner
     * @param resourceName
     * @return
     */
    List<OptionVO> getSystemIds(List<String> country, String operator, List<String> partner, List<String> resourceName);

    /**
     * 获取可用的 offer，partnerOfferId
     *
     * @param country
     * @param operator
     * @param partner
     * @param resourceName
     * @return
     */
    List<OptionVO> getPartnerIds(List<String> country, String operator, List<String> partner, List<String> resourceName);


    /**
     * 获取epm 统计记录
     *
     * @param country
     * @param operator
     * @param partner
     * @param resourceNames
     * @param offerName
     * @param offerIds
     * @param begin
     * @param end
     * @param timeZone
     * @param showType
     * @param showLimitPull
     * @param belong
     * @param queryType
     * @return
     * @throws Exception
     */
    PageVO getEpmRecord(List<String> country, String operator,
                        List<String> partner, List<String> resourceNames, String offerName,
                        List<String> offerIds,
                        String begin, String end, String timeZone, Integer showType, Boolean showLimitPull, String belong, String queryType) throws Exception;

    /**
     * 获取tag 对应的offerIds
     *
     * @param tagIds
     * @return
     */
    List<String> getOfferIds(List<String> tagIds);

    /**
     * 检查转化
     */
    void checkTrans();

    /**
     * 查询近一个月收入
     *
     * @return
     */
    List getOneMonthRevenue();

    /**
     * 对比数据
     *
     * @param list
     * @param begin
     * @param end
     * @return
     */
    String comparativeData(List<JSONObject> list, String begin, String end);


    /**
     * epm 同步接口
     */
    void syncCalculateEpm();

    /**
     * 同步不可用的epm
     *
     * @throws ParseException
     */
    void importUnused() throws ParseException;

    /**
     * check UnusedOffer
     */
    void checkUnusedOffer();

    /**
     * import msisdn param
     */
    void importMsisdnParam();

    /**
     * 查找收益和实际转化数
     *
     * @param startDate
     * @param endDate
     * @param resourceNames
     * @param operators
     * @param country
     * @param times
     * @return
     */
    JSONObject getTranNumAndRevenue(String startDate, String endDate, List<String> resourceNames, List<String> operators, String country,List<String> times);

    /**
     * check redis click
     * @return
     */
    String checkEpmList();


    /**
     *
     * recheck offer epm
     * @param ip
     */
    void recheckOfferEpm(String ip);

    /**
     * offer每小时对比数据库跟redis
     * @param date
     * @return
     */
    JSONArray recheckEpmHour(String date);

    /**
     * 导入pay sub mo
     * @param shortCode
     * @param keyword
     * @param operator
     * @param partner
     */
    void savePaySubMo(String shortCode, String keyword, String operator, String partner);

    /**
     * 同步mo
     */
    void incrPayMoTrans();

    /**
     * 同步epm redis数据
     * @param appId
     * @param operator
     * @param time
     */
    void syncOnlineEpm(String appId, String operator, String time);

    /**
     * 处理epm计算
     * @param msgBodyStr
     */
    void handleEpmCalculate(String msgBodyStr);

    /**
     * 刷新offerCap
     * @param msgBodyStr
     */
    void handleUnUseOffer(String msgBodyStr);

    /**
     * 处理offer 转化告警
     * @param msgBodyStr
     */
    void handleCheckOfferTrans(String msgBodyStr);

    /**
     * 查询testOffer
     * @param offerId
     * @return
     */
    OfferModel getTestOfferInfo(String offerId);

    /**
     * 同步epm.
     */
    void syncEpmRedis();

    /**
     * 查找 auto-test-offer 的点击等信息
     * @param offerJsonList
     * @return com.alibaba.fastjson.JSONObject
     * @author Curry
     * @date 2023/5/23
     */
    JSONObject getTestOfferEpm(List<JSONObject> offerJsonList);
}
