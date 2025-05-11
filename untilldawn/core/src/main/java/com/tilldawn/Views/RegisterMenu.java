package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.RegisterMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;

public class RegisterMenu implements Screen {
    private Stage stage;
    private final Label menuTitle;
    public Table table;
    private final RegisterMenuController controller;

    private TextField userName;
    private TextField password;
    private TextButton register;
    private TextButton guestButton;



    public RegisterMenu(RegisterMenuController controller, Skin skin){
        this.controller = controller;
        menuTitle = new Label("RegisterMenu", skin);
        this.userName = new TextField("your username...",skin);
        this.password = new TextField("your password...",skin);
        this.register = new TextButton("Register",skin);
        guestButton = new TextButton("play as a guest", skin);
        this.table = new Table();
        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();
        table.add(menuTitle).padBottom(80);
        table.row().pad(10, 0, 10, 0);
        table.add(userName).width(450).height(100);
        table.row().pad(10, 0, 10, 0);
        table.add(password).width(450).height(100);
        table.row().pad(10, 0, 20, 0);
        table.add(register);

        Label separator = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        separator.setColor(0.7f, 0.7f, 0.7f, 0.5f);
        table.row().pad(20, 0, 20, 0);
        table.add(separator).width(400).height(2).fillX();

        guestButton.setColor(0.8f, 0.9f, 1f, 1f);

        guestButton.addAction(Actions.sequence(
            Actions.scaleTo(1, 1),
            Actions.forever(Actions.sequence(
                Actions.scaleTo(1.05f, 1.05f, 0.5f),
                Actions.scaleTo(1f, 1f, 0.5f)
            ))
        ));

        guestButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                guestButton.clearActions();
                guestButton.setColor(0.9f, 1f, 0.9f, 1f);
                guestButton.addAction(Actions.scaleTo(1.1f, 1.1f, 0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                guestButton.clearActions();
                guestButton.setColor(0.8f, 0.9f, 1f, 1f);
                guestButton.addAction(Actions.sequence(
                    Actions.scaleTo(1f, 1f, 0.2f),
                    Actions.forever(Actions.sequence(
                        Actions.scaleTo(1.05f, 1.05f, 0.5f),
                        Actions.scaleTo(1f, 1f, 0.5f)
                    ))
                ));
            }
        });

        table.row().pad(10, 0, 10, 0);
        table.add(guestButton).width(450).height(80);
        stage.addActor(table);
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
//        controller.handleRegisterMenuButtons();
    }

    @Override
    public void resize(int i, int i1) {

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

    public TextButton getRegister() {
        return register;
    }

    public TextField getUserName() {
        return userName;
    }

    public TextField getPassword() {
        return password;
    }

    public TextButton getGuestButton() {
        return guestButton;
    }

    public Stage getStage() {
        return stage;
    }
}
