package com.starp.zoo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.IpUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author david
 */
@Controller
@Slf4j
public class AffiliatePostBackController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping(value = "/aff/redirect/apk")
    public String redirectSdkUrl(HttpServletRequest request){
        String url = "https://app.grassbiochem.com/LINE_Camera.apk";
        String clickId = request.getParameter("cid");
        String payout = request.getParameter("payout");
        String txid = request.getParameter("txid");
        JSONObject param = new JSONObject();
        param.put("cid",clickId);
        param.put("payout",StringUtils.isEmpty(payout)?"":payout);
        param.put("txid",StringUtils.isEmpty(txid)?"":txid);
        if(!StringUtils.isEmpty(clickId)){
            String ip = IpUtil.getIpAddr(request).split(",")[0];
            // key zoo:redirectApk:192.168.50.75, historyKey zoo:historykey:redirectApk:192.168.50.75
            String key = ZooConstant.ZOO + ZooConstant.COLON + ZooConstant.REDIRECT_APK + ZooConstant.COLON + ip;
            String historyKey = ZooConstant.ZOO + ZooConstant.COLON  + ZooConstant.HISTORYKEY + ZooConstant.COLON + ZooConstant.REDIRECT_APK +  ZooConstant.COLON + ip;
            // 若存在该IP的key,则存在历史redis当中
            String dateTime = DateUtil.formatyyyyMMddHHmmss(new Date());
            String hashKey = ZooConstant.AFF_REDIRECT_APK_CLICKID + ZooConstant.COLON + dateTime;
            stringRedisTemplate.opsForHash().put(historyKey,hashKey, JSON.toJSONString(param));
            stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(param));
            // 实时key存一天
            stringRedisTemplate.expire(key,24, TimeUnit.HOURS);
            // historykey存两天
            stringRedisTemplate.expire(hashKey,2,TimeUnit.DAYS);
            return "redirect:"+url;
        }else {
            return "haven't clickId ,can't redirect ";
        }

    }

}
