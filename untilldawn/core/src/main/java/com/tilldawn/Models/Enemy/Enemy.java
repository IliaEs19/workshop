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
    protected float stateTime;


    protected float speed;


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
        this.speed = 50;

        if (type.canShoot()) {
            this.bullets = new Array<>();
            this.shootTimer = 0;
        }
    }


    public void update(float delta, Vector2 playerPosition) {
        if (!isAlive) return;

        stateTime += delta;


        updateBehavior(delta, playerPosition);


        bounds.setPosition(x - width/2, y - height/2);


        if (type.canShoot()) {
            updateBullets(delta);
        }
    }


    protected abstract void updateBehavior(float delta, Vector2 playerPosition);


    protected void shoot(Vector2 playerPosition) {
        if (!type.canShoot() || bullets == null) return;


        float dx = playerPosition.x - x;
        float dy = playerPosition.y - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            dx /= length;
            dy /= length;


            Bullet bullet = new Bullet(x, y, dx, dy, type.getDamage());
            bullets.add(bullet);
        }
    }


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


    public void render(SpriteBatch batch) {
        if (!isAlive) return;


        TextureRegion region = type.getTextureRegion();
        if (region != null) {
            batch.draw(region,
                x - width/2, y - height/2,
                width, height);
        }


        if (bullets != null) {
            for (Bullet bullet : bullets) {
                bullet.render(batch);
            }
        }
    }


    public void takeDamage(int damage) {
        if (!isAlive) return;

        health -= damage;
        if (health <= 0) {
            isAlive = false;
        }
    }


    public boolean checkBulletCollision(Rectangle bulletBounds) {
        return isAlive && bounds.overlaps(bulletBounds);
    }


    public boolean checkPlayerCollision(Rectangle playerBounds) {
        return isAlive && bounds.overlaps(playerBounds);
    }


    public Vector2 getPosition() {
        return new Vector2(x, y);
    }


    public Rectangle getBounds() {
        return bounds;
    }


    public boolean isAlive() {
        return isAlive;
    }


    public EnemyType getType() {
        return type;
    }


    public int getHealth() {
        return health;
    }


    public Array<Bullet> getBullets() {
        return bullets;
    }
}
