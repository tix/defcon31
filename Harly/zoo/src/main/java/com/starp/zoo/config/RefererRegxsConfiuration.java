package com.starp.zoo.config;


import com.starp.zoo.common.constant.RedisConstants;
import com.starp.zoo.entity.zoo.RefererRegxModel;
import com.starp.zoo.repo.zoo.RefererRegxRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vic on 2020/5/22
 */
@Component
public class RefererRegxsConfiuration {

    @Autowired
    RefererRegxRepo refererRegxRepo;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void initRefererRegx(){

        //从数据库中查询出最新的Regxs
        List<RefererRegxModel> all = refererRegxRepo.findAll();

        //删除redis中的历史Regxs
        String urlRegxsKey = RedisConstants.NAMESPACE_HTTP + RedisConstants.NAMESPACE_REFERER + RedisConstants.NAMESPACE_URL_REGXS;
        stringRedisTemplate.delete(urlRegxsKey);

        String refererUrlRegxsKey = RedisConstants.NAMESPACE_HTTP + RedisConstants.NAMESPACE_REFERER + RedisConstants.NAMESPACE_REFERER_URL_REGXS;
        stringRedisTemplate.delete(refererUrlRegxsKey);

        Map<Object, Object> regxsMap = new HashMap<>(1);
        Map<Object, Object> refererUrlRegxsMap = new HashMap<>(1);
        for(RefererRegxModel refererRegxModel : all){
            regxsMap.put(refererRegxModel.getUrlRegx(), refererRegxModel.getIdentification());
            refererUrlRegxsMap.put(refererRegxModel.getRefererUrlRegx(), refererRegxModel.getIdentification());
        }

        //将查询出的Regxs同步到redis
        stringRedisTemplate.opsForHash().putAll(urlRegxsKey, regxsMap);
        stringRedisTemplate.opsForHash().putAll(refererUrlRegxsKey, refererUrlRegxsMap);
    }
}
