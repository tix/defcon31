package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.OfferTagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;


/**
 * @author Charles
 * @date 2019/3/5
 * @description :
 */

@Repository
public interface OfferTagRepo extends JpaRepository<OfferTagModel,String> {


    /***
     * find offers
     * @Author David
     * @Date 11:16 2019/3/6
     * @param id
     * @return java.util.List<com.starp.zoo.entity.zoo.OfferTagModel>
     **/
    List<OfferTagModel> findAllByTagId(String id);

    /**
     * 根据offerId 删除
     * @param offerId
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByOfferId(String offerId);

    /**
     * find model
     * @param id
     * @return
     */
    List<OfferTagModel> findByTagId(String id);

    /**
     * delete model
     * @param offerId
     * @param id
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByOfferIdAndTagId(String offerId, String id);

    /**
     * 查找关联app offer 对应的 stack
     * SELECT t1.* FROM t_offer_tag t1 LEFT JOIN t_tag t2 ON t1.tag_id = t2.identification AND t2.tag_type=1 WHERE t2.identification IS NOT NULL
     * @return
     */
    @Query(value = "SELECT t1.* FROM t_offer_tag t1 LEFT JOIN t_tag t2 ON t1.tag_id = t2.identification AND t2.tag_type=1 WHERE t2.identification IS NOT NULL AND t1.offer_id IS NOT NULL", nativeQuery = true)
    List<OfferTagModel> findQuery();


    /**
     * delet by tagId
     * @param identification
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByTagId(String identification);

    /**
     * 获取其他标签的 offerTagModel
     * @return
     */
    @Query(value = "SELECT t1.* FROM t_offer_tag t1 LEFT JOIN t_tag t2 ON t1.tag_id = t2.identification AND t2.tag_type=3 WHERE t2.identification IS NOT NULL AND t1.offer_id IS NOT NULL", nativeQuery = true)
    List<OfferTagModel> findOtherTagQuery();

    /**
     * 获取其他标签的 offerTagModel
     * @param offerId
     * @return
     */
    @Query(value = "SELECT t2.category_id FROM t_offer_tag t1 LEFT JOIN t_category_tag t2 ON t1.tag_id = t2.tag_id AND t1.offer_id=?1 WHERE t2.identification IS NOT NULL AND t1.offer_id IS NOT NULL", nativeQuery = true)
    List<String> findAppIdsByOfferId(String offerId);

    /**
     * 查找 相关的 offer stack id
     * @param identification
     * @return
     */
    @Query(value = "SELECT t1.tag_id from t_offer_tag t1 left join t_tag t2 on t2.identification = t1.tag_id where t1.offer_id=?1 and t2.tag_type=1 limit 1", nativeQuery = true)
    String findOfferStackId(String identification);


    /**
     * 查找 相关的 offer group id
     * @param identification
     * @return
     */
    @Query(value = "SELECT t1.tag_id from t_offer_tag t1 left join t_tag t2 on t2.identification = t1.tag_id where t1.offer_id=?1 and t2.tag_type=2 ", nativeQuery = true)
    List<String> findOfferGroupId(String identification);

    /**
     * 根据 offerId 查找 OtherId
     * @param identification
     * @return
     */
    @Query(value = "SELECT t1.tag_id from t_offer_tag t1 left join t_tag t2 on t2.identification = t1.tag_id where t1.offer_id=?1 and t2.tag_type=3 ", nativeQuery = true)
    List<String> findOfferOtherId(String identification);

    /**
     * 根据tagId 查找开启的offer model
     * @param tagId
     * @return
     */
    @Query(value = "SELECT t2.offer_name FROM t_offer_tag t1 left join t_offer t2 on t1.offer_id = t2.identification where t1.tag_id =?1 and t2.status = 1",nativeQuery = true)
    List<String> findOfferIdsByStart(String tagId);

    /**
     * 根据tagId 查找关闭的offer model
     * @param tagId
     * @return
     */
    @Query(value = "SELECT t2.offer_name FROM t_offer_tag t1 left join t_offer t2 on t1.offer_id = t2.identification where t1.tag_id =?1 and t2.status = 2 order by t2.closeTime desc limit 10",nativeQuery = true)
    List<String> findOfferIdsByClose(String tagId);
    /**
     * 根据tagIds 查找
     * @param tagIds
     * @return
     */
    List<OfferTagModel> findAllByTagIdIn(List<String> tagIds);

    /**
     * 根据任务组Id 查询 offerIds
     * @param groupId
     * @return java.util.List<java.lang.String>
     * @author Curry
     * @date 2022/12/12
     */
    @Query(value = "SELECT offer_id FROM t_offer_tag WHERE tag_id = :groupId", nativeQuery = true)
    List<String> findOfferIdsByGroupId(String groupId);

    /**
     * 根据运营商找栈
     * @param operator
     * @return
     */
    @Query(value = " select tag_name from t_tag where tag_type = 1 and identification in (SELECT t1.tag_id FROM t_offer_tag t1 where t1.offer_id in (select identification from t_offer where operator = ?1 and status = 1) GROUP BY t1.tag_id)",nativeQuery = true)
    List<String> findTagIdByOfferOperator(String operator);



    /**
     * 根据运营商查询改运营商下开启offer 关联栈的cap 并且按照cap 降序排列
      * @param operator
     * @return
     */
   @Query(value = "select t5.tag_name,sum(t6.cap)as cap from t_offer t6 left join (select t4.tag_name,offer_id from t_offer_tag t3  LEFT JOIN (select identification,tag_name from t_tag where tag_type = 1 and identification in (SELECT t1.tag_id FROM t_offer_tag t1 where t1.offer_id in (select identification from t_offer where operator = ?1 and status = 1 ) GROUP BY t1.tag_id)) t4 on t3.tag_id = t4.identification where t4.identification is not null) t5 on t6.identification = t5.offer_id where t5.offer_id is not null and t6.status = 1 GROUP BY t5.tag_name ORDER BY sum(t6.cap) desc",nativeQuery = true)
   List<Object[]> findTagNameCapOrderByCap(String operator);


    /**
     * 根据tagName查找对应的offer
     * @param tagName
     * @return
     */
   @Query(value = "select identification from t_offer where identification in(select offer_id from t_offer_tag where tag_id in (select identification from t_tag where tag_name = ?1)) and status = 1 order by cap desc",nativeQuery = true)
   List<String> fetchOfferIdsByTagName(String tagName);


    /**
     * 根据tagName查找对应的关闭的offer
     * @param tagName
     * @param count
     * @return
     */
    @Query(value = "select identification from t_offer where identification in(select offer_id from t_offer_tag where tag_id in (select identification from t_tag where tag_name = ?1)) and status = 0 order by closeTime desc limit ?2",nativeQuery = true)
    List<String> fetchCloseOfferIdsByTagName(String tagName,int count);

}
