package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tilldawn.Controllers.GameController;
import com.tilldawn.Models.Bullet;
import com.tilldawn.Models.Enemy.Enemy;
import com.tilldawn.Models.Enemy.EnemyManager;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Models.Item.Item;
import com.tilldawn.Models.Weapon;

public class GameView implements Screen {
    // Constants
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;
    private static final float PLAYER_SPEED = 200; // pixels per second

    // اندازه دنیای بازی - بسیار بزرگتر از صفحه نمایش
    private static final float GAME_WORLD_WIDTH = 3000;
    private static final float GAME_WORLD_HEIGHT = 3000;

    // اندازه ثابت برای کاراکتر
    private static final float PLAYER_WIDTH = 64;
    private static final float PLAYER_HEIGHT = 64;

    // Core components
    private final GameController controller;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;
    private Viewport uiViewport;

    // Game entities
    private Texture backgroundTexture;
    private Texture playerTexture;
    private TextureRegion currentPlayerFrame;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;

    // Player state
    private Vector2 playerPosition;
    private Vector2 playerVelocity;
    private Rectangle playerBounds;
    private boolean isPlayerMoving;
    private PlayerDirection playerDirection;

    // Weapon system
    private Weapon currentWeapon;
    private boolean autoAim = false;

    // Mouse position in world coordinates
    private Vector3 mousePosition = new Vector3();
    private boolean canShoot = true; // متغیر جدید برای کنترل امکان شلیک


    private float playerHealth;
    private float playerMaxHealth;
    private float playerSpeed;
    private float basePlayerSpeed; // سرعت پایه بدون بوست
    private float damageBoostTimer = 0;
    private float speedBoostTimer = 0;
    private float damageMultiplier = 1.0f;
    private float speedMultiplier = 1.0f;
    private float invincibilityTimer = 0; // زمان نامیرایی پس از آسیب دیدن
    private static final float INVINCIBILITY_DURATION = 1.0f;
    private EnemyManager enemyManager;


    // Input handling
    private boolean keyW = false;
    private boolean keyA = false;
    private boolean keyS = false;
    private boolean keyD = false;
    private boolean mouseLeft = false;
    private boolean keySpace = false;
    private boolean keyR = false;

    // Game settings
    private HeroType selectedHero;
    private WeaponType selectedWeapon;
    private int gameTimeMinutes;
    private float gameTimeElapsed = 0;
    private boolean gameOver = false;

    // Direction enum for player animation
    private enum PlayerDirection {
        UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }

