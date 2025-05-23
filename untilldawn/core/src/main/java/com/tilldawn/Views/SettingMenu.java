package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Controllers.SettingMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.GameSettings;
import com.tilldawn.Models.MusicManager;

public class SettingMenu implements Screen {
    private Stage stage;
    private Table mainTable;
    private final SettingMenuController controller;
    private Image backgroundImage;

    // کنترل‌های تنظیمات
    private CheckBox musicEnabledCheckbox;
    private Slider volumeSlider;
    private TextButton mainThemeButton;
    private TextButton secondaryThemeButton;
    private TextButton backButton;
    private ProgressBar volumeBar;
    private Label volumeValueLabel;

    // ثابت‌های طراحی
    private static final float BUTTON_WIDTH = 700;
    private static final float BUTTON_HEIGHT = 100;
    private static final float PADDING = 20;
    private static final float SLIDER_WIDTH = 300;
    private static final Color TITLE_COLOR = new Color(0.2f, 0.8f, 1f, 1f); // آبی روشن
    private static final Color SECTION_TITLE_COLOR = new Color(0.9f, 0.9f, 0.3f, 1f); // زرد روشن
    private static final Color BUTTON_COLOR = new Color(0.2f, 0.6f, 0.9f, 1f); // آبی
    private static final Color BUTTON_HOVER_COLOR = new Color(0.3f, 0.7f, 1f, 1f); // آبی روشن‌تر
    private static final Color ACTIVE_BUTTON_COLOR = new Color(0.3f, 0.8f, 0.3f, 1f); // سبز
    private static final Color BACK_BUTTON_COLOR = new Color(0.8f, 0.3f, 0.3f, 1f); // قرمز

