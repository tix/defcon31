package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.OfferAutoScriptModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/9.
 */
@Repository
public interface OfferAutoScriptRepo extends JpaRepository<OfferAutoScriptModel, String> {

    /**
      * 删除符合条件的OfferAutoScriptModel
      * @param identification
      * @return
      */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByOfferId(String identification);

    /**
      * 查找符合条件的OfferAutoScriptModel
      * @param identification
      * @return OfferAutoScriptModel
      */
    List<OfferAutoScriptModel> findByOfferId(String identification);


    /**
      * 查找符合条件的OfferAutoScriptModel
      * @param offerIds
      * @return OfferAutoScriptModel
      */
    @Query(value = "select * from t_offer_auto_script where offer_id in (:offerIds)", nativeQuery = true)
    List<OfferAutoScriptModel> findQuery(@Param("offerIds") List<String> offerIds);


    /**
     * find auto script
     * @param identification
     * @return
     */
    List<OfferAutoScriptModel> findByAutoScriptId(String identification);

    /**
     * delete offerscript model
     * @param offerId
     * @param identification
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByOfferIdAndAutoScriptId(String offerId, String identification);

    /**
     * find all model
     * @param identification
     * @return
     */
    List<OfferAutoScriptModel> findAllByAutoScriptId(String identification);

    /**
     * 根据脚本主键删除
     * @param identification
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByAutoScriptId(String identification);
}