    public GameView(GameController controller, HeroType hero, WeaponType weapon, int timeMinutes) {
        this.controller = controller;
        this.selectedHero = hero;
        this.selectedWeapon = weapon;
        this.gameTimeMinutes = timeMinutes;
        if (hero != null) {
            // تبدیل مقادیر عددی به مقادیر بازی
            this.playerMaxHealth = hero.getHealthPoints() * 25; // هر واحد سلامتی = 25 نقطه
            this.playerHealth = this.playerMaxHealth;
            this.basePlayerSpeed = hero.getSpeed() * 40; // هر واحد سرعت = 40 واحد سرعت بازی
            this.playerSpeed = this.basePlayerSpeed;
        }

        batch = new SpriteBatch();

        // دوربین بازی
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        // دوربین UI
        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, uiCamera);
        uiCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);

        // شروع بازیکن در مرکز دنیای بازی
        playerPosition = new Vector2(GAME_WORLD_WIDTH / 2, GAME_WORLD_HEIGHT / 2);
        playerVelocity = new Vector2();
        playerBounds = new Rectangle(
            playerPosition.x - PLAYER_WIDTH/2,
            playerPosition.y - PLAYER_HEIGHT/2,
            PLAYER_WIDTH,
            PLAYER_HEIGHT
        );
        playerDirection = PlayerDirection.DOWN;

        // ایجاد سلاح
        if (selectedWeapon != null) {
            currentWeapon = new Weapon(selectedWeapon);
        } else {
            // سلاح پیش‌فرض
            currentWeapon = new Weapon(WeaponType.REVOLVER);
        }

        enemyManager = new EnemyManager(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, gameTimeMinutes * 60);

        loadAssets();
        setupInput();
    }

    private void loadAssets() {
        // لود پس‌زمینه با قابلیت تکرار
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/game.png"));
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // لود تصویر کاراکتر
        if (selectedHero != null && selectedHero.getTextureRegion() != null) {
            playerTexture = selectedHero.getTextureRegion().getTexture();
            currentPlayerFrame = selectedHero.getTextureRegion();
        } else {
            playerTexture = new Texture(Gdx.files.internal("heroes/character3.png"));
            currentPlayerFrame = new TextureRegion(playerTexture);
        }

        // ساخت انیمیشن ساده
        Array<TextureRegion> frames = new Array<>();
        frames.add(currentPlayerFrame);
        walkAnimation = new Animation<>(0.1f, frames);
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Keys.W: keyW = true; break;
                    case Keys.A: keyA = true; break;
                    case Keys.S: keyS = true; break;
                    case Keys.D: keyD = true; break;
                    case Keys.ESCAPE: controller.pauseGame(); break;
                    case Keys.SPACE: toggleAutoAim(); break;
                    case Keys.R: startReload(); break;
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Keys.W: keyW = false; break;
                    case Keys.A: keyA = false; break;
                    case Keys.S: keyS = false; break;
                    case Keys.D: keyD = false; break;
                    case Keys.R: keyR = false; break;
                }
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Buttons.LEFT) {
                    mouseLeft = true;
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Buttons.LEFT) {
                    mouseLeft = false;
                }
                return true;
            }
        });
    }

    private void toggleAutoAim() {
        autoAim = !autoAim;
    }

    private void startReload() {
        if (currentWeapon != null) {
            currentWeapon.startReload();
        }
    }

    @Override
    public void render(float delta) {
        // بروزرسانی منطق بازی
        update(delta);

        // پاک کردن صفحه
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // بروزرسانی دوربین برای دنبال کردن بازیکن
        updateCamera();
        camera.update();

        // تبدیل موقعیت ماوس به مختصات دنیای بازی
        updateMousePosition();

        // رسم عناصر بازی
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // رسم پس‌زمینه تکرارشونده
        drawRepeatingBackground();

        // رسم دشمن‌ها و آیتم‌ها
        enemyManager.render(batch);

        // رسم کاراکتر با افکت چشمک زدن در حالت نامیرایی
        if (invincibilityTimer <= 0 || Math.sin(stateTime * 20) > 0) {
            batch.draw(currentPlayerFrame,
                playerPosition.x - PLAYER_WIDTH/2,
                playerPosition.y - PLAYER_HEIGHT/2,
                PLAYER_WIDTH,
                PLAYER_HEIGHT);
        }

        // رسم سلاح و گلوله‌ها
        if (currentWeapon != null) {
            currentWeapon.render(batch);
        }

        batch.end();

        // رسم UI
        drawUI();
    }

    private void updateMousePosition() {
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePosition);
    }

    // متد جدید برای رسم پس‌زمینه تکرارشونده
    private void drawRepeatingBackground() {
        // محاسبه مستطیل قابل مشاهده توسط دوربین
        float startX = camera.position.x - camera.viewportWidth/2 * camera.zoom;
        float startY = camera.position.y - camera.viewportHeight/2 * camera.zoom;
        float endX = camera.position.x + camera.viewportWidth/2 * camera.zoom;
        float endY = camera.position.y + camera.viewportHeight/2 * camera.zoom;

        // محاسبه تعداد تکرارهای مورد نیاز
        int bgWidth = backgroundTexture.getWidth();
        int bgHeight = backgroundTexture.getHeight();

        // محاسبه شروع رسم (برای تکرار صحیح)
        int startTileX = (int)(startX / bgWidth) - 1;
        int startTileY = (int)(startY / bgHeight) - 1;
        int endTileX = (int)(endX / bgWidth) + 1;
        int endTileY = (int)(endY / bgHeight) + 1;

        // رسم تایل‌های پس‌زمینه
        for (int x = startTileX; x <= endTileX; x++) {
            for (int y = startTileY; y <= endTileY; y++) {
                batch.draw(backgroundTexture,
                    x * bgWidth,
                    y * bgHeight,
                    bgWidth,
                    bgHeight);
            }
        }
    }

    private void updateCamera() {
        // دوربین بازیکن را دنبال می‌کند
        camera.position.x = playerPosition.x;
        camera.position.y = playerPosition.y;
    }

    private void update(float delta) {
        if (gameOver) return;

        // بروزرسانی زمان بازی
        gameTimeElapsed += delta;
        if (gameTimeElapsed >= gameTimeMinutes * 60) {
            gameOver = true;
            controller.endGame();
            return;
        }

        // بروزرسانی تایمر نامیرایی
        if (invincibilityTimer > 0) {
            invincibilityTimer -= delta;
        }

        // بروزرسانی موقعیت ماوس و هدف
        updateMousePosition();
        float targetX = mousePosition.x;
        float targetY = mousePosition.y;

        if (autoAim) {
            // در اینجا باید نزدیکترین دشمن را پیدا کنید
            Enemy nearestEnemy = findNearestEnemy();
            if (nearestEnemy != null) {
                Vector2 enemyPos = nearestEnemy.getPosition();
                targetX = enemyPos.x;
                targetY = enemyPos.y;
            }
        }

        // بروزرسانی سلاح - مستقل از حرکت بازیکن
        if (currentWeapon != null) {
            currentWeapon.update(delta, playerPosition, targetX, targetY);

            // شلیک با کلیک چپ ماوس - مستقل از حرکت بازیکن
            if (mouseLeft) {
                boolean shotFired = currentWeapon.shoot(playerPosition, targetX, targetY);

                if (shotFired) {
                    // اعمال ضریب آسیب اگر بوست فعال است
                    if (damageMultiplier > 1.0f) {
                        // این متد باید به کلاس Bullet اضافه شود
                        for (Bullet bullet : currentWeapon.getBullets()) {
                            bullet.setDamageMultiplier(damageMultiplier);
                        }
                    }
                }
            }

            // ریلود با کلید R
            if (keyR) {
                currentWeapon.startReload();
            }
        }

        // بروزرسانی دشمن‌ها
        enemyManager.update(delta, playerPosition);

        // بررسی برخورد گلوله‌های بازیکن با دشمن‌ها
        if (currentWeapon != null) {
            enemyManager.checkBulletCollisions(currentWeapon.getBullets());
        }

        // بررسی برخورد بازیکن با دشمن‌ها (فقط اگر نامیرا نباشد)
        if (invincibilityTimer <= 0) {
            if (enemyManager.checkPlayerCollisions(playerBounds)) {
                // بازیکن آسیب می‌بیند
                takeDamage(10); // مقدار آسیب را می‌توانید تنظیم کنید
            }
        }

        // بررسی جمع‌آوری آیتم‌ها
        Array<Item> collectedItems = enemyManager.checkItemCollisions(playerBounds);
        for (Item item : collectedItems) {
            applyItemEffect(item);
        }

        // بروزرسانی تایمرهای بوست
        if (damageBoostTimer > 0) {
            damageBoostTimer -= delta;
            if (damageBoostTimer <= 0) {
                damageMultiplier = 1.0f;
            }
        }

        if (speedBoostTimer > 0) {
            speedBoostTimer -= delta;
            if (speedBoostTimer <= 0) {
                speedMultiplier = 1.0f;
                playerSpeed = basePlayerSpeed;
            }
        }

        // محاسبه جهت حرکت بازیکن
        updatePlayerVelocity();

        // بروزرسانی موقعیت بازیکن
        if (playerVelocity.len() > 0) {
            // نرمالایز و مقیاس‌بندی سرعت با در نظر گرفتن ضریب سرعت
            playerVelocity.nor().scl(playerSpeed * speedMultiplier * delta);
            playerPosition.add(playerVelocity);
            isPlayerMoving = true;

            // بروزرسانی مرزهای بازیکن
            playerBounds.setPosition(
                playerPosition.x - PLAYER_WIDTH/2,
                playerPosition.y - PLAYER_HEIGHT/2
            );
        } else {
            isPlayerMoving = false;
        }

        // بروزرسانی انیمیشن
        stateTime += delta;
        if (isPlayerMoving && walkAnimation != null) {
            currentPlayerFrame = walkAnimation.getKeyFrame(stateTime, true);
        }

        // بروزرسانی جهت بازیکن برای انیمیشن
        updatePlayerDirection();
    }

    private void takeDamage(float amount) {
        // اگر بازیکن نامیراست، آسیب نمی‌بیند
        if (invincibilityTimer > 0) return;

        playerHealth -= amount;

        // فعال کردن حالت نامیرایی موقت
        invincibilityTimer = INVINCIBILITY_DURATION;

        // بررسی پایان بازی
        if (playerHealth <= 0) {
            playerHealth = 0;
            gameOver = true;
            controller.endGame();
        }
    }

    private Enemy findNearestEnemy() {
        Enemy nearest = null;
        float minDistanceSquared = Float.MAX_VALUE;

        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy.isAlive()) {
                Vector2 enemyPos = enemy.getPosition();
                float dx = enemyPos.x - playerPosition.x;
                float dy = enemyPos.y - playerPosition.y;
                float distanceSquared = dx * dx + dy * dy;

                if (distanceSquared < minDistanceSquared) {
                    minDistanceSquared = distanceSquared;
                    nearest = enemy;
                }
            }
        }

        return nearest;
    }

    private void applyItemEffect(Item item) {
        switch (item.getType()) {
            case HEALTH:
                playerHealth = Math.min(playerHealth + 25, playerMaxHealth);
                break;
            case AMMO:
                if (currentWeapon != null) {
                    // افزایش مهمات
                    currentWeapon.addAmmo(10);
                }
                break;
            case SPEED_BOOST:
                speedMultiplier = 1.5f;
                playerSpeed = basePlayerSpeed * speedMultiplier;
                speedBoostTimer = 10; // 10 ثانیه بوست سرعت
                break;
            case DAMAGE_BOOST:
                damageMultiplier = 2.0f;
                damageBoostTimer = 10; // 10 ثانیه بوست آسیب
                break;
        }
    }

    private void updatePlayerVelocity() {
        playerVelocity.set(0, 0);

        // حرکت 8 جهته
        if (keyW) playerVelocity.y += 1;
        if (keyS) playerVelocity.y -= 1;
        if (keyA) playerVelocity.x -= 1;
        if (keyD) playerVelocity.x += 1;
    }

    private void updatePlayerDirection() {
        // تعیین جهت بازیکن بر اساس سرعت
        if (playerVelocity.x > 0) {
            if (playerVelocity.y > 0) {
                playerDirection = PlayerDirection.UP_RIGHT;
            } else if (playerVelocity.y < 0) {
                playerDirection = PlayerDirection.DOWN_RIGHT;
            } else {
                playerDirection = PlayerDirection.RIGHT;
            }
        } else if (playerVelocity.x < 0) {
            if (playerVelocity.y > 0) {
                playerDirection = PlayerDirection.UP_LEFT;
            } else if (playerVelocity.y < 0) {
                playerDirection = PlayerDirection.DOWN_LEFT;
            } else {
                playerDirection = PlayerDirection.LEFT;
            }
        } else {
            if (playerVelocity.y > 0) {
                playerDirection = PlayerDirection.UP;
            } else if (playerVelocity.y < 0) {
                playerDirection = PlayerDirection.DOWN;
            }
        }

        // چرخش تصویر بر اساس جهت
        if (playerDirection == PlayerDirection.LEFT ||
            playerDirection == PlayerDirection.UP_LEFT ||
            playerDirection == PlayerDirection.DOWN_LEFT) {
            if (!currentPlayerFrame.isFlipX()) {
                currentPlayerFrame.flip(true, false);
            }
        } else if (playerDirection == PlayerDirection.RIGHT ||
            playerDirection == PlayerDirection.UP_RIGHT ||
            playerDirection == PlayerDirection.DOWN_RIGHT) {
            if (currentPlayerFrame.isFlipX()) {
                currentPlayerFrame.flip(true, false);
            }
        }
    }

    private void drawUI() {
        // تنظیم دوربین UI
        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // محاسبه زمان باقی‌مانده
        int remainingSeconds = (int)(gameTimeMinutes * 60 - gameTimeElapsed);
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        // رسم زمان و اطلاعات سلاح
        BitmapFont font = new BitmapFont();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, String.format("Time: %02d:%02d", minutes, seconds),
            20, WORLD_HEIGHT - 20);

        // نمایش اطلاعات سلاح
        if (currentWeapon != null) {
            font.draw(batch, String.format("Weapon: %s | Ammo: %d/%d",
                    currentWeapon.getType().getName(),
                    currentWeapon.getCurrentAmmo(),
                    currentWeapon.getType().getMaxAmmo()),
                WORLD_WIDTH - 300, WORLD_HEIGHT - 20);

            // نمایش وضعیت ریلود
            if (currentWeapon.isReloading()) {
                font.draw(batch, "Reloading... " +
                        (int)(currentWeapon.getReloadProgress() * 100) + "%",
                    WORLD_WIDTH - 300, WORLD_HEIGHT - 40);
            }
        }

        // نمایش سلامتی بازیکن
        font.draw(batch, String.format("Health: %.0f/%.0f", playerHealth, playerMaxHealth),
            20, WORLD_HEIGHT - 40);

        // نمایش وضعیت auto-aim
        String autoAimStatus = autoAim ? "ON" : "OFF";
        font.draw(batch, "Auto-Aim: " + autoAimStatus, 20, WORLD_HEIGHT - 60);

        // نمایش بوست‌های فعال
        if (damageBoostTimer > 0) {
            font.draw(batch, String.format("Damage Boost: %.1fs", damageBoostTimer),
                20, WORLD_HEIGHT - 80);
        }

        if (speedBoostTimer > 0) {
            font.draw(batch, String.format("Speed Boost: %.1fs", speedBoostTimer),
                20, WORLD_HEIGHT - 100);
        }

        // نمایش قهرمان انتخاب شده
        if (selectedHero != null) {
            font.draw(batch, "Hero: " + selectedHero.getName(),
                WORLD_WIDTH - 300, WORLD_HEIGHT - 60);
        }

        // نمایش وضعیت کنترل‌ها
        font.draw(batch, "WASD: Move | Mouse: Aim | Click: Shoot | R: Reload | Space: Auto-Aim",
            WORLD_WIDTH/2 - 200, 20);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height);
    }

    @Override
    public void pause() {
        // Handle game pause
    }

    @Override
    public void resume() {
        // Handle game resume
    }

    @Override
    public void hide() {
        // Called when this screen is no longer the current screen
    }

    @Override
    public void show() {
        // Called when this screen becomes the current screen
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        playerTexture.dispose();

        if (currentWeapon != null) {
            currentWeapon.dispose();
        }
    }

    // Getters for controller access
    public Vector2 getPlayerPosition() {
        return playerPosition;
    }

    public Rectangle getPlayerBounds() {
        return playerBounds;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // Method for controller to force game over
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    // متد برای تغییر سلاح
    public void setWeapon(WeaponType weaponType) {
        if (currentWeapon != null) {
            currentWeapon.dispose();
        }
        currentWeapon = new Weapon(weaponType);
    }

    // متد برای دسترسی به سلاح فعلی
    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }
}
