package com.starp.zoo.repo.payment;

import com.starp.zoo.entity.payment.ShortCodeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author david
 */
public interface ShortCodeRepo extends JpaRepository<ShortCodeModel,String> {

    /**
     * 查找shortCode model
     * @param partner
     * @param operator
     * @param shortCode
     * @param keyword
     * @return
     */
    ShortCodeModel findFirstByPartnerAndOperatorAndShortCodeAndCommand(String partner,String operator,String shortCode,String keyword);

    /**
     * 更新shortcode model
     * @param identification
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update ShortCodeModel t set t.associateZoo = 1 where t.identification = ?1")
    void updateShortCodeByOffer(String identification);

}
