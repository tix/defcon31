package com.starp.zoo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.constant.CacheNameSpace;
import com.starp.zoo.constant.ZooConstant;
import com.starp.zoo.entity.zoo.AppEpmModel;
import com.starp.zoo.service.IAppEpmResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author david
 */

@Service
public class AppEpmResetServiceImpl implements IAppEpmResetService {

    @Autowired
    private StringRedisTemplate masterRedisTemplate;

    @Resource(name = "cluster3RedisTemplate")
    private StringRedisTemplate cluster3RedisTemplate;

    @Override
    public void saveModel(JSONObject params) {
        String country = params.getString(ZooConstant.COUNTRY);
        int epmResetHour = params.getInteger("epmRestHour");
        int epmRestExponentiation = params.getInteger("epmRestExponentiation");
        String epmResetKey = CacheNameSpace.APP_EPM_RESET;
        AppEpmModel appEpmModel = new AppEpmModel();
        appEpmModel.setCountry(country);
        appEpmModel.setEpmRestHour(epmResetHour);
        appEpmModel.setEpmRestExponentiation(epmRestExponentiation);
        masterRedisTemplate.opsForHash().put(epmResetKey, country, JSON.toJSONString(appEpmModel));
    }

    @Override
    public AppEpmModel getModel(String country) {
        AppEpmModel appEpmModel = getModelFromRedis(country);
        return appEpmModel;
    }

    private AppEpmModel getModelFromRedis(String country) {
        AppEpmModel appEpmModel = null;
        String epmResetKey = CacheNameSpace.APP_EPM_RESET;
        if (StringUtils.isEmpty(country)) {
            return null;
        }
        Object obj = cluster3RedisTemplate.opsForHash().get(epmResetKey, country);
        if (obj != null) {
            appEpmModel = JSONObject.parseObject(String.valueOf(obj), AppEpmModel.class);
        }
        return appEpmModel;
    }

    @Override
    public List<AppEpmModel> getModelList(List<String> countryList) {
        List<AppEpmModel> appEpmModelList = new ArrayList<>();
        if (countryList != null && countryList.size() > 0) {
            for (String country : countryList) {
                AppEpmModel appEpmModel = getModelFromRedis(country);
                if (appEpmModel != null) {
                    appEpmModelList.add(appEpmModel);
                }
            }
        } else {
            Set<Object> keys = cluster3RedisTemplate.opsForHash().keys(CacheNameSpace.APP_EPM_RESET);
            if (keys != null && keys.size() > 0) {
                for (Object key : keys) {
                    AppEpmModel appEpmModel = getModelFromRedis(key.toString());
                    appEpmModelList.add(appEpmModel);
                }
            }
        }
        return appEpmModelList;
    }

    @Override
    public void deleteModel(String country) {
        String epmResetKey = CacheNameSpace.APP_EPM_RESET;
        if (!StringUtils.isEmpty(country)) {
            masterRedisTemplate.opsForHash().delete(epmResetKey, country);
        }
    }

    @Override
    public void resetEpmHour(JSONObject jsonObject) {
        String country = jsonObject.getString(ZooConstant.COUNTRY);
        int resetHour = jsonObject.getInteger("epmRestHour");
        AppEpmModel appEpmModel = getModelFromRedis(country);
        if (appEpmModel != null) {
            appEpmModel.setEpmRestHour(resetHour);
            masterRedisTemplate.opsForHash().put(CacheNameSpace.APP_EPM_RESET, country, JSON.toJSONString(appEpmModel));
        }
    }

    @Override
    public void resetEpmExponentiation(JSONObject jsonObject) {
        String country = jsonObject.getString(ZooConstant.COUNTRY);
        int resetExponentiation = jsonObject.getInteger("epmRestExponentiation");
        AppEpmModel appEpmModel = getModelFromRedis(country);
        if (appEpmModel != null) {
            appEpmModel.setEpmRestExponentiation(resetExponentiation);
            masterRedisTemplate.opsForHash().put(CacheNameSpace.APP_EPM_RESET, country, JSON.toJSONString(appEpmModel));
        }
    }
}
