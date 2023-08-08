package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AffPostBackModel;
import com.starp.zoo.entity.zoo.ComparativeModdel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

/**
 * @author covey
 */
public interface ComparativeRepo extends CrudRepository<AffPostBackModel, Long> {


    /**
     * 用来和EPM做比较
     * @param beganDate
     * @param endDate
     * @return
     */
    @Query(value = "select new com.starp.zoo.entity.zoo.ComparativeModdel(t.systemOfferId,count(t)) from AffPostBackModel t where t.createTime>=?1 and t.createTime<=?2 group by t.systemOfferId")
    List<ComparativeModdel> comparativeData(Date beganDate, Date endDate);
}
