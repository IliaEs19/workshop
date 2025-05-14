package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;

public class AvatarSelectionDialog extends Dialog {

    private static final float DIALOG_WIDTH = 900;
    private static final float DIALOG_HEIGHT = 600;
    private static final float AVATAR_SIZE = 150;
    private static final float AVATAR_FRAME_PADDING = 10;

    private final Stage stage;
    private final String username;
    private final Runnable onAvatarChanged;

    private int selectedAvatarIndex = -1;
    private String customAvatarPath = null;

    // مسیر آواتارهای پیش‌فرض
    private static final String[] DEFAULT_AVATAR_PATHS = {
        "avatars/character1.jpg",
        "avatars/character2.jpg",
        "avatars/character3.jpg",
        "avatars/character4.jpg"
    };

    // آرایه برای نگهداری کانتینرهای آواتار
    private Array<Table> avatarContainers = new Array<>();
    // آرایه برای نگهداری نشانگرهای انتخاب
    private Array<Image> selectionIndicators = new Array<>();

    public AvatarSelectionDialog(Stage stage, String username, Runnable onAvatarChanged) {
        super("Select Avatar", GameAssetManager.getGameAssetManager().getSkin());
        this.stage = stage;
        this.username = username;
        this.onAvatarChanged = onAvatarChanged;

        // تنظیمات اولیه دیالوگ
        setModal(true);
        setMovable(false);
        setResizable(false);

        // ایجاد محتوای دیالوگ
        createContent();

        // تنظیم اندازه و موقعیت دیالوگ
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setPosition(
            (stage.getWidth() - DIALOG_WIDTH) / 2,
            (stage.getHeight() - DIALOG_HEIGHT) / 2
        );
    }

    private void createContent() {
        // استایل عنوان
        getTitleLabel().setAlignment(Align.center);
        getTitleLabel().setFontScale(1.5f);
        getTitleLabel().setColor(Color.GOLD);

        // ایجاد جدول اصلی
        Table contentTable = new Table();
        contentTable.setFillParent(true);
        contentTable.pad(20);

        // عنوان بخش آواتارهای پیش‌فرض
        Label presetLabel = new Label("PRESET AVATARS", GameAssetManager.getGameAssetManager().getSkin());
        presetLabel.setFontScale(1.2f);
        presetLabel.setColor(Color.CYAN);
        contentTable.add(presetLabel).colspan(4).padBottom(20).row();

        // نمایش آواتارهای پیش‌فرض در یک ردیف
        Table presetAvatarsTable = new Table();

        // دریافت آواتار فعلی کاربر
        User user = SaveData.getInstance().getUser(username);
        String currentAvatarPath = (user != null) ? user.getAvatarPath() : "";

        for (int i = 0; i < DEFAULT_AVATAR_PATHS.length; i++) {
            final int avatarIndex = i;
            Table avatarContainer = createAvatarContainer(DEFAULT_AVATAR_PATHS[i], i);

            // اضافه کردن نشانگر انتخاب (ابتدا مخفی است)
            Image selectionIndicator = createSelectionIndicator();
            avatarContainer.addActor(selectionIndicator);
            selectionIndicator.setPosition(
                (avatarContainer.getWidth() - selectionIndicator.getWidth()) / 2,
                (avatarContainer.getHeight() - selectionIndicator.getHeight()) / 2
            );
            selectionIndicator.setVisible(false);

            // ذخیره نشانگر در آرایه
            selectionIndicators.add(selectionIndicator);

            // اضافه کردن لیسنر کلیک
            avatarContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectPresetAvatar(avatarIndex);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    // افکت هاور
                    avatarContainer.addAction(Actions.color(new Color(1.2f, 1.2f, 1.2f, 1f), 0.2f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    // بازگشت به حالت عادی
                    avatarContainer.addAction(Actions.color(Color.WHITE, 0.2f));
                }
            });

            // اضافه کردن به جدول و آرایه
            presetAvatarsTable.add(avatarContainer).size(AVATAR_SIZE + AVATAR_FRAME_PADDING * 2 + 20).pad(10);
            avatarContainers.add(avatarContainer);

            // اگر این آواتار فعلی کاربر است، انتخاب شود
            if (currentAvatarPath.equals(DEFAULT_AVATAR_PATHS[i])) {
                selectPresetAvatar(i);
            }
        }

        contentTable.add(presetAvatarsTable).colspan(4).padBottom(30).row();

        // خط جداکننده
        Image separator = new Image(createColorDrawable(Color.GRAY));
        contentTable.add(separator).colspan(4).height(2).expandX().fillX().padBottom(20).row();

