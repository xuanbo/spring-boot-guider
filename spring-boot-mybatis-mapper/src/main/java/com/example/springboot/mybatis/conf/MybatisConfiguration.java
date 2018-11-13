package com.example.springboot.mybatis.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class MybatisConfiguration {

    private static final String MAPPER_HELPER_PREFIX = "mapper";

    /**
     * 通用mapper的配置
     *
     * @return Properties
     */
    @Bean
    @ConfigurationProperties(MAPPER_HELPER_PREFIX)
    public Properties mapperProperties() {
        return new Properties();
    }

}
