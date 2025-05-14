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
import com.tilldawn.Views.AvatarSelectionDialog;
import com.tilldawn.Views.MainMenu;
import com.tilldawn.Views.ProfileMenu;
import com.badlogic.gdx.utils.Timer;


public class ProfileMenuController {
    private ProfileMenu view;
    private boolean changeUsernameButtonPressed = false;
    private boolean changePasswordButtonPressed = false;
    private boolean deleteAccountButtonPressed = false;

    public void setView(ProfileMenu view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        if (view != null) {

            view.getChangeAvatarButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showAvatarSelectionDialog();
                }
            });

            // لیسنر دکمه تغییر نام کاربری
            view.getChangeUsernameButton().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!changeUsernameButtonPressed) {
                        changeUsernameButtonPressed = true;
                        processChangeUsername();
                        // Reset flag after a short delay to prevent multiple triggers
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                changeUsernameButtonPressed = false;
                            }
                        });
                    }
                }
            });

            // لیسنر دکمه تغییر رمز عبور
            view.getChangePasswordButton().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!changePasswordButtonPressed) {
                        changePasswordButtonPressed = true;
                        processChangePassword();
                        // Reset flag after a short delay to prevent multiple triggers
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                changePasswordButtonPressed = false;
                            }
                        });
                    }
                }
            });

            // لیسنر دکمه حذف حساب کاربری
            view.getDeleteAccountButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (!deleteAccountButtonPressed) {
                        deleteAccountButtonPressed = true;
                        confirmDeleteAccount();
                        // Reset flag after a short delay to prevent multiple triggers
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                deleteAccountButtonPressed = false;
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

    private void showAvatarSelectionDialog() {
        // ایجاد و نمایش دیالوگ انتخاب آواتار
        AvatarSelectionDialog dialog = new AvatarSelectionDialog(
            view.getStage(),
            view.getCurrentUsername(),
            new Runnable() {
                @Override
                public void run() {
                    // به‌روزرسانی آواتار در صفحه پروفایل پس از تغییر
                    view.updateUserAvatar(view.getCurrentUsername());
                }
            }
        );

        dialog.show(view.getStage());
    }

    private void processChangeUsername() {
        String currentUsername = view.getCurrentUsername();
        String newUsername = view.getNewUsername().getText().trim();
        String currentPassword = view.getCurrentPassword().getText();

        // بررسی خالی نبودن فیلدها
        if (newUsername.isEmpty()) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "New username cannot be empty.", null);
            return;
        }

        if (currentPassword.isEmpty()) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "Please enter your current password to confirm changes.", null);
            return;
        }

        // بررسی یکسان نبودن نام کاربری جدید با نام کاربری فعلی
        if (newUsername.equals(currentUsername)) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "New username is the same as current username.", null);
            return;
        }

        // بررسی تکراری نبودن نام کاربری جدید
        if (SaveData.getInstance().userExists(newUsername)) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "Username already exists. Please choose a different username.", null);
            return;
        }

        // تأیید رمز عبور فعلی
        if (!SaveData.getInstance().validateUser(currentUsername, currentPassword)) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "Incorrect password. Please enter your correct password.", null);
            return;
        }

        // تغییر نام کاربری
        boolean success = SaveData.getInstance().changeUsername(currentUsername, newUsername, currentPassword);

        if (success) {
            // به‌روزرسانی نام کاربری نمایش داده شده
            view.updateUsernameLabel(newUsername);

            // پاک کردن فیلدها
            view.getNewUsername().setText("");
            view.getCurrentPassword().setText("");

            // نمایش پیام موفقیت
            final String finalNewUsername = newUsername;
            DialogManager.showSuccessDialog(view.getStage(), "Success",
                "Username has been changed successfully!", null);


            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            Main.getMain().setScreen(new ProfileMenu(
                                new ProfileMenuController(),
                                GameAssetManager.getGameAssetManager().getSkin(),
                                finalNewUsername));
                        }
                    });
                }
            }, 8); // 2 ثانیه تأخیر
        } else {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "Failed to change username. Please try again later.", null);
        }
    }

    private void processChangePassword() {
        String currentUsername = view.getCurrentUsername();
        String currentPassword = view.getCurrentPassword().getText();
        String newPassword = view.getNewPassword().getText();
        String confirmPassword = view.getConfirmPassword().getText();

        // بررسی خالی نبودن فیلدها
        if (currentPassword.isEmpty()) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "Current password cannot be empty.", null);
            return;
        }

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "New password and confirmation cannot be empty.", null);
            return;
        }

        // بررسی تطابق رمز عبور جدید و تأیید آن
        if (!newPassword.equals(confirmPassword)) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "New password and confirmation do not match.", null);
            return;
        }

        // بررسی یکسان نبودن رمز عبور جدید با رمز عبور فعلی
        if (newPassword.equals(currentPassword)) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "New password is the same as current password.", null);
            return;
        }

        // اعتبارسنجی رمز عبور جدید
        Result passwordValidation = validatePassword(newPassword);
        if (!passwordValidation.isSuccessful()) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                passwordValidation.getMessage(), null);
            return;
        }

        // تأیید رمز عبور فعلی
        if (!SaveData.getInstance().validateUser(currentUsername, currentPassword)) {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "Incorrect current password. Please try again.", null);
            return;
        }

        // تغییر رمز عبور
        boolean success = SaveData.getInstance().changePassword(currentUsername, currentPassword, newPassword);

        if (success) {
            // پاک کردن فیلدها
            view.getCurrentPassword().setText("");
            view.getNewPassword().setText("");
            view.getConfirmPassword().setText("");

            // نمایش پیام موفقیت
            DialogManager.showSuccessDialog(view.getStage(), "Success",
                "Password has been changed successfully!", null);
        } else {
            DialogManager.showErrorDialog(view.getStage(), "Change Failed",
                "Failed to change password. Please try again later.", null);
        }
    }

    private void confirmDeleteAccount() {
        DialogManager.showConfirmDialog(view.getStage(), "Confirm Deletion",
            "Are you sure you want to delete your account? This action cannot be undone.",
            new Runnable() {
                @Override
                public void run() {
                    processDeleteAccount();
                }
            },
            null);
    }

    private void processDeleteAccount() {
        String currentUsername = view.getCurrentUsername();
        String currentPassword = view.getCurrentPassword().getText();

        // بررسی خالی نبودن رمز عبور
        if (currentPassword.isEmpty()) {
            DialogManager.showErrorDialog(view.getStage(), "Deletion Failed",
                "Please enter your current password to confirm account deletion.", null);
            return;
        }

        // تأیید رمز عبور
        if (!SaveData.getInstance().validateUser(currentUsername, currentPassword)) {
            DialogManager.showErrorDialog(view.getStage(), "Deletion Failed",
                "Incorrect password. Please enter your correct password.", null);
            return;
        }

        // حذف حساب کاربری
        boolean success = SaveData.getInstance().removeUser(currentUsername);

        if (success) {
            // نمایش پیام موفقیت و بازگشت به منوی اصلی
            DialogManager.showSuccessDialog(view.getStage(), "Success",
                "Your account has been deleted successfully!", new Runnable() {
                    @Override
                    public void run() {
                        Main.getMain().setScreen(new MainMenu(
                            new MainMenuController(),
                            GameAssetManager.getGameAssetManager().getSkin()));
                    }
                });
        } else {
            DialogManager.showErrorDialog(view.getStage(), "Deletion Failed",
                "Failed to delete account. Please try again later.", null);
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
