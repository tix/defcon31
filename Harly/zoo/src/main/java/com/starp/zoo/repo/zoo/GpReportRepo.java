package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.GpReportModel;
import com.starp.zoo.entity.zoo.ResultGpReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;

/***
 * 
 * @Author David
 * @Date 14:51 2019/2/25
 * @param  
 * @return 
 **/

public interface GpReportRepo extends JpaRepository<GpReportModel,String>,JpaSpecificationExecutor<GpReportModel> {


    /***
     * find config
     * @Author David
     * @Date 17:05 2019/2/25
     * @param  id
     * @return com.starp.zoo.entity.zoo.GpReportModel
     **/
    @Query("select p from GpReportModel p where p.identification = ?1")
    GpReportModel findConfig(String id);


    /***
     * update status
     * @Author David
     * @Date 17:31 2019/2/25
     * @param  id
     * @param type
     * @return void
     **/
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update GpReportModel t set t.status=?2 where t.identification=?1")
    void updateStatus(String id, Integer type);


    /***
     * find start config
     * @Author David
     * @Date 19:41 2019/2/25
     * @param  status
     * @return java.util.List<com.starp.zoo.entity.zoo.GpReportModel>
     **/
    @Query("select p from GpReportModel p where p.status = ?1 order by p.createTime desc ")
    List<GpReportModel> findAllByStatus(int status);


    /***
     * update model is online
     * @Author David
     * @Date 10:44 2019/2/26
     * @param  id
     * @param isOnline
     * @return void
     **/
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update GpReportModel  t set t.online = ?2 where t.identification = ?1")
    void updateIsOnline(String id,String isOnline);


    /**
     * 批量更改状态
     * @param status
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query(value = "update t_gp_report p set p.status =:status where p.identification in(:ids)",nativeQuery = true)
    void multilUpdateStatus(@Param(value = "status") String status, @Param(value = "ids") List<String> ids);

    /**
     * 更新email
     * @param id
     * @param mail
     */
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update GpReportModel  t set t.email = ?2 where t.identification = ?1")
    void updateEmail(String id, String mail);

    /**
     * findAllResultGpReportModel
     * @return
     */
    @Query(value = "select new com.starp.zoo.entity.zoo.ResultGpReportModel(t.identification,t.name) from GpReportModel  t")
    List<ResultGpReportModel> findAllResultGpReportModel();
}
