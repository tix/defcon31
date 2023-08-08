package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.config.aws.sqs.Producer;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.LogConstant;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.*;
import com.starp.zoo.repo.zoo.*;
import com.starp.zoo.service.IAffService;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.service.IEpmService;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.util.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * @author  Charles,
 * @date: 2018/9/19.
 */
@Slf4j
@Service
public class AffServiceImpl implements IAffService {

    @Autowired
    private AffPostBackRepo affPostBackRepo;

    @Autowired
    private AffPostBackIeRepo affPostBackIeRepo;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private AffClickInfoRepo affClickInfoRepo;

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private AffSmartLinkRepo smartLinkRepo;

    @Autowired
    private IEpmService epmService;

    @Autowired
    @Lazy
    private IApplicationService applicationService;

    @Autowired
    private StringRedisTemplate masterRedisTemplate;

    @Autowired
    private AffSmartLinkRepo affSmartLinkRepo;

    @Autowired
    private DeductConfigRepo deductConfigRepo;

    @Autowired
    private AffiliateRepo affiliateRepo;

    @Autowired
    private ChannelTransRepo channelTransRepo;

    @Autowired
    private AffApkPostBackRepo affApkPostBackRepo;

    @Lazy
    @Autowired
    private IOfferService offerService;

    @Autowired
    private Producer producer;

    /**
     * tracking
     */
    @Resource(name = "trackRedisTemplate")
    private StringRedisTemplate trackRedisTemplate;

    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;

    @Override
    public String getClickId(String appId, String offerId){
        String clickId =ZooConstant.APP.toUpperCase() + DateUtil.getCurrentTimeSeconds()  + RandomUtil.getRandomNum(6);
        Map<String, String> map = new HashMap<>(3);
        map.put(ZooConstant.OFFER_ID, offerId);
        map.put(ZooConstant.APP_ID, appId);
        String key = ZooConstant.ZOO_CLICK_ID + ZooConstant.COLON + clickId;
        trackRedisTemplate.opsForHash().putAll(key, map);
        trackRedisTemplate.expire(key, NumberEnum.TWO.getNum(), TimeUnit.DAYS);
        return clickId;
    }

