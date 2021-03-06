package com.example.springboot.mybatis.entity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public abstract class Entity implements Serializable {

    @NotNull
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}
