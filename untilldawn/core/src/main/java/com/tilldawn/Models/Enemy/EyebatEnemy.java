package com.tilldawn.Models.Enemy;

import com.badlogic.gdx.math.Vector2;

public class EyebatEnemy extends Enemy {
    private static final float SHOOT_INTERVAL = 3.0f; // هر 3 ثانیه یک بار شلیک می‌کند

    public EyebatEnemy(float x, float y) {
        super(EnemyType.EYEBAT, x, y, 35, 35);
        this.speed = 80; // سرعت بیشتر از هیولای شاخک
    }

    @Override
    protected void updateBehavior(float delta, Vector2 playerPosition) {
        // حرکت به سمت بازیکن با فاصله
        float dx = playerPosition.x - x;
        float dy = playerPosition.y - y;
        float distanceSquared = dx * dx + dy * dy;

        // اگر فاصله کمتر از 200 است، حرکت نمی‌کند
        if (distanceSquared > 200 * 200) {
            float length = (float) Math.sqrt(distanceSquared);
            dx /= length;
            dy /= length;

            x += dx * speed * delta;
            y += dy * speed * delta;
        }

        // شلیک به سمت بازیکن
        shootTimer += delta;
        if (shootTimer >= SHOOT_INTERVAL) {
            shoot(playerPosition);
            shootTimer = 0;
        }
    }
}
