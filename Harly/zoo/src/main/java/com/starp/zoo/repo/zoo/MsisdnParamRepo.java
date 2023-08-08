package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.MsisdnParamModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author Charles
 * @date 2019/5/6
 * @description :
 */
public interface MsisdnParamRepo extends JpaRepository<MsisdnParamModel, String>, JpaSpecificationExecutor<MsisdnParamModel> {

    /**
     * findByCountryAndAndOperator
     * @param country
     * @param operator
     * @return
     */
    MsisdnParamModel findByCountryAndAndOperator(String country,String operator);
}
