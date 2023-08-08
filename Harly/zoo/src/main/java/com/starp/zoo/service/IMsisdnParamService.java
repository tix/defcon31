package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.MsisdnParamModel;
import com.starp.zoo.vo.PageVO;

/**
 * @author Charles
 * @date 2019/5/6
 * @description :
 */
public interface IMsisdnParamService {

    /**
     * 保存
     * @param msisdnParamModel
     */
    void save(MsisdnParamModel msisdnParamModel);

    /**
     * 主键查找
     * @param id
     * @return
     */
    MsisdnParamModel getById(String id);

    /**
     * 翻页获取
     * @param page
     * @param limit
     * @param country
     * @param operator
     * @return
     */
    PageVO getList(int page, int limit, String country, String operator);

    /**
     * 删除
     * @param id
     */
    void delete(String id);
}
