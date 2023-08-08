package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.config.AndroidGroupGetAesConfig;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.service.IAddressMsisdnService;
import com.starp.zoo.util.AesUtil;
import com.starp.zoo.util.DesUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * AddressMsisdnController.
 *
 * @author magic
 * @data 2022/4/21
 */
@RestController
@Slf4j
public class AddressMsisdnController {

    @Autowired
    private IAddressMsisdnService addressMsisdnService;

    @PostMapping(value = {"/456/gf454","/vcd/ffffg","/9fg/9gfdg","/c02/ydgop","/43x/sapx9","/9dg/saf32", "/xcy/897vx","/f33/76gsd","/h4n/fdihn","/fs5/7892a","/fdv/001sf", "/xjk/hjx5j", "/ydn/8zkdf","/okk/8pobn","/ku3/24gjk"})
    public String saveAddressMsisdn(@RequestBody String value, HttpServletRequest request) throws Exception {
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
            addressMsisdnService.save(jsonObject);
        } catch (Exception e) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
        JSONObject str = new JSONObject();
        str.put("string", "success");
        return ResponseInfoUtil.success(JSON.toJSONString(str), desUtil);
    }

    /**
     * saveAddressMsisdnNew
     * @param value
     * @param request
     * @return byte[]
     * @author Curry
     * @date 2022/11/1
     */
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING"})
    @RequestMapping({"/caddr", "/gsave", "/cabuse", "/gachievement", "/cactual", "/gadministrator", "/cadvice", "/gage", "/cairport", "/galternative",
            "/cangle", "/ganyone", "/csapplication", "/gargue", "/cart", "/casset", "/gathletic", "/caudience", "/cbaby", "/cbar", "/cbattery",
    "/gbefore", "/gbench", "/gcandidate", "/ccarrier", "/ccell"})
    public byte[] saveAddressMsisdnNew(@RequestBody byte[] value, HttpServletRequest request) throws Exception {
        AesUtil desUtil = AndroidGroupGetAesConfig.getAes(request);
        int encodeType = 1;
        if (request.getRequestURI().indexOf(ZooConstant.SPLIT_C) == -1) {
            encodeType = 2;
        }
        JSONObject jsonObject = null;
        try {
            if (StringUtils.isEmpty(value) || StringUtils.isEmpty(desUtil.decode(value, encodeType))) {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.SEND_EVENT_CODE_NULL.getCode(), ResponseInfoEnum.SEND_EVENT_CODE_NULL.getMsg());
                log.info("SEND_HTML NULL ,URL:{}", request.getRequestURL());
                return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
            }
            String jsonStr = new String(desUtil.decode(value, encodeType));
            jsonObject = JSON.parseObject(jsonStr);
            addressMsisdnService.save(jsonObject);
        } catch (Exception e) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
        }
        JSONObject str = new JSONObject();
        str.put("string", "success");
        return ResponseInfoUtil.successByte(JSON.toJSONString(str), desUtil, encodeType);
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
