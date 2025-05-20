package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tilldawn.Models.Hero.WeaponType;

public class Weapon {
    private WeaponType type;
    private TextureRegion texture;
    private float rotation = 0;
    private Vector2 position;
    private int currentAmmo;
    private float reloadTimer = 0;
    private boolean isReloading = false;
    private Array<Bullet> bullets;
    private float shootTimer = 0;
    private static final float SHOOT_DELAY = 0.1f; // حداقل زمان بین شلیک‌ها

    // فاصله تفنگ از مرکز بازیکن
    private static final float WEAPON_OFFSET_X = 20;
    private static final float WEAPON_OFFSET_Y = 0;

    // اندازه تفنگ
    private static final float WEAPON_WIDTH = 40;
    private static final float WEAPON_HEIGHT = 20;

    // متغیر برای دیباگ
    private boolean lastShootAttemptFailed = false;
    private String shootFailReason = "";

    public Weapon(WeaponType type) {
        this.type = type;
        this.currentAmmo = type.getMaxAmmo();
        this.position = new Vector2();
        this.bullets = new Array<>();

        // لود تکسچر تفنگ
        loadTexture();
    }

    private void loadTexture() {
        try {
            // لود تکسچر از مسیر مشخص شده در WeaponType
            Texture weaponTexture = new Texture(Gdx.files.internal(type.getTexturePath()));
            texture = new TextureRegion(weaponTexture);
        } catch (Exception e) {
            // اگر فایل پیدا نشد، یک تکسچر پیش‌فرض بسازیم
            Texture defaultTexture = new Texture(Gdx.files.internal("GUNS/SMG.png"));
            texture = new TextureRegion(defaultTexture);
            Gdx.app.error("Weapon", "Error loading texture: " + e.getMessage());
        }
    }

    public void update(float delta, Vector2 playerPosition, float targetX, float targetY) {
        // بروزرسانی تایمرها
        if (isReloading) {
            reloadTimer += delta;
            if (reloadTimer >= type.getReloadTime()) {
                completeReload();
            }
        }

        if (shootTimer > 0) {
            shootTimer -= delta;
        }

        // محاسبه زاویه بین بازیکن و هدف (موس)
        updateRotation(playerPosition, targetX, targetY);

        // بروزرسانی موقعیت تفنگ نسبت به بازیکن
        updatePosition(playerPosition);

        // بروزرسانی گلوله‌ها
        for (int i = bullets.size - 1; i >= 0; i--) {
            try {
                Bullet bullet = bullets.get(i);
                bullet.update(delta);

                // حذف گلوله‌های خارج از صفحه
                if (bullet.isOutOfBounds()) {
                    bullets.removeIndex(i);
                }
            } catch (Exception e) {
                // حذف گلوله‌های مشکل‌دار
                bullets.removeIndex(i);
                Gdx.app.error("Weapon", "Error updating bullet: " + e.getMessage());
            }
        }
    }

    private void updateRotation(Vector2 playerPosition, float targetX, float targetY) {
        // محاسبه زاویه بین بازیکن و هدف
        float dx = targetX - playerPosition.x;
        float dy = targetY - playerPosition.y;
        rotation = (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    private void updatePosition(Vector2 playerPosition) {
        // محاسبه موقعیت تفنگ با توجه به زاویه
        float radians = (float) Math.toRadians(rotation);
        float offsetX = WEAPON_OFFSET_X * (float) Math.cos(radians) - WEAPON_OFFSET_Y * (float) Math.sin(radians);
        float offsetY = WEAPON_OFFSET_X * (float) Math.sin(radians) + WEAPON_OFFSET_Y * (float) Math.cos(radians);

        position.x = playerPosition.x + offsetX;
        position.y = playerPosition.y + offsetY;
    }

    public void render(SpriteBatch batch) {
        // رسم گلوله‌ها (اول گلوله‌ها را رسم می‌کنیم تا پشت تفنگ باشند)
        for (int i = 0; i < bullets.size; i++) {
            try {
                Bullet bullet = bullets.get(i);
                bullet.render(batch);
            } catch (Exception e) {
                Gdx.app.error("Weapon", "Error rendering bullet: " + e.getMessage());
            }
        }

        // رسم تفنگ
        batch.draw(texture,
            position.x - WEAPON_WIDTH/2, position.y - WEAPON_HEIGHT/2,
            WEAPON_WIDTH/2, WEAPON_HEIGHT/2,
            WEAPON_WIDTH, WEAPON_HEIGHT,
            1, 1, rotation);
    }

    public boolean shoot(Vector2 playerPosition, float targetX, float targetY) {
        lastShootAttemptFailed = false;

        // بررسی شرایط شلیک
        if (isReloading) {
            lastShootAttemptFailed = true;
            shootFailReason = "Reloading";
            return false;
        }

        if (shootTimer > 0) {
            lastShootAttemptFailed = true;
            shootFailReason = "Cooldown";
            return false;
        }

        if (currentAmmo <= 0) {
            lastShootAttemptFailed = true;
            shootFailReason = "No Ammo";
            startReload(); // ریلود اتوماتیک
            return false;
        }

        // کاهش مهمات
        currentAmmo--;
        shootTimer = SHOOT_DELAY;

        // محاسبه جهت شلیک
        float dx = targetX - position.x; // استفاده از موقعیت تفنگ به جای بازیکن
        float dy = targetY - position.y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        // اطمینان از صفر نبودن طول
        if (length < 0.0001f) {
            dx = 1;
            dy = 0;
        } else {
            dx /= length;
            dy /= length;
        }

        // ایجاد گلوله‌ها بر اساس تعداد پروجکتایل سلاح
        for (int i = 0; i < type.getProjectileCount(); i++) {
            float spreadAngle = 0;
            if (type.getProjectileCount() > 1) {
                // پخش گلوله‌ها برای سلاح‌هایی مثل شاتگان
                spreadAngle = (i - (type.getProjectileCount() - 1) / 2f) * 10;
            }

            float radians = (float) Math.toRadians(spreadAngle);
            float spreadDx = dx * (float) Math.cos(radians) - dy * (float) Math.sin(radians);
            float spreadDy = dx * (float) Math.sin(radians) + dy * (float) Math.cos(radians);

            try {
                Bullet bullet = new Bullet(position.x, position.y, spreadDx, spreadDy, type.getDamage());
                bullets.add(bullet);
            } catch (Exception e) {
                Gdx.app.error("Weapon", "Error creating bullet: " + e.getMessage());
            }
        }

        // اگر مهمات تمام شد، ریلود اتوماتیک
        if (currentAmmo <= 0) {
            startReload();
        }

        return true;
    }

    public void startReload() {
        if (!isReloading && currentAmmo < type.getMaxAmmo()) {
            isReloading = true;
            reloadTimer = 0;
        }
    }

    private void completeReload() {
        currentAmmo = type.getMaxAmmo();
        isReloading = false;
        reloadTimer = 0;
    }

    public WeaponType getType() {
        return type;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public float getReloadProgress() {
        if (!isReloading) return 0;
        return reloadTimer / type.getReloadTime();
    }

    public Array<Bullet> getBullets() {
        return bullets;
    }

    public boolean wasLastShootAttemptFailed() {
        return lastShootAttemptFailed;
    }

    public String getShootFailReason() {
        return shootFailReason;
    }

    public void dispose() {
        if (texture != null && texture.getTexture() != null) {
            texture.getTexture().dispose();
        }
    }
}
