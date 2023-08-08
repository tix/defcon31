package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.*;
import com.starp.zoo.repo.zoo.*;
import com.starp.zoo.service.ICategoryService;
import com.starp.zoo.service.ISmartLinkService;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.*;

import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author Charles
 * @date 2019/3/13
 * @description :
 */
@Slf4j
@Service
public class SmartLinkServiceImpl implements ISmartLinkService {

    @Autowired
    private AffSmartLinkRepo smartLinkRepo;

    @Autowired
    private ICategoryService categoryService;

    @PersistenceContext(unitName = "zEntityManger")
    EntityManager zooEntityManager;

    @Autowired
    private TagRepo tagRepo;

    @Autowired
    private CategoryTagRepo categoryTagRepo;

    @Autowired
    private DeductConfigRepo deductConfigRepo;

    @Autowired
    private AffiliateRepo  affiliateRepo;

    @Autowired
    private OfferTagRepo offerTagRepo;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public AffSmartLinkModel getById(String smlId) {
        AffSmartLinkModel affSmartLinkModel =  smartLinkRepo.findById(smlId).get();
        // 查找所有扣量信息
        List<DeductConfigModel> deductConfigModels = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        String sql =
        new StringBuilder(" SELECT t8.identification,t8.smart_link_id, t8.offer_id, t8.deduct, t8.payout, t7.offer_name, t7.tag_id, t7.tag_name")
            .append(" FROM")
            .append("   (")
            .append("     SELECT t5.*, t6.tag_name FROM")
            .append("     (")
            .append("       SELECT t3.offer_id, t3.tag_id, t4.offer_name FROM")
            .append("       (")
            .append("         SELECT * ")
            .append("         FROM")
            .append("           t_offer_tag")
            .append("         WHERE")
            .append("           tag_id IN ")
            .append("           (")
            .append("             SELECT t2.identification FROM t_category_tag t1")
            .append("             LEFT JOIN t_tag t2")
            .append("             ON t1.tag_id = t2.identification")
            .append("             WHERE t1.category_id =:smartLinkId")
            .append("           )")
            .append("       )AS t3")
            .append("       LEFT JOIN t_offer t4")
            .append("       ON t3.offer_id = t4.identification")
            .append("     )AS t5")
            .append("     LEFT JOIN")
            .append("     t_tag t6")
            .append("     ON t5.tag_id = t6.identification")
            .append("   )AS t7")
            .append(" LEFT JOIN")
            .append("   t_aff_deduct t8")
            .append(" ON t7.offer_id=t8.offer_id AND t8.smart_link_id=:smartLinkId")
            .append(" ORDER BY")
            .append("   t7.tag_name ASC,")
            .append("   t7.offer_name ASC").toString();
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        nativeQuery.setParameter("smartLinkId", smlId);
        List<Object[]> result = nativeQuery.getResultList();

        if(result != null && result.size() > 0){
            for(Object[] object : result){
                DeductConfigModel deductConfigModel = new DeductConfigModel();
                deductConfigModel.setIdentification((String)object[0]);
                deductConfigModel.setSmartLinkId((String)object[1]);
                deductConfigModel.setOfferId((String)object[2]);
                deductConfigModel.setDeduct(object[3] == null? 0 : (Integer) object[3]);
                deductConfigModel.setPayout((Float) object[4]);
                deductConfigModel.setOfferName((String)object[5]);
                deductConfigModel.setTagId((String)object[6]);
                deductConfigModel.setGroup((String)object[7]);
                deductConfigModels.add(deductConfigModel);
                if(tags != null && !tags.contains(deductConfigModel.getTagId())){
                    tags.add(deductConfigModel.getTagId());
                }
            }
        }
        affSmartLinkModel.setDeducts(deductConfigModels);
        affSmartLinkModel.setTags(tags);
        return affSmartLinkModel;
    }

    @Override
    public void deleteById(String smlid) {
        smartLinkRepo.deleteById(smlid);
        categoryTagRepo.deleteByTypeAndCategoryId(ZooConstant.CATEGORY_AFFILIATE, smlid);
        String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON + ZooConstant.AFFILIATE + CacheNameSpace.COLON + smlid;
        stringRedisTemplate.delete(key);
    }

