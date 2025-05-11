package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tilldawn.Main;
import com.tilldawn.Models.DialogManager;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Views.MainMenu;
import com.tilldawn.Views.ProfileLoginMenu;
import com.tilldawn.Views.*;

public class ProfileLoginMenuController {
    private ProfileLoginMenu view;
    private boolean accessButtonPressed = false;

    public void setView(ProfileLoginMenu view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        if (view != null) {
            // لیسنر دکمه دسترسی به پروفایل
            view.getAccessButton().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!accessButtonPressed) {
                        accessButtonPressed = true;
                        processProfileAccess();
                        // Reset flag after a short delay to prevent multiple triggers
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                accessButtonPressed = false;
                            }
                        });
                    }
                }
            });

            // لیسنر دکمه بازگشت
            view.getBackButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // بازگشت به منوی اصلی
                    Main.getMain().setScreen(new MainMenu(
                        new MainMenuController(),
                        GameAssetManager.getGameAssetManager().getSkin()));
                }
            });
        }
    }

    private void processProfileAccess() {
        String username = view.getUserName().getText().trim();
        String password = view.getPassword().getText();

        // بررسی خالی نبودن فیلدها
        if (username.isEmpty() || password.isEmpty()) {
            DialogManager.showErrorDialog(view.getStage(), "Access Failed",
                "Username and password cannot be empty.", null);
            return;
        }

        // بررسی وجود کاربر
        if (!SaveData.getInstance().userExists(username)) {
            DialogManager.showErrorDialog(view.getStage(), "Access Failed",
                "Username not found. Please check your username or register first.", null);
            return;
        }

        // بررسی صحت رمز عبور
        if (!SaveData.getInstance().validateUser(username, password)) {
            DialogManager.showErrorDialog(view.getStage(), "Access Failed",
                "Incorrect password. Please try again.", null);
            return;
        }

        // دسترسی موفقیت‌آمیز به پروفایل
        Main.getMain().setScreen(new ProfileMenu(
            new ProfileMenuController(),
            GameAssetManager.getGameAssetManager().getSkin(),
            username));
    }
}
