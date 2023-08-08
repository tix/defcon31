package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AffApkPostBackModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author david
 */
public interface AffApkPostBackRepo extends JpaRepository<AffApkPostBackModel,String> {

    /**
     * 是否存在postback
     * @param ip
     * @return
     */
    boolean existsByIp(String ip);

}