    @Override
    public PageVO getList(int page, int limit, String name, String affId, String id) {
        Specification<AffSmartLinkModel> specification = new Specification<AffSmartLinkModel>() {
            @Override
            public Predicate toPredicate(Root<AffSmartLinkModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(name)) {
                    predicates.add(criteriaBuilder.equal(root.get("name"), name));
                }
                if(!StringUtils.isEmpty(affId)) {
                    predicates.add(criteriaBuilder.equal(root.get("affId"), affId));
                }
                if(id != null) {
                    predicates.add(criteriaBuilder.equal(root.get("id"), id));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        PageVO<JSONObject> pageVO = new PageVO<>();
        Long total = smartLinkRepo.count(specification);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setLimit(limit);
        page = page > 0 ? page -1 : 0;
        List<JSONObject> json = new ArrayList<>();
        List<AffSmartLinkModel> affSmartLinkModels = smartLinkRepo.findAll(specification, PageRequest.of(page, limit)).getContent();
        // 获取所有的 smartlink 标签Map
        Map<String, List<TagModel>> tagModelMap = categoryService.getCategoryTagMap(ZooConstant.CATEGORY_AFFILIATE);
        Map<String, String>  affiliateMap = getAffNameMap();
        for(AffSmartLinkModel affSmartLinkModel : affSmartLinkModels){
            JSONObject jsonObject = (JSONObject) JSON.toJSON(affSmartLinkModel);
            jsonObject.put(ZooConstant.TAG_GROUP, tagModelMap.get(affSmartLinkModel.getIdentification()));
            jsonObject.put(ZooConstant.AFF_NAME, affiliateMap.get(affSmartLinkModel.getAffId()));
            json.add(jsonObject);
        }
        pageVO.setList(json);
        return pageVO;
    }

    private Map<String, String> getAffNameMap(){
        List<AffiliateModel> affiliateModels = affiliateRepo.findAll();
        Map<String, String> map = new HashMap<>(1);
        if(affiliateModels != null && affiliateModels.size() > 0){
            for(AffiliateModel affiliateModel : affiliateModels) {
                map.put(affiliateModel.getIdentification(), affiliateModel.getName());
            }
        }
        return map;
    }


    @Override
    public List<OptionVO> getAllSmartLinkNames() {
        String sql = "select distinct(name) from  t_aff_smart_link order by name asc";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        List<String> result = nativeQuery.getResultList();
        List<OptionVO> optionVOS = new ArrayList<>();
        if (result != null && result.size() > 0) {
            for(String appName : result){
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
    @Transactional(propagation = REQUIRED, rollbackFor = Exception.class)
    public void saveConfig(AffSmartLinkModel affSmartLinkModel){
        AffSmartLinkModel saveModel = smartLinkRepo.save(affSmartLinkModel);
        if (affSmartLinkModel.getDeducts() != null && affSmartLinkModel.getDeducts().size() > 0) {
            List<String> tags = new ArrayList<>();
            for(DeductConfigModel deductConfigModel : affSmartLinkModel.getDeducts()){
                if (StringUtils.isEmpty(deductConfigModel.getSmartLinkId())) {
                    deductConfigModel.setSmartLinkId(saveModel.getIdentification());
                }
                if(tags != null && !tags.contains(deductConfigModel.getTagId())){
                    tags.add(deductConfigModel.getTagId());
                }
                if (StringUtils.isEmpty(deductConfigModel.getOfferId())) {
                    log.info("[SMART_LINK_SAVE] [DEDUCT_OFFER_ID_IS_NULL:{}]", JSON.toJSONString(deductConfigModel));
                }
            }
            List<CategoryTagModel> originTags = categoryTagRepo.findAllByTypeAndCategoryId(ZooConstant.CATEGORY_AFFILIATE, saveModel.getIdentification());
            List<CategoryTagModel> deleteModels = new ArrayList<>();
            List<String> originTagIds = new ArrayList<>();
            if (originTags != null && originTags.size() > 0) {
                for (CategoryTagModel ctModel : originTags) {
                    if(tags != null && !tags.contains(ctModel.getTagId())){
                        deleteModels.add(ctModel);
                    }
                    originTagIds.add(ctModel.getTagId());
                }
            }

            List<CategoryTagModel> addModels = new ArrayList<>();
            if (tags != null && tags.size() > 0) {
                for (String tagId : tags) {
                    if (!originTagIds.contains(tagId)) {
                        CategoryTagModel categoryTagModel = new CategoryTagModel();
                        categoryTagModel.setType(ZooConstant.CATEGORY_AFFILIATE);
                        categoryTagModel.setCategoryId(saveModel.getIdentification());
                        categoryTagModel.setTagId(tagId);
                        addModels.add(categoryTagModel);
                    }
                }
            }
            if (addModels != null && addModels.size() > 0) {
                categoryTagRepo.saveAll(addModels);
            }
            if (deleteModels != null && deleteModels.size() > 0) {
                categoryTagRepo.deleteAll(deleteModels);
            }
            log.info("[SMART_LINK_SAVE] [DEDUCTS:{}]", JSON.toJSONString(affSmartLinkModel.getDeducts()));
            deductConfigRepo.saveAll(affSmartLinkModel.getDeducts());
            // 设置 redis
            String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON + ZooConstant.AFFILIATE + CacheNameSpace.COLON + saveModel.getIdentification();
            // 将 app 与 offer 的关联信息保存到 redis 中
            List<OfferModel> offerModels = offerRepo.queryInTagIds(tags);
            if (offerModels != null && offerModels.size() > 0) {
                for (OfferModel offerModel : offerModels) {
                    if (offerModel != null) {
                        stringRedisTemplate.opsForHash().put(key, offerModel.getIdentification(), offerModel.getOperator());
                    }
                }
            }
        } else {
            categoryTagRepo.deleteByTypeAndCategoryId(ZooConstant.CATEGORY_AFFILIATE, saveModel.getIdentification());
            deductConfigRepo.deleteBySmartLinkId(saveModel.getIdentification());
            String key = CacheNameSpace.ZOO_OFFER_ASSIGN + CacheNameSpace.COLON + ZooConstant.AFFILIATE + CacheNameSpace.COLON + saveModel.getIdentification();
            stringRedisTemplate.delete(key);
        }
    }

    @Override
    public AffSmartLinkModel getBySmartLinkId(String smlId) {
        return smartLinkRepo.findFirstById(smlId);
    }

    @Override
    public List<OptionVO> getAllSmartLinkIds() {
        String sql = "select distinct(id) from  t_aff_smart_link order by id asc";
        Query nativeQuery = zooEntityManager.createNativeQuery(sql);
        nativeQuery.unwrap(NativeQuery.class);
        List<String> result = nativeQuery.getResultList();
        List<OptionVO> optionVOS = new ArrayList<>();
        if (result != null && result.size() > 0) {
            for(String id : result){
                OptionVO optionVO = new OptionVO();
                optionVO.setIdentification(id);
                optionVO.setValue(id);
                optionVO.setLabel(id);
                optionVOS.add(optionVO);
            }
        }
        return optionVOS;
    }

    @Override
    public List<DeductConfigModel> getInitDeductModels(List<String> tagIds) {
        List<DeductConfigModel> deductConfigModels = new ArrayList<>();
        if(tagIds != null && tagIds.size() > 0) {
            String sql = "SELECT t11.offer_id, t11.tag_id, t11.tag_name, t22.offer_name FROM  " +
                    "   (SELECT t1.offer_id, t1.tag_id, t2.tag_name FROM t_offer_tag t1 " +
                    "       LEFT JOIN t_tag t2 ON t1.tag_id = t2.identification AND t2.tag_type=2 WHERE t2.identification IN (:tags)" +
                    "   ) AS t11 " +
                    "LEFT JOIN " +
                    "t_offer t22 ON t11.offer_id = t22.identification " +
                    "ORDER BY t11.tag_name ASC, t22.offer_name ASC";
            StringBuffer stb = new StringBuffer(sql);
            Query nativeQuery = zooEntityManager.createNativeQuery(stb.toString());
            nativeQuery.unwrap(NativeQuery.class);
            nativeQuery.setParameter("tags", tagIds);
            List<Object[]> result = nativeQuery.getResultList();
            if (result != null && result.size() > 0) {
                for (Object[] object : result) {
                    DeductConfigModel deductConfigModel = new DeductConfigModel();
                    deductConfigModel.setOfferId((String) object[0]);
                    deductConfigModel.setTagId((String) object[1]);
                    deductConfigModel.setGroup((String) object[2]);
                    deductConfigModel.setOfferName((String) object[3]);
                    deductConfigModel.setDeduct(0);
                    deductConfigModels.add(deductConfigModel);
                }
            }
        }
        return deductConfigModels;
    }

    @Override
    public boolean checkUniqueId(String id) {
        int count = smartLinkRepo.countQuery(id);
        if(count > 0){
            return false;
        }
        return true;
    }
}
