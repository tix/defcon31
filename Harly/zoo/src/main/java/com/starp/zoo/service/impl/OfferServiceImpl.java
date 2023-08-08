package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.vanroy.springdata.jest.JestElasticsearchTemplate;
import com.github.vanroy.springdata.jest.mapper.JestResultsExtractor;
import com.google.common.collect.Lists;
import com.starp.zoo.common.BadRequestException;
import com.starp.zoo.common.ResponseInfoEnum;
import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.config.aws.sqs.Producer;
import com.starp.zoo.constant.*;
import com.starp.zoo.entity.payment.ShortCodeModel;
import com.starp.zoo.entity.zoo.AffClickInfoModel;
import com.starp.zoo.entity.zoo.AffSmartLinkModel;
import com.starp.zoo.entity.zoo.AppUserEventModel;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.AutoScriptModel;
import com.starp.zoo.entity.zoo.CategoryTagModel;
import com.starp.zoo.entity.zoo.OfferAutoScriptModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.OfferTagModel;
import com.starp.zoo.entity.zoo.OfferTaskModel;
import com.starp.zoo.entity.zoo.OfferTempModel;
import com.starp.zoo.entity.zoo.ResultOfferModel;
import com.starp.zoo.entity.zoo.ScriptModel;
import com.starp.zoo.entity.zoo.SubscribeModel;
import com.starp.zoo.entity.zoo.TagModel;
import com.starp.zoo.repo.payment.ShortCodeRepo;
import com.starp.zoo.repo.zoo.AffClickInfoRepo;
import com.starp.zoo.repo.zoo.AffEpmInfoRepo;
import com.starp.zoo.repo.zoo.AffSmartLinkRepo;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.repo.zoo.AutoScriptRepo;
import com.starp.zoo.repo.zoo.CategoryTagRepo;
import com.starp.zoo.repo.zoo.OfferAutoScriptRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.OfferTagRepo;
import com.starp.zoo.repo.zoo.OfferTaskRepo;
import com.starp.zoo.repo.zoo.OfferTempRepo;
import com.starp.zoo.repo.zoo.ScriptRepo;
import com.starp.zoo.repo.zoo.SubscribeRepo;
import com.starp.zoo.repo.zoo.TagRepo;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.service.ITagService;
import com.starp.zoo.util.*;
import com.starp.zoo.vo.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/11/9.
 */
@SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
@Slf4j
@Service
public class OfferServiceImpl implements IOfferService {

    @Value("${zoo.domain}")
    private String zooDomain;

    @Autowired
    private ApplicationRepo appInfoRepo;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private OfferAutoScriptRepo offerAutoScriptRepo;

    @Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @Autowired
    private AutoScriptRepo autoScriptRepo;

    @Autowired
    private ScriptRepo scriptRepo;

    @Autowired
    private OfferTaskRepo offerTaskRepo;

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private OfferTagRepo offerTagRepo;

    @Autowired
    private AffClickInfoRepo affClickInfoRepo;

    @Autowired
    @Lazy
    private IApplicationService applicationService;

    @Autowired
    private AffSmartLinkRepo smartLinkRepo;

    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;

    @Autowired
    private StringRedisTemplate masterRedisTemplate;

    /**
     * tracking
     */
    @Resource(name = "trackRedisTemplate")
    private StringRedisTemplate trackRedisTemplate;


    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;

    @Autowired
    private CategoryTagRepo categoryTagRepo;

    @Autowired
    @Lazy
    private ITagService tagService;

    @Value("${elasticsearch.appevent.index}")
    private String elasticIndex;

    @Value("${elasticsearch.appevent.type}")
    private String elasticType;

    @Autowired
    JestElasticsearchTemplate jestElasticsearchTemplate;

    @Autowired
    private SubscribeRepo subscribeRepo;

    @Autowired
    private OfferTempRepo offerTempRepo;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    Producer producer;

    @Lazy
    @Autowired
    IOfferService offerService;

    @Autowired
    private ShortCodeRepo shortCodeRepo;

    @Autowired
    private AffEpmInfoRepo epmInfoRepo;

    private static final Logger epmListLog = LoggerFactory.getLogger(LogNameEnum.EPM_LIST.getLogName());

    @Override
    public void save(OfferModel offerModel) {
        List<String> jsList = offerModel.getJsList();
        if (!StringUtils.isEmpty(offerModel.getIdentification())) {
            OfferModel originModel = getById(offerModel.getIdentification());
            boolean isNeedJs = !StringUtils.isEmpty(originModel.getType()) && (ZooConstant.OFFER_WAP.equals(originModel.getType()) ||
                    ZooConstant.OFFER_PIN_WAP.equals(originModel.getType())) || ZooConstant.OFFER_PIN_MO.equals(originModel.getType());
            if (isNeedJs) {
                //删除 js 关联
                offerAutoScriptRepo.deleteByOfferId(offerModel.getIdentification());
            }
        }
        OfferModel saveModel = offerRepo.save(offerModel);
        boolean isNeedJs = !StringUtils.isEmpty(offerModel.getType()) && jsList != null && (ZooConstant.OFFER_WAP.equals(offerModel.getType()) ||
                ZooConstant.OFFER_PIN_WAP.equals(offerModel.getType())) || ZooConstant.OFFER_PIN_MO.equals(offerModel.getType());
        if (isNeedJs) {
            for (String jsId : jsList) {
                OfferAutoScriptModel jsModel = new OfferAutoScriptModel();
                jsModel.setOfferId(saveModel.getIdentification());
                jsModel.setAutoScriptId(jsId);
                offerAutoScriptRepo.save(jsModel);
            }
        }
    }