    @Override
    @Async
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    public void savePostBack(AffPostBackModel affModel) {
        String key = ZooConstant.ZOO_CLICK_ID + ZooConstant.COLON + affModel.getClickId();
        String appId = (String) trackRedisTemplate.opsForHash().get(key, ZooConstant.APP_ID);
        String offerId = (String) trackRedisTemplate.opsForHash().get(key, ZooConstant.OFFER_ID);
        OfferModel offerModel = offerRepo.findFirstByOfferId(offerId);
        boolean existTestOffer = false;
        if(!StringUtils.isEmpty(offerId)){
            Boolean isTestOffer = cluster3RedisTemplate.opsForHash().hasKey(ZooConstant.ZOO_TEST_OFFER_INFO,offerId);
            existTestOffer = isTestOffer != null && isTestOffer;
        }

        if(offerModel==null && !existTestOffer){
            log.info("APP AFF POST BACK NOT BELONG TO US :{}",JSON.toJSONString(affModel));
            return;
        }
        if(existTestOffer){
            offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(ZooConstant.ZOO_TEST_OFFER_INFO,offerId)),OfferModel.class);
        }
        Double revenue = null;
        if(affModel.getPayout() != null && affModel.getPayout() > 0){
            revenue = affModel.getPayout().doubleValue();
        }else {
            revenue = offerModel.getPayout();
        }
        affModel.setResourceId(appId);
        affModel.setSystemOfferId(offerId);
        affModel.setPartnerOfferId(offerModel.getPartnerOfferId());
        AffPostBackModel saveModel = affPostBackRepo.save(affModel);
        epmService.incrTrans(saveModel.getResourceId(), offerModel, revenue, true, saveModel.getResourceType());
    }

    @Override
    public void saveAffApkPostBack(JSONObject postback) {
        log.info("AFF APK POST BACK MODEL:{}",JSON.toJSONString(postback));
        String ip = postback.getString("ip");
        String appId = postback.getString("appId");
        String mnc = postback.getString("mnc");
        JSONObject paramJson = new JSONObject();
        if(!StringUtils.isEmpty(ip)){
            String key = ZooConstant.ZOO + ZooConstant.COLON + ZooConstant.REDIRECT_APK + ZooConstant.COLON + ip;
            Boolean existKey = cluster3RedisTemplate.hasKey(key);
            if(existKey!=null && existKey){
                String param = cluster3RedisTemplate.opsForValue().get(key);
                if(!StringUtils.isEmpty(param) && param.indexOf(ZooConstant.TXID)<0){
                    paramJson.put("cid",param);
                    paramJson.put("txid","");
                    paramJson.put("payout","");
                }else {
                     paramJson = JSON.parseObject(param);
                }
                boolean existPostback = affApkPostBackRepo.existsByIp(ip);
                if(!existPostback){
                    String url = ZooConstant.AFF_APK_POSTBACK_URL;
                    AffApkPostBackModel affApkPostBackModel = new AffApkPostBackModel();
                    affApkPostBackModel.setAppId(appId);
                    affApkPostBackModel.setClickId(paramJson.getString("cid"));
                    affApkPostBackModel.setIp(ip);
                    affApkPostBackModel.setMnc(mnc);
                    String redirectUrl = url + "&cid="+paramJson.getString("cid") + "&payout=" + paramJson.getString("payout") + "&txid=" +paramJson.getString("txid");
                    affApkPostBackModel.setPostbackUrl(redirectUrl);
                    affApkPostBackRepo.save(affApkPostBackModel);
                    JSONObject result = HttpUtil.doGet(redirectUrl);
                    log.info("AFF APK POST BACK URL:{}, RESULT :{}",redirectUrl,JSON.toJSONString(result));
                }
            }
        }
    }

    private void handleNewTrans(AffPostBackModel affModel) {
        String key = ZooConstant.ZOO_CLICK_ID + ZooConstant.COLON + affModel.getClickId();
        String appId = (String) trackRedisTemplate.opsForHash().get(key, ZooConstant.APP_ID);
        String offerId = (String) trackRedisTemplate.opsForHash().get(key, ZooConstant.OFFER_ID);
        OfferModel offerModel = offerRepo.findFirstByOfferId(offerId);
        if(offerModel==null){
            log.info("APP AFF POST BACK NOT BELONG TO US :{}",JSON.toJSONString(affModel));
            return;
        }
        Double revenue = null;
        if(affModel.getPayout() != null && affModel.getPayout() > 0){
            revenue = affModel.getPayout().doubleValue();
        }else {
            revenue = offerModel.getPayout();
        }
        affModel.setResourceId(appId);
        affModel.setSystemOfferId(offerId);
        affModel.setPartnerOfferId(offerModel.getPartnerOfferId());
        AffPostBackModel saveModel = affPostBackRepo.save(affModel);
        epmService.incrTrans(saveModel.getResourceId(), offerModel, revenue, true, saveModel.getResourceType());
    }

    private void handleOldTrans(AffPostBackModel saveModel) {
        //获取offer配置
        OfferModel offerModel = offerRepo.findFirstByPartnerAndOfferId(saveModel.getPartner(), saveModel.getSystemOfferId());
        if(offerModel != null && saveModel.getResourceType() != 0) {
            Double revenue = null;
            if(saveModel.getPayout() != null && saveModel.getPayout() > 0){
                revenue = saveModel.getPayout().doubleValue();
            }else {
                revenue = offerModel.getPayout();
            }
            epmService.incrTrans(saveModel.getResourceId(), offerModel, revenue, true, saveModel.getResourceType());
            try {
                // 回调
                if (saveModel.getClickId().contains(ZooConstant.AFF_SMART_LINK_SPLITTER)) {
                    handleTrans(offerModel, saveModel);
                }
            } catch (Exception e) {
                log.info("{} [AFF_SERVICE] [HANDLE TRANS ERROR:{}]", LogConstant.ZOO, e.toString(), e);
            }
        }
    }

    private void handleTrans(OfferModel offerModel, AffPostBackModel affPostBackModel) throws Exception {
        String clickId = affPostBackModel.getClickId();
        String[] arr = clickId.split(ZooConstant.AFF_SMART_LINK_SPLITTER);
        if(arr != null && arr.length == NumberEnum.TWO.getNum()) {
            String smId = arr[1];
            AffSmartLinkModel affSmartLinkModel = affSmartLinkRepo.findFirstByIdentification(smId);
            if (affSmartLinkModel != null) {
                // 判断是否需要扣量
                DeductConfigModel deductConfigModel = deductConfigRepo.findBySmartLinkIdAndOfferId(smId, offerModel.getIdentification());
                boolean isDeduct = checkDeduct(deductConfigModel, offerModel);
                log.info("{} [AFF_SERVICE] [HANDLE_TRANS] [SMART_LINK_ID:{} OFFER_ID:{} DEDUCT_MODEL:{} DEDUCT:{}]", LogConstant.ZOO, smId, offerModel.getIdentification(), JSON.toJSONString(deductConfigModel), isDeduct);
                if (!isDeduct) {
                    AffiliateModel affiliateModel = affiliateRepo.findFirstByIdentification(affSmartLinkModel.getAffId());
                    String subClickId = arr[0].split(ZooConstant.AFF_OFFER_SPLITTER)[1];
                    String url = formatUrl(affiliateModel, subClickId, deductConfigModel);
                    JSONObject json = HttpUtil.doGet(url);
                    log.info("{} [AFF_SERVICE] [HANDLE_TRANS] [SMART_LINK_ID:{} OFFER_ID:{} URL:{} RESPONSE:{}]", LogConstant.ZOO, smId, offerModel.getIdentification(), url, json.toJSONString());
                    ChannelTransModel channelTransModel = new ChannelTransModel();
                    channelTransModel.setChannelId(affiliateModel.getIdentification());
                    channelTransModel.setChannelName(affiliateModel.getName());
                    channelTransModel.setSystemOfferId(offerModel.getOfferId());
                    channelTransModel.setPartner(offerModel.getPartner());
                    channelTransModel.setPartnerOfferId(offerModel.getPartnerOfferId());
                    channelTransModel.setPayout(deductConfigModel.getPayout());
                    channelTransModel.setClickId(clickId);
                    channelTransModel.setRequestUrl(url);
                    channelTransModel.setResponse(json.getString(HttpUtil.MSG));
                    channelTransModel.setResponseStatus(json.getInteger(HttpUtil.CODE));
                    channelTransRepo.save(channelTransModel);
                }
            }
        }
    }

    private String formatUrl(AffiliateModel affiliateModel, String clickId, DeductConfigModel deductConfigModel) throws Exception {
        String url = null;
        if(affiliateModel != null) {
            url = affiliateModel.getUrl();
            // replace clickId(点击参数)
            url = getReplaceUrl(url, affiliateModel.getClickIdParam(), clickId);
            // replace offerId
            url = getReplaceUrl(url, affiliateModel.getOfferIdParam(), deductConfigModel.getSmartLinkId());
            url = getReplaceUrl(url, affiliateModel.getPayoutParam(), String.valueOf(deductConfigModel.getPayout()));
        }
        url = encodeOthersValue(url);
        return url;
    }

    /**
     * 替换参数值
     * @param url
     * @param param
     * @param value
     * @return
     */
    private static String  getReplaceUrl(String url, String param, String value){
        Matcher matcher = PatternUtil.URL_PARAMS_PATTERN.matcher(url);
        //先替换透传再替换clickId 防止clickId参数与透传参数一样
        if(!StringUtils.isEmpty(param)) {
            if (url.contains(param + ZooConstant.EQUAL_MARK)) {
                while (matcher.find()) {
                    if (matcher.group(2).equals(param)) {
                        //替换 channelId
                        url = url.replace(matcher.group(2) + ZooConstant.EQUAL_MARK + matcher.group(3), matcher.group(2) + ZooConstant.EQUAL_MARK + value);
                    }
                }
            }else {
                url += url.contains(ZooConstant.INTERROGATION_MARK) ? "" : ZooConstant.INTERROGATION_MARK;
                url += ZooConstant.AND_MARK + param + ZooConstant.EQUAL_MARK + value;
            }
        }
        return url;
    }

    /**
     * 将不必要字段转为 encode
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    private String encodeOthersValue(String url) throws UnsupportedEncodingException {
        Matcher matcher = PatternUtil.URL_PARAMS_PATTERN.matcher(url);
        while (matcher.find()) {
            url = url.replace(matcher.group(2) + ZooConstant.EQUAL_MARK + matcher.group(3), matcher.group(2) + ZooConstant.EQUAL_MARK + URLEncoder.encode(matcher.group(3), "UTF-8"));
        }
        return url;
    }

    private boolean checkDeduct(DeductConfigModel deductConfigModel, OfferModel offerModel) throws Exception {
        boolean result = true;
        if(offerModel != null && deductConfigModel != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            // 设置刷新统计时区
            sdf.setTimeZone(TimeZone.getTimeZone(offerModel.getResetTimezone()));
            // 判断是否有 扣量hash 存在 zoo_deduct:offer-identification:smartlink-identification:date   date为刷新时区的日期
            String date = sdf.format(new Date());
            String key = CacheNameSpace.ZOO_DEDUCT + CacheNameSpace.COLON + offerModel.getIdentification() + CacheNameSpace.COLON + deductConfigModel.getSmartLinkId() + CacheNameSpace.COLON + date;
            //应写转化数量+1
            masterRedisTemplate.opsForHash().increment(key, CacheNameSpace.FILED_SHOULD_WRITE, 1);
            masterRedisTemplate.expire(key, NumberEnum.SEVEN.getNum(), TimeUnit.DAYS);
            //已写转化数
            Long writeCount = masterRedisTemplate.opsForHash().increment(key, CacheNameSpace.FILED_WRITE, 0);
            int deduct = deductConfigModel.getDeduct();
            int cap = offerModel.getCap();
            //判断是否超 cap ， 超cap则全部扣量
            if (writeCount < cap) {
                //当前offer扣量计数器current+1
                Long countNum = masterRedisTemplate.opsForHash().increment(key, CacheNameSpace.FILED_CURRENT, 1);
                Long currentNum = countNum % 100;
                // 读取hash redis 数组
                String randmonBitsStr = (String) cluster3RedisTemplate.opsForHash().get(key, CacheNameSpace.FILED_RANDOM_BITS);
                if (currentNum == 0 || StringUtils.isEmpty(randmonBitsStr) || ZooConstant.NULL_STR.equalsIgnoreCase(randmonBitsStr)) {
                    List<Integer> list = getDeductRandomList(deduct);
                    randmonBitsStr = new Gson().toJson(list);
                    masterRedisTemplate.opsForHash().put(key, CacheNameSpace.FILED_RANDOM_BITS, randmonBitsStr);
                }
                //根据randmonbits判断是否扣量
                List<Integer> randomBitsList = new Gson().fromJson(randmonBitsStr, new TypeToken<List<Integer>>() {
                }.getType());
                if (randomBitsList.get(currentNum.intValue()) == 0) {
                    //已转化 +1
                    masterRedisTemplate.opsForHash().increment(key, CacheNameSpace.FILED_WRITE, 1);
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * 获取 扣量{0， 1}数组
     * @param randomNum
     * @return
     * @throws Exception
     */
    public static List<Integer> getDeductRandomList(Integer randomNum) throws Exception{
        List<Integer> list = new ArrayList(100);
        int[] randomArr2 = RandomUtil.randomArray(0, 99, randomNum);
        for (int i = 0; i < NumberEnum.ONE_HUNDRED.getNum(); i++) {
            list.add(0);
            for (int j = 0; j < randomArr2.length; j++) {
                if (randomArr2[j] == i) {
                    list.set(i, 1);
                }
            }
        }
        return list;
    }


    @Override
    @Async
    public void save(AffPostBackIeModel affPostBackIEModel) {
        if(affPostBackIEModel != null){
            affPostBackIeRepo.save(affPostBackIEModel);
        }
    }

    @Override
    @Async
    public void saveClickInfo(String ipAddress, String deviceId, String clickId, OfferModel offerModel , String userAgent, String categoryId, int type, boolean isSave) {
        if (offerModel != null) {
            epmService.incrClick(categoryId, offerModel.getIdentification(), type);
            String key = CacheNameSpace.AFF_OFFER_PULL_COUNTER + offerModel.getIdentification() + ZooConstant.COLON + SubCountServiceImpl.formatDateByTimeZone(offerModel);
            masterRedisTemplate.opsForValue().increment(key, 1L);
            masterRedisTemplate.expire(key, NumberEnum.ONE_DAY_MILLISECONDS.getNum(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 一个用户在一个offer内一天只能拉取一次
     * @param ipAddress
     * @param categoryType
     * @param categoryId
     * @param offerId
     * @return
     */
    @Override
    public long getPullNum(String ipAddress, String categoryType, String categoryId, String offerId) {
        String key = CacheNameSpace.AFF_PULL_COUNTER + CacheNameSpace.COLON + ipAddress
                + CacheNameSpace.COLON + categoryType + CacheNameSpace.COLON + categoryId + CacheNameSpace.COLON + offerId;
        Long counter = masterRedisTemplate.opsForValue().increment(key, 1);
        if(counter != null && counter.intValue() == NumberEnum.ONE.getNum()){
            masterRedisTemplate.expire(key, NumberEnum.ONE_DAY_MILLISECONDS.getNum(), TimeUnit.MILLISECONDS);
        }
        return counter == null ? 0 : counter.longValue();
    }
}
