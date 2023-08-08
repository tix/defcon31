package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.NewScriptModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/***
 *
 * @Author David
 * @Date 11:40 2019/1/3
 * @param  
 * @return 
 **/
@Repository
public interface NewAutoScriptRepo extends JpaRepository<NewScriptModel,String>, JpaSpecificationExecutor<NewScriptModel>{


    /**
     * 查找符合条件的NewScriptModel
     * @param id
     * @return AutoScriptModel
     */
    NewScriptModel findByIdentification(String id);


    /**
     * 查找符合条件的NewScriptModel
     * @param jsname
     * @return AutoScriptModel
     */
    List<NewScriptModel> findByNameLike(String jsname);


    /***
     * 根据country 跟type类型查找list集合
     * @Author David
     * @Date 15:09 2019/1/7
     * @param coutry
     * @param type
     * @return java.util.List<com.starp.zoo.entity.zoo.NewScriptModel>
     **/
    List<NewScriptModel> findAllByCountryAndEventType(String coutry,int type);



    /***
     * 根据country查找list
     * @Author David
     * @Date 15:11 2019/1/7
     * @param country
     * @return java.util.List<com.starp.zoo.entity.zoo.NewScriptModel>
     **/
    List<NewScriptModel> findAllByCountry(String country);


    /**
     * 批量删除
     * @param ids
     */
    @Modifying
    @Transactional(rollbackFor = Exception.class)
    void deleteAllByIdentificationIn(List<String> ids);
}

