package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.config.AndroidGroupGetAesConfig;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.SubscribeModel;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.service.IAppTranService;
import com.starp.zoo.service.IAppUserEventService;
import com.starp.zoo.service.ISubCountService;
import com.starp.zoo.service.ISubscribeService;
import com.starp.zoo.service.ITransformService;
import com.starp.zoo.util.AesUtil;
import com.starp.zoo.util.DesUtil;
import com.starp.zoo.util.IpUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Charles
 * @date 2019/8/9
 * @description :
 */
@Controller
public class AndroidGroupWriteTransController {

    @Autowired
    private ISubscribeService subscribeService;

    @Autowired
    private ISubCountService subCountService;

    @Autowired
    private ITransformService transformService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IAppUserEventService appUserEventService;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private IAppTranService appTranService;

    /**
     * App 流量转化接口
     *
     * @param jsonObject
     * @param request
     * @return
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/kv/0xMgu"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResponseInfo appTransfer(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String encodeStr = jsonObject.getString(ZooConstant.VALUE);
        JSONObject params = JSONObject.parseObject(desUtil.decode(encodeStr));
        String appId = params.getString("appId");
        String offerId = params.getString("offerId");
        String userId = params.getString("userId");
        String status = params.getString("status");
        String packageName = params.getString("packageName");
        String userAgent = request.getHeader("user-agent");
        String ip = IpUtil.getIpAddr(request);
        SubscribeModel subscribeModel = new SubscribeModel();
        subscribeModel.setIp(ip);
        subscribeModel.setOfferId(offerId);
        subscribeModel.setUserId(userId);
        subscribeModel.setStatus(status);
        subscribeModel.setType(ZooConstant.APP);
        subscribeModel.setUserAgent(userAgent);
        subscribeModel.setPackageName(packageName);
        subscribeService.save(subscribeModel);
        //更新转化总数
        if (!StringUtils.isEmpty(status) && Integer.parseInt(status) == 1) {
            transformService.updateSubCount(appId, offerId);
            subCountService.incrAppTransCount(offerId, appId);
        }
        return ResponseInfoUtil.success();
    }

    /**
     * App 流量转化接口
     * 利刃_WRITE_SUBSCRIBE
     *
     * @param value
     * @param request
     * @return
     * @throws Exception
     */
    @Timed
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/dsz/45s67","/v79/ppp31","/464/fgdfb","/0bv/p07xq", "/45h/fd234","/90a/432kj", "/ng3/5hvsw","/ypo/op09a","/mxi/sji2o", "/nxh/sduhi", "/wij/shd3j", "/op6/djk3s", "/win/cbd6h","/fsd/gf575","/424/fsdgs","/ng2/mcd35", "/gyd/dfhu2", "/tys/xud8h", "/yud/cdi8s", "/dbf/xcnbd","/y84/f64rq", "/yfk/fuiod", "/urn/fidn3", "/diq/ahduz", "/xsn/dfuna","/78w/18gfd","/ooo/sss88","/gdf/h76w1", "/kdj/op5fi", "/cyd/dfj5k", "/sud/k2bum", "/l2h/ju2nk", "/qne/njksd", "/uye/cjudk", "/cds/sdhyu", "/udh/dfs23", "/oue/73qho", "/vbc/kNs2t", "/fdn/xbb2j", "/udb/B2BJ0", "/vgj/ysIg9", "/2og/vbgS3", "/2nb/xbh4M", "/REb/vbhH2", "/vjs/lkhTj", "/kgb/xRs3j", "/bmv/tjhgn", "/vgb/aJbgs", "/asn/bvnGs", "/pkv/klbmr", "/cvm/hjk8b"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String appTransfer(@RequestBody String value, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(desUtil.decode(value));
            params.put("ip", IpUtil.getIpAddr(request));
            String userAgent = request.getHeader("user-agent");
            params.put("userAgent", userAgent);
        } catch (Exception e) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsg(errorMessage, desUtil);
        }
        appTranService.sendAppTrans(params);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("string", "success");
        return ResponseInfoUtil.success(JSON.toJSONString(jsonObject), desUtil);
    }

    /**
     * new 利刃_WRITE_SUBSCRIBE
     * @param value
     * @param request
     * @return byte[]
     * @author Curry
     * @date 2022/11/1
     */
    @Timed
    @SuppressFBWarnings({"RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT", "DM_DEFAULT_ENCODING"})
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping({"/cwrite", "/gmake", "/cabove", "/gaccomplish", "/caction", "/gadditional", "/cadult", "/gafford", "/cagree", "/galmost",
            "/camount", "/ganother", "/csapparent", "/gappropriate", "/caround", "/casleep", "/gassociate", "/cattention", "/cavoid", "/cbake",
            "/cbasic", "/gbeautiful", "/gbelief", "/gcall", "/ccapture", "/ccatch"})
    @ResponseBody
    public byte[] appTransferNew(@RequestBody byte[] value, HttpServletRequest request) throws Exception {
        AesUtil desUtil = AndroidGroupGetAesConfig.getAes(request);
        int encodeType = 1;
        if (request.getRequestURI().indexOf(ZooConstant.SPLIT_C) == -1) {
            encodeType = 2;
        }
        JSONObject params = null;
        try {
            params = JSONObject.parseObject(new String(desUtil.decode(value, encodeType)));
            params.put("ip", IpUtil.getIpAddr(request));
            String userAgent = request.getHeader("user-agent");
            params.put("userAgent", userAgent);
        } catch (Exception e) {
            String errorMessage = formatErrorMessage(ResponseInfoEnum.DESC_DECODE_ERROR.getCode(), ResponseInfoEnum.DESC_DECODE_ERROR.getMsg());
            return ResponseInfoUtil.errorMsgByte(errorMessage, desUtil, encodeType);
        }
        appTranService.sendAppTrans(params);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("string", "success");
        return ResponseInfoUtil.successByte(JSON.toJSONString(jsonObject), desUtil, encodeType);
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
