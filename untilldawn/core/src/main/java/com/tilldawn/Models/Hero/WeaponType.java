package com.tilldawn.Models.Hero;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum WeaponType {
    REVOLVER("Revolver", 20, 1, 1.0f, 6,
        "A powerful handgun with high accuracy and damage","Guns/REVOLVER.png"),


    SHOTGUN("Shotgun", 10, 4, 1.0f, 2,
        "Shotgun with spread projectiles for close combat","Guns/SHOTGUN.png"),

    SMGS_DUAL("SMGs Dual", 8, 1, 2.0f, 24,
        "Dual submachine guns with high fire rate and ammo capacity","Guns/SMG.png");

    private final String name;
    private final int damage;
    private final int projectileCount;
    private final float reloadTime;
    private final int maxAmmo;
    private final String englishDescription;
    private TextureRegion textureRegion;
    private final String texturePath;

    WeaponType(String name, int damage, int projectileCount, float reloadTime, int maxAmmo,
               String englishDescription, String texturePath) {
        this.name = name;
        this.damage = damage;
        this.projectileCount = projectileCount;
        this.reloadTime = reloadTime;
        this.maxAmmo = maxAmmo;
        this.englishDescription = englishDescription;
        this.texturePath = texturePath;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public int getProjectileCount() {
        return projectileCount;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public String getEnglishDescription() {
        return englishDescription;
    }


    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public static void loadTextures(Texture weaponsTexture) {
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

    public static void disposeAllTextures() {
        for (WeaponType weapon : values()) {
            if (weapon.textureRegion != null && weapon.textureRegion.getTexture() != null) {
                weapon.textureRegion.getTexture().dispose();
            }
        }
    }
}
