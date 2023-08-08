package com.starp.zoo.repo.zoo;

import com.starp.zoo.entity.zoo.ResultScriptModel;
import com.starp.zoo.entity.zoo.ScriptModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Charles
 * @date 2019/3/5
 * @description :
 */
@Repository
public interface ScriptRepo extends JpaRepository<ScriptModel, String>, JpaSpecificationExecutor<ScriptModel> {


    /**
     * 根据创建时间倒叙获取所有
     * @return
     */
    List<ScriptModel> findAllByOrderByCreateTimeDesc();

    /**
     * 获取js脚本
     * @param offerId
     * @return
     */
    @Query(value = "select t1.* from t_script t1 left join t_offer_auto_script t2 on  t1.identification = t2.auto_script_id AND t1.type<>2 AND t2.offer_id=?1 where t2.offer_id is not null order by t2.sort_index asc",
    nativeQuery = true)
    List<ScriptModel> findOfferScript(String offerId);

    /**
     * 查是否存在重复脚本
     * @param script
     * @return
     */
    boolean existsByScript(String script);

    /**
     * 根据国家查找
     * @param country
     * @return
     */
    List<ScriptModel> findAllByCountry(String country);


    /**
     * 根据国家查找正则
     * @param country
     * @param type
     * @param eventType
     * @return
     */
    @Query("select p from ScriptModel p where p.country = ?1 and p.type = ?2 and p.eventType =?3")
    List<ScriptModel> findAllScriptModel(String country,Integer type,Integer eventType);


    /**
     * 根据type查找model
     * @param type
     * @return
     */
    List<ScriptModel> findByType(Integer type);

    /**
     * 查询所有JS名称
     * @return
     */
    @Query(value = "select distinct t.name from ScriptModel t")
    List<String> findallNames();

    /**
     * 根据name查询JS
     * @param name
     * @return
     */
    @Query(value = "select t.script from ScriptModel t where t.name=?1")
    String findByName(String name);

    /**
     * 根据名称查找model
     * @param name
     * @return
     */
    ScriptModel findFirstByName(String name);

    /**
     * 重写js 查找
     * @param country
     * @return
     */
    @Query("select t from ScriptModel t where t.type = 1 and t.country=?1")
    List<ScriptModel> findQuery(String country);

    /**
     * 找到对用JS
     * @param regular
     * @return
     */
    @Query("SELECT p FROM ScriptModel p WHERE  p.regular LIKE CONCAT('%',?1,'%') ")
    List<ScriptModel> findScriptModel(String regular);

    /**
     * findAllResultScriptModel
     * @return
     */
    @Query(value = "select new com.starp.zoo.entity.zoo.ResultScriptModel(p.identification,p.name,p.script) from ScriptModel p ")
    List<ResultScriptModel> findAllResultScriptModel();
}
