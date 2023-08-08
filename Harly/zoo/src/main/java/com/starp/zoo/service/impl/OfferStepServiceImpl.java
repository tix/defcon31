package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.*;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.OfferStepRepo;
import com.starp.zoo.repo.zoo.OfferStepTempRepo;
import com.starp.zoo.repo.zoo.StepAssignRepo;
import com.starp.zoo.service.IOfferStepService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.*;

/**
 * @author charles
 */
@Service
public class OfferStepServiceImpl implements IOfferStepService {

    @Autowired
    private OfferStepRepo offerStepRepo;

    @Autowired
    private StepAssignRepo stepAssignRepo;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private OfferStepTempRepo offerStepTempRepo;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;


    @Override
    public void save(OfferStepModel offerStepModel) {
        if (!StringUtils.isEmpty(offerStepModel.getIdentification())) {
            // 删除原有 redis 记录
            OfferStepModel originModel = getById(offerStepModel.getIdentification());
            List<String> originOfferIds = stepAssignRepo.getOfferIds(originModel.getIdentification());
            // 删除关联
            if (originOfferIds != null && originOfferIds.size() > 0) {
                for (String offerId : originOfferIds) {
                    String key = ZooConstant.PROTC_OFFER_STEP + ZooConstant.COLON + offerId;
                    deleteStepRedis(key,originModel.getRegex());
                }
            }
            stepAssignRepo.deleteAllByStepId(offerStepModel.getIdentification());
        }
        offerStepModel.setStepName(offerStepModel.getStepName().trim());
        OfferStepModel saveModel = offerStepRepo.save(offerStepModel);
        JSONObject json = JSONObject.parseObject(JSON.toJSONString(saveModel));
        json.put("offerIds", new ArrayList<>());
        //保存关联
        List<String> currentOfferIds = offerStepModel.getOfferIds();
        if (currentOfferIds != null && currentOfferIds.size() > 0) {
            List<StepAssignModel> assignModels = new ArrayList<>();
            for (String offerId : currentOfferIds) {
                StepAssignModel assignModel = new StepAssignModel();
                assignModel.setStepId(saveModel.getIdentification());
                assignModel.setOfferId(offerId);
                assignModels.add(assignModel);
                //保存到 redis
                String key = ZooConstant.PROTC_OFFER_STEP + ZooConstant.COLON + offerId;
                stringRedisTemplate.opsForHash().put(key, offerStepModel.getRegex(), json.toJSONString());
            }
            stepAssignRepo.saveAll(assignModels);
        }
    }

    @CacheEvict(value = "step",key = "#stepId")
    public void deleteStepRedis(String stepId, String regex) {
        stringRedisTemplate.opsForHash().delete(stepId, regex);
    }

    @Override
    public OfferStepModel getById(String id) {
        OfferStepModel model = offerStepRepo.findById(id).get();
        List<String> offerIds = stepAssignRepo.getOfferIds(id);
        model.setOfferIds(offerIds);
        return model;
    }

