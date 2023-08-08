package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.config.aws.sqs.Producer;
import com.starp.zoo.constant.*;
import com.starp.zoo.entity.zoo.*;
import com.starp.zoo.repo.zoo.*;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.service.IEpmService;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.EmailUtil;
import com.starp.zoo.vo.EpmClickVO;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.discovery.zen.ZenPing;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Charles
 * @date 2019/1/22
 * @description :
 */
@Slf4j
@Service
public class EpmServiceImpl implements IEpmService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private AffSmartLinkRepo affSmartLinkRepo;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private OfferTagRepo offerTagRepo;

    @Autowired
    private AffEpmInfoRepo affEpmInfoRepo;

    @Autowired
    private AffPostBackRepo affPostBackRepo;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private ComparativeRepo comparativeRepo;

    @Autowired
    @Lazy
    private IOfferService offerService;

    @Autowired
    private CategoryTagRepo categoryTagRepo;

    @Autowired
    private MsisdnParamRepo msisdnParamRepo;

    @Autowired
    @Lazy
    private IApplicationService applicationService;

    @Lazy
    @Autowired
    private IEpmService epmService;


    @Autowired
    private Producer producer;


    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;
    private Logger log1;

    @Override
    public void incrClick(String categoryId, String offerId, int type) {
        if (type != 0) {
            String key = getRedisKey(categoryId, offerId, type);
            Long clickNum = stringRedisTemplate.opsForHash().increment(key, CacheNameSpace.CLICK, 1);
            if (clickNum == 1L) {
                //设置 TTL
                stringRedisTemplate.expire(key, NumberEnum.THREE.getNum(), TimeUnit.DAYS);
                updateRedisEpmCount(offerId, categoryId, key);
            }
        }
    }

    private void delEpmOfferByOfferId(String offerId, String appId, String operator) {
        String appEmpKeyStart = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator;
        //先获取 epm List 长度
        String appEpmListKey = appEmpKeyStart + CacheNameSpace.COLON + CacheNameSpace.LIST;
        stringRedisTemplate.opsForList().remove(appEpmListKey, 0, offerId);
        stringRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator, offerId);
        // 不满足条件的offer
        stringRedisTemplate.opsForHash().increment(CacheNameSpace.ZOO_UNUSED_OFFER, offerId, 1);
        stringRedisTemplate.expire(CacheNameSpace.ZOO_UNUSED_OFFER, 1, TimeUnit.DAYS);
    }

    @Override
    public void incrTrans(String categoryId, OfferModel offerModel, Double revenue, boolean isPostBack, int type) {
        if (type != 0) {
            if (!StringUtils.isEmpty(categoryId) && offerModel != null) {
                String key = getRedisKey(categoryId, offerModel.getIdentification(), type);
                // 是否存在 zoo_aff_epm_counter:app:appId:offerId:2021-08-04-01
                boolean existKey = checkExistEpmHourKey(key);
                if (revenue != null && revenue > 0) {
                    stringRedisTemplate.opsForHash().increment(key, CacheNameSpace.REVENUE, revenue);
                    updateEpmHourRedis(offerModel.getIdentification(), categoryId, key, existKey);
                }
                List<String> appList = offerTagRepo.findAppIdsByOfferId(offerModel.getIdentification());
                if (isPostBack) {
                    //postBackTrans
                    stringRedisTemplate.opsForHash().increment(key, CacheNameSpace.POST_BACK_TRANS, 1);
                    //更新当前小时key
                    updateEpmHourRedis(offerModel.getIdentification(), categoryId, key, existKey);
                    Long transCount = 0L;
                    String offerTransKey = CacheNameSpace.ZOO_OFFER_TRNS_COUNTER + categoryId + CacheNameSpace.COLON + offerModel.getOperator() + CacheNameSpace.COLON + DateUtil.today();
                    Long offerTransCount = stringRedisTemplate.opsForHash().increment(offerTransKey, offerModel.getIdentification(), 1);
                    if(offerTransCount == 1){
                        stringRedisTemplate.expire(offerTransKey,NumberEnum.ONE.getNum(),TimeUnit.DAYS);
                    }
                    Long offerDayTransCount = stringRedisTemplate.opsForHash().increment(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.POST_BACK_TRANS, 1);
                    if (offerDayTransCount == 1) {
                        stringRedisTemplate.expire(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), 2, TimeUnit.DAYS);
                    }
                    transCount = stringRedisTemplate.opsForHash().increment(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.POST_BACK_TRANS, 0);
                    String postbackKey = CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification());
                    log.info("OFFER POST BACK, OFFER ID:{},KEY:{},COUNT,TIME:{}", offerModel.getIdentification(), postbackKey, transCount, DateUtil.formatyyyyMMddHHmmss(new Date()));
                    //从epmList中删除超cap的offer
                    int cap = offerModel.getCap();
                    if (transCount >= cap) {
                        for (String appId : appList) {
                            delEpmOfferByOfferId(offerModel.getIdentification(), appId, offerModel.getOperator());
                            log.info("UPDATE EPM LIST AND FILTER , DELETE EPMLIST AND FILTER BY OVER OFFER POSTBACK CAP ,APPID:{}, OFFERID:{}, POSTBACK COUNT:{}, OFFER CAP  :{}  ", appId, offerModel.getIdentification(), transCount, offerModel.getCap());
                        }
                    }
                } else {
                    stringRedisTemplate.opsForHash().increment(key, CacheNameSpace.APP_TRANS, 1);
                    // 更新对应的redis
                    updateEpmHourRedis(offerModel.getIdentification(), categoryId, key, existKey);
                    Integer appCap = offerModel.getAppCap();
                    String appTranKey = CacheNameSpace.ZOO_APP_TRNS_COUNTER + categoryId + CacheNameSpace.COLON + offerModel.getOperator() + CacheNameSpace.COLON + DateUtil.today();
                    Long appTransCount = stringRedisTemplate.opsForHash().increment(appTranKey, offerModel.getIdentification(), 1);
                    if(appTransCount == 1){
                        stringRedisTemplate.expire(appTranKey,NumberEnum.ONE.getNum(),TimeUnit.DAYS);
                    }
                    Long offerDayTransCount = stringRedisTemplate.opsForHash().increment(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.APP_TRANS, 1);
                    if (offerDayTransCount == 1) {
                        stringRedisTemplate.expire(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), 2, TimeUnit.DAYS);
                    }
                    offerDayTransCount = stringRedisTemplate.opsForHash().increment(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.APP_TRANS, 0);
                    if (appCap != null && offerDayTransCount >= appCap) {
                        for (String appId : appList) {
                            delEpmOfferByOfferId(offerModel.getIdentification(), appId, offerModel.getOperator());
                            log.info("UPDATE EPM LIST AND FILTER , DELETE EPMLIST AND FILTER BY OVER OFFER APP CAP ,APPID:{}, OFFERID:{}, APP TRANS COUNT:{}, APP CAP  :{}  ", appId, offerModel.getIdentification(), appTransCount, offerModel.getAppCap());
                        }
                    }
                }
                stringRedisTemplate.expire(key, NumberEnum.THREE.getNum(), TimeUnit.DAYS);
            }
            //设置 offer 转化总数
            if (isPostBack && offerModel != null) {
                String counterKey = CacheNameSpace.AFF_OFFER_COUNTER + CacheNameSpace.COLON
                        + offerModel.getIdentification() + CacheNameSpace.COLON + SubCountServiceImpl.formatDateByTimeZone(offerModel);
                Long receiveCount = stringRedisTemplate.opsForValue().increment(counterKey, 1);
                if (null != receiveCount && receiveCount == 1) {
                    stringRedisTemplate.expire(counterKey, NumberEnum.THREE.getNum(), TimeUnit.DAYS);
                }
            }
        }
    }

    private void updateEpmHourRedis(String offerId, String categoryId, String key, boolean existKey) {
        if (!existKey) {
            //设置更新redis
            updateRedisEpmCount(offerId, categoryId, key);
            // 对应新增的key设置过期时间
            stringRedisTemplate.expire(key, NumberEnum.ONE.getNum(), TimeUnit.DAYS);
        }
    }

    private boolean checkExistEpmHourKey(String key) {
        Boolean existRedisKey = stringRedisTemplate.hasKey(key);
        boolean existKey = false;
        if (existRedisKey != null && existRedisKey) {
            existKey = true;
        }
        return existKey;
    }

    @SuppressFBWarnings({"ES_COMPARING_PARAMETER_STRING_WITH_EQ", "ES_COMPARING_PARAMETER_STRING_WITH_EQ"})
    private void updateRedisEpmCount(String offerId, String categoryId, String key) {
        //更新redis key
        if (offerId != ZooConstant.UNKNOWN && categoryId != ZooConstant.UNKNOWN) {
            OfferModel offerModel = null;
            Object obj = cluster3RedisTemplate.opsForHash().get(ZooConstant.ZOO_TEST_OFFER_INFO, offerId);
            if (obj != null) {
                offerModel = JSONObject.parseObject(String.valueOf(obj), OfferModel.class);
            } else {
                offerModel = offerService.getOfferModel(offerId);
            }
            ApplicationModel applicationModel = applicationService.getById(categoryId);
            if (offerModel != null && applicationModel != null) {
                Map<String, String> params = new HashMap<>(1);
                params.put(ZooConstant.COUNTRY, offerModel.getCountry());
                params.put(ZooConstant.OPERATOR, offerModel.getOperator());
                params.put(ZooConstant.PARTNER, offerModel.getPartner());
                params.put(ZooConstant.OFFER_NAME, offerModel.getOfferName());
                params.put(ZooConstant.OFFER_ID, offerModel.getOfferId());
                params.put(ZooConstant.APP_NAME, applicationModel.getAppName());
                params.put(ZooConstant.APP_ID, categoryId);
                params.put(ZooConstant.CAP, String.valueOf(offerModel.getCap()));
                params.put(ZooConstant.PARTNER_OFFER_ID, offerModel.getPartnerOfferId());
                params.put(ZooConstant.OFFER_STATUS, String.valueOf(offerModel.getStatus()));
                params.put(ZooConstant.BELONG, offerModel.getBelong());
                stringRedisTemplate.opsForHash().putAll(key, params);
            }
        }
    }

    private String getRedisKey(String categoryId, String offerId, int type) {
        String category = type == ZooConstant.CATEGORY_APP ? ZooConstant.APP : ZooConstant.AFFILIATE;
        String keyStart = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.COLON + category
                + CacheNameSpace.COLON + categoryId + CacheNameSpace.COLON + offerId + CacheNameSpace.COLON;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        return keyStart + sdf.format(calendar.getTime());
    }

    private Calendar getBeforeHourCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private Calendar getCurrentHourCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @SuppressFBWarnings({"REC_CATCH_EXCEPTION", "BX_UNBOXING_IMMEDIATELY_REBOXED", "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", "NP_NULL_PARAM_DEREF"})
    @Async
    @Override
    public void executeCalculateEpm(boolean isRetry, Calendar retryCalendar) {
        //遍历前一小时的 redis EPM counter 如果存在该 offer 回传不支持透传的， 则 categoryId 为 NONE
        Calendar calendar = isRetry ? retryCalendar : getBeforeHourCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        String date = sdf.format(calendar.getTime());
        String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + date;
        log.info("{} [EPM_SERVICE] [date:{}] [step-0] [CALCULATE_EPM] KEY_Pattern:{}", LogConstant.ZOO, date, pattern);
        Set<String> hKeySet = cluster3RedisTemplate.keys(pattern);
        List<OfferModel> offerModels = offerRepo.findAll();
        Map<String, String> offerOpMap = offerModels.stream().collect(Collectors.toMap(OfferModel::getIdentification, OfferModel::getOperator, (key1, key2) -> key1));
        if (hKeySet != null && hKeySet.size() > 0) {
            log.info("{} [EPM_SERVICE] [date:{}] [step-1] [CALCULATE_EPM] KEY_SET:{}", LogConstant.ZOO, date, JSON.toJSONString(hKeySet));
            Map<String, List<String>> appOperatorMap = new HashMap<>(1);
            for (String hKey : hKeySet) {
                String[] strArr = hKey.split(CacheNameSpace.COLON);
                String appId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[2] : "";
                String offerId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[3] : "";
                // 生成appId_operator : offerIdList map
                if (offerOpMap.containsKey(offerId) && !StringUtils.isEmpty(appId)) {
                    String appOperatorKey = appId + ZooConstant.UNDER_LINE + offerOpMap.get(offerId);
                    boolean existAppOperator = appOperatorMap.containsKey(appOperatorKey);
                    if (existAppOperator) {
                        List<String> offerList = appOperatorMap.get(appOperatorKey);
                        if (!offerList.contains(offerId)) {
                            offerList.add(offerId);
                            appOperatorMap.put(appOperatorKey, offerList);
                        }
                    } else {
                        List<String> offerList = new ArrayList<>();
                        offerList.add(offerId);
                        appOperatorMap.put(appOperatorKey, offerList);
                    }
                }
            }
            Set<Map.Entry<String, List<String>>> opEntry = appOperatorMap.entrySet();
            List<JSONObject> epmCalculateList = opEntry.stream().map(a -> {
                String appOpKey = a.getKey();
                List<String> offerIdList = a.getValue();
                JSONObject params = new JSONObject();
                params.put("epmKey", appOpKey);
                params.put("date", date);
                params.put("isRetry", isRetry);
                params.put("offerId", offerIdList);
                return params;
            }).collect(Collectors.toList());
            //发送到队列
            epmCalculateList.stream().forEach(a -> {
                log.info("EPM CALCULATE SQS [STEP-1] INFO:{}", JSON.toJSONString(a));
                if (isRetry) {
                    producer.sendToQueueEpmRetryCalculate(new BaseSqsMessage(new Integer[]{ZooConstant.QUEUE_EPM_RETRY_CALCULATE}, ZooConstant.EPM_RETRY_CALCULATE_KEY, JSON.toJSONString(a)));
                } else {
                    producer.sendToQueueEpmCalculate(new BaseSqsMessage(new Integer[]{ZooConstant.QUEUE_EPM_CALCULATE}, ZooConstant.EPM_CALCULATE_KEY, JSON.toJSONString(a)));
                }
            });
        }
    }

    /**
     * 队列计算EPM
     *
     * @param message
     */
    @Override
    public void handleEpmCalculate(String message) {
        log.info("EPM CALCULATE SQS [STEP-3] INFO:{}", message);
        JSONObject params = JSONObject.parseObject(message);
        String offerOpKey = params.getString("epmKey");
        String appId = offerOpKey.split(ZooConstant.UNDER_LINE)[0];
        String date = params.getString("date");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        boolean isRetry = params.getBoolean("isRetry");
        List<String> offerIdList = JSONArray.parseArray(params.getString("offerId"), String.class);
        Map<String, Map<String, Double>> appEpmMap = new HashMap<>(10);
        Map<String, AffEpmInfoModel> appInitEpmMap = new HashMap<>(10);
        Map<String, Double> appOperatorEpmMap = new HashMap<>(10);
        offerIdList.stream().forEach(a -> {
            String offerId = a;
            String redisKey = CacheNameSpace.AFF_EPM_COUNTER + ":app:" + appId + ":" + offerId + ":" + date;
            try {
                Map<Object, Object> epmCounterInfo = cluster3RedisTemplate.opsForHash().entries(redisKey);
                Object clickNum = epmCounterInfo.get(CacheNameSpace.CLICK);
                Object appTransNum = epmCounterInfo.get(CacheNameSpace.APP_TRANS);
                Object postBackTransNum = epmCounterInfo.get(CacheNameSpace.POST_BACK_TRANS);
                Object revenue = epmCounterInfo.get(CacheNameSpace.REVENUE);
                OfferModel testOfferModel = epmService.getTestOfferInfo(offerId);
                OfferModel offerModel = null;
                if (testOfferModel != null) {
                    offerModel = testOfferModel;
                } else {
                    offerModel = offerService.findOfferModel(offerId);
                }
                Object moNum = 0L;
                if (offerModel != null && !StringUtils.isEmpty(offerModel.getPayKeyword())) {
                    moNum = epmCounterInfo.get(CacheNameSpace.MO_TRANS);
                }
                AffEpmInfoModel affEpmInfoModel = getEpmInfoModel(appId, date, sdf, clickNum, appTransNum, postBackTransNum, offerId, offerModel, revenue, moNum);
                log.info("{} [EPM_SERVICE] [date:{}] [step-1-Iterator] KEY:{} [CATEGORY_ID:{} OFFER_ID:{} CLICK_NUM:{} APP_TRANS_NUM:{} POST_BACK_TRANS_NUM:{} REVENUE:{}] ",
                        LogConstant.ZOO, date, redisKey, appId, offerId, clickNum, appTransNum, postBackTransNum, revenue);
                Boolean existUnusedOffer = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_UNUSED_OFFER, offerId);
                if (existUnusedOffer != null && existUnusedOffer) {
                    AffEpmInfoModel unusedModel = getEpmInfoModel(appId, date, sdf, clickNum, appTransNum, postBackTransNum, offerId, offerModel, revenue, moNum);
                    updateUnusedEpm(unusedModel, isRetry);
                } else {
                    double epm = affEpmInfoModel.getEpm();
                    // 如果回调无法识别或者无回调,则设置点击信息（type=1 或者 type=3 均需要记录click 信息）
                    if (offerModel != null && offerModel.getCallbackType() == ZooConstant.CALLBACK_TYPE_3 && epm == 0) {
                        epm = offerModel.getInitEpm();
                        // 如果 epm 值为 0 则设置为 -1（在计算时计算为平均值）
                        if (epm == 0) {
                            epm = -1;
                        }
                    }
                    if (!isRetry) {
                        epm = recalculateEpm(offerModel, epm, redisKey);
                    }
                    appOperatorEpmMap.put(offerId, epm);
                    appInitEpmMap.put(appId + ZooConstant.COLON + offerId, affEpmInfoModel);
                    Double totalEpm = appOperatorEpmMap.get(ZooConstant.TOTAL);
                    if (epm >= 0) {
                        appOperatorEpmMap.put(ZooConstant.TOTAL, totalEpm == null ? epm : totalEpm + epm);
                    }
                    appEpmMap.put(appId + CacheNameSpace.COLON + offerModel.getOperator(), appOperatorEpmMap);
                }
            } catch (Exception e) {
                log.error("{} [EPM_SERVICE] [date:{}] [step-1-error] [CALCULATE EPM] [KEY:{}] [error:{}]", LogConstant.ZOO, date, redisKey, JSON.toJSONString(e));
            }
        });
        setEpmList(appEpmMap, appInitEpmMap, isRetry, date);
    }


    private void updateUnusedEpm(AffEpmInfoModel unusedModel, boolean isRetry) {
        try {
            if (isRetry) {
                affEpmInfoRepo.deleteEpm(unusedModel.getResourceId(), unusedModel.getOfferId(), unusedModel.getCalculateHour());
                affEpmInfoRepo.save(unusedModel);
            } else {
                affEpmInfoRepo.save(unusedModel);
            }
        }catch (Exception e){
            log.error("DELETE UNUSED OFFER ERROR:{}",e.getMessage());
        }

    }

    @SuppressFBWarnings({"NP_NULL_ON_SOME_PATH", "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE"})
    private double recalculateEpm(OfferModel offerModel, double epm, String redisKey) {
        double resetEpm = epm;
        Long clickNum = null;
        Double revenue = null;
        if (offerModel != null) {
            String country = offerModel.getCountry();
            Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.APP_EPM_RESET, country);
            if (obj != null) {
                AppEpmModel appEpmModel = JSON.parseObject(String.valueOf(obj), AppEpmModel.class);
                int calculateHours = appEpmModel.getEpmRestHour();
                double exponentiation = appEpmModel.getEpmRestExponentiation();
                if (calculateHours >= NumberEnum.ONE.getNum() && calculateHours <= NumberEnum.TWENTY_FOUR.getNum()) {
                    JSONObject json = handleBeforeHours(redisKey, calculateHours);
                    clickNum = json.getLong("click");
                    revenue = json.getDouble("revenue");
                }
                Float initEcpm = revenue == null || revenue == 0 ? 0F : new BigDecimal(revenue / clickNum).floatValue();
                resetEpm = new BigDecimal(Math.pow(initEcpm.doubleValue(), exponentiation)).doubleValue();
            }
        }
        return resetEpm;
    }

    @SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE"})
    private JSONObject handleBeforeHours(String originKey, int calculateHours) {
        Long clickNum = Long.valueOf(0L);
        Double revenue = Double.valueOf(0D);
        String lastHour = DateUtil.addTime(DateUtil.formatyyyyMMddHHmmss(new Date()), DateConstant.HOUR_STR, -1).split(ZooConstant.COLON)[0];
        String currentHour = lastHour.split(" ")[0] + "-" + lastHour.split(" ")[1];
        for (int index = 1; index <= calculateHours; index++) {
            String calDate = DateUtil.addTime(DateUtil.formatyyyyMMddHHmmss(new Date()), DateConstant.HOUR_STR, -index).split(ZooConstant.COLON)[0];
            String date = calDate.split(" ")[0] + "-" + calDate.split(" ")[1];
            String key = originKey.replace(currentHour, date);
            Long cn = stringRedisTemplate.opsForHash().increment(key, CacheNameSpace.CLICK, 0L);
            Double rev = stringRedisTemplate.opsForHash().increment(key, CacheNameSpace.REVENUE, 0D);
            clickNum = cn == null ? clickNum : cn.longValue() + clickNum.longValue();
            revenue = rev == null ? revenue : rev.doubleValue() + revenue.doubleValue();
        }
        JSONObject json = new JSONObject();
        json.put("click", clickNum);
        json.put("revenue", revenue);
        return json;
    }

    @SuppressFBWarnings({"NP_NULL_ON_SOME_PATH","DM_BOXED_PRIMITIVE_FOR_PARSING"})
    private AffEpmInfoModel getEpmInfoModel(String categoryId, String calculateTime, SimpleDateFormat sdf,
                                            Object clickNumObj, Object appTransNum, Object postBackTransNumObj, String offerId, OfferModel offerModel,
                                            Object revenueObj, Object moNumObj) {
        Long postBackTransNum = (postBackTransNumObj != null ? Long.valueOf(postBackTransNumObj.toString()) : 0L);
        Long clickNum = (clickNumObj != null ? Long.valueOf(clickNumObj.toString()) : 0L);
        Double revenue = (revenueObj != null ? Double.valueOf(revenueObj.toString()) : 0);
        Long moNum = (moNumObj != null ? Long.valueOf(moNumObj.toString()) : 0L);
        AffEpmInfoModel affEpmInfoModel = new AffEpmInfoModel();
        affEpmInfoModel.setCalculateHour(calculateTime);
        String createHourTime = calculateTime.substring(0, NumberEnum.TEN.getNum()) + " " + calculateTime.substring(NumberEnum.ELEVEN.getNum(), calculateTime.split("").length);
        affEpmInfoModel.setCreateTime(DateUtil.formatHourTime(createHourTime));
        affEpmInfoModel.setClickNum(clickNum);
        affEpmInfoModel.setAppTransNum(appTransNum != null ? Long.valueOf(appTransNum.toString()) : 0L);
        affEpmInfoModel.setTransNum(postBackTransNum);
        affEpmInfoModel.setResourceId(categoryId);
        ApplicationModel appModel = applicationService.getById(categoryId);
        affEpmInfoModel.setResourceName(appModel != null ? appModel.getAppName() : ZooConstant.UNKNOWN);
        affEpmInfoModel.setResourceType(ZooConstant.CATEGORY_APP);
        double epm = 0d;
        affEpmInfoModel.setOfferId(offerId);
        if (offerModel != null) {
            if (!StringUtils.isEmpty(offerModel.getPayShortCode()) && !StringUtils.isEmpty(offerModel.getPayKeyword())) {
                affEpmInfoModel.setMoNum(moNum != null ? Long.valueOf(moNum.toString()) : 0L);
            } else {
                affEpmInfoModel.setMoNum(0L);
            }
            affEpmInfoModel.setOfferName(offerModel.getOfferName());
            affEpmInfoModel.setPartner(offerModel.getPartner());
            affEpmInfoModel.setCountry(offerModel.getCountry());
            affEpmInfoModel.setOperator(offerModel.getOperator());

            if (revenue == null || revenue == 0) {
                double payout = offerModel.getPayout() != null ? offerModel.getPayout() : 0d;
                if (!StringUtils.isEmpty(offerModel.getPayShortCode())) {
                    if (moNum != null) {
                        revenue = payout * moNum;
                    }
                } else {
                    revenue = payout * postBackTransNum;
                }
                affEpmInfoModel.setRevenue(revenue);
            }
        }
        if (revenue != null && clickNum != null && clickNum > 0) {
            //保留三位小数
            epm = new BigDecimal(revenue / clickNum).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        affEpmInfoModel.setRevenue(revenue);
        affEpmInfoModel.setBelong(offerModel.getBelong());
        affEpmInfoModel.setEpm(epm);
        return affEpmInfoModel;
    }

    /**
     * 设置 epm list
     *
     * @param epmMap
     */
    @Async
    public void setEpmList(Map<String, Map<String, Double>> epmMap, Map<String, AffEpmInfoModel> initEpmMap, boolean isRetry, String date) {
        if (epmMap != null && epmMap.size() > 0) {
            //遍历 epmMap 设置 epm list
            for (Map.Entry<String, Map<String, Double>> entry : epmMap.entrySet()) {
                try {
                    //key:【aff_epm_list:app-identification:operator:list】
                    String appIdOperator = entry.getKey();
                    Map<String, Double> offerEpmMap = entry.getValue();
                    setEachOfferEpm(appIdOperator, offerEpmMap, initEpmMap, isRetry, date);
                } catch (Exception e) {
                    log.error("{} [EPM_SERVICE] [date:{}] [step-3-error] [CALCULATE EPM] [ALL_APP_EPM_MAP:{}] ERROR:{}", LogConstant.ZOO, date, JSON.toJSONString(epmMap), JSON.toJSONString(e));
                }
            }
            log.info("{} [EPM_SERVICE] [date:{}] [step-4] [CALCULATE EPM] [ALL_APP_EPM_MAP:{}]", LogConstant.ZOO, date, JSON.toJSONString(epmMap));
        }
    }

    private void setEachOfferEpm(String appIdOperator, Map<String, Double> offerEpmMap, Map<String, AffEpmInfoModel> initEpmMap, boolean isRetry, String date) {
        // zoo_aff_epm_list:appId:TH_AIS:list
        String epmListKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appIdOperator + CacheNameSpace.COLON + CacheNameSpace.LIST;
        log.info("{} [EPM_SERVICE] [date:{}] [step-3-0] [SET EPM_LIST] [CATEGORY_ID:{}] [\"OFFER_EPM_MAP\":{}, \"INIT_EPM_MAP\":{}]",
                LogConstant.ZOO, date, appIdOperator, JSON.toJSONString(offerEpmMap), JSON.toJSONString(initEpmMap));
        if (offerEpmMap != null && offerEpmMap.size() > 1) {
            Double totalEpm = offerEpmMap.get(ZooConstant.TOTAL);
            //遍历，将 epm = -n <0时 即：该offer不支持回传或者无法识别的情况下, 将取一个占比n的值 默认占比为 epm = -1 时取 总 epm 的平均 epm 值
            for (Map.Entry<String, Double> offerEntry : offerEpmMap.entrySet()) {
                try {
                    Double initEpmPercent = offerEntry.getValue();
                    Double initTotalEpm = offerEpmMap.get(ZooConstant.TOTAL);
                    if (initEpmPercent == -1) {
                        //设置为总 epm 的平均值(出去一个total)
                        initEpmPercent = initTotalEpm / (offerEpmMap.size() - 1);
                        offerEntry.setValue(initEpmPercent);
                        // 重新计算 total 值
                        totalEpm += initEpmPercent;
                    }
                    //修改 totalEpm 的值
                    if (!ZooConstant.TOTAL.equals(offerEntry.getKey())) {
                        try {
                            saveInitEpm(appIdOperator, offerEntry.getKey(), initEpmPercent, initEpmMap, isRetry, date);
                        } catch (Exception e) {
                            log.info("{} [EPM_SERVICE] [date:{}] [step-3-error:SAVE_EPM_MODEL] [SET EPM_LIST] [EPM_MODEL:{}] ERROR:{}", LogConstant.ZOO, date, JSON.toJSONString(initEpmMap.get(offerEntry.getKey())), e.getMessage(), e);
                        }
                    }
                } catch (Exception e) {
                    log.error("{} [EPM_SERVICE] [date:{}] [step-3-error-1] [SET EPM_LIST] [CATEGORY_ID:{}] ERROR:{}", LogConstant.ZOO, date, appIdOperator, JSON.toJSONString(e));
                    throw e;
                }
            }
            if (isRetry) {
                // 如果是 retry 则返回
                return;
            }
            try {
                handleRestEpmList(offerEpmMap, appIdOperator, totalEpm, epmListKey, date);
            } catch (Exception e) {
                throw e;
            }
        }
    }

    @SuppressFBWarnings("NP_NULL_PARAM_DEREF")
    private void handleRestEpmList(Map<String, Double> offerEpmMap, String appIdOperator, Double totalEpm, String epmListKey, String date) {
        // map转换成list进行排序
        List<Map.Entry<String, Double>> entryList = new ArrayList<>(offerEpmMap.entrySet());
        //排序
        Collections.sort(entryList, getMapSorter());
        log.info("{} [EPM_SERVICE] [date:{}] [step-3-1] [SET EPM_LIST] [CATEGORY_ID:{}] [OFFER_EPM_MAP:{}] [INIT_TOTAL_EPM:{}]", LogConstant.ZOO, date, appIdOperator, JSON.toJSONString(offerEpmMap), totalEpm);
        //生成新的EPM list
        List<String> generateEpmList = new ArrayList<>();
        for (Map.Entry<String, Double> offerEntry : entryList) {
            try {
                String offerId = offerEntry.getKey();
                // 是否在跑量时间段内
                if (getIsTime(offerId)) {
                    boolean isEnableSetRedisList = !ZooConstant.TOTAL.equals(offerEntry.getKey()) && !StringUtils.isEmpty(offerEntry.getKey()) && !ZooConstant.UNKNOWN.equals(offerEntry.getKey());
                    if (isEnableSetRedisList) {
                        int epmBaseNum = 0;
                        if (totalEpm != null && totalEpm > NumberEnum.ONE.getNum()) {
                            //基数超过 100
                            epmBaseNum = new BigDecimal(offerEntry.getValue() / totalEpm * NumberEnum.ONE_HUNDRED.getNum()).setScale(0, BigDecimal.ROUND_UP).intValue();
                            if (epmBaseNum == 0) {
                                epmBaseNum = NumberEnum.ONE.getNum();
                            }
                        } else {
                            BigDecimal templateNum = getTemplateNum(totalEpm);
                            if (offerEntry.getValue() == 0) {
                                epmBaseNum = 1;
                            } else {
                                log.info("EPM RESET OFFER ID:{},OFFER EPM :{},TEMPLATE NUM:{}", offerId, offerEntry.getValue(), templateNum);
                                epmBaseNum = new BigDecimal(offerEntry.getValue()).multiply(templateNum).setScale(0, BigDecimal.ROUND_UP).intValue();
                            }
                            // 单个offer在EpmList长度不会超过100
                            epmBaseNum = checkEpmBaseNum(epmBaseNum, totalEpm, offerEntry.getValue(), appIdOperator, offerEntry.getKey(), epmListKey);
                        }
                        for (int i = 0; i < epmBaseNum; i++) {
                            generateEpmList.add(String.valueOf(offerEntry.getKey()));
                        }
                    }
                }
            } catch (Exception e) {
                log.error("{} [EPM_SERVICE] [date:{}] [step-3-error-2] [SET EPM_LIST] [CATEGORY_ID:{}] ERROR:{}", LogConstant.ZOO, date, appIdOperator, JSON.toJSONString(e));
                throw e;
            }
        }
        //移除元素
        stringRedisTemplate.opsForList().trim(epmListKey, -1, 0);
        // 当所有条件都不符合 generateEpmList 会为null不保存
        if (generateEpmList != null && generateEpmList.size() > 0) {
            stringRedisTemplate.opsForList().rightPushAll(epmListKey, generateEpmList);
            //设置 ttl
            stringRedisTemplate.expire(epmListKey, NumberEnum.ONE.getNum(), TimeUnit.DAYS);
        }
        log.info("{} [EPM_SERVICE] [date:{}] [step-3-2] [SET EPM_LIST] [CATEGORY_ID:{}] [OFFER_EPM_MAP:{}]", LogConstant.ZOO, date, appIdOperator, JSON.toJSONString(offerEpmMap));
    }

    private int checkEpmBaseNum(int epmBaseNum, Double totalEpm, Double epm, String appIdOperator, String offerId, String epmListKey) {
        int errorNum = epmBaseNum;
        if (epmBaseNum > NumberEnum.ONE_HUNDRED.getNum()) {
            epmBaseNum = new BigDecimal(epm / totalEpm * NumberEnum.ONE_HUNDRED.getNum()).setScale(0, BigDecimal.ROUND_UP).intValue();
            if (epmBaseNum == 0) {
                epmBaseNum = NumberEnum.ONE.getNum();
            }
            Map<String, Object> epmErrorInfo = generateEpmListErrorInfo(errorNum, epmBaseNum, appIdOperator, offerId, epmListKey);
            sendEpmListExceptionMail(epmErrorInfo);
        }
        return epmBaseNum;
    }

    private Map<String, Object> generateEpmListErrorInfo(int errorNum, int epmBaseNum, String appIdOperator, String offerId, String epmListKey) {
        Map<String, Object> contentModel = new HashMap<>(9);
        contentModel.put(ZooConstant.TITLE, ZooConstant.EPM_LIST_EXCEPTION_SUBJECT);
        contentModel.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
        contentModel.put(ZooConstant.APP_ID, appIdOperator.split(":")[0]);
        contentModel.put(ZooConstant.OPERATOR, appIdOperator.split(":")[1]);
        contentModel.put(ZooConstant.ERROR_EPM_LIST_SIZE, errorNum);
        contentModel.put(ZooConstant.RE_CALCUATE_EPM_LIST_SIZE, epmBaseNum);
        contentModel.put(ZooConstant.EPM_LIST_KEY, epmListKey);
        contentModel.put(ZooConstant.OFFER_ID, offerId);
        contentModel.put(ZooConstant.EMAIL_UUID, UUID.randomUUID().toString());
        return contentModel;
    }


    private BigDecimal getTemplateNum(Double totalEpm) {
        BigDecimal templateNum = new BigDecimal(NumberEnum.ONE_HUNDRED.getNum());
        int index = 1;
        while (true) {
            BigDecimal totalNum = (new BigDecimal(totalEpm).multiply(templateNum).multiply(new BigDecimal(NumberEnum.TEN.getNum())));
            index++;
            if (totalNum.compareTo(new BigDecimal(NumberEnum.ONE_HUNDRED.getNum())) == -1) {
                templateNum = templateNum.multiply(new BigDecimal(NumberEnum.TEN.getNum()));
            } else {
                break;
            }
            if (index > NumberEnum.TWENTY.getNum()) {
                break;
            }
        }
        return templateNum;
    }


    public boolean getIsTime(String offerId) {
        boolean inTime = true;
        Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId);
        if (obj != null) {
            OfferModel offerModel = JSON.parseObject(String.valueOf(obj), OfferModel.class);
            //判断是否在可跑时间段内
            String date = DateUtil.getHourByTimeZone(new Date(), ZooConstant.GMT_8).split("\\s+")[1];
            if (!StringUtils.isEmpty(offerModel.getTimeRange()) && offerModel.getTimeRange().split(ZooConstant.COMMA).length > 0) {
                String[] timeRange = offerModel.getTimeRange().split(",");
                if(timeRange.length == NumberEnum.TWO.getNum()){
                    inTime = Integer.parseInt(date) >= Integer.parseInt(timeRange[0]) && Integer.parseInt(date) <= Integer.parseInt(timeRange[1]);
                }
            }
            log.info("epm pull offerModel check is inTime :{}, result:{}", JSON.toJSONString(offerModel), inTime);
        }
        return inTime;
    }

    @Async
    public void saveInitEpm(String appIdOperator, String offerId, double initEpm, Map<String, AffEpmInfoModel> initEpmMap, boolean isRetry, String date) {
        log.info("{} [EPM_SERVICE] [date:{}] [step-3-0-iterator:SAVE_EPM_MODEL} [{\"offerId\":{}, \"initEpm\":{}}]", LogConstant.ZOO, date, offerId, initEpm);
        if (!StringUtils.isEmpty(offerId) && initEpmMap != null && initEpmMap.size() > 0) {
            // categoryId:offerId
            String appId = appIdOperator.split(":")[0];
            AffEpmInfoModel affEpmInfoModel = initEpmMap.get(appId + ZooConstant.COLON + offerId);
            try {
                if (affEpmInfoModel != null) {
                    log.info("{} [EPM_SERVICE] [date:{}] [step-3-0-iterator:SAVE_EPM_MODEL] [SET EPM_LIST] [CATEGORY_ID:{} OFFER_ID:{}] [EPM_MODEL:{}] [isRetry:{}]",
                            LogConstant.ZOO, date, affEpmInfoModel.getResourceId(), affEpmInfoModel.getOfferId(), JSON.toJSONString(affEpmInfoModel), isRetry);
                    if (isRetry) {
                        log.info("{} [EPM_SERVICE RETRY DELETE MODEL] [date:{}] [step-3-0-iterator:SAVE_EPM_MODEL] [SET EPM_LIST] [CATEGORY_ID:{} OFFER_ID:{}] [EPM_MODEL:{}] ",
                                LogConstant.ZOO, date, affEpmInfoModel.getResourceId(), affEpmInfoModel.getOfferId(), JSON.toJSONString(affEpmInfoModel));
                        affEpmInfoRepo.deleteEpm(affEpmInfoModel.getResourceId(), affEpmInfoModel.getOfferId(), affEpmInfoModel.getCalculateHour());
                    }
                    affEpmInfoModel.setEpm(initEpm);
                    affEpmInfoRepo.save(affEpmInfoModel);
                }
            } catch (Exception e) {
                log.error("EPM SERVICE RETRY CALCULATE EPM MODEL:{}, ERROR:{}", JSON.toJSONString(affEpmInfoModel), JSON.toJSONString(e));
            }

        }
    }

    private Comparator<Map.Entry<String, Double>> getMapSorter() {
        // 降序比较器
        return new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                // TODO Auto-generated method stub
                return o2.getValue().compareTo(o1.getValue());
            }
        };
    }

    @Override
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    public List<OptionVO> getResourceNames(List<String> country, String operator, List<String> partner) {
        String sql = "select distinct(resource_name) from t_aff_epm where 1=1";
        if (country != null && country.size() > 0) {
            sql += " and country IN :country";
        }
        if (!StringUtils.isEmpty(operator)) {
            sql += " and operator=:operator";
        }
        if (partner != null && partner.size() > 0) {
            sql += " and partner IN :partner";
        }
        sql += " ORDER BY resource_name ASC";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        if (country != null && country.size() > 0) {
            nativeQuery.setParameter("country", country);
        }
        if (!StringUtils.isEmpty(operator)) {
            nativeQuery.setParameter("operator", operator);
        }
        if (partner != null && partner.size() > 0) {
            nativeQuery.setParameter("partner", partner);
        }
        List<String> appNameList = nativeQuery.getResultList();
        List<String> allAppNames = applicationRepo.findStartAppNames();
        allAppNames.removeAll(appNameList);
        allAppNames.addAll(appNameList);
        List<OptionVO> optionVOS = new ArrayList<>();
        if (allAppNames != null && allAppNames.size() > 0) {
            for (String name : allAppNames) {
                optionVOS.add(new OptionVO(name, name, name));
            }
        }
        return optionVOS;
    }

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @Override
    public List<OptionVO> getOfferName(List<String> country, String operator, List<String> partner, List<String> resourceNames) {
        List<String> offerNameList = null;
        if (resourceNames != null && resourceNames.size() > 0) {
            String sql = "select distinct(offer_name) from t_aff_epm where 1=1";
            if (country != null && country.size() > 0) {
                sql += " and country IN :country";
            }
            if (!StringUtils.isEmpty(operator)) {
                sql += " and operator=:operator";
            }
            if (partner != null && partner.size() > 0) {
                sql += " and partner IN :partner";
            }
            if (resourceNames.size() > 0) {
                sql += " AND resource_name in :resourceName";
            }
            sql += " ORDER BY offer_name ASC";
            Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
            nativeQuery.unwrap(NativeQuery.class);
            if (country != null && country.size() > 0) {
                nativeQuery.setParameter("country", country);
            }
            if (!StringUtils.isEmpty(operator)) {
                nativeQuery.setParameter("operator", operator);
            }
            if (partner != null && partner.size() > 0) {
                nativeQuery.setParameter("partner", partner);
            }
            if (resourceNames.size() > 0) {
                nativeQuery.setParameter("resourceName", resourceNames);
            }
            offerNameList = nativeQuery.getResultList();
        } else {
            CriteriaBuilder criteriaBuilder = zooEntityManager.getCriteriaBuilder();
            CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
            //具体实体的Root
            Root<OfferModel> root = criteriaQuery.from(OfferModel.class);
            criteriaQuery.select(root.get("offerName"));
            offerNameList = zooEntityManager.createQuery(criteriaQuery).getResultList();
        }
        List<String> testOfferNames = getTestOfferNames();
        offerNameList.addAll(testOfferNames);
        List<OptionVO> optionVOS = new ArrayList<>();
        if (offerNameList != null && offerNameList.size() > 0) {
            for (String name : offerNameList) {
                optionVOS.add(new OptionVO(name, name, name));
            }
        }

        return optionVOS;
    }

    private List<String> getTestOfferNames() {
        List<String> names = new ArrayList<>();
        List<Object> offerIds = new ArrayList<>(cluster3RedisTemplate.opsForHash().keys(ZooConstant.ZOO_TEST_OFFER_INFO));
        if (offerIds != null && offerIds.size() > 0) {
            for (Object offerId : offerIds) {
                OfferModel offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(ZooConstant.ZOO_TEST_OFFER_INFO, offerId)), OfferModel.class);
                names.add(offerModel.getOfferName());
            }
        }
        return names;
    }

    @Override
    public List<OptionVO> getSystemIds(List<String> country, String operator, List<String> partner, List<String> resourceNames) {
        String sql = "select t.identification, t.offer_id from t_offer t where  t.identification in " +
                "(select distinct(offer_id) from t_aff_epm where 1=1";
        if (country != null && country.size() > 0) {
            sql += " and country IN :country";
        }
        if (!StringUtils.isEmpty(operator)) {
            sql += " and operator=:operator";
        }
        if (partner != null && partner.size() > 0) {
            sql += " and partner IN :partner";
        }
        if (resourceNames.size() > 0) {
            sql += " AND resource_name in :resourceName";
        }
        sql += ") ORDER BY t.offer_id ASC";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        if (country != null && country.size() > 0) {
            nativeQuery.setParameter("country", country);
        }
        if (!StringUtils.isEmpty(operator)) {
            nativeQuery.setParameter("operator", operator);
        }
        if (partner != null && partner.size() > 0) {
            nativeQuery.setParameter("partner", partner);
        }
        if (resourceNames.size() > 0) {
            nativeQuery.setParameter("resourceName", resourceNames);
        }
        List<Object[]> offerNameList = nativeQuery.getResultList();
        List<OptionVO> testOfferVos = getTestOfferVOS();
        List<OptionVO> optionVOS = new ArrayList<>();
        if (offerNameList != null && offerNameList.size() > 0) {
            for (Object[] object : offerNameList) {
                String id = (String) object[0];
                String systemOfferId = (String) object[1];
                optionVOS.add(new OptionVO(id, systemOfferId, id));
            }
        }
        optionVOS.addAll(testOfferVos);
        return optionVOS;
    }

    private List<OptionVO> getTestOfferVOS() {
        List<OptionVO> optionVOS = new ArrayList<>();
        List<Object> offerIds = new ArrayList<>(cluster3RedisTemplate.opsForHash().keys(ZooConstant.ZOO_TEST_OFFER_INFO));
        if (offerIds != null && offerIds.size() > 0) {
            for (Object offerId : offerIds) {
                OfferModel offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(ZooConstant.ZOO_TEST_OFFER_INFO, offerId)), OfferModel.class);
                optionVOS.add(new OptionVO(offerModel.getIdentification(), offerModel.getOfferId(), offerModel.getIdentification()));
            }
        }
        return optionVOS;
    }


    @Override
    public List<OptionVO> getPartnerIds(List<String> country, String operator, List<String> partner, List<String> resourceNames) {
        String sql = "select t.identification, t.partner_offer_id from t_offer t where  t.identification in " +
                "(select distinct(offer_id) from t_aff_epm where 1=1";
        if (country != null && country.size() > 0) {
            sql += " and country IN :country";
        }
        if (!StringUtils.isEmpty(operator)) {
            sql += " and operator=:operator";
        }
        if (partner != null && partner.size() > 0) {
            sql += " and partner IN :partner";
        }
        if (resourceNames.size() > 0) {
            sql += " AND resource_name in :resourceName";
        }
        sql += ") ORDER BY t.partner_offer_id ASC";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        if (country != null && country.size() > 0) {
            nativeQuery.setParameter("country", country);
        }
        if (!StringUtils.isEmpty(operator)) {
            nativeQuery.setParameter("operator", operator);
        }
        if (partner != null && partner.size() > 0) {
            nativeQuery.setParameter("partner", partner);
        }
        if (resourceNames.size() > 0) {
            nativeQuery.setParameter("resourceName", resourceNames);
        }
        List<Object[]> offerNameList = nativeQuery.getResultList();
        List<OptionVO> optionVOS = new ArrayList<>();
        if (offerNameList != null && offerNameList.size() > 0) {
            for (Object[] object : offerNameList) {
                String id = (String) object[0];
                String partnerOfferId = (String) object[1];
                if (!StringUtils.isEmpty(partnerOfferId)) {
                    optionVOS.add(new OptionVO(id, partnerOfferId, id));
                }
            }
        }
        return optionVOS;
    }

    @Override
    public PageVO getEpmRecord(List<String> country, String operator,
                               List<String> partner, List<String> resourceNames, String offerName,
                               List<String> offerIds, String beginStr, String endStr, String timezone,
                               Integer showType, Boolean showLimitPull, String belong, String queryType) throws Exception {
        if (showLimitPull != null && showLimitPull) {
            resetOfferIds(offerIds);
        }
        boolean showExpend = !StringUtils.isEmpty(offerName) || offerIds != null && offerIds.size() > 0;
        long beginTime = DateUtil.times2tamptoLong(beginStr, timezone);
        long endTime = DateUtil.times2tamptoLong(endStr, timezone);
        Date begin = DateUtil.getDateByTimeZone(beginTime, ZooConstant.GMT_0);
        Date end = DateUtil.getDateByTimeZone(endTime, ZooConstant.GMT_0);
        List<AffEpmInfoModel> list = getOfflineModels(country, operator, partner, resourceNames, offerName, offerIds, begin, end, timezone, showType, belong, showExpend, queryType);
        // 判断是否获取实时
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);
        Calendar calendar = getCurrentHourCalendar();
        if (end.after(Calendar.getInstance().getTime())) {
            // 查找当前的 offer
            List<AffEpmInfoModel> affCurrentEpmVos = getCurrentHourEpms(calendar, country, operator, partner, resourceNames, offerName, offerIds, showType, belong, timezone, queryType);
            // 合并结果
            list = getCombinedList(list, affCurrentEpmVos, showType, queryType);
        }
        List<AffEpmInfoModel> sortList = sortEpmList(showType, list);
        List<AffEpmInfoModel> updateEpmList = updateEpmModel(sortList, queryType, showType);
        PageVO<AffEpmInfoModel> pageVO = new PageVO<>();
        pageVO.setTotal(Long.valueOf(updateEpmList.size()));
        pageVO.setList(sortList);
        return pageVO;
    }

    private List<AffEpmInfoModel> updateEpmModel(List<AffEpmInfoModel> list, String queryType, Integer showType) {
        for (AffEpmInfoModel epmInfoModel : list) {
            OfferModel offerModel = offerService.getOfferModelByRedis(epmInfoModel.getOfferId());
            if (offerModel != null) {
                epmInfoModel.setOfferStatus(offerModel.getStatus());
                epmInfoModel.setCap(Long.valueOf(offerModel.getCap()));
                epmInfoModel.setSystemOfferId(offerModel.getOfferId());
                epmInfoModel.setPartnerOfferId(offerModel.getPartnerOfferId());
                // 如果是根据app查询并且查询类型是payment,则postback 为0
                if (showType == ZooConstant.EPM_SHOW_TYPE_APP) {
                    if (!StringUtils.isEmpty(queryType) && queryType.equalsIgnoreCase(ZooConstant.PAYMENT)) {
                        epmInfoModel.setTransNum(Long.valueOf(NumberEnum.ZERO.getNum()));
                    }
                }
            }
        }
        return list;
    }

    private List<AffEpmInfoModel> getOfflineModels(List<String> countries, String operator, List<String> partners, List<String> resourceNames, String offerName, List<String> offerIds, Date begin, Date end, String timezone, Integer showType, String belong, boolean showExpend, String queryType) throws Exception {
        String beginMonth = DateUtil.formatMonth(begin);
        String endMonth = DateUtil.formatMonth(end);
        String endTime = DateUtil.formatyyyyMMddHHmmss(end);
        String beginTime = DateUtil.formatyyyyMMddHHmmss(begin);
        List<AffEpmInfoModel> list = new ArrayList<>();
        String tableName = "";
        // 2021年之前的数据查询的是t_aff_epm2020 表
        if (DateUtil.isBefore(end, DateUtil.formatTime(ZooConstant.TIME_2020))) {
            tableName = ZooConstant.EPM_INFO_TABLE_NAME + "2020";
            list.addAll(generateEpmInfoList(tableName, countries, operator, partners, resourceNames, offerName, offerIds, beginTime, endTime, timezone, showType, belong, showExpend, queryType));
        } else {
            // 查询日期跨月
            if (!beginMonth.equalsIgnoreCase(endMonth)) {
                int monthCount = 0;
                if (DateUtil.isBefore(begin, DateUtil.formatTime(ZooConstant.TIME_2020))) {
                    tableName = ZooConstant.EPM_INFO_TABLE_NAME + ZooConstant.YEAR_2020;
                    list = generateEpmInfoList(tableName, countries, operator, partners, resourceNames, offerName, offerIds, beginTime, ZooConstant.TIME_2020, timezone, showType, belong, showExpend, queryType);
                    monthCount = DateUtil.getMonthSpace(ZooConstant.TIME_2020, DateUtil.formatyyyyMMddHHmmss(end));
                    beginMonth = ZooConstant.TIME_2020.substring(0, 7);
                } else {
                    monthCount = DateUtil.getMonthSpace(DateUtil.formatyyyyMMddHHmmss(begin), DateUtil.formatyyyyMMddHHmmss(end));
                }
                for (int i = 0; i <= monthCount; i++) {
                    String monthStr = DateUtil.addMonth(beginMonth, i);
                    if (monthStr.split("-")[0].equalsIgnoreCase(ZooConstant.YEAR_2020)) {
                        continue;
                    }
                    tableName = ZooConstant.EPM_INFO_TABLE_NAME + monthStr.replaceAll("-", "");
                    if (DateUtil.formatMonth(new Date()).equalsIgnoreCase(monthStr)) {
                        tableName = ZooConstant.EPM_INFO_TABLE_NAME;
                    }
                    list.addAll(generateEpmInfoList(tableName, countries, operator, partners, resourceNames, offerName, offerIds, beginTime, endTime, timezone, showType, belong, showExpend, queryType));
                }
            } else {
                // 查询时间不跨月
                if (DateUtil.formatMonth(new Date()).equalsIgnoreCase(beginMonth)) {
                    tableName = ZooConstant.EPM_INFO_TABLE_NAME;
                } else {
                    tableName = ZooConstant.EPM_INFO_TABLE_NAME + beginMonth.replaceAll("-", "");
                }
                list.addAll(generateEpmInfoList(tableName, countries, operator, partners, resourceNames, offerName, offerIds, beginTime, endTime, timezone, showType, belong, showExpend, queryType));
            }
        }
        list = combineEpmList(list, showType, queryType);
        return list;
    }

    private List<AffEpmInfoModel> combineEpmList(List<AffEpmInfoModel> list, Integer showType, String queryType) {
        List<AffEpmInfoModel> newList = new ArrayList<>();
        if (showType == ZooConstant.EPM_SHOW_TYPE_DAY || showType == ZooConstant.EPM_SHOW_TYPE_HOUR) {
            newList = list.stream().collect(Collectors.toMap(k -> k.getDate() + "_" + k.getOfferId() + "_" + k.getOfferName(), a -> a, (a1, a2) -> {
                a1.setClickNum((a1.getClickNum() == null ? 0 : a1.getClickNum()) + (a2.getClickNum() == null ? 0 : a2.getClickNum()));
                a1.setAppTransNum((a1.getAppTransNum() == null ? 0 : a1.getAppTransNum()) + (a2.getAppTransNum() == null ? 0 : a2.getAppTransNum()));
                generateTrans(a1, a2, queryType);
                a1.setRevenue((a1.getRevenue() == null ? 0 : a1.getRevenue()) + (a2.getRevenue() == null ? 0 : a2.getRevenue()));
                if (a1.getClickNum() != 0) {
                    a1.setEpm(a1.getRevenue() / a1.getClickNum());
                }
                return a1;
            })).values().stream().collect(Collectors.toList());
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_OFFER) {
            newList = list.stream().collect(Collectors.toMap(k -> k.getIdentification() + "_" + k.getPartner() + "_"
                    + k.getCountry() + "_" + k.getOperator() + "_" + k.getOfferId(), a -> a, (a1, a2) -> {
                a1.setClickNum((a1.getClickNum() == null ? 0 : a1.getClickNum()) + (a2.getClickNum() == null ? 0 : a2.getClickNum()));
                a1.setAppTransNum((a1.getAppTransNum() == null ? 0 : a1.getAppTransNum()) + (a2.getAppTransNum() == null ? 0 : a2.getAppTransNum()));
                generateTrans(a1, a2, queryType);
                a1.setRevenue((a1.getRevenue() == null ? 0 : a1.getRevenue()) + (a2.getRevenue() == null ? 0 : a2.getRevenue()));
                if (a1.getClickNum() != 0) {
                    a1.setEpm(a1.getRevenue() / a1.getClickNum());
                }
                return a1;
            })).values().stream().collect(Collectors.toList());
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_COUNTRY) {
            newList = list.stream().collect(Collectors.toMap(AffEpmInfoModel::getCountry, a -> a, (a1, a2) -> {
                a1.setClickNum((a1.getClickNum() == null ? 0 : a1.getClickNum()) + (a2.getClickNum() == null ? 0 : a2.getClickNum()));
                a1.setAppTransNum((a1.getAppTransNum() == null ? 0 : a1.getAppTransNum()) + (a2.getAppTransNum() == null ? 0 : a2.getAppTransNum()));
                generateTrans(a1, a2, queryType);
                a1.setRevenue((a1.getRevenue() == null ? 0 : a1.getRevenue()) + (a2.getRevenue() == null ? 0 : a2.getRevenue()));
                if (a1.getClickNum() != 0) {
                    a1.setEpm(a1.getRevenue() / a1.getClickNum());
                }
                return a1;
            })).values().stream().collect(Collectors.toList());
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_PARTNER) {
            newList = list.stream().collect(Collectors.toMap(AffEpmInfoModel::getPartner, a -> a, (a1, a2) -> {
                a1.setClickNum((a1.getClickNum() == null ? 0 : a1.getClickNum()) + (a2.getClickNum() == null ? 0 : a2.getClickNum()));
                a1.setAppTransNum((a1.getAppTransNum() == null ? 0 : a1.getAppTransNum()) + (a2.getAppTransNum() == null ? 0 : a2.getAppTransNum()));
                generateTrans(a1, a2, queryType);
                a1.setRevenue((a1.getRevenue() == null ? 0 : a1.getRevenue()) + (a2.getRevenue() == null ? 0 : a2.getRevenue()));
                if (a1.getClickNum() != 0) {
                    a1.setEpm(a1.getRevenue() / a1.getClickNum());
                }
                return a1;
            })).values().stream().collect(Collectors.toList());
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_APP) {
            newList = list.stream().collect(Collectors.toMap(k -> k.getIdentification() + "_" + k.getPartner() + "_"
                    + k.getCountry() + "_" + k.getOperator() + "_" + k.getResourceName() + "_" + k.getOfferId(), a -> a, (a1, a2) -> {
                a1.setClickNum((a1.getClickNum() == null ? 0 : a1.getClickNum()) + (a2.getClickNum() == null ? 0 : a2.getClickNum()));
                a1.setAppTransNum((a1.getAppTransNum() == null ? 0 : a1.getAppTransNum()) + (a2.getAppTransNum() == null ? 0 : a2.getAppTransNum()));
                generateTrans(a1, a2, queryType);
                a1.setRevenue((a1.getRevenue() == null ? 0 : a1.getRevenue()) + (a2.getRevenue() == null ? 0 : a2.getRevenue()));
                if (a1.getClickNum() != 0) {
                    a1.setEpm(a1.getRevenue() / a1.getClickNum());
                }
                return a1;
            })).values().stream().collect(Collectors.toList());
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_OPERATOR) {
            newList = list.stream().collect(Collectors.toMap(AffEpmInfoModel::getOperator, a -> a, (a1, a2) -> {
                a1.setClickNum((a1.getClickNum() == null ? 0 : a1.getClickNum()) + (a2.getClickNum() == null ? 0 : a2.getClickNum()));
                a1.setAppTransNum((a1.getAppTransNum() == null ? 0 : a1.getAppTransNum()) + (a2.getAppTransNum() == null ? 0 : a2.getAppTransNum()));
                generateTrans(a1, a2, queryType);
                a1.setRevenue((a1.getRevenue() == null ? 0 : a1.getRevenue()) + (a2.getRevenue() == null ? 0 : a2.getRevenue()));
                if (a1.getClickNum() != 0) {
                    a1.setEpm(a1.getRevenue() / a1.getClickNum());
                }
                return a1;
            })).values().stream().collect(Collectors.toList());
        }
        return newList;
    }

    private void generateTrans(AffEpmInfoModel a1, AffEpmInfoModel a2, String queryType) {
        if (queryType.equals(ZooConstant.PAYMENT)) {
            a1.setMoNum((a1.getMoNum() == null ? 0 : a1.getMoNum()) + (a2.getMoNum() == null ? 0 : a2.getMoNum()));
        } else {
            a1.setTransNum((a1.getTransNum() == null ? 0 : a1.getTransNum()) + (a2.getTransNum() == null ? 0 : a2.getTransNum()));
        }
    }

    private List<AffEpmInfoModel> sortEpmList(Integer showType, List<AffEpmInfoModel> newList) {
        if (showType == ZooConstant.EPM_SHOW_TYPE_DAY || showType == ZooConstant.EPM_SHOW_TYPE_HOUR) {
            List<AffEpmInfoModel> sortList = newList.stream().sorted((o1, o2) -> {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if (showType == ZooConstant.EPM_SHOW_TYPE_DAY) {
                        Date dt1 = format.parse(o1.getDate() + " 00:00:00");
                        Date dt2 = format.parse(o2.getDate() + " 00:00:00");
                        // 默认降序, 升序的话 把 dt1 和 dt2 调换位置
                        return Long.compare(dt2.getTime(), dt1.getTime());
                    } else {
                        Date dt1 = format.parse(o1.getDate() + ":00:00");
                        Date dt2 = format.parse(o2.getDate() + ":00:00");
                        // 默认降序, 升序的话 把 dt1 和 dt2 调换位置
                        return Long.compare(dt2.getTime(), dt1.getTime());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }).collect(Collectors.toList());
            return sortList;
        } else {
            return newList;
        }
    }

    private List<AffEpmInfoModel> generateEpmInfoList(String tableName, List<String> countries, String operator, List<String> partners, List<String> resourceNames, String offerName, List<String> offerIds, String begin, String end, String timezone, Integer showType, String belong, boolean showExpend, String queryType) {
        List<AffEpmInfoModel> epmInfoModelList = new ArrayList<>();
        try {
            String calculateHourLike = DateUtil.formatMonth(new Date());
            if (!tableName.equalsIgnoreCase(ZooConstant.EPM_INFO_TABLE_NAME)) {
                String tableTime = tableName.substring(9);
                calculateHourLike = tableTime.substring(0, 4) + "-" + tableTime.substring(4);
            }
            StringBuffer sql = generateEpmSql(tableName, timezone, showType, showExpend, operator, countries, partners, resourceNames, offerName, offerIds, belong, calculateHourLike, queryType);
            Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
            nativeQuery.unwrap(NativeQuery.class);
            setQueryParams(nativeQuery, countries, operator, partners, resourceNames, offerName, offerIds, begin, end, timezone, showType, belong);
            List<Object[]> resultList = nativeQuery.getResultList();
            epmInfoModelList = formatResult(resultList, showType, showExpend, queryType);
            epmInfoModelList = addOfferTags(epmInfoModelList);
        } catch (Exception e) {
            log.error("EPM ERROR :{}", JSON.toJSONString(e));
        }
        return epmInfoModelList;
    }

    /**
     * MO统计列表新增 TAGS 数据
     *
     * @param epmInfoModelList
     * @return
     */
    private List<AffEpmInfoModel> addOfferTags(List<AffEpmInfoModel> epmInfoModelList) {
        for (AffEpmInfoModel modelPlus : epmInfoModelList) {
            String offerTagSql = "select offer_id,tag_id from t_offer_tag where offer_id = \'" + modelPlus.getOfferId() + "'";
            List<Object[]> offerTagResultList = zooEntityManager.createNativeQuery(offerTagSql).getResultList();
            Set<OfferTagModel> offerTagModels = new HashSet<>();
            for (int i = 0; i < offerTagResultList.size(); i++) {
                offerTagModels.add(new OfferTagModel(offerTagResultList.get(i)[0].toString(), offerTagResultList.get(i)[1].toString()));
            }
            for (OfferTagModel model : offerTagModels) {
                String tagSql = "select tag_name,tag_type from t_tag where identification = \'" + model.getTagId() + "'";
                List<Object[]> tagResultList = zooEntityManager.createNativeQuery(tagSql).getResultList();
                model.setTagModel(new TagModel(tagResultList.get(0)[0].toString(), Integer.parseInt(tagResultList.get(0)[1].toString())));
            }
            modelPlus.setOfferTags(offerTagModels);
        }
        return epmInfoModelList;
    }

    private List<AffEpmInfoModel> formatResult(List<Object[]> resultList, Integer showType, boolean showExpend, String queryType) {
        List<AffEpmInfoModel> epmInfoModels = new ArrayList<>();
        if (showType == ZooConstant.EPM_SHOW_TYPE_DAY || showType == ZooConstant.EPM_SHOW_TYPE_HOUR) {
            for (Object[] tuple : resultList) {
                AffEpmInfoModel model = getSqlDateModel(tuple, showExpend, queryType);
                epmInfoModels.add(model);
            }
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_OFFER) {
            for (Object[] tuple : resultList) {
                AffEpmInfoModel model = getSqlOfferModel(tuple, showExpend, queryType);
                if (model == null) {
                    continue;
                }
                epmInfoModels.add(model);
            }
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_COUNTRY) {
            for (Object[] tuple : resultList) {
                AffEpmInfoModel model = getSqlCountryModel(tuple, showExpend, queryType);
                epmInfoModels.add(model);
            }
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_PARTNER) {
            for (Object[] tuple : resultList) {
                AffEpmInfoModel model = getSqlPartnerOrOpeModel(tuple, showExpend, queryType, 0);
                epmInfoModels.add(model);
            }
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_APP) {
            for (Object[] tuple : resultList) {
                AffEpmInfoModel model = getSqlAppModel(tuple, showExpend, queryType);
                if (model == null) {
                    continue;
                }
                epmInfoModels.add(model);
            }
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_OPERATOR) {
            for (Object[] tuple : resultList) {
                AffEpmInfoModel model = getSqlPartnerOrOpeModel(tuple, showExpend, queryType, 1);
                epmInfoModels.add(model);
            }
        }
        return epmInfoModels;
    }

    private AffEpmInfoModel getSqlPartnerOrOpeModel(Object[] tuple, boolean showExpend, String queryType, int type) {
        AffEpmInfoModel model = new AffEpmInfoModel();
        if (tuple[0] == null) {
            return null;
        }
        if (type == 0) {
            model.setPartner(tuple[0].toString());
        } else {
            model.setOperator(tuple[0].toString());
        }
        model.setClickNum((long) Integer.parseInt(tuple[1].toString()));
        model.setAppTransNum((long) Integer.parseInt(tuple[2].toString()));
        if (ZooConstant.ZOO.equalsIgnoreCase(queryType)) {
            model.setTransNum((long) Integer.parseInt(tuple[3].toString()));
        } else {
            model.setMoNum((long) Integer.parseInt(tuple[3].toString()));
        }
        model.setRevenue(Double.parseDouble(tuple[4].toString()));
        if (model.getClickNum() != 0) {
            model.setEpm(model.getRevenue() / model.getClickNum());
        }
        return model;
    }

    private AffEpmInfoModel getSqlOfferModel(Object[] tuple, boolean showExpend, String queryType) {
        AffEpmInfoModel model = new AffEpmInfoModel();
        model.setResourceName(ZooConstant.TOTAL);
        if (tuple[0] == null) {
            return null;
        }
        model.setIdentification(tuple[0].toString());
        model.setPartner(tuple[1].toString());
        model.setCountry(tuple[2].toString());
        model.setOperator(tuple[3].toString());
        model.setOfferName(tuple[4].toString());
        model.setOfferId(tuple[5].toString());
        model.setClickNum((long) Integer.parseInt(tuple[6].toString()));
        model.setAppTransNum((long) Integer.parseInt(tuple[7].toString()));
        if (ZooConstant.ZOO.equalsIgnoreCase(queryType)) {
            model.setTransNum((long) Integer.parseInt(tuple[8].toString()));
        } else {
            model.setMoNum((long) Integer.parseInt(tuple[8].toString()));
        }
        model.setRevenue(Double.parseDouble(tuple[9].toString()));
        if (model.getClickNum() != 0) {
            model.setEpm(model.getRevenue() / model.getClickNum());
        }
        return model;

    }

    private AffEpmInfoModel getSqlDateModel(Object[] tuple, boolean showExpend, String queryType) {
        AffEpmInfoModel model = new AffEpmInfoModel();
        model.setDate(tuple[0].toString());
        model.setClickNum((long) Integer.parseInt(tuple[1].toString()));
        model.setAppTransNum((long) Integer.parseInt(tuple[2].toString()));
        if (ZooConstant.ZOO.equalsIgnoreCase(queryType)) {
            model.setTransNum((long) Integer.parseInt(tuple[3].toString()));
        } else {
            model.setMoNum((long) Integer.parseInt(tuple[3].toString()));
        }
        model.setRevenue(Double.parseDouble(tuple[4].toString()));
        if (showExpend) {
            model.setOfferName(tuple[5].toString());
            model.setOfferId(tuple[6].toString());
        } else {
            model.setOfferName("****");
            model.setOfferId("****");
        }
        if (model.getClickNum() != 0) {
            model.setEpm(model.getRevenue() / model.getClickNum());
        }
        return model;

    }

    private AffEpmInfoModel getSqlCountryModel(Object[] tuple, boolean showExpend, String queryType) {
        AffEpmInfoModel model = new AffEpmInfoModel();
        if (tuple[0] == null) {
            return null;
        }
        model.setCountry(tuple[0].toString());
        model.setClickNum((long) Integer.parseInt(tuple[1].toString()));
        model.setAppTransNum((long) Integer.parseInt(tuple[2].toString()));
        if (ZooConstant.ZOO.equalsIgnoreCase(queryType)) {
            model.setTransNum((long) Integer.parseInt(tuple[3].toString()));
        } else {
            model.setMoNum((long) Integer.parseInt(tuple[3].toString()));
        }
        model.setRevenue(Double.parseDouble(tuple[4].toString()));
        if (model.getClickNum() != 0) {
            model.setEpm(model.getRevenue() / model.getClickNum());
        }
        return model;
    }

    private AffEpmInfoModel getSqlAppModel(Object[] tuple, boolean showExpend, String queryType) {
        AffEpmInfoModel model = new AffEpmInfoModel();
        model.setResourceName(ZooConstant.TOTAL);
        if (tuple[0] == null) {
            return null;
        }
        model.setIdentification(tuple[0].toString());
        model.setPartner(tuple[1].toString());
        model.setCountry(tuple[2].toString());
        model.setOperator(tuple[3].toString());
        model.setResourceName(tuple[4].toString());
        model.setOfferId(tuple[5].toString());
        model.setClickNum((long) Integer.parseInt(tuple[6].toString()));
        model.setAppTransNum((long) Integer.parseInt(tuple[7].toString()));
        if (ZooConstant.ZOO.equalsIgnoreCase(queryType)) {
            model.setTransNum((long) Integer.parseInt(tuple[8].toString()));
        } else {
            model.setMoNum((long) Integer.parseInt(tuple[8].toString()));
        }
        model.setRevenue(Double.parseDouble(tuple[9].toString()));
        if (model.getClickNum() != 0) {
            model.setEpm(model.getRevenue() / model.getClickNum());
        }
        return model;


    }

    private void setQueryParams(Query nativeQuery, List<String> countries, String operator, List<String> partners, List<String> resourceNames, String offerName, List<String> offerIds, String beginTime, String endTime, String timezone, Integer showType, String belong) {
        nativeQuery.setParameter("beginTime", beginTime);
        nativeQuery.setParameter("endTime", endTime);
        if (!StringUtils.isEmpty(operator)) {
            nativeQuery.setParameter("operator", operator);
        }
        if (!StringUtils.isEmpty(belong)) {
            nativeQuery.setParameter("belong", belong);
        }
        if (!StringUtils.isEmpty(offerName)) {
            nativeQuery.setParameter("offerName", offerName);
        }
        if (countries != null && countries.size() > 0) {
            nativeQuery.setParameter("countries", countries);
        }
        if (offerIds != null && offerIds.size() > 0) {
            nativeQuery.setParameter("offerIds", offerIds);
        }
        if (partners != null && partners.size() > 0) {
            nativeQuery.setParameter("partners", partners);
        }
        if (resourceNames != null && resourceNames.size() > 0) {
            nativeQuery.setParameter("resourceNames", resourceNames);
        }
    }

    private StringBuffer generateEpmSql(String tableName, String timezone, Integer showType, Boolean showExpend, String operator, List<String> countries, List<String> partners, List<String> resourceNames, String offerName, List<String> offerIds, String belong, String calculateHourLike, String queryType) throws Exception {
        // 获取当前时区
        StringBuffer sql = new StringBuffer();
        String systemTimeZone = "00:00";
        systemTimeZone = systemTimeZone.startsWith("-") ? systemTimeZone : ("+" + systemTimeZone);
        String tz = timezone.replace("GMT", "");
        String sqlSelect = " sum(cast(click_num as signed)) as clickNum, sum(cast(app_trans_num as signed)) as appTransNum, sum(cast(trans_num as signed)) as transNum, sum(cast(revenue as decimal(19,2))) as revenue ";
        if (queryType.equalsIgnoreCase(ZooConstant.PAYMENT)) {
            sqlSelect = " sum(cast(click_num as signed)) as clickNum, sum(cast(app_trans_num as signed)) as appTransNum, sum(cast(mo_num as signed)) as  moNum, sum(cast(revenue as decimal(19,2))) as revenue ";
        }
        String sqlwhere = generateSqlWhere(countries, operator, partners, resourceNames, offerName, offerIds, belong, calculateHourLike);
        if (ZooConstant.EPM_SHOW_TYPE_DAY == showType || ZooConstant.EPM_SHOW_TYPE_HOUR == showType) {
            String formatStr = showType == ZooConstant.EPM_SHOW_TYPE_DAY ? "%Y-%m-%d" : "%Y-%m-%d %H";
            sql.append("SELECT DATE_FORMAT(CONVERT_TZ(calculate_hour, \'" + systemTimeZone + "\', \'" + tz + "\'),\'" + formatStr + "\') as date," + sqlSelect);
            if (showExpend) {
                sql.append(",offer_name,offer_id");
            }
            sql.append(String.format(" FROM %s ", tableName));
            sql.append(sqlwhere);
            sql.append(" GROUP BY date ORDER BY date");
        } else if (ZooConstant.EPM_SHOW_TYPE_OFFER == showType) {
            sql.append("SELECT CONCAT(CONCAT(CONCAT(CONCAT(CONCAT(CONCAT(partner,'_'),country),'_'),operator),'_'),offer_id) as identification, partner, country, operator, offer_name, offer_id, " + sqlSelect);
            sql.append(String.format(" FROM %s ", tableName));
            sql.append(sqlwhere);
            sql.append(" GROUP BY offer_id");
        } else if (ZooConstant.EPM_SHOW_TYPE_COUNTRY == showType) {
            sql.append("SELECT country," + sqlSelect);
            sql.append(String.format(" FROM %s ", tableName));
            sql.append(sqlwhere);
            sql.append(" GROUP BY country");
        } else if (ZooConstant.EPM_SHOW_TYPE_PARTNER == showType) {
            sql.append("SELECT partner," + sqlSelect);
            sql.append(String.format(" FROM %s ", tableName));
            sql.append(sqlwhere);
            sql.append(" GROUP BY partner");
        } else if (ZooConstant.EPM_SHOW_TYPE_APP == showType) {
            sql.append("SELECT resource_id as identification , partner, country, operator, resource_name, offer_id, " + sqlSelect);
            sql.append(String.format(" FROM %s ", tableName));
            sql.append(sqlwhere);
            sql.append(" GROUP BY resource_id");
        } else if (ZooConstant.EPM_SHOW_TYPE_OPERATOR == showType) {
            sql.append("SELECT operator," + sqlSelect);
            sql.append(String.format(" FROM %s ", tableName));
            sql.append(sqlwhere);
            sql.append(" GROUP BY operator");
        }
        return sql;
    }

    private String generateSqlWhere(List<String> countries, String operator, List<String> partners, List<String> resourceNames, String offerName, List<String> offerIds, String belong, String calculateHourLike) {
        StringBuffer sqlWhere = new StringBuffer();
        sqlWhere.append("where createTime >= :beginTime and createTime <= :endTime");
        if (!StringUtils.isEmpty(operator)) {
            sqlWhere.append(" and operator = :operator");
        }
        if (!StringUtils.isEmpty(belong)) {
            sqlWhere.append(" and belong = :belong");
        }
        if (!StringUtils.isEmpty(offerName)) {
            sqlWhere.append(" and offer_name = :offerName");
        }
        if (countries != null && countries.size() > 0) {
            sqlWhere.append(" and country in (:countries)");
        }
        if (offerIds != null && offerIds.size() > 0) {
            sqlWhere.append(" and offer_id in (:offerIds)");
        }
        if (partners != null && partners.size() > 0) {
            sqlWhere.append(" and partner in (:partners)");
        }
        if (resourceNames != null && resourceNames.size() > 0) {
            sqlWhere.append(" and resource_name in (:resourceNames)");
        }
        sqlWhere.append(" and calculate_hour like '%" + calculateHourLike + "%'");
        return sqlWhere.toString();
    }

    public void setOfferStatus(List<AffEpmInfoModel> affEpmInfoModelList) {
        for (AffEpmInfoModel affEpmInfoModel : affEpmInfoModelList) {
            OfferModel firstByOfferId = offerService.getOfferModel(affEpmInfoModel.getOfferId());
            if (firstByOfferId != null) {
                affEpmInfoModel.setOfferStatus(firstByOfferId.getStatus());
            }
        }
    }

    /**
     * 获取合并后的list
     *
     * @param list
     * @param models
     * @param showType
     * @param queryType
     * @return
     */
    private List<AffEpmInfoModel> getCombinedList(List<AffEpmInfoModel> list, List<AffEpmInfoModel> models, int showType, String queryType) {
        LinkedHashMap<String, AffEpmInfoModel> map = getLinkedMap(list, showType);
        if (models != null && models.size() > 0) {
            if (map != null) {
                for (AffEpmInfoModel model : models) {
                    String key = model.getDate();
                    if (showType == ZooConstant.EPM_SHOW_TYPE_COUNTRY) {
                        key = model.getCountry();
                    }
                    if (showType == ZooConstant.EPM_SHOW_TYPE_PARTNER) {
                        key = model.getPartner();
                    }
                    if (showType == ZooConstant.EPM_SHOW_TYPE_OFFER) {
                        key = model.getPartner() + ZooConstant.UNDER_LINE
                                + model.getCountry() + ZooConstant.UNDER_LINE
                                + model.getOperator() + ZooConstant.UNDER_LINE
                                + model.getOfferId();
                    }
                    if (showType == ZooConstant.EPM_SHOW_TYPE_APP) {
                        key = model.getResourceName();
                    }
                    if (showType == ZooConstant.EPM_SHOW_TYPE_OPERATOR) {
                        key = model.getOperator();
                    }
                    AffEpmInfoModel totalModel = map.get(key);
                    if (totalModel == null) {
                        totalModel = model;
                    } else {
                        totalModel.setClickNum(model.getClickNum() + totalModel.getClickNum());
                        if (ZooConstant.ZOO.equalsIgnoreCase(queryType)) {
                            totalModel.setTransNum(model.getTransNum() + totalModel.getTransNum());
                        } else {
                            totalModel.setMoNum(model.getMoNum() + totalModel.getMoNum());
                        }
                        totalModel.setRevenue(model.getRevenue() + totalModel.getRevenue());
                        totalModel.setAppTransNum(model.getAppTransNum() + totalModel.getAppTransNum());
                        if (totalModel.getClickNum() != 0) {
                            totalModel.setEpm(totalModel.getRevenue() / totalModel.getClickNum());
                        }
                    }
                    if (showType == ZooConstant.EPM_SHOW_TYPE_OFFER) {
                        totalModel.setOfferStatus(model.getOfferStatus());
                    }
                    map.put(key, totalModel);
                }
            }
        }
        List<AffEpmInfoModel> combinedList = getMapList(map, showType);
        return combinedList;
    }

    private List<AffEpmInfoModel> getMapList(LinkedHashMap<String, AffEpmInfoModel> map, int showType) {
        List<AffEpmInfoModel> list = new ArrayList<>();
        if (map != null && map.size() > 0) {
            boolean isNeedSort = showType == ZooConstant.EPM_SHOW_TYPE_OFFER || showType == ZooConstant.EPM_SHOW_TYPE_COUNTRY
                    || showType == ZooConstant.EPM_SHOW_TYPE_PARTNER;
            if (isNeedSort) {
                for (Map.Entry<String, AffEpmInfoModel> entry : map.entrySet()) {
                    list.add(entry.getValue());
                }
            } else {
                ListIterator<Map.Entry<String, AffEpmInfoModel>> descMap =
                        new ArrayList<Map.Entry<String, AffEpmInfoModel>>(map.entrySet()).listIterator(map.size());
                while (descMap.hasPrevious()) {
                    Map.Entry<String, AffEpmInfoModel> entry = descMap.previous();
                    list.add(entry.getValue());
                }
            }
        }
        return list;
    }

    private LinkedHashMap<String, AffEpmInfoModel> getLinkedMap(List<AffEpmInfoModel> list, int showType) {
        LinkedHashMap<String, AffEpmInfoModel> map = new LinkedHashMap<>();
        if (list != null && list.size() > 0) {
            for (AffEpmInfoModel model : list) {
                if (showType == ZooConstant.EPM_SHOW_TYPE_OFFER) {
                    OfferModel offerModel = offerService.getOfferModel(model.getOfferId());
                    if (offerModel != null) {
                        model.setOfferStatus(offerModel.getStatus());
                        model.setCap(Long.valueOf(offerModel.getCap()));
                        model.setSystemOfferId(offerModel.getOfferId());
                        model.setPartnerOfferId(offerModel.getPartnerOfferId());
                    } else {
                        model.setOfferStatus(NumberEnum.ZERO.getNum());
                    }
                    map.put(model.getIdentification(), model);
                } else if (showType == ZooConstant.EPM_SHOW_TYPE_DAY || showType == ZooConstant.EPM_SHOW_TYPE_HOUR) {
                    map.put(model.getDate(), model);
                } else if (showType == ZooConstant.EPM_SHOW_TYPE_COUNTRY) {
                    map.put(model.getCountry(), model);
                } else if (showType == ZooConstant.EPM_SHOW_TYPE_PARTNER) {
                    map.put(model.getPartner(), model);
                } else if (showType == ZooConstant.EPM_SHOW_TYPE_APP) {
                    map.put(model.getResourceName(), model);
                } else if (showType == ZooConstant.EPM_SHOW_TYPE_OPERATOR) {
                    map.put(model.getOperator(), model);
                }
            }
        }
        return map;
    }

    private List<AffEpmInfoModel> getTupleResult(List<Tuple> tuples, int showType, boolean showExpend) {
        List<AffEpmInfoModel> list = new ArrayList<>();
        if (showType == ZooConstant.EPM_SHOW_TYPE_DAY || showType == ZooConstant.EPM_SHOW_TYPE_HOUR) {
            for (Tuple tuple : tuples) {
                AffEpmInfoModel model = getDateModel(tuple, showExpend);
                list.add(model);
            }
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_OFFER) {
            for (Tuple tuple : tuples) {
                AffEpmInfoModel model = getOfferTupleModel(tuple, showExpend);
                if (model == null) {
                    continue;
                }
                list.add(model);
            }
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_COUNTRY) {
            for (Tuple tuple : tuples) {
                AffEpmInfoModel model = new AffEpmInfoModel();
                if (tuple.get(0) == null) {
                    continue;
                }
                model.setCountry(tuple.get(0).toString());
                model.setClickNum((Long) tuple.get(1));
                model.setAppTransNum((Long) tuple.get(2));
                model.setTransNum((Long) tuple.get(3));
                model.setRevenue((Double) tuple.get(4));
                if (model.getClickNum() != 0) {
                    model.setEpm(model.getRevenue() / model.getClickNum());
                }
                list.add(model);
            }
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_PARTNER) {
            for (Tuple tuple : tuples) {
                AffEpmInfoModel model = new AffEpmInfoModel();
                if (tuple.get(0) == null) {
                    continue;
                }
                model.setPartner(tuple.get(0).toString());
                model.setClickNum((Long) tuple.get(1));
                model.setAppTransNum((Long) tuple.get(2));
                model.setTransNum((Long) tuple.get(3));
                model.setRevenue((Double) tuple.get(4));
                if (model.getClickNum() != 0) {
                    model.setEpm(model.getRevenue() / model.getClickNum());
                }
                list.add(model);
            }
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_APP) {
            for (Tuple tuple : tuples) {
                AffEpmInfoModel model = getResourceTupleModel(tuple, showExpend);
                if (model == null) {
                    continue;
                }
                list.add(model);
            }
        }
        return list;
    }

    private AffEpmInfoModel getResourceTupleModel(Tuple tuple, boolean showExpend) {
        AffEpmInfoModel model = new AffEpmInfoModel();
        model.setResourceName(ZooConstant.TOTAL);
        if (tuple.get(0) == null) {
            return null;
        }
        model.setIdentification(tuple.get(0).toString());
        model.setPartner(tuple.get(1).toString());
        model.setCountry(tuple.get(2).toString());
        model.setOperator(tuple.get(3).toString());
        model.setResourceName(tuple.get(4).toString());
        model.setOfferId(tuple.get(5).toString());
        model.setClickNum((Long) tuple.get(6));
        model.setAppTransNum((Long) tuple.get(7));
        model.setTransNum((Long) tuple.get(8));
        model.setRevenue((Double) tuple.get(9));
        if (model.getClickNum() != 0) {
            model.setEpm(model.getRevenue() / model.getClickNum());
        }
        return model;
    }

    private AffEpmInfoModel getOfferTupleModel(Tuple tuple, boolean showExpend) {
        AffEpmInfoModel model = new AffEpmInfoModel();
        model.setResourceName(ZooConstant.TOTAL);
        if (tuple.get(0) == null) {
            return null;
        }
        model.setIdentification(tuple.get(0).toString());
        model.setPartner(tuple.get(1).toString());
        model.setCountry(tuple.get(2).toString());
        model.setOperator(tuple.get(3).toString());
        model.setOfferName(tuple.get(4).toString());
        model.setOfferId(tuple.get(5).toString());
        model.setClickNum((Long) tuple.get(6));
        model.setAppTransNum((Long) tuple.get(7));
        model.setTransNum((Long) tuple.get(8));
        model.setRevenue((Double) tuple.get(9));
        if (model.getClickNum() != 0) {
            model.setEpm(model.getRevenue() / model.getClickNum());
        }
        return model;
    }

    private AffEpmInfoModel getDateModel(Tuple tuple, boolean showExpend) {
        AffEpmInfoModel model = new AffEpmInfoModel();
        model.setDate(tuple.get(0).toString());
        model.setClickNum((Long) tuple.get(1));
        model.setAppTransNum((Long) tuple.get(2));
        model.setTransNum((Long) tuple.get(3));
        model.setRevenue((Double) tuple.get(4));
        if (showExpend) {
            model.setOfferName(tuple.get(5).toString());
            model.setOfferId(tuple.get(6).toString());
        } else {
            model.setOfferName("****");
            model.setOfferId("****");
        }
        if (model.getClickNum() != 0) {
            model.setEpm(model.getRevenue() / model.getClickNum());
        }
        return model;
    }

    @SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
    private void initQuery(CriteriaBuilder criteriaBuilder, CriteriaQuery<Tuple> criteriaQuery, Root<AffEpmInfoModel> root, List<String> country,
                           String operator, List<String> partner, List<String> resourceNames, String offerName,
                           List<String> offerIds, Date begin, Date end, String timezone, Integer showType, boolean showExpend, String belong) {
        // 获取当前时区
        String sql = "SELECT REPLACE(TIMEDIFF(NOW(), UTC_TIMESTAMP),':00:00', ':00')";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        String systemTimeZone = "00:00";
        systemTimeZone = systemTimeZone.startsWith("-") ? systemTimeZone : ("+" + systemTimeZone);
        String tz = timezone.replace("GMT", "");
        // 初始选择条件
        initQuerySelect(criteriaBuilder, criteriaQuery, root, systemTimeZone, tz, showType, showExpend);
        initWhereParams(criteriaBuilder, criteriaQuery, root, country, operator, partner, resourceNames, offerName, offerIds, begin, end, belong);
        // 分组排序
        if (showType == ZooConstant.EPM_SHOW_TYPE_DAY || showType == ZooConstant.EPM_SHOW_TYPE_HOUR) {
            String formatStr = showType == ZooConstant.EPM_SHOW_TYPE_DAY ? "%Y-%m-%d" : "%Y-%m-%d %H";
            criteriaQuery.groupBy((Expression<?>) criteriaBuilder.function("DATE_FORMAT", String.class,
                    criteriaBuilder.function("CONVERT_TZ", Date.class, root.get("createTime"),
                            criteriaBuilder.literal(systemTimeZone), criteriaBuilder.literal(tz)),
                    criteriaBuilder.literal(formatStr)).alias("date"));
            criteriaQuery.orderBy(criteriaBuilder.asc((Expression<?>) criteriaBuilder.function("DATE_FORMAT", String.class,
                    criteriaBuilder.function("CONVERT_TZ", Date.class, root.get("createTime"),
                            criteriaBuilder.literal(systemTimeZone), criteriaBuilder.literal(tz)),
                    criteriaBuilder.literal(formatStr)).alias("date")));
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_OFFER) {
            criteriaQuery.groupBy(root.get("offerId"));
            List<Order> orders = new ArrayList<>();
            orders.add(criteriaBuilder.asc(root.get("country")));
            orders.add(criteriaBuilder.asc(root.get("operator")));
            orders.add(criteriaBuilder.asc(root.get("partner")));
            orders.add(criteriaBuilder.asc(root.get("offerName")));
            criteriaQuery.orderBy(orders);
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_COUNTRY) {
            criteriaQuery.groupBy(root.get("country"));
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("country")));
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_PARTNER) {
            criteriaQuery.groupBy(root.get("partner"));
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("partner")));
        } else if (showType == ZooConstant.EPM_SHOW_TYPE_APP) {
            criteriaQuery.groupBy(root.get("resourceId"));
            List<Order> orders = new ArrayList<>();
            orders.add(criteriaBuilder.asc(root.get("country")));
            orders.add(criteriaBuilder.asc(root.get("operator")));
            orders.add(criteriaBuilder.asc(root.get("partner")));
            orders.add(criteriaBuilder.asc(root.get("resourceName")));
            criteriaQuery.orderBy(orders);
        }
    }

    private void initWhereParams(CriteriaBuilder criteriaBuilder, CriteriaQuery<Tuple> criteriaQuery, Root<AffEpmInfoModel> root,
                                 List<String> country, String operator, List<String> partner, List<String> resourceNames, String offerName, List<String> offerIds, Date begin, Date end, String belong) {
        List<Predicate> predicates = new ArrayList<>();
        if (!StringUtils.isEmpty(operator)) {
            predicates.add(criteriaBuilder.equal(root.get("operator"), operator));
        }

        if (!StringUtils.isEmpty(belong)) {
            predicates.add(criteriaBuilder.equal(root.get("belong"), belong));
        }
        if (country != null && country.size() > 0) {
            CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("country"));
            for (String c : country) {
                in.value(c);
            }
            predicates.add(in);
        }
        if (partner != null && partner.size() > 0) {
            CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("partner"));
            for (String par : partner) {
                in.value(par);
            }
            predicates.add(in);
        }
        if (resourceNames != null && resourceNames.size() > 0) {
            CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("resourceName"));
            for (String resourceName : resourceNames) {
                in.value(resourceName);
            }
            predicates.add(in);
        }
        if (!StringUtils.isEmpty(offerName)) {
            predicates.add(criteriaBuilder.equal(root.get("offerName"), offerName));
        }
        if (offerIds != null && offerIds.size() > 0) {
            CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("offerId"));
            for (String id : offerIds) {
                in.value(id);
            }
            predicates.add(in);
        }
        predicates.add(criteriaBuilder.between(root.get("createTime"), begin, end));
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
    }

    private void initQuerySelect(CriteriaBuilder criteriaBuilder, CriteriaQuery<Tuple> criteriaQuery, Root<AffEpmInfoModel> root,
                                 String systemTimeZone, String timezone, Integer showType, boolean showExpend) {
        if (ZooConstant.EPM_SHOW_TYPE_DAY == showType || ZooConstant.EPM_SHOW_TYPE_HOUR == showType) {
            String formatStr = showType == ZooConstant.EPM_SHOW_TYPE_DAY ? "%Y-%m-%d" : "%Y-%m-%d %H";
            List<Selection<?>> selections = new ArrayList<>();
            // 如果是按照服务器时区进行天或者小时划分
            selections.add((Expression<?>) criteriaBuilder.function("DATE_FORMAT", String.class,
                    criteriaBuilder.function("CONVERT_TZ", Date.class, root.get("createTime"),
                            criteriaBuilder.literal(systemTimeZone), criteriaBuilder.literal(timezone)),
                    criteriaBuilder.literal(formatStr)).alias("date"));
            selections.add(criteriaBuilder.sum(root.get("clickNum").as(Integer.class)).alias("clickNum"));
            selections.add(criteriaBuilder.sum(root.get("appTransNum").as(Integer.class)).alias("appTransNum"));
            selections.add(criteriaBuilder.sum(root.get("transNum").as(Integer.class)).alias("transNum"));
            selections.add(criteriaBuilder.sum(root.get("revenue").as(Float.class)).alias("revenue"));
            if (showExpend) {
                selections.add(root.get("offerName"));
                selections.add(root.get("offerId"));
            }
            criteriaQuery.multiselect(selections);
        } else if (ZooConstant.EPM_SHOW_TYPE_OFFER == showType) {
            criteriaQuery.multiselect(
                    // 将区分的 部分拼接为主键
                    criteriaBuilder.concat(
                            criteriaBuilder.concat(criteriaBuilder.concat(
                                            criteriaBuilder.concat(
                                                    criteriaBuilder.concat(
                                                            criteriaBuilder.concat(
                                                                    root.get("partner"),
                                                                    ZooConstant.UNDER_LINE),
                                                            root.get("country")),
                                                    ZooConstant.UNDER_LINE),
                                            root.get("operator")),
                                    ZooConstant.UNDER_LINE),
                            root.get("offerId")).alias("identification"),
                    root.get("partner"),
                    root.get("country"),
                    root.get("operator"),
                    root.get("offerName"),
                    root.get("offerId"),
                    criteriaBuilder.sum(root.get("clickNum").as(Integer.class)).alias("clickNum"),
                    criteriaBuilder.sum(root.get("appTransNum").as(Integer.class)).alias("appTransNum"),
                    criteriaBuilder.sum(root.get("transNum").as(Integer.class)).alias("transNum"),
                    criteriaBuilder.sum(root.get("revenue").as(Float.class)).alias("revenue")
            );
        } else if (ZooConstant.EPM_SHOW_TYPE_COUNTRY == showType) {
            criteriaQuery.multiselect(
                    root.get("country"),
                    criteriaBuilder.sum(root.get("clickNum").as(Integer.class)).alias("clickNum"),
                    criteriaBuilder.sum(root.get("appTransNum").as(Integer.class)).alias("appTransNum"),
                    criteriaBuilder.sum(root.get("transNum").as(Integer.class)).alias("transNum"),
                    criteriaBuilder.sum(root.get("revenue").as(Float.class)).alias("revenue")
            );
        } else if (ZooConstant.EPM_SHOW_TYPE_PARTNER == showType) {
            criteriaQuery.multiselect(
                    root.get("partner"),
                    criteriaBuilder.sum(root.get("clickNum").as(Integer.class)).alias("clickNum"),
                    criteriaBuilder.sum(root.get("appTransNum").as(Integer.class)).alias("appTransNum"),
                    criteriaBuilder.sum(root.get("transNum").as(Integer.class)).alias("transNum"),
                    criteriaBuilder.sum(root.get("revenue").as(Float.class)).alias("revenue")
            );
        } else if (ZooConstant.EPM_SHOW_TYPE_APP == showType) {
            criteriaQuery.multiselect(getResourceNameMultiSelect(criteriaBuilder, root));
        }
    }

    private List<Selection<?>> getResourceNameMultiSelect(CriteriaBuilder criteriaBuilder, Root root) {
        List<Selection<?>> selections = new ArrayList<>();
        // 将区分的 部分拼接为主键
        selections.add(root.get("resourceId").alias("identification"));
        selections.add(root.get("partner"));
        selections.add(root.get("country"));
        selections.add(root.get("operator"));
        selections.add(root.get("resourceName"));
        selections.add(root.get("offerId"));
        selections.add(criteriaBuilder.sum(root.get("clickNum").as(Integer.class)).alias("clickNum"));
        selections.add(criteriaBuilder.sum(root.get("appTransNum").as(Integer.class)).alias("appTransNum"));
        selections.add(criteriaBuilder.sum(root.get("transNum").as(Integer.class)).alias("transNum"));
        selections.add(criteriaBuilder.sum(root.get("revenue").as(Float.class)).alias("revenue"));
        return selections;
    }

    /**
     * 设置系统ID和PID
     *
     * @param list
     */
    private void setSidAndPid(List<AffEpmInfoModel> list) {
        List<OfferModel> offerModels = offerRepo.findAll();
        HashMap<String, String> maps = new HashMap<>(1);
        for (OfferModel offerModel : offerModels) {
            maps.put(offerModel.getOfferName(), offerModel.getPartnerOfferId() + "|" + offerModel.getOfferId() + "|" + offerModel.getCap());
        }
        for (AffEpmInfoModel model : list) {
            String offerName = model.getOfferName();
            String idStr = maps.get(offerName);
            if (!StringUtils.isEmpty(idStr)) {
                String[] ids = idStr.split("\\|");
                model.setPartnerOfferId(ids[0]);
                model.setSystemOfferId(ids[1]);
                Long cap = StringUtils.isEmpty(ids[2]) ? 0L : Long.parseLong(ids[2]);
                model.setCap(cap);
                model.setTransRemainder(model.getCap() - model.getTransNum());
            }
        }
    }

    private void resetOfferIds(List<String> offerIds) {
        List<String> limitPullOfferIds = offerRepo.queryLimitOfferIds();
        boolean needCombined = limitPullOfferIds != null && limitPullOfferIds.size() > 0 && offerIds != null && offerIds.size() > 0;
        if (needCombined) {
            List<String> ids = new ArrayList<>();
            for (String id : offerIds) {
                if (limitPullOfferIds.contains(id)) {
                    ids.add(id);
                }
            }
            offerIds.clear();
            offerIds.addAll(ids);
        } else if (limitPullOfferIds != null && limitPullOfferIds.size() > 0) {
            offerIds.clear();
            offerIds.addAll(limitPullOfferIds);
        }
    }


    @Override
    public List<String> getOfferIds(List<String> tagIds) {
        List<String> offerIds = null;
        if (tagIds != null && tagIds.size() > 0) {
            // 查找出所有 满足的 offerId
            StringBuilder sql = new StringBuilder("SELECT offer_id FROM t_offer_tag WHERE tag_id IN (:tagIds)");
            Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
            nativeQuery.unwrap(NativeQuery.class);
            nativeQuery.setParameter("tagIds", tagIds);
            offerIds = nativeQuery.getResultList();
        }
        return offerIds;
    }

    /**
     * 获取当前小时的 EPM 值
     *
     * @param calendar
     * @param country
     * @param operator
     * @param partner
     * @param resourceNames
     * @param offerName
     * @param belong
     * @param queryType
     * @return
     */
    @Timed
    private List<AffEpmInfoModel> getCurrentHourEpms(Calendar calendar, List<String> country, String operator, List<String> partner,
                                                     List<String> resourceNames, String offerName, List<String> offerIds, int showType, String belong, String timeZone, String queryType) {
        List<AffEpmInfoModel> allCurrentEpms = new ArrayList();
        //遍历当前小时的 redis EPM counter 如果存在该 offer 回传不支持透传的， 则 categoryId 为 NONE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + sdf.format(calendar.getTime());
        log.info("{} [CALCULATE_CURRENT EPM] KEY_Pattern:{}", LogConstant.ZOO, pattern);
        Set<String> hKeySet = cluster3RedisTemplate.keys(pattern);
        if (hKeySet != null && hKeySet.size() > 0) {
            log.info("{} [CALCULATE_EPM] KEY_SET:{}", LogConstant.ZOO, JSON.toJSONString(hKeySet));
            //key: categoryId value: offer:epm
            for (String hKey : hKeySet) {
                try {
                    //redis hash:【aff_epm_counter:type:app-identification:offer-identification:yyyy-MM-dd-HH】
                    String[] strArr = hKey.split(CacheNameSpace.COLON);
                    String category = strArr != null && strArr.length == NumberEnum.FIVE.getNum() ? strArr[1] : ZooConstant.UNKNOWN;
                    String categoryId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[2] : ZooConstant.UNKNOWN;
                    String offerId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[3] : ZooConstant.UNKNOWN;
                    if (offerIds != null && offerIds.size() > 0 && !offerIds.contains(offerId)) {
                        continue;
                    }
                    log.info("{} [CALCULATE_CURRENT EPM] KEY:{} [CATEGORY:{} CATEGORY_ID:{}] [OFFER_ID:{}]", LogConstant.ZOO, hKey, category, categoryId, offerId);
                    AffEpmInfoModel affEpmInfoModel = getCurrentTranModel(categoryId, calendar, showType, timeZone, hKey, offerId, queryType);
                    boolean isReturn = true;
                    if (country != null && country.size() > 0 && !country.contains(affEpmInfoModel.getCountry())) {
                        isReturn = false;
                    }
                    if (!StringUtils.isEmpty(operator) && !operator.equalsIgnoreCase(affEpmInfoModel.getOperator())) {
                        isReturn = false;
                    }
                    if (partner != null && partner.size() > 0 && !partner.contains(affEpmInfoModel.getPartner())) {
                        isReturn = false;
                    }
                    if (resourceNames != null && resourceNames.size() > 0 && !resourceNames.contains(affEpmInfoModel.getResourceName())) {
                        isReturn = false;
                    }
                    if (!StringUtils.isEmpty(offerName) && !offerName.equalsIgnoreCase(affEpmInfoModel.getOfferName())) {
                        isReturn = false;
                    }
                    if (isReturn) {
                        boolean existTestOffer = checkExistTestOffer(affEpmInfoModel.getOfferId());
                        OfferModel offerModel = null;
                        if (existTestOffer) {
                            offerModel = epmService.getTestOfferInfo(affEpmInfoModel.getOfferId());
                        } else {
                            offerModel = offerService.getOfferModel(affEpmInfoModel.getOfferId());
                        }
                        affEpmInfoModel.setBelong(offerModel.getBelong());
                        if (!StringUtils.isEmpty(belong) && belong.equalsIgnoreCase(offerModel.getBelong())) {
                            allCurrentEpms.add(affEpmInfoModel);
                        } else if (StringUtils.isEmpty(belong)) {
                            allCurrentEpms.add(affEpmInfoModel);
                        }
                    }
                } catch (Exception e) {
                    log.info("{} [EPM_SERVICE] [CALCULATE_CURRENT EPM] [KEY:{}]", LogConstant.ZOO, hKey, e);
                }
            }
        }
        return allCurrentEpms;
    }

    @Override
    @Cacheable(value = "testOffer", key = "#offerId")
    public OfferModel getTestOfferInfo(String offerId) {
        OfferModel offerModel = null;
        Object obj = cluster3RedisTemplate.opsForHash().get(ZooConstant.ZOO_TEST_OFFER_DATA, offerId);
        if (obj != null) {
            offerModel = JSONObject.parseObject(String.valueOf(obj), OfferModel.class);
        }
        return offerModel;
    }

    @Override
    @SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "DM_BOXED_PRIMITIVE_FOR_PARSING"})
    @Async
    public void syncEpmRedis() {
        List<OfferModel> offerModels = offerRepo.findAll();
        Map<String, String> map = new HashMap<>(1);
        if (offerModels != null && offerModels.size() > 0) {
            for (OfferModel offerModel : offerModels) {

                JSONObject json = (JSONObject) JSON.toJSON(offerModel);
                json.put("offerTags", offerModel.getOfferTags());
                String stackId = offerTagRepo.findOfferStackId(offerModel.getIdentification());
                json.put("stack", stackId);
                // 将当前序列化对象保存到 redis 中
                stringRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification(), json.toJSONString());
                map.put(offerModel.getIdentification(), offerModel.getOperator());
            }
        }
        Set<String> keys = cluster3RedisTemplate.keys(CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.ASTERISK);
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                stringRedisTemplate.delete(key);
            }
        }
        List<CategoryTagModel> ctModels = categoryTagRepo.findAll();
        if (ctModels != null && ctModels.size() > 0) {
            for (CategoryTagModel categoryTagModel : ctModels) {
                String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON +
                        (categoryTagModel.getType() == ZooConstant.CATEGORY_APP ? ZooConstant.APP : ZooConstant.AFFILIATE)
                        + CacheNameSpace.COLON + categoryTagModel.getCategoryId();
                // 将 app 与 offer 的关联信息保存到 redis 中
                List<OfferTagModel> offerTagModels = offerTagRepo.findAllByTagId(categoryTagModel.getTagId());
                if (offerTagModels != null && offerTagModels.size() > 0) {
                    for (OfferTagModel model : offerTagModels) {
                        String offerId = model.getOfferId();
                        String operator = map.get(offerId);
                        stringRedisTemplate.opsForHash().put(key, offerId, operator);
                        // 将offer_assign 信息同步到redis
                        OfferModel offerModel = offerService.getOfferModel(offerId);
                        // 当offer状态为开启时
                        if (offerModel != null && offerModel.getStatus() == 1) {
                            Integer redisOfferCap = 0;
                            Integer redisAppCap = 0;
                            Integer pullCount = 0;
                            Boolean existOfferCap = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId), CacheNameSpace.POST_BACK_TRANS);
                            Boolean existAppCap = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId), CacheNameSpace.APP_TRANS);
                            Boolean existPullCount = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId), CacheNameSpace.PULL_COUNT);
                            if (existOfferCap != null && existOfferCap) {
                                redisOfferCap = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId), CacheNameSpace.POST_BACK_TRANS)));
                            }
                            if (existAppCap != null && existAppCap) {
                                redisAppCap = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId), CacheNameSpace.APP_TRANS)));
                            }
                            if (existPullCount != null && existPullCount) {
                                pullCount = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId), CacheNameSpace.PULL_COUNT)));
                            }
                            offerService.updateOfferRedis(offerModel, offerId, categoryTagModel.getCategoryId(), redisOfferCap, redisAppCap, pullCount, "initRedis");
                        } else if (offerModel != null && offerModel.getStatus() != 1) {
                            //更新redis, 从epmlist 跟 zoo_offer_assign:filter删除
                            String epmListKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + categoryTagModel.getCategoryId() + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + CacheNameSpace.LIST;
                            Boolean existEpmList = cluster3RedisTemplate.hasKey(epmListKey);
                            if (existEpmList != null && existEpmList) {
                                stringRedisTemplate.opsForList().remove(epmListKey, 0, offerId);
                            }
                            String assignKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + categoryTagModel.getCategoryId() + CacheNameSpace.COLON + offerModel.getOperator();
                            Boolean existOfferAssign = cluster3RedisTemplate.opsForHash().hasKey(assignKey, offerId);
                            if (existOfferAssign != null && existOfferAssign) {
                                stringRedisTemplate.opsForHash().delete(assignKey, offerId);
                            }
                        }

                    }
                }
            }
        }
    }

    @Override
    public JSONObject getTestOfferEpm(List<JSONObject> offerJsonList) {
        JSONObject resultJson = new JSONObject();
        List<String> offerIds = offerJsonList.stream().map(e -> e.getString("identification")).collect(Collectors.toList());
        String dayTime = DateUtil.formatDay(DateUtil.getDateByTimeZone(System.currentTimeMillis(), ZooConstant.GMT_8));
        String begin = DateUtil.foreDay(dayTime) + "-16";
        String end = dayTime + "-15";
        List<EpmClickVO> redisVoClickList = new ArrayList<>();
        // 查询redis中当前小时的点击等
        for (String offerId : offerIds) {
            String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + offerId + CacheNameSpace.ASTERISK + DateUtil.formatDayHourString(new Date());
            Set<String> hKeySet = cluster3RedisTemplate.keys(pattern);
            long clickNum = 0;
            long appTransNum = 0;
            long postBackTransNum = 0;
            if (hKeySet != null && hKeySet.size() > 0) {
                for (String key : hKeySet) {
                    List<Object> queryParams = new ArrayList<>();
                    queryParams.add(CacheNameSpace.CLICK);
                    queryParams.add(CacheNameSpace.APP_TRANS);
                    queryParams.add(CacheNameSpace.POST_BACK_TRANS);
                    List<Object> params = cluster3RedisTemplate.opsForHash().multiGet(key, queryParams);
                    clickNum += StringUtils.isEmpty(params.get(0)) ? 0 : Long.parseLong(String.valueOf(params.get(0)));
                    appTransNum += StringUtils.isEmpty(params.get(1)) ? 0 : Long.parseLong(String.valueOf(params.get(1)));
                    postBackTransNum += StringUtils.isEmpty(params.get(2)) ? 0 : Long.parseLong(String.valueOf(params.get(2)));
                }
            }
            EpmClickVO redisClickVo = new EpmClickVO(offerId, clickNum, appTransNum, postBackTransNum);
            redisVoClickList.add(redisClickVo);
        }
        // 查询数据库中当天的点击、包内、转化
        List<EpmClickVO> objects = affEpmInfoRepo.queryClickAndAppTransAndRevenueInOfferId(offerIds, begin, end);
        if (objects != null && objects.size() > 0) {
            // 合并 redis 中当前小时点击
            for (EpmClickVO sqlClickVo : objects) {
                for (EpmClickVO redisClickVO : redisVoClickList) {
                    if (redisClickVO.getOfferId().equals(sqlClickVo.getOfferId())) {
                        redisClickVO.setClick(sqlClickVo.getClick() + redisClickVO.getClick());
                        redisClickVO.setAppTrans(sqlClickVo.getAppTrans() + redisClickVO.getAppTrans());
                        redisClickVO.setPostBack(sqlClickVo.getPostBack() + redisClickVO.getPostBack());
                    }
                }
            }
        }
        // 赋值
        for (EpmClickVO clickVO : redisVoClickList) {
            for (JSONObject jsonObject : offerJsonList) {
                if (jsonObject.getString("identification").equals(clickVO.getOfferId())) {
                    jsonObject.put("click", clickVO.getClick());
                    jsonObject.put("appTrans", clickVO.getAppTrans());
                    jsonObject.put("postBack", clickVO.getPostBack());
                }
            }
        }
        resultJson.put("list", offerJsonList);
        return resultJson;
    }

    private boolean checkExistTestOffer(String offerId) {
        OfferModel offerModel = epmService.getTestOfferInfo(offerId);
        if (offerModel != null) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    @Timed
    private AffEpmInfoModel getCurrentTranModel(String categoryId, Calendar calendar, int showType, String timeZone, String key, String offerId, String queryType) {
        AffEpmInfoModel model = new AffEpmInfoModel();
        String date = showType == ZooConstant.EPM_SHOW_TYPE_DAY ? DateUtil.formatDay(calendar.getTime()) : DateUtil.formatDayHour(calendar.getTime());
        String timeZoneDate = DateUtil.getHourByTimeZone(DateUtil.formatHourTime(date), timeZone);
        List<Object> queryParams = generateQueryParams(queryType);
        List<Object> params = cluster3RedisTemplate.opsForHash().multiGet(key, queryParams);
        String country = String.valueOf(params.get(0));
        String operator = String.valueOf(params.get(1));
        String partner = String.valueOf(params.get(2));
        String partnerOfferId = String.valueOf(params.get(3));
        String offerName = String.valueOf(params.get(4));
        String systemOfferId = String.valueOf(params.get(5));
        String cap = StringUtils.isEmpty(String.valueOf(params.get(6))) ? "0" : String.valueOf(params.get(6));
        String appName = String.valueOf(params.get(7));
        String status = String.valueOf(params.get(8));
        String clickNum = StringUtils.isEmpty(params.get(9)) ? "0" : String.valueOf(params.get(9));
        String appTransNum = StringUtils.isEmpty(params.get(10)) ? "0" : String.valueOf(params.get(10));
        String postBackTransNum = StringUtils.isEmpty(params.get(11)) ? "0" : String.valueOf(params.get(11));
        String revenue = StringUtils.isEmpty(params.get(12)) ? "0" : String.valueOf(params.get(12));
        String belong = String.valueOf(params.get(13));
        if (StringUtils.isEmpty(country)) {
            boolean existTestOffer = checkExistTestOffer(offerId);
            OfferModel offerModel = null;
            if (existTestOffer) {
                offerModel = epmService.getTestOfferInfo(offerId);
            } else {
                offerModel = offerService.getOfferModel(offerId);
            }
            if (offerModel != null) {
                country = offerModel.getCountry();
                operator = offerModel.getOperator();
                partner = offerModel.getPartner();
                partnerOfferId = offerModel.getPartnerOfferId();
                systemOfferId = offerModel.getOfferId();
                offerName = offerModel.getOfferName();
                cap = String.valueOf(offerModel.getCap());
                ApplicationModel appModel = applicationService.getById(categoryId);
                appName = appModel.getAppName();
                belong = offerModel.getBelong();
            }
        }
        model.setDate(timeZoneDate);
        model.setOfferId(offerId);
        model.setOfferName(offerName);
        model.setPartnerOfferId(partner);
        model.setSystemOfferId(systemOfferId);
        model.setCountry(country);
        model.setOperator(operator);
        model.setPartner(partner);
        model.setClickNum(Long.valueOf(clickNum));
        model.setAppTransNum(Long.valueOf(appTransNum));
        if (queryType.equalsIgnoreCase(ZooConstant.ZOO)) {
            model.setTransNum(Long.valueOf(postBackTransNum));
        } else if (queryType.equalsIgnoreCase(ZooConstant.PAYMENT)) {
            model.setMoNum(Long.valueOf(postBackTransNum));
        }
        model.setRevenue(Double.valueOf(revenue));
        model.setPartnerOfferId(partnerOfferId);
        if (cap.equalsIgnoreCase(ZooConstant.NULL_STR)) {
            cap = "0";
        }
        model.setCap(Long.valueOf(cap));
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        model.setResourceName(appName);
        model.setResourceType(ZooConstant.CATEGORY_APP);
        if (ZooConstant.ZOO.equalsIgnoreCase(queryType)) {
            Long transRemainder = model.getCap() - model.getTransNum();
            model.setTransRemainder(transRemainder);
        }
        model.setOfferStatus(Integer.valueOf(status));
        model.setBelong(belong);
        return model;
    }

    /**
     * 生成查询参数
     *
     * @param queryType
     * @return
     */
    private List<Object> generateQueryParams(String queryType) {
        List<Object> params = new ArrayList<>();
        params.add(ZooConstant.COUNTRY);
        params.add(ZooConstant.OPERATOR);
        params.add(ZooConstant.PARTNER);
        params.add(ZooConstant.PARTNER_OFFER_ID);
        params.add(ZooConstant.OFFER_NAME);
        params.add(ZooConstant.OFFER_ID);
        params.add(ZooConstant.CAP);
        params.add(ZooConstant.APP_NAME);
        params.add(ZooConstant.OFFER_STATUS);
        params.add(CacheNameSpace.CLICK);
        params.add(CacheNameSpace.APP_TRANS);
        if (queryType.equalsIgnoreCase(ZooConstant.ZOO)) {
            params.add(CacheNameSpace.POST_BACK_TRANS);
        } else {
            params.add(CacheNameSpace.MO_TRANS);
        }
        params.add(CacheNameSpace.REVENUE);
        params.add(ZooConstant.BELONG);
        return params;
    }

    /**
     * 检查转化异常定时任务
     */
    @Async
    @Override
    public void checkTrans() {
        //获取配置（链接启用且开启告警）
        try {
            List<OfferModel> offerModels = offerRepo.findByStatusAndAlarmStatus(1, 1);
            //遍历配置（查看 1 小时内最新的点击是否带来转化）
            for (OfferModel offerModel : offerModels) {
                producer.sendToQueueOfferTransAlarm(new BaseSqsMessage(new Integer[]{ZooConstant.QUEUE_OFFER_CHECK_TRANS}, ZooConstant.QUEUE_OFFER_CHECK_TRANS_MODEL, JSON.toJSONString(offerModel)));
            }
        } catch (Exception e) {
            sendAffAlaram();
        }
    }

    /**
     * 处理offer 转化 检查
     *
     * @param msgBody
     */
    @Override
    public void handleCheckOfferTrans(String msgBody) {
        try {
            OfferModel offerModel = JSONObject.parseObject(msgBody, OfferModel.class);
            //1.先查询转化数量，有转化就跳过
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -offerModel.getTransRange());
            Long count = affPostBackRepo.countByPartnerAndSystemOfferIdAndCreateTimeAfter(offerModel.getPartner(),
                    offerModel.getOfferId(), calendar.getTime());
            //2.没有转化时，再查询是否点击数量达到 alarmThreshold 及以上，如果是则发送邮件
            if (count == 0) {
                String hour = DateUtil.beforeHour().split(" ")[0] + "-" + DateUtil.beforeHour().split(" ")[1];
                Long countClick = affEpmInfoRepo.queryClickNumByPartnerAndOfferId(offerModel.getPartner(),
                        offerModel.getIdentification(),hour);
                countClick = countClick != null ? countClick : 0L;
                boolean needSend = offerModel.getAlarmThreshold() > 0 && countClick >= offerModel.getAlarmThreshold()
                        && !StringUtils.isEmpty(offerModel.getEmail());
                if (offerModel.getAlarmThreshold() > 0 && countClick >= offerModel.getAlarmThreshold()) {
                    List<String> appIds = categoryTagRepo.queryAppIds(offerModel.getIdentification());
                    appIds = removeDuplicate(appIds);
                    for (String appId : appIds) {
                        closeOfferRedis(appId, offerModel.getIdentification(), offerModel.getOperator());
                    }
                }
                log.info("{} [CHECK AFF TRANSACTION] [PARTNER:{} OFFER_ID:{} DATE:{}] COUNT_CLICK:{}, NEED_SEND:{}", LogConstant.ZOO, offerModel.getPartner(),
                        offerModel.getOfferId(), DateUtil.formatyyyyMMddHHmmss(calendar.getTime()), countClick, needSend);
                if (needSend) {
                    Map<String, Object> contentModel = new HashMap<>(9);
                    contentModel.put(ZooConstant.TITLE, ZooConstant.AFF_TRANS_MAIL_SUBJECT);
                    contentModel.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
                    contentModel.put(ZooConstant.BEGIN_TIME, DateUtil.formatyyyyMMddHHmmss(calendar.getTime()));
                    calendar.add(Calendar.MINUTE, offerModel.getTransRange());
                    contentModel.put(ZooConstant.END_TIME, DateUtil.formatyyyyMMddHHmmss(calendar.getTime()));
                    contentModel.put(ZooConstant.THRESHOLD, offerModel.getAlarmThreshold());
                    contentModel.put(ZooConstant.AFF_NAME, offerModel.getPartner());
                    contentModel.put(ZooConstant.OFFER_ID, offerModel.getOfferId());
                    contentModel.put(ZooConstant.OFFER_NAME, offerModel.getOfferName());
                    contentModel.put(ZooConstant.PARTNER_OFFER_ID, offerModel.getPartnerOfferId());
                    contentModel.put(ZooConstant.MESSAGE, offerModel.getComment());
                    contentModel.put(ZooConstant.EMAIL_UUID, UUID.randomUUID().toString());
                    String[] emails = offerModel.getEmail().split(",");
                    if (emails.length > 1) {
                        String[] ccEmails = Arrays.copyOfRange(emails, 1, emails.length);
                        emailUtil.sendMimeMessageMail(ZooConstant.AFF_TRANS_MAIL_TEMPLATE, emails[0],
                                ZooConstant.AFF_TRANS_MAIL_SUBJECT, contentModel, ccEmails);
                    } else {
                        emailUtil.sendMimeMessageMail(ZooConstant.AFF_TRANS_MAIL_TEMPLATE, emails[0],
                                ZooConstant.AFF_TRANS_MAIL_SUBJECT, contentModel);
                    }
                    // 关闭 offer
                    offerService.changeStatus(offerModel.getIdentification(), ZooConstant.STATUS_0);
                }
            }
        } catch (Exception e) {
            log.error("{} [CHECK AFF TRANSACTION] {}", LogConstant.ZOO, LogConstant.ERROR, e);
        }
    }


    private void sendAffAlaram() {
        try {
            Map<String, Object> contentModel = generateAlaramContent();
            String[] emails = {"traffic@starpavilion-digital.com", "aaron.huang@starpavilion-digital.com",
                    "cherry.wang@starpavilion-digital.com", "david.li@starpavilion-digital.com", "alie.shi@starpavilion-digital.com"};
            emailUtil.sendMimeMessageMail(ZooConstant.AFF_TRANS_MAIL_TEMPLATE, emails[emails.length - 1],
                    ZooConstant.AFF_TRANS_MAIL_SUBJECT, contentModel, emails);
        } catch (Exception e) {
            log.info("Send Alarm email error:{}", JSON.toJSONString(e));
        }

    }

    private Map<String, Object> generateAlaramContent() {
        Map<String, Object> contentModel = new HashMap<>(9);
        contentModel.put(ZooConstant.TITLE, ZooConstant.AFF_TRANS_MAIL_SUBJECT);
        contentModel.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
        contentModel.put(ZooConstant.BEGIN_TIME, DateUtil.formatyyyyMMddHHmmss(new Date()));
        contentModel.put(ZooConstant.END_TIME, DateUtil.formatyyyyMMddHHmmss(new Date()));
        contentModel.put(ZooConstant.THRESHOLD, 0);
        contentModel.put(ZooConstant.AFF_NAME, "");
        contentModel.put(ZooConstant.OFFER_ID, "");
        contentModel.put(ZooConstant.OFFER_NAME, "");
        contentModel.put(ZooConstant.PARTNER_OFFER_ID, "");
        contentModel.put(ZooConstant.MESSAGE, "测试---动物园数据库连接异常请检查");
        contentModel.put(ZooConstant.EMAIL_UUID, UUID.randomUUID().toString());
        return contentModel;
    }


    private void closeOfferRedis(String appId, String offerId, String operator) {
        log.info("UPDATE ALARM REDIS, EPM / FILTER, APPID:{},OPERAOTR:{},OFFERID:{}", appId, operator, offerId);
        //更新redis, 从epmlist 跟 zoo_offer_assign:filter删除
        String epmListKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + CacheNameSpace.LIST;
        Boolean existEpmList = cluster3RedisTemplate.hasKey(epmListKey);
        if (existEpmList != null && existEpmList) {
            stringRedisTemplate.opsForList().remove(epmListKey, 0, offerId);
        }
        String assignKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator;
        Boolean existOfferAssign = cluster3RedisTemplate.opsForHash().hasKey(assignKey, offerId);
        if (existOfferAssign != null && existOfferAssign) {
            stringRedisTemplate.opsForHash().delete(assignKey, offerId);
        }
        stringRedisTemplate.opsForHash().increment(CacheNameSpace.ZOO_UNUSED_OFFER, offerId, 1);
    }

    public List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    @Override
    public List getOneMonthRevenue() {
        List list = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -NumberEnum.TWENTY_NINE.getNum());
        String day = DateUtil.formatDay(calendar);
        if (day.substring(NumberEnum.ZERO.getNum(), NumberEnum.SEVEN.getNum()).equalsIgnoreCase(DateUtil.formatMonth(new Date()))) {
            list = generateLocalRevenue(day, list);
        } else {
            // 需要分表查询 30天跨两个月
            String beforeMonth = DateUtil.beforeMonth();
            String monthStr = DateUtil.today().substring(0, NumberEnum.SEVEN.getNum());
            String beforeMonthStr = ZooConstant.EPM_INFO_TABLE_NAME + beforeMonth.replaceAll("-", "");
            list = generateEpmRevenues(beforeMonthStr, day);
            // 当前月份
            List monthList = generateEpmRevenues(ZooConstant.EPM_INFO_TABLE_NAME, monthStr + "-01");
            list.addAll(monthList);
        }
        return list;
    }

    private List generateLocalRevenue(String day, List list) {
        List<Double> revenues = affEpmInfoRepo.queryOneMonthRevenue(day, DateUtil.formatMonth(new Date()));
        if (revenues != null && revenues.size() > 0) {
            for (Double revenue : revenues) {
                list.add(new BigDecimal(revenue).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
        }
        return list;
    }

    private List<Double> generateEpmRevenues(String tableName, String day) {
        List<Double> revenues = new ArrayList<>();
        String sql = "select t.revenue from (select sum(m.revenue) revenue,substring(m.calculate_hour, 1, 10) as date from " + tableName + " m where substring(m.calculate_hour, 1, 10)>= \'" + day + "\'and m.calculate_hour like ?1 group by substring(m.calculate_hour, 1, 10)) as t order by t.date";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        nativeQuery.setParameter(1, ZooConstant.PERCENT_SIGN + day.substring(0, 7) + ZooConstant.PERCENT_SIGN);
        List<Object> resultList = nativeQuery.getResultList();
        for (Object result : resultList) {
            revenues.add(new BigDecimal(result.toString()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
        return revenues;
    }

    @Override
    public String comparativeData(List<JSONObject> list, String begin, String end) {
        List<String> offerIds = new ArrayList<>();
        Date beganDate = new Date(Long.parseLong(begin));
        Date endDate = new Date(Long.parseLong(end));
        List<ComparativeModdel> comparativeModdels = comparativeRepo.comparativeData(beganDate, endDate);
        for (ComparativeModdel comparativeModdel : comparativeModdels) {
            for (JSONObject jsonObject : list) {
                if (jsonObject.getString("systemOfferId") != null) {
                    if (jsonObject.getString("systemOfferId").equalsIgnoreCase(comparativeModdel.getOfferId())) {
                        if (comparativeModdel.getCount() != jsonObject.getLong("transNum")) {
                            offerIds.add(comparativeModdel.getOfferId());
                        }
                    }
                }
            }
        }
        if (offerIds.size() > 0) {
            return offerIds.toString();
        } else {
            return null;
        }
    }

    /**
     * 同步老的epmlist 至新的epmlist
     */
    @Override
    public void syncCalculateEpm() {
        Set<String> oldEpmListKeys = cluster3RedisTemplate.keys(CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.ASTERISK + CacheNameSpace.LIST);
        if (oldEpmListKeys != null && oldEpmListKeys.size() > 0) {
            for (String oldEpmKey : oldEpmListKeys) {
                List<String> oldEpmList = cluster3RedisTemplate.opsForList().range(oldEpmKey, 0, -1);
                String appId = oldEpmKey.split(CacheNameSpace.COLON)[2];
                if (oldEpmList != null && oldEpmList.size() > 0) {
                    for (String offerId : oldEpmList) {
                        OfferModel offerModel = offerService.getOfferModel(offerId);
                        if (offerModel != null) {
                            stringRedisTemplate.opsForList().rightPush(CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator() + CacheNameSpace.COLON + CacheNameSpace.LIST, offerId);
                        }
                    }
                }
            }
        }
    }

    @Override
    @Async
    public void importUnused() throws ParseException {
        String epmCounterYesterDayPartner = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + DateUtil.yesterday() + CacheNameSpace.ASTERISK;
        String epmCounterTodayPartner = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + DateUtil.today() + CacheNameSpace.ASTERISK;
        handleImportUnusedEpm(epmCounterYesterDayPartner);
        handleImportUnusedEpm(epmCounterTodayPartner);
    }

    @Timed
    @Override
    public void checkUnusedOffer() {
        Map<Object, Object> unusedMap = cluster3RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_UNUSED_OFFER);
        for (Map.Entry<Object, Object> entry : unusedMap.entrySet()) {
            String offerId = String.valueOf(entry.getKey());
            OfferModel offerModel = offerService.getOfferModel(offerId);
            if (offerModel != null) {
                producer.sendToQueueOfferCheckUnused(new BaseSqsMessage(new Integer[]{ZooConstant.QUEUE_OFFER_REFRESH_UNUSED}, ZooConstant.QUEUE_OFFER_REFRESH_UNUSED_MODEL, JSON.toJSONString(offerModel)));
            }
        }
    }

    /**
     * 处理unused offer
     *
     * @param msgBody
     */
    @Override
    public void handleUnUseOffer(String msgBody) {
        OfferModel offerModel = JSONObject.parseObject(msgBody, OfferModel.class);
        String offerId = offerModel.getIdentification();
        String restTimeZone = offerModel.getResetTimezone();
        String resetTime = offerModel.getResetTime() == null ? "00:00:00" : offerModel.getResetTime();
        log.info("UPDATE UNUSED OFFER, OFFERID:{},RESETIMEZONE:{},RESTTIME:{},CURRENTTIME:{}", offerModel.getIdentification(), restTimeZone, resetTime, DateUtil.formatyyyyMMddHHmmss(new Date()));
        if (DateUtil.needRefresh(restTimeZone, resetTime)) {
            log.info("NEED UPDATE UNUSED OFFER, OFFERID:{},RESETIMEZONE:{},RESTTIME:{},CURRENTTIME:{}", offerModel.getIdentification(), restTimeZone, resetTime, DateUtil.formatyyyyMMddHHmmss(new Date()));
            stringRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_UNUSED_OFFER, offerId);
            if (offerModel.getStatus() == 1) {
                List<String> appIds = offerTagRepo.findAppIdsByOfferId(offerId);
                if (appIds != null && appIds.size() > 0) {
                    for (String appId : appIds) {
                        String assignKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator();
                        Boolean existOfferKey = cluster3RedisTemplate.opsForHash().hasKey(assignKey, offerId);
                        if (existOfferKey != null && existOfferKey) {
                            log.info("EXIST APP FILTER ,APPID :{}, OFFERID:{}", appId, offerId);
                        } else {
                            OfferModel filterModel = new OfferModel();
                            filterModel.setMaxPull(offerModel.getMaxPull());
                            filterModel.setAppCap(offerModel.getAppCap());
                            filterModel.setCap(offerModel.getCap());
                            String stack = offerTagRepo.findOfferStackId(offerId);
                            filterModel.setStack(stack);
                            filterModel.setOperator(offerModel.getOperator());
                            filterModel.setResetTimezone(offerModel.getResetTimezone());
                            filterModel.setResetTime(offerModel.getResetTime());
                            stringRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_UNUSED_OFFER, offerId);
                            if (getIsTime(offerId)) {
                                stringRedisTemplate.opsForHash().put(assignKey, offerId, JSON.toJSONString(filterModel));
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void importMsisdnParam() {
        List<MsisdnParamModel> msisdnParamModelList = msisdnParamRepo.findAll();
        if (msisdnParamModelList != null && msisdnParamModelList.size() > 0) {
            for (MsisdnParamModel msisdnParamModel : msisdnParamModelList) {
                String msisdnKey = CacheNameSpace.ZOO_MSISDN_PARAM_INFO;
                stringRedisTemplate.opsForHash().put(msisdnKey, msisdnParamModel.getOperator(), JSON.toJSONString(msisdnParamModel));
            }
        }
    }

    private void handleImportUnusedEpm(String epmCounterPartner) throws ParseException {
        Set<String> epmCounterList = cluster3RedisTemplate.keys(epmCounterPartner);
        if (epmCounterList != null && epmCounterList.size() > 0) {
            for (String epmCountKey : epmCounterList) {
                String appId = epmCountKey.split(CacheNameSpace.COLON)[2];
                String offerId = epmCountKey.split(CacheNameSpace.COLON)[3];
                String date = epmCountKey.split(CacheNameSpace.COLON)[4];
                boolean existEpm = affEpmInfoRepo.existsByResourceIdAndOfferIdAndCalculateHour(appId, offerId, date);
                if (!existEpm) {
                    OfferModel offerModel = offerService.getOfferModel(offerId);
                    if (offerModel != null) {
                        Long clickNum = stringRedisTemplate.opsForHash().increment(epmCountKey, CacheNameSpace.CLICK, 0);
                        Long appTransNum = stringRedisTemplate.opsForHash().increment(epmCountKey, CacheNameSpace.APP_TRANS, 0);
                        Long postBackTransNum = stringRedisTemplate.opsForHash().increment(epmCountKey, CacheNameSpace.POST_BACK_TRANS, 0);
                        Double revenue = stringRedisTemplate.opsForHash().increment(epmCountKey, CacheNameSpace.REVENUE, 0d);
                        Calendar calendar = getTimeCalendar(date);
                        Long moNum = 0L;
                        if (!StringUtils.isEmpty(offerModel.getPayShortCode())) {
                            moNum = stringRedisTemplate.opsForHash().increment(epmCountKey, CacheNameSpace.MO_TRANS, 0);
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
                        AffEpmInfoModel affEpmInfoModel = getEpmInfoModel(appId, date, sdf, clickNum, appTransNum, postBackTransNum, offerId, offerModel, revenue, moNum);
                        affEpmInfoRepo.save(affEpmInfoModel);
                    }
                }
            }
        }
    }

    private Calendar getTimeCalendar(String strTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        Date date = sdf.parse(strTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Override
    public JSONObject getTranNumAndRevenue(String startDate, String endDate, List<String> resourceNames, List<String> operators, String country, List<String> times) {
        log.info("into getTranNumAndRevenue");
        JSONObject jsonObject = new JSONObject();
        // 判断是否获取实时(优化之后app分析不查实时)
        List<RevenueAndTranNumModel> redisData = new ArrayList<>();
        List<RevenueAndTranNumModel> revenueAndTranNumModels = comboModel(redisData, startDate, endDate, resourceNames, operators, country, times);
        log.info("[revenueAndTranNumModels]" + revenueAndTranNumModels.toString());
        if (revenueAndTranNumModels != null && revenueAndTranNumModels.size() > 0) {
            for (int i = 0; i < revenueAndTranNumModels.size(); i++) {
                if (revenueAndTranNumModels.get(i).getTime() == null) {
                    jsonObject.put(revenueAndTranNumModels.get(i).getAppId() + "_" + revenueAndTranNumModels.get(i).getOperator(), revenueAndTranNumModels.get(i));
                } else {
                    jsonObject.put(revenueAndTranNumModels.get(i).getTime(), revenueAndTranNumModels.get(i));
                }
            }
        }
        log.info("[getTranNumAndRevenue][jsonObject]:" + jsonObject.toJSONString());
        return jsonObject;
    }

    @Override
    public String checkEpmList() {
        Calendar calendar = getCurrentHourCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        String date = sdf.format(calendar.getTime());
        String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + date;
        Set<String> hKeySet = cluster3RedisTemplate.keys(pattern);
        if (hKeySet != null && hKeySet.size() > 0) {
            return ZooConstant.SUCCESS;
        } else {
            return ZooConstant.FAIL;
        }
    }

    @Override
    @Async
    public void recheckOfferEpm(String ip) {
        String day = DateUtil.yesterday();
        deleteEpmHourRedis(day);
        for (int i = 0; i < NumberEnum.TWENTY_FOUR.getNum(); i++) {
            String dayTime = "";
            if (i < 10) {
                dayTime = day + "-0" + i;
            } else {
                dayTime = day + "-" + i;
            }
            String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + dayTime;
            Set<String> hKeySet = cluster3RedisTemplate.keys(pattern);
            if (hKeySet != null && hKeySet.size() > 0) {
                for (String hKey : hKeySet) {
                    Long clickNum = stringRedisTemplate.opsForHash().increment(hKey, CacheNameSpace.CLICK, 0);
                    Long appTransNum = stringRedisTemplate.opsForHash().increment(hKey, CacheNameSpace.APP_TRANS, 0);
                    Long postBackTransNum = stringRedisTemplate.opsForHash().increment(hKey, CacheNameSpace.POST_BACK_TRANS, 0);
                    Double revenue = stringRedisTemplate.opsForHash().increment(hKey, CacheNameSpace.REVENUE, 0d);
                    // zoo:offer_epm_hour:2021-05-31-10
                    String offerHourEpm = ZooConstant.ZOO + ZooConstant.COLON + ZooConstant.OFFER_EPM_HOUR + ZooConstant.COLON + dayTime;
                    Boolean existOfferHourEpm = cluster3RedisTemplate.hasKey(offerHourEpm);
                    updateOfferHourEpm(offerHourEpm, clickNum, appTransNum, postBackTransNum, revenue);
                    boolean epmHour = existOfferHourEpm != null && existOfferHourEpm;
                    if (!epmHour) {
                        stringRedisTemplate.expire(offerHourEpm, 1, TimeUnit.DAYS);
                    }
                }
            }
        }
        compareEpmAndDb(day, ip);
    }

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @Override
    public JSONArray recheckEpmHour(String date) {
        JSONArray info = new JSONArray();
        try {
            String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + date;
            Set<String> hKeySet = cluster3RedisTemplate.keys(pattern);
            if (hKeySet != null && hKeySet.size() > 0) {
                for (String key : hKeySet) {
                    JSONObject result = compareEpmHourData(key, date);
                    if (result != null) {
                        info.add(result);
                    }
                }
            }
        } catch (Exception e) {
            log.info("ZOO EPM COMPARE DB AND REDIS HOUR STEP-1 ERROR:{}", e.getMessage());
        }
        return info;
    }

    @Override
    public void savePaySubMo(String shortCode, String keyword, String operator, String partner) {
        OfferModel offerModel = offerRepo.findFirstByPayShortCodeAndPayKeywordAndOperatorAndPartner(shortCode, keyword, operator, partner);
        if (offerModel != null) {
            List<String> appIds = offerTagRepo.findAppIdsByOfferId(offerModel.getIdentification());
            if (appIds != null && appIds.size() > 0) {
                String key = getRedisKey(appIds.get(0), offerModel.getIdentification(), ZooConstant.CATEGORY_APP);
                Boolean existKey = cluster3RedisTemplate.hasKey(key);
                updateRedisEpmCount(offerModel.getIdentification(), appIds.get(0), key);
                if (!(existKey != null && existKey)) {
                    stringRedisTemplate.expire(key, 1, TimeUnit.DAYS);
                }
                // 写入转化
                String moKey = ZooConstant.ZOO_PAY_MO_POSTBACK + ZooConstant.COLON + offerModel.getIdentification()
                        + ZooConstant.COLON + DateUtil.formatDay(new Date());
                Boolean existMoKey = stringRedisTemplate.hasKey(moKey);
                stringRedisTemplate.opsForHash().increment(moKey, CacheNameSpace.MO_TRANS, 1);
                if (!(existMoKey != null && existKey)) {
                    stringRedisTemplate.expire(moKey, 1, TimeUnit.DAYS);
                }
                stringRedisTemplate.opsForHash().increment(key, CacheNameSpace.MO_TRANS, 1);
            }

        }
    }

    @Override
    public void incrPayMoTrans() {
        String calculateTime = DateUtil.formatDay(new Date());
        List<AffEpmInfoModel> affEpmInfoModelList = affEpmInfoRepo.findTodayEpmModels(calculateTime);
        if (affEpmInfoModelList != null && affEpmInfoModelList.size() > 0) {
            for (AffEpmInfoModel affEpmInfoModel : affEpmInfoModelList) {
                String offerId = affEpmInfoModel.getOfferId();
                OfferModel offerModel = offerService.getOfferModel(offerId);
                if (offerModel != null) {
                    if (!StringUtils.isEmpty(offerModel.getPayShortCode()) && !StringUtils.isEmpty(offerModel.getPayKeyword())) {
                        offerRepo.updatePayMoTrans(affEpmInfoModel.getTransNum(), 0L, affEpmInfoModel.getIdentification());
                    }
                }
            }
        }
    }

    @Override
    public void syncOnlineEpm(String appId, String operator, String time) {
        List<AffEpmInfoModel> epmInfoModels = affEpmInfoRepo.findEpmModelList(appId, time, operator);
        epmInfoModels.stream().forEach(a -> {
            String key = "zoo_aff_epm_counter:app:" + appId + ":";
            String offerId = a.getOfferId();
            key = key + offerId + ":" + time;
            Map<String, String> param = new HashMap<>(1);
            param.put("click", String.valueOf(a.getClickNum()));
            param.put("country", a.getCountry());
            param.put("offerName", a.getOfferName());
            param.put("cap", String.valueOf(a.getCap()));
            param.put("belong", String.valueOf(a.getBelong()));
            param.put("partner", a.getPartner());
            param.put("appName", a.getResourceName());
            param.put("appId", appId);
            param.put("offerId", offerRepo.findByIdentification(a.getOfferId()).getOfferId());
            param.put("partnerOfferId", a.getPartnerOfferId());
            param.put("offer_status", String.valueOf(a.getOfferStatus()));
            param.put("operator", a.getOperator());
            param.put("app_trans", String.valueOf(a.getAppTransNum()));
            param.put("post_back_trans", String.valueOf(a.getTransNum()));
            param.put("revenue", String.valueOf(a.getRevenue()));
            stringRedisTemplate.opsForHash().putAll(key, param);
        });
    }


    @SuppressFBWarnings("RC_REF_COMPARISON")
    public JSONObject compareEpmHourData(String key, String date) {
        JSONObject result = new JSONObject();
        try {
            Long postBackTransNum = stringRedisTemplate.opsForHash().increment(key, CacheNameSpace.POST_BACK_TRANS, 0);
            String[] strArr = key.split(CacheNameSpace.COLON);
            String appId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[2] : ZooConstant.UNKNOWN;
            String offerId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[3] : ZooConstant.UNKNOWN;
            boolean existDbInfo = affEpmInfoRepo.existsByResourceIdAndOfferIdAndCalculateHour(appId, offerId, date);
            if (!existDbInfo) {
                result.put(ZooConstant.NOT_EXIST_DB, key);
            } else {
                AffEpmInfoModel affEpmInfoModel = affEpmInfoRepo.findFirstByResourceIdAndOfferIdAndCalculateHour(appId, offerId, date);
                if (affEpmInfoModel != null) {
                    boolean transSame = postBackTransNum == affEpmInfoModel.getTransNum();
                    if (!transSame) {
                        result.put(ZooConstant.TRANS_COUNT_NOT_MATCH, key);
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            log.info("ZOO EPM COMPARE DB AND REDIS HOUR STEP-2 ERROR:{},KEY:{}", e.getMessage(), key);
        }
        return result;
    }

    private void deleteEpmHourRedis(String day) {
        String offerHourEpm = ZooConstant.ZOO + ZooConstant.COLON + ZooConstant.OFFER_EPM_HOUR + ZooConstant.COLON + day + ZooConstant.STAR;
        Set<String> epmHourKeys = cluster3RedisTemplate.keys(offerHourEpm);
        if (epmHourKeys != null && epmHourKeys.size() > 0) {
            for (String key : epmHourKeys) {
                stringRedisTemplate.delete(key);
            }
        }
    }

    /**
     * 对比数据
     *
     * @param dayTime
     */
    private void compareEpmAndDb(String dayTime, String ip) {
        String pattern = ZooConstant.ZOO + ZooConstant.COLON + ZooConstant.OFFER_EPM_HOUR + ZooConstant.COLON + dayTime + ZooConstant.STAR;
        Set<String> hKeySet = cluster3RedisTemplate.keys(pattern);
        List<JSONObject> alarmArray = new ArrayList<>();
        if (hKeySet != null && hKeySet.size() > 0) {
            for (String hKey : hKeySet) {
                String calcuateTime = hKey.split(ZooConstant.COLON)[2];
                List<Object> epmValue = affEpmInfoRepo.findEpmValue(calcuateTime);
                JSONObject result = compareDate(hKey, epmValue);
                boolean correctData = result.getBoolean("correctData");
                result.put("epmTime", calcuateTime);
                log.info("RECHECK EPM HOUR :{},IP:{}", JSON.toJSONString(result), ip);
                if (!correctData) {
                    alarmArray.add(result);
                }
            }
        }
        sendRecheckEpmHourMail(alarmArray);
    }

    private void sendRecheckEpmHourMail(List<JSONObject> result) {
        try {
            Map<String, Object> contentModel = generateEpmHourContent(result);
            log.info(ZooConstant.RECHECK_EPM_HOUR_SUBJECT + " LOG INFO:{}", JSON.toJSONString(result));
            String[] emails = {"david.li@starpavilion-digital.com", "alie.shi@starpavilion-digital.com", "aaron.huang@starpavilion-digital.com", "lemon.tian@starpavilion-digital.com"};
            emailUtil.sendMimeMessageMail(ZooConstant.RECHECK_EPM_HOUR_TEMPLATE, emails[emails.length - 1],
                    ZooConstant.RECHECK_EPM_HOUR_SUBJECT, contentModel, emails);
        } catch (Exception e) {
            log.error("Send recheck epm hour email error:{}", JSON.toJSONString(e));
        }
    }


    private void sendEpmListExceptionMail(Map<String, Object> result) {
        try {
            log.info(ZooConstant.EPM_LIST_EXCEPTION_SUBJECT + " LOG INFO:{}", JSON.toJSONString(result));
            String email = "ops@starpavilion-digital.com";
            emailUtil.sendMimeMessageMail(ZooConstant.EPM_LIST_EXCEPTION_TEMPLATE, email,
                    ZooConstant.EPM_LIST_EXCEPTION_SUBJECT, result);
        } catch (Exception e) {
            log.error("Send recheck epm hour email error:{}", JSON.toJSONString(e));
        }
    }

    private Map<String, Object> generateEpmHourContent(List<JSONObject> result) {
        Map<String, Object> contentModel = new HashMap<>(9);
        contentModel.put(ZooConstant.TITLE, ZooConstant.RECHECK_EPM_HOUR_SUBJECT);
        contentModel.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
        StringBuffer time = new StringBuffer();
        StringBuffer alarmInfo = new StringBuffer();
        for (JSONObject info : result) {
            time.append(info.getString(ZooConstant.EPM_TIME)).append(Constants.COMMA);
            alarmInfo.append("[ 时间段: " + info.getString(ZooConstant.EPM_TIME) + ",  redis 转化数: " + info.getString(ZooConstant.REDIS_TRAN_NUM) + ",  数据库转化数: " + info.getString(ZooConstant.DB_TRAN_NUM) + " ] " + "\r\n");
        }
        contentModel.put(ZooConstant.EPM_TIME, time);
        contentModel.put(ZooConstant.ALARM_INFO, alarmInfo.toString());
        contentModel.put(ZooConstant.EMAIL_UUID, UUID.randomUUID().toString());
        return contentModel;
    }

    /**
     * 比较数据是否正确
     *
     * @param hKey
     * @param epmValue
     * @return
     */
    @SuppressFBWarnings({"DM_BOXED_PRIMITIVE_FOR_PARSING", "EC_UNRELATED_TYPES", "EC_UNRELATED_TYPES"})
    private JSONObject compareDate(String hKey, List<Object> epmValue) {
        JSONObject result = new JSONObject();
        boolean correctData = true;
        int dbTranNum = 0;
        Long redisTranNum = 0L;
        Boolean existEpm = cluster3RedisTemplate.hasKey(hKey);
        for (int i = 0; i < epmValue.size(); i++) {
            Object[] arr = (Object[]) epmValue.get(i);
            if (arr[1] != null) {
                dbTranNum = Integer.valueOf(arr[1].toString());
            }
        }
        // 如果转化数量不对则redis 数据与数据库数据不一致
        if (existEpm != null && existEpm) {
            redisTranNum = stringRedisTemplate.opsForHash().increment(hKey, CacheNameSpace.POST_BACK_TRANS, 0);
            correctData = redisTranNum.intValue() <= dbTranNum;
        } else {
            if (dbTranNum > 0) {
                correctData = false;
            }
        }
        result.put("correctData", correctData);
        result.put("dbTranNum", dbTranNum);
        result.put("redisTranNum", redisTranNum);
        return result;
    }

    private void updateOfferHourEpm(String offerHourEpm, Long clickNum, Long appTransNum, Long postBackTransNum, Double revenue) {
        stringRedisTemplate.opsForHash().increment(offerHourEpm, CacheNameSpace.CLICK, clickNum.longValue());
        stringRedisTemplate.opsForHash().increment(offerHourEpm, CacheNameSpace.APP_TRANS, appTransNum.longValue());
        stringRedisTemplate.opsForHash().increment(offerHourEpm, CacheNameSpace.POST_BACK_TRANS, postBackTransNum.longValue());
        stringRedisTemplate.opsForHash().increment(offerHourEpm, CacheNameSpace.REVENUE, revenue.doubleValue());
    }


    /**
     * 从redis中获取小时数据(转化数和收益)
     *
     * @param calendar
     * @param country
     * @param appIds
     * @param operators
     * @param times
     * @return
     */
    public List<RevenueAndTranNumModel> getRedisData(Calendar calendar, String country, List<String> appIds, List<String> operators, List<String> times) {
        List<RevenueAndTranNumModel> models = new ArrayList();
        //遍历当前小时的 redis EPM counter 如果存在该 offer 回传不支持透传的， 则 categoryId 为 NONE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + sdf.format(calendar.getTime());
        Set<String> hKeySet = cluster3RedisTemplate.keys(pattern);
        if (times != null && times.size() > 0) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH");
        }
        if (hKeySet != null && hKeySet.size() > 0) {
            for (String hKey : hKeySet) {
                try {
                    //redis hash:【aff_epm_counter:type:app-identification:offer-identification:yyyy-MM-dd-HH】
                    String[] strArr = hKey.split(CacheNameSpace.COLON);
                    String category = strArr != null && strArr.length == NumberEnum.FIVE.getNum() ? strArr[1] : ZooConstant.UNKNOWN;
                    String categoryId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[2] : ZooConstant.UNKNOWN;
                    String offerId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[3] : ZooConstant.UNKNOWN;
                    Long transNum = stringRedisTemplate.opsForHash().increment(hKey, CacheNameSpace.POST_BACK_TRANS, 0);
                    Double revenue = stringRedisTemplate.opsForHash().increment(hKey, CacheNameSpace.REVENUE, 0d);
                    RevenueAndTranNumModel revenueAndTranNumModel = getRevenueAndTranNumModel(category, categoryId, transNum, revenue, offerId, sdf.format(calendar.getTime()));
                    models.add(revenueAndTranNumModel);
                } catch (Exception e) {
                    log.error("redis 获取小时数据失败:" + e);
                }
            }
        }

        List<RevenueAndTranNumModel> resultList = new ArrayList<>();
        if (models != null && models.size() > 0) {
            for (RevenueAndTranNumModel model : models) {
                boolean flag = true;
                if (!StringUtils.isEmpty(country) && !country.equalsIgnoreCase(model.getCountry())) {
                    flag = false;
                }
                if (appIds != null && appIds.size() > 0) {
                    if (operators != null && operators.size() > 0 && !operators.contains(model.getOperator())) {
                        flag = false;
                    }
                    if (!appIds.contains(model.getAppId())) {
                        flag = false;
                    }
                }
                if (flag) {
                    resultList.add(model);
                }
            }
        }
        log.info("计算小时收益和转化结束");
        return resultList;

    }

    public RevenueAndTranNumModel getRevenueAndTranNumModel(String category, String categoryId, long transNum, double revenue, String offerId, String time) {
        RevenueAndTranNumModel model = new RevenueAndTranNumModel();
        OfferModel offerModel = offerRepo.findFirstByIdentification(offerId);
        model.setTransNum(String.valueOf(transNum));
        model.setRevenue(String.valueOf(revenue));
        model.setCountry(offerModel.getCountry());
        model.setOperator(offerModel.getOperator());
        model.setTime(time);
        if (ZooConstant.APP.equalsIgnoreCase(category)) {
            ApplicationModel appModel = applicationService.getById(categoryId);
            model.setAppId(appModel != null ? appModel.getAppName() : ZooConstant.UNKNOWN);
        } else if ((ZooConstant.AFFILIATE.equalsIgnoreCase(category))) {
            AffSmartLinkModel affSmartLinkModel = affSmartLinkRepo.findFirstByIdentification(categoryId);
            model.setAppId(affSmartLinkModel != null ? affSmartLinkModel.getName() : ZooConstant.UNKNOWN);
        }
        return model;
    }


    /**
     * 合并结果
     *
     * @param redisData
     * @param startDate
     * @param endDate
     * @param resourceNames
     * @param operators
     * @param country
     * @param times
     * @return
     */
    public List<RevenueAndTranNumModel> comboModel(List<RevenueAndTranNumModel> redisData, String startDate, String endDate, List<String> resourceNames, List<String> operators, String country, List<String> times) {
        List<RevenueAndTranNumModel> result = new ArrayList<>();
        List<RevenueAndTranNumModel> mysqlData = new ArrayList<>();
        String[] start = startDate.split(":");
        String[] end = endDate.split(":");
        String monthTime = DateUtil.formatYearMonthTime(DateUtil.formatHourTime(end[0]));
		// todo 判断前后是否在同一月否则要查两次表
        String tableName = "t_aff_epm";
        try {
            // 判断是否跨月
            String todayMonthTime = DateUtil.formatYearMonthTime(new Date());
            String beginEightTime = DateUtil.addTime(startDate, DateConstant.DAY_STR, NumberEnum.ONE.getNum());
            if (beginEightTime.substring(0, NumberEnum.SEVEN.getNum()).equals(endDate.substring(0, NumberEnum.SEVEN.getNum()))) {
                // 查询某月
                if (!todayMonthTime.equals(monthTime)) {
                    tableName += monthTime;
                }
                mergeMysqlDateList(mysqlData, times, resourceNames, operators, tableName, start, end, country);
            } else {
                // 跨月查询
                int monthCount = DateUtil.getMonthSpace(beginEightTime, endDate);
                for (int i = 0; i <= monthCount; i++) {
                    String monthStr = DateUtil.addMonth(beginEightTime.substring(0, NumberEnum.SEVEN.getNum()), i);
                    tableName = ZooConstant.EPM_INFO_TABLE_NAME + monthStr.replaceAll(ZooConstant.EQUALS, "");
                    if (todayMonthTime.equalsIgnoreCase(monthStr.replaceAll(ZooConstant.EQUALS, ""))) {
                        tableName = ZooConstant.EPM_INFO_TABLE_NAME;
                    }
                    mergeMysqlDateList(mysqlData, times, resourceNames, operators, tableName, start, end, country);
                }
            }
        } catch (Exception e) {
            log.error("findTranNumAndRevenue error [message]:" + e);
        }
        log.info("[mysqlData]:" + mysqlData.toString());
        if (redisData.size() > 0 && mysqlData.size() > 0) {
            LinkedHashMap<String, RevenueAndTranNumModel> map = getMap(mysqlData, redisData);
            for (Map.Entry<String, RevenueAndTranNumModel> entry : map.entrySet()) {
                result.add(entry.getValue());
            }
        } else if (redisData.size() > 0 && mysqlData.size() == 0) {
            return redisData;
        } else if (redisData.size() == 0 && mysqlData.size() > 0) {
            // 取消实时查询redis为0
            return mysqlData;
        }
        log.info("[comboModelResult]:" + result);
        return result;
    }

    private void mergeMysqlDateList(List<RevenueAndTranNumModel> mysqlData, List<String> times, List<String> resourceNames, List<String> operators, String tableName, String[] start, String[] end, String country) {
        StringBuilder sql = null;
        if (times != null && times.size() > 0) {
            sql = new StringBuilder("SELECT DATE_FORMAT(createtime,'%Y-%m-%d %H') as time,SUM(trans_num),sum(revenue) FROM " + tableName + " WHERE date_format(createtime,'%Y-%m-%d %H') BETWEEN :startDate AND :endDate AND country = :country");
            if (resourceNames != null && resourceNames.size() > 0) {
                sql = sql.append(" AND resource_name IN :resource_names");
            }
            if (operators != null && operators.size() > 0) {
                sql = sql.append(" AND operator IN :operators");
            }
            sql = sql.append(" GROUP BY time");
        } else {
            sql = new StringBuilder("SELECT resource_name,operator,SUM(trans_num),sum(revenue) FROM " + tableName + " WHERE date_format(createtime,'%Y-%m-%d %H') BETWEEN :startDate AND :endDate AND resource_name IN :resource_names AND operator IN :operators AND country = :country GROUP BY resource_name,operator");
        }
        Query query = zooEntityManager.createNativeQuery(sql.toString());
        query.setParameter("startDate", start[0]);
        query.setParameter("endDate", end[0]);
        if (resourceNames != null && resourceNames.size() > 0) {
            query.setParameter("resource_names", resourceNames);
        }
        if (operators != null && operators.size() > 0) {
            query.setParameter("operators", operators);
        }
        query.setParameter("country", country);
        List resultList = query.getResultList();
        log.info("[time]:" + DateUtil.formatyyyyMMddHHmmss(new Date()) + "    [selectSql]:" + sql.toString());
        log.info("sqlParams:[startDate]:" + start[0] + "[endDate]" + end[0]);
        for (int j = 0; j < resultList.size(); j++) {
            Object[] obj = (Object[]) resultList.get(j);
            RevenueAndTranNumModel model = new RevenueAndTranNumModel();
            model.setCountry(country);
            if (times != null && times.size() > 0) {
                model.setTime(obj[0].toString());
                model.setTransNum(obj[1].toString());
                model.setRevenue(obj[2].toString());
            } else {
                model.setAppId(obj[0].toString());
                model.setOperator(obj[1].toString());
                model.setTransNum(obj[2].toString());
                model.setRevenue(obj[3].toString());
            }
            mysqlData.add(model);
        }
    }

    public LinkedHashMap<String, RevenueAndTranNumModel> getMap(List<RevenueAndTranNumModel> mysqlData, List<RevenueAndTranNumModel> redisData) {
        LinkedHashMap<String, RevenueAndTranNumModel> map = new LinkedHashMap<>();
        if (mysqlData != null && mysqlData.size() > 0) {
            for (RevenueAndTranNumModel model : mysqlData) {
                if (model.getAppId() != null) {
                    map.put(model.getAppId() + "_" + model.getOperator(), model);
                } else {
                    map.put(model.getTime(), model);
                }
            }
        }

        if (redisData != null && mysqlData.size() > 0) {
            for (RevenueAndTranNumModel model : redisData) {
                String key = null;
                if (model.getTime() == null) {
                    key = model.getAppId() + "_" + model.getOperator();
                } else {
                    key = model.getTime();
                }
                RevenueAndTranNumModel mapModel = map.get(key);
                if (mapModel == null) {
                    mapModel = model;
                } else {
                    mapModel.setRevenue(String.valueOf(Double.parseDouble(model.getRevenue()) + Double.parseDouble(mapModel.getRevenue())));
                    mapModel.setTransNum(String.valueOf(Long.parseLong(model.getTransNum()) + Long.parseLong(mapModel.getTransNum())));
                }
                map.put(key, mapModel);
            }
        }
        return map;
    }

}
