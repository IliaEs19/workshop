package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tilldawn.Main;
import com.tilldawn.Models.DialogManager;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Result;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Views.ForgotPasswordMenu;
import com.tilldawn.Views.LoginMenu;

public class ForgotPasswordMenuController {
    private ForgotPasswordMenu view;
    private boolean resetButtonPressed = false;

    public void setView(ForgotPasswordMenu view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        if (view != null) {
            // لیسنر دکمه بازنشانی رمز عبور
            view.getResetButton().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!resetButtonPressed) {
                        resetButtonPressed = true;
                        processPasswordReset();
                        // Reset flag after a short delay to prevent multiple triggers
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                resetButtonPressed = false;
                            }
                        });
                    }
                }
            });

            // لیسنر دکمه بازگشت
            view.getBackButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // بازگشت به منوی ورود
                    Main.getMain().setScreen(new LoginMenu(
                        new LoginMenuController(),
                        GameAssetManager.getGameAssetManager().getSkin()));
                }
            });
        }
    }

    private void processPasswordReset() {
        String username = view.getUsername();
        String securityAnswer = view.getSecurityAnswer().getText().trim();
        String newPassword = view.getNewPassword().getText();
        String confirmPassword = view.getConfirmPassword().getText();

        // بررسی خالی نبودن فیلدها
        if (securityAnswer.isEmpty()) {
            DialogManager.showErrorDialog(view.getStage(), "Reset Failed",
                "Security answer cannot be empty.", null);
            return;
        }

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            DialogManager.showErrorDialog(view.getStage(), "Reset Failed",
                "New password and confirmation cannot be empty.", null);
            return;
        }

        // بررسی صحت پاسخ سؤال امنیتی
        if (!SaveData.getInstance().validateSecurityAnswer(username, securityAnswer)) {
            DialogManager.showErrorDialog(view.getStage(), "Reset Failed",
                "Incorrect security answer. Please try again.", null);
            return;
        }

        // بررسی تطابق رمز عبور و تأیید آن
        if (!newPassword.equals(confirmPassword)) {
            DialogManager.showErrorDialog(view.getStage(), "Reset Failed",
                "New password and confirmation do not match.", null);
            return;
        }

        Result passwordValidation = validatePassword(newPassword);
        if (!passwordValidation.isSuccessful()) {
            DialogManager.showErrorDialog(view.getStage(), "Reset Failed",
                passwordValidation.getMessage(), null);
            return;
        }

        boolean success = SaveData.getInstance().resetPasswordWithSecurityQuestion(
            username, securityAnswer, newPassword);

        if (success) {
            DialogManager.showSuccessDialog(view.getStage(), "Success",
                "Password has been reset successfully!", new Runnable() {
                    @Override
                    public void run() {
                        Main.getMain().setScreen(new LoginMenu(
                            new LoginMenuController(),
                            GameAssetManager.getGameAssetManager().getSkin()));
                    }
                });
        } else {
            DialogManager.showErrorDialog(view.getStage(), "Reset Failed",
                "Failed to reset password. Please try again later.", null);
        }
    }

    // اعتبارسنجی رمز عبور
    public static Result validatePassword(String password) {
        if (password.length() < 8) {
            return new Result(false, "Password must be at least 8 characters long.");
        } else if (!password.matches(".*[@%$#&*()_].*")) {
            return new Result(false, "Password must contain at least one special character (@%$#&*()_).");
        } else if (!password.matches(".*\\d.*")) {
            return new Result(false, "Password must contain at least one number.");
        } else if (!password.matches(".*[A-Z].*")) {
            return new Result(false, "Password must contain at least one uppercase letter.");
        }

        return new Result(true, "");
    }
}
