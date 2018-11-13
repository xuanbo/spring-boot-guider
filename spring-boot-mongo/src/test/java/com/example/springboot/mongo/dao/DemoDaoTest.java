package com.example.springboot.mongo.dao;

import com.example.springboot.mongo.Application;
import com.example.springboot.mongo.entity.Demo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class DemoDaoTest {

    private static final Logger LOG = LoggerFactory.getLogger(DemoDaoTest.class);

    @Autowired
    private DemoDao demoDao;

    @Test
    public void page() {
        // 第几页从0开始。。
        Pageable page = new PageRequest(0, 10);
        Page<Demo> demoPage = demoDao.page(new Criteria(), page);
        LOG.info("totalPage: {}, totalElements: {}, data: {}", demoPage.getTotalPages(), demoPage.getTotalElements(), demoPage.getContent());
    }

}
