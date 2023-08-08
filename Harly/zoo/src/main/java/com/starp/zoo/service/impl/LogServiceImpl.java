package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.common.constant.ProtocolCrackConstant;
import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.config.aws.sqs.Producer;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.HttpHtmlModel;
import com.starp.zoo.entity.zoo.HttpLoggingModel;
import com.starp.zoo.entity.zoo.OfferStepModel;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.repo.zoo.HttpHtmlRepo;
import com.starp.zoo.repo.zoo.HttpLoggingRepo;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.service.ILogService;
import com.starp.zoo.vo.AppEventVo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author charles
 */
@Service
public class LogServiceImpl implements ILogService {

    @Autowired
    private Producer producer;

    @Autowired
    private HttpLoggingRepo httpLoggingRepo;

    @Autowired
    private HttpHtmlRepo httpHtmlRepo;

    @Resource(name = "cluster2RedisTemplate")
    private StringRedisTemplate cluster2RedisTemplate;


    @Autowired
    private IApplicationService applicationService;

    @Timed
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @Async
    @Override
    public void saveLog(String appId, String offerId, String aff, String pid,
                        String url, String operator, String body, Long timestamp,
                        String ipAddress, Integer status,String userId) {
        ApplicationModel applicationModel = applicationService.getById(appId);
        if(applicationModel != null && applicationModel.getLogStatus() == 0){
            return;
        }
        HttpLoggingModel httpLoggingModel = new HttpLoggingModel();
        httpLoggingModel.setAppId(appId);
        httpLoggingModel.setOfferId(offerId);
        httpLoggingModel.setPid(pid);
        httpLoggingModel.setCarrier(operator);
        httpLoggingModel.setUrl(url);
        httpLoggingModel.setIp(ipAddress);
        httpLoggingModel.setClientTime(new Date(timestamp));
        httpLoggingModel.setBody(body);
        httpLoggingModel.setStatus(status);
        httpLoggingModel.setUserId(userId);

        if (!StringUtils.isEmpty(status) && status != ProtocolCrackConstant.LOG_STATUS_RESPONSE) {
            //如果不是响应请求，则返回
            return;
        }
        AppEventVo appEventVo = new AppEventVo();
        if (timestamp != null) {
            appEventVo.setClientTime(new Date(timestamp));
        }
        appEventVo.setAff(aff);
        appEventVo.setAppId(appId);
        appEventVo.setOfferId(offerId);
        appEventVo.setOperator(operator);
        appEventVo.setPid(pid);
        appEventVo.setUserId(userId);
        appEventVo.setUrl(url);
        if(!StringUtils.isEmpty(url)){
            saveEvent(url,appEventVo,body,pid);
        }

        String key = Constants.PROTC_OFFER_STEP + Constants.COLON + offerId;
        Map<Object, Object> map = cluster2RedisTemplate.opsForHash().entries(key);
        if (map != null && map.size() > 0) {
            // map 中 hkey 为 url regex
            for(Map.Entry entry : map.entrySet()) {
                boolean isMatch = checkMatch(url, (String) entry.getKey());
                if (isMatch) {
                    OfferStepModel offerStepModel = JSON.parseObject((String) entry.getValue(), OfferStepModel.class);
                    appEventVo.setPage(offerStepModel.getStepName());
                    appEventVo.setStep(String.valueOf(offerStepModel.getStepIndex()));
                }
            }
        }
    }

