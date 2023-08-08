package com.starp.zoo.config.aws.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Session;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author starp
 */
@Slf4j
@Component
public class Producer {

    @Resource
    protected JmsTemplate jmsTemplate;

    @Value("${sqs.url.appUserEvent}")
    String queueAppUserEvent;

    @Value("${sqs.url.zooOfferMaxPull}")
    String queueOfferOverMaxPull;

    @Value("${sqs.url.zooAppTrans}")
    String queueAppTrans;



    /**
     * 接口队列
     */
    @Value("${sqs.url.epmCalculate}")
    String queueEpmCalculate;

    @Value("${sqs.url.epmRetryCalculate}")
    String queueEpmRetryCalculate;

    @Value("${sqs.url.offerStart}")
    String queueOfferStart;

    @Value("${sqs.url.offerRunTime}")
    String queueOfferRunTime;

    @Value("${sqs.url.offerCheckUnused}")
    String queueOfferCheckUnused;

    @Value("${sqs.url.epmAlarm}")
    String queueEpmAlarm;

    @Value("${sqs.url.offerTransAlarm}")
    String queueOfferTransAlarm;

    @Resource
    ObjectMapper objectMapper;

    public void sendToQueueAppUserEvent(BaseSqsMessage message) {
        if(null != message && sizeof(message) < Constants.SQS_MAX_LENTH){
            send(queueAppUserEvent, message);
        }
    }

    public void sendToQueueOfferOverMaxPull(BaseSqsMessage message){
        send(queueOfferOverMaxPull,message);
    }

    public void sendToQueueOfferAppTrans(BaseSqsMessage message){
        send(queueAppTrans,message);
    }

    /**
     * 发送EPM 计算队列
     * @param message
     */
    public void sendToQueueEpmCalculate(BaseSqsMessage message){
        send(queueEpmCalculate,message);
    }

    /**
     * 发送EPM retry计算队列
     * @param message
     */
    public void sendToQueueEpmRetryCalculate(BaseSqsMessage message){
        send(queueEpmRetryCalculate,message);
    }

    /**
     * 发送offer 自动开启
     * @param message
     */
    public void sendToQueueOfferStart(BaseSqsMessage message){
        send(queueOfferStart,message);
    }

    /**
     * 发送offer 跑量队列
     * @param message
     */
    public void sendToQueueOfferRunTime(BaseSqsMessage message){
        send(queueOfferRunTime,message);
    }

    /**
     * 发送offer 刷新unused队列
     * @param message
     */
    public void sendToQueueOfferCheckUnused(BaseSqsMessage message){
        send(queueOfferCheckUnused,message);
    }

    /**
     * 发送epm 告警
     * @param message
     */
    public void sendToQueueEpmAlarm(BaseSqsMessage message){
        send(queueEpmAlarm,message);
    }



    /**
     * 发送 offer 转化告警
     * @param message
     */
    public void sendToQueueOfferTransAlarm(BaseSqsMessage message){
        send(queueOfferTransAlarm,message);
    }

    public <MESSAGE extends Serializable> void send(String queue, MESSAGE payload) {
        jmsTemplate.send(queue, new MessageCreator() {
            @Override
            public javax.jms.Message createMessage(Session session) throws JMSException {
                try {
                    javax.jms.Message createMessage = session.createTextMessage(objectMapper.writeValueAsString(payload));
                    return createMessage;
                } catch (Exception | Error e) {
                    log.error("{} {} {} {} {}{} {}{} {}{}", LogConstant.ZOO, LogConstant.AWS, LogConstant.SQS, LogConstant.SENDING, LogConstant.MESSAGE, payload, LogConstant.QUEUE, queue, LogConstant.ERROR, e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static int sizeof(Object obj){
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream)) {
            objectOutputStream.writeObject(obj);
            return byteOutputStream.toByteArray().length;
        } catch (IOException e) {
            log.error("Producer sizeof error:{}", e.getStackTrace());
        }
        return 1;
    }
}
