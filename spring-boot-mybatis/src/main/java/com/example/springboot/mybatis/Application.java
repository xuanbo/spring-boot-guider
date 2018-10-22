package com.example.springboot.mybatis;

import com.example.springboot.mybatis.constants.AppConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 也可以不用加扫描mapper接口
@MapperScan(AppConstant.MAPPER_SCAN_PACKAGE)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
