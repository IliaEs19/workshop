package com.tilldawn.Models.Hero;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum AbilityType {
    VITALITY(
        "VITALITY",
        "افزایش ماکسیمم HP به اندازه یک واحد",
        "Increases maximum health points by one unit.",
        "heroes/character4.png",
        AbilityCategory.DEFENSE
    ),

    DAMAGER(
        "DAMAGER",
        "افزایش 25 درصدی میزان دمیج سلاح به مدت 10 ثانیه",
        "Increases weapon damage by 25% for 10 seconds.",
        "heroes/character4.png",
        AbilityCategory.ATTACK
    ),

    PROCREASE(
        "PROCREASE",
        "افزایش یک واحدی Projectile سلاح",
        "Increases weapon projectile count by one unit.",
        "heroes/character4.png",
        AbilityCategory.ATTACK
    ),

    AMOCREASE(
        "AMOCREASE",
        "افزایش 5 واحدی حداکثر تعداد تیرهای سلاح",
        "Increases maximum weapon ammo by 5 units.",
        "heroes/character4.png",
        AbilityCategory.UTILITY
    ),

    SPEEDY(
        "SPEEDY",
        "2 برابر شدن سرعت حرکت بازیکن به مدت 10 ثانیه",
        "Doubles player movement speed for 10 seconds.",
        "heroes/character4.png",
        AbilityCategory.MOVEMENT
    );

    public enum AbilityCategory {
        ATTACK(new Color(0.9f, 0.3f, 0.3f, 1f)),
        DEFENSE(new Color(0.3f, 0.7f, 0.9f, 1f)),
        MOVEMENT(new Color(0.3f, 0.9f, 0.3f, 1f)),
        UTILITY(new Color(0.9f, 0.7f, 0.3f, 1f));

        private final Color color;

        AbilityCategory(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

    private final String name;
    private final String persianDescription;
    private final String englishDescription;
    private final String texturePath;
    private final AbilityCategory category;
    private TextureRegion textureRegion;

    AbilityType(String name, String persianDescription, String englishDescription,
                String texturePath, AbilityCategory category) {
        this.name = name;
        this.persianDescription = persianDescription;
        this.englishDescription = englishDescription;
        this.texturePath = texturePath;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getPersianDescription() {
        return persianDescription;
    }

    public String getEnglishDescription() {
        return englishDescription;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public AbilityCategory getCategory() {
        return category;
    }

    public Color getCategoryColor() {
        return category.getColor();
    }

    public TextureRegion getTextureRegion() {
        if (textureRegion == null) {
            try {
                Texture texture = new Texture(texturePath);
                texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                textureRegion = new TextureRegion(texture);
            } catch (Exception e) {
                System.err.println("Error loading texture for ability " + name + ": " + e.getMessage());
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
        for (AbilityType ability : values()) {
            ability.disposeTexture();
        }
    }
}
