package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.OfferTaskModel;
import com.starp.zoo.service.IOfferTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 
 * @Author David
 * @Date 18:13 2018/12/18
 * @param  
 * @return 
 **/

@Controller
public class ConfigOfferTaskListController {

    @Autowired
    private IOfferTaskService offerTaskService;



    @RequestMapping(value="/offer/tasklist")
    @ResponseBody
    public JSONPObject offerTaskList(HttpServletRequest request, @RequestParam String callback){
            int status = Integer.parseInt(request.getParameter("status"));
            String country = request.getParameter("country");
            String operator = request.getParameter("operator");
            String appId = request.getParameter("appId");
            List offer = new ArrayList<>();
            List<OfferTaskModel> returnlist = new ArrayList<>();
            List<OfferTaskModel>  offerTaskModelList = offerTaskService.findOfferTask(country,operator,status,appId);
            for(OfferTaskModel offerTaskModel:offerTaskModelList){
                offerTaskModel.setOfferId(offer.toString());
                returnlist.add(offerTaskModel);
            }

        return  new JSONPObject(callback, ResponseInfoUtil.success(returnlist));
    }

    @RequestMapping(value="/offer/task/show",method = RequestMethod.GET)
    @ResponseBody
    public JSONPObject getOfferTask(HttpServletRequest request,@RequestParam String callback) throws JSONException {
        String id = request.getParameter("identification");
        OfferTaskModel offerTaskModel = offerTaskService.getOfferTask(id);
        List<OfferTaskModel> taskModelList = offerTaskService.findSelectOffer(offerTaskModel.getAppId(),offerTaskModel.getCountry(),offerTaskModel.getOperator());
        List offer = new ArrayList<>();
        for(OfferTaskModel model:taskModelList){
            String[] offid = model.getOfferId().split(",");
            JSONObject object  = new JSONObject();
            for(int a=0;a<offid.length;a++){
                String offerName = offerTaskService.findOfferName(offid[a]);
                object.put(offerName,offid[a]);
            }
            offer.add(object);
        }
        offerTaskModel.setOfferId(offer.toString());
        return new JSONPObject(callback,offerTaskModel) ;
    }





    @RequestMapping(value = "/offer/app", method = RequestMethod.GET)
    @ResponseBody
    public JSONPObject getAppName(@RequestParam String callback) throws Exception{
        List<ApplicationModel> applicationModelList = offerTaskService.findAppInfo();
        return new JSONPObject(callback, applicationModelList);
    }

    @RequestMapping(value="/offer/findAll",method = RequestMethod.GET)
    @ResponseBody
    public JSONPObject getAllOffer(@RequestParam String callback){
        List<OfferModel> offerModelList = offerTaskService.findOfferInfo();
        return new JSONPObject(callback,offerModelList);
    }


    @RequestMapping(value = "/offertask/save", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo saveOfferModel(@RequestBody Map<String,Object> map){
        try{
        String operator = map.get("offerOperator").toString();
        String appid = map.get("offerApp").toString();
        String country = map.get("offerCountry").toString();
        String appName = map.get("offerAppName").toString();
        int time = Integer.parseInt(map.get("offerTime").toString());
        List list = (List) map.get("offerTask");
        for(int i=1;i<=list.size();i++){
            int level = i;
            String offerId = list.get(i-1).toString();
            String taskofferid = offerId.substring(1,offerId.indexOf("]")).replace(" ","");
            OfferTaskModel taskModel = new OfferTaskModel();
            taskModel.setAppId(appid);
            taskModel.setStatus(1);
            taskModel.setCountry(country);
            taskModel.setOperator(operator);
            taskModel.setOfferTime(time);
            taskModel.setAppName(appName);
            taskModel.setLevel(level);
            taskModel.setOfferId(taskofferid);
            offerTaskService.save(taskModel);
            }
        }catch (Exception e){
            return ResponseInfoUtil.error();
        }
        return ResponseInfoUtil.success();
    }

    @Transactional(rollbackFor = Exception.class)
    @RequestMapping(value = "/offertask/update", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo updateOfferTask(@RequestBody Map<String,Object> map){
        try{
            String identification = map.get("identification").toString();
            String operator = map.get("offerOperator").toString();
            String appId = map.get("offerApp").toString();
            String country = map.get("offerCountry").toString();
            String appName = map.get("offerAppName").toString();
            Date createtime = offerTaskService.getOfferTask(identification).getCreateTime();
            List offerTask = (List) map.get("offerTask");
            int time = Integer.parseInt(map.get("offerTime").toString());
            offerTaskService.deleteOfferTask(identification);
            for(int i=1;i<=offerTask.size();i++){
                int level = i;
                String offerId = offerTask.get(i-1).toString();
                String taskofferid = offerId.substring(1,offerId.indexOf("]")).replace(" ","");
                OfferTaskModel taskModel = new OfferTaskModel();
                taskModel.setAppId(appId);
                taskModel.setCreateTime(createtime);
                taskModel.setStatus(1);
                taskModel.setCountry(country);
                taskModel.setOperator(operator);
                taskModel.setOfferTime(time);
                taskModel.setAppName(appName);
                taskModel.setLevel(level);
                taskModel.setOfferId(taskofferid);
                offerTaskService.save(taskModel);
            }
        }catch (Exception e){
            return ResponseInfoUtil.error();
        }
        return ResponseInfoUtil.success();
    }

    @RequestMapping(value = "/offertask/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo deleteOfferTask(HttpServletRequest request){
        try {
            String identification = request.getParameter("id");
            offerTaskService.deleteOfferTask(identification);
            return ResponseInfoUtil.success();
        }catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }



    @RequestMapping(value = "/offerTask/changeType", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo changeOfferTaskType(HttpServletRequest request){
        try {
            String identification = request.getParameter("id");
            String type = request.getParameter("type");
            offerTaskService.changeType(identification,type);
            return ResponseInfoUtil.success();
        }catch (Exception e){
            return ResponseInfoUtil.error();
        }
    }

    @RequestMapping(value="/select/offer",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public JSONPObject selectOffers(HttpServletRequest request,@RequestParam String callback){
        String country = request.getParameter("country");
        String operator = request.getParameter("operator");
        List<OfferModel> offerModelList = offerTaskService.selectOffers(country,operator);
        return new JSONPObject(callback,offerModelList);
    }
}