    private void saveEvent(String url,AppEventVo appEventVo,String body,String pid) {
        //ES VODAFONE
        if(url.indexOf(ProtocolCrackConstant.ES_VODAPHONE_PAGE1) >= 0){
            appEventVo.setPage("PAGE1");
            appEventVo.setStep("step-01");
        }
        if(url.indexOf(ProtocolCrackConstant.ES_VODAPHONE_PAGE2) >= 0){
            appEventVo.setPage("PAGE2");
            appEventVo.setStep("step-02");
        }
        if(url.indexOf(ProtocolCrackConstant.ES_VODAPHONE_PAGE3) >= 0){
            appEventVo.setPage("PAGE3");
            appEventVo.setStep("step-03");
        }
        //PT MEO
        if(url.indexOf(ProtocolCrackConstant.PT_MEO_PAGE1) >= 0) {
            appEventVo.setPage("PAGE1");
            appEventVo.setStep("step-01");
        }
        if(url.indexOf(ProtocolCrackConstant.PT_MEO_PAGE2) >= 0){
            appEventVo.setPage("PAGE2");
            appEventVo.setStep("step-02");
        }
        if(url.indexOf(ProtocolCrackConstant.PT_MEO_PAGE3) >= 0){
            appEventVo.setPage("PAGE3");
            appEventVo.setStep("step-03");
        }
        // 处理IT
        handleIt(url,appEventVo);
        handleTh(url,appEventVo,body);
        handleMy(url,appEventVo,pid);


    }

    private void handleMy(String url, AppEventVo appEventVo,String pid) {
        // MY_DIGI  253937
        if (url.indexOf(ProtocolCrackConstant.MY_DIGI_AFF_1_NETWORK00) >= 0) {
            appEventVo.setPage("AFF1_NETWORK-00");
            appEventVo.setStep("step-01");
        }
        if (url.indexOf(ProtocolCrackConstant.MY_DIGI_AFF_1_NETWORK01) >= 0) {
            appEventVo.setPage("AFF1_NETWORK-01");
            appEventVo.setStep("step-02");
        }
        if (url.indexOf(ProtocolCrackConstant.MY_DIGI_AFF_1_AOC11) >= 0) {
            appEventVo.setPage("AOC-01");
            appEventVo.setStep("step-03");
        }
        if (url.indexOf(ProtocolCrackConstant.MY_DIGI_AFF_1_AOC12) >= 0) {
            appEventVo.setPage("AOC-01");
            appEventVo.setStep("step-04");
        }

        // MY_DIGI 742241
        if (url.indexOf(ProtocolCrackConstant.MY_DIGI_AFF_2_NETWORK00) >= 0) {
            appEventVo.setPage("AFF2_NETWORK-01");
            appEventVo.setStep("step-01");
        }
        if (url.indexOf(ProtocolCrackConstant.MY_DIGI_AFF_2_NETWORK002) >= 0) {
            appEventVo.setPage("AFF2_NETWORK-01");
            appEventVo.setStep("step-02");
        }

        // MY_CELCOM
        if(url.indexOf(ProtocolCrackConstant.MY_CELCOM_CMT_GAME_LP)>=0){
            appEventVo.setPage("MY_CELCOM_CMT_LP_01-01");
            appEventVo.setStep("step-01");
        }
        if(url.indexOf(ProtocolCrackConstant.MY_CELCOM_SPARK_REDIRECT)>=0) {
            appEventVo.setPage("MY_CELCOM_SPARK_AOC_01");
            appEventVo.setStep("step-01");
        }
        if(url.indexOf(ProtocolCrackConstant.MY_CELCOM_GADMOBE_REDIRECT)>=0){
            appEventVo.setPage("MY_CELCOM_GADMOBE_AOC_01");
            appEventVo.setStep("step-01");
        }
        HttpHtmlModel httpHtmlModel = httpHtmlRepo.findByIdentificationLike(pid);
        boolean isReturnConfirmPin = false;
        if(httpHtmlModel!=null){
            isReturnConfirmPin = true;
        }
        if(url.indexOf(ProtocolCrackConstant.MY_CELCOM_AOC)>=0&&!isReturnConfirmPin){
            appEventVo.setPage("MY_CELCOM_SEND_PIN");
            appEventVo.setStep("step-02");
        }
        if(url.indexOf(ProtocolCrackConstant.MY_CELCOM_PIN)>=0){
            appEventVo.setPage("MY_CELCOM_RETURN_SEND_PIN");
            appEventVo.setStep("step-03");
        }
        if(url.indexOf(ProtocolCrackConstant.MY_CELCOM_AOC)>=0&&isReturnConfirmPin){
            appEventVo.setPage("MY_CELCOM_CONFIRM PIN");
            appEventVo.setStep("step-04");
        }
    }

