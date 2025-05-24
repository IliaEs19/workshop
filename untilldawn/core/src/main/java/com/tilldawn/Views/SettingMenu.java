package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Controllers.SettingMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.GameSettings;
import com.tilldawn.Models.MusicManager;

public class SettingMenu implements Screen {
    private Stage stage;
    private Table mainTable;
    private final SettingMenuController controller;
    private Image backgroundImage;


    private CheckBox musicEnabledCheckbox;
    private Slider volumeSlider;
    private TextButton mainThemeButton;
    private TextButton secondaryThemeButton;
    private TextButton backButton;
    private ProgressBar volumeBar;
    private Label volumeValueLabel;


    private static final float BUTTON_WIDTH = 700;
    private static final float BUTTON_HEIGHT = 100;
    private static final float PADDING = 20;
    private static final float SLIDER_WIDTH = 300;
    private static final Color TITLE_COLOR = new Color(0.2f, 0.8f, 1f, 1f);
    private static final Color SECTION_TITLE_COLOR = new Color(0.9f, 0.9f, 0.3f, 1f);
    private static final Color BUTTON_COLOR = new Color(0.2f, 0.6f, 0.9f, 1f);
    private static final Color BUTTON_HOVER_COLOR = new Color(0.3f, 0.7f, 1f, 1f);
    private static final Color ACTIVE_BUTTON_COLOR = new Color(0.3f, 0.8f, 0.3f, 1f);
    private static final Color BACK_BUTTON_COLOR = new Color(0.8f, 0.3f, 0.3f, 1f);

    public SettingMenu(SettingMenuController controller, Skin skin) {
        this.controller = controller;
        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);












        mainTable = new Table();
        mainTable.setFillParent(true);


        Table panelTable = new Table();
        panelTable.setBackground(createPanelBackground());
        panelTable.pad(PADDING * 5);


        Label titleLabel = new Label("SETTINGS", GameAssetManager.getGameAssetManager().getSkin(), "default");
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(TITLE_COLOR);
        titleLabel.setAlignment(Align.center);
        panelTable.add(titleLabel).colspan(2).pad(PADDING).expandX().fillX().row();


        Image titleSeparator = createSeparator(2, TITLE_COLOR);
        panelTable.add(titleSeparator).colspan(2).pad(PADDING / 2).expandX().fillX().row();


        Label musicSettingsLabel = new Label("MUSIC SETTINGS", GameAssetManager.getGameAssetManager().getSkin());
        musicSettingsLabel.setFontScale(1.5f);
        musicSettingsLabel.setColor(SECTION_TITLE_COLOR);
        panelTable.add(musicSettingsLabel).colspan(2).pad(PADDING).expandX().fillX().row();


        musicEnabledCheckbox = new CheckBox(" Enable Music", GameAssetManager.getGameAssetManager().getSkin());
        musicEnabledCheckbox.setChecked(MusicManager.getInstance().isMusicEnabled());
        musicEnabledCheckbox.getLabel().setFontScale(1.2f);
        panelTable.add(musicEnabledCheckbox).colspan(2).pad(PADDING / 2).left().row();


        Label selectMusicLabel = new Label("SELECT MUSIC:", GameAssetManager.getGameAssetManager().getSkin());
        selectMusicLabel.setFontScale(1.2f);
        panelTable.add(selectMusicLabel).colspan(2).pad(PADDING).left().row();


        Table musicButtonsTable = new Table();


