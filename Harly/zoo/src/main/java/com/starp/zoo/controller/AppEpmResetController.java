package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.AppEpmModel;
import com.starp.zoo.service.IAppEpmResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author david
 */
@RestController
@RequestMapping(value = "app/epmreset")
public class AppEpmResetController {

    @Autowired
    IAppEpmResetService service;

    @PostMapping(value = "/save")
    public ResponseInfo saveAppEpm(@RequestBody JSONObject params){
        service.saveModel(params);
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/fetch")
    public ResponseInfo fetchAppEpm(@RequestParam(required = false)String country){
        AppEpmModel appEpmModel = service.getModel(country);
        return ResponseInfoUtil.success(appEpmModel);
    }

    @PostMapping(value = "/get")
    public ResponseInfo getAppEpmList(@RequestBody JSONObject params){
        JSONArray countries = params.getJSONArray("country");
        List<AppEpmModel> appEpmModelList  = service.getModelList(JSONObject.parseArray(countries.toJSONString(), String.class));
        return ResponseInfoUtil.success(appEpmModelList);
    }

    @GetMapping(value = "/delete")
    public ResponseInfo deleteAppEpm(@RequestParam String country){
        service.deleteModel(country);
        return ResponseInfoUtil.success();
    }

    @PostMapping(value = "/resetHour")
    public ResponseInfo resetHour(@RequestBody JSONObject jsonObject){
        service.resetEpmHour(jsonObject);
        return ResponseInfoUtil.success();
    }


    @PostMapping(value = "/resetExponentiation")
    public ResponseInfo resetExponentiation(@RequestBody JSONObject jsonObject){
        service.resetEpmExponentiation(jsonObject);
        return ResponseInfoUtil.success();
    }

}
