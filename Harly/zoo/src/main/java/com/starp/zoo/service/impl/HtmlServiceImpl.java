package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.HtmlInfoModel;
import com.starp.zoo.entity.zoo.MsisdnParamModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.repo.zoo.HtmlInfoRepo;
import com.starp.zoo.repo.zoo.MsisdnParamRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.service.IApplicationService;
import com.starp.zoo.service.IHtmlService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.S3Util;
import com.starp.zoo.util.SpringMvcFileUpLoad;
import com.starp.zoo.vo.HtmlInfoVO;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author Charles
 * Created by Charles, DATE: 2018/5/16.
 */
@Service
@Slf4j
public class HtmlServiceImpl implements IHtmlService {

    @Autowired
    private HtmlInfoRepo htmlInfoRepo;

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster2RedisTemplate")
    private StringRedisTemplate cluster2RedisTemplate;

    @Value("${s3.html_path}")
    private String htmlPath;

    @Autowired
    private SpringMvcFileUpLoad springMvcFileUpLoad;

    @Autowired
    private MsisdnParamRepo msisdnParamRepo;

    @Autowired
    private S3Util s3Util;

    @Autowired
    private IApplicationService applicationService;

    @PersistenceContext(unitName = "appEventEntityManger")
    EntityManager appEventEntityManager;

    @SuppressWarnings("AlibabaAvoidNewDateGetTime")
    @Timed
    @Async
    @Override
    public HtmlInfoModel saveModel(HtmlInfoModel htmlInfoModel) throws IOException {
        String appId = htmlInfoModel.getAppId();
        String offerId = htmlInfoModel.getOfferId();
        String htmlContent = htmlInfoModel.getSource();
        ApplicationModel applicationModel = applicationService.getById(appId);
        String appName = applicationModel == null ? appId : applicationModel.getAppName();
        htmlInfoModel.setAppName(appName);
        OfferModel offerModel = offerRepo.findFirstByIdentification(offerId);
        String offerName = offerModel == null ? offerId : offerModel.getOfferName();
        htmlInfoModel.setOfferName(offerName);
        String fileName = "autoRedirectHtml_" + System.currentTimeMillis() + UUID.randomUUID() + ".html";

        String s3Path = htmlPath + "/" + appName + "/" + offerName;
        String saveUrl = s3Util.uploadToS3(s3Path, fileName, htmlContent);
        htmlInfoModel.setSaveUrl(saveUrl);
        htmlInfoRepo.save(htmlInfoModel);
        return htmlInfoModel;
    }

