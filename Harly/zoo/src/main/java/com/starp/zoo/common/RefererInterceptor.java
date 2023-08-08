package com.starp.zoo.common;


import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.common.constant.RedisConstants;
import com.starp.zoo.util.RefererInterceptorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

/**
 * @author Vic on 2020/5/22
 */
@Configuration
public class RefererInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    RefererInterceptorUtils refererInterceptorUtils;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {

        //验证referer
        boolean refererIsValid = refererInterceptorUtils.validReferer(req);
        if(!refererIsValid){
            resp.sendRedirect("/wap/error");
            return false;
        }

        if(!StringUtils.isEmpty(req.getRequestURL()) && req.getRequestURL().indexOf(Constants.DTAC)>0){
            String header = req.getHeader("X-Requested-With");
            if(!StringUtils.isEmpty(header) && Pattern.matches(Constants.PARTERN_REGEX, header)){
                resp.sendRedirect("/wap/error");
                return false;
            }
        }

        //保存referer
        String regxsKey = RedisConstants.NAMESPACE_HTTP + RedisConstants.NAMESPACE_REFERER + RedisConstants.NAMESPACE_URL_REGXS;
        String sessionRefKey = refererInterceptorUtils.getRegxs(req, regxsKey);

        HttpSession session = req.getSession();
        if(!StringUtils.isEmpty(sessionRefKey)){
            session.setAttribute(sessionRefKey, req.getRequestURL());
        }

        return true;
    }
}
