package com.starp.zoo.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.service.IApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/7.
 */
@Controller
public class ApplicationController {
    
    @Autowired
    private IApplicationService appInfoService;
    
    @RequestMapping(value = "/application/save", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo saveAppModel(@RequestBody ApplicationModel applicationModel){
        appInfoService.save(applicationModel);
        return ResponseInfoUtil.success();
    }
    
    @RequestMapping(value = "/application/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject getAppList(@RequestParam Integer status, @RequestParam String productType, @RequestParam String callback){
        return new JSONPObject(callback, ResponseInfoUtil.success(appInfoService.getAll(status, productType)));
    }
    
    @RequestMapping(value = "/application/get", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject getApp(@RequestParam String appId, @RequestParam String callback){
        return new JSONPObject(callback, appInfoService.getById(appId));
    }
    
    @RequestMapping(value = "/application/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo deleteApp(@RequestParam String appId){
        appInfoService.deleteById(appId);
        return ResponseInfoUtil.success();
    }
    
    @RequestMapping(value = "/application/update/status", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo updateStatus(@RequestParam String appId, @RequestParam Integer status){
        appInfoService.updateStatus(appId, status);
        return ResponseInfoUtil.success();
    }
    
    @RequestMapping(value = "/application/check/name", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Boolean checkExists(@RequestParam String name, @RequestParam int type, @RequestParam String id){
        if(!appInfoService.checkExists(name, type, id)){
            return true;
        }
        return false;
    }



}
