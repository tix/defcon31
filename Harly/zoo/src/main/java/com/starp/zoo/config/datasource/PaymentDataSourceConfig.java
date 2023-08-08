package com.starp.zoo.config.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 *
 * @Author vic
 * @Date 18:11 2018/12/18
 * @param
 * @return
 **/
@Configuration
public class PaymentDataSourceConfig {

    @Bean(name = "paymentDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.druid.payment")
    public DataSourceProperties paymentDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "paymentDataSource")
    @Qualifier("paymentDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid.payment")
    public DataSource paymentDataSource(){
        return DruidDataSourceBuilder.create().build();
    }
}
