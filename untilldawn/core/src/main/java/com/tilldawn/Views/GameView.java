package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
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
import com.tilldawn.Models.Hero.AbilityType;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Models.Item.Item;
import com.tilldawn.Models.Weapon;

import static com.tilldawn.Models.Item.ItemType.DAMAGE_BOOST;

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
    private float damageMultiplier = 1.0f;
    private float speedMultiplier = 1.0f;
    private float invincibilityTimer = 0; // زمان نامیرایی پس از آسیب دیدن
    private static final float INVINCIBILITY_DURATION = 1.0f;
    private EnemyManager enemyManager;

    private int playerLevel = 1;
    private int playerXP = 0;
    private int xpToNextLevel = 20;
    private Array<AbilityType> playerAbilities = new Array<>();
    private boolean showAbilitySelection = false;
    private AbilityType[] abilityChoices;
    private float damageBoostTimer = 0;
    private float speedBoostTimer = 0;


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

    public void selectAbility(int index) {
        if (showAbilitySelection && abilityChoices != null && index >= 0 && index < abilityChoices.length) {
            // اضافه کردن توانایی انتخاب شده به لیست توانایی‌های بازیکن
            playerAbilities.add(abilityChoices[index]);

            // اعمال اثرات توانایی انتخاب شده
            applyAbilityEffects(abilityChoices[index]);

            // بستن صفحه انتخاب توانایی
            showAbilitySelection = false;
            abilityChoices = null;

            // ادامه بازی
            controller.resumeGame();
        }
    }

    private void applyAbilityEffects(AbilityType ability) {
        switch (ability) {
            case VITALITY:
                // افزایش سلامتی حداکثر به اندازه یک واحد (25 نقطه)
                playerMaxHealth += 25;
                playerHealth += 25; // سلامتی فعلی نیز افزایش می‌یابد
                break;
            case DAMAGER:
                // افزایش 25 درصدی میزان دمیج سلاح به مدت 10 ثانیه
                damageMultiplier = 1.25f;
                damageBoostTimer = 10;
                break;
            case PROCREASE:
                // افزایش یک واحدی Projectile سلاح
                if (currentWeapon != null) {
                    currentWeapon.increaseProjectileCount(1);
                }
                break;
            case AMOCREASE:
                // افزایش 5 واحدی حداکثر تعداد تیرهای سلاح
                if (currentWeapon != null) {
                    currentWeapon.increaseMaxAmmo(5);
                    currentWeapon.addAmmo(5);
                }
                break;
            case SPEEDY:
                // 2 برابر شدن سرعت حرکت بازیکن به مدت 10 ثانیه
                speedMultiplier = 2.0f;
                playerSpeed = basePlayerSpeed * speedMultiplier;
                speedBoostTimer = 10;
                break;
        }
    }

    private void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                // اگر صفحه انتخاب قابلیت نمایش داده می‌شود، کلیدهای 1، 2، 3 را بررسی کن
                if (showAbilitySelection) {
                    switch (keycode) {
                        case Keys.NUM_1:
                            selectAbility(0);
                            return true;
                        case Keys.NUM_2:
                            selectAbility(1);
                            return true;
                        case Keys.NUM_3:
                            selectAbility(2);
                            return true;
                    }
                    return false;
                }

                // کلیدهای عادی بازی
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

                    // اگر صفحه انتخاب توانایی نمایش داده می‌شود، بررسی کن که آیا روی یک توانایی کلیک شده است
                    if (showAbilitySelection) {
                        checkAbilityClick(screenX, screenY);
                        return true;
                    }
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

    private void checkAbilityClick(int screenX, int screenY) {
        // تبدیل مختصات صفحه به مختصات دوربین UI
        Vector3 touchPoint = new Vector3(screenX, screenY, 0);
        uiViewport.unproject(touchPoint);

        // محاسبه موقعیت کارت‌های توانایی
        float cardWidth = 200;
        float cardHeight = 150;
        float startX = WORLD_WIDTH / 2 - (abilityChoices.length * cardWidth + (abilityChoices.length - 1) * 20) / 2;
        float startY = WORLD_HEIGHT / 2 - cardHeight / 2;

        // بررسی کلیک روی هر کارت
        for (int i = 0; i < abilityChoices.length; i++) {
            float x = startX + i * (cardWidth + 20);
            float y = startY;

            if (touchPoint.x >= x && touchPoint.x <= x + cardWidth &&
                touchPoint.y >= y && touchPoint.y <= y + cardHeight) {
                selectAbility(i);
                return;
            }
        }
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

        // رسم صفحه انتخاب توانایی اگر فعال است
        if (showAbilitySelection) {
            renderAbilitySelection();
        }
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

        // اگر صفحه انتخاب توانایی نمایش داده می‌شود، بازی را بروز نکن
        if (showAbilitySelection) {
            return;
        }

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

            // شلیک با کلیک چپ ماوس
            if (mouseLeft) {
                boolean shotFired = currentWeapon.shoot(playerPosition, targetX, targetY);

                if (shotFired && damageMultiplier > 1.0f) {
                    // اعمال ضریب آسیب به گلوله‌ها
                    for (Bullet bullet : currentWeapon.getBullets()) {
                        bullet.setDamageMultiplier(damageMultiplier);
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

        // بررسی برخورد گلوله‌های دشمن با بازیکن
        if (invincibilityTimer <= 0) {
            for (Enemy enemy : enemyManager.getEnemies()) {
                if (enemy.getBullets() != null) {
                    for (int i = enemy.getBullets().size - 1; i >= 0; i--) {
                        Bullet bullet = enemy.getBullets().get(i);
                        if (bullet.getBounds().overlaps(playerBounds)) {
                            takeDamage(bullet.getDamage());
                            enemy.getBullets().removeIndex(i);
                            break;
                        }
                    }
                }
            }
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
        if (invincibilityTimer > 0) {
            return;
        }

        // کاهش سلامتی بازیکن
        playerHealth -= amount;

        // فعال کردن حالت نامیرایی موقت پس از آسیب دیدن
        invincibilityTimer = INVINCIBILITY_DURATION;

        // بررسی پایان بازی
        if (playerHealth <= 0) {
            playerHealth = 0;
            gameOver = true;
            controller.endGame();
        }
    }

    private void updatePlayerSpeed() {
        // سرعت پایه از نوع قهرمان
        playerSpeed = basePlayerSpeed;

        // اعمال قابلیت Speed Boost
        if (hasAbility(AbilityType.SPEEDY)) {
            playerSpeed *= 1.2f;
        }

        // اعمال ضریب سرعت موقت
        playerSpeed *= speedMultiplier;
    }

    private boolean hasAbility(AbilityType type) {
        return playerAbilities.contains(type, true);
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

    private void renderAbilitySelection() {
        // تنظیم دوربین UI
        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // رسم پس‌زمینه نیمه‌شفاف
        Texture pixelTexture = new Texture(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pixelTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.setColor(Color.WHITE);

        // رسم عنوان
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(2);
        titleFont.setColor(Color.GOLD);
        String title = "LEVEL UP! Choose an ability:";
        GlyphLayout layout = new GlyphLayout(titleFont, title);
        titleFont.draw(batch, title, WORLD_WIDTH / 2 - layout.width / 2, WORLD_HEIGHT - 100);

        // رسم گزینه‌های توانایی
        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.2f);

        if (abilityChoices != null) {
            float cardWidth = 200;
            float cardHeight = 150;
            float startX = WORLD_WIDTH / 2 - (abilityChoices.length * cardWidth + (abilityChoices.length - 1) * 20) / 2;
            float startY = WORLD_HEIGHT / 2 - cardHeight / 2;

            for (int i = 0; i < abilityChoices.length; i++) {
                AbilityType ability = abilityChoices[i];
                float x = startX + i * (cardWidth + 20);
                float y = startY;

                // رسم کارت توانایی با رنگ دسته‌بندی
                batch.setColor(ability.getCategoryColor());
                batch.draw(pixelTexture, x, y, cardWidth, cardHeight);
                batch.setColor(Color.WHITE);

                // رسم آیکون توانایی
                TextureRegion icon = ability.getTextureRegion();
                if (icon != null) {
                    batch.draw(icon, x + 10, y + cardHeight - 60, 40, 40);
                }

                // رسم عنوان توانایی
                font.setColor(Color.WHITE);
                font.draw(batch, ability.getName(), x + 60, y + cardHeight - 30);

                // رسم توضیحات توانایی
                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, ability.getEnglishDescription(), x + 10, y + cardHeight - 70, cardWidth - 20, -1, true);

                // رسم شماره گزینه
                font.setColor(Color.YELLOW);
                font.draw(batch, "Press " + (i + 1) + " to select", x + 10, y + 30);
            }
        }

        batch.end();
    }

    private void applyItemEffect(Item item) {
        switch (item.getType()) {
            case HEALTH:
                // افزایش سلامتی
                playerHealth = Math.min(playerHealth + 25, playerMaxHealth);
                break;

            case AMMO:
                // افزایش مهمات
                if (currentWeapon != null) {
                    currentWeapon.addAmmo(10);
                }
                break;

            case SPEED_BOOST:
                // افزایش سرعت موقت
                speedMultiplier = 1.5f;
                playerSpeed = basePlayerSpeed * speedMultiplier;
                speedBoostTimer = 10; // 10 ثانیه بوست سرعت
                break;

            case DAMAGE_BOOST:
                // افزایش آسیب موقت
                damageMultiplier = 2.0f;
                damageBoostTimer = 10; // 10 ثانیه بوست آسیب
                break;

            case EXPERIENCE:
                // افزایش XP
                addXP(3); // هر دانه XP، 3 امتیاز تجربه می‌دهد
                break;
        }
    }

    private void addXP(int amount) {
        playerXP += amount;

        // بررسی لول آپ
        if (playerXP >= xpToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        playerLevel++;
        playerXP -= xpToNextLevel;

        // محاسبه تجربه مورد نیاز برای لول بعدی (فرمول 20i)
        xpToNextLevel = 20 * playerLevel;

        // نمایش انتخاب توانایی
        showAbilitySelection = true;

        // انتخاب 3 توانایی تصادفی
        abilityChoices = getRandomAbilities(3);

        // توقف موقت بازی
        controller.pauseGame();
    }

    private AbilityType[] getRandomAbilities(int count) {
        AbilityType[] allTypes = AbilityType.values();

        // تعداد توانایی‌های موجود را بررسی می‌کنیم
        if (count > allTypes.length) {
            count = allTypes.length;
        }

        // انتخاب تصادفی توانایی‌ها بدون تکرار
        AbilityType[] abilities = new AbilityType[count];
        boolean[] used = new boolean[allTypes.length];

        for (int i = 0; i < count; i++) {
            int index;
            do {
                index = (int)(Math.random() * allTypes.length);
            } while (used[index]);

            used[index] = true;
            abilities[i] = allTypes[index];
        }

        return abilities;
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

        // نمایش سطح و تجربه
        font.draw(batch, String.format("Level: %d | XP: %d/%d",
                playerLevel, playerXP, xpToNextLevel),
            20, WORLD_HEIGHT - 60);

        // نمایش وضعیت auto-aim
        String autoAimStatus = autoAim ? "ON" : "OFF";
        font.draw(batch, "Auto-Aim: " + autoAimStatus, 20, WORLD_HEIGHT - 80);

        // نمایش بوست‌های فعال
        if (damageBoostTimer > 0) {
            font.draw(batch, String.format("Damage Boost: %.1fs", damageBoostTimer),
                20, WORLD_HEIGHT - 100);
        }

        if (speedBoostTimer > 0) {
            font.draw(batch, String.format("Speed Boost: %.1fs", speedBoostTimer),
                20, WORLD_HEIGHT - 120);
        }

        // نمایش توانایی‌های فعال
        font.setColor(Color.CYAN);
        font.draw(batch, "Abilities:", WORLD_WIDTH - 300, WORLD_HEIGHT - 60);

        // نمایش توانایی‌ها با آیکون و رنگ دسته‌بندی
        float abilityIconSize = 20;
        float abilitySpacing = 5;
        float startX = WORLD_WIDTH - 280;
        float startY = WORLD_HEIGHT - 80;

        for (int i = 0; i < playerAbilities.size; i++) {
            AbilityType ability = playerAbilities.get(i);
            float x = startX;
            float y = startY - (i * (abilityIconSize + abilitySpacing));

            // رسم پس‌زمینه با رنگ دسته‌بندی
            Texture pixelTexture = new Texture(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            batch.setColor(ability.getCategoryColor());
            batch.draw(pixelTexture, x, y, abilityIconSize, abilityIconSize);

            // رسم آیکون توانایی اگر موجود باشد
            TextureRegion icon = ability.getTextureRegion();
            if (icon != null) {
                batch.setColor(Color.WHITE);
                batch.draw(icon, x, y, abilityIconSize, abilityIconSize);
            }

            // رسم نام توانایی
            batch.setColor(Color.WHITE);
            font.draw(batch, ability.getName(), x + abilityIconSize + 5, y + abilityIconSize - 2);
        }

        // نمایش قهرمان انتخاب شده
        font.setColor(Color.WHITE);
        if (selectedHero != null) {
            font.draw(batch, "Hero: " + selectedHero.getName(),
                WORLD_WIDTH - 300, WORLD_HEIGHT - 100 - (Math.max(playerAbilities.size, 0) * (abilityIconSize + abilitySpacing)));
        }

        // نمایش وضعیت کنترل‌ها در پایین صفحه
        font.setColor(Color.LIGHT_GRAY);
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
