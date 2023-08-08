package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.AutoScriptModel;

import java.util.List;

/**
 * 
 * @Author David
 * @Date 18:06 2018/12/18
 * @param  
 * @return 
 **/
public interface IConfigJsService {
    /**
     * 获取AutoScriptModel
     * @Author David
     * @Date 17:16 2018/12/18
     * @param
     * @return AutoScriptModel
     **/
    List<AutoScriptModel> getAllConfig();

    /**
     * 保存AutoScriptModel
     * @Author David
     * @Date  2018/12/18
     * @param scriptModel
     * @return
     **/
    void save(AutoScriptModel scriptModel);


    /**
     * 通过名字获取AutoScriptModel
     * @Author David
     * @Date 17:17 2018/12/18
     * @param jsname
     * @return AutoScriptModel
     **/
    List<AutoScriptModel> getConfigByName(String jsname);


    /**
     * 通过identification获取AutoScriptModel
     * @Author David
     * @Date 17:18 2018/12/18
     * @param id
     * @return AutoScriptModel
     **/
    AutoScriptModel findConfigJs(String id);

    /**
     * 删除AutoScriptModel
     * @Author David
     * @Date 17:18 2018/12/18
     * @param id
     * @return
     **/
    void delete(String id);
}
