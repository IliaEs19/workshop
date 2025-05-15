package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.tilldawn.Main;
import com.tilldawn.Models.DialogManager;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Views.LoginMenu;
import com.tilldawn.Views.PreGameMenu;

public class PreGameMenuController {
    private PreGameMenu view;
    private SaveData saveData;
    private User currentUser;

    // سلاح و قهرمان انتخاب شده در منو
    private WeaponType selectedWeapon;
    private HeroType selectedHero;
    private int selectedTime;

    public PreGameMenuController() {
        saveData = SaveData.getInstance();
        currentUser = SaveData.getCurrentUser();

        // بررسی اینکه آیا کاربر لاگین کرده است
        if (currentUser == null) {
            showLoginRequiredError();
            return;
        }

        // ایجاد نمای منو
        view = new PreGameMenu(this);

        // بارگذاری اطلاعات بازی قبلی کاربر اگر وجود داشته باشد
        loadUserGameData();
    }

    /**
     * بررسی می‌کند که آیا کاربر لاگین کرده است و در صورت نیاز خطای مناسب نمایش داده می‌شود
     * @return true اگر کاربر لاگین کرده باشد
     */
    public boolean checkUserLoggedIn() {
        if (currentUser == null) {
            showLoginRequiredError();
            return false;
        }
        return true;
    }

    /**
     * نمایش خطای لاگین نکردن و هدایت به صفحه لاگین
     */
    private void showLoginRequiredError() {
        Gdx.app.log("PreGameController", "Please, Login first.");

        // ایجاد یک LoginController و نمایش صفحه لاگین
        LoginMenuController loginController = new LoginMenuController();
        LoginMenu loginMenu = new LoginMenu(loginController, GameAssetManager.getGameAssetManager().getSkin());
        Main.getMain().setScreen(loginMenu);

        // اضافه کردن یک رویداد برای نمایش خطا بعد از اینکه صفحه لاگین کاملاً بارگذاری شد
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                DialogManager.showErrorDialog(loginMenu.getStage(), "Fail", "Please, Login first.", null);
            }
        });
    }

    /**
     * بارگذاری اطلاعات بازی قبلی کاربر
     */
    private void loadUserGameData() {
        if (currentUser != null) {
            // بررسی اینکه آیا کاربر قبلاً بازی کرده است
            if (currentUser.getTotalGamesPlayed() > 0) {
                // بارگذاری سلاح قبلی
                String lastWeaponName = currentUser.getLastWeaponUsed();
                if (lastWeaponName != null && !lastWeaponName.isEmpty()) {
                    for (WeaponType weapon : WeaponType.values()) {
                        if (weapon.getName().equals(lastWeaponName)) {
                            selectedWeapon = weapon;
                            break;
                        }
                    }
                }

                // بارگذاری قهرمان قبلی
                String lastHeroName = currentUser.getLastHeroUsed();
                if (lastHeroName != null && !lastHeroName.isEmpty()) {
                    for (HeroType hero : HeroType.values()) {
                        if (hero.getName().equals(lastHeroName)) {
                            selectedHero = hero;
                            break;
                        }
                    }
                }

                // بارگذاری زمان بازی قبلی
                selectedTime = currentUser.getLastGameTime();
                if (selectedTime <= 0) {
                    selectedTime = 2; // زمان پیش‌فرض
                }
            } else {
                // تنظیم مقادیر پیش‌فرض برای کاربر جدید
                if (WeaponType.values().length > 0) {
                    selectedWeapon = WeaponType.values()[0];
                }
                if (HeroType.values().length > 0) {
                    selectedHero = HeroType.values()[0];
                }
                selectedTime = 2; // زمان پیش‌فرض
            }
        }
    }

    /**
     * ذخیره اطلاعات بازی کاربر
     */
    public void saveUserGameData() {
        if (currentUser != null && selectedWeapon != null && selectedHero != null) {
            currentUser.setLastWeaponUsed(selectedWeapon.getName());
            currentUser.setLastHeroUsed(selectedHero.getName());
            currentUser.setLastGameTime(selectedTime);

            // به‌روزرسانی کاربر در SaveData
            saveData.updateUser(currentUser);
        }
    }

    /**
     * شروع بازی با تنظیمات انتخاب شده
     */
    public void startGame() {
        if (!checkUserLoggedIn()) {
            return;
        }

        if (selectedHero == null || selectedWeapon == null || selectedTime <= 0) {
            if (view != null) {
                DialogManager.showErrorDialog(view.getStage(),"Fail","Please select all fields.",null);
            }
            return;
        }

        // ذخیره اطلاعات بازی قبل از شروع
        saveUserGameData();

        // افزایش تعداد بازی‌های انجام شده
        currentUser.incrementTotalGamesPlayed();
        saveData.updateUser(currentUser);

//        // ایجاد GameController و شروع بازی
//        GameController gameController = new GameController(selectedHero, selectedWeapon, selectedTime);
//        Main.getMain().setScreen(gameController.getGameScreen());
    }

    /**
     * تنظیم سلاح انتخاب شده
     */
    public void setSelectedWeapon(WeaponType weapon) {
        this.selectedWeapon = weapon;
    }

    /**
     * تنظیم قهرمان انتخاب شده
     */
    public void setSelectedHero(HeroType hero) {
        this.selectedHero = hero;
    }

    /**
     * تنظیم زمان بازی انتخاب شده
     */
    public void setSelectedTime(int time) {
        this.selectedTime = time;
    }

    /**
     * دریافت سلاح انتخاب شده
     */
    public WeaponType getSelectedWeapon() {
        return selectedWeapon;
    }

    /**
     * دریافت قهرمان انتخاب شده
     */
    public HeroType getSelectedHero() {
        return selectedHero;
    }

    /**
     * دریافت زمان بازی انتخاب شده
     */
    public int getSelectedTime() {
        return selectedTime;
    }

    /**
     * دریافت نمای منو
     */
    public PreGameMenu getView() {
        return view;
    }
}
