package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.config.aws.sqs.Producer;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.CategoryTagModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.ProductTypeModel;
import com.starp.zoo.entity.zoo.TagModel;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.repo.zoo.CategoryTagRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.OfferTaskRepo;
import com.starp.zoo.repo.zoo.ProductTypeRepo;
import com.starp.zoo.repo.zoo.TagRepo;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.service.ICategoryService;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.EmailUtil;
import com.starp.zoo.util.S3Util;
import com.starp.zoo.vo.AppOpCapVO;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/7.
 */
@Service
@Slf4j
public class ApplicationServiceImpl implements IApplicationService {

    @Autowired
    private ApplicationRepo appInfoRepo;

    @Autowired
    private ProductTypeRepo productTypeRepo;

    @Autowired
    private OfferTaskRepo offerTaskRepo;

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private CategoryTagRepo categoryTagRepo;

    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private StringRedisTemplate masterRedisTemplate;

    @Autowired
    private Producer producer;


    /**
     * 从节点3
     */
    @javax.annotation.Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;


    /**
     * 从节点3
     */
    @javax.annotation.Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private IOfferService offerService;

    @Autowired
    private S3Util s3Util;

    @Autowired
    private EmailUtil emailUtil;

    /**
     * 存储段(桶名)
     */
    @Value("${s3.sdkBucketName}")
    private String bucketName;

    public ApplicationServiceImpl() {
    }


