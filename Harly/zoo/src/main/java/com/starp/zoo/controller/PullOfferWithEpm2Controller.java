//package com.starp.zoo.controller;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.starp.zoo.common.ResponseInfo;
//import com.starp.zoo.common.ResponseInfoUtil;
//import com.starp.zoo.constant.CacheNameSpace;
//import com.starp.zoo.constant.Constant;
//import com.starp.zoo.constant.LogConstant;
//import com.starp.zoo.constant.ZooConstant;
//import com.starp.zoo.entity.zoo.ApplicationModel;
//import com.starp.zoo.entity.zoo.OfferModel;
//import com.starp.zoo.entity.zoo.ScriptModel;
//import com.starp.zoo.service.*;
//import com.starp.zoo.util.IpUtil;
//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.*;
//
///**
// * @author Charles
// * @date 2019/6/24
// * @description :
// */
//@Slf4j
//@RestController
//public class PullOfferWithEpm2Controller {
//
//    @Autowired
//    private IApplicationService applicationService;
//
//    @Autowired
//    private IGetOfferService getOfferService;
//
//    @Autowired
//    private IAffService affService;
//
//    @Autowired
//    private IEpmService epmService;
//
//    @Autowired
//    private IOfferService offerService;
//
//    @Autowired
//    private IScriptService scriptService;
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    /**
//     * APP 拉取 offer 接口
//     * @param params
//     * @param appId
//     * @param mnc
//     * @param isLog
//     * @param usedList
//     * @param isTest
//     * @param request
//     * @param response
//     * @return
//     * @throws IOException
//     */
//    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
//    @CrossOrigin(origins = "*", maxAge = 3600)
//    @RequestMapping(value = {"/alo/jkl", "/r5SW/7wU5"}, method = {RequestMethod.POST, RequestMethod.GET})
//    @ResponseBody
//    public ResponseInfo getConfig(@RequestBody(required = false) Map<String, Object> params, @RequestParam(required = false) String deviceId,
//                                  @RequestParam(required = false) String appId, @RequestParam(required = false) String mnc,
//                                  @RequestParam(required = false, value = "log") Boolean isLog, @RequestParam( name="used", required = false) List<String> usedList,
//                                  @RequestParam(required = false, value = "test") Boolean isTest,
//                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String ipAddress = IpUtil.getIpAddr(request);
//        if(params != null && StringUtils.isEmpty(appId) && StringUtils.isEmpty(mnc) && isLog == null && usedList == null){
//            log.info("{} [PULL OFFER WITH EPM] [REQUEST BODY:{}]", LogConstant.ZOO, JSON.toJSONString(params));
//            appId = (String)params.get("appId");
//            deviceId = (String)params.get("deviceId");
//            mnc = (String)params.get("mnc");
//            //所有跑过的 offer 的主键
//            usedList = (List<String>) params.get("used");
//        }
//        String operator = Constant.OPERATORMAP.get(mnc);
//        OfferModel resultModel = null;
//        //如果前一次的 list 为空或者 大小为0，即为第一次获取，根据epm返回
//
//        if(!StringUtils.isEmpty(appId)){
//            ApplicationModel applicationModel = applicationService.getById(appId);
//            if(isLog != null && isLog) {
//                log.info("{} [PULL_TASK_WITH_EPM] [APP:{}] [IP:{}] [APP_MODEL:{}]", LogConstant.ZOO, appId, ipAddress, operator, JSON.toJSONString(applicationModel));
//            }
//            // 判断是否超过拉取次数或者app是否存在且为开启
//            boolean isCanPull = applicationModel != null && usedList != null && usedList.size() <= applicationModel.getMaxPullNum() && applicationModel.getStatus() == ZooConstant.STATUS_1;
//            if(!isCanPull){
//                return ResponseInfoUtil.success();
//            }
//            resultModel = getOfferService.getEnableOfferModel(false, deviceId, ZooConstant.CATEGORY_APP, appId, ipAddress, operator, usedList, isLog, false);
//            if(resultModel != null && resultModel.getCallbackType() != ZooConstant.CALLBACK_TYPE_2){
//                //把配置中的链接根据配置情况替换完成
//                String clickId = affService.getClickId(appId, resultModel.getOfferId());
//                if (resultModel.getPartner().equalsIgnoreCase(ZooConstant.IE)) {
//                    clickId = resultModel.getPartnerOfferId() + UUID.randomUUID().toString();
//                }
//                String url = PullOfferWithEpmController.formatUrl(appId, clickId, resultModel);
//                resultModel.setUrl(url);
//                //统计点击信息
//                affService.saveClickInfo(ipAddress, deviceId, clickId, resultModel, request.getHeader(ZooConstant.USER_AGENT), appId, ZooConstant.CATEGORY_APP, true);
//            }
//        }
//        if(isTest != null && isTest && resultModel != null ){
//            response.sendRedirect(resultModel.getUrl());
//        }
//        String result = JSON.toJSONString(getResultMap(resultModel));
//        if (!StringUtils.isEmpty(request)) {
//            Base64.Encoder encoder = Base64.getEncoder();
//            result = encoder.encodeToString(result.getBytes());
//            return ResponseInfoUtil.success(result);
//        } else {
//            return ResponseInfoUtil.success();
//        }
//    }
//
//    /**
//     * 获取同一 应用 所有的 offer 的主键
//     * @return
//     */
//    public List<OfferModel> getAllOffers(int type, String appId){
//        // 去查找app分配的tag
//        List<OfferModel> offers = offerService.findOffersByCategoryId(type, appId);
//        return offers;
//    }
//
//    public Map getResultMap(OfferModel model){
//        Map<String, Object> result = new HashMap<>(3);
//        if(model != null) {
//            //返回当前时间戳和 list
//            result.put("offer", getOfferJSON(model));
//            // 找到关联的 js 脚本
//            List<ScriptModel> scriptModels = scriptService.findScriptByOfferId(model.getIdentification());
//            if (scriptModels != null && scriptModels.size() > 0) {
//                result.put("script", getScriptJSON(scriptModels));
//            }
//        }
//        return result;
//    }
//
//    private List<JSONObject> getScriptJSON(List<ScriptModel> scriptModels) {
//        List<JSONObject> list = new ArrayList<>();
//        if (scriptModels != null && scriptModels.size() > 0) {
//            for (ScriptModel scriptModel : scriptModels) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("regular", scriptModel.getRegular());
//                jsonObject.put("script", scriptModel.getScript());
//                jsonObject.put("eventType", scriptModel.getEventType());
//                jsonObject.put("msisdnLocation", scriptModel.getMsisdnLocation());
//                jsonObject.put("pinRegular", scriptModel.getPinRegular());
//                jsonObject.put("pinInputLocation", scriptModel.getPinInputLocation());
//                jsonObject.put("pinConfirmLocation", scriptModel.getPinConfirmLocation());
//                jsonObject.put("pinBtnLocation", scriptModel.getPinBtnLocation());
//                list.add(jsonObject);
//            }
//        }
//        return list;
//    }
//
//    private JSONObject getOfferJSON(OfferModel model) {
//        JSONObject jsonObject = new JSONObject();
//        if (model != null) {
//            jsonObject.put("identification", model.getIdentification());
//            jsonObject.put("partner", model.getPartner());
//            jsonObject.put("isCrawlHtml", model.getIsCrawlHtml());
//            jsonObject.put("type", model.getType());
//            jsonObject.put("duration", model.getDuration());
//            jsonObject.put("url", model.getUrl());
//        }
//        return jsonObject;
//    }
//}
