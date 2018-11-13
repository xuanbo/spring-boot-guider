package com.example.springboot.web.model;

import java.io.Serializable;

public class Demo implements Serializable {

    private String id;

    private String name;

    public Demo() {
    }

    public Demo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Demo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
