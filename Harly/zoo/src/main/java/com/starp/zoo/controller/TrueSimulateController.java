package com.starp.zoo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author david
 */

@Controller
@RequestMapping(value = "/true")
@Slf4j
public class TrueSimulateController {

    @Value("${recaptcha.checkTokenUrl}")
    public String checkRecaptchaUrl;

    @Value("${recaptcha.secretV2}")
    public String secretV2;

    @Value("${recaptcha.secretV3}")
    public String secretV3;

    @GetMapping(value = "rechaptchaV2/index")
    public String redirectV2(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession){
        return "true/TrueRecaptchaV2.html" ;
    }

    @GetMapping(value = "rechaptchaV3/index")
    public String redirectV3(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession){
        return "true/TrueRecaptchaV3.html" ;
    }

    @RequestMapping(value = "webapi",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String checkV2(HttpSession httpSession,@RequestBody(required = false) String uuid,HttpServletRequest request){
        String redirectUrl = "";
        String carryToken = uuid.substring(uuid.indexOf("<uuid>")+6, uuid.indexOf("</uuid>"));
        String referer = request.getHeader("referer");
        String response = "";
        String secret = "";
        boolean verifySuccess = false;
        // 验证google
        if(referer.indexOf(Constants.TRUE2_INDEX)> 0 || referer.indexOf(Constants.TRUE3_INDEX) >0 ){
           if(referer.indexOf(Constants.TRUE2_INDEX)> 0){
               response = carryToken;
               secret = secretV2;
           }else if(referer.indexOf(Constants.TRUE3_INDEX)> 0){
               response = carryToken;
               secret = secretV3;
           }
            Map<String,String> checkParam = new HashMap<>(1);
            checkParam.put("response",response);
            checkParam.put("secret",secret);
            String result = HttpUtil.doPost(checkRecaptchaUrl, checkParam);
            JSONObject resultJson = JSON.parseObject(result);
            log.info("recaptcha result:{}", JSON.toJSONString(result));
            if(resultJson!=null && resultJson.getBoolean(Constants.SUCCESS)){
                verifySuccess = true;
            }
            if(verifySuccess){
                httpSession.setAttribute(Constants.GOOGLE_TOKEN, carryToken);
                String tokenId = UUID.randomUUID().toString();
                httpSession.setAttribute(Constants.TOKEN,tokenId);
                redirectUrl = "<UpdateMobileResponse><result status=\"OK\" >" +
                        "<reason-code>0</reason-code><message>Operation Successful</message></result><token-id>"+tokenId+"</token-id></UpdateMobileResponse>";
            }else {
                redirectUrl = "/true/error.html";
            }
        } else if(referer.indexOf(Constants.RECHECK)> 0){
            verifySuccess = verifyValue(carryToken,httpSession);
            if(verifySuccess){
                String tokenId = UUID.randomUUID().toString();
                httpSession.setAttribute(Constants.TOKEN,tokenId);
                redirectUrl = "<UpdateMobileResponse><result status=\"OK\" >" +
                        "<reason-code>0</reason-code><message>Operation Successful</message></result><token-id>"+tokenId+"</token-id></UpdateMobileResponse>";
            }
        }else if(referer.indexOf(Constants.FRONT)> 0){
                redirectUrl = "<UpdateMobileResponse><result status=\"OK\" ><reason-code>0</reason-code><message>Operation Successful</message></result><token-id>ca46919350e380932bdfcb4fd7e5d3dabbd2f07468f10502e3e61996c29cee0917ceddda51dd921e183ceefe737ffd0f8c7ae48896285b63700787c4acb7a710</token-id></UpdateMobileResponse>";
        }
        return redirectUrl;
    }

    private boolean verifyValue(String carryToken, HttpSession httpSession) {
        String saveCarryToken = "";
        if(!StringUtils.isEmpty(carryToken)){
            // 大于16位的为google token
            if(carryToken.length() > Constants.FIFTY){
                if(httpSession.getAttribute(Constants.GOOGLE_TOKEN)!=null){
                    saveCarryToken = httpSession.getAttribute(Constants.GOOGLE_TOKEN).toString();
                }
            }else {
                if(httpSession.getAttribute(Constants.TOKEN)!=null){
                    saveCarryToken = httpSession.getAttribute(Constants.TOKEN).toString();
                }
            }
        }
        boolean verify = !StringUtils.isEmpty(carryToken) && !StringUtils.isEmpty(saveCarryToken) && carryToken.equalsIgnoreCase(saveCarryToken);
        return verify;
    }

    @GetMapping(value = "checkbox_aoc")
    public String checkBox(HttpServletRequest request,HttpSession httpSession){
        String tokenId = request.getParameter("ttoken");
        String googleToken = request.getParameter("token");
        String saveTokenId = "";
        String saveGoogleTokenId = "";
        if(httpSession.getAttribute(Constants.TOKEN)!=null){
            saveTokenId = httpSession.getAttribute(Constants.TOKEN).toString();
        }
        if(httpSession.getAttribute(Constants.GOOGLE_TOKEN)!=null){
            saveGoogleTokenId = httpSession.getAttribute(Constants.GOOGLE_TOKEN).toString();
        }
        log.info("ttoken:{},token:{},saveTokenId:{},saveGoogleTokenId:{}",tokenId,googleToken,saveTokenId,saveGoogleTokenId);
        boolean verify = !StringUtils.isEmpty(tokenId) && !StringUtils.isEmpty(googleToken) && tokenId.equalsIgnoreCase(saveTokenId) && googleToken.equalsIgnoreCase(saveGoogleTokenId);
        httpSession.setAttribute(Constants.GOOGLE_TOKEN,googleToken);
        if(verify){
            return "redirect:/true/recheck";
        }else {
            return "redirect:/true/error.html";
        }
    }

    @GetMapping(value = "recheck")
    public String recheck(HttpSession httpSession,Model model){
        String googleToken = "";
        if(httpSession.getAttribute(Constants.GOOGLE_TOKEN)!=null){
            googleToken = httpSession.getAttribute(Constants.GOOGLE_TOKEN).toString();
        }
        model.addAttribute(Constants.GOOGLE_TOKEN, googleToken);
        return "true/recheck.html";
    }

    @GetMapping(value = "recheck2")
    public String recheck2(HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
        String tokenId = request.getParameter("ttoken");
        String googleToken = request.getParameter("token");
        String saveTokenId = "";
        String saveGoogleTokenId = "";
        if(httpSession.getAttribute(Constants.TOKEN)!=null){
            saveTokenId = httpSession.getAttribute(Constants.TOKEN).toString();
        }
        if(httpSession.getAttribute(Constants.GOOGLE_TOKEN)!=null){
            saveGoogleTokenId = httpSession.getAttribute(Constants.GOOGLE_TOKEN).toString();
        }
        // 判断token是否相等
        String url = "";
        boolean verify = !StringUtils.isEmpty(googleToken) && !StringUtils.isEmpty(saveTokenId) && tokenId.equalsIgnoreCase(saveTokenId) && googleToken.equalsIgnoreCase(saveGoogleTokenId);
        if(verify){
            url = "true/check";
        }else {
            url = "true/error";
        }
        return  "redirect:/"+url;
    }

    @GetMapping(value = "check")
    public String check(HttpServletRequest request,HttpSession httpSession,Model model){
        String aocId = UUID.randomUUID().toString();
        httpSession.setAttribute("aocId",aocId);
        model.addAttribute("aocId",aocId);
        return "redirect:/true/aoc?aoc="+aocId;
    }

    @GetMapping(value = "aoc")
    public String aoc(HttpServletRequest request,HttpServletResponse response,HttpSession httpSession,Model model){
        String aocId = request.getParameter("aoc");
        String saveAocId = "";
        if( httpSession.getAttribute(Constants.AOC_ID)!=null){
            saveAocId = httpSession.getAttribute("aocId").toString();
        }
        if(!StringUtils.isEmpty(aocId) && !StringUtils.isEmpty(saveAocId) && aocId.equalsIgnoreCase(saveAocId)){
            return "true/aoc.html";
        }else {
            return "error.html";
        }
    }

    @PostMapping(value = "api_aoc")
    @ResponseBody
    public String apiAoc(HttpServletRequest request,HttpServletResponse response,HttpSession httpSession){
        String accessToken = UUID.randomUUID().toString();
        httpSession.setAttribute("accessToken",accessToken);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "200");
        jsonObject.put("statusText", "Success");
        jsonObject.put("access_token", "accessToken");
        jsonObject.put("cp_trans_id", "1590314809374506");
        jsonObject.put("service_id", "7115870703");
        jsonObject.put("css_keyword", "H");
        jsonObject.put("cp_id", "CP1558");
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "aoc-frontend",method = {RequestMethod.GET,RequestMethod.POST})
    public String frontAoc(HttpSession session, Model model){
        String tranId = "ca46919350e380932bdfcb4fd7e5d3dabbd2f07468f10502e3e61996c29cee0917ceddda51dd921e183ceefe737ffd0f8c7ae48896285b63700787c4acb7a710";
        session.setAttribute(Constants.TOKEN, tranId);
        session.setAttribute(Constants.TRANID, tranId);
        model.addAttribute(Constants.TRANID, tranId);
        return "true/aoc-frontend.html";
    }

