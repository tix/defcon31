package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.config.AndroidGroupGetAesConfig;
import com.starp.zoo.config.AndroidGroupGetDesConfig;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.UserMobileInfoModel;
import com.starp.zoo.repo.zoo.UserMobileInfoRepo;
import com.starp.zoo.util.AesUtil;
import com.starp.zoo.util.DesUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Charles
 * @date 2019/4/17
 * @description :
 */
@RestController
public class UserMobileController {

    @Autowired
    private UserMobileInfoRepo userMobileInfoRepo;

    /**
     * SEND_DEVICEID_MSISDN
     *
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = {"/vcx/xx088","/cv3/hjg1s","/92s/7v1ga","/cv4/9702z", "/cbf/dgdfg", "/6jd/fgh87", "/k77/g6jzs","/gfd/768jz","/sdh/78njz", "/ysn/snziu", "/twg/dyu9l", "/sg2/d6jdn", "/ks2/fdi5h","/jh8/v854x","/87g/jy354","/0gf/pkhr1", "/dcu/su8d4", "/mus/sh76g", "/nxj/weu24", "/swj/xcdid","/s89/1qwrz", "/cgd/oj55f", "/ysw/cdbyj", "/ndh/su87d", "/dza/cdn3g","/15o/4567e","/0vc/zs35y", "/umv/iof7l", "/xow/cdj4l", "/svd/p4sub", "/gsi/bzjk9", "/bfs/jkas1", "/cxz/cdg3z", "/sdu/gbd68", "/pqn/bgy37", "/ioq/xu3l7", "/bnf/fKs3n", "/sdi/fd3nj", "/bhj/BGT2j", "/xkz/gjt9m", "/tvn/rj24L", "/vgH/gvj3f", "/mid/Hjk2V", "/vkg/ghRt9", "/msg/ktjJm", "/vGd/gbyUt", "/csk/lgjk2", "/fxy/rchle", "/mcs/gksgb", "/wjt/gmxcn", "/onl/bj3Js"})
    public ResponseInfo saveMobile(@RequestBody(required = false) String body, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String des = desUtil.decode(body);
        JSONObject jsonObject = JSON.parseObject(des);
        String deviceId = jsonObject.getString("deviceId");
        String mobile = jsonObject.getString("mobile");
        // device id 大于 15 位时 mobile只取前15位
        if (!StringUtils.isEmpty(deviceId) && deviceId.length() > NumberEnum.FIFTEEN.getNum()) {
            deviceId = deviceId.substring(0, 15);
        }
        UserMobileInfoModel model = userMobileInfoRepo.findFirstByDeviceIdOrderByCreateTimeDesc(deviceId);
        if (model != null) {
            userMobileInfoRepo.updateDeviceId(model.getIdentification(), deviceId, mobile);
        } else {
            // 处理电话号码
            UserMobileInfoModel userMobileInfo = new UserMobileInfoModel();
            userMobileInfo.setDeviceId(deviceId);
            userMobileInfo.setMobile(mobile);
            userMobileInfoRepo.save(userMobileInfo);
        }
        return ResponseInfoUtil.success();
    }

    /**
     * new SEND_DEVICEID_MSISDN
     * @param body
     * @param request
     * @return byte[]
     * @author Curry
     * @date 2022/11/1
     */
    @RequestMapping({"/csend", "/gsemsi", "/cable", "/gaccess", "/cacquire", "/gadapt", "/cadmit", "/gadvocate", "/cagent", "/galive",
            "/camazing", "/ganniversary", "/csanywhere", "/gappointment", "/carm", "/cartistic", "/gassist", "/cattack", "/cauto", "/cbad",
            "/cbarrier", "/gbean", "/gbehavior", "/gcable", "/ccapacity", "/ccash"})
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING"})
    public byte[] saveMobileNew(@RequestBody(required = false) byte[] body, HttpServletRequest request) throws Exception {
        AesUtil desUtil = AndroidGroupGetAesConfig.getAes(request);
        int encodeType = 1;
        if (request.getRequestURI().indexOf(ZooConstant.SPLIT_C) == -1) {
            encodeType = 2;
        }
        String des = new String(desUtil.decode(body, encodeType));
        JSONObject jsonObject = JSON.parseObject(des);
        String deviceId = jsonObject.getString("deviceId");
        String mobile = jsonObject.getString("mobile");
        // device id 大于 15 位时 mobile只取前15位
        if (!StringUtils.isEmpty(deviceId) && deviceId.length() > NumberEnum.FIFTEEN.getNum()) {
            deviceId = deviceId.substring(0, 15);
        }
        UserMobileInfoModel model = userMobileInfoRepo.findFirstByDeviceIdOrderByCreateTimeDesc(deviceId);
        if (model != null) {
            userMobileInfoRepo.updateDeviceId(model.getIdentification(), deviceId, mobile);
        } else {
            // 处理电话号码
            UserMobileInfoModel userMobileInfo = new UserMobileInfoModel();
            userMobileInfo.setDeviceId(deviceId);
            userMobileInfo.setMobile(mobile);
            userMobileInfoRepo.save(userMobileInfo);
        }
        return ResponseInfoUtil.successByte("ok", desUtil, encodeType);
    }

