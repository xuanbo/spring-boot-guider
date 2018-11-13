package com.example.springboot.redis.conf;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfiguration {

    /**
     * 配置单实例
     *
     * @return Config
     */
    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("localhost:6379")
                .setDatabase(0);
        return config;
    }

    @Bean
    public RedissonClient redissonClient(Config config) {
        return Redisson.create(config);
    }

}
