package com.starp.zoo.aop;

import com.starp.zoo.config.aws.sqs.BaseSqsMessage;
import com.starp.zoo.config.aws.sqs.Producer;
import com.starp.zoo.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Charles
 * @date 2019/6/3
 * @description :
 */
@Slf4j
@Aspect
@Component
public class AppUserEventAspect {

    @Autowired
    Producer producer;

    @Pointcut("execution(public * com.starp.zoo.service..*.sendToQuery(..))")
    public void webLog(){}

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        if(null == ret){
            return;
        }
        BaseSqsMessage baseSqsMessage = (BaseSqsMessage)ret;
        log.info("{} {} {} [{}:{}] {}", LogConstant.ZOO, LogConstant.APP_EVENT, LogConstant.BASE_MESSAGE_SQS_MESSAGES, baseSqsMessage.toString());
        try{
            producer.sendToQueueAppUserEvent(baseSqsMessage);
        }catch (Exception e) {
            log.error("{} {} {} [{}:{}] {}", LogConstant.ZOO, LogConstant.APP_EVENT, LogConstant.MESSAGE, baseSqsMessage.toString(), LogConstant.ERROR, e);
        }
    }
}
