package com.example.springboot.mongo.dao.impl;

import com.example.springboot.mongo.dao.DemoDao;
import com.example.springboot.mongo.entity.Demo;
import org.springframework.stereotype.Repository;

@Repository
public class DemoDaoImpl extends BaseDaoImpl<Demo> implements DemoDao {
}
