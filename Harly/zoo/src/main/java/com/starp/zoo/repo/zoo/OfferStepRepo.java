package com.starp.zoo.repo.zoo;

import com.alibaba.fastjson.JSONArray;
import com.starp.zoo.entity.zoo.OfferStepModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author charles
 */
public interface OfferStepRepo extends JpaRepository<OfferStepModel, String>, JpaSpecificationExecutor<OfferStepModel> {

    /**
     * 批量查找
     * @param ids
     * @return
     */
    List<OfferStepModel> findByIdentificationIn(List<String> ids);

    /**
     * 批量获取名称
     * @param query
     * @return
     */
    List<OfferStepModel> findByStepNameLike(String query);

    /**
     * 批量删除
     * @param ids
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "delete from OfferStepModel t where t.identification in (:ids)")
    void multiDelete(@Param("ids") List<String> ids);

    /**
     * 检查名称
     * @param name
     * @return
     */
    boolean existsByStepName(String name);

    /**
     * findByNameList
     * @param steps
     * @return
     */
    @Query(value = "select t from OfferStepModel t where t.stepName in (:steps)")
    List<OfferStepModel> findByNameList(JSONArray steps);

    /**
     * update lock
     * @param id
     * @param lockStatus
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("update OfferStepModel t set t.lockStatus=?2 where t.identification=?1")
    void updateLock(String id, String lockStatus);}