    private String enCrypt(String str) {
        char[] chars = str.toCharArray();
        int[] index = new int[chars.length];
        char[] real = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            index[i] = (int) chars[i] + 4;
            real[i] = (char) index[i];
        }
        return new String(real);
    }

    @Override
    public JSONObject getHtmlInfoList(String appId, String offerId, Integer draw, Integer start, Integer length) throws Exception {
        Specification specification = new Specification<HtmlInfoModel>() {
            @Override
            public Predicate toPredicate(Root<HtmlInfoModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (appId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("appId"), appId));
                }
                if (!StringUtils.isEmpty(offerId)) {
                    predicates.add(criteriaBuilder.equal(root.get("offerId"), offerId));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        Page page = htmlInfoRepo.findAll(specification, PageRequest.of(start, length));
        List<HtmlInfoModel> htmlInfoModels = page.getContent();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", htmlInfoModels);
        jsonObject.put("draw", draw);
        Long total = htmlInfoRepo.countByAppIdAndOfferId(appId, offerId);
        jsonObject.put("recordsTotal", total);
        jsonObject.put("recordsFiltered", total);
        return jsonObject;
    }


    @Override
    public JSONObject initHtmlInfo(String appId, String offerId, String userId, String startDate, String endDate, Integer draw, Integer start, Integer length) {
        Specification specification = new Specification<HtmlInfoModel>() {
            @Override
            public Predicate toPredicate(Root<HtmlInfoModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(appId)) {
                    predicates.add(criteriaBuilder.equal(root.get("appId"), appId));
                }
                if (!StringUtils.isEmpty(offerId)) {
                    predicates.add(criteriaBuilder.equal(root.get("offerId"), offerId));
                }
                if (!StringUtils.isEmpty(userId)) {
                    predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
                }
                if (!StringUtils.isEmpty(startDate)) {
                    Date begintime = DateUtil.formatTime(startDate);
                    predicates.add(criteriaBuilder.greaterThan(root.<Date>get("createTime"), begintime));
                }
                if (!StringUtils.isEmpty(endDate)) {
                    Date endtime = DateUtil.formatTime(endDate);
                    predicates.add(criteriaBuilder.lessThan(root.<Date>get("createTime"), endtime));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };

        Page page = htmlInfoRepo.findAll(specification, PageRequest.of(length == 0 ? 0 : start / length, length));
        List<HtmlInfoModel> htmlInfoModels = page.getContent();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", htmlInfoModels);
        jsonObject.put("draw", draw);
        Specification specification2 = new Specification<Integer>() {
            @Override
            public Predicate toPredicate(Root<Integer> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(appId)) {
                    predicates.add(criteriaBuilder.equal(root.get("appId"), appId));
                }
                if (!StringUtils.isEmpty(offerId)) {
                    predicates.add(criteriaBuilder.equal(root.get("offerId"), offerId));
                }
                if (!StringUtils.isEmpty(userId)) {
                    predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
                }
                if (!StringUtils.isEmpty(startDate)) {
                    Date begintime = DateUtil.formatTime(startDate);
                    predicates.add(criteriaBuilder.greaterThan(root.<Date>get("createTime"), begintime));
                }
                if (!StringUtils.isEmpty(endDate)) {
                    Date endtime = DateUtil.formatTime(endDate);
                    predicates.add(criteriaBuilder.lessThan(root.<Date>get("createTime"), endtime));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        long total = htmlInfoRepo.count(specification2);
        jsonObject.put("recordsTotal", total);
        jsonObject.put("recordsFiltered", total);
        return jsonObject;
    }


    @Override
    public List<HtmlInfoModel> searchHtmlInfo(String appName, String offerName) {
        Specification specification = new Specification<HtmlInfoModel>() {
            @Override
            public Predicate toPredicate(Root<HtmlInfoModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(appName)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), appName));
                }
                if (!StringUtils.isEmpty(offerName)) {
                    predicates.add(criteriaBuilder.equal(root.get("operator"), offerName));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        return htmlInfoRepo.findAll(specification);


    }

    @Override
    public HtmlInfoModel getHtmlInfo(String id) {
        HtmlInfoModel htmlInfoModel = htmlInfoRepo.findByIdentification(id);
        return htmlInfoModel;
    }


    @Override
    public List<ApplicationModel> initAppName() {
        List<ApplicationModel> all = new ArrayList<>();
        String key = "zoo_applicationModel_str_list";
        Long size = cluster2RedisTemplate.opsForList().size(key);
        if (size != null && size > NumberEnum.ZERO.getNum()) {
            List<String> list = cluster2RedisTemplate.opsForList().range(key, 0, -1);
            if (list != null && list.size() > NumberEnum.ZERO.getNum()) {
                for (String appStr : list) {
                    ApplicationModel applicationModel = JSON.parseObject(appStr, ApplicationModel.class);
                    all.add(applicationModel);
                }
            }
        } else {
            all = applicationRepo.findAll();
            List<String> list = new ArrayList<>();
            for (ApplicationModel applicationModel : all) {
                String appStr = JSON.toJSONString(applicationModel);
                list.add(appStr);
            }
            stringRedisTemplate.opsForList().rightPushAll(key, list);
        }
        return all;
    }

    @Override
    public List<OptionVO> initOfferName() {
        List<String> offerNames = offerRepo.findOfferNames();
        List<OptionVO> optionVOS = new ArrayList<>();
        if (offerNames != null && offerNames.size() > 0) {
            for (String offerName : offerNames) {
                OptionVO optionVO = new OptionVO();
                optionVO.setIdentification(offerName);
                optionVO.setValue(offerName);
                optionVO.setLabel(offerName);
                optionVOS.add(optionVO);
            }
        }
        return optionVOS;
    }

    @Override
    public PageVO<HtmlInfoModel> findAll(Integer page, Integer limit) {
        PageVO<HtmlInfoModel> pageVO = new PageVO<>();
        pageVO.setTotal(htmlInfoRepo.count());
        pageVO.setPage(page);
        pageVO.setLimit(limit);
        page = page > 0 ? page - 1 : 0;
        pageVO.setList(htmlInfoRepo.findAll(PageRequest.of(page, limit)).getContent());
        return pageVO;
    }

    @Override
    public PageVO<HtmlInfoModel> findBy(String appName, String offerName, String userId, Integer page, Integer limit, Date beginTime, Date endTime, String country) {
        Specification<HtmlInfoModel> specification = new Specification<HtmlInfoModel>() {
            @Override
            public Predicate toPredicate(Root<HtmlInfoModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(appName)) {
                    predicates.add(criteriaBuilder.equal(root.get("appName"), appName));
                }
                if (!StringUtils.isEmpty(country) && StringUtils.isEmpty(offerName)) {
                    List<String> offerNames = offerRepo.findOfferNamesByCountry(country);
                    if (offerNames != null) {
                        Path<Object> path = root.get("offerName");
                        CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                        for (String name : offerNames) {
                            in.value(name);
                        }
                        predicates.add(criteriaBuilder.and(in));
                    }
                } else if (!StringUtils.isEmpty(offerName)) {
                    predicates.add(criteriaBuilder.equal(root.get("offerName"), offerName));
                }
                if (!StringUtils.isEmpty(userId)) {
                    predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
                }
                if (beginTime != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), beginTime));
                }
                if (endTime != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), endTime));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };

        PageVO<HtmlInfoModel> pageVO = new PageVO<>();
        pageVO.setPage(page);
        pageVO.setTotal(htmlInfoRepo.count(specification));
        pageVO.setLimit(limit);
        page = page > 0 ? page - 1 : 0;
        pageVO.setList(htmlInfoRepo.findAll(specification, PageRequest.of(page, limit)).getContent());
        return pageVO;
    }

    @Override
    public PageVO getHtmlList(String appName, String offerName, String userId, String country, Integer page, Integer limit, String datetime) {
        List<HtmlInfoVO> result = new ArrayList<>();
        List<String> offerNames = new ArrayList<>();
        if (!StringUtils.isEmpty(country) && StringUtils.isEmpty(offerName)) {
            offerNames = offerRepo.findOfferNamesByCountry(country);
        } else if (!StringUtils.isEmpty(offerName)) {
            offerNames.add(offerName);
        }
        String tableName = "t_html_info";
        String now = DateUtil.formatDay(new Date());
        if (!datetime.equals(now)) {
            tableName += datetime.replace("-", "");
        }
        //查电话号码
        List<MsisdnParamModel> allRule = msisdnParamRepo.findAll();
        String sql = getHtmlInfoSql(offerNames, appName, userId, page, limit, tableName);
        long total = 0L;
        if (!StringUtils.isEmpty(sql)) {
            String countSql = getHtmlInfoCountSql(offerNames, appName, userId, tableName);
            Query query = appEventEntityManager.createNativeQuery(sql);
            Query queryCount = appEventEntityManager.createNativeQuery(countSql);
            if (!StringUtils.isEmpty(appName)) {
                query.setParameter("appName", appName);
                queryCount.setParameter("appName", appName);
            }
            if (!StringUtils.isEmpty(userId)) {
                query.setParameter("userId", userId);
                queryCount.setParameter("userId", userId);
            }
            if (offerNames != null && offerNames.size() > 0) {
                query.setParameter("offerNames", offerNames);
                queryCount.setParameter("offerNames", offerNames);
            }
            total = Long.parseLong(((BigInteger) queryCount.getSingleResult()).toString());
            List<Object[]> resultList = query.getResultList();
            if (resultList != null) {
                for (Object[] objects : resultList) {
                    HtmlInfoVO model = new HtmlInfoVO();
                    model.setAppName(objects[0] != null ? objects[0].toString() : null);
                    model.setOfferName(objects[1] != null ? objects[1].toString() : null);
                    model.setCreateTime((Date) objects[2]);
                    model.setOriginUrl(objects[3] != null ? objects[3].toString() : null);
                    model.setSaveUrl(objects[4] != null ? objects[4].toString() : null);
                    model.setUserId(objects[5] != null ? objects[5].toString() : null);
                    result.add(model);
                }
            }
        }
        List<HtmlInfoVO> finallyResult = getFinallyResult(allRule, result);
        PageVO<HtmlInfoVO> pageVO = new PageVO<>();
        pageVO.setTotal(total);
        page = page > 1 ? page - 1 : 0;
        pageVO.setPage(page);
        pageVO.setLimit(limit);
        pageVO.setList(finallyResult);
        return pageVO;
    }

    /**
     * 查询htmlinfo数据sql.
     *
     * @param offerNames
     * @param appName
     * @param userId
     * @param page
     * @param limit
     * @param tableName
     * @return
     */
    private String getHtmlInfoSql(List<String> offerNames, String appName, String userId, Integer page, Integer limit, String tableName) {
        StringBuilder sql = new StringBuilder();
        StringBuilder isExistTableName = new StringBuilder(" select table_name FROM information_schema.tables where table_schema='appeventdb' and table_name='" + tableName + "' ");
        Query isExistQuery = appEventEntityManager.createNativeQuery(isExistTableName.toString());
        try {
            Object singleResult = isExistQuery.getSingleResult();
        } catch (Exception e) {
            return "";
        }
        sql.append(" select app_name, offer_name, createtime, origin_url, save_url, user_id from " + tableName + " ");
        StringBuilder whereSql = new StringBuilder(" where 1=1 ");
        if (!StringUtils.isEmpty(appName)) {
            whereSql.append("and app_name = :appName ");
        }
        if (!StringUtils.isEmpty(userId)) {
            whereSql.append("and user_id = :userId ");
        }
        if (offerNames != null && offerNames.size() > 0) {
            whereSql.append("and offer_name in :offerNames ");
        }
        whereSql.append(" limit " + (page - 1) * limit + "," + limit + "");
        return sql.append(whereSql).toString();
    }

    /**
     * 查询htmlInfoCountsql.
     *
     * @param offerNames
     * @param appName
     * @param userId
     * @param tableName
     * @return
     */
    private String getHtmlInfoCountSql(List<String> offerNames, String appName, String userId, String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(1) from " + tableName + " ");
        StringBuilder whereSql = new StringBuilder(" where 1=1 ");
        if (!StringUtils.isEmpty(appName)) {
            whereSql.append("and app_name = :appName ");
        }
        if (!StringUtils.isEmpty(userId)) {
            whereSql.append("and user_id = :userId ");
        }
        if (offerNames != null && offerNames.size() > 0) {
            whereSql.append("and offer_name in :offerNames ");
        }
        return sql.append(whereSql).toString();
    }

    /**
     * 匹配电话号码.
     *
     * @param allRule
     * @param result
     * @return
     */
    private List<HtmlInfoVO> getFinallyResult(List<MsisdnParamModel> allRule, List<HtmlInfoVO> result) {
        for (MsisdnParamModel msisdnParamModel : allRule) {
            String regularStr = regularStr(msisdnParamModel);
            for (HtmlInfoVO htmlInfoVO : result) {
                if (!StringUtils.isEmpty(htmlInfoVO.getOriginUrl()) && htmlInfoVO.getOriginUrl().contains(regularStr)) {
                    String[] splitOne = htmlInfoVO.getOriginUrl().split(msisdnParamModel.getMsisdnRegOne());
                    if (splitOne.length == 2) {
                        String[] splitTwo = splitOne[1].split(msisdnParamModel.getMsisdnRegTwo());
                        if (splitTwo.length == 2) {
                            htmlInfoVO.setMsisdn(splitTwo[0]);
                        } else {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return result;
    }

    private String regularStr(MsisdnParamModel msisdnParamModel) {
        String result = "";
        String htmlRegular = msisdnParamModel.getHtmlRegular();
        if (ZooConstant.REG_TWO.equals(htmlRegular.substring(NumberEnum.ZERO.getNum(), NumberEnum.ONE.getNum()))) {
            if (htmlRegular.contains(ZooConstant.REG_ONE)) {
                result = htmlRegular.substring(NumberEnum.ONE.getNum(), htmlRegular.length() - NumberEnum.TWO.getNum());
            } else if (htmlRegular.contains(ZooConstant.REG_THREE)) {
                result = htmlRegular.substring(NumberEnum.ONE.getNum(), htmlRegular.length() - NumberEnum.ONE.getNum());
            }
        } else if (ZooConstant.REG_ONE.equals(htmlRegular.substring(NumberEnum.ZERO.getNum(), NumberEnum.TWO.getNum()))) {
            if (htmlRegular.contains(ZooConstant.REG_ONE)) {
                result = htmlRegular.substring(NumberEnum.TWO.getNum(), htmlRegular.length() - NumberEnum.TWO.getNum());
            } else if (htmlRegular.contains(ZooConstant.REG_THREE)) {
                result = htmlRegular.substring(NumberEnum.TWO.getNum(), htmlRegular.length() - NumberEnum.ONE.getNum());
            }
        }
        return result;
    }

    /**
     * 获取sql.
     *
     * @param offerNames
     * @param appName
     * @param userId
     * @param beginTime
     * @param endTime
     * @return
     */
    private String getSql(List<String> offerNames, String appName, String userId, Date beginTime, Date endTime) {
        String endStr = DateUtil.formatDay(endTime);
        String beginStr = DateUtil.formatDay(beginTime);
        StringBuilder sql = new StringBuilder();
        //结束日期为当天
        if (DateUtil.formatDay(new Date()).equals(endStr)) {
            sql = commonPart(appName, userId, offerNames, NumberEnum.ZERO.getNum(), false);
            if (DateUtil.formatDay(new Date()).equals(beginStr)) {
                //开始日期为当天
                return sql.toString();
            } else if (DateUtil.dateofSepcial(-NumberEnum.ONE.getNum()).equals(beginStr)) {
                //开始日期为昨天
                sql.append(commonPart(appName, userId, offerNames, NumberEnum.ONE.getNum(), true));
            } else {
                //开始日期为前天或者前天以前
                sql.append(commonPart(appName, userId, offerNames, NumberEnum.ONE.getNum(), true));
                sql.append(commonPart(appName, userId, offerNames, NumberEnum.TWO.getNum(), true));
            }
        } else if (DateUtil.dateofSepcial(-NumberEnum.ONE.getNum()).equals(endStr)) {
            //结束日期为昨天
            sql = commonPart(appName, userId, offerNames, NumberEnum.ONE.getNum(), false);
            if (beginTime.before(DateUtil.formatDayTime(DateUtil.dateofSepcial(-NumberEnum.TWO.getNum()))) || DateUtil.dateofSepcial(-NumberEnum.TWO.getNum()).equals(beginStr)) {
                //开始日期为前天或者小于前天
                sql.append(commonPart(appName, userId, offerNames, NumberEnum.TWO.getNum(), true));
            }
        } else if (DateUtil.dateofSepcial(-NumberEnum.TWO.getNum()).equals(endStr)) {
            //结束日期为前天
            sql = commonPart(appName, userId, offerNames, NumberEnum.TWO.getNum(), false);
        }
        return sql.toString();
    }

    /**
     * 拼接sql.
     *
     * @param appName
     * @param userId
     * @param offerNames
     * @param index
     * @param isUnion
     * @return
     */
    private StringBuilder commonPart(String appName, String userId, List<String> offerNames, int index, boolean isUnion) {
        //结束日期为前天
        String tableName = "t_html_info";
        if (index != 0) {
            tableName = "t_html_info" + DateUtil.dateofSepcial(-index).replace("-", "");
        }
        StringBuilder sql = new StringBuilder();
        StringBuilder isExistTableName = new StringBuilder(" select table_name FROM information_schema.tables where table_schema='appeventdb' and table_name='" + tableName + "' ");
        Query isExistQuery = appEventEntityManager.createNativeQuery(isExistTableName.toString());
        try {
            Object singleResult = isExistQuery.getSingleResult();
        } catch (Exception e) {
            return sql;
        }
        if (isUnion) {
            sql.append(" union all ");
        }
        sql.append(" select app_name, offer_name, createtime, origin_url, save_url, user_id from " + tableName + " ");
        StringBuilder whereSql = new StringBuilder(" where createtime between :beginTime and :endTime ");
        if (!StringUtils.isEmpty(appName)) {
            whereSql.append("and app_name = :appName ");
        }
        if (!StringUtils.isEmpty(userId)) {
            whereSql.append("and user_id = :userId ");
        }
        if (offerNames != null && offerNames.size() > 0) {
            whereSql.append("and offer_name in :offerNames ");
        }
        return sql.append(whereSql);
    }

    @Override
    public List<OptionVO> initCountry() {
        List<String> countries = offerRepo.findCountries();
        List<OptionVO> optionVOS = new ArrayList<>();
        if (countries != null && countries.size() > 0) {
            for (String country : countries) {
                OptionVO optionVO = new OptionVO();
                optionVO.setIdentification(country);
                optionVO.setValue(country);
                optionVO.setLabel(country);
                optionVOS.add(optionVO);
            }
        }
        return optionVOS;
    }

    @Override
    public void handleSaveMsisdn(HtmlInfoModel htmlInfoModel) {
        String offerId = htmlInfoModel.getOfferId();
        String offerStr = String.valueOf(cluster2RedisTemplate.opsForHash().get(CacheNameSpace.ZOO_OFFER, offerId));
        if (!StringUtils.isEmpty(offerStr)) {
            OfferModel offerModel = JSON.parseObject(offerStr, OfferModel.class);
            String msisdnKey = CacheNameSpace.ZOO_MSISDN_PARAM_INFO;
            Object obj = cluster2RedisTemplate.opsForHash().get(msisdnKey, offerModel.getOperator());
            if (obj != null) {
                String msisdnParamStr = String.valueOf(obj);
                if (!StringUtils.isEmpty(msisdnParamStr)) {
                    if (msisdnParamStr != null) {
                        MsisdnParamModel msisdnParamModel = JSON.parseObject(msisdnParamStr, MsisdnParamModel.class);
                        String msisdnRegOne = msisdnParamModel.getMsisdnRegOne();
                        String msisdnRegTwo = msisdnParamModel.getMsisdnRegTwo();
                        String htmlRegular = msisdnParamModel.getHtmlRegular();
                        if (htmlRegular != null && Pattern.matches(htmlRegular, htmlInfoModel.getOriginUrl())) {
                            String url = htmlInfoModel.getOriginUrl();
                            String source = htmlInfoModel.getSource();
                            if (!StringUtils.isEmpty(source) && source.split(msisdnRegOne).length > 1) {
                                String[] split = source.split(msisdnRegOne);
                                if (split.length > 1) {
                                    String[] split1 = split[1].split(msisdnRegTwo);
                                    if (split1.length > 0) {
                                        String msisdn = split[1].split(msisdnRegTwo)[0];
                                        saveMsisdnRedis(msisdn, offerId, htmlInfoModel);
                                    }
                                }
                            } else if (!StringUtils.isEmpty(url) && url.toLowerCase().indexOf(ZooConstant.MSISDN) > 0) {
                                String msisdnOne = url.toLowerCase().split("msisdn=")[1];
                                String msisdn = msisdnOne.split(ZooConstant.AND_MARK)[0];
                                saveMsisdnRedis(msisdn, offerId, htmlInfoModel);
                            }
                        }
                    }
                }
            }
        }
    }

    private void saveMsisdnRedis(String msisdn, String offerId, HtmlInfoModel htmlInfoModel) {
        String msisdnOfferKey = CacheNameSpace.ZOO_OFFER_MSISDN_INFO + offerId + CacheNameSpace.COLON + DateUtil.today();
        Boolean existMsisdnOffer = cluster2RedisTemplate.hasKey(msisdnOfferKey);
        if (existMsisdnOffer != null && existMsisdnOffer) {
            stringRedisTemplate.opsForHash().put(msisdnOfferKey, htmlInfoModel.getUserId(), msisdn);
        } else {
            stringRedisTemplate.opsForHash().put(msisdnOfferKey, htmlInfoModel.getUserId(), msisdn);
            stringRedisTemplate.expire(msisdnOfferKey, 1, TimeUnit.DAYS);
        }
    }


}