        // عنوان بخش آپلود آواتار سفارشی
        Label customLabel = new Label("CUSTOM AVATAR", GameAssetManager.getGameAssetManager().getSkin());
        customLabel.setFontScale(1.2f);
        customLabel.setColor(Color.CYAN);
        contentTable.add(customLabel).colspan(4).padBottom(20).row();

        // ایجاد ناحیه درگ و دراپ
        Table dropZone = createDropZone();
        contentTable.add(dropZone).colspan(4).width(400).height(200).padBottom(20).row();

        // دکمه‌های اکشن دیالوگ
        Table buttonTable = new Table();

        // دکمه ذخیره
        TextButton saveButton = new TextButton("SAVE", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(saveButton, new Color(0.2f, 0.8f, 0.2f, 1f), new Color(0.3f, 0.9f, 0.3f, 1f));
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSelectedAvatar();
            }
        });

        // دکمه لغو
        TextButton cancelButton = new TextButton("CANCEL", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(cancelButton, new Color(0.8f, 0.2f, 0.2f, 1f), new Color(0.9f, 0.3f, 0.3f, 1f));
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        buttonTable.add(cancelButton).width(300).height(100).pad(10);
        buttonTable.add(saveButton).width(300).height(100).pad(10);

        contentTable.add(buttonTable).colspan(4).expandX().fillX();

        getContentTable().add(contentTable).expand().fill();
    }

    private Table createAvatarContainer(String avatarPath, int index) {
        Table container = new Table();
        container.setBackground(createPanelBackground(new Color(0.15f, 0.15f, 0.25f, 0.7f)));
        container.setName("avatarContainer" + index);

        // ایجاد قاب آواتار
        Image frame = createAvatarFrame(AVATAR_SIZE + AVATAR_FRAME_PADDING * 2);
        frame.setName("avatarFrame" + index);

        try {
            // بارگذاری تصویر آواتار
            Texture avatarTexture = new Texture(Gdx.files.internal(avatarPath));
            avatarTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            Image avatarImage = new Image(avatarTexture);
            avatarImage.setSize(AVATAR_SIZE, AVATAR_SIZE);
            avatarImage.setName("avatarImage" + index);

            // اضافه کردن قاب و آواتار به کانتینر
            container.add(frame).size(AVATAR_SIZE + AVATAR_FRAME_PADDING * 2);
            container.addActor(avatarImage);
            avatarImage.setPosition(AVATAR_FRAME_PADDING, AVATAR_FRAME_PADDING);

            // اضافه کردن شماره آواتار
            Label indexLabel = new Label("#" + (index + 1), GameAssetManager.getGameAssetManager().getSkin());
            indexLabel.setFontScale(0.8f);
            indexLabel.setColor(Color.WHITE);
            container.add(indexLabel).padTop(5).row();

        } catch (Exception e) {
            Gdx.app.error("AvatarSelectionDialog", "Error loading avatar: " + e.getMessage());

            // در صورت خطا، یک تصویر خالی نمایش دهیم
            Image emptyImage = new Image(createColorDrawable(new Color(0.3f, 0.3f, 0.5f, 1)));
            emptyImage.setSize(AVATAR_SIZE, AVATAR_SIZE);

            container.add(frame).size(AVATAR_SIZE + AVATAR_FRAME_PADDING * 2);
            container.addActor(emptyImage);
            emptyImage.setPosition(AVATAR_FRAME_PADDING, AVATAR_FRAME_PADDING);
        }

        // قابلیت کلیک کردن روی کانتینر
        container.setTouchable(Touchable.enabled);

        return container;
    }

    // ایجاد نشانگر انتخاب (تیک یا دایره)
    private Image createSelectionIndicator() {
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);

        // ایجاد یک دایره با تیک داخل آن
        pixmap.setColor(new Color(0.2f, 0.8f, 0.2f, 0.9f)); // سبز نیمه شفاف
        pixmap.fillCircle(25, 25, 25);

        // رسم تیک
        pixmap.setColor(Color.WHITE);
        pixmap.fillTriangle(15, 25, 22, 32, 35, 15);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new Image(texture);
    }

    private Table createDropZone() {
        Table dropZone = new Table();
        dropZone.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
        dropZone.pad(20);

        // آیکون آپلود
        Image uploadIcon = createUploadIcon();

        // متن راهنما
        Label dropLabel = new Label("Drag & Drop Image Here\nor", GameAssetManager.getGameAssetManager().getSkin());
        dropLabel.setFontScale(1.1f);
        dropLabel.setAlignment(Align.center);
        dropLabel.setColor(Color.WHITE);

        // دکمه انتخاب فایل
        TextButton browseButton = new TextButton("BROWSE FILES", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(browseButton, new Color(0.2f, 0.6f, 0.9f, 1f), new Color(0.3f, 0.7f, 1f, 1f));
        browseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                openFileChooser();
            }
        });

        // اضافه کردن به منطقه درگ و دراپ
        dropZone.add(uploadIcon).size(60, 60).padBottom(10).row();
        dropZone.add(dropLabel).padBottom(15).row();
        dropZone.add(browseButton).width(180).height(50);

        // اضافه کردن لیسنر برای درگ و دراپ
        setupDragAndDrop(dropZone);

        return dropZone;
    }

    private Image createUploadIcon() {
        Pixmap pixmap = new Pixmap(60, 60, Pixmap.Format.RGBA8888);

        // پس‌زمینه شفاف
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        // رسم آیکون آپلود (فلش به بالا)
        pixmap.setColor(Color.WHITE);

        // رسم مستطیل پایین
        pixmap.fillRectangle(15, 40, 30, 5);

        // رسم خط عمودی
        pixmap.fillRectangle(27, 15, 6, 25);

        // رسم مثلث بالا
        pixmap.fillTriangle(15, 20, 45, 20, 30, 5);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new Image(texture);
    }

    private void setupDragAndDrop(Table dropZone) {
        // اضافه کردن لیسنر برای تغییر رنگ هنگام درگ
        dropZone.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // تغییر رنگ هنگام هاور
                dropZone.setBackground(createPanelBackground(new Color(0.25f, 0.25f, 0.35f, 0.6f)));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // بازگشت به رنگ عادی
                dropZone.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
            }
        });

        // توجه: پیاده‌سازی واقعی درگ و دراپ در LibGDX نیاز به کد پلتفرم خاص دارد
        // که در بخش بعدی توضیح داده می‌شود
    }

    private void selectPresetAvatar(int index) {
        // انتخاب آواتار پیش‌فرض
        selectedAvatarIndex = index;
        customAvatarPath = null;

        // نمایش بصری انتخاب
        updateSelectionIndicators(index);

        // افکت انیمیشن برای آواتار انتخاب شده
        Table selectedContainer = avatarContainers.get(index);
        selectedContainer.addAction(Actions.sequence(
            Actions.scaleTo(1.1f, 1.1f, 0.1f),
            Actions.scaleTo(1.0f, 1.0f, 0.1f)
        ));
    }

    private void updateSelectionIndicators(int selectedIndex) {
        // پنهان کردن همه نشانگرها
        for (int i = 0; i < selectionIndicators.size; i++) {
            selectionIndicators.get(i).setVisible(false);
        }

        // نمایش نشانگر انتخاب شده
        if (selectedIndex >= 0 && selectedIndex < selectionIndicators.size) {
            selectionIndicators.get(selectedIndex).setVisible(true);

            // افکت ظاهر شدن
            selectionIndicators.get(selectedIndex).addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.visible(true),
                Actions.alpha(1, 0.3f)
            ));
        }
    }

    private void openFileChooser() {
        // این بخش در ادامه توضیح داده می‌شود
        showMessage("File browser will be opened.\nPlease select an image file.");
    }

    private void saveSelectedAvatar() {
        boolean success = false;

        if (selectedAvatarIndex >= 0 && selectedAvatarIndex < DEFAULT_AVATAR_PATHS.length) {
            // ذخیره آواتار پیش‌فرض
            success = SaveData.getInstance().saveUserAvatar(username, DEFAULT_AVATAR_PATHS[selectedAvatarIndex]);
        } else if (customAvatarPath != null && !customAvatarPath.isEmpty()) {
            // ذخیره آواتار سفارشی
            success = SaveData.getInstance().saveUserAvatar(username, customAvatarPath);
        } else {
            showMessage("Please select an avatar first!");
            return;
        }

        if (success) {
            // فراخوانی کالبک برای به‌روزرسانی UI
            if (onAvatarChanged != null) {
                onAvatarChanged.run();
            }

            // بستن دیالوگ
            hide();
        } else {
            showMessage("Failed to save avatar. Please try again.");
        }
    }

    private void showMessage(String message) {
        Dialog messageDialog = new Dialog("", GameAssetManager.getGameAssetManager().getSkin());
        messageDialog.text(message);
        messageDialog.button("OK");
        messageDialog.show(stage);
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
        int borderThickness = 3;

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

    private TextureRegionDrawable createColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
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
                button.setColor(hoverColor);
                button.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setColor(normalColor);
                button.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });
    }
    // سایر متدهای کمکی مانند قبل...
    // createAvatarFrame, createPanelBackground, createColorDrawable, styleButton, showMessage
}
