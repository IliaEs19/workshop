package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.AvatarSelectionController;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.ButtonClickListener;
import com.tilldawn.Models.GameAssetManager;
//import com.tilldawn.Models.SoundManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;

public class AvatarSelectionScreen implements Screen {
    private Stage stage;
    private Table mainTable;
    private final AvatarSelectionController controller;
    private final User user; // کاربر ثبت‌نام شده

    // آواتارها
    private Image[] avatarImages;
    private Image selectedAvatarImage;
    private Table avatarContainer; // ظرف نگهدارنده آواتار برای تنظیم موقعیت صحیح
    private Image avatarFrame; // قاب آواتار
    private int selectedAvatarIndex = -1; // شاخص آواتار انتخاب شده
    private boolean selectionComplete = false; // آیا انتخاب آواتار کامل شده است؟

    // دکمه‌ها
    private TextButton continueButton;
    private TextButton spinAgainButton;

    // تنظیمات انیمیشن
    private static final float SPIN_DURATION = 1.5f; // مدت زمان چرخش (ثانیه)
    private static final float DISPLAY_DURATION = 0.1f; // مدت زمان نمایش هر آواتار (ثانیه)
    private static final int SPIN_CYCLES = 2; // تعداد چرخه‌های کامل

    // مسیر آواتارها
    private static final String[] AVATAR_PATHS = {
        "avatars/character1.jpg",
        "avatars/character2.jpg",
        "avatars/character3.jpg",
        "avatars/character4.jpg"
    };

    // اندازه‌ها
    private static final float AVATAR_SIZE = 450; // اندازه آواتار
    private static final float FRAME_PADDING = 30; // فاصله قاب از آواتار

    public AvatarSelectionScreen(AvatarSelectionController controller, Skin skin, User user) {
        this.controller = controller;
        this.user = user;
        controller.setView(this);

        // بارگذاری آواتارها
        loadAvatars();
    }

