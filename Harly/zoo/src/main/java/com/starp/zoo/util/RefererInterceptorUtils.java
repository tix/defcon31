package com.starp.zoo.util;

import com.starp.zoo.common.constant.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Vic on 2020/5/22
 */
@Component
@Slf4j
public class RefererInterceptorUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean validReferer(HttpServletRequest req){
        String refererRegxsKey = RedisConstants.NAMESPACE_HTTP + RedisConstants.NAMESPACE_REFERER + RedisConstants.NAMESPACE_REFERER_URL_REGXS;
        String sessionRefKey = getRegxs(req, refererRegxsKey);
        //如果需要referer验证，并且验证不通过则跳转至error页面
        String referer = req.getHeader("referer");
        HttpSession session = req.getSession();
        if(!StringUtils.isEmpty(sessionRefKey)){
            Object sessionRef = session.getAttribute(sessionRefKey);
            if (StringUtils.isEmpty(sessionRef) || StringUtils.isEmpty(referer) || !String.valueOf(sessionRef).equals(referer)) {
                return false;
            }
        }
        return true;
    }

    public String getRegxs(HttpServletRequest req, String refererRegxsKey){
        Map<Object, Object> refererRegxsMap = stringRedisTemplate.opsForHash().entries(refererRegxsKey);
        StringBuffer requestURL = req.getRequestURL();
        String sessionRefKey = "";
        if(null != refererRegxsMap){
            Iterator<Object> iterator = refererRegxsMap.keySet().iterator();
            while (iterator.hasNext()) {
                String regx = String.valueOf(iterator.next());
                if (Pattern.matches(regx, requestURL)) {
                    sessionRefKey =  String.valueOf(refererRegxsMap.get(regx));
                    break;
                }
            }
        }
        return sessionRefKey;
    }
}
