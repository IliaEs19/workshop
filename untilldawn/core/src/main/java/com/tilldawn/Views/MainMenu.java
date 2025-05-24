package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.SoundManager;
import com.tilldawn.Models.User;

import java.util.ArrayList;

import static com.tilldawn.Models.SoundManager.addSoundToButton;

public class MainMenu implements Screen {
    private Stage stage;
    private final Label menuTitle;
    public Table table;
    private final MainMenuController controller;

    private Texture backgroundTexture;
    private SpriteBatch batch;

    private Table userInfoTable;
    private Image userAvatar;
    private Label usernameTitleLabel;
    private Label usernameLabel;
    private Label scoreTitleLabel;
    private Label scoreLabel;
    private Label gamePlayedTitleLabel;
    private Label gamePlayedLabel;
    private TextButton logoutButton;
    private TextButton saveGame;

    private Array<Texture> generatedTextures = new Array<>();
    private ArrayList<TextButton> menus = new ArrayList<>();
    private TextButton exit;


    private final Color buttonColor = new Color(0.8f, 0.9f, 1f, 1f);
    private final Color hoverColor = new Color(0.9f, 1f, 0.9f, 1f);

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
        menus.add(new TextButton("ScoreBoard", skin));
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
        addSoundToButton(exit);
        table.add(exit).width(420).height(100).padLeft(1100);
        stage.addActor(table);


        createUserInfoPanel();
    }


    private void createUserInfoPanel() {
        Skin skin = GameAssetManager.getGameAssetManager().getSkin();


        userInfoTable = new Table();

        userInfoTable.setSize(250, 220);
        userInfoTable.setPosition(20, Gdx.graphics.getHeight() - 240);


        usernameTitleLabel = new Label("username:", skin);
        usernameTitleLabel.setColor(Color.CYAN);
        usernameTitleLabel.setFontScale(1.2f);

        usernameLabel = new Label("", skin);
        usernameLabel.setColor(Color.WHITE);
        usernameLabel.setFontScale(1.2f);

        scoreTitleLabel = new Label("score:", skin);
        scoreTitleLabel.setColor(Color.CYAN);
        scoreTitleLabel.setFontScale(1.2f);

        scoreLabel = new Label("", skin);
        scoreLabel.setColor(Color.GOLD);
        scoreLabel.setFontScale(1.2f);

        gamePlayedTitleLabel = new Label("games played:", skin);
        gamePlayedTitleLabel.setColor(Color.CYAN);
        gamePlayedTitleLabel.setFontScale(1.2f);

        gamePlayedLabel = new Label("", skin);
        gamePlayedLabel.setColor(Color.WHITE);
        gamePlayedLabel.setFontScale(1.2f);


        logoutButton = new TextButton("logout", skin);
        logoutButton.setColor(Color.FIREBRICK);
        logoutButton.getLabel().setFontScale(1.0f);
        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                SaveData.setCurrentUser(null);

                updateUserInfoPanel();
            }
        });

        saveGame = new TextButton("savegame",skin);
        logoutButton.setColor(Color.FIREBRICK);
        logoutButton.getLabel().setFontScale(1.0f);



        Table infoTable = new Table();
        infoTable.add(usernameTitleLabel).padRight(10);
        infoTable.add(usernameLabel).left().row();
        infoTable.add(scoreTitleLabel).padRight(10);
        infoTable.add(scoreLabel).left().row();
        infoTable.add(gamePlayedTitleLabel).padRight(10);
        infoTable.add(gamePlayedLabel).left();


        userAvatar = new Image();

        userInfoTable.add(userAvatar).size(60, 60).pad(10).padTop(150).row();
        userInfoTable.add(infoTable).pad(10).row();
        userInfoTable.add(logoutButton).size(180, 60).pad(10).row();
        userInfoTable.add(saveGame).size(255, 65).pad(10).row();



        stage.addActor(userInfoTable);


        updateUserInfoPanel();
    }


    private Drawable createCircleWithBorderDrawable(int size, Color fillColor, Color borderColor, int borderWidth) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(size, size, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);


        pixmap.setColor(borderColor);
        pixmap.fillCircle(size / 2, size / 2, size / 2 - 1);


        pixmap.setColor(fillColor);
        pixmap.fillCircle(size / 2, size / 2, size / 2 - borderWidth - 1);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();


        generatedTextures.add(texture);

        return new TextureRegionDrawable(new TextureRegion(texture));
    }

        private void updateUserInfoPanel() {
        if (SaveData.getCurrentUser() != null) {
            User currentUser = SaveData.getCurrentUser();


            userInfoTable.setVisible(true);
            usernameLabel.setText(currentUser.getUserName());
            scoreLabel.setText(String.valueOf(currentUser.getHighScore()));
            gamePlayedLabel.setText(String.valueOf(currentUser.getTotalGamesPlayed()));


            if (currentUser.getAvatarPath() != null && !currentUser.getAvatarPath().isEmpty()) {
                try {
                    Texture avatarTexture = new Texture(Gdx.files.internal(currentUser.getAvatarPath()));
                    userAvatar.setDrawable(new TextureRegionDrawable(new TextureRegion(avatarTexture)));
                } catch (Exception e) {

                    userAvatar.setDrawable(createCircleWithBorderDrawable(64,
                        new Color(0.4f, 0.6f, 0.9f, 1f),
                        new Color(0.2f, 0.3f, 0.8f, 1f),
                        3));
                }
            } else {

                userAvatar.setDrawable(createCircleWithBorderDrawable(64,
                    new Color(0.4f, 0.6f, 0.9f, 1f),
                    new Color(0.2f, 0.3f, 0.8f, 1f),
                    3));
            }
        } else {

            userInfoTable.setVisible(false);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        Main.getBatch().end();

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();


        updateUserInfoPanel();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        controller.handleMainMenuButtons();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        if (userInfoTable != null) {
            userInfoTable.setPosition(20, height - 240);
        }
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


        for (Texture texture : generatedTextures) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }
    public ArrayList<TextButton> getMenus() {
        return menus;
    }

    public TextButton getExit() {
        return exit;
    }
}
