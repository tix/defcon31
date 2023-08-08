package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.AffiliateModel;
import com.starp.zoo.entity.zoo.TiktokCallbackModel;
import com.starp.zoo.repo.zoo.TiktokCallbackRepo;
import com.starp.zoo.service.IAffiliateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@RestController
@RequestMapping("/tiktok")
public class TiktokConfigController {

    @Autowired
    TiktokCallbackRepo tiktokCallbackRepo;

    @GetMapping("callback")
    public ResponseInfo list(HttpServletRequest request, HttpServletResponse response){
        TiktokCallbackModel tiktokCallbackModel = new TiktokCallbackModel();
        tiktokCallbackModel.setAppId(request.getParameter("app_id"));
        tiktokCallbackModel.setAuthCode(request.getParameter("auth_code"));
        tiktokCallbackModel.setAdvertiserId(request.getParameter("advertiser_id"));
        tiktokCallbackModel.setExtra(request.getParameter("extra"));
        tiktokCallbackRepo.save(tiktokCallbackModel);
        return ResponseInfoUtil.success();
    }
}
