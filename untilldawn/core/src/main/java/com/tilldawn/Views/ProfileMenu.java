package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.ProfileMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;

public class ProfileMenu implements Screen {
    private Stage stage;
    private final Label menuTitle;
    public Table table;
    private final ProfileMenuController controller;
    private final String currentUsername;

    private Label usernameValueLabel;
    private TextField newUsername;
    private TextField currentPassword;
    private TextField newPassword;
    private TextField confirmPassword;
    private CheckBox showPasswordsCheckbox;
    private TextButton changeUsernameButton;
    private TextButton changePasswordButton;
    private TextButton deleteAccountButton;
    private TextButton backButton;

    // اندازه‌های استاندارد برای عناصر فرم
    private static final float FIELD_WIDTH = 450;
    private static final float FIELD_HEIGHT = 70;
    private static final float LABEL_SCALE = 1.0f;
    private static final float TITLE_SCALE = 1.8f;
    private static final float BUTTON_WIDTH = 500;
    private static final float BUTTON_HEIGHT_SMALL = 100;
    private static final float PADDING = 15;
    private static final float BUTTON_SPACING = 10;

    public ProfileMenu(ProfileMenuController controller, Skin skin, String username) {
        this.controller = controller;
        this.currentUsername = username;

        // عنوان با فونت بزرگتر
        menuTitle = new Label("PROFILE", skin);
        menuTitle.setFontScale(TITLE_SCALE);
        menuTitle.setColor(Color.CYAN);

        // نمایش نام کاربری فعلی
        this.usernameValueLabel = new Label(username, skin);
        this.usernameValueLabel.setColor(Color.WHITE);
        this.usernameValueLabel.setFontScale(1.2f);

        // فیلدهای ورودی با اندازه بزرگتر
        this.newUsername = new TextField("", skin);
        this.newUsername.setMessageText("Enter new username...");

        this.currentPassword = new TextField("", skin);
        this.currentPassword.setMessageText("Enter current password...");
        this.currentPassword.setPasswordMode(true);
        this.currentPassword.setPasswordCharacter('*');

        this.newPassword = new TextField("", skin);
        this.newPassword.setMessageText("Enter new password...");
        this.newPassword.setPasswordMode(true);
        this.newPassword.setPasswordCharacter('*');

        this.confirmPassword = new TextField("", skin);
        this.confirmPassword.setMessageText("Confirm new password...");
        this.confirmPassword.setPasswordMode(true);
        this.confirmPassword.setPasswordCharacter('*');

        // چک‌باکس برای نمایش/مخفی کردن رمز عبور
        this.showPasswordsCheckbox = new CheckBox(" Show Passwords", skin);
        this.showPasswordsCheckbox.setChecked(false);
        this.showPasswordsCheckbox.getLabel().setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
        this.showPasswordsCheckbox.getLabel().setFontScale(0.8f);
        this.showPasswordsCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean showPasswords = showPasswordsCheckbox.isChecked();
                currentPassword.setPasswordMode(!showPasswords);
                newPassword.setPasswordMode(!showPasswords);
                confirmPassword.setPasswordMode(!showPasswords);
            }
        });

        // دکمه‌ها
        this.changeUsernameButton = new TextButton("CHANGE USERNAME", skin);
        this.changePasswordButton = new TextButton("CHANGE PASSWORD", skin);
        this.deleteAccountButton = new TextButton("DELETE ACCOUNT", skin);
        this.backButton = new TextButton("BACK", skin);

        this.table = new Table();

        // اعمال فونت بزرگتر به TextField ها
        TextField.TextFieldStyle style = this.newUsername.getStyle();
        style.font.getData().setScale(1.2f);

        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();

        // تنظیم فاصله‌ها
        table.pad(50);

        // اضافه کردن عنوان
        table.add(menuTitle).colspan(2).padBottom(40);

        // بخش نام کاربری فعلی
        table.row().pad(PADDING, 70, PADDING, 0);
        Label currentUsernameLabel = new Label("CURRENT USERNAME:", GameAssetManager.getGameAssetManager().getSkin());
        currentUsernameLabel.setColor(Color.CYAN);
        currentUsernameLabel.setFontScale(LABEL_SCALE);
        table.add(currentUsernameLabel).width(200).right().padRight(30);
        table.add(usernameValueLabel).width(FIELD_WIDTH).height(40).left();

        // جدول برای تغییر نام کاربری
        table.row().pad(PADDING * 2, 70, PADDING, 0);
        Label changeUsernameTitle = new Label("CHANGE USERNAME", GameAssetManager.getGameAssetManager().getSkin());
        changeUsernameTitle.setColor(Color.GOLD);
        changeUsernameTitle.setFontScale(1.2f);
        table.add(changeUsernameTitle).colspan(2).center().padBottom(10);

        // فیلد نام کاربری جدید
        table.row().pad(PADDING, 70, PADDING, 0);
        Label newUsernameLabel = new Label("NEW USERNAME:", GameAssetManager.getGameAssetManager().getSkin());
        newUsernameLabel.setColor(Color.CYAN);
        newUsernameLabel.setFontScale(LABEL_SCALE);
        table.add(newUsernameLabel).width(200).right().padRight(30);
        table.add(newUsername).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();

        // دکمه تغییر نام کاربری
        table.row().pad(PADDING, 0, PADDING * 2, 0);
        changeUsernameButton.setColor(new Color(0.2f, 0.6f, 0.9f, 1f));
        changeUsernameButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                changeUsernameButton.setColor(new Color(0.3f, 0.7f, 1f, 1f));
                changeUsernameButton.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                changeUsernameButton.setColor(new Color(0.2f, 0.6f, 0.9f, 1f));
                changeUsernameButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });
        table.add(changeUsernameButton).colspan(2).width(BUTTON_WIDTH + 30).height(BUTTON_HEIGHT_SMALL);

        // خط جداکننده
        table.row().pad(PADDING, 0, PADDING, 0);
        Label separator1 = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        separator1.setColor(Color.GRAY);
        table.add(separator1).colspan(2).width(500).height(1).pad(PADDING);

        // جدول برای تغییر رمز عبور
        table.row().pad(PADDING, 70, PADDING, 0);
        Label changePasswordTitle = new Label("CHANGE PASSWORD", GameAssetManager.getGameAssetManager().getSkin());
        changePasswordTitle.setColor(Color.GOLD);
        changePasswordTitle.setFontScale(1.2f);
        table.add(changePasswordTitle).colspan(2).center().padBottom(10);

        // فیلد رمز عبور فعلی
        table.row().pad(PADDING, 70, PADDING, 0);
        Label currentPasswordLabel = new Label("CURRENT PASSWORD:", GameAssetManager.getGameAssetManager().getSkin());
        currentPasswordLabel.setColor(Color.CYAN);
        currentPasswordLabel.setFontScale(LABEL_SCALE);
        table.add(currentPasswordLabel).width(200).right().padRight(30);
        table.add(currentPassword).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();

        // فیلد رمز عبور جدید
        table.row().pad(PADDING, 70, PADDING, 0);
        Label newPasswordLabel = new Label("NEW PASSWORD:", GameAssetManager.getGameAssetManager().getSkin());
        newPasswordLabel.setColor(Color.CYAN);
        newPasswordLabel.setFontScale(LABEL_SCALE);
        table.add(newPasswordLabel).width(200).right().padRight(30);
        table.add(newPassword).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();

        // فیلد تأیید رمز عبور
        table.row().pad(PADDING, 70, PADDING, 0);
        Label confirmPasswordLabel = new Label("CONFIRM PASSWORD:", GameAssetManager.getGameAssetManager().getSkin());
        confirmPasswordLabel.setColor(Color.CYAN);
        confirmPasswordLabel.setFontScale(LABEL_SCALE);
        table.add(confirmPasswordLabel).width(200).right().padRight(30);
        table.add(confirmPassword).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();

        // چک‌باکس نمایش رمز عبور
        table.row().pad(5, 0, PADDING, 0);
        table.add().width(200); // ستون خالی
        table.add(showPasswordsCheckbox).left();

        // دکمه تغییر رمز عبور
        table.row().pad(PADDING, 0, PADDING * 2, 0);
        changePasswordButton.setColor(new Color(0.2f, 0.6f, 0.9f, 1f));
        changePasswordButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                changePasswordButton.setColor(new Color(0.3f, 0.7f, 1f, 1f));
                changePasswordButton.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                changePasswordButton.setColor(new Color(0.2f, 0.6f, 0.9f, 1f));
                changePasswordButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });
        table.add(changePasswordButton).colspan(2).width(BUTTON_WIDTH + 30).height(BUTTON_HEIGHT_SMALL);

        // خط جداکننده
        table.row().pad(PADDING, 0, PADDING, 0);
        Label separator2 = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        separator2.setColor(Color.GRAY);
        table.add(separator2).colspan(2).width(500).height(1).pad(PADDING);

        // جدول برای حذف حساب کاربری
        table.row().pad(PADDING, 0, PADDING, 0);
        Label deleteAccountTitle = new Label("DANGER ZONE", GameAssetManager.getGameAssetManager().getSkin());
        deleteAccountTitle.setColor(Color.RED);
        deleteAccountTitle.setFontScale(1.2f);
        table.add(deleteAccountTitle).colspan(2).center().padBottom(10);

        // دکمه حذف حساب کاربری
        table.row().pad(PADDING, 0, PADDING * 2, 0);
        deleteAccountButton.setColor(new Color(0.9f, 0.2f, 0.2f, 1f)); // رنگ قرمز
        deleteAccountButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                deleteAccountButton.setColor(new Color(1f, 0.3f, 0.3f, 1f)); // قرمز روشن‌تر
                deleteAccountButton.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                deleteAccountButton.setColor(new Color(0.9f, 0.2f, 0.2f, 1f)); // برگشت به رنگ اصلی
                deleteAccountButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });
        table.add(deleteAccountButton).colspan(2).width(BUTTON_WIDTH).height(BUTTON_HEIGHT_SMALL);

        // خط جداکننده
        table.row().pad(PADDING, 0, PADDING, 0);
        Label separator3 = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        separator3.setColor(Color.GRAY);
        table.add(separator3).colspan(2).width(500).height(1).pad(PADDING);

        // دکمه بازگشت
        table.row().pad(PADDING, 0, PADDING, 0);
        backButton.setColor(new Color(0.5f, 0.5f, 0.5f, 1f)); // رنگ خاکستری
        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                backButton.setColor(new Color(0.7f, 0.7f, 0.7f, 1f)); // خاکستری روشن‌تر
                backButton.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                backButton.setColor(new Color(0.5f, 0.5f, 0.5f, 1f)); // برگشت به رنگ اصلی
                backButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });
        table.add(backButton).colspan(2).width(BUTTON_WIDTH - 275).height(BUTTON_HEIGHT_SMALL);

        stage.addActor(table);

        // تنظیم اندازه فونت برای TextField ها
        try {
            newUsername.getStyle().font.getData().setScale(1.2f);
            currentPassword.getStyle().font.getData().setScale(1.2f);
            newPassword.getStyle().font.getData().setScale(1.2f);
            confirmPassword.getStyle().font.getData().setScale(1.2f);
        } catch (Exception e) {
            Gdx.app.log("ProfileMenu", "Could not set font scale for TextFields");
        }
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1); // پس‌زمینه تیره
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update(i, i1, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public TextField getNewUsername() {
        return newUsername;
    }

    public TextField getCurrentPassword() {
        return currentPassword;
    }

    public TextField getNewPassword() {
        return newPassword;
    }

    public TextField getConfirmPassword() {
        return confirmPassword;
    }

    public CheckBox getShowPasswordsCheckbox() {
        return showPasswordsCheckbox;
    }

    public TextButton getChangeUsernameButton() {
        return changeUsernameButton;
    }

    public TextButton getChangePasswordButton() {
        return changePasswordButton;
    }

    public TextButton getDeleteAccountButton() {
        return deleteAccountButton;
    }

    public TextButton getBackButton() {
        return backButton;
    }

    public Stage getStage() {
        return stage;
    }

    public void updateUsernameLabel(String newUsername) {
        this.usernameValueLabel.setText(newUsername);
    }
}
