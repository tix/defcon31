package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.LogConstant;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.TagsOptionVO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * @author Charles
 * @date 2019/3/1
 * @description :
 */
@RestController
@RequestMapping("/config/offer")
@Slf4j
public class OfferConfigController {

    @Autowired
    private IOfferService offerService;

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @PostMapping(value = "/save")
    public ResponseInfo save(@RequestBody JSONObject jsonObject) {
        log.info("{} [SAVE_OFFER_SERVICE] [DATA:{}]", LogConstant.ZOO,jsonObject.toJSONString());
        if (jsonObject != null) {
            JSONObject tags = jsonObject.getJSONObject(ZooConstant.TAGS);
            TagsOptionVO tagsOptionVO = new TagsOptionVO();
            if (tags != null) {
                List<OptionVO> stack = JSONObject.parseArray(tags.getJSONArray(ZooConstant.TAG_STACK).toJSONString(), OptionVO.class);
                List<OptionVO> group = JSONObject.parseArray(tags.getJSONArray(ZooConstant.TAG_GROUP).toJSONString(), OptionVO.class);
                List<OptionVO> others = JSONObject.parseArray(tags.getJSONArray(ZooConstant.TAG_OTHERS).toJSONString(), OptionVO.class);
                tagsOptionVO.setStack(stack);
                tagsOptionVO.setGroup(group);
                tagsOptionVO.setOthers(others);
            }
            List<String> time = (List<String>) jsonObject.get(ZooConstant.TIME_RANGE);
            OfferModel offerModel = JSONObject.parseObject(jsonObject.toJSONString(), OfferModel.class);
            boolean updateTimeRange = time != null && time.size() > 0;
            if (updateTimeRange) {
                offerModel.setTimeRange(time.get(0) + "," + time.get(1));
            }
            String result = offerService.saveAllConfig(offerModel, tagsOptionVO);
            if(result == ZooConstant.SUCCESS){
                return ResponseInfoUtil.success();
            }else {
                return ResponseInfoUtil.error();
            }
        }
        return ResponseInfoUtil.success();
    }

    @PostMapping(value = "/multi/save")
    public ResponseInfo multiSave(@RequestBody JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {
            offerService.multiSaveOfferAutoTest(jsonArray);
            return ResponseInfoUtil.success();
        }
        return ResponseInfoUtil.error();
    }

    @PostMapping(value = "/autoTest/list")
    public ResponseInfo getAutoTestOfferList(@RequestBody JSONObject jsonObject) {
        List<String> countryList = jsonObject.getJSONArray("country").toJavaList(String.class);
        List<String> operatorList = jsonObject.getJSONArray("operator").toJavaList(String.class);
        List<String> partnerList = jsonObject.getJSONArray("partner").toJavaList(String.class);
        List<Integer> testStatusList = jsonObject.getJSONArray("testStatus").toJavaList(Integer.class);
        Integer page = jsonObject.getInteger("page");
        Integer limit = jsonObject.getInteger("limit");
        String begin = jsonObject.getString("begin");
        String end = jsonObject.getString("end");
        return ResponseInfoUtil.success(offerService.getAutoTestOfferList(countryList, operatorList, partnerList, testStatusList, page, limit, begin, end));
    }

    @RequestMapping(value = "/import/today/redis/info",method = {RequestMethod.GET})
    @ResponseBody
    public ResponseInfo importOfferRedisInfo(){
        offerService.importTodayRedis();
        return ResponseInfoUtil.success();
    }



    @GetMapping(value = "/check/id")
    public ResponseInfo checkId(@RequestParam String offerId) {
        return ResponseInfoUtil.success(offerService.checkUniqueId(offerId));
    }

    @GetMapping(value = "/check/url")
    public ResponseInfo checkUrl(@RequestParam String url) {
        return ResponseInfoUtil.success(offerService.checkUniqueUrl(url));
    }

    @GetMapping(value = "/check/name")
    public ResponseInfo checkName(@RequestParam String name) {
        return ResponseInfoUtil.success(offerService.checkUniqueName(name));
    }

    @PostMapping(value = "/find/clickId")
    public ResponseInfo findClickIdAndMsisdn(@RequestBody JSONObject jsonObject) {
        String offerId = jsonObject.getString("offerId");
        Integer num = jsonObject.getInteger("num");
        return ResponseInfoUtil.success(offerService.findClickIdAndMsisdn(offerId, num));
    }

