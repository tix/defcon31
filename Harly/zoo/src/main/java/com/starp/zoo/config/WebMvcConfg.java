//package com.starp.zoo.config;
//
//import com.starp.zoo.common.RefererInterceptor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * @author Vic on 2020/5/22
// */
//@Configuration
//public class WebMvcConfg implements WebMvcConfigurer {
//
//    @Bean
//    public RefererInterceptor refererInterceptor() {
//        return new RefererInterceptor();
//    }
//
//    /**
//     * 注册拦截器
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        //referer拦截
//        registry.addInterceptor(refererInterceptor());
//    }
//}
