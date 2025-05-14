package com.tilldawn.Models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userName ;
    private String password ;
    private String securityQuestion;
    private String securityAnswer;
    private int avatarIndex = -1;
    private String avatarPath; // فیلد جدید برای ذخیره مسیر آواتار

    public User(){}

//    public User(String userName, String password, String securityQuestion, String securityAnswer) {
//        this.userName = userName;
//        this.password = password;
//        this.securityQuestion = securityQuestion;
//        this.securityAnswer = securityAnswer;
//        this.avatarPath = ""; // مقدار پیش‌فرض خالی
//    }

    // سازنده جدید با آواتار
    public User(String userName, String password, String securityQuestion, String securityAnswer, String avatarPath) {
        this.userName = userName;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.avatarPath = avatarPath;
    }

    public User(String userName, String password, String securityQuestion, String securityAnswer) {
        this(userName, password, securityQuestion, securityAnswer, "");
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
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

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public void setAvatarIndex(int avatarIndex) {
        this.avatarIndex = avatarIndex;
    }
}
