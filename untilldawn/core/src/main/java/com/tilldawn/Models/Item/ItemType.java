package com.tilldawn.Models.Item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum ItemType {
    HEALTH("Health", "items/health.png"),
    AMMO("Ammo", "items/AMMO.png"),
    SPEED_BOOST("Speed Boost", "items/SPEED.png"),
    DAMAGE_BOOST("Damage Boost", "items/DAMAGE.png"),
    EXPERIENCE("XP", "items/DAMAGE.png"); // آیتم تجربه جدید

    private final String name;
    private final String texturePath;
    private TextureRegion textureRegion;

    ItemType(String name, String texturePath) {
        this.name = name;
        this.texturePath = texturePath;
    }

    public String getName() {
        return name;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public static void loadTextures() {
        for (ItemType type : values()) {
            try {
                Texture texture = new Texture(type.getTexturePath());
                type.setTextureRegion(new TextureRegion(texture));
            } catch (Exception e) {
                System.err.println("Failed to load texture for item: " + type.getName());
            }
        }
    }

    public static void disposeTextures() {
        for (ItemType type : values()) {
            if (type.textureRegion != null && type.textureRegion.getTexture() != null) {
                type.textureRegion.getTexture().dispose();
            }
        }
    }
}