    @Override
    public List<OfferModel> getAll(String offerName, String country, String operator, String partner, String isNewOffer) {
        Specification specification = new Specification<OfferModel>() {
            @Override
            public Predicate toPredicate(Root<OfferModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(offerName)) {
                    predicates.add(criteriaBuilder.equal(root.get("offerName"), offerName));
                }
                if (!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if (!StringUtils.isEmpty(operator)) {
                    predicates.add(criteriaBuilder.equal(root.get("operator"), operator));
                }
                if (!StringUtils.isEmpty(partner)) {
                    predicates.add(criteriaBuilder.equal(root.get("partner"), partner));
                }
                if (!StringUtils.isEmpty(isNewOffer)) {
                    predicates.add(criteriaBuilder.equal(root.get("isNewOffer"), isNewOffer));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.asc(root.get("offerName")));
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        return offerRepo.findAll(specification);
    }

    @Override
    public OfferModel getById(String id) {
        StringBuilder sql = new StringBuilder("select t.auto_script_id from t_offer_auto_script t where t.offer_id=?1");
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        nativeQuery.setParameter(1, id);
        List<String> scriptIdList = nativeQuery.getResultList();

        OfferModel offerModel = getOfferModel(id);
        offerModel.setJsList(scriptIdList);
        return offerModel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteById(String id) {
        List<OfferTaskModel> offerTaskModels = offerTaskRepo.findAllByOfferId(id);
        // List<ApplicationModel> applicationModels = new ArrayList<>();
        for (OfferTaskModel offerTaskModel : offerTaskModels) {
            ApplicationModel applicationModel = applicationService.getAppModel(offerTaskModel.getAppId());
            if (applicationModel != null && applicationModel.getStatus() == 1) {
                return -1;
            }
        }
        offerAutoScriptRepo.deleteByOfferId(id);
        offerRepo.deleteById(id);
        return 0;
    }

    @Override
    public void copy(OfferModel offerModel) throws Exception {
        OfferModel originModel = getById(offerModel.getIdentification());
        offerModel.setIdentification(null);
        offerModel.setCreateTime(new Date());
        if (!StringUtils.isEmpty(originModel.getOfferName()) && originModel.getOfferName().equalsIgnoreCase(offerModel.getOfferName())) {
            offerModel.setOfferName(originModel.getOfferName() + "_copy_" + System.currentTimeMillis());
        }
        if (!StringUtils.isEmpty(originModel.getOfferId()) && originModel.getOfferId().equalsIgnoreCase(offerModel.getOfferId())) {
            offerModel.setOfferId(originModel.getOfferId() + "_copy_" + System.currentTimeMillis());
        }
        save(offerModel);
    }

    @Override
    public List<String> getOfferNameList(String isNewOffer) throws Exception {
        StringBuilder sql = new StringBuilder("select t.offer_name from t_offer t where t.new_offer = '" + isNewOffer + "' order by t.createTime desc");
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        return nativeQuery.getResultList();
    }

    @Override
    public List<String> getOfferPartnerList() {
        StringBuilder sql = new StringBuilder("select t.partner from t_offer t");
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        List<String> queryList = nativeQuery.getResultList();
        List<String> list = new ArrayList<>();
        for (String str : queryList) {
            if (list.contains(str)) {
                continue;
            }
            list.add(str);
        }
        return list;
    }

    @Override
    public List<Map<String, String>> getOfferScriptList() {
        List<AutoScriptModel> autoScriptModels = autoScriptRepo.findQuery();
        List<Map<String, String>> list = new ArrayList<>();
        for (AutoScriptModel autoScriptModel : autoScriptModels) {
            Map<String, String> map = new HashMap<>(2);
            map.put("identification", autoScriptModel.getIdentification());
            map.put("name", autoScriptModel.getName());
            list.add(map);
        }
        return list;
    }

    @Override
    public List<OfferModel> getAll(List<String> offerIds) {
        List<OfferModel> offerModels = offerRepo.findQuery(offerIds);
        List<OfferAutoScriptModel> offerAutoScriptModels = offerAutoScriptRepo.findQuery(offerIds);
        List<AutoScriptModel> autoScriptModels = autoScriptRepo.findAll();
        for (OfferModel offerModel : offerModels) {
            List<AutoScriptModel> autoScripts = new ArrayList<>();
            for (OfferAutoScriptModel offerAutoScriptModel : offerAutoScriptModels) {
                if (offerAutoScriptModel.getOfferId().equals(offerModel.getIdentification())) {
                    for (AutoScriptModel autoScriptModel : autoScriptModels) {
                        if (autoScriptModel.getIdentification().equals(offerAutoScriptModel.getAutoScriptId())) {
                            autoScripts.add(autoScriptModel);
                        }
                    }
                }
            }
            offerModel.setAutoScripts(autoScripts);
        }
        return offerModels;
    }

    @Override
    public List<OfferModel> getAllByCreateTimeDesc(List<String> offerIds) {
        List<OfferModel> offerModels = null;
        if (offerIds != null && offerIds.size() > 0) {
            offerModels = offerRepo.findQuery(offerIds);
        }
        return offerModels;
    }

    @Override
    public ApplicationModel checkUse(String offerId, String country, String operator) {
        ApplicationModel applicationModel = null;
        OfferModel originModel = getById(offerId);
        if (!originModel.getCountry().equals(country) || !originModel.getOperator().equals(operator)) {
            if (offerTaskRepo.existsByOfferId(offerId)) {
                OfferTaskModel offerTaskModel = offerTaskRepo.findFirstByOfferId(offerId);
                if (offerTaskModel != null) {
                    applicationModel = applicationService.getAppModel(offerTaskModel.getAppId());
                }
            }
        }
        return applicationModel;
    }

    @Override
    public void deleteAppRelate(String offerId) {
        offerTaskRepo.deleteByOfferId(offerId);
    }

    @Override
    public boolean checkExists(String name, int type, String id) {
        int count = offerRepo.countByOfferName(name);
        if (type != ZooConstant.TYPE_UPDATE && count > 0) {
            return true;
        }
        if (type == ZooConstant.TYPE_UPDATE && count == 1) {
            OfferModel offerModel = offerRepo.findFirstByOfferName(name);
            if (offerModel != null && !offerModel.getIdentification().equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkIdExists(String offerId, int type, String id) {
        int count = offerRepo.countByOfferId(offerId);
        if (type != ZooConstant.TYPE_UPDATE && count > 0) {
            return true;
        }
        if (type == ZooConstant.TYPE_UPDATE && count == 1) {
            OfferModel offerModel = offerRepo.findFirstByOfferId(offerId);
            if (offerModel != null && !offerModel.getIdentification().equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public OfferModel getByOfferId(String offerId) {
        return offerRepo.findFirstByOfferId(offerId);
    }

    @Override
    public PageVO<JSONObject> getPageList(int page, int limit, List<String> ids, List<String> offerNames, List<String> emails, String country, String operator, String partner, String tagId, String offerId, String partnerOfferId, Integer status, String belong) {
        Specification<OfferModel> specification = new Specification<OfferModel>() {
            @Override
            public Predicate toPredicate(Root<OfferModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(tagId)) {
                    Join<OfferModel, OfferTagModel> join = root.join("offerTags", JoinType.LEFT);
                    join.on(
                            criteriaBuilder.equal(join.get("tagId"), tagId)
                    );
                    predicates.add(criteriaBuilder.isNotNull(join.get("identification")));
                }
                if (ids != null && ids.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("identification"));
                    for (String id : ids) {
                        in.value(id);
                    }
                    predicates.add(in);
                }
                if (offerNames != null && offerNames.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("offerName"));
                    for (String name : offerNames) {
                        in.value(name);
                    }
                    predicates.add(in);
                }
                if (emails != null && emails.size() > 0) {
                    List<Predicate> emailPre = new ArrayList<>();
                    for (int i = 0; i < emails.size(); i++) {
                        emailPre.add(criteriaBuilder.like(root.get("email"), "%" + emails.get(i) + "%"));
                    }
                    Predicate predicateOr = criteriaBuilder.or(emailPre.toArray(new Predicate[emailPre.size()]));
                    predicates.add(predicateOr);
                }
                if (!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if (!StringUtils.isEmpty(operator)) {
                    predicates.add(criteriaBuilder.equal(root.get("operator"), operator));
                }
                if (!StringUtils.isEmpty(partner)) {
                    predicates.add(criteriaBuilder.equal(root.get("partner"), partner));
                }
                if (!StringUtils.isEmpty(offerId)) {
                    predicates.add(criteriaBuilder.equal(root.get("offerId"), offerId));
                }
                if (!StringUtils.isEmpty(partnerOfferId)) {
                    predicates.add(criteriaBuilder.equal(root.get("partnerOfferId"), partnerOfferId));
                }
                if (!StringUtils.isEmpty(belong)) {
                    predicates.add(criteriaBuilder.equal(root.get("belong"), belong));
                }
                if (status != null) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                orders.add(criteriaBuilder.asc(root.get("offerName")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        PageVO<JSONObject> pageVO = getPageVo(specification, page, limit);
        return pageVO;
    }

    public PageVO<JSONObject> getPageVo(Specification<OfferModel> specification, int page, int limit) {
        PageVO<JSONObject> pageVO = new PageVO();
        Long total = offerRepo.count(specification);
        pageVO.setTotal(total);
        page = page >= 1 ? page - 1 : 0;
        List<OfferModel> models = offerRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        List<String> assignedOfferIds = getAllAssignedOfferId();
        List<JSONObject> json = new ArrayList<>();
        if (models != null && models.size() > 0) {
            for (OfferModel model : models) {
                JSONObject jsonObject = (JSONObject) JSON.toJSON(model);
                jsonObject.put("offerTags", model.getOfferTags());
                jsonObject.put(ZooConstant.IS_ASSIGNED, assignedOfferIds.contains(model.getIdentification()));
                json.add(jsonObject);
            }
        }
        pageVO.setList(json);
        pageVO.setLimit(limit);
        pageVO.setPage(page);
        return pageVO;
    }

    private List<String> getAllAssignedOfferId() {
        List<String> list = new ArrayList<>();
        List<OfferAutoScriptModel> offerAutoScriptModels = offerAutoScriptRepo.findAll();
        if (offerAutoScriptModels != null && offerAutoScriptModels.size() > 0) {
            for (OfferAutoScriptModel offerAutoScriptModel : offerAutoScriptModels) {
                list.add(offerAutoScriptModel.getOfferId());
            }
        }
        return list;
    }

    @Override
    public List<OptionVO> getPartnerOptions() {
        StringBuilder sql = new StringBuilder("select distinct t.partner from t_offer t order by t.partner asc");
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        List<String> queryList = nativeQuery.getResultList();
        List<OptionVO> list = new ArrayList<>();
        for (String str : queryList) {
            OptionVO optionVO = new OptionVO();
            optionVO.setIdentification(str);
            optionVO.setLabel(str);
            optionVO.setValue(str);
            if (list != null && !list.contains(optionVO)) {
                list.add(optionVO);
            }
        }
        return list;
    }

    /**
     * 不从 caffeine 中查询 offer
     * @param offerId
     * @return com.starp.app.entity.OfferModel
     * @author Curry
     * @date 2023/5/9
     */
    public OfferModel getOfferModelByRedis(String offerId) {
        OfferModel offerModel = null;
        if (!StringUtils.isEmpty(offerId)) {
            Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId);
            if (obj != null) {
                offerModel = JSON.parseObject(String.valueOf(obj), OfferModel.class);
                if (StringUtils.isEmpty(offerModel.getIdentification())) {
                    offerModel.setIdentification(offerId);
                    log.info("REDIS OFFER ID IS NULL,OFFER INFO :{}", JSON.toJSONString(offerModel));
                }
            } else {
                offerModel = offerRepo.findByIdentification(offerId);
            }
        }
        return offerModel;
    }

    @Override
    public JSONObject getConfigModel(String id) {
        OfferModel offerModel = getOfferModelByRedis(id);
        if (offerModel != null) {
            String sql = "select t0.identification, t0.name from t_script t0  left join t_offer_auto_script t on t.auto_script_id=t0.identification where t.offer_id=?1 order by t.sort_index asc";
            Query nativeQuery = zooEntityManager.createNativeQuery(sql);
            nativeQuery.unwrap(NativeQuery.class);
            nativeQuery.setParameter(1, id);
            List<Object[]> scriptIdList = nativeQuery.getResultList();
            List<ScriptModel> scripts = new ArrayList<>();
            List<String> jsList = new ArrayList<>();
            if (scriptIdList != null && scriptIdList.size() > 0) {
                for (Object[] object : scriptIdList) {
                    ScriptModel scriptModel = new ScriptModel();
                    scriptModel.setIdentification((String) object[0]);
                    scriptModel.setName((String) object[1]);
                    scripts.add(scriptModel);
                    jsList.add((String) object[0]);
                }
            }
            offerModel.setScripts(scripts);
            offerModel.setJsList(jsList);
            TagsOptionVO tagsOptionVO = new TagsOptionVO();
            List<OptionVO> stackTags = new ArrayList<>();
            List<OptionVO> groupTags = new ArrayList<>();
            List<OptionVO> othersTags = new ArrayList<>();
            List<TagModel> tagModels = tagRepo.findQuery(id);
            if (tagModels != null && tagModels.size() > 0) {
                for (TagModel tag : tagModels) {
                    OptionVO optionVO = new OptionVO();
                    optionVO.setIdentification(tag.getIdentification());
                    optionVO.setLabel(tag.getTagName());
                    optionVO.setValue(tag.getIdentification());
                    if (tag.getTagType() == ZooConstant.TAG_TYPE_STACK) {
                        stackTags.add(optionVO);
                    } else if (tag.getTagType() == ZooConstant.TAG_TYPE_GROUP) {
                        groupTags.add(optionVO);
                    } else {
                        othersTags.add(optionVO);
                    }
                }
                tagsOptionVO.setStack(stackTags);
                tagsOptionVO.setGroup(groupTags);
                tagsOptionVO.setOthers(othersTags);
            }
            JSONObject jsonObject = (JSONObject) JSON.toJSON(offerModel);
            double initEpm = offerModel.getInitEpm() == null ? 1D : offerModel.getInitEpm();
            jsonObject.put(ZooConstant.INIT_EPM_NUM, new BigDecimal(initEpm * 100).intValue());
            jsonObject.put(ZooConstant.TAGS, tagsOptionVO);
            if (!StringUtils.isEmpty(offerModel.getTimeRange())) {
                String[] timeRange = offerModel.getTimeRange().split(",");
                jsonObject.put(ZooConstant.TIME_RANGE, timeRange);
            }

            return jsonObject;
        }
        return null;
    }

    @SuppressFBWarnings({"RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", "DM_BOXED_PRIMITIVE_FOR_PARSING", "REC_CATCH_EXCEPTION"})
    @Override
    public String saveAllConfig(OfferModel offerModel, TagsOptionVO tagsOptionVO) {
        String result = ZooConstant.SUCCESS;
        try {
            if (offerModel != null) {
                if (offerModel.getStatus() == 0) {
                    offerModel.setCloseTime(new Date());
                    updateCloseOffer(offerModel);
                }
                // 修复重复保存 offerTags bug
                offerModel.setOfferTags(null);
                List<String> testOfferIds = cluster1RedisTemplate.opsForList().range(CacheNameSpace.ZOO_AUTO_TEST_OFFER_ID, 0, -1);
                if (testOfferIds != null && testOfferIds.size() > 0) {
                    if (testOfferIds.contains(offerModel.getIdentification())) {
                        // 直接修改测试offer测试状态置为已完成,从测试主键集合中删除(否则日志一直打印，且特定情况会出bug)
                        offerModel.setTestStatus(NumberEnum.TWO.getNum());
                        masterRedisTemplate.opsForList().remove(CacheNameSpace.ZOO_AUTO_TEST_OFFER_ID, 0, offerModel.getIdentification());
                    }
                }
                OfferModel saveModel = offerRepo.save(offerModel);
                String offerId = saveModel.getIdentification();
                offerAutoScriptRepo.deleteByOfferId(offerId);
                if (offerModel.getJsList() != null && offerModel.getJsList().size() > 0) {
                    for (int i = 0; i < offerModel.getJsList().size(); i++) {
                        offerAutoScriptRepo.save(new OfferAutoScriptModel(offerId, offerModel.getJsList().get(i), i));
                    }
                }
                deleteOldAssign(offerId, offerModel.getOperator());
                List<String> beforeUpdateAppIds = offerTagRepo.findAppIdsByOfferId(saveModel.getIdentification());
                // 修复JPA 删除@OneToMany 关系时无法删除bug
                saveModel.setOfferTags(null);
                offerTagRepo.deleteByOfferId(offerId);
                String stackId = null;
                if (tagsOptionVO != null) {
                    if (tagsOptionVO.getStack() != null && tagsOptionVO.getStack().size() > 0) {
                        for (OptionVO optionVO : tagsOptionVO.getStack()) {
                            stackId = saveOfferTag(offerId, optionVO, offerModel.getOperator(), ZooConstant.TAG_TYPE_STACK);
                        }
                    }
                    if (tagsOptionVO.getGroup() != null && tagsOptionVO.getGroup().size() > 0) {
                        for (OptionVO optionVO : tagsOptionVO.getGroup()) {
                            saveOfferTag(offerId, optionVO, offerModel.getOperator(), ZooConstant.TAG_TYPE_GROUP);
                        }
                    }
                    String protectedId = CacheNameSpace.ZOO_PROTECTED_TAG + CacheNameSpace.COLON + offerModel.getOperator();
                    removeProtectedTagList(protectedId, offerId);
                    if (tagsOptionVO.getOthers() != null && tagsOptionVO.getOthers().size() > 0) {
                        for (OptionVO optionVO : tagsOptionVO.getOthers()) {
                            if (optionVO.getLabel().equalsIgnoreCase(CacheNameSpace.PROTECTED)) {
                                String protectKey = CacheNameSpace.ZOO_PROTECTED_TAG + CacheNameSpace.COLON + offerModel.getOperator();
                                masterRedisTemplate.opsForList().leftPush(protectKey, offerId);
                            }
                            saveOfferTag(offerId, optionVO, offerModel.getOperator(), ZooConstant.TAG_TYPE_OTHERS);
                        }
                    }
                }
                JSONObject json = (JSONObject) JSON.toJSON(saveModel);
                json.put("offerTags", offerModel.getOfferTags());
                json.put("stack", stackId);
                updateEpmCurrentRedis(saveModel);
                masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, saveModel.getIdentification(), json.toJSONString());
                masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER_ASSIGN, saveModel.getIdentification(), json.toJSONString());
                List<String> afterUpdateAppIds = categoryTagRepo.queryAppIds(saveModel.getIdentification());
                afterUpdateAppIds.addAll(beforeUpdateAppIds);
                afterUpdateAppIds = removeDuplicate(afterUpdateAppIds);
                update(afterUpdateAppIds, offerId, offerModel);
            }
        } catch (Exception e) {
            log.error("SAVE OFFER ERROR:{}", JSON.toJSONString(e));
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            result = ZooConstant.FAIL;
        }
        return result;
    }

    @CacheEvict(value = "protectTag", key = "#protectTagId")
    public void removeProtectedTagList(String protectTagId, String offerId) {
        masterRedisTemplate.opsForList().remove(protectTagId, 0, offerId);
    }

    /**
     * 更新对应的redis key
     *
     * @param saveModel
     */
    private void updateEpmCurrentRedis(OfferModel saveModel) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        String date = sdf.format(System.currentTimeMillis());
        String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + saveModel.getIdentification() + CacheNameSpace.ASTERISK + date;
        Set<String> keys = cluster3RedisTemplate.keys(pattern);
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                Map<String, String> params = new HashMap<>(1);
                params.put(ZooConstant.CAP, String.valueOf(saveModel.getCap()));
                params.put(ZooConstant.OFFER_NAME, saveModel.getOfferName());
                params.put(ZooConstant.OFFER_STATUS, String.valueOf(saveModel.getStatus()));
                params.put(ZooConstant.PARTNER_OFFER_ID, saveModel.getPartnerOfferId());
                masterRedisTemplate.opsForHash().putAll(key, params);
            }
        }
    }

    public void update(List<String> afterUpdateAppIds, String offerId, OfferModel offerModel) {
        if (afterUpdateAppIds != null && afterUpdateAppIds.size() > 0) {
            Integer redisOfferCap = 0;
            Integer redisAppCap = 0;
            Integer pullCount = 0;
            Boolean existOfferCap = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.POST_BACK_TRANS);
            Boolean existAppCap = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.APP_TRANS);
            Boolean existPullCount = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.PULL_COUNT);
            if (existOfferCap != null && existOfferCap) {
                redisOfferCap = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.POST_BACK_TRANS)));
            }
            if (existAppCap != null && existAppCap) {
                redisAppCap = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.APP_TRANS)));
            }
            if (existPullCount != null && existPullCount) {
                pullCount = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()), CacheNameSpace.PULL_COUNT)));
            }
            for (String appId : afterUpdateAppIds) {
                updateOfferRedis(offerModel, offerId, appId, redisOfferCap, redisAppCap, pullCount, "saveOffer");
            }
        }
    }


    public void updateCloseOffer(OfferModel offerModel) {
        OfferModel dbOffer = offerRepo.findByIdentification(offerModel.getIdentification());
        if (dbOffer != null && dbOffer.getStatus() == NumberEnum.ONE.getNum()) {
            List<String> appIds = categoryTagRepo.queryAppIds(offerModel.getIdentification());
            if (appIds != null && appIds.size() > 0) {
                for (String appId : appIds) {
                    updateClostOfferRedis(appId, offerModel.getIdentification(), offerModel.getOperator());
                }
            }
        }
    }

    public List removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    private void updateClostOfferRedis(String appId, String offerId, String operator) {
        log.info("ZOO DELETE OFFER INFO APPID:{},OFFERID:{},OPERATOR:{}", appId, offerId, operator);
        //更新redis, 从epmlist 跟 zoo_offer_assign:filter删除
        String epmListKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + CacheNameSpace.LIST;
        Boolean existEpmList = cluster3RedisTemplate.hasKey(epmListKey);
        log.info("ZOO DELETE EPM LIST, EPM LIST KEY:{}, EXIST EPM LIST:{}", epmListKey, existEpmList);
        if (existEpmList != null && existEpmList) {
            masterRedisTemplate.opsForList().remove(epmListKey, 0, offerId);
        }
        String assignKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator;
        Boolean existOfferAssign = cluster3RedisTemplate.opsForHash().hasKey(assignKey, offerId);
        log.info("ZOO DELETE EPM APP-OFFER-FILTER, FILTER KEY:{}, EXIST EPM FILTER:{}", assignKey, existOfferAssign);
        if (existOfferAssign != null && existOfferAssign) {
            masterRedisTemplate.opsForHash().delete(assignKey, offerId);
        }
        masterRedisTemplate.opsForHash().increment(CacheNameSpace.ZOO_UNUSED_OFFER, offerId, 1);
        masterRedisTemplate.expire(CacheNameSpace.ZOO_UNUSED_OFFER, 1, TimeUnit.DAYS);
    }

    private void addNewOfferToEpmList(String appId, String offerId, String operator) {
        String appEmpKeyStart = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator;
        //先获取 epm List 长度
        String appEpmListKey = appEmpKeyStart + CacheNameSpace.COLON + CacheNameSpace.LIST;
        Boolean existEpmList = cluster3RedisTemplate.hasKey(appEpmListKey);
        if (existEpmList != null && existEpmList) {
            List<String> appEpmOfferList = cluster3RedisTemplate.opsForList().range(appEpmListKey, 0, -1);
            int maxNum = 0;
            if (appEpmOfferList != null && appEpmOfferList.size() > 0) {
                maxNum = appEpmOfferList.lastIndexOf(appEpmOfferList.get(0)) + 1;
            }
            int firstOfferCounter = 0;
            if (appEpmOfferList != null && appEpmOfferList.size() > 0) {
                String preOfferId = "";
                for (int i = 0; i < appEpmOfferList.size(); i++) {
                    String currOfferId = appEpmOfferList.get(i);
                    if (i == maxNum && !StringUtils.isEmpty(currOfferId) && !currOfferId.equals(preOfferId)) {
                        break;
                    }
                    preOfferId = currOfferId;
                    firstOfferCounter++;
                }
            }
            if (maxNum != firstOfferCounter) {
                firstOfferCounter = maxNum;
            }
            List<String> offerIdList = new ArrayList<>();
            for (int i = 0; i < firstOfferCounter; i++) {
                offerIdList.add(offerId);
            }
            masterRedisTemplate.opsForList().leftPushAll(appEpmListKey, offerIdList);

        }
    }

    private void deleteOldAssign(String offerId, String operator) {

        List<CategoryTagModel> ctModels = categoryTagRepo.queryOfferAssign(offerId);
        if (ctModels != null && ctModels.size() > 0) {
            for (CategoryTagModel ctModel : ctModels) {
                String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON +
                        (ctModel.getType() == ZooConstant.CATEGORY_APP ? ZooConstant.APP : ZooConstant.AFFILIATE)
                        + CacheNameSpace.COLON + ctModel.getCategoryId();
                masterRedisTemplate.opsForHash().delete(key, offerId);
            }
        }
    }

    private String saveOfferTag(String offerId, OptionVO optionVO, String operator, int type) {
        String tagId = optionVO.getIdentification();
        if (StringUtils.isEmpty(optionVO.getIdentification()) || !tagRepo.existsById(optionVO.getIdentification())) {
            TagModel tag = tagRepo.save(new TagModel(optionVO.getLabel(), type));
            tagId = tag.getIdentification();
        }
        if (!StringUtils.isEmpty(tagId)) {
            offerTagRepo.save(new OfferTagModel(offerId, tagId));

            // 判断该 tag 是否被分配到某个 category
            List<CategoryTagModel> categoryTagModels = categoryTagRepo.findByTagId(tagId);
            if (categoryTagModels != null && categoryTagModels.size() > 0) {
                for (CategoryTagModel categoryTagModel : categoryTagModels) {
                    // 将 app 与 offer 的关联信息保存到 redis 中 zoo_offer_assign:app:
                    String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON +
                            (categoryTagModel.getType() == ZooConstant.CATEGORY_APP ? ZooConstant.APP : ZooConstant.AFFILIATE)
                            + CacheNameSpace.COLON + categoryTagModel.getCategoryId();
                    masterRedisTemplate.opsForHash().put(key, offerId, operator);
                }
            }
        }
        return tagId;
    }

    @Override
    public void deleteTestOfferRedis(String id) {
        masterRedisTemplate.opsForList().remove(CacheNameSpace.ZOO_AUTO_TEST_OFFER_ID, 0, id);
    }

    @Override
    public void delete(String id) {
        // 保存在临时表中
        OfferModel offerModel = getOfferModel(id);
        List<String> afterUpdateAppIds = offerTagRepo.findAppIdsByOfferId(id);
        for (String appId : afterUpdateAppIds) {
            log.info("UPDATE EPM LIST AND FILTER , DELETE EPMLIST AND FILTER BY DELETE OFFER ,APPID:{}, OFFERID:{}", appId, offerModel.getIdentification());
            //更新redis, 从epmlist 跟 zoo_offer_assign:filter删除
            String epmListKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator() + CacheNameSpace.COLON + CacheNameSpace.LIST;
            Boolean existEpmList = cluster3RedisTemplate.hasKey(epmListKey);
            if (existEpmList != null && existEpmList) {
                masterRedisTemplate.opsForList().remove(epmListKey, 0, id);
            }
            String assignKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator();
            Boolean existOfferAssign = cluster3RedisTemplate.opsForHash().hasKey(assignKey, id);
            if (existOfferAssign != null && existOfferAssign) {
                masterRedisTemplate.opsForHash().delete(assignKey, id);
            }
        }
        String result = JSON.toJSONString(offerModel);
        OfferTempModel offerTempModel = JSON.parseObject(result, OfferTempModel.class);
        offerTempRepo.save(offerTempModel);
        deleteOldAssign(id, offerModel.getOperator());
        // 删除tag
        offerTagRepo.deleteByOfferId(id);
        // 删除js
        offerAutoScriptRepo.deleteByOfferId(id);
        // 删除model
        offerRepo.deleteById(id);

        // 从 redis 删除对象保存
        deleteOfferRedis(id);

        // 删除测试offerRedis
        Boolean existOfferIdRedis = cluster3RedisTemplate.hasKey(CacheNameSpace.ZOO_AUTO_TEST_OFFER_ID);
        if (existOfferIdRedis != null && existOfferIdRedis) {
            masterRedisTemplate.opsForList().remove(CacheNameSpace.ZOO_AUTO_TEST_OFFER_ID, 0, id);
        }

    }

    @CacheEvict(value = "offer", key = "#offerId")
    public void deleteOfferRedis(String offerId) {
        masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_OFFER, offerId);
    }

    @Override
    public int getDefaultDuration(String country, String operator) {
        OfferModel offerModel = offerRepo.findFirstByCountryAndOperatorAndDurationNotNull(country, operator);
        if (offerModel != null) {
            return offerModel.getDuration();
        }
        return 0;
    }

    @Override
    public boolean checkUniqueId(String offerId) {
        int count = offerRepo.countByOfferId(offerId);
        if (count > 0) {
            return false;
        }
        return true;
    }

    @Override
    public String checkUniqueUrl(String url) {
        OfferModel offerModel = offerRepo.findFirstByUrl(url);
        if (offerModel != null) {
            return offerModel.getOfferName();
        } else {
            return "";
        }
    }

    @Override
    public boolean checkUniqueName(String name) {
        int count = offerRepo.countByOfferName(name);
        if (count > 0) {
            return false;
        }
        return true;
    }

    @Override
    public Long checkUniquePartnerOfferId(String country, String operator, String partner, String partnerOfferId) {
        Specification<OfferModel> specification = new Specification<OfferModel>() {
            @Override
            public Predicate toPredicate(Root<OfferModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if (!StringUtils.isEmpty(operator)) {
                    predicates.add(criteriaBuilder.equal(root.get("operator"), operator));
                }
                if (!StringUtils.isEmpty(partner)) {
                    predicates.add(criteriaBuilder.equal(root.get("partner"), partner));
                }
                if (!StringUtils.isEmpty(partnerOfferId)) {
                    predicates.add(criteriaBuilder.equal(root.get("partnerOfferId"), partnerOfferId));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        return offerRepo.count(specification);
    }

    @Override
    public List<OptionVO> getOfferNameOptions(String query) {
        List<String> nameList = offerRepo.queryNames(ZooConstant.PERCENT_SIGN + query + ZooConstant.PERCENT_SIGN);
        ;
        List<OptionVO> optionVOS = new ArrayList<>();
        if (nameList != null && nameList.size() > 0) {
            for (String name : nameList) {
                optionVOS.add(new OptionVO(name, name, name));
            }
        }
        return optionVOS;
    }


    @Override
    public List<OptionVO> getZooOfferNameOptions(String query) {
        List<OfferModel> modelList = offerRepo.findOfferNameLike(query);
        List<OptionVO> optionVOS = new ArrayList<>();
        if (modelList != null && modelList.size() > 0) {
            for (OfferModel offer : modelList) {
                optionVOS.add(new OptionVO(offer.getIdentification(), offer.getOfferName(), offer.getIdentification()));
            }
        }
        return optionVOS;
    }

    @Override
    public List<JSONObject> getAll(String query) {
        CriteriaBuilder criteriaBuilder = zooEntityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
        //具体实体的Root
        Root<OfferModel> root = criteriaQuery.from(OfferModel.class);
        criteriaQuery.multiselect(
                root.get("identification"),
                root.get("offerName")
        );
        List<Predicate> predicates = new ArrayList<>();
        if (!StringUtils.isEmpty(query)) {
            predicates.add(criteriaBuilder.like(root.get("offerName"), ZooConstant.PERCENT_SIGN + query + ZooConstant.PERCENT_SIGN));
        }
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
        List<Order> orders = new ArrayList<>();
        orders.add(criteriaBuilder.desc(root.get("createTime")));
        orders.add(criteriaBuilder.asc(root.get("offerName")));
        criteriaQuery.orderBy(orders);
        List<Tuple> tuples = zooEntityManager.createQuery(criteriaQuery).getResultList();
        List<JSONObject> list = new ArrayList<>();
        if (tuples != null && tuples.size() > 0) {
            for (Tuple tuple : tuples) {
                JSONObject json = new JSONObject();
                json.put("identification", tuple.get(0));
                json.put("offerName", tuple.get(1));
                list.add(json);
            }
        }
        return list;
    }

    @Override
    public List<OptionVO> getAllOptions(String query) {
        List<ResultOfferModel> results = offerRepo.resultOfferModel();
        List<OptionVO> list = new ArrayList<>();
        if (results != null && results.size() > 0) {
            for (ResultOfferModel resultOfferModel : results) {
                OptionVO json = new OptionVO();
                String identification = resultOfferModel.getIdentification();
                String offerName = resultOfferModel.getOfferName();
                json.setIdentification(identification);
                json.setLabel(offerName);
                json.setValue(identification);
                list.add(json);
            }
        }
        return list;
    }

    @Override
    public List<OfferModel> findOffersByCategoryId(int type, String categoryId) {
        Map<String, OfferTagModel> stackModels = getStackModels();
        Map<String, List<OfferTagModel>> otherTagsModels = getOtherTagModels();
        List<OfferModel> offerModels = offerRepo.findQuery(type, categoryId);
        if (offerModels != null && offerModels.size() > 0 && stackModels != null && stackModels.size() > 0) {
            for (OfferModel offerModel : offerModels) {
                OfferTagModel offerTagModel = stackModels.get(offerModel.getIdentification());
                if (offerTagModel != null) {
                    offerModel.setStack(offerTagModel.getTagId());
                }
                List<OfferTagModel> list = otherTagsModels.get(offerModel.getIdentification());
                offerModel.setOtherTags(list);
            }
        }
        return offerModels;
    }

    private List<OfferModel> findOfferByCategoryIdWithJoinColumn(int type, String categoryId) {
        List<OfferModel> offerModels = offerRepo.findQuery(type, categoryId);
        if (offerModels != null && offerModels.size() > 0) {
            for (OfferModel offerModel : offerModels) {
                Set<OfferTagModel> offerTagModels = offerModel.getOfferTags();
                if (offerTagModels != null && offerTagModels.size() > 0) {
                    for (OfferTagModel offerTagModel : offerTagModels) {
                        TagModel tagModel = offerTagModel.getTagModel();
                        if (tagModel != null && ZooConstant.TAG_TYPE_STACK == tagModel.getTagType()) {
                            offerModel.setStack(offerTagModel.getIdentification());
                            continue;
                        }
                    }
                }
            }
        }
        return offerModels;
    }

    private Map<String, OfferTagModel> getStackModels() {
        Map<String, OfferTagModel> map = new HashMap<>(1);
        List<OfferTagModel> offerTagModels = offerTagRepo.findQuery();
        if (offerTagModels != null && offerTagModels.size() > 0) {
            for (OfferTagModel offerTagModel : offerTagModels) {
                map.put(offerTagModel.getOfferId(), offerTagModel);
            }
        }
        return map;
    }

    private Map<String, List<OfferTagModel>> getOtherTagModels() {
        Map<String, List<OfferTagModel>> map = new HashMap<>(1);
        List<OfferTagModel> offerTagModels = offerTagRepo.findOtherTagQuery();
        if (offerTagModels != null && offerTagModels.size() > 0) {
            for (OfferTagModel offerTagModel : offerTagModels) {
                List<OfferTagModel> list = map.get(offerTagModel.getOfferId());
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(offerTagModel);
                map.put(offerTagModel.getOfferId(), list);
            }
        }
        return map;
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(List<String> ids, String status) {
        offerRepo.updateStatus(status, ids);
        if (status.equalsIgnoreCase(String.valueOf(NumberEnum.ZERO.getNum()))) {
            offerRepo.updateOffersCloseTime(new Date(), ids);
        }
        if (ids != null && ids.size() > 0) {
            for (String id : ids) {
                OfferModel offerModel = offerRepo.findByIdentification(id);
                if (offerModel != null) {
                    if (status.equalsIgnoreCase(String.valueOf(NumberEnum.ZERO.getNum()))) {
                        offerModel.setCloseTime(new Date());
                    }
                    JSONObject json = (JSONObject) JSON.toJSON(offerModel);
                    json.put("offerTags", offerModel.getOfferTags());
                    String stackId = offerTagRepo.findOfferStackId(offerModel.getIdentification());
                    json.put("stack", stackId);
                    // 将当前序列化对象保存到 redis 中
                    masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification(), json.toJSONString());
                    List<String> appIds = categoryTagRepo.queryAppIds(id);
                    appIds = removeDuplicate(appIds);
                    if (appIds != null && appIds.size() > 0) {
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
                        for (String appId : appIds) {
                            updateOfferRedis(offerModel, id, appId, redisOfferCap, redisAppCap, pullCount, "changeStatus");
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<OptionVO> getOfferIdOptions(String query) {
        String sql = "SELECT DISTINCT(offer_id) FROM t_offer WHERE offer_id like ?1 ORDER BY offer_id ASC";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        nativeQuery.setParameter(1, ZooConstant.PERCENT_SIGN + query + ZooConstant.PERCENT_SIGN);
        List<String> resultList = nativeQuery.getResultList();
        List<OptionVO> optionVOS = new ArrayList<>();
        if (resultList != null && resultList.size() > 0) {
            for (String name : resultList) {
                optionVOS.add(new OptionVO(name, name, name));
            }
        }
        return optionVOS;
    }

    @Override
    public List<OptionVO> getPartnerOfferIdOptions(String query) {
        String sql = "SELECT DISTINCT(partner_offer_id) FROM t_offer WHERE partner_offer_id like ?1 ORDER BY partner_offer_id ASC";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql.toString());
        nativeQuery.unwrap(NativeQuery.class);
        nativeQuery.setParameter(1, ZooConstant.PERCENT_SIGN + query + ZooConstant.PERCENT_SIGN);
        List<String> resultList = nativeQuery.getResultList();
        List<OptionVO> optionVOS = new ArrayList<>();
        if (resultList != null && resultList.size() > 0) {
            for (String name : resultList) {
                optionVOS.add(new OptionVO(name, name, name));
            }
        }
        return optionVOS;
    }

    @Override
    public List<OfferModel> getOffers(String country, List<String> operators) {
        Specification specification = new Specification<OfferModel>() {
            @Override
            public Predicate toPredicate(Root<OfferModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if (operators != null && operators.size() > 0) {
                    CriteriaBuilder.In<String> in = criteriaBuilder.in(root.get("operator"));
                    for (String operator : operators) {
                        in.value(operator);
                    }
                    predicates.add(in);
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                orders.add(criteriaBuilder.asc(root.get("offerName")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        return offerRepo.findAll(specification);
    }

    @Override
    public OfferModel findOfferId(String offerId) {
        return offerRepo.findById(offerId).get();
    }

    @Override
    public Map<String, List<OptionVO>> getParamTips(String partner, String type) {
        Map<String, List<OptionVO>> map = new HashMap<>(1);
        Specification specification = new Specification<OfferModel>() {
            @Override
            public Predicate toPredicate(Root<OfferModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("partner"), partner));
                if (!StringUtils.isEmpty(type)) {
                    predicates.add(criteriaBuilder.equal(root.get("type"), type));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                orders.add(criteriaBuilder.asc(root.get("offerName")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        List<OfferModel> offerModels = offerRepo.findAll(specification);
        if (offerModels != null && offerModels.size() > 0) {
            List<String> params = new ArrayList<>();
            for (OfferModel offerModel : offerModels) {
                List<OptionVO> clickList = map.get("click");
                if (clickList == null) {
                    clickList = new ArrayList<>();
                }
                if (!StringUtils.isEmpty(offerModel.getClickIdParam()) && !params.contains(offerModel.getClickIdParam())) {
                    params.add(offerModel.getClickIdParam());
                    clickList.add(new OptionVO(offerModel.getIdentification(), offerModel.getClickIdParam(), offerModel.getIdentification()));
                    map.put("click", clickList);
                }

                List<OptionVO> offerIdList = map.get("offerId");
                if (offerIdList == null) {
                    offerIdList = new ArrayList<>();
                }
                if (!StringUtils.isEmpty(offerModel.getPartnerOfferIdParam()) && !params.contains(offerModel.getPartnerOfferIdParam())) {
                    params.add(offerModel.getPartnerOfferIdParam());
                    offerIdList.add(new OptionVO(offerModel.getIdentification(), offerModel.getPartnerOfferIdParam(), offerModel.getIdentification()));
                    map.put("offerId", offerIdList);
                }

                List<OptionVO> extendList = map.get("extend");
                if (extendList == null) {
                    extendList = new ArrayList<>();
                }
                if (!StringUtils.isEmpty(offerModel.getExtendParam()) && !params.contains(offerModel.getExtendParam())) {
                    params.add(offerModel.getExtendParam());
                    extendList.add(new OptionVO(offerModel.getIdentification(), offerModel.getExtendParam(), offerModel.getIdentification()));
                    map.put("extend", extendList);
                }
            }
        }
        return map;
    }

    @Override
    @Async
    public void autoUpdate() {
        String sql = "select a1.offer_id,a2.category_id from (select t1.tag_id,t1.offer_id from t_offer_tag t1 LEFT  JOIN t_tag t2 on t1.tag_id = t2.identification and t2.tag_type = 2 and t1.offer_id in(SELECT identification FROM `t_offer` where auto_status = 1) where t2.identification is not null) as a1 LEFT JOIN (select category_id,tag_id from t_category_tag where tag_id in (select t1.identification as tagId from t_tag t1 left join t_offer_tag t2 on t1.identification=t2.tag_id where t1.tag_type=2)GROUP BY category_id,tag_id)as a2 on a1.tag_id = a2.tag_id where a1.offer_id is not null and a2.category_id is not null GROUP BY a1.offer_id,a2.category_id";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        List<Object[]> resultList = nativeQuery.getResultList();
        Map<String, List<String>> offerAppList = generateOfferAppList(resultList);
        List<OfferModel> offerModels = generateOfferMode(offerAppList);
        if (offerModels != null && offerModels.size() > 0) {
            for (OfferModel offerModel : offerModels) {
                List<String> appIds = offerAppList.get(offerModel.getIdentification());
                JSONObject offerAutoStartJson = new JSONObject();
                offerAutoStartJson.put("offerModel", offerModel);
                offerAutoStartJson.put("appId", appIds);
                producer.sendToQueueOfferStart(new BaseSqsMessage(new Integer[ZooConstant.QUEUE_OFFER_AUTO_START], ZooConstant.OFFER_AUTO_START_MODEL, JSON.toJSONString(offerAutoStartJson)));
            }
        }

    }

    /**
     * 生成对应的数据
     *
     * @param resultList
     * @return
     */
    private Map<String, List<String>> generateOfferAppList(List<Object[]> resultList) {
        List<OfferAppVO> arrayList = new ArrayList<>();
        for (Object[] tuple : resultList) {
            OfferAppVO offerAppVO = new OfferAppVO();
            offerAppVO.setOfferId(tuple[0].toString());
            offerAppVO.setAppId(tuple[1].toString());
            arrayList.add(offerAppVO);
        }
        Map<String, List<String>> map = arrayList.stream().collect(Collectors.toMap(OfferAppVO::getOfferId, offerAppVO -> Lists.newArrayList(offerAppVO.getAppId()), (List<String> newList, List<String> oldList) -> {
            oldList.addAll(newList);
            return oldList;
        }));
        return map;
    }

    private List<OfferModel> generateOfferMode(Map<String, List<String>> offerAppList) {
        List<OfferModel> offerModelList = new ArrayList<>();
        Set<String> keys = offerAppList.keySet();
        List<String> offerIds = new ArrayList<>(keys);
        for (String key : offerIds) {
            OfferModel offerModel = getOfferModel(key);
            if (offerModel != null) {
                offerModelList.add(offerModel);
            }
        }
        return offerModelList;
    }


    /**
     * 更新redis List
     *
     * @param appIds
     * @param offerModel
     * @param methodName
     */
    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    public void updateOfferRedisList(List<String> appIds, OfferModel offerModel, String methodName) {
        if (appIds != null && appIds.size() > 0) {
            Integer redisOfferCap = 0;
            Integer redisAppCap = 0;
            Integer pullCount = 0;
            Map<Object,Object> redisInfoMap = cluster3RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerModel.getIdentification() + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerModel.getIdentification()));
            Object redisOfferCapObj = redisInfoMap.get(CacheNameSpace.POST_BACK_TRANS);
            Object redisAppCapObj = redisInfoMap.get(CacheNameSpace.APP_TRANS);
            Object pullCountObj = redisInfoMap.get(CacheNameSpace.PULL_COUNT);
            if (redisOfferCapObj != null) {
                redisOfferCap = Integer.valueOf(String.valueOf(redisOfferCapObj));
            }
            if (redisAppCapObj != null) {
                redisAppCap = Integer.valueOf(String.valueOf(redisAppCapObj));
            }
            if (pullCountObj != null) {
                pullCount = Integer.valueOf(String.valueOf(pullCountObj));
            }
            for (String appId : appIds) {
                updateOfferRedis(offerModel, offerModel.getIdentification(), appId, redisOfferCap, redisAppCap, pullCount, methodName);
            }
        }
    }

    /**
     * 更新EPM , Offer_Assign_Filter redis
     *
     * @param offerModel
     * @param offerId
     * @param appId
     */
    @SuppressFBWarnings({"DM_BOXED_PRIMITIVE_FOR_PARSING", "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE", "DLS_DEAD_LOCAL_STORE"})
    @Override
    public void updateOfferRedis(OfferModel offerModel, String offerId, String appId, Integer redisOfferCap, Integer redisAppCap, Integer pullCount, String methodName) {
        log.info("UPDATE OFFER REDIS ,OFFERID:{},APPID:{},REDIS_OFFER_CAP:{},REDIS_APP_CAP:{},REDIS_PULLCOUNT:{},MAX_PULL:{},METHOD:{}", offerId, appId, redisOfferCap, redisAppCap, pullCount, offerModel.getMaxPull(), methodName);
        List<String> offerIds = categoryTagRepo.queryOfferIds(appId);
        boolean haveUpdateGroup = offerIds != null && offerIds.size() > 0 && !offerIds.contains(offerId);
        if (haveUpdateGroup) {
            log.info("UPDATE EPM LIST AND FILTER , DELETE EPMLIST AND FILTER BY OVER NOT CONTAIN IN APP ,APPID:{}, OFFERID:{}", appId, offerModel.getIdentification());
            updateClostOfferRedis(appId, offerId, offerModel.getOperator());
        } else {
            String offerDayCount = CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId);
            String filterKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator();
            String appEmpKeyStart = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator() + CacheNameSpace.COLON + CacheNameSpace.LIST;
            Integer appCap = offerModel.getAppCap();
            Integer cap = offerModel.getCap();
            Integer maxPullCount = offerModel.getMaxPull();
            Map<Object,Object> offerCountMap = cluster3RedisTemplate.opsForHash().entries(offerDayCount);
            Boolean existAppTransKey = offerCountMap.containsKey(CacheNameSpace.APP_TRANS);
            Boolean existOfferTransKey = offerCountMap.containsKey(CacheNameSpace.POST_BACK_TRANS);
            Boolean existOfferPullCounter = offerCountMap.containsKey(CacheNameSpace.PULL_COUNT);
            Boolean existEmpListKey = cluster3RedisTemplate.hasKey(appEmpKeyStart);
            List<String> epmListKeys = null;
            if (existEmpListKey != null && existEmpListKey) {
                epmListKeys = cluster3RedisTemplate.opsForList().range(appEmpKeyStart, 0, -1);
            }
            Boolean offerExistEpmList = existEmpListKey != null && existEmpListKey && epmListKeys != null && epmListKeys.indexOf(offerId) > -1;
            Boolean existFilter = cluster3RedisTemplate.opsForHash().hasKey(filterKey, offerId);
            Boolean existEpmAndFilter = existFilter != null && existFilter;
            // EPM不存在该offer,或者该offer不满足条件后被删除需要重新添加
            if (!existEpmAndFilter) {
                //存在被拉取的次数
                boolean existOfferPull = existOfferPullCounter != null && existOfferPullCounter;
                if (!existOfferPull) {
                    // 若在跑量时间并且是开启状态则添加
                    if (getIsTime(offerId) && offerModel.getStatus() == 1) {
                        addNewOfferToEpmList(appId, offerId, offerModel.getOperator());
                        handleOfferFilter(offerModel, offerId, appId);
                    }
                } else {
                    boolean isInTime = getIsTime(offerId);
                    boolean updateEpmAndFilterRedis = checkNeedUpdateEpmAndFilterRedis(appCap, cap, maxPullCount, existAppTransKey, existOfferTransKey, pullCount, isInTime, redisOfferCap, redisAppCap, pullCount);
                    // 是否需要添加redis
                    if (updateEpmAndFilterRedis) {
                        if (!offerExistEpmList && offerModel.getStatus() == 1) {
                            addNewOfferToEpmList(appId, offerId, offerModel.getOperator());
                        }
                        OfferModel filterModel = generateFitlterModel(offerModel, offerId);
                        Boolean existUnusedOffer = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_UNUSED_OFFER, offerId);
                        if (existUnusedOffer != null && existUnusedOffer) {
                            log.info("NEED UPDATE UNUSED OFFER, OFFERID:{},", offerModel.getIdentification());
                            masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_UNUSED_OFFER, offerId);
                        }
                        if (offerModel.getStatus() == 1 && isInTime) {
                            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator(), offerId, JSON.toJSONString(filterModel));
                        }
                    } else {
                        // 删除redis
                        log.info("UPDATE EPM LIST AND FILTER , DELETE EPMLIST AND FILTER, appId:{}, ,offer:{}, maxPullcount:{} , pullCount:{}, cap :{}, redisOfferCap:{}, appCap:{}, redisAppCap:{}, isInTime:{}", appId, offerModel.getIdentification(), maxPullCount, pullCount, cap, redisOfferCap, appCap, redisAppCap, getIsTime(offerId));
                        updateClostOfferRedis(appId, offerId, offerModel.getOperator());
                    }
                }
            } else {
                boolean removeRedis = offerModel.getStatus() != 1 || !getIsTime(offerId) || (maxPullCount != null && maxPullCount <= pullCount) || cap <= redisOfferCap || (appCap != null && appCap <= redisAppCap);
                log.info("exist fitler offer info , offerId:{},status:{},inTime:{}", offerModel.getIdentification(), offerModel.getStatus(), getIsTime(offerId));
                if (removeRedis) {
                    log.info("UPDATE EPM LIST AND FILTER , DELETE EPMLIST AND FILTER, appId:{}, ,offer:{}, maxPullcount:{} , pullCount:{}, cap :{}, redisOfferCap:{}, appCap:{}, redisAppCap:{}, isInTime:{}", appId, offerModel.getIdentification(), maxPullCount, pullCount, cap, redisOfferCap, appCap, redisAppCap, getIsTime(offerId));
                    updateClostOfferRedis(appId, offerId, offerModel.getOperator());
                } else {
                    OfferModel filterModel = generateFitlterModel(offerModel, offerId);
                    masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator(), offerId, JSON.toJSONString(filterModel));
                }
            }
        }
    }

    private void handleOfferFilter(OfferModel offerModel, String offerId, String appId) {
        OfferModel filterModel = new OfferModel();
        filterModel.setMaxPull(offerModel.getMaxPull());
        filterModel.setAppCap(offerModel.getAppCap());
        filterModel.setCap(offerModel.getCap());
        String stack = offerTagRepo.findOfferStackId(offerId);
        filterModel.setStack(stack);
        filterModel.setOperator(offerModel.getOperator());
        filterModel.setResetTimezone(offerModel.getResetTimezone());
        filterModel.setResetTime(offerModel.getResetTime());
        Boolean existUnusedOffer = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_UNUSED_OFFER, offerId);
        if (existUnusedOffer != null && existUnusedOffer) {
            masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_UNUSED_OFFER, offerId);
        }
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator(), offerId, JSON.toJSONString(filterModel));
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    @Override
    @Async
    public void initFilterRedis() {
        List<ApplicationModel> applicationModelList = appInfoRepo.findByStatus(1);
        if (applicationModelList != null && applicationModelList.size() > 0) {
            log.info("Application model :{},size:{}", JSON.toJSONString(applicationModelList), applicationModelList.size());
            for (ApplicationModel applicationModel : applicationModelList) {
                List<String> offerIds = categoryTagRepo.queryOfferIds(applicationModel.getIdentification());
                for (String offerId : offerIds) {
                    OfferModel offerModel = getOfferModel(offerId);
                    if (offerModel != null && offerModel.getStatus() == 1) {
                        try {
                            log.info("appInfo:{} , init offerModel:{}", JSON.toJSONString(applicationModel), JSON.toJSONString(offerIds));
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
                            updateOfferRedis(offerModel, offerId, applicationModel.getIdentification(), redisOfferCap, redisAppCap, pullCount, "initFilterRedis");
                        } catch (Exception e) {
                            log.info("init redis error :{}, offerModel:{}", JSON.toJSONString(e), JSON.toJSONString(offerModel));
                        }
                    }
                }
            }
        }
    }

    /**
     * 同步新的redis
     */
    @Override
    @Async
    public void importTodayRedis() {
        String offerTranKeyPartner = CacheNameSpace.ZOO_OFFER_TRNS_COUNTER + CacheNameSpace.ASTERISK + DateUtil.today();
        String appTransKeyPartner = CacheNameSpace.ZOO_APP_TRNS_COUNTER + CacheNameSpace.ASTERISK + DateUtil.today();
        String pullCounterKeyPartner = CacheNameSpace.ZOO_OFF_PULL_COUNTER + CacheNameSpace.ASTERISK + DateUtil.today();
        Set<String> offerTransKeys = cluster3RedisTemplate.keys(offerTranKeyPartner);
        Set<String> appTransKeys = cluster3RedisTemplate.keys(appTransKeyPartner);
        Set<String> pullCountKeys = cluster3RedisTemplate.keys(pullCounterKeyPartner);
        if (offerTransKeys != null && offerTransKeys.size() > 0) {
            updateNewOfferTodayRedis(offerTransKeys, CacheNameSpace.POST_BACK_TRANS);
        }
        if (appTransKeys != null && appTransKeys.size() > 0) {
            updateNewOfferTodayRedis(appTransKeys, CacheNameSpace.APP_TRANS);
        }
        if (pullCountKeys != null && pullCountKeys.size() > 0) {
            updateNewOfferTodayRedis(pullCountKeys, CacheNameSpace.PULL_COUNT);
        }
    }

    @SuppressFBWarnings({"DM_BOXED_PRIMITIVE_FOR_PARSING", "UC_USELESS_OBJECT"})
    @Override
    public JSONObject fechStackOffer(String operator, String country, Integer closeCount) {
        JSONObject resultJson = new JSONObject();
        List<Object[]> result = offerTagRepo.findTagNameCapOrderByCap(operator);
        List<String> tagNames = new ArrayList<>();
        JSONObject tagCaps = new JSONObject(new LinkedHashMap<>());
        JSONObject offerInfo = new JSONObject();
        List<JSONObject> offerInfoList = new ArrayList<>();
        for (Object obj : result) {
            Object[] row = (Object[]) obj;
            String tagName = (String) row[0];
            tagNames.add(tagName);
            String cap = row[1].toString();
            List<String> offerIds = offerTagRepo.fetchOfferIdsByTagName(tagName);
            int totalTrans = 0;
            // 详细offer信息
            List<JSONObject> offerJsonList = new ArrayList<>();
            // 开启offer根据cap大小排列
            for (String offerId : offerIds) {
                OfferModel offerModel = getOfferModel(offerId);
                JSONObject offerJson = new JSONObject();
                offerJson.put("offerName", offerModel.getOfferName());
                offerJson.put("cap", offerModel.getCap());
                offerJson.put("status", offerModel.getStatus());
                offerJsonList.add(offerJson);
                Boolean existOfferCap = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId), CacheNameSpace.POST_BACK_TRANS);
                if (existOfferCap != null && existOfferCap) {
                    totalTrans = totalTrans + Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER_DAY_INFO + offerId + CacheNameSpace.COLON + DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId), CacheNameSpace.POST_BACK_TRANS)));
                }
            }
            // 关闭offer 根据关闭时间查询
            if (closeCount == null) {
                closeCount = 50;
            }
            List<String> closeOfferIds = offerTagRepo.fetchCloseOfferIdsByTagName(tagName, closeCount);
            for (String offerId : closeOfferIds) {
                OfferModel offerModel = getOfferModel(offerId);
                JSONObject offerJson = new JSONObject();
                offerJson.put("offerName", offerModel.getOfferName());
                offerJson.put("closeTime", DateUtil.formatyyyyMMddHHmmss(offerModel.getCloseTime()));
                offerJson.put("status", offerModel.getStatus());
                offerJsonList.add(offerJson);
            }
            tagCaps.put(tagName, totalTrans + "/" + cap);
            offerInfo.put(tagName, offerJsonList);
        }
        tagNames = sortTagNamesByTrans(tagCaps.entrySet());
        JSONObject offerJSON = new JSONObject();
        offerJSON.put("offerIds", offerInfo);
        offerInfoList.add(offerJSON);
        resultJson.put("stackNames", tagNames);
        resultJson.put("stackCap", tagCaps);
        resultJson.put("list", offerInfoList);
        return resultJson;
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    private List<String> sortTagNamesByTrans(Set<Map.Entry<String, Object>> entries) {
        List<String> transzeroList = new ArrayList<>();
        List<String> transList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : entries) {
            String tagName = entry.getKey();
            String trans = entry.getValue().toString().split("/")[0];
            if (!StringUtils.isEmpty(trans) && Integer.valueOf(trans) == 0) {
                transzeroList.add(tagName);
            } else {
                transList.add(tagName);
            }
        }
        transList.addAll(transzeroList);
        return transList;
    }

    @Override
    @Async
    public void initOfferRedis() {
        List<OfferModel> offerModelList = offerRepo.findAll();
        if (offerModelList != null && offerModelList.size() > 0) {
            for (OfferModel offerModel : offerModelList) {
                masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification(), JSON.toJSONString(offerModel));
                if (offerModel.getCloseTime() == null) {
                    offerRepo.updateCloseTime(offerModel.getCreateTime(), offerModel.getIdentification());
                    offerModel.setCloseTime(offerModel.getCreateTime());
                    masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification(), JSON.toJSONString(offerModel));
                }
            }
        }
    }

    private Calendar getCurrentHourCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    public void checkAlarmEpm(String msgBody) {
        ApplicationModel applicationModel = JSONObject.parseObject(msgBody, ApplicationModel.class);
        JSONArray epmOperatorList = applicationModel.getEpmAlarmOperator();
        for (Object object : epmOperatorList) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(object));
            List<String> operators = JSONArray.parseArray(jsonObject.getJSONArray("operatorArr").toString(), String.class);
            Double rate = jsonObject.getDouble("rate");
            Integer time = jsonObject.getInteger("time");
            String startTime = jsonObject.getString("startTime");
            String endTime = jsonObject.getString("endTime");
            String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + applicationModel.getIdentification();
            if (operators != null && operators.size() > 0) {
                // 每15分钟记录一次数据,如果是整点则取上个小时的zoo_aff_epm_counter:app:appId:offerId:2021-01-06-04
                String localTimeMinute = getEpmAlarmMinute();
                String[] localTimeList = DateUtil.formatDayHour(new Date()).split(" ");
                String epmCountRedisHour = localTimeList[0] + "-" + localTimeList[1];
                if (localTimeMinute.equalsIgnoreCase("00")) {
                    String[] beforeHourList = DateUtil.addTime(null, DateConstant.HOUR_STR, -1).split(":")[0].split(" ");
                    epmCountRedisHour = beforeHourList[0] + "-" + beforeHourList[1];
                }
                pattern = pattern + CacheNameSpace.ASTERISK + epmCountRedisHour;
                Set<String> hKeySet = cluster3RedisTemplate.keys(pattern);
                List<String> epmOperators = new ArrayList<>();
                String recordKeyStart = CacheNameSpace.ZOO_APP_EPM_ALRAM + CacheNameSpace.COLON + applicationModel.getIdentification() + CacheNameSpace.COLON;
                deleteRecords(operators, recordKeyStart);
                if (hKeySet != null && hKeySet.size() > 0) {
                    for (String redisKey : hKeySet) {
                        String offerId = redisKey.split(ZooConstant.COLON)[3];
                        Integer clickNum = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(redisKey, CacheNameSpace.CLICK) != null ? cluster3RedisTemplate.opsForHash().get(redisKey, CacheNameSpace.CLICK) : "0"));
                        Double revenue = new BigDecimal(String.valueOf(cluster3RedisTemplate.opsForHash().get(redisKey, CacheNameSpace.REVENUE) != null ? cluster3RedisTemplate.opsForHash().get(redisKey, CacheNameSpace.REVENUE) : "0.0")).setScale(3, RoundingMode.HALF_UP).doubleValue();
                        OfferModel offerModel = getOfferModel(offerId);
                        // 记录存在告警operator 的数据
                        if (offerModel != null && operators.contains(offerModel.getOperator())) {
                            String operator = offerModel.getOperator();
                            epmOperators.add(operator);
                            String epmMinute = getEpmAlarmMinute();
                            String recordKey = recordKeyStart + operator + ZooConstant.COLON + DateUtil.formatyyyyMMddHH(new Date()) + ZooConstant.COLON + epmMinute;
                            // 每15分钟记录一次数据 redisKey 为 zoo_app_epm_alarm:appId:operaotr:2021-01-08 12:15 ,并保存两天的记录
                            Boolean existAppOperatorRedis = cluster3RedisTemplate.hasKey(recordKey);
                            if (existAppOperatorRedis != null && existAppOperatorRedis) {
                                logRecordRedis(recordKey, clickNum, revenue);
                            } else {
                                logRecordRedis(recordKey, clickNum, revenue);
                                masterRedisTemplate.expire(recordKey, 1, TimeUnit.DAYS);
                            }
                        }
                    }
                }
                boolean inAlarmTime = checkIsInAlarmTime(startTime, endTime);
                if (inAlarmTime) {
                    String localAlarmTime = DateUtil.formatyyyyMMddHH(new Date()) + ZooConstant.COLON + getEpmAlarmMinute() + ZooConstant.COLON + "00";
                    checkAppEpmAndMail(operators, epmOperators, time, recordKeyStart, localTimeMinute, applicationModel, rate, localAlarmTime);
                }
            }
        }
    }

    @Override
    public OfferModel getTestOffer(String partnerId) {
        OfferModel offerModel = null;
        Boolean existTestOffer = cluster3RedisTemplate.opsForHash().hasKey(ZooConstant.ZOO_TEST_OFFER, partnerId);
        if (existTestOffer != null && existTestOffer) {
            offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(ZooConstant.ZOO_TEST_OFFER, partnerId)), OfferModel.class);
        }
        return offerModel;
    }

    @Override
    public void saveTestOffer(OfferModel offerModel) {
        String key = ZooConstant.ZOO_TEST_OFFER;
        String testOfferKey = ZooConstant.ZOO_TEST_OFFER_INFO;
        masterRedisTemplate.opsForHash().put(ZooConstant.ZOO_TEST_OFFER_DATA,offerModel.getIdentification(),JSON.toJSONString(offerModel));
        masterRedisTemplate.opsForHash().put(testOfferKey, offerModel.getOfferId(), JSON.toJSONString(offerModel));
        masterRedisTemplate.opsForHash().put(key, offerModel.getPartner(), JSON.toJSONString(offerModel));
    }




    private boolean checkIsInAlarmTime(String startTime, String endTime) {
        boolean inAlarmTime = false;
        if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
            String timeZone = "GMT+08:00";
            String localHour = DateUtil.getHourByTimeZone(new Date(), timeZone).split(" ")[1];
            if (Integer.valueOf(localHour) > Integer.valueOf(startTime) && Integer.valueOf(localHour) < Integer.valueOf(endTime)) {
                inAlarmTime = true;
            }
        }
        if (StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)) {
            inAlarmTime = true;
        }
        return inAlarmTime;

    }

    private void deleteRecords(List<String> operators, String recordKeyStart) {
        if (operators != null && operators.size() > 0) {
            for (String operator : operators) {
                String epmMinute = getEpmAlarmMinute();
                String recordKey = recordKeyStart + operator + ZooConstant.COLON + DateUtil.formatyyyyMMddHH(new Date()) + ZooConstant.COLON + epmMinute;
                Boolean existEpmRecord = cluster3RedisTemplate.hasKey(recordKey);
                if (existEpmRecord != null && existEpmRecord) {
                    masterRedisTemplate.delete(recordKey);
                }
            }

        }
    }

    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    private String getEpmAlarmMinute() {
        String minute = DateUtil.formatyyyyMMddHHmm(new Date()).split(ZooConstant.COLON)[1];
        int time = Integer.valueOf(minute);
        int overTimeMinute = time / 15;
        String epmMinute = String.valueOf(overTimeMinute * 15 == 0 ? "00" : overTimeMinute * 15);
        return epmMinute;
    }

    /**
     * epm 告警运营商有数据并且告警区段有完整数据
     *
     * @param operators
     * @param epmOperators
     * @param time
     * @param recordKeyStart
     * @param localTimeMinute
     * @param applicationModel
     * @param rate
     */
    @SuppressFBWarnings("DM_BOXED_PRIMITIVE_FOR_PARSING")
    private void checkAppEpmAndMail(List<String> operators, List<String> epmOperators, Integer time, String recordKeyStart, String localTimeMinute, ApplicationModel applicationModel, Double rate, String localAlarmTime) {
        for (String operator : operators) {
            if (epmOperators.contains(operator)) {
                String[] startEpmAlarm = DateUtil.addTime(localAlarmTime, DateConstant.MIN_STR, -(time * 2)).split(ZooConstant.COLON);
                String startEpmAlarmKey = recordKeyStart + operator + ZooConstant.COLON + startEpmAlarm[0] + CacheNameSpace.COLON + startEpmAlarm[1];
                String mailStartTime = DateUtil.addTime(localAlarmTime, DateConstant.MIN_STR, -(time * 2));
                String mailMidTime = DateUtil.addTime(localAlarmTime, DateConstant.MIN_STR, -(time * 1));
                Boolean existTotalAlarm = cluster3RedisTemplate.hasKey(startEpmAlarmKey);
                boolean needAlarm = (Integer.valueOf(localTimeMinute) % time == 0 || localTimeMinute.equalsIgnoreCase("00")) && existTotalAlarm;
                log.info("APP EPM ALARM STEP 8,APP NAME:{},LOCAL TIME MINUTE:{},TIME:{}, START EPM ALARM KEY:{}", applicationModel.getAppName(), localTimeMinute, time, startEpmAlarmKey);
                if (needAlarm) {
                    String[] beforeOneAlarm = DateUtil.addTime(localAlarmTime, DateConstant.MIN_STR, -time).split(ZooConstant.COLON);
                    String beforeOneEpmAlarmKey = recordKeyStart + operator + ZooConstant.COLON + beforeOneAlarm[0] + CacheNameSpace.COLON + beforeOneAlarm[1];
                    String localTime = DateUtil.formatyyyyMMddHH(new Date()) + ZooConstant.COLON + localTimeMinute;
                    String nowEpmAlarmKey = recordKeyStart + operator + ZooConstant.COLON + localTime;
                    Boolean existBeforeEpmAlarm = cluster3RedisTemplate.hasKey(beforeOneEpmAlarmKey);
                    Boolean existNowEpmAlarm = cluster3RedisTemplate.hasKey(nowEpmAlarmKey);
                    boolean existAlarmData = existBeforeEpmAlarm != null && existBeforeEpmAlarm && existNowEpmAlarm != null && existNowEpmAlarm;
                    log.info("APP EPM ALARM STEP 9,APP NAME:{},BEFORE ONE ALARM KEY:{},NOW ALARM KEY:{}", applicationModel.getAppName(), beforeOneEpmAlarmKey, nowEpmAlarmKey);
                    if (existAlarmData) {
                        double beforeOneAlarmRevenue = Double.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(beforeOneEpmAlarmKey, CacheNameSpace.REVENUE) != null ? cluster3RedisTemplate.opsForHash().get(beforeOneEpmAlarmKey, CacheNameSpace.REVENUE) : "0.0"));
                        int beforeOneAlarmClickNum = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(beforeOneEpmAlarmKey, CacheNameSpace.CLICK) != null ? cluster3RedisTemplate.opsForHash().get(beforeOneEpmAlarmKey, CacheNameSpace.CLICK) : "0"));
                        double nowRevenue = Double.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(nowEpmAlarmKey, CacheNameSpace.REVENUE) != null ? cluster3RedisTemplate.opsForHash().get(nowEpmAlarmKey, CacheNameSpace.REVENUE) : "0.0"));
                        int nowClickNum = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(nowEpmAlarmKey, CacheNameSpace.CLICK) != null ? cluster3RedisTemplate.opsForHash().get(nowEpmAlarmKey, CacheNameSpace.CLICK) : "0"));
                        // 60分钟告警
                        if (time.equals(NumberEnum.SIXTY.getNum())) {
                            checkAlarmRateMail(applicationModel, operator, new BigDecimal(beforeOneAlarmRevenue), new BigDecimal(beforeOneAlarmClickNum), new BigDecimal(nowRevenue), new BigDecimal(nowClickNum), rate, mailStartTime, mailMidTime, localAlarmTime);
                        } else {
                            if (startEpmAlarm[1].equalsIgnoreCase("00")) {
                                BigDecimal nowCutRevenue = new BigDecimal(nowRevenue).subtract(new BigDecimal(beforeOneAlarmRevenue)).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal nowCutClickNum = new BigDecimal(nowClickNum).subtract(new BigDecimal(beforeOneAlarmClickNum));
                                checkAlarmRateMail(applicationModel, operator, new BigDecimal(beforeOneAlarmRevenue), new BigDecimal(beforeOneAlarmClickNum), nowCutRevenue, nowCutClickNum, rate, mailStartTime, mailMidTime, localAlarmTime);
                            } else if (beforeOneAlarm[1].equalsIgnoreCase("00")) {
                                BigDecimal beforeTwoAlarmRevenue = new BigDecimal(String.valueOf(cluster3RedisTemplate.opsForHash().get(startEpmAlarmKey, CacheNameSpace.REVENUE)));
                                BigDecimal beforeTwoAlarmClickNum = new BigDecimal(String.valueOf(cluster3RedisTemplate.opsForHash().get(startEpmAlarmKey, CacheNameSpace.CLICK)));
                                BigDecimal beforeOneRevenue = new BigDecimal(beforeOneAlarmRevenue).subtract(beforeTwoAlarmRevenue);
                                BigDecimal beforeOneClick = new BigDecimal(beforeOneAlarmClickNum).subtract(beforeTwoAlarmClickNum);
                                checkAlarmRateMail(applicationModel, operator, beforeOneRevenue, beforeOneClick, new BigDecimal(nowRevenue), new BigDecimal(nowClickNum), rate, mailStartTime, mailMidTime, localAlarmTime);
                            } else {
                                BigDecimal beforeTwoAlarmRevenue = new BigDecimal(String.valueOf(cluster3RedisTemplate.opsForHash().get(startEpmAlarmKey, CacheNameSpace.REVENUE)));
                                BigDecimal beforeTwoAlarmClickNum = new BigDecimal(String.valueOf(cluster3RedisTemplate.opsForHash().get(startEpmAlarmKey, CacheNameSpace.CLICK)));
                                BigDecimal beforeOneRevenue = new BigDecimal(beforeOneAlarmRevenue).subtract(beforeTwoAlarmRevenue);
                                BigDecimal beforeOneClick = new BigDecimal(beforeOneAlarmClickNum).subtract(beforeTwoAlarmClickNum);
                                BigDecimal nowCutRevenue = new BigDecimal(nowRevenue).subtract(new BigDecimal(beforeOneAlarmRevenue));
                                BigDecimal nowCutClick = new BigDecimal(nowClickNum).subtract(new BigDecimal(beforeOneAlarmClickNum));
                                checkAlarmRateMail(applicationModel, operator, beforeOneRevenue, beforeOneClick, nowCutRevenue, nowCutClick, rate, mailStartTime, mailMidTime, localAlarmTime);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查告警并发送邮件
     *
     * @param applicationModel
     * @param operator
     * @param beforeOneAlarmRevenue
     * @param beforeOneAlarmClickNum
     * @param nowRevenue
     * @param nowClickNum
     * @param rate
     * @param mailStartTime
     * @param mailMidTime
     */
    private void checkAlarmRateMail(ApplicationModel applicationModel, String operator, BigDecimal beforeOneAlarmRevenue, BigDecimal beforeOneAlarmClickNum, BigDecimal nowRevenue, BigDecimal nowClickNum, Double rate, String mailStartTime, String mailMidTime, String localTime) {
        BigDecimal beforeEpm;
        BigDecimal nowEpm;
        if (beforeOneAlarmRevenue.compareTo(new BigDecimal(0)) == 0 || beforeOneAlarmClickNum.compareTo(new BigDecimal(0)) == 0) {
            beforeEpm = new BigDecimal(0);
        } else {
            beforeEpm = beforeOneAlarmRevenue.divide(beforeOneAlarmClickNum, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(NumberEnum.ONE_THOUSAND.getNum()));
        }
        if (nowRevenue.compareTo(new BigDecimal(0)) == 0 || nowClickNum.compareTo(new BigDecimal(0)) == 0) {
            nowEpm = new BigDecimal(0);
        } else {
            nowEpm = nowRevenue.divide(nowClickNum, 6, RoundingMode.HALF_UP).multiply(new BigDecimal(NumberEnum.ONE_THOUSAND.getNum()));
        }
        BigDecimal calRate = beforeEpm.subtract(nowEpm);
        BigDecimal redisRate = new BigDecimal(0);
        boolean alarm = false;
        if (calRate.compareTo(new BigDecimal(0)) > 0 && beforeEpm.compareTo(new BigDecimal(0)) > 0) {
            if (calRate.compareTo(new BigDecimal(0)) == 0 || beforeEpm.compareTo(new BigDecimal(0)) == 0) {
                redisRate = new BigDecimal(0);
            } else {
                redisRate = calRate.divide(beforeEpm, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(NumberEnum.ONE_HUNDRED.getNum()));
            }
            alarm = redisRate.compareTo(new BigDecimal(rate)) > 0;
        }
        log.info("CHECK EPM RATE APP :{},OPERATOR:{},BEFORE EPM :{},NOW EPM:{}, RATE:{}, RECORD RATE:{},RESULT:{}", JSON.toJSONString(applicationModel), operator, beforeEpm.doubleValue(), nowEpm.doubleValue(), rate, redisRate, alarm);
        if (alarm) {
            try {
                Map<String, Object> content = new HashMap<>(8);
                content.put(ZooConstant.TITLE, ZooConstant.ZOO_EPM_ALARM_MAIL_SUBJECT);
                content.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
                content.put(ZooConstant.BEGIN_TIME, mailStartTime + "~" + mailMidTime);
                content.put(ZooConstant.END_TIME, mailMidTime + "~" + localTime);
                content.put(ZooConstant.APP_NAME, applicationModel.getAppName());
                content.put(ZooConstant.OPERATOR, operator);
                content.put(ZooConstant.RATE, rate);
                content.put(ZooConstant.RECORD_RATE, redisRate);
                content.put(ZooConstant.BEFORE_REVENUE, beforeOneAlarmRevenue);
                content.put(ZooConstant.BEFORE_CLICK, beforeOneAlarmClickNum);
                content.put(ZooConstant.NOW_REVENUE, nowRevenue);
                content.put(ZooConstant.NOW_CLICK, nowClickNum);
                content.put(ZooConstant.BEFORE_EPM, beforeEpm);
                content.put(ZooConstant.NOW_EPM, nowEpm);
                List<String> defaultEmail = applicationModel.getEmails();
                log.info("EPM ALARM EMAIL :{},CONTENT:{}", JSON.toJSONString(defaultEmail), JSONObject.toJSONString(content));
                if (!StringUtils.isEmpty(defaultEmail)) {
                    String[] mails = defaultEmail.toArray(new String[defaultEmail.size()]);
                    if (mails.length == 1) {
                        emailUtil.sendMimeMessageMail(ZooConstant.ZOO_EPM_ALARM_MAIL_TEMPLATE, mails[0], ZooConstant.ZOO_EPM_ALARM_MAIL_SUBJECT, content);
                    } else {
                        String[] ccEmails = Arrays.copyOfRange(mails, 1, mails.length);
                        emailUtil.sendMimeMessageMail(ZooConstant.ZOO_EPM_ALARM_MAIL_TEMPLATE, mails[0], ZooConstant.ZOO_EPM_ALARM_MAIL_SUBJECT, content, ccEmails);
                    }
                }
            } catch (Exception e) {
                log.error("zoo epm alarm error:{}", JSON.toJSONString(e));
            }

        }
    }


    private void logRecordRedis(String recordKey, Integer clickNum, Double revenue) {
        masterRedisTemplate.opsForHash().increment(recordKey, CacheNameSpace.CLICK, clickNum);
        masterRedisTemplate.opsForHash().increment(recordKey, CacheNameSpace.REVENUE, revenue);
    }

    @Override
    public OfferModel getOfferModel(String offerId) {
        if (!StringUtils.isEmpty(offerId)) {
            OfferModel offerModel = offerService.findOfferModel(offerId);
            return offerModel;
        } else {
            return null;
        }

    }

    @Override
    @Cacheable(value = "offer", key = "#offerId")
    public OfferModel findOfferModel(String offerId) {
        OfferModel offerModel = null;
        if (!StringUtils.isEmpty(offerId)) {
            Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId);
            if (obj != null) {
                offerModel = JSON.parseObject(String.valueOf(obj), OfferModel.class);
                if (StringUtils.isEmpty(offerModel.getIdentification())) {
                    offerModel.setIdentification(offerId);
                    log.info("REDIS OFFER ID IS NULL,OFFER INFO :{}", JSON.toJSONString(offerModel));
                }
            } else {
                offerModel = offerRepo.findByIdentification(offerId);
            }
        }
        return offerModel;
    }

    @Override
    @Async
    public void handleAutoStart(String msgBody) {
        JSONObject params = JSONObject.parseObject(msgBody);
        OfferModel offerModel = JSONObject.parseObject(params.getString("offerModel"), OfferModel.class);
        List<String> appIds = JSONArray.parseArray(params.getString("appId"), String.class);
        Integer start = offerModel.getAutoStart();
        Integer end = offerModel.getAutoEnd();
        appIds = removeDuplicate(appIds);
        Calendar calendar = Calendar.getInstance();
        log.info("ZOO AUTO HANDLE OFFER STATUS ,OFFERID:{},APPID:{}", offerModel.getOfferId(), JSON.toJSONString(appIds));
        if (start != null && calendar.get(Calendar.HOUR_OF_DAY) == start) {
            // 开启
            log.info("AUTO START OFFER INFO,OFFERID:{},OFFER START TIME:{}", offerModel.getOfferId(), start);
            offerModel.setStatus(ZooConstant.STATUS_1);
            OfferModel saveModel = offerRepo.save(offerModel);
            JSONObject json = (JSONObject) JSON.toJSON(saveModel);
            List<OfferTagModel> offerTags = offerTagRepo.findAllByTagId(saveModel.getIdentification());
            json.put("offerTags", offerTags);
            String stackId = offerTagRepo.findOfferStackId(saveModel.getIdentification());
            json.put("stack", stackId);
            // 将当前序列化对象保存到 redis 中
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, saveModel.getIdentification(), json.toJSONString());
        }
        if (end != null && calendar.get(Calendar.HOUR_OF_DAY) == end) {
            // 关闭
            log.info("AUTO CLOSE OFFER INFO,OFFERID:{},OFFER CLOSE TIME:{}", offerModel.getOfferId(), end);
            offerModel.setStatus(ZooConstant.STATUS_0);
            OfferModel saveModel = offerRepo.save(offerModel);
            JSONObject json = (JSONObject) JSON.toJSON(saveModel);
            List<OfferTagModel> offerTags = offerTagRepo.findAllByTagId(saveModel.getIdentification());
            json.put("offerTags", offerTags);
            String stackId = offerTagRepo.findOfferStackId(saveModel.getIdentification());
            json.put("stack", stackId);
            // 将当前序列化对象保存到 redis 中
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, saveModel.getIdentification(), json.toJSONString());
        }
        updateOfferRedisList(appIds, offerModel, "handleAutoStart");
    }

    @Override
    @Async
    public void handleAutoUpdateRedis(OfferModel offerModel) {
        List<String> appIds = categoryTagRepo.queryAppIds(offerModel.getIdentification());
        appIds = removeDuplicate(appIds);
        updateOfferRedisList(appIds, offerModel, "handleAutoUpdateRedis");
    }


    @Override
    @Async
    public void deleteTimoutRedis() {
        String offerTransKey = CacheNameSpace.ZOO_OFFER_TRNS_COUNTER + CacheNameSpace.ASTERISK;
        String offerPullKey = CacheNameSpace.ZOO_OFF_PULL_COUNTER + CacheNameSpace.ASTERISK;
        Set<String> offerTransKeySet = cluster3RedisTemplate.keys(offerTransKey);
        Set<String> offerPullKeySet = cluster3RedisTemplate.keys(offerPullKey);
        if(offerPullKeySet != null && offerPullKeySet.size() >0){
            offerPullKeySet.stream().forEach(a->{
                offerService.deleteTimoutKey(a);
            });
        }

        if(offerTransKeySet != null && offerTransKeySet.size() >0){
            offerTransKeySet.stream().forEach(a->{
                offerService.deleteTimoutKey(a);
            });
        }
    }

    @Override
    @Async
    public void deleteAlarmTimeOutKey(String key) {
        String[] strArr = key.split(CacheNameSpace.COLON);
        if (strArr.length == NumberEnum.FIVE.getNum()) {
            String time = strArr[3];
            Date redisDate = DateUtil.formatHourTime(time);
            Date yesterday = DateUtil.formatDayTime(DateUtil.yesterday());
            boolean deleteKey = DateUtil.isBefore(redisDate, yesterday);
            if (deleteKey) {
                masterRedisTemplate.delete(key);
            }
        }
    }

    @Override
    @Async
    public void deleteAppEventRedis() {
        String keyParam = ZooConstant.APP_EVENT_CODE  + CacheNameSpace.ASTERISK;
        Set<String> keySet = cluster3RedisTemplate.keys(keyParam);
        if(keySet != null && keySet.size() >0){
            keySet.stream().forEach(a->{
                offerService.deleteEventRedis(a);
            });
        }
    }

    @Override
    @Async
    public void deleteEventRedis(String key) {
        masterRedisTemplate.delete(key);
    }

    @Override
    public JSONObject getEpmListRedis(List<String> appNames) {
        List<AppOfferRedisVo> resultList = new ArrayList<>();
        for (String appName : appNames) {
            String appId = appInfoRepo.findByAppName(appName);
            List<String> operators = categoryTagRepo.queryOperators(appId);
            if (operators != null && operators.size() > 0) {
                for (String operator : operators) {
                    String key = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + CacheNameSpace.LIST;
                    List<String> list = cluster3RedisTemplate.opsForList().range(key, 0, -1);
                    if (list != null && list.size() > 0) {
                        Map<String, List<String>> offerSizeMap = list.stream().collect(Collectors.groupingBy(e -> e));
                        for (Map.Entry<String, List<String>> entry : offerSizeMap.entrySet()) {
                            AppOfferRedisVo vo = new AppOfferRedisVo();
                            vo.setAppName(appName);
                            vo.setOperator(operator);
                            OfferModel offerModel = offerService.getOfferModel(entry.getKey());
                            if (offerModel != null) {
                                vo.setOfferName(offerModel.getOfferName());
                                vo.setOfferCount(offerSizeMap.get(entry.getKey()).size());
                                resultList.add(vo);
                            }
                        }
                    }
                }
            }
        }
        resultList = resultList.stream().sorted(Comparator.comparing(AppOfferRedisVo :: getAppName).thenComparing(AppOfferRedisVo :: getOperator).thenComparing(AppOfferRedisVo :: getOfferCount).reversed()).collect(Collectors.toList());
        JSONObject resultJson = new JSONObject();
        resultJson.put("list", resultList);
        return resultJson;
    }

    @Override
    @Async
    public void autoUpdateDate(Integer retryTime) {
        String sql = "select a1.offer_id,a2.category_id from (select t1.tag_id,t1.offer_id from t_offer_tag t1 LEFT  JOIN t_tag t2 on t1.tag_id = t2.identification and t2.tag_type = 2 and t1.offer_id in(SELECT identification FROM `t_offer` where auto_status = 1) where t2.identification is not null) as a1 LEFT JOIN (select category_id,tag_id from t_category_tag where tag_id in (select t1.identification as tagId from t_tag t1 left join t_offer_tag t2 on t1.identification=t2.tag_id where t1.tag_type=2)GROUP BY category_id,tag_id)as a2 on a1.tag_id = a2.tag_id where a1.offer_id is not null and a2.category_id is not null GROUP BY a1.offer_id,a2.category_id";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        List<Object[]> resultList = nativeQuery.getResultList();
        Map<String, List<String>> offerAppList = generateOfferAppList(resultList);
        List<OfferModel> offerModels = generateOfferMode(offerAppList);
        if (offerModels != null && offerModels.size() > 0) {
            for (OfferModel offerModel : offerModels) {
                List<String> appIds = offerAppList.get(offerModel.getIdentification());
                JSONObject offerAutoStartJson = new JSONObject();
                offerAutoStartJson.put("offerModel", offerModel);
                offerAutoStartJson.put("appId", appIds);
                offerAutoStartJson.put("retryTime", retryTime);
                producer.sendToQueueOfferStart(new BaseSqsMessage(new Integer[ZooConstant.QUEUE_OFFER_AUTO_START], ZooConstant.OFFER_AUTO_START_MODEL, JSON.toJSONString(offerAutoStartJson)));
            }
        }
    }

    @Override
    @Async
    public void handleAutoStartRetry(String msgBodyStr, Integer retryTime) {
        JSONObject params = JSONObject.parseObject(msgBodyStr);
        OfferModel offerModel = JSONObject.parseObject(params.getString("offerModel"), OfferModel.class);
        List<String> appIds = JSONArray.parseArray(params.getString("appId"), String.class);
        Integer start = offerModel.getAutoStart();
        Integer end = offerModel.getAutoEnd();
        appIds = removeDuplicate(appIds);
        log.info("ZOO AUTO HANDLE OFFER STATUS ,OFFERID:{},APPID:{}", offerModel.getOfferId(), JSON.toJSONString(appIds));
        if (start != null && retryTime.equals(start)) {
            // 开启
            log.info("AUTO START OFFER INFO,OFFERID:{},OFFER START TIME:{}", offerModel.getOfferId(), start);
            offerModel.setStatus(ZooConstant.STATUS_1);
            OfferModel saveModel = offerRepo.save(offerModel);
            JSONObject json = (JSONObject) JSON.toJSON(saveModel);
            List<OfferTagModel> offerTags = offerTagRepo.findAllByTagId(saveModel.getIdentification());
            json.put("offerTags", offerTags);
            String stackId = offerTagRepo.findOfferStackId(saveModel.getIdentification());
            json.put("stack", stackId);
            // 将当前序列化对象保存到 redis 中
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, saveModel.getIdentification(), json.toJSONString());
        }
        if (end != null && retryTime.equals(end)) {
            // 关闭
            log.info("AUTO CLOSE OFFER INFO,OFFERID:{},OFFER CLOSE TIME:{}", offerModel.getOfferId(), end);
            offerModel.setStatus(ZooConstant.STATUS_0);
            OfferModel saveModel = offerRepo.save(offerModel);
            JSONObject json = (JSONObject) JSON.toJSON(saveModel);
            List<OfferTagModel> offerTags = offerTagRepo.findAllByTagId(saveModel.getIdentification());
            json.put("offerTags", offerTags);
            String stackId = offerTagRepo.findOfferStackId(saveModel.getIdentification());
            json.put("stack", stackId);
            // 将当前序列化对象保存到 redis 中
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, saveModel.getIdentification(), json.toJSONString());
        }
        updateOfferRedisList(appIds, offerModel, "handleAutoStart");
    }

    @Override
    public boolean advanceFreeOffer(String offerId, String appName) {
        String appId = appInfoRepo.findFirstByAppName(appName).getIdentification();
        List<AutoStackInfoVO> autoStackInfoVOS = JSON.parseArray(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId)), AutoStackInfoVO.class);
        // 剩余两条时，还有一条为合计需要全部释放。否则会出现无法全部释放bug
        if (NumberEnum.TWO.getNum() == autoStackInfoVOS.size()) {
            return freeOfferInApp(appName);
        }
        if (!recoverOfferGroup(Arrays.asList(offerId))) {
            return false;
        }
        updateAutoOpenOfferRemove(Arrays.asList(offerId));
        autoStackInfoVOS.removeIf(item -> item.getOfferId().equals(offerId));
        autoStackInfoVOS = calculateSumMo(autoStackInfoVOS, appName);
        // 更新Redis中该App对应的AutoStackInfoVO的moNum信息
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId, JSONObject.toJSONString(autoStackInfoVOS));
        return true;
    }

    @Override
    public List<OptionVO> getUsableOfferOption() {
        List<JSONObject> offerJsons = checkUsableOffer();
        List<OptionVO> optionVOs = new ArrayList<>();
        if (offerJsons != null && offerJsons.size() > 0) {
            for (JSONObject offerJson : offerJsons) {
                OptionVO vo = new OptionVO();
                vo.setIdentification(offerJson.getString("id"));
                vo.setLabel(offerJson.getString(ZooConstant.OFFER_NAME));
                vo.setValue(offerJson.getString(ZooConstant.OFFER_NAME));
                optionVOs.add(vo);
            }
        }
        return optionVOs;
    }

    @Override
    public List<String> getUsableGroupOption() {
        Map<Object, Object> appMap = cluster3RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG);
        List<String> testGroups = new ArrayList<>();
        if (appMap != null && appMap.size() > 0) {
            for (Map.Entry<Object, Object> entry : appMap.entrySet()) {
                testGroups.add(JSONObject.parseArray(entry.getValue().toString()).toJavaList(JSONObject.class).get(0).getString("testGroup"));
            }
        }
        return testGroups;
    }

    @Override
    public boolean handleOfferAddGroup(List<String> offerIds, String testGroup) {
        deleteOfferGroup(offerIds);
        TagModel tagModel = tagRepo.findFirstByTagName(testGroup);
        List<String> oldOfferIds = offerTagRepo.findOfferIdsByGroupId(tagModel.getIdentification());
        if (oldOfferIds != null && oldOfferIds.size() > 0) {
            oldOfferIds.addAll(offerIds);
        }
        try {
            tagService.saveTag(tagModel, oldOfferIds);
        } catch (BadRequestException e) {
            log.error("SAVE TAG MODEL ERROR:{}", e.getMessage());
        }
        updateAutoOpenOfferPut(offerIds);
        Map<Object, Object> entries = cluster3RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG);
        String appName = "";
        String appId = "";
        String description = "";
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String flagGroup = JSONObject.parseArray(entry.getValue().toString()).toJavaList(JSONObject.class).get(0).getString("testGroup");
            if (testGroup.equals(flagGroup)) {
                appName = JSONObject.parseArray(entry.getValue().toString()).toJavaList(JSONObject.class).get(0).getString("appId");
                description = JSONObject.parseArray(entry.getValue().toString()).toJavaList(JSONObject.class).get(0).getString("appName");
                appId = String.valueOf(entry.getKey());
                break;
            }
        }
        // 查出当前智能栈配置中 autoStackInfoVOS 所有信息，将新信息存储至集合中
        List<AutoStackInfoVO> autoStackInfoVOS = JSON.parseArray(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId)), AutoStackInfoVO.class);
        for (String offerId : offerIds) {
            OfferModel offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId)), OfferModel.class);
            if (offerModel == null) {
                offerModel = offerRepo.findByIdentification(offerId);
            }
            // 赋值此时 offer 初始、当前 mo
            int initMoNum = 0;
            String moKey = ZooConstant.ZOO_PAY_MO_POSTBACK + ZooConstant.COLON + offerId + ZooConstant.COLON + DateUtil.formatDay(new Date());
            Object obj = cluster3RedisTemplate.opsForHash().get(moKey, CacheNameSpace.MO_TRANS);
            if (obj != null) {
                initMoNum = Integer.valueOf(String.valueOf(obj));
            }
            AutoStackInfoVO autoStackInfoVO = new AutoStackInfoVO(appName, description, testGroup, offerModel.getOperator(), offerModel.getOfferName(), initMoNum, initMoNum, initMoNum, offerId, DateUtil.formatDay(new Date()));
            autoStackInfoVOS.add(0, autoStackInfoVO);
        }
        autoStackInfoVOS = calculateSumMo(autoStackInfoVOS, appName);
        // 更新Redis中该App对应的AutoStackInfoVO的moNum信息
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId, JSONObject.toJSONString(autoStackInfoVOS));
        return true;
    }

    @Override
    public void saveEpmListInfo() {
        for (String pattern : Constant.SAVE_EPM_LIST_INFO_KEY) {
            Set<String> keys = cluster3RedisTemplate.keys(pattern);
            if (keys != null && keys.size() > 0) {
                for (String key : keys) {
                    List<String> epmList = cluster3RedisTemplate.opsForList().range(key, 0, -1);
                    String operator = key.split(CacheNameSpace.COLON)[2];
                    if (epmList == null) {
                        epmListLog.info("DATA:{}, OPERATOR:{}, SELECT_EPM_LIST_KEY:{}, LIST_NULL", DateUtil.formatDayHourString(new Date()), operator, key);
                    } else {
                        epmListLog.info("DATA:{}, OPERATOR:{}, SELECT_EPM_LIST_KEY:{}, LIST_SIZE:{}, LIST:{}", DateUtil.formatDayHourString(new Date()), operator, key, epmList.size(), JSONObject.toJSONString(epmList));
                    }
                }
            }
        }
    }

    @Override
    public void multiSaveOfferAutoTest(JSONArray jsonArray) {
        List<JSONObject> jsonObjectList = JSONObject.parseArray(jsonArray.toJSONString(), JSONObject.class);
        // 根据日期进行 offerName 编号
        String dateStr = DateUtil.getTimeByTimeZone(new Date(), ZooConstant.GMT_8).replaceAll("-","").substring(2);
        String autoOfferNameKey = CacheNameSpace.ZOO_AUTO_OFFER_NAME + dateStr;
        Boolean existOfferNameKey = cluster3RedisTemplate.hasKey(autoOfferNameKey);
        if (existOfferNameKey != null && !existOfferNameKey) {
            masterRedisTemplate.expire(autoOfferNameKey, 2, TimeUnit.DAYS);
        }
        for (JSONObject jsonObject : jsonObjectList) {
            OptionVO groupVo = jsonObject.getObject(ZooConstant.TAG_GROUP, OptionVO.class);
            List<String> time = (List<String>) jsonObject.get(ZooConstant.TIME_RANGE);
            OfferModel offerModel = JSONObject.parseObject(jsonObject.toJSONString(), OfferModel.class);
            Long order = masterRedisTemplate.opsForHash().increment(autoOfferNameKey, offerModel.getPartner(), 1);
            // 此时需要赋值offerName依次添加 01 02
            String offerName = offerModel.getPartner() + "-" + dateStr + ZooConstant.AUTO_TEST + order;
            offerModel.setOfferName(offerName);
            boolean updateTimeRange = time != null && time.size() > 0;
            if (updateTimeRange) {
                offerModel.setTimeRange(time.get(0) + "," + time.get(1));
            }
            saveOfferAutoTest(offerModel, groupVo);
        }
    }

    @Override
    public JSONObject getAutoTestOfferList(List<String> countryList, List<String> operatorList, List<String> partnerList, List<Integer> testStatusList, Integer page, Integer limit, String begin, String end) {
        List<OfferModel> modelList = getOfferModelList(countryList, operatorList, partnerList, testStatusList, page, limit, begin, end);
        Map<String, List<OfferModel>> collect = modelList.stream().collect(Collectors.groupingBy(item -> item.getPartner() + "-" + DateUtil.getTimeByTimeZone(item.getCreateTime(), ZooConstant.GMT_8).replaceAll("-", "").substring(2)));
        List<JSONObject> list = new ArrayList<>();
        if (collect != null && collect.size() > 0) {
            List<JSONObject> finalList = list;
            collect.forEach((key, value) -> {
                JSONObject json = new JSONObject();
                json.put("partnerDate", key);
                long testing = value.stream().filter(e -> e.getTestStatus().equals(NumberEnum.ZERO.getNum())).count();
                long untested = value.stream().filter(e -> e.getTestStatus().equals(NumberEnum.ONE.getNum())).count();
                long tested = value.stream().filter(e -> e.getTestStatus().equals(NumberEnum.TWO.getNum())).count();
                JSONObject statusJson = new JSONObject();
                statusJson.put("testing", testing);
                statusJson.put("untested", untested);
                statusJson.put("tested", tested);
                json.put("testStatus", statusJson);
                // 给 offer 赋值 Tags
                List<JSONObject> offerJsonList = new ArrayList<>();
                for (OfferModel model : value) {
                    JSONObject offerJson = JSON.parseObject(JSONObject.toJSONString(model));
                    TagsOptionVO tagsOptionVO = new TagsOptionVO();
                    List<OptionVO> stackTags = new ArrayList<>();
                    List<OptionVO> groupTags = new ArrayList<>();
                    List<OptionVO> othersTags = new ArrayList<>();
                    List<TagModel> tagModels = tagRepo.findQuery(model.getIdentification());
                    if (tagModels != null && tagModels.size() > 0) {
                        for (TagModel tag : tagModels) {
                            OptionVO optionVO = new OptionVO();
                            optionVO.setIdentification(tag.getIdentification());
                            optionVO.setLabel(tag.getTagName());
                            optionVO.setValue(tag.getIdentification());
                            if (tag.getTagType() == ZooConstant.TAG_TYPE_STACK) {
                                stackTags.add(optionVO);
                            } else if (tag.getTagType() == ZooConstant.TAG_TYPE_GROUP) {
                                groupTags.add(optionVO);
                            } else {
                                othersTags.add(optionVO);
                            }
                        }
                        tagsOptionVO.setStack(stackTags);
                        tagsOptionVO.setGroup(groupTags);
                        tagsOptionVO.setOthers(othersTags);
                    }
                    offerJson.put(ZooConstant.TAGS, tagsOptionVO);
                    if (!StringUtils.isEmpty(model.getTimeRange())) {
                        String[] timeRange = model.getTimeRange().split(",");
                        offerJson.put(ZooConstant.TIME_RANGE, timeRange);
                    }
                    offerJsonList.add(offerJson);
                }
                json.put("offerModelList", offerJsonList);
                finalList.add(json);
            });
        }
        JSONObject resultJson = new JSONObject();
        // 分页下标
        int beginIndex = limit * (page - 1);
        int endIndex = (beginIndex + limit) > list.size() ? list.size() : (beginIndex + limit);
        list = list.subList(beginIndex, endIndex);
        resultJson.put("list", list);
        resultJson.put("total", list.size());
        return resultJson;
    }

    /**
     * 查找测试offer集合
     * @param countryList
     * @param operatorList
     * @param partnerList
     * @param testStatusList
     * @param page
     * @param limit
     * @param begin
     * @param end
     * @return java.util.List<com.starp.zoo.entity.zoo.OfferModel>
     * @author Curry
     * @date 2023/5/23
     */
    private List<OfferModel> getOfferModelList(List<String> countryList, List<String> operatorList, List<String> partnerList, List<Integer> testStatusList, Integer page, Integer limit, String begin, String end) {
        long beginTime = DateUtil.timeStrToLong(begin, ZooConstant.GMT_8);
        long endTime = DateUtil.timeStrToLong(end, ZooConstant.GMT_8);
        Date beginDate = DateUtil.getDateByTimeZone(beginTime, ZooConstant.GMT_0);
        Date endDate = DateUtil.getDateByTimeZone(endTime, ZooConstant.GMT_0);
        Specification<OfferModel> specification = (root, cq, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(cb.between(root.get("createTime"), beginDate, endDate));
            if (countryList != null && countryList.size() > 0) {
                CriteriaBuilder.In<String> in = cb.in(root.get("country"));
                for (String app : countryList) {
                    in.value(app);
                }
                predicateList.add(in);
            }
            if (operatorList != null && operatorList.size() > 0) {
                CriteriaBuilder.In<String> in = cb.in(root.get("operator"));
                for (String app : operatorList) {
                    in.value(app);
                }
                predicateList.add(in);
            }
            if (partnerList != null && partnerList.size() > 0) {
                CriteriaBuilder.In<String> in = cb.in(root.get("partner"));
                for (String app : partnerList) {
                    in.value(app);
                }
                predicateList.add(in);
            }
            if (testStatusList != null && testStatusList.size() > 0) {
                CriteriaBuilder.In<Integer> in = cb.in(root.get("testStatus"));
                for (Integer app : testStatusList) {
                    in.value(app);
                }
                predicateList.add(in);
            } else {
                predicateList.add(cb.isNotNull(root.get("testStatus")));
            }
            cq.where(cb.and(predicateList.toArray(new Predicate[predicateList.size()])));
            return cq.getRestriction();
        };
        return offerRepo.findAll(specification);
    }

    private void saveOfferAutoTest(OfferModel offerModel, OptionVO groupVo) {
        OfferModel saveModel = offerRepo.save(offerModel);
        String offerId = saveModel.getIdentification();
        // 测试offer主键redis集合
        masterRedisTemplate.opsForList().leftPush(CacheNameSpace.ZOO_AUTO_TEST_OFFER_ID, offerId);
        offerTagRepo.save(new OfferTagModel(offerId, groupVo.getIdentification()));
        updateEpmCurrentRedis(saveModel);
        String offerJson = JSONObject.toJSONString(saveModel);
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerId, offerJson);
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER_ASSIGN, offerId, offerJson);
    }

    /**
     * 删除超时key
     *
     * @param key
     */
    @Override
    @Async
    public void deleteTimoutKey(String key) {
        String[] strArr = key.split(CacheNameSpace.COLON);
        if (strArr.length == NumberEnum.FIVE.getNum() && !StringUtils.isEmpty(strArr[NumberEnum.FOUR.getNum()])) {
            String time = strArr[4];
            Date redisDate = DateUtil.formatDayTime(time);
            Date yesterday = DateUtil.formatDayTime(DateUtil.yesterday());
            boolean deleteKey = DateUtil.isBefore(redisDate, yesterday);
            if (deleteKey) {
                masterRedisTemplate.delete(key);
            }
        }
    }

    @Override
    @Async
    public void autoRunTime() {
        String sql = "select a1.offer_id,a2.category_id from (select t1.tag_id,t1.offer_id from t_offer_tag t1 LEFT  JOIN t_tag t2 on t1.tag_id = t2.identification and t2.tag_type = 2 and t1.offer_id in(SELECT identification FROM `t_offer` where status = 1 and  auto_status = 0 and timeRange is not null) where t2.identification is not null) as a1 LEFT JOIN (select category_id,tag_id from t_category_tag where tag_id in (select t1.identification as tagId from t_tag t1 left join t_offer_tag t2 on t1.identification=t2.tag_id where t1.tag_type=2)GROUP BY category_id,tag_id)as a2 on a1.tag_id = a2.tag_id where a1.offer_id is not null and a2.category_id is not null GROUP BY a1.offer_id,a2.category_id";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        List<Object[]> resultList = nativeQuery.getResultList();
        Map<String, List<String>> offerAppList = generateOfferAppList(resultList);
        List<OfferModel> offerModels = generateOfferMode(offerAppList);
        if (offerModels != null && offerModels.size() > 0) {
            for (OfferModel offerModel : offerModels) {
                List<String> appIds = offerAppList.get(offerModel.getIdentification());
                JSONObject params = new JSONObject();
                params.put("appId", appIds);
                params.put("offer", offerModel);
                producer.sendToQueueOfferRunTime(new BaseSqsMessage(new Integer[]{ZooConstant.QUEUE_OFFER_RUNTIME},
                        ZooConstant.QUEUE_OFFER_RUNTIME_MODEL, JSON.toJSONString(params)));
            }
        }
    }

    @Override
    public void handleOfferRunTime(String msgBody){
        JSONObject params = JSONObject.parseObject(msgBody);
        List<String> appIds = JSONArray.parseArray(params.getString("appId"),String.class);
        OfferModel offerModel = JSONObject.parseObject(params.getString("offer"),OfferModel.class);
        updateOfferRedisList(appIds, offerModel, "autoRunTime");
    }

    @Override
    @Async
    public void syncTestOffer() {
        Map<Object,Object> testInfo = cluster3RedisTemplate.opsForHash().entries(ZooConstant.ZOO_TEST_OFFER_INFO);
        if(testInfo != null){
            Set<Map.Entry<Object, Object>> testOfferInfoSet = testInfo.entrySet();
            testOfferInfoSet.stream().forEach(a->{
                OfferModel offerModel = JSONObject.parseObject(String.valueOf(a.getValue()),OfferModel.class);
                masterRedisTemplate.opsForHash().put(ZooConstant.ZOO_TEST_OFFER_DATA,offerModel.getIdentification(),JSON.toJSONString(offerModel));
            });
        }

    }



    @Override
    public void updateOfferShortCode() {
        List<OfferModel> offerModelList = offerRepo.findShortCodeOffer();
        if (offerModelList != null && offerModelList.size() > 0) {
            for (OfferModel offerModel : offerModelList) {
                String partner = offerModel.getPartner();
                String operator = offerModel.getOperator();
                String shortCode = offerModel.getPayShortCode();
                String keyword = offerModel.getPayKeyword();
                ShortCodeModel shortCodeModel = shortCodeRepo.findFirstByPartnerAndOperatorAndShortCodeAndCommand(partner, operator, shortCode, keyword);
                if (shortCodeModel != null) {
                    shortCodeRepo.updateShortCodeByOffer(shortCodeModel.getIdentification());
                }
            }
        }
    }

    /**
     * mo转化告警
     */
    @Override
    @Async
    public void checkOfferMoCr() {
        List<OfferModel> offerModelList = offerRepo.findByMailCapStatusAndMailMoCrStatus(1, 1);
        if (offerModelList != null && offerModelList.size() > 0) {
            for (OfferModel offerModel : offerModelList) {
                double crThreshold = offerModel.getMoCrThreshold();
                int clickThreshold = offerModel.getMoClickThreshold();
                String hour = DateUtil.beforeHour().split(" ")[0] + "-" + DateUtil.beforeHour().split(" ")[1];
                List<Object> epmValue = epmInfoRepo.findOfferEpmHour(hour, offerModel.getIdentification());
                int clickNum = 0;
                int transNum = 0;
                for (int i = 0; i < epmValue.size(); i++) {
                    Object[] arr = (Object[]) epmValue.get(i);
                    if (arr[0] != null && arr[1] != null) {
                        clickNum = Integer.valueOf(arr[0].toString());
                        transNum = Integer.valueOf(arr[1].toString());
                    }
                }
                if (clickNum > clickThreshold) {
                    BigDecimal redisThreshold = new BigDecimal(transNum).divide(new BigDecimal(clickNum), 2, RoundingMode.DOWN).multiply(new BigDecimal(NumberEnum.ONE_HUNDRED.getNum()));
                    int value = redisThreshold.compareTo(new BigDecimal(crThreshold));
                    if (value < 0) {
                        sendCrMail(clickNum, transNum, redisThreshold.doubleValue(), offerModel, hour);
                    }
                }
            }
        }
    }

    /**
     * 更新 offer initEpm 值
     *
     * @param arpuAvgVoList
     */
    @Override
    public void updateInitEpm(List<JSONObject> arpuAvgVoList) {
        for (JSONObject jsonObject : arpuAvgVoList) {
            List<String> offerIds = offerRepo.findAllByPayShortCodeAndPayKeyword(jsonObject.getString("shortCode"), jsonObject.getString("payPartner"));
            if (offerIds != null && offerIds.size() > 0) {
                for (String id : offerIds) {
                    offerRepo.updateInitEpm(jsonObject.getDouble("arpuAvg"), id);
                    // 从redis获取数据,尽量不查数据库
                    OfferModel offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, id)), OfferModel.class);
                    if (offerModel != null) {
                        // 同步Redis
                        offerModel.setInitEpm(jsonObject.getDouble("arpuAvg"));
                        masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification());
                        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification(), JSON.toJSONString(offerModel));
                    }
                }
            }
        }
    }

    /**
     * 获取智能栈数据
     *
     * @param offerNames
     * @return
     */
    @Override
    public JSONObject getAutoStackList(JSONArray offerNames) {
        JSONObject resultJson = new JSONObject();
        List<String> offerIds = new ArrayList<>();
        // 从Redis获取历史添加的offer智能栈的所有主键集合，用于首次进入页面展示旧数据
        Object offerIdObj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_INIT_OFFERIDS);
        if (offerIdObj != null) {
            offerIds = (List<String>) JSON.parse(String.valueOf(offerIdObj));
        }
        if (offerNames != null && offerNames.size() > 0) {
            offerIds.addAll(offerRepo.findByNameList(offerNames));
            offerIds = offerIds.stream().distinct().collect(Collectors.toList());
        }
        if (offerIds == null || offerIds.size() == 0) {
            resultJson.put("list", null);
            resultJson.put("operators", null);
            return resultJson;
        }
        if (offerNames != null && offerNames.size() > 0) {
            offerIds.addAll(offerRepo.findByNameList(offerNames));
            offerIds = offerIds.stream().distinct().collect(Collectors.toList());
        }
        // 将offerIds存储至Redis，以便于前端不选择offer直接加载上次添加的数据显示，防止刷新页面上次数据丢夫
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_INIT_OFFERIDS, JSON.toJSONString(offerIds));
        List<JSONObject> offerJsonList = new ArrayList<>();
        List<JSONObject> closeJsonList = new ArrayList<>();
        for (String offerId : offerIds) {
            OfferModel offerModel = getOfferModel(offerId);
            if (offerModel == null) {
                continue;
            }
            JSONObject offerJson = new JSONObject();
            offerJson.put("id", offerModel.getIdentification());
            offerJson.put("offerName", offerModel.getOfferName());
            offerJson.put("status", offerModel.getStatus());
            offerJson.put("operator", offerModel.getOperator());
            offerJson.put("partner", offerModel.getPartner());
            // 添加到offer关闭状态集合
            if (offerModel.getStatus() == 0) {
                offerJson.put("closeTime", DateUtil.formatyyyyMMddHHmmss(offerModel.getCloseTime()));
                closeJsonList.add(offerJson);
            } else {
                // 获取开启offer的cap，并计算开启offer的MO数
                offerJson.put("cap", offerModel.getCap());
                offerJsonList.add(offerJson);
            }
        }
        closeJsonList = closeJsonList.stream().sorted(Comparator.comparing(e -> DateUtil.formatTime(e.getString("closeTime")), Comparator.reverseOrder())).collect(Collectors.toList());
        offerJsonList = offerJsonList.stream().sorted(Comparator.comparing(e -> e.getInteger("cap"), Comparator.reverseOrder())).collect(Collectors.toList());
        // 将开启offer信息的集合存储至Redis，便于智能栈配置获取使用
        Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_OFFER_LIST);
        if (obj != null) {
            // 遍历新旧集合对比有tagName标记的继续添加此标签表示已被分配, 然后即可存入新集合用于更新开启 offer 集合中的 cap
            List<JSONObject> oldOfferList = (List<JSONObject>) JSON.parse(String.valueOf(obj));
            for (JSONObject oldJson : oldOfferList) {
                for (JSONObject newJson : offerJsonList) {
                    if (oldJson.getString("id").equals(newJson.getString("id")) && !StringUtils.isEmpty(oldJson.getString("tagName"))) {
                        newJson.put("tagName", Constants.ASSIGNED);
                    }
                }
            }
        }
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_OFFER_LIST, String.valueOf(offerJsonList));
        offerJsonList.addAll(closeJsonList);
        Map<Object, Map<Object, List<JSONObject>>> jsonMap = offerJsonList.stream().collect(Collectors.groupingBy(item -> item.get("operator"), Collectors.groupingBy(e -> e.get("partner"))));
        List<Map<Object, Map<Object, List<JSONObject>>>> offerInfoList = new ArrayList<>();
        offerInfoList.add(jsonMap);
        resultJson.put("list", offerInfoList);
        resultJson.put("operators", jsonMap.keySet());
        return resultJson;
    }

    @Override
    public void deleteAutoStackOffer(String offerId) {
        // 将智能栈列表所需删除的offerId存储至Redis
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, offerId, "智能栈删除ids");
    }

    @Override
    public JSONObject getAutoStackConfigInfo(List<String> appNames, List<JSONObject> opeAndOfferList) {
        JSONObject filterJson = filterOfferAndApp(appNames, opeAndOfferList);
        if (filterJson.containsKey(Constants.LIST)) {
            return filterJson;
        }
        List<JSONObject> offerList = (List<JSONObject>) filterJson.get("offerList");
        List<String> filterAppNameList = (List<String>) filterJson.get("filterAppNameList");
        List<AutoStackInfoVO> appGroupList = (List<AutoStackInfoVO>) filterJson.get("appGroupList");
        List<String> ids = filterJson.getJSONArray("ids").toJavaList(String.class);
        int tagNameNum = 0;
        for (String appName : filterAppNameList) {
            tagNameNum++;
            // 删除offer原任务组并将其放入测试任务组
            deleteOfferGroup(ids);
            putOfferTestGroup(ids, appName, offerList, tagNameNum);
            // 添加完毕后再次筛选数据
            filterJson = filterOfferAndApp(filterAppNameList, opeAndOfferList);
            if (filterJson.containsKey(Constants.LIST)) {
                return filterJson;
            }
            offerList = (List<JSONObject>) filterJson.get("offerList");
            filterAppNameList = (List<String>) filterJson.get("filterAppNameList");
            appGroupList = (List<AutoStackInfoVO>) filterJson.get("appGroupList");
            ids = filterJson.getJSONArray("ids").toJavaList(String.class);
        }
        JSONObject resultJson = new JSONObject();
        resultJson.put("list", appGroupList);
        return resultJson;
    }

    @Override
    public void refreshOfferMo(String appName) throws Exception {
        String appId = appInfoRepo.findFirstByAppName(appName).getIdentification();
        List<AutoStackInfoVO> autoStackInfoVOS = JSON.parseArray(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId)), AutoStackInfoVO.class);
        if (autoStackInfoVOS != null && autoStackInfoVOS.size() > 0) {
            for (AutoStackInfoVO autoStackInfoVO : autoStackInfoVOS) {
                if (autoStackInfoVO.getOfferId().equals("合计")) {
                    continue;
                }
                int moNum = 0;
                String moKey = ZooConstant.ZOO_PAY_MO_POSTBACK + ZooConstant.COLON + autoStackInfoVO.getOfferId() + ZooConstant.COLON + DateUtil.formatDay(new Date());
                Object obj = cluster3RedisTemplate.opsForHash().get(moKey, CacheNameSpace.MO_TRANS);
                if (obj != null) {
                    moNum = Integer.valueOf(String.valueOf(obj));
                }
                autoStackInfoVO.setNowMoNum(moNum);
                autoStackInfoVO.setNewMoNum(moNum - autoStackInfoVO.getInitMoNum());
            }
            autoStackInfoVOS = calculateSumMo(autoStackInfoVOS, appName);
            // 更新Redis中该App对应的AutoStackInfoVO的moNum信息
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId, JSONObject.toJSONString(autoStackInfoVOS));
        }
    }

    @Override
    public boolean freeOfferInApp(String appName) {
        String appId = appInfoRepo.findFirstByAppName(appName).getIdentification();
        List<AutoStackInfoVO> autoStackInfoVOS = JSON.parseArray(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId)), AutoStackInfoVO.class);
        List<String> offerIds = new ArrayList<>();
        for (AutoStackInfoVO autoStackInfoVO : autoStackInfoVOS) {
            String offerId = autoStackInfoVO.getOfferId();
            if (offerId.equals("合计")) {
                continue;
            }
            offerIds.add(offerId);
        }
        // 在未恢复 offer 与原有任务组关系之前，记录测试任务组 ID
        String testGroupId = offerTagRepo.findOfferGroupId(autoStackInfoVOS.get(0).getOfferId()).get(0);
        if (!recoverOfferGroup(offerIds)) {
            return false;
        }
        try {
            updateAutoOpenOfferRemove(offerIds);
            // 删除智能栈配置要释放的APP信息
            masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId);
            // 恢复原有任务组与app关联关系，并删除测试任务组与app关联关系
            Object object = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.RECOVER_APP_GROUP_LIST, appId);
            if (object != null) {
                List<String> appGroupList = (List<String>) JSON.parse(String.valueOf(object));
                applicationService.resetAppTag(appId, appGroupList, false);
            }
            tagRepo.deleteById(testGroupId);
            masterRedisTemplate.opsForHash().delete(CacheNameSpace.RECOVER_APP_GROUP_LIST, appId);
        } catch (Exception e) {
            log.error("freeOffer redis ERROR:{},testId:{}", JSON.toJSONString(e), testGroupId);
            return false;
        }
        return true;
    }

    /**
     * 释放完 offer 需更新 offer 智能栈列表存储的开启offer信息, 删除 tagName 标签
     * @param offerIds
     * @return void
     * @author Curry
     * @date 2022/12/9
     */
    private void updateAutoOpenOfferRemove(List<String> offerIds) {
        List<JSONObject> oldOfferList = (List<JSONObject>) JSON.parse(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_OFFER_LIST)));
        oldOfferList.forEach(e -> {
            for (String id : offerIds) {
                if (e.getString("id").equals(id)) {
                    // 删除 offer 已被分配标签
                    e.remove("tagName");
                }
            }
        });
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_OFFER_LIST, String.valueOf(oldOfferList));
    }

    /**
     * 恢复数据库offer与原有任务组关联关系，并删除数据库与测试任务组关联关系
     * @param offerIds
     * @return
     */
    public boolean recoverOfferGroup(List<String> offerIds) {
        try {
            for (String offerId : offerIds) {
                List<OptionVO> otherList = new ArrayList<>();
                List<OptionVO> stackList = new ArrayList<>();
                List<OptionVO> groupList = new ArrayList<>();
                List<String> otherIds = offerTagRepo.findOfferOtherId(offerId);
                if (otherIds != null && otherIds.size() > 0) {
                    for (String otherId : otherIds) {
                        OptionVO vo = new OptionVO();
                        vo.setIdentification(otherId);
                        vo.setValue(otherId);
                        vo.setLabel(tagRepo.findByIdentification(otherId).getTagName());
                        otherList.add(vo);
                    }
                }
                String stackId = offerTagRepo.findOfferStackId(offerId);
                if (!StringUtils.isEmpty(stackId)) {
                    OptionVO vo = new OptionVO();
                    vo.setIdentification(stackId);
                    vo.setValue(stackId);
                    vo.setLabel(tagRepo.findByIdentification(stackId).getTagName());
                    stackList.add(vo);
                }
                Object obj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.RECOVER_OFFER_GROUP_LIST, offerId);
                if (obj != null) {
                    List<String> offerGroupList = (List<String>) JSON.parse(String.valueOf(obj));
                    for (String groupId : offerGroupList) {
                        OptionVO groupVo = new OptionVO();
                        groupVo.setIdentification(groupId);
                        groupVo.setValue(groupId);
                        groupVo.setLabel(tagRepo.findByIdentification(groupId).getTagName());
                        groupList.add(groupVo);
                    }
                }
                OfferModel offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId)), OfferModel.class);
                if (offerModel == null) {
                    offerModel = offerRepo.findByIdentification(offerId);
                }
                TagsOptionVO tagsOptionVO = new TagsOptionVO();
                tagsOptionVO.setOthers(otherList);
                tagsOptionVO.setGroup(groupList);
                tagsOptionVO.setStack(stackList);
                saveAllConfig(offerModel, tagsOptionVO);
                masterRedisTemplate.opsForHash().delete(CacheNameSpace.RECOVER_OFFER_GROUP_LIST, offerId);
            }
        } catch (Exception e) {
            // getMessage()异常信息打印为null，打印出异常堆栈信息
            log.error("freeOffer mysql ERROR:{}", JSON.toJSONString(e));
            return false;
        }
        return true;
    }

    @Override
    public List<String> getOldAutoStackOfferNames() {
        List<String> offerNameList = new ArrayList<>();
        Object object = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_INIT_OFFERIDS);
        if (object == null) {
            return offerNameList;
        }
        List<String> offerIdList = (List<String>) JSON.parse(String.valueOf(object));
        // 筛选掉Redis中存储要删除的智能栈ids中的offer
        offerIdList = offerIdList.stream().filter(e -> cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, e) == null).collect(Collectors.toList());
        for (String id : offerIdList) {
            OfferModel offerModel = getOfferModel(id);
            if (offerModel != null) {
                offerNameList.add(offerModel.getOfferName());
            }
        }
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_INIT_OFFERIDS, JSON.toJSONString(offerIdList));
        // 清空掉offer黑名单，不然后续再次添加已删除的offer会被过滤
        Map<Object, Object> deleteMap = cluster3RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE);
        for (Object obj : deleteMap.keySet()) {
            String key = String.valueOf(obj);
            if (!key.equals(CacheNameSpace.ZOO_AUTOSTACK_INIT_OFFERIDS) && !key.equals(CacheNameSpace.ZOO_AUTOSTACK_OFFER_LIST)) {
                masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, key);
            }
        }
        return offerNameList;
    }

    @Override
    public void deleteRedisAutoStackList() {
        Boolean flag = cluster3RedisTemplate.hasKey(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE);
        if (flag != null && flag) {
            masterRedisTemplate.delete(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE);
        }
    }

    /**
     * 筛选掉已经分配过的app和已被分配的offer
     * @param appNames
     * @param opeAndOfferList
     */
    private JSONObject filterOfferAndApp(List<String> appNames, List<JSONObject> opeAndOfferList) {
        JSONObject resultJson = new JSONObject();
        List<AutoStackInfoVO> appGroupList = new ArrayList<>();
        // 从Redis加载历史添加的智能栈配置测试信息，todo 即便智能栈列表那边清空了offer也不影响历史信息(历史信息只能通过释放按钮进行删除)，只是不能给新选取的app分配任务组和offer
        Map<Object, Object> appMap = cluster3RedisTemplate.opsForHash().entries(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG);
        for (Object obj : appMap.keySet()) {
            String appId = String.valueOf(obj);
            appGroupList.addAll(JSON.parseArray(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId)), AutoStackInfoVO.class));
        }
        resultJson.put(Constants.LIST, appGroupList);
        if (appNames == null || appNames.size() == 0) {
            return resultJson;
        }
        // 更新智能栈列表信息，否则中途关闭的offer，将会加载至智能栈配置
        List<String> oldAutoStackOfferNames = getOldAutoStackOfferNames();
        if (oldAutoStackOfferNames != null && oldAutoStackOfferNames.size() > 0) {
            getAutoStackList(JSONArray.parseArray(JSON.toJSONString(oldAutoStackOfferNames)));
        }
        List<String> filterAppNameList = new ArrayList<>();
        for (Object appName : appNames) {
            if (!appMap.keySet().contains(appInfoRepo.findByAppName(String.valueOf(appName)))) {
                filterAppNameList.add(String.valueOf(appName));
            }
        }
        // 从Redis获取开启offer信息，若没有开启offer信息就无法分配给所选app，直接返回appGroupList。不在进行后续创建新任务组分配给新app等操作
        List<JSONObject> offerList = checkUsableOffer();
        if (filterAppNameList.size() == 0 || offerList == null || offerList.size() == 0) {
            return resultJson;
        }
        // 要加入智能栈配置的 offer 主键集合
        List<String> ids = new ArrayList<>();
        if (opeAndOfferList != null && opeAndOfferList.size() > 0) {
            // 运营商和数量限制模式
            ids = selectOfferForOpeAndNum(offerList, opeAndOfferList);
        } else {
            // 正常模式
            ids = selectTwoOfferMo(offerList);
        }
        if (ids == null || ids.size() == 0) {
            return resultJson;
        }
        resultJson.remove(Constants.LIST);
        resultJson.put("offerList", offerList);
        resultJson.put("appGroupList", appGroupList);
        resultJson.put("filterAppNameList", filterAppNameList);
        resultJson.put("ids", ids);
        return resultJson;
    }

    /**
     * 供智能栈配置可用 offer 集合
     * @return java.util.List<com.alibaba.fastjson.JSONObject>
     * @author Curry
     * @date 2022/12/12
     */
    public List<JSONObject> checkUsableOffer () {
        List<JSONObject> offerList;
        Object offersObj = cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_OFFER_LIST);
        if (offersObj != null) {
            offerList = JSON.parseArray(String.valueOf(offersObj)).toJavaList(JSONObject.class);
            offerList = offerList.stream().filter(e -> !StringUtils.hasText(e.getString("tagName"))).collect(Collectors.toList());
            return offerList;
        }
        return null;
    }

    /**
     * 每个运营商中选取两条mo最少的两条，上游不重复
     * @param offerList
     * @return
     */
    public List<String> selectTwoOfferMo(List<JSONObject> offerList) {
        setMoNumOffers(offerList);
        Map<Object, List<JSONObject>> jsonMap = offerList.stream().sorted(Comparator.comparing(e -> e.getInteger("moNum"))).collect(Collectors.groupingBy(e -> e.getString("operator")));
        List<String> ids = new ArrayList<>();
        for (List<JSONObject> valueList : jsonMap.values()) {
            Map<String, String> partnerMap = new HashMap<>(6);
            List<JSONObject> list = valueList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> e.getString("partner")))), ArrayList::new));
            for (JSONObject item : valueList) {
                if (partnerMap.get(item.getString("partner")) != null) {
                    // 如果只有一个上游，因为上游不能重复则直接结束循环
                    if (list != null && list.size() == 1) {
                        ids.addAll(partnerMap.values());
                        break;
                    }
                    continue;
                }
                partnerMap.put(item.getString("partner"), item.getString("id"));
                // 只有一个条记录代表只有一个上游，或者当前运营商已经选够两条offer则break
                if (valueList.size() == 1 || partnerMap.size() >= 2) {
                    ids.addAll(partnerMap.values());
                    break;
                }
            }
        }
        return ids;
    }

    /**
     * 在智能栈配置 offer 符合条件中再次进行运营商和数量限制
     * @param offerList
     * @param opeAndOfferList
     * @return java.util.List<java.lang.String>
     * @author Curry
     * @date 2022/12/14
     */
    public List<String> selectOfferForOpeAndNum(List<JSONObject> offerList, List<JSONObject> opeAndOfferList) {
        int num = 0;
        // 先进行 MO 赋值，用于后续筛选
        setMoNumOffers(offerList);
        Map<Object, List<JSONObject>> jsonMap = offerList.stream().collect(Collectors.groupingBy(e -> e.getString("operator")));
        List<String> ids = new ArrayList<>();
        for (JSONObject json : opeAndOfferList) {
            String operator = json.getString("operator");
            int operatorTotal = json.getInteger("offerNum");
            for (Map.Entry<Object, List<JSONObject>> entry : jsonMap.entrySet()) {
                // 有符合的运营商数据
                if (entry.getKey().equals(operator)) {
                    List<JSONObject> values = entry.getValue();
                    if (values == null) {
                        break;
                    }
                    Map<String, List<JSONObject>> collect = values.stream().sorted(Comparator.comparing(e -> e.getInteger("moNum"))).collect(Collectors.groupingBy(e -> e.getString("partner")));
                    if (collect != null && operatorTotal != 0) {
                        // 向下取整
                        num = operatorTotal / collect.size();
                        for (Map.Entry<String, List<JSONObject>> partnerOffer : collect.entrySet()) {
                            List<JSONObject> filterOffers = partnerOffer.getValue();
                            for (int i = 0; i < partnerOffer.getValue().size(); i++) {
                                // 每个上游取够数量结束筛选
                                if (i >= num) {
                                    break;
                                }
                                ids.add(filterOffers.get(i).getString("id"));
                            }
                        }
                    }
                }
            }
        }
        return ids;
    }

    /**
     * 给智能栈中可用 offer 集合中赋予 moNum 标签，用于选取两条mo最少的两条
     * @param offerList
     * @return void
     * @author Curry
     * @date 2022/12/12
     */
    public void setMoNumOffers(List<JSONObject> offerList) {
        for (JSONObject jsonObject : offerList) {
            int moNum = 0;
            String moKey = ZooConstant.ZOO_PAY_MO_POSTBACK + ZooConstant.COLON + jsonObject.get("id") + ZooConstant.COLON + DateUtil.formatDay(new Date());
            Object obj = cluster3RedisTemplate.opsForHash().get(moKey, CacheNameSpace.MO_TRANS);
            if (obj != null) {
                moNum = Integer.valueOf(String.valueOf(obj));
            }
            jsonObject.put("moNum", moNum);
        }
    }

    /**
     * 解除该offer原有的所有任务组关联关系，即删除t_offer_tag中以该offerId并且tag_type为2的所有数据
     *
     * @param ids
     */
    void deleteOfferGroup(List<String> ids) {
        List<JSONObject> offerGroupIds = new ArrayList<>();
        ids.forEach(offerId -> {
            List<String> otherIds = offerTagRepo.findOfferOtherId(offerId);
            List<OptionVO> otherList = new ArrayList<>();
            List<OptionVO> stackList = new ArrayList<>();
            List<OptionVO> groupList = new ArrayList<>();
            if (otherIds != null && otherIds.size() > 0) {
                for (String otherId : otherIds) {
                    OptionVO vo = new OptionVO();
                    vo.setIdentification(otherId);
                    vo.setValue(otherId);
                    vo.setLabel(tagRepo.findByIdentification(otherId).getTagName());
                    otherList.add(vo);
                }
            }
            String stackId = offerTagRepo.findOfferStackId(offerId);
            if (!StringUtils.isEmpty(stackId)) {
                OptionVO vo = new OptionVO();
                vo.setIdentification(stackId);
                vo.setValue(stackId);
                vo.setLabel(tagRepo.findByIdentification(stackId).getTagName());
                stackList.add(vo);
            }
            List<String> groupIds = offerTagRepo.findOfferGroupId(offerId);
            if (groupIds != null && groupIds.size() > 0) {
                for (String groupId : groupIds) {
                    JSONObject json = new JSONObject();
                    json.put("offerId", offerId);
                    json.put("groupId", groupId);
                    offerGroupIds.add(json);
                }
                // todo 将删除的offer与原有任务组关联关系保存至Redis，便于后续释放按钮recover操作;
                masterRedisTemplate.opsForHash().put(CacheNameSpace.RECOVER_OFFER_GROUP_LIST, offerId, JSON.toJSONString(groupIds));
            }
            // 删除原有任务组与offer关联redis关系，并删除数据库
            OfferModel offerModel = getOfferModel(offerId);
            TagsOptionVO tagsOptionVO = new TagsOptionVO();
            tagsOptionVO.setOthers(otherList);
            tagsOptionVO.setGroup(groupList);
            tagsOptionVO.setStack(stackList);
            saveAllConfig(offerModel, tagsOptionVO);
        });
    }

    /**
     * 放入测试任务组并分配给APP
     *
     * @param ids
     * @param appName
     * @param offerList
     * @param tagNameNum
     */
    private void putOfferTestGroup(List<String> ids, String appName, List<JSONObject> offerList, Integer tagNameNum) {
        String appId = appInfoRepo.findByAppName(appName);
        TagModel tagModel = new TagModel(DateUtil.formatDayMinString(new Date()) + tagNameNum, 2);
        try {
            tagModel = tagService.saveTag(tagModel, ids);
        } catch (BadRequestException e) {
            log.error("SAVE TAG MODEL ERROR:{}", e.getMessage());
        }
        updateAutoOpenOfferPut(ids);
        List<String> tagIds = new ArrayList<>();
        tagIds.add(tagModel.getIdentification());
        List<CategoryTagModel> modelList = categoryTagRepo.findAllByTypeAndCategoryId(1, appId);
        if (modelList != null && modelList.size() > 0) {
            List<String> oldGroups = new ArrayList<>();
            for (CategoryTagModel categoryTagModel : modelList) {
                oldGroups.add(categoryTagModel.getTagId());
            }
            masterRedisTemplate.opsForHash().put(CacheNameSpace.RECOVER_APP_GROUP_LIST, appId, JSON.toJSONString(oldGroups));
        }
        // 调用重置tag方法，删除app原来所有任务组，将测试任务组加入进行。方法会自动同步offer与app的redis关联关系
        applicationService.resetAppTag(appId, tagIds, false);
        List<AutoStackInfoVO> autoStackInfoVOS = calculateAutoStackConfigInfo(ids, offerList, appName, tagModel);
        // 将智能栈配置信息保存至Redis,todo 只有通过释放按钮才能解删除Redis中智能栈配置历史信息并解除offer与app的关联关系
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_APP_CONFIG, appId, JSONObject.toJSONString(autoStackInfoVOS));
    }

    /**
     * 添加完 offer 需更新 offer 智能栈列表存储的开启offer信息, 标记上 tagName 标签
     * @param ids
     * @return void
     * @author Curry
     * @date 2022/12/12
     */
    private void updateAutoOpenOfferPut(List<String> ids) {
        List<JSONObject> oldOfferList = (List<JSONObject>) JSON.parse(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_OFFER_LIST)));
        oldOfferList.forEach(e -> {
            for (String id : ids) {
                if (e.getString("id").equals(id)) {
                    e.put("tagName", Constants.ASSIGNED);
                }
            }
        });
        // 把已经放入测试任务组的offer添加上tagName标记，刷新开启状态offer集合的Redis
        masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_AUTOSTACK_OFFERIDS_DELETE, CacheNameSpace.ZOO_AUTOSTACK_OFFER_LIST, String.valueOf(oldOfferList));
    }


    /**
     * 计算智能栈配置信息
     * @param ids          加入智能栈配置中的offerId
     * @param offerList    需要取集合中的 moNum
     * @param appName
     * @param tagModel
     * @return
     */
    private List<AutoStackInfoVO> calculateAutoStackConfigInfo(List<String> ids, List<JSONObject> offerList, String appName, TagModel tagModel) {
        ApplicationModel applicationModel = appInfoRepo.findFirstByAppName(appName);
        List<AutoStackInfoVO> autoStackInfoVOS = new ArrayList<>();
        for (String offerId : ids) {
            AutoStackInfoVO autoStackInfoVO = new AutoStackInfoVO();
            OfferModel offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId)), OfferModel.class);
            if (offerModel == null) {
                offerModel = offerRepo.findByIdentification(offerId);
            }
            autoStackInfoVO.setAppId(appName);
            autoStackInfoVO.setAppName(applicationModel.getDescription());
            autoStackInfoVO.setOfferName(offerModel.getOfferName());
            autoStackInfoVO.setTestGroup(tagModel.getTagName());
            autoStackInfoVO.setOperator(offerModel.getOperator());
            autoStackInfoVO.setNewMoNum(0);
            autoStackInfoVO.setOfferId(offerId);
            autoStackInfoVO.setCreateTime(DateUtil.formatDay(new Date()));
            int initMoNum = 0;
            // 遍历offerList赋予对应需要加入智能栈配置的offer的mo数值, autoStackInfoVOS 将存入智能栈配置中
            for (JSONObject jsonObject : offerList) {
                if (offerModel.getOfferName().equals(jsonObject.getString("offerName"))) {
                    initMoNum = jsonObject.getInteger("moNum");
                }
                autoStackInfoVO.setInitMoNum(initMoNum);
                autoStackInfoVO.setNowMoNum(initMoNum);
            }
            autoStackInfoVOS.add(autoStackInfoVO);
        }
        return calculateSumMo(autoStackInfoVOS, appName);
    }

    /**
     * 计算总和值
     * @param autoStackInfoVOS
     * @param appName
     * @return java.util.List<com.starp.zoo.vo.AutoStackInfoVO>
     * @author Curry
     * @date 2022/12/13
     */
    private List<AutoStackInfoVO> calculateSumMo(List<AutoStackInfoVO> autoStackInfoVOS, String appName) {
        ApplicationModel applicationModel = appInfoRepo.findFirstByAppName(appName);
        String description = applicationModel.getDescription();
        // 计算合计数值显示
        int sumInitMo = 0;
        int sumNowMo = 0;
        int sumNewtMo = 0;
        // 排序否则手动加入新offer会可能出现合并不了单元格
        autoStackInfoVOS = autoStackInfoVOS.stream().sorted(Comparator.comparing(e -> e.getOperator())).collect(Collectors.toList());
        for (AutoStackInfoVO autoStackInfoVO : autoStackInfoVOS) {
            if (autoStackInfoVO.getOperator().equals("合计")) {
                continue;
            }
            sumInitMo += autoStackInfoVO.getInitMoNum();
            sumNowMo += autoStackInfoVO.getNowMoNum();
            sumNewtMo += autoStackInfoVO.getNewMoNum();
        }
        boolean flag = false;
        for (AutoStackInfoVO autoStackInfoVO : autoStackInfoVOS) {
            if (autoStackInfoVO.getOperator().equals("合计")) {
                flag = true;
                break;
            }
        }
        if (flag) {
            for (AutoStackInfoVO autoStackInfoVO : autoStackInfoVOS) {
                if (autoStackInfoVO.getOperator().equals("合计")) {
                    autoStackInfoVO.setAppId(appName);
                    autoStackInfoVO.setAppName(description);
                    autoStackInfoVO.setTestGroup("合计");
                    autoStackInfoVO.setOfferName("合计");
                    autoStackInfoVO.setOfferId("合计");
                    autoStackInfoVO.setOperator("合计");
                    autoStackInfoVO.setInitMoNum(sumInitMo);
                    autoStackInfoVO.setNowMoNum(sumNowMo);
                    autoStackInfoVO.setNewMoNum(sumNewtMo);
                    break;
                }
            }
        } else {
            AutoStackInfoVO autoStackInfoVO = new AutoStackInfoVO();
            autoStackInfoVO.setAppId(appName);
            autoStackInfoVO.setAppName(description);
            autoStackInfoVO.setTestGroup("合计");
            autoStackInfoVO.setOfferName("合计");
            autoStackInfoVO.setOfferId("合计");
            autoStackInfoVO.setOperator("合计");
            autoStackInfoVO.setInitMoNum(sumInitMo);
            autoStackInfoVO.setNowMoNum(sumNowMo);
            autoStackInfoVO.setNewMoNum(sumNewtMo);
            autoStackInfoVOS.add(autoStackInfoVO);
        }
        return autoStackInfoVOS;
    }

    /**
     * 解除offer与任务组关联关系，需要同步offer与任务组的 Redis: ZOO_OFFER_ASSIGN, ZOO_OFFER_ASSIGN_FILTER, AFF_EPM_LIST
     * (废弃)
     * @param ids
     * @param appId
     */
    private void deleteOfferGroupRedis(List<String> ids, String appId) {
        for (String offerId : ids) {
            String operator = offerRepo.queryCarrier(offerId);
            String assignKey = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON + ZooConstant.APP + CacheNameSpace.COLON + appId;
            String assignFilterKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator;
            String epmListKey = CacheNameSpace.AFF_EPM_LIST + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + operator + CacheNameSpace.COLON + CacheNameSpace.LIST;
            Boolean existAssignKey = cluster3RedisTemplate.opsForHash().hasKey(assignKey, offerId);
            Boolean existAssignFilterKey = cluster3RedisTemplate.opsForHash().hasKey(assignFilterKey, offerId);
            Boolean existEpmList = cluster3RedisTemplate.hasKey(epmListKey);
            if (existEpmList != null && existEpmList) {
                masterRedisTemplate.opsForList().remove(epmListKey, 0, offerId);
            }
            if (existAssignKey != null && existAssignKey) {
                masterRedisTemplate.opsForHash().delete(assignKey, offerId);
            }
            if (existAssignFilterKey != null && existAssignFilterKey) {
                masterRedisTemplate.opsForHash().delete(assignFilterKey, offerId);
            }
        }
    }


    /**
     * 发送MO cr告警邮件
     *
     * @param clickNum
     * @param transNum
     * @param redisThreshold
     * @param offerModel
     */
    private void sendCrMail(int clickNum, int transNum, double redisThreshold, OfferModel offerModel, String hour) {
        try {
            Map<String, Object> content = new HashMap<>(8);
            content.put(ZooConstant.TITLE, ZooConstant.ZOO_MO_CR_ALARM_MAIL_SUBJECT);
            content.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
            content.put(ZooConstant.SHORTCODE, offerModel.getPayShortCode());
            content.put(ZooConstant.KEYWORD, offerModel.getPayKeyword());
            content.put(ZooConstant.CLICK, clickNum);
            content.put(ZooConstant.TRANS, transNum);
            content.put(ZooConstant.REDIS_CR_RATE, redisThreshold);
            content.put(ZooConstant.MO_CR_THRESHOLD, offerModel.getMoCrThreshold());
            content.put(ZooConstant.CLICK_THRESHOLD, offerModel.getMoClickThreshold());
            content.put(ZooConstant.CURRENT_HOUR, hour);
            content.put(ZooConstant.EMAIL_UUID, UUID.randomUUID().toString());
            String mail = offerModel.getEmail();
            log.info("MO CR ALARM EMAIL :{},CONTENT:{}", mail, JSONObject.toJSONString(content));
            if (!StringUtils.isEmpty(mail)) {
                emailUtil.sendMimeMessageMail(ZooConstant.ZOO_MO_CR_ALARM_MAIL_TEMPLATE, mail, ZooConstant.ZOO_MO_CR_ALARM_MAIL_SUBJECT, content);
            }
        } catch (Exception e) {
            log.error("zoo mo cr alarm error:{}", JSON.toJSONString(e));
        }
    }


    /**
     * 同步当前小时的 aff_epm_counter:app:app-identification:offer-identification:yyyy-MM-dd-HH
     */
    @Override
    public void initEpmCurrentHourRedis() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        String date = sdf.format(System.currentTimeMillis());
        String pattern = CacheNameSpace.AFF_EPM_COUNTER + CacheNameSpace.ASTERISK + date;
        Set<String> keys = cluster3RedisTemplate.keys(pattern);
        if (keys != null && keys.size() > 0) {
            for (String key : keys) {
                String[] strArr = key.split(CacheNameSpace.COLON);
                String appId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[2] : ZooConstant.UNKNOWN;
                String offerId = strArr.length == NumberEnum.FIVE.getNum() ? strArr[3] : ZooConstant.UNKNOWN;
                if (offerId != ZooConstant.UNKNOWN && appId != ZooConstant.UNKNOWN) {
                    OfferModel offerModel = getOfferModel(offerId);
                    ApplicationModel applicationModel = applicationService.getById(appId);
                    if (offerModel != null && applicationModel != null) {
                        Map<String, String> params = new HashMap<>(1);
                        params.put(ZooConstant.COUNTRY, offerModel.getCountry());
                        params.put(ZooConstant.OPERATOR, offerModel.getOperator());
                        params.put(ZooConstant.PARTNER, offerModel.getPartner());
                        params.put(ZooConstant.OFFER_NAME, offerModel.getOfferName());
                        params.put(ZooConstant.OFFER_ID, offerModel.getOfferId());
                        params.put(ZooConstant.APP_NAME, applicationModel.getAppName());
                        params.put(ZooConstant.APP_ID, appId);
                        params.put(ZooConstant.CAP, String.valueOf(offerModel.getCap()));
                        params.put(ZooConstant.PARTNER_OFFER_ID, offerModel.getPartnerOfferId());
                        params.put(ZooConstant.OFFER_STATUS, String.valueOf(offerModel.getStatus()));
                        params.put(ZooConstant.BELONG, offerModel.getBelong());
                        masterRedisTemplate.opsForHash().putAll(key, params);
                    }
                }
            }
        }

    }


    private void updateNewOfferTodayRedis(Set<String> offerKeys, String redisHashKey) {
        String offerInfoKeyStart = CacheNameSpace.ZOO_OFFER_DAY_INFO;
        for (String offerKey : offerKeys) {
            Map<Object, Object> offerPullCounter = new HashMap<>(1);
            Boolean existOfferPullKey = cluster3RedisTemplate.hasKey(offerKey);
            if (null != existOfferPullKey && existOfferPullKey) {
                offerPullCounter.putAll(cluster3RedisTemplate.opsForHash().entries(offerKey));
            }
            for (Map.Entry entry : offerPullCounter.entrySet()) {
                String offerId = String.valueOf(entry.getKey());
                // 从老的redis取出值
                OfferModel offerModel = offerRepo.findByIdentification(offerId);
                String today = "";
                if (offerModel != null) {
                    today = DateUtil.dayByTimeZone(offerModel.getResetTimezone(), offerModel.getResetTime(), offerId);
                } else {
                    today = DateUtil.today();
                }
                Integer oldOfferTranCount = Integer.valueOf(String.valueOf(entry.getValue() != null ? entry.getValue() : "0"));
                String offerInfoKey = offerInfoKeyStart + offerId + CacheNameSpace.COLON + today;
                Boolean existNewOfferTrans = cluster3RedisTemplate.opsForHash().hasKey(offerInfoKey, redisHashKey);
                if (existNewOfferTrans != null && existNewOfferTrans) {
                    Integer newOfferTrans = Integer.valueOf(String.valueOf(cluster3RedisTemplate.opsForHash().get(offerInfoKey, redisHashKey)));
                    newOfferTrans = newOfferTrans + oldOfferTranCount;
                    masterRedisTemplate.opsForHash().put(offerInfoKey, redisHashKey, String.valueOf(newOfferTrans));
                } else {
                    masterRedisTemplate.opsForHash().put(offerInfoKey, redisHashKey, String.valueOf(oldOfferTranCount));
                }
            }
        }
    }

    private OfferModel generateFitlterModel(OfferModel offerModel, String offerId) {
        OfferModel filterModel = new OfferModel();
        filterModel.setCap(offerModel.getCap());
        filterModel.setAppCap(offerModel.getAppCap());
        filterModel.setMaxPull(offerModel.getMaxPull());
        String stack = offerTagRepo.findOfferStackId(offerId);
        filterModel.setStack(stack);
        filterModel.setOperator(offerModel.getOperator());
        filterModel.setResetTimezone(offerModel.getResetTimezone());
        filterModel.setResetTime(offerModel.getResetTime());
        return filterModel;
    }


    public boolean getIsTime(String offerId) {
        boolean inTime = true;
        String date = DateUtil.getHourByTimeZone(new Date(), ZooConstant.GMT_8).split("\\s+")[1];
        Boolean exsitOffer = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_OFFER, offerId);
        if (exsitOffer != null && exsitOffer) {
            OfferModel offerModel = JSON.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId)), OfferModel.class);
            //判断是否在可跑时间段内
            if (!StringUtils.isEmpty(offerModel.getTimeRange()) && offerModel.getTimeRange().split(ZooConstant.COMMA).length > 0) {
                String[] timeRange = offerModel.getTimeRange().split(",");
                if (timeRange.length > 1) {
                    inTime = Integer.parseInt(date) >= Integer.parseInt(timeRange[0]) && Integer.parseInt(date) <= Integer.parseInt(timeRange[1]);
                }
            }
            log.info("OFFER ID :{}, CURRENTTIME:{},TIMERANGE:{},RESULT", offerId, date, offerModel.getTimeRange(), inTime);
        }
        return inTime;
    }

    @Override
    public PageVO countJs(String offer, List<String> appName, long beginTime, long endTime, boolean distinct) {
        // 分组聚合
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("userId").field("userId.keyword")
                .subAggregation(AggregationBuilders.terms("eventCode").field("eventCode.keyword")
                        .subAggregation(AggregationBuilders.terms("param1").field("param1.keyword")));
        // 创建where条件
        BoolQueryBuilder boolQueryBuilder = getQueryBuilder(offer, appName, beginTime, endTime);

        SearchQuery searchQuery = new NativeSearchQueryBuilder().withIndices(elasticIndex + "*").withTypes(elasticType)
                .withSort(SortBuilders.fieldSort("eventTime").order(SortOrder.ASC))
                .withQuery(boolQueryBuilder).addAggregation(termsAggregationBuilder).build();
        MetricAggregation query = jestElasticsearchTemplate.query(searchQuery, new JestResultsExtractor<MetricAggregation>() {
            @Override
            public MetricAggregation extract(SearchResult searchResult) {
                return searchResult.getAggregations();
            }
        });
        List<AppUserEventModel> modelList = getResult(query);
        List<CountJsVO> countJsVOList = new ArrayList<>();
        for (AppUserEventModel appUserEventModel : modelList) {
            String param = appUserEventModel.getParam1();
            String regular = "";
            if (!StringUtils.isEmpty(param) && param.indexOf(ZooConstant.HTTP) > -1) {
                regular = param.substring(7);
                if (regular.indexOf(ZooConstant.INTERROGATION_MARK) > -1) {
                    String[] txt = regular.split("\\" + ZooConstant.INTERROGATION_MARK);
                    regular = txt[0];
                }
                if (regular.endsWith(ZooConstant.LEFT_LINE)) {
                    regular = regular.substring(0, regular.length() - 1);
                }
                List<ScriptModel> scriptModels = scriptRepo.findScriptModel(regular);
                if (scriptModels != null && scriptModels.size() > 0) {
                    CountJsVO countJsVO = new CountJsVO();
                    countJsVO.setOfferName(offer);
                    countJsVO.setScriptName(scriptModels.get(0).getName());
                    countJsVO.setRegular(scriptModels.get(0).getRegular());
                    countJsVO.setScript(scriptModels.get(0).getScript());
                    long count = getCountByScript(offer, appName, scriptModels.get(0).getScript(), beginTime, endTime, distinct).getTotal();
                    countJsVO.setCount((int) count);
                    countJsVOList.add(countJsVO);
                }
            }
        }
        List<CountJsVO> distinctList = new CopyOnWriteArrayList();
        for (CountJsVO countJsVO : countJsVOList) {
            if (distinctList.size() > 0) {
                boolean exist = false;
                for (CountJsVO distinctVo : distinctList) {
                    exist = countJsVO.getOfferName().equalsIgnoreCase(distinctVo.getOfferName()) && countJsVO.getRegular().equalsIgnoreCase(distinctVo.getRegular());
                }
                if (!exist) {
                    distinctList.add(countJsVO);
                } else {
                    break;
                }
            } else {
                distinctList.add(countJsVO);
            }
        }
        PageVO pageVO = new PageVO();
        pageVO.setList(distinctList);
        return pageVO;
    }

    /**
     * 获取userList
     *
     * @param offerName
     * @param script
     * @param distinctUser
     * @param beginTime
     * @param endTime
     * @param appNames
     * @return
     */
    @Override
    public PageVO getUserList(String offerName, List<String> appNames, String script, boolean distinctUser, long beginTime, long endTime) {
        PageVO pageVO = getCountByScript(offerName, appNames, script, beginTime, endTime, distinctUser);
        return pageVO;
    }

    @Override
    @Async
    public void updateStatus(String payChannel, String operator, String shortCode, String keyword) {
        offerRepo.updateOfferStatus(payChannel, operator, shortCode, keyword);
        OfferModel offerModel = offerRepo.findFirstByOperatorAndPayShortCodeAndPayKeyword(operator, shortCode, keyword);
        if (offerModel != null) {
            masterRedisTemplate.opsForHash().delete(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification());
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification(), JSON.toJSONString(offerModel));
            List<String> appIds = offerTagRepo.findAppIdsByOfferId(offerModel.getIdentification());
            if (appIds != null && appIds.size() > 0) {
                for (String appId : appIds) {
                    updateClostOfferRedis(appId, offerModel.getIdentification(), operator);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(String identification, int status0) {
        offerRepo.updateStatusById(status0, identification);
        OfferModel offerModel = offerRepo.findByIdentification(identification);
        if (offerModel != null) {
            JSONObject json = (JSONObject) JSON.toJSON(offerModel);
            json.put("offerTags", offerModel.getOfferTags());
            String stackId = offerTagRepo.findOfferStackId(offerModel.getIdentification());
            json.put("stack", stackId);
            // 将当前序列化对象保存到 redis 中
            masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification(), json.toJSONString());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeLog(List<String> ids, int status) {
        offerRepo.updateLogStatus(status, ids);
        if (ids != null && ids.size() > 0) {
            for (String id : ids) {
                OfferModel offerModel = offerRepo.findByIdentification(id);
                if (offerModel != null) {
                    JSONObject json = (JSONObject) JSON.toJSON(offerModel);
                    json.put("offerTags", offerModel.getOfferTags());
                    String stackId = offerTagRepo.findOfferStackId(offerModel.getIdentification());
                    json.put("stack", stackId);
                    // 将当前序列化对象保存到 redis 中
                    masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER, offerModel.getIdentification(), json.toJSONString());
                }
            }
        }
    }

    @Override
    public List<SubscribeModel> findClickIdAndMsisdn(String offerId, int num) {
        OfferModel offerModel = offerRepo.findFirstByOfferId(offerId);
        List<SubscribeModel> list = subscribeRepo.findByOfferId(offerModel.getIdentification(), num);
        return list;
    }

    @Override
    public void sendOfferMaxPullQueue(String offerId) {
        producer.sendToQueueOfferOverMaxPull(new BaseSqsMessage(new Integer[]{}, "offerOverMaxPull", offerId));

    }


    /**
     * 是否需重新添加
     *
     * @param appCap
     * @param cap
     * @param maxPullCount
     * @param existAppTransKey
     * @param existOfferTransKey
     * @param pullCount
     * @param isInTime
     * @param redisOfferCap
     * @param redisAppCap
     * @param count
     * @return
     */
    private boolean checkNeedUpdateEpmAndFilterRedis(Integer appCap, Integer cap, Integer maxPullCount, Boolean existAppTransKey, Boolean existOfferTransKey, int pullCount, boolean isInTime, Integer redisOfferCap, Integer redisAppCap, Integer count) {
        boolean needUpdate = false;
        // 存在包内转化但是不存在Postback时
        boolean existAppTransNotExistOfferTrans = existAppTransKey != null && existAppTransKey && !(existOfferTransKey != null && existOfferTransKey);
        // 存在Postback但是不存在包内转化时
        boolean existOfferTransNotExistAppTrans = existOfferTransKey != null && existOfferTransKey && !(existAppTransKey != null && existAppTransKey);
        // 存在包内转化并且存在Postback时
        boolean existOfferTransExistAppTrans = existOfferTransKey != null && existOfferTransKey && existAppTransKey != null && existAppTransKey;
        // 不存在包内转化并且不存在postback时
        boolean notexistAppTransAndOfferTrans = !(existAppTransKey != null && existAppTransKey) && !(existOfferTransKey != null && existOfferTransKey);
        if (existAppTransNotExistOfferTrans) {
            boolean updateRedis = isInTime && (maxPullCount == null || maxPullCount > pullCount) && (appCap == null || appCap > redisAppCap);
            if (updateRedis) {
                needUpdate = true;
            }
        }
        if (existOfferTransNotExistAppTrans) {
            boolean updateRedis = isInTime && (maxPullCount == null || maxPullCount >= pullCount) && cap >= redisOfferCap;
            if (updateRedis) {
                needUpdate = true;
            }
        }
        if (existOfferTransExistAppTrans) {
            boolean updateRedis = isInTime && (maxPullCount == null || maxPullCount >= pullCount) && (appCap == null || appCap >= redisAppCap) && cap >= redisOfferCap;
            if (updateRedis) {
                needUpdate = true;
            }
        }
        if (notexistAppTransAndOfferTrans) {
            boolean updateRedis = isInTime && (maxPullCount == null || maxPullCount >= pullCount);
            if (updateRedis) {
                needUpdate = true;
            }
        }
        return needUpdate;
    }


    /**
     * 根据offerName 跟脚本统计evenCode为16的执行数量
     *
     * @param offerName
     * @param script
     * @param begin
     * @param end
     * @return
     */
    private PageVO getCountByScript(String offerName, List<String> appNames, String script, long begin, long end, boolean distinct) {
        List<Integer> eventCodeList = new ArrayList<>();
        eventCodeList.add(NumberEnum.SIX_TEEN.getNum());
        eventCodeList.add(NumberEnum.EIGHT_TEEN.getNum());
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("eventCode").field("eventCode.keyword")
                .subAggregation(AggregationBuilders.terms("param1").field("param1.keyword")
                        .subAggregation(AggregationBuilders.terms("userId").field("userId.keyword")));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("offerName", offerName))
                .must(QueryBuilders.rangeQuery("eventTime").format("epoch_millis").gte(begin).lte(end));
        if (appNames != null && appNames.size() > 0) {
            BoolQueryBuilder multiQueryBuilder = QueryBuilders.boolQuery();
            for (String appName : appNames) {
                multiQueryBuilder.should(QueryBuilders.matchPhraseQuery("appName", appName));
            }
            boolQueryBuilder.must(multiQueryBuilder);
        }
        BoolQueryBuilder multiQueryBuilder = QueryBuilders.boolQuery();
        for (Integer eventCode : eventCodeList) {
            multiQueryBuilder.should(QueryBuilders.matchPhraseQuery("eventCode", eventCode));
        }
        boolQueryBuilder.must(multiQueryBuilder);
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(elasticIndex + "*")
                .withTypes(elasticType)
                .withQuery(boolQueryBuilder).addAggregation(termsAggregationBuilder).build();
        MetricAggregation query = jestElasticsearchTemplate.query(searchQuery, new JestResultsExtractor<MetricAggregation>() {
            @Override
            public MetricAggregation extract(SearchResult searchResult) {
                return searchResult.getAggregations();
            }
        });
        PageVO pageVO = getPageVO(query, distinct, script);
        return pageVO;
    }

    private PageVO getPageVO(MetricAggregation query, boolean distinct, String script) {
        TermsAggregation eventCodeTerms = query.getTermsAggregation("eventCode");
        List<String> userIds = new ArrayList<>();
        for (TermsAggregation.Entry eventCode : eventCodeTerms.getBuckets()) {
            if (eventCode.getKey().equalsIgnoreCase(String.valueOf(NumberEnum.SIX_TEEN.getNum()))) {
                TermsAggregation paramTerms = eventCode.getTermsAggregation("param1");
                if (paramTerms != null && paramTerms.getBuckets().size() > 0) {
                    for (TermsAggregation.Entry param : paramTerms.getBuckets()) {
                        TermsAggregation userTerms = param.getTermsAggregation("userId");
                        if (param.getKey().equalsIgnoreCase(script)) {
                            for (TermsAggregation.Entry id : userTerms.getBuckets()) {
                                if (distinct) {
                                    userIds.add(id.getKey());
                                } else {
                                    for (int i = 0; i < id.getCount(); i++) {
                                        userIds.add(id.getKey());
                                    }
                                }

                            }
                        }
                    }
                }
            } else {
                TermsAggregation paramTerms = eventCode.getTermsAggregation("param1");
                for (TermsAggregation.Entry param : paramTerms.getBuckets()) {
                    TermsAggregation userTerms = param.getTermsAggregation("userId");
                    for (TermsAggregation.Entry id : userTerms.getBuckets()) {
                        if (distinct) {
                            userIds.add(id.getKey());
                        } else {
                            for (int i = 0; i < id.getCount(); i++) {
                                userIds.add(id.getKey());
                            }
                        }
                    }
                }
            }
        }
        PageVO pageVO = new PageVO();
        List<String> tranUserList = new CopyOnWriteArrayList<>();
        if (distinct) {
            for (String userId : userIds) {
                if (tranUserList.size() > 0) {
                    boolean exist = false;
                    for (String distinctId : tranUserList) {
                        exist = userId.equalsIgnoreCase(distinctId);
                    }
                    if (!exist) {
                        tranUserList.add(userId);
                    } else {
                        break;
                    }
                } else {
                    tranUserList.add(userId);
                }
            }
        } else {
            tranUserList = userIds;
        }
        pageVO.setTotal((long) tranUserList.size());
        pageVO.setList(tranUserList);
        return pageVO;
    }

    private List<AppUserEventModel> getResult(MetricAggregation query) {
        List<AppUserEventModel> appUserEventModelList = new ArrayList<>();
        TermsAggregation userIdTerms = query.getTermsAggregation("userId");
        if (userIdTerms != null && userIdTerms.getBuckets().size() > 0) {
            for (TermsAggregation.Entry userId : userIdTerms.getBuckets()) {
                String user = userId.getKey();
                TermsAggregation eventCodeTerms = userId.getTermsAggregation("eventCode");
                if (eventCodeTerms != null && eventCodeTerms.getBuckets().size() > 0) {
                    for (TermsAggregation.Entry eventCode : eventCodeTerms.getBuckets()) {
                        String code = eventCode.getKey();
                        int index = eventCodeTerms.getBuckets().indexOf(eventCode);
                        List<String> pointList = new ArrayList<>();
                        pointList.add(String.valueOf(NumberEnum.TEN.getNum()));
                        pointList.add(String.valueOf(NumberEnum.ELEVEN.getNum()));
                        List<String> jsList = new ArrayList<>();
                        jsList.add(String.valueOf(NumberEnum.SIX_TEEN.getNum()));
                        jsList.add(String.valueOf(NumberEnum.EIGHT_TEEN.getNum()));
                        boolean needSave = false;
                        if (index != eventCodeTerms.getBuckets().size() - 1) {
                            String lastCode = eventCodeTerms.getBuckets().get(index + 1).getKey();
                            needSave = pointList.contains(code) && jsList.contains(lastCode);
                        }
                        if (needSave) {
                            TermsAggregation param1Terms = eventCode.getTermsAggregation("param1");
                            if (param1Terms != null && param1Terms.getBuckets().size() > 0) {
                                for (TermsAggregation.Entry param1 : param1Terms.getBuckets()) {
                                    AppUserEventModel model = new AppUserEventModel();
                                    model.setUserId(user);
                                    model.setEventCode(Integer.valueOf(code));
                                    model.setParam1(param1.getKey());
                                    appUserEventModelList.add(model);
                                }
                            }
                        }
                    }
                }
            }
        }
        return appUserEventModelList;
    }


    private BoolQueryBuilder getQueryBuilder(String offerName, List<String> appNames, Long begin, Long end) {
        List<Integer> eventCodeList = new ArrayList<>();
        eventCodeList.add(NumberEnum.TEN.getNum());
        eventCodeList.add(NumberEnum.ELEVEN.getNum());
        eventCodeList.add(NumberEnum.SIX_TEEN.getNum());
        eventCodeList.add(NumberEnum.EIGHT_TEEN.getNum());
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchPhraseQuery("offerName", offerName))
                .must(QueryBuilders.rangeQuery("eventTime").format("epoch_millis").gte(begin).lte(end));
        if (appNames != null && appNames.size() > 0) {
            BoolQueryBuilder multiQueryBuilder = QueryBuilders.boolQuery();
            for (String appName : appNames) {
                multiQueryBuilder.should(QueryBuilders.matchPhraseQuery("appName", appName));
            }
            boolQueryBuilder.must(multiQueryBuilder);
        }
        BoolQueryBuilder multiQueryBuilder = QueryBuilders.boolQuery();
        for (Integer eventCode : eventCodeList) {
            multiQueryBuilder.should(QueryBuilders.matchPhraseQuery("eventCode", eventCode));
        }
        boolQueryBuilder.must(multiQueryBuilder);
        return boolQueryBuilder;
    }

    @Override
    @Async
    public void checkOfferInTime() {
        List<OfferModel> offerModelList = offerRepo.findByStatus(1);
        if (offerModelList != null && offerModelList.size() > 0) {
            for (OfferModel offerModel : offerModelList) {
                boolean isInTime = getIsTime(offerModel.getIdentification());
                if (isInTime) {
                    Boolean existUnusedOffer = cluster3RedisTemplate.opsForHash().hasKey(CacheNameSpace.ZOO_UNUSED_OFFER, offerModel.getIdentification());
                    if (existUnusedOffer != null && existUnusedOffer) {
                        log.info("OFFER IN TIME BUT ALSO EXIST IN UNUSED, OFFERID :{}", offerModel.getIdentification());
                    } else {
                        List<String> appIds = categoryTagRepo.queryAppIds(offerModel.getIdentification());
                        for (String appId : appIds) {
                            String filterKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator();
                            Boolean existFilter = cluster3RedisTemplate.opsForHash().hasKey(filterKey, offerModel.getIdentification());
                            // 如果不存在则添加进去
                            if (existFilter != null && existFilter) {
                                log.info("OFFER:{},EXIST IN FILTER", offerModel.getIdentification());
                            } else {
                                OfferModel filterModel = generateFitlterModel(offerModel, offerModel.getIdentification());
                                masterRedisTemplate.opsForHash().put(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + appId + CacheNameSpace.COLON + offerModel.getOperator(), offerModel.getIdentification(), JSON.toJSONString(filterModel));
                            }
                        }
                    }
                }
            }
        }
    }


    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    @Override
    public String formatConfig(String partnerId, String ipAddress) throws Exception {
        String url = "";
        Boolean existTestOffer = cluster3RedisTemplate.opsForHash().hasKey(ZooConstant.ZOO_TEST_OFFER, partnerId);
        if (existTestOffer != null && existTestOffer) {
            OfferModel offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(ZooConstant.ZOO_TEST_OFFER, partnerId)), OfferModel.class);
            //把配置中的链接根据配置情况替换完成
            String deviceId = "";
            String appId = ZooConstant.TEST_APP_ID;
            String clickId = getClickId(appId, offerModel.getOfferId());
            if (!StringUtils.isEmpty(offerModel.getPartner()) && offerModel.getPartner().equalsIgnoreCase(ZooConstant.IE)) {
                clickId = offerModel.getPartnerOfferId() + UUID.randomUUID().toString();
            }
            //统计点击信息
            saveClickInfo(ipAddress, deviceId, clickId, offerModel, "", appId, ZooConstant.CATEGORY_APP, true, partnerId);
            url = formatUrl(appId, clickId, offerModel);
        }
        return url;
    }

    public String getClickId(String appId, String offerId) {
        String clickId = ZooConstant.APP.toUpperCase() + DateUtil.getCurrentTimeSeconds() + RandomUtil.getRandomNum(6);
        Map<String, String> map = new HashMap<>(3);
        map.put(ZooConstant.OFFER_ID, offerId);
        map.put(ZooConstant.APP_ID, appId);
        String key = ZooConstant.ZOO_CLICK_ID + ZooConstant.COLON + clickId;
        trackRedisTemplate.opsForHash().putAll(key, map);
        trackRedisTemplate.expire(key, NumberEnum.TWO.getNum(), TimeUnit.DAYS);
        return clickId;
    }

    public void saveClickInfo(String ipAddress, String deviceId, String clickId, OfferModel offerModel, String userAgent, String categoryId, int type, boolean isSave, String partnerId) {
        if (offerModel != null) {
            incrClick(categoryId, offerModel.getIdentification(), type, partnerId);
            String key = CacheNameSpace.AFF_OFFER_PULL_COUNTER + offerModel.getIdentification() + ZooConstant.COLON + SubCountServiceImpl.formatDateByTimeZone(offerModel);
            masterRedisTemplate.opsForValue().increment(key, 1L);
            masterRedisTemplate.expire(key, NumberEnum.ONE_DAY_MILLISECONDS.getNum(), TimeUnit.MILLISECONDS);
        }
        AffClickInfoModel affClickInfoModel = new AffClickInfoModel();
        if (offerModel != null && isSave) {
            affClickInfoModel.setCallbackType(NumberEnum.ONE.getNum());
            affClickInfoModel.setIp(ipAddress);
            affClickInfoModel.setClickId(clickId);
            affClickInfoModel.setOfferId(offerModel.getOfferId());
            affClickInfoModel.setAffName(offerModel.getPartner());
            affClickInfoModel.setOfferName(offerModel.getOfferName());
            affClickInfoModel.setUserAgent(userAgent);
            affClickInfoModel.setResourceId(categoryId);
            affClickInfoModel.setResourceType(type);
            if (ZooConstant.CATEGORY_APP == type) {
                ApplicationModel appModel = applicationService.getById(categoryId);
                affClickInfoModel.setResourceName(appModel != null ? appModel.getAppName() : ZooConstant.UNKNOWN);
            } else {
                AffSmartLinkModel affSmartLinkModel = smartLinkRepo.findFirstByIdentification(categoryId);
                affClickInfoModel.setResourceName(affSmartLinkModel != null ? affSmartLinkModel.getName() : ZooConstant.UNKNOWN);
            }
        }
    }

    public void incrClick(String categoryId, String offerId, int type, String partnerId) {
        if (type != 0) {
            String key = getRedisKey(categoryId, offerId, type);
            Long clickNum = masterRedisTemplate.opsForHash().increment(key, CacheNameSpace.CLICK, 1);
            if (clickNum == 1L) {
                //设置 TTL
                updateRedisEpmCount(offerId, categoryId, key, partnerId);
                masterRedisTemplate.expire(key, NumberEnum.THREE.getNum(), TimeUnit.DAYS);
            }
        }
    }


    @SuppressFBWarnings({"ES_COMPARING_PARAMETER_STRING_WITH_EQ", "ES_COMPARING_PARAMETER_STRING_WITH_EQ"})
    private void updateRedisEpmCount(String offerId, String categoryId, String key, String partnerId) {
        //更新redis key
        if (offerId != ZooConstant.UNKNOWN && categoryId != ZooConstant.UNKNOWN) {
            OfferModel offerModel = null;
            Boolean existTestOffer = cluster3RedisTemplate.opsForHash().hasKey(ZooConstant.ZOO_TEST_OFFER, partnerId);
            if (existTestOffer != null && existTestOffer) {
                offerModel = JSONObject.parseObject(String.valueOf(cluster3RedisTemplate.opsForHash().get(ZooConstant.ZOO_TEST_OFFER, partnerId)), OfferModel.class);
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
                masterRedisTemplate.opsForHash().putAll(key, params);
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


    public static String formatUrl(String appId, String clickId, OfferModel offerModel) throws UnsupportedEncodingException {
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

    public static String getReplaceUrl(String url, String param, String value) {
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

    public static String encodeOthersValue(String url) throws UnsupportedEncodingException {
        Matcher matcher = PatternUtil.URL_PARAMS_PATTERN.matcher(url);
        while (matcher.find()) {
            url = url.replace(matcher.group(2) + ZooConstant.EQUAL_MARK + matcher.group(3), matcher.group(2) + ZooConstant.EQUAL_MARK + URLEncoder.encode(matcher.group(3), "UTF-8"));
        }
        return url;
    }


}
