package com.starp.zoo.repo.zoo;

import com.alibaba.fastjson.JSONArray;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.ResultOfferModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/9.
 */
public interface OfferRepo extends JpaRepository<OfferModel, String>, JpaSpecificationExecutor<OfferModel> {

    /**
     * 查询所有的offerName
     * @return
     */
    @Query(value = "select distinct t.offerName from OfferModel t")
    List<String> findOfferNames();

    /**
     * 根据城市查询
     * @param country
     * @return
     */
    @Query(value = "select distinct t.offerName from OfferModel t where country = ?1")
    List<String> findOfferNamesByCountry(String country);

    /**
     * 查找符合条件的OfferModel
     * @param id
     * @return OfferModel
     */
    OfferModel findFirstByIdentification(String id);


    /**
     * 查找符合条件的OfferModel
     * @param offerIds
     * @return OfferModel
     */
    @Query(value = "select * from t_offer where identification in (:offerIds) order by createTime desc", nativeQuery = true)
    List<OfferModel> findQuery(@Param("offerIds") List<String> offerIds);


    /**
     * findUsedQuery
     * @param offerIds
     * @return
     */
    @Query(value = "select stack from t_offer where identification in (SELECT t1.offer_id from t_offer_tag t1 left join t_tag t2 on t2.identification = t1.tag_id where t1.offer_id in (:offerIds) and t2.tag_type=1 and t1.tag_id is not null ) order by createTime desc", nativeQuery = true)
    List<String> findUsedQuery(@Param("offerIds") List<String> offerIds);



    /**
      * 统计offerModel
      * @param name
      * @return 数量
      */
    int countByOfferName(String name);

    /**
      * 查找符合条件的OfferModel
      * @param name
      * @return OfferModel
      */
    OfferModel findFirstByOfferName(String name);

    /**
     * 查找符合条件的OfferModel
     * @param url
     * @return OfferModel
     */
    OfferModel findFirstByUrl(String url);


    /**
      * 统计offerModel
      * @param offerId
      * @return offermodel 数量
      */
    int countByOfferId(String offerId);


    /**
      * 查找符合条件的OfferModel
      * @param offerId
      * @return OfferModel
      */
    OfferModel findFirstByOfferId(String offerId);

    /**
     * 根据网盟名称与offerid 进行查询
     * @param partner
     * @param offerId
     * @return
     */
    OfferModel findFirstByPartnerAndOfferId(String partner, String offerId);


    /***
     * 通过中间表找到对应的offer
     * @Author David
     * @Date 18:28 2019/3/6
     * @param  offerId
     * @return com.starp.zoo.entity.zoo.OfferModel
     **/
    OfferModel findByIdentification(String offerId);

   /***
    * find Offer
    * @Author David
    * @Date 18:04 2019/3/8
    * @param  name
    * @return java.util.List<com.starp.zoo.entity.zoo.OfferModel>
    **/
   @Query("SELECT p FROM OfferModel p WHERE p.offerName LIKE CONCAT('%',:name,'%')")
   List<OfferModel> findOfferNameLike(@Param("name") String name);

    /**
     * 找到app关联的所有开启的 offer
     * @param type
     * @param categoryId
     * @return
     */
    @Query(value = "SELECT t.* FROM t_offer t  WHERE t.status=1 AND t.identification IN (SELECT DISTINCT(t1.offer_id) FROM t_offer_tag t1 WHERE t1.tag_id IN (SELECT t2.tag_id FROM t_category_tag t2 WHERE t2.type=?1 AND t2.category_id=?2))", nativeQuery = true)
    List<OfferModel> findQuery(int type, String categoryId);

    /**
     * 查找默认 duration 的默认model
     * @param country
     * @param operator
     * @return
     */
    OfferModel findFirstByCountryAndOperatorAndDurationNotNull(String country, String operator);

    /**
     * 批量修改状态
     * @param status
     * @param ids
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update t_offer t set t.status=:status where t.identification in(:ids)", nativeQuery = true)
    void updateStatus(@Param(value = "status") String status, @Param(value = "ids") List<String> ids);

    /**
     * 修改测试状态
     * @param testStatus
     * @param identification
     * @return void
     * @author Curry
     * @date 2023/5/24
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("UPDATE OfferModel o SET o.testStatus = :testStatus WHERE o.identification = :identification")
    void updateTestStatusByIdentification(@Param("testStatus") Integer testStatus, @Param("identification") String identification);

    /**
     * 查找开启告警的 offer 配置
     * @param status
     * @param alarmStatus
     * @return
     */
    List<OfferModel> findByStatusAndAlarmStatus(int status, int alarmStatus);

    /**
     * 查询城市
     * @return
     */
    @Query(value = "select distinct t.country from OfferModel t")
    List<String> findCountries();

    /**
     * 根据自动开启状态查找
     * @param status
     * @return
     */
    List<OfferModel> findByAutoStatus(int status);

    /**
     * 查询所有运营商
     * @return
     */
    @Query(value = "select distinct t.operator from OfferModel t")
    List<String> fetchOperators();

    /**
     * 查询所有partner
     * @return
     */
    @Query(value = "select distinct t.partner from OfferModel t")
    List<String> fetchPartners();

    /**
     * 根据国家查询operator
     * @param country
     * @return
     */
    @Query(value = "select distinct t.operator from OfferModel t where t.country=?1")
    List<String> findOpByParam(String country);

    /**
     * 根据国家operator查询partner
     * @param country
     * @param operator
     * @return
     */
    @Query(value = "select distinct t.partner from OfferModel t where t.country=?1 and t.operator=?2")
    List<String> findParByParam(String country, String operator);

