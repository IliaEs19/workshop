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
    // مرجع به کلاس GameView برای دسترسی به متدهای آن
    private GameView gameView;

    // متغیرهای مربوط به نمایش پیام فعال‌سازی چیت
    private boolean showCheatMessage;
    private String cheatMessage;
    private float messageTimer;
    private static final float MESSAGE_DURATION = 3.0f; // مدت زمان نمایش پیام (3 ثانیه)
    private BitmapFont messageFont;

    // وضعیت چیت‌های فعال
    private boolean infiniteShootingEnabled;

    /**
     * سازنده کلاس CheatManager
     * @param gameView مرجع به کلاس GameView
     */
    public CheatManager(GameView gameView) {
        this.gameView = gameView;
        this.showCheatMessage = false;
        this.messageTimer = 0;
        this.cheatMessage = "";
        this.infiniteShootingEnabled = false;

        // ایجاد فونت برای نمایش پیام‌ها
        messageFont = new BitmapFont();
        messageFont.getData().setScale(1.5f);
        messageFont.setColor(Color.YELLOW);
    }

    /**
     * پردازش کلید چیت وارد شده
     * @param key شماره کلید
     * @return true اگر چیت کد فعال شد، false در غیر این صورت
     */
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

    /**
     * چیت کد 1: کاهش زمان باقی‌مانده بازی به میزان یک دقیقه
     */
    private boolean decreaseRemainingTime() {
        if (gameView.decreaseGameTime(60)) { // کاهش 60 ثانیه (1 دقیقه)
            showMessage("Cheat Activated: Time decreased by 1 minute!");
            return true;
        }
        showMessage("Cheat Failed: Cannot decrease time further!");
        return false;
    }

    /**
     * چیت کد 2: افزایش سطح بازیکن (لول آپ)
     */
    private boolean forceLevelUp() {
        gameView.forceLevelUp();
        showMessage("Cheat Activated: Level Up!");
        return true;
    }

    /**
     * چیت کد 3: پر کردن جان بازیکن در صورت خالی بودن
     */
    private boolean refillHealth() {
        if (gameView.getPlayerHealth() < gameView.getPlayerMaxHealth() * 0.5f) {
            gameView.refillPlayerHealth();
            showMessage("Cheat Activated: Health Refilled!");
            return true;
        }
        showMessage("Cheat Failed: Health must be below 50%!");
        return false;
    }

    /**
     * چیت کد 4: شروع نبرد با باس (Elder)
     */
    private boolean startBossFight() {
        EnemyManager enemyManager = gameView.getEnemyManager();
        if (enemyManager != null) {
            // پاک کردن همه دشمن‌های موجود
            enemyManager.clearAllEnemies();

            // ایجاد یک Elder در نزدیکی بازیکن
            Vector2 playerPos = gameView.getPlayerPosition();
            float spawnX = playerPos.x + 200; // 200 واحد به راست بازیکن
            float spawnY = playerPos.y;

            ElderEnemy elder = new ElderEnemy(spawnX, spawnY, gameView.getRemainingGameTime());
            enemyManager.addCustomEnemy(elder);

            showMessage("Cheat Activated: Boss Fight Started!");
            return true;
        }
        showMessage("Cheat Failed: Cannot start boss fight!");
        return false;
    }

    /**
     * چیت کد 5: فعال/غیرفعال کردن شلیک بی‌نهایت بدون نیاز به ریلود
     */
    private boolean toggleInfiniteShooting() {
        infiniteShootingEnabled = !infiniteShootingEnabled;
        if (infiniteShootingEnabled) {
            showMessage("Cheat Activated: Infinite Shooting Without Reload!");
        } else {
            showMessage("Cheat Deactivated: Normal Shooting Restored!");
        }
        return true;
    }

    /**
     * نمایش پیام فعال‌سازی چیت
     * @param message پیام مورد نظر
     */
    private void showMessage(String message) {
        this.cheatMessage = message;
        this.showCheatMessage = true;
        this.messageTimer = MESSAGE_DURATION;
    }

    /**
     * بروزرسانی وضعیت چیت‌ها
     * این متد باید در هر فریم فراخوانی شود
     * @param delta زمان گذشته از فریم قبلی
     */
    public void update(float delta) {
        // بروزرسانی تایمر پیام
        if (showCheatMessage) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                showCheatMessage = false;
            }
        }
    }

    /**
     * رسم پیام‌های چیت
     * @param batch اسپرایت بچ برای رسم
     */
    public void render(SpriteBatch batch) {
        if (showCheatMessage) {
            // رسم پیام در بالای صفحه
            messageFont.draw(batch, cheatMessage,
                400 - 250,
                480 - 300);
        }
    }

    /**
     * آزادسازی منابع
     */
    public void dispose() {
        if (messageFont != null) {
            messageFont.dispose();
        }
    }

    /**
     * آیا شلیک بی‌نهایت بدون ریلود فعال است؟
     */
    public boolean isInfiniteShootingEnabled() {
        return infiniteShootingEnabled;
    }
}
