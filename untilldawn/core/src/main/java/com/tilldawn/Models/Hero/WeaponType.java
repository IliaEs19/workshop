package com.tilldawn.Models.Hero;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public enum WeaponType {
    REVOLVER("Revolver", 20, 1, 1.0f, 6,
        "A powerful handgun with high accuracy and damage"),

    SHOTGUN("Shotgun", 10, 4, 1.0f, 2,
        "Shotgun with spread projectiles for close combat"),

    SMGS_DUAL("SMGs Dual", 8, 1, 2.0f, 24,
        "Dual submachine guns with high fire rate and ammo capacity");

    private final String name;
    private final int damage;
    private final int projectileCount;
    private final float reloadTime;
    private final int maxAmmo;
    private final String englishDescription;
    private TextureRegion textureRegion;

    WeaponType(String name, int damage, int projectileCount, float reloadTime, int maxAmmo,
               String englishDescription) {
        this.name = name;
        this.damage = damage;
        this.projectileCount = projectileCount;
        this.reloadTime = reloadTime;
        this.maxAmmo = maxAmmo;
        this.englishDescription = englishDescription;
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

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    public static void loadTextures(Texture weaponsTexture) {
        // این متد برای لود کردن تکسچر سلاح‌ها از اسپرایت شیت استفاده می‌شود
        // مثال:
        // REVOLVER.setTextureRegion(new TextureRegion(weaponsTexture, 0, 0, 64, 64));
        // SHOTGUN.setTextureRegion(new TextureRegion(weaponsTexture, 64, 0, 64, 64));
        // SMGS_DUAL.setTextureRegion(new TextureRegion(weaponsTexture, 128, 0, 64, 64));
    }

    public static void disposeAllTextures() {
        for (WeaponType weapon : values()) {
            if (weapon.textureRegion != null && weapon.textureRegion.getTexture() != null) {
                weapon.textureRegion.getTexture().dispose();
            }
        }
    }
}