        mainThemeButton = new TextButton("Music 1", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(mainThemeButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);


        if (MusicManager.getInstance().isMainThemePlaying()) {
            mainThemeButton.setColor(ACTIVE_BUTTON_COLOR);
        }


        secondaryThemeButton = new TextButton("Music 2", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(secondaryThemeButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);


        if (MusicManager.getInstance().isSecondaryThemePlaying()) {
            secondaryThemeButton.setColor(ACTIVE_BUTTON_COLOR);
        }

        musicButtonsTable.add(mainThemeButton).width(BUTTON_WIDTH / 2 - 10).height(BUTTON_HEIGHT).pad(5);
        musicButtonsTable.add(secondaryThemeButton).width(BUTTON_WIDTH / 2 - 10).height(BUTTON_HEIGHT).pad(5);

        panelTable.add(musicButtonsTable).colspan(2).pad(PADDING / 2).expandX().fillX().row();


        Label volumeLabel = new Label("VOLUME:", GameAssetManager.getGameAssetManager().getSkin());
        volumeLabel.setFontScale(1.2f);
        panelTable.add(volumeLabel).colspan(2).pad(PADDING).left().row();


        Table volumeControlTable = new Table();


        volumeSlider = new Slider(0, 1, 0.01f, false, GameAssetManager.getGameAssetManager().getSkin());
        volumeSlider.setValue(MusicManager.getInstance().getVolume());
        volumeControlTable.add(volumeSlider).width(SLIDER_WIDTH).padRight(PADDING);


        volumeValueLabel = new Label(Math.round(volumeSlider.getValue() * 100) + "%",
            GameAssetManager.getGameAssetManager().getSkin());
        volumeValueLabel.setFontScale(1.2f);
        volumeControlTable.add(volumeValueLabel).width(50);

        panelTable.add(volumeControlTable).colspan(2).pad(PADDING / 2).expandX().fillX().row();


        volumeBar = new ProgressBar(0, 1, 0.01f, false, GameAssetManager.getGameAssetManager().getSkin());
        volumeBar.setValue(volumeSlider.getValue());
        volumeBar.setAnimateDuration(0.3f);
        panelTable.add(volumeBar).colspan(2).pad(PADDING / 2).width(SLIDER_WIDTH).height(15).row();

        Label controlSettingsLabel = new Label("CONTROL SETTINGS", GameAssetManager.getGameAssetManager().getSkin());
        controlSettingsLabel.setFontScale(1.5f);
        controlSettingsLabel.setColor(SECTION_TITLE_COLOR);
        panelTable.add(controlSettingsLabel).colspan(2).pad(PADDING).expandX().fillX().row();


        Label movementControlLabel = new Label("MOVEMENT CONTROL:", GameAssetManager.getGameAssetManager().getSkin());
        movementControlLabel.setFontScale(1.2f);
        panelTable.add(movementControlLabel).colspan(2).pad(PADDING).left().row();


        Table controlOptionsTable = new Table();


        final TextButton wasdButton = new TextButton("WASD Keys", GameAssetManager.getGameAssetManager().getSkin());
        final TextButton arrowsButton = new TextButton("Arrow Keys", GameAssetManager.getGameAssetManager().getSkin());


        if (controller.getControlType() == GameSettings.ControlType.WASD) {
            wasdButton.setColor(ACTIVE_BUTTON_COLOR);
            arrowsButton.setColor(BUTTON_COLOR);
        } else {
            wasdButton.setColor(BUTTON_COLOR);
            arrowsButton.setColor(ACTIVE_BUTTON_COLOR);
        }


        wasdButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.setControlType(GameSettings.ControlType.WASD);
                wasdButton.setColor(ACTIVE_BUTTON_COLOR);
                arrowsButton.setColor(BUTTON_COLOR);
            }
        });

        arrowsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.setControlType(GameSettings.ControlType.ARROWS);
                wasdButton.setColor(BUTTON_COLOR);
                arrowsButton.setColor(ACTIVE_BUTTON_COLOR);
            }
        });


        styleButton(wasdButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);
        styleButton(arrowsButton, BUTTON_COLOR, BUTTON_HOVER_COLOR);


        controlOptionsTable.add(wasdButton).width(BUTTON_WIDTH / 2 - 10).height(BUTTON_HEIGHT / 1.5f).pad(5);
        controlOptionsTable.add(arrowsButton).width(BUTTON_WIDTH / 2 - 10).height(BUTTON_HEIGHT / 1.5f).pad(5);


        panelTable.add(controlOptionsTable).colspan(2).pad(PADDING / 2).expandX().fillX().row();


        Image controlSeparator = createSeparator(1, new Color(0.5f, 0.5f, 0.5f, 1f));
        panelTable.add(controlSeparator).colspan(2).pad(PADDING).expandX().fillX().row();



        Image bottomSeparator = createSeparator(1, new Color(0.5f, 0.5f, 0.5f, 1f));
        panelTable.add(bottomSeparator).colspan(2).pad(PADDING).expandX().fillX().row();


        backButton = new TextButton("BACK", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(backButton, BACK_BUTTON_COLOR, new Color(0.9f, 0.4f, 0.4f, 1f));
        panelTable.add(backButton).colspan(2).width(BUTTON_WIDTH / 2).height(BUTTON_HEIGHT).pad(PADDING).row();


        mainTable.add(panelTable).width(900).pad(50);


        stage.addActor(mainTable);


        addListeners();


        mainTable.getColor().a = 0;
        mainTable.addAction(Actions.fadeIn(1.0f));
    }

    private void addListeners() {

        musicEnabledCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MusicManager.getInstance().setMusicEnabled(musicEnabledCheckbox.isChecked());
            }
        });


        mainThemeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MusicManager.getInstance().switchToMainTheme();
                updateMusicButtonColors();
            }
        });


        secondaryThemeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MusicManager.getInstance().switchToSecondaryTheme();
                updateMusicButtonColors();
            }
        });


        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                MusicManager.getInstance().setVolume(volume);
                volumeValueLabel.setText(Math.round(volume * 100) + "%");
                volumeBar.setValue(volume);
            }
        });


        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                mainTable.addAction(Actions.sequence(
                    Actions.fadeOut(0.3f),
                    Actions.run(new Runnable() {
                        @Override
                        public void run() {

                            Main.getMain().setScreen(new MainMenu(
                                new MainMenuController(),
                                GameAssetManager.getGameAssetManager().getSkin()));
                        }
                    })
                ));
            }
        });
    }

    private void updateMusicButtonColors() {

        if (MusicManager.getInstance().isMainThemePlaying()) {
            mainThemeButton.setColor(ACTIVE_BUTTON_COLOR);
            secondaryThemeButton.setColor(BUTTON_COLOR);
        } else if (MusicManager.getInstance().isSecondaryThemePlaying()) {
            mainThemeButton.setColor(BUTTON_COLOR);
            secondaryThemeButton.setColor(ACTIVE_BUTTON_COLOR);
        } else {
            mainThemeButton.setColor(BUTTON_COLOR);
            secondaryThemeButton.setColor(BUTTON_COLOR);
        }
    }

        private TextureRegionDrawable createPanelBackground() {
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.8f);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(texture);
    }

        private Image createSeparator(int height, Color color) {
        Pixmap pixmap = new Pixmap(10, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        Image separator = new Image(texture);
        return separator;
    }

        private void styleButton(final TextButton button, final Color normalColor, final Color hoverColor) {
        button.setColor(normalColor);

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.color(hoverColor, 0.2f));
                button.addAction(Actions.scaleTo(1.05f, 1.05f, 0.2f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {

                if ((button == mainThemeButton && MusicManager.getInstance().isMainThemePlaying()) ||
                    (button == secondaryThemeButton && MusicManager.getInstance().isSecondaryThemePlaying())) {
                    button.addAction(Actions.color(ACTIVE_BUTTON_COLOR, 0.2f));
                } else {
                    button.addAction(Actions.color(normalColor, 0.2f));
                }
                button.addAction(Actions.scaleTo(1f, 1f, 0.2f));
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
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

    public CheckBox getMusicEnabledCheckbox() {
        return musicEnabledCheckbox;
    }

    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    public TextButton getMainThemeButton() {
        return mainThemeButton;
    }

    public TextButton getSecondaryThemeButton() {
        return secondaryThemeButton;
    }

    public TextButton getBackButton() {
        return backButton;
    }

    public Stage getStage() {
        return stage;
    }
}
