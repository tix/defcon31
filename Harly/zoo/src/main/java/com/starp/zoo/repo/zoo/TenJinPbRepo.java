package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.TenJinPbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * TenJinPBRepo.
 *
 * @author magic
 * @data 2022/4/14
 */
public interface TenJinPbRepo extends JpaRepository<TenJinPbModel, String>, JpaSpecificationExecutor<TenJinPbModel> {

    /**
     * 根据advertisingId查找.
     *
     * @param advertisingId
     * @return
     */
    TenJinPbModel findByAdvertisingId(String advertisingId);
}
