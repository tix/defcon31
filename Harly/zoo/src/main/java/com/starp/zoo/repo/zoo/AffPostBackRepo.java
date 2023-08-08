package com.starp.zoo.repo.zoo;


import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.AffPostBackModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

/**
 * @DATE: 2018/9/19.
 * @author Charles
 */
public interface AffPostBackRepo extends JpaRepository<AffPostBackModel, String> {

    /**根据 affSub 、 offerId 、 createTime 统计满足条件的转化
     * @param partner
     * @param systemOfferId
     * @param time
     * @return
     */
    Long countByPartnerAndSystemOfferIdAndCreateTimeAfter(String partner, String systemOfferId, Date time);

    /**
     * 获取时间段内所有的点击信息
     * @param partner
     * @param systemOfferId
     * @param startTime
     * @param endTime
     * @return
     */
    List<AffPostBackModel> findAllByPartnerAndSystemOfferIdAndCreateTimeBetween(String partner, String systemOfferId, Date startTime, Date endTime);

    /**
     * 根据offerIds和时间查找
     * @param offerIds
     * @param begin
     * @param end
     * @return
     */
    @Query(value = "SELECT  count(identification) from t_aff_postback where offer_id  in  ?1 and createtime >= ?2 and createtime< ?3 ",nativeQuery = true)
    Long findByIdsAndTime(List<String> offerIds, Date begin, Date end);
}
