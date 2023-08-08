package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.HttpHtmlModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author vic.zhao
 * @date 2019/9/27
 * @description :
 */
@Repository
public interface HttpHtmlRepo extends JpaRepository<HttpHtmlModel, String> {

    /***
     * update status
     * @param  id
     * @param  param5
     * @param  param6
     * @return void
     **/
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update HttpHtmlModel t set t.param5=?2,t.param6=?3 where t.identification=?1")
    void updateParam5AndParam6(String id, String param5, String param6);

    /***
     * update status
     * @param  id
     * @param  param6
     * @return void
     **/
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update HttpHtmlModel t set t.param6=?2 where t.identification=?1")
    void updateParam6(String id, String param6);

    /***
     * update status
     * @param  id
     * @param  param7
     * @return void
     **/
    @Transactional(rollbackFor = Exception.class)
    @Modifying
    @Query("update HttpHtmlModel t set t.param7=?2 where t.identification=?1")
    void updateParam7(String id, String param7);

    /**
     * 根据主键查找
     * @param pid
     * @return
     */
    HttpHtmlModel findByIdentificationLike(String pid);

    /**
     * 主键查找
     * @param id
     * @return
     */
    HttpHtmlModel findByIdentification(String id);
}
