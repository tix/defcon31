package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.service.IAppTranService;
import com.starp.zoo.service.ISubscribeService;
import com.starp.zoo.util.DesUtil;
import com.starp.zoo.util.IpUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/10.
 */
@Controller
public class TransformController {

    @Autowired
    private ISubscribeService subscribeService;

    @Autowired
    private IAppTranService appTranService;

    @RequestMapping(value = "/api/offer/trans/test/reset", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo deleteTestIp(@RequestParam(required = false) String offerId, HttpServletRequest request) {
        String ip = IpUtil.getIpAddr(request);
        subscribeService.deleteTestData(offerId, ip);
        return ResponseInfoUtil.success();
    }

    /**
     * App 流量转化接口
     * HTTP_WRITE_SUBSCRIBE
     *
     * @param body
     * @param request
     * @return
     */
    @Timed
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/di2/ish8g", "/ydn/8hj2p", "/udj/cudjf", "/cxd/sdhul", "/wtm/sdu9j", "/sgd/cud6h", "/tml/dhfap", "/hgs/cud8l", "/yfb/vun4j", "/sug/bz6ym", "/kaz/hue9k", "/bjk/n8sdb", "/udb/sbd5j", "/udy/msu34", "/sod/sa4id", "/oqj/cu2jk", "/bjv/sv3Jg", "/dsk/zf78h", "/kop/xnB2t", "/byk/h3J2B", "/bst/v2sKg", "/sjg/bnAs4", "/vb2/shKtg", "/dbv/bjkB2", "/dfg/vkjT4", "/pum/dMn3g", "/hgk/dMn3g", "/qmt/bgj2G", "/dwg/vlsdx", "/dfr/lksjt", "/i3k/Njk3k"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String appTransfer(@RequestBody String body, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        JSONObject params = JSONObject.parseObject(desUtil.decode(body));
        try {
            params.put("ip", IpUtil.getIpAddr(request));
            String userAgent = request.getHeader("user-agent");
            params.put("userAgent", userAgent);
            appTranService.sendAppTrans(params);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("string", "success");
            return ResponseInfoUtil.success(JSON.toJSONString(jsonObject), desUtil);
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
