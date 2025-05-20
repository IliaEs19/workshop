package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
//import com.tilldawn.Controllers.GameController;
import com.tilldawn.Controllers.GameController;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Controllers.PreGameMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.WeaponType;

public class PreGameMenu implements Screen {
    private Stage stage;
    private PreGameMenuController controller;
    private Table mainTable;
    private Skin skin;
    private HeroType selectedHero = null;
    private WeaponType selectedWeapon = null;
    private int selectedTime = 0; // زمان انتخاب شده به دقیقه
    private final Array<Integer> availableTimes = new Array<>(new Integer[]{2, 5, 10, 20}); // آپشن‌های زمان بازی

    // ثابت‌های طراحی
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.2f, 1f);
    private static final Color TITLE_COLOR = new Color(0.9f, 0.7f, 0.2f, 1f); // طلایی
    private static final Color SELECTED_COLOR = new Color(0.3f, 0.6f, 0.9f, 1f); // آبی
    private static final Color TEXT_COLOR = new Color(0.9f, 0.9f, 0.9f, 1f); // سفید
    private static final float PADDING = 25f;

    // بخش‌های مختلف منو
    private Table heroSelectionTable;
    private Table weaponSelectionTable;
    private Table timeSelectionTable;
    private Table previewTable;

    // جزئیات انتخاب شده
    private Label heroNameLabel;
    private Label heroDescLabel;
    private Image heroImage;
    private Label weaponNameLabel;
    private Label weaponDescLabel;
    private Image weaponImage;
    private Label timeLabel;


    public PreGameMenu(PreGameMenuController controller) {
        this.controller = controller;
        skin = GameAssetManager.getGameAssetManager().getSkin(); // اضافه کردن این خط

        // بررسی اینکه آیا کاربر لاگین کرده است
        if (!controller.checkUserLoggedIn()) {
            return; // اگر کاربر لاگین نکرده باشد، از سازنده خارج می‌شویم
        }
    }


    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // ایجاد جدول اصلی
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(createBackground(BACKGROUND_COLOR));
        mainTable.pad(PADDING);

        // ایجاد عنوان با افکت درخشش
        Label titleLabel = new Label("PRE-GAME SETUP", skin);
        titleLabel.setFontScale(2.2f);
        titleLabel.setColor(TITLE_COLOR);
        titleLabel.setAlignment(Align.center);

        // افزودن انیمیشن درخشش به عنوان
        titleLabel.addAction(Actions.forever(Actions.sequence(
            Actions.color(new Color(TITLE_COLOR.r, TITLE_COLOR.g, TITLE_COLOR.b, 0.8f), 1.5f),
            Actions.color(TITLE_COLOR, 1.5f)
        )));

        // ایجاد بخش‌های مختلف منو
        createHeroSelectionSection();
        createWeaponSelectionSection();
        createTimeSelectionSection();
        createPreviewSection();
        createButtonsSection();

        // افزودن همه بخش‌ها به جدول اصلی
        mainTable.add(titleLabel).colspan(3).expandX().fillX().pad(PADDING).row();

        Table contentTable = new Table();

        // ستون اول: انتخاب قهرمان و سلاح
        Table leftColumn = new Table();
        leftColumn.add(new Label("SELECT HERO", skin)).padBottom(10).row();
        leftColumn.add(heroSelectionTable).expandX().fillX().height(300).row();
        leftColumn.add(new Label("SELECT WEAPON", skin)).padTop(20).padBottom(10).row();
        leftColumn.add(weaponSelectionTable).expandX().fillX().height(250).row();

        // ستون دوم: پیش‌نمایش
        Table middleColumn = new Table();
        middleColumn.add(previewTable).expand().fill();

        // ستون سوم: انتخاب زمان و دکمه‌ها
        Table rightColumn = new Table();
        rightColumn.add(new Label("SELECT GAME TIME", skin)).padBottom(10).row();
        rightColumn.add(timeSelectionTable).expandX().fillX().height(150).row();

        // افزودن ستون‌ها به جدول محتوا
        contentTable.add(leftColumn).width(300).padRight(20);
        contentTable.add(middleColumn).expand().fill();
        contentTable.add(rightColumn).width(300).padLeft(30);

        mainTable.add(contentTable).expand().fill().row();

        // ایجاد بخش دکمه‌ها
        Table buttonSection = new Table();

        TextButton backButton = new TextButton("BACK", skin);
        styleButton(backButton, new Color(0.7f, 0.3f, 0.3f, 1f));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getMain().setScreen(new MainMenu(
                    new MainMenuController(),
                    GameAssetManager.getGameAssetManager().getSkin()));
            }
        });

        TextButton startButton = new TextButton("START GAME", skin);
        styleButton(startButton, new Color(0.3f, 0.7f, 0.3f, 1f));
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedHero != null && selectedWeapon != null && selectedTime > 0) {
                    startGame();
                } else {
                    showErrorMessage("please select all fields.");
                }
            }
        });

        buttonSection.add(backButton).width(250).padRight(30);
        buttonSection.add(startButton).width(500);

        mainTable.add(buttonSection).colspan(3).padTop(20).row();

        // انتخاب اولین گزینه‌ها به صورت پیش‌فرض
        if (HeroType.values().length > 0) {
            selectHero(HeroType.values()[0]);
        }
        if (WeaponType.values().length > 0) {
            selectWeapon(WeaponType.values()[0]);
        }
        selectedTime = availableTimes.get(0);
        updateTimeLabel();

        stage.addActor(mainTable);

        // افزودن انیمیشن ورودی
        mainTable.getColor().a = 0f;
        mainTable.addAction(Actions.sequence(
            Actions.fadeIn(0.5f, Interpolation.smooth)
        ));

        WeaponType defaultWeapon = controller.getSelectedWeapon();
        HeroType defaultHero = controller.getSelectedHero();
        int defaultTime = controller.getSelectedTime();

        if (defaultHero != null) {
            selectHero(defaultHero);
        } else if (HeroType.values().length > 0) {
            selectHero(HeroType.values()[0]);
        }

        if (defaultWeapon != null) {
            selectWeapon(defaultWeapon);
        } else if (WeaponType.values().length > 0) {
            selectWeapon(WeaponType.values()[0]);
        }

        if (defaultTime > 0) {
            selectedTime = defaultTime;
            updateTimeLabel();
        } else {
            selectedTime = availableTimes.get(0);
            updateTimeLabel();
        }
    }

    private void createHeroSelectionSection() {
        heroSelectionTable = new Table();
//        heroSelectionTable.setBackground(createGradientBackground(
//            new Color(0.15f, 0.15f, 0.25f, 0.9f),
//            new Color(0.1f, 0.1f, 0.2f, 0.9f)
//        ));
        heroSelectionTable.pad(15);

        // ایجاد اسکرول پنل برای لیست قهرمانان
        Table heroListTable = new Table();

        for (final HeroType hero : HeroType.values()) {
            Table buttonContainer = new Table();
            buttonContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
            buttonContainer.pad(5);

            TextButton heroButton = new TextButton(hero.getName(), skin);
            styleHeroButton(heroButton, hero);

            heroButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectHero(hero);
                }
            });

            buttonContainer.add(heroButton).expandX().fillX();
            heroListTable.add(buttonContainer).expandX().fillX().pad(5).padLeft(40).row();
        }

        ScrollPane heroListScrollPane = new ScrollPane(heroListTable, skin);
        heroListScrollPane.setScrollingDisabled(true, false);
        heroListScrollPane.setFadeScrollBars(false);

        heroSelectionTable.add(heroListScrollPane).expand().fill().width(400);
    }

    private void createWeaponSelectionSection() {
        weaponSelectionTable = new Table();
//        weaponSelectionTable.setBackground(createGradientBackground(
//            new Color(0.15f, 0.15f, 0.25f, 0.9f),
//            new Color(0.1f, 0.1f, 0.2f, 0.9f)
//        ));
        weaponSelectionTable.pad(15);

        // ایجاد اسکرول پنل برای لیست سلاح‌ها
        Table weaponListTable = new Table();

        for (final WeaponType weapon : WeaponType.values()) {
            Table buttonContainer = new Table();
            buttonContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
            buttonContainer.pad(5);

            TextButton weaponButton = new TextButton(weapon.getName(), skin);
            styleWeaponButton(weaponButton, weapon);

            weaponButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectWeapon(weapon);
                }
            });

            buttonContainer.add(weaponButton).expandX().fillX();
            weaponListTable.add(buttonContainer).expandX().fillX().pad(5).padLeft(40).row();
        }

        ScrollPane weaponListScrollPane = new ScrollPane(weaponListTable, skin);
        weaponListScrollPane.setScrollingDisabled(true, false);
        weaponListScrollPane.setFadeScrollBars(false);

        weaponSelectionTable.add(weaponListScrollPane).expand().fill().width(400);
    }

    private void createTimeSelectionSection() {
        timeSelectionTable = new Table();
        timeSelectionTable.setBackground(createGradientBackground(
            new Color(0.15f, 0.15f, 0.25f, 0.9f),
            new Color(0.1f, 0.1f, 0.2f, 0.9f)
        ));
        timeSelectionTable.pad(15);

        // ایجاد گزینه‌های انتخاب زمان
        Table timeOptionsTable = new Table();

        for (final Integer time : availableTimes) {
            TextButton timeButton = new TextButton(time + " MINUTE", skin);
            styleTimeButton(timeButton);

            timeButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedTime = time;
                    updateTimeLabel();

                    // به‌روزرسانی رنگ دکمه‌ها
                    for (Actor actor : timeOptionsTable.getChildren()) {
                        if (actor instanceof TextButton) {
                            TextButton button = (TextButton) actor;
                            if (button.getText().toString().startsWith(String.valueOf(selectedTime))) {
                                button.setColor(SELECTED_COLOR);
                            } else {
                                button.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));
                            }
                        }
                    }
                }
            });

            // انتخاب اولین گزینه به صورت پیش‌فرض
            if (time.equals(availableTimes.get(0))) {
                timeButton.setColor(SELECTED_COLOR);
            }

            timeOptionsTable.add(timeButton).pad(5).padRight(20).expandX().fillX().row();
        }

        timeSelectionTable.add(timeOptionsTable).expand().fill().width(400);
    }

    private void createPreviewSection() {
        previewTable = new Table();
        previewTable.setBackground(createGradientBackground(
            new Color(0.15f, 0.15f, 0.25f, 0.9f),
            new Color(0.1f, 0.1f, 0.2f, 0.9f)
        ));
        previewTable.pad(20);

        // عنوان پیش‌نمایش
        Label previewTitle = new Label("PREVIEW", skin);
        previewTitle.setFontScale(1.5f);
        previewTitle.setColor(TITLE_COLOR);
        previewTitle.setAlignment(Align.center);

        // بخش قهرمان
        Table heroPreview = new Table();
        heroPreview.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
        heroPreview.pad(15);

        Label heroSectionTitle = new Label("SELECTED HERO", skin);
        heroSectionTitle.setColor(SELECTED_COLOR);

        heroImage = new Image();
        heroImage.setSize(300, 300);

        heroNameLabel = new Label("", skin);
        heroNameLabel.setFontScale(1.3f);
        heroNameLabel.setColor(TEXT_COLOR);

        heroDescLabel = new Label("", skin);
        heroDescLabel.setWrap(true);
        heroDescLabel.setAlignment(Align.left);
        heroDescLabel.setColor(TEXT_COLOR);

        heroPreview.add(heroSectionTitle).colspan(2).padBottom(10).row();
        heroPreview.add(heroImage).size(300).padRight(15).padLeft(200);
        Table heroInfoTable = new Table();
        heroInfoTable.add(heroNameLabel).left().padBottom(10).row();
        heroInfoTable.add(heroDescLabel).width(350).left();
        heroPreview.add(heroInfoTable).expand().fill().row();

        // بخش سلاح
        Table weaponPreview = new Table();
        weaponPreview.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
        weaponPreview.pad(15);

        Label weaponSectionTitle = new Label("SELECTED WEAPON", skin);
        weaponSectionTitle.setColor(SELECTED_COLOR);

        weaponImage = new Image();
        weaponImage.setSize(150, 150);

        weaponNameLabel = new Label("", skin);
        weaponNameLabel.setFontScale(1.3f);
        weaponNameLabel.setColor(TEXT_COLOR);

        weaponDescLabel = new Label("", skin);
        weaponDescLabel.setWrap(true);
        weaponDescLabel.setAlignment(Align.left);
        weaponDescLabel.setColor(TEXT_COLOR);

        weaponPreview.add(weaponSectionTitle).colspan(2).padBottom(10).row();
        weaponPreview.add(weaponImage).size(150).padRight(15);
        Table weaponInfoTable = new Table();
        weaponInfoTable.add(weaponNameLabel).left().padBottom(10).row();
        weaponInfoTable.add(weaponDescLabel).width(300).left();
        weaponPreview.add(weaponInfoTable).expand().fill().row();

        // بخش زمان
        Table timePreview = new Table();
        timePreview.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
        timePreview.pad(15);

        Label timeSectionTitle = new Label("GAME TIME", skin);
        timeSectionTitle.setColor(SELECTED_COLOR);

        timeLabel = new Label("", skin);
        timeLabel.setFontScale(1.5f);
        timeLabel.setColor(TEXT_COLOR);

        timePreview.add(timeSectionTitle).padBottom(10).row();
        timePreview.add(timeLabel);

        // افزودن همه بخش‌ها به جدول پیش‌نمایش
        previewTable.add(previewTitle).colspan(2).expandX().fillX().padBottom(20).row();
        previewTable.add(heroPreview).expand().fill().padRight(10);

        Table rightPreviewColumn = new Table();
        rightPreviewColumn.add(weaponPreview).expand().fill().row();
        rightPreviewColumn.add(timePreview).expandX().fillX().height(100).padTop(10).row();

        previewTable.add(rightPreviewColumn).expand().fill();

        // افزودن انیمیشن‌های مختلف به اجزای پیش‌نمایش
        addPulseEffect(heroImage);
        addPulseEffect(weaponImage);
        addFloatEffect(timeLabel);
    }

    private void createButtonsSection() {
        // این بخش در متد show() پیاده‌سازی شده است
    }

    private void selectHero(HeroType hero) {
        this.selectedHero = hero;

        // به‌روزرسانی تصویر قهرمان با انیمیشن
        if (hero.getTextureRegion() != null) {
            heroImage.setDrawable(new TextureRegionDrawable(hero.getTextureRegion()));
        } else {
            // تنظیم تصویر پیش‌فرض اگر تکسچر موجود نیست
            heroImage.setDrawable(createDefaultImage());
        }

        // اعمال انیمیشن بزرگنمایی به تصویر
        heroImage.addAction(Actions.sequence(
            Actions.scaleTo(0.9f, 0.9f, 0.1f),
            Actions.scaleTo(1.1f, 1.1f, 0.2f),
            Actions.scaleTo(1.0f, 1.0f, 0.1f)
        ));

        // به‌روزرسانی اطلاعات قهرمان با انیمیشن
        heroNameLabel.setText(hero.getName());
        heroDescLabel.setText(hero.getDescription());

        // افزودن افکت فید-این به کل جدول جزئیات
        heroImage.getParent().addAction(Actions.sequence(
            Actions.alpha(0.5f),
            Actions.alpha(1f, 0.4f)
        ));

        // به‌روزرسانی رنگ دکمه‌ها
        updateHeroButtonsColors(hero);
        controller.setSelectedHero(hero);
    }

    private void selectWeapon(WeaponType weapon) {
        this.selectedWeapon = weapon;

        // به‌روزرسانی تصویر سلاح با انیمیشن
        if (weapon.getTextureRegion() != null) {
            weaponImage.setDrawable(new TextureRegionDrawable(weapon.getTextureRegion()));
        } else {
            // تنظیم تصویر پیش‌فرض اگر تکسچر موجود نیست
            weaponImage.setDrawable(createDefaultImage());
        }

        // اعمال انیمیشن بزرگنمایی به تصویر
        weaponImage.addAction(Actions.sequence(
            Actions.scaleTo(0.9f, 0.9f, 0.1f),
            Actions.scaleTo(1.1f, 1.1f, 0.2f),
            Actions.scaleTo(1.0f, 1.0f, 0.1f)
        ));

        // به‌روزرسانی اطلاعات سلاح با انیمیشن
        weaponNameLabel.setText(weapon.getName());
        weaponDescLabel.setText(
            weapon.getEnglishDescription() + "\n\n" +
                "|DAMAGE: " + weapon.getDamage() +
                " |PROJECTILE COUNT: " + weapon.getProjectileCount() +
                " |RELOAD TIME: " + weapon.getReloadTime() + "s" +
                " |MAX AMMO: " + weapon.getMaxAmmo()
        );

        weaponImage.getParent().addAction(Actions.sequence(
            Actions.alpha(0.5f),
            Actions.alpha(1f, 0.4f)
        ));

        updateWeaponButtonsColors(weapon);
        controller.setSelectedWeapon(weapon);
    }

    private void updateTimeLabel() {
        if (timeLabel != null) {
            timeLabel.setText(selectedTime + "MINUTE");

            // افزودن انیمیشن به برچسب زمان
            timeLabel.addAction(Actions.sequence(
                Actions.scaleTo(1.2f, 1.2f, 0.2f),
                Actions.scaleTo(1.0f, 1.0f, 0.2f)
            ));
        }
        controller.setSelectedTime(selectedTime);
    }

    private void updateHeroButtonsColors(HeroType selectedHero) {
        for (Actor actor : heroSelectionTable.getChildren()) {
            if (actor instanceof ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) actor;
                Table content = (Table) scrollPane.getActor();

                for (Actor containerActor : content.getChildren()) {
                    if (containerActor instanceof Table) {
                        Table container = (Table) containerActor;
                        for (Actor buttonActor : container.getChildren()) {
                            if (buttonActor instanceof TextButton) {
                                TextButton button = (TextButton) buttonActor;
                                if (button.getText().toString().equals(selectedHero.getName())) {
                                    button.setColor(SELECTED_COLOR);
                                } else {
                                    button.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateWeaponButtonsColors(WeaponType selectedWeapon) {
        for (Actor actor : weaponSelectionTable.getChildren()) {
            if (actor instanceof ScrollPane) {
                ScrollPane scrollPane = (ScrollPane) actor;
                Table content = (Table) scrollPane.getActor();

                for (Actor containerActor : content.getChildren()) {
                    if (containerActor instanceof Table) {
                        Table container = (Table) containerActor;
                        for (Actor buttonActor : container.getChildren()) {
                            if (buttonActor instanceof TextButton) {
                                TextButton button = (TextButton) buttonActor;
                                if (button.getText().toString().equals(selectedWeapon.getName())) {
                                    button.setColor(SELECTED_COLOR);
                                } else {
                                    button.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void styleHeroButton(TextButton button, final HeroType hero) {
        button.pad(12);
        button.getLabel().setFontScale(1.0f);
        button.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));

        // افزودن افکت‌های هاور و کلیک
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.sequence(
                    Actions.color(SELECTED_COLOR, 0.2f),
                    Actions.scaleTo(1.05f, 1.05f, 0.1f)
                ));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (selectedHero != hero) {
                    button.addAction(Actions.parallel(
                        Actions.color(new Color(0.6f, 0.6f, 0.7f, 1f), 0.2f),
                        Actions.scaleTo(1f, 1f, 0.1f)
                    ));
                } else {
                    button.addAction(Actions.scaleTo(1f, 1f, 0.1f));
                }
            }
        });
    }

    private void styleWeaponButton(TextButton button, final WeaponType weapon) {
        button.pad(12);
        button.getLabel().setFontScale(1.0f);
        button.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));

        // افزودن افکت‌های هاور و کلیک
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.sequence(
                    Actions.color(SELECTED_COLOR, 0.2f),
                    Actions.scaleTo(1.05f, 1.05f, 0.1f)
                ));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (selectedWeapon != weapon) {
                    button.addAction(Actions.parallel(
                        Actions.color(new Color(0.6f, 0.6f, 0.7f, 1f), 0.2f),
                        Actions.scaleTo(1f, 1f, 0.1f)
                    ));
                } else {
                    button.addAction(Actions.scaleTo(1f, 1f, 0.1f));
                }
            }
        });
    }

    private void styleTimeButton(TextButton button) {
        button.pad(12);
        button.getLabel().setFontScale(1.1f);
        button.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));

        // افزودن افکت‌های هاور
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!button.getColor().equals(SELECTED_COLOR)) {
                    button.addAction(Actions.sequence(
                        Actions.color(new Color(0.7f, 0.7f, 0.8f, 1f), 0.2f),
                        Actions.scaleTo(1.05f, 1.05f, 0.1f)
                    ));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!button.getColor().equals(SELECTED_COLOR)) {
                    button.addAction(Actions.parallel(
                        Actions.color(new Color(0.6f, 0.6f, 0.7f, 1f), 0.2f),
                        Actions.scaleTo(1f, 1f, 0.1f)
                    ));
                } else {
                    button.addAction(Actions.scaleTo(1f, 1f, 0.1f));
                }
            }
        });
    }

    private void styleButton(TextButton button, Color baseColor) {
        button.pad(12);
        button.getLabel().setFontScale(1.3f);
        button.setColor(baseColor);

        // افزودن افکت‌های هاور و کلیک
        final Color originalColor = new Color(baseColor);

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.sequence(
                    Actions.color(new Color(originalColor).lerp(Color.WHITE, 0.3f), 0.2f),
                    Actions.scaleTo(1.05f, 1.05f, 0.1f)
                ));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.addAction(Actions.parallel(
                    Actions.color(originalColor, 0.2f),
                    Actions.scaleTo(1f, 1f, 0.1f)
                ));
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                button.addAction(Actions.sequence(
                    Actions.color(new Color(originalColor).lerp(Color.BLACK, 0.2f), 0.1f),
                    Actions.color(originalColor, 0.1f)
                ));
            }
        });
    }

    private void addPulseEffect(Actor actor) {
        actor.addAction(Actions.forever(Actions.sequence(
            Actions.scaleTo(1.05f, 1.05f, 1.0f, Interpolation.smooth),
            Actions.scaleTo(0.95f, 0.95f, 1.0f, Interpolation.smooth)
        )));
    }

    private void addFloatEffect(Actor actor) {
        actor.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0, 5, 1.0f, Interpolation.smooth),
            Actions.moveBy(0, -5, 1.0f, Interpolation.smooth)
        )));
    }

    private void startGame() {
        // انیمیشن خروج قبل از شروع بازی
        mainTable.addAction(Actions.sequence(
            Actions.fadeOut(0.5f, Interpolation.smooth),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    // ایجاد بازی جدید با تنظیمات انتخاب شده
                    GameController gameController = new GameController(selectedHero, selectedWeapon, selectedTime);
                    Main.getMain().setScreen(gameController.getGameScreen());
                }
            })
        ));
        controller.startGame();
    }

    private void showErrorMessage(String message) {
        Dialog dialog = new Dialog("FAIL", skin);
        dialog.text(message);
        dialog.button("OK").padBottom(10);

        // افزودن افکت به دیالوگ
        dialog.show(stage);
        dialog.setPosition(
            (stage.getWidth() - dialog.getWidth()) / 2,
            (stage.getHeight() - dialog.getHeight()) / 2
        );

        // انیمیشن ظاهر شدن دیالوگ
        dialog.getColor().a = 0f;
        dialog.addAction(Actions.fadeIn(0.3f, Interpolation.smooth));
    }

    private TextureRegionDrawable createBackground(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        return drawable;
    }

    private TextureRegionDrawable createGradientBackground(Color topColor, Color bottomColor) {
        Pixmap pixmap = new Pixmap(1, 100, Pixmap.Format.RGBA8888);

        for (int y = 0; y < 100; y++) {
            float ratio = (float)y / 99f;
            Color blendedColor = new Color(
                topColor.r * (1 - ratio) + bottomColor.r * ratio,
                topColor.g * (1 - ratio) + bottomColor.g * ratio,
                topColor.b * (1 - ratio) + bottomColor.b * ratio,
                topColor.a * (1 - ratio) + bottomColor.a * ratio
            );
            pixmap.setColor(blendedColor);
            pixmap.drawLine(0, y, 0, y);
        }

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        return drawable;
    }

    private TextureRegionDrawable createPanelBackground(Color color) {
        Pixmap pixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        // افزودن حاشیه ظریف
        pixmap.setColor(new Color(color.r + 0.1f, color.g + 0.1f, color.b + 0.1f, color.a));
        for (int i = 0; i < 2; i++) {
            pixmap.drawRectangle(i, i, pixmap.getWidth() - i*2, pixmap.getHeight() - i*2);
        }

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        return drawable;
    }

    private TextureRegionDrawable createDefaultImage() {
        Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.3f, 0.3f, 0.4f, 1f));
        pixmap.fill();

        // افزودن یک الگوی جالب برای تصویر پیش‌فرض
        pixmap.setColor(new Color(0.4f, 0.4f, 0.5f, 1f));
        for (int i = 0; i < 100; i += 10) {
            pixmap.drawLine(0, i, i, 0);
            pixmap.drawLine(100-i, 0, 100, i);
            pixmap.drawLine(0, 100-i, i, 100);
            pixmap.drawLine(100-i, 100, 100, 100-i);
        }

        pixmap.setColor(Color.WHITE);
        pixmap.drawRectangle(0, 0, 99, 99);
        pixmap.drawLine(0, 0, 99, 99);
        pixmap.drawLine(99, 0, 0, 99);

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        return drawable;
    }

    @Override
    public void render(float delta) {
        // افزودن تپش رنگ ملایم به پس‌زمینه
        float t = (System.currentTimeMillis() % 10000) / 10000f;
        float r = 0.05f + 0.02f * (float)Math.sin(t * Math.PI * 2);
        float g = 0.05f + 0.01f * (float)Math.sin((t + 0.33f) * Math.PI * 2);
        float b = 0.1f + 0.03f * (float)Math.sin((t + 0.66f) * Math.PI * 2);

        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
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

    public Stage getStage() {
        return stage;
    }
}
