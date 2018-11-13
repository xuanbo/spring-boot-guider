package com.example.springboot.mongo.service.impl;

import com.example.springboot.mongo.dao.DemoDao;
import com.example.springboot.mongo.entity.Demo;
import com.example.springboot.mongo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoDao demoDao;

    @Override
    public Page<Demo> page(Pageable pageable) {
        return demoDao.page(new Criteria(), pageable);
    }
}
