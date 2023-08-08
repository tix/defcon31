package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.AffiliateModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
public interface IAffiliateService {

    /**
     * 保存
     * @param affiliateModel
     */
    void save(AffiliateModel affiliateModel);

    /**
     * 主键查找
     * @param id
     * @return
     */
    AffiliateModel getById(String id);

    /**
     * 获取列表
     * @param page
     * @param limit
     * @param name
     * @return
     */
    PageVO getList(int page, int limit, String name);

    /**
     * 删除
     * @param id
     */
    void deleteById(String id);

    /**
     * 查找所有名称
     * @return
     */
    List<OptionVO> getAllNames();

    /**
     * 查找所有渠道选项
     * @return
     */
    List<OptionVO> getAll();
}
