package com.starp.zoo.config.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author magic
 * @date 2021/6/16
 */
@Configuration
public class AppEventDataSourceConfig {

    @Bean(name = "appEventDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid.appevent")
    public DataSourceProperties appEventDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "appEventDataSource")
    @Qualifier("appEventDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.appevent")
    public DataSource appEventDataSource() {
        return DruidDataSourceBuilder.create().build();
    }
}
