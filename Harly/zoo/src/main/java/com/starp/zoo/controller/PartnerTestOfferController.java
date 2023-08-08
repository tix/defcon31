package com.starp.zoo.controller;

import com.starp.zoo.service.IOfferService;
import com.starp.zoo.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author david
 */
@Controller
public class PartnerTestOfferController {

    @Autowired
    private IOfferService offerService;

    /**
     * offer 自动封装推广接口(offer redirect link) /frl?id=
     * @param partnerId 系统 offerId
     * @return
     */
    @GetMapping("/frl")
    public String redirectUrl2(@RequestParam(name = "id") String partnerId,
                               @RequestParam(name = "test", required = false) boolean isTest, HttpServletRequest request,
                               HttpServletResponse response) throws  Exception {
        String ipAddress = IpUtil.getIpAddr(request);
        String url = offerService.formatConfig(partnerId,ipAddress);
        if (StringUtils.isEmpty(url)) {
            response.sendError(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value(), HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.toString());
            return null;
        }
        return "redirect:" + url;
    }

}
