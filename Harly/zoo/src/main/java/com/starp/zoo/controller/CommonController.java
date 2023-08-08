package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.service.CommonService;
import com.starp.zoo.service.IEpmService;
import com.starp.zoo.util.EncodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Charles
 * @date 2019/7/29
 * @description :
 */
@RestController
public class CommonController {

    @Autowired
    private IEpmService epmService;

    @Autowired
    private CommonService commonService;

    @GetMapping("/common/home/channel")
    public ResponseInfo getChannelOneMonthRevenue(){
        return ResponseInfoUtil.success(epmService.getOneMonthRevenue());
    }

    @PostMapping("/common/encode")
    public ResponseInfo encode(@RequestBody JSONObject json) throws Exception {
        return ResponseInfoUtil.success(EncodeUtil.encode(json.toJSONString()));
    }

    @GetMapping("/save/url")
    public ResponseInfo saveUrl(HttpServletRequest request, @RequestParam String url) {
        commonService.saveUrl(request, url);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/check/url")
    public ResponseInfo checkUrl(HttpServletRequest request) {
        return commonService.checkUrl(request);
    }

}
