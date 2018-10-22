package com.example.springboot.mybatis.service.impl;

import com.example.springboot.mybatis.dao.DemoDao;
import com.example.springboot.mybatis.entity.Demo;
import com.example.springboot.mybatis.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return demoDao.updateByPrimaryKey(demo);
    }

    @Override
    public int deleteById(Long id) {
        return demoDao.deleteByPrimaryKey(id);
    }

    @Override
    public Demo findById(Long id) {
        return demoDao.selectByPrimaryKey(id);
    }

    @Override
    public List<Demo> findAll() {
        return demoDao.selectAll();
    }

}
