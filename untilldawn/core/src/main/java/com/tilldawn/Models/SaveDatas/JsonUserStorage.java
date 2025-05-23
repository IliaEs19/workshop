package com.tilldawn.Models.SaveDatas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.tilldawn.Models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUserStorage implements UserDataStorage {
    private static final String SAVE_FILE = "data/users.json";
    private Map<String, User> users;
    private final Json json;

    // کلاس کمکی برای ذخیره‌سازی لیست کاربران در JSON
    private static class UserList {
        public ArrayList<User> users = new ArrayList<>();
    }

    public JsonUserStorage() {
        users = new HashMap<>();
        json = new Json();
        json.setOutputType(OutputType.json);
        loadUsersFromFile();
    }

    private void loadUsersFromFile() {
        FileHandle file = Gdx.files.local(SAVE_FILE);

        if (file.exists()) {
            try {
                JsonValue root = new JsonReader().parse(file);
                JsonValue userArray = root.get("users");

                for (JsonValue userValue = userArray.child; userValue != null; userValue = userValue.next) {
                    String username = userValue.getString("userName");
                    String password = userValue.getString("password");

                    String securityQuestion = "";
                    String securityAnswer = "";
                    if (userValue.has("securityQuestion") && userValue.has("securityAnswer")) {
                        securityQuestion = userValue.getString("securityQuestion");
                        securityAnswer = userValue.getString("securityAnswer");
                    }

                    String avatarPath = "";
                    if (userValue.has("avatarPath")) {
                        avatarPath = userValue.getString("avatarPath");
                    }

                    String lastWeaponUsed = "";
                    int lastGameTime = 0;
                    String lastHeroUsed = "";
                    int highScore = 0;
                    int totalGamesPlayed = 0;
                    int totalKills = 0;
                    float longestSurvivalTime = 0;
                    float totalSurvivalTime = 0;

                    if (userValue.has("lastWeaponUsed")) {
                        lastWeaponUsed = userValue.getString("lastWeaponUsed");
                    }
                    if (userValue.has("lastGameTime")) {
                        lastGameTime = userValue.getInt("lastGameTime");
                    }
                    if (userValue.has("lastHeroUsed")) {
                        lastHeroUsed = userValue.getString("lastHeroUsed");
                    }
                    if (userValue.has("highScore")) {
                        highScore = userValue.getInt("highScore");
                    }
                    if (userValue.has("totalGamesPlayed")) {
                        totalGamesPlayed = userValue.getInt("totalGamesPlayed");
                    }
                    if (userValue.has("totalKills")) {
                        totalKills = userValue.getInt("totalKills");
                    }
                    if (userValue.has("longestSurvivalTime")) {
                        longestSurvivalTime = userValue.getFloat("longestSurvivalTime");
                    }
                    if (userValue.has("totalSurvivalTime")) {
                        totalSurvivalTime = userValue.getFloat("totalSurvivalTime");
                    }

                    User user = new User(username, password, securityQuestion, securityAnswer, avatarPath);
                    user.setLastWeaponUsed(lastWeaponUsed);
                    user.setLastGameTime(lastGameTime);
                    user.setLastHeroUsed(lastHeroUsed);
                    user.setHighScore(highScore);
                    user.setTotalGamesPlayed(totalGamesPlayed);
                    user.setTotalKills(totalKills);
                    user.updateLongestSurvivalTime(longestSurvivalTime);
                    user.addSurvivalTime(totalSurvivalTime);

                    users.put(username, user);
                }

                Gdx.app.log("JsonUserStorage", "Loaded " + users.size() + " users from JSON");
            } catch (Exception e) {
                Gdx.app.error("JsonUserStorage", "Error loading users from JSON", e);
            }
        }
    }

    private void saveUsersToFile() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);

            // ایجاد دایرکتوری اگر وجود نداشته باشد
            if (!file.parent().exists()) {
                file.parent().mkdirs();
            }

            // تبدیل Map به لیست برای ذخیره‌سازی
            UserList userList = new UserList();
            userList.users.addAll(users.values());

            // ذخیره‌سازی به فرمت JSON
            String jsonString = json.prettyPrint(userList);
            file.writeString(jsonString, false);

            Gdx.app.log("JsonUserStorage", "Saved " + users.size() + " users to JSON");
        } catch (Exception e) {
            Gdx.app.error("JsonUserStorage", "Error saving users to JSON", e);
        }
    }

    @Override
    public void saveUser(User user) {
        users.put(user.getUserName(), user);
        saveUsersToFile();
    }

    @Override
    public User loadUser(String username) {
        return users.get(username);
    }

    @Override
    public List<User> loadAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean deleteUser(String username) {
        if (users.remove(username) != null) {
            saveUsersToFile();
            return true;
        }
        return false;
    }

    @Override
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
