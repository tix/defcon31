package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.service.IAndroidMncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


/**
 * @author david
 */
@RestController
@RequestMapping("/mnc")
public class AndroidMncController {

    @Autowired
    private IAndroidMncService mncService;

    @GetMapping(value = "/fetchlist")
    public ResponseInfo fetchList(@RequestParam(required = false) String country, @RequestParam(required = false) String type, @RequestParam(required = false) String mnc) {
        return ResponseInfoUtil.success(mncService.findMncList(country, type, mnc));
    }

    @PostMapping("/saveMnc")
    public ResponseInfo saveMncNew(@RequestBody JSONObject jsonObject) {
        String country = jsonObject.getString("country");
        String mnc = jsonObject.getString("mnc");
        String type = jsonObject.getString("type");
        String regex = jsonObject.getString("regex");
        String areaCode = jsonObject.getString("areaCode");
        String url = jsonObject.getString("url");
        String identification = jsonObject.getString("identification");
        String oldMnc = jsonObject.getString("oldMnc");
        mncService.saveMnc(country, mnc, type, regex, areaCode, url, identification, oldMnc);
        return ResponseInfoUtil.success();
    }


    @GetMapping(value = "/delete")
    public ResponseInfo delete(HttpServletRequest request) {
        mncService.delete(request.getParameter("id"), request.getParameter("type"), request.getParameter("mnc"));
        return ResponseInfoUtil.success();
    }


}
