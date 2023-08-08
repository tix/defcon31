package com.starp.zoo.config.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
/**
 *
 * @Author vic
 * @Date 18:06 2018/12/18
 * @param
 * @return
 **/
@Configuration
public class StatisticsDataSourceConfig {
    
    @Bean(name = "statisticsDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid.statistics")
    public DataSourceProperties statisticsDataSourceProperties() {
        return new DataSourceProperties();
    }
    
    @Bean(name = "statisticsDataSource")
    @Qualifier("statisticsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.statistics")
    public DataSource secondaryDataSource(){
        return DruidDataSourceBuilder.create().build();
    }
}
