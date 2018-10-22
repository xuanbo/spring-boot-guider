package com.example.springboot.mybatis.dao;

import com.example.springboot.mybatis.entity.Demo;
import com.example.springboot.mybatis.util.MyMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoDao extends MyMapper<Demo> {
}
