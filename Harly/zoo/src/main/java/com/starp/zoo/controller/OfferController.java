package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.service.IOfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * @author Charles
 * Created by Charles, DATE, 2018/11/8.
 */
@Controller
public class OfferController {

    @Autowired
    private IOfferService offerService;

    @RequestMapping(value = "/offer/save", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo saveOfferModel(@RequestBody OfferModel offerModel){
        offerService.save(offerModel);
        return ResponseInfoUtil.success();
    }

    @RequestMapping(value = "/offer/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject getOfferList(@RequestParam String offerName, @RequestParam String country,
                                    @RequestParam String operator, @RequestParam String partner, @RequestParam String callback, HttpServletRequest request){
        String isNewOffer = request.getParameter("isNewOffer");
        return new JSONPObject(callback, ResponseInfoUtil.success(offerService.getAll(offerName, country, operator, partner,isNewOffer)));
    }

    @RequestMapping(value = "/offer/get", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject getOffer(@RequestParam String id, @RequestParam String callback){
        return new JSONPObject(callback, offerService.getById(id));
    }

    @RequestMapping(value = "/offer/delete", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo deleteOffer(@RequestParam String id){
        Integer code = offerService.deleteById(id);
        if(code == 0){
            return ResponseInfoUtil.success();
        }
        return ResponseInfoUtil.error();
    }

    @RequestMapping(value = "/offer/copy", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo copyOffer(@RequestBody OfferModel offerModel)throws Exception{
        offerService.copy(offerModel);
        return ResponseInfoUtil.success();
    }

    @RequestMapping(value = "/offer/name/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject getOfferNameList(@RequestParam String isNewOffer,@RequestParam String callback)throws Exception{
        return new JSONPObject(callback, ResponseInfoUtil.success(offerService.getOfferNameList(isNewOffer)));
    }

    @RequestMapping(value = "/offer/partner/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject getOfferPartnerList(@RequestParam String callback)throws Exception{
        return new JSONPObject(callback, ResponseInfoUtil.success(offerService.getOfferPartnerList()));
    }

    @RequestMapping(value = "/offer/script/list", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject getOfferScriptList(@RequestParam String callback)throws Exception{
        return new JSONPObject(callback, ResponseInfoUtil.success(offerService.getOfferScriptList()));
    }

    @RequestMapping(value = "/offer/check/use", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo checkOfferUse(@RequestBody Map<String, String> params)throws Exception{
        String id = params.get("identification");
        String country = params.get("country");
        String operator = params.get("operator");
        return ResponseInfoUtil.success(offerService.checkUse(id, country,operator) == null ? 0 : 1);
    }

    @RequestMapping(value = "/offer/delete/relate", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo deleteRelate(@RequestBody Map<String, String> params){
        String offerId = params.get("identification");
        offerService.deleteAppRelate(offerId);
        return ResponseInfoUtil.success();
    }

    @RequestMapping(value = "/offer/check/name", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Boolean checkExists(@RequestParam String name, @RequestParam int type, @RequestParam String id){
        if(!offerService.checkExists(name, type, id)){
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/offer/check/offerId", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Boolean checkIdExists(@RequestParam String offerId, @RequestParam int type, @RequestParam String id){
        if(!offerService.checkIdExists(offerId, type, id)){
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/offer/stack/fetch",method = {RequestMethod.GET})
    @ResponseBody
    public ResponseInfo fetchStackOffer(@RequestParam String country, @RequestParam String operator, @RequestParam(required = false) Integer closeCount){
        return  ResponseInfoUtil.success(offerService.fechStackOffer(operator,country,closeCount));
    }




    @GetMapping(value = "/delete/timeout/redis")
    @ResponseBody
    public ResponseInfo deleteEpmRedis(){
        offerService.deleteTimoutRedis();
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/delete/appEvent/redis")
    @ResponseBody
    public ResponseInfo deleteAppEventCodeRedis(){
        offerService.deleteAppEventRedis();
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/offer/check/mocr")
    @ResponseBody
    public ResponseInfo checkMoCr(){
        offerService.checkOfferMoCr();
        return ResponseInfoUtil.success();
    }

    @RequestMapping("/offer/update/initEpm")
    @ResponseBody
    public ResponseInfo updateInitEpm(@RequestBody String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        List<JSONObject> arpuAvgVoList = (List<JSONObject>)jsonObject.get("arpuAvgVoList");
        offerService.updateInitEpm(arpuAvgVoList);
        return ResponseInfoUtil.success();
    }

    @RequestMapping("/offer/oldSelectOfferNames")
    @ResponseBody
    public ResponseInfo oldSelectOfferNames() {
        return ResponseInfoUtil.success(offerService.getOldAutoStackOfferNames());
    }

    @RequestMapping("/offer/autoStackList")
    @ResponseBody
    public ResponseInfo autoStackList(@RequestBody JSONObject jsonObject) {
        JSONArray offerNames = jsonObject.getJSONArray("offerNames");
        return ResponseInfoUtil.success(offerService.getAutoStackList(offerNames));
    }

    @RequestMapping("/offer/delete/redisAutoStackList")
    @ResponseBody
    public ResponseInfo deleteRedisAutoStackList() {
        offerService.deleteRedisAutoStackList();
        return ResponseInfoUtil.success();
    }

    @RequestMapping("/offer/deleteAutoStackOffer")
    @ResponseBody
    public ResponseInfo deleteAutoStackOffer(@RequestParam(required = false) String offerId) {
        offerService.deleteAutoStackOffer(offerId);
        return ResponseInfoUtil.success();
    }

    @RequestMapping("/offer/autoStackConfig")
    @ResponseBody
    public ResponseInfo autoStackConfig(@RequestBody JSONObject jsonObject) {
        List<String> appNameList = jsonObject.getJSONArray("appNames").toJavaList(String.class);
        List<JSONObject> opeAndOfferList = jsonObject.getJSONArray("opeAndOffer").toJavaList(JSONObject.class);
        return ResponseInfoUtil.success(offerService.getAutoStackConfigInfo(appNameList, opeAndOfferList));
    }

    @GetMapping("/offer/auto/usableOffer")
    @ResponseBody
    public ResponseInfo autoGetUsableOffer() {
        return ResponseInfoUtil.success(offerService.getUsableOfferOption());
    }

    @GetMapping("/auto/usableGroup")
    @ResponseBody
    public ResponseInfo autoGetUsableGroup() {
        return ResponseInfoUtil.success(offerService.getUsableGroupOption());
    }

    @RequestMapping("/handle/usableGroup")
    @ResponseBody
    public ResponseInfo handleOfferAddGroup(@RequestBody JSONObject jsonObject) {
        List<String> offerIds = jsonObject.getJSONArray("offerIds").toJavaList(String.class);
        String testGroup = jsonObject.getString("testGroup");
        if (offerService.handleOfferAddGroup(offerIds, testGroup)) {
            return ResponseInfoUtil.success();
        }
        return ResponseInfoUtil.error();
    }

    @RequestMapping("/offer/refreshOfferMo")
    @ResponseBody
    public ResponseInfo refreshOfferMo(@RequestParam(required = false) String appName) throws Exception {
        offerService.refreshOfferMo(appName);
        return ResponseInfoUtil.success();
    }

    @RequestMapping("/offer/refreshAllOfferMo")
    @ResponseBody
    public ResponseInfo refreshAllOfferMo(@RequestBody List<String> appNames) throws Exception {
        for (String appName : appNames) {
            offerService.refreshOfferMo(appName);
        }
        return ResponseInfoUtil.success();
    }

    @RequestMapping("/offer/freeOfferInApp")
    @ResponseBody
    public ResponseInfo freeOfferInApp(@RequestParam(required = false) String appName) {
        if (offerService.freeOfferInApp(appName)) {
            return ResponseInfoUtil.success();
        }
        return ResponseInfoUtil.error(ResponseInfoEnum.FREE_STACK_ERROR.getCode(), ResponseInfoEnum.FREE_STACK_ERROR.getMsg());
    }

    @RequestMapping("/offer/advanceFreeOffer")
    @ResponseBody
    public ResponseInfo advanceFreeOffer(@RequestParam(required = false) String offerId, @RequestParam(required = false) String appName) {
        if (offerService.advanceFreeOffer(offerId, appName)) {
            return ResponseInfoUtil.success();
        }
        return ResponseInfoUtil.error(ResponseInfoEnum.FREE_STACK_ERROR.getCode(), ResponseInfoEnum.FREE_STACK_ERROR.getMsg());
    }

    @RequestMapping("/offer/getEpmListRedis")
    @ResponseBody
    public ResponseInfo getEpmListRedis(@RequestBody JSONObject jsonObject) {
        List<String> appNames = jsonObject.getJSONArray("appNames").toJavaList(String.class);
        return ResponseInfoUtil.success(offerService.getEpmListRedis(appNames));
    }

}
