package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.SubCountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/10.
 */
@Repository
public interface SubCountRepo extends JpaRepository<SubCountModel, String> {
    
    /**
      * 查找符合条件的SubCountModel
      * @param identification
      * @return SubCountModel
      */
    SubCountModel findFirstByOfferIdOrderByCreateTime(String identification);
    
    /**
      * 是否存在符合条件的SubCountModel
      * @param offerId
      * @return boolean
      */
    boolean existsByOfferId(String offerId);
    
    /**
      * 更新SubCountModel count
      * @param offerId
      * @return
      */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update t_sub_count set count = count+1 WHERE offer_id=?1", nativeQuery = true)
    int updateCount(String offerId);
}
