package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.LogConstant;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.*;
import com.starp.zoo.repo.zoo.AffClickInfoRepo;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.OfferTagRepo;
import com.starp.zoo.service.*;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.EmailUtil;
import com.starp.zoo.util.PatternUtil;
import com.starp.zoo.util.RandomUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@Slf4j
@Service
public class GetOfferServiceImpl implements IGetOfferService {

	@Autowired
	private EmailUtil emailUtil;

	@Autowired
	private IApplicationService applicationService;

	@Autowired
	private ISubscribeService subscribeService;

	@Autowired
	StringRedisTemplate masterRedisTemplate;

	/**
	 * tracking
	 */
	@Resource(name = "trackRedisTemplate")
	private StringRedisTemplate trackRedisTemplate;

	@Resource(name = "cluster1RedisTemplate")
	private StringRedisTemplate cluster1RedisTemplate;


	@Autowired
	private OfferRepo offerRepo;

	@Autowired
	private OfferTagRepo offerTagRepo;

	@Autowired
	private IEpmService epmService;

	@Autowired
	private AffClickInfoRepo affClickInfoRepo;

	@Autowired
	private IOfferService offerService;

	@Autowired
	private ApplicationRepo applicationRepo;

	@Autowired
	private IGetOfferService getOfferService;

	@Timed
	@SuppressFBWarnings({"DM_DEFAULT_ENCODING", "NP_ALWAYS_NULL", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "NP_NULL_PARAM_DEREF", "SA_LOCAL_SELF_COMPARISON", "DLS_DEAD_LOCAL_STORE"})
	@Override
	public OfferModel getEnableOfferModel(String deviceId, String appId, String ipAddress, String operator, List<String> usedList, Boolean isLog, boolean isTest) {
		OfferModel resultModel = new OfferModel();
		//获取app下当前运营商的offer过滤信息列表
		Map<Object, Object> offerFiltersMap = cluster1RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator);
		List<String> offerFiltersKeys = new ArrayList<>();
		for (Object obj : offerFiltersMap.keySet()) {
			offerFiltersKeys.add(String.valueOf(obj));
		}
		if (offerFiltersKeys == null || offerFiltersMap.size() == 0) {
			// 直接拉取测试offer
			return getTestOffer(operator, appId, deviceId);
		}
		//筛选出跑过的 offerTags(APP 需要)
		List<String> usedTags = getOfferService.getUsedStackTags(appId, deviceId, operator, usedList);
		//获取当前运营商下的所有offer最大拉取次数列表
		Map<Object, Object> offerPulledCounterMap = getOfferService.getOfferPulledCounterListWithOperator(appId, operator);
		//获取当前运营商下的所有offer的上游回传转化数列表//todo 需要加operator
		Map<Object, Object> userTransListJson = cluster1RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_USER_TRANS_LIST + appId + CacheNameSpace.COLON + deviceId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + DateUtil.today());
		//获取当前app的epmlist
		List<String> appEpmOfferList = getOfferService.getAppEpmOfferList(appId, offerFiltersKeys, operator);
		int userOfferEpmIndex = getOfferService.getUserOfferEpmIndex(appEpmOfferList, appId, operator);
		String preOfferId = "";
		String filterType = "";
		OfferModel offerFilterModel = new OfferModel();
		int size = appEpmOfferList.size();
		int loop = 1;
		JSONObject resulJSON = getOfferService.checkEnbaleOffer(userOfferEpmIndex, size, appEpmOfferList, preOfferId, offerFiltersKeys, filterType, usedList, usedTags, deviceId, userTransListJson, offerPulledCounterMap, resultModel, appId, operator, offerFilterModel, offerFiltersMap, loop, ipAddress);
		resultModel = JSON.parseObject(resulJSON.getString("offerModel"), OfferModel.class);
		filterType = resulJSON.getString("filterType");
		//如果获取offer失败则返回带错误信息的offer
		if (!StringUtils.isEmpty(filterType)) {
			checkLog(isLog, appId, ipAddress, preOfferId, "error_type-" + filterType);
			setPullOfferErrorMsg(resultModel, filterType, appId, operator, userOfferEpmIndex, appEpmOfferList, offerFiltersKeys);
		}
		if (resultModel == null || !StringUtils.isEmpty(resultModel.getErrorMsg())) {
			// 没有可用的offer，此处开始拉取测试offer
			resultModel = getTestOffer(operator, appId, deviceId);
		}
		return resultModel;
	}

	/**
	 * 拉取测试offer
	 * @param operator
	 * @param appId
	 * @param deviceId
	 * @return com.starp.zoo.entity.zoo.OfferModel
	 * @author Curry
	 * @date 2023/5/24
	 */
	private OfferModel getTestOffer(String operator, String appId, String deviceId) {
		OfferModel offerModel = new OfferModel();
		List<String> testOfferIds = cluster1RedisTemplate.opsForList().range(CacheNameSpace.ZOO_AUTO_TEST_OFFER_ID, 0, -1);
		if (testOfferIds == null || testOfferIds.size() == 0) {
			offerModel.setErrorCode(ResponseInfoEnum.NULL_ENABLE_TEST_OFFER.getCode());
			offerModel.setErrorMsg(ResponseInfoEnum.NULL_ENABLE_TEST_OFFER.getMsg());
			return offerModel;
		}
		String testEpmCountKey = CacheNameSpace.ZOO_AUTO_TEST_OFFER_EPM_COUNT + DateUtil.today();
		Long testOfferEpmCount = Long.valueOf(String.valueOf(cluster1RedisTemplate.opsForValue().get(testEpmCountKey) == null ? "0" : cluster1RedisTemplate.opsForValue().get(testEpmCountKey)));
		masterRedisTemplate.opsForValue().increment(testEpmCountKey, 1);
		//如果为 0 则设置 ttl 为 24h
		if (testOfferEpmCount == 0) {
			masterRedisTemplate.expire(testEpmCountKey, NumberEnum.ONE_DAY_MILLISECONDS.getNum(), TimeUnit.MILLISECONDS);
		}
		int pullIndex = (int) (testOfferEpmCount % testOfferIds.size());
		offerModel = loopTestOffer(testOfferIds, pullIndex, operator, appId, deviceId);
		return offerModel;
	}

	/**
	 * 遍历可用测试offer
	 * @param testOfferIds
	 * @param pullIndex
	 * @param operator
	 * @param appId
	 * @param deviceId
	 * @return com.starp.zoo.entity.zoo.OfferModel
	 * @author Curry
	 * @date 2023/5/24
	 */
	private OfferModel loopTestOffer(List<String> testOfferIds, int pullIndex, String operator, String appId, String deviceId) {
		OfferModel offerModel = new OfferModel();
		for (String ignored : testOfferIds) {
			String offerId = testOfferIds.get(pullIndex);
			offerModel = offerService.getOfferModel(offerId);
			if (offerModel == null) {
				pullIndex++;
				continue;
			}
			boolean isUse = isEnabledTestOffer(offerModel, operator, appId, deviceId);
			if (isUse) {
				String offerPullCountKey = CacheNameSpace.ZOO_OFF_PULL_COUNTER + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + DateUtil.today();
				Long offerPullCount = masterRedisTemplate.opsForHash().increment(offerPullCountKey, offerId, 1L);
				String offerDayPullCountKey = CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification());
				Long offerDayPullCount = masterRedisTemplate.opsForHash().increment(offerDayPullCountKey, CacheNameSpace.PULL_COUNT, 1);
				if (offerPullCount == 1) {
					masterRedisTemplate.expire(offerPullCountKey, NumberEnum.ONE.getNum(), TimeUnit.DAYS);
				}
				if (offerDayPullCount == 1) {
					masterRedisTemplate.expire(offerDayPullCountKey, 2, TimeUnit.DAYS);
				}
				return offerModel;
			}
			// 拉取到最后一个重新开始拉取
			if (pullIndex == testOfferIds.size() - 1) {
				pullIndex = 0;
			} else {
				pullIndex++;
			}
		}
		// 遍历完毕没有可用 offer 赋值错误信息
		offerModel.setErrorCode(ResponseInfoEnum.NULL_ENABLE_TEST_OFFER.getCode());
		offerModel.setErrorMsg(ResponseInfoEnum.NULL_ENABLE_TEST_OFFER.getMsg());
		return offerModel;
	}

	/**
	 * 判断测试offer是否可用
	 * @param offerModel
	 * @param operator
	 * @param appId
	 * @param deviceId
	 * @return boolean
	 * @author Curry
	 * @date 2023/5/24
	 */
	public boolean isEnabledTestOffer(OfferModel offerModel, String operator, String appId, String deviceId) {
		// offer 关闭、运营商不匹配、不是利刃不拉取
		if (offerModel.getStatus() == NumberEnum.ZERO.getNum() || !offerModel.getOperator().equals(operator) || !offerModel.getCrackType().equals(NumberEnum.ONE.getNum())) {
			return false;
		}
		// 判断是否达到最大拉取次数
		if (offerModel.getMaxPull() != null) {
			// 获取最大拉取统计数
			Object pullCount = cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.PULL_COUNT);
			int count = StringUtils.isEmpty(pullCount) ? 0 : Integer.parseInt(String.valueOf(pullCount));
			if (count >= offerModel.getMaxPull()) {
				offerModel.setTestStatus(1);
				offerModel.setErrorCode(0);
				offerModel.setErrorMsg(null);
				// 达到拉取次数将offer的测试状态改为待分配
				offerRepo.updateTestStatusByIdentification(1, offerModel.getIdentification());
				masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification(), JSON.toJSONString(offerModel));
				return false;
			}
		}
		// 判断最大转化数，只允许超出两个
		ApplicationModel applicationModel = applicationService.getAppModel(appId);
		int appOperatorCap = getOperatorCap(appId, operator, applicationModel);
		int userAppOperatorTrans = getUserAppTrans(appId, operator, deviceId);
		boolean userOverAppOperatorTransNum = userAppOperatorTrans >= appOperatorCap + NumberEnum.TWO.getNum();
		if (userOverAppOperatorTransNum) {
			return false;
		}
		return true;
	}

	@SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
	private int getOperatorCap(String appId, String operator, ApplicationModel applicationModel) {
		String cap = "0";
		if (applicationModel != null) {
			cap = String.valueOf(applicationModel.getMaxPullNum());
			String key = ZooConstant.APP_OPERATOR_CAP_KEY;
			String hashKey = operator + ZooConstant.COLON + appId;
			Object capValue = cluster1RedisTemplate.opsForHash().get(key, hashKey);
			if (capValue != null) {
				cap = String.valueOf(capValue);
			}
		}
		return Integer.parseInt(cap);
	}

	@SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
	private int getUserAppTrans(String appId, String operator, String deviceId) {
		int transNum = 0;
		String transKey = CacheNameSpace.ZOO_USER_TRANS_LIST + appId + CacheNameSpace.COLON + deviceId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + DateUtil.today();
		List<Object> values = cluster1RedisTemplate.opsForHash().values(transKey);
		if (values != null && values.size() > 0) {
			transNum = values.size();
		}
		return transNum;
	}

	/**
	 * checkEnbaleOffer
	 *
	 * @param userOfferEpmIndex
	 * @param size
	 * @param appEpmOfferList
	 * @param preOfferId
	 * @param offerFiltersKeys
	 * @param filterType
	 * @param usedList
	 * @param usedTags
	 * @param deviceId
	 * @param userTransListJson
	 * @param offerPulledCounterMap
	 * @param resultModel
	 * @param appId
	 * @param operator
	 * @param offerFilterModel
	 * @param offerFiltersMap
	 * @param loop
	 * @param ip
	 * @return
	 */
	@Timed
	@Override
	public JSONObject checkEnbaleOffer(int userOfferEpmIndex, int size, List<String> appEpmOfferList, String preOfferId, List<String> offerFiltersKeys, String filterType, List<String> usedList, List<String> usedTags, String deviceId, Map<Object, Object> userTransListJson, Map<Object, Object> offerPulledCounterMap,
	                                   OfferModel resultModel, String appId, String operator, OfferModel offerFilterModel, Map<Object, Object> offerFiltersMap, int loop, String ip) {
		JSONObject resultJson = new JSONObject();
		String protectedId = CacheNameSpace.ZOO_PROTECTED_TAG + CacheNameSpace.COLON + operator;
		List<String> protectedOfferList = getOfferService.findProtectedTagList(protectedId);
		boolean pullTopOffer = getOfferService.checkCanPullTopOffer(offerFiltersMap, protectedOfferList, ip, deviceId, offerFiltersKeys, usedList, usedTags, offerFilterModel, userTransListJson, offerPulledCounterMap, appId, operator);
		if (pullTopOffer) {
			resultModel = handleTopOffer(appId, operator, appEpmOfferList);
		} else {
			for (int i = userOfferEpmIndex; i < size; i++) {
				String currOfferId = appEpmOfferList.get(i);
				if (checkCurrentOfferIsTopOffer(currOfferId, appId, operator)) {
					continue;
				}
				// 是否是上一个轮询不符合拉取条件的同offer
				if (preOfferId.equals(currOfferId)) {
					continue;
				}
				preOfferId = currOfferId;
				if (!offerFiltersKeys.contains(currOfferId)) {
					log.error("PULL_ERROR_OFFER:{}, FILTER_KEY:{}, LIST_KEY:{}", JSONObject.toJSONString(offerService.getOfferModel(currOfferId)), offerFiltersKeys, appEpmOfferList);
					filterType = "5";
					synEpmAndFilter(appId, operator, currOfferId);
					if (i == appEpmOfferList.size() - 1 && loop == 1) {
						resultModel = reloopOffer(size - 1, i, appEpmOfferList, preOfferId, offerFiltersKeys, filterType, usedList, usedTags, deviceId, userTransListJson, offerPulledCounterMap, resultModel, appId, operator, offerFilterModel, offerFiltersMap, loop, ip);
						break;
					} else {
						continue;
					}
				}
				offerFilterModel = generateFilterOfferModel(offerFilterModel, offerFiltersMap, currOfferId);
				if (usedList != null && usedList.contains(currOfferId)) {
					filterType = "2";
					if (i == appEpmOfferList.size() - 1 && loop == 1) {
						resultModel = reloopOffer(size, i, appEpmOfferList, preOfferId, offerFiltersKeys, filterType, usedList, usedTags, deviceId, userTransListJson, offerPulledCounterMap, resultModel, appId, operator, offerFilterModel, offerFiltersMap, loop, ip);
						break;
					} else {
						continue;
					}
				}
				if (usedTags != null && usedTags.contains(offerFilterModel.getStack())) {
					filterType = "3";
					if (i == appEpmOfferList.size() - 1 && loop == 1) {
						resultModel = reloopOffer(size, i, appEpmOfferList, preOfferId, offerFiltersKeys, filterType, usedList, usedTags, deviceId, userTransListJson, offerPulledCounterMap, resultModel, appId, operator, offerFilterModel, offerFiltersMap, loop, ip);
						break;
					} else {
						continue;
					}
				}
				if (subscribeService.isTransformedFromRedis(offerFilterModel, deviceId, userTransListJson)) {
					filterType = "4";
					if (i == appEpmOfferList.size() - 1 && loop == 1) {
						resultModel = reloopOffer(size, i, appEpmOfferList, preOfferId, offerFiltersKeys, filterType, usedList, usedTags, deviceId, userTransListJson, offerPulledCounterMap, resultModel, appId, operator, offerFilterModel, offerFiltersMap, loop, ip);
						break;
					} else {
						continue;
					}
				}
				if (offerFilterModel.getMaxPull() != null && checkMaxPull(offerFilterModel, offerPulledCounterMap, appId, currOfferId)) {
					filterType = "0";
					if (i == appEpmOfferList.size() - 1 && loop == 1) {
						resultModel = reloopOffer(size, i, appEpmOfferList, preOfferId, offerFiltersKeys, filterType, usedList, usedTags, deviceId, userTransListJson, offerPulledCounterMap, resultModel, appId, operator, offerFilterModel, offerFiltersMap, loop, ip);
						break;
					} else {
						continue;
					}
				}
				resultModel = offerService.getOfferModel(currOfferId);
				if (null != resultModel) {
					filterType = "";
					getOfferService.handleUpdatePullOfferRedis(appId, operator, currOfferId, resultModel, loop, appEpmOfferList, i);
					break;
				}
			}
		}
		resultJson = generateResutJson(resultJson, resultModel, filterType);
		return resultJson;
	}

	private OfferModel handleTopOffer(String appId, String operator, List<String> appEpmOfferList) {
		String offerId = findTopOfferId(appId, operator);
		OfferModel resultModel = offerService.getOfferModel(offerId);
		getOfferService.handleUpdatePullOfferRedis(appId, operator, offerId, resultModel, 1, appEpmOfferList, 0);
		return resultModel;
	}

	@Override
	@Cacheable(value = "protectTag", key = "#protectTagId")
	public List<String> findProtectedTagList(String protectTagId) {
		List<String> protectedTagList = cluster1RedisTemplate.opsForList().range(protectTagId, 0, -1);
		return protectedTagList;
	}

	/**
	 * 删除epmList中错误的不符合条件offer
	 * @param appId
	 * @param operator
	 * @param currOfferId
	 * @return void
	 * @author Curry
	 * @date 2023/4/23
	 */
	private void synEpmAndFilter(String appId, String operator, String currOfferId) {
		String appEpmListKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + CacheNameSpace.LIST;
		log.info("SYNSC EPM AND FILTER, DELETE EPM OFFER, HASH KEY:{}", appEpmListKey);
		Boolean existEpmList = cluster1RedisTemplate.hasKey(appEpmListKey);
		if (existEpmList != null && existEpmList) {
			masterRedisTemplate.opsForList().remove(appEpmListKey, 0, currOfferId);
		}
	}

	@Timed
	@Override
	public boolean checkCanPullTopOffer(Map<Object, Object> offerFiltersMap, List<String> protectedOfferList, String ip, String deviceId, List<String> offerFiltersKeys, List<String> usedList, List<String> usedTags, OfferModel offerFilterModel, Map<Object, Object> userTransListJson, Map<Object, Object> offerPulledCounterMap, String appId, String operator) {
		boolean pullTopOffer = false;
		Object obj = cluster1RedisTemplate.opsForHash().get(ZooConstant.APP_OPERATOR_TOP_OFFER + appId, operator);
		if (obj != null) {
			String topOfferId = String.valueOf(obj);
			boolean canUseTopOffer = checkExistTopOffer(topOfferId);
			offerFilterModel = generateFilterOfferModel(offerFilterModel, offerFiltersMap, topOfferId);
			boolean statusIsOk = checkOfferPullStatus(protectedOfferList, topOfferId, ip, deviceId, offerFiltersKeys, usedList, usedTags, offerFilterModel, userTransListJson, offerPulledCounterMap, appId);
			if (canUseTopOffer && statusIsOk) {
				pullTopOffer = true;
			}
		}
		return pullTopOffer;
	}


	private String findTopOfferId(String appId, String operator) {
		String topOfferId = "";
		Object obj = cluster1RedisTemplate.opsForHash().get(ZooConstant.APP_OPERATOR_TOP_OFFER + appId, operator);
		if (obj != null) {
			topOfferId = String.valueOf(obj);
		}
		return topOfferId;
	}

	private OfferModel findOfferModel(String offerId) {
		OfferModel resultModel;
		Object obj = cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId);
		if (obj != null) {
			log.info("CHECK TOP OFFER FROM REDIS :{}", cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId));
			resultModel = JSON.parseObject(String.valueOf(obj), OfferModel.class);
		} else {
			resultModel = offerRepo.findByIdentification(offerId);
		}
		return resultModel;
	}

	private JSONObject generateResutJson(JSONObject resultJson, OfferModel resultModel, String filterType) {
		if (resultModel != null && !StringUtils.isEmpty(resultModel.getIdentification())) {
			resultJson.put("filterType", "");
		} else {
			resultJson.put("filterType", filterType);
		}
		resultJson.put("offerModel", JSON.toJSONString(resultModel));
		return resultJson;
	}

	private boolean checkExistTopOffer(String topOfferId) {
		Boolean existUnusedOffer = cluster1RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_UNUSED_OFFER, topOfferId);
		boolean canUseTopOffer = !(existUnusedOffer != null && existUnusedOffer);
		log.info("UNUSED OFFER EXIST TOP OFFER:{},OFFERID:{}", canUseTopOffer, topOfferId);
		return canUseTopOffer;
	}

	private OfferModel generateFilterOfferModel(OfferModel offerFilterModel, Map<Object, Object> offerFiltersMap, String currOfferId) {
		offerFilterModel = JSON.parseObject(String.valueOf(offerFiltersMap.get(currOfferId)), OfferModel.class);
		if (offerFilterModel != null) {
			offerFilterModel.setIdentification(currOfferId);
			return offerFilterModel;
		} else {
			return null;
		}
	}

	private boolean checkCurrentOfferIsTopOffer(String currOfferId, String appId, String operator) {
		Object obj = cluster1RedisTemplate.opsForHash().get(ZooConstant.APP_OPERATOR_TOP_OFFER + appId, operator);
		if (obj != null) {
			String topOfferId = String.valueOf(obj);
			if (topOfferId.equalsIgnoreCase(currOfferId)) {
				return true;
			}
		}
		return false;
	}

	private String logFilterType(int num, String ip, String currOfferId, String deviceId) {
		String filter = "";
		if (num == NumberEnum.SEVEN.getNum()) {
			filter = "7";
			log.info(" PROTECTED OFFER SEVENT DAY, IP :{}, OFFER:{}, DEVICEID:{}", ip, currOfferId, deviceId);
		} else {
			filter = "30";
			log.info(" PROTECTED OFFER THIRTY DAY, IP :{}, OFFER:{}, DEVICEID:{}", ip, currOfferId, deviceId);
		}
		return filter;
	}

	private boolean checkOfferPullStatus(List<String> protectedOfferList, String currOfferId, String ip, String deviceId, List<String> offerFiltersKeys, List<String> usedList, List<String> usedTags, OfferModel offerFilterModel, Map<Object, Object> userTransListJson, Map<Object, Object> offerPulledCounterMap, String appId) {
		if (offerFilterModel == null) {
			return false;
		}
		boolean protecteOfferCantPull = false;
		boolean isProtectedOffer = protectedOfferList != null && protectedOfferList.size() > 0 && protectedOfferList.indexOf(currOfferId) > -1;
		if (isProtectedOffer) {
			boolean protectOffer = checkProtectedOffer(ip, deviceId, currOfferId);
			if (protectOffer) {
				protecteOfferCantPull = true;
			}
			protectOffer = checkProtectedThirtyOffer(ip, deviceId, currOfferId);
			if (protectOffer) {
				protecteOfferCantPull = true;
			}
		}
		boolean statusIsOk = offerFiltersKeys.contains(currOfferId) && !(usedList != null && usedList.contains(currOfferId))
				&& !(usedTags != null && usedTags.contains(offerFilterModel.getStack())) &&
				!subscribeService.isTransformedFromRedis(offerFilterModel, deviceId, userTransListJson) &&
				!(offerFilterModel.getMaxPull() != null && checkMaxPull(offerFilterModel, offerPulledCounterMap, appId, currOfferId)) && !protecteOfferCantPull;
		return statusIsOk;
	}

	private OfferModel reloopOffer(int size, int i, List<String> appEpmOfferList, String preOfferId, List<String> offerFiltersKeys, String filterType, List<String> usedList, List<String> usedTags, String deviceId, Map<Object, Object> userTransListJson, Map<Object, Object> offerPulledCounterMap, OfferModel resultModel, String appId, String operator, OfferModel offerFilterModel, Map<Object, Object> offerFiltersMap, int loop, String ip) {
		int start = 0;
		loop++;
		String offerModel = checkEnbaleOffer(start, size, appEpmOfferList, preOfferId, offerFiltersKeys, filterType, usedList, usedTags, deviceId, userTransListJson, offerPulledCounterMap, resultModel, appId, operator, offerFilterModel, offerFiltersMap, loop, ip).getString("offerModel");
		resultModel = JSON.parseObject(offerModel, OfferModel.class);
		return resultModel;
	}

	@Timed
	@Override
	public void handleUpdatePullOfferRedis(String appId, String operator, String currOfferId, OfferModel resultModel, int loop, List<String> appEpmOfferList, int userOfferEpmIndex) {
		String offerPullCountKey = CacheNameSpace.ZOO_OFF_PULL_COUNTER + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + DateUtil.today();
		Long offerPullCount = masterRedisTemplate.opsForHash().increment(offerPullCountKey, currOfferId, 1L);
		String offerDayPullCountKey = CacheNameSpace.ZOO_OFFER_DAY_INFO + currOfferId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(resultModel.getResetTimezone(), resultModel.getResetTime(), resultModel.getIdentification());
		Long offerDayPullCount = masterRedisTemplate.opsForHash().increment(offerDayPullCountKey, CacheNameSpace.PULL_COUNT, 1);
		if (offerPullCount == 1) {
			masterRedisTemplate.expire(offerPullCountKey, NumberEnum.ONE.getNum(), TimeUnit.DAYS);
		}
		if (offerDayPullCount == 1) {
			masterRedisTemplate.expire(offerDayPullCountKey, 2, TimeUnit.DAYS);
		}
		boolean isTopOffer = checkIsTopOffer(appId, operator, currOfferId);
		boolean updateEpmCount = !isTopOffer && appEpmOfferList.get(userOfferEpmIndex).equalsIgnoreCase(resultModel.getIdentification());
		if (updateEpmCount) {
			String appPullCounterInEpmKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + CacheNameSpace.COUNT + CacheNameSpace.COLON + DateUtil.today();
			masterRedisTemplate.opsForValue().increment(appPullCounterInEpmKey, 1L);
			masterRedisTemplate.expire(appPullCounterInEpmKey, NumberEnum.ONE.getNum(), TimeUnit.DAYS);
		}
	}

	private boolean checkIsTopOffer(String appId, String operator, String currOfferId) {
		boolean checkResult = false;
		Boolean existTopOffer = cluster1RedisTemplate.opsForHash().hasKey(ZooConstant.APP_OPERATOR_TOP_OFFER + appId, operator);
		if (existTopOffer != null && existTopOffer) {
			checkResult = currOfferId.equalsIgnoreCase(String.valueOf(cluster1RedisTemplate.opsForHash().get(ZooConstant.APP_OPERATOR_TOP_OFFER + appId, operator) == null ? "" : cluster1RedisTemplate.opsForHash().get(ZooConstant.APP_OPERATOR_TOP_OFFER + appId, operator)));
		}
		return checkResult;
	}

	@Timed
	@Override
	public boolean checkProtectedThirtyOffer(String ip, String deviceId, String currOfferId) {
		boolean existIpOffer = false;
		boolean existUserIdOffer = false;
		String protectIpThirtyDayKey = CacheNameSpace.ZOO_PROTECTED_OFFER_THIRTY_DAY + CacheNameSpace.IP + CacheNameSpace.COLON + ip;
		String protectUserIdThirtyDayKey = CacheNameSpace.ZOO_PROTECTED_OFFER_THIRTY_DAY + CacheNameSpace.USERID + CacheNameSpace.COLON + deviceId;
		Boolean existIpKey = cluster1RedisTemplate.hasKey(protectIpThirtyDayKey);
		Boolean existUserIdKey = cluster1RedisTemplate.hasKey(protectUserIdThirtyDayKey);
		if (existIpKey != null && existIpKey) {
			Long ipCount = masterRedisTemplate.opsForValue().increment(protectIpThirtyDayKey, 0);
			existIpOffer = ipCount != null && ipCount >= 5L;
			log.info("PROTECT OFFER THIRTY DAYS KEY:{}, COUNT:{},RESULT:{}", protectIpThirtyDayKey, ipCount, existIpOffer);
		}
		if (existUserIdKey != null && existUserIdKey) {
			Long userIdCount = masterRedisTemplate.opsForValue().increment(protectUserIdThirtyDayKey, 0);
			existUserIdOffer = userIdCount != null && userIdCount >= 5L;
		}
		if (existUserIdOffer) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断
	 *
	 * @param ip
	 * @param deviceId
	 * @param currOfferId
	 * @return
	 */
	@Timed
	@Override
	public boolean checkProtectedOffer(String ip, String deviceId, String currOfferId) {
		boolean existIpOffer = false;
		boolean existUserIdOffer = false;
		String protectIpSevenDayKey = CacheNameSpace.ZOO_PROTECTED_OFFER_SEVEN_DAY + CacheNameSpace.IP + CacheNameSpace.COLON + ip + CacheNameSpace.COLON + currOfferId;
		String protectUserIdSevenDayKey = CacheNameSpace.ZOO_PROTECTED_OFFER_SEVEN_DAY + CacheNameSpace.USERID + CacheNameSpace.COLON + deviceId + CacheNameSpace.COLON + currOfferId;
		Boolean existIpKey = cluster1RedisTemplate.hasKey(protectIpSevenDayKey);
		Boolean existUserIdKey = cluster1RedisTemplate.hasKey(protectUserIdSevenDayKey);
		if (existIpKey != null && existIpKey) {
			List<String> protectIpOfferList = cluster1RedisTemplate.opsForList().range(protectIpSevenDayKey, 0, -1);
			existIpOffer = protectIpOfferList != null && protectIpOfferList.size() > 0 && protectIpOfferList.indexOf(currOfferId) > -1;
			log.info("PROTECT OFFER THIRTY DAYS KEY:{}, RESULT:{}", protectIpSevenDayKey, existIpOffer);
		}
		if (existUserIdKey != null && existUserIdKey) {
			List<String> protectUserIdOfferList = cluster1RedisTemplate.opsForList().range(protectUserIdSevenDayKey, 0, -1);
			existUserIdOffer = protectUserIdOfferList != null && protectUserIdOfferList.size() > 0 && protectUserIdOfferList.indexOf(currOfferId) > -1;
		}
		if (existUserIdOffer) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	@Timed
	@Override
	public List<String> getAppEpmOfferList(String appId, List<String> offerFiltersKeys, String operator) {
		//先获取 epm List长度
		String appEpmListKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + CacheNameSpace.LIST;
		Long appEpmListSize = cluster1RedisTemplate.opsForList().size(appEpmListKey);
		List<String> appEpmOfferList = new ArrayList<>();
		if (appEpmListSize != null && appEpmListSize > 0) {
			//listKey:【aff_epm_list:xxx:app-identification:list】
			appEpmOfferList = cluster1RedisTemplate.opsForList().range(appEpmListKey, 0, -1);
			// 判断epm 里的 offer 和 offer Map 里的 offer 是否保持一致，不一致且有新增则将 epm list 末尾加上新增 offer 的主键
			addNewOfferToEpmList(offerFiltersKeys, appEpmOfferList);
		} else {
			appEpmOfferList = offerFiltersKeys;
		}

		return appEpmOfferList;
	}

	@Timed
	@Override
	public Map<Object, Object> getOfferPulledCounterListWithOperator(String appId, String operator) {
		String offerPullCounterKey = CacheNameSpace.ZOO_OFF_PULL_COUNTER + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + DateUtil.today();
		Map<Object, Object> offerPullCounter = new HashMap<>(1);
		Boolean existOfferPullKey = cluster1RedisTemplate.hasKey(offerPullCounterKey);
		if (null != existOfferPullKey && existOfferPullKey) {
			offerPullCounter.putAll(cluster1RedisTemplate.opsForHash().entries(offerPullCounterKey));
		} else {
			masterRedisTemplate.expire(offerPullCounterKey, NumberEnum.ONE.getNum(), TimeUnit.DAYS);
		}
		return offerPullCounter;
	}

	@Timed
	@Override
	public int getUserOfferEpmIndex(List<String> appEpmOfferList, String appId, String operator) {
		//获取当前用户的index,countKey:【aff_epm_list:xxx:app-identification:count:2019-01-22】
		String appPullCounterInEpmKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + CacheNameSpace.COUNT + CacheNameSpace.COLON + DateUtil.today();
		Long appPullCounterInEpm = Long.valueOf(String.valueOf(cluster1RedisTemplate.opsForValue().get(appPullCounterInEpmKey) == null ? "0" : cluster1RedisTemplate.opsForValue().get(appPullCounterInEpmKey)));
		//如果其实为 0 则设置 ttl 为 24h
		if (appPullCounterInEpm == 0) {
			masterRedisTemplate.expire(appPullCounterInEpmKey, NumberEnum.ONE_DAY_MILLISECONDS.getNum(), TimeUnit.MILLISECONDS);
		}
		return appEpmOfferList != null ? (int) (appPullCounterInEpm % appEpmOfferList.size()) : 0;
	}

	/**
	 * 获取完整的 epm list (包含新增的 offer)
	 */
	private void addNewOfferToEpmList(List<String> offerFiltersKeys, List<String> appEpmOfferList) {
		int firstOfferCounter = 0;
		if (appEpmOfferList != null && appEpmOfferList.size() > 0) {
			String preOfferId = "";
			int maxNum = appEpmOfferList.lastIndexOf(appEpmOfferList.get(0)) + 1;
			for (int i = 0; i < appEpmOfferList.size(); i++) {
				String currOfferId = appEpmOfferList.get(i);
				if (i > maxNum && !StringUtils.isEmpty(currOfferId) && currOfferId.equals(preOfferId)) {
					break;
				}
				preOfferId = currOfferId;
				firstOfferCounter++;
			}
		}

		for (String offerId : offerFiltersKeys) {
			if (!appEpmOfferList.contains(offerId)) {
				// 不存在则将该 offer Id 添加到 epm list 中(放在list 前面)
				for (int i = 0; i < firstOfferCounter; i++) {
					appEpmOfferList.add(0, offerId);
				}
			}
		}
	}

	/**
	 * getClickId
	 *
	 * @param appId
	 * @param ipAddress
	 * @param offerId
	 * @return
	 */
	@Override
	public String getClickId(String appId, String ipAddress, String offerId) {
		String clickId = ZooConstant.APP.toUpperCase() + DateUtil.getCurrentTimeSeconds() + RandomUtil.getRandomNum(6);
		Map<String, String> map = new HashMap<>(3);
		map.put(ZooConstant.OFFER_ID, offerId);
		map.put(ZooConstant.APP_ID, appId);
		map.put(ZooConstant.IP, ipAddress);
		String key = ZooConstant.ZOO_CLICK_ID + ZooConstant.COLON + clickId;
		trackRedisTemplate.opsForHash().putAll(key, map);
		trackRedisTemplate.expire(key, NumberEnum.TWO.getNum(), TimeUnit.DAYS);
		return clickId;
	}

	@Override
	public String formatUrl(String appId, String clickId, OfferModel offerModel) throws Exception {
		String url = "";
		if (offerModel != null && !StringUtils.isEmpty(offerModel.getUrl())) {
			// replace appId(扩展透传参数)
			url = getReplaceUrl(offerModel.getUrl(), offerModel.getExtendParam(), appId);
			// replace clickId(点击参数)
			url = getReplaceUrl(url, offerModel.getClickIdParam(), clickId);
			// replace offerId(上游offer主键)
			url = getReplaceUrl(url, offerModel.getPartnerOfferIdParam(), offerModel.getPartnerOfferId());
			url = url.replace(ZooConstant.PARAM_CLICKID, clickId);
			url = encodeOthersValue(url);
		}
		return url;
	}

	public String getReplaceUrl(String url, String param, String value) {
		Matcher matcher = PatternUtil.URL_PARAMS_PATTERN.matcher(url);
		//先替换透传再替换clickId 防止clickId参数与透传参数一样
		if (!StringUtils.isEmpty(param)) {
			if (url.contains(param + ZooConstant.EQUAL_MARK)) {
				while (matcher.find()) {
					if (matcher.group(2).equals(param)) {
						//替换 appId
						url = url.replace(matcher.group(2) + ZooConstant.EQUAL_MARK + matcher.group(3), matcher.group(2) + ZooConstant.EQUAL_MARK + value);
					}
				}
			} else {
				url += url.contains(ZooConstant.INTERROGATION_MARK) ? "" : ZooConstant.INTERROGATION_MARK;
				url += ZooConstant.AND_MARK + param + ZooConstant.EQUAL_MARK + value;
			}
		}
		return url;
	}

	public String encodeOthersValue(String url) throws Exception {
		Matcher matcher = PatternUtil.URL_PARAMS_PATTERN.matcher(url);
		while (matcher.find()) {
			url = url.replace(matcher.group(2) + ZooConstant.EQUAL_MARK + matcher.group(3), matcher.group(2) + ZooConstant.EQUAL_MARK + URLEncoder.encode(matcher.group(3), "UTF-8"));
		}
		return url;
	}

	public static String formatDateByTimeZone(OfferModel offerModel) {
		Calendar calendar = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (offerModel != null) {
			if (!StringUtils.isEmpty(offerModel.getResetTimezone())) {
				df.setTimeZone(TimeZone.getTimeZone(offerModel.getResetTimezone()));
			}
			if (!StringUtils.isEmpty(offerModel.getResetTime())) {
				String resetTimeStr = offerModel.getResetTime();
				String[] timeArr = resetTimeStr.split(ZooConstant.COLON);
				if (timeArr != null && timeArr.length == NumberEnum.THREE.getNum()) {
					calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArr[0]));
					calendar.add(Calendar.MINUTE, Integer.parseInt(timeArr[1]));
					calendar.add(Calendar.SECOND, Integer.parseInt(timeArr[2]));
				}
			}
		}
		return df.format(calendar.getTime());
	}

	@SuppressFBWarnings("SF_SWITCH_NO_DEFAULT")
	private void setPullOfferErrorMsg(OfferModel offerModel, String type, String appId, String operator, int userOfferEpmIndex, List<String> appEpmOfferList, List<String> offerFiltersKeys) {
		String epmKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator;
		String filterKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator;
		log.info("PULL OFFER ERROR INFO LOG,APPID:{},OPERATOR:{},TYPE:{},EPM INDEX:{},INDEX OFFER:{},LAST INDEX OFFER:{}, EPM LAST OFFER EXIST FILTER:{},EPM REDIS KEY:{}, FILTER EPM KEY:{}", appId, operator, type, userOfferEpmIndex, appEpmOfferList.get(userOfferEpmIndex), appEpmOfferList.get(appEpmOfferList.size() - 1), offerFiltersKeys.indexOf(appEpmOfferList.get(appEpmOfferList.size() - 1)), epmKey, filterKey);
		switch (type) {
			case "0":
				offerModel.setErrorCode(ResponseInfoEnum.OVER_OFFER_MAX_PULL_NUM.getCode());
				offerModel.setErrorMsg(ResponseInfoEnum.OVER_OFFER_MAX_PULL_NUM.getMsg());
				break;
			case "1":
				offerModel.setErrorCode(ResponseInfoEnum.TRANSFER_USER_PULL_WEEK.getCode());
				offerModel.setErrorMsg(ResponseInfoEnum.TRANSFER_USER_PULL_WEEK.getMsg());
				break;
			case "2":
				offerModel.setErrorCode(ResponseInfoEnum.ONE_USER_PUFF_OFFER_IN_USERLIST.getCode());
				offerModel.setErrorMsg(ResponseInfoEnum.ONE_USER_PUFF_OFFER_IN_USERLIST.getMsg());
				break;
			case "3":
				offerModel.setErrorCode(ResponseInfoEnum.ONE_USER_PUFF_OFFER_IN_USERTAG.getCode());
				offerModel.setErrorMsg(ResponseInfoEnum.ONE_USER_PUFF_OFFER_IN_USERTAG.getMsg());
				break;
			case "4":
				offerModel.setErrorCode(ResponseInfoEnum.TRANSFER_OFFER_PER_DAY.getCode());
				offerModel.setErrorMsg(ResponseInfoEnum.TRANSFER_OFFER_PER_DAY.getMsg());
				break;
			case "5":
				offerModel.setErrorCode(ResponseInfoEnum.OFFER_NOT_MATCH_OPERATOR.getCode());
				offerModel.setErrorMsg(ResponseInfoEnum.OFFER_NOT_MATCH_OPERATOR.getMsg());
				break;
			case "7":
				offerModel.setErrorCode(ResponseInfoEnum.PROTECTED_OFFER_SEVEN_DAY.getCode());
				offerModel.setErrorMsg(ResponseInfoEnum.PROTECTED_OFFER_SEVEN_DAY.getMsg());
				break;
			case "30":
				offerModel.setErrorCode(ResponseInfoEnum.PROTECTED_OFFER_THIRTY_DAY.getCode());
				offerModel.setErrorMsg(ResponseInfoEnum.PROTECTED_OFFER_THIRTY_DAY.getMsg());
				break;
			default:
				break;

		}

	}

	@Override
	public void sendOfferMaxPullMail(String offerId) {
		Object obj = cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId);
		if (obj != null) {
			OfferModel offerModel = JSON.parseObject(String.valueOf(obj), OfferModel.class);
			boolean checkUnSend = offerModel == null || StringUtils.isEmpty(offerModel.getEmail()) || !offerModel.isAlarmThresholdCheck();
			if (checkUnSend) {
				return;
			}
			try {
				String key = CacheNameSpace.ZOO_OFFER_MAX_PULL_EMAIL + CacheNameSpace.COLON + offerModel.getIdentification();
				Long count = masterRedisTemplate.opsForValue().increment(key, 1);
				if (count != null && count > 1) {
					return;
				}
				log.info("Send offer Max Pull Email OFFER:[{}]", JSON.toJSONString(offerModel));
				Map<String, Object> model = new HashMap<>(1);
				model.put(ZooConstant.TITLE, ZooConstant.MAX_PULL_MAIL_SUBJECT);
				model.put(ZooConstant.MESSAGE, "该OFFER 已满足当日最大拉取数，请及时安排下一步操作。");
				model.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
				model.put(ZooConstant.AFFILIATE, offerModel.getPartner());
				model.put(ZooConstant.OFFER_NAME, offerModel.getOfferName());
				model.put(ZooConstant.OFFER_ID, offerModel.getOfferId());
				model.put(ZooConstant.PARTNER_OFFER_ID, offerModel.getPartnerOfferId());
				model.put(ZooConstant.COMMENT, offerModel.getComment());
				model.put(ZooConstant.EMAIL_UUID, UUID.randomUUID().toString());
				emailUtil.sendMimeMessageMail(ZooConstant.MAX_PULL_TEMPLATE, offerModel.getEmail(), ZooConstant.MAX_PULL_MAIL_SUBJECT, model);
				masterRedisTemplate.expire(key, NumberEnum.ONE.getNum(), TimeUnit.DAYS);
			} catch (Exception e) {
				log.error("最大拉取次数告警邮件发送异常", e);
			}
		}

	}

	private OfferModel getOfferModel(String id) {
		OfferModel offerModel;
		String offerStr = (String) cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, id);
		if (StringUtils.isEmpty(offerStr)) {
			offerModel = offerRepo.findFirstByIdentification(id);
			if (offerModel != null) {
				String stackId = offerTagRepo.findOfferStackId(offerModel.getIdentification());
				offerModel.setStack(stackId);
			}
		} else {
			offerModel = JSON.parseObject(offerStr, OfferModel.class);
		}
		if (offerModel != null && offerModel.getStatus() == ZooConstant.STATUS_1) {
			return offerModel;
		}
		return null;
	}

	/**
	 * 检查最大拉取
	 *
	 * @param offerModel
	 * @return
	 */
	@SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
	private boolean checkMaxPull(OfferModel offerModel, Map<Object, Object> offerPullCounters, String appId, String currOfferId) {
		// 获取最大拉取统计数stringRedisTemplate.hasKey(offerPullCounterKey)
		int count = 0;
		List<String> appIds = offerTagRepo.findAppIdsByOfferId(currOfferId);
		Boolean existPullCount = cluster1RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.PULL_COUNT);
		if (existPullCount != null && existPullCount) {
			count = Integer.valueOf(String.valueOf(cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.PULL_COUNT)));
		}
		if (offerModel.getMaxPull() != null && count >= offerModel.getMaxPull()) {
			for (String appIdKey : appIds) {
				String appEpmListKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appIdKey + CacheNameSpace.COLON + offerModel.getOperator() + CacheNameSpace.COLON + CacheNameSpace.LIST;
				masterRedisTemplate.opsForList().remove(appEpmListKey, 0, offerModel.getIdentification());
				masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER +
						ZooConstant.COLON + appIdKey + ZooConstant.COLON + offerModel.getOperator(), offerModel.getIdentification());
				log.info("UPDATE EPM LIST AND FILTER , DELETE EPMLIST AND FILTER BY OVER OFFER PULL COUNT ,APPID:{}, OFFERID:{}, PULL COUNT:{}, MAXPULL COUNT:{}  ", appId, offerModel.getIdentification(), count, offerModel.getMaxPull());
			}
			// 不满足条件的offer
			masterRedisTemplate.opsForHash().increment(CacheNameSpace.ZOO_UNUSED_OFFER, currOfferId, 1);
			masterRedisTemplate.expire(CacheNameSpace.ZOO_UNUSED_OFFER, 1, TimeUnit.DAYS);
			offerService.sendOfferMaxPullQueue(currOfferId);
			return true;
		}
		return false;
	}

	private void checkLog(Boolean isLog, String categoryId, String ipAddress, String id, String type) {
		if (isLog != null && isLog) {
			log.info("{} [PULL_TASK_WITH_EPM] [APP:{}] [IP:{}]  [ID:{}] [CHECK_USELESS : {}]", LogConstant.ZOO, categoryId, ipAddress, id, type);
		}
	}

	/**
	 * 获取用过的 tag
	 *
	 * @param appId
	 * @param deviceId
	 * @param operator
	 * @param usedList
	 * @return
	 */
	@Timed
	@Override
	public List<String> getUsedStackTags(String appId, String deviceId, String operator, List<String> usedList) {
		List<String> usedTags = new ArrayList<>();
		String key = CacheNameSpace.ZOO_USER_TRANS_LIST + appId + CacheNameSpace.COLON + deviceId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + DateUtil.today();
		List<Object> useOfferList = new ArrayList<>();
		for (String offerId : usedList) {
			useOfferList.add(offerId);
		}
		List<Object> stackList = cluster1RedisTemplate.opsForHash().multiGet(key, useOfferList);
		for (Object stack : stackList) {
			if (stack != null) {
				usedTags.add(String.valueOf(stack));
			}
		}
		return usedTags;
	}
}
