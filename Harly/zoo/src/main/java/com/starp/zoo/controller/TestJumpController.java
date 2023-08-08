package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.IOException;
import java.util.List;

/**
 * @author Charles
 * @date 2019/5/16
 * @description :
 */
@Controller
public class TestJumpController {

    @Autowired
    private CommonService commonService;

    @GetMapping("/test/301/1")
    public String redirectTo1(HttpServletResponse response){
        response.setStatus(301);
        response.setHeader("Location","http://sandbox.new.zoo.starpavilion-digital.com/test/302");
        return null;
    }

    @GetMapping("/test/302")
    public String redirectTo2(){
        return "redirect:" + "/test/301/2";
    }

    @GetMapping("/test/301/2")
    public String redirectTo3(HttpServletResponse response){
        response.setStatus(301);
        response.setHeader("Location","http://sandbox.new.zoo.starpavilion-digital.com/test/test3.html");
        return null;
    }

    @GetMapping("/test/302/2")
    public String redirectTo4(){
        return "redirect:" + "/test/test3.html";
    }

    @RequestMapping(value = {"/prada/{t}/{t}", "/gucci/{t}/{t}", "/hermes/{t}/{t}", "/louisv/{t}/{t}", "/chanel/{t}/{t}", "/fendi/{t}/{t}", "/coach/{t}/{t}"})
    public void domainJump(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String originalUrl = request.getRequestURL().toString();
        String queryParam = request.getQueryString();
        String redirectUrl = commonService.getRedirectUrl(originalUrl, queryParam);
        // 设置响应状态码为 302
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        // 设置重定向 URL
        response.setHeader("Location", redirectUrl);
        // 关闭连接，避免浏览器继续等待响应
        response.setHeader("Connection", "close");
        response.sendRedirect(redirectUrl);
    }

    @RequestMapping("/save/domain/baseUrl")
    @ResponseBody
    public ResponseInfo saveDomainBaseUrl(@RequestBody JSONObject jsonObject) {
        String baseDomain = jsonObject.getString(ZooConstant.BASE_DOMAIN);
        String targetUrl = jsonObject.getString(ZooConstant.TARGET_URL);
        commonService.saveBaseUrl(baseDomain, targetUrl);
        return ResponseInfoUtil.success();
    }

    @RequestMapping("/update/domain/targetUrl")
    @ResponseBody
    public ResponseInfo updateDomainTargetUrl(@RequestBody JSONObject jsonObject) {
        String baseUrl = jsonObject.getString(ZooConstant.BASE_URL);
        String targetUrl = jsonObject.getString(ZooConstant.TARGET_URL);
        commonService.updateTargetUrl(baseUrl, targetUrl);
        return ResponseInfoUtil.success();
    }

    @RequestMapping("/getBaseDomain")
    @ResponseBody
    public ResponseInfo getBaseDomain() {
        return ResponseInfoUtil.success(commonService.getBaseDomain());
    }

    @RequestMapping("/getDomainUrl")
    @ResponseBody
    public ResponseInfo getDomainUrlVoList(@RequestBody JSONObject jsonObject) {
        Integer page = jsonObject.getInteger(ZooConstant.PAGE);
        Integer limit = jsonObject.getInteger(ZooConstant.LIMIT);
        return ResponseInfoUtil.success(commonService.getDomainUrlVo(page, limit));
    }

    @RequestMapping("/multiDelete/baseUrl")
    @ResponseBody
    public ResponseInfo multiDeleteBaseUrl(@RequestBody List<String> baseUrlList) {
        commonService.multiDeleteBaseUrl(baseUrlList);
        return ResponseInfoUtil.success();
    }
}
