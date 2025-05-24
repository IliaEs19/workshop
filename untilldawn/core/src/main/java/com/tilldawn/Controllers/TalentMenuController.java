package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Views.MainMenu;
import com.tilldawn.Views.TalentMenu;

public class TalentMenuController {
    private TalentMenu view;

    public void setView(TalentMenu view) {
        this.view = view;
    }

        public void addBackButtonListener(ClickListener listener) {
    }

        public void goToMainMenu() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Main.getMain().setScreen(new MainMenu(new MainMenuController(),
                    GameAssetManager.getGameAssetManager().getSkin()));
            }
        });
    }

        public void selectDefaultHero(HeroType hero) {


        Gdx.app.log("TalentMenuController", "Selected hero: " + hero.getName());
    }
}
