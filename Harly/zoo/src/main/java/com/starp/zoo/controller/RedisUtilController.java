package com.starp.zoo.controller;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @Author vic
 * @Date 18:13 2018/12/18
 * @param
 * @return
 **/
@Controller
public class RedisUtilController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping(value = "/redis/hget", method = RequestMethod.GET)
    @ResponseBody
    public String hget(HttpServletRequest request, @RequestParam String key, @RequestParam(required = false) String field){
        if(!StringUtils.isEmpty(field)){
            JSONObject jsonObject = new JSONObject();
            if(null != stringRedisTemplate.opsForHash() && null != stringRedisTemplate.opsForHash().get(key, field)) {
                HashOperations<String, Object, Object> stringObjectObjectHashOperations = stringRedisTemplate.opsForHash();
                if(null != stringObjectObjectHashOperations){
                    Object o = stringObjectObjectHashOperations.get(key, field);
                    if(o != null ){
                        jsonObject.put(field, o.toString());
                    }
                }
            }
            return jsonObject.toJSONString();
        } else {
            return JSONObject.toJSONString(stringRedisTemplate.opsForHash().entries(key));
        }
    }
}
