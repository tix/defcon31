package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AutoScriptModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/9.
 */
@Repository
public interface AutoScriptRepo extends JpaRepository<AutoScriptModel, String> {

    /**
      * 查找符合条件的AutoScriptModel
      * @param
      * @return AutoScriptModel
      */
    @Query("select t from AutoScriptModel t order by t.name asc, t.createTime desc ")
    List<AutoScriptModel> findQuery();

    /**
     * 根据时间倒叙取出所有
     * @return
     */
    List<AutoScriptModel> findAllByOrderByCreateTimeDesc();
}
