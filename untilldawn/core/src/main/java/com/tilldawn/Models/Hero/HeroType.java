package com.tilldawn.Models.Hero;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum HeroType {
    SHANA(
        "Shana",
        "A balanced character with moderate speed and health.",
        4,
        4,
        "heroes/character1.png"
    ),

    DIAMOND(
        "Diamond",
        "A slow but durable character with high health.",
        7,
        1,
        "heroes/character2.png"
    ),

    SCARLET(
        "Scarlet",
        "A fast character with low health.",
        3,
        5,
        "heroes/character2.png"
    ),

    LILITH(
        "Lilith",
        "A moderately fast character with good health.",
        5,
        3,
        "heroes/character3.png"
    ),

    DASHER(
        "Dasher",
        "An extremely fast character with very low health.",
        2,
        10,
        "heroes/character4.png"
    );

    private final String name;
    private final String description;
    private final int healthPoints;
    private final int speed;
    private final String texturePath;
    private TextureRegion textureRegion;

    HeroType(String name, String description, int healthPoints, int speed, String texturePath) {
        this.name = name;
        this.description = description;
        this.healthPoints = healthPoints;
        this.speed = speed;
        this.texturePath = texturePath;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public int getSpeed() {
        return speed;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public TextureRegion getTextureRegion() {
        if (textureRegion == null) {
            try {
                Texture texture = new Texture(texturePath);
                textureRegion = new TextureRegion(texture);
            } catch (Exception e) {

                System.err.println("Error loading texture for " + name + ": " + e.getMessage());
            }
        }
        return textureRegion;
    }

    public void disposeTexture() {
        if (textureRegion != null && textureRegion.getTexture() != null) {
            textureRegion.getTexture().dispose();
            textureRegion = null;
        }
    }

    public static void disposeAllTextures() {
        for (HeroType hero : values()) {
            hero.disposeTexture();
        }
    }
}
