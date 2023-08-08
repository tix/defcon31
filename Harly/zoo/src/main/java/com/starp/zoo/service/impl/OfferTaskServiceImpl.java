package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.entity.zoo.OfferTaskModel;
import com.starp.zoo.repo.statistics.MyMapRepo;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.repo.zoo.OfferRepo;
import com.starp.zoo.repo.zoo.OfferTaskRepo;
import com.starp.zoo.service.IOfferTaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.util.*;

/**
 *
 * @Author David
 * @Date 18:04 2018/12/18
 **/
@Service
public class OfferTaskServiceImpl implements IOfferTaskService {

    @Autowired
   private MyMapRepo mymapRepo;

    @Resource
    private  ApplicationRepo applicationRepo;

    @Autowired
    private OfferRepo offerRepo;

    @Autowired
    private OfferTaskRepo offerTaskRepo;

    @Autowired
    private StringRedisTemplate masterRedisTemplate;

    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;
    
    /**
     * 获取所有的App
     * @return
     */
    @Override
    public List<ApplicationModel> findAppInfo(){
        List<ApplicationModel> all = new ArrayList<>();
        String key = "zoo_applicationModel_str_list";
        Long size = cluster3RedisTemplate.opsForList().size(key);
        if(size!=null&&size> NumberEnum.ZERO.getNum()){
            List<String> list = cluster3RedisTemplate.opsForList().range(key, 0, -1);
            if(list!=null&&list.size()>0){
                for (String appStr : list) {
                    ApplicationModel applicationModel = JSON.parseObject(appStr, ApplicationModel.class);
                    all.add(applicationModel);
                }
            }
        }else{
            all = applicationRepo.findAll();
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
    public List<OfferModel> findOfferInfo() {
        List<OfferModel> offerModelList = offerRepo.findAll();
        return offerModelList;
    }

    @Override
    public void save(OfferTaskModel offerTaskModel) {
        offerTaskRepo.save(offerTaskModel);
    }

    @Override
    public List<OfferTaskModel> findOfferTask(String country, String operator,int status,String appId) {
        Specification specification = new Specification<OfferTaskModel>() {
            @Override
            public Predicate toPredicate(Root<OfferTaskModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if(!StringUtils.isEmpty(operator)) {
                    predicates.add(criteriaBuilder.equal(root.get("operator"), operator));
                }
                if(status!=0) {
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                }
                if(!StringUtils.isEmpty(appId)){
                    predicates.add(criteriaBuilder.equal(root.get("appId"), appId));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.groupBy(root.get("country"),root.get("operator"),root.get("appId"));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        return  offerTaskRepo.findAll(specification);

    }



    @Override
    public List<OfferTaskModel> findSelectOffer(String appId,String country,String operator) {
        List<OfferTaskModel> offerTaskModelList= offerTaskRepo.findByAppIdAndCountryAndOperatorOrderByLevelAsc(appId,country,operator);
        return  offerTaskModelList;
    }

    @Override
    public List<OfferTaskModel> removeDuplicate(List<OfferTaskModel> offerTask) {
        Set<OfferTaskModel> set = new TreeSet<OfferTaskModel>(new Comparator<OfferTaskModel>() {
            @Override
            public int compare(OfferTaskModel o1, OfferTaskModel o2) {
                //字符串,则按照asicc码升序排列
                return o1.getAppId().compareTo(o2.getAppId());
            }
        });
        set.addAll(offerTask);
        return new ArrayList<OfferTaskModel>(set);
    }

    @Override
    public OfferTaskModel getOfferTask(String id) {
        OfferTaskModel offerTaskModel = offerTaskRepo.findByIdentification(id);
        return offerTaskModel;
    }

    @Override
    public String findOfferName(String offerId) {
        OfferModel offerModel = offerRepo.findFirstByIdentification(offerId);
        String offerName = offerModel ==null ? null : offerModel.getOfferName();
        return offerName;
    }
    
    
    /**
     * 删除任务集合
     * @param identification
     */
    @Override
    public void deleteOfferTask(String identification) {
        OfferTaskModel model = offerTaskRepo.findByIdentification(identification);
        List<OfferTaskModel> tasklist = offerTaskRepo.findByAppIdAndCountryAndOperatorOrderByLevelAsc(model.getAppId(),model.getCountry(),model.getOperator());
        for(OfferTaskModel offerTaskModel:tasklist){
            offerTaskRepo.deleteById(offerTaskModel.getIdentification());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeType(String identification, String type) {
        OfferTaskModel model = offerTaskRepo.findByIdentification(identification);
        List<OfferTaskModel> tasklist = offerTaskRepo.findByAppIdAndCountryAndOperatorOrderByLevelAsc(model.getAppId(),model.getCountry(),model.getOperator());
        for(OfferTaskModel taskModel:tasklist){
            if("stop".equalsIgnoreCase(type)){
                offerTaskRepo.updateType(taskModel.getIdentification(),2);
            }else{
                offerTaskRepo.updateType(taskModel.getIdentification(),1);
            }
        }
    }

    @Override
    public List<OfferTaskModel> findByAppId(String key) {
        return offerTaskRepo.findAllByAppIdAndStatusOrderByLevelAsc(key, ZooConstant.OFFER_TASK_OPEN);
    }

    @Override
    public List<OfferModel> selectOffers(String country, String operator) {
        Specification specification = new Specification<OfferModel>() {
            @Override
            public Predicate toPredicate(Root<OfferModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if(!StringUtils.isEmpty(operator)) {
                    predicates.add(criteriaBuilder.equal(root.get("operator"), operator));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        return  offerRepo.findAll(specification);
    }


}
