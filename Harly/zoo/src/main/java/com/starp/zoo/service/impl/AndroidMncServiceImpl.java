package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.Constant;
import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.ApplicationModel;
import com.starp.zoo.entity.zoo.MncPermissionModel;
import com.starp.zoo.repo.zoo.ApplicationRepo;
import com.starp.zoo.repo.zoo.MncPermissionRepo;
import com.starp.zoo.service.IAndroidMncService;
import com.starp.zoo.service.IApplicationService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author david
 */
@Slf4j
@Service
public class AndroidMncServiceImpl implements IAndroidMncService {

    @Autowired
    StringRedisTemplate masterRedisTemplate;

    @Resource(name = "cluster1RedisTemplate")
    private StringRedisTemplate cluster1RedisTemplate;

    @Autowired
    MncPermissionRepo mncPermissionRepo;

    @Autowired
    ApplicationRepo applicationRepo;

    @Autowired
    private IApplicationService applicationService;

    @Override
    public List<MncPermissionModel> findMncList(String country, String type, String mnc) {
        Specification<MncPermissionModel> specification = new Specification<MncPermissionModel>() {
            @Override
            public Predicate toPredicate(Root<MncPermissionModel> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(country)) {
                    predicates.add(criteriaBuilder.equal(root.get("country"), country));
                }
                if (!StringUtils.isEmpty(type)) {
                    predicates.add(criteriaBuilder.equal(root.get("type"), type));
                }
                if (!StringUtils.isEmpty(mnc)) {
                    predicates.add(criteriaBuilder.equal(root.get("mnc"), mnc));
                }
                criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()])));
                List<Order> orders = new ArrayList<>();
                orders.add(criteriaBuilder.desc(root.get("createTime")));
                criteriaQuery.orderBy(orders);
                return criteriaQuery.getRestriction();
            }
        };
        return mncPermissionRepo.findAll(specification);
    }

    @Override
    public void saveMnc(String country, String mnc, String type, String regex, String areaCode, String url, String identification, String oldMnc) {
        MncPermissionModel mncPermissionModel = new MncPermissionModel();
        mncPermissionModel.setCountry(country);
        mncPermissionModel.setMnc(mnc);
        mncPermissionModel.setType(type);
        mncPermissionModel.setRegex(regex);
        mncPermissionModel.setAreaCode(areaCode);
        mncPermissionModel.setUrl(url);
        mncPermissionModel.setIdentification(identification);
        mncPermissionRepo.save(mncPermissionModel);
        masterRedisTemplate.opsForHash().delete(ZooConstant.MNC_PERMISSION + type, oldMnc);
        //存入redis
        if (!StringUtils.isEmpty(type)) {
            if (type.equalsIgnoreCase(String.valueOf(NumberEnum.TWO.getNum())) || type.equalsIgnoreCase(String.valueOf(NumberEnum.FIVE.getNum()))) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("country", country);
                jsonObject.put("regex", regex);
                jsonObject.put("areaCode", areaCode);
                jsonObject.put("mnc", mnc);
                jsonObject.put("url", url);
                masterRedisTemplate.opsForHash().put(ZooConstant.MNC_PERMISSION + type, mnc, JSON.toJSONString(jsonObject));
            } else {
                masterRedisTemplate.opsForHash().put(ZooConstant.MNC_PERMISSION + type, mnc, "1");
            }
        } else {
            masterRedisTemplate.opsForHash().put(ZooConstant.MNC_PERMISSION + type, mnc, "1");
        }
    }

    @Override
    public void delete(String id, String type, String mnc) {
        mncPermissionRepo.deleteById(id);
        masterRedisTemplate.opsForHash().delete(ZooConstant.MNC_PERMISSION + type, mnc);
    }

    @Timed
    @Override
    public String findMncResult(String param) {
        JSONObject result = new JSONObject();
        Boolean existFb = cluster1RedisTemplate.opsForHash().hasKey(ZooConstant.MNC_PERMISSION + NumberEnum.ONE.getNum(), param);
        if (existFb != null && existFb) {
            result.put("fb", 1);
        } else {
            result.put("fb", 0);
        }
        try {
            Object msisdnInfo = cluster1RedisTemplate.opsForHash().get(ZooConstant.MNC_PERMISSION + NumberEnum.TWO.getNum(), param);
            if (msisdnInfo != null ) {
                result.put("msisdn", JSON.parseObject(String.valueOf(msisdnInfo)).toJSONString());
            } else {
                result.put("msisdn", "");
            }
        }catch (Exception e){
            result.put("msisdn", "");
        }
        Boolean existPermission = cluster1RedisTemplate.opsForHash().hasKey(ZooConstant.MNC_PERMISSION + NumberEnum.THREE.getNum(), param);
        if (existPermission != null && existPermission) {
            result.put("permissions", 1);
        } else {
            result.put("permissions", 0);
        }
        Boolean existWifi = cluster1RedisTemplate.opsForHash().hasKey(ZooConstant.MNC_PERMISSION + NumberEnum.FOUR.getNum(), param);
        if (existWifi != null && existWifi) {
            result.put("wifi", 1);
        } else {
            result.put("wifi", 0);
        }
        Object obj = cluster1RedisTemplate.opsForHash().get(ZooConstant.MNC_PERMISSION + NumberEnum.FIVE.getNum(), param);
        if (obj != null) {
            result.put("autoMsisdn", JSON.parseObject(String.valueOf(obj)).toJSONString());
        } else {
            result.put("autoMsisdn", "");
        }
        result.put("random", UUID.randomUUID());
        return JSON.toJSONString(result);
    }

    @Override
    @Cacheable(value = "mnc",key = "#mnc")
    public String generateOp(String mnc) {
        String mncKey = ZooConstant.ADMIN_MNC_CONFIG;
        String operator = "";
        if (!StringUtils.isEmpty(mnc)) {
            Object obj = cluster1RedisTemplate.opsForHash().get(mncKey, mnc);
            if (obj != null) {
                operator = (String) obj;
            } else {
                operator = Constant.OPERATORMAP.get(mnc);
            }
        }
        return operator;
    }

    @Override
    @Async
    public void syncMysql() {
        Set<String> fbKeys = cluster1RedisTemplate.keys(ZooConstant.MNC_CODE + "*");
        if (null != fbKeys && fbKeys.size() > 0) {
            for (String fbkey : fbKeys) {
                MncPermissionModel mncPermissionModel = new MncPermissionModel();
                String[] split = fbkey.split(":");
                mncPermissionModel.setCountry(split[2]);
                mncPermissionModel.setMnc(split[3]);
                mncPermissionModel.setType("1");
                mncPermissionRepo.save(mncPermissionModel);
            }
        }

        Set<String> msisdnKeys = cluster1RedisTemplate.keys("zoo:msisdn*");
        if (null != msisdnKeys && msisdnKeys.size() > 0) {
            for (String msisdnkey : msisdnKeys) {
                String value = cluster1RedisTemplate.opsForValue().get(msisdnkey);
                JSONObject jsonObject = JSON.parseObject(value);
                MncPermissionModel mncPermissionModel = new MncPermissionModel();
                mncPermissionModel.setType("2");
                mncPermissionModel.setMnc(jsonObject.getString("mnc"));
                mncPermissionModel.setCountry(jsonObject.getString("country"));
                mncPermissionModel.setAreaCode(jsonObject.getString("areaCode"));
                mncPermissionModel.setRegex(jsonObject.getString("regex"));
                mncPermissionModel.setUrl(jsonObject.getString("url"));
                mncPermissionRepo.save(mncPermissionModel);
            }
        }

        Set<String> permissionKeys = cluster1RedisTemplate.keys("zoo:permission*");
        if (null != permissionKeys && permissionKeys.size() > 0) {
            for (String permissionKey : permissionKeys) {
                MncPermissionModel mncPermissionModel = new MncPermissionModel();
                String[] split = permissionKey.split(":");
                mncPermissionModel.setCountry(split[2]);
                mncPermissionModel.setMnc(split[3]);
                mncPermissionModel.setType("3");
                mncPermissionRepo.save(mncPermissionModel);
            }
        }

        Set<String> wifiKeys = cluster1RedisTemplate.keys("zoo:wifi*");
        if (null != wifiKeys && wifiKeys.size() > 0) {
            for (String wifiKey : wifiKeys) {
                MncPermissionModel mncPermissionModel = new MncPermissionModel();
                String[] split = wifiKey.split(":");
                mncPermissionModel.setCountry(split[2]);
                mncPermissionModel.setMnc(split[3]);
                mncPermissionModel.setType("4");
                mncPermissionRepo.save(mncPermissionModel);
            }
        }
        Set<String> autoMsisdns = cluster1RedisTemplate.keys("zoo:autoMsisdn*");
        if (null != autoMsisdns && autoMsisdns.size() > 0) {
            for (String autoMsisdn : autoMsisdns) {
                String value = cluster1RedisTemplate.opsForValue().get(autoMsisdn);
                JSONObject jsonObject = JSON.parseObject(value);
                MncPermissionModel mncPermissionModel = new MncPermissionModel();
                mncPermissionModel.setType("5");
                mncPermissionModel.setMnc(jsonObject.getString("mnc"));
                mncPermissionModel.setCountry(jsonObject.getString("country"));
                mncPermissionModel.setAreaCode(jsonObject.getString("areaCode"));
                mncPermissionModel.setRegex(jsonObject.getString("regex"));
                mncPermissionModel.setUrl(jsonObject.getString("url"));
                mncPermissionRepo.save(mncPermissionModel);
            }
        }
    }

    @Override
    @Async
    public void syncRedis() {
        List<MncPermissionModel> all = mncPermissionRepo.findAll();
        for (MncPermissionModel mcnPermission : all) {
            String type = mcnPermission.getType();
            if (!StringUtils.isEmpty(type)) {
                if (type.equalsIgnoreCase(String.valueOf(NumberEnum.TWO.getNum())) || type.equalsIgnoreCase(String.valueOf(NumberEnum.FIVE.getNum()))) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("country", mcnPermission.getCountry());
                    jsonObject.put("regex", mcnPermission.getRegex());
                    jsonObject.put("areaCode", mcnPermission.getAreaCode());
                    jsonObject.put("mnc", mcnPermission.getMnc());
                    jsonObject.put("url", mcnPermission.getUrl());
                    masterRedisTemplate.opsForHash().put(ZooConstant.MNC_PERMISSION + type, mcnPermission.getMnc(), JSON.toJSONString(jsonObject));
                } else {
                    masterRedisTemplate.opsForHash().put(ZooConstant.MNC_PERMISSION + type, mcnPermission.getMnc(), "1");
                }
            } else {
                masterRedisTemplate.opsForHash().put(ZooConstant.MNC_PERMISSION + type, mcnPermission.getMnc(), "1");
            }
        }
    }

    @Timed
    @Override
    public JSONObject findAppStatus(String appId) {
        JSONObject result = new JSONObject();
        String logStatus = "0";
        Integer adStatus = 0;
        String fbId = "";
        ApplicationModel applicationModel = applicationService.getById(appId);
        if (applicationModel != null) {
            logStatus = String.valueOf(applicationModel.getLogStatus());
            adStatus = applicationModel.getAdStatus() != null ? applicationModel.getAdStatus() : 0;
            fbId = StringUtils.isEmpty(applicationModel.getFbId()) ? fbId : applicationModel.getFbId();
        }
        result.put("logStatus", logStatus);
        result.put("adsStatus", adStatus);
        result.put("fbId", fbId);
        return result;
    }
}
