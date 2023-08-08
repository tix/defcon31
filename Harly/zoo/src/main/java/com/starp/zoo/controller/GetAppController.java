package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.service.IApplicationService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Charles
 * @date 2019/7/22
 * @description :
 */
@RestController
public class GetAppController {

    @Autowired
    private IApplicationService applicationService;

    @GetMapping("/apst")
    public ResponseInfo getAppStatus(@RequestParam String token, HttpServletResponse response) {
        if(!StringUtils.isEmpty(token)) {
            ApplicationModel app = applicationService.getAppModel(token);
            if (app != null && app.getStatus() == ZooConstant.STATUS_1) {
                return ResponseInfoUtil.success();
            }
        }
        response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        return ResponseInfoUtil.error();
    }
}
