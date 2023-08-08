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
 * @Date 18:10 2018/12/18
 * @param
 * @return
 **/
@Configuration
public class ZooDataSourceConfig {

    @Primary
    @Bean(name = "zooDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid.zoo")
    public DataSourceProperties zooDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "zooDataSource")
    @Qualifier("zooDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.zoo")
    public DataSource secondaryDataSource(){
        return DruidDataSourceBuilder.create().build();
    }
}
