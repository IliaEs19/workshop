package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SaveData {
    private static final String SAVE_FILE = "data/users.json";

    private static SaveData instance;
    private Map<String, User> users;
    private final Json json;

    private SaveData() {
        users = new HashMap<>();
        json = new Json();
        json.setOutputType(OutputType.json); // خروجی خوانا و زیبا
        loadUsers();
    }

    public static SaveData getInstance() {
        if (instance == null) {
            instance = new SaveData();
        }
        return instance;
    }

    /**
     * کلاس کمکی برای ذخیره‌سازی لیست کاربران در JSON
     */
    private static class UserList {
        public ArrayList<User> users = new ArrayList<>();
    }

    /**
     * لود کردن تمام کاربران از فایل JSON
     */
    private void loadUsers() {
        FileHandle file = Gdx.files.local(SAVE_FILE);

        if (file.exists()) {
            try {
                // خواندن آرایه کاربران از فایل JSON
                JsonValue root = new JsonReader().parse(file);
                JsonValue userArray = root.get("users");

                for (JsonValue userValue = userArray.child; userValue != null; userValue = userValue.next) {
                    String username = userValue.getString("userName");
                    String password = userValue.getString("password");

                    User user = new User(username, password);
                    users.put(username, user);
                }

                Gdx.app.log("SaveData", "Loaded " + users.size() + " users from JSON");
            } catch (Exception e) {
                Gdx.app.error("SaveData", "Error loading users from JSON", e);
            }
        } else {
            Gdx.app.log("SaveData", "No saved users found");
        }
    }

    /**
     * ذخیره تمام کاربران در فایل JSON
     */
    private void saveUsers() {
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

            Gdx.app.log("SaveData", "Saved " + users.size() + " users to JSON");
        } catch (Exception e) {
            Gdx.app.error("SaveData", "Error saving users to JSON", e);
        }
    }

    /**
     * افزودن یک کاربر جدید
     * @param username نام کاربری
     * @param password رمز عبور
     * @return true اگر با موفقیت اضافه شد، false اگر نام کاربری قبلاً وجود داشت
     */
    public boolean addUser(String username, String password) {
        if (users.containsKey(username)) {
            return false; // کاربر قبلاً وجود دارد
        }

        users.put(username, new User(username, password));
        saveUsers();
        return true;
    }

    /**
     * بررسی اعتبار کاربر
     * @param username نام کاربری
     * @param password رمز عبور
     * @return true اگر نام کاربری و رمز عبور صحیح باشند
     */
    public boolean validateUser(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }

        return user.getPassword().equals(password);
    }

    /**
     * دریافت کاربر با نام کاربری مشخص
     * @param username نام کاربری
     * @return شیء User یا null اگر کاربر یافت نشد
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * دریافت لیست تمام کاربران
     * @return لیستی از تمام کاربران
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * حذف یک کاربر
     * @param username نام کاربری
     * @return true اگر کاربر با موفقیت حذف شد
     */
    public boolean removeUser(String username) {
        if (users.remove(username) != null) {
            saveUsers();
            return true;
        }
        return false;
    }

    /**
     * به‌روزرسانی اطلاعات کاربر
     * @param user شیء User با اطلاعات به‌روز
     * @return true اگر کاربر با موفقیت به‌روز شد
     */
    public boolean updateUser(User user) {
        if (users.containsKey(user.getUserName())) {
            users.put(user.getUserName(), user);
            saveUsers();
            return true;
        }
        return false;
    }
}
