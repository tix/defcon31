package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AddressMsisdnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * AddressMsisdnRepo.
 *
 * @author magic
 * @data 2022/4/21
 */
public interface AddressMsisdnRepo extends JpaRepository<AddressMsisdnModel, String>, JpaSpecificationExecutor<AddressMsisdnModel> {
    /**
     * 通过电话号码查询.
     *
     * @param msisdn
     * @return
     */
    AddressMsisdnModel findByMsisdn(String msisdn);
}
