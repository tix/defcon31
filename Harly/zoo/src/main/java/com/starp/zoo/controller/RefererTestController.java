package com.starp.zoo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Vic on 2020/5/22
 */
@Controller
@RequestMapping("/referer")
public class RefererTestController {

    @RequestMapping("/test1")
    @ResponseBody
    public String test1(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
        return "test1";
    }

    @RequestMapping("/test2")
    @ResponseBody
    public String test2(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
        return "test2";
    }
}
