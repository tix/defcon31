package com.starp.zoo.controller;

import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.TenJinPbModel;
import com.starp.zoo.service.ITenJinPbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TenJinPBController.
 *
 * @author magic
 * @data 2022/4/14
 */
@RestController
public class TenJinPbController {

    @Autowired
    private ITenJinPbService tenJinPBService;

    /**
     * tenjin save.
     * @param appId
     * @param advertisingId
     * @param ipAddress
     * @param country
     * @param campaignName
     * @param siteId
     * @param userAgent
     * @return
     */
    @GetMapping("/tenjin/save")
    public ResponseInfo save(@RequestParam String appId, @RequestParam String advertisingId,
                             @RequestParam(required = false) String ipAddress, @RequestParam(required = false) String country,
                             @RequestParam(required = false) String campaignName, @RequestParam(required = false) String siteId,
                             @RequestParam(required = false) String userAgent) {
        if (StringUtils.isEmpty(appId)) {
            return ResponseInfoUtil.error("appid is null");
        }
        if (StringUtils.isEmpty(advertisingId)) {
            return ResponseInfoUtil.error("advertising_id is null");
        }
        TenJinPbModel model = new TenJinPbModel();
        model.setAppId(appId);
        model.setAdvertisingId(advertisingId);
        model.setIpAddress(ipAddress);
        model.setCountry(country);
        model.setCampaignName(campaignName);
        model.setSiteId(siteId);
        model.setUserAgent(userAgent);
        tenJinPBService.saveModel(model);
        return ResponseInfoUtil.success();
    }
}
