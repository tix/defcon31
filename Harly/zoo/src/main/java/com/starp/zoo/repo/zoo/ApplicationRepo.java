package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.ApplicationModel;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/7.
 */
@Repository
public interface ApplicationRepo extends JpaRepository<ApplicationModel, String>, JpaSpecificationExecutor<ApplicationModel> {
    /**
     * 查询所有appName
     * @return
     */
    @Query(value = "select distinct t.appName  from ApplicationModel t")
    List<String> findAppNames();

    /**
     * 根据 appName获取主键
     * @param appName
     * @return
     */
    @Query(value = "select t.identification from ApplicationModel t where t.appName = :appName")
    String findByAppName(String appName);

    /**
      * 查找符合条件的ApplicationModel
      * @param appId
      * @return ApplicationModel
      */
    ApplicationModel findFirstByIdentification(String appId);


    /**
      * 更新ApplicationModel 的状态
      * @param status
      * @param appId
      * @return
      */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update ApplicationModel t set t.status=?1 where t.identification=?2")
    void updateStatus(Integer status, String appId);

    /**
      * 统计符合条件ApplicationModel 的数量
      * @param name
      * @return count
      */
    int countByAppName(String name);


    /**
      * 查找符合条件的ApplicationModel
      * @param name
      * @return ApplicationModel
      */
    ApplicationModel findFirstByAppName(String name);


    /**
     * 判断启用的该 app 是否存在
     * @param appId
     * @param status1
     * @return
     */
    boolean existsByIdentificationAndStatus(String appId, int status1);

    /**
     * 查找 app 关联 offer 数量
     * @param appId
     * @return
     */
    @Query(value = "select count(t1.identification) from t_offer t1 where t1.identification in (select offer_id from t_offer_tag where tag_id in (select tag_id from t_category_tag where category_id=?1))", nativeQuery = true)
    Long findOfferNum(String appId);

    /**
     * 更新ApplicationModel 日志的状态
     * @param status
     * @param appId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update ApplicationModel t set t.logStatus=?1 where t.identification=?2")
    void updateLogStatus(Integer status, String appId);

    /**
     * 批量删除
     * @param ids
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteByIdentificationIn(List<String> ids);

    /**
     * 找到在线的app
     * @param status
     * @return
     */
    List<ApplicationModel> findByStatus(Integer status);

    /**
     * 是否存在appName
     * @param appName
     * @return
     */
    boolean existsByAppName(String appName);

    /**
     * 查询开启的appNames
     * @return
     */
    @Query("select t.appName from ApplicationModel t where t.status = 1")
    List<String> findStartAppNames();
}
