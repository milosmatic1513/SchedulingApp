package com.example.scheduleme.DataClasses;

public class User {
    private String name;
    private boolean loginAuth;

    public User() {
        this("");
    }

    public User(String name) {
        this(name,false);

    }
    public User(String name,boolean loginAuth) {
        this.name = name;
        this.loginAuth = loginAuth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLoginAuth(boolean loginAuth)
    {
        this.loginAuth = loginAuth;
    }

    public boolean isLoginAuth()
    {
        return this.loginAuth;
    }
    public boolean equals(User user)
    {
        if(user.getName().equalsIgnoreCase(this.getName()) && user.isLoginAuth()==this.isLoginAuth())
        {
            return true;
        }
        return false;
    }
}
