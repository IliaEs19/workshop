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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.ProfileLoginMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;

public class ProfileLoginMenu implements Screen {
    private Stage stage;
    private final Label menuTitle;
    public Table table;
    private final ProfileLoginMenuController controller;

    private TextField userName;
    private TextField password;
    private CheckBox showPasswordCheckbox;
    private TextButton accessButton;
    private TextButton backButton;

    // اندازه‌های استاندارد برای عناصر فرم
    private static final float FIELD_WIDTH = 470;
    private static final float FIELD_HEIGHT = 100;
    private static final float LABEL_SCALE = 1.0f;
    private static final float TITLE_SCALE = 1.8f;
    private static final float BUTTON_WIDTH = 465;
    private static final float BUTTON_HEIGHT = 120;
    private static final float PADDING = 15;
    private static final float BUTTON_SPACING = 10;

    public ProfileLoginMenu(ProfileLoginMenuController controller, Skin skin) {
        this.controller = controller;

        menuTitle = new Label("ACCESS PROFILE", skin);
        menuTitle.setFontScale(TITLE_SCALE);
        menuTitle.setColor(Color.CYAN);

        this.userName = new TextField("", skin);
        this.userName.setMessageText("Enter your username...");

        this.password = new TextField("", skin);
        this.password.setMessageText("Enter your password...");
        this.password.setPasswordMode(true);
        this.password.setPasswordCharacter('*');

        this.showPasswordCheckbox = new CheckBox(" Show Password", skin);
        this.showPasswordCheckbox.setChecked(false);
        this.showPasswordCheckbox.getLabel().setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
        this.showPasswordCheckbox.getLabel().setFontScale(0.8f);
        this.showPasswordCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                password.setPasswordMode(!showPasswordCheckbox.isChecked());
            }
        });

        this.accessButton = new TextButton("ACCESS PROFILE", skin);
        this.backButton = new TextButton("BACK", skin);

        this.table = new Table();

        TextField.TextFieldStyle style = this.userName.getStyle();
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
        table.pad(70);

        // اضافه کردن عنوان
        table.add(menuTitle).colspan(2).padBottom(60);

        // توضیحات
        Label infoLabel = new Label("Please enter your credentials to access your profile", GameAssetManager.getGameAssetManager().getSkin());
        infoLabel.setColor(Color.LIGHT_GRAY);
        infoLabel.setFontScale(1.0f);
        table.row().pad(PADDING, 0, PADDING * 2, 0);
        table.add(infoLabel).colspan(2).center();

        // بخش نام کاربری
        table.row().pad(PADDING, 70, PADDING, 100);
        Label userLabel = new Label("USERNAME:", GameAssetManager.getGameAssetManager().getSkin());
        userLabel.setColor(Color.CYAN);
        userLabel.setFontScale(LABEL_SCALE);
        table.add(userLabel).width(200).right().padRight(30);
        table.add(userName).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();

        // بخش رمز عبور
        table.row().pad(PADDING, 70, PADDING, 100);
        Label passLabel = new Label("PASSWORD:", GameAssetManager.getGameAssetManager().getSkin());
        passLabel.setColor(Color.CYAN);
        passLabel.setFontScale(LABEL_SCALE);
        table.add(passLabel).width(200).right().padRight(30);

        // ایجاد جدول برای قرار دادن فیلد رمز عبور و چک‌باکس
        Table passwordTable = new Table();
        passwordTable.add(password).width(FIELD_WIDTH).height(FIELD_HEIGHT);
        passwordTable.row().pad(5, 0, 0, 0);
        passwordTable.add(showPasswordCheckbox).left();

        table.add(passwordTable).left();

        // دکمه دسترسی به پروفایل
        table.row().pad(PADDING * 2, 0, BUTTON_SPACING, 0);
        accessButton.setColor(new Color(0.2f, 0.6f, 0.9f, 1f));

        accessButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                accessButton.setColor(new Color(0.3f, 0.7f, 1f, 1f)); // آبی روشن‌تر
                accessButton.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                accessButton.setColor(new Color(0.2f, 0.6f, 0.9f, 1f)); // برگشت به رنگ اصلی
                accessButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });

        table.add(accessButton).colspan(2).width(BUTTON_WIDTH).height(BUTTON_HEIGHT - 20);

        // دکمه بازگشت
        table.row().pad(BUTTON_SPACING, 0, PADDING * 2, 0);
        backButton.setColor(new Color(0.9f, 0.3f, 0.3f, 1f)); // رنگ قرمز ملایم

        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                backButton.setColor(new Color(1f, 0.4f, 0.4f, 1f)); // قرمز روشن‌تر
                backButton.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                backButton.setColor(new Color(0.9f, 0.3f, 0.3f, 1f)); // برگشت به رنگ اصلی
                backButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });

        table.add(backButton).colspan(2).width(BUTTON_WIDTH).height(BUTTON_HEIGHT - 30);

        stage.addActor(table);

        // تنظیم اندازه فونت برای TextField ها
        try {
            userName.getStyle().font.getData().setScale(1.2f);
            password.getStyle().font.getData().setScale(1.2f);
        } catch (Exception e) {
            Gdx.app.log("ProfileLoginMenu", "Could not set font scale for TextFields");
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

    public TextField getUserName() {
        return userName;
    }

    public TextField getPassword() {
        return password;
    }

    public CheckBox getShowPasswordCheckbox() {
        return showPasswordCheckbox;
    }

    public TextButton getAccessButton() {
        return accessButton;
    }

    public TextButton getBackButton() {
        return backButton;
    }

    public Stage getStage() {
        return stage;
    }
}
