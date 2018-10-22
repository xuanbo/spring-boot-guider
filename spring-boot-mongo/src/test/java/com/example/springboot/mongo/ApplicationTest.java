package com.example.springboot.mongo;

import com.example.springboot.mongo.entity.Demo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void save() {
        Demo demo = new Demo();
        demo.setName("奔波儿灞");
        mongoTemplate.save(demo);
    }

    @Test
    public void find() {
        List<Demo> demos = mongoTemplate.findAll(Demo.class);
        LOG.info("demos: {}", demos);
    }

}
