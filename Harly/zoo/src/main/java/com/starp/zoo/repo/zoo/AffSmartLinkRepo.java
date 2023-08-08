package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AffSmartLinkModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@Repository
public interface AffSmartLinkRepo extends JpaRepository<AffSmartLinkModel, String>, JpaSpecificationExecutor<AffSmartLinkModel> {

    /**
     * 根据主键查找
     * @param categoryId
     * @return
     */
    AffSmartLinkModel findFirstByIdentification(String categoryId);

    /**
     * 根据 id 查找
     * @param smlId
     * @return
     */
    AffSmartLinkModel findFirstById(String smlId);

    /**
     * 统计
     * @param id
     * @return
     */
    @Query("select count(t.id) from AffSmartLinkModel t where t.id=?1")
    int countQuery(String id);

    /**
     * 根据名称查找
     * @param resourceName
     * @return
     */
    AffSmartLinkModel findFirstByName(String resourceName);

    /**
     * 查询所有name
     * @return
     */
    @Query(value = "select distinct t.name from AffSmartLinkModel t")
    List<String> findNames();
}