    public SettingMenu(SettingMenuController controller, Skin skin) {
        this.controller = controller;
        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

//        // بارگذاری تصویر پس‌زمینه
//        try {
//            Texture backgroundTexture = GameAssetManager.getGameAssetManager().getMainMenuBackground();
//            backgroundImage = new Image(backgroundTexture);
//            backgroundImage.setFillParent(true);
//            stage.addActor(backgroundImage);
//        } catch (Exception e) {
//            Gdx.app.error("SettingMenu", "Could not load background image", e);
//        }

        // ایجاد جدول اصلی
        mainTable = new Table();
        mainTable.setFillParent(true);

        // ایجاد پنل نیمه‌شفاف برای پس‌زمینه منو
        Table panelTable = new Table();
        panelTable.setBackground(createPanelBackground());
        panelTable.pad(PADDING * 5);

        // عنوان اصلی منو
        Label titleLabel = new Label("SETTINGS", GameAssetManager.getGameAssetManager().getSkin(), "default");
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(TITLE_COLOR);
        titleLabel.setAlignment(Align.center);
        panelTable.add(titleLabel).colspan(2).pad(PADDING).expandX().fillX().row();

        // خط جداکننده زیر عنوان
        Image titleSeparator = createSeparator(2, TITLE_COLOR);
        panelTable.add(titleSeparator).colspan(2).pad(PADDING / 2).expandX().fillX().row();

        // بخش تنظیمات موزیک
        Label musicSettingsLabel = new Label("MUSIC SETTINGS", GameAssetManager.getGameAssetManager().getSkin());
        musicSettingsLabel.setFontScale(1.5f);
        musicSettingsLabel.setColor(SECTION_TITLE_COLOR);
        panelTable.add(musicSettingsLabel).colspan(2).pad(PADDING).expandX().fillX().row();

        // چک‌باکس فعال/غیرفعال کردن موزیک
        musicEnabledCheckbox = new CheckBox(" Enable Music", GameAssetManager.getGameAssetManager().getSkin());
        musicEnabledCheckbox.setChecked(MusicManager.getInstance().isMusicEnabled());
        musicEnabledCheckbox.getLabel().setFontScale(1.2f);
        panelTable.add(musicEnabledCheckbox).colspan(2).pad(PADDING / 2).left().row();

        // انتخاب موزیک - عنوان
        Label selectMusicLabel = new Label("SELECT MUSIC:", GameAssetManager.getGameAssetManager().getSkin());
        selectMusicLabel.setFontScale(1.2f);
        panelTable.add(selectMusicLabel).colspan(2).pad(PADDING).left().row();

        // دکمه‌های انتخاب موزیک
        Table musicButtonsTable = new Table();

        // دکمه موزیک اصلی
        mainThemeButton = new TextButton("Music 1", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(mainThemeButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);

        // اگر موزیک اصلی در حال پخش است، رنگ دکمه را تغییر دهید
        if (MusicManager.getInstance().isMainThemePlaying()) {
            mainThemeButton.setColor(ACTIVE_BUTTON_COLOR);
        }

        // دکمه موزیک ثانویه
        secondaryThemeButton = new TextButton("Music 2", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(secondaryThemeButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);

        // اگر موزیک ثانویه در حال پخش است، رنگ دکمه را تغییر دهید
        if (MusicManager.getInstance().isSecondaryThemePlaying()) {
            secondaryThemeButton.setColor(ACTIVE_BUTTON_COLOR);
        }

        musicButtonsTable.add(mainThemeButton).width(BUTTON_WIDTH / 2 - 10).height(BUTTON_HEIGHT).pad(5);
        musicButtonsTable.add(secondaryThemeButton).width(BUTTON_WIDTH / 2 - 10).height(BUTTON_HEIGHT).pad(5);

        panelTable.add(musicButtonsTable).colspan(2).pad(PADDING / 2).expandX().fillX().row();

        // تنظیم بلندی صدا - عنوان
        Label volumeLabel = new Label("VOLUME:", GameAssetManager.getGameAssetManager().getSkin());
        volumeLabel.setFontScale(1.2f);
        panelTable.add(volumeLabel).colspan(2).pad(PADDING).left().row();

        // اسلایدر تنظیم بلندی صدا و نمایش درصد
        Table volumeControlTable = new Table();

        // اسلایدر
        volumeSlider = new Slider(0, 1, 0.01f, false, GameAssetManager.getGameAssetManager().getSkin());
        volumeSlider.setValue(MusicManager.getInstance().getVolume());
        volumeControlTable.add(volumeSlider).width(SLIDER_WIDTH).padRight(PADDING);

        // برچسب نمایش درصد
        volumeValueLabel = new Label(Math.round(volumeSlider.getValue() * 100) + "%",
            GameAssetManager.getGameAssetManager().getSkin());
        volumeValueLabel.setFontScale(1.2f);
        volumeControlTable.add(volumeValueLabel).width(50);

        panelTable.add(volumeControlTable).colspan(2).pad(PADDING / 2).expandX().fillX().row();

        // نوار پیشرفت نمایش بلندی صدا
        volumeBar = new ProgressBar(0, 1, 0.01f, false, GameAssetManager.getGameAssetManager().getSkin());
        volumeBar.setValue(volumeSlider.getValue());
        volumeBar.setAnimateDuration(0.3f);
        panelTable.add(volumeBar).colspan(2).pad(PADDING / 2).width(SLIDER_WIDTH).height(15).row();

        Label controlSettingsLabel = new Label("CONTROL SETTINGS", GameAssetManager.getGameAssetManager().getSkin());
        controlSettingsLabel.setFontScale(1.5f);
        controlSettingsLabel.setColor(SECTION_TITLE_COLOR);
        panelTable.add(controlSettingsLabel).colspan(2).pad(PADDING).expandX().fillX().row();

// عنوان بخش کنترل حرکت
        Label movementControlLabel = new Label("MOVEMENT CONTROL:", GameAssetManager.getGameAssetManager().getSkin());
        movementControlLabel.setFontScale(1.2f);
        panelTable.add(movementControlLabel).colspan(2).pad(PADDING).left().row();

// گزینه‌های کنترل حرکت
        Table controlOptionsTable = new Table();

// دکمه‌های انتخاب نوع کنترل
        final TextButton wasdButton = new TextButton("WASD Keys", GameAssetManager.getGameAssetManager().getSkin());
        final TextButton arrowsButton = new TextButton("Arrow Keys", GameAssetManager.getGameAssetManager().getSkin());

// تنظیم وضعیت دکمه‌ها بر اساس تنظیمات فعلی
        if (controller.getControlType() == GameSettings.ControlType.WASD) {
            wasdButton.setColor(ACTIVE_BUTTON_COLOR);
            arrowsButton.setColor(BUTTON_COLOR);
        } else {
            wasdButton.setColor(BUTTON_COLOR);
            arrowsButton.setColor(ACTIVE_BUTTON_COLOR);
        }

// اضافه کردن لیسنرها برای دکمه‌ها
        wasdButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.setControlType(GameSettings.ControlType.WASD);
                wasdButton.setColor(ACTIVE_BUTTON_COLOR);
                arrowsButton.setColor(BUTTON_COLOR);
            }
        });

        arrowsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.setControlType(GameSettings.ControlType.ARROWS);
                wasdButton.setColor(BUTTON_COLOR);
                arrowsButton.setColor(ACTIVE_BUTTON_COLOR);
            }
        });

// اضافه کردن استایل به دکمه‌ها
        styleButton(wasdButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);
        styleButton(arrowsButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);

// اضافه کردن دکمه‌ها به جدول گزینه‌ها
        controlOptionsTable.add(wasdButton).width(BUTTON_WIDTH / 2 - 10).height(BUTTON_HEIGHT / 1.5f).pad(5);
        controlOptionsTable.add(arrowsButton).width(BUTTON_WIDTH / 2 - 10).height(BUTTON_HEIGHT / 1.5f).pad(5);

// اضافه کردن جدول گزینه‌ها به پنل اصلی
        panelTable.add(controlOptionsTable).colspan(2).pad(PADDING / 2).expandX().fillX().row();

