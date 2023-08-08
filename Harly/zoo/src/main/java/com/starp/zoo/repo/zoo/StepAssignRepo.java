package com.starp.zoo.repo.zoo;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.starp.zoo.entity.zoo.StepAssignModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author charles
 */
public interface StepAssignRepo extends JpaRepository<StepAssignModel, String> {

    /**
     * getOfferIds
     * @param identification
     * @return
     */
    @Query("select t.offerId from StepAssignModel t where t.stepId=?1")
    List<String> getOfferIds(String identification);

    /**
     * 删除
     * @param stepId
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteAllByStepId(String stepId);

    /**
     * 批量删除
     *
     * @param stepIds
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteAllByStepIdIn(List<String> stepIds);

    /**
     * 查询stepids
     * @param ids
     * @return
     */
    @Query("select t.stepId from StepAssignModel t where t.offerId in (:ids)")
    List<String> getStepIds(@Param(value = "ids") List<String> ids);

    /**
     * existsByOfferIdAndStepId
     * @param offerId
     * @param stepId
     * @return
     */
    boolean existsByOfferIdAndStepId(String offerId, String stepId);
}
