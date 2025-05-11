package com.tilldawn.Models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userName ;
    private String password ;
    private final Map<String, String> securityQuestion;


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.securityQuestion = new HashMap<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
