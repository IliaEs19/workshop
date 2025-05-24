package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tilldawn.Main;
import com.tilldawn.Models.DialogManager;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;
import com.tilldawn.Views.ForgotPasswordMenu;
import com.tilldawn.Views.LoginMenu;
import com.tilldawn.Views.MainMenu;
import com.tilldawn.Views.PreGameMenu;

public class LoginMenuController {
    private LoginMenu view;
    private boolean loginButtonPressed = false;

    public void setView(LoginMenu view) {
        this.view = view;
        setupListeners();
    }

    private void setupListeners() {
        if (view != null) {

            view.getLoginButton().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!loginButtonPressed) {
                        loginButtonPressed = true;
                        processLogin();

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                loginButtonPressed = false;
                            }
                        });
                    }
                }
            });


            view.getForgotPasswordButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    String username = view.getUserName().getText().trim();
                    if (username.isEmpty()) {
                        DialogManager.showErrorDialog(view.getStage(), "Error",
                            "Please enter your username first.", null);
                        return;
                    }


                    if (!SaveData.getInstance().userExists(username)) {
                        DialogManager.showErrorDialog(view.getStage(), "Error",
                            "Username not found. Please check your username.", null);
                        return;
                    }


                    String securityQuestion = SaveData.getInstance().getSecurityQuestion(username);
                    if (securityQuestion == null || securityQuestion.isEmpty()) {
                        DialogManager.showErrorDialog(view.getStage(), "Error",
                            "No security question found for this user.", null);
                        return;
                    }


                    Main.getMain().setScreen(new ForgotPasswordMenu(
                        new ForgotPasswordMenuController(),
                        GameAssetManager.getGameAssetManager().getSkin(),
                        username, securityQuestion));
                }
            });


            view.getBackButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    Main.getMain().setScreen(new MainMenu(
                        new MainMenuController(),
                        GameAssetManager.getGameAssetManager().getSkin()));
                }
            });
        }
    }

    private void processLogin() {
        String username = view.getUserName().getText().trim();
        String password = view.getPassword().getText();


        if (username.isEmpty() || password.isEmpty()) {
            DialogManager.showErrorDialog(view.getStage(), "Login Failed",
                "Username and password cannot be empty.", null);
            return;
        }


        if (!SaveData.getInstance().userExists(username)) {
            DialogManager.showErrorDialog(view.getStage(), "Login Failed",
                "Username not found. Please check your username or register first.", null);
            return;
        }


        if (!SaveData.getInstance().validateUser(username, password)) {
            DialogManager.showErrorDialog(view.getStage(), "Login Failed",
                "Incorrect password. Please try again.", null);
            return;
        }

        User loggedInUser = SaveData.getUser(username);
        SaveData.setCurrentUser(loggedInUser);
        DialogManager.showSuccessDialog(view.getStage(), "Success", "Login successful!", new Runnable() {
            @Override
            public void run() {
                PreGameMenuController preGameMenuController = new PreGameMenuController();
                if (preGameMenuController.getView() != null) {
                    Main.getMain().setScreen(preGameMenuController.getView());
                }
            }
        });
    }
}
