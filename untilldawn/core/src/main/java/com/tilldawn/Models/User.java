package com.tilldawn.Models;

import com.tilldawn.Models.Hero.WeaponType;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String userName ;
    private String password ;
    private String securityQuestion;
    private String securityAnswer;
    private int avatarIndex = -1;
    private String avatarPath;

    private String lastWeaponUsed;
    private int lastGameTime;
    private String lastHeroUsed;
    private int highScore;
    private int totalGamesPlayed;

    public User(){}

    private int totalKills;
    private float longestSurvivalTime;
    private float totalSurvivalTime;


    public User(String userName, String password, String securityQuestion, String securityAnswer, String avatarPath) {
        this.userName = userName;
        this.password = password;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.avatarPath = avatarPath;

        this.lastWeaponUsed = "";
        this.lastGameTime = 0;
        this.lastHeroUsed = "";
        this.highScore = 0;
        this.totalGamesPlayed = 0;
        this.totalKills = 0;
        this.longestSurvivalTime = 0;
        this.totalSurvivalTime = 0;
    }

    public User(String userName, String password, String securityQuestion, String securityAnswer) {
        this(userName, password, securityQuestion, securityAnswer, "");
    }

    public String getLastWeaponUsed() {
        return lastWeaponUsed;
    }

    public void setLastWeaponUsed(String lastWeaponUsed) {
        this.lastWeaponUsed = lastWeaponUsed;
    }

    public void setLastWeaponUsed(WeaponType weaponType) {
        if (weaponType != null) {
            this.lastWeaponUsed = weaponType.getName();
        }
    }

    public int getLastGameTime() {
        return lastGameTime;
    }

    public void setLastGameTime(int lastGameTime) {
        this.lastGameTime = lastGameTime;
    }

    public String getLastHeroUsed() {
        return lastHeroUsed;
    }

    public void setLastHeroUsed(String lastHeroUsed) {
        this.lastHeroUsed = lastHeroUsed;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        if (highScore > this.highScore) {
            this.highScore = highScore;
        }
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void incrementTotalGamesPlayed() {
        this.totalGamesPlayed++;
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

    public int getTotalKills() {
        return totalKills;
    }

        public void addKills(int kills) {
        this.totalKills += kills;
    }

        public void setTotalKills(int totalKills) {
        this.totalKills = totalKills;
    }

        public float getLongestSurvivalTime() {
        return longestSurvivalTime;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

        public void updateLongestSurvivalTime(float survivalTime) {
        if (survivalTime > this.longestSurvivalTime) {
            this.longestSurvivalTime = survivalTime;
        }
    }

        public float getTotalSurvivalTime() {
        return totalSurvivalTime;
    }

        public void addSurvivalTime(float survivalTime) {
        this.totalSurvivalTime += survivalTime;
    }
}
