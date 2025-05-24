package com.tilldawn.Models.Enemy;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tilldawn.Models.Bullet;

public class ElderEnemy extends Enemy {
    private static final float DASH_INTERVAL = 5.0f;
    private static final float DASH_SPEED = 300.0f;
    private static final float DASH_DURATION = 1.0f;

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
        this.speed = 40;
        this.isDashing = false;
        this.dashTimer = 0;
        this.dashDurationTimer = 0;
        this.dashDirection = new Vector2();
        this.maxShieldSize = 400;
        this.shieldSize = maxShieldSize;
        this.gameTime = 0;
        this.gameMaxTime = gameMaxTime;
    }

    @Override
    protected void updateBehavior(float delta, Vector2 playerPosition) {

        gameTime += delta;


        float progress = Math.min(gameTime / gameMaxTime, 1.0f);
        shieldSize = maxShieldSize * (1.0f - progress);

        if (isDashing) {

            dashDurationTimer += delta;


            x += dashDirection.x * DASH_SPEED * delta;
            y += dashDirection.y * DASH_SPEED * delta;


            if (dashDurationTimer >= DASH_DURATION) {
                isDashing = false;
                dashDurationTimer = 0;
            }
        } else {

            float dx = playerPosition.x - x;
            float dy = playerPosition.y - y;
            float length = (float) Math.sqrt(dx * dx + dy * dy);

            if (length > 0) {
                dx /= length;
                dy /= length;

                x += dx * speed * delta;
                y += dy * speed * delta;
            }


            dashTimer += delta;
            if (dashTimer >= DASH_INTERVAL) {
                startDash(playerPosition);
                dashTimer = 0;
            }
        }


        shootTimer += delta;
        if (shootTimer >= 1.0f) {

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


        float dx = playerPosition.x - x;
        float dy = playerPosition.y - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            dx /= length;
            dy /= length;
            dashDirection.set(dx, dy);
        } else {
            dashDirection.set(1, 0);
        }
    }

    @Override
    public void render(SpriteBatch batch) {

        if (shieldSize > 0) {


        }


        super.render(batch);
    }

    @Override
    public boolean checkBulletCollision(Rectangle bulletBounds) {

        if (shieldSize > 0) {
            float centerX = x;
            float centerY = y;
            float bulletCenterX = bulletBounds.x + bulletBounds.width / 2;
            float bulletCenterY = bulletBounds.y + bulletBounds.height / 2;

            float dx = centerX - bulletCenterX;
            float dy = centerY - bulletCenterY;
            float distanceSquared = dx * dx + dy * dy;

            if (distanceSquared <= shieldSize * shieldSize / 4) {
                return true;
            }
        }


        return super.checkBulletCollision(bulletBounds);
    }

    public float getShieldSize() {
        return shieldSize;
    }
}
