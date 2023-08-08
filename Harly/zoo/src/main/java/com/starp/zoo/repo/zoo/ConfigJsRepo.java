package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.AutoScriptModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 
 * @Author David
 * @Date 18:01 2018/12/18
 **/
public interface ConfigJsRepo extends JpaRepository<AutoScriptModel,String>{

    /**
      * 查找符合条件的AutoScriptModel
      * @param id
      * @return AutoScriptModel
      */
    AutoScriptModel findByIdentification(String id);


    /**
      * 查找符合条件的AutoScriptModel
      * @param jsname
      * @return AutoScriptModel
      */
    List<AutoScriptModel> findByNameLike(String jsname);
}
