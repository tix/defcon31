package com.starp.zoo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.entity.zoo.OfferStepModel;
import com.starp.zoo.vo.AnalysisBodyVO;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author charles
 */
public interface ICrackStepService {

    /**
     * 破解步骤
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
     * @param isDebug
     * @param deviceId
     * @param timeStamp
     * @param cookie
     * @return
     * @throws Exception
     */
    Map<String,Object> analysisBody(HttpServletRequest request, String url, String body, String pin, String pid, String responseHeader,
                                    String requestHeader, String operator, String appId, String offerId, String userId,
                                    String isDebug, String deviceId,Long timeStamp,String cookie) throws Exception;
    
    
    /**
     * handleCrack
     * @param request
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
     * @param isDebug
     * @param deviceId
     * @param paramArray
     * @param timeStamp
     * @param cookie
     * @return
     * @throws Exception
     */
    Map<String,Object> handleCrack(HttpServletRequest request, String url, String body, String pin, String pid, String responseHeader,
            String requestHeader, String operator, String appId, String offerId, String userId, OfferStepModel offerStepModel, String isDebug,
                                   String deviceId,List<JSONObject> paramArray,Long timeStamp,String cookie) throws Exception ;
    
    /**
     * getRedirect
     * @param request
     * @param pid
     * @param url
     * @param body
     * @param responseHeader
     * @param requestHeader
     * @param operator
     * @param offerStepModel
     * @param isDebug
     * @param userId
     * @param paramArray
     * @return
     * @throws Exception
     */
    JSONObject getRedirect(HttpServletRequest request, String pid, String url, String body, String responseHeader, String requestHeader, String operator, OfferStepModel offerStepModel,String isDebug,String userId,List<JSONObject> paramArray) throws Exception;
    
    /**
     * formatReturnResult
     * @param analysisBodyVos
     * @param resultJson
     * @param analysisList
     * @return
     */
    Map<String, Object> formatReturnResult(List<AnalysisBodyVO> analysisBodyVos, JSONObject resultJson, Map<String,Object> analysisList);
    
    /**
     * getHtml
     * @param request
     * @param url
     * @param responseBody
     * @param pin
     * @param pid
     * @param responseHeader
     * @param requestHeader
     * @param operator
     * @param appId
     * @param offerId
     * @param offerStepModel
     * @param isDebug
     * @param deviceId
     * @param userId
     * @param timeStamp
     * @param paramList
     * @param cookie
     * @return
     * @throws Exception
     */
    JSONObject getHtml(HttpServletRequest request, String url, String responseBody, String pin, String pid,
            String responseHeader, String requestHeader, String operator, String appId, String offerId, OfferStepModel offerStepModel,
                       String isDebug, String deviceId,String userId,List<JSONObject> paramList,Long timeStamp,String cookie) throws Exception;
    
    /**
     * optickssecurity
     * @param url
     * @param responseBody
     * @param responseHeader
     * @param requestHeader
     * @param offerStepModel
     * @param pid
     * @param userId
     * @return
     * @throws Exception
     */
    JSONObject optickssecurity(String url, String responseBody, String responseHeader, String requestHeader, OfferStepModel offerStepModel, String pid,String userId) throws Exception;
    
    /**
     * getSuccess
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
    JSONObject getSuccess(String url, String body, String pin, String pid, String responseHeader, String requestHeader, String operator, String appId, String offerId, String userId);
    
    /**
     * handleHtmlXhr
     * @param request
     * @param url
     * @param responseBody
     * @param pin
     * @param pid
     * @param responseHeader
     * @param requestHeader
     * @param operator
     * @param appId
     * @param offerId
     * @param offerStepModel
     * @param isDebug
     * @param deviceId
     * @param userId
     * @param paramArray
     * @return
     */
    JSONObject handleHtmlXhr(HttpServletRequest request, String url, String responseBody, String pin, String pid, String responseHeader,
            String requestHeader, String operator, String appId, String offerId, OfferStepModel offerStepModel, String isDebug, String deviceId,String userId,List<JSONObject> paramArray);
    
    /**
     * handleHtmlDocumentPage
     * @param request
     * @param url
     * @param responseBody
     * @param pin
     * @param pid
     * @param responseHeader
     * @param requestHeader
     * @param operator
     * @param appId
     * @param offerId
     * @param offerStepModel
     * @param isDebug
     * @param deviceId
     * @param userId
     * @param timeStamp
     * @param paramArray
     * @param cookie
     * @return
     */
    JSONObject handleHtmlDocumentPage(HttpServletRequest request, String url, String responseBody, String pin, String pid, String responseHeader, String requestHeader, String operator, String appId, String offerId,
                                      OfferStepModel offerStepModel, String isDebug, String deviceId,String userId,List<JSONObject> paramArray,Long timeStamp,String cookie);
    
    /**
     * generateParams
     * @param url
     * @param responseHeader
     * @param requestHeader
     * @param appId
     * @param offerId
     * @param pin
     * @param responseBody
     * @param params
     * @param offerStepModel
     * @param deviceId
     * @return
     */
    JSONObject generateParams(String url, String responseHeader, String requestHeader, String appId, String offerId, String pin, String responseBody, JSONObject params, OfferStepModel offerStepModel, String deviceId);

    /**
     * analysisBodyByParamArray
     * @param request
     * @param bodyJsonArray
     * @return
     */
    Map<String, Object> analysisBodyParamArray(HttpServletRequest request, List<JSONObject> bodyJsonArray);

    /**
     * test jskiller
     * @param params
     * @return
     */
    String testJsKiller(Map<String, String> params);

    /**
     * findStepMap
     * @param stepId
     * @return
     */
    Map<Object, Object> findStepMap(String stepId);
}
