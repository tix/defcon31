package com.starp.zoo.config.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author Vic on 2020/7/17
 */
@Configuration
public class LettuceRedisConfig {

    /**
     * 主节点(写权限)
     */
    @Value("${spring.redis.host}")
    private String host1;

    @Value("${spring.redis.port}")
    private int port1;

    @Value("${spring.redis.timeout}")
    private long timeout1;

    @Value("${spring.redis.lettuce.shutdown-timeout}")
    private long shutDownTimeout1;

    @Value("${spring.redis.database}")
    private int database1;

    @Value("${spring.redis.lettuce.pool.max-idle}")
    private int poolMaxIdle1;

    @Value("${spring.redis.lettuce.pool.max-active}")
    private int poolMaxActive1;

    @Value("${spring.redis.lettuce.pool.max-wait}")
    private long poolMaxWait1;

    @Value("${spring.redis.lettuce.pool.min-idle}")
    private int poolMinIdle1;

    /**
     * zoo 从节点1(拉取offer,mnc,epmlist)
     */
    @Value("${spring.redis2.host}")
    private String host2;

    @Value("${spring.redis2.port}")
    private int port2;

    @Value("${spring.redis2.timeout}")
    private long timeout2;

    @Value("${spring.redis2.lettuce.shutdown-timeout}")
    private long shutDownTimeout2;

    @Value("${spring.redis2.database}")
    private int database2;

    /**
     * zoo 从节点2(offer破解步骤)
     */
    @Value("${spring.redis3.host}")
    private String host3;

    @Value("${spring.redis3.port}")
    private int port3;

    @Value("${spring.redis3.timeout}")
    private long timeout3;

    @Value("${spring.redis3.lettuce.shutdown-timeout}")
    private long shutDownTimeout3;

    @Value("${spring.redis3.database}")
    private int database3;

    /**
     * zoo从节点3(epm计算，定时任务，页面查询redis)
     */
    @Value("${spring.redis4.host}")
    private String host4;

    @Value("${spring.redis4.port}")
    private int port4;

    @Value("${spring.redis4.timeout}")
    private long timeout4;

    @Value("${spring.redis4.lettuce.shutdown-timeout}")
    private long shutDownTimeout4;

    @Value("${spring.redis4.database}")
    private int database4;

    /**
     * 记录offer点击
     */
    @Value("${spring.redis5.host}")
    private String host5;

    @Value("${spring.redis5.port}")
    private int port5;

    @Value("${spring.redis5.timeout}")
    private long timeout5;

    @Value("${spring.redis5.lettuce.shutdown-timeout}")
    private long shutDownTimeout5;

    @Value("${spring.redis5.database}")
    private int database5;


