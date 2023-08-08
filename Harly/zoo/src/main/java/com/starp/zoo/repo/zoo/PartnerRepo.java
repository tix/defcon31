package com.starp.zoo.repo.zoo;


import com.starp.zoo.entity.zoo.PartnerModel;
import com.starp.zoo.entity.zoo.ResultPartnerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author covey
 */
public interface PartnerRepo extends JpaRepository<PartnerModel, String>, JpaSpecificationExecutor<PartnerModel> {

    /**
     * 根据ID查询
     * @param partnerId
     * @return
     */
    PartnerModel findFirstByPartnerId(String partnerId);

    /**
     * 删除
     * @param partnerId
     */
    void deleteByPartnerId(String partnerId);


    /**
     * 检查唯一性
     * @param partnerId
     * @return
     */
    boolean existsByPartnerId(String partnerId);

    /**
     * 查询出最晚创建的一个
     * @return
     */
    PartnerModel findTopByOrderByCreateTimeDesc();

    /**
     * 查询所有partnerName
     * @return
     */
    @Query(value = "select distinct t.partnerName from PartnerModel t")
    List<String> findPartnerNames();

    /**
     * 查询所有partnerId
     * @return
     */
    @Query(value = "select distinct t.partnerId from PartnerModel t")
    List<String> findPartnerIds();

    /**
     * 根据ID查询PartnerModel
     * @param partnerId
     * @return
     */
    PartnerModel findByPartnerId(String partnerId);


}
