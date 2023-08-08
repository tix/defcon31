package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.config.AndroidGroupGetAesConfig;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.util.AesUtil;
import com.starp.zoo.util.DesUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/7/22
 * @description :
 */
@RestController
public class AndroidGroupGetAppController {

    @Autowired
    private IApplicationService applicationService;

    @PostMapping(value = {"/kv/s9kyT"})
    public ResponseInfo getAppStatus(@PathVariable(required = false) String app, @RequestBody JSONObject jsonObject, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String encodeStr = jsonObject.getString(ZooConstant.VALUE);
        JSONObject params = JSONObject.parseObject(desUtil.decode(encodeStr));
        String token = params.getString("token");
        if (!StringUtils.isEmpty(token)) {
            ApplicationModel application = applicationService.getAppModel(token);
            if (application != null && application.getStatus() == ZooConstant.STATUS_1) {
                return ResponseInfoUtil.success();
            }
        }
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return ResponseInfoUtil.error();
    }

    /**
     * GET APP STATUS
     *
     * @param value
     * @param request
     * @return
     * @throws Exception
     */
    @Timed
    @PostMapping(value = {"/9fs/834tg","/45g/7891s","/4df/0cvpa","/4gf/xch68", "/3fg/676hd", "/fud/54da2", "/h5j/3453h","/h67/98dft","/din/c86h2", "/sdh/cuu2s", "/ydb/udhsa", "/dfd/cuid3", "/gdh/dfui3","/u46/45cva","/gds/dsa56","/fp5/ds2qe","/f3f/4gs1h","/yxb/wuh2q", "/ydg/sdhui", "/amd/hj3id", "/sxs/fdh7h", "/ydc/df46j", "/tej/niodj", "/jdl/dfh6j", "/zax/sdhsa","/1ds/54few","/785/5467l","/xvz/podw1", "/yjl/duion", "/ydm/xcid2", "/ds8/njapq", "/zxp/dnk2z", "/cad/2bnj3", "/cus/aoz2j", "/ysm/xbh75", "/nkk/hui29", "/hjd/yuiw2", "h2l/jB24k", "/vkf/fkS5j", "/jvn/nsp2M", "/xnj/v8gsT", "/bvg/tsjTR", "/dbj/Bjk2k", "/sig/v8gsT", "/h2l/jB24k", "/qwe/HJkhk", "/vjs/Bgk5t", "/rns/xkf1g", "/hgv/jkbTs", "/ksg/bgkgt", "/N2K/HJj5k"})
    public String getAppStatus(@RequestBody String value, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(desUtil.decode(value));
        } catch (Exception e) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
        String token = params.getString("token");
        if (!StringUtils.isEmpty(token)) {
            ApplicationModel application = applicationService.getAppModel(token);
            if (application != null) {
                if (application.getStatus() == ZooConstant.STATUS_1) {
                    JSONObject str = new JSONObject();
                    str.put("string", "success");
                    return ResponseInfoUtil.success(JSON.toJSONString(str), desUtil);
                } else {
                    String errorMessage = formatErrorMessage(ResponseInfoEnum.APP_CLOSE.getCode(), ResponseInfoEnum.APP_CLOSE.getMsg());
                    return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
                }
            } else {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.APP_NULL.getCode(), ResponseInfoEnum.APP_NULL.getMsg());
                return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
            }
        } else {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.TOKEN_NULL.getCode(), ResponseInfoEnum.TOKEN_NULL.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
    }

    /**
     * NEW GET APP STATUS
     * @param value
     * @param request
     * @return java.lang.String
     * @author Curry
     * @date 2022/11/1
     */
    @Timed
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING"})
    @PostMapping({"/cstatus", "/gobtpac", "/cabandon", "/gacademic", "/cacid", "/gactually", "/cadmire", "/gadvise","/cagency", "/galbum",
            "/calthough", "/gangry", "/csanything", "/gapply", "/cargument", "/carticle", "/gassign", "/catmosphere", "/cauthor", "/cback",
            "/cbarely", "/gbattle", "/gbegin", "/gcabin", "/ccapability", "/ccarry"})
    public byte[] getAppStatusNew(@RequestBody byte[] value, HttpServletRequest request) throws Exception {
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
        String token = params.getString("token");
        if (!StringUtils.isEmpty(token)) {
            ApplicationModel application = applicationService.getAppModel(token);
            if (application != null) {
                if (application.getStatus() == ZooConstant.STATUS_1) {
                    JSONObject str = new JSONObject();
                    str.put("string", "success");
                    return ResponseInfoUtil.successByte(JSON.toJSONString(str), desUtil, encodeType);
                } else {
                    String errorMessage = formatErrorMessage(ResponseInfoEnum.APP_CLOSE.getCode(), ResponseInfoEnum.APP_CLOSE.getMsg());
                    return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
                }
            } else {
                String errorMessage = formatErrorMessage(ResponseInfoEnum.APP_NULL.getCode(), ResponseInfoEnum.APP_NULL.getMsg());
                return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
            }
        } else {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.TOKEN_NULL.getCode(), ResponseInfoEnum.TOKEN_NULL.getMsg());
            return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
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
