package com.starp.zoo.service;



import com.starp.zoo.entity.zoo.PartnerModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;

/**
 * @author david
 */
public interface IPartnerService {

    /**
     * 查询所有
     * @return
     */
    List<PartnerModel> findAll();

    /**
     * 保存和修改
     * @param partnerModel
     */
    void save(PartnerModel partnerModel);

    /**
     * 删除
     * @param partnerModel
     */
    void delete(PartnerModel partnerModel);


    /**
     * 检查Id 是否存在
     * @param valueOf
     * @return
     */
    boolean existsByPartnerId(String valueOf);

    /**
     * 获取列表
     * @param page
     * @param limit
     * @param partnerName
     * @return
     */
    PageVO getList(Integer page, Integer limit, String partnerName);

    /**
     * 查询最后创建的
     * @return
     */
    PartnerModel findLast();

    /**
     * 查询所有partnerName
     * @return
     */
    List<OptionVO> findPartnerNames();

    /**
     * 查询所有partnerId
     * @return
     */
    List<OptionVO> findPartnerIds();

    /**
     * 查找id
     * @param partnerId
     * @return
     */
    PartnerModel findByPartnerId(String partnerId);

    /**
     * check exist testoffer
     * @param id
     * @return
     */
    boolean existTestOffer(String id);
}
