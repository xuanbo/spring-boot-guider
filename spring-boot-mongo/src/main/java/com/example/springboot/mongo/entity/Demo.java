package com.example.springboot.mongo.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "T_Demo")
public class Demo extends Entity {

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
