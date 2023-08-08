package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.service.IHttpLoggingService;
import com.starp.zoo.service.IProtcLogService;
import com.starp.zoo.vo.HttpLoggingVO;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import com.starp.zoo.vo.SingleStepVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author covey
 */
@RestController
public class ProtcLogController {

    @Autowired
    private IProtcLogService protcLogService;

    @Autowired
    private IHttpLoggingService httpLoggingService;

    @RequestMapping(value = "/protc/log/list")
    public ResponseInfo findLog(@RequestParam String country, @RequestParam String operator, @RequestParam String appName, @RequestParam String offerId,
                                @RequestParam(required = false) String stepId, @RequestParam(required = false) String pid,
                                @RequestParam long begin, @RequestParam long end, @RequestParam int limit, @RequestParam int page, HttpServletRequest request) {
        PageVO logs = protcLogService.findLog(country, operator, appName, offerId, stepId, pid, limit, page, begin, end);
        return ResponseInfoUtil.success(logs);
    }

    @RequestMapping(value = "/protc/log/fetchOffersWithOperators")
    public ResponseInfo fetchOffersWithOperators(HttpServletRequest request) {
        String operator = request.getParameter("operator");
        List<OptionVO> list = protcLogService.fetchOffersWithOperators(operator);
        return ResponseInfoUtil.success(list);
    }

    @PostMapping("/protc/log")
    public ResponseInfo findProtcLog(@RequestBody JSONObject jsonObject) {
        String operator = jsonObject.getString("operator");
        String appName = jsonObject.getString("appName");
        String offerId = jsonObject.getString("offerId");
        String userId = jsonObject.getString("userId");
        String stepNumber = jsonObject.getString("stepNumber");
        String pid = jsonObject.getString("pid");
        Long begin = jsonObject.getLong("begin");
        Long end = jsonObject.getLong("end");
        String stepName = jsonObject.getString("stepName");
        Integer page = jsonObject.getInteger("page");
        Integer limit = jsonObject.getInteger("limit");
        List<HttpLoggingVO> result = httpLoggingService.findFormS3(operator, appName, offerId, userId, pid, stepNumber, begin, end, stepName, page, limit);
        return ResponseInfoUtil.success(result);
    }

    @PostMapping("/protc/detail/log")
    public ResponseInfo findProtcLogDetail(@RequestBody JSONObject jsonObject) {
        String operator = jsonObject.getString("operator");
        String appName = jsonObject.getString("appName");
        String offerId = jsonObject.getString("offerId");
        String userId = jsonObject.getString("userId");
        String pid = jsonObject.getString("pid");
        String recordDate = jsonObject.getString("recordDate");
        String stepNumber = jsonObject.getString("stepNumber");
        String stepName = jsonObject.getString("stepName");
        List<SingleStepVO> detail = httpLoggingService.findDetail(operator, appName, offerId, userId, pid, recordDate, stepNumber, stepName);
        return ResponseInfoUtil.success(detail);
    }

    @GetMapping("/protc/delete/s3_logging")
    public ResponseInfo deleteLoggingFile() {
        httpLoggingService.deleteLoggingFile();
        return ResponseInfoUtil.success();
    }

}
