package com.starp.zoo.service;

import com.alibaba.fastjson.JSONArray;
import com.starp.zoo.entity.zoo.OfferStepModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;

/**
 * @author charles
 */
public interface IOfferStepService {

    /**
     * 保存
     * @param offerStepModel
     */
    void save(OfferStepModel offerStepModel);

    /**
     * 获取一个
     * @param id
     * @return
     */
    OfferStepModel getById(String id);

    /**
     * 查询列表
     * @param country
     * @param operator
     * @param partner
     * @param offerName
     * @param systemOfferId
     * @param partnerOfferId
     * @param tagId
     * @param stepName
     * @param page
     * @param limit
     * @return
     */
    PageVO getList(String country, String operator, String partner,
                   String offerName, String systemOfferId, String partnerOfferId,
                   String tagId, String stepName, int page, int limit);

    /**
     * 单个删除
     * @param id
     * @return
     */
    String deleteById(String id);

    /**
     * 批量删除
     * @param ids
     */
    void multiDelete(List<String> ids);

    /**
     * 获取名称
     * @param query
     * @return
     */
    List<OptionVO> getNames(String query);

    /**
     * check unique name
     * @param name
     * @return
     */
    boolean checkUniqueName(String name);

    /**
     * initRedis
     */
    void initRedis();

    /**
     * handleMultiToAdd
     * @param offerNames
     * @param steps
     */
    void handleMultiToAdd(JSONArray offerNames, JSONArray steps);

    /**
     * lock
     * @param id
     */
    void lock(String id);

    /**
     * unlock
     * @param id
     */
    void unlock(String id);

    /**
     * updateRedis
     */
    void updateRedis();
}
