package com.tilldawn.Models.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tilldawn.Models.Bullet;
import com.tilldawn.Models.Item.ItemType;

public abstract class Enemy {
    protected EnemyType type;
    protected float x, y;
    protected float width, height;
    protected int health;
    protected boolean isAlive;
    protected Rectangle bounds;
    protected float stateTime; // برای انیمیشن

    // سرعت پایه دشمن
    protected float speed;

    // برای دشمن‌هایی که شلیک می‌کنند
    protected Array<Bullet> bullets;
    protected float shootTimer;

    public Enemy(EnemyType type, float x, float y, float width, float height) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.health = type.getMaxHealth();
        this.isAlive = true;
        this.bounds = new Rectangle(x - width/2, y - height/2, width, height);
        this.stateTime = 0;
        this.speed = 50; // سرعت پایه

        if (type.canShoot()) {
            this.bullets = new Array<>();
            this.shootTimer = 0;
        }
    }

    // متد اصلی بروزرسانی که در هر فریم صدا زده می‌شود
    public void update(float delta, Vector2 playerPosition) {
        if (!isAlive) return;

        stateTime += delta;

        // بروزرسانی موقعیت و رفتار دشمن
        updateBehavior(delta, playerPosition);

        // بروزرسانی مرزهای برخورد
        bounds.setPosition(x - width/2, y - height/2);

        // بروزرسانی گلوله‌ها اگر دشمن شلیک می‌کند
        if (type.canShoot()) {
            updateBullets(delta);
        }
    }

    // این متد در کلاس‌های فرزند پیاده‌سازی می‌شود
    protected abstract void updateBehavior(float delta, Vector2 playerPosition);

    // متد شلیک برای دشمن‌هایی که می‌توانند شلیک کنند
    protected void shoot(Vector2 playerPosition) {
        if (!type.canShoot() || bullets == null) return;

        // محاسبه جهت به سمت بازیکن
        float dx = playerPosition.x - x;
        float dy = playerPosition.y - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            dx /= length;
            dy /= length;

            // ایجاد گلوله جدید
            Bullet bullet = new Bullet(x, y, dx, dy, type.getDamage());
            bullets.add(bullet);
        }
    }

    // بروزرسانی گلوله‌ها
    protected void updateBullets(float delta) {
        if (bullets == null) return;

        for (int i = bullets.size - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(delta);

            if (bullet.isOutOfBounds()) {
                bullets.removeIndex(i);
            }
        }
    }

    // رسم دشمن و گلوله‌هایش
    public void render(SpriteBatch batch) {
        if (!isAlive) return;

        // رسم دشمن
        TextureRegion region = type.getTextureRegion();
        if (region != null) {
            batch.draw(region,
                x - width/2, y - height/2,
                width, height);
        }

        // رسم گلوله‌ها
        if (bullets != null) {
            for (Bullet bullet : bullets) {
                bullet.render(batch);
            }
        }
    }

    // دریافت آسیب
    public void takeDamage(int damage) {
        if (!isAlive) return;

        health -= damage;
        if (health <= 0) {
            isAlive = false;
        }
    }

    // بررسی برخورد با گلوله
    public boolean checkBulletCollision(Rectangle bulletBounds) {
        return isAlive && bounds.overlaps(bulletBounds);
    }

    // بررسی برخورد با بازیکن
    public boolean checkPlayerCollision(Rectangle playerBounds) {
        return isAlive && bounds.overlaps(playerBounds);
    }

    // گرفتن موقعیت دشمن
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    // گرفتن مرزهای برخورد
    public Rectangle getBounds() {
        return bounds;
    }

    // بررسی زنده بودن
    public boolean isAlive() {
        return isAlive;
    }

    // گرفتن نوع دشمن
    public EnemyType getType() {
        return type;
    }

    // گرفتن سلامتی دشمن
    public int getHealth() {
        return health;
    }

    // گرفتن لیست گلوله‌ها
    public Array<Bullet> getBullets() {
        return bullets;
    }
}
