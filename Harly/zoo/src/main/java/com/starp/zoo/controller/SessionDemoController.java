package com.starp.zoo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author Vic on 2020/5/22
 */
@Controller
public class SessionDemoController {

    @RequestMapping("/session/set")
    @ResponseBody
    public String set(HttpSession session) {
        String key = "test";
        session.setAttribute(key, new Date());
        return key;
    }

    @RequestMapping("/session/get")
    @ResponseBody
    public String get(HttpSession session) {
        String value = (String) session.getAttribute("test").toString();
        return value;
    }
}
