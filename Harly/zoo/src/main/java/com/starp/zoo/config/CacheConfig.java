package com.starp.zoo.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author david
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.cache.caffeine")
public class CacheConfig {

    private Map<String, CacheSpec> spec;

    @Data
    public static class CacheSpec {
        private Integer expireTime;
        private Integer maxSize;
        private Integer initCapacity;
    }

    @Bean
    public CacheManager cacheManager(Ticker ticker) {
        SimpleCacheManager manager = new SimpleCacheManager();
        if (spec != null) {
            List<CaffeineCache> caches =
                    spec.entrySet().stream()
                            .map(entry -> buildCache(entry.getKey(),
                                    entry.getValue(),
                                    ticker))
                            .collect(Collectors.toList());
            manager.setCaches(caches);
        }
        return manager;
    }

    private CaffeineCache buildCache(String name, CacheConfig.CacheSpec cacheSpec, Ticker ticker) {
        final Caffeine<Object, Object> caffeineBuilder
                = Caffeine.newBuilder()
                .initialCapacity(cacheSpec.getInitCapacity())
                .expireAfterWrite(cacheSpec.getExpireTime(), TimeUnit.SECONDS)
                .maximumSize(cacheSpec.getMaxSize())
                .ticker(ticker);
        return new CaffeineCache(name, caffeineBuilder.build());
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }
}
