package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.AppInstallModel;
import com.starp.zoo.service.IAppInstallService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author covey
 */
@Slf4j
@RestController
@RequestMapping("/appInstall")
public class AppInstallController {

    @Autowired
    private IAppInstallService appInstallService;

    /**
     * 查询所有appName
     * @return
     */
    @RequestMapping("/findAllAppNames")
    public ResponseInfo findAllAppNames(){
        List<OptionVO> appNames=appInstallService.findAppNames();
        return ResponseInfoUtil.success(appNames);
    }

    /**
     * 初始化页面数据
     * @return
     */
    @RequestMapping(value = "/findList", method = RequestMethod.POST)
    public ResponseInfo findList(@RequestBody JSONObject jsonObject){
        Integer page = jsonObject.getInteger("page");
        Integer limit = jsonObject.getInteger("limit");
        PageVO pageVO=appInstallService.findList(page,limit);
        return ResponseInfoUtil.success(pageVO);
    }

    /**
     * 根据参数查询
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/findByParam" ,method = RequestMethod.POST)
    public ResponseInfo findByParam(@RequestBody JSONObject jsonObject) throws ParseException {
        Integer page = jsonObject.getInteger("page");
        Integer limit = jsonObject.getInteger("limit");
        JSONArray countries = jsonObject.getJSONArray("countries");
        JSONArray appNames = jsonObject.getJSONArray("appNames");
        String beginStr = jsonObject.getString("begin");
        String endStr = jsonObject.getString("end");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date begin=null;
        Date end=null;
        if(beginStr!=null&&endStr!=null){
            begin = simpleDateFormat.parse(beginStr);
            end = simpleDateFormat.parse(endStr);
        }

        PageVO pageVO=appInstallService.findByParam(page,limit,countries,appNames,begin,end);
        return ResponseInfoUtil.success(pageVO);
    }

    /**
     * 保存
     * @param jsonObject
     * @return
     */
    @RequestMapping(value = "/save" ,method = RequestMethod.POST)
    public ResponseInfo save(@RequestBody JSONObject jsonObject) throws ParseException {
        String appName = jsonObject.getString("appName");
        String timeStr = jsonObject.getString("time");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dayTime = simpleDateFormat.parse(timeStr);
        JSONArray countries = jsonObject.getJSONArray("countries");
        for (int i = 0; i < countries.size(); i++) {
            AppInstallModel appInstallModel = new AppInstallModel();
            JSONObject object = countries.getJSONObject(i);
            String country = object.getString("country");
            String num = object.getString("num");
            appInstallModel.setAppName(appName);
            appInstallModel.setCountry(country);
            appInstallModel.setDayTime(dayTime);
            appInstallModel.setInstallation(Integer.parseInt(num));
            appInstallService.save(appInstallModel);
        }
        return ResponseInfoUtil.success();
    }

    /**
     * 修改
     * @param appInstallModel
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public ResponseInfo update(@RequestBody AppInstallModel appInstallModel){
        appInstallService.update(appInstallModel);
        return ResponseInfoUtil.success();
    }


    /**
     * 删除
     * @param appInstallModel
     * @return
     */
    @RequestMapping(value = "/deleteOne",method = RequestMethod.POST)
    public ResponseInfo deleteOne(@RequestBody AppInstallModel appInstallModel){
        appInstallService.delete(appInstallModel);
        return ResponseInfoUtil.success();
    }

    @PostMapping(value = "/check/appInstallNum")
    public String findAppAnalysis(@RequestBody String params){
        JSONObject jsonObject =  JSONObject.parseObject(params);
        log.info("receve app install check:{}",jsonObject.toJSONString());
        List<String> appIds = (List<String>) jsonObject.get("appId");
        List<String> times = (List<String>) jsonObject.get("time");
        String country = jsonObject.getString("country");
        JSONObject appInstallNum = appInstallService.findInstallNumByAppId(country,appIds,times);
        return appInstallNum.toJSONString();
    }

}
