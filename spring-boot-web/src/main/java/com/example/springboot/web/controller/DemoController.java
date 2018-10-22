package com.example.springboot.web.controller;

import com.example.springboot.web.model.Demo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private static final Logger LOG = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/{id}")
    public Demo show(@PathVariable String id) {
        LOG.debug("show: {}", id);
        return new Demo(id, id);
    }

}
