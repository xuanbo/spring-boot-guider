# spring-boot-mybatis

> 快速集成mybatis

## 介绍

* [数据源](#数据源)
* [集成mybatis](#集成mybatis)
* [补充](#补充)

## 数据源

要操作数据库肯定离不开数据源，下面介绍数据源的配置，这里以`Hikari`数据源为例

### 依赖

这里使用mysql数据，依赖即可
```xml
<dependencies>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>2.6.2</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.40</version>
    </dependency>
</dependencies>
```

### 配置数据源

我们配置数据库连接信息
```yaml
spring:
  datasource:
    # hikari数据源配置
    hikari:
      driver-class-name: com.mysql.jdbc.Driver
      jdbc-url: jdbc:mysql://localhost:3306/demo?userUnicode=true&characterEncoding=UTF8&useSSL=false
      username: root
      password: 123456
```

这里简单的配置了下，并未设置一些例如最大连接数、超时等参数，详细参数见`Hikari`介绍。

配置一个DataSource bean
```java
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
```

注解`ConfigurationProperties`会将配置属性映射成`POJO`，更详细的使用见spring-boot文档。

### 测试数据源

编写单元测试测试下数据源是否配置正确
```java
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private DataSource dataSource;

    @Test
    public void dataSource() {
        LOG.info("dataSource: {}", dataSource.getClass());
    }

}
```

## 集成mybatis

这里介绍mybatis官网注解方式集成，xml就算了。。

### 依赖

依赖mybatis官方的`mybatis-spring-boot-starter`，并排除掉`tomcat-jdbc`数据源，用`Hikari`数据源
```xml
<dependencies>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>1.3.2</version>
        <exclusions>
            <exclusion>
                <groupId>org.apache.tomcat</groupId>
                <artifactId>tomcat-jdbc</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
</dependencies>
```

### 配置mybatis

我们配置mapper的xml文件位置以及包别名
```yaml
mybatis:
  # mapper的xml位置
  mapper-locations: classpath:mapper/*Mapper.xml
  # 包别名
  type-aliases-package: com.example.springboot.mybatis.entity
```

启动类添加`MapperScan`注解扫描mapper接口，**当然也可以不添加，因为对应的Mapper接口上添加了@Mapper注解**。
```java
@SpringBootApplication
// 也可以不用加扫描mapper接口
@MapperScan(AppConstant.MAPPER_SCAN_PACKAGE)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

这样最简单的环境就配置成功了

### 编写demo

#### 实体类

我们搞个实体类，其中`Entity`有一个`id`字段
```java
public class Demo extends Entity {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
```

#### Mapper接口

面向数据库的一些操作
```java
@Mapper
public interface DemoDao {

    int insert(Demo demo);

    int updateById(Demo demo);

    int deleteById(@Param("id") Long id);

    Demo findById(@Param("id") Long id);

    List<Demo> findAll();

}
```

`Mapper`注解是mybatis提供的，标记为一个Mapper，会被spring容器管理

#### Service

下面简单的写下接口及其实现
```java
public interface DemoService {

    int insert(Demo demo);

    int updateById(Demo demo);

    int deleteById(Long id);

    Demo findById(Long id);

    List<Demo> findAll();

}

@Service
@Transactional
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoDao demoDao;

    @Override
    public int insert(Demo demo) {
        return demoDao.insert(demo);
    }

    @Override
    public int updateById(Demo demo) {
        return demoDao.updateById(demo);
    }

    @Override
    public int deleteById(Long id) {
        return demoDao.deleteById(id);
    }

    @Override
    public Demo findById(Long id) {
        return demoDao.findById(id);
    }

    @Override
    public List<Demo> findAll() {
        return demoDao.findAll();
    }

}
```

`Transactional`注解开启事务

#### Controller

简单rest接口
```java
@RestController
@RequestMapping(DemoController.PATH)
@Validated
public class DemoController {

    static final String PATH = "/demo";

    @Autowired
    private DemoService demoService;

    @PostMapping
    public Rest<Demo> add(@Validated @RequestBody Demo demo) {
        demoService.insert(demo);
        return Rest.ok(MessageConstant.OK, demo);
    }

    @PutMapping
    public Rest<Demo> modify(@Validated @RequestBody Demo demo) {
        demoService.updateById(demo);
        return Rest.ok(MessageConstant.OK, demo);
    }

    @DeleteMapping("/{id}")
    public Rest<Integer> remove(@PathVariable Long id) {
        return Rest.ok(MessageConstant.OK, demoService.deleteById(id));
    }

    @GetMapping("/{id}")
    public Rest<Demo> find(@PathVariable Long id) {
        return Rest.ok(MessageConstant.OK, demoService.findById(id));
    }

    @GetMapping
    public Rest<List<Demo>> findAll() {
        return Rest.ok(MessageConstant.OK, demoService.findAll());
    }

}
```

其中`Validated`注解与校验参数有关，以后会与统一异常处理一起专门讲解

### 运行demo

运行后可用postman进行测试接口，略

## 补充

上面简单的集成了mybatis，但实际开发中还不够，我们需要通用Mapper、分页

这里介绍java config手动集成方式，对于官方提供的starter，这里不做集成与介绍。

### 集成pagehelper

我们在配置好了`mybatis-spring-boot-starter`的基础上再集成

#### 依赖

依赖`pagehelper`即可
```xml
<!-- mybatis分页插件 -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper</artifactId>
    <version>5.1.2</version>
</dependency>
```

#### 配置bean

将`PageInterceptor`注册为bean，即可被自动扫描，添加到mybatis的plugin中取
```java
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
```

#### 插件配置

我们还是习惯将将插件参数配置在`application.yml`，方便进行统一管理
```yaml
# 分页
pageHelper:
  helperDialect: mysql
  reasonable: "true"
  params: count=countSql
```

#### 测试

编写单元测试
```java
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class DemoDaoTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private DemoDao demoDao;

    @Test
    public void page() {
        PageHelper.startPage(2, 2);
        List<Demo> demos = demoDao.findAll();
        LOG.info("demos: {}", demos);
    }

}
```

### 集成通用Mapper

我们还是在配置好了`mybatis-spring-boot-starter`的基础上再集成

我**为了利用mybatis官方优秀的自动配置**，将通用Mapper的配置加入到其中

当然，如果用`mapper-spring-boot-starter`则很简单了，直接看通用mapper的官网即可，这里不做介绍

#### 依赖

推荐用4.0的
```xml
<!-- mybatis通用mapper -->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper</artifactId>
    <version>4.0.0</version>
</dependency>
```

#### 修改mybatis官方的自动配置

我们建一个`org.mybatis.spring.boot.autoconfigure`包，重写`MybatisAutoConfiguration`

主要添加了wrapperConf方法，替换为通用Mapper的Configuration

再将通用Mapper需要的配置定义为属性，让外部注入
```java
/**
 *    Copyright 2015-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.spring.boot.autoconfigure;

import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

/**
 * {@link EnableAutoConfiguration Auto-Configuration} for Mybatis. Contributes a
 * {@link SqlSessionFactory} and a {@link SqlSessionTemplate}.
 *
 * If {@link org.mybatis.spring.annotation.MapperScan} is used, or a
 * configuration file is specified as a property, those will be considered,
 * otherwise this auto-configuration will attempt to register mappers based on
 * the interface definitions in or under the root auto-configuration package.
 *
 * @author Eddú Meléndez
 * @author Josh Long
 * @author Kazuki Shimizu
 * @author Eduardo Macarrón
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

    private final MybatisProperties properties;

    private final Properties mapperProperties;

    private final Interceptor[] interceptors;

    private final ResourceLoader resourceLoader;

    private final DatabaseIdProvider databaseIdProvider;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    public MybatisAutoConfiguration(MybatisProperties properties,
                                    Properties mapperProperties,
                                    ObjectProvider<Interceptor[]> interceptorsProvider,
                                    ResourceLoader resourceLoader,
                                    ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                    ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        this.properties = properties;
        this.mapperProperties = mapperProperties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
    }

    @PostConstruct
    public void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(), "Cannot find config location: " + resource
                    + " (please add config file or check your Mybatis configuration)");
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        Configuration configuration = this.properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new Configuration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }

        // 这里替换为mapper中的Configuration，这样可以利用mybatis官方优秀的配置
        wrapperConf(factory, configuration);

        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            factory.setMapperLocations(this.properties.resolveMapperLocations());
        }

        return factory.getObject();
    }

    /**
     * 将mybatis官方的Configuration替换为通用Mapper的configuration
     *
     * @param factory SqlSessionFactoryBean
     * @param configuration mybatis官方的Configuration
     */
    private void wrapperConf(SqlSessionFactoryBean factory, Configuration configuration) {
        tk.mybatis.mapper.session.Configuration conf = new tk.mybatis.mapper.session.Configuration();
        if (configuration != null) {
            BeanUtils.copyProperties(configuration, conf);
        }
        conf.setMapperHelper(new MapperHelper(this.mapperProperties));
        factory.setConfiguration(conf);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    /**
     * This will just scan the same base package as Spring Boot does. If you want
     * more power, you can explicitly use
     * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
     * mappers working correctly, out-of-the-box, similar to using Spring Data JPA
     * repositories.
     */
    public static class AutoConfiguredMapperScannerRegistrar
            implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {

        private BeanFactory beanFactory;

        private ResourceLoader resourceLoader;

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

            logger.debug("Searching for mappers annotated with @Mapper");

            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

            try {
                if (this.resourceLoader != null) {
                    scanner.setResourceLoader(this.resourceLoader);
                }

                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
                if (logger.isDebugEnabled()) {
                    for (String pkg : packages) {
                        logger.debug("Using auto-configuration base package '{}'", pkg);
                    }
                }

                scanner.setAnnotationClass(Mapper.class);
                scanner.registerFilters();
                scanner.doScan(StringUtils.toStringArray(packages));
            } catch (IllegalStateException ex) {
                logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
            }
        }

        @Override
        public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }

        @Override
        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }
    }

    /**
     * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up
     * creating instances of {@link MapperFactoryBean}. If
     * {@link org.mybatis.spring.annotation.MapperScan} is used then this
     * auto-configuration is not needed. If it is _not_ used, however, then this
     * will bring in a bean registrar and automatically register components based
     * on the same component-scanning path as Spring Boot itself.
     */
    @org.springframework.context.annotation.Configuration
    @Import({ AutoConfiguredMapperScannerRegistrar.class })
    @ConditionalOnMissingBean(MapperFactoryBean.class)
    public static class MapperScannerRegistrarNotFoundConfiguration {

        @PostConstruct
        public void afterPropertiesSet() {
            logger.debug("No {} found.", MapperFactoryBean.class.getName());
        }
    }

}
```

#### 配置通用Mapper的配置bean

因为修改后的自动配置依赖外部提供通用mapper的配置，我们定义即可
```java
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
```

#### 自定义Mapper接口

不直接集成Mapper，而是自定义一个
```java
package com.example.springboot.mybatis.util;

import tk.mybatis.mapper.common.Mapper;

/**
 * 自定义Mapper接口
 *
 * @param <T> 实体
 */
public interface MyMapper<T> extends Mapper<T> {
}
```

#### 全局配置

我们还是习惯将将通用mapper参数配置在`application.yml`，方便进行统一管理
```yaml
mapper:
  mappers:
    - com.example.springboot.mybatis.util.MyMapper
  not-empty: true
```

#### 修改entity

我们添加`Id`、`Table`等注解

#### Mapper接口继承自定义Mapper接口

继承我们自定义的Mapper
```java
@Mapper
public interface DemoDao extends MyMapper<Demo> {
}
```

#### 测试

编写单元测试
```java
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class DemoDaoTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private DemoDao demoDao;

    @Test
    public void selectAll() {
        List<Demo> demos = demoDao.selectAll();
        LOG.info("demos: {}", demos);
    }

    @Test
    public void select() {
        Demo demo = new Demo();
        demo.setName("张三");
        List<Demo> demos = demoDao.select(demo);
        LOG.info("demos: {}", demos);
    }
}

```