    @RequestMapping(value = "front-api/api/upd-tx",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String frontApi(HttpSession session, Model model){
        return "Update successfully";
    }

    @PostMapping(value = "backend/verify")
    @ResponseBody
    public String verify(@RequestBody JSONObject paramJson, HttpServletResponse response, HttpSession session){
        String accessToken = paramJson.getString("insertion_access_token");
        String saveAccessToken = "";
        if(session.getAttribute(Constants.TOKEN)!=null){
            saveAccessToken = session.getAttribute(Constants.TRANID).toString();
        }
        if(!StringUtils.isEmpty(accessToken) && !StringUtils.isEmpty(saveAccessToken) && accessToken.equalsIgnoreCase(saveAccessToken)){
            JSONObject jsonResult = new JSONObject();
            JSONObject jsonData = new JSONObject();
            jsonResult.put("code", 200);
            jsonResult.put("description", "Success");
            jsonResult.put("display_text_en", "Thank you and please wait for SMS confirmation.");
            jsonResult.put("display_text_th", "ขอบคุณค่ะ กรุณารอ SMS ยืนยันการสมัครบริการ");
            jsonData.put("trans_id", "GPOPQQBHNL");
            jsonData.put("msisdn", "66645583627");
            jsonData.put("shortcode", "4270303");
            jsonData.put("aoc_channel", "OTP");
            jsonData.put("aoc_service_verify", "OFF");
            jsonData.put("aoc_captcha_version", null);
            jsonData.put("aoc_service_name_en", "HD WP");
            jsonData.put("aoc_service_name_th", "HD WP");
            jsonData.put("price_mt_postpaid", 9);
            jsonData.put("total_msg_per_day", 9);
            jsonData.put("cp_call_center", "020263588");
            jsonData.put("aoc_webbanner_url", null);
            jsonData.put("aoc_success_url", "https://nextportal.hlifeplus.com/callback");
            jsonData.put("aoc_failure_url", null);
            jsonResult.put("data", jsonData);
            return jsonResult.toJSONString();
        }else {
            return "";
        }
    }

    @PostMapping(value = "backend/subscribe")
    @ResponseBody
    public String subscribe(@RequestBody JSONObject params, HttpSession session){
        return "{\"code\":200,\"description\":\"Success\",\"display_text_en\":\"Thank you and please wait for SMS confirmation.\",\"display_text_th\":\"ขอบคุณค่ะ กรุณารอ SMS ยืนยันการสมัครบริการ\",\"data\":{\"trans_id\":\"GPOPQQBHNL\",\"cp_trans_id\":\"1590314809374506\",\"msisdn\":\"66645583627\",\"shortcode\":\"4270303\",\"service_id\":\"7115870703\",\"css_keyword\":\"H\"}}";
    }
}
