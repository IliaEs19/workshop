package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    private static final float BULLET_SPEED = 500; // سرعت گلوله (پیکسل بر ثانیه)
    private static final float BULLET_SIZE = 8; // اندازه گلوله

    private float x, y;
    private float velocityX, velocityY;
    private int damage;
    private Rectangle bounds;
    private static TextureRegion texture; // تکسچر مشترک برای همه گلوله‌ها
    private float rotation; // زاویه چرخش گلوله

    // حد خارج شدن از صفحه
    private static final float OUT_OF_BOUNDS_DISTANCE = 2000;

    public Bullet(float x, float y, float directionX, float directionY, int damage) {
        this.x = x;
        this.y = y;
        this.velocityX = directionX * BULLET_SPEED;
        this.velocityY = directionY * BULLET_SPEED;
        this.damage = damage;
        this.bounds = new Rectangle(x - BULLET_SIZE/2, y - BULLET_SIZE/2, BULLET_SIZE, BULLET_SIZE);
        this.rotation = (float) Math.toDegrees(Math.atan2(directionY, directionX));

        // لود تکسچر اگر هنوز لود نشده است
        if (texture == null) {
            loadTexture();
        }
    }

    private static void loadTexture() {
        try {
            // لود تکسچر گلوله - مسیر را تغییر دهید
            Texture bulletTexture = new Texture(Gdx.files.internal("GUNS/BULLET.png"));
            texture = new TextureRegion(bulletTexture);
        } catch (Exception e) {
            // اگر فایل پیدا نشد، یک تکسچر ساده بسازیم
            Texture defaultTexture = new Texture(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            texture = new TextureRegion(defaultTexture);
            Gdx.app.error("Bullet", "Error loading texture: " + e.getMessage());
        }
    }

    public void update(float delta) {
        // بروزرسانی موقعیت
        x += velocityX * delta;
        y += velocityY * delta;

        // بروزرسانی محدوده برخورد
        bounds.setPosition(x - BULLET_SIZE/2, y - BULLET_SIZE/2);
    }

    public void render(SpriteBatch batch) {
        // رسم گلوله با چرخش مناسب
        batch.draw(texture,
            x - BULLET_SIZE/2, y - BULLET_SIZE/2,
            BULLET_SIZE/2, BULLET_SIZE/2,
            BULLET_SIZE, BULLET_SIZE,
            1, 1, rotation);
    }

    public boolean isOutOfBounds() {
        // بررسی خارج شدن از محدوده بازی
        return Math.abs(x) > OUT_OF_BOUNDS_DISTANCE || Math.abs(y) > OUT_OF_BOUNDS_DISTANCE;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getDamage() {
        return damage;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public static void disposeTexture() {
        if (texture != null && texture.getTexture() != null) {
            texture.getTexture().dispose();
        }
    }
}
