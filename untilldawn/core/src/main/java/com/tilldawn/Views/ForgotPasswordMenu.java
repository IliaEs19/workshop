package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.ForgotPasswordMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;

public class ForgotPasswordMenu implements Screen {
    private Stage stage;
    private final Label menuTitle;
    public Table table;
    private final ForgotPasswordMenuController controller;

    private final String username;
    private final String securityQuestion;
    private Label usernameLabel;
    private Label questionLabel;
    private TextField securityAnswer;
    private TextField newPassword;
    private TextField confirmPassword;
    private TextButton resetButton;
    private TextButton backButton;


    private static final float FIELD_WIDTH = 450;
    private static final float FIELD_HEIGHT = 90;
    private static final float LABEL_SCALE = 1.0f;
    private static final float TITLE_SCALE = 1.8f;
    private static final float BUTTON_WIDTH = 465;
    private static final float BUTTON_HEIGHT_SMALL = 100;
    private static final float PADDING = 15;
    private static final float BUTTON_SPACING = 10;

    public ForgotPasswordMenu(ForgotPasswordMenuController controller, Skin skin, String username, String securityQuestion) {
        this.controller = controller;
        this.username = username;
        this.securityQuestion = securityQuestion;


        menuTitle = new Label("RESET PASSWORD", skin);
        menuTitle.setFontScale(TITLE_SCALE);
        menuTitle.setColor(Color.CYAN);


        this.securityAnswer = new TextField("", skin);
        this.securityAnswer.setMessageText("Enter your answer...");

        this.newPassword = new TextField("", skin);
        this.newPassword.setMessageText("Enter new password...");
        this.newPassword.setPasswordMode(true);
        this.newPassword.setPasswordCharacter('*');

        this.confirmPassword = new TextField("", skin);
        this.confirmPassword.setMessageText("Confirm new password...");
        this.confirmPassword.setPasswordMode(true);
        this.confirmPassword.setPasswordCharacter('*');


        this.resetButton = new TextButton("RESET PASSWORD", skin);
        this.backButton = new TextButton("BACK", skin);

        this.table = new Table();


        TextField.TextFieldStyle style = this.securityAnswer.getStyle();
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


        table.add(menuTitle).colspan(2).padBottom(40);


        table.row().pad(PADDING, 70, PADDING, 100);
        Label userTitleLabel = new Label("USERNAME:", GameAssetManager.getGameAssetManager().getSkin());
        userTitleLabel.setColor(Color.CYAN);
        userTitleLabel.setFontScale(LABEL_SCALE);
        table.add(userTitleLabel).width(200).right().padRight(30);

        usernameLabel = new Label(username, GameAssetManager.getGameAssetManager().getSkin());
        usernameLabel.setColor(Color.WHITE);
        usernameLabel.setFontScale(1.2f);
        table.add(usernameLabel).width(FIELD_WIDTH).height(40).left();


        table.row().pad(PADDING, 70, PADDING, 100);
        Label questionTitleLabel = new Label("SECURITY QUESTION:", GameAssetManager.getGameAssetManager().getSkin());
        questionTitleLabel.setColor(Color.CYAN);
        questionTitleLabel.setFontScale(LABEL_SCALE);
        table.add(questionTitleLabel).width(200).right().padRight(30);

        questionLabel = new Label(securityQuestion, GameAssetManager.getGameAssetManager().getSkin());
        questionLabel.setColor(Color.WHITE);
        questionLabel.setFontScale(1.2f);
        questionLabel.setWrap(true);
        table.add(questionLabel).width(FIELD_WIDTH).height(60).left();


        table.row().pad(PADDING, 70, PADDING, 100);
        Label answerLabel = new Label("YOUR ANSWER:", GameAssetManager.getGameAssetManager().getSkin());
        answerLabel.setColor(Color.CYAN);
        answerLabel.setFontScale(LABEL_SCALE);
        table.add(answerLabel).width(200).right().padRight(30);
        table.add(securityAnswer).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();


        table.row().pad(PADDING, 70, PADDING, 100);
        Label newPassLabel = new Label("NEW PASSWORD:", GameAssetManager.getGameAssetManager().getSkin());
        newPassLabel.setColor(Color.CYAN);
        newPassLabel.setFontScale(LABEL_SCALE);
        table.add(newPassLabel).width(200).right().padRight(30);
        table.add(newPassword).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();


        table.row().pad(PADDING, 70, PADDING, 100);
        Label confirmPassLabel = new Label("CONFIRM PASSWORD:", GameAssetManager.getGameAssetManager().getSkin());
        confirmPassLabel.setColor(Color.CYAN);
        confirmPassLabel.setFontScale(LABEL_SCALE);
        table.add(confirmPassLabel).width(200).right().padRight(30);
        table.add(confirmPassword).width(FIELD_WIDTH).height(FIELD_HEIGHT).left();


        table.row().pad(PADDING * 2, 0, BUTTON_SPACING, 0);
        resetButton.setColor(new Color(0.2f, 0.6f, 0.9f, 1f));

        resetButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                resetButton.setColor(new Color(0.3f, 0.7f, 1f, 1f));
                resetButton.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                resetButton.setColor(new Color(0.2f, 0.6f, 0.9f, 1f));
                resetButton.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });

        table.add(resetButton).colspan(2).width(BUTTON_WIDTH + 30).height(BUTTON_HEIGHT_SMALL);


        table.row().pad(BUTTON_SPACING, 0, PADDING * 2, 0);
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

        table.add(backButton).colspan(2).width(BUTTON_WIDTH - 240).height(BUTTON_HEIGHT_SMALL);

        stage.addActor(table);


        try {
            securityAnswer.getStyle().font.getData().setScale(1.2f);
            newPassword.getStyle().font.getData().setScale(1.2f);
            confirmPassword.getStyle().font.getData().setScale(1.2f);
        } catch (Exception e) {
            Gdx.app.log("ForgotPasswordMenu", "Could not set font scale for TextFields");
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

    public String getUsername() {
        return username;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public TextField getSecurityAnswer() {
        return securityAnswer;
    }

    public TextField getNewPassword() {
        return newPassword;
    }

    public TextField getConfirmPassword() {
        return confirmPassword;
    }

    public TextButton getResetButton() {
        return resetButton;
    }

    public TextButton getBackButton() {
        return backButton;
    }

    public Stage getStage() {
        return stage;
    }
}
