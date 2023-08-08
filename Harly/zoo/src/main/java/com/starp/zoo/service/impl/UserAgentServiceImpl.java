package com.starp.zoo.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.starp.zoo.service.IUserAgentService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URL;

/**
 * @author david
 */
@Service
@Slf4j
public class UserAgentServiceImpl implements IUserAgentService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    @SuppressFBWarnings({"DM_NEXTINT_VIA_NEXTDOUBLE", "DM_BOXED_PRIMITIVE_FOR_PARSING"})
    public JSONObject getGlobalHeaders(String url, boolean isRedirect, String userId){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("User-Agent", "Mozilla/5.0 (Linux; Android 9; vivo 1902 Build/PPR1.180610.011; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/89.0.4389.90 Mobile Safari/537.36");
        jsonObject.put("Accept","text/html,text/plain,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        jsonObject.put("Accept-Language","en-US,en;q=0.9");
        if(!StringUtils.isEmpty(url) && !isRedirect){
            jsonObject.put("Referer", url);
            String origin = getOrigin(url);
            if(!StringUtils.isEmpty(origin)){
                jsonObject.put("Origin", origin);
            }
        }

        return jsonObject;
    }

    public  String getOrigin(String url){
        StringBuilder origin = new StringBuilder();
        try {
            URL url1 = new URL(url);
            origin.append(url1.getProtocol())
                    .append("://")
                    .append(url1.getHost());
        }catch (Exception e) {
            log.error("ProtocolCrackUtil getGlobalHeaders error:", e);
        }
        return origin.toString();
    }
}
