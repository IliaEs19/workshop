package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class DialogManager {

    private static final float ANIMATION_DURATION = 0.3f;

    /**
     * نمایش دیالوگ خطا با طراحی زیبا
     */
    public static void showErrorDialog(Stage stage, String title, String message, final Runnable onDismiss) {
        // ایجاد دیالوگ با استایل پیش‌فرض
        final Dialog dialog = new Dialog("", GameAssetManager.getGameAssetManager().getSkin()) {
            @Override
            protected void result(Object object) {
                if (object instanceof Boolean && (Boolean) object) {
                    hide();
                    remove();
                    if (onDismiss != null) {
                        onDismiss.run();
                    }
                }
            }
        };

        // پاک کردن جدول عنوان و اضافه کردن عنوان سفارشی
        dialog.getTitleTable().clear();

        // عنوان سفارشی با رنگ قرمز
        Label titleLabel = new Label(title, GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setColor(Color.FIREBRICK);
        titleLabel.setFontScale(1.2f);
        dialog.getTitleTable().add(titleLabel).expandX().center().padTop(15);

        // اضافه کردن دکمه بستن (X) به عنوان
        TextButton closeButton = new TextButton("X", GameAssetManager.getGameAssetManager().getSkin());
        closeButton.setColor(Color.FIREBRICK);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                dialog.remove();
                if (onDismiss != null) {
                    onDismiss.run();
                }
            }
        });
        dialog.getTitleTable().add(closeButton).padRight(10).padTop(5).size(30, 30);

        // محتوای دیالوگ
        Table contentTable = new Table();
        contentTable.pad(20);

        // ایجاد آیکون خطا (دایره قرمز)
        Image errorIcon = createColorCircle(Color.RED, 30);
        contentTable.add(errorIcon).size(48, 48).padRight(20);

        // پیام خطا
        Label messageLabel = new Label(message, GameAssetManager.getGameAssetManager().getSkin());
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.left);
        messageLabel.setColor(Color.RED);
        contentTable.add(messageLabel).width(350).expandX().fillX();

        dialog.getContentTable().add(contentTable).pad(20);

        // دکمه OK
        TextButton okButton = new TextButton("OK", GameAssetManager.getGameAssetManager().getSkin());
        okButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1)); // رنگ قرمز

        // افکت hover برای دکمه
        okButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                okButton.setColor(new Color(1f, 0.3f, 0.3f, 1)); // قرمز روشن‌تر
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                okButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1)); // قرمز اصلی
            }
        });

        dialog.button(okButton, true); // true به عنوان نتیجه برگردانده می‌شود
        dialog.getButtonTable().padBottom(15);

        // تنظیمات نهایی دیالوگ
        dialog.setModal(true);
        dialog.setMovable(false);

        // نمایش دیالوگ با انیمیشن
        dialog.show(stage);
        dialog.setOrigin(Align.center);
        dialog.setScale(0.1f);
        dialog.getColor().a = 0;
        dialog.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(ANIMATION_DURATION),
                Actions.scaleTo(1, 1, ANIMATION_DURATION, Interpolation.swingOut)
            )
        ));

        // تغییر رنگ پس‌زمینه (اگر امکان‌پذیر باشد)
        try {
            Window dialogWindow = (Window) dialog.getChildren().get(0);
            dialogWindow.setColor(new Color(1f, 0.9f, 0.9f, 0.95f)); // پس‌زمینه قرمز بسیار روشن
        } catch (Exception e) {
            Gdx.app.log("DialogManager", "Could not change background color");
        }
    }

    /**
     * نمایش دیالوگ موفقیت با طراحی زیبا
     */
    public static void showSuccessDialog(Stage stage, String title, String message, final Runnable onDismiss) {
        // ایجاد دیالوگ با استایل پیش‌فرض
        final Dialog dialog = new Dialog("", GameAssetManager.getGameAssetManager().getSkin()) {
            @Override
            protected void result(Object object) {
                if (object instanceof Boolean && (Boolean) object) {
                    hide();
                    remove();
                    if (onDismiss != null) {
                        onDismiss.run();
                    }
                }
            }
        };

        // پاک کردن جدول عنوان و اضافه کردن عنوان سفارشی
        dialog.getTitleTable().clear();

        // عنوان سفارشی با رنگ سبز
        Label titleLabel = new Label(title, GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setColor(Color.FOREST);
        titleLabel.setFontScale(1.2f);
        dialog.getTitleTable().add(titleLabel).expandX().center().padTop(15);

        // اضافه کردن دکمه بستن (X) به عنوان
        TextButton closeButton = new TextButton("X", GameAssetManager.getGameAssetManager().getSkin());
        closeButton.setColor(Color.FOREST);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                dialog.remove();
                if (onDismiss != null) {
                    onDismiss.run();
                }
            }
        });
        dialog.getTitleTable().add(closeButton).padRight(10).padTop(5).size(30, 30);

        // محتوای دیالوگ
        Table contentTable = new Table();
        contentTable.pad(20);

        // ایجاد آیکون موفقیت (دایره سبز)
        Image successIcon = createColorCircle(Color.GREEN, 30);
        contentTable.add(successIcon).size(48, 48).padRight(20);

        // پیام موفقیت
        Label messageLabel = new Label(message, GameAssetManager.getGameAssetManager().getSkin());
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.left);
        messageLabel.setColor(Color.GREEN);
        contentTable.add(messageLabel).width(350).expandX().fillX();

        dialog.getContentTable().add(contentTable).pad(20);

        // دکمه OK
        TextButton okButton = new TextButton("OK", GameAssetManager.getGameAssetManager().getSkin());
        okButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1)); // رنگ سبز

        // افکت hover برای دکمه
        okButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                okButton.setColor(new Color(0.3f, 1f, 0.3f, 1)); // سبز روشن‌تر
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                okButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1)); // سبز اصلی
            }
        });

        dialog.button(okButton, true); // true به عنوان نتیجه برگردانده می‌شود
        dialog.getButtonTable().padBottom(15);

        // تنظیمات نهایی دیالوگ
        dialog.setModal(true);
        dialog.setMovable(false);

        // نمایش دیالوگ با انیمیشن
        dialog.show(stage);
        dialog.setOrigin(Align.center);
        dialog.setScale(0.1f);
        dialog.getColor().a = 0;
        dialog.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(ANIMATION_DURATION),
                Actions.scaleTo(1, 1, ANIMATION_DURATION, Interpolation.swingOut)
            )
        ));

        // تغییر رنگ پس‌زمینه (اگر امکان‌پذیر باشد)
        try {
            Window dialogWindow = (Window) dialog.getChildren().get(0);
            dialogWindow.setColor(new Color(0.9f, 1f, 0.9f, 0.95f)); // پس‌زمینه سبز بسیار روشن
        } catch (Exception e) {
            Gdx.app.log("DialogManager", "Could not change background color");
        }
    }

    /**
     * نمایش دیالوگ تأیید با دو گزینه بله/خیر
     */
    public static void showConfirmDialog(Stage stage, String title, String message, final Runnable onYes, final Runnable onNo) {
        // ایجاد دیالوگ با استایل پیش‌فرض
        final Dialog dialog = new Dialog("", GameAssetManager.getGameAssetManager().getSkin()) {
            @Override
            protected void result(Object object) {
                hide();
                remove();
                if (object instanceof Boolean) {
                    boolean result = (Boolean) object;
                    if (result && onYes != null) {
                        onYes.run();
                    } else if (!result && onNo != null) {
                        onNo.run();
                    }
                }
            }
        };

        // پاک کردن جدول عنوان و اضافه کردن عنوان سفارشی
        dialog.getTitleTable().clear();

        // عنوان سفارشی با رنگ آبی
        Label titleLabel = new Label(title, GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setColor(Color.ROYAL);
        titleLabel.setFontScale(1.2f);
        dialog.getTitleTable().add(titleLabel).expandX().center().padTop(15);

        // محتوای دیالوگ
        Table contentTable = new Table();
        contentTable.pad(20);

        // ایجاد آیکون سؤال (دایره آبی)
        Image questionIcon = createColorCircle(Color.ROYAL, 48);
        contentTable.add(questionIcon).size(48, 48).padRight(20);

        // پیام تأیید
        Label messageLabel = new Label(message, GameAssetManager.getGameAssetManager().getSkin());
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.left);
        messageLabel.setColor(Color.DARK_GRAY);
        contentTable.add(messageLabel).width(350).expandX().fillX();

        dialog.getContentTable().add(contentTable).pad(20);

        // دکمه No
        TextButton noButton = new TextButton("No", GameAssetManager.getGameAssetManager().getSkin());
        noButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1)); // رنگ قرمز

        // افکت hover برای دکمه No
        noButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                noButton.setColor(new Color(1f, 0.3f, 0.3f, 1)); // قرمز روشن‌تر
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                noButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1)); // قرمز اصلی
            }
        });

        // دکمه Yes
        TextButton yesButton = new TextButton("Yes", GameAssetManager.getGameAssetManager().getSkin());
        yesButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1)); // رنگ سبز

        // افکت hover برای دکمه Yes
        yesButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                yesButton.setColor(new Color(0.3f, 1f, 0.3f, 1)); // سبز روشن‌تر
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                yesButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1)); // سبز اصلی
            }
        });

        // اضافه کردن دکمه‌ها به دیالوگ
        dialog.button(noButton, false); // false به عنوان نتیجه برگردانده می‌شود
        dialog.button(yesButton, true); // true به عنوان نتیجه برگردانده می‌شود
        dialog.getButtonTable().padBottom(15);

        // تنظیمات نهایی دیالوگ
        dialog.setModal(true);
        dialog.setMovable(false);

        // نمایش دیالوگ با انیمیشن
        dialog.show(stage);
        dialog.setOrigin(Align.center);
        dialog.setScale(0.1f);
        dialog.getColor().a = 0;
        dialog.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(ANIMATION_DURATION),
                Actions.scaleTo(1, 1, ANIMATION_DURATION, Interpolation.swingOut)
            )
        ));

        // تغییر رنگ پس‌زمینه (اگر امکان‌پذیر باشد)
        try {
            Window dialogWindow = (Window) dialog.getChildren().get(0);
            dialogWindow.setColor(new Color(0.9f, 0.95f, 1f, 0.95f)); // پس‌زمینه آبی بسیار روشن
        } catch (Exception e) {
            Gdx.app.log("DialogManager", "Could not change background color");
        }
    }

    /**
     * ایجاد یک دایره رنگی (برای استفاده به عنوان آیکون)
     */
    private static Image createColorCircle(Color color, int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(size / 2, size / 2, size / 2 - 1);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new Image(texture);
    }
}
