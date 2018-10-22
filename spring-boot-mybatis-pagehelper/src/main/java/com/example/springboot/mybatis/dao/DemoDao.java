package com.example.springboot.mybatis.dao;

import com.example.springboot.mybatis.entity.Demo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DemoDao {

    int insert(Demo demo);

    int updateById(Demo demo);

    int deleteById(@Param("id") Long id);

    Demo findById(@Param("id") Long id);

    List<Demo> findAll();

}
