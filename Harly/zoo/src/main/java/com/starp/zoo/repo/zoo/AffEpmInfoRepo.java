package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AffEpmInfoModel;
import com.starp.zoo.vo.EpmClickVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Charles
 * @date 2019/1/22
 * @description :
 */
@Repository
public interface AffEpmInfoRepo extends JpaRepository<AffEpmInfoModel, String>, JpaSpecificationExecutor<AffEpmInfoModel> {

    /**
     * 查看是否存在该条记录
     * @param resourceId
     * @param offerId
     * @param calculateHour
     * @return
     */
    boolean existsByResourceIdAndOfferIdAndCalculateHour(String resourceId, String offerId, String calculateHour);

    /**
     * 查询一月收入
     * @param day
     * @return
     */
    @Query(value = "select sum(revenue) from t_aff_epm where calculate_hour like ?1", nativeQuery = true)
    Double queryOneDayRevenue(String day);


    /**
     * 统计点击数
     * @param appNames
     * @param day
     * @param country
     * @return
     */
    @Query(value="select sum(click_num) from t_aff_epm where resource_name in ?1 and calculate_hour LIKE CONCAT('%',?2,'%') and country = ?3 ",nativeQuery = true)
    Long queryClickNum(List<String> appNames,String day,String country);

    /**
     * 根据国家,offer 名称查找
     * @param appNames
     * @param day
     * @param country
     * @param offerId
     * @return
     */
    @Query(value = "select sum(trans_num) from t_aff_epm where resource_name in ?1 and calculate_hour LIKE CONCAT('%',?2,'%') and country = ?3 and offer_id = ?4",nativeQuery = true)
    Long queryTransNum(List<String> appNames,String day,String country,String offerId);


    /**
     * 根据事件查找model
     * @param appNames
     * @param day
     * @return
     */
    //@Query(value = "select * from t_aff_epm   where  calculate_hour like ?1 group by country",nativeQuery = true)

    @Query("SELECT p FROM AffEpmInfoModel p WHERE p.resourceName in ?1 and p.calculateHour LIKE CONCAT('%',?2,'%') group by p.country")
    List<AffEpmInfoModel> findEpmModels(List<String> appNames,String day);

    /**
     * 根据时间国家查找
     * @param appNames
     * @param day
     * @param country
     * @return
     */
    @Query("select p from AffEpmInfoModel p where p.resourceName in ?1 and p.calculateHour LIKE CONCAT('%',?2,'%') and p.country = ?3 group by p.offerId")
    List<AffEpmInfoModel> findEpmByCountry(List<String> appNames, String day,String country);

    /**
     * 查找一月收入
     * @param day
     * @param month
     * @return
     */
    @Query(value = "select t.revenue from (select sum(m.revenue) revenue,substring(m.calculate_hour, 1, 10) as date from t_aff_epm m where substring(m.calculate_hour, 1, 10)>=?1 and m.calculate_hour LIKE CONCAT('%',?2,'%') group by substring(m.calculate_hour, 1, 10)) as t order by t.date", nativeQuery = true)
    List<Double> queryOneMonthRevenue(String day,String month);

    /**
     * epm计算retry时删除历史epm计算结果
     * @param appId
     * @param offerId
     * @param hour
     * @return
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    @Query("delete from AffEpmInfoModel p where p.resourceId = ?1 and p.offerId = ?2 and p.calculateHour = ?3")
    void deleteEpm(String appId, String offerId, String hour);

    /**
     * 查找每小时epm的数据
     * @param calculateHour
     * @return
     */
    @Query(value = "select sum(p.click_num)as clickNum,sum(p.trans_num)as tranNum,sum(p.app_trans_num) as appTranNum,sum(p.revenue) from t_aff_epm p where  p.calculate_hour =?1 ",nativeQuery = true)
    List<Object> findEpmValue(String calculateHour);


    /**
     * 查找每小时epm的数据
     * @param calculateHour
     * @param offerId
     * @return
     */
    @Query(value = "select sum(p.click_num)as clickNum,sum(p.mo_num)as moNum from t_aff_epm p where  p.calculate_hour =?1 and p.offer_id = ?2 ",nativeQuery = true)
    List<Object> findOfferEpmHour(String calculateHour,String offerId);


    /**
     * 根据条件查找epm hour
     * @param appId
     * @param offerId
     * @param calculatehour
     * @return
     */
    AffEpmInfoModel findFirstByResourceIdAndOfferIdAndCalculateHour(String appId,String offerId,String calculatehour);


    /**
     * 查找当天的EPM info
     * @param day
     * @return
     */
    @Query("SELECT p FROM AffEpmInfoModel p WHERE  p.calculateHour LIKE CONCAT('%',?1,'%') ")
    List<AffEpmInfoModel> findTodayEpmModels(String day);

    /**
     * 查询EPMlist
     * @param appId
     * @param time
     * @param operator
     * @return
     */
    @Query("select p from AffEpmInfoModel p where p.resourceId = ?1 and p.calculateHour = ?2 and p.operator = ?3")
    List<AffEpmInfoModel> findEpmModelList(String appId, String time, String operator);

    /**
     * 查询offer某天的 mo_num
     * @param offerId
     * @param begin
     * @param end
     * @return
     */
    @Query(value = "SELECT sum(e.mo_num) FROM t_aff_epm e WHERE e.offer_id = :offerId and e.createtime BETWEEN :begin AND :end", nativeQuery = true)
    Integer selectMoNumByDay(String offerId, Date begin, Date end);


    /**
     * 查询点击
     * @param partner
     * @param offerId
     * @param calculateHour
     * @return
     */
    @Query(value="select sum(click_num) from t_aff_epm where partner = ?1 and  offer_id= ?2 and calculate_hour = ?3 ",nativeQuery = true)
    Long queryClickNumByPartnerAndOfferId(String partner,String offerId,String calculateHour);

    /**
     * 查询当天的部分offer点击、包内、转化
     * @param offerIds
     * @param begin
     * @param end
     * @return java.util.List<java.lang.Object[]>
     * @author Curry
     * @date 2023/5/23
     */
    @Query(value = "SELECT new com.starp.zoo.vo.EpmClickVO (t.offerId, SUM(t.clickNum), SUM(t.appTransNum), sum(t.transNum)) FROM AffEpmInfoModel  t WHERE t.offerId in (:offerIds) AND t.calculateHour BETWEEN :begin AND :end GROUP BY t.offerId")
	List<EpmClickVO> queryClickAndAppTransAndRevenueInOfferId(List<String> offerIds, String begin, String end);
}
