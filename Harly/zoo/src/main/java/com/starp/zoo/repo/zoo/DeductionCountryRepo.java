package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.DeductionCountryModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author david
 */
public interface DeductionCountryRepo extends JpaRepository<DeductionCountryModel,String> {


    /**
     * 根据国家查找
     * @param country
     * @param time
     * @return
     */
    DeductionCountryModel findByCountryAndTime(String country,String time);


    /**
     * 根据最近时间查找
     * @param country
     * @param time
     * @return
     */
    DeductionCountryModel findFirstByCountryAndTimeBeforeOrderByTimeDesc(String country,String time);
}