    private void handleTh(String url, AppEventVo appEventVo,String body) {
        // 泰国
        handleTruemove(url,appEventVo,body);
        // TH_TRUEMOVE 217167
        //TH_AIS
        if(url.indexOf(ProtocolCrackConstant.TH_AIS_AFF_E6)>=0){
            appEventVo.setPage("TH_AIS-AFF-E6_AOC_01");
            appEventVo.setStep("step-01");
        }
        if(url.indexOf(ProtocolCrackConstant.TH_AIS_AFF_E6_TRACK)>=0&&url.indexOf(ProtocolCrackConstant.TH_AIS_AFF_CPM)>=0){
            appEventVo.setPage("TH_AIS-AFF-E6_AOC_02");
            appEventVo.setStep("step-02");
        }
        if(url.indexOf(ProtocolCrackConstant.TH_AIS_AFF_E6_LOAD)>=0){
            appEventVo.setPage("TH_AIS-AFF-E6_AOC_03");
            appEventVo.setStep("step-03");
        }
        if(url.indexOf(ProtocolCrackConstant.TH_AIS_AFF_E6_AOC_01)>=0){
            appEventVo.setPage("TH_AIS-AFF-AOC");
            appEventVo.setStep("step-05");
        }
        if(url.indexOf(ProtocolCrackConstant.TH_AIS_AFF_PIN_E6_OTP_LINK)>=0){
            appEventVo.setPage("TH_AIS-AFF-SEND-PIN");
            appEventVo.setStep("step-06");
        }
        if(url.indexOf(ProtocolCrackConstant.TH_AIS_AFF_CONFIRM_PIN)>=0){
            appEventVo.setPage("TH_AIS_AFF-CONFIRM-PIN");
            appEventVo.setStep("step-07");
        }

        // ais-Marvel
        if(url.indexOf(ProtocolCrackConstant.TH_AIS_MARVEL_LP)>=0){
            appEventVo.setPage("TH_AIS-AFF-MARVEL_LP-01");
            appEventVo.setStep("step-01");
        }
        if(url.indexOf(ProtocolCrackConstant.TH_AIS_4K_LP)>=0){
            appEventVo.setPage("TH_AIS-AFF-4K_LP-01");
            appEventVo.setStep("step-01");
        }
        //TH_DTAC
        //OFFERID:247485
        if(url.indexOf(ProtocolCrackConstant.TH_DTAC_PIN_01)>=0){
            appEventVo.setPage("TH_DTAC_PIN_01");
            appEventVo.setStep("step-01");
        }
        if(url.indexOf(ProtocolCrackConstant.TH_DTAC_PIN_02)>=0){
            appEventVo.setPage("TH_DTAC_PIN_02");
            appEventVo.setStep("step-02");
        }
        if(url.indexOf(ProtocolCrackConstant.TH_DTAC_SEND_PIN)>=0){
            appEventVo.setPage("TH_DTAC_SEND_RESEND_01");
            appEventVo.setStep("step-03");
        }
    }