    /**
     * 配置zoo master lettuce连接池
     *
     * @return
     */
    @Bean
    public GenericObjectPoolConfig redisPool() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(poolMaxIdle1);
        genericObjectPoolConfig.setMinIdle(poolMinIdle1);
        genericObjectPoolConfig.setMaxTotal(poolMaxActive1);
        genericObjectPoolConfig.setMaxWaitMillis(poolMaxWait1);
        return genericObjectPoolConfig;
    }

    /**
     * 配置zoo master数据源的
     *
     * @return
     */
    @Bean
    public RedisStandaloneConfiguration redisMasterConfig() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host1);
        redisStandaloneConfiguration.setPort(port1);
        redisStandaloneConfiguration.setDatabase(database1);
        return redisStandaloneConfiguration;
    }

    /**
     * 配置zoo 从节点1数据源
     *
     * @return
     */
    @Bean
    public RedisStandaloneConfiguration redisCluster1Config() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host2);
        redisStandaloneConfiguration.setPort(port2);
        redisStandaloneConfiguration.setDatabase(database2);
        return redisStandaloneConfiguration;
    }

    /**
     * 配置zoo 从节点2数据源
     *
     * @return
     */
    @Bean
    public RedisStandaloneConfiguration redisCluster2Config() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host3);
        redisStandaloneConfiguration.setPort(port3);
        redisStandaloneConfiguration.setDatabase(database3);
        return redisStandaloneConfiguration;
    }

    /**
     * 配置zoo 从节点3数据源
     *
     * @return
     */
    @Bean
    public RedisStandaloneConfiguration redisCluster3Config() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host4);
        redisStandaloneConfiguration.setPort(port4);
        redisStandaloneConfiguration.setDatabase(database4);
        return redisStandaloneConfiguration;
    }

    /**
     * 配置offer track数据源
     *
     * @return
     */
    @Bean
    public RedisStandaloneConfiguration redisTrackConfig() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host5);
        redisStandaloneConfiguration.setPort(port5);
        redisStandaloneConfiguration.setDatabase(database5);
        return redisStandaloneConfiguration;
    }



    /**
     * 配置第一个数据源的连接工厂
     * 这里注意：需要添加@Primary 指定bean的名称，目的是为了创建两个不同名称的LettuceConnectionFactory
     *
     * @param config
     * @param redisMasterConfig
     * @return
     */
    @Bean("masterFactory")
    @Primary
    public LettuceConnectionFactory factory(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisMasterConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().shutdownTimeout(Duration.ofMillis(shutDownTimeout1)).commandTimeout(Duration.ofMillis(timeout1)).poolConfig(config).build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisMasterConfig, clientConfiguration);
        lettuceConnectionFactory.setShareNativeConnection(false);
        return lettuceConnectionFactory;
    }

    @Bean("cluster1Factory")
    public LettuceConnectionFactory factory2(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisCluster1Config) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().shutdownTimeout(Duration.ofMillis(shutDownTimeout2)).commandTimeout(Duration.ofMillis(timeout2)).poolConfig(config).build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisCluster1Config, clientConfiguration);
        lettuceConnectionFactory.setShareNativeConnection(false);
        return lettuceConnectionFactory;
    }

    @Bean("cluster2Factory")
    public LettuceConnectionFactory factory3(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisCluster2Config) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().shutdownTimeout(Duration.ofMillis(shutDownTimeout3)).commandTimeout(Duration.ofMillis(timeout3)).poolConfig(config).build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisCluster2Config, clientConfiguration);
        lettuceConnectionFactory.setShareNativeConnection(false);
        return lettuceConnectionFactory;
    }

    @Bean("cluster3Factory")
    public LettuceConnectionFactory factory4(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisCluster3Config) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().shutdownTimeout(Duration.ofMillis(shutDownTimeout4)).commandTimeout(Duration.ofMillis(timeout4)).poolConfig(config).build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisCluster3Config, clientConfiguration);
        lettuceConnectionFactory.setShareNativeConnection(false);
        return lettuceConnectionFactory;
    }

    @Bean("trackFactory")
    public LettuceConnectionFactory factory5(GenericObjectPoolConfig config, RedisStandaloneConfiguration redisTrackConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().shutdownTimeout(Duration.ofMillis(shutDownTimeout5)).commandTimeout(Duration.ofMillis(timeout5)).poolConfig(config).build();
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisTrackConfig, clientConfiguration);
        lettuceConnectionFactory.setShareNativeConnection(false);
        return lettuceConnectionFactory;
    }


    /**
     * 配置第一个数据源的RedisTemplate
     * 注意：这里指定使用名称=factory 的 RedisConnectionFactory
     * 并且标识第一个数据源是默认数据源 @Primary
     *
     * @param factory
     * @return
     */
    @Bean
    @Primary
    public StringRedisTemplate masterRedisTemplate(@Qualifier("masterFactory") RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * 配置从节点1数据源的RedisTemplate
     * 注意：这里指定使用名称=factory2 的 RedisConnectionFactory
     *
     * @param factory2
     * @return
     */
    @Bean("cluster1RedisTemplate")
    public StringRedisTemplate cluster1RedisTemplate(@Qualifier("cluster1Factory") RedisConnectionFactory factory2) {
        return new StringRedisTemplate(factory2);
    }

    /**
     * 配置从节点2数据源的RedisTemplate
     * 注意：这里指定使用名称=factory3 的 RedisConnectionFactory
     *
     * @param factory3
     * @return
     */
    @Bean("cluster2RedisTemplate")
    public StringRedisTemplate cluster2RedisTemplate(@Qualifier("cluster2Factory") RedisConnectionFactory factory3) {
        return new StringRedisTemplate(factory3);
    }

    /**
     * 配置从节点3数据源的RedisTemplate
     * 注意：这里指定使用名称=factory4 的 RedisConnectionFactory
     *
     * @param factory4
     * @return
     */
    @Bean("cluster3RedisTemplate")
    public StringRedisTemplate cluster3RedisTemplate(@Qualifier("cluster3Factory") RedisConnectionFactory factory4) {
        return new StringRedisTemplate(factory4);
    }

    /**
     * 配置track数据源的RedisTemplate
     * 注意：这里指定使用名称=factory5 的 RedisConnectionFactory
     *
     * @param factory5
     * @return
     */
    @Bean("trackRedisTemplate")
    public StringRedisTemplate trackRedisTemplate(@Qualifier("trackFactory") RedisConnectionFactory factory5) {
        return new StringRedisTemplate(factory5);
    }

}