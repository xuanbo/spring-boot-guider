package com.example.springboot.mybatis.entity;

import org.hibernate.validator.constraints.NotBlank;

public class Demo extends Entity {

    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "name='" + name + '\'' +
                "} " + super.toString();
    }
}
