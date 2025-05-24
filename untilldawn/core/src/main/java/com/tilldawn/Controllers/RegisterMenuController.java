package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tilldawn.Main;
import com.tilldawn.Models.*;
import com.tilldawn.Views.AvatarSelectionScreen;
import com.tilldawn.Views.MainMenu;
import com.tilldawn.Views.PreGameMenu;
import com.tilldawn.Views.RegisterMenu;

public class RegisterMenuController {
    private RegisterMenu view;
    private boolean registerButtonPressed = false;

    public void setView(RegisterMenu view) {
        this.view = view;
        setupListeners();
    }

    private void processRegistration() {
        String userName = view.getUserName().getText();
        String password = view.getPassword().getText();
        String question = view.getSecurityQuestionSelect().getSelected();
        String answer = view.getSecurityAnswer().getText();

        Result result = validatePassword(password);
        if(userName.equals("your username...") || userName.isEmpty()){
            DialogManager.showErrorDialog(view.getStage(),"Error","username can not be empty.",null);
        }
        else if(password.equals("your password...") || password.isEmpty()){
            DialogManager.showErrorDialog(view.getStage(),"Error","password can not be empty.",null);
        }
        else if(SaveData.getInstance().getUser(userName) != null){
            DialogManager.showErrorDialog(view.getStage(),"Error","username already exist! choose another username.",null);
        }
        else if (!result.isSuccessful()) {
            DialogManager.showErrorDialog(view.getStage(),"Error", result.getMessage(), null);
        }
        else if(answer.equalsIgnoreCase("Enter your answer...") || answer.isEmpty()){
            DialogManager.showErrorDialog(view.getStage(),"Error","answer can not be empty.",null);
        }
        else if(SaveData.getInstance().addUser(userName,password,question,answer)){
            DialogManager.showSuccessDialog(view.getStage(), "Success", "Registration successful!", new Runnable() {
                @Override
                public void run() {
                    User user = SaveData.getInstance().getUser(userName);


                    Main.getMain().setScreen(new AvatarSelectionScreen(
                        new AvatarSelectionController(),
                        GameAssetManager.getGameAssetManager().getSkin(),
                        user));                }
            });
        }
    }


    private void setupListeners() {
        if (view != null) {
            view.getRegister().addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!registerButtonPressed) {
                        registerButtonPressed = true;
                        processRegistration();

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                registerButtonPressed = false;
                            }
                        });
                    }
                }
            });

            view.getGuestButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    Main.getMain().setScreen(new PreGameMenu(new PreGameMenuController()));
                }
            });

            view.getBackButton().addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    Main.getMain().setScreen(new MainMenu(new MainMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
                }
            });
        }
    }

    public static Result validatePassword(String password) {
        if (password.length() < 8) {
            return new Result(false, "Password must be at least 8 characters long.");
        } else if (!password.matches(".*[@%$#&*()_].*")) {
            return new Result(false, "Password must contain at least one special character (@%$#&*()_).");
        } else if (!password.matches(".*\\d.*")) {
            return new Result(false, "Password must contain at least one number.");
        } else if (!password.matches(".*[A-Z].*")) {
            return new Result(false, "Password must contain at least one uppercase letter.");
        }

        return new Result(true, "");
    }
}
