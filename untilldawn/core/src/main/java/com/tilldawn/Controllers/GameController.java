package com.tilldawn.Controllers;

import com.badlogic.gdx.Gdx;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Views.GameOverScreen;
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
        // برگشت به منوی اصلی به جای خروج کامل از بازی
        returnToMainMenu();
    }

    /**
     * این متد زمانی فراخوانی می‌شود که بازی به پایان رسیده است
     * (به دلیل اتمام زمان یا از بین رفتن سلامتی بازیکن)
     */
    public void endGame() {
        // بررسی شرایط پایان بازی
        boolean isVictory = false;
        int kills = 0;
        float survivalTime = 0;

        if (gameView != null) {
            // اگر زمان بازی به پایان رسیده باشد و بازیکن زنده باشد، پیروزی است
            isVictory = gameView.isTimeUp() && !gameView.isPlayerDead();
            kills = gameView.getPlayerKills();
            survivalTime = gameView.getSurvivalTime();

            // محاسبه امتیاز بر اساس فرمول
            score = (int)(survivalTime * kills);
        }

        // ذخیره نتایج بازی
        SaveData saveData = SaveData.getInstance();
        if (saveData.getCurrentUser() != null) {
            saveData.getCurrentUser().setHighScore(Math.max(saveData.getCurrentUser().getHighScore(), score));
            saveData.updateUser(saveData.getCurrentUser());
        }

        // نمایش صفحه پایان بازی
        Main.getMain().setScreen(new GameOverScreen(this, isVictory, kills, survivalTime));
    }

    /**
     * این متد زمانی فراخوانی می‌شود که بازیکن تسلیم می‌شود
     * (مثلاً از طریق منوی pause)
     */
    public void giveUp() {
        // زمانی که بازیکن تسلیم می‌شود
        if (gameView != null) {
            // اطمینان حاصل کنید که بازی از حالت pause خارج شده است
            isPaused = false;

            int kills = gameView.getPlayerKills();
            float survivalTime = gameView.getSurvivalTime();

            // محاسبه امتیاز بر اساس فرمول
            score = (int)(survivalTime * kills);

            // ذخیره نتایج بازی
            SaveData saveData = SaveData.getInstance();
            if (saveData.getCurrentUser() != null) {
                saveData.getCurrentUser().setHighScore(Math.max(saveData.getCurrentUser().getHighScore(), score));
                saveData.updateUser(saveData.getCurrentUser());
            }

            // نمایش صفحه پایان بازی با وضعیت شکست
            Main.getMain().setScreen(new GameOverScreen(this, false, kills, survivalTime));
        }
    }

    /**
     * این متد برای بازگشت به منوی اصلی از صفحه پایان بازی استفاده می‌شود
     */
    public void returnToMainMenu() {
        // بازگشت به منوی اصلی
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