    @Override
    public void save(ApplicationModel applicationModel) {
        appInfoRepo.save(applicationModel);
        masterRedisTemplate.delete("zoo_applicationModel_str_list");
        masterRedisTemplate.delete("zoo_appName_list");
        masterRedisTemplate.delete("zoo_package_name_list");
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_APP, applicationModel.getIdentification(), JSON.toJSONString(applicationModel));
    }

    @Override
    public List<ApplicationModel> getAll(Integer status, String productType) {
        Specification specification = new Specification<ApplicationModel>() {
            @Override
            public Predicate toPredicate(Root<ApplicationModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                if (!StringUtils.isEmpty(productType)) {
                    predicates.add(criteriaBuilder.equal(root.get("productType"), productType));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        List<ApplicationModel> applicationModels = appInfoRepo.findAll(specification);
        List<ProductTypeModel> productTypeRepoList = productTypeRepo.findAll();
        for (ApplicationModel applicationModel : applicationModels) {
            for (ProductTypeModel productTypeModel : productTypeRepoList) {
                if (applicationModel.getProductType().equals(productTypeModel.getProductKey())) {
                    applicationModel.setProductType(productTypeModel.getProductValue());
                }
            }
        }
        return applicationModels;
    }

    @Override
    public ApplicationModel getById(String appId) {
        if (StringUtils.isEmpty(appId)) {
            return null;
        }
        Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, appId);
        if (obj != null) {
            ApplicationModel applicationModel = JSON.parseObject(String.valueOf(obj), ApplicationModel.class);
            return applicationModel;
        } else {
            Optional<ApplicationModel> byId = appInfoRepo.findById(appId);
            if (byId != null && byId.isPresent()) {
                return byId.get();
            }
        }
        return null;
    }

    @Override
    public void deleteById(String appId) {
        offerTaskRepo.deleteByAppId(appId);
        categoryTagRepo.deleteByTypeAndCategoryId(ZooConstant.CATEGORY_APP, appId);
        appInfoRepo.deleteById(appId);
        masterRedisTemplate.delete("zoo_applicationModel_str_list");
        masterRedisTemplate.delete("zoo_appName_list");
        masterRedisTemplate.delete("zoo_package_name_list");

        String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON + ZooConstant.APP + CacheNameSpace.COLON + appId;
        masterRedisTemplate.delete(key);
        Set<String> keyFilters = cluster3RedisTemplate.keys(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.ASTERISK);
        if (keyFilters != null && keyFilters.size() > 0) {
            for (String keyFilter : keyFilters) {
                masterRedisTemplate.delete(keyFilter);
            }
        }
        masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_APP, appId);
        masterRedisTemplate.delete(CacheNameSpace.ZOO_APP + CacheNameSpace.COLON + appId);
        updateAppOfferRedis(appId, 0, null);
    }

    @Override
    public void updateStatus(String appId, Integer status) {
        appInfoRepo.updateStatus(status, appId);
        Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, appId);
        ApplicationModel applicationModel = null;
        if (obj != null) {
            applicationModel = JSON.parseObject(String.valueOf(obj), ApplicationModel.class);
        } else {
            applicationModel = appInfoRepo.findFirstByIdentification(appId);
        }
        if (applicationModel != null) {
            applicationModel.setStatus(status);
            masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_APP, appId);
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_APP, appId, JSON.toJSONString(applicationModel));
            updateAppOfferRedis(appId, status, applicationModel);
        }
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    private void updateAppOfferRedis(String appId, Integer status, ApplicationModel applicationModel) {
        String appEmpKeyStart = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.ASTERISK;
        Set<String> appEpmOperatorListKeys = cluster3RedisTemplate.keys(appEmpKeyStart);
        if (status == 0) {
            Set<String> filterKeys = cluster3RedisTemplate.keys(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.ASTERISK);
            if (filterKeys != null && filterKeys.size() > 0) {
                for (String filterKey : filterKeys) {
                    masterRedisTemplate.delete(filterKey);
                }
            }
            if (appEpmOperatorListKeys != null && appEpmOperatorListKeys.size() > 0) {
                for (String appEpmOperatorKey : appEpmOperatorListKeys) {
                    Boolean existEpmList = cluster3RedisTemplate.hasKey(appEpmOperatorKey);
                    if (existEpmList != null && existEpmList) {
                        masterRedisTemplate.delete(appEpmOperatorKey);
                    }
                }
            }
            Boolean existApp = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_APP, appId);
            if (existApp != null && existApp) {
                deleteAppRedis(appId);
            }
        } else if (status == 1) {
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_APP, applicationModel.getIdentification(), JSON.toJSONString(applicationModel));
            List<String> offerIds = categoryTagRepo.queryOfferIds(appId);
            if (offerIds != null && offerIds.size() > 0) {
                for (String offerId : offerIds) {
                    OfferModel offerModel = offerRepo.findByIdentification(offerId);
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
                    offerService.updateOfferRedis(offerModel, offerId, appId, redisOfferCap, redisAppCap, pullCount, "updateAppOfferRedis");
                }
            }
        }
    }

    @Override
    public boolean checkExists(String name, int type, String appId) {
        int count = appInfoRepo.countByAppName(name);
        if (type == 1 && count > 0) {
            return true;
        }
        if (type == ZooConstant.TYPE_UPDATE && count == 1) {
            ApplicationModel applicationModel = appInfoRepo.findFirstByAppName(name);
            if (applicationModel != null && !applicationModel.getIdentification().equals(appId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkActive(String appId) {
        return appInfoRepo.existsByIdentificationAndStatus(appId, ZooConstant.STATUS_1);
    }

    @Override
    public PageVO getList(int page, int limit, List<String> appNames, String packageName, String productType, Integer status) {
        Specification specification = new Specification<ApplicationModel>() {
            @Override
            public Predicate toPredicate(Root<ApplicationModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(appNames) && appNames.size() > 0) {
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("appName"));
                    for (String appName : appNames) {
                        in.value(appName);
                    }
                    predicates.add(criteriaBuilder.and(in));
                }
                if (!StringUtils.isEmpty(packageName)) {
                    predicates.add(criteriaBuilder.equal(root.get("packageName"), packageName));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                if (!StringUtils.isEmpty(productType)) {
                    predicates.add(criteriaBuilder.equal(root.get("productType"), productType));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        PageVO<JSONObject> pageVO = new PageVO<>();
        Long total = appInfoRepo.count(specification);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setLimit(limit);
        page = page > 0 ? page - 1 : 0;
        List<JSONObject> json = new ArrayList<>();
        List<ApplicationModel> applicationModels = appInfoRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        // 获取所有的 app 标签Map
        Map<String, List<TagModel>> tagModelMap = categoryService.getCategoryTagMap(ZooConstant.CATEGORY_APP);
        Map<String, String> productMap = getProductNameMap();
        for (ApplicationModel applicationModel : applicationModels) {
            applicationModel.setProductType(productMap.get(applicationModel.getProductType()));
            JSONObject jsonObject = (JSONObject) JSON.toJSON(applicationModel);
            jsonObject.put(ZooConstant.TAG_GROUP, tagModelMap.get(applicationModel.getIdentification()));
            json.add(jsonObject);
        }
        pageVO.setList(json);
        return pageVO;
    }

    private List<AppOpCapVO> getAppOperatorCapList(String appId) {
        List<AppOpCapVO> appOpCapVOList = new ArrayList<>();
        String key = ZooConstant.APP_OPERATOR_CAP_KEY;
        Set<Object> hashKeys = cluster3RedisTemplate.opsForHash().keys(key);
        if (hashKeys != null && hashKeys.size() >0) {
            for (Object hashKey : hashKeys) {
                if (String.valueOf(hashKey).indexOf(appId) > 0) {
                    Boolean hasHashKey = cluster3RedisTemplate.opsForHash().hasKey(key, hashKey);
                    if (hasHashKey != null && hasHashKey) {
                        String operator = "";
                        if (hashKey.toString().indexOf(ZooConstant.COLON) > 0) {
                            operator = hashKey.toString().split(ZooConstant.COLON)[0];
                        }
                        String cap = String.valueOf(cluster3RedisTemplate.opsForHash().get(key, hashKey));
                        if (!StringUtils.isEmpty(cap) && !StringUtils.isEmpty(operator)) {
                            AppOpCapVO appOpCapVO = new AppOpCapVO();
                            appOpCapVO.setAppId(appId);
                            appOpCapVO.setCap(cap);
                            appOpCapVO.setOperator(operator);
                            appOpCapVOList.add(appOpCapVO);
                        }
                    }

                }
            }
        }
        return appOpCapVOList;
    }


    private List<JSONObject> getAppTopOperatorList(String appId) {
        List<JSONObject> appOpCapVOList = new ArrayList<>();
        String key = ZooConstant.APP_OPERATOR_TOP_OFFER + appId;
        Boolean exitKey = cluster3RedisTemplate.hasKey(key);
        if (exitKey != null && exitKey) {
            Set<Object> hashKeys = cluster3RedisTemplate.opsForHash().keys(key);
            for (Object hashKey : hashKeys) {
                Boolean hasHashKey = cluster3RedisTemplate.opsForHash().hasKey(key, hashKey);
                if (hasHashKey != null && hasHashKey) {
                    String operator = hashKey.toString();
                    String offerId = String.valueOf(cluster3RedisTemplate.opsForHash().get(key, hashKey));
                    if (!StringUtils.isEmpty(offerId) && !StringUtils.isEmpty(operator)) {
                        OfferModel offerModel = offerService.getOfferModel(offerId);
                        JSONObject opTopOffer = new JSONObject();
                        opTopOffer.put("appId", appId);
                        opTopOffer.put("offerId", offerId);
                        if (offerModel != null) {
                            opTopOffer.put("offerName", offerModel.getOfferName());
                        }
                        opTopOffer.put("operator", operator);
                        appOpCapVOList.add(opTopOffer);
                    }
                }
            }
        }
        return appOpCapVOList;
    }

    private Map<String, String> getProductNameMap() {
        Map<String, String> map = new HashMap<>(1);
        List<ProductTypeModel> productTypeRepoList = productTypeRepo.findAll();
        if (productTypeRepoList != null && productTypeRepoList.size() > 0) {
            for (ProductTypeModel productTypeModel : productTypeRepoList) {
                map.put(productTypeModel.getProductKey(), productTypeModel.getProductValue());
            }
        }
        return map;
    }

    @Override
    public List<OptionVO> getAllAppNames() {
        List<String> result = null;
        String key = "zoo_appName_list";
        Long size = cluster3RedisTemplate.opsForList().size(key);
        if (size != null && size > NumberEnum.ZERO.getNum()) {
            result = cluster3RedisTemplate.opsForList().range(key, 0, -1);
        } else {
            String sql = "select distinct(app_name) from  t_application_info order by app_name asc";
            Query nativeQuery = zooEntityManager.createNativeQuery(sql);
            nativeQuery.unwrap(NativeQuery.class);
            result = nativeQuery.getResultList();
            masterRedisTemplate.opsForList().rightPushAll(key, result);
        }
        List<OptionVO> optionVOS = new ArrayList<>();
        if (result != null && result.size() > 0) {
            for (String appName : result) {
                OptionVO optionVO = new OptionVO();
                optionVO.setIdentification(appName);
                optionVO.setValue(appName);
                optionVO.setLabel(appName);
                optionVOS.add(optionVO);
            }
        }
        return optionVOS;
    }

    @Override
    public List<OptionVO> getAllPackageNames() {
        List<String> result = null;
        String key = "zoo_package_name_list";
        Long size = cluster3RedisTemplate.opsForList().size(key);
        if (size != null && size > NumberEnum.ZERO.getNum()) {
            result = cluster3RedisTemplate.opsForList().range(key, 0, -1);
        } else {
            String sql = "select distinct(package_name) from  t_application_info order by package_name asc";
            Query nativeQuery = zooEntityManager.createNativeQuery(sql);
            nativeQuery.unwrap(NativeQuery.class);
            result = nativeQuery.getResultList();
            Boolean existKey = cluster3RedisTemplate.hasKey(key);
            if (existKey != null && existKey) {
                masterRedisTemplate.opsForList().rightPushAll(key, result);
            }
        }
        List<OptionVO> optionVOS = new ArrayList<>();
        if (result != null && result.size() > 0) {
            for (String packageName : result) {
                OptionVO optionVO = new OptionVO();
                optionVO.setIdentification(packageName);
                optionVO.setValue(packageName);
                optionVO.setLabel(packageName);
                optionVOS.add(optionVO);
            }
        }
        return optionVOS;
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    @Override
    public void resetAppTag(String appId, List<String> tagIds, boolean saveOffer) {
        categoryTagRepo.deleteByTypeAndCategoryId(ZooConstant.CATEGORY_APP, appId);
        String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON + ZooConstant.APP + CacheNameSpace.COLON + appId;
        masterRedisTemplate.delete(key);
        Set<String> filterKeys = cluster3RedisTemplate.keys(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.ASTERISK);
        boolean updateFilter = filterKeys != null && filterKeys.size() > 0 && !saveOffer;
        if (updateFilter) {
            for (String filterKey : filterKeys) {
                masterRedisTemplate.delete(filterKey);
            }
        }
        if (tagIds != null && tagIds.size() > 0) {
            for (String tagId : tagIds) {
                if (!tagRepo.existsById(tagId)) {
                    TagModel tag = new TagModel();
                    tag.setTagName(tagId);
                    tag.setTagType(ZooConstant.TAG_TYPE_GROUP);
                    tagRepo.save(tag);
                    tagId = tag.getIdentification();
                }
                CategoryTagModel categoryTagModel = new CategoryTagModel();
                categoryTagModel.setType(ZooConstant.CATEGORY_APP);
                categoryTagModel.setCategoryId(appId);
                categoryTagModel.setTagId(tagId);
                categoryTagRepo.save(categoryTagModel);
            }
        }
        List<OfferModel> offerModels = null;
        // 将 app 与 offer 的关联信息保存到 redis 中
        if (tagIds != null && tagIds.size() > 0) {
            offerModels = getOfferByTags(tagIds);
        } else {
            masterRedisTemplate.delete(key);
        }
        if (!saveOffer) {
            if (offerModels != null && offerModels.size() > 0) {
                for (OfferModel offerModel : offerModels) {
                    Integer redisOfferCap = 0;
                    Integer redisAppCap = 0;
                    Integer pullCount = 0;
                    Boolean existOfferCap = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.POST_BACK_TRANS);
                    Boolean existAppCap = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.APP_TRANS);
                    Boolean existPullCount = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.PULL_COUNT);
                    if (existOfferCap != null && existOfferCap) {
                        redisOfferCap = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.POST_BACK_TRANS)));
                    }
                    if (existAppCap != null && existAppCap) {
                        redisAppCap = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.APP_TRANS)));
                    }
                    if (existPullCount != null && existPullCount) {
                        pullCount = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.PULL_COUNT)));
                    }
                    offerService.updateOfferRedis(offerModel, offerModel.getIdentification(), appId, redisOfferCap, redisAppCap, pullCount, "resetAppTag");
                }
            }
        }
    }

    @Override
    @Async
    public void saveConfig(ApplicationModel applicationModel, List<String> tagIds, List<AppOpCapVO> opCapList, JSONArray operatorTopOffer, JSONArray sdkUrl, JSONArray epmOperators) {
        ApplicationModel saveModel = appInfoRepo.save(applicationModel);
        Boolean existApp = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_APP, applicationModel.getIdentification());
        if (sdkUrl != null && sdkUrl.size() > 0) {
            applicationModel.setSdkUrl(sdkUrl);
        }
        if (epmOperators != null && epmOperators.size() > 0) {
            applicationModel.setEpmAlarmOperator(epmOperators);
        }
        if (existApp != null && existApp) {
            deleteAppRedis(applicationModel.getIdentification());
        }
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_APP, applicationModel.getIdentification(), JSON.toJSONString(applicationModel));
        masterRedisTemplate.delete("zoo_applicationModel_str_list");
        masterRedisTemplate.delete("zoo_appName_list");
        masterRedisTemplate.delete("zoo_package_name_list");
        // 保存tagIds
        boolean needUpdateEpmRedis = true;
        List<String> beforeTagIds = categoryTagRepo.querTagIds(applicationModel.getIdentification());
        if (!StringUtils.isEmpty(applicationModel.getIdentification())) {
            ApplicationModel beforeModel = appInfoRepo.findFirstByIdentification(applicationModel.getIdentification());
            if (beforeModel != null && beforeModel.getStatus() == applicationModel.getStatus() && tagIds.equals(beforeTagIds)) {
                needUpdateEpmRedis = false;
            }
        }
        deleteTopOffer(applicationModel.getIdentification());
        if (operatorTopOffer != null && operatorTopOffer.size() > 0) {
            for (Object object : operatorTopOffer) {
                JSONObject topOffer = JSON.parseObject(String.valueOf(object));
                String offerName = topOffer.getString("offerName");
                String operator = topOffer.getString("operator");
                String appId = topOffer.getString("appId");
                OfferModel offerModel = offerRepo.findFirstByOfferName(offerName);
                if (offerModel != null && !StringUtils.isEmpty(appId)) {
                    String topOfferKey = ZooConstant.APP_OPERATOR_TOP_OFFER + appId;
                    masterRedisTemplate.opsForHash().put(topOfferKey, operator, offerModel.getIdentification());
                }
            }
        }
        resetAppTag(saveModel.getIdentification(), tagIds, true);
        String capKey = ZooConstant.APP_OPERATOR_CAP_KEY;
        Set<Object> keys = cluster3RedisTemplate.opsForHash().keys(capKey);
        if (keys != null && keys.size() >0) {
            for (Object hashKey : keys) {
                if (hashKey.toString().indexOf(applicationModel.getIdentification()) > 0) {
                    masterRedisTemplate.opsForHash().delete(capKey, hashKey);
                }
            }
        }
        for (AppOpCapVO appOpCapVO : opCapList) {
            String hashKey = appOpCapVO.getOperator() + ZooConstant.COLON + applicationModel.getIdentification();
            masterRedisTemplate.opsForHash().put(capKey, hashKey, appOpCapVO.getCap());
        }

        if (needUpdateEpmRedis) {
            Set<String> filterKeys = cluster3RedisTemplate.keys(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + applicationModel.getIdentification() + CacheNameSpace.ASTERISK);
            if (filterKeys != null && filterKeys.size() > 0) {
                for (String filterKey : filterKeys) {
                    masterRedisTemplate.delete(filterKey);
                }
            }
            updateAppOfferRedis(applicationModel.getIdentification(), applicationModel.getStatus(), applicationModel);
        }
    }

    @CacheEvict(value = "app",key = "#appId")
    public void deleteAppRedis(String appId) {
        masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_APP, appId);
    }

    private void deleteTopOffer(String appId) {
        Boolean existTopOffer = cluster3RedisTemplate.hasKey(ZooConstant.APP_OPERATOR_TOP_OFFER + appId);
        if (existTopOffer != null && existTopOffer) {
            Set<Object> offerIds = cluster3RedisTemplate.opsForHash().keys(ZooConstant.APP_OPERATOR_TOP_OFFER + appId);
            if (offerIds != null && offerIds.size() > 0) {
                for (Object object : offerIds) {
                    Boolean existTopOfferKey = cluster3RedisTemplate.opsForHash().hasKey(ZooConstant.APP_OPERATOR_TOP_OFFER + appId, object);
                    if (existTopOfferKey != null && existTopOfferKey) {
                        masterRedisTemplate.opsForHash().delete(ZooConstant.APP_OPERATOR_TOP_OFFER + appId, object);
                    }
                }
            }
        }
    }

    private List<OfferModel> getOfferByTags(List<String> tagIds) {
        return offerRepo.queryInTagIds(tagIds);
    }

    @Override
    public JSONObject getAppById(String id) {
        ApplicationModel applicationModel = appInfoRepo.findById(id).get();
        if (applicationModel != null) {
            // 设置group
            List<String> group = new ArrayList<>();
            List<CategoryTagModel> categoryTagModels = categoryTagRepo.findAllByTypeAndCategoryId(ZooConstant.CATEGORY_APP, applicationModel.getIdentification());
            if (categoryTagModels != null && categoryTagModels.size() > 0) {
                for (CategoryTagModel categoryTagModel : categoryTagModels) {
                    group.add(categoryTagModel.getTagId());
                }
            }
            JSONObject jsonObject = (JSONObject) JSON.toJSON(applicationModel);
            jsonObject.put(ZooConstant.TAG_GROUP, group);
            jsonObject.put(ZooConstant.APP_OPERATOR_CAP_LIST, getAppOperatorCapList(applicationModel.getIdentification()));
            jsonObject.put(ZooConstant.APP_OPERATOR_TOP_OFFER_LIST, getAppTopOperatorList(applicationModel.getIdentification()));
            jsonObject.put(ZooConstant.SDK_APP_OPERATOR, getAppSdkUrl(id));
            jsonObject.put(ZooConstant.S3_URL, getS3Url(id));
            jsonObject.put(ZooConstant.EPM_APP_OPERATOR, getAppEpmAlaramOperator(id));
            jsonObject.put(ZooConstant.EMAILS, getMails(id));
            return jsonObject;
        }
        return null;
    }

    private String getS3Url(String appId) {
        ApplicationModel applicationModel = null;
        String s3Url = "";
        Boolean existApp = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_APP, appId);
        if (existApp != null && existApp) {
            applicationModel = JSON.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, appId)), ApplicationModel.class);
            if (!StringUtils.isEmpty(applicationModel.getS3Url())) {
                s3Url = applicationModel.getS3Url();
            }
        }
        return s3Url;
    }

    private String[] getMails(String id) {
        ApplicationModel applicationModel = null;
        String[] mails = {};
        Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, id);
        if (obj != null) {
            applicationModel = JSON.parseObject(String.valueOf(obj), ApplicationModel.class);
            List<String> emailList = applicationModel.getEmails();
            if (emailList != null && emailList.size() > 0) {
                mails = emailList.toArray(new String[emailList.size()]);
            }
        }
        return mails;
    }

    private JSONArray getAppEpmAlaramOperator(String id) {
        ApplicationModel applicationModel = null;
        JSONArray epmArray = new JSONArray();
        Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, id);
        if (obj != null) {
            applicationModel = JSON.parseObject(String.valueOf(obj), ApplicationModel.class);
            if (applicationModel.getEpmAlarmOperator() != null) {
                epmArray = applicationModel.getEpmAlarmOperator();
            }
        }
        return epmArray;
    }

    private JSONArray getAppSdkUrl(String id) {
        ApplicationModel applicationModel = null;
        JSONArray sdkArray = new JSONArray();
        Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, id);
        if (obj != null) {
            applicationModel = JSON.parseObject(String.valueOf(obj), ApplicationModel.class);
            if (applicationModel.getSdkUrl() != null) {
                sdkArray = applicationModel.getSdkUrl();
            }
        }
        return sdkArray;
    }

    @Override
    public List<ApplicationModel> getAllAppOptions() {
        List<ApplicationModel> all = new ArrayList<>();
        String key = "zoo_applicationModel_str_list";
        Long size = cluster3RedisTemplate.opsForList().size(key);
        if (size != null && size > NumberEnum.ZERO.getNum()) {
            List<String> list = cluster3RedisTemplate.opsForList().range(key, 0, -1);
            if (list != null && list.size() > NumberEnum.ZERO.getNum()) {
                for (String appStr : list) {
                    ApplicationModel applicationModel = JSON.parseObject(appStr, ApplicationModel.class);
                    all.add(applicationModel);
                }
            }
        } else {
            all = appInfoRepo.findAll();
            List<String> list = new ArrayList<>();
            for (ApplicationModel applicationModel : all) {
                String appStr = JSON.toJSONString(applicationModel);
                list.add(appStr);
            }
            masterRedisTemplate.opsForList().rightPushAll(key, list);
        }

        return all;
    }

    @Override
    public Long getAppOfferNum(String appId) {
        return appInfoRepo.findOfferNum(appId);
    }

    @Timed
    @Override
    public ApplicationModel getByIdentification(String id) {
        return appInfoRepo.findFirstByIdentification(id);
    }

    @Override
    public void updateLogStatus(String appId, int status) {
        appInfoRepo.updateLogStatus(status, appId);
        Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, appId);
        if (obj != null) {
            ApplicationModel applicationModel = JSON.parseObject(String.valueOf(obj), ApplicationModel.class);
            applicationModel.setLogStatus(status);
            masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_APP, appId);
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_APP, appId, JSON.toJSONString(applicationModel));
        }

    }

    @Override
    public void multiUpdateLogStatus(List<String> ids, int status) {
        if (ids != null && ids.size() > 0) {
            for (int i = 0; i < ids.size(); i++) {
                updateLogStatus(ids.get(i), status);
            }
        }
    }

    @Override
    public String checkAppName(String appName) {
        boolean existAppName = appInfoRepo.existsByAppName(appName);
        if (existAppName) {
            return ZooConstant.FAIL;
        } else {
            return ZooConstant.SUCCESS;
        }
    }

    @Override
    @Cacheable(value = "app",key = "#appId")
    public ApplicationModel getAppModel(String appId) {
        Object object = cluster1RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, appId);
        if(object != null){
            ApplicationModel applicationModel = JSON.parseObject(String.valueOf(object), ApplicationModel.class);
            return applicationModel;
        }else {
            return null;
        }
    }

    @Override
    public void openAppLog(List<String> operators, List<String> appNames) {
        for (String appName : appNames) {
            for (String operator : operators) {
                masterRedisTemplate.opsForHash().put(CacheNameSpace.APP_USER_EVENT_LOG_STATUS,appName + "_" + operator, "openLog");
            }
        }
    }

    @Override
    public PageVO getAppLogConfig(List<String> appNames, List<String> operators, Integer page, Integer limit) {
        PageVO<JSONObject> pageVO = new PageVO<>();
        Map<Object, Object> appEventLog = cluster3RedisTemplate.opsForHash().entries(CacheNameSpace.APP_USER_EVENT_LOG_STATUS);
        List<JSONObject> result = new ArrayList<>();
        if (appEventLog == null || appEventLog.entrySet().size() == 0) {
            pageVO.setTotal(0L);
            page = page >= 1 ? page - 1 : 0;
            pageVO.setList(null);
            pageVO.setLimit(limit);
            pageVO.setPage(page);
            return pageVO;
        }
        boolean appFlag = appNames == null || appNames.size() ==  0;
        boolean operatorFlag = operators == null || operators.size() == 0;
        for (Map.Entry<Object, Object> entry : appEventLog.entrySet()) {
            String key = String.valueOf(entry.getKey());
            if (appNames != null && appNames.size() > 0) {
                for (String appName : appNames) {
                    String appStr = key.split("_")[0];
                    if (appStr.equals(appName)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("appName", appStr);
                        jsonObject.put("operator", key.split("_")[1] + "_" + key.split("_")[2]);
                        result.add(jsonObject);
                    }
                }
            }
            if (operators != null && operators.size() > 0) {
                for (String operator : operators) {
                    String operatorStr = key.split("_")[1] + "_" + key.split("_")[2];
                    if (operatorStr.equals(operator)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("appName", key.split("_")[0]);
                        jsonObject.put("operator", operatorStr);
                        result.add(jsonObject);
                    }
                }
            }
            // 查询全部
            if (appFlag && operatorFlag) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appName", key.split("_")[0]);
                jsonObject.put("operator", key.split("_")[1] + "_" + key.split("_")[2]);
                result.add(jsonObject);
            }
        }
        result = result.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> e.getString("appName") + e.getString("operator")))), ArrayList::new));
        Long total = Long.parseLong(String.valueOf(result.size()));
        // 分页下标
        int begin = limit * (page - 1);
        int end = (begin + limit) > result.size() ? result.size() : (begin + limit);
        result = result.subList(begin, end).stream().sorted(Comparator.comparing(e -> e.getString("appName"))).collect(Collectors.toList());
        pageVO.setTotal(total);
        pageVO.setLimit(10);
        pageVO.setPage(1);
        pageVO.setList(result);
        return pageVO;
    }

    @Override
    public void multiDelete(List<String> ids) {
        if (ids != null && ids.size() > 0) {
            offerTaskRepo.deleteByAppIdIn(ids);
            categoryTagRepo.deleteByTypeAndCategoryIdIn(ZooConstant.CATEGORY_APP, ids);
            appInfoRepo.deleteByIdentificationIn(ids);
            masterRedisTemplate.delete("zoo_applicationModel_str_list");
            masterRedisTemplate.delete("zoo_appName_list");
            masterRedisTemplate.delete("zoo_package_name_list");
            for (String appId : ids) {
                String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON + ZooConstant.APP + CacheNameSpace.COLON + appId;
                masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_APP, appId);
                masterRedisTemplate.delete(key);
                updateAppOfferRedis(appId, 0, null);
            }
        }
    }

    @Override
    public void importRedis() {
        List<ApplicationModel> applicationModelList = appInfoRepo.findByStatus(1);
        if (applicationModelList != null && applicationModelList.size() > 0) {
            for (ApplicationModel applicationModel : applicationModelList) {
                masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_APP, applicationModel.getIdentification(), JSON.toJSONString(applicationModel));
            }
        }
    }

    @Override
    public List<JSONObject> getAppOperatorOffers(String appId, String operator) {
        List<JSONObject> offers = new ArrayList<>();
        List<String> offerIds = categoryTagRepo.queryOfferIds(appId);
        for (String offerId : offerIds) {
            Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId);
            if (obj != null) {
                OfferModel offerModel = JSONObject.parseObject(String.valueOf(obj), OfferModel.class);
                if (offerModel.getOperator().equalsIgnoreCase(operator)) {
                    JSONObject offerJson = new JSONObject();
                    offerJson.put("offerName", offerModel.getOfferName());
                    offerJson.put("offerId", offerId);
                    offers.add(offerJson);
                }
            }
        }
        return offers;
    }

    @SuppressFBWarnings({"DM_DEFAULT_ENCODING", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "DLS_DEAD_LOCAL_STORE"})
    @Override
    public byte[] getSdkByAppId(String appId, String mnc, String model) {
        Boolean existApp = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_APP, appId);
        String url = "";
        Boolean existMncRedis = cluster3RedisTemplate.opsForHash().hasKey(ZooConstant.ADMIN_MNC_CONFIG, mnc);
        String operator = "";
        String errorUUID = UUID.randomUUID().toString();
        if (existMncRedis != null && existMncRedis) {
            operator = String.valueOf(cluster3RedisTemplate.opsForHash().get(ZooConstant.ADMIN_MNC_CONFIG, mnc));
        } else {
            return errorUUID.getBytes();
        }
        Boolean existBlackName = cluster3RedisTemplate.hasKey(ZooConstant.BLACK_MACHINE);
        if (existBlackName != null && existBlackName) {
            Set<Object> blackList = cluster3RedisTemplate.opsForHash().keys(ZooConstant.BLACK_MACHINE);
            boolean existBlackModel = checkExistBlackName(blackList, model);
            if (existBlackModel) {
                return errorUUID.getBytes();
            }
        }
        if (existApp != null && existApp && !StringUtils.isEmpty(mnc)) {
            ApplicationModel applicationModel = JSON.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_APP, appId)), ApplicationModel.class);
            JSONArray sdkUrl = applicationModel.getSdkUrl();
            if (sdkUrl != null && sdkUrl.size() > 0) {
                for (Object sdk : sdkUrl) {
                    if (existMncRedis != null && existMncRedis) {
                        JSONObject sdkJson = (JSONObject) JSON.toJSON(sdk);
                        String opArray = sdkJson.getString(ZooConstant.OPERATOR_ARRAY);
                        log.info("SDK JSON:{}", JSON.toJSONString(sdkJson));
                        if (opArray.indexOf(operator) > -1) {
                            url = sdkJson.getString(ZooConstant.SDK_URL);
                            break;
                        }
                    }
                }
            }
        }
        log.info("pull sdk, appId:{},mnc:{},model:{},url:{}", appId, mnc, model, url);
        if (!StringUtils.isEmpty(url)) {
            String[] splitStr = url.split("/");
            String userName = splitStr[4];
            String key = url.split("/")[splitStr.length - 1];
            byte[] value = new byte[0];
            String fileName = key;
            ResourceLoader loader = new DefaultResourceLoader();
            String localPath = "static/sdk/" + userName + "/" + fileName;
            Resource resource = loader.getResource(localPath);
            try {
                value = getResourceByte(localPath, resource);
                if (value != null) {
                    return value;
                } else {
                    return errorUUID.getBytes();
                }
            } catch (Exception e) {
                log.info("PULL SDK ERROR:{}", JSON.toJSONString(e));
            }
        }
        return errorUUID.getBytes();

    }

    private byte[] getResourceByte(String localPath, Resource resource) {
        InputStream input = null;
        byte[] data = null;
        try {
            //获取文件流
            input = resource.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = input.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();
            input.close();
            outStream.close();
            return data;
        } catch (IOException e) {
            log.error(" download error path:{} ERROR:{}", localPath, e.getMessage());
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return data;
    }

    @Timed
    @Override
    public void checkAlarmEpm() {
        List<ApplicationModel> applicationModelList = new ArrayList<>();
        Map<Object, Object> appMap = cluster3RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_APP);
        if (appMap != null && appMap.size() > 0) {
            for (Map.Entry<Object, Object> entry : appMap.entrySet()) {
                JSONObject appJson = JSONObject.parseObject(entry.getValue().toString());
                ApplicationModel applicationModel = JSONObject.parseObject(appJson.toJSONString(), ApplicationModel.class);
                JSONArray epmArrayList = applicationModel.getEpmAlarmOperator();
                if (epmArrayList != null && epmArrayList.size() > 0) {
                    applicationModelList.add(applicationModel);
                }
            }
        }
        if (applicationModelList != null && applicationModelList.size() > 0) {
            for (ApplicationModel applicationModel : applicationModelList) {
                producer.sendToQueueEpmAlarm(new BaseSqsMessage(new Integer[]{ZooConstant.QUEUE_APP_EPM_ALARM},ZooConstant.QUEUE_APP_EPM_ALARM_MODEL,JSON.toJSONString(applicationModel)));
            }
        }

    }


    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    private byte[] reconnectS3(String bucketTotalName, String key) {
        log.info("reconnect zoo s3, path:{}", bucketTotalName + "/" + key);
        s3Util.deleteEache(bucketTotalName + "/" + key);
        byte[] value = UUID.randomUUID().toString().getBytes();
        try {
            value = s3Util.download(bucketTotalName, key, bucketTotalName + "/" + key);
        } catch (Exception reconnectError) {
            log.info("ZOO S3 pull SDK reconnectException", JSON.toJSONString(reconnectError));
        }
        return value;
    }

    private boolean checkExistBlackName(Set<Object> blackList, String redisName) {
        boolean existBlackName = false;
        if (blackList != null && blackList.size() > 0) {
            for (Object name : blackList) {
                if (redisName.indexOf(String.valueOf(name)) > -1) {
                    existBlackName = true;
                    break;
                }
            }
        }
        return existBlackName;
    }
}
