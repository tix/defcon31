package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AffPostBackIeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

/**
 * @author Charles
 * @date 2018/12/12
 * @description :
 */
public interface AffPostBackIeRepo extends JpaRepository<AffPostBackIeModel, String> {

    /**
     * 统计某个时间段内收到的点击
     * @param telcoid
     * @param sc
     * @param ke
     * @param startTime
     * @param endTime
     * @return
     */
    long countByTelcoidAndShortCodeAndKeywordAndCreateTimeBetween(String telcoid, String sc, String ke, Date startTime, Date endTime);
}
