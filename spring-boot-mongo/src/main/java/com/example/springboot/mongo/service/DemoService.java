package com.example.springboot.mongo.service;

import com.example.springboot.mongo.entity.Demo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DemoService {

    /**
     * 分页查询
     *
     * @param pageable 分页参数信息
     * @return Page<Demo>
     */
    Page<Demo> page(Pageable pageable);

}
