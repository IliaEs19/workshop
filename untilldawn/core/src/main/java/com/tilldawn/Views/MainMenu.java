package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Main;

import java.util.ArrayList;

public class MainMenu implements Screen {
    private Stage stage;
    private final Label menuTitle;
    public Table table;
    private final MainMenuController controller;

    private ArrayList<TextButton> menus = new ArrayList<>();

    public MainMenu(MainMenuController controller, Skin skin) {
        this.controller = controller;
        menus.add(new TextButton("RegisterMenu",skin));
        menus.add(new TextButton("LoginMenu",skin));
        menus.add(new TextButton("ProfileMenu",skin));
        menus.add(new TextButton("SettingMenu",skin));
        menus.add(new TextButton("PreGameMenu",skin));
        menus.add(new TextButton("TalentMenu",skin));
        this.menuTitle = new Label("MainMenu", skin);
        this.table = new Table();

        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();
        menuTitle.setColor(Color.GREEN);
        table.add(menuTitle).padBottom(100);
        table.row().pad(10, 0 , 10 , 0);
        table.row().pad(10, 0 , 10 , 0);
        for(TextButton textButton : menus){
            table.add(textButton);
            table.row().pad(10, 0 , 10 , 0);
        }
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        controller.handleMainMenuButtons();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public ArrayList<TextButton> getMenus() {
        return menus;
    }
}
