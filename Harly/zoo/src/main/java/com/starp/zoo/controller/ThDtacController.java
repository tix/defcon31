package com.starp.zoo.controller;


import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.common.constant.RedisConstants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;

/**
 * @author covey
 */
@RestController
@RequestMapping(value = "/dtac")
public class ThDtacController {

    @RequestMapping(value = "/createSession")
    public ResponseInfo lpSession() throws IOException {
        return ResponseInfoUtil.success();
    }

    @RequestMapping(value = "/lp")
    public void lp(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("/dtac/confirm.html");
    }

    @RequestMapping(value = "/aoc/sendPin")
    public ResponseInfo sendPin(HttpSession session){
        String pin = "1234";
        session.setAttribute(RedisConstants.PIN, pin);
        return ResponseInfoUtil.success(pin + " is your OTP and it is valid for 10 Mins.");
    }

    @RequestMapping(value = "/aoc/confirmPin")
    public ResponseInfo confirmPin(HttpSession session,@RequestParam String pin){
        String sessionPin = String.valueOf(session.getAttribute(RedisConstants.PIN));
        if(!StringUtils.isEmpty(sessionPin) && sessionPin.equalsIgnoreCase(pin)){
            return ResponseInfoUtil.success(RedisConstants.SUCCESS);
        }else {
            return ResponseInfoUtil.success(RedisConstants.ERROR);
        }
    }
}
