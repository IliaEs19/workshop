package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
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
    private CheckBox showPasswordCheckbox;
    private SelectBox<String> securityQuestionSelect;
    private TextField securityAnswer;
    private TextButton register;
    private TextButton guestButton;
    private TextButton backButton;

    private static final String[] SECURITY_QUESTIONS = {
        "What was the name of your first pet?",
        "What is your mother's maiden name?",
        "What was the name of your elementary school?",
        "In what city were you born?",
        "What was your childhood nickname?"
    };

    private static final float FIELD_WIDTH = 450;
    private static final float FIELD_HEIGHT = 90;
    private static final float LABEL_SCALE = 1.0f;
    private static final float TITLE_SCALE = 1.8f;
    private static final float BUTTON_WIDTH = 465;
    private static final float BUTTON_HEIGHT = 120;
    private static final float PADDING = 15;

    public RegisterMenu(RegisterMenuController controller, Skin skin) {
        this.controller = controller;

        menuTitle = new Label("REGISTER ACCOUNT", skin);
        menuTitle.setFontScale(TITLE_SCALE);
        menuTitle.setColor(Color.CYAN);

        this.userName = new TextField("", skin);
        this.userName.setMessageText("Enter your username...");

        this.password = new TextField("", skin);
        this.password.setMessageText("Enter your password...");
        this.password.setPasswordMode(true);
        this.password.setPasswordCharacter('*');

        this.showPasswordCheckbox = new CheckBox(" Show Password", skin);
        this.showPasswordCheckbox.setChecked(false);
        this.showPasswordCheckbox.getLabel().setColor(new Color(0.8f, 0.8f, 0.8f, 1f));
        this.showPasswordCheckbox.getLabel().setFontScale(0.8f);
        this.showPasswordCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                password.setPasswordMode(!showPasswordCheckbox.isChecked());
            }
        });

        this.securityQuestionSelect = new SelectBox<>(skin);
        Array<String> questions = new Array<>(SECURITY_QUESTIONS);
        this.securityQuestionSelect.setItems(questions);
        this.securityQuestionSelect.setAlignment(Align.center);

        this.securityAnswer = new TextField("", skin);
        this.securityAnswer.setMessageText("Enter your answer...");

        this.register = new TextButton("REGISTER", skin);
        this.backButton = new TextButton("BACK", skin);
        this.guestButton = new TextButton("PLAY AS GUEST", skin);
        this.table = new Table();

        TextField.TextFieldStyle style = this.userName.getStyle();
        style.font.getData().setScale(1.2f);

        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        table.setFillParent(true);
        table.center();

        table.pad(70);

        table.add(menuTitle).colspan(2).padBottom(60);

        table.row().pad(PADDING, 70, PADDING, 100);
        Label userLabel = new Label("USERNAME:", GameAssetManager.getGameAssetManager().getSkin());
        userLabel.setColor(Color.CYAN);
        userLabel.setFontScale(LABEL_SCALE);
        table.add(userLabel).width(200).right().padRight(30);
        table.add(userName).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();

        table.row().pad(PADDING, 70, PADDING, 100);
        Label passLabel = new Label("PASSWORD:", GameAssetManager.getGameAssetManager().getSkin());
        passLabel.setColor(Color.CYAN);
        passLabel.setFontScale(LABEL_SCALE);
        table.add(passLabel).width(200).right().padRight(30);

        Table passwordTable = new Table();
        passwordTable.add(password).width(FIELD_WIDTH).height(FIELD_HEIGHT);
        passwordTable.row().pad(5, 0, 0, 0);
        passwordTable.add(showPasswordCheckbox).left();

        table.add(passwordTable).left();

        table.row().pad(PADDING, 70, PADDING, 100);
        Label questionLabel = new Label("SECURITY QUESTION:", GameAssetManager.getGameAssetManager().getSkin());
        questionLabel.setColor(Color.CYAN);
        questionLabel.setFontScale(LABEL_SCALE);
        table.add(questionLabel).width(200).right().padRight(30);
        table.add(securityQuestionSelect).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();

        table.row().pad(PADDING, 70, PADDING * 2, 100);
        Label answerLabel = new Label("YOUR ANSWER:", GameAssetManager.getGameAssetManager().getSkin());
        answerLabel.setColor(Color.CYAN);
        answerLabel.setFontScale(LABEL_SCALE);
        table.add(answerLabel).width(200).right().padRight(30);
        table.add(securityAnswer).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();

        table.row().pad(PADDING * 2, 0, PADDING * 2, 0);
        register.setColor(new Color(0.2f, 0.6f, 0.9f, 1f));

        register.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                register.setColor(new Color(0.3f, 0.7f, 1f, 1f));
                register.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                register.setColor(new Color(0.2f, 0.6f, 0.9f, 1f));
                register.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });

        table.add(register).colspan(2).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);

        table.row();
        guestButton.setColor(new Color(0.3f, 0.7f, 0.7f, 1f));

        guestButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                guestButton.setColor(new Color(0.4f, 0.8f, 0.8f, 1f));
                guestButton.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                guestButton.setColor(new Color(0.3f, 0.7f, 0.7f, 1f));
                guestButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });

        table.add(guestButton).colspan(2).width(BUTTON_WIDTH + 80).height(BUTTON_HEIGHT - 20).padBottom(PADDING * 2);

        table.row().pad(PADDING, 0, PADDING, 0);
        Label separator2 = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        separator2.setColor(Color.CYAN);
        table.add(separator2).colspan(2).width(500).height(2).pad(PADDING * 2);

        table.row();
        backButton.setColor(new Color(0.9f, 0.3f, 0.3f, 1f));

        backButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                backButton.setColor(new Color(1f, 0.4f, 0.4f, 1f));
                backButton.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                backButton.setColor(new Color(0.9f, 0.3f, 0.3f, 1f));
                backButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });

        table.add(backButton).colspan(2).width(BUTTON_WIDTH - 240).height(85).padBottom(PADDING * 2);

        stage.addActor(table);

        try {
            securityQuestionSelect.getStyle().font.getData().setScale(1.2f);
        } catch (Exception e) {
            Gdx.app.log("RegisterMenu", "Could not set font scale for SelectBox");
        }
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {
        stage.getViewport().update(i, i1, true);
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

    public TextButton getRegister() {
        return register;
    }

    public TextField getUserName() {
        return userName;
    }

    public TextField getPassword() {
        return password;
    }

    public CheckBox getShowPasswordCheckbox() {
        return showPasswordCheckbox;
    }

    public SelectBox<String> getSecurityQuestionSelect() {
        return securityQuestionSelect;
    }

    public TextField getSecurityAnswer() {
        return securityAnswer;
    }

    public TextButton getGuestButton() {
        return guestButton;
    }

    public Stage getStage() {
        return stage;
    }

    public TextButton getBackButton() {
        return backButton;
    }
}
