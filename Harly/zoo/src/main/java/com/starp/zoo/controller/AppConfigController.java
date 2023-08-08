package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.vo.AppOpCapVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Charles
 * @date 2019/3/6
 * @description :
 */
@RestController
@RequestMapping("/config/app")
public class AppConfigController {

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private StringRedisTemplate masterRedisTemplate;

    @PostMapping("/save")
    public ResponseInfo save(@RequestBody JSONObject jsonObject){
        JSONArray tags = jsonObject.getJSONArray(ZooConstant.TAG_GROUP);
        JSONArray operatorCaps = jsonObject.getJSONArray(ZooConstant.APP_OPERATOR_CAP_LIST);
        JSONArray operatorTopOffer = jsonObject.getJSONArray(ZooConstant.APP_OPERATOR_TOP_OFFER_LIST);
        JSONArray sdkOperators = jsonObject.getJSONArray(ZooConstant.SDK_APP_OPERATOR);
        JSONArray epmOperators = jsonObject.getJSONArray(ZooConstant.EPM_APP_OPERATOR);
        JSONArray emails = jsonObject.getJSONArray(ZooConstant.EMAILS);
        List<String> emailList =JSONArray.parseArray(emails.toString(),String.class);
        List<String> tagIds = null;
        List<AppOpCapVO> opCapList= new ArrayList<>();
        if(tags != null){
            tagIds = JSONObject.parseArray(tags.toJSONString(), String.class);
        }
        if(operatorCaps != null){
            opCapList = JSONObject.parseArray(operatorCaps.toJSONString(),AppOpCapVO.class);
        }
        ApplicationModel applicationModel = JSONObject.parseObject(jsonObject.toJSONString(), ApplicationModel.class);
        applicationModel.setEmails(emailList);
        applicationService.saveConfig(applicationModel, tagIds, opCapList,operatorTopOffer,sdkOperators,epmOperators);
        return ResponseInfoUtil.success();
    }

    @PostMapping("open/appLog")
    public ResponseInfo openAppLog(@RequestBody JSONObject jsonObject) {
        List<String> operators = jsonObject.getJSONArray("operators").toJavaList(String.class);
        List<String> appNames = jsonObject.getJSONArray("appNames").toJavaList(String.class);
        applicationService.openAppLog(operators, appNames);
        return ResponseInfoUtil.success();
    }

    @RequestMapping("close/appLog")
    public ResponseInfo closeAppLog(@RequestParam(required = false) String logKey) {
        masterRedisTemplate.opsForHash().delete(CacheNameSpace.APP_USER_EVENT_LOG_STATUS, logKey);
        return ResponseInfoUtil.success();
    }

    @PostMapping("get/appLogConfig")
    public ResponseInfo getAppLogConfig(@RequestBody JSONObject jsonObject) {
        List<String> operators = jsonObject.getJSONArray("operators").toJavaList(String.class);
        List<String> appNames = jsonObject.getJSONArray("appNames").toJavaList(String.class);
        Integer page = jsonObject.getInteger("page");
        Integer limit = jsonObject.getInteger("limit");
        return ResponseInfoUtil.success(applicationService.getAppLogConfig(appNames, operators, page, limit));
    }

    @GetMapping("operator/topOffer")
    public ResponseInfo fetchAppOperatorOffers(@RequestParam String appId, @RequestParam String operator){
        return ResponseInfoUtil.success(applicationService.getAppOperatorOffers(appId,operator));
    }

    @GetMapping("/get/{id}")
    public ResponseInfo get(@PathVariable String id){
        return ResponseInfoUtil.success(applicationService.getAppById(id));
    }

    @PostMapping("/list")
    public ResponseInfo list(@RequestBody JSONObject jsonObject){
        int page = jsonObject.getInteger("page");
        int limit = jsonObject.getInteger("limit");
        JSONArray jsonArray = jsonObject.getJSONArray("appNames");
        List<String> appNames =  jsonArray == null ? null :Arrays.asList(jsonArray.toArray(new String[jsonArray.size()]));
        String packageName = jsonObject.getString("packageName");
        String productType = jsonObject.getString("productType");
        Integer status = jsonObject.getInteger("status");
        return ResponseInfoUtil.success(applicationService.getList(page, limit, appNames, packageName, productType, status));
    }

    @GetMapping("/delete/{id}")
    public ResponseInfo delete(@PathVariable String id){
        applicationService.deleteById(id);
        return ResponseInfoUtil.success();
    }

    @PostMapping("/multi/delete")
    public ResponseInfo delete(@RequestBody List<String> ids){
        applicationService.multiDelete(ids);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/change/status/{id}/{status}")
    public ResponseInfo status(@PathVariable String id, @PathVariable int status){
        applicationService.updateStatus(id, status);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/change/log/status/{id}/{status}")
    public ResponseInfo logStatus(@PathVariable String id, @PathVariable int status){
        applicationService.updateLogStatus(id, status);
        return ResponseInfoUtil.success();
    }

    @PostMapping("/multi/change/log/{status}")
    public ResponseInfo multiLogStatus(@RequestBody List<String> ids, @PathVariable int status){
        applicationService.multiUpdateLogStatus(ids, status);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/names")
    public ResponseInfo appNames(){
        return ResponseInfoUtil.success(applicationService.getAllAppNames());
    }

    @GetMapping("/packages")
    public ResponseInfo packageNames(){
        return ResponseInfoUtil.success(applicationService.getAllPackageNames());
    }

    @PostMapping("/tags/save/{appId}")
    public ResponseInfo saveTag(@PathVariable String appId, @RequestBody List<String> tagIds){
        applicationService.resetAppTag(appId, tagIds, false);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/options")
    public ResponseInfo appOptions(){
        return ResponseInfoUtil.success(applicationService.getAllAppOptions());
    }

    @GetMapping("/offerNum/{appId}")
    public ResponseInfo getAppOfferNum(@PathVariable String appId){
        return ResponseInfoUtil.success(applicationService.getAppOfferNum(appId));
    }


    @GetMapping(value = "/check/appName")
    public ResponseInfo checkAppName(@RequestParam(required = true) String name){
        return ResponseInfoUtil.success(applicationService.checkAppName(name));
    }

    /**
     * app epm 告警检查
     * @return
     */
    @GetMapping("/alarm/epm")
    public ResponseInfo checkAppEpmAlarm(){
        applicationService.checkAlarmEpm();
        return ResponseInfoUtil.success();
    }

}
