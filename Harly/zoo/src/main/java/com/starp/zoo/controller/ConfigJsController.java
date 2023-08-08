package com.starp.zoo.controller;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.AutoScriptModel;
import com.starp.zoo.service.IConfigJsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @Author David
 * @Date 18:02 2018/12/18
 **/
@Controller
public class ConfigJsController {

    @Autowired
    private IConfigJsService configJsService;

    @RequestMapping(value="/config/js/list")
    @ResponseBody
    public JSONPObject getJsConfigInfo(HttpServletRequest request,@RequestParam String callback){
        String jsname = request.getParameter("jsname");
        List<AutoScriptModel> scriptModels = new ArrayList<>();
        if(StringUtils.isEmpty(jsname)){
            List<AutoScriptModel> scriptModelList = configJsService.getAllConfig();
            scriptModels.addAll(scriptModelList);
        }else{
            List<AutoScriptModel> models = configJsService.getConfigByName(jsname);
            scriptModels.addAll(models);
        }
        return  new JSONPObject(callback, ResponseInfoUtil.success(scriptModels));
    }

    @RequestMapping(value="/jsconfig/get",method = RequestMethod.GET)
    @ResponseBody
    public JSONPObject getJsConfigByName(@RequestParam String callback){
        List<AutoScriptModel> scriptModelList =configJsService.getAllConfig();
        return new JSONPObject(callback,scriptModelList);
    }


    @RequestMapping(value="/jsconfig/save",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo saveJsConfig(@RequestBody Map<String,String> map){
        try {
            String jsname = map.get("jsName").toString();
            String jsURL = map.get("jsURL").toString();
            String jsScirpt = map.get("jsScript").toString();
            int jsType = Integer.parseInt(map.get("jsType").toString());
            AutoScriptModel scriptModel = new AutoScriptModel();
            scriptModel.setName(jsname);
            scriptModel.setRegular(jsURL);
            scriptModel.setScript(jsScirpt);
            scriptModel.setEventType(jsType);
            configJsService.save(scriptModel);
            return ResponseInfoUtil.success();
        }
        catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }

    @RequestMapping(value="/jsconfig/delete",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo deleteJsConfig(HttpServletRequest request){
        try{
            String id = request.getParameter("id");
            configJsService.delete(id);
            return ResponseInfoUtil.success();
        }catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }

    @RequestMapping(value="/jsconfig/update",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo updateJsConfig(@RequestBody Map<String,String> map){
        try {
            String id = map.get("id").toString();
            String jsname = map.get("jsName").toString();
            String jsURL = map.get("jsURL").toString();
            String jsScirpt = map.get("jsScript").toString();
            int jsType = Integer.parseInt(map.get("jsType").toString());
            AutoScriptModel scriptModel = configJsService.findConfigJs(id);
            scriptModel.setIdentification(id);
            scriptModel.setName(jsname);
            scriptModel.setRegular(jsURL);
            scriptModel.setScript(jsScirpt);
            scriptModel.setEventType(jsType);
            configJsService.save(scriptModel);
            return ResponseInfoUtil.success();
        }
        catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }




    @RequestMapping(value = "/config/js/show",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public JSONPObject showConfigJs(HttpServletRequest request,@RequestParam String callback){
        String id = request.getParameter("identification");
        AutoScriptModel model = configJsService.findConfigJs(id);
        return new JSONPObject(callback,model);
    }

}
