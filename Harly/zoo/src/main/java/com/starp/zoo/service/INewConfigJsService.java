package com.starp.zoo.service;

import com.starp.zoo.entity.zoo.NewScriptModel;
import com.starp.zoo.entity.zoo.OfferAutoScriptModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;

/***
 *
 * @Author David
 * @Date 11:47 2019/1/3
 * @param
 * @return
 **/
public interface INewConfigJsService {

    /**
     * 获取NewAutoScriptModel
     * @Author David
     * @Date 17:16 2018/12/18
     * @param
     * @return AutoScriptModel
     **/
    List<NewScriptModel> getAllConfig();

    /**
     * 保存NewAutoScriptModel
     * @Author David
     * @Date  2018/12/18
     * @param scriptModel
     * @return
     **/
    void save(NewScriptModel scriptModel);


    /**
     * 通过名字获取NewAutoScriptModel
     * @Author David
     * @Date 17:17 2018/12/18
     * @param jsname
     * @param country
     * @return AutoScriptModel
     **/
    List<NewScriptModel> getJsConfig(String jsname,String country);


    /**
     * 通过identification获取NewAutoScriptModel
     * @Author David
     * @Date 17:18 2018/12/18
     * @param id
     * @return AutoScriptModel
     **/
    NewScriptModel findConfigJs(String id);

    /**
     * 删除NewAutoScriptModel
     * @Author David
     * @Date 17:18 2018/12/18
     * @param id
     * @return
     **/
    void delete(String id);

    /***
     * 通过国家，类型找到新ScriptModel
     * @Author David
     * @Date 16:11 2019/1/7
     * @param  jsType
     * @param jsUrl
     * @param country
     * @return com.starp.zoo.entity.zoo.NewScriptModel
     **/
    NewScriptModel findJsByCountryAndType(String jsUrl,String country, Integer jsType);

}
