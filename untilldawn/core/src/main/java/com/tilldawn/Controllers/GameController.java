package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Views.GameView;
import com.tilldawn.Views.MainMenu;
//import com.tilldawn.Views.PauseMenu;

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

        // Create game view
        this.gameView = new GameView(this, hero, weapon, timeMinutes);
    }

    public void startGame() {
        Main.getMain().setScreen(gameView);
    }

    public void pauseGame() {
        isPaused = true;
        // Show pause menu
        //Main.getMain().setScreen(new PauseMenu(this));
    }

    public void resumeGame() {
        isPaused = false;
        Main.getMain().setScreen(gameView);
    }

    public void exitGame() {
        // کد خروج از بازی
        // می‌تواند به صفحه اصلی برگردد یا بازی را کاملاً ببندد
        Gdx.app.exit();
    }

    public void endGame() {
        // Save game results
        SaveData saveData = SaveData.getInstance();
        if (saveData.getCurrentUser() != null) {
            saveData.getCurrentUser().setHighScore(Math.max(saveData.getCurrentUser().getHighScore(), score));
            saveData.updateUser(saveData.getCurrentUser());
        }

        // Show game over screen or return to main menu
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
