package com.starp.zoo.repo.statistics;

import com.starp.zoo.entity.statistics.MyMapModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/8.
 */
@Repository
public interface MyMapRepo extends JpaRepository<MyMapModel, String> {

    /**
     * 获取所有符合条件的MyMapModel
     *
     * @param keyValue keyValue key值
     * @return MyMapModel
     */
    
    

    List<MyMapModel> findAllByKeyNameOrderByItemKeyAscCreateTimeAsc(String keyValue);
}
