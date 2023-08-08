package com.starp.zoo;

import cn.hutool.core.io.unit.DataSize;
import com.github.vanroy.springdata.jest.JestElasticsearchTemplate;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.searchbox.client.JestClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.servlet.MultipartConfigElement;

/**
 *
 * @Author vic
 * @Date 18:12 2018/12/18
 * @param
 * @return
 **/
@EnableAsync
@EnableCaching
@SpringBootApplication(exclude = {ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class})
public class  ZooApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZooApplication.class, args);
    }

    @Bean
    public JestElasticsearchTemplate elasticsearchTemplate(JestClient client) {
        return new JestElasticsearchTemplate(client);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "zoo");
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 单文件最大 20M
        factory.setMaxFileSize("20MB");
        // 设置总上传数据总大小(整个HTTP请求包含文件、参数) 30M
        factory.setMaxRequestSize("30MB");
        return factory.createMultipartConfig();
    }
}
