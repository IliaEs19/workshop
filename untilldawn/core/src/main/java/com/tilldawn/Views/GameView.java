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
    private int playerKills = 0; // تعداد kill های بازیکن
    private float lightRadius = 100f;
    private Texture lightTexture;

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
    private int xpToNextLevel = 3;
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


    //pause menu
    private boolean isPaused = false;
    private Texture pixelTexture;

    // گزینه فعلی انتخاب شده در منو
    private int pauseMenuSelectedOption = 0;

    // گزینه‌های منو
    private final String[] pauseMenuOptions = {
        "Resume Game",
        "Cheat Codes",
        "View Abilities",
        "Exit Game"
    };

    // تکسچر برای پس‌زمینه منو
    private Texture pauseMenuBackground;

    // رنگ‌های منو
    private final Color MENU_BACKGROUND_COLOR = new Color(0, 0.2f, 0.3f, 0.85f);
    private final Color MENU_TITLE_COLOR = new Color(0.9f, 0.9f, 0.3f, 1);
    private final Color MENU_OPTION_COLOR = new Color(0.8f, 0.8f, 0.8f, 1);
    private final Color MENU_SELECTED_COLOR = new Color(0.2f, 0.8f, 0.2f, 1);
    private final Color MENU_KEY_COLOR = new Color(0.9f, 0.6f, 0.3f, 1);

    // فونت‌های منو
    private BitmapFont titleFont;
    private BitmapFont optionFont;
    private BitmapFont descriptionFont;

    // نمایش کدهای تقلب
    private boolean showingCheatCodes = false;

    // نمایش توانایی‌ها
    private boolean showingAbilities = false;

    // زمان انیمیشن
    private float menuAnimationTime = 0;

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

    private void loadMenuAssets() {
        // ایجاد تکسچر پیکسل مشترک برای همه منوها
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixelTexture = new Texture(pixmap);
        pixmap.dispose();

        // لود تکسچر پس‌زمینه منو (اگر نیاز دارید)
        // pauseMenuBackground = new Texture(Gdx.files.internal("backgrounds/PAUSEMENU.png"));

        // ایجاد فونت‌ها
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);

        optionFont = new BitmapFont();
        optionFont.getData().setScale(1.8f);

        descriptionFont = new BitmapFont();
        descriptionFont.getData().setScale(1.2f);
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
        createLightTexture();
    }

    private void createLightTexture() {
        int size = 512; // اندازه تکسچر نور
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(size, size, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);

        // ایجاد گرادیان دایره‌ای
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float distX = x - size/2f;
                float distY = y - size/2f;
                float dist = (float) Math.sqrt(distX * distX + distY * distY);

                // نرمال‌سازی فاصله
                float normalized = Math.min(dist / (size/2f), 1f);

                // محاسبه آلفا (شفافیت) بر اساس فاصله از مرکز
                float alpha = 1f - normalized;
                alpha = Math.max(0, alpha);

                // تنظیم رنگ پیکسل
                pixmap.setColor(1, 1, 1, alpha * 0.5f); // رنگ سفید با آلفای متغیر
                pixmap.drawPixel(x, y);
            }
        }

        lightTexture = new Texture(pixmap);
        pixmap.dispose();
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
                // اگر صفحه انتخاب توانایی نمایش داده می‌شود، کلیدهای 1، 2، 3 را بررسی کن
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

                // اگر منو در حالت نمایش است، کلیدهای منو را بررسی کن
                if (isPaused) {
                    switch (keycode) {
                        case Keys.UP:
                            navigatePauseMenu(-1);
                            return true;
                        case Keys.DOWN:
                            navigatePauseMenu(1);
                            return true;
                        case Keys.ENTER:
                            selectPauseMenuOption();
                            return true;
                        case Keys.ESCAPE:
                        case Keys.P:
                            togglePauseMenu();
                            return true;
                        case Keys.BACK:
                            if (showingCheatCodes || showingAbilities) {
                                showingCheatCodes = false;
                                showingAbilities = false;
                                return true;
                            }
                            break;
                    }
                    return false;
                }

                // کلیدهای عادی بازی
                switch (keycode) {
                    case Keys.W: keyW = true; break;
                    case Keys.A: keyA = true; break;
                    case Keys.S: keyS = true; break;
                    case Keys.D: keyD = true; break;
                    case Keys.P: togglePauseMenu(); break;
                    case Keys.ESCAPE: togglePauseMenu(); break;
                    case Keys.SPACE: toggleAutoAim(); break;
                    case Keys.R: startReload(); break;
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                // اگر منو در حالت نمایش است، کلیدهای بازی را نادیده بگیر
                if (isPaused || showAbilitySelection) {
                    return false;
                }

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
                // اگر منو در حالت نمایش است، کلیک ماوس را برای منو بررسی کن
                if (isPaused) {
                    if (button == Buttons.LEFT) {
                        checkPauseMenuClick(screenX, screenY);
                    }
                    return true;
                }

                // اگر صفحه انتخاب توانایی نمایش داده می‌شود، بررسی کن که آیا روی یک توانایی کلیک شده است
                if (showAbilitySelection) {
                    if (button == Buttons.LEFT) {
                        checkAbilityClick(screenX, screenY);
                    }
                    return true;
                }

                if (button == Buttons.LEFT) {
                    mouseLeft = true;
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                // اگر منو در حالت نمایش است، کلیک ماوس را نادیده بگیر
                if (isPaused || showAbilitySelection) {
                    return false;
                }

                if (button == Buttons.LEFT) {
                    mouseLeft = false;
                }
                return true;
            }
        });
    }

    private void togglePauseMenu() {
        isPaused = !isPaused;
        showingCheatCodes = false;
        showingAbilities = false;
        menuAnimationTime = 0;

        if (isPaused) {
            pauseMenuSelectedOption = 0;
        }
    }

    /**
     * جابجایی بین گزینه‌های منوی pause
     * @param direction جهت جابجایی (1 برای پایین، -1 برای بالا)
     */
    private void navigatePauseMenu(int direction) {
        pauseMenuSelectedOption += direction;

        if (pauseMenuSelectedOption < 0) {
            pauseMenuSelectedOption = pauseMenuOptions.length - 1;
        } else if (pauseMenuSelectedOption >= pauseMenuOptions.length) {
            pauseMenuSelectedOption = 0;
        }
    }

    /**
     * انتخاب گزینه فعلی منوی pause
     */
    private void selectPauseMenuOption() {
        switch (pauseMenuSelectedOption) {
            case 0: // Resume Game
                togglePauseMenu();
                break;
            case 1: // Cheat Codes
                showingCheatCodes = true;
                showingAbilities = false;
                break;
            case 2: // View Abilities
                showingAbilities = true;
                showingCheatCodes = false;
                break;
            case 3: // Exit Game
                gameOver = true;
                controller.exitGame();
                break;
        }
    }

    /**
     * بررسی کلیک روی گزینه‌های منوی pause
     * @param screenX مختصات X کلیک
     * @param screenY مختصات Y کلیک
     */
    private void checkPauseMenuClick(int screenX, int screenY) {
        // اگر در حال نمایش کدهای تقلب یا توانایی‌ها هستیم، بررسی کن که آیا روی دکمه بازگشت کلیک شده است
        if (showingCheatCodes || showingAbilities) {
            // تبدیل مختصات صفحه به مختصات دوربین UI
            Vector3 touchPoint = new Vector3(screenX, screenY, 0);
            uiViewport.unproject(touchPoint);

            // بررسی کلیک روی دکمه بازگشت
            float backButtonX = WORLD_WIDTH - 100;
            float backButtonY = 50;
            float backButtonWidth = 80;
            float backButtonHeight = 40;

            if (touchPoint.x >= backButtonX && touchPoint.x <= backButtonX + backButtonWidth &&
                touchPoint.y >= backButtonY && touchPoint.y <= backButtonY + backButtonHeight) {
                showingCheatCodes = false;
                showingAbilities = false;
                return;
            }

            return;
        }

        // تبدیل مختصات صفحه به مختصات دوربین UI
        Vector3 touchPoint = new Vector3(screenX, screenY, 0);
        uiViewport.unproject(touchPoint);

        // محاسبه موقعیت گزینه‌های منو
        float menuWidth = 400;
        float menuHeight = 300;
        float menuX = WORLD_WIDTH / 2 - menuWidth / 2;
        float menuY = WORLD_HEIGHT / 2 - menuHeight / 2;

        float optionHeight = 50;
        float optionSpacing = 10;
        float optionsStartY = menuY + menuHeight - 120;

        for (int i = 0; i < pauseMenuOptions.length; i++) {
            float optionY = optionsStartY - i * (optionHeight + optionSpacing);

            if (touchPoint.x >= menuX + 20 && touchPoint.x <= menuX + menuWidth - 20 &&
                touchPoint.y >= optionY - optionHeight && touchPoint.y <= optionY) {
                pauseMenuSelectedOption = i;
                selectPauseMenuOption();
                return;
            }
        }
    }

    private void renderPauseMenu() {
        if (!isPaused) return;

        // تنظیم دوربین UI
        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // رسم پس‌زمینه نیمه‌شفاف
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pixelTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // اگر در حال نمایش کدهای تقلب هستیم
        if (showingCheatCodes) {
            renderCheatCodes();
        }
        // اگر در حال نمایش توانایی‌ها هستیم
        else if (showingAbilities) {
            renderAbilitiesList();
        }
        // در غیر این صورت، منوی اصلی را نمایش بده
        else {
            renderMainPauseMenu();
        }

        batch.end();
    }

    /**
     * رسم منوی اصلی pause
     */
    private void renderMainPauseMenu() {
        // محاسبه موقعیت و اندازه منو
        float menuWidth = 400;
        float menuHeight = 300;
        float menuX = WORLD_WIDTH / 2 - menuWidth / 2;
        float menuY = WORLD_HEIGHT / 2 - menuHeight / 2;

        // افکت انیمیشن ظاهر شدن منو
        float scale = Math.min(1, menuAnimationTime * 3);
        float actualMenuWidth = menuWidth * scale;
        float actualMenuHeight = menuHeight * scale + 110;
        float actualMenuX = WORLD_WIDTH / 2 - actualMenuWidth / 2;
        float actualMenuY = WORLD_HEIGHT / 2 - actualMenuHeight / 2;

        // رسم پس‌زمینه منو (یک مستطیل شیک با حاشیه)
        // پس‌زمینه اصلی
        batch.setColor(0.1f, 0.1f, 0.2f, 0.9f);
        batch.draw(pixelTexture, actualMenuX, actualMenuY, actualMenuWidth, actualMenuHeight);

        // حاشیه منو
        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        float borderThickness = 2;
        // حاشیه بالا
        batch.draw(pixelTexture, actualMenuX, actualMenuY + actualMenuHeight - borderThickness, actualMenuWidth, borderThickness);
        // حاشیه پایین
        batch.draw(pixelTexture, actualMenuX, actualMenuY, actualMenuWidth, borderThickness);
        // حاشیه چپ
        batch.draw(pixelTexture, actualMenuX, actualMenuY, borderThickness, actualMenuHeight);
        // حاشیه راست
        batch.draw(pixelTexture, actualMenuX + actualMenuWidth - borderThickness, actualMenuY, borderThickness, actualMenuHeight);

        // اگر منو هنوز در حال انیمیشن است، منتظر بمان
        if (scale < 1) {
            return;
        }

        // رسم عنوان منو
        titleFont.setColor(MENU_TITLE_COLOR);
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "GAME PAUSED");
        titleFont.draw(batch, titleLayout,
            menuX + menuWidth / 2 - titleLayout.width / 2,
            menuY + menuHeight - 30);

        // خط جداکننده زیر عنوان
        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        batch.draw(pixelTexture, menuX + 20, menuY + menuHeight - 70, menuWidth - 40, 1);

        // رسم گزینه‌های منو
        float optionHeight = 70;
        float optionSpacing = 10;
        float optionsStartY = menuY + menuHeight - 80;

        for (int i = 0; i < pauseMenuOptions.length; i++) {
            float optionY = optionsStartY - i * (optionHeight + optionSpacing);

            // رسم پس‌زمینه گزینه انتخاب شده
            if (i == pauseMenuSelectedOption) {
                // پس‌زمینه گزینه انتخاب شده با گرادیان
                batch.setColor(0.2f, 0.3f, 0.5f, 0.7f);
                batch.draw(pixelTexture, menuX + 20, optionY - optionHeight, menuWidth - 40, optionHeight);

                // حاشیه گزینه انتخاب شده
                batch.setColor(0.4f, 0.6f, 1f, 1);
                // حاشیه بالا
                batch.draw(pixelTexture, menuX + 20, optionY, menuWidth - 40, 1);
                // حاشیه پایین
                batch.draw(pixelTexture, menuX + 20, optionY - optionHeight, menuWidth - 40, 1);
                // حاشیه چپ
                batch.draw(pixelTexture, menuX + 20, optionY - optionHeight, 1, optionHeight);
                // حاشیه راست
                batch.draw(pixelTexture, menuX + menuWidth - 20 - 1, optionY - optionHeight, 1, optionHeight);
            }

            // رسم متن گزینه
            optionFont.setColor(i == pauseMenuSelectedOption ? Color.WHITE : MENU_OPTION_COLOR);
            optionFont.draw(batch, pauseMenuOptions[i], menuX + 40, optionY - 15);

            // رسم کلید میانبر
            String shortcutKey = "";
            switch (i) {
                case 0: shortcutKey = "ESC"; break;
                case 1: shortcutKey = "C"; break;
                case 2: shortcutKey = "A"; break;
                case 3: shortcutKey = "Q"; break;
            }

            optionFont.setColor(MENU_KEY_COLOR);
            GlyphLayout keyLayout = new GlyphLayout(optionFont, shortcutKey);
            optionFont.draw(batch, shortcutKey,
                menuX + menuWidth - 60 - keyLayout.width,
                optionY - 15);
        }

        // رسم راهنما در پایین منو
        descriptionFont.setColor(Color.LIGHT_GRAY);
        descriptionFont.draw(batch, "Use arrow keys to navigate and Enter to select",
            menuX + 20, menuY - 70);
    }

    /**
     * رسم صفحه کدهای تقلب
     */
    private void renderCheatCodes() {
        // محاسبه موقعیت و اندازه منو
        float menuWidth = 500;
        float menuHeight = 400;
        float menuX = WORLD_WIDTH / 2 - menuWidth / 2;
        float menuY = WORLD_HEIGHT / 2 - menuHeight / 2;

        // رسم پس‌زمینه منو
        batch.setColor(0.1f, 0.1f, 0.2f, 0.9f);
        batch.draw(pixelTexture, menuX, menuY, menuWidth, menuHeight);

        // حاشیه منو
        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        float borderThickness = 2;
        // حاشیه بالا
        batch.draw(pixelTexture, menuX, menuY + menuHeight - borderThickness, menuWidth, borderThickness);
        // حاشیه پایین
        batch.draw(pixelTexture, menuX, menuY, menuWidth, borderThickness);
        // حاشیه چپ
        batch.draw(pixelTexture, menuX, menuY, borderThickness, menuHeight);
        // حاشیه راست
        batch.draw(pixelTexture, menuX + menuWidth - borderThickness, menuY, borderThickness, menuHeight);

        // رسم عنوان
        titleFont.setColor(MENU_TITLE_COLOR);
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "CHEAT CODES");
        titleFont.draw(batch, titleLayout,
            menuX + menuWidth / 2 - titleLayout.width / 2,
            menuY + menuHeight - 30);

        // خط جداکننده زیر عنوان
        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        batch.draw(pixelTexture, menuX + 20, menuY + menuHeight - 70, menuWidth - 40, 1);

        // رسم توضیحات
        descriptionFont.setColor(Color.WHITE);
        descriptionFont.draw(batch, "This section will be implemented later.",
            menuX + 20, menuY + menuHeight - 80);

        // رسم دکمه بازگشت
        batch.setColor(0.3f, 0.3f, 0.5f, 0.9f);
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 80, 40);

        // حاشیه دکمه بازگشت
        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        // حاشیه بالا
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50 + 40, 80, 1);
        // حاشیه پایین
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 80, 1);
        // حاشیه چپ
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 1, 40);
        // حاشیه راست
        batch.draw(pixelTexture, WORLD_WIDTH - 100 + 80 - 1, 50, 1, 40);

        optionFont.setColor(Color.WHITE);
        GlyphLayout backLayout = new GlyphLayout(optionFont, "Back");
        optionFont.draw(batch, backLayout,
            WORLD_WIDTH - 100 + 40 - backLayout.width / 2,
            50 + 25);
    }

    /**
     * رسم لیست توانایی‌های بازیکن
     */
    private void renderAbilitiesList() {
        // محاسبه موقعیت و اندازه منو
        float menuWidth = 500;
        float menuHeight = 400;
        float menuX = WORLD_WIDTH / 2 - menuWidth / 2;
        float menuY = WORLD_HEIGHT / 2 - menuHeight / 2;

        // رسم پس‌زمینه منو
        batch.setColor(0.1f, 0.1f, 0.2f, 0.9f);
        batch.draw(pixelTexture, menuX, menuY, menuWidth, menuHeight);

        // حاشیه منو
        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        float borderThickness = 2;
        // حاشیه بالا
        batch.draw(pixelTexture, menuX, menuY + menuHeight - borderThickness, menuWidth, borderThickness);
        // حاشیه پایین
        batch.draw(pixelTexture, menuX, menuY, menuWidth, borderThickness);
        // حاشیه چپ
        batch.draw(pixelTexture, menuX, menuY, borderThickness, menuHeight);
        // حاشیه راست
        batch.draw(pixelTexture, menuX + menuWidth - borderThickness, menuY, borderThickness, menuHeight);

        // رسم عنوان
        titleFont.setColor(MENU_TITLE_COLOR);
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "YOUR ABILITIES");
        titleFont.draw(batch, titleLayout,
            menuX + menuWidth / 2 - titleLayout.width / 2,
            menuY + menuHeight - 30);

        // خط جداکننده زیر عنوان
        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        batch.draw(pixelTexture, menuX + 20, menuY + menuHeight - 70, menuWidth - 40, 1);

        // رسم لیست توانایی‌ها
        if (playerAbilities.size == 0) {
            descriptionFont.setColor(Color.LIGHT_GRAY);
            descriptionFont.draw(batch, "You haven't acquired any abilities yet.",
                menuX + 20, menuY + menuHeight - 80);
        } else {
            float abilityCardWidth = 460;
            float abilityCardHeight = 75;
            float abilityCardSpacing = 10;
            float abilitiesStartY = menuY + menuHeight - 80;

            for (int i = 0; i < playerAbilities.size; i++) {
                AbilityType ability = playerAbilities.get(i);
                float cardY = abilitiesStartY - i * (abilityCardHeight + abilityCardSpacing);

                // رسم پس‌زمینه کارت توانایی
                batch.setColor(ability.getCategoryColor());
                batch.draw(pixelTexture, menuX + 20, cardY - abilityCardHeight, abilityCardWidth, abilityCardHeight);

                // رسم آیکون توانایی
                TextureRegion icon = ability.getTextureRegion();
                if (icon != null) {
                    batch.setColor(Color.WHITE);
                    batch.draw(icon, menuX + 30, cardY - abilityCardHeight + 10, 40, 40);
                }

                // رسم نام توانایی
                optionFont.setColor(Color.WHITE);
                optionFont.draw(batch, ability.getName(), menuX + 80, cardY - 20);

                // رسم توضیحات توانایی
                descriptionFont.setColor(Color.LIGHT_GRAY);
                descriptionFont.draw(batch, ability.getEnglishDescription(),
                    menuX + 80, cardY - 40, 380, -1, true);
            }
        }

        // رسم دکمه بازگشت
        batch.setColor(0.3f, 0.3f, 0.5f, 0.9f);
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 80, 40);

        // حاشیه دکمه بازگشت
        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        // حاشیه بالا
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50 + 40, 80, 1);
        // حاشیه پایین
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 80, 1);
        // حاشیه چپ
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 1, 40);
        // حاشیه راست
        batch.draw(pixelTexture, WORLD_WIDTH - 100 + 80 - 1, 50, 1, 40);

        optionFont.setColor(Color.WHITE);
        GlyphLayout backLayout = new GlyphLayout(optionFont, "Back");
        optionFont.draw(batch, backLayout,
            WORLD_WIDTH - 100 + 40 - backLayout.width / 2,
            50 + 25);
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

        // اگر auto-aim غیرفعال شد، کرسر را به موقعیت قبلی برگردان
        if (!autoAim) {
            // موقعیت فعلی موس را حفظ کن
            Vector3 mouseScreenPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(mouseScreenPos);
            mousePosition.set(mouseScreenPos);
        }
    }

    private void startReload() {
        if (currentWeapon != null) {
            currentWeapon.startReload();
        }
    }

    @Override
    public void render(float delta) {
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

        // بازنشانی رنگ batch به حالت پیش‌فرض
        batch.setColor(Color.WHITE);

        // رسم پس‌زمینه تکرارشونده
        drawRepeatingBackground();

        // رسم ناحیه روشن اطراف بازیکن
        if (lightTexture != null) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            float lightSize = lightRadius * 2;
            batch.draw(lightTexture,
                playerPosition.x - lightSize/2,
                playerPosition.y - lightSize/2,
                lightSize, lightSize);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }

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

        if (autoAim) {
            Enemy target = findNearestEnemy();
            if (target != null) {
                drawTargetIndicator(target);
            }
        }

        // رسم صفحه انتخاب توانایی اگر فعال است
        if (showAbilitySelection) {
            renderAbilitySelection();
        }

        // رسم منوی pause اگر فعال است
        if (isPaused) {
            renderPauseMenu();
        }
    }

    private void drawTargetIndicator(Enemy target) {
        // تنظیم دوربین بازی
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // رسم نشانگر هدف
        batch.setColor(1, 0, 0, 0.7f);
        Vector2 pos = target.getPosition();
        float size = 15;

        // استفاده از نسخه متد draw که با Texture کار می‌کند
        if (pixelTexture != null) {
            batch.draw(pixelTexture,
                pos.x - size/2, pos.y - size/2,
                size, size);
        }

        // بازنشانی رنگ
        batch.setColor(Color.WHITE);
        batch.end();
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

        if (isPaused) {
            menuAnimationTime += delta;
            return;
        }

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

        Enemy nearestEnemy = null;
        if (autoAim) {
            nearestEnemy = findNearestEnemy();
            if (nearestEnemy != null) {
                Vector2 enemyPos = nearestEnemy.getPosition();
                targetX = enemyPos.x;
                targetY = enemyPos.y;

                // تنظیم موقعیت ماوس روی دشمن هدف (این بخش اضافه شده)
                Vector3 screenPos = new Vector3(targetX, targetY, 0);
                camera.project(screenPos);
                Gdx.input.setCursorPosition((int)screenPos.x, (int)screenPos.y);
            }
        }


        // بروزرسانی سلاح - مستقل از حرکت بازیکن
        if (currentWeapon != null) {
            currentWeapon.update(delta, playerPosition, targetX, targetY);

            // شلیک با کلیک چپ ماوس یا در حالت auto-aim
            if (mouseLeft || (autoAim && nearestEnemy != null)) {
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
            enemyManager.checkBulletCollisions(currentWeapon.getBullets(), this);
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
        float maxRange = 800f; // حداکثر فاصله برای هدف‌گیری خودکار

        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy.isAlive()) {
                Vector2 enemyPos = enemy.getPosition();
                float dx = enemyPos.x - playerPosition.x;
                float dy = enemyPos.y - playerPosition.y;
                float distanceSquared = dx * dx + dy * dy;

                // فقط دشمنانی که در محدوده مشخصی هستند را در نظر بگیر
                if (distanceSquared < maxRange * maxRange && distanceSquared < minDistanceSquared) {
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
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pixelTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.setColor(Color.WHITE);

        // رسم عنوان
        titleFont.setColor(Color.GOLD);
        String title = "LEVEL UP! Choose an ability:";
        GlyphLayout layout = new GlyphLayout(titleFont, title);
        titleFont.draw(batch, title, WORLD_WIDTH / 2 - layout.width / 2, WORLD_HEIGHT - 100);

        // رسم گزینه‌های توانایی
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

                // حاشیه کارت
                batch.setColor(Color.WHITE);
                float borderThickness = 1;
                // حاشیه بالا
                batch.draw(pixelTexture, x, y + cardHeight - borderThickness, cardWidth, borderThickness);
                // حاشیه پایین
                batch.draw(pixelTexture, x, y, cardWidth, borderThickness);
                // حاشیه چپ
                batch.draw(pixelTexture, x, y, borderThickness, cardHeight);
                // حاشیه راست
                batch.draw(pixelTexture, x + cardWidth - borderThickness, y, borderThickness, cardHeight);

                batch.setColor(Color.WHITE);

                // رسم آیکون توانایی
                TextureRegion icon = ability.getTextureRegion();
                if (icon != null) {
                    batch.draw(icon, x + 10, y + cardHeight - 60, 40, 40);
                }

                // رسم عنوان توانایی
                BitmapFont font = new BitmapFont();
                font.getData().setScale(1.2f);
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

    public void addKill() {
        playerKills++;
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

        font.draw(batch, String.format("Kills: %d", playerKills),
            20, WORLD_HEIGHT - 60);

        // نمایش سطح و تجربه
        font.draw(batch, String.format("Level: %d | XP: %d/%d",
                playerLevel, playerXP, xpToNextLevel),
            20, WORLD_HEIGHT - 80);

        float xpBarWidth = 150;
        float xpBarHeight = 10;
        float xpBarX = 120;
        float xpBarY = WORLD_HEIGHT - 85;

        // رسم پس‌زمینه نوار XP
        batch.setColor(0.3f, 0.3f, 0.3f, 1);
        batch.draw(pixelTexture, xpBarX + 30, xpBarY - 5, xpBarWidth, xpBarHeight);

        // رسم نوار XP پر شده
        float fillWidth = (float)playerXP / xpToNextLevel * xpBarWidth;
        batch.setColor(0.2f, 0.7f, 1f, 1);
        batch.draw(pixelTexture, xpBarX + 30, xpBarY - 5, fillWidth, xpBarHeight);

        // رسم متن XP
        font.setColor(1, 1, 1, 1);
        font.draw(batch, String.format("XP: %d/%d", playerXP, xpToNextLevel),
            xpBarX + xpBarWidth + 40, xpBarY + xpBarHeight - 2);

        // نمایش وضعیت auto-aim
        String autoAimStatus = autoAim ? "ON" : "OFF";
        font.draw(batch, "Auto-Aim: " + autoAimStatus, 20, WORLD_HEIGHT - 100);

        // نمایش بوست‌های فعال
        if (damageBoostTimer > 0) {
            font.draw(batch, String.format("Damage Boost: %.1fs", damageBoostTimer),
                20, WORLD_HEIGHT - 120);
        }

        if (speedBoostTimer > 0) {
            font.draw(batch, String.format("Speed Boost: %.1fs", speedBoostTimer),
                20, WORLD_HEIGHT - 140);
        }

        // نمایش توانایی‌های فعال
        font.setColor(Color.CYAN);
        font.draw(batch, "Abilities:", WORLD_WIDTH - 300, WORLD_HEIGHT - 60);

        // نمایش توانایی‌ها با آیکون و رنگ دسته‌بندی
        float abilityIconSize = 20;
        float abilitySpacing = 5;
        float startX = WORLD_WIDTH - 280;
        float startY = WORLD_HEIGHT - 100;

        for (int i = 0; i < playerAbilities.size; i++) {
            AbilityType ability = playerAbilities.get(i);
            float x = startX;
            float y = startY - (i * (abilityIconSize + abilitySpacing));

            // رسم پس‌زمینه با رنگ دسته‌بندی
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
        loadMenuAssets();
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        playerTexture.dispose();

        if (pixelTexture != null) {
            pixelTexture.dispose();
        }

        if (titleFont != null) {
            titleFont.dispose();
        }

        if (optionFont != null) {
            optionFont.dispose();
        }

        if (descriptionFont != null) {
            descriptionFont.dispose();
        }

        if (currentWeapon != null) {
            currentWeapon.dispose();
        }

        if (lightTexture != null) {
            lightTexture.dispose();
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
