package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 *
 * @Author vic
 * @Date 18:13 2018/12/18
 * @param
 * @return
 **/
@Slf4j
@Controller
public class HealthExaminationController {
    
    @RequestMapping(value = "/health/examination", method = RequestMethod.GET)
    @ResponseBody
    public ResponseInfo getConfig(HttpServletRequest request){
        return ResponseInfoUtil.success();
    }

    @PostMapping("/za/test")
    @ResponseBody
    public ResponseInfo analysisBody(HttpServletRequest request, @RequestBody String body) throws Exception {
        log.info(body);
        return ResponseInfoUtil.success();
    }
    
    
}
