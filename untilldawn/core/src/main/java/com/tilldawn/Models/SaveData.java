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

    private static User currentUser;

    private static SaveData instance;
    private static Map<String, User> users;
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

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        SaveData.currentUser = currentUser;
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

                    String securityQuestion = "";
                    String securityAnswer = "";
                    if (userValue.has("securityQuestion") && userValue.has("securityAnswer")) {
                        securityQuestion = userValue.getString("securityQuestion");
                        securityAnswer = userValue.getString("securityAnswer");
                    }

                    // اضافه کردن خواندن مسیر آواتار
                    String avatarPath = "";
                    if (userValue.has("avatarPath")) {
                        avatarPath = userValue.getString("avatarPath");
                    }

                    String lastWeaponUsed = "";
                    int lastGameTime = 0;
                    String lastHeroUsed = "";
                    int highScore = 0;
                    int totalGamesPlayed = 0;

                    // متغیرهای جدید
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

                    // بارگذاری متغیرهای جدید
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

                    // اگر تعداد بازی‌های انجام شده بیشتر از صفر است، آن را تنظیم کنید
                    if (totalGamesPlayed > 0) {
                        for (int i = 0; i < totalGamesPlayed; i++) {
                            user.incrementTotalGamesPlayed();
                        }
                    }

                    // تنظیم متغیرهای جدید
                    user.setTotalKills(totalKills);
                    user.updateLongestSurvivalTime(longestSurvivalTime);
                    user.addSurvivalTime(totalSurvivalTime);

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

    public boolean saveUserAvatar(String username, String avatarPath) {
        User user = users.get(username);
        if (user == null) {
            return false;
        }

        user.setAvatarPath(avatarPath);
        saveUsers();
        return true;
    }

    /**
     * ذخیره شماره آواتار برای کاربر
     * @param username نام کاربری
     * @param avatarIndex شماره آواتار
     * @return true اگر با موفقیت ذخیره شد
     */
    public boolean saveUserAvatarByIndex(String username, int avatarIndex) {
        // مسیر آواتارها بر اساس شماره
        String[] AVATAR_PATHS = {
            "avatars/character1.jpg",
            "avatars/character2.jpg",
            "avatars/character3.jpg",
            "avatars/character4.jpg"
        };

        // بررسی معتبر بودن شماره آواتار
        if (avatarIndex < 0 || avatarIndex >= AVATAR_PATHS.length) {
            return false;
        }

        return saveUserAvatar(username, AVATAR_PATHS[avatarIndex]);
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

            // چاپ مسیر فایل برای دیباگ
            System.out.println("User data saved to: " + file.file().getAbsolutePath());

            Gdx.app.log("SaveData", "Saved " + users.size() + " users to JSON");
        } catch (Exception e) {
            Gdx.app.error("SaveData", "Error saving users to JSON", e);
        }
    }

    /**
     * افزودن یک کاربر جدید با سؤال و پاسخ امنیتی
     * @param username نام کاربری
     * @param password رمز عبور
     * @param securityQuestion سؤال امنیتی
     * @param securityAnswer پاسخ سؤال امنیتی
     * @return true اگر با موفقیت اضافه شد، false اگر نام کاربری قبلاً وجود داشت
     */
    public boolean addUser(String username, String password, String securityQuestion, String securityAnswer) {
        if (users.containsKey(username)) {
            return false; // کاربر قبلاً وجود دارد
        }

        User user = new User(username, password, securityQuestion, securityAnswer);
        users.put(username, user);
        saveUsers();
        return true;
    }

    public boolean changeUsername(String oldUsername, String newUsername, String password) {
        // بررسی وجود کاربر قدیمی
        User user = users.get(oldUsername);
        if (user == null) {
            return false; // کاربر وجود ندارد
        }

        // بررسی صحت رمز عبور
        if (!user.getPassword().equals(password)) {
            return false; // رمز عبور نادرست است
        }

        // بررسی تکراری نبودن نام کاربری جدید
        if (users.containsKey(newUsername)) {
            return false; // نام کاربری جدید قبلاً وجود دارد
        }

        // ذخیره مسیر آواتار کاربر قدیمی
        String avatarPath = user.getAvatarPath();
        Gdx.app.log("SaveData", "Avatar path for old user: " + avatarPath);

        // ایجاد کاربر جدید با نام کاربری جدید و همان اطلاعات قبلی
        User newUser = new User(
            newUsername,
            user.getPassword(),
            user.getSecurityQuestion(),
            user.getSecurityAnswer()
        );

        // به‌صورت مستقیم آدرس آواتار را به کاربر جدید منتقل می‌کنیم
        newUser.setAvatarPath(avatarPath);
        Gdx.app.log("SaveData", "Set avatar path for new user: " + newUser.getAvatarPath());

        // حذف کاربر قدیمی و اضافه کردن کاربر جدید
        users.remove(oldUsername);
        users.put(newUsername, newUser);

        // ذخیره تغییرات
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
     * بررسی پاسخ سؤال امنیتی کاربر
     * @param username نام کاربری
     * @param securityAnswer پاسخ سؤال امنیتی
     * @return true اگر پاسخ سؤال امنیتی صحیح باشد
     */
    public boolean validateSecurityAnswer(String username, String securityAnswer) {
        User user = users.get(username);
        if (user == null || user.getSecurityAnswer() == null || user.getSecurityAnswer().isEmpty()) {
            return false;
        }

        return user.getSecurityAnswer().equalsIgnoreCase(securityAnswer.trim());
    }

    /**
     * دریافت سؤال امنیتی کاربر
     * @param username نام کاربری
     * @return سؤال امنیتی یا null اگر کاربر یافت نشد یا سؤال امنیتی ندارد
     */
    public String getSecurityQuestion(String username) {
        User user = users.get(username);
        if (user == null) {
            return null;
        }
        return user.getSecurityQuestion();
    }

    /**
     * بازیابی رمز عبور با استفاده از سؤال امنیتی
     * @param username نام کاربری
     * @param securityAnswer پاسخ سؤال امنیتی
     * @return رمز عبور اگر پاسخ صحیح باشد، در غیر این صورت null
     */
    public String recoverPassword(String username, String securityAnswer) {
        if (validateSecurityAnswer(username, securityAnswer)) {
            User user = users.get(username);
            if (user != null) {
                return user.getPassword();
            }
        }
        return null;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (validateUser(username, oldPassword)) {
            User user = users.get(username);

            // ذخیره آدرس آواتار قبل از تغییر رمز عبور
            String avatarPath = user.getAvatarPath();
            Gdx.app.log("SaveData", "Saving avatar path before password change: " + avatarPath);

            // تغییر رمز عبور
            user.setPassword(newPassword);

            // اطمینان از حفظ آدرس آواتار
            if (avatarPath != null && !avatarPath.isEmpty()) {
                user.setAvatarPath(avatarPath);
                Gdx.app.log("SaveData", "Restored avatar path after password change: " + user.getAvatarPath());
            }

            saveUsers();
            return true;
        }
        return false;
    }

    /**
     * تغییر رمز عبور با استفاده از سؤال امنیتی (برای بازیابی رمز عبور)
     * @param username نام کاربری
     * @param securityAnswer پاسخ سؤال امنیتی
     * @param newPassword رمز عبور جدید
     * @return true اگر رمز عبور با موفقیت تغییر کرد
     */
    public boolean resetPasswordWithSecurityQuestion(String username, String securityAnswer, String newPassword) {
        if (validateSecurityAnswer(username, securityAnswer)) {
            User user = users.get(username);
            user.setPassword(newPassword);
            saveUsers();
            return true;
        }
        return false;
    }

    /**
     * تغییر سؤال و پاسخ امنیتی
     * @param username نام کاربری
     * @param password رمز عبور (برای تأیید هویت)
     * @param newSecurityQuestion سؤال امنیتی جدید
     * @param newSecurityAnswer پاسخ امنیتی جدید
     * @return true اگر سؤال و پاسخ امنیتی با موفقیت تغییر کردند
     */
    public boolean changeSecurityQuestion(String username, String password,
                                          String newSecurityQuestion, String newSecurityAnswer) {
        if (validateUser(username, password)) {
            User user = users.get(username);
            user.setSecurityQuestion(newSecurityQuestion);
            user.setSecurityAnswer(newSecurityAnswer);
            saveUsers();
            return true;
        }
        return false;
    }

    /**
     * دریافت کاربر با نام کاربری مشخص
     * @param username نام کاربری
     * @return شیء User یا null اگر کاربر یافت نشد
     */
    public static User getUser(String username) {
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

    /**
     * بررسی وجود نام کاربری
     * @param username نام کاربری
     * @return true اگر نام کاربری قبلاً ثبت شده باشد
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    /**
     * دریافت تعداد کاربران ثبت شده
     * @return تعداد کاربران
     */
    public int getUserCount() {
        return users.size();
    }

    /**
     * دریافت مسیر فایل ذخیره‌سازی کاربران
     * @return مسیر کامل فایل
     */
    public String getUserDataFilePath() {
        FileHandle file = Gdx.files.local(SAVE_FILE);
        return file.file().getAbsolutePath();
    }

    /**
     * خواندن محتوای فایل ذخیره‌سازی کاربران
     * @return محتوای فایل به صورت رشته
     */
    public String getUserDataFileContent() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (file.exists()) {
                return file.readString();
            } else {
                return "File does not exist";
            }
        } catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }
}