    @PostMapping(value = "/check/partner/offer/id")
    public ResponseInfo checkPartnerOfferId(@RequestBody JSONObject jsonObject) {
        if (jsonObject != null) {
            String partner = jsonObject.getString(ZooConstant.PARTNER);
            String partnerOfferId = jsonObject.getString(ZooConstant.OFFER_ID);
            String country = jsonObject.getString(ZooConstant.COUNTRY);
            String operator = jsonObject.getString(ZooConstant.OPERATOR);
            return ResponseInfoUtil.success(offerService.checkUniquePartnerOfferId(country, operator, partner, partnerOfferId));
        }
        return ResponseInfoUtil.success();
    }


    @GetMapping(value = "/get/{id}")
    public ResponseInfo get(@PathVariable String id) {
        return ResponseInfoUtil.success(offerService.getConfigModel(id));
    }

    @PostMapping(value = "/list")
    public ResponseInfo list(@RequestParam int page, @RequestParam int limit,
                             @RequestParam(required = false) String name, @RequestParam(required = false) String country,
                             @RequestParam(required = false) String operator, @RequestParam(required = false) String partner,
                             @RequestParam(required = false) String tag, @RequestParam(required = false) String offerId,
                             @RequestParam(required = false) String partnerOfferId, @RequestParam(required = false) Integer status,
                             @RequestParam(required = false) String belong, @RequestBody(required = false) JSONObject jsonObject) {
        List<String> emailList = null;
        if (!StringUtils.isEmpty(jsonObject.getString(ZooConstant.EMAILS))) {
            JSONArray emailArr = jsonObject.getJSONArray(ZooConstant.EMAILS);
            if (emailArr.size() > 0) {
                emailList = JSONObject.parseArray(emailArr.toJSONString(), String.class);
            }
        }
        List<String> ids = null;
        if (jsonObject.getJSONArray(ZooConstant.IDS) != null) {
            ids = JSONObject.parseArray(jsonObject.getJSONArray("ids").toJSONString(), String.class);
        }
        List<String> offerNames = null;
        if (jsonObject.getJSONArray(ZooConstant.OFFER_NAMES) != null) {
            offerNames = JSONObject.parseArray(jsonObject.getJSONArray("offerNames").toJSONString(), String.class);
        }
        if (offerNames == null) {
            offerNames = new ArrayList<>();
        }
        if (!StringUtils.isEmpty(name)) {
            offerNames.add(name);
        }
        return ResponseInfoUtil.success(offerService.getPageList(page, limit, ids, offerNames, emailList, country, operator, partner, tag, offerId, partnerOfferId, status, belong));
    }

