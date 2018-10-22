# spring-boot-web

> web快速入门

## 介绍

* [web入门](#web入门)
* [日志打印](#日志打印)
* [测试](#测试)
* [补充](#补充)

## web入门

一个简单的demo，以及spring-boot的**约定**(套路)

### 依赖

```xml
<!-- web依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>${spring.boot.version}</version>
</dependency>
```

### 插件

```xml
<plugins>
    <!-- java 1.8编译，推荐 -->
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.0</version>
        <configuration>
            <source>1.8</source>
            <target>1.8</target>
        </configuration>
    </plugin>
    
    <!-- spring boot插件 -->
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>${spring.boot.version}</version>
        <executions>
            <execution>
                <goals>
                    <goal>repackage</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
```

### 项目结构

推荐的如下结构
```
com
 +- example
     +- myproject
         +- Application.java
         |
         +- domain
         |   +- Customer.java
         |   +- CustomerRepository.java
         |
         +- service
         |   +- CustomerService.java
         |
         +- web
             +- CustomerController.java
```

`Application`为启动类，推荐在包的外层，这样可以保证到子包中的bean可以被默认扫描到

### 入口类

入口类采用注解`@SpringBootApplication`，会自动配置扫包等
```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### 自定义配置

自定义配置一般推荐javaconfig的形式配置，添加`@Configuration`
```java
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
```

这样就配置了一个bean，与xml中`<bean class="org.springframework.web.client.RestTemplat"/>`等同

### 运行

* 开发时，一般直接运行`Application.java`中的main方法，或者`mvn clean spring-boot:run`启动
* 部署时，先打jar包，再以`java -jar xxx.jar`的方式运行。
    ```shell
    mvn clean package -DskipTests
    ```

## 日志打印

### 全局配置

在`application.yml`中配置即可
```yaml
# 设置日志级别，当然我们也可以采用配置文件配置
logging:
  level:
    root: info
    # com.example.springboot包日志打印级别
    com:
      example:
        springboot: debug
  # 自定义logback日志配置，推荐命名为logback-spring.xml
  # config: logback-spring.xml
```

### 自定义logback配置文件

首先在`application.yml`中配置日志路径
```yaml
logging:
  # 自定义logback日志配置，推荐命名为logback-spring.xml
  config: logback-spring.xml
```

然后配置logback日志即可，如果命名为logback-spring.xml则会自动配置，不需要指定
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
    <!--定义日志文件的存储地址 勿在logback的配置中使用相对路径-->
    <property name="LOG_HOME" value="/home"/>

    <!-- 控制台输出 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 按照每天生成日志文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!--日志文件输出的文件名-->
            <FileNamePattern>${LOG_HOME}/xxx.log.%d{yyyy-MM-dd}.log</FileNamePattern>
            <!--日志文件保留天数-->
            <MaxHistory>15</MaxHistory>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
        </encoder>
        <!--日志文件最大的大小-->
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
            <MaxFileSize>50MB</MaxFileSize>
        </triggeringPolicy>
    </appender>

    <!--日志异步到数据库 -->
    <appender name="DB" class="ch.qos.logback.classic.db.DBAppender">
        <!--日志异步到数据库 -->
        <connectionSource class="ch.qos.logback.core.db.DriverManagerConnectionSource">
            <!--连接池 -->
            <dataSource class="com.mchange.v2.c3p0.ComboPooledDataSource">
                <driverClass>com.mysql.jdbc.Driver</driverClass>
                <url>jdbc:mysql://127.0.0.1:3306/databaseName</url>
                <user>root</user>
                <password>root</password>
            </dataSource>
        </connectionSource>
    </appender>

    <!-- show parameters for hibernate sql 专为Hibernate定制 -->
    <logger name="org.hibernate.type.descriptor.sql.BasicBinder" level="TRACE"/>
    <logger name="org.hibernate.type.descriptor.sql.BasicExtractor" level="DEBUG"/>
    <logger name="org.hibernate.SQL" level="DEBUG"/>
    <logger name="org.hibernate.engine.QueryParameters" level="DEBUG"/>
    <logger name="org.hibernate.engine.query.HQLQueryPlan" level="DEBUG"/>

    <!-- myibatis configure -->
    <logger name="com.apache.ibatis" level="TRACE"/>
    <logger name="java.sql.Connection" level="DEBUG"/>
    <logger name="java.sql.Statement" level="DEBUG"/>
    <logger name="java.sql.PreparedStatement" level="DEBUG"/>

    <!-- 日志输出级别 -->
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

### 打日志

我们采用slf4j接口，**不要用具体的实现**，面向接口
```java
package com.example.springboot.web.controller;

import com.example.springboot.web.model.Demo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/{id}")
    public Demo show(@PathVariable String id) {
        LOG.debug("show: {}", id);
        return new Demo(id, id);
    }

}
```

常用日志级别`debug`、`info`、`warn`、`error`

提示，一定要用占位符`{}`输入内容。

```java
public class LogController {

    private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);

    public void doLog() {
        // 正确姿势
        LOG.debug("show: {}", id);
        
        // 枪毙
        LOG.debug("show" + id);
    }

}
```

### Debug模式

spring-boot由于自动配置，很多时候我们都不知道自动配置了哪些bean，这时可以打开debug模式
```yaml
debug: true
```

## 测试

### 依赖

pom添加依赖`spring-boot-starter-test`
```xml
<!-- 测试 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>${spring.boot.version}</version>
    <scope>test</scope>
</dependency>
```

### 测试类

测试类均放在`test/java`目录下，加上测试相关的注解
```java
package com.example.springboot.web;

import com.example.springboot.web.model.Demo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    private static final String SERVICE_SHOW = "http://127.0.0.1:8080/demo/1";

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void show() {
        ResponseEntity<Demo> entity = restTemplate.getForEntity(SERVICE_SHOW, Demo.class);
        if (entity.getStatusCode() == HttpStatus.OK) {
            LOG.info("结果: {}", entity.getBody());
        }
    }

}
```

## 补充

### 启动运行代码

很多时候我们希望在启动时运行一些代码，比如加载一些东东，那么你需要使用`CommandLineRunner`
```java
import org.springframework.boot.*;
import org.springframework.stereotype.*;

@Component
public class MyBean implements CommandLineRunner {

    public void run(String... args) {
        // Do something...
    }

}
```

你可以实现`org.springframework.core.Ordered`接口或添加`org.springframework.core.annotation.Order`注解按顺序执行

### yaml使用

你也许想把一些配置写在配置文件中，代码中使用这些配置，例如
```yaml
foo:
  list:
    - name: my name
      description: my description
    - name: my name2
      description: my description2
```

将其映射为Java对象，`MyPojo`需要拥有`name`、`description`属性
```java
@Component
@ConfigurationProperties("foo")
public class FooProperties {

    private final List<MyPojo> list = new ArrayList<>();

    public List<MyPojo> getList() {
        return this.list;
    }

}
```

加上`@Component`后，`FooProperties`就可以直接在代码中注入了

### Profile

在生产中，我们肯定有很多环境，`dev`、`test`、`prod`等

我们的配置文件可以以application-profile.yml的形式命名，然后指定使用的profile
* 运行时传入`spring.profiles.active=prod`参数
    ```shell
    # maven运行
    mvn clean spring-boot:run -Dspring.profiles.active=test
    
    # jar运行
    mvn clean pakcage -DskipTests
    cd target
    java -Dspring.profiles.active=test -jar xxx.jar
    ```
* 在application.yml中通过`spring.profiles.active`指定（**上面的优先级更高哦**）
    ```yaml
    spring:
      profiles:
        active: dev
    ```