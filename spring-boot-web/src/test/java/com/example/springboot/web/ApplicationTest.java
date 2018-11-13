package com.example.springboot.web;

import com.example.springboot.web.model.Demo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationTest.class);

    private static final String SERVICE_SHOW = "http://127.0.0.1:8080/demo/1";

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void show() {
        ResponseEntity<Demo> entity = restTemplate.getForEntity(SERVICE_SHOW, Demo.class);
        if (entity.getStatusCode() == HttpStatus.OK) {
            LOG.info("结果: {}", entity.getBody());
        }
    }

}
