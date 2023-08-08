package com.starp.zoo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfo;
import com.starp.zoo.common.ResponseInfoUtil;
import com.starp.zoo.constant.LogConstant;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.service.IEpmService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.IpUtil;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * @author Charles
 * @date 2019/1/24
 * @description :
 */
@Slf4j
@RestController
public class EpmController {

    @Autowired
    private IEpmService epmService;

    /**
     * epm计算接口（每小时执行）
     */
    @RequestMapping(value = "/execute/epm/calculate", method = RequestMethod.GET)
    @ResponseBody
    public ResponseInfo executeCalculateEpm() {
        epmService.executeCalculateEpm(false, null);
        return ResponseInfoUtil.success();
    }

    /**
     * EPM补数据接口
     *
     * @param begin yyyy-MM-dd-HH
     * @param end   yyyy-MM-dd-HH
     * @return
     */
    @RequestMapping(value = "/execute/epm/retry/calculate", method = RequestMethod.GET)
    @ResponseBody
    public ResponseInfo executeRetryCalculateEpm(@RequestParam(required = false) String begin, @RequestParam(required = false) String end) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        Calendar beginCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        Calendar endCalendar;
        Calendar tempCalendar;
        if(StringUtils.isEmpty(begin) && StringUtils.isEmpty(end)){
            beginCalendar = getBeforeHourCalendar();
            endCalendar = getBeforeHourCalendar();
            tempCalendar = (Calendar) beginCalendar.clone();
        }else {
            calendar = Calendar.getInstance();
            beginCalendar = Calendar.getInstance();
            beginCalendar.setTime(sdf.parse(begin));
            beginCalendar = getHourStartCalendar(beginCalendar);
            endCalendar = Calendar.getInstance();
            endCalendar.setTime(sdf.parse(end));
            endCalendar = getHourStartCalendar(endCalendar);
            tempCalendar = (Calendar) beginCalendar.clone();

        }
        // 当前时间>= 开始时间  && 结束时间 >= 开始时间
        while (!calendar.getTime().before(beginCalendar.getTime()) &&
                !endCalendar.getTime().before(beginCalendar.getTime())) {
            log.info("{} [EPM_RETRY] [{}----{}] [date:{}]", LogConstant.ZOO, begin, end, sdf.format(tempCalendar.getTime()));
            epmService.executeCalculateEpm(true, tempCalendar);
            beginCalendar.add(Calendar.HOUR_OF_DAY, 1);
        }
        return ResponseInfoUtil.success();
    }


    private Calendar getBeforeHourCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }




    @GetMapping(value = "/import/msisdn/param")
    @ResponseBody
    public ResponseInfo importMsisdnParam() {
        epmService.importMsisdnParam();
        return ResponseInfoUtil.success();
    }


    @GetMapping(value = "/recheck/offer/epm/execute")
    public ResponseInfo recheckEpm(HttpServletRequest request){
        String ip = IpUtil.getIpAddr(request);
        log.info("execute recheck epm ,time:{},ip:{}",DateUtil.formatyyyyMMddHHmmss(new Date()), ip);
        epmService.recheckOfferEpm(ip);
        return ResponseInfoUtil.success();
    }

    @GetMapping(value = "/recheck/offer/epm/hour/execute")
    public ResponseInfo recheckOfferEpmHour(@RequestParam(required = false) String date,HttpServletRequest request){
        log.info("execute compare redis/db epm ,time:{},ip:{}",DateUtil.formatyyyyMMddHHmmss(new Date()), IpUtil.getIpAddr(request));
        return ResponseInfoUtil.success(epmService.recheckEpmHour(date).toJSONString());
    }



    /**
     * 每小时执行检查 unused offer是否到达刷新时间
     * @return
     */
    @Timed
    @GetMapping(value = "/check/unused/offer")
    @ResponseBody
    public ResponseInfo checkUnusedOffer() {
        epmService.checkUnusedOffer();
        return ResponseInfoUtil.success();
    }

    private Calendar getHourStartCalendar(Calendar calendar) {
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }


    @PostMapping(value = "/epm/resource/name/list")
    public ResponseInfo getResourceNames(@RequestBody JSONObject jsonObject) {
        List<String> country = new ArrayList<>();
        if (!StringUtils.isEmpty(jsonObject.getString(ZooConstant.COUNTRY))) {
            JSONArray array = jsonObject.getJSONArray(ZooConstant.COUNTRY);
            if (array.size() > 0) {
                for (Object o : array) {
                    country.add(o.toString());
                }
            }
        }
        List<String> partner = new ArrayList<>();
        if (!StringUtils.isEmpty(jsonObject.getString(ZooConstant.PARTNER))) {
            JSONArray array = jsonObject.getJSONArray(ZooConstant.PARTNER);
            if (array.size() > 0) {
                for (Object o : array) {
                    partner.add(o.toString());
                }
            }
        }
        String operator = jsonObject.getString("operator");
        return ResponseInfoUtil.success(epmService.getResourceNames(country, operator, partner));
    }

    @RequestMapping(value = "/epm/offer/name/list", method = RequestMethod.POST)
    public ResponseInfo getOfferNames(@RequestBody JSONObject jsonObject) {
        List<String> country = new ArrayList<>();
        if (!StringUtils.isEmpty(jsonObject.getString(ZooConstant.COUNTRY))) {
            JSONArray array = jsonObject.getJSONArray(ZooConstant.COUNTRY);
            if (array.size() > 0) {
                for (Object o : array) {
                    country.add(o.toString());
                }
            }
        }
        List<String> partner = new ArrayList<>();
        if (!StringUtils.isEmpty(jsonObject.getString(ZooConstant.PARTNER))) {
            JSONArray array = jsonObject.getJSONArray(ZooConstant.PARTNER);
            if (array.size() > 0) {
                for (Object o : array) {
                    partner.add(o.toString());
                }
            }
        }
        String operator = jsonObject.getString("operator");
        JSONArray jsonArray = jsonObject.getJSONArray("resourceName");
        List<String> resourceNames = Arrays.asList(jsonArray.toArray(new String[jsonArray.size()]));
        return ResponseInfoUtil.success(epmService.getOfferName(country, operator, partner, resourceNames));
    }

    @RequestMapping(value = "/epm/offer/system/id/list", method = RequestMethod.POST)
    public ResponseInfo getSystemIds(@RequestBody JSONObject jsonObject) {
        JSONArray jsonCountryArray = jsonObject.getJSONArray("country");
        List<String> country = Arrays.asList(jsonCountryArray.toArray(new String[jsonCountryArray.size()]));
        JSONArray jsonPartnerArray = jsonObject.getJSONArray("partner");
        List<String> partner = Arrays.asList(jsonPartnerArray.toArray(new String[jsonPartnerArray.size()]));
        String operator = jsonObject.getString("operator");
        JSONArray jsonArray = jsonObject.getJSONArray("resourceName");
        List<String> resourceNames = Arrays.asList(jsonArray.toArray(new String[jsonArray.size()]));
        return ResponseInfoUtil.success(epmService.getSystemIds(country, operator, partner, resourceNames));
    }

    @RequestMapping(value = "/epm/offer/partner/id/list", method = RequestMethod.POST)
    public ResponseInfo getPartnerIds(@RequestBody JSONObject jsonObject) {
        JSONArray jsonCountryArray = jsonObject.getJSONArray("country");
        List<String> country = Arrays.asList(jsonCountryArray.toArray(new String[jsonCountryArray.size()]));
        JSONArray jsonPartnerArray = jsonObject.getJSONArray("partner");
        List<String> partner = Arrays.asList(jsonPartnerArray.toArray(new String[jsonPartnerArray.size()]));
        String operator = jsonObject.getString("operator");
        JSONArray jsonArray = jsonObject.getJSONArray("resourceName");
        List<String> resourceNames = Arrays.asList(jsonArray.toArray(new String[jsonArray.size()]));
        return ResponseInfoUtil.success(epmService.getPartnerIds(country, operator, partner, resourceNames));
    }

    @PostMapping("/epm/testOffer/list")
    public ResponseInfo getTestOfferEpm(@RequestBody JSONArray jsonArray) {
        if (jsonArray != null && jsonArray.size() > 0) {
            List<JSONObject> offerJsonList = JSONObject.parseArray(jsonArray.toJSONString(), JSONObject.class);
            JSONObject jsonObject = epmService.getTestOfferEpm(offerJsonList);
            return ResponseInfoUtil.success(jsonObject);
        }
        return ResponseInfoUtil.error();
    }

    /**
     * epm页面查询
     * @param jsonObject
     * @param request
     * @return
     */
    @RequestMapping(value = "/epm/list", method = RequestMethod.POST)
    public ResponseInfo getEpmRecords(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        try {
            List<String> country = new ArrayList<>();
            if (!StringUtils.isEmpty(jsonObject.getString(ZooConstant.COUNTRY))) {
                JSONArray array = jsonObject.getJSONArray(ZooConstant.COUNTRY);
                if (array.size() > 0) {
                    for (Object o : array) {
                        country.add(o.toString());
                    }
                }
            }
            String belong = jsonObject.getString("belong");
            String queryType = jsonObject.getString("queryType");
            String operator = jsonObject.getString(ZooConstant.OPERATOR);
            List<String> partner = new ArrayList<>();
            if (!StringUtils.isEmpty(jsonObject.getString(ZooConstant.PARTNER))) {
                JSONArray array = jsonObject.getJSONArray(ZooConstant.PARTNER);
                if (array.size() > 0) {
                    for (Object o : array) {
                        partner.add(o.toString());
                    }
                }
            }
            Integer showType = jsonObject.getInteger("showType");
            if(showType == null){
                showType = Integer.valueOf(ZooConstant.EPM_SHOW_TYPE_OFFER);
            }
            JSONArray array = jsonObject.getJSONArray(ZooConstant.RESOURCE_NAME);
            Object[] objects = array != null ? array.toArray() : null;
            List<String> resourceNames = new ArrayList<>();
            if (objects != null && objects.length > 0) {
                for (Object object : objects) {
                    resourceNames.add(object.toString());
                }
            }
            String offerName = jsonObject.getString(ZooConstant.OFFER_NAME);
            String offerId = jsonObject.getString(ZooConstant.OFFER_ID);
            JSONArray jsonArray = jsonObject.getJSONArray(ZooConstant.TAGS);
            List<String> tags = jsonArray != null ? JSONObject.parseArray(jsonArray.toJSONString(), String.class) : null;
            if (jsonObject.getLong(ZooConstant.BEGIN) == null && jsonObject.getLong(ZooConstant.END) == null) {
                // 如果为空则返回空列表
                return ResponseInfoUtil.success();
            }
            String timezone = jsonObject.getString("timezone");
            String beginTime = jsonObject.getString(ZooConstant.BEGIN);
            String endTime = jsonObject.getString(ZooConstant.END);
            List<String> offerIds = epmService.getOfferIds(tags);
            if (offerIds == null) {
                offerIds = new ArrayList<>();
            }
            if (!StringUtils.isEmpty(offerId)) {
                offerIds.add(offerId);
            }
            Boolean showLimitPull = jsonObject.getBoolean("showLimitPull");
            return ResponseInfoUtil.success(epmService.getEpmRecord(country, operator, partner,
                    resourceNames, offerName, offerIds, beginTime, endTime, timezone, showType, showLimitPull, belong, queryType));

        }catch (Exception e){
            log.error("epm list error:{}",e.getMessage());
        }
        return null;
    }

    /**
     * 检查转化异常定时任务
     * @return
     */
    @GetMapping("/check/trans")
    public ResponseInfo checkTrans() {
        epmService.checkTrans();
        return ResponseInfoUtil.success();
    }


    @PostMapping("/epm/comparativeData")
    public ResponseInfo comparativeData(@RequestBody List<JSONObject> list, @RequestParam String begin, @RequestParam String end) {
        String offerId = epmService.comparativeData(list, begin, end);
        return ResponseInfoUtil.success(offerId);
    }


    @PostMapping(value = "/zoo/tranNumAndRevenue")
    public String findAppAnalysis(@RequestBody String params) {
        JSONObject jsonObject = JSONObject.parseObject(params);
        String country = jsonObject.getString("country");
        String startDate = jsonObject.getString("startDateZoo");
        String endDate = jsonObject.getString("endDateZoo");
        List<String> appIds = (List<String>) jsonObject.get("appId");
        List<String> operators = (List<String>) jsonObject.get("operator");
        List<String> times = (List<String>) jsonObject.get("time");
        JSONObject installNum = epmService.getTranNumAndRevenue(startDate, endDate, appIds, operators, country,times);
        return installNum.toJSONString();
    }

    /**
     * 检查是否生成EPM
     * @return
     */
    @GetMapping(value = "/check/zoo/epm/list")
    public String checkEpmList(){
        String result = epmService.checkEpmList();
        return result;
    }


    @GetMapping(value = "/epm/pay/subMo")
    public ResponseInfo importPaySubMo(@RequestParam String shortCode,@RequestParam String keyword,@RequestParam String operator,@RequestParam String partner){
        epmService.savePaySubMo(shortCode,keyword,operator,partner);
        return ResponseInfoUtil.success();
    }



    /**
     * 将affEpm 从数据库导入redis
     * @param appId
     * @param operator
     * @param time
     * @return
     */
    @GetMapping(value = "sync/online/epm")
    public ResponseInfo syncOnlineEpm(@RequestParam String appId,@RequestParam String operator,@RequestParam String time){
        epmService.syncOnlineEpm(appId,operator,time);
        return ResponseInfoUtil.success();
    }



}
