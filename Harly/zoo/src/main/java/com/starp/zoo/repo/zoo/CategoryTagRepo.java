package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.CategoryTagModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Charles
 * @date 2019/3/6
 * @description :
 */
@Repository
public interface CategoryTagRepo extends JpaRepository<CategoryTagModel, String> {

    /**
     * 根据 categoryId 找到关联
     * @param type
     * @param categoryId
     * @return
     */
    List<CategoryTagModel> findAllByTypeAndCategoryId(int type, String categoryId);

    /**
     * 删除关联
     * @param type
     * @param categoryId
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByTypeAndCategoryId(int type, String categoryId);

    /**
     * 删除关联
     * @param type
     * @param ids
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByTypeAndCategoryIdIn(int type, List<String> ids);

    /**
     * 删除 tag 关联
     * @param id
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByTagId(String id);

    /**
     * 按照 tagId 统计
     * @param tagId
     * @return
     */
    List<CategoryTagModel> findByTagId(String tagId);

    /**
     * 查找所有 该offer分配信息
     * @param offerid
     * @return
     */
    @Query(value = "select * from t_category_tag where tag_id in (select t1.identification from t_tag t1 left join t_offer_tag t2 on t1.identification=t2.tag_id where t1.tag_type=2 and t2.offer_id=?1)", nativeQuery = true)
    List<CategoryTagModel> queryOfferAssign(String offerid);


    /**
     * 查找所有 该offer分配信息
     * @param offerid
     * @return
     */
    @Query(value = "select category_id from t_category_tag where tag_id in (select t1.identification from t_tag t1 left join t_offer_tag t2 on t1.identification=t2.tag_id where t1.tag_type=2 and t2.offer_id=?1)", nativeQuery = true)
    List<String> queryAppIds(String offerid);



    /**
     * 根据appId查找offerId
     * @param appId
     * @return
     */
    @Query(value = "select offer_id  from t_offer_tag where tag_id in (select t1.identification from t_tag t1 left join t_category_tag t2 on t1.identification=t2.tag_id where t1.tag_type=2 and t2.category_id=?1 ) ", nativeQuery = true)
    List<String> queryOfferIds(String appId);


    /**
     * 根据appId查找operators
     * @param appId
     * @return
     */
    @Query(value = "SELECT distinct operator FROM t_offer t WHERE t.identification in(SELECT offer_id FROM t_offer_tag WHERE tag_id IN(SELECT t1.identification FROM t_tag t1 LEFT JOIN t_category_tag t2 ON t1.identification=t2.tag_id WHERE t1.tag_type=2 AND t2.category_id=?1))", nativeQuery = true)
    List<String> queryOperators(String appId);

    /**
     * 根据appId查找tagId
     * @param appId
     * @return
     */
    @Query(value = "select tag_id from t_category_tag where category_id = ?1 ",nativeQuery = true)
    List<String> querTagIds(String appId);

    /**
     * 根据 appId 和 groupId 删除
     * @param appId
     * @param groupId
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByCategoryIdAndTagId(String appId, String groupId);
}
