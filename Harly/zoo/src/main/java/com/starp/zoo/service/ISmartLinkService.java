package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.AffSmartLinkModel;
import com.starp.zoo.entity.zoo.DeductConfigModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
public interface ISmartLinkService {

    /**
     * 主键查找
     * @param appId
     * @return
     */
    AffSmartLinkModel getById(String appId);

    /**
     * 删除
     * @param appId
     */
    void deleteById(String appId);

    /**
     * 获取列表
     * @param page
     * @param limit
     * @param name
     * @param affId
     * @param id
     * @return
     */
    PageVO getList(int page, int limit, String name, String affId, String id);

    /**
     * 获取所有smartLink 名称
     * @return
     */
    List<OptionVO> getAllSmartLinkNames();

    /**
     * 保存 配置
     * @param affSmartLinkModel
     */
    void saveConfig(AffSmartLinkModel affSmartLinkModel);

    /**
     * 获取model
     * @param smlId
     * @return
     */
    AffSmartLinkModel getBySmartLinkId(String smlId);

    /**
     * 获取所有id
     * @return
     */
    List<OptionVO> getAllSmartLinkIds();

    /**
     * 获取初始的扣量model
     * @param tagIds
     * @return
     */
    List<DeductConfigModel> getInitDeductModels(List<String> tagIds);

    /**
     * 检查唯一 id
     * @param offerId
     * @return
     */
    boolean checkUniqueId(String offerId);
}
