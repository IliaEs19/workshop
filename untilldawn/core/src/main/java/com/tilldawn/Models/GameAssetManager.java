package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class GameAssetManager {
    private static GameAssetManager gameAssetManager ;
    private Skin skin ;
       // = new Skin(Gdx.files.internal("skin1/pixthulhu-ui.json"));

    private Texture mainMenuBackground; // تصویر پس‌زمینه منوی اصلی

    private GameAssetManager() {
        loadAssets(); // بارگذاری منابع در یک متد جداگانه
    }

    private void loadAssets() {
        skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        mainMenuBackground = new Texture(Gdx.files.internal("backgrounds/MainMenu.jpg"));
    }


    public static GameAssetManager getGameAssetManager(){
        if(gameAssetManager == null)
            gameAssetManager = new GameAssetManager();
        return gameAssetManager;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public Texture getMainMenuBackground() {
        return mainMenuBackground;
    }

    public static void resetGameAssetManager() {
        if (gameAssetManager != null) {
            gameAssetManager.dispose(); // آزادسازی منابع قبلی
            gameAssetManager = null;
        }
        // نمونه جدید در فراخوانی بعدی getGameAssetManager ایجاد خواهد شد
    }

    // آزادسازی منابع
    public void dispose() {
        if (skin != null) {
            skin.dispose();
        }
        skin.dispose();
        if (mainMenuBackground != null) {
            mainMenuBackground.dispose();
        }
    }
}
