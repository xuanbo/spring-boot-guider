# spring-boot-redis

> 快速集成redis

## 介绍

* [集成redis](#集成redis)
* [补充](#补充)

## 集成redis

### 依赖

依赖`spring-boot-starter-data-redis`模块
```xml
<!-- redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
    <version>${spring.boot.version}</version>
</dependency>
```

### 全局配置

在`application.yml`中配置redis的连接信息
```yaml
spring:
  # redis配置
  redis:
    database: 0
    host: 127.0.0.1
    port: 6379
    pool:
      max-active: 16
      max-idle: 8
```

支持哨兵、cluster模式，参考官网配置

从`RedisAutoConfiguation`中可以看到springboot默认帮我们配置了`RedisTemplate`、`StringRedisTemplate`两个操作redis的模板bean

### 使用

简单的使用`StringRedisTemplate`进行`string`数据结构的操作
```java
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    private static final String KEY = "com.example.spring.boot";
    private static final String VALUE = "redis";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void setAndGet() {
        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps(KEY);
        ops.set(VALUE);
        LOG.info("get key[{}] -> {}", KEY, ops.get());
    }

}
```

## 补充

`spring-boot-starter-data-redis`模块帮我们自动配置了`Jedis`，往往我们会有分布式锁、异步等需求，这时`redisson`是一个更好的选择。

### redisson

`redisson`我们用的最多的就是分布式锁，下面介绍与spring-boot的集成以及分布式锁的使用

#### 依赖

```xml
<!-- redisson -->
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson</artifactId>
    <version>3.7.0</version>
</dependency>
```

#### 配置single节点

我们测试一般用redis单实例，因此redisson配置单实例即可（支持哨兵、cluster）

```java
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
```

这里就简单配置单实例，其他配置采用默认配置

#### 分布式锁模板代码

```java
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    private static final String LOCK_KEY = "lock";

    @Autowired
    private RedissonClient redissonClient;

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
```