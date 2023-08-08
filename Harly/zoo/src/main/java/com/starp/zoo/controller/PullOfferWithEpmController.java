package com.starp.zoo.controller;

import com.starp.zoo.constant.*;
import com.starp.zoo.entity.zoo.*;
import com.starp.zoo.service.*;
import com.starp.zoo.util.PatternUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;

/**
 * @author Charles
 * @date 2019/1/21
 * @description : 根据epm 以及 offerTag 来获取可用的配置
 */
@Slf4j
@Controller
public class PullOfferWithEpmController {

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IGetOfferService getOfferService;

    @Autowired
    private IAffService affService;

    @Autowired
    private IEpmService epmService;

    @Autowired
    private IOfferService offerService;

    @Autowired
    private IScriptService scriptService;

    @Autowired
    private ISmartLinkService smartLinkService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
//    @CrossOrigin(origins = "*", maxAge = 3600)
//    @RequestMapping(value = {"/api/ftc/cfg/epm", "/gof"}, method = {RequestMethod.POST, RequestMethod.GET})
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
//                String clickId = affService.getClickId(appId, resultModel.getOfferId());;
//                if (resultModel.getPartner().equalsIgnoreCase(ZooConstant.IE)) {
//                    clickId = resultModel.getPartnerOfferId() + UUID.randomUUID().toString();
//                }
//                String url = formatUrl(appId, clickId, resultModel);
//                resultModel.setUrl(url);
//                //统计点击信息
//                affService.saveClickInfo(ipAddress, deviceId, clickId, resultModel, request.getHeader(ZooConstant.USER_AGENT), appId, ZooConstant.CATEGORY_APP, true);
//            }
//        }
//        if(isTest != null && isTest && resultModel != null ){
//            response.sendRedirect(resultModel.getUrl());
//        }
//        return ResponseInfoUtil.success(getResultMap(resultModel));
//    }
//
//    /**
//     * app 拉取加密返回
//     * @param params
//     * @param deviceId
//     * @param appId
//     * @param mnc
//     * @param isLog
//     * @param usedList
//     * @param isTest
//     * @param request
//     * @param response
//     * @return
//     * @throws Exception
//     */
//    @CrossOrigin(origins = "*", maxAge = 3600)
//    @RequestMapping(value = {"/api/ftc/cfg/epm-2", "/gof2"}, method = {RequestMethod.POST, RequestMethod.GET})
//    @ResponseBody
//    public ResponseInfo getConfigWithAes(@RequestBody(required = false) Map<String, Object> params, @RequestParam(required = false) String deviceId,
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
//            isLog = params.get("log") != null ? (Boolean) params.get("log") : false;
//            isTest = params.get("test") != null ? (Boolean) params.get("test") : false;
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
//                String url = formatUrl(appId, clickId, resultModel);
//                resultModel.setUrl(url);
//                //统计点击信息
//                affService.saveClickInfo(ipAddress, deviceId, clickId, resultModel, request.getHeader(ZooConstant.USER_AGENT), appId, ZooConstant.CATEGORY_APP, true);
//            }
//        }
//        if(isTest != null && isTest && resultModel != null ){
//            response.sendRedirect(resultModel.getUrl());
//        }
//        return ResponseInfoUtil.success(EncodeUtil.encode(JSON.toJSONString(getResultMap(resultModel))));
//    }
//
//    /**
//     * app 加密拉取加密返回
//     * @param request
//     * @param response
//     * @return
//     * @throws Exception
//
//    @CrossOrigin(origins = "*", maxAge = 3600)
//    @RequestMapping(value = {"/hH4cU/a8W"}, method = {RequestMethod.POST, RequestMethod.GET})
//    @ResponseBody
//     */
//    public ResponseInfo getConfigWithAes2(@RequestBody(required = false) JSONObject json,
//                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String value = json.getString("value");
//        if (StringUtils.isEmpty(value)) {
//            return ResponseInfoUtil.error();
//        }
//        JSONObject params = JSON.parseObject(EncodeUtil.decode(value));
//        String ipAddress = IpUtil.getIpAddr(request);
//        log.info("{} [PULL OFFER WITH EPM] [REQUEST BODY:{}]", LogConstant.ZOO, json.toJSONString());
//        String appId = (String)params.get("appId");
//        String deviceId = (String)params.get("deviceId");
//        String mnc = (String)params.get("mnc");
//        Boolean isLog = params.get("log") != null ? (Boolean) params.get("log") : false;
//        Boolean isTest = params.get("test") != null ? (Boolean) params.get("test") : false;
//
//        //所有跑过的 offer 的主键
//        JSONArray jsonArray = params.getJSONArray("used");
//        List<String> usedList = jsonArray != null ? JSONArray.parseArray(jsonArray.toJSONString(), String.class) : null;
//        String operator = Constant.OPERATORMAP.get(mnc);
//        if(StringUtils.isEmpty(operator)){
//            return ResponseInfoUtil.error();
//        }
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
//            resultModel = getOfferService.getEnableOfferModel(true, deviceId, ZooConstant.CATEGORY_APP, appId, ipAddress, operator, usedList, isLog, false);
//            if(resultModel != null && resultModel.getCallbackType() != ZooConstant.CALLBACK_TYPE_2){
//                //把配置中的链接根据配置情况替换完成
//                String clickId = affService.getClickId(appId, resultModel.getOfferId());
//                if (resultModel.getPartner().equalsIgnoreCase(ZooConstant.IE)) {
//                    clickId = resultModel.getPartnerOfferId() + UUID.randomUUID().toString();
//                }
//                String url = formatUrl(appId, clickId, resultModel);
//                resultModel.setUrl(url);
//                //统计点击信息
//                affService.saveClickInfo(ipAddress, deviceId, clickId, resultModel, request.getHeader(ZooConstant.USER_AGENT), appId, ZooConstant.CATEGORY_APP, false);
//            }
//        }
//        if(isTest != null && isTest && resultModel != null ){
//            return ResponseInfoUtil.success(getResultMap(resultModel));
//        }
//        return ResponseInfoUtil.success(EncodeUtil.encode(JSON.toJSONString(getResultMap(resultModel))));
//    }
//
//
//    /**
//     * app 拉取加密返回 2
//     * @param params
//     * @param deviceId
//     * @param appId
//     * @param mnc
//     * @param isLog
//     * @param usedList
//     * @param isTest
//     * @param request
//     * @param response
//     * @return
//     * @throws Exception
//     */
//    @CrossOrigin(origins = "*", maxAge = 3600)
//    @RequestMapping(value = {"/api/ftc/cfg/epm-2/ec2", "/gof2-ec2"}, method = {RequestMethod.POST, RequestMethod.GET})
//    @ResponseBody
//    public ResponseInfo getConfigWithAesEncode2(@RequestBody(required = false) Map<String, Object> params, @RequestParam(required = false) String deviceId,
//                                         @RequestParam(required = false) String appId, @RequestParam(required = false) String mnc,
//                                         @RequestParam(required = false, value = "log") Boolean isLog, @RequestParam( name="used", required = false) List<String> usedList,
//                                         @RequestParam(required = false, value = "test") Boolean isTest,
//                                         HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String ipAddress = IpUtil.getIpAddr(request);
//        if(params != null && StringUtils.isEmpty(appId) && StringUtils.isEmpty(mnc) && isLog == null && usedList == null){
//            log.info("{} [PULL OFFER WITH EPM] [REQUEST BODY:{}]", LogConstant.ZOO, JSON.toJSONString(params));
//            appId = (String)params.get("appId");
//            deviceId = (String)params.get("deviceId");
//            mnc = (String)params.get("mnc");
//            isLog = params.get("log") != null ? (Boolean) params.get("log") : false;
//            isTest = params.get("test") != null ? (Boolean) params.get("test") : false;
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
//                String url = formatUrl(appId, clickId, resultModel);
//                resultModel.setUrl(url);
//                //统计点击信息
//                affService.saveClickInfo(ipAddress, deviceId, clickId, resultModel, request.getHeader(ZooConstant.USER_AGENT), appId, ZooConstant.CATEGORY_APP, true);
//            }
//        }
//        if(isTest != null && isTest && resultModel != null ){
//            response.sendRedirect(resultModel.getUrl());
//        }
//        return ResponseInfoUtil.success(StringEncodeUtil2.encodeServer(JSON.toJSONString(getResultMap(resultModel))));
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
//            result.put("time", System.currentTimeMillis());
//            result.put("offer", model);
//            // 找到关联的 js 脚本
//            List<ScriptModel> scriptModels = scriptService.findScriptByOfferId(model.getIdentification());
//            if (scriptModels != null && scriptModels.size() > 0) {
//                result.put("script", scriptModels);
//            }
//        }
//        return result;
//    }

