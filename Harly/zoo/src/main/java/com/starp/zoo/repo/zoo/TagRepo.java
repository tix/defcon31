package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.ResultTagModel;
import com.starp.zoo.entity.zoo.TagModel;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @Author David
 * @Date 11:59 2019/3/4
 * @param
 * @return
 **/
@Repository
public interface TagRepo extends JpaRepository<TagModel,String>,JpaSpecificationExecutor<TagModel> {


    /**
     * 批量删除
     * @param ids
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteAllByIdentificationIn(List<String> ids);


    /**
     * 查找offer 关联的 tags
     * @param offerId
     * @return
     */
    @Query(value = "SELECT * FROM t_tag WHERE identification IN (select tag_id from t_offer_tag where offer_id=?1) order by createtime DESC", nativeQuery = true)
    List<TagModel> findQuery(String offerId);

    /**
     * find tagmodel
     * @param id
     * @return
     */
    TagModel findByIdentification(String id);

    /**
     * tag 存在
     * @param tagName
     * @return
     */
    boolean existsByTagName(String tagName);

    /**
     * 根据名称查找
     * @param tagName
     * @return
     */
    TagModel findFirstByTagName(String tagName);

    /**
     * findAllTagModel
     * @return
     */
    @Query(value = "select new com.starp.zoo.entity.zoo.ResultTagModel(t.identification,t.tagName,t.tagType) from TagModel t")
    List<ResultTagModel> findAllTagModel();

    /**
     * findTagId
     * @param tagName
     * @return
     */
    @Query(value = "SELECT identification FROM t_tag where tag_name = ?1",nativeQuery = true)
    String findTagId(String tagName);
}
