package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.starp.zoo.entity.zoo.AppInstallModel;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.repo.zoo.AppInstallRepo;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.service.IAppInstallService;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.vo.OptionVO;
import com.starp.zoo.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author covey
 */
@Slf4j
@Service
public class AppInstallServiceImpl implements IAppInstallService {

    @Autowired
    private AppInstallRepo appInstallRepo;

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private StringRedisTemplate masterRedisTemplate;

    @Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @Override
    public List<OptionVO> findAppNames() {
        List<OptionVO> options = new LinkedList<>();
        List<String> appNames = null;
        String key = "zoo_appName_list" ;
        Long size = cluster1RedisTemplate.opsForList().size(key);
        if(size!=null&&size>0){
            appNames = cluster1RedisTemplate.opsForList().range(key, 0, -1);
        }else {
            appNames = applicationRepo.findAppNames();
            masterRedisTemplate.opsForList().rightPushAll(key, appNames);
        }
        if(appNames!=null&&appNames.size()>0){
            for (String appName : appNames) {
                OptionVO option = new OptionVO();
                option.setLabel(appName);
                option.setValue(appName);
                option.setIdentification(appName);
                options.add(option);
            }
        }
        return options;
    }

    @Override
    public PageVO findList(Integer page, Integer limit) {
        PageVO<AppInstallModel> pageVO = new PageVO<>();
        pageVO.setTotal(appInstallRepo.count());
        pageVO.setPage(page);
        pageVO.setLimit(limit);
        page = page > 0 ? page - 1 : 0;
        pageVO.setList(appInstallRepo.findAll(PageRequest.of(page, limit)).getContent());
        return pageVO;
    }

    @Override
    public PageVO findByParam(Integer page, Integer limit, JSONArray countries, JSONArray appNames, Date begin,Date end) {

        Specification<AppInstallModel> specification = new Specification<AppInstallModel>() {
            @Override
            public Predicate toPredicate(Root<AppInstallModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (appNames.size() > 0) {
                    Path<Object> path = root.get("appName");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                    for (Object appName : appNames) {
                        in.value(appName);
                    }
                    predicates.add(criteriaBuilder.and(in));
                }
                if (countries.size() > 0) {
                    Path<Object> path = root.get("country");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
                    for (Object country : countries) {
                        in.value(country);
                    }
                    predicates.add(criteriaBuilder.and(in));
                }
                if(begin!=null){
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("dayTime"), begin));
                }if(end!=null){
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.<Date>get("dayTime"), end));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("dayTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };

        PageVO<AppInstallModel> pageVO = new PageVO<>();
        pageVO.setPage(page);
        pageVO.setTotal(appInstallRepo.count(specification));
        pageVO.setLimit(limit);
        page = page > 0 ? page - 1 : 0;
        pageVO.setList(appInstallRepo.findAll(specification, PageRequest.of(page, limit)).getContent());
        return pageVO;
    }

    @Override
    public void save(AppInstallModel appInstallModel) {
        ApplicationModel applicationModel = applicationRepo.findFirstByAppName(appInstallModel.getAppName());
        String description = applicationModel.getDescription();
        appInstallModel.setDescription(description);
        appInstallRepo.save(appInstallModel);
    }

    @Override
    public void update(AppInstallModel appInstallModel) {
        appInstallRepo.save(appInstallModel);
    }

    @Override
    public void delete(AppInstallModel appInstallModel) {
        appInstallRepo.delete(appInstallModel);
    }

    @Override
    public JSONObject findInstallNumByAppId (String country,List < String > appIds,List<String> times) {
        JSONObject jsonObject = new JSONObject();
        for (String appId : appIds) {
            List<AppInstallModel> appInstallModels = appInstallRepo.findAppInstallModels(country,appId, DateUtil.formatDayTime(times.get(0)),DateUtil.formatDayTime(times.get(1)));
            if (appInstallModels != null) {
                int installNum = 0;
                for(AppInstallModel appInstallModel:appInstallModels){
                    installNum = installNum+appInstallModel.getInstallation();
                }
                jsonObject.put(appId, installNum);
            }
        }
        return jsonObject;
    }
}

