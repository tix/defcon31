package com.starp.zoo.repo.zoo;


import com.starp.zoo.entity.zoo.AppInstallModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author covey
 */
@Repository
public interface AppInstallRepo extends JpaRepository<AppInstallModel, String>, JpaSpecificationExecutor<AppInstallModel> {

    /**
     * 根据appName查找
     * @param country
     * @param appName
     * @param begin
     * @param end
     * @return
     */
    @Query("select p from AppInstallModel  p where p.country =?1 and p.appName = ?2 and p.dayTime >= ?3 and p.dayTime <= ?4")
    List<AppInstallModel> findAppInstallModels(String country, String appName, Date begin, Date end);

}
