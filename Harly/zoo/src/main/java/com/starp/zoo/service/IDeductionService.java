package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.DeductionModel;
import com.starp.zoo.vo.OptionVO;

import java.util.Date;
import java.util.List;

/**
 * @author covey
 */
public interface IDeductionService {
    /**
     * 获取所有operator
     * @return
     */
    List<OptionVO> fetchOperators();

    /**
     * 获取所有partner
     * @return
     */
    List<OptionVO> fetchPartners();

    /**
     * 获取所有offer
     * @return
     */
    List<OptionVO> fetchOffers();

    /**
     * 根据条件查询
     * @param country
     * @param partner
     * @param offerName
     * @param operator
     * @param begin
     * @param end
     * @return
     */
    List<DeductionModel> find(String country, String partner, String offerName, String operator, Long begin, Long end);

    /**
     * 根据国家查询operator
     * @param country
     * @return
     */
    List<OptionVO> findOpByParam(String country);

    /**
     * 根据运营商查询partner
     *
     * @param country
     * @param operator
     * @return
     */
    List<OptionVO> findParByParam(String country, String operator);

    /**
     * 根据上游查offer
     * @param country
     * @param operator
     * @param partner
     * @return
     */
    List<OptionVO> findOffByParam(String country, String operator, String partner);
}
