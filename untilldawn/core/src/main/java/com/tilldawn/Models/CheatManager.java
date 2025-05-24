package com.tilldawn.Models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.tilldawn.Models.CheatCode;
import com.tilldawn.Models.Enemy.ElderEnemy;
import com.tilldawn.Models.Enemy.EnemyManager;
import com.tilldawn.Views.GameView;

public class CheatManager {

    private GameView gameView;


    private boolean showCheatMessage;
    private String cheatMessage;
    private float messageTimer;
    private static final float MESSAGE_DURATION = 3.0f;
    private BitmapFont messageFont;


    private boolean infiniteShootingEnabled;

        public CheatManager(GameView gameView) {
        this.gameView = gameView;
        this.showCheatMessage = false;
        this.messageTimer = 0;
        this.cheatMessage = "";
        this.infiniteShootingEnabled = false;


        messageFont = new BitmapFont();
        messageFont.getData().setScale(1.5f);
        messageFont.setColor(Color.YELLOW);
    }

        public boolean processCheatKey(int key) {
        CheatCode cheatCode = CheatCode.getByKeyCode(key);
        if (cheatCode == null) {
            return false;
        }

        switch (cheatCode) {
            case DECREASE_TIME:
                return decreaseRemainingTime();
            case LEVEL_UP:
                return forceLevelUp();
            case REFILL_HEALTH:
                return refillHealth();
            case BOSS_FIGHT:
                return startBossFight();
            case INFINITE_SHOOTING:
                return toggleInfiniteShooting();
            default:
                return false;
        }
    }

        private boolean decreaseRemainingTime() {
        if (gameView.decreaseGameTime(60)) {
            showMessage("Cheat Activated: Time decreased by 1 minute!");
            return true;
        }
        showMessage("Cheat Failed: Cannot decrease time further!");
        return false;
    }

        private boolean forceLevelUp() {
        gameView.forceLevelUp();
        showMessage("Cheat Activated: Level Up!");
        return true;
    }

        private boolean refillHealth() {
        if (gameView.getPlayerHealth() < gameView.getPlayerMaxHealth() * 0.5f) {
            gameView.refillPlayerHealth();
            showMessage("Cheat Activated: Health Refilled!");
            return true;
        }
        showMessage("Cheat Failed: Health must be below 50%!");
        return false;
    }

        private boolean startBossFight() {
        EnemyManager enemyManager = gameView.getEnemyManager();
        if (enemyManager != null) {

            enemyManager.clearAllEnemies();


            Vector2 playerPos = gameView.getPlayerPosition();
            float spawnX = playerPos.x + 200;
            float spawnY = playerPos.y;

            ElderEnemy elder = new ElderEnemy(spawnX, spawnY, gameView.getRemainingGameTime());
            enemyManager.addCustomEnemy(elder);

            showMessage("Cheat Activated: Boss Fight Started!");
            return true;
        }
        showMessage("Cheat Failed: Cannot start boss fight!");
        return false;
    }

        private boolean toggleInfiniteShooting() {
        infiniteShootingEnabled = !infiniteShootingEnabled;
        if (infiniteShootingEnabled) {
            showMessage("Cheat Activated: Infinite Shooting Without Reload!");
        } else {
            showMessage("Cheat Deactivated: Normal Shooting Restored!");
        }
        return true;
    }

        private void showMessage(String message) {
        this.cheatMessage = message;
        this.showCheatMessage = true;
        this.messageTimer = MESSAGE_DURATION;
    }

        public void update(float delta) {

        if (showCheatMessage) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                showCheatMessage = false;
            }
        }
    }

        public void render(SpriteBatch batch) {
        if (showCheatMessage) {

            messageFont.draw(batch, cheatMessage,
                400 - 250,
                480 - 300);
        }
    }

        public void dispose() {
        if (messageFont != null) {
            messageFont.dispose();
        }
    }

        public boolean isInfiniteShootingEnabled() {
        return infiniteShootingEnabled;
    }
}
