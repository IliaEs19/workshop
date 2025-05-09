package com.tilldawn.Controllers;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
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
                            Main.getMain().setScreen(new RegisterMenu(new RegisterMenuController(),GameAssetManager.getGameAssetManager().getSkin()));
                            break;
                        case "LoginMenu":
                            //Main.getMain().setScreen(new LoginMenu(/* constructor args */));
                            break;
                        case "ProfileMenu":
                            //Main.getMain().setScreen(new ProfileMenu(/* constructor args */));
                            break;
                        case "SettingMenu":
                            //Main.getMain().setScreen(new SettingMenu(/* constructor args */));
                            break;
                        case "PreGameMenu":
                            Main.getMain().setScreen(new PreGameMenu(new PreGameMenuController(), GameAssetManager.getGameAssetManager().getSkin()));
                            break;
                        case "TalentMenu":
                            //Main.getMain().setScreen(new TalentMenu(/* constructor args */));
                            break;
                    }

                    break;
                }
            }
        }
    }
}
