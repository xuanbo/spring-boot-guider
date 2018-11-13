package com.example.springboot.mybatis.dao;

import com.example.springboot.mybatis.Application;
import com.example.springboot.mybatis.ApplicationTest;
import com.example.springboot.mybatis.entity.Demo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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
