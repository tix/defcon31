package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.SubscribeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/10.
 */
@Repository
public interface SubscribeRepo extends JpaRepository<SubscribeModel, String> {

    /**
      * 统计符合条件的SubscibeModel数量
      * @param offerId
      * @param status
      * @param startDate
      * @param endDate
      * @return count
     */
    @Query("select count(t.identification) from SubscribeModel t where t.offerId=?1 and  t.status=?2 and t.createTime>=?3 and t.createTime<=?4")
    int countQuery(String offerId, String status, Date startDate, Date endDate);


    /**
      * 是否包含符合条件的SubscribeModel
      * @param offerId
      * @param status
      * @param ip
      * @param yesterday
      * @return boolean
      */
    boolean existsByOfferIdAndStatusAndIpAndCreateTimeAfter(String offerId, String status, String ip, Date yesterday);

    /**
      * 是否包含符合条件的SubscribeModel
      * @param s
      * @param s1
      * @param s2
      * @return boolean
      */
    boolean existsByOfferIdAndStatusAndIp(String s, String s1, String s2);

    /**
     * 根据 offerid ip 删除测试数据
     * @param offerId
     * @param ip
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByOfferIdAndIp(String offerId, String ip);

    /**
     * 根据 ip 删除测试数据
     * @param ip
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    void deleteByIp(String ip);

    /**
     * 根据offer的identification和时间查找
     * @param ids
     * @param begin
     * @param end
     * @return
     */
    @Query(value = "SELECT  count(identification) from t_subscribe where offer_id  in  ?1 and createtime >= ?2 and createtime< ?3 ",nativeQuery = true)
    Long findByIdsAndTime(List<String> ids, Date begin, Date end);

    /**
     * findByOfferId
     * @param offerId
     * @param num
     * @return
     */
    @Query(value = "select * from t_subscribe where offer_id=?1 order by createtime desc limit 0 ,?2",nativeQuery = true)
    List<SubscribeModel> findByOfferId(String offerId,int num);
}