// خط جداکننده بعد از تنظیمات کنترل
        Image controlSeparator = createSeparator(1, new Color(0.5f, 0.5f, 0.5f, 1f));
        panelTable.add(controlSeparator).colspan(2).pad(PADDING).expandX().fillX().row();


        // خط جداکننده قبل از دکمه بازگشت
        Image bottomSeparator = createSeparator(1, new Color(0.5f, 0.5f, 0.5f, 1f));
        panelTable.add(bottomSeparator).colspan(2).pad(PADDING).expandX().fillX().row();

        // دکمه بازگشت
        backButton = new TextButton("BACK", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(backButton, BACK_BUTTON_COLOR, new Color(0.9f, 0.4f, 0.4f, 1f));
        panelTable.add(backButton).colspan(2).width(BUTTON_WIDTH / 2).height(BUTTON_HEIGHT).pad(PADDING).row();

        // اضافه کردن پنل به جدول اصلی
        mainTable.add(panelTable).width(900).pad(50);

        // اضافه کردن جدول اصلی به استیج
        stage.addActor(mainTable);

        // اضافه کردن لیسنرها
        addListeners();

        // انیمیشن ورودی
        mainTable.getColor().a = 0;
        mainTable.addAction(Actions.fadeIn(1.0f));
    }

    private void addListeners() {
        // لیسنر چک‌باکس فعال/غیرفعال کردن موزیک
        musicEnabledCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MusicManager.getInstance().setMusicEnabled(musicEnabledCheckbox.isChecked());
            }
        });

        // لیسنر دکمه موزیک اصلی
        mainThemeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MusicManager.getInstance().switchToMainTheme();
                updateMusicButtonColors();
            }
        });

        // لیسنر دکمه موزیک ثانویه
        secondaryThemeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MusicManager.getInstance().switchToSecondaryTheme();
                updateMusicButtonColors();
            }
        });

        // لیسنر اسلایدر تنظیم بلندی صدا
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                MusicManager.getInstance().setVolume(volume);
                volumeValueLabel.setText(Math.round(volume * 100) + "%");
                volumeBar.setValue(volume);
            }
        });

        // لیسنر دکمه بازگشت
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // انیمیشن خروج
                mainTable.addAction(Actions.sequence(
                    Actions.fadeOut(0.3f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {
                            // بازگشت به منوی اصلی
                            Main.getMain().setScreen(new MainMenu(
                                new MainMenuController(),
                                GameAssetManager.getGameAssetManager().getSkin()));
                        }
                    })
                ));
            }
        });
    }

    private void updateMusicButtonColors() {
        // به‌روزرسانی رنگ دکمه‌های موزیک بر اساس موزیک در حال پخش
        if (MusicManager.getInstance().isMainThemePlaying()) {
            mainThemeButton.setColor(ACTIVE_BUTTON_COLOR);
            secondaryThemeButton.setColor(BUTTON_COLOR);
        } else if (MusicManager.getInstance().isSecondaryThemePlaying()) {
            mainThemeButton.setColor(BUTTON_COLOR);
            secondaryThemeButton.setColor(ACTIVE_BUTTON_COLOR);
        } else {
            mainThemeButton.setColor(BUTTON_COLOR);
            secondaryThemeButton.setColor(BUTTON_COLOR);
        }
    }

    /**
     * ایجاد یک پس‌زمینه نیمه‌شفاف برای پنل
     */
    private TextureRegionDrawable createPanelBackground() {
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.8f); // رنگ مشکی نیمه‌شفاف
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(texture);
    }

    /**
     * ایجاد یک خط جداکننده با رنگ مشخص
     */
    private Image createSeparator(int height, Color color) {
        Pixmap pixmap = new Pixmap(10, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        Image separator = new Image(texture);
        return separator;
    }

    /**
     * اعمال استایل به دکمه
     */
    private void styleButton(final TextButton button, final Color normalColor, final Color hoverColor) {
        button.setColor(normalColor);

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.color(hoverColor, 0.2f));
                button.addAction(Actions.scaleTo(1.05f, 1.05f, 0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // اگر این دکمه فعال است، به رنگ فعال برگردد، در غیر این صورت به رنگ عادی
                if ((button == mainThemeButton && MusicManager.getInstance().isMainThemePlaying()) ||
                    (button == secondaryThemeButton && MusicManager.getInstance().isSecondaryThemePlaying())) {
                    button.addAction(Actions.color(ACTIVE_BUTTON_COLOR, 0.2f));
                } else {
                    button.addAction(Actions.color(normalColor, 0.2f));
                }
                button.addAction(Actions.scaleTo(1f, 1f, 0.2f));
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

    public CheckBox getMusicEnabledCheckbox() {
        return musicEnabledCheckbox;
    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    public TextButton getMainThemeButton() {
        return mainThemeButton;
    }

    public TextButton getSecondaryThemeButton() {
        return secondaryThemeButton;
    }

    public TextButton getBackButton() {
        return backButton;
    }

    public Stage getStage() {
        return stage;
    }
}
