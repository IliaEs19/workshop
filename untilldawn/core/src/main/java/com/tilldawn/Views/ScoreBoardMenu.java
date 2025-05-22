package com.tilldawn.Views;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreBoardMenu implements Screen{
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;

    private final MainMenuController controller;
    private final Stage stage;
    private final Skin skin;
    private final SpriteBatch batch;
    private final SaveData saveData;
    private final String currentUsername;

    // تکسچرها و استایل‌های گرافیکی
    private Texture backgroundTexture;
    private Texture ribbonTexture;
    private Texture goldMedalTexture;
    private Texture silverMedalTexture;
    private Texture bronzeMedalTexture;
    private Texture filterPanelTexture;
    private Texture buttonTexture;
    private Texture buttonHoverTexture;
    private Texture tableHeaderTexture;
    private Texture tableRowTexture;
    private Texture tableHighlightTexture;

    // فونت‌ها
    private BitmapFont titleFont;
    private BitmapFont headerFont;
    private BitmapFont regularFont;

    // انواع مرتب‌سازی
    private enum SortType {
        SCORE, USERNAME, KILLS, SURVIVAL_TIME
    }

    private SortType currentSortType = SortType.SCORE;
    private boolean sortAscending = false;

    // جدول امتیازات
    private Table scoreboardTable;
    private ScrollPane scrollPane;

    public ScoreBoardMenu(MainMenuController controller) {
        this.controller = controller;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        this.skin = GameAssetManager.getGameAssetManager().getSkin();
        this.saveData = SaveData.getInstance();

        // دریافت نام کاربری فعلی
        User currentUser = SaveData.getCurrentUser();
        this.currentUsername = (currentUser != null) ? currentUser.getUserName() : "";

        // بارگذاری منابع گرافیکی
        loadAssets();

        // ایجاد رابط کاربری
        createUI();

        // تنظیم ورودی
        Gdx.input.setInputProcessor(stage);
    }

    private void loadAssets() {
        // ایجاد تکسچرهای رنگی به جای بارگذاری فایل‌های خارجی
        backgroundTexture = createColorTexture(0.15f, 0.15f, 0.2f, 1.0f);
        ribbonTexture = createColorTexture(0.8f, 0.6f, 0.2f, 1.0f);

        // ایجاد آیکون‌های مدال
        goldMedalTexture = createMedalTexture(new Color(1.0f, 0.8f, 0.0f, 1.0f), 32);
        silverMedalTexture = createMedalTexture(new Color(0.8f, 0.8f, 0.8f, 1.0f), 32);
        bronzeMedalTexture = createMedalTexture(new Color(0.8f, 0.5f, 0.2f, 1.0f), 32);

        filterPanelTexture = createColorTexture(0.2f, 0.2f, 0.3f, 0.8f);
        buttonTexture = createColorTexture(0.3f, 0.3f, 0.5f, 1.0f);
        buttonHoverTexture = createColorTexture(0.4f, 0.4f, 0.6f, 1.0f);
        tableHeaderTexture = createColorTexture(0.25f, 0.25f, 0.35f, 1.0f);
        tableRowTexture = createColorTexture(0.2f, 0.2f, 0.25f, 0.9f);
        tableHighlightTexture = createColorTexture(0.3f, 0.5f, 0.7f, 0.7f);

        // تنظیم فونت‌ها
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);
        titleFont.setColor(Color.GOLD);

        headerFont = new BitmapFont();
        headerFont.getData().setScale(1.8f);
        headerFont.setColor(Color.WHITE);

        regularFont = new BitmapFont();
        regularFont.getData().setScale(1.5f);
        regularFont.setColor(Color.WHITE);
    }

    private Texture createMedalTexture(Color color, int size) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(size, size, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // رنگ پس‌زمینه مدال
        pixmap.setColor(color);
        pixmap.fillCircle(size/2, size/2, size/2);

        // حاشیه مدال
        pixmap.setColor(new Color(1, 1, 1, 0.8f));
        pixmap.drawCircle(size/2, size/2, size/2);

        // طرح داخل مدال
        pixmap.setColor(new Color(1, 1, 1, 0.5f));
        pixmap.fillCircle(size/2, size/2, size/4);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void createUI() {
        // ساختار اصلی
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        // عنوان
        Label titleLabel = new Label("SCOREBOARD", new Label.LabelStyle(titleFont, Color.GOLD));
        titleLabel.setAlignment(Align.center);

        // پنل فیلتر
        Table filterTable = createFilterPanel();

        // جدول امتیازات
        scoreboardTable = new Table();
        updateScoreboardTable();

        // اسکرول پنل برای جدول
        scrollPane = new ScrollPane(scoreboardTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        // دکمه بازگشت
        TextButton backButton = createStyledButton("Back to Main Menu");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getMain().setScreen(new MainMenu(controller,GameAssetManager.getGameAssetManager().getSkin()));
            }
        });

        // چینش عناصر در جدول اصلی
        mainTable.add(titleLabel).colspan(2).pad(20).row();
        mainTable.add(filterTable).colspan(2).pad(10).fillX().row();
        mainTable.add(scrollPane).colspan(2).expand().fill().pad(10).row();
        mainTable.add(backButton).colspan(2).pad(10).width(240).height(50);

        // اضافه کردن جدول اصلی به استیج
        stage.addActor(mainTable);
    }

    private Table createFilterPanel() {
        Table filterTable = new Table();
        if (filterPanelTexture != null) {
            filterTable.setBackground(new TextureRegionDrawable(new TextureRegion(filterPanelTexture)));
        }
        filterTable.pad(10);

        // عنوان فیلتر
        Label filterLabel = new Label("Sort By:", new Label.LabelStyle(headerFont, Color.WHITE));

        // دکمه‌های مرتب‌سازی
        TextButton scoreButton = createFilterButton("Score", SortType.SCORE);
        TextButton usernameButton = createFilterButton("Username", SortType.USERNAME);
        TextButton killsButton = createFilterButton("Kills", SortType.KILLS);
        TextButton survivalButton = createFilterButton("Survival Time", SortType.SURVIVAL_TIME);

        // چینش عناصر در پنل فیلتر
        filterTable.add(filterLabel).padRight(20);
        filterTable.add(scoreButton).padRight(10).width(120).height(40);
        filterTable.add(usernameButton).padRight(10).width(120).height(40);
        filterTable.add(killsButton).padRight(10).width(120).height(40);
        filterTable.add(survivalButton).width(120).height(40);

        return filterTable;
    }

    private TextButton createFilterButton(String text, final SortType sortType) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = regularFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(buttonHoverTexture));

        final TextButton button = new TextButton(text, buttonStyle);

        // اگر این دکمه نوع مرتب‌سازی فعلی است، آن را هایلایت کن
        if (currentSortType == sortType) {
            button.setText(text + (sortAscending ? " ↑" : " ↓"));
        }

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // اگر همان نوع مرتب‌سازی انتخاب شده، جهت مرتب‌سازی را عوض کن
                if (currentSortType == sortType) {
                    sortAscending = !sortAscending;
                } else {
                    currentSortType = sortType;
                    sortAscending = false;
                }

                // بروزرسانی متن دکمه
                button.setText(text + (sortAscending ? " ↑" : " ↓"));

                // بروزرسانی جدول امتیازات
                updateScoreboardTable();
            }
        });

        return button;
    }

    private TextButton createStyledButton(String text) {
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = headerFont;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(buttonHoverTexture));

        return new TextButton(text, buttonStyle);
    }

    private void updateScoreboardTable() {
        scoreboardTable.clear();

        // سبک هدر جدول
//        Table.TableStyle headerStyle = new Table.TableStyle();
//        if (tableHeaderTexture != null) {
//            headerStyle.background = new TextureRegionDrawable(new TextureRegion(tableHeaderTexture));
//        }

        // ایجاد هدر جدول
        Table headerTable = new Table();
        if (tableHeaderTexture != null) {
            headerTable.setBackground(new TextureRegionDrawable(new TextureRegion(tableHeaderTexture)));
        }
        headerTable.pad(10);
        headerTable.add(new Label("Rank", new Label.LabelStyle(headerFont, Color.WHITE))).width(60).padRight(10);
        headerTable.add(new Label("Username", new Label.LabelStyle(headerFont, Color.WHITE))).width(150).padRight(10);
        headerTable.add(new Label("Score", new Label.LabelStyle(headerFont, Color.WHITE))).width(100).padRight(10);
        headerTable.add(new Label("Kills", new Label.LabelStyle(headerFont, Color.WHITE))).width(100).padRight(10);
        headerTable.add(new Label("Survival Time", new Label.LabelStyle(headerFont, Color.WHITE))).width(150);

        scoreboardTable.add(headerTable).fillX().row();

        // دریافت و مرتب‌سازی کاربران
        List<User> users = getSortedUsers();

        // محدود کردن به 10 کاربر برتر
        int userCount = Math.min(users.size(), 10);

        // نمایش کاربران
        for (int i = 0; i < userCount; i++) {
            User user = users.get(i);

            // ایجاد ردیف جدول
            Table rowTable = new Table();
            rowTable.pad(8);

            // تعیین استایل ردیف
            if (user.getUserName().equals(currentUsername)) {
                // هایلایت کردن کاربر فعلی
                if (tableHighlightTexture != null) {
                    rowTable.setBackground(new TextureRegionDrawable(new TextureRegion(tableHighlightTexture)));
                } else {
                    // اگر تکسچر هایلایت موجود نباشد، از رنگ استفاده می‌کنیم
                    rowTable.setBackground(new TextureRegionDrawable(createColorTexture(0.3f, 0.5f, 0.8f, 0.7f)));
                }
            } else {
                // استایل عادی برای سایر کاربران
                if (tableRowTexture != null) {
                    rowTable.setBackground(new TextureRegionDrawable(new TextureRegion(tableRowTexture)));
                } else {
                    // اگر تکسچر ردیف موجود نباشد، از رنگ استفاده می‌کنیم
                    rowTable.setBackground(new TextureRegionDrawable(createColorTexture(0.2f, 0.2f, 0.2f, 0.7f)));
                }
            }

            // ایجاد سلول رتبه با آیکون مدال (در صورت نیاز)
            Table rankCell = new Table();

            // نمایش متفاوت برای 3 نفر برتر
            if (i == 0 && goldMedalTexture != null) {
                Image medalImage = new Image(new TextureRegion(goldMedalTexture));
                rankCell.add(medalImage).size(30, 30).padRight(5);
            } else if (i == 1 && silverMedalTexture != null) {
                Image medalImage = new Image(new TextureRegion(silverMedalTexture));
                rankCell.add(medalImage).size(30, 30).padRight(5);
            } else if (i == 2 && bronzeMedalTexture != null) {
                Image medalImage = new Image(new TextureRegion(bronzeMedalTexture));
                rankCell.add(medalImage).size(30, 30).padRight(5);
            }

            // افزودن شماره رتبه
            Label rankLabel = new Label(String.valueOf(i + 1), new Label.LabelStyle(regularFont, Color.WHITE));
            rankCell.add(rankLabel);

            // تبدیل زمان به فرمت دقیقه:ثانیه
            int minutes = (int)(user.getLongestSurvivalTime() / 60);
            int seconds = (int)(user.getLongestSurvivalTime() % 60);
            String survivalTimeStr = String.format("%02d:%02d", minutes, seconds);

            // افزودن سلول‌ها به ردیف
            rowTable.add(rankCell).width(60).padRight(10);
            rowTable.add(new Label(user.getUserName(), new Label.LabelStyle(regularFont, Color.WHITE))).width(150).padRight(10);
            rowTable.add(new Label(String.valueOf(user.getHighScore()), new Label.LabelStyle(regularFont, Color.WHITE))).width(100).padRight(10);
            rowTable.add(new Label(String.valueOf(user.getTotalKills()), new Label.LabelStyle(regularFont, Color.WHITE))).width(100).padRight(10);
            rowTable.add(new Label(survivalTimeStr, new Label.LabelStyle(regularFont, Color.WHITE))).width(150);

            // افزودن ردیف به جدول
            scoreboardTable.add(rowTable).fillX().row();
        }
    }

    // متد کمکی برای ایجاد تکسچر رنگی
    private Texture createColorTexture(float r, float g, float b, float a) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private List<User> getSortedUsers() {
        List<User> users = saveData.getAllUsers();

        // مرتب‌سازی کاربران بر اساس معیار انتخاب شده
        switch (currentSortType) {
            case SCORE:
                if (sortAscending) {
                    users.sort(Comparator.comparingInt(User::getHighScore));
                } else {
                    users.sort(Comparator.comparingInt(User::getHighScore).reversed());
                }
                break;

            case USERNAME:
                if (sortAscending) {
                    users.sort(Comparator.comparing(User::getUserName));
                } else {
                    users.sort(Comparator.comparing(User::getUserName).reversed());
                }
                break;

            case KILLS:
                if (sortAscending) {
                    users.sort(Comparator.comparingInt(User::getTotalKills));
                } else {
                    users.sort(Comparator.comparingInt(User::getTotalKills).reversed());
                }
                break;

            case SURVIVAL_TIME:
                if (sortAscending) {
                    users.sort(Comparator.comparingDouble(User::getLongestSurvivalTime));
                } else {
                    users.sort(Comparator.comparingDouble(User::getLongestSurvivalTime).reversed());
                }
                break;
        }

        return users;
    }

    @Override
    public void render(float delta) {
        // پاک کردن صفحه
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // رسم پس‌زمینه
        batch.begin();
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        }
        batch.end();

        // رسم رابط کاربری
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();

        // آزادسازی منابع گرافیکی
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (ribbonTexture != null) ribbonTexture.dispose();
        if (goldMedalTexture != null) goldMedalTexture.dispose();
        if (silverMedalTexture != null) silverMedalTexture.dispose();
        if (bronzeMedalTexture != null) bronzeMedalTexture.dispose();
        if (filterPanelTexture != null) filterPanelTexture.dispose();
        if (buttonTexture != null) buttonTexture.dispose();
        if (buttonHoverTexture != null) buttonHoverTexture.dispose();
        if (tableHeaderTexture != null) tableHeaderTexture.dispose();
        if (tableRowTexture != null) tableRowTexture.dispose();
        if (tableHighlightTexture != null) tableHighlightTexture.dispose();

        // آزادسازی فونت‌ها
        if (titleFont != null) titleFont.dispose();
        if (headerFont != null) headerFont.dispose();
        if (regularFont != null) regularFont.dispose();
    }
}
