package com.starp.zoo.config.aws.sqs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazon.sqs.javamessaging.message.SQSTextMessage;
import com.starp.zoo.common.constant.Constants;
import com.starp.zoo.constant.*;
import com.starp.zoo.entity.zoo.AffPostBackIeModel;
import com.starp.zoo.entity.zoo.AffPostBackModel;
import com.starp.zoo.entity.zoo.AppUserEventModel;
import com.starp.zoo.entity.zoo.OfferModel;
import com.starp.zoo.service.*;
import com.starp.zoo.util.DateUtil;
import com.starp.zoo.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author starp
 */
@Slf4j
@Component
public class Consumer {

    @Value("${spring.mail.default-to}")
    private String defaultEmail;

    @Value("${sqs.url.sendAffPostBack}")
    String queueSendAffPostBack;

    @Value("${sqs.url.affApkPostBack}")
    String queueAffApkPostBack;


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

    @Autowired
    private IAffService affService;

    @Autowired
    private StringRedisTemplate masterRedisTemplate;


    @Autowired
    private IGetOfferService getOfferService;


    @Autowired
    private IOfferService offerService;

    @Autowired
    private IAppTranService appTranService;

    @Autowired
    private IEpmService epmService;

    @Autowired
    private Environment environment;

    @Autowired
    private EmailUtil emailUtil;

