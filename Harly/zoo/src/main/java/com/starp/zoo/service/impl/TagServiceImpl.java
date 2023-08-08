package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.common.BadRequestException;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.*;
import com.starp.zoo.repo.zoo.CategoryTagRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.OfferTagRepo;
import com.starp.zoo.repo.zoo.TagRepo;
import com.starp.zoo.service.IOfferService;
import com.starp.zoo.service.ITagService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import com.starp.zoo.vo.TagsOptionVO;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/***
 *
 * @Author David
 * @Date 11:41 2019/3/4
 * @param
 * @return
 **/
@Service
public class TagServiceImpl implements ITagService{

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private OfferTagRepo offerTagRepo;

    @Autowired
    private CategoryTagRepo categoryTagRepo;

    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private IOfferService offerService;

    @Override
    public PageVO getTagList(int page, int limit, String type, String name) {
        Specification specification = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList= new ArrayList<>();
                if(!StringUtils.isEmpty(type)){
                    predicateList.add(criteriaBuilder.equal(root.get("tagType"),type));
                }
                if(!StringUtils.isEmpty(name)){
                    predicateList.add(criteriaBuilder.equal(root.get("tagName"),name));
                }
                criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));
                return criteriaQuery.getRestriction();
            }
        };
        PageVO<JSONObject> pageVo  = new PageVO<>();
        Long total =  tagRepo.count(specification);
        pageVo.setTotal(total);
        page = page>=1?page-1:0;
        Page<TagModel> pageResult = tagRepo.findAll(specification, PageRequest.of(page,limit));
        List<TagModel> tagModelList =  pageResult.getContent();
        List<JSONObject> resultList = new ArrayList<>();
        if(tagModelList!=null&&tagModelList.size()>0){
            for(TagModel tagModel:tagModelList){
                JSONObject jsonObject = (JSONObject) JSON.toJSON(tagModel);
                resultList.add(jsonObject);
            }
        }
        pageVo.setList(resultList);
        pageVo.setLimit(limit);
        pageVo.setPage(page);
        return pageVo;
    }

    @Override
    public TagModel findTagById(String id) {
        TagModel tagModel = tagRepo.findByIdentification(id);
        return tagModel;
    }

    @Override
    public void save(TagModel tagModel) {
        tagRepo.save(tagModel);
    }

    @Override
    public void delete(String id) {
        TagModel tagModel = tagRepo.findByIdentification(id);
        if (tagModel != null && tagModel.getTagType() == ZooConstant.TAG_TYPE_GROUP) {
            // 删除 redis 关联关系
            List<CategoryTagModel> ctModels = categoryTagRepo.findByTagId(id);
            List<OfferTagModel> offerTagModels = offerTagRepo.findByTagId(id);
            if (ctModels != null && ctModels.size() > 0) {
                for (CategoryTagModel ctModel : ctModels) {
                    String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON +
                            (ctModel.getType() == ZooConstant.CATEGORY_APP ? ZooConstant.APP : ZooConstant.AFFILIATE)
                            + CacheNameSpace.COLON + ctModel.getCategoryId();
                    if (offerTagModels != null && offerTagModels.size() > 0) {
                        for (OfferTagModel offerTagModel : offerTagModels) {
                            stringRedisTemplate.opsForHash().delete(key, offerTagModel.getOfferId());
                        }
                    }
                }
            }
        }
        tagRepo.deleteById(id);
        // 删除 app 及 smartLink 关联
        categoryTagRepo.deleteByTagId(id);
        // 删除 offer 关联
        offerTagRepo.deleteByTagId(id);
    }

    @Override
    public void multiDelete(List<String> ids) {
        if(ids != null && ids.size() > 0) {
            for(String id : ids){
                delete(id);
            }
        }
    }

    @Override
    public List<OptionVO> findTagName(String name) {
        String sql = String.format("SELECT distinct(tag_name) FROM t_tag where tag_name like '%s' order by tag_name desc",
                ZooConstant.PERCENT_SIGN + name + ZooConstant.PERCENT_SIGN);
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        List<String> titles = nativeQuery.getResultList();
        List<OptionVO> optionVOS = new ArrayList<>();
        if(titles != null && titles.size() > 0){
            for(String title : titles){
                OptionVO optionVo = new OptionVO();
                optionVo.setLabel(title);
                optionVo.setValue(title);
                optionVOS.add(optionVo);
            }
        }
        return optionVOS;
    }

    @Override
    public List<OfferTagModel> findOffers(String id) {
        return offerTagRepo.findAllByTagId(id);
    }

    @Override
    public void saveTagOffer(List<String> tagModelList, String id) {
        List<OfferTagModel> list = offerTagRepo.findByTagId(id);
        for(OfferTagModel offerTagModel: list){
            offerTagRepo.deleteByOfferIdAndTagId(offerTagModel.getOfferId(),id);
        }
        if(tagModelList !=null){
            for(String offerId:tagModelList){
                OfferTagModel offerTagModel = new OfferTagModel();
                offerTagModel.setOfferId(offerId);
                offerTagModel.setTagId(id);
                offerTagRepo.save(offerTagModel);
            }
        }
    }


    @Override
    public TagsOptionVO getAllOptions() {
        TagsOptionVO tagsOptionVO = new TagsOptionVO();
        List<OptionVO> stackTags = new ArrayList<>();
        List<OptionVO> groupTags = new ArrayList<>();
        List<OptionVO> othersTags = new ArrayList<>();
        List<ResultTagModel> tagModels = tagRepo.findAllTagModel();
        if(tagModels != null && tagModels.size() > 0){
            for(ResultTagModel tag : tagModels){
                OptionVO optionVO = new OptionVO();
                optionVO.setIdentification(tag.getIdentification());
                optionVO.setLabel(tag.getTagName());
                optionVO.setValue(tag.getIdentification());
                if(tag.getTagType() == ZooConstant.TAG_TYPE_STACK) {
                    stackTags.add(optionVO);
                }else if(tag.getTagType() == ZooConstant.TAG_TYPE_GROUP) {
                    groupTags.add(optionVO);
                }else {
                    othersTags.add(optionVO);
                }
            }
            tagsOptionVO.setStack(stackTags);
            tagsOptionVO.setGroup(groupTags);
            tagsOptionVO.setOthers(othersTags);
        }
        return tagsOptionVO;
    }

    @Override
    public List<OfferTagModel> getOfferTag(String id) {
        return offerTagRepo.findAllByTagId(id);
    }

    @Override
    public TagModel saveTag(TagModel model, List<String> offerIds) throws BadRequestException {
        TagModel saveModel = null;
        if(model != null){
            if(StringUtils.isEmpty(model.getIdentification()) && tagRepo.existsByTagName(model.getTagName())){
                throw new BadRequestException("已存在该名称的TAG");
            }else if(!StringUtils.isEmpty(model.getIdentification())){
                TagModel origin = tagRepo.findFirstByTagName(model.getTagName());
                if(origin != null && !model.getIdentification().equalsIgnoreCase(origin.getIdentification())){
                    throw new BadRequestException("已存在该名称的TAG");
                }
            }
            //检查是否有相同Tag。。。
            saveModel= tagRepo.save(model);
            boolean saveProtectedRedis = model.getTagType().equals(NumberEnum.THREE.getNum()) && model.getTagName().equalsIgnoreCase(CacheNameSpace.PROTECTED);
            if(saveProtectedRedis){
                updateProtectedRedis(offerIds);
            }
            // 保存offer关联
            if(offerIds != null){
                if(offerIds.size() > 0) {
                   handleTag(saveModel,offerIds,model);
                }else {
                    List<OfferTagModel> offerTagModels = offerTagRepo.findByTagId(saveModel.getIdentification());
                    if (saveModel.getTagType() == ZooConstant.TAG_TYPE_GROUP) {
                        Set<String> keys = cluster3RedisTemplate.keys(CacheNameSpace.ZOO_OFFER_ASSIGN+ CacheNameSpace.ASTERISK);
                        Set<String> filterKeys = cluster3RedisTemplate.keys(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.ASTERISK );
                        if (keys != null && keys.size() > 0 && offerTagModels != null && offerTagModels.size() > 0) {
                            for (String key : keys) {
                                for (OfferTagModel offerTagModel : offerTagModels) {
                                    stringRedisTemplate.opsForHash().delete(key, offerTagModel.getOfferId());
                                }
                            }
                        }
                        if(filterKeys != null && filterKeys.size() > 0 && offerTagModels != null && offerTagModels.size() > 0){
                            for (String filterKey : filterKeys) {
                                for (OfferTagModel offerTagModel : offerTagModels) {
                                    stringRedisTemplate.opsForHash().delete(filterKey, offerTagModel.getOfferId());
                                }
                            }
                        }
                    }
                    offerTagRepo.deleteByTagId(saveModel.getIdentification());
                }
            }
        }
        return saveModel;
    }

    private void updateProtectedRedis(List<String> offerIds) {
        Set<String> protectedKeySet = cluster3RedisTemplate.keys(CacheNameSpace.ZOO_PROTECTED_TAG + CacheNameSpace.ASTERISK);
        if(protectedKeySet != null && protectedKeySet.size() >0){
            for(String key : protectedKeySet){
                stringRedisTemplate.delete(key);
            }
        }
        for(String offer : offerIds){
            OfferModel offerModel = offerService.getOfferModel(offer);
            if(offerModel != null ){
                String protectKey = CacheNameSpace.ZOO_PROTECTED_TAG + CacheNameSpace.COLON + offerModel.getOperator();
                stringRedisTemplate.opsForList().leftPush(protectKey,offer);
            }
        }

    }

    private void handleTag(TagModel saveModel, List<String> offerIds, TagModel model) {
        // 判断该 tag 是否被分配到某个 category
        List<CategoryTagModel> categoryTagModels = categoryTagRepo.findByTagId(saveModel.getIdentification());
        List<OfferTagModel> origins = offerTagRepo.findAllByTagId(saveModel.getIdentification());
        List<String> enableNextIds = new ArrayList<>();
        if (origins != null && origins.size() > 0) {
            for (OfferTagModel offerTagModel : origins) {
                if (!offerIds.contains(offerTagModel.getIdentification())) {
                    offerTagRepo.deleteById(offerTagModel.getIdentification());
                    if(categoryTagModels != null && categoryTagModels.size() > 0 && saveModel.getTagType() == ZooConstant.TAG_TYPE_GROUP) {
                        for (CategoryTagModel categoryTagModel : categoryTagModels) {
                            // 将 app 与 offer 的关联信息保存到 redis 中
                            String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON +
                                    (categoryTagModel.getType() == ZooConstant.CATEGORY_APP ? ZooConstant.APP : ZooConstant.AFFILIATE)
                                    + CacheNameSpace.COLON + categoryTagModel.getCategoryId();
                            stringRedisTemplate.opsForHash().delete(key, offerTagModel.getOfferId());
                            Set<String> filterKeys = cluster3RedisTemplate.keys(CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + categoryTagModel.getCategoryId() + CacheNameSpace.ASTERISK);
                            if(filterKeys != null && filterKeys.size() > 0 ){
                                for(String filterKey : filterKeys){
                                    stringRedisTemplate.delete(filterKey);
                                }
                            }
                        }
                    }
                } else {
                    enableNextIds.add(offerTagModel.getIdentification());
                }
            }
        }
        for (String offerId : offerIds) {
            if (enableNextIds != null && !enableNextIds.contains(offerId) && !StringUtils.isEmpty(offerId)) {
                OfferTagModel offerTagModel = new OfferTagModel();
                offerTagModel.setOfferId(offerId);
                offerTagModel.setTagId(model.getIdentification());
                offerTagRepo.save(offerTagModel);
                if(categoryTagModels != null && categoryTagModels.size() > 0 && saveModel.getTagType() == ZooConstant.TAG_TYPE_GROUP) {
                    for (CategoryTagModel categoryTagModel : categoryTagModels) {
                        // 将 app 与 offer 的关联信息保存到 redis 中
                        String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON +
                                (categoryTagModel.getType() == ZooConstant.CATEGORY_APP ? ZooConstant.APP: ZooConstant.AFFILIATE)
                                + CacheNameSpace.COLON + categoryTagModel.getCategoryId();
                        String operator = offerRepo.queryCarrier(offerId);
                        stringRedisTemplate.opsForHash().put(key, offerId, operator);
                        String filterKey = CacheNameSpace.ZOO_OFFER_ASSIGN_FILTER + CacheNameSpace.COLON + categoryTagModel.getCategoryId() + CacheNameSpace.COLON + operator;
                        OfferModel dbOfferModel = offerRepo.findFirstByIdentification(offerId);
                        if(dbOfferModel != null){
                            OfferModel filterModel = new OfferModel();
                            filterModel.setMaxPull(dbOfferModel.getMaxPull());
                            filterModel.setCap(dbOfferModel.getCap());
                            filterModel.setAppCap(dbOfferModel.getAppCap());
                            String stack = offerTagRepo.findOfferStackId(offerId);
                            filterModel.setStack(stack);
                            filterModel.setOperator(dbOfferModel.getOperator());
                            filterModel.setResetTimezone(dbOfferModel.getResetTimezone());
                            filterModel.setResetTime(dbOfferModel.getResetTime());
                            stringRedisTemplate.opsForHash().put(filterKey, offerId ,JSON.toJSONString(filterModel));
                        }

                    }
                }
            }
        }
    }


}
