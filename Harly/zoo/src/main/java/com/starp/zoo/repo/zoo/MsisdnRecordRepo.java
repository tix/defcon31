package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.MsisdnRecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * MsisdnRecordRepo.
 *
 * @author magic
 * @date 2021/10/29
 */
public interface MsisdnRecordRepo extends JpaRepository<MsisdnRecordModel, String>, JpaSpecificationExecutor<MsisdnRecordModel> {

    /**
     * 通过电话查找.
     *
     * @param msisdn
     * @return
     */
    MsisdnRecordModel findByMsisdn(String msisdn);

    /**
     * 拉取電話(通过result).
     *
     * @return
     */
    @Query(value = " select * from t_msisdn_record t where t.result is null and t.currrent_day_is_pull is null LIMIT 1", nativeQuery = true)
    MsisdnRecordModel findOneByResult();

    /**
     * 拉取電話(通过r1).
     *
     * @return
     */
    @Query(value = " select * from t_msisdn_record t where t.r1 is null and t.currrent_day_is_pull is null LIMIT 1", nativeQuery = true)
    MsisdnRecordModel findOneByR1();


    /**
     * 拉取電話(通过r2).
     *
     * @return
     */
    @Query(value = " select * from t_msisdn_record t where t.r2 is null and t.currrent_day_is_pull is null LIMIT 1", nativeQuery = true)
    MsisdnRecordModel findOneByR2();

    /**
     * 拉取電話(通过r3).
     *
     * @return
     */
    @Query(value = " select * from t_msisdn_record t where t.r3 is null and t.currrent_day_is_pull is null LIMIT 1", nativeQuery = true)
    MsisdnRecordModel findOneByR3();

    /**
     * 拉取電話(通过r4).
     *
     * @return
     */
    @Query(value = " select * from t_msisdn_record t where t.r4 is null and t.currrent_day_is_pull is null LIMIT 1", nativeQuery = true)
    MsisdnRecordModel findOneByR4();

    /**
     * 拉取電話(通过r5).
     *
     * @return
     */
    @Query(value = " select * from t_msisdn_record t where t.r5 is null and t.currrent_day_is_pull is null LIMIT 1", nativeQuery = true)
    MsisdnRecordModel findOneByR5();

    /**
     * 拉取電話(通过r6).
     *
     * @return
     */
    @Query(value = " select * from t_msisdn_record t where t.r6 is null and t.currrent_day_is_pull is null LIMIT 1", nativeQuery = true)
    MsisdnRecordModel findOneByR6();

    /**
     * 拉取電話(通过r7).
     *
     * @return
     */
    @Query(value = " select * from t_msisdn_record t where t.r7 is null and t.currrent_day_is_pull is null LIMIT 1", nativeQuery = true)
    MsisdnRecordModel findOneByR7();

    /**
     * 每天倒序取8000个 msisdn
     * @return
     */
    @Query(value = "select * from t_msisdn_record t order by t.identification desc limit 8000",nativeQuery = true)
    List<MsisdnRecordModel> findMsisdnDay();


    /**
     * 根据id下标查找
     * @param beginId
     * @param endId
     * @return
     */
    @Query(value = "select * from t_msisdn_record t where t.identification >= ?1 and t.identification <?2 ",nativeQuery = true)
    List<MsisdnRecordModel> findResetList(int beginId,int endId );

    /**
     * 倒序取昨天最后一个对象的id
     * @param time
     * @return
     */
    @Query(value = "select t.identification from t_msisdn_record t where t.createTime <= ?1 order by t.identification desc limit 1",nativeQuery = true)
    int findTodayId(String time);

    /**
     * 是否存在msisdn
     * @param msisdn
     * @return
     */
    boolean existsByMsisdn(String msisdn);
}
