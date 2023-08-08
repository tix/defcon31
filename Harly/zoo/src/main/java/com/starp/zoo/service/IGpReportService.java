package com.starp.zoo.service;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.GpReportModel;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;

import java.util.List;


/***
 *
 * @Author David
 * @Date 15:37 2019/2/25
 * @param
 * @return
 **/
public interface IGpReportService {

    /***
     * save model
     * @Author David
     * @Date 15:37 2019/2/25
     * @param  gpReportModel
     * @return void
     **/
    void save(GpReportModel gpReportModel);

    /***
     * delete config
     * @Author David
     * @Date 15:49 2019/2/25
     * @param  id
     * @return void
     **/
    void delete(String id);

    /***
     * get config list
     * @Author David
     * @Date 15:59 2019/2/25
     * @param  name
     * @param page
     * @param limit
     * @param online
     * @return java.util.List<com.starp.zoo.entity.zoo.GpReportModel>
     **/
    PageVO getConfigList(String name,int page,int limit,String online);

    /***
     * find by identification
     * @Author David
     * @Date 17:01 2019/2/25
     * @param  id
     * @return com.starp.zoo.entity.zoo.GpReportModel
     **/
    GpReportModel findById(String id);

    /***
     * change status
     * @Author David
     * @Date 17:27 2019/2/25
     * @param  id
     * @param type
     * @return void
     **/
    void changeStatus(String id, int type);

    /***
     * get All config
     * @Author David
     * @Date 19:39 2019/2/25
     * @param
     * @return java.util.List<com.starp.zoo.entity.zoo.GpReportModel>
     **/
    List<GpReportModel> getStartConfig();


    /***
     * 开启检查
     * @Author David
     * @Date 19:44 2019/2/25
     * @param  model
     * @return void
     **/
    void checkConfig(GpReportModel model);

    /**
     * get all report names
     * @return
     */
    List<OptionVO> getNames();

    /**
     * 批量修改状态
     * @param status
     * @param ids
     */
    void multiUpdateStatus(String status, List<String> ids);


    /**
     * 批量删除
     * @param ids
     */
    void multiDelete(List<String> ids);

    /**
     * 更新email
     * @param id
     * @param mail
     */
    void updateEmail(String id, String mail);
}
