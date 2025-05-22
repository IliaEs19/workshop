package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;

import java.util.ArrayList;

public class MainMenu implements Screen {
    private Stage stage;
    private final Label menuTitle;
    public Table table;
    private final MainMenuController controller;

    private Texture backgroundTexture; // تصویر پس‌زمینه
    private SpriteBatch batch; // برای رندر کردن تصویر پس‌زمینه



    private ArrayList<TextButton> menus = new ArrayList<>();
    private TextButton exit;

    // رنگ یکسان برای همه دکمه‌ها
    private final Color buttonColor = new Color(0.8f, 0.9f, 1f, 1f);  // آبی روشن
    private final Color hoverColor = new Color(0.9f, 1f, 0.9f, 1f);   // سبز روشن

    public MainMenu(MainMenuController controller, Skin skin) {

        this.backgroundTexture = GameAssetManager.getGameAssetManager().getMainMenuBackground();
        this.batch = new SpriteBatch();

        this.controller = controller;
        menus.add(new TextButton("RegisterMenu", skin));
        menus.add(new TextButton("LoginMenu", skin));
        menus.add(new TextButton("ProfileMenu", skin));
        menus.add(new TextButton("SettingMenu", skin));
        menus.add(new TextButton("PreGameMenu", skin));
        menus.add(new TextButton("TalentMenu", skin));
        menus.add(new TextButton("ScoreBoard",skin));
        this.exit = new TextButton("Exit", skin);
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
        menuTitle.setFontScale(1.5f);
        //table.add(menuTitle).padBottom(100).padLeft(900);
        table.row().pad(15, 0, 15, 0);

        for (TextButton button : menus) {
            button.setColor(buttonColor);

            button.addAction(Actions.sequence(
                Actions.scaleTo(1, 1),
                Actions.forever(Actions.sequence(
                    Actions.scaleTo(1.05f, 1.05f, 0.5f),
                    Actions.scaleTo(1f, 1f, 0.5f)
                ))
            ));

            button.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    button.clearActions();
                    button.setColor(hoverColor);
                    button.addAction(Actions.scaleTo(1.1f, 1.1f, 0.2f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    button.clearActions();
                    button.setColor(buttonColor);
                    button.addAction(Actions.sequence(
                        Actions.scaleTo(1f, 1f, 0.2f),
                        Actions.forever(Actions.sequence(
                            Actions.scaleTo(1.05f, 1.05f, 0.5f),
                            Actions.scaleTo(1f, 1f, 0.5f)
                        ))
                    ));
                }
            });

            table.add(button).width(430).height(100).padLeft(1100).padBottom(0);
            table.row().pad(15, 0, 15, 0);
        }

        exit.setColor(buttonColor);

        exit.addAction(Actions.sequence(
            Actions.scaleTo(1, 1),
            Actions.forever(Actions.sequence(
                Actions.scaleTo(1.05f, 1.05f, 0.5f),
                Actions.scaleTo(1f, 1f, 0.5f)
            ))
        ));

        exit.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                exit.clearActions();
                exit.setColor(hoverColor);
                exit.addAction(Actions.scaleTo(1.1f, 1.1f, 0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                exit.clearActions();
                exit.setColor(buttonColor);
                exit.addAction(Actions.sequence(
                    Actions.scaleTo(1f, 1f, 0.2f),
                    Actions.forever(Actions.sequence(
                        Actions.scaleTo(1.05f, 1.05f, 0.5f),
                        Actions.scaleTo(1f, 1f, 0.5f)
                    ))
                ));
            }
        });

        table.add(exit).width(420).height(100).padLeft(1100);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        Main.getBatch().end();

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        controller.handleMainMenuButtons();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        if (stage != null) {
            stage.dispose();
        }
    }

    public ArrayList<TextButton> getMenus() {
        return menus;
    }

    public TextButton getExit() {
        return exit;
    }
}
