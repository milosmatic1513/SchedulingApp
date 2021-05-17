package com.example.scheduleme.DataClasses;

public class User {
    private String name;
    private boolean loginAuth;

    public User() {
        this("");
    }

    public User(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
