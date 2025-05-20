package com.tilldawn.Models.Enemy;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum EnemyType {
    TREE("Tree", 0, 0, "enemies/tree.png", false),
    TENTACLE_MONSTER("Tentacle Monster", 25, 1, "enemies/tentacle_monster.png", false),
    EYEBAT("Eyebat", 50, 5, "enemies/eyebat.png", true),
    ELDER("Elder", 400, 10, "enemies/elder.png", true);

    private final String name;
    private final int maxHealth;
    private final int damage;
    private final String texturePath;
    private final boolean canShoot;
    private TextureRegion textureRegion;

    EnemyType(String name, int maxHealth, int damage, String texturePath, boolean canShoot) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.damage = damage;
        this.texturePath = texturePath;
        this.canShoot = canShoot;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getDamage() {
        return damage;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public boolean canShoot() {
        return canShoot;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public static void loadTextures() {
        // این متد در زمان شروع بازی فراخوانی می‌شود تا تکسچرهای دشمن‌ها را لود کند
        for (EnemyType type : values()) {
            try {
                Texture texture = new Texture(type.getTexturePath());
                type.setTextureRegion(new TextureRegion(texture));
            } catch (Exception e) {
                System.err.println("Failed to load texture for enemy: " + type.getName());
            }
        }
    }

    public static void disposeTextures() {
        // این متد در زمان پایان بازی فراخوانی می‌شود تا منابع را آزاد کند
        for (EnemyType type : values()) {
            if (type.textureRegion != null && type.textureRegion.getTexture() != null) {
                type.textureRegion.getTexture().dispose();
            }
        }
    }
}