    private void handleTruemove(String url, AppEventVo appEventVo,String body) {
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_NEWTWORK00) >= 0
                && url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_NEWTWORK00+ProtocolCrackConstant.API) >= 0) {
            appEventVo.setPage("NETWORK-01");
            appEventVo.setStep("step-01");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_NEWTWORK02) >= 0) {
            appEventVo.setPage("NETWORK-02");
            appEventVo.setStep("step-02");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_NEWTWORK00) >= 0
                && url.indexOf(ProtocolCrackConstant.HTML) >= 0) {
            appEventVo.setPage("NETWORK-03");
            appEventVo.setStep("step-03");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AOC01) >= 0
                || url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AOC04) >= 0) {
            appEventVo.setPage("AOC-01");
            appEventVo.setStep("step-04");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AOC02) >= 0) {
            appEventVo.setPage("AOC-02");
            appEventVo.setStep("step-05");
        }
        // get google token 返回
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_GOOGLE_TOKEN) >= 0) {
            if (!StringUtils.isEmpty(body) && body.indexOf(ProtocolCrackConstant.OK_MARK) >= 0) {
                appEventVo.setPage("AOC-02");
                appEventVo.setStep("step-end");
            }
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AOC03) >= 0) {
            appEventVo.setPage("AOC-03");
            appEventVo.setStep("step-01");
        }
        handleTruemoveAff2(url,appEventVo);
    }
    private void handleTruemoveAff2(String url, AppEventVo appEventVo) {
        //TH_TRUEMOVE 处理 aff 2 流程 523668
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_2_NETWORK00) >= 0) {
            appEventVo.setPage("AFF-2_NETWORK-00");
            appEventVo.setStep("step-01");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_NETWORK_GET_MSISDN) >= 0) {
            appEventVo.setPage("AFF-2_NETWORK-00");
            appEventVo.setStep("step-02");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_2_NETWORK01) >= 0) {
            appEventVo.setPage("AFF-2_NETWORK-01");
            appEventVo.setStep("step-03");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_2_NEWTWORK02) >= 0) {
            if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_SPECIAL_PAGE_PARAM) >= 0) {
                appEventVo.setPage("AFF-2_NETWORK-02");
                appEventVo.setStep("step-04-special");
            } else {
                appEventVo.setPage("AFF-2_NETWORK-02");
                appEventVo.setStep("step-04");
            }
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_2_NEWTWORK03) >= 0) {
            appEventVo.setPage("AFF-2_NETWORK-02");
            appEventVo.setStep("step-05");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_2_NEWTWORK04) >= 0) {
            appEventVo.setPage("AFF-2_NETWORK-02");
            appEventVo.setStep("step-06");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AOC05) >= 0) {
            appEventVo.setPage("AFF-2_AOC-00");
            appEventVo.setStep("step-07");
        }
        //TH_TRUEMOVE 处理 aff 3 408871 | 193170
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_3_NETWORK00) >= 0) {
            appEventVo.setPage("AFF-3_NETWORK-00");
            appEventVo.setStep("step-01");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AFF_3_NETWORK01) >= 0) {
            appEventVo.setPage("AFF-3_NETWORK-01");
            appEventVo.setStep("step-02");
        }
        if (url.indexOf(ProtocolCrackConstant.TH_TRUEMOVE_AOC06) >= 0) {
            appEventVo.setPage("AFF-3_AOC-00");
            appEventVo.setStep("step-03");
        }
    }

    private void handleIt(String url, AppEventVo appEventVo) {
        //IT WIND
        //IT WIND 网盟step1
        if(url.indexOf(ProtocolCrackConstant.IT_WIND_AFF_NETWORK_00) >= 0) {
            appEventVo.setPage("NETWORK-00");
            appEventVo.setStep("step-01");
        }
        //IT WIND 网盟step2
        if(url.indexOf(ProtocolCrackConstant.IT_WIND_AFF_NETWORK_01) >= 0) {
            appEventVo.setPage("NETWORK-01");
            appEventVo.setStep("step-02");
        }
        //IT WIND STEP 1
        if (url.indexOf(ProtocolCrackConstant.IT_WIND_STEP_01) >= 0) {
            appEventVo.setPage("NETWORK-02");
            appEventVo.setStep("step-03");
        }
        if (url.indexOf(ProtocolCrackConstant.IT_WIND_STEP_03) >= 0) {
            appEventVo.setPage("NETWORK-03");
            appEventVo.setStep("step-04");
        }
        //IT WIND AOC
        if(url.indexOf(ProtocolCrackConstant.IT_AOC_STCN) >= 0) {
            appEventVo.setPage("AOC-01");
            appEventVo.setStep("step-05");
        }
        if(url.indexOf(ProtocolCrackConstant.IT_AOC_STSB) >= 0) {
            appEventVo.setPage("AOC-02");
            appEventVo.setStep("step-06");
        }
        //IT WIND cookieMonster获取cookie后提交表单
        if(url.indexOf(ProtocolCrackConstant.IT_WIND_COOKIEMONSTER) >= 0
                || url.indexOf(ProtocolCrackConstant.IT_VODAPHONE_COOKIEMONSTER) >= 0) {
            appEventVo.setPage("COOKIE-MONSTER");
            appEventVo.setStep("step-07");
        }
        //IT VODAFONE 网盟step1
        if(url.indexOf(ProtocolCrackConstant.IT_VODAPHONE_AFF_NETWORK_00) >= 0) {
            appEventVo.setPage("NETWORK-00");
            appEventVo.setStep("step-01");
        }
        //IT VODAFONE 网盟step2s
        boolean isPage01 = url.indexOf(ProtocolCrackConstant.IT_VODAPHONE_AFF_NETWORK_012) >= 0 || (url.indexOf(ProtocolCrackConstant.IT_VODAPHONE_AFF_NETWORK_02) >= 0 && url.indexOf(ProtocolCrackConstant.IT_VODAPHONE_AFF_NETWORK_03) >= 0)
                || url.indexOf(ProtocolCrackConstant.IT_VODAPHONE_AFF_NETWORK_01) >= 0;
        if(isPage01) {
            appEventVo.setPage("NETWORK-01");
            appEventVo.setStep("step-02");
        }
        //IT TIM
        if(url.indexOf(ProtocolCrackConstant.IT_TIM_AFF_NETWORK_00) >= 0 || url.indexOf(ProtocolCrackConstant.IT_TIM_AFF_NETWORK_01) >= 0) {
            appEventVo.setPage("NETWORK-00");
            appEventVo.setStep("step-01");
        }
        //AOC iframe 页面保存页面源码并返回sc=T的页面
        if(url.indexOf(ProtocolCrackConstant.IT_TIM_AOC_00) >= 0) {
            appEventVo.setPage("AOC-00");
            appEventVo.setStep("step-02");
        }

        if(url.indexOf(ProtocolCrackConstant.IT_TIM_AOC_01) >= 0) {
            appEventVo.setPage("AOC-01");
            appEventVo.setStep("step-03");
        }
        //处理action.html页面
        if(url.indexOf(ProtocolCrackConstant.IT_TIM_AOC_013) >= 0) {
            //如果包含sc=T，先判断是第一次进入还是第二次，如果是第一次则从数据库取出获取id为html01缓存的页面源码并解析出不带sc=T的链接,否则取出html02
            if(url.indexOf(ProtocolCrackConstant.IT_TIM_AOC_014) >= 0){
                appEventVo.setPage("AOC-014");
                appEventVo.setStep("step-05");
                //如果不包含sc=t，如果是第一次则再次保存id为html2的页面，如果是第二次，则什么也不做
            } else {
                appEventVo.setPage("AOC-013");
                appEventVo.setStep("step-04");
            }
        }
        if(url.indexOf(ProtocolCrackConstant.IT_TIM_AOC_022) >= 0){
            appEventVo.setPage("AOC-022");
            appEventVo.setStep("step-06");
        }
    }

    /**
     * 检查链接是否匹配
     * @param url
     * @param key
     * @return
     */
    private boolean checkMatch(String url, String key) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(key)) {
            return false;
        }
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
}