    public static String formatUrl(String appId, String clickId, OfferModel offerModel) throws UnsupportedEncodingException {
        String url = "";
        if(offerModel != null && !StringUtils.isEmpty(offerModel.getUrl())){
            // replace appId(扩展透传参数)
            url = getReplaceUrl(offerModel.getUrl(), offerModel.getExtendParam(), appId);
            // replace clickId(点击参数)
            url = getReplaceUrl(url, offerModel.getClickIdParam(), clickId);
            // replace offerId(上游offer主键)
            url = getReplaceUrl(url, offerModel.getPartnerOfferIdParam(), offerModel.getPartnerOfferId());
            url = url.replace(ZooConstant.PARAM_CLICKID, clickId);
            url = encodeOthersValue(url);
        }
        return url;
    }

    public static String  getReplaceUrl(String url, String param, String value){
        Matcher matcher = PatternUtil.URL_PARAMS_PATTERN.matcher(url);
        //先替换透传再替换clickId 防止clickId参数与透传参数一样
        if(!StringUtils.isEmpty(param)) {
            if (url.contains(param + ZooConstant.EQUAL_MARK)) {
                while (matcher.find()) {
                    if (matcher.group(2).equals(param)) {
                        //替换 appId
                        url = url.replace(matcher.group(2) + ZooConstant.EQUAL_MARK + matcher.group(3), matcher.group(2) + ZooConstant.EQUAL_MARK + value);
                    }
                }
            }else {
                url += url.contains(ZooConstant.INTERROGATION_MARK) ? "" : ZooConstant.INTERROGATION_MARK;
                url += ZooConstant.AND_MARK + param + ZooConstant.EQUAL_MARK + value;
            }
        }
        return url;
    }

    public static String encodeOthersValue(String url) throws UnsupportedEncodingException {
        Matcher matcher = PatternUtil.URL_PARAMS_PATTERN.matcher(url);
        while (matcher.find()) {
            url = url.replace(matcher.group(2) + ZooConstant.EQUAL_MARK + matcher.group(3), matcher.group(2) + ZooConstant.EQUAL_MARK + URLEncoder.encode(matcher.group(3), "UTF-8"));
        }
        return url;
    }

//    /**
//     * callbackType == 2 的click信息回调接口
//     * @param appId
//     * @param offerId
//     * @param clickId
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = {"/api/offer/clk/back", "/tyt/bck"}, method = {RequestMethod.GET, RequestMethod.POST})
//    @ResponseBody
//    public ResponseInfo callbackClickInfo(@RequestParam String appId, @RequestParam String offerId,
//                                          @RequestParam String clickId, HttpServletRequest request){
//        String ipAddress = IpUtil.getIpAddr(request);
//        affService.saveClickInfo(ipAddress, clickId, offerId, request.getHeader(ZooConstant.USER_AGENT), appId);
//        return ResponseInfoUtil.success();
//    }
}
