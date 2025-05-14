package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Views.MainMenu;
import com.tilldawn.Views.TalentMenu;

public class TalentMenuController {
    private TalentMenu view;

    public void setView(TalentMenu view) {
        this.view = view;
    }

    /**
     * اضافه کردن یک دکمه بازگشت به منوی اصلی
     * این متد می‌تواند در صورت نیاز فراخوانی شود
     */
    public void addBackButtonListener(ClickListener listener) {
        // اگر نیاز به اضافه کردن لیسنر به دکمه بازگشت باشد
    }

    /**
     * بازگشت به منوی اصلی
     */
    public void goToMainMenu() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Main.getMain().setScreen(new MainMenu(new MainMenuController(),
                    GameAssetManager.getGameAssetManager().getSkin()));
            }
        });
    }

    /**
     * انتخاب قهرمان پیش‌فرض بازی
     * این متد می‌تواند برای ذخیره انتخاب کاربر استفاده شود
     */
    public void selectDefaultHero(HeroType hero) {
        // در اینجا می‌توانید کد ذخیره انتخاب کاربر را اضافه کنید
        // مثلاً ذخیره در فایل تنظیمات یا پایگاه داده
        Gdx.app.log("TalentMenuController", "Selected hero: " + hero.getName());
    }
}
