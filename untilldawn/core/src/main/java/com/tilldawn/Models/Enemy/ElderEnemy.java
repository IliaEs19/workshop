package com.tilldawn.Models.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tilldawn.Models.Bullet;

public class ElderEnemy extends Enemy {
    private static final float DASH_INTERVAL = 5.0f; // هر 5 ثانیه یک بار dash می‌زند
    private static final float DASH_SPEED = 300.0f; // سرعت dash
    private static final float DASH_DURATION = 1.0f; // مدت زمان dash

    private boolean isDashing;
    private float dashTimer;
    private float dashDurationTimer;
    private Vector2 dashDirection;
    private float shieldSize;
    private float maxShieldSize;
    private float gameTime;
    private float gameMaxTime;

    public ElderEnemy(float x, float y, float gameMaxTime) {
        super(EnemyType.ELDER, x, y, 70, 70);
        this.speed = 40; // سرعت پایه
        this.isDashing = false;
        this.dashTimer = 0;
        this.dashDurationTimer = 0;
        this.dashDirection = new Vector2();
        this.maxShieldSize = 400; // اندازه اولیه حفاظ
        this.shieldSize = maxShieldSize;
        this.gameTime = 0;
        this.gameMaxTime = gameMaxTime;
    }

    @Override
    protected void updateBehavior(float delta, Vector2 playerPosition) {
        // بروزرسانی زمان بازی
        gameTime += delta;

        // بروزرسانی اندازه حفاظ
        float progress = Math.min(gameTime / gameMaxTime, 1.0f);
        shieldSize = maxShieldSize * (1.0f - progress);

        if (isDashing) {
            // اگر در حال dash است
            dashDurationTimer += delta;

            // حرکت با سرعت dash در جهت تعیین شده
            x += dashDirection.x * DASH_SPEED * delta;
            y += dashDirection.y * DASH_SPEED * delta;

            // پایان dash
            if (dashDurationTimer >= DASH_DURATION) {
                isDashing = false;
                dashDurationTimer = 0;
            }
        } else {
            // حرکت عادی به سمت بازیکن
            float dx = playerPosition.x - x;
            float dy = playerPosition.y - y;
            float length = (float) Math.sqrt(dx * dx + dy * dy);

            if (length > 0) {
                dx /= length;
                dy /= length;

                x += dx * speed * delta;
                y += dy * speed * delta;
            }

            // بررسی زمان dash
            dashTimer += delta;
            if (dashTimer >= DASH_INTERVAL) {
                startDash(playerPosition);
                dashTimer = 0;
            }
        }

        // شلیک گلوله‌های تصادفی
        shootTimer += delta;
        if (shootTimer >= 1.0f) {
            // شلیک به چند جهت تصادفی
            for (int i = 0; i < 3; i++) {
                float angle = MathUtils.random(360);
                float radians = (float) Math.toRadians(angle);
                float dx = (float) Math.cos(radians);
                float dy = (float) Math.sin(radians);

                if (bullets != null) {
                    Bullet bullet = new Bullet(x, y, dx, dy, type.getDamage());
                    bullets.add(bullet);
                }
            }
            shootTimer = 0;
        }
    }

    private void startDash(Vector2 playerPosition) {
        isDashing = true;
        dashDurationTimer = 0;

        // تعیین جهت dash به سمت بازیکن
        float dx = playerPosition.x - x;
        float dy = playerPosition.y - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            dx /= length;
            dy /= length;
            dashDirection.set(dx, dy);
        } else {
            dashDirection.set(1, 0); // جهت پیش‌فرض
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // رسم حفاظ
        if (shieldSize > 0) {
            // اینجا باید کد رسم حفاظ را اضافه کنید
            // می‌توانید از ShapeRenderer استفاده کنید یا یک تکسچر دایره‌ای
        }

        // رسم دشمن و گلوله‌ها
        super.render(batch);
    }

    @Override
    public boolean checkBulletCollision(Rectangle bulletBounds) {
        // اگر حفاظ فعال است، ابتدا برخورد با حفاظ را بررسی کنید
        if (shieldSize > 0) {
            float centerX = x;
            float centerY = y;
            float bulletCenterX = bulletBounds.x + bulletBounds.width / 2;
            float bulletCenterY = bulletBounds.y + bulletBounds.height / 2;

            float dx = centerX - bulletCenterX;
            float dy = centerY - bulletCenterY;
            float distanceSquared = dx * dx + dy * dy;

            if (distanceSquared <= shieldSize * shieldSize / 4) {
                return true; // برخورد با حفاظ
            }
        }

        // بررسی برخورد با خود دشمن
        return super.checkBulletCollision(bulletBounds);
    }

    public float getShieldSize() {
        return shieldSize;
    }
}
