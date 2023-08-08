package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.OfferTaskModel;
import java.util.List;

/**
 *
 * @Author David
 * @Date 18:07 2018/12/18
 * @param
 * @return
 **/
public interface IOfferTaskService {

    /**
     * 获取ApplicationModel
     * @Author David
     * @Date 17:19 2018/12/18
     * @param
     * @return ApplicationModel
     **/
    List<ApplicationModel> findAppInfo();

    /**
     * 获取OfferModel
     * @Author David
     * @Date 17:20 2018/12/18
     * @param
     * @return offermodel
     **/
    List<OfferModel> findOfferInfo();

    /**
     * 保存OfferTaskModel
     * @Author David
     * @Date 17:20 2018/12/18
     * @param offerTaskModel
     * @return
     **/
    void save(OfferTaskModel offerTaskModel);

    /**
     * 获取OfferTaskModel
     * @Author David
     * @Date 17:20 2018/12/18
     * @param country
     * @param operator
     * @param status
     * @param appId
     * @return OfferTaskModel
     **/
    List<OfferTaskModel> findOfferTask(String country, String operator,int status,String appId);


    /**
     * 获取OfferTaskModel
     * @Author David
     * @Date 17:21 2018/12/18
     * @param appId
     * @param country
     * @param operator
     * @return OfferTaskModel
     **/
    List<OfferTaskModel> findSelectOffer(String appId,String country,String operator);


    /**
     * removeDuplicate
     * @Author David
     * @Date 17:22 2018/12/18
     * @param offerTask
     * @return OfferTaskModel
     **/
    List<OfferTaskModel> removeDuplicate(List<OfferTaskModel> offerTask);


    /**
     * get OfferTaskModel
     * @Author David
     * @Date 17:23 2018/12/18
     * @param id
     * @return OfferTaskModel
     **/
    OfferTaskModel getOfferTask(String id);


    /**
     * get offer name
     * @Author David
     * @Date 17:23 2018/12/18
     * @param offerId
     * @return String offername
     **/
    String findOfferName(String offerId);


    /**
     * delete OfferTaskModel by identification
     * @Author David
     * @Date 17:23 2018/12/18
     * @param identification
     * @return
     **/
    void deleteOfferTask(String identification);



    /**
     * change offertaskmodel status
     * @Author David
     * @Date 17:24 2018/12/18
     * @param identification
     * @param type
     * @return
     **/
    void changeType(String identification, String type);


    /**
     * get OfferTaskModel by appid
     * @Author David
     * @Date 17:25 2018/12/18
     * @param key
     * @return OfferTaskModel
     **/
    List<OfferTaskModel> findByAppId(String key);


    /**
     * get OfferModel
     * @Author David
     * @Date 17:25 2018/12/18
     * @param country
     * @param operator
     * @return OfferModel
     **/
    List<OfferModel> selectOffers(String country, String operator);
}