    @GetMapping(value = "/delete/{id}")
    public ResponseInfo delete(@PathVariable String id) {
        offerService.delete(id);
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/delete/testOffer/{id}")
    public ResponseInfo deleteTestOfferRedis(@PathVariable String id) {
        offerService.deleteTestOfferRedis(id);
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/partners")
    public ResponseInfo partnerOptions() {
        return ResponseInfoUtil.success(offerService.getPartnerOptions());
    }

    @GetMapping(value = "/zoo/names")
    public ResponseInfo zooOfferNameOptions(@RequestParam String query) {
        return ResponseInfoUtil.success(offerService.getZooOfferNameOptions(query));
    }

    @GetMapping(value = "/names")
    public ResponseInfo offerNameOptions(@RequestParam String query) {
        return ResponseInfoUtil.success(offerService.getOfferNameOptions(query));
    }

    @GetMapping(value = "/offerIds")
    public ResponseInfo offerIdOptions(@RequestParam String query) {
        return ResponseInfoUtil.success(offerService.getOfferIdOptions(query));
    }

    @GetMapping(value = "/partnerOfferIds")
    public ResponseInfo partnerOfferIdOptions(@RequestParam String query) {
        return ResponseInfoUtil.success(offerService.getPartnerOfferIdOptions(query));
    }

    @GetMapping(value = "/all")
    public ResponseInfo getAll(@RequestParam String query) {
        return ResponseInfoUtil.success(offerService.getAll(query));
    }

    @GetMapping(value = "/all/options")
    public ResponseInfo getAllOptions(@RequestParam(required = false) String query) {
        return ResponseInfoUtil.success(offerService.getAllOptions(query));
    }

    @GetMapping(value = "/default/duration")
    public ResponseInfo defaultDuration(@RequestParam String country, @RequestParam String operator) {
        return ResponseInfoUtil.success(offerService.getDefaultDuration(country, operator));

    }

    @PostMapping(value = "/multi/change/status/{status}")
    public ResponseInfo multiChangeStatus(@PathVariable String status, @RequestBody List<String> ids) {
        offerService.changeStatus(ids, status);
        return ResponseInfoUtil.success();

    }

    @PostMapping(value = "/multi/change/log/{status}")
    public ResponseInfo multiChangeLog(@PathVariable int status, @RequestBody List<String> ids) {
        offerService.changeLog(ids, status);
        return ResponseInfoUtil.success();

    }

    @PostMapping(value = "/getWith/country/operators")
    public ResponseInfo getWithCountryOperators(@RequestBody JSONObject jsonObject) {
        String country = jsonObject.getString("country");
        JSONArray jsonArray = jsonObject.getJSONArray("operators");
        List<String> operators = null;
        if (jsonArray != null) {
            operators = JSONObject.parseArray(jsonArray.toJSONString(), String.class);
        }
        return ResponseInfoUtil.success(offerService.getOffers(country, operators));
    }

    @GetMapping("/param/tips")
    public ResponseInfo getParamTips(@RequestParam String partner, @RequestParam(required = false) String type) {
        return ResponseInfoUtil.success(offerService.getParamTips(partner, type));
    }

    @GetMapping("/auto/update/status")
    public ResponseInfo autoUpdateOfferStatus() {
        offerService.autoUpdate();
        return ResponseInfoUtil.success();
    }

    /**
     * 指定时间开启关闭offer
     * @param retryTime
     * @return com.starp.zoo.common.ResponseInfo
     * @author Curry
     * @date 2022/11/19
     */
    @GetMapping("/auto/update/status/retry")
    public ResponseInfo autoUpdateOfferStatusDate(@RequestParam Integer retryTime) {
        offerService.autoUpdateDate(retryTime);
        return ResponseInfoUtil.success();
    }

    @GetMapping("/auto/update/offer/runtime")
    public ResponseInfo autoUpdateRunTime(){
        offerService.autoRunTime();
        return ResponseInfoUtil.success();
    }


    @GetMapping("/update/shortCode")
    public ResponseInfo updateOfferShortCode(){
        offerService.updateOfferShortCode();
        return ResponseInfoUtil.success();
    }

    /**
     * 超cap告警关闭offer
     *
     * @param request
     * @return
     */
    @GetMapping("/updateStatus")
    public ResponseInfo updateStatus(HttpServletRequest request) {
        String payChannel = request.getParameter("payChannel");
        String operator = request.getParameter("operator");
        String shortCode = request.getParameter("shortCode");
        String keyword = request.getParameter("keyword");
        offerService.updateStatus(payChannel, operator, shortCode, keyword);
        return ResponseInfoUtil.success();
    }

    @PostMapping("/countJs")
    public ResponseInfo countJs(@RequestBody JSONObject jsonObject) {
        String offer = jsonObject.getString("offerName");
        JSONArray apps = jsonObject.getJSONArray("appName");
        List<String> appName = JSONArray.parseArray(apps.toJSONString(), String.class);
        long beginTime = jsonObject.getLong("beginTime");
        long endTime = jsonObject.getLong("endTime");
        boolean distinctUser = jsonObject.getBoolean("isRemoval");
        return ResponseInfoUtil.success(offerService.countJs(offer, appName, beginTime, endTime, distinctUser));
    }


    @PostMapping("/userList")
    public ResponseInfo getUserList(@RequestBody JSONObject jsonObject) {
        String offerName = jsonObject.getString("offerName");
        String script = jsonObject.getString("script");
        long beginTime = jsonObject.getLong("begin");
        long endTime = jsonObject.getLong("end");
        boolean distinctUser = jsonObject.getBoolean("distinct");
        JSONArray apps = jsonObject.getJSONArray("appName");
        List<String> appName = JSONArray.parseArray(apps.toJSONString(), String.class);
        return ResponseInfoUtil.success(offerService.getUserList(offerName, appName, script, distinctUser, beginTime, endTime));
    }


    @GetMapping("test/get")
    public ResponseInfo getTestOffer(@RequestParam(required = false)String partnerId){
        return ResponseInfoUtil.success(offerService.getTestOffer(partnerId));
    }

    @PostMapping("test/save")
    public ResponseInfo saveTestOffer(@RequestBody JSONObject jsonObject){
        List<String> time = (List<String>) jsonObject.get(ZooConstant.TIME_RANGE);
        OfferModel offerModel = JSONObject.parseObject(jsonObject.toJSONString(), OfferModel.class);
        boolean updateTimeRange = time != null && time.size() > 0;
        if (updateTimeRange) {
            offerModel.setTimeRange(time.get(0) + "," + time.get(1));
        }
        String id = String.valueOf(UUID.randomUUID());
        Date createTime = new Date();
        offerModel.setIdentification(id);
        offerModel.setCreateTime(createTime);
        offerService.saveTestOffer(offerModel);
        return ResponseInfoUtil.success();
    }

    @GetMapping("sync/test/offer")
    public ResponseInfo syncTestOffer(){
        offerService.syncTestOffer();
        return ResponseInfoUtil.success();
    }


}
