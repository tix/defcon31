package com.starp.zoo.config.redis;

import com.starp.zoo.constant.NumberEnum;
import com.starp.zoo.constant.ZooConstant;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.io.IOException;

/**
 * @author vic on 18/9/5.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds=60)
public class RedisSessionConfig {

    @Autowired
    private Environment env;

    /**
     * 主节点(写权限)
     */
    @Value("${spring.redis.host}")
    private String host1;

    @Value("${spring.redis.port}")
    private int port1;

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        String host = env.getProperty("spring.redis.host");
        redisStandaloneConfiguration.setHostName(host != null ? host : ZooConstant.LOCALHOST);
        String portStr = env.getProperty("spring.redis.port");
        int port = portStr != null ? Integer.parseInt(portStr) : NumberEnum.SIX_THOUSAND_THREE_HUNDRED_AND_SEVENTY_NINE.getNum();
        redisStandaloneConfiguration.setPort(port);
        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration,
                lettuceClientConfigurationBuilder.build());
        return factory;
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host1 + ":" + port1).setTimeout(2000);
        return Redisson.create(config);
    }
}
