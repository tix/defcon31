package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.entity.zoo.HtmlInfoModel;
import com.starp.zoo.service.IHtmlService;
import com.starp.zoo.util.DesUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/8/9
 * @description :
 */
@Controller
@Slf4j
public class AndroidGroupHtmlController {

    @Resource
    private IHtmlService htmlService;

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/kv/poxU6"}, method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseInfo saveHtml(@RequestBody JSONObject params, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String jsonStr = desUtil.decode(params.getString("values"));
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        HtmlInfoModel htmlInfoModel = new HtmlInfoModel();
        htmlInfoModel.setAppId(StringUtils.trimWhitespace(jsonObject.getString("appid")));
        htmlInfoModel.setOfferId(StringUtils.trimWhitespace(jsonObject.getString("offerid")));
        htmlInfoModel.setOriginUrl(jsonObject.getString("originUrl"));
        htmlInfoModel.setUserId(jsonObject.getString("userid"));
        htmlInfoModel.setSource(jsonObject.getString("source"));
        if (StringUtils.isEmpty(htmlInfoModel.getAppId()) || StringUtils.isEmpty(htmlInfoModel.getOfferId())) {
            return ResponseInfoUtil.wrong(null);
        }
        htmlService.saveModel(htmlInfoModel);
        return ResponseInfoUtil.success();
    }

    /**
     * API_UPLOAD_SOURCE
     * 利刃_SEND_HTML
     *
     * @param value
     * @param request
     * @return
     * @throws Exception
     */
    @Timed
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/yxn/8shbx", "/bhd/dwm2u", "/sjd/dji6h","/chj/uis3h", "/tdn/cbdus", "/xty/sdu3z", "/xyu/ui4n2","/vif/yu5kh", "/ydn/qmwoc", "/soi/sdhum","/udn/cjufe", "/cyf/dfk9j", "/bum/u2bkq", "/buz/fhu1i", "/njz/nds8q", "/dsi/ewh21", "/cdb/sdui2", "/ubs/6nj2b", "/dfj/znue8", "/io3/Hk8BN", "/a7y/vSn3b", "/cxu/nuT5t", "/bvp/vhTjn", "/djs/sf3Mb", "/vbs/vbNts", "/usp/Asw3e", "/dsz/we3fe", "/ckt/xmg5T", "/tjs/ptswX", "/nba/bvktc", "/jbp/Ashge", "/fgn/lsTes", "/wjf/stjsf", "/J3d/B2kmx"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String saveHtml(@RequestBody(required = false) String value, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        JSONObject jsonObject = null;
        try {
            if (StringUtils.isEmpty(value) || StringUtils.isEmpty(desUtil.decode(value))) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.SEND_EVENT_CODE_NULL.getCode(), ResponseInfoEnum.SEND_EVENT_CODE_NULL.getMsg());
                log.info("SEND_HTML NULL ,URL:{}", request.getRequestURL());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
            String jsonStr = desUtil.decode(value);
            jsonObject = JSON.parseObject(jsonStr);
            HtmlInfoModel htmlInfoModel = new HtmlInfoModel();
            htmlInfoModel.setAppId(StringUtils.trimWhitespace(jsonObject.getString("appid")));
            htmlInfoModel.setOfferId(StringUtils.trimWhitespace(jsonObject.getString("offerid")));
            htmlInfoModel.setOriginUrl(jsonObject.getString("originUrl"));
            htmlInfoModel.setUserId(jsonObject.getString("userid"));
            htmlInfoModel.setSource(jsonObject.getString("source"));
            if (StringUtils.isEmpty(htmlInfoModel.getAppId())) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.APPID_NULL.getCode(), ResponseInfoEnum.APPID_NULL.getMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
            if (StringUtils.isEmpty(htmlInfoModel.getOfferId())) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.OFFERID_NULL.getCode(), ResponseInfoEnum.OFFERID_NULL.getMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
            htmlService.handleSaveMsisdn(htmlInfoModel);
            JSONObject str = new JSONObject();
            str.put("string", "success");
            return ResponseInfoUtil.success(JSON.toJSONString(str), desUtil);
        } catch (Exception e) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
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

}
