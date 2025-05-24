package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;
import com.tilldawn.Views.GameOverScreen;
import com.tilldawn.Views.GameView;
import com.tilldawn.Views.MainMenu;

public class GameController {
    private GameView gameView;
    private final HeroType selectedHero;
    private final WeaponType selectedWeapon;
    private final int gameTimeMinutes;
    private boolean isPaused = false;
    private int score = 0;

    public GameController(HeroType hero, WeaponType weapon, int timeMinutes) {
        this.selectedHero = hero;
        this.selectedWeapon = weapon;
        this.gameTimeMinutes = timeMinutes;

        this.gameView = new GameView(this, hero, weapon, timeMinutes);
    }

    public void startGame() {
        Main.getMain().setScreen(gameView);
    }

    public void pauseGame() {
        isPaused = true;
    }

    public void resumeGame() {
        isPaused = false;
        Main.getMain().setScreen(gameView);
    }

    public void exitGame() {
        returnToMainMenu();
    }

    public void endGame() {
        boolean isVictory = false;
        int kills = 0;
        float survivalTime = 0;

        if (gameView != null) {
            isVictory = gameView.isTimeUp() && !gameView.isPlayerDead();
            kills = gameView.getPlayerKills();
            survivalTime = gameView.getSurvivalTime();

            score = (int)(survivalTime * kills);
        }

        SaveData saveData = SaveData.getInstance();
        if (saveData.getCurrentUser() != null) {
            User currentUser = saveData.getCurrentUser();


            currentUser.setHighScore(Math.max(currentUser.getHighScore(), score));


            currentUser.addKills(kills);


            currentUser.updateLongestSurvivalTime(survivalTime);
            currentUser.addSurvivalTime(survivalTime);


            currentUser.incrementTotalGamesPlayed();


            saveData.updateUser(currentUser);
        }


        Main.getMain().setScreen(new GameOverScreen(this, isVictory, kills, survivalTime));
    }

        public void giveUp() {

        if (gameView != null) {
            int kills = gameView.getPlayerKills();
            float survivalTime = gameView.getSurvivalTime();


            score = (int)(survivalTime * kills);


            SaveData saveData = SaveData.getInstance();
            if (saveData.getCurrentUser() != null) {
                User currentUser = saveData.getCurrentUser();


                currentUser.setHighScore(Math.max(currentUser.getHighScore(), score));


                currentUser.addKills(kills);


                currentUser.updateLongestSurvivalTime(survivalTime);
                currentUser.addSurvivalTime(survivalTime);


                currentUser.incrementTotalGamesPlayed();


                saveData.updateUser(currentUser);
            }


            Main.getMain().setScreen(new GameOverScreen(this, false, kills, survivalTime));
        }
    }


    public void returnToMainMenu() {

        Main.getMain().setScreen(new MainMenu(new MainMenuController(),
            GameAssetManager.getGameAssetManager().getSkin()));
    }

    public void addScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }

    public GameView getGameScreen() {
        return gameView;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public HeroType getSelectedHero() {
        return selectedHero;
    }

    public WeaponType getSelectedWeapon() {
        return selectedWeapon;
    }

    public int getGameTimeMinutes() {
        return gameTimeMinutes;
    }
}
