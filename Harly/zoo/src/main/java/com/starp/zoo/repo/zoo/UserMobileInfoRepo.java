package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.UserMobileInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Charles
 * @date 2019/4/17
 * @description :
 */
public interface UserMobileInfoRepo extends JpaRepository<UserMobileInfoModel, String> {

    /**
     * 查询 用户号码
     * @param userId
     * @param mnc
     * @return
     */
    UserMobileInfoModel findFirstByUserIdAndMncOrderByCreateTimeDesc(String userId, String mnc);

    /**
     * 根据设备Id 查询
     * @param deviceid
     * @param mnc
     * @return
     */
    UserMobileInfoModel findFirstByDeviceIdAndMncOrderByCreateTimeDesc(String deviceid, String mnc);

    /**
     * 根据deviceid查询
     * @param deviceid
     * @return
     */
    UserMobileInfoModel findFirstByDeviceIdOrderByCreateTimeDesc(String deviceid);

    /**
     * update deviceId
     * @param id
     * @param deviceId
     * @param msisdn
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update UserMobileInfoModel  t set t.deviceId = ?2, t.mobile = ?3  where t.identification = ?1")
    void updateDeviceId(String id,String deviceId,String msisdn);

}
