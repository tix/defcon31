package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.AexErrorCodeEnum;
import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.common.constant.ProtocolCrackConstant;
import com.starp.zoo.constant.*;
import com.starp.zoo.controller.Methods;
import com.starp.zoo.entity.zoo.*;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.repo.zoo.UserMobileInfoRepo;
import com.starp.zoo.service.IAndroidMncService;
import com.starp.zoo.service.ICrackStepService;
import com.starp.zoo.service.IScriptService;
import com.starp.zoo.service.IUserAgentService;
import com.starp.zoo.util.JsonUtil;
import com.starp.zoo.util.PatternUtil;
import com.starp.zoo.vo.AnalysisBodyVO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author charles
 */
@Slf4j
@Service
public class CrackStepServiceImpl implements ICrackStepService {

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster2RedisTemplate")
    private StringRedisTemplate cluster2RedisTemplate;


    @Autowired
    private IScriptService scriptService;

    @Value("${resttemplate.url.jskiller}")
    String jskillerUrl;

    @Value("${resttemplate.url.jskillerZaMtn}")
    String jskillerZaMtnUrl;

    @Value("${resttemplate.url.jskillerZaMtnOpticks}")
    String jskillerZaMtnOpticksUrl;

    @Value("${resttemplate.url.jskillerZaVodacom}")
    String jskillerVodacomUrl;

    @Value("${jskiller.optickssecurity}")
    String optickssecurity;

    @Autowired
    private UserMobileInfoRepo userMobileInfoRepo;

    @Autowired
    private IUserAgentService userAgentService;

    @Autowired
    private ICrackStepService crackStepService;

    @Autowired
    private IAndroidMncService mncService;

    @Timed
    @Override
    public Map<String,Object> analysisBody(HttpServletRequest request, String url, String body, String pin, String pid, String responseHeader, String requestHeader,
                                           String operator, String appId, String offerId, String userId, String isDebug, String deviceId,Long timeStamp,String cookie) throws Exception {
        Map<String,Object> analysisList = new HashMap<>(1);
        //先从redis 获取配置
        String key = Constants.PROTC_OFFER_STEP + Constants.COLON + offerId;
        try {
            Map<Object, Object> map = crackStepService.findStepMap(key);
            if (map == null || map.size() == 0) {
                analysisList.put(ZooConstant.ERROR_CODE, AexErrorCodeEnum.OFFER_STEP_NULL.getCode());
                analysisList.put(ZooConstant.ERROR_MESSAGE,offerId + AexErrorCodeEnum.OFFER_STEP_NULL.getMsg());
                return analysisList;
            }
            // map 中 hkey 为 url regex
            boolean matchUrl = false;
            for(Map.Entry entry : map.entrySet()) {
                boolean isMatch = checkMatch(url, (String) entry.getKey());
                if (isMatch) {
                    matchUrl = true;
                    OfferStepModel offerStepModel = JSON.parseObject((String) entry.getValue(), OfferStepModel.class);
                    //执行破解
                    analysisList = crackStepService.handleCrack(request, url, body, pin, pid, responseHeader, requestHeader, operator, appId,
                            offerId, userId, offerStepModel, isDebug, deviceId,null,timeStamp,cookie);
                    analysisList.put(TruemoveAocJs.IS_REDIRECT, offerStepModel.getType());
                    analysisList.put("stepIndex",offerStepModel.getStepIndex());
                    analysisList.put("stepName",offerStepModel.getStepName());
                }
            }
            if(!matchUrl){
                analysisList.put(ZooConstant.ERROR_CODE, AexErrorCodeEnum.OFFER_URL_NOT_MATCH.getCode());
                analysisList.put(ZooConstant.ERROR_MESSAGE,url + AexErrorCodeEnum.OFFER_URL_NOT_MATCH.getMsg());
            }
        } catch (Exception e) {
            log.info("analysisBody error:", e);
        }
        return analysisList;
    }

    /**
     * 破解
     * @param url
     * @param body
     * @param pin
     * @param pid
     * @param responseHeader
     * @param requestHeader
     * @param operator
     * @param appId
     * @param offerId
     * @param userId
     * @param offerStepModel
     * @param deviceId
     * @return
     */
    @Override
    @Timed
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    public Map<String,Object> handleCrack(HttpServletRequest request, String url, String body, String pin, String pid, String responseHeader, String requestHeader, String operator, String appId, String offerId,
                                          String userId, OfferStepModel offerStepModel, String isDebug, String deviceId,List<JSONObject> paramArray,Long timeStamp,String cookie) throws Exception {
        Map<String,Object> analysisList = new HashMap<>(1);
        List<AnalysisBodyVO> analysisBodyVos = new ArrayList<>();
        JSONObject resultJson = new JSONObject();
        if (Constants.TYPE_REDIRECT == offerStepModel.getType()) {
            // 解析 url
            resultJson = crackStepService.getRedirect(request, pid, url, body, responseHeader, requestHeader, operator, offerStepModel,isDebug,userId,paramArray);
        }
        if (Constants.TYPE_HTML == offerStepModel.getType() || Constants.TYPE_XHR == offerStepModel.getType() ) {
            // 执行 html 破解
            resultJson = crackStepService.getHtml(request, url, body, pin, pid, responseHeader, requestHeader, operator, appId, offerId, offerStepModel,isDebug,deviceId,userId,paramArray,timeStamp,cookie);
        }
        if (Constants.TYPE_SUCCESS == offerStepModel.getType()) {
            // 解析 url
             resultJson = crackStepService.getSuccess(url, body, pin, pid, responseHeader, requestHeader, operator, appId, offerId, userId);
        }
        if(Constants.TYPE_OPTICKSSECURITY == offerStepModel.getType()){
             resultJson = crackStepService.optickssecurity(url, body, responseHeader, requestHeader, offerStepModel, pid,userId);
        }
        analysisList = crackStepService.formatReturnResult(analysisBodyVos,resultJson,analysisList);
        return analysisList;
    }

