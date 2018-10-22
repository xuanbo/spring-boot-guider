package com.example.springboot.mybatis.conf;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 配置Hikari数据源
 */
@Configuration
public class DataSourceConfiguration {

    private static final String PREFIX = "spring.datasource.hikari";

    @Bean
    @ConfigurationProperties(prefix = PREFIX)
    public HikariConfig hikariConfig() {
        return new HikariConfig();
    }

    @Bean
    @Primary
    public DataSource dataSource(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

}