    @JmsListener(destination = "${sqs.url.sendAffPostBack}")
    public void processMessageSendAffPostBack(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                String msgName = baseSqsMessage.getMsgName();
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                if(msgName != null && ZooConstant.AFF_POST_BACK_MODEL.equals(msgName)){
                    AffPostBackModel affPostBackModel = JSON.parseObject(msgBodyStr, AffPostBackModel.class);
                    affService.savePostBack(affPostBackModel);
                }
                if(msgName != null && ZooConstant.AFF_POST_BACK_IE_MODEL.equals(msgName)){
                    AffPostBackIeModel affPostBackModel = JSON.parseObject(msgBodyStr, AffPostBackIeModel.class);
                    affService.save(affPostBackModel);
                }
            }
        }catch (Exception e) {
            checkError(queueSendAffPostBack, message, e);
            throw e;
        }
    }

    @JmsListener(destination = "${sqs.url.zooOfferMaxPull}")
    public void processMessageSendOfferOverMaxPull(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                getOfferService.sendOfferMaxPullMail(msgBodyStr);

            }
        }catch (Exception e) {
            checkError(queueOfferOverMaxPull, message, e);
            throw e;
        }
    }

    @JmsListener(destination = "${sqs.url.zooAppTrans}")
    public void processMessageSendAppTrans(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                appTranService.sendOfferAppTrans(msgBodyStr);
            }
        }catch (Exception e) {
            checkError(queueAppTrans, message, e);
            throw e;
        }
    }

    @JmsListener(destination = "${sqs.url.affApkPostBack}")
    public void processMessageAffApkPostBack(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                String msgName = baseSqsMessage.getMsgName();
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                if(msgName != null && ZooConstant.AFF_APK_POSTBACK_MODEL.equals(msgName)){
                    JSONObject affApkPostback = JSONObject.parseObject(msgBodyStr);
                    affService.saveAffApkPostBack(affApkPostback);
                }
            }
        }catch (Exception e) {
            checkError(queueAffApkPostBack, message, e);
            throw e;
        }
    }




    /**
     * 处理EPM 计算
     * @param message
     * @throws Exception
     */
    @JmsListener(destination = "${sqs.url.epmCalculate}")
    public void processMessageEpmCalculate(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                String msgName = baseSqsMessage.getMsgName();
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                log.info("EPM CALCULATE SQS [STEP-2] INFO:{}",msgBodyStr);
                if(msgName != null && ZooConstant.EPM_CALCULATE_KEY.equals(msgName)){
                    epmService.handleEpmCalculate(msgBodyStr);
                }
            }
        }catch (Exception e) {
            checkError(queueEpmCalculate, message, e);
            throw e;
        }
    }


    /**
     * 处理retry EPM 计算
     * @param message
     * @throws Exception
     */
    @JmsListener(destination = "${sqs.url.epmRetryCalculate}")
    public void processMessageEpmRetryCalculate(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                String msgName = baseSqsMessage.getMsgName();
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                if(msgName != null && ZooConstant.EPM_RETRY_CALCULATE_KEY.equals(msgName)){
                    epmService.handleEpmCalculate(msgBodyStr);
                }
            }
        }catch (Exception e) {
            checkError(queueEpmRetryCalculate, message, e);
            throw e;
        }
    }


    /**
     * 处理offer自动开关
     * @param message
     * @throws Exception
     */
    @JmsListener(destination = "${sqs.url.offerStart}")
    public void processMessageOfferAutoStart(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                String msgName = baseSqsMessage.getMsgName();
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                Integer retryTime = JSONObject.parseObject(msgBodyStr).getInteger("retryTime");
                if(msgName != null && ZooConstant.OFFER_AUTO_START_MODEL.equals(msgName)){
                    if (retryTime != null) {
                        offerService.handleAutoStartRetry(msgBodyStr, retryTime);
                    } else {
                        offerService.handleAutoStart(msgBodyStr);
                    }
                }
            }
        }catch (Exception e) {
            checkError(queueOfferStart, message, e);
            throw e;
        }
    }


    /**
     * 刷新offer unused
     * @param message
     * @throws Exception
     */
    @JmsListener(destination = "${sqs.url.offerCheckUnused}")
    public void processMessageRefreshOfferCap(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                String msgName = baseSqsMessage.getMsgName();
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                if(msgName != null && ZooConstant.QUEUE_OFFER_REFRESH_UNUSED_MODEL.equals(msgName)){
                    epmService.handleUnUseOffer(msgBodyStr);
                }
            }
        }catch (Exception e) {
            checkError(queueOfferCheckUnused, message, e);
            throw e;
        }
    }



    /**
     *  app 运营商 epm下降告警
     * @param message
     * @throws Exception
     */
    @JmsListener(destination = "${sqs.url.epmAlarm}")
    public void processMessageAppEpmAlarm(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                String msgName = baseSqsMessage.getMsgName();
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                if(msgName != null && ZooConstant.QUEUE_APP_EPM_ALARM_MODEL.equals(msgName)){
                    offerService.checkAlarmEpm(msgBodyStr);
                }
            }
        }catch (Exception e) {
            checkError(queueEpmAlarm, message, e);
            throw e;
        }
    }



    /**
     *  offer转化告警
     * @param message
     * @throws Exception
     */
    @JmsListener(destination = "${sqs.url.offerTransAlarm}")
    public void processMessageOfferTransAlarm(@Payload final Object message) throws Exception{
        if(null == message){
            return;
        }
        try {
            if(message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                String msgName = baseSqsMessage.getMsgName();
                if(baseSqsMessage.getMsgBody() == null){
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                if(msgName != null && ZooConstant.QUEUE_OFFER_CHECK_TRANS_MODEL.equals(msgName)){
                    epmService.handleCheckOfferTrans(msgBodyStr);
                }
            }
        }catch (Exception e) {
            checkError(queueOfferTransAlarm, message, e);
            throw e;
        }
    }


    /**
     *  offer跑量时间检查
     * @param message
     * @throws Exception
     */
    @JmsListener(destination = "${sqs.url.offerRunTime}")
    public void processMessageOfferRunTime(@Payload final Object message) throws Exception {
        if (null == message) {
            return;
        }
        try {
            if (message instanceof SQSTextMessage) {
                SQSTextMessage textMsg = (SQSTextMessage) message;
                BaseSqsMessage baseSqsMessage = JSON.parseObject(textMsg.getText(), BaseSqsMessage.class);
                String msgName = baseSqsMessage.getMsgName();
                if (baseSqsMessage.getMsgBody() == null) {
                    return;
                }
                String msgBodyStr = baseSqsMessage.getMsgBody().toString();
                if (msgName != null && ZooConstant.QUEUE_OFFER_RUNTIME_MODEL.equals(msgName)) {
                    offerService.handleOfferRunTime(msgBodyStr);
                }
            }
        } catch (Exception e) {
            checkError(queueOfferRunTime, message, e);
        }
    }




    @Async
    public void checkError(String queueName, Object message, Exception e) throws Exception {
        //判断 redis 是否存在该条记录
        //从redis 检查是否存在当前错误记录
        SQSTextMessage textMsg = (SQSTextMessage) message;
        String msgId = textMsg.getSQSMessageId();
        String redisKey = CacheNameSpace.QUEUE_CONSUMER + msgId;
        Long isChecked = masterRedisTemplate.opsForValue().increment(redisKey, 1);
        if (null != isChecked && isChecked == 1){
            // 设置10分钟过期
            masterRedisTemplate.expire(redisKey, NumberEnum.TEN.getNum(), TimeUnit.MINUTES);
            String messageStr = JSON.toJSONString(message);
            //如果不存在则打印日志并发送邮件
            log.error("{} {} {} {} [{} {}] {}", LogConstant.ZOO, LogConstant.CONSUMER, LogConstant.QUEUE, queueName
                    ,LogConstant.MESSAGE, messageStr, LogConstant.ERROR, e);
            if(message instanceof SQSTextMessage) {
                messageStr = ((TextMessage) message).getText();
            }
            Map<String, Object> contentModel = new HashMap<>(8);
            contentModel.put(ZooConstant.TITLE, ZooConstant.QUEUE_MAIL_SUBJECT);
            contentModel.put(ZooConstant.EMAIL_DATE, DateUtil.formatyyyyMMddHHmmss(new Date()));
            contentModel.put(ZooConstant.SYSTEM, LogConstant.ZOO);
            contentModel.put(ZooConstant.QUEUE_NAME, queueName);
            contentModel.put(ZooConstant.MESSAGE_ID, msgId);
            contentModel.put(ZooConstant.MESSAGE, messageStr);
            contentModel.put(ZooConstant.EMAIL_UUID, UUID.randomUUID().toString());
            contentModel.put(ZooConstant.EXCEPTION, Consumer.getStackString(e));
            String profile = String.format(" 【%s】", environment.getActiveProfiles()[0].toUpperCase());
            emailUtil.sendMimeMessageMail(ZooConstant.QUEUE_MAIL_TEMPLATE, defaultEmail, ZooConstant.QUEUE_MAIL_SUBJECT + profile, contentModel);
        }
    }

    private static String getStackString(Exception e){
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.close();
            pw.close();
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "ErrorInfoFromException(异常堆栈打印错误)";
        }
    }
}