    private void loadAvatars() {
        avatarImages = new Image[AVATAR_PATHS.length];
        for (int i = 0; i < AVATAR_PATHS.length; i++) {
            try {
                Texture texture = new Texture(Gdx.files.internal(AVATAR_PATHS[i]));
                avatarImages[i] = new Image(texture);
                avatarImages[i].setSize(AVATAR_SIZE, AVATAR_SIZE);
            } catch (Exception e) {
                Gdx.app.error("AvatarSelectionScreen", "Error loading avatar: " + AVATAR_PATHS[i], e);

                // اگر بارگذاری تصویر با خطا مواجه شد، یک تصویر خالی ایجاد کنید
                Pixmap pixmap = new Pixmap((int)AVATAR_SIZE, (int)AVATAR_SIZE, Pixmap.Format.RGBA8888);
                pixmap.setColor(new Color(MathUtils.random(), MathUtils.random(), MathUtils.random(), 1));
                pixmap.fill();
                Texture texture = new Texture(pixmap);
                pixmap.dispose();
                avatarImages[i] = new Image(texture);
                avatarImages[i].setSize(AVATAR_SIZE, AVATAR_SIZE);
            }
        }
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // ایجاد جدول اصلی
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // ایجاد پس‌زمینه
        Table backgroundTable = new Table();
        //backgroundTable.setBackground(createPanelBackground(new Color(0.05f, 0.05f, 0.1f, 0.9f)));
        backgroundTable.setFillParent(true);
        stage.addActor(backgroundTable);

        // ایجاد پنل اصلی
        Table contentPanel = new Table();
        //contentPanel.setBackground(createPanelBackground(new Color(0.1f, 0.1f, 0.2f, 0.9f)));
        contentPanel.pad(30);

        // عنوان
        Label titleLabel = new Label("AVATAR SELECTION", GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setFontScale(1.8f);
        titleLabel.setColor(Color.GOLD);
        titleLabel.setAlignment(Align.center);
        contentPanel.add(titleLabel).colspan(2).padBottom(30).expandX().fillX().row();

        // توضیحات
        Label descLabel = new Label("Congratulations " + user.getUserName() + "! Your random avatar is being selected...",
            GameAssetManager.getGameAssetManager().getSkin());
        descLabel.setFontScale(1.2f);
        descLabel.setColor(Color.CYAN);
        descLabel.setWrap(true);
        contentPanel.add(descLabel).colspan(2).padBottom(40).width(600).row();

        // ایجاد ظرف نگهدارنده آواتار
        avatarContainer = new Table();

        // ایجاد قاب آواتار
        avatarFrame = createAvatarFrame(AVATAR_SIZE + FRAME_PADDING * 2);

        // اضافه کردن قاب آواتار به ظرف نگهدارنده
        avatarContainer.add(avatarFrame).size(AVATAR_SIZE + FRAME_PADDING * 2);

        // پنل نگهدارنده آواتار و قاب
        Table avatarPanel = new Table();
        avatarPanel.setBackground(createPanelBackground(new Color(0.15f, 0.15f, 0.25f, 0.8f)));
        avatarPanel.pad(20);

        // اضافه کردن ظرف نگهدارنده آواتار به پنل
        avatarPanel.add(avatarContainer).size(AVATAR_SIZE + FRAME_PADDING * 2).pad(10);

        // اضافه کردن پنل آواتار به پنل اصلی
        contentPanel.add(avatarPanel).colspan(2).padBottom(30).row();

        // ایجاد دکمه‌ها
        continueButton = new TextButton("CONTINUE", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(continueButton, new Color(0.2f, 0.8f, 0.2f, 1f), new Color(0.3f, 0.9f, 0.3f, 1f));
        continueButton.setVisible(false); // در ابتدا مخفی است

        spinAgainButton = new TextButton("SPIN AGAIN", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(spinAgainButton, new Color(0.2f, 0.6f, 0.9f, 1f), new Color(0.3f, 0.7f, 1f, 1f));
        spinAgainButton.setVisible(false); // در ابتدا مخفی است

        // اضافه کردن دکمه‌ها به پنل اصلی
        Table buttonTable = new Table();
        buttonTable.add(spinAgainButton).width(500).height(120).padRight(20);
        buttonTable.add(continueButton).width(500).height(120);
        contentPanel.add(buttonTable).colspan(2).padTop(20).row();

        // اضافه کردن پنل اصلی به جدول اصلی
        mainTable.add(contentPanel).width(700).height(600);

        // اضافه کردن لیسنرها به دکمه‌ها
        continueButton.addListener(new ButtonClickListener(new Runnable() {
            @Override
            public void run() {
                if (selectionComplete && selectedAvatarIndex >= 0) {
                    // ذخیره آواتار انتخاب شده برای کاربر
                    controller.saveUserAvatar(user, selectedAvatarIndex);

                    // انتقال به صفحه بعدی (مثلاً منوی اصلی یا پروفایل)
                    Main.getMain().setScreen(new MainMenu(new MainMenuController(),
                        GameAssetManager.getGameAssetManager().getSkin()));
                }
            }
        }));

        spinAgainButton.addListener(new ButtonClickListener(new Runnable() {
            @Override
            public void run() {
                if (selectionComplete) {
                    // شروع مجدد انیمیشن چرخش
                    selectionComplete = false;
                    startSpinningAnimation();
                }
            }
        }));

        // شروع انیمیشن چرخش
        startSpinningAnimation();
    }

    private void startSpinningAnimation() {
        // پنهان کردن دکمه‌ها
        continueButton.setVisible(false);
        spinAgainButton.setVisible(false);

        // حذف آواتار قبلی (اگر وجود داشته باشد)
        if (selectedAvatarImage != null) {
            selectedAvatarImage.remove();
            selectedAvatarImage = null;
        }

        // ایجاد توالی اکشن‌ها برای نمایش آواتارهای مختلف
        SequenceAction sequenceAction = new SequenceAction();

        // اضافه کردن اکشن‌های نمایش هر آواتار
        float delay = 0;

        // فاز اول: چرخش سریع
        for (int cycle = 0; cycle < SPIN_CYCLES; cycle++) {
            for (int i = 0; i < avatarImages.length; i++) {
                final int avatarIndex = i;
                sequenceAction.addAction(Actions.delay(delay));
                sequenceAction.addAction(Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        showAvatar(avatarIndex);
                        // پخش صدای تغییر آواتار
                        //SoundManager.getInstance().playButtonClick();
                    }
                }));

                // افزایش تأخیر برای هر آواتار (سرعت چرخش با گذشت زمان کاهش می‌یابد)
                delay += DISPLAY_DURATION * (1 + cycle * 0.1f);
            }
        }

        // فاز دوم: کند شدن تدریجی و توقف روی یک آواتار تصادفی
        final int randomIndex = MathUtils.random(0, avatarImages.length - 1);
        for (int i = 0; i < 5; i++) { // چند بار دیگر بچرخد و کند شود
            int index = (randomIndex + i) % avatarImages.length;
            final int avatarIndex = index;
            sequenceAction.addAction(Actions.delay(delay));
            sequenceAction.addAction(Actions.run(new Runnable() {
                @Override
                public void run() {
                    showAvatar(avatarIndex);
                    //SoundManager.getInstance().playButtonClick();
                }
            }));
            delay += DISPLAY_DURATION * (1 + i * 0.5f); // کند شدن تدریجی
        }

        // آواتار نهایی انتخاب شده
        sequenceAction.addAction(Actions.delay(delay));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                // نمایش آواتار نهایی
                selectedAvatarIndex = randomIndex;
                showAvatar(selectedAvatarIndex);

                // پخش صدای انتخاب نهایی
                //SoundManager.getInstance().playSuccess();

                // نمایش دکمه‌ها
                continueButton.setVisible(true);
                spinAgainButton.setVisible(true);

                // انیمیشن بزرگ‌نمایی برای آواتار انتخاب شده
                if (selectedAvatarImage != null) {
                    selectedAvatarImage.addAction(Actions.sequence(
                        Actions.scaleTo(1.2f, 1.2f, 0.2f),
                        Actions.scaleTo(1f, 1f, 0.2f),
                        Actions.scaleTo(1.1f, 1.1f, 0.2f),
                        Actions.scaleTo(1f, 1f, 0.2f)
                    ));
                }

                // انیمیشن درخشش برای قاب آواتار
                avatarFrame.addAction(Actions.sequence(
                    Actions.color(Color.GOLD, 0.3f),
                    Actions.color(Color.WHITE, 0.3f),
                    Actions.color(Color.GOLD, 0.3f),
                    Actions.color(Color.WHITE, 0.3f)
                ));

                selectionComplete = true;
            }
        }));

        // اجرای توالی اکشن‌ها
        stage.addAction(sequenceAction);
    }

    private void showAvatar(int index) {
        // حذف آواتار قبلی (اگر وجود داشته باشد)
        if (selectedAvatarImage != null) {
            selectedAvatarImage.remove();
        }

        // نمایش آواتار جدید
        selectedAvatarImage = new Image(((TextureRegionDrawable)avatarImages[index].getDrawable()).getRegion());
        selectedAvatarImage.setSize(AVATAR_SIZE, AVATAR_SIZE);

        // اضافه کردن آواتار به ظرف نگهدارنده در موقعیت صحیح
        // موقعیت آواتار را نسبت به قاب تنظیم می‌کنیم
        avatarContainer.addActor(selectedAvatarImage);

        // تنظیم موقعیت آواتار در مرکز قاب
        float x = (avatarFrame.getWidth() - AVATAR_SIZE) / 2;
        float y = (avatarFrame.getHeight() - AVATAR_SIZE) / 2;
        selectedAvatarImage.setPosition(x, y);
    }

    private Image createAvatarFrame(float size) {
        // ایجاد قاب برای آواتار
        Pixmap pixmap = new Pixmap((int)size, (int)size, Pixmap.Format.RGBA8888);

        // رنگ پس‌زمینه (داخل قاب)
        pixmap.setColor(new Color(0.2f, 0.2f, 0.3f, 1));
        pixmap.fill();

        // رنگ قاب (حاشیه)
        pixmap.setColor(Color.WHITE);

        // ضخامت قاب
        int borderThickness = 5;

        // کشیدن حاشیه (چهار ضلع)
        for (int i = 0; i < borderThickness; i++) {
            pixmap.drawRectangle(i, i, (int)size - i * 2, (int)size - i * 2);
        }

        // ایجاد تصویر از pixmap
        Texture frameTexture = new Texture(pixmap);
        pixmap.dispose();

        Image frame = new Image(frameTexture);
        frame.setSize(size, size);

        return frame;
    }

    private TextureRegionDrawable createPanelBackground(Color color) {
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texture));
    }

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
                button.addAction(Actions.color(normalColor, 0.2f));
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

        // آزادسازی تصاویر آواتارها
        if (avatarImages != null) {
            for (Image avatar : avatarImages) {
                if (avatar != null && avatar.getDrawable() instanceof TextureRegionDrawable) {
                    Texture texture = ((TextureRegionDrawable)avatar.getDrawable()).getRegion().getTexture();
                    if (texture != null) {
                        texture.dispose();
                    }
                }
            }
        }
    }
}
