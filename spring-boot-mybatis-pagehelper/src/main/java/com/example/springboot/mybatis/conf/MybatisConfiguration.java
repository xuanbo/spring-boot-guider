package com.example.springboot.mybatis.conf;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class MybatisConfiguration {

    private static final String PAGE_HELPER_PREFIX = "pageHelper";

    /**
     * 分页插件配置
     *
     * @return Properties
     */
    @Bean
    @ConfigurationProperties(PAGE_HELPER_PREFIX)
    public Properties pageProperties() {
        return new Properties();
    }

    /**
     * 注册分页插件
     *
     * @return PageInterceptor
     */
    @Bean
    public Interceptor pageInterceptor(Properties pageProperties){
        Interceptor pageInterceptor = new PageInterceptor();
        pageInterceptor.setProperties(pageProperties);
        return pageInterceptor;
    }

}
