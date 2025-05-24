package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.tilldawn.Main;
import com.tilldawn.Models.DialogManager;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Views.LoginMenu;
import com.tilldawn.Views.PreGameMenu;

public class PreGameMenuController {
    private PreGameMenu view;
    private SaveData saveData;
    private User currentUser;


    private WeaponType selectedWeapon;
    private HeroType selectedHero;
    private int selectedTime;

    public PreGameMenuController() {
        saveData = SaveData.getInstance();
        currentUser = SaveData.getCurrentUser();


        if (currentUser == null) {
            showLoginRequiredError();
            return;
        }


        view = new PreGameMenu(this);


        loadUserGameData();
    }

        public boolean checkUserLoggedIn() {
        if (currentUser == null) {
            showLoginRequiredError();
            return false;
        }
        return true;
    }

        private void showLoginRequiredError() {
        Gdx.app.log("PreGameController", "Please, Login first.");


        LoginMenuController loginController = new LoginMenuController();
        LoginMenu loginMenu = new LoginMenu(loginController, GameAssetManager.getGameAssetManager().getSkin());
        Main.getMain().setScreen(loginMenu);


        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                DialogManager.showErrorDialog(loginMenu.getStage(), "Fail", "Please, Login first.", null);
            }
        });
    }

        private void loadUserGameData() {
        if (currentUser != null) {

            if (currentUser.getTotalGamesPlayed() > 0) {

                String lastWeaponName = currentUser.getLastWeaponUsed();
                if (lastWeaponName != null && !lastWeaponName.isEmpty()) {
                    for (WeaponType weapon : WeaponType.values()) {
                        if (weapon.getName().equals(lastWeaponName)) {
                            selectedWeapon = weapon;
                            break;
                        }
                    }
                }


                String lastHeroName = currentUser.getLastHeroUsed();
                if (lastHeroName != null && !lastHeroName.isEmpty()) {
                    for (HeroType hero : HeroType.values()) {
                        if (hero.getName().equals(lastHeroName)) {
                            selectedHero = hero;
                            break;
                        }
                    }
                }


                selectedTime = currentUser.getLastGameTime();
                if (selectedTime <= 0) {
                    selectedTime = 2;
                }
            } else {

                if (WeaponType.values().length > 0) {
                    selectedWeapon = WeaponType.values()[0];
                }
                if (HeroType.values().length > 0) {
                    selectedHero = HeroType.values()[0];
                }
                selectedTime = 2;
            }
        }
    }

        public void saveUserGameData() {
        if (currentUser != null && selectedWeapon != null && selectedHero != null) {
            currentUser.setLastWeaponUsed(selectedWeapon.getName());
            currentUser.setLastHeroUsed(selectedHero.getName());
            currentUser.setLastGameTime(selectedTime);


            saveData.updateUser(currentUser);
        }
    }

        public void startGame() {
        if (!checkUserLoggedIn()) {
            return;
        }

        if (selectedHero == null || selectedWeapon == null || selectedTime <= 0) {
            if (view != null) {
                DialogManager.showErrorDialog(view.getStage(),"Fail","Please select all fields.",null);
            }
            return;
        }


        saveUserGameData();


        currentUser.incrementTotalGamesPlayed();
        saveData.updateUser(currentUser);


        GameController gameController = new GameController(selectedHero, selectedWeapon, selectedTime);
        Main.getMain().setScreen(gameController.getGameScreen());
    }

        public void setSelectedWeapon(WeaponType weapon) {
        this.selectedWeapon = weapon;
    }

        public void setSelectedHero(HeroType hero) {
        this.selectedHero = hero;
    }

        public void setSelectedTime(int time) {
        this.selectedTime = time;
    }

        public WeaponType getSelectedWeapon() {
        return selectedWeapon;
    }

        public HeroType getSelectedHero() {
        return selectedHero;
    }

        public int getSelectedTime() {
        return selectedTime;
    }

        public PreGameMenu getView() {
        return view;
    }
}
