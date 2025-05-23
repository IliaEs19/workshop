package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;

public class GameCursor {

    private static Cursor customCursor;
    private static boolean initialized = false;

    /**
     * مقداردهی اولیه کرسر بازی
     * این متد باید فقط یک بار در ابتدای بازی فراخوانی شود
     */
    public static void initialize() {
        if (!initialized) {
            // ایجاد کرسر سفارشی متناسب با تم بازی
            customCursor = createGameCursor();

            // تنظیم کرسر سفارشی
            if (customCursor != null) {
                Gdx.graphics.setCursor(customCursor);
            }

            initialized = true;
        }
    }

    /**
     * آزادسازی منابع کرسر
     * این متد باید در انتهای بازی فراخوانی شود
     */
    public static void dispose() {
        if (customCursor != null) {
            customCursor.dispose();
            customCursor = null;
        }
        initialized = false;
    }

    /**
     * ایجاد کرسر سفارشی متناسب با تم بازی
     * در اینجا یک کرسر نشانه‌گیر با تم تیره و قرمز ایجاد می‌کنیم
     * که مناسب بازی‌های اکشن و تیراندازی است
     */
    private static Cursor createGameCursor() {
        // افزایش سایز کرسر به 48×48
        int cursorSize = 64;
        int centerPoint = cursorSize / 2;

        // ایجاد یک پیکسل‌مپ با اندازه بزرگتر
        Pixmap pixmap = new Pixmap(cursorSize, cursorSize, Pixmap.Format.RGBA8888);

        // پاک کردن پیکسل‌مپ (شفاف)
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        // طراحی یک کرسر نشانه‌گیر با تم تیره و قرمز
        // با توجه به اندازه بزرگتر، ضخامت خطوط و اندازه دایره‌ها را افزایش می‌دهیم

        // حلقه بیرونی (مشکی با آلفای کمتر برای سایه)
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.drawCircle(centerPoint, centerPoint, 21);

        // خطوط اصلی (مشکی)
        pixmap.setColor(0, 0, 0, 1);
        // خط افقی
        pixmap.fillRectangle(centerPoint - 15, centerPoint - 1, 30, 3);
        // خط عمودی
        pixmap.fillRectangle(centerPoint - 1, centerPoint - 15, 3, 30);

        // حلقه میانی (قرمز تیره)
        pixmap.setColor(0.7f, 0, 0, 1);
        pixmap.drawCircle(centerPoint, centerPoint, 12);
        pixmap.drawCircle(centerPoint, centerPoint, 11);

        // حلقه داخلی (قرمز روشن)
        pixmap.setColor(1, 0, 0, 1);
        pixmap.drawCircle(centerPoint, centerPoint, 6);
        pixmap.drawCircle(centerPoint, centerPoint, 5);

        // نقطه مرکزی (سفید)
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fillCircle(centerPoint, centerPoint, 2);

        // ایجاد کرسر از پیکسل‌مپ (نقطه هات‌اسپات در مرکز)
        Cursor cursor = Gdx.graphics.newCursor(pixmap, centerPoint, centerPoint);

        // آزادسازی پیکسل‌مپ
        pixmap.dispose();

        return cursor;
    }
}
