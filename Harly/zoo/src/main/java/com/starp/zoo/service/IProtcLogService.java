package com.starp.zoo.service;

import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;

/**
 * @author covey
 */
public interface IProtcLogService {

    /**
     * 查日志
     * @param country
     * @param operator
     * @param appName
     * @param offerId
     * @param stepId
     * @param pid
     * @param limit
     * @param page
     * @param begin
     * @param end
     * @return
     */
    PageVO findLog(String country, String operator, String appName, String offerId, String stepId, String pid, int limit, int page, long begin, long end);

    /**
     * fetchOffersWithOperators
     * @param operator
     * @return
     */
    List<OptionVO> fetchOffersWithOperators(String operator);
}
