package com.example.springboot.mybatis.util;

import tk.mybatis.mapper.common.Mapper;

/**
 * 自定义Mapper接口
 *
 * @param <T> 实体
 */
public interface MyMapper<T> extends Mapper<T> {
}