    /**
     * GET_MSISDN
     * GET DIVICEID
     *
     * @param body
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = {"/das/7876x","/v90/bvkco","/vc4/5640z","/fdc/bn143", "/vk8/gjf34", "/uih/54hd3", "/gf3/gfd37","/s1g/657gg","/usd/fhuaz", "/tdj/so9zy", "/su9/qu4mj", "/ydm/uxn4e","/s5z/aw45s","/1jk/dt574", "/8gg/4hsd6", "/yun/cbd4h", "/dhf/uu3fd", "/sah/xnsy4", "/anx/suh3u", "/xsu/7ycdh","/456/48fdq", "/ydn/fdui5", "/ydn/dfubx", "/duz/cby3a", "/scn/xsn2j","/fds/157ew","/dsf/ujh77","/vxc/fd023", "/yfn/oid5k", "/udn/dicn3", "/und/jo2fs", "/cuo/3si89", "/xnj/njks7", "/dsz/hjgf4", "/cdu/52jdj", "/zug/aho2n", "/uib/bv97a", "/y2j/kHJj2", "/ebj/bj4hx", "/xjb/vjg2T", "/vcn/ds2AfS", "/gkx/bks7T", "/tls/rl6sN", "/NI2/fdk2b", "/qtn/gjTkn", "/aka/xjgRt", "/lxs/bngth", "/bvk/b2ks3", "/giu/vjfbn", "/gks/g3ksRt", "/h8L/BJK8c"})
    public String getMobile(@RequestBody(required = false) String body, HttpServletRequest request) throws Exception {
        DesUtil desUtil = AndroidGroupGetDesConfig.getDes(request);
        String deviceid = desUtil.decode(body);
        UserMobileInfoModel userMobileInfoModel = null;
        if (!StringUtils.isEmpty(deviceid)) {
            userMobileInfoModel = userMobileInfoRepo.findFirstByDeviceIdOrderByCreateTimeDesc(deviceid);
        }
        String result = JSON.toJSONString(userMobileInfoModel);
        return ResponseInfoUtil.success(result, desUtil);
    }

    /**
     * NEW GET_MSISDN
     * @param body
     * @param request
     * @return byte[]
     * @author Curry
     * @date 2022/11/1
     */
    @RequestMapping({"/gcall", "/cmsi", "/cability", "/gaccept", "/cacknowledge", "/gad","/cadmission", "/gadviser", "/cagenda", "/galcohol",
            "/calways", "/ganimal", "/csanyway", "/gappoint", "/carise", "/cartist", "/gassignment", "/cattach", "/cauthority", "/cbackground",
            "/cbarrel", "/gbeach", "/gbeginning", "/gcabinet", "/ccapable", "/ccase"})
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING"})
    public byte[] getMobileNew(@RequestBody(required = false) byte[] body, HttpServletRequest request) throws Exception {
        AesUtil desUtil = AndroidGroupGetAesConfig.getAes(request);
        int encodeType = 1;
        if (request.getRequestURI().indexOf(ZooConstant.SPLIT_C) == -1) {
            encodeType = 2;
        }
        String deviceId = new String(desUtil.decode(body, encodeType));
        UserMobileInfoModel userMobileInfoModel = null;
        if (!StringUtils.isEmpty(deviceId)) {
            userMobileInfoModel = userMobileInfoRepo.findFirstByDeviceIdOrderByCreateTimeDesc(deviceId);
        }
        String result = JSON.toJSONString(userMobileInfoModel);
        return ResponseInfoUtil.successByte(result, desUtil, encodeType);
    }
}
