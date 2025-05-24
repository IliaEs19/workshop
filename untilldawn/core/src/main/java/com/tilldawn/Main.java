package com.tilldawn;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.GameCursor;
import com.tilldawn.Models.MusicManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Views.MainMenu;


public class Main extends Game {

    private static Main main;
    private static SpriteBatch batch;

    @Override
    public void create() {
        GameAssetManager.resetGameAssetManager();
        MusicManager.getInstance().playDefaultMusic();

        main = this;
        batch = new SpriteBatch();
        GameCursor.initialize();
        SaveData.getInstance();
        main.setScreen(new MainMenu(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        MusicManager.getInstance().dispose();
        GameAssetManager.getGameAssetManager().dispose();
        GameCursor.dispose();
        batch.dispose();
    }

    public static Main getMain() {
        return main;
    }

    public static void setMain(Main main) {
        Main.main = main;
    }

    public static SpriteBatch getBatch() {
        return batch;
    }

    public static void setBatch(SpriteBatch batch) {
        Main.batch = batch;
    }
}
