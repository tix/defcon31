package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.OfferTaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @Author David
 * @Date 18:00 2018/12/18
 **/
public interface OfferTaskRepo extends JpaRepository<OfferTaskModel,String>, JpaSpecificationExecutor<OfferTaskModel> {

    /**
     * 获取所有符合条件的OfferTaskModel
     *
     * @param appId
     * @param country
     * @param operator
     * @return OfferTaskModel
     */
    List<OfferTaskModel> findByAppIdAndCountryAndOperatorOrderByLevelAsc(String appId,String country,String operator);



    /**
     * 获取符合条件的OfferTaskModel
     *
     * @param id
     * @return OfferTaskModel
     */
    OfferTaskModel findByIdentification(String id);



    /**
     * 更新OfferTaskModel状态为暂停
     *
     * @param id
     * @param status
     */
    @Modifying
    @Query("update OfferTaskModel p set p.status = ?2 where p.identification =?1")
    void updateType(String id,int status);


    /**
     * 删除OfferTaskModel
     *
     * @param appId
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByAppId(String appId);


    /**
     * 获取所有符合条件的OfferTaskModel
     *
     * @param id
     * @return OfferTaskModel
     */
    List<OfferTaskModel> findAllByOfferId(String id);



    /**
     * 获取所有符合条件的OfferTaskModel
     *
     * @param key
     * @return OfferTaskModel
     */
    List<OfferTaskModel> findAllByAppIdOrderByLevelAsc(String key);


    /**
     * 是否存在符合条件的OfferTaskModel
     *
     * @param offerId
     * @return boolean
     */
    boolean existsByOfferId(String offerId);


    /**
     * 查找符合条件的OfferTaskModel
     *
     * @param offerId
     * @return OfferTaskModel
     */
    OfferTaskModel findFirstByOfferId(String offerId);



    /**
     * 删除OfferTaskModel
     *
     * @param offerId
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByOfferId(String offerId);

    /**
     * 获取开启的任务组
     * @param key
     * @param app
     * @return
     */
    List<OfferTaskModel> findAllByAppIdAndStatusOrderByLevelAsc(String key, int app);

    /**
     * 批量删除
     * @param ids
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByAppIdIn(List<String> ids);
}
