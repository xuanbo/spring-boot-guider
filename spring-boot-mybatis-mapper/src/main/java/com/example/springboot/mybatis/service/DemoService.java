package com.example.springboot.mybatis.service;

import com.example.springboot.mybatis.entity.Demo;

import java.util.List;

public interface DemoService {

    int insert(Demo demo);

    int updateById(Demo demo);

    int deleteById(Long id);

    Demo findById(Long id);

    List<Demo> findAll();

}
