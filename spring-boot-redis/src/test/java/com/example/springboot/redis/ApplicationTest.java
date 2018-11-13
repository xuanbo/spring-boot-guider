package com.example.springboot.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    private static final String KEY = "com.example.spring.boot";
    private static final String VALUE = "redis";
    private static final String LOCK_KEY = "lock";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void setAndGet() {
        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps(KEY);
        ops.set(VALUE);
        LOG.info("get key[{}] -> {}", KEY, ops.get());
    }

    @Test
    public void lock() {
        RLock lock = redissonClient.getLock(LOCK_KEY);
        try {
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                try {
                    LOG.info("获取到锁:{}", LOCK_KEY);
                    // 做一些事情
                    Thread.sleep(2000L);
                } finally {
                    lock.unlock();
                    LOG.info("释放锁:{}", LOCK_KEY);
                }
            } else {
                LOG.warn("没有获取到锁:{}", LOCK_KEY);
            }
        } catch (InterruptedException e) {
            LOG.error("获取锁被打断:{}", LOCK_KEY);
        }
    }

}
