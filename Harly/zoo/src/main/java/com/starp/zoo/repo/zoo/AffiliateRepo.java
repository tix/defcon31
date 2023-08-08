package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AffiliateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@Repository
public interface AffiliateRepo extends JpaRepository<AffiliateModel, String>, JpaSpecificationExecutor<AffiliateModel> {

    /**
     * 根据主键查找
     * @param id
     * @return
     */
    AffiliateModel findFirstByIdentification(String id);
}
