package com.example.springboot.web.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * web配置
 */
@Configuration
public class WebConfiguration {

    /**
     * 配置RestTemplate，默认实现为JDK的URLConnection
     *
     * @return RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
