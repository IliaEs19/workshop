package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.tilldawn.Models.SaveDatas.JsonUserStorage;
import com.tilldawn.Models.SaveDatas.SqliteUserStorage;
import com.tilldawn.Models.SaveDatas.UserDataStorage;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
    private static SaveData instance;
    private static User currentUser;

    private UserDataStorage jsonStorage;
    private UserDataStorage sqliteStorage;
    private boolean useSqlite;

    private SaveData() {

        jsonStorage = new JsonUserStorage();


        try {
            sqliteStorage = new SqliteUserStorage();
            useSqlite = true;


            if (sqliteStorage.loadAllUsers().isEmpty() && !jsonStorage.loadAllUsers().isEmpty()) {
                migrateFromJsonToSqlite();
            }

            Gdx.app.log("SaveData", "SQLite storage initialized successfully");
        } catch (Exception e) {
            useSqlite = false;
            Gdx.app.error("SaveData", "Failed to initialize SQLite, falling back to JSON storage", e);
        }
    }

    private void migrateFromJsonToSqlite() {
        List<User> jsonUsers = jsonStorage.loadAllUsers();

        for (User user : jsonUsers) {
            sqliteStorage.saveUser(user);
        }

        Gdx.app.log("SaveData", "Migrated " + jsonUsers.size() + " users from JSON to SQLite");
    }

    public static SaveData getInstance() {
        if (instance == null) {
            instance = new SaveData();
        }
        return instance;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        SaveData.currentUser = currentUser;
    }


    public void setStorageMethod(boolean useSqlite) {
        if (this.useSqlite != useSqlite) {
            this.useSqlite = useSqlite;
            Gdx.app.log("SaveData", "Storage method changed to " + (useSqlite ? "SQLite" : "JSON"));
        }
    }


    public boolean isUsingSqlite() {
        return useSqlite;
    }

    private UserDataStorage getActiveStorage() {
        return useSqlite ? sqliteStorage : jsonStorage;
    }

    public boolean addUser(String username, String password, String securityQuestion, String securityAnswer) {
        if (getActiveStorage().userExists(username)) {
            return false;
        }

        User user = new User(username, password, securityQuestion, securityAnswer);
        getActiveStorage().saveUser(user);


        if (useSqlite) {
            jsonStorage.saveUser(user);
        }

        return true;
    }

    public boolean validateUser(String username, String password) {
        User user = getActiveStorage().loadUser(username);
        if (user == null) {
            return false;
        }

        return user.getPassword().equals(password);
    }

    public boolean validateSecurityAnswer(String username, String securityAnswer) {
        User user = getActiveStorage().loadUser(username);
        if (user == null || user.getSecurityAnswer() == null || user.getSecurityAnswer().isEmpty()) {
            return false;
        }

        return user.getSecurityAnswer().equalsIgnoreCase(securityAnswer.trim());
    }

    public String getSecurityQuestion(String username) {
        User user = getActiveStorage().loadUser(username);
        if (user == null) {
            return null;
        }
        return user.getSecurityQuestion();
    }

    public String recoverPassword(String username, String securityAnswer) {
        if (validateSecurityAnswer(username, securityAnswer)) {
            User user = getActiveStorage().loadUser(username);
            if (user != null) {
                return user.getPassword();
            }
        }
        return null;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (validateUser(username, oldPassword)) {
            User user = getActiveStorage().loadUser(username);
            user.setPassword(newPassword);
            getActiveStorage().saveUser(user);


            if (useSqlite) {
                jsonStorage.saveUser(user);
            }

            return true;
        }
        return false;
    }

    public boolean resetPasswordWithSecurityQuestion(String username, String securityAnswer, String newPassword) {
        if (validateSecurityAnswer(username, securityAnswer)) {
            User user = getActiveStorage().loadUser(username);
            user.setPassword(newPassword);
            getActiveStorage().saveUser(user);


            if (useSqlite) {
                jsonStorage.saveUser(user);
            }

            return true;
        }
        return false;
    }

    public boolean changeSecurityQuestion(String username, String password,
                                          String newSecurityQuestion, String newSecurityAnswer) {
        if (validateUser(username, password)) {
            User user = getActiveStorage().loadUser(username);
            user.setSecurityQuestion(newSecurityQuestion);
            user.setSecurityAnswer(newSecurityAnswer);
            getActiveStorage().saveUser(user);


            if (useSqlite) {
                jsonStorage.saveUser(user);
            }

            return true;
        }
        return false;
    }

    public static User getUser(String username) {
        return getInstance().getActiveStorage().loadUser(username);
    }

    public List<User> getAllUsers() {
        return getActiveStorage().loadAllUsers();
    }

    public boolean removeUser(String username) {
        boolean result = getActiveStorage().deleteUser(username);


        if (result && useSqlite) {
            jsonStorage.deleteUser(username);
        }

        return result;
    }

    public boolean updateUser(User user) {
        if (getActiveStorage().userExists(user.getUserName())) {
            getActiveStorage().saveUser(user);


            if (useSqlite) {
                jsonStorage.saveUser(user);
            }

            return true;
        }
        return false;
    }

    public boolean userExists(String username) {
        return getActiveStorage().userExists(username);
    }

    public int getUserCount() {
        return getAllUsers().size();
    }

    public boolean saveUserAvatar(String username, String avatarPath) {
        User user = getActiveStorage().loadUser(username);
        if (user == null) {
            return false;
        }

        user.setAvatarPath(avatarPath);
        getActiveStorage().saveUser(user);


        if (useSqlite) {
            jsonStorage.saveUser(user);
        }

        return true;
    }

    public boolean saveUserAvatarByIndex(String username, int avatarIndex) {

        String[] AVATAR_PATHS = {
            "avatars/character1.jpg",
            "avatars/character2.jpg",
            "avatars/character3.jpg",
            "avatars/character4.jpg"
        };


        if (avatarIndex < 0 || avatarIndex >= AVATAR_PATHS.length) {
            return false;
        }

        return saveUserAvatar(username, AVATAR_PATHS[avatarIndex]);
    }

    public boolean changeUsername(String oldUsername, String newUsername, String password) {
        if (getActiveStorage().userExists(newUsername)) {
            return false;
        }

        User user = getActiveStorage().loadUser(oldUsername);
        if (user == null || !user.getPassword().equals(password)) {
            return false;
        }

        User newUser = new User(
            newUsername,
            user.getPassword(),
            user.getSecurityQuestion(),
            user.getSecurityAnswer(),
            user.getAvatarPath()
        );


        newUser.setLastWeaponUsed(user.getLastWeaponUsed());
        newUser.setLastGameTime(user.getLastGameTime());
        newUser.setLastHeroUsed(user.getLastHeroUsed());
        newUser.setHighScore(user.getHighScore());
        newUser.setTotalGamesPlayed(user.getTotalGamesPlayed());
        newUser.setTotalKills(user.getTotalKills());
        newUser.updateLongestSurvivalTime(user.getLongestSurvivalTime());
        newUser.addSurvivalTime(user.getTotalSurvivalTime());


        getActiveStorage().deleteUser(oldUsername);
        getActiveStorage().saveUser(newUser);


        if (useSqlite) {
            jsonStorage.deleteUser(oldUsername);
            jsonStorage.saveUser(newUser);
        }

        return true;
    }


    static class UserList {
        public ArrayList<User> users = new ArrayList<>();
    }

    public void dispose() {
        if (sqliteStorage instanceof SqliteUserStorage) {
            ((SqliteUserStorage) sqliteStorage).close();
        }
    }
}
