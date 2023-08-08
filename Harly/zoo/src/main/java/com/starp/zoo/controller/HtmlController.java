package com.starp.zoo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.HtmlInfoModel;
import com.starp.zoo.service.IHtmlService;
import com.starp.zoo.util.EncodeUtil;
import com.starp.zoo.util.StringEncodeUtil2;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/5/16.
 */
@Controller
public class HtmlController {

    @Resource
    private IHtmlService htmlService;

    @Value("${s3.html_path}")
    private String htmlPath;

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = {"/app/status/update", "/ast/ulod", "/F5tq/7Dg3"}, method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseInfo saveHtml(@RequestBody Map<String, String> params,
                                 HttpServletRequest request) throws Exception {
        String jsonStr = EncodeUtil.decode(params.get("values"));
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        HtmlInfoModel htmlInfoModel = new HtmlInfoModel();
        htmlInfoModel.setAppId(StringUtils.trimWhitespace(jsonObject.getString("appid")));
        htmlInfoModel.setOfferId(StringUtils.trimWhitespace(jsonObject.getString("offerid")));
        htmlInfoModel.setOriginUrl(jsonObject.getString("originUrl"));
        htmlInfoModel.setUserId(jsonObject.getString("userid"));
        htmlInfoModel.setSource(jsonObject.getString("source"));
        if (StringUtils.isEmpty(htmlInfoModel.getAppId()) || StringUtils.isEmpty(htmlInfoModel.getOfferId())) {
            return ResponseInfoUtil.wrong(null);
        }
        htmlService.saveModel(htmlInfoModel);
        return ResponseInfoUtil.success();
    }

    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/app/status/update/ec2", method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseInfo saveHtmlEncode2(@RequestBody Map<String, String> params,
                                        HttpServletRequest request) throws Exception {
        String jsonStr = StringEncodeUtil2.decodeServer(params.get("values"));
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        HtmlInfoModel htmlInfoModel = new HtmlInfoModel();
        htmlInfoModel.setAppId(StringUtils.trimWhitespace(jsonObject.getString("appid")));
        htmlInfoModel.setOfferId(StringUtils.trimWhitespace(jsonObject.getString("offerid")));
        htmlInfoModel.setOriginUrl(jsonObject.getString("originUrl"));
        htmlInfoModel.setUserId(jsonObject.getString("userid"));
        htmlInfoModel.setSource(jsonObject.getString("source"));
        if (StringUtils.isEmpty(htmlInfoModel.getAppId()) || StringUtils.isEmpty(htmlInfoModel.getOfferId())) {
            return ResponseInfoUtil.wrong(null);
        }
        htmlService.saveModel(htmlInfoModel);
        return ResponseInfoUtil.success();
    }

    @RequestMapping(value = "/html/list/get", method = RequestMethod.GET)
    @ResponseBody
    public JSONPObject getHtmlList(@RequestParam String appId, @RequestParam String offerId,
                                   @RequestParam Integer draw, @RequestParam Integer start, @RequestParam Integer length,
                                   @RequestParam String callback, HttpServletRequest request) throws Exception {
        return new JSONPObject(callback, htmlService.getHtmlInfoList(appId, offerId, draw, start, length));
    }

    /**
     * 初始化
     *
     * @param request
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/page/init", method = RequestMethod.POST)
    @ResponseBody
    public ResponseInfo initHtml(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws Exception {
        Integer page = jsonObject.getInteger("page");
        Integer limit = jsonObject.getInteger("limit");
        PageVO pageVO = htmlService.findAll(page, limit);
        if (pageVO == null) {
            return ResponseInfoUtil.error();
        } else {
            return ResponseInfoUtil.success(pageVO);
        }
    }

    @RequestMapping(value = "/page/params", method = RequestMethod.POST)
    @ResponseBody
    public ResponseInfo findByParams(@RequestBody JSONObject jsonObject) throws Exception {
        String appName = jsonObject.getString("appName");
        String offerName = jsonObject.getString("offerName");
        String userId = jsonObject.getString("userId");
        String country = jsonObject.getString("country");
        Integer page = jsonObject.getInteger("page");
        Integer limit = jsonObject.getInteger("limit");
        String datetime = jsonObject.getString("datetime");
        PageVO htmlList = htmlService.getHtmlList(appName, offerName, userId, country, page, limit, datetime);
        if (htmlList == null) {
            return ResponseInfoUtil.error();
        } else {
            return ResponseInfoUtil.success(htmlList);
        }
    }

    @RequestMapping(value = "/page/initAppName", method = RequestMethod.GET)
    @ResponseBody
    public ResponseInfo initAppName() throws Exception {
        List<ApplicationModel> applicationModels = htmlService.initAppName();
        return ResponseInfoUtil.success(applicationModels);
    }

    @RequestMapping(value = "/page/initOfferName", method = RequestMethod.GET)
    @ResponseBody
    public ResponseInfo initOfferName() throws Exception {
        List<OptionVO> offerNames = htmlService.initOfferName();
        return ResponseInfoUtil.success(offerNames);
    }

    @RequestMapping(value = "/page/initCountry", method = RequestMethod.GET)
    @ResponseBody
    public ResponseInfo initCountry() throws Exception {
        List<OptionVO> country = htmlService.initCountry();
        return ResponseInfoUtil.success(country);
    }


    @RequestMapping(value = "/page/show", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public JSONPObject showhtml(HttpServletRequest request, @RequestParam String callback) {
        String id = request.getParameter("id");
        HtmlInfoModel htmlInfoModel = htmlService.getHtmlInfo(id);
        return new JSONPObject(callback, htmlInfoModel);
    }


}
