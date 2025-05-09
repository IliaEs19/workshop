package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class GameAssetManager {
    private static GameAssetManager gameAssetManager ;
    private Skin skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));

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
}