    /**
     * 处理 html 流程
     * @param url
     * @param responseBody
     * @param responseHeader
     * @param requestHeader
     * @param offerStepModel
     * @param pid
     *
     * @return
     */
    @Timed
    @Override
    public JSONObject optickssecurity(String url, String responseBody, String responseHeader, String requestHeader, OfferStepModel offerStepModel, String pid,String userId) throws Exception {
        JSONObject returnJson = new JSONObject();
        List<HttpHtmlModel> byReferStepIdList = getHttpHtmlModel(pid + Constants.COLON + offerStepModel.getRefererStep());
        JSONObject header = userAgentService.getGlobalHeaders(null, false,userId);
        if(byReferStepIdList != null && byReferStepIdList.size()>0){
            if(byReferStepIdList.get(0) != null){
                HttpHtmlModel httpHtmlModel1 = byReferStepIdList.get(0);
                header = userAgentService.getGlobalHeaders(httpHtmlModel1.getUrl(), false,userId);
            }
        }
        AnalysisBodyVO analysisBodyVO = new AnalysisBodyVO();
        analysisBodyVO.setType(offerStepModel.getCrackType());
        analysisBodyVO.setStayTime(offerStepModel.getStayTime() != null ? offerStepModel.getStayTime() : 0);
        analysisBodyVO.setStepTime(offerStepModel.getStepTime() != null ? offerStepModel.getStepTime() : 0);
        analysisBodyVO.setMethod(offerStepModel.getMethod());
        if(offerStepModel.getSaveCookie() == null){
            analysisBodyVO.setSaveCookie(1);
        }else {
            analysisBodyVO.setSaveCookie(offerStepModel.getSaveCookie());
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        Base64.Encoder encoder = Base64.getEncoder();
        map.add("country", offerStepModel.getCountry());
        map.add("url", encoder.encodeToString(url.getBytes("UTF-8")));
        if(!StringUtils.isEmpty(requestHeader)){
            map.add("requestHeader", encoder.encodeToString(requestHeader.getBytes("UTF-8")));
        }
        if(!StringUtils.isEmpty(responseHeader)){
            map.add("responseHeader", encoder.encodeToString(responseHeader.getBytes("UTF-8")));
        }
        map.add("html", encoder.encodeToString(responseBody.getBytes("UTF-8")));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity( optickssecurity, request , String.class);
        JSONObject jskillerRes = JSONObject.parseObject(response.getBody());
        analysisBodyVO.setUrl(jskillerRes.getString("nextUrl"));
        syncJsHeader(jskillerRes, header);
        analysisBodyVO.setHs(header.toJSONString());
        returnJson.put("analysisBodyVo",JSON.toJSONString(analysisBodyVO));
        return returnJson;
    }

    @Timed
    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    @Override
    public Map<String, Object> formatReturnResult(List<AnalysisBodyVO> analysisBodyVos, JSONObject resultJson, Map<String,Object> analysisList) {
        //如果执行未报错
        if(StringUtils.isEmpty(resultJson.getString(ZooConstant.ERROR_CODE))){
            // 如果执行JS 结果不为空
            String  analysisBodyVoJson = resultJson.getString(ZooConstant.ANALYSISBODY_VO);
            AnalysisBodyVO analysisBodyVo = new AnalysisBodyVO();
            if(!StringUtils.isEmpty(analysisBodyVoJson)){
                Object analysisVoType = JSON.parse(analysisBodyVoJson);
                if(analysisVoType instanceof JSONObject){
                    analysisBodyVo = JSON.parseObject(analysisBodyVoJson,AnalysisBodyVO.class);
                    analysisBodyVos.add(analysisBodyVo);
                }else if(analysisVoType instanceof JSONArray){
                    analysisBodyVos = JSONArray.parseArray(analysisBodyVoJson,AnalysisBodyVO.class);
                }
                analysisList.put("analysisList",analysisBodyVos);
            }
        }else {
            analysisList.put("errorCode",resultJson.getInteger(ZooConstant.ERROR_CODE));
            analysisList.put("errorMessage",resultJson.getString(ZooConstant.ERROR_MESSAGE));
        }
        return analysisList;
    }

    /**
     * 处理成功页面, app内写转化
     * @param url
     * @param body
     * @param pin
     * @param pid
     * @param responseHeader
     * @param requestHeader
     * @param operator
     * @param appId
     * @param offerId
     * @param userId
     * @return
     */
    @Timed
    @Override
    public JSONObject getSuccess(String url, String body, String pin, String pid, String responseHeader, String requestHeader,
                                      String operator, String appId, String offerId, String userId) {
        JSONObject resultJson = new JSONObject();
        String realUrl = "http://api.analysis-portpull.com/api/offer/trans";
        JSONObject json = new JSONObject();
        json.put("appId", appId);
        json.put("offerId", offerId);
        ApplicationModel applicationModel = applicationRepo.findById(appId).get();
        json.put("packageName", applicationModel.getPackageName());
        json.put("userId",  userId);
        json.put("status", 1);
        AnalysisBodyVO analysisBodyVO = new AnalysisBodyVO();
        analysisBodyVO.setUrl(realUrl);
        analysisBodyVO.setFormData(json.toJSONString());
        JSONObject header = userAgentService.getGlobalHeaders(url, false,userId);
        header.put("Content-Type", "application/json");
        analysisBodyVO.setHs(header.toJSONString());
        resultJson.put("analysisBodyVo",JSON.toJSONString(analysisBodyVO));
        return resultJson;
    }

    /**
     * 处理 html 流程
     * @param url
     * @param responseBody
     * @param responseHeader
     * @param requestHeader
     * @param offerStepModel
     * @param deviceId
     * @return
     */
    @Timed
    @Override
    public JSONObject getHtml(HttpServletRequest request, String url, String responseBody, String pin, String pid, String responseHeader, String requestHeader,
                              String operator, String appId, String offerId, OfferStepModel offerStepModel, String isDebug, String deviceId,String userId,List<JSONObject> paramArray,Long timeStamp,String cookie) throws Exception {
        JSONObject returnJson;
        // 如果是某个页面的异步流程
        if (!StringUtils.isEmpty(offerStepModel.getOriginPage()) && offerStepModel.getType() == Constants.TYPE_XHR) {
             returnJson  = crackStepService.handleHtmlXhr(request, url, responseBody, pin, pid, responseHeader, requestHeader, operator, appId, offerId, offerStepModel,isDebug,deviceId,userId,paramArray);
        } else {
            // 如果包含 html 则解析页面
             returnJson = crackStepService.handleHtmlDocumentPage(request, url, responseBody, pin, pid, responseHeader, requestHeader, operator, appId, offerId, offerStepModel,isDebug,deviceId,userId,paramArray,timeStamp,cookie);
        }
        return returnJson;
    }

    private boolean checkDom(String responseBody) {
        if (StringUtils.isEmpty(responseBody)) {
            return false;
        }
        Document document = Jsoup.parse(responseBody);
        return document != null;
    }

    /**
     * 处理 html 异步步骤
     * @param url
     * @param responseBody
     * @param pin
     * @param pid
     * @param responseHeader
     * @param requestHeader
     * @param offerStepModel
     * @param deviceId
     * @return
     * @throws Exception
     */
    @Timed
    @Override
    public JSONObject handleHtmlXhr(HttpServletRequest request, String url, String responseBody, String pin, String pid, String responseHeader, String requestHeader, String operator,
                                    String appId, String offerId, OfferStepModel offerStepModel, String isDebug, String deviceId,String userId,List<JSONObject> paramArray){
        JSONObject returnJson = new JSONObject();
        AnalysisBodyVO analysisBodyVO = new AnalysisBodyVO();
        JSONObject params = new JSONObject();
        HttpHtmlModel httpHtmlModel = generateHtmlModel(pid,offerStepModel,responseBody,url,responseHeader,operator);
        List<HttpHtmlModel> httpHtmlModelList = new ArrayList<>();
        if(paramArray != null && paramArray.size()>0){
            httpHtmlModelList = generateHtmlModelList(pid,offerStepModel,paramArray,operator);
        }
        //获取需要读取的step
        List<HttpHtmlModel> byIdList = getHttpHtmlModel(pid + Constants.COLON + offerStepModel.getOriginPage());
        if(byIdList != null && byIdList.size() >0){
            HttpHtmlModel byId = byIdList.get(0);
            if(byId != null){
                params.put("page", byId);
            }
        }
        List<HttpHtmlModel> byId2List = getHttpHtmlModel(pid + Constants.COLON + offerStepModel.getOriginPage2());
        if(byId2List != null && byId2List.size() >0){
            HttpHtmlModel byId2 = byId2List.get(0);
            if(byId2 != null){
                params.put("page2", byId2);
            }
        }
        //查找referstep
        List<HttpHtmlModel> byReferStepIdList = getHttpHtmlModel(pid + Constants.COLON + offerStepModel.getRefererStep());
        JSONObject header = userAgentService.getGlobalHeaders(null, false,userId);
        if(byReferStepIdList != null && byReferStepIdList.size() >0){
            HttpHtmlModel byReferStepId = byReferStepIdList.get(0);
            if(byReferStepId != null){
                HttpHtmlModel httpHtmlModel1 = byReferStepId;
                header = userAgentService.getGlobalHeaders(httpHtmlModel1.getUrl(), false,userId);
            }
        }
        if (StringUtils.isEmpty(offerStepModel.getScript())) {
            returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.OFFER_STEP_SCRIPT_NULL.getCode());
            returnJson.put(ZooConstant.ERROR_MESSAGE,offerStepModel.getIdentification() + AexErrorCodeEnum.OFFER_STEP_SCRIPT_NULL.getMsg());
            return returnJson;
        }
        params = crackStepService.generateParams(url,responseHeader,requestHeader,appId,offerId,pin,responseBody,params,offerStepModel,deviceId);
        JSONObject bodyJson;
        if(paramArray != null && paramArray.size() >0){
            List<JSONObject> paramList = generateParamsList(paramArray,offerStepModel,deviceId);
            bodyJson = handleJs(request, params, offerStepModel.getScript(),offerStepModel,isDebug,paramList);
        } else {
            bodyJson = handleJs(request, params, offerStepModel.getScript(),offerStepModel,isDebug,null);
        }
        // 执行JS 未出错
        if(StringUtils.isEmpty(bodyJson.getString(ZooConstant.ERROR_CODE))){
            String jsBack = bodyJson.getString("handleResult");
            Object jsBackReuslt = JSON.parse(jsBack);
            if(jsBackReuslt instanceof  JSONObject){
                returnJson = handleHtmlXhrJsonResult(jsBack,offerStepModel,operator,header,pin,httpHtmlModel,analysisBodyVO,returnJson);
            }else if(jsBackReuslt instanceof JSONArray){
                returnJson = handleHtmlXhrArrayResult(jsBack,offerStepModel,operator,header,pin,httpHtmlModelList,analysisBodyVO,returnJson);
            }
        }else {
            returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_ERROR.getCode());
            if(!StringUtils.isEmpty(isDebug) && isDebug.equalsIgnoreCase(ZooConstant.HANDLE_DEBUG_JS)){
                returnJson.put(ZooConstant.ERROR_MESSAGE,bodyJson.getString(ZooConstant.ERROR_MESSAGE));
            }else {
                returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_ERROR.getMsg());
            }
        }
        return returnJson;
    }

    private List<HttpHtmlModel> generateHtmlModelList(String pid, OfferStepModel offerStepModel, List<JSONObject> paramArray, String operator) {
        List<HttpHtmlModel> httpHtmlModelList = new ArrayList<>();
        if(paramArray != null && paramArray.size() >0){
            for(JSONObject param : paramArray){
                HttpHtmlModel httpHtmlModel = new HttpHtmlModel();
                httpHtmlModel.setIdentification(pid + Constants.COLON + offerStepModel.getIdentification());
                httpHtmlModel.setHtml(param.getString("body"));
                httpHtmlModel.setUrl(param.getString("path"));
                httpHtmlModel.setHeader(param.getString("sh"));
                httpHtmlModel.setOperator(operator);
                httpHtmlModel.setStepName(offerStepModel.getStepName());
                httpHtmlModelList.add(httpHtmlModel);
            }
        }
        return httpHtmlModelList;
    }

    private JSONObject handleHtmlXhrArrayResult(String jsBack, OfferStepModel offerStepModel, String operator, JSONObject header, String pin, List<HttpHtmlModel> httpHtmlModelList, AnalysisBodyVO analysisBodyVO, JSONObject returnJson) {
        JSONArray resultList = JSONArray.parseArray(jsBack);
        List<HttpHtmlModel> saveHtmlList = new ArrayList<>();
        List<AnalysisBodyVO> analysisBodyVOList = new ArrayList<>();
        for(Object objresult : resultList){
            JSONObject bdHandleResult = JSON.parseObject(objresult.toString());
            if(bdHandleResult!=null){
                JSONObject resultJson = bdHandleResult.getJSONObject("result");
                String realUrl = resultJson.getString("url");
                String formDataStr = resultJson.getString("form");
                boolean existForm = resultJson.containsKey("form");
                String method = StringUtils.isEmpty(resultJson.getString("method")) ? offerStepModel.getMethod() : resultJson.getString("method");
                String pinRegx = StringUtils.isEmpty(resultJson.getString("pinRegx")) ? offerStepModel.getPinRegex() : resultJson.getString("pinRegx");
                String contentType = resultJson.getString("Content-Type");
                String aio = resultJson.getString("aio");
                String intervals = resultJson.getString("intervals");
                String ttl = resultJson.getString("ttl");
                String saveCookie = resultJson.getString("saveCookie");
                // 设置 Content-type 默认发送 json
                if(AlexConstant.ZA_MTN.equalsIgnoreCase(operator)){
                    if(!StringUtils.isEmpty(contentType)){
                        header.put("Content-Type", contentType);
                    }
                }else {
                    contentType = StringUtils.isEmpty(contentType) ? "text/plain" : contentType;
                    header.put("Content-Type", contentType);
                }

                // 是否保存页面(不是pin码返回)
                if(offerStepModel.isSave() && StringUtils.isEmpty(pin)) {
                    HttpHtmlModel localHtml =httpHtmlModelList.get(resultList.indexOf(objresult));
                    if(!StringUtils.isEmpty(localHtml.getHtml()) && !localHtml.getHtml().contains(AlexConstant.CAPTCHA_NOT_READY)){
                        saveHtmlList.add(localHtml);
                    }
                }
                syncJsHeader(resultJson, header);
                analysisBodyVO = setAnalysisBodyVoParam(analysisBodyVO,method,realUrl,offerStepModel,header,formDataStr,pinRegx,aio,intervals,ttl,existForm);
                //判断是否保存cookie,以JS中为准
                if(!StringUtils.isEmpty(saveCookie)){
                    analysisBodyVO.setSaveCookie(Integer.valueOf(saveCookie));
                }else {
                    if(offerStepModel.getSaveCookie() == null){
                        analysisBodyVO.setSaveCookie(1);
                    }else {
                        analysisBodyVO.setSaveCookie(offerStepModel.getSaveCookie());
                    }
                }
                analysisBodyVOList.add(analysisBodyVO);
            }else {
                returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getCode());
                returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getMsg());
            }
        }
        // 保存修改后的页面数据
        saveResultPageArray(resultList, null);
        if(saveHtmlList != null && saveHtmlList.size() >0){
            saveHttpHtmlListRedis(saveHtmlList);
        }
        if(analysisBodyVOList != null && analysisBodyVOList.size() >0){
            returnJson.put("analysisBodyVo",JSON.toJSONString(analysisBodyVOList));
        }
        return returnJson;
    }

    /**
     * 保存HTML 数组
     * @param resultList
     * @param originHtml
     */
    private void saveResultPageArray(JSONArray resultList, HttpHtmlModel originHtml) {
        //保存修改后的页面数据
        if(resultList != null && resultList.size() >0){
            List<HttpHtmlModel> httpHtmlModelList = new ArrayList<>();
            for(Object obj : resultList){
                JSONObject bdHandleResult = JSON.parseObject(obj.toString());
                if (bdHandleResult.containsKey(AlexConstant.PAGE)){
                    String page1 = bdHandleResult.getString(AlexConstant.PAGE);
                    if(JsonUtil.isJSONValid(page1)){
                        JSONObject page = JSON.parseObject(page1);
                        if (page != null) {
                            HttpHtmlModel httpHtmlModel  = JSONObject.parseObject(page.toJSONString(), HttpHtmlModel.class);
                            httpHtmlModelList.add(httpHtmlModel);
                        }
                    }
                }
            }
            if(httpHtmlModelList != null && httpHtmlModelList.size() >0){
                saveHttpHtmlListRedis(httpHtmlModelList);
            }
        }
    }

    private void saveHttpHtmlListRedis(List<HttpHtmlModel> saveHtmlList) {
        HttpHtmlModel firstHtml = saveHtmlList.get(0);
        String httpHtmlKey = ZooConstant.HTML_INFO + CacheNameSpace.COLON +  firstHtml.getIdentification();
        stringRedisTemplate.opsForValue().set(httpHtmlKey,JSON.toJSONString(saveHtmlList));
        stringRedisTemplate.expire(httpHtmlKey,3, TimeUnit.MINUTES);
    }

    private JSONObject handleHtmlXhrJsonResult(String jsBack, OfferStepModel offerStepModel, String operator, JSONObject header, String pin, HttpHtmlModel httpHtmlModel, AnalysisBodyVO analysisBodyVO, JSONObject returnJson) {
        JSONObject bdHandleResult  = JSON.parseObject(jsBack);
        if(bdHandleResult!=null){
            JSONObject resultJson = bdHandleResult.getJSONObject("result");
            String realUrl = resultJson.getString("url");
            String formDataStr = resultJson.getString("form");
            boolean existForm = resultJson.containsKey("form");
            String method = StringUtils.isEmpty(resultJson.getString("method")) ? offerStepModel.getMethod() : resultJson.getString("method");
            String pinRegx = StringUtils.isEmpty(resultJson.getString("pinRegx")) ? offerStepModel.getPinRegex() : resultJson.getString("pinRegx");
            String contentType = resultJson.getString("Content-Type");
            String aio = resultJson.getString("aio");
            String intervals = resultJson.getString("intervals");
            String ttl = resultJson.getString("ttl");
            String saveCookie = resultJson.getString("saveCookie");
            // 设置 Content-type 默认发送 json
            if(AlexConstant.ZA_MTN.equalsIgnoreCase(operator)){
                if(!StringUtils.isEmpty(contentType)){
                    header.put("Content-Type", contentType);
                }
            }else {
                contentType = StringUtils.isEmpty(contentType) ? "text/plain" : contentType;
                header.put("Content-Type", contentType);
            }
            // 保存修改后的页面数据
            saveResultPage(bdHandleResult, null);
            // 是否保存页面(不是pin码返回)
            if(offerStepModel.isSave() && StringUtils.isEmpty(pin)) {
                if(!StringUtils.isEmpty(httpHtmlModel.getHtml()) && !httpHtmlModel.getHtml().contains(AlexConstant.CAPTCHA_NOT_READY)){
                    saveHttpHtmlModelRedis(httpHtmlModel);
                }
            }
            syncJsHeader(resultJson, header);
            analysisBodyVO = setAnalysisBodyVoParam(analysisBodyVO,method,realUrl,offerStepModel,header,formDataStr,pinRegx,aio,intervals,ttl, existForm);
            //判断是否保存cookie,以JS中为准
            if(!StringUtils.isEmpty(saveCookie)){
                analysisBodyVO.setSaveCookie(Integer.valueOf(saveCookie));
            }else {
                if(offerStepModel.getSaveCookie() == null){
                    analysisBodyVO.setSaveCookie(1);
                }else {
                    analysisBodyVO.setSaveCookie(offerStepModel.getSaveCookie());
                }
            }
            returnJson.put("analysisBodyVo",JSON.toJSONString(analysisBodyVO));
        }else {
            returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getCode());
            returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getMsg());
        }
        return returnJson;
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    private List<JSONObject> generateParamsList(List<JSONObject> paramArray, OfferStepModel offerStepModel, String deviceId) {
        List<JSONObject> paramList = new ArrayList<>();
        for(JSONObject param : paramArray){
            JSONObject paramObj = new JSONObject();
            paramObj.put("url", param.getString("path"));
            paramObj.put("header", param.getString("sh"));
            paramObj.put("rheader", param.getString("rh"));
            paramObj.put("appId", param.getString("ap"));
            paramObj.put("offerId", param.getString("of"));
            paramObj.put("pin", param.getString("upc"));
            paramObj.put("body", param.getString("body"));
            String aio = param.getString("aio");
            boolean isMatchParam = !StringUtils.isEmpty(aio) && aio.length() == 4 && Integer.valueOf(aio.substring(3)) == NumberEnum.ONE.getNum();
            if(isMatchParam){
                boolean checkMsisdn = offerStepModel !=null && offerStepModel.getGetMsisdn() != null && offerStepModel.getGetMsisdn() == 1;
                if(checkMsisdn){
                    UserMobileInfoModel userMobileInfoModel = null;
                    if (!StringUtils.isEmpty(deviceId)) {
                        String subDeviceId = deviceId;
                        if(deviceId.length() >= NumberEnum.EIGHT_TEEN.getNum()){
                            subDeviceId = deviceId.substring(0,15);
                        }
                        userMobileInfoModel = userMobileInfoRepo.findFirstByDeviceIdOrderByCreateTimeDesc(subDeviceId);
                        if(userMobileInfoModel != null){
                            paramObj.put("msisdn",userMobileInfoModel.getMobile());
                        }
                    }
                }
            }
           paramList.add(paramObj);
        }
        return paramList;
    }

    @Timed
    @Override
    public JSONObject generateParams(String url, String responseHeader, String requestHeader, String appId, String offerId, String pin, String responseBody, JSONObject params, OfferStepModel offerStepModel, String deviceId) {
        params.put("url", url);
        params.put("header", responseHeader);
        params.put("rheader", requestHeader);
        params.put("appId", appId);
        params.put("offerId", offerId);
        params.put("pin", pin);
        params.put("body", responseBody);
        boolean checkMsisdn = offerStepModel !=null && offerStepModel.getGetMsisdn() != null && offerStepModel.getGetMsisdn() == 1;
        if(checkMsisdn){
            UserMobileInfoModel userMobileInfoModel = null;
            if (!StringUtils.isEmpty(deviceId)) {
                String subDeviceId = deviceId;
                if(deviceId.length() >= NumberEnum.EIGHT_TEEN.getNum()){
                    subDeviceId = deviceId.substring(0,15);
                }
                userMobileInfoModel = userMobileInfoRepo.findFirstByDeviceIdOrderByCreateTimeDesc(subDeviceId);
                if(userMobileInfoModel != null){
                    params.put("msisdn",userMobileInfoModel.getMobile());
                }
            }
        }
        return params;
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    @Override
    public Map<String, Object> analysisBodyParamArray(HttpServletRequest request, List<JSONObject> bodyJsonArray) {
        Map<String,Object> analysisList = new HashMap<>(0);
        for(JSONObject param : bodyJsonArray){
            String aio = param.getString("aio");
            boolean isMatchParam = !StringUtils.isEmpty(aio) && aio.length() == 4 && Integer.valueOf(aio.substring(3)) == NumberEnum.ONE.getNum();
            if(isMatchParam){
                //先从redis 获取配置
                String offerId = param.getString("of");
                String key = Constants.PROTC_OFFER_STEP + Constants.COLON + offerId;
                try {
                    Map<Object, Object> map = cluster2RedisTemplate.opsForHash().entries(key);
                    if (map == null || map.size() == 0) {
                        analysisList.put(ZooConstant.ERROR_CODE, AexErrorCodeEnum.OFFER_STEP_NULL.getCode());
                        analysisList.put(ZooConstant.ERROR_MESSAGE,offerId + AexErrorCodeEnum.OFFER_STEP_NULL.getMsg());
                        return analysisList;
                    }
                    // map 中 hkey 为 url regex
                    boolean matchUrl = false;
                    String url = param.getString("path");
                    for(Map.Entry entry : map.entrySet()) {
                        boolean isMatch = checkMatch(url, (String) entry.getKey());
                        if (isMatch) {
                            matchUrl = true;
                            OfferStepModel offerStepModel = JSON.parseObject((String) entry.getValue(), OfferStepModel.class);
                            //执行破解
                            String appId = param.getString("ap");
                            String isDebug = param.getString("debug");
                            String deviceId = param.getString("deviceId");
                            Long nodejsTimestamp = param.getLong("timeStamp");
                            String operator = mncService.generateOp(param.getString("sio"));
                            String cookie = param.getString("cookie");
                            analysisList = crackStepService.handleCrack(request, url, param.getString("body"), param.getString("upc"),
                                    param.getString("pid"), param.getString("sh"), param.getString("rh"), operator, appId,
                                    offerId, param.getString("userId"), offerStepModel, isDebug, deviceId,bodyJsonArray,nodejsTimestamp,cookie);
                            analysisList.put(TruemoveAocJs.IS_REDIRECT, offerStepModel.getType());
                            analysisList.put("stepIndex",offerStepModel.getStepIndex());
                            analysisList.put("stepName",offerStepModel.getStepName());
                        }
                    }
                    if(!matchUrl){
                        analysisList.put(ZooConstant.ERROR_CODE, AexErrorCodeEnum.OFFER_URL_NOT_MATCH.getCode());
                        analysisList.put(ZooConstant.ERROR_MESSAGE,url + AexErrorCodeEnum.OFFER_URL_NOT_MATCH.getMsg());
                    }
                } catch (Exception e) {
                    log.info("analysisBody error:", e);
                }
                break;
            }
        }
        return analysisList;
    }



    private AnalysisBodyVO setAnalysisBodyVoParam(AnalysisBodyVO analysisBodyVO, String method, String realUrl, OfferStepModel offerStepModel, JSONObject header, String formDataStr, String pinRegx, String aio, String intervals, String ttl, boolean existForm) {
        if (!StringUtils.isEmpty(formDataStr)) {
            analysisBodyVO.setFormData(formDataStr);
        }else if(existForm){
            analysisBodyVO.setFormData("");
        }
        analysisBodyVO.setType(offerStepModel.getCrackType());
        analysisBodyVO.setMethod(method);
        analysisBodyVO.setUrl(realUrl);
        analysisBodyVO.setNextRegex(offerStepModel.getNextRegex());
        analysisBodyVO.setHs(header.toJSONString());
        int stayTime = offerStepModel.getStayTime() != null ? (offerStepModel.getStayTime() < 1000 ? offerStepModel.getStayTime() * 1000 : offerStepModel.getStayTime()) : 0;
        int stepTime = offerStepModel.getStepTime() != null ? (offerStepModel.getStepTime() < 1000 ? offerStepModel.getStepTime() * 1000 : offerStepModel.getStepTime()) : 0;
        analysisBodyVO.setStayTime(stayTime);
        analysisBodyVO.setStepTime(stepTime);
        if(!StringUtils.isEmpty(pinRegx)){
            analysisBodyVO.setPinRegx(pinRegx);
        }
        //判断 是否需要发送pin码正则
        if (!StringUtils.isEmpty(offerStepModel.getPinRegex())  && offerStepModel.isPin()) {
            analysisBodyVO.setPinRegx(offerStepModel.getPinRegex());
        }
        if(!StringUtils.isEmpty(aio)){
            analysisBodyVO.setAio(Integer.valueOf(aio));
        }
        if(!StringUtils.isEmpty(intervals)){
            analysisBodyVO.setIntervals(Integer.valueOf(intervals));
        }
        if(!StringUtils.isEmpty(ttl)){
            analysisBodyVO.setTtl(Long.valueOf(ttl));
        }

        return analysisBodyVO;
    }

    private HttpHtmlModel generateHtmlModel(String pid, OfferStepModel offerStepModel, String responseBody, String url, String responseHeader, String operator) {
        HttpHtmlModel httpHtmlModel = new HttpHtmlModel();
        httpHtmlModel.setIdentification(pid + Constants.COLON + offerStepModel.getIdentification());
        httpHtmlModel.setHtml(responseBody);
        httpHtmlModel.setUrl(url);
        httpHtmlModel.setHeader(responseHeader);
        httpHtmlModel.setOperator(operator);
        httpHtmlModel.setStepName(offerStepModel.getStepName());
        return httpHtmlModel;
    }

    private void syncJsHeader(JSONObject resultJson, JSONObject header){
        header.put("User-Agent", "Mozilla/5.0 (Linux; Android 9; vivo 1902 Build/PPR1.180610.011; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/89.0.4389.90 Mobile Safari/537.36");
        header.put("Accept","text/html,text/plain,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        header.put("Accept-Language","en-US,en;q=0.9");

        JSONObject resultHeader = resultJson.getJSONObject("header");
        if(null!=resultHeader){
            Iterator<String> iterator = resultHeader.keySet().iterator();
            while (iterator.hasNext()){
                String next = iterator.next();
                header.put(next, resultHeader.getString(next));
            }
        }
    }

    /**
     * 处理 html 页面
     * @param url
     * @param responseBody
     * @param pin
     * @param pid
     * @param responseHeader
     * @param requestHeader
     * @param offerStepModel
     * @param deviceId
     * @param userId
     * @return
     * @throws Exception
     */
    @SuppressFBWarnings("NP_LOAD_OF_KNOWN_NULL_VALUE")
    @SuppressWarnings("PMD")
    @Timed
    @Override
    public JSONObject handleHtmlDocumentPage(HttpServletRequest request, String url, String responseBody, String pin, String pid, String responseHeader, String requestHeader, String operator, String appId, String offerId,
                                             OfferStepModel offerStepModel, String isDebug, String deviceId,String userId,List<JSONObject> paramArray,Long timeStamp,String cookie) {
        JSONObject returnJson = new JSONObject();
        JSONArray nodejsResult = new JSONArray();
        String htmlKey =ZooConstant.HTML_INFO + CacheNameSpace.COLON +  pid + Constants.COLON + offerStepModel.getRefererStep();
        Boolean existHtmlModel = cluster2RedisTemplate.hasKey(htmlKey);
        JSONObject header = userAgentService.getGlobalHeaders(null, false,userId);
        if(existHtmlModel != null && existHtmlModel){
            List<HttpHtmlModel> httpHtmlModelList = getHttpHtmlModel(htmlKey);
            if(httpHtmlModelList != null && httpHtmlModelList.size() >0){
                HttpHtmlModel httpHtmlModel1 = httpHtmlModelList.get(0);
                header = userAgentService.getGlobalHeaders(httpHtmlModel1.getUrl(), false,userId);
            }
        }
        String method = offerStepModel.getMethod();
        String pinRegx = offerStepModel.getPinRegex();
        AnalysisBodyVO analysisBodyVO = new AnalysisBodyVO();
        HttpHtmlModel httpHtmlModel = formatHttpHtmlModel(pid,offerStepModel,responseBody,url,responseHeader,operator);
        List<HttpHtmlModel> httpHtmlModelList = formatHttpHtmlModelList(offerStepModel,pid,operator,paramArray);
        if (!StringUtils.isEmpty(offerStepModel.getScript())) {
            JSONObject params = initJsParams(url, responseHeader, appId, offerId, pin, responseBody, offerStepModel, httpHtmlModel, deviceId);
            params.put("page", httpHtmlModel);
            //当一个页面被进入多次处理时，需要保存每一次处理中间参数
            String pageId = pid + Constants.COLON + offerStepModel.getIdentification();
            List<HttpHtmlModel> originHtmlList = getHttpHtmlModel(pageId);
            HttpHtmlModel originHtml = null;
            if(null != originHtmlList && originHtmlList.size() >0){
                originHtml = originHtmlList.get(0);
                params.put("page", originHtml);
            }
            try {
                if(null != originHtml){
                    truemoveAoc01(offerStepModel, responseBody, originHtml);
                    nodejsResult = zaJsKillerActive(requestHeader, offerStepModel, responseBody, httpHtmlModel, header,timeStamp,cookie,url,pid);
                }else {
                    truemoveAoc01(offerStepModel, responseBody, httpHtmlModel);
                    nodejsResult = zaJsKillerActive(requestHeader, offerStepModel, responseBody, httpHtmlModel, header,timeStamp,cookie,url,pid);
                }
                if(nodejsResult != null && nodejsResult.size() >0){
                    params.put("nodejsResult",nodejsResult.toJSONString());
                }
            }catch (Exception e){
                returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.SAVE_PAGE_HANDLE_NODEJS_ERROR.getCode());
                returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.SAVE_PAGE_HANDLE_NODEJS_ERROR.getMsg());
            }
            String jsBack = null;
            JSONObject bodyJson = null;
            if(paramArray != null && paramArray.size() >0){
                 List<JSONObject> paramList = genereateParamList(url,  appId, offerId, pin,  offerStepModel, httpHtmlModelList, deviceId,paramArray,nodejsResult);
                 bodyJson = handleJs(request, params, offerStepModel.getScript(),offerStepModel,isDebug,paramList);
            }else {
                bodyJson = handleJs(request, params, offerStepModel.getScript(),offerStepModel,isDebug,null);

            }
            if(StringUtils.isEmpty(bodyJson.getString(ZooConstant.ERROR_CODE))){
                jsBack = bodyJson.getString("handleResult");
                returnJson = handleHtmlPageJsonReuslt(jsBack,method,pinRegx,operator,header,originHtml,offerStepModel,pin,httpHtmlModel,analysisBodyVO,returnJson,httpHtmlModelList);
            }else {
                returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.SAVE_PAGE_HANDLE_JS_ERROR.getCode());
                if(!StringUtils.isEmpty(isDebug) && isDebug.equalsIgnoreCase(ZooConstant.HANDLE_DEBUG_JS)){
                    returnJson.put(ZooConstant.ERROR_MESSAGE,bodyJson.getString(ZooConstant.ERROR_MESSAGE));
                }else {
                    returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.SAVE_PAGE_HANDLE_JS_ERROR.getMsg());
                }
            }
        }else {
            returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.SAVE_PAGE_HANDLE_JS_RESPONSE_NULL.getCode());
            returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.SAVE_PAGE_HANDLE_JS_RESPONSE_NULL.getMsg());
        }
        return returnJson;
    }

    /**
     * 处理save page 数组结果
     * @param jsBack
     * @param method
     * @param pinRegx
     * @param operator
     * @param header
     * @param originHtml
     * @param offerStepModel
     * @param pin
     * @param httpHtmlModelList
     * @param returnJson
     * @return
     */
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    private JSONObject handleHtmlPageJsonReusltArray(Object jsBack, String method, String pinRegx, String operator, JSONObject header, HttpHtmlModel originHtml, OfferStepModel offerStepModel, String pin, List<HttpHtmlModel> httpHtmlModelList, JSONObject returnJson) {
        JSONArray bdHandleResultList = (JSONArray) jsBack;
        List<AnalysisBodyVO> analysisBodyVOList = new ArrayList<>();
        if(bdHandleResultList != null && bdHandleResultList.size() >0){
            for(Object obj : bdHandleResultList){
                JSONObject bdHandleResult = JSONObject.parseObject(obj.toString());
                if(bdHandleResult != null){
                    AnalysisBodyVO analysisBodyVO = new AnalysisBodyVO();
                    JSONObject resultJson = bdHandleResult;
                    if(resultJson != null){
                        String realUrl = resultJson.getString("url");
                        String formDataStr = resultJson.getString("form");
                        boolean existForm = resultJson.containsKey("form");
                        method = StringUtils.isEmpty(resultJson.getString("method")) ? method : resultJson.getString("method");
                        pinRegx = StringUtils.isEmpty(resultJson.getString("pinRegx")) ? pinRegx : resultJson.getString("pinRegx");
                        String contentType = resultJson.getString("Content-Type");
                        String aio = resultJson.getString("aio");
                        String intervals = resultJson.getString("intervals");
                        String ttl = resultJson.getString("ttl");
                        String saveCookie = resultJson.getString("saveCookie");
                        //vodacom保存cookie
                        String setCookie = resultJson.getString("setCookie");
                        if(AlexConstant.ZA_MTN.equalsIgnoreCase(operator)){
                            if(!StringUtils.isEmpty(contentType)){
                                header.put("Content-Type", contentType);
                            }
                        } else {
                            contentType = StringUtils.isEmpty(contentType) ? "text/plain" : contentType;
                            header.put("Content-Type", contentType);
                        }
                        syncJsHeader(resultJson, header);
                        saveResultPage(bdHandleResult, originHtml);

                        if (!StringUtils.isEmpty(formDataStr)) {
                            analysisBodyVO.setFormData(formDataStr);
                        }else if(existForm){
                            analysisBodyVO.setFormData("");
                        }
                        analysisBodyVO = setParamAnalysisBody(analysisBodyVO,offerStepModel,method,realUrl,header,aio,intervals,ttl,setCookie);
                        if(!StringUtils.isEmpty(pinRegx)){
                            analysisBodyVO.setPinRegx(pinRegx);
                        }
                        //判断是否保存cookie,以JS中为准
                        if(!StringUtils.isEmpty(saveCookie)){
                            analysisBodyVO.setSaveCookie(Integer.valueOf(saveCookie));
                        }else {
                            if(offerStepModel.getSaveCookie() == null){
                                analysisBodyVO.setSaveCookie(1);
                            }else {
                                analysisBodyVO.setSaveCookie(offerStepModel.getSaveCookie());
                            }
                        }
                        analysisBodyVOList.add(analysisBodyVO);
                    }else {
                        returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getCode());
                        returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getMsg());
                    }
                }else {
                    returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getCode());
                    returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getMsg());
                }
            }
            // 是否保存页面(不是pin码返回)
            if(offerStepModel.isSave() && StringUtils.isEmpty(pin)) {
                if(httpHtmlModelList != null && httpHtmlModelList.size() >0){
                    Boolean existHttpHtmlModel = cluster2RedisTemplate.hasKey(httpHtmlModelList.get(0).getIdentification());
                    if (!(existHttpHtmlModel != null && existHttpHtmlModel)) {
                        saveHttpHtmlListRedis(httpHtmlModelList);
                    }
                }
            }
        }else {
            returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getCode());
            returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getMsg());
        }
        if(analysisBodyVOList != null && analysisBodyVOList.size() >0){
            returnJson.put("analysisBodyVo",JSON.toJSONString(analysisBodyVOList));
        }
        return returnJson;
    }


    /**
     * 处理vodacom nodejs
     * @param requestHeader
     * @param offerStepModel
     * @param responseBody
     * @param originHtml
     * @param header
     * @return
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    private JSONArray zaJsKillerActive(String requestHeader, OfferStepModel offerStepModel, String responseBody, HttpHtmlModel originHtml, JSONObject header, Long timeStamp,String cookie,String url,String pid) {
        JSONArray nodeJsResult = new JSONArray();
        boolean checkNodeResult = StringUtils.isEmpty(offerStepModel.getStepName()) || (!offerStepModel.getStepName().startsWith(ZaConstant.VODACOM_NODEKS_PATTERN) && !offerStepModel.getStepName().startsWith(ZaConstant.MTN_NODEJS_PATTERN) && !offerStepModel.getStepName().startsWith(ZaConstant.MTN_OPTICKS_NODEJS_PATTERN));
        if (checkNodeResult) {
            return nodeJsResult;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(requestHeader);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, Object> map= new LinkedMultiValueMap<String, Object>();
            map.add("htmlStr", responseBody);
            String pageId = pid + Constants.COLON + offerStepModel.getRefererStep();
            String htmlKey =ZooConstant.HTML_INFO + CacheNameSpace.COLON +  pageId;
            if(jsonObject.containsKey(Constants.USER_AGENT_UPER)){
                map.add("userAgent", jsonObject.getString(Constants.USER_AGENT_UPER));
            }
            if(jsonObject.containsKey(Constants.USER_AGENT_LOWER)){
                map.add("userAgent", jsonObject.getString(Constants.USER_AGENT_LOWER));
            }
            if(jsonObject.containsKey(Constants.REFERER)){
                String referer = jsonObject.getString(Constants.REFERER);
                if(StringUtils.isEmpty(referer)){
                    Boolean existHtmlModel = stringRedisTemplate.hasKey(htmlKey);
                    if(existHtmlModel != null && existHtmlModel){
                        List<HttpHtmlModel> httpHtmlModelList = getHttpHtmlModel(pageId);
                        if(httpHtmlModelList != null && httpHtmlModelList.size() >0){
                            HttpHtmlModel httpHtmlModel = httpHtmlModelList.get(0);
                            referer = httpHtmlModel.getUrl();
                        }
                    }
                }
                map.add("referer", referer);
            }

            map.add("ttl", String.valueOf(timeStamp));
            map.add("location", url);
            map.add("isdebugger","0");
            map.add("pid", pid);

            String jsUrl = jskillerVodacomUrl;
            //vodacom 需要传cookie
            if (offerStepModel.getStepName().startsWith(ZaConstant.VODACOM_NODEKS_PATTERN)) {
                if(!StringUtils.isEmpty(cookie)){
                    map.add("cookie",jsonObject.getString("cookie"));
                }
            } else if (offerStepModel.getStepName().startsWith(ZaConstant.MTN_NODEJS_PATTERN)){
                jsUrl = jskillerZaMtnUrl;
            } else if (offerStepModel.getStepName().startsWith(ZaConstant.MTN_OPTICKS_NODEJS_PATTERN)){
                jsUrl = jskillerZaMtnOpticksUrl;
            }
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(jsUrl, request, String.class);
            if(null != response){
                String body = response.getBody();
                if(null != body){
                    nodeJsResult = JSONArray.parseArray(body);
                    String key = ZooConstant.NODE_JS_RESULT + offerStepModel.getIdentification();
                    Boolean existKey = stringRedisTemplate.hasKey(key);
                    if(!(existKey != null && existKey)){
                        stringRedisTemplate.opsForValue().set(key,body);
                        stringRedisTemplate.expire(key,10,TimeUnit.MINUTES);
                    }
                    if(nodeJsResult != null && nodeJsResult.size() >0){
                        originHtml.setParam2(body);
                    }
                }
            }
        }catch (Exception e){
            log.error("NodeJs execute error:{}",e.getMessage());
        }
        return nodeJsResult;
    }


    private List<HttpHtmlModel> formatHttpHtmlModelList(OfferStepModel offerStepModel,String pid, String operator, List<JSONObject> paramArray) {
        List<HttpHtmlModel> httpHtmlModelList = new ArrayList<>();
        if(paramArray != null && paramArray.size() >0){
            for(JSONObject param :paramArray){
                HttpHtmlModel httpHtmlModel = new HttpHtmlModel();
                httpHtmlModel.setIdentification(pid + Constants.COLON + offerStepModel.getIdentification());
                httpHtmlModel.setHtml(param.getString("sh"));
                httpHtmlModel.setUrl(param.getString("path"));
                httpHtmlModel.setHeader(param.getString("rh"));
                httpHtmlModel.setOperator(operator);
                httpHtmlModel.setStepName(offerStepModel.getStepName());
                httpHtmlModelList.add(httpHtmlModel);
            }
        }
        return httpHtmlModelList;
    }

    /**
     * 处理HTML DocumentPage JSON 结果
     * @param jsBack
     * @param method
     * @param pinRegx
     * @param operator
     * @param header
     * @param originHtml
     * @param offerStepModel
     * @param pin
     * @param httpHtmlModel
     * @param analysisBodyVO
     * @param returnJson
     * @param httpHtmlModelList
     * @return
     */
    private JSONObject handleHtmlPageJsonReuslt(String jsBack, String method, String pinRegx, String operator, JSONObject header, HttpHtmlModel originHtml, OfferStepModel offerStepModel, String pin, HttpHtmlModel httpHtmlModel, AnalysisBodyVO analysisBodyVO, JSONObject returnJson, List<HttpHtmlModel> httpHtmlModelList) {
        JSONObject bdHandleResult = JSON.parseObject(jsBack);
        Object resultObj  = bdHandleResult.get("result");
        if(resultObj != null){
            if(resultObj instanceof JSONObject){
                JSONObject resultJson = bdHandleResult.getJSONObject("result");
                String realUrl = resultJson.getString("url");
                String formDataStr = resultJson.getString("form");
                boolean existForm = resultJson.containsKey("form");
                method = StringUtils.isEmpty(resultJson.getString("method")) ? method : resultJson.getString("method");
                pinRegx = StringUtils.isEmpty(resultJson.getString("pinRegx")) ? pinRegx : resultJson.getString("pinRegx");
                String contentType = resultJson.getString("Content-Type");
                String aio = resultJson.getString("aio");
                String intervals = resultJson.getString("intervals");
                String ttl = resultJson.getString("ttl");
                String saveCookie = resultJson.getString("saveCookie");
                //vodacom 添加cookie
                String setCookie = resultJson.getString("setCookie");
                if(AlexConstant.ZA_MTN.equalsIgnoreCase(operator)){
                    if(!StringUtils.isEmpty(contentType)){
                        header.put("Content-Type", contentType);
                    }
                } else {
                    contentType = StringUtils.isEmpty(contentType) ? "text/plain" : contentType;
                    header.put("Content-Type", contentType);
                }
                syncJsHeader(resultJson, header);
                saveResultPage(bdHandleResult, originHtml);
                // 是否保存页面(不是pin码返回)
                if(offerStepModel.isSave() && StringUtils.isEmpty(pin)) {
                    Boolean existHttpHtmlModel = cluster2RedisTemplate.hasKey(httpHtmlModel.getIdentification());
                    if (!(existHttpHtmlModel != null && existHttpHtmlModel)) {
                        saveHttpHtmlModelRedis(httpHtmlModel);
                    }
                }
                if (!StringUtils.isEmpty(formDataStr)) {
                    analysisBodyVO.setFormData(formDataStr);
                }else if(existForm){
                    analysisBodyVO.setFormData("");
                }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                   analysisBodyVO = setParamAnalysisBody(analysisBodyVO,offerStepModel,method,realUrl,header,aio,intervals,ttl,setCookie);
                if(!StringUtils.isEmpty(pinRegx)){
                    analysisBodyVO.setPinRegx(pinRegx);
                }
                //判断是否保存cookie,以JS中为准
                if(!StringUtils.isEmpty(saveCookie)){
                    analysisBodyVO.setSaveCookie(Integer.valueOf(saveCookie));
                }else {
                    if(offerStepModel.getSaveCookie() == null){
                        analysisBodyVO.setSaveCookie(1);
                    }else {
                        analysisBodyVO.setSaveCookie(offerStepModel.getSaveCookie());
                    }
                }
                returnJson.put("analysisBodyVo",JSON.toJSONString(analysisBodyVO));
            }else if(resultObj instanceof JSONArray){
                returnJson = handleHtmlPageJsonReusltArray(resultObj,method,pinRegx,operator,header,originHtml,offerStepModel,pin,httpHtmlModelList,returnJson);
            }
        }else {
            returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getCode());
            returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.READ_PAGE_HANDLE_JS_RESPONSE_NULL.getMsg());
        }
        return returnJson;
    }

    /**
     * 生成数组参数
     * @param url
     * @param appId
     * @param offerId
     * @param pin
     * @param offerStepModel
     * @param httpHtmlModelList
     * @param deviceId
     * @param paramArray
     * @param nodeJsResult
     * @return
     */
    private List<JSONObject> genereateParamList(String url,  String appId, String offerId, String pin, OfferStepModel offerStepModel,
                                                List<HttpHtmlModel> httpHtmlModelList, String deviceId, List<JSONObject> paramArray, JSONArray nodeJsResult) {
        List<JSONObject> paramList = new ArrayList<>();
        for(JSONObject paramJson :paramArray){
            JSONObject params = new JSONObject();
            params.put("url", paramJson.getString("path"));
            params.put("header", paramJson.getString("sh"));
            params.put("appId", appId);
            params.put("offerId", offerId);
            if (!StringUtils.isEmpty(pin)) {
                params.put("pin", pin);
            } else {
                params.put("body", paramJson.getString("rh"));
            }
            boolean checkMsisdn = offerStepModel !=null && offerStepModel.getGetMsisdn() != null && offerStepModel.getGetMsisdn() == 1;
            if(checkMsisdn){
                UserMobileInfoModel userMobileInfoModel = null;
                if (!StringUtils.isEmpty(deviceId)) {
                    String subDeviceId = deviceId;
                    if(deviceId.length() >= NumberEnum.EIGHT_TEEN.getNum()){
                        subDeviceId = deviceId.substring(0,15);
                    }
                    userMobileInfoModel = userMobileInfoRepo.findFirstByDeviceIdOrderByCreateTimeDesc(subDeviceId);
                    if(userMobileInfoModel != null){
                        params.put("msisdn",userMobileInfoModel.getMobile());
                    }
                }
            }
            String responseBody = paramJson.getString("body");
            if (!StringUtils.isEmpty(responseBody)) {
                try{
                    Document document = Jsoup.parse(responseBody);
                    Element form = document.selectFirst(ProtocolCrackConstant.FORM);
                    // 如果包含 from 且需要解析(提取表单数据)   获取表单数据
                    if (form != null && offerStepModel.isCommitForm()) {
                        String actionUrl = getActionUrl(url, form.attr(ProtocolCrackConstant.ACTION));
                        JSONObject formData = getFormDataJson(form, offerStepModel);
                        params.put("action", actionUrl);
                        params.put("form", formData);
                        // 设置 httpHtml
                        HttpHtmlModel httpHtmlModel = httpHtmlModelList.get(paramArray.indexOf(paramJson));
                        if(httpHtmlModel != null ){
                            httpHtmlModel.setFormData(formData.toJSONString());
                            httpHtmlModel.setNextUrl(actionUrl);
                        }
                    }
                }catch (Exception e){
                    log.error("HANDLE FORM ERROR:{},OFFER ID:{}",e.getMessage(),offerId);
                }
            }
            paramList.add(params);
            JSONObject nodejsResult = new JSONObject();
            nodejsResult.put("nodejsResult",nodeJsResult.toJSONString());
            paramList.add(nodejsResult);
        }
        return paramList;
    }

    private void saveHttpHtmlModelRedis(HttpHtmlModel httpHtmlModel) {
        String httpHtmlKey = ZooConstant.HTML_INFO + CacheNameSpace.COLON +  httpHtmlModel.getIdentification();
        stringRedisTemplate.opsForValue().set(httpHtmlKey,JSON.toJSONString(httpHtmlModel));
        stringRedisTemplate.expire(httpHtmlKey, 3, TimeUnit.MINUTES);
    }

    private List<HttpHtmlModel> getHttpHtmlModel(String pageId) {
        List<HttpHtmlModel> redisHtmlList = new ArrayList<>();
        HttpHtmlModel httpHtmlModel = null;
        String result = "";
        if(!StringUtils.isEmpty(pageId)){
            Object htmlObject = cluster2RedisTemplate.opsForValue().get(ZooConstant.HTML_INFO + CacheNameSpace.COLON + pageId);
            if(htmlObject != null){
                result = String.valueOf(htmlObject);
                if(!StringUtils.isEmpty(result)){
                    Object htmlValue = JSON.parse(result);
                    if(htmlValue instanceof JSONObject){
                        httpHtmlModel =JSONObject.parseObject(result,HttpHtmlModel.class);
                        redisHtmlList.add(httpHtmlModel);
                    }else if(htmlValue instanceof JSONArray){
                        redisHtmlList = JSONArray.parseArray(result,HttpHtmlModel.class);
                    }
                }
            }
        }
        return redisHtmlList;
    }

    /**
     * 设置返回AnalysisBody
     * @param analysisBodyVO
     * @param offerStepModel
     * @param method
     * @param realUrl
     * @param header
     * @param aio
     * @param intervals
     * @param ttl
     * @return
     */
    private AnalysisBodyVO setParamAnalysisBody(AnalysisBodyVO analysisBodyVO, OfferStepModel offerStepModel, String method, String realUrl, JSONObject header, String aio, String intervals, String ttl,String setCookie) {
        analysisBodyVO.setType(offerStepModel.getCrackType());
        int stayTime = offerStepModel.getStayTime() != null ? (offerStepModel.getStayTime() < 1000 ? offerStepModel.getStayTime() * 1000 : offerStepModel.getStayTime()) : 0;
        int stepTime = offerStepModel.getStepTime() != null ? (offerStepModel.getStepTime() < 1000 ? offerStepModel.getStepTime() * 1000 : offerStepModel.getStepTime()) : 0;
        analysisBodyVO.setStayTime(stayTime);
        analysisBodyVO.setStepTime(stepTime);
        analysisBodyVO.setMethod(method);
        analysisBodyVO.setUrl(realUrl);
        analysisBodyVO.setSetCookie(setCookie);
        analysisBodyVO.setNextRegex(offerStepModel.getNextRegex());
        analysisBodyVO.setHs(header.toJSONString());
        if(!StringUtils.isEmpty(aio)){
            analysisBodyVO.setAio(Integer.valueOf(aio));
        }
        if(!StringUtils.isEmpty(intervals)){
            analysisBodyVO.setIntervals(Integer.valueOf(intervals));
        }
        if(!StringUtils.isEmpty(ttl)){
            analysisBodyVO.setTtl(Long.valueOf(ttl));
        }
        return analysisBodyVO;
    }

    private HttpHtmlModel formatHttpHtmlModel(String pid, OfferStepModel offerStepModel, String responseBody, String url, String responseHeader, String operator) {
        HttpHtmlModel httpHtmlModel = new HttpHtmlModel();
        httpHtmlModel.setIdentification(pid + Constants.COLON + offerStepModel.getIdentification());
        httpHtmlModel.setHtml(responseBody);
        httpHtmlModel.setUrl(url);
        httpHtmlModel.setHeader(responseHeader);
        httpHtmlModel.setOperator(operator);
        httpHtmlModel.setStepName(offerStepModel.getStepName());
        return  httpHtmlModel;
    }

    private void saveResultPage(JSONObject bdHandleResult, HttpHtmlModel originHtml){
        //保存修改后的页面数据
        if (bdHandleResult.containsKey(AlexConstant.PAGE)){
            String page1 = bdHandleResult.getString(AlexConstant.PAGE);
            if(JsonUtil.isJSONValid(page1)){
                JSONObject page = JSON.parseObject(page1);
                if (page != null) {
                    HttpHtmlModel httpHtmlModel  = JSONObject.parseObject(page.toJSONString(), HttpHtmlModel.class);
                    saveHttpHtmlModelRedis(httpHtmlModel);
                }
            } else {
                if(null != originHtml){
                    originHtml.setHtml(page1);
                    saveHttpHtmlModelRedis(originHtml);
                }
            }
        }

    }

    private JSONObject initJsParams(String url, String responseHeader, String appId, String offerId, String pin, String responseBody, OfferStepModel offerStepModel, HttpHtmlModel httpHtmlModel, String deviceId){
        JSONObject params = new JSONObject();
        params.put("url", url);
        params.put("header", responseHeader);
        params.put("appId", appId);
        params.put("offerId", offerId);
        if (!StringUtils.isEmpty(pin)) {
            params.put("pin", pin);
        } else {
            params.put("body", responseBody);
        }
        boolean checkMsisdn = offerStepModel !=null && offerStepModel.getGetMsisdn() != null && offerStepModel.getGetMsisdn() == 1;
        if(checkMsisdn){
            UserMobileInfoModel userMobileInfoModel = null;
            if (!StringUtils.isEmpty(deviceId)) {
                String subDeviceId = deviceId;
                if(deviceId.length() >= NumberEnum.EIGHT_TEEN.getNum()){
                    subDeviceId = deviceId.substring(0,15);
                }
                userMobileInfoModel = userMobileInfoRepo.findFirstByDeviceIdOrderByCreateTimeDesc(subDeviceId);
                if(userMobileInfoModel != null){
                    params.put("msisdn",userMobileInfoModel.getMobile());
                }
            }
        }
        if (!StringUtils.isEmpty(responseBody)) {
            try{
                Document document = Jsoup.parse(responseBody);
                Element form = document.selectFirst(ProtocolCrackConstant.FORM);
                // 如果包含 from 且需要解析(提取表单数据)   获取表单数据
                if (form != null && offerStepModel.isCommitForm()) {
                    String actionUrl = getActionUrl(url, form.attr(ProtocolCrackConstant.ACTION));
                    JSONObject formData = getFormDataJson(form, offerStepModel);
                    params.put("action", actionUrl);
                    params.put("form", formData);
                    // 设置 httpHtml
                    httpHtmlModel.setFormData(formData.toJSONString());
                    httpHtmlModel.setNextUrl(actionUrl);
                }
            }catch (Exception e){
                log.error("HANDLE FORM ERROR:{},OFFER ID:{}",e.getMessage(),offerId);
            }
        }
        return params;
    }



    private void truemoveAoc01(OfferStepModel offerStepModel, String responseBody, HttpHtmlModel originHtml){
        if (TruemoveAocJs.AOC_XHR_1.equals(offerStepModel.getStepName())
                || TruemoveAocJs.AOC_XHR_1_ID.equals(offerStepModel.getIdentification())) {
            Document doc = Jsoup.parseBodyFragment(responseBody);
            String encodeStr = doc.getElementById("copy-22-for-more-info").dataset().get("global-style");
            String decodeKey = responseBody.split("data-style-location=\"")[1].split("\"")[0];
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("encodeStr", encodeStr);
            map.add("decodeKey", decodeKey);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
            ResponseEntity<String> response = restTemplate.postForEntity( jskillerUrl, request , String.class);
            originHtml.setParam2(response.getBody());
//            String param3 = responseBody.split("data-style-location=\"")[1].split("\"")[0];
        }
    }

    @SuppressWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private void zaMtnActivate(String requestHeader, OfferStepModel offerStepModel, String responseBody, HttpHtmlModel originHtml, JSONObject header){
        if (!StringUtils.isEmpty(offerStepModel.getStepName()) && offerStepModel.getStepName().startsWith(ZaConstant.MTN_NODEJS_PATTERN)) {
            JSONObject jsonObject = JSON.parseObject(requestHeader);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
            map.add("htmlStr", responseBody);
            map.add("referer", header.getString("Referer"));
            map.add("location", originHtml.getUrl());
            map.add("userAgent", jsonObject.getString("user-agent"));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(jskillerZaMtnUrl, request, String.class);
            if(null != response){
                String body = response.getBody();
                if(null != body){
                    String[] split = body.split("\\|\\|\\|");
                    if(null != split && split.length >= NumberEnum.FOUR.getNum()){
                        originHtml.setParam2(split[0]);
                        originHtml.setParam3(split[1]);
                        originHtml.setParam4(split[2]);
                        originHtml.setParam5(split[3]);
                    }
                }
            }
        }
    }

    /**
     * 获取表单数据
     * @param form
     * @param offerStepModel
     * @return
     * @throws Exception
     */
    private JSONObject getFormDataJson(Element form, OfferStepModel offerStepModel){
        JSONObject formData = null;
        Elements elements = form.select(ProtocolCrackConstant.INPUT);
        if (elements != null && elements.size() > 0) {
            formData = new JSONObject();;
            Iterator<Element> iterator = elements.iterator();
            while (iterator.hasNext()) {
                Element element = iterator.next();
                formData.put(element.attr(ProtocolCrackConstant.NAME), element.val());
            }
        }
        return formData;
    }

    /**
     * 获取 action url
     * @param url
     * @param attr
     * @return
     */
    private String getActionUrl(String url, String attr) {
        String actionUrl = attr;
        Matcher matcher = PatternUtil.PROTOCOL_TYPE.matcher(actionUrl);
        // 如果 action 不包含域名，则添加域名
        if (!matcher.find()) {
            try {
                URL thisUrl = new URL(url);
                String domain = StringUtils.isEmpty(thisUrl.getPath()) ?
                        url : url.substring(0, url.indexOf(thisUrl.getPath()));
                actionUrl = domain + actionUrl;
            } catch (Exception e) {
                log.error("url error:{}", e.getMessage(), e);
            }
        }
        return actionUrl;
    }


    /**
     * 处理 js
     * @param obj
     * @param jsStr
     * @param offerStepModel
     * @return
     * @throws Exception
     */
    private JSONObject handleJs(HttpServletRequest request, JSONObject obj, String jsStr, OfferStepModel offerStepModel, String isDebug,List<JSONObject> paramArray) {
        JSONObject result = new JSONObject();
        try {
            //获取JavaScript执行引擎
            ScriptEngineManager m = new ScriptEngineManager();
            //执行JavaScript代码
            ScriptEngine engine = m.getEngineByName("JavaScript");
            engine.eval(jsStr);
            // 从脚本引擎中返回一个给定接口的实现
            Invocable invocable = (Invocable) engine;
            Methods methods = invocable.getInterface(Methods.class);
            if(paramArray != null && paramArray.size() >0){
                // 参数为数组
                for(JSONObject param : paramArray){
                    param.put(AlexConstant.USER_AGENT, request.getHeader(AlexConstant.USER_AGENT));
                }
                JSONArray objArray = new JSONArray();
                for(JSONObject jsonObject : paramArray){
                    objArray.add(jsonObject);
                }
                result.put("handleResult",methods.execute(objArray));
            }else {
                obj.put(AlexConstant.USER_AGENT, request.getHeader(AlexConstant.USER_AGENT));
                result.put("handleResult", methods.execute(obj));
            }
            return result;
        }catch (Exception e){
            log.info("Error:{}",e.getMessage());
            result.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.HANDLE_JS_ERROR.getCode());
            if(!StringUtils.isEmpty(isDebug) && isDebug.equalsIgnoreCase(ZooConstant.HANDLE_DEBUG_JS)){
                JSONObject errorMessage = new JSONObject();
                errorMessage.put("error",e.getMessage());
                errorMessage.put("param",JSON.toJSONString(obj));
                errorMessage.put("offerModel",offerStepModel);
                result.put(ZooConstant.ERROR_MESSAGE,JSON.toJSONString(errorMessage));
            }else {
                result.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.HANDLE_JS_ERROR.getMsg());
            }
            return result;
        }
    }

    /**
     * 处理 redirect 流程
     * @param url
     * @param body
     * @param responseHeader
     * @param requestHeader
     * @param offerStepModel
     * @return
     */
    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    @Timed
    @Override
    public JSONObject getRedirect(HttpServletRequest request, String pid, String url, String body, String responseHeader, String requestHeader, String operator, OfferStepModel offerStepModel,String isDebug,String userId,List<JSONObject> paramArray) {
            JSONObject returnJson = new JSONObject();
            JSONObject params = generateParamsJson(url,body,responseHeader);
            int crackType = offerStepModel.getCrackType() ==null ? NumberEnum.TWO.getNum() : offerStepModel.getCrackType();
            boolean pullScript = offerStepModel.getPullScript() != null && offerStepModel.getPullScript() ==1  && crackType== NumberEnum.ONE.getNum() ? true : false;
            // 破解步骤为利刃并且需要拉取JS
            if(crackType== NumberEnum.ONE.getNum() && pullScript){
                returnJson = handleSwordPullScript(returnJson,crackType,offerStepModel,operator,url);
                return returnJson;
            } else {
                JSONObject resultJson;
                if (!StringUtils.isEmpty(offerStepModel.getScript())) {
                    if(paramArray != null && paramArray.size() >0){
                        List<JSONObject> paramList = genereateParamArray(paramArray);
                        resultJson = handleJs(request, params, offerStepModel.getScript(),offerStepModel,isDebug,paramList);
                    }else {
                        resultJson = handleJs(request, params, offerStepModel.getScript(),offerStepModel,isDebug,null);
                    }
                    log.info("handle js return value :{},offer url regex:{}",JSON.toJSONString(resultJson),offerStepModel.getRegex());
                    // 若执行JS 未出错
                    if(resultJson!=null && StringUtils.isEmpty(resultJson.getString(ZooConstant.ERROR_CODE))){
                        String jsBack = resultJson.getString("handleResult");
                        returnJson = handleAexRedirect(jsBack,crackType,offerStepModel,pid,userId,returnJson);
                    }else {
                        returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.AEX_HANDLE_JS_ERROR.getCode());
                        if(!StringUtils.isEmpty(isDebug) && isDebug.equalsIgnoreCase(ZooConstant.HANDLE_DEBUG_JS)){
                            returnJson.put(ZooConstant.ERROR_MESSAGE,resultJson.getString(ZooConstant.ERROR_MESSAGE));
                        }else {
                            returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.AEX_HANDLE_JS_ERROR.getMsg());
                        }
                    }
                }else {
                    returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.AEX_OFFER_STEP_SCRIPT_NULL.getCode());
                    returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.AEX_OFFER_STEP_SCRIPT_NULL.getMsg());
                }
                return returnJson;
            }
    }

    private JSONObject handleAexRedirect(String jsBack, int crackType, OfferStepModel offerStepModel, String pid, String userId, JSONObject returnJson) {
        Object resultObj = JSON.parse(jsBack);
        if(resultObj instanceof JSONObject){
            JSONObject json = JSON.parseObject(jsBack);
            returnJson = formatAexRedirectResult(json,crackType,offerStepModel,pid,jsBack,returnJson,userId);
        }else if(resultObj instanceof JSONArray){
            JSONArray resultArray = JSONArray.parseArray(jsBack);
            List<AnalysisBodyVO> analysisBodyVOList = new ArrayList<>();
            for(Object object :resultArray){
                JSONObject aexJsonResult = JSONObject.parseObject(object.toString());
                if(aexJsonResult != null){
                    JSONObject result = aexJsonResult.getJSONObject("result");
                    AnalysisBodyVO analysisBodyVo = generateAnalysisBody(crackType,offerStepModel,result);
                    //查找referstep
                    JSONObject header = new JSONObject();
                    String id = pid + Constants.COLON + offerStepModel.getRefererStep();
                    List<HttpHtmlModel> byReferStepIdList = getHttpHtmlModel(id);
                    if(byReferStepIdList != null && byReferStepIdList.size() >0){
                        HttpHtmlModel byReferStepId = byReferStepIdList.get(0);
                        if(byReferStepId != null){
                            HttpHtmlModel httpHtmlModel1 = byReferStepId;
                            header = userAgentService.getGlobalHeaders(httpHtmlModel1.getUrl(), false,userId);
                        }
                        JSONObject bdHandleResult = JSON.parseObject(jsBack);
                        syncJsHeader(bdHandleResult.getJSONObject("result"), header);
                        analysisBodyVo.setHs(header.toJSONString());
                        analysisBodyVOList.add(analysisBodyVo);
                    }
                }else {
                    returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.AEX_HANDLE_JS_RESPONSE_NULL.getCode());
                    returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.AEX_HANDLE_JS_RESPONSE_NULL.getMsg());
                }
            }
            if(analysisBodyVOList != null && analysisBodyVOList.size() >0){
                returnJson.put("analysisBodyVo",JSON.toJSONString(analysisBodyVOList));
            }
        }

        return returnJson;
    }

    /**
     * formatAexRedirectResult
     * @param json
     * @param crackType
     * @param offerStepModel
     * @param pid
     * @param jsBack
     * @param returnJson
     * @param userId
     * @return
     */
    private JSONObject formatAexRedirectResult(JSONObject json, int crackType, OfferStepModel offerStepModel, String pid, String jsBack, JSONObject returnJson, String userId) {
        if(json!=null){
            JSONObject result = json.getJSONObject("result");
            if (result != null) {
                AnalysisBodyVO analysisBodyVo = generateAnalysisBody(crackType,offerStepModel,result);
                //查找referstep
                JSONObject header = new JSONObject();
                String id = pid + Constants.COLON + offerStepModel.getRefererStep();
                List<HttpHtmlModel> byReferStepIdList = getHttpHtmlModel(id);
                if(byReferStepIdList != null && byReferStepIdList.size() >0){
                    HttpHtmlModel byReferStepId = byReferStepIdList.get(0);
                    if(byReferStepId != null){
                        HttpHtmlModel httpHtmlModel1 = byReferStepId;
                        header = userAgentService.getGlobalHeaders(httpHtmlModel1.getUrl(), false,userId);
                    }
                }
                JSONObject bdHandleResult = JSON.parseObject(jsBack);
                syncJsHeader(bdHandleResult.getJSONObject("result"), header);
                analysisBodyVo.setHs(header.toJSONString());
                returnJson.put("analysisBodyVo",JSON.toJSONString(analysisBodyVo));
            }
        }else {
            returnJson.put(ZooConstant.ERROR_CODE,AexErrorCodeEnum.AEX_HANDLE_JS_RESPONSE_NULL.getCode());
            returnJson.put(ZooConstant.ERROR_MESSAGE,AexErrorCodeEnum.AEX_HANDLE_JS_RESPONSE_NULL.getMsg());
        }
        return returnJson;
    }

    /**
     * 处理破解步骤为利刃并且需要拉取JS
     * @param returnJson
     * @param crackType
     * @param offerStepModel
     * @param operator
     * @param url
     * @return
     */
    private JSONObject handleSwordPullScript(JSONObject returnJson, int crackType, OfferStepModel offerStepModel, String operator, String url) {
        AnalysisBodyVO analysisBodyVo = generateBody(crackType,offerStepModel);
        if(!StringUtils.isEmpty(operator) ){
            String country = operator.split(ZooConstant.UNDER_LINE)[0].toLowerCase();
            if(!StringUtils.isEmpty(country)){
                JSONObject scriptJson = scriptService.getAppScriptJson(url,country);
                if(!StringUtils.isEmpty(scriptJson.getString(ZooConstant.ERROR_MESSAGE))){
                    returnJson.put(ZooConstant.ERROR_CODE,scriptJson.getInteger(ZooConstant.ERROR_CODE));
                    returnJson.put(ZooConstant.ERROR_MESSAGE,scriptJson.getString(ZooConstant.ERROR_MESSAGE));
                }else {
                    ScriptModel scriptModel =JSON.parseObject(scriptJson.getString(ZooConstant.SCRIPT_MODEL),ScriptModel.class);
                    if(scriptModel!=null){
                        analysisBodyVo.setCrackScript(JSON.toJSONString(scriptModel));
                    }
                }
            }
        }
        if(StringUtils.isEmpty(returnJson.getString(ZooConstant.ERROR_CODE))){
            returnJson.put("analysisBodyVo",JSON.toJSONString(analysisBodyVo));
        }
        return returnJson;
    }

    private List<JSONObject> genereateParamArray(List<JSONObject> paramArray) {
        List<JSONObject> formatParamList = new ArrayList<>();
        for(JSONObject jsonObject : paramArray){
            JSONObject params = new JSONObject();
            params.put("url", jsonObject.getString("path"));
            params.put("body", jsonObject.getString("body"));
            params.put("header", jsonObject.getString("sh"));
            formatParamList.add(params);
        }
        return formatParamList;
    }

    private JSONObject generateParamsJson(String url, String body, String responseHeader) {
        JSONObject params = new JSONObject();
        params.put("url", url);
        params.put("body", body);
        params.put("header", responseHeader);
        return params;
    }

    private AnalysisBodyVO generateBody(Integer crackType,OfferStepModel offerStepModel) {
        AnalysisBodyVO analysisBodyVo = new AnalysisBodyVO();
        analysisBodyVo.setType(crackType);
        int stayTime = offerStepModel.getStayTime() != null ? (offerStepModel.getStayTime() < 1000 ? offerStepModel.getStayTime() * 1000 : offerStepModel.getStayTime()) : 0;
        int stepTime = offerStepModel.getStepTime() != null ? (offerStepModel.getStepTime() < 1000 ? offerStepModel.getStepTime() * 1000 : offerStepModel.getStepTime()) : 0;
        analysisBodyVo.setStayTime(stayTime);
        analysisBodyVo.setStepTime(stepTime);
        analysisBodyVo.setNextRegex(offerStepModel.getNextRegex());
        if(offerStepModel.getSaveCookie() == null){
            analysisBodyVo.setSaveCookie(1);
        }else {
            analysisBodyVo.setSaveCookie(offerStepModel.getSaveCookie());
        }
        return analysisBodyVo;
    }

    private AnalysisBodyVO generateAnalysisBody(int crackType, OfferStepModel offerStepModel, JSONObject result) {
        String realUrl = result.getString("url");
        String aio = result.getString("aio");
        String intervals = result.getString("intervals");
        String ttl = result.getString("ttl");
        String saveCookie = result.getString("saveCookie");
        AnalysisBodyVO analysisBodyVo = new AnalysisBodyVO();
        analysisBodyVo.setUrl(realUrl);
        analysisBodyVo.setType(crackType);
        int stayTime = offerStepModel.getStayTime() != null ? (offerStepModel.getStayTime() < 1000 ? offerStepModel.getStayTime() * 1000 : offerStepModel.getStayTime()) : 0;
        int stepTime = offerStepModel.getStepTime() != null ? (offerStepModel.getStepTime() < 1000 ? offerStepModel.getStepTime() * 1000 : offerStepModel.getStepTime()) : 0;
        analysisBodyVo.setStayTime(stayTime);
        analysisBodyVo.setStepTime(stepTime);
        analysisBodyVo.setNextRegex(offerStepModel.getNextRegex());
        analysisBodyVo.setMethod(offerStepModel.getMethod());
        if(!StringUtils.isEmpty(aio)){
            analysisBodyVo.setAio(Integer.valueOf(aio));
        }
        if(!StringUtils.isEmpty(intervals)){
            analysisBodyVo.setIntervals(Integer.valueOf(intervals));
        }
        if(!StringUtils.isEmpty(ttl)){
            analysisBodyVo.setTtl(Long.valueOf(ttl));
        }
        //判断是否保存cookie,以JS中为准
        if(!StringUtils.isEmpty(saveCookie)){
            analysisBodyVo.setSaveCookie(Integer.valueOf(saveCookie));
        }else {
            if(offerStepModel.getSaveCookie() == null){
                analysisBodyVo.setSaveCookie(1);
            }else {
                analysisBodyVo.setSaveCookie(offerStepModel.getSaveCookie());
            }
        }
        return analysisBodyVo;
    }

    /**
     * 检查链接是否匹配
     * @param url
     * @param key
     * @return
     */
    private boolean checkMatch(String url, String key) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(key)) {
            return false;
        }
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    @Override
    public String testJsKiller(Map<String, String> params) {
        JSONArray nodeJsResult = new JSONArray();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, Object> map= new LinkedMultiValueMap<String, Object>();
            map.add("htmlStr", params.get("htmlStr"));
            map.add("referer", params.get("referer"));
            map.add("userAgent", params.get("userAgent"));
            map.add("location",params.get("location"));
            map.add("isdebugger","0");
            String jsUrl = "";
            if(ZaConstant.ZA_VODACOM.equalsIgnoreCase(params.get(ZaConstant.CARRIER))){
                jsUrl = jskillerVodacomUrl;
            }else if(ZaConstant.ZA_MTN.equalsIgnoreCase(params.get(ZaConstant.CARRIER))){
                jsUrl = jskillerZaMtnUrl;
            }
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(jsUrl, request, String.class);
            if(null != response){
                String body = response.getBody();
                nodeJsResult = JSONArray.parseArray(body);
                log.info("za test jskiller NodeJs execute result:{}",body);
            }
        }catch (Exception e){
            log.error("za test jskiller NodeJs execute error:{}",e.getMessage());
        }
        JSONArray returJsonArray = nodeJsResult.stream().map(obj -> {
            JSONObject returnObj = new JSONObject();
            JSONObject jsonObj = (JSONObject)obj;
            jsonObj.forEach((key,val) -> {
                returnObj.put(key.replace("formData","form"),val);
            });
            return returnObj;
        }).collect(Collectors.toCollection(JSONArray::new));
        return JSON.toJSONString(returJsonArray);
    }

    @Override
    @Cacheable(value = "step",key = "#stepId")
    public Map<Object, Object> findStepMap(String stepId) {
        Map<Object,Object> map = cluster2RedisTemplate.opsForHash().entries(stepId);
        return map;
    }
}
