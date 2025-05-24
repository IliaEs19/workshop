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
    private static final float SHOOT_DELAY = 0.05f;
    private int additionalProjectileCount = 0;
    private int additionalMaxAmmo = 0;


    private static final float WEAPON_OFFSET_X = 20;
    private static final float WEAPON_OFFSET_Y = 0;


    private static final float WEAPON_WIDTH = 40;
    private static final float WEAPON_HEIGHT = 20;


    private boolean lastShootAttemptFailed = false;
    private String shootFailReason = "";

    public Weapon(WeaponType type) {
        this.type = type;
        this.currentAmmo = type.getMaxAmmo();
        this.position = new Vector2();
        this.bullets = new Array<>();
        this.additionalProjectileCount = 0;
        this.additionalMaxAmmo = 0;
        loadTexture();
    }

    private void loadTexture() {
        try {

            Texture weaponTexture = new Texture(Gdx.files.internal(type.getTexturePath()));
            texture = new TextureRegion(weaponTexture);
        } catch (Exception e) {

            Texture defaultTexture = new Texture(Gdx.files.internal("GUNS/SMG.png"));
            texture = new TextureRegion(defaultTexture);
            Gdx.app.error("Weapon", "Error loading texture: " + e.getMessage());
        }
    }

    public void update(float delta, Vector2 playerPosition, float targetX, float targetY) {

        if (isReloading) {
            reloadTimer += delta;
            if (reloadTimer >= type.getReloadTime()) {
                completeReload();
            }
        }

        if (shootTimer > 0) {
            shootTimer -= delta;
        }


        updateRotation(playerPosition, targetX, targetY);


        updatePosition(playerPosition);


        for (int i = bullets.size - 1; i >= 0; i--) {
            try {
                Bullet bullet = bullets.get(i);
                bullet.update(delta);


                if (bullet.isOutOfBounds()) {
                    bullets.removeIndex(i);
                }
            } catch (Exception e) {

                bullets.removeIndex(i);
                Gdx.app.error("Weapon", "Error updating bullet: " + e.getMessage());
            }
        }
    }


    private void updateRotation(Vector2 playerPosition, float targetX, float targetY) {

        float dx = targetX - playerPosition.x;
        float dy = targetY - playerPosition.y;
        rotation = (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    private void updatePosition(Vector2 playerPosition) {

        float radians = (float) Math.toRadians(rotation);
        float offsetX = WEAPON_OFFSET_X * (float) Math.cos(radians) - WEAPON_OFFSET_Y * (float) Math.sin(radians);
        float offsetY = WEAPON_OFFSET_X * (float) Math.sin(radians) + WEAPON_OFFSET_Y * (float) Math.cos(radians);

        position.x = playerPosition.x + offsetX;
        position.y = playerPosition.y + offsetY;
    }

    public void render(SpriteBatch batch) {

        for (int i = 0; i < bullets.size; i++) {
            try {
                Bullet bullet = bullets.get(i);
                bullet.render(batch);
            } catch (Exception e) {
                Gdx.app.error("Weapon", "Error rendering bullet: " + e.getMessage());
            }
        }


        batch.draw(texture,
            position.x - WEAPON_WIDTH/2, position.y - WEAPON_HEIGHT/2,
            WEAPON_WIDTH/2, WEAPON_HEIGHT/2,
            WEAPON_WIDTH, WEAPON_HEIGHT,
            1, 1, rotation);
    }

    public boolean shoot(Vector2 playerPosition, float targetX, float targetY, boolean infiniteShootingEnabled) {

        if (!infiniteShootingEnabled && isReloading) {
            lastShootAttemptFailed = true;
            shootFailReason = "Reloading";
            return false;
        }


        if (shootTimer > 0) {
            lastShootAttemptFailed = true;
            shootFailReason = "Cooldown";
            return false;
        }


        if (!infiniteShootingEnabled && currentAmmo <= 0) {
            lastShootAttemptFailed = true;
            shootFailReason = "No ammo";
            startReload();
            return false;
        }


        shootTimer = SHOOT_DELAY;
        lastShootAttemptFailed = false;


        if (!infiniteShootingEnabled) {
            currentAmmo--;
        }


        float dx = targetX - position.x;
        float dy = targetY - position.y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length < 0.0001f) {
            dx = 1;
            dy = 0;
        } else {
            dx /= length;
            dy /= length;
        }


        int totalProjectiles = getTotalProjectileCount();
        for (int i = 0; i < totalProjectiles; i++) {
            float spreadAngle = 0;
            if (totalProjectiles > 1) {

                spreadAngle = (i - (totalProjectiles - 1) / 2f) * 10;
            }

            float radians = (float) Math.toRadians(spreadAngle);
            float spreadDx = dx * (float) Math.cos(radians) - dy * (float) Math.sin(radians);
            float spreadDy = dx * (float) Math.sin(radians) + dy * (float) Math.cos(radians);

            try {
                Bullet bullet = new Bullet(position.x, position.y, spreadDx, spreadDy, type.getDamage());
                bullets.add(bullet);
            } catch (Exception e) {
                System.err.println("Error creating bullet: " + e.getMessage());
            }
        }


        if (!infiniteShootingEnabled && currentAmmo <= 0) {
            startReload();
        }

        return true;
    }


    public boolean shoot(Vector2 playerPosition, float targetX, float targetY) {

        return shoot(playerPosition, targetX, targetY, false);
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

    public void addAmmo(int amount) {
        currentAmmo = Math.min(currentAmmo + amount, getTotalMaxAmmo());
    }

    public void dispose() {
        if (texture != null && texture.getTexture() != null) {
            texture.getTexture().dispose();
        }
    }

    public void increaseProjectileCount(int amount) {



        if (additionalProjectileCount == 0) {

            additionalProjectileCount = amount;
        } else {

            additionalProjectileCount += amount;
        }

        System.out.println("Increased projectile count by " + amount +
            ". Total projectiles: " + getTotalProjectileCount());
    }

        public void increaseMaxAmmo(int amount) {


        if (additionalMaxAmmo == 0) {

            additionalMaxAmmo = amount;
        } else {

            additionalMaxAmmo += amount;
        }


        if (currentAmmo < getTotalMaxAmmo()) {
            currentAmmo += amount;
        }


        currentAmmo = Math.min(currentAmmo, getTotalMaxAmmo());

        System.out.println("Increased max ammo by " + amount +
            ". New max ammo: " + getTotalMaxAmmo() +
            ", Current ammo: " + currentAmmo);
    }

        public int getTotalProjectileCount() {
        return type.getProjectileCount() + additionalProjectileCount;
    }

        public int getTotalMaxAmmo() {
        return type.getMaxAmmo() + additionalMaxAmmo;
    }

    public void refillAmmo() {
        currentAmmo = getTotalMaxAmmo();
    }
}
