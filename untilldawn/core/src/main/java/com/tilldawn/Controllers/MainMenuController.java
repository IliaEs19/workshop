package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.SoundManager;
import com.tilldawn.Views.MainMenu;
import com.tilldawn.Views.PreGameMenu;
import com.tilldawn.Views.*;

public class MainMenuController {
    private MainMenu view;

    public void setView(MainMenu view) {
        this.view = view;
    }

    public void handleMainMenuButtons() {
        if (view != null) {
            for (TextButton button : view.getMenus()) {
                if (button.isPressed()) {
                    String name = button.getText().toString();

                    switch (name) {
                        case "RegisterMenu":
                            SoundManager.getInstance().play(SoundManager.BUTTON_CLICK);
                            Main.getMain().setScreen(new RegisterMenu(new RegisterMenuController(),GameAssetManager.getGameAssetManager().getSkin()));
                            break;
                        case "LoginMenu":
                            SoundManager.getInstance().play(SoundManager.BUTTON_CLICK);
                            Main.getMain().setScreen(new LoginMenu(new LoginMenuController(),GameAssetManager.getGameAssetManager().getSkin()));
                            break;
                        case "ProfileMenu":
                            SoundManager.getInstance().play(SoundManager.BUTTON_CLICK);
                            Main.getMain().setScreen(new ProfileLoginMenu(new ProfileLoginMenuController(),
                                GameAssetManager.getGameAssetManager().getSkin()));
                            break;
                        case "SettingMenu":
                            SoundManager.getInstance().play(SoundManager.BUTTON_CLICK);
                            Main.getMain().setScreen(new SettingMenu(new SettingMenuController(),GameAssetManager.getGameAssetManager().getSkin()));
                            break;
                        case "PreGameMenu":
                            SoundManager.getInstance().play(SoundManager.BUTTON_CLICK);
                            PreGameMenuController preGameMenuController = new PreGameMenuController();
                            if (preGameMenuController.getView() != null) {
                                Main.getMain().setScreen(preGameMenuController.getView());
                            }
                            break;
                        case "TalentMenu":
                            SoundManager.getInstance().play(SoundManager.BUTTON_CLICK);
                            Main.getMain().setScreen(new TalentMenu(new TalentMenuController()));
                            break;
                        case "ScoreBoard":
                            SoundManager.getInstance().play(SoundManager.BUTTON_CLICK);
                            Main.getMain().setScreen(new ScoreBoardMenu(this));
                            break;
                        case "Exit":
                            Gdx.app.exit();
                            break;
                    }

                    break;
                }
            }
        }
    }
}
