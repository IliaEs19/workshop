package com.tilldawn.Models.Item;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Item {
    private ItemType type;
    private float x, y;
    private float width, height;
    private Rectangle bounds;
    private float stateTime;
    private float lifeTime; // زمان باقی‌مانده آیتم قبل از ناپدید شدن
    private float floatOffset; // برای افکت شناور بودن آیتم

    public Item(ItemType type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 20;
        this.bounds = new Rectangle(x - width/2, y - height/2, width, height);
        this.stateTime = 0;
        this.lifeTime = 15; // آیتم‌ها 15 ثانیه بعد ناپدید می‌شوند
        this.floatOffset = 0;
    }

    public void update(float delta) {
        stateTime += delta;
        lifeTime -= delta;

        // افکت شناور بودن آیتم
        floatOffset = (float) Math.sin(stateTime * 5) * 2;

        // بروزرسانی مرزهای برخورد
        bounds.setPosition(x - width/2, y - height/2 + floatOffset);
    }

    public void render(SpriteBatch batch) {
        if (type.getTextureRegion() != null) {
            batch.draw(type.getTextureRegion(),
                x - width/2,
                y - height/2 + floatOffset,
                width, height);
        }
    }

    public boolean checkCollision(Rectangle playerBounds) {
        return bounds.overlaps(playerBounds);
    }

    public boolean isExpired() {
        return lifeTime <= 0;
    }

    public ItemType getType() {
        return type;
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }
}
