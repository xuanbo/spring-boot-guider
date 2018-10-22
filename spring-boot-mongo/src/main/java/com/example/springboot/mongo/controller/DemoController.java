package com.example.springboot.mongo.controller;

import com.example.springboot.mongo.entity.Demo;
import com.example.springboot.mongo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping
    public Page<Demo> page(@PageableDefault(page = 0, size = 20) Pageable pageable) {
        return demoService.page(pageable);
    }

}
