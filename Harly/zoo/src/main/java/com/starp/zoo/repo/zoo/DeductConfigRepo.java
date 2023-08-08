package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.DeductConfigModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Charles
 * @date 2019/3/14
 * @description :
 */
@Repository
public interface DeductConfigRepo extends JpaRepository<DeductConfigModel, String>, JpaSpecificationExecutor<DeductConfigModel> {

    /**
     * 根据smartlink 配置查找
     * @param smlId
     * @return
     */
    List<DeductConfigModel> findAllBySmartLinkId(String smlId);

    /**
     * 删除 扣量
     * @param identification
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteBySmartLinkId(String identification);

    /**
     * 根据offer 与 smartlink id 查找
     * @param identification
     * @param identification1
     * @return
     */
    DeductConfigModel findBySmartLinkIdAndOfferId(String identification, String identification1);
}
