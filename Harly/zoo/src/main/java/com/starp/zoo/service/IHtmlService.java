package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.HtmlInfoModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.Date;
import java.util.List;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/5/16.
 */
public interface IHtmlService {

    /**
     * save htmlInfoModel
     *
     * @param htmlInfoModel
     * @return
     * @throws Exception
     * @Author David
     * @Date 17:27 2018/12/18
     **/
    HtmlInfoModel saveModel(HtmlInfoModel htmlInfoModel) throws Exception;


    /**
     * get HtmlInfo
     *
     * @param appId
     * @param offerId
     * @param draw
     * @param start
     * @param length
     * @return JSONObject
     * @throws Exception
     * @Author David
     * @Date 17:27 2018/12/18
     **/
    JSONObject getHtmlInfoList(String appId, String offerId, Integer draw, Integer start, Integer length) throws Exception;


    /***
     * init html info
     * @Author David
     * @Date 17:42 2018/12/18
     * @param  appName
     * @param offerName
     * @param draw
     * @param start
     * @param length
     * @param startDate
     * @param endDate
     * @param userId
     * @return com.alibaba.fastjson.JSONObject
     **/
    JSONObject initHtmlInfo(String appName, String offerName, String userId, String startDate, String endDate, Integer draw, Integer start, Integer length);


    /***
     * search html info
     * @Author David
     * @Date 17:43 2018/12/18
     * @param  appName
     * @param offerName
     * @return java.util.List<com.starp.zoo.entity.zoo.HtmlInfoModel>
     **/
    List<HtmlInfoModel> searchHtmlInfo(String appName, String offerName);


    /***
     * get html info
     * @Author David
     * @Date 17:43 2018/12/18
     * @param id
     * @return com.starp.zoo.entity.zoo.HtmlInfoModel
     **/
    HtmlInfoModel getHtmlInfo(String id);


    /***
     * get all ApplicationModel
     * @Author David
     * @Date 17:45 2018/12/18
     * @param
     * @return java.util.List<com.starp.zoo.entity.zoo.ApplicationModel>
     **/
    List<ApplicationModel> initAppName();

    /**
     * get all OfferModel
     *
     * @param
     * @return java.util.List<com.starp.zoo.entity.zoo.OfferModel>
     * @Author David
     * @Date 17:46 2018/12/18
     **/
    List<OptionVO> initOfferName();

    /**
     * 根据条件查询
     *
     * @param page
     * @param limit
     * @return
     */
    PageVO<HtmlInfoModel> findAll(Integer page, Integer limit);

    /**
     * 查询所有/根据时间查询
     *
     * @param appName
     * @param offerName
     * @param userId
     * @param page
     * @param limit
     * @param beginTime
     * @param endTime
     * @param country
     * @return
     */
    PageVO<HtmlInfoModel> findBy(String appName, String offerName, String userId, Integer page, Integer limit, Date beginTime, Date endTime, String country);

    /**
     * 查询htmlinfo列表.
     *
     * @param appName
     * @param offerName
     * @param userId
     * @param country
     * @param page
     * @param limit
     * @param datetime
     * @return
     */
    PageVO getHtmlList(String appName, String offerName, String userId, String country, Integer page, Integer limit, String datetime);

    /**
     * 查询所有城市
     *
     * @return
     */
    List<OptionVO> initCountry();

    /**
     * save msisdn
     *
     * @param htmlInfoModel
     */
    void handleSaveMsisdn(HtmlInfoModel htmlInfoModel);
}
