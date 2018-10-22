package com.example.springboot.mybatis.controller;

import com.example.springboot.mybatis.constants.MessageConstant;
import com.example.springboot.mybatis.entity.Demo;
import com.example.springboot.mybatis.service.DemoService;
import com.example.springboot.mybatis.util.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(DemoController.PATH)
@Validated
public class DemoController {

    static final String PATH = "/demo";

    @Autowired
    private DemoService demoService;

    @PostMapping
    public Rest<Demo> add(@Validated @RequestBody Demo demo) {
        demoService.insert(demo);
        return Rest.ok(MessageConstant.OK, demo);
    }

    @PutMapping
    public Rest<Demo> modify(@Validated @RequestBody Demo demo) {
        demoService.updateById(demo);
        return Rest.ok(MessageConstant.OK, demo);
    }

    @DeleteMapping("/{id}")
    public Rest<Integer> remove(@PathVariable Long id) {
        return Rest.ok(MessageConstant.OK, demoService.deleteById(id));
    }

    @GetMapping("/{id}")
    public Rest<Demo> find(@PathVariable Long id) {
        return Rest.ok(MessageConstant.OK, demoService.findById(id));
    }

    @GetMapping
    public Rest<List<Demo>> findAll() {
        return Rest.ok(MessageConstant.OK, demoService.findAll());
    }

}