    /**
     * 根据国家operator partner查询offer
     * @param country
     * @param operator
     * @param partner
     * @return
     */
    @Query(value = "select distinct t.offerName from OfferModel t where t.country=?1 and t.operator=?2 and t.partner=?3")
    List<String> findOffByParam(String country, String operator, String partner);

    /**
     * 查找 匹配的 所有 offer
     * @param tagIds
     * @return
     */
    @Query(value = "select t1.* from t_offer t1 left join t_offer_tag t2 on t1.identification=t2.offer_id where t2.tag_id in (:tagIds)", nativeQuery = true)
    List<OfferModel> queryInTagIds(@Param("tagIds") List<String> tagIds);

    /**
     * 查 offer 运营商
     * @param offerId
     * @return
     */
    @Query("select t.operator from OfferModel t where t.identification=?1")
    String queryCarrier(String offerId);

    /**
     * 根据offerNames查询
     * @param offerNames
     * @return
     */
    @Query(value = "select t  from OfferModel t where t.offerName in (:offerNames)")
    List<OfferModel> findByOfferNames(@Param("offerNames") List<String> offerNames);


    /**
     * 更新status
     * @param payChannel
     * @param operator
     * @param shortCode
     * @param keyword
     */
    @Modifying(clearAutomatically = true)
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update t_offer t set t.status = 0 where t.partner = ?1 and t.operator = ?2 and t.pay_shortCode = ?3 and t.pay_keyword = ?4 and t.mail_cap_status = 1", nativeQuery = true)
    void updateOfferStatus(String payChannel,String operator,String shortCode,String keyword);

    /**
     * 修改状态
     * @param status0
     * @param identification
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("update OfferModel t set t.status=?1 where t.identification=?2")
    void updateStatusById(int status0, String identification);

    /**
     * 查找 受限制 offer id
     * @return
     */
    @Query("select t.identification from OfferModel t where t.maxPull>0 or t.maxPull is not null")
    List<String> queryLimitOfferIds();

    /**
     * 获取 offer
     * @param offIds
     * @return
     */
    List<OfferModel> findByIdentificationIn(List offIds);

    /**
     * findByOperator
     * @param opertaor
     * @return
     */
    List<OfferModel> findByOperator(String opertaor);

    /**
     * 批量修改状态
     * @param status
     * @param ids
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update t_offer t set t.is_log=:status where t.identification in(:ids)", nativeQuery = true)
    void updateLogStatus(@Param(value = "status") int status, @Param(value = "ids") List<String> ids);

    /**
     * 查找名称
     * @param query
     * @return
     */
    @Query("select distinct t.offerName from OfferModel t where t.offerName like ?1")
    List<String> queryNames(String query);

    /**
     * resultOfferModel
     * @return
     */
    @Query(value = "select new com.starp.zoo.entity.zoo.ResultOfferModel(t.identification,t.offerName) from OfferModel t")
    List<ResultOfferModel> resultOfferModel();

    /**
     * findOfferIdByIdentification
     * @param offerId
     * @return
     */
    @Query(value = "select t from  OfferModel t where t.identification = ?1")
    OfferModel findOfferByIdentification(String offerId);

    /**
     * findByNameList
     * @param offerNames
     * @return
     */
    @Query(value = "select  t.identification from OfferModel t where t.offerName in(:offerNames)")
    List<String> findByNameList(JSONArray offerNames);


    /**
     * update close time
     * @param createTime
     * @param identification
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("update OfferModel t set t.closeTime=?1 where t.identification=?2")
    void updateCloseTime(Date createTime, String identification);


    /**
     * 批量修改closeTime
     * @param closeTime
     * @param ids
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query(value = "update t_offer t set t.closeTime = :closeTime where t.identification in(:ids)", nativeQuery = true)
    void updateOffersCloseTime(@Param(value = "closeTime") Date closeTime, @Param(value = "ids") List<String> ids);


    /**
     * 根据offer状态查找
     * @param status
     * @return
     */
    List<OfferModel> findByStatus(int status);

    /**
     * 查找offer model
     * @param operator
     * @param shortCode
     * @param keyword
     * @return
     */
    OfferModel findFirstByOperatorAndPayShortCodeAndPayKeyword(String operator,String shortCode,String keyword);


    /**
     * 根据短码跟关键字查询
     * @param shortCode
     * @param keyword
     * @param operator
     * @param partner
     * @return
     */
    OfferModel findFirstByPayShortCodeAndPayKeywordAndOperatorAndPartner(String shortCode,String keyword,String operator,String partner);

    /**
     * 根据短码和关键字查询 offerModel 集合
     * @param shortCode
     * @param keyword
     * @return
     */
    @Query("select p.identification from OfferModel p where p.payShortCode =?1 and p.payKeyword =?2")
    List<String> findAllByPayShortCodeAndPayKeyword(String shortCode,String keyword);

    /**
     * 批量更新 offer 的 init_epm 值
     * @param initEpm
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update OfferModel p set p.initEpm = :initEpm where p.identification = :id")
    void updateInitEpm(Double initEpm, String id);


    /**
     * 查找offermodel
     * @return
     */
    @Query("select t from OfferModel t where t.payShortCode is not null  and t.payKeyword is not null")
    List<OfferModel> findShortCodeOffer();

    /**
     * 更新epm
     * @param moNum
     * @param transNum
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update AffEpmInfoModel p set p.moNum = ?1, p.transNum = ?2 where p.identification = ?3")
    void updatePayMoTrans(Long moNum,Long transNum,String id);

    /**
     * 根据告警条件查找offer
     * @param capStatus
     * @param moCrStatus
     * @return
     */
    List<OfferModel> findByMailCapStatusAndMailMoCrStatus(int capStatus,int moCrStatus);
}