    @Override
    public PageVO getList(String country, String operator, String partner,
                          String offerName, String systemOfferId, String partnerOfferId,
                          String tagId, String stepName, int page, int limit) {
        PageVO<OfferStepModel> pageVO = new PageVO<>();
        //获取 offerids
        List<String> offerIds = getOfferIds(country, operator, partner, offerName, systemOfferId, partnerOfferId, tagId);
        List<String> stepIds = offerIds != null && offerIds.size() > 0 ? stepAssignRepo.getStepIds(offerIds) : null;
        boolean isNullForOffer = offerIds.size() > 0 && (stepIds == null || stepIds.size() == 0);
        if (isNullForOffer) {
            pageVO.setTotal(0L);
            page = page >= 1? page - 1: 0;
            pageVO.setList(null);
            pageVO.setLimit(limit);
            pageVO.setPage(page);
            return pageVO;
        }
        Specification<OfferStepModel> specification = new Specification<OfferStepModel>() {
            @Override
            public Predicate toPredicate(Root<OfferStepModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (stepIds != null && stepIds.size() > 0) {
                    CriteriaBuilder.In<String>  in = criteriaBuilder.in(root.get("identification"));
                    for (String id : stepIds) {
                        in.value(id);
                    }
                    predicates.add(in);
                }
                if (!StringUtils.isEmpty(stepName)) {
                    predicates.add(criteriaBuilder.equal(root.get("stepName"), stepName));
                }

                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        Long total = offerStepRepo.count(specification);
        pageVO.setTotal(total);
        page = page >= 1? page - 1: 0;
        List<OfferStepModel> stepModels = offerStepRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        pageVO.setList(stepModels);
        pageVO.setLimit(limit);
        pageVO.setPage(page);
        return pageVO;
    }

    private List<String> getOfferIds(String country, String operator, String partner,
                                     String offerName, String systemOfferId, String partnerOfferId,
                                     String tagId) {
        List offerIds = new ArrayList<>();
        boolean isNeedGetOffer = !StringUtils.isEmpty(country) || !StringUtils.isEmpty(operator) || !StringUtils.isEmpty(partner)
                || !StringUtils.isEmpty(offerName) || !StringUtils.isEmpty(systemOfferId) || !StringUtils.isEmpty(partnerOfferId)
                || !StringUtils.isEmpty(tagId);
        if (isNeedGetOffer) {
            Specification<OfferModel> offerSpecification = new Specification<OfferModel>() {
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
                    if (!StringUtils.isEmpty(systemOfferId)) {
                        predicates.add(criteriaBuilder.equal(root.get("offerId"), systemOfferId));
                    }
                    if (!StringUtils.isEmpty(partnerOfferId)) {
                        predicates.add(criteriaBuilder.equal(root.get("partnerOfferId"), partnerOfferId));
                    }
                    criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                    List<Order> orders = new ArrayList<>();
                    orders.add(criteriaBuilder.asc(root.get("offerName")));
                    criteriaQuery.orderBy(orders);
                    return criteriaQuery.getRestriction();
                }
            };
            List<OfferModel> offers = offerRepo.findAll(offerSpecification);
            if (offers != null && offers.size() > 0) {
                for (OfferModel offer : offers) {
                    offerIds.add(offer.getIdentification());
                }
            }
        }
        return offerIds;
    }

    @Override
    public String deleteById(String id) {
        OfferStepModel originModel = getById(id);
        // 破解步骤被锁定
        if(!StringUtils.isEmpty(originModel.getLockStatus()) && originModel.getLockStatus().equalsIgnoreCase(String.valueOf(NumberEnum.ONE.getNum()))){
            return ZooConstant.FAIL;
        }else {
            String result = JSON.toJSONString(originModel);
            OfferStepTempModel offerStepTempModel = JSON.parseObject(result,OfferStepTempModel.class);
            offerStepTempRepo.save(offerStepTempModel);
            offerStepRepo.deleteById(id);
            List<String> delList = originModel.getOfferIds();
            if (delList != null && delList.size() > 0) {
                for (String offerId : delList) {
                    String key = ZooConstant.PROTC_OFFER_STEP + ZooConstant.COLON + offerId;
                    deleteStepRedis(key,originModel.getRegex());
                }
            }
            stepAssignRepo.deleteAllByStepId(id);
            return ZooConstant.SUCCESS;
        }
    }

    @Override
    public void multiDelete(List<String> ids) {
        List<OfferStepModel> stepModels = offerStepRepo.findByIdentificationIn(ids);
        if(stepModels!=null && stepModels.size()>0){
            for(OfferStepModel offerStepModel:stepModels){
                String result = JSON.toJSONString(offerStepModel);
                OfferStepTempModel tempModel = JSON.parseObject(result,OfferStepTempModel.class);
                offerStepTempRepo.save(tempModel);
            }
        }
        offerStepRepo.multiDelete(ids);
        // 删除 redis
        if (stepModels != null && stepModels.size() > 0) {
            for (OfferStepModel stepModel : stepModels) {
                List<String> delList = stepAssignRepo.getOfferIds(stepModel.getIdentification());
                if (delList != null && delList.size() > 0) {
                    for (String offerId : delList) {
                        String key = ZooConstant.PROTC_OFFER_STEP + ZooConstant.COLON + offerId;
                        stringRedisTemplate.opsForHash().delete(key, stepModel.getRegex());
                    }
                }
            }
        }
        stepAssignRepo.deleteAllByStepIdIn(ids);
    }

    @Override
    public List<OptionVO> getNames(String query) {
        List<OptionVO> list = new ArrayList<>();
        List<OfferStepModel> stepModels = offerStepRepo.findByStepNameLike(ZooConstant.PERCENT_SIGN + query + ZooConstant.PERCENT_SIGN);
        if (stepModels != null && stepModels.size() > 0) {
            for (OfferStepModel step : stepModels) {
                OptionVO optionVo = new OptionVO(step.getIdentification(), step.getStepName(), step.getIdentification());
                list.add(optionVo);
            }
        }
        return list;
    }

    @Override
    public boolean checkUniqueName(String name) {
        return offerStepRepo.existsByStepName(name);
    }

    @Override
    public void initRedis() {
        Set<String> keys = cluster3RedisTemplate.keys(ZooConstant.PROTC_OFFER_STEP + "*");
        if (keys != null && keys.size() > 0) {
           stringRedisTemplate.delete(keys);
        }
        List<OfferStepModel> stepModels = offerStepRepo.findAll();
        if (stepModels != null && stepModels.size() > 0) {
            for (OfferStepModel stepModel : stepModels) {
                List<String> offerIds = stepAssignRepo.getOfferIds(stepModel.getIdentification());
                if (offerIds != null && offerIds.size() > 0) {
                    for (String offerId : offerIds) {
                        //保存到 redis
                        JSONObject json = (JSONObject) JSON.toJSON(stepModel);
                        String key = ZooConstant.PROTC_OFFER_STEP + ZooConstant.COLON + offerId;
                        stringRedisTemplate.opsForHash().put(key, stepModel.getRegex(), json.toJSONString());
                    }
                }
            }
        }
    }

    @Override
    public void handleMultiToAdd(JSONArray offerNames, JSONArray steps) {
        List<OfferStepModel> offerStepModels = offerStepRepo.findByNameList(steps);
        List<String> offerIds = offerRepo.findByNameList(offerNames);
        for (OfferStepModel offerStepModel : offerStepModels) {
            JSONObject json = (JSONObject) JSON.toJSON(offerStepModel);
            //保存关联
            if (offerIds != null && offerIds.size() > 0) {
                List<StepAssignModel> assignModels = new ArrayList<>();
                for (String offerId : offerIds) {
                    boolean exist = stepAssignRepo.existsByOfferIdAndStepId(offerId, offerStepModel.getIdentification());
                    if(!exist){
                        StepAssignModel assignModel = new StepAssignModel();
                        assignModel.setStepId(offerStepModel.getIdentification());
                        assignModel.setOfferId(offerId);
                        assignModels.add(assignModel);
                        //保存到 redis
                        String key = ZooConstant.PROTC_OFFER_STEP + ZooConstant.COLON + offerId;
                        stringRedisTemplate.opsForHash().put(key, offerStepModel.getRegex(), json.toJSONString());
                    }
                }
                stepAssignRepo.saveAll(assignModels);
            }
        }
    }

    @Override
    public void lock(String id) {
        offerStepRepo.updateLock(id,String.valueOf(NumberEnum.ONE.getNum()));
    }

    @Override
    public void unlock(String id) {
        offerStepRepo.updateLock(id,String.valueOf(NumberEnum.TWO.getNum()));
    }

    @Override
    @SuppressFBWarnings({"DM_DEFAULT_ENCODING", "NP_ALWAYS_NULL", "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE", "NP_NULL_PARAM_DEREF"})
    public void updateRedis() {
        Set<String> keys = cluster3RedisTemplate.keys(ZooConstant.PROTC_OFFER_STEP + "*");
        if(keys != null && keys.size() >0){
            keys.stream().forEach(a->{
                Set<Object> regexList = cluster3RedisTemplate.opsForHash().keys(a);
                if(regexList != null && regexList.size() >0){
                    regexList.stream().forEach(b->{
                        Object step = cluster3RedisTemplate.opsForHash().get(a,b);
                        if(step != null){
                            OfferStepModel offerStepModel = JSONObject.parseObject(
                                    String.valueOf(step),OfferStepModel.class);
                            offerStepModel.setOfferIds(new ArrayList<>());
                            stringRedisTemplate.opsForHash().delete(a,b);
                            stringRedisTemplate.opsForHash().put(a,b,JSON.toJSONString(offerStepModel));
                        }
                    });
                }
            });
        }

    }

}
