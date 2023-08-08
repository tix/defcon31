package com.starp.zoo.config.aws.sqs;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.starp.zoo.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.annotation.PostConstruct;
import javax.jms.Session;

/**
 * @author starp
 */
@Slf4j
@Configuration
@EnableJms
public class JmsConfig {

    @PostConstruct
    public void init() {
        log.info("{} {} {} JMSCONFIG STARTED.", LogConstant.ZOO, LogConstant.AWS, LogConstant.SQS);
    }

    private final SQSConnectionFactory connectionFactory;

    public JmsConfig() {
        connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(),
                AmazonSQSClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_1)
                        .withCredentials(new AWSStaticCredentialsProvider(
                                        new BasicAWSCredentials(System.getProperty("aws.access_key"), System.getProperty("aws.secret_access_key"))
                                )
                        )
        );
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory
                = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(this.connectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setConcurrency("1-6");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate(this.connectionFactory);
        return jmsTemplate;
    }
}
