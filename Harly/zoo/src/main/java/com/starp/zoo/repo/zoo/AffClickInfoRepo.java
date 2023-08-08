package com.starp.zoo.repo.zoo;


import com.starp.zoo.entity.zoo.AffClickInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

/**
 * @author Charles,
 * @date: 2018/9/29.
 */
public interface AffClickInfoRepo extends JpaRepository<AffClickInfoModel, String> {

    /**
     * 根据 affName, offerId, createTime 统计满足条件的数量
     * @param affName
     * @param offerId
     * @param startDate
     * @return
     */
    long countByAffNameAndOfferIdAndCreateTimeAfter(String affName, String offerId, Date startDate);

    /**
     * 根据 affName, offerId 统计时间段内满足条件的数量
     * @param affName
     * @param offerId
     * @param startTime
     * @param endTime
     * @return
     */
    long countByAffNameAndOfferIdAndCreateTimeBetween(String affName, String offerId, Date startTime, Date endTime);

    /**
     * findClickId
     * @param ip
     * @param offerId
     * @param date
     * @return
     */
    @Query(value = "select t.clickId from AffClickInfoModel t where t.ip=?1 and t.offerId = ?2 and t.createTime >=?3")
    String findClickId(String ip, String offerId, Date date);
}
