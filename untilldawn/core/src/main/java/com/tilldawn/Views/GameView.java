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
import com.tilldawn.Models.*;
import com.tilldawn.Models.Enemy.Enemy;
import com.tilldawn.Models.Enemy.EnemyManager;
import com.tilldawn.Models.Hero.AbilityType;
import com.tilldawn.Models.Hero.HeroType;
import com.tilldawn.Models.Hero.WeaponType;
import com.tilldawn.Models.Item.Item;

import static com.tilldawn.Models.Item.ItemType.DAMAGE_BOOST;

public class GameView implements Screen {

    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;
    private static final float PLAYER_SPEED = 200;


    private static final float GAME_WORLD_WIDTH = 3000;
    private static final float GAME_WORLD_HEIGHT = 3000;


    private static final float PLAYER_WIDTH = 64;
    private static final float PLAYER_HEIGHT = 64;


    private final GameController controller;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera uiCamera;
    private Viewport viewport;
    private Viewport uiViewport;


    private Texture backgroundTexture;
    private Texture playerTexture;
    private TextureRegion currentPlayerFrame;
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;


    private Vector2 playerPosition;
    private Vector2 playerVelocity;
    private Rectangle playerBounds;
    private boolean isPlayerMoving;
    private PlayerDirection playerDirection;
    private int playerKills = 0;
    private float lightRadius = 100f;
    private Texture lightTexture;


    private Weapon currentWeapon;
    private boolean autoAim = false;


    private Vector3 mousePosition = new Vector3();
    private boolean canShoot = true;


    private float playerHealth;
    private float playerMaxHealth;
    private float playerSpeed;
    private float basePlayerSpeed;
    private float damageMultiplier = 1.0f;
    private float speedMultiplier = 1.0f;
    private float invincibilityTimer = 0;
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



    private HeroType selectedHero;
    private WeaponType selectedWeapon;
    private int gameTimeMinutes;
    private float gameTimeElapsed = 0;
    private boolean gameOver = false;



    private boolean isPaused = false;
    private Texture pixelTexture;


    private int pauseMenuSelectedOption = 0;


    private final String[] pauseMenuOptions = {
        "Resume Game",
        "Cheat Codes",
        "View Abilities",
        "Exit Game"
    };


    private Texture pauseMenuBackground;


    private final Color MENU_BACKGROUND_COLOR = new Color(0, 0.2f, 0.3f, 0.85f);
    private final Color MENU_TITLE_COLOR = new Color(0.9f, 0.9f, 0.3f, 1);
    private final Color MENU_OPTION_COLOR = new Color(0.8f, 0.8f, 0.8f, 1);
    private final Color MENU_SELECTED_COLOR = new Color(0.2f, 0.8f, 0.2f, 1);
    private final Color MENU_KEY_COLOR = new Color(0.9f, 0.6f, 0.3f, 1);


    private BitmapFont titleFont;
    private BitmapFont optionFont;
    private BitmapFont descriptionFont;


    private boolean showingCheatCodes = false;


    private boolean showingAbilities = false;


    private float menuAnimationTime = 0;


    private enum PlayerDirection {
        UP, DOWN, LEFT, RIGHT, UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }


    private boolean keyW = false;
    private boolean keyA = false;
    private boolean keyS = false;
    private boolean keyD = false;
    private boolean keyUp = false;
    private boolean keyDown = false;
    private boolean keyLeft = false;
    private boolean keyRight = false;
    private boolean mouseLeft = false;
    private boolean keySpace = false;
    private boolean keyR = false;

    private CheatManager cheatManager;


    public GameView(GameController controller, HeroType hero, WeaponType weapon, int timeMinutes) {
        this.controller = controller;
        this.selectedHero = hero;
        this.selectedWeapon = weapon;
        this.gameTimeMinutes = timeMinutes;
        cheatManager = new CheatManager(this);
        if (hero != null) {

            this.playerMaxHealth = hero.getHealthPoints() * 25;
            this.playerHealth = this.playerMaxHealth;
            this.basePlayerSpeed = hero.getSpeed() * 40;
            this.playerSpeed = this.basePlayerSpeed;
        }

        batch = new SpriteBatch();


        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);


        uiCamera = new OrthographicCamera();
        uiViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, uiCamera);
        uiCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);


        playerPosition = new Vector2(GAME_WORLD_WIDTH / 2, GAME_WORLD_HEIGHT / 2);
        playerVelocity = new Vector2();
        playerBounds = new Rectangle(
            playerPosition.x - PLAYER_WIDTH/2,
            playerPosition.y - PLAYER_HEIGHT/2,
            PLAYER_WIDTH,
            PLAYER_HEIGHT
        );
        playerDirection = PlayerDirection.DOWN;


        if (selectedWeapon != null) {
            currentWeapon = new Weapon(selectedWeapon);
        } else {

            currentWeapon = new Weapon(WeaponType.REVOLVER);
        }

        enemyManager = new EnemyManager(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, gameTimeMinutes * 60);

        loadAssets();
        setupInput();
    }

    private void loadMenuAssets() {

        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixelTexture = new Texture(pixmap);
        pixmap.dispose();





        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.5f);

        optionFont = new BitmapFont();
        optionFont.getData().setScale(1.8f);

        descriptionFont = new BitmapFont();
        descriptionFont.getData().setScale(1.2f);
    }

    private void loadAssets() {

        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/game.png"));
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);


        if (selectedHero != null && selectedHero.getTextureRegion() != null) {
            playerTexture = selectedHero.getTextureRegion().getTexture();
            currentPlayerFrame = selectedHero.getTextureRegion();
        } else {
            playerTexture = new Texture(Gdx.files.internal("heroes/character3.png"));
            currentPlayerFrame = new TextureRegion(playerTexture);
        }


        Array<TextureRegion> frames = new Array<>();
        frames.add(currentPlayerFrame);
        walkAnimation = new Animation<>(0.1f, frames);
        createLightTexture();
    }

    private void createLightTexture() {
        int size = 512;
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(size, size, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);


        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                float distX = x - size/2f;
                float distY = y - size/2f;
                float dist = (float) Math.sqrt(distX * distX + distY * distY);


                float normalized = Math.min(dist / (size/2f), 1f);


                float alpha = 1f - normalized;
                alpha = Math.max(0, alpha);


                pixmap.setColor(1, 1, 1, alpha * 0.5f);
                pixmap.drawPixel(x, y);
            }
        }

        lightTexture = new Texture(pixmap);
        pixmap.dispose();
    }


    public void selectAbility(int index) {
        if (showAbilitySelection && abilityChoices != null && index >= 0 && index < abilityChoices.length) {

            playerAbilities.add(abilityChoices[index]);


            applyAbilityEffects(abilityChoices[index]);


            showAbilitySelection = false;
            abilityChoices = null;


            controller.resumeGame();
        }
    }

    private void applyAbilityEffects(AbilityType ability) {
        switch (ability) {
            case VITALITY:

                playerMaxHealth += 25;
                playerHealth += 25;
                break;
            case DAMAGER:

                damageMultiplier = 1.25f;
                damageBoostTimer = 10;
                break;
            case PROCREASE:

                if (currentWeapon != null) {
                    currentWeapon.increaseProjectileCount(1);
                }
                break;
            case AMOCREASE:

                if (currentWeapon != null) {
                    currentWeapon.increaseMaxAmmo(5);
                    currentWeapon.addAmmo(5);
                }
                break;
            case SPEEDY:

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


                if (keycode >= Keys.NUM_4 && keycode <= Keys.NUM_8) {
                    int cheatKey = keycode - Keys.NUM_0;
                    if (CheatCode.isValidCheatKeyCode(cheatKey)) {
                        cheatManager.processCheatKey(cheatKey);
                        return true;
                    }
                }


                switch (keycode) {

                    case Keys.W: keyW = true; break;
                    case Keys.A: keyA = true; break;
                    case Keys.S: keyS = true; break;
                    case Keys.D: keyD = true; break;


                    case Keys.UP: keyUp = true; break;
                    case Keys.DOWN: keyDown = true; break;
                    case Keys.LEFT: keyLeft = true; break;
                    case Keys.RIGHT: keyRight = true; break;


                    case Keys.P: togglePauseMenu(); break;
                    case Keys.ESCAPE: togglePauseMenu(); break;
                    case Keys.SPACE: toggleAutoAim(); break;
                    case Keys.R: startReload(); break;
                }
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {

                if (isPaused || showAbilitySelection) {
                    return false;
                }

                switch (keycode) {

                    case Keys.W: keyW = false; break;
                    case Keys.A: keyA = false; break;
                    case Keys.S: keyS = false; break;
                    case Keys.D: keyD = false; break;


                    case Keys.UP: keyUp = false; break;
                    case Keys.DOWN: keyDown = false; break;
                    case Keys.LEFT: keyLeft = false; break;
                    case Keys.RIGHT: keyRight = false; break;

                    case Keys.R: keyR = false; break;
                }
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                if (isPaused) {
                    if (button == Buttons.LEFT) {
                        checkPauseMenuClick(screenX, screenY);
                    }
                    return true;
                }


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

        private void navigatePauseMenu(int direction) {
        pauseMenuSelectedOption += direction;

        if (pauseMenuSelectedOption < 0) {
            pauseMenuSelectedOption = pauseMenuOptions.length - 1;
        } else if (pauseMenuSelectedOption >= pauseMenuOptions.length) {
            pauseMenuSelectedOption = 0;
        }
    }

        private void selectPauseMenuOption() {
        switch (pauseMenuSelectedOption) {
            case 0:
                togglePauseMenu();
                break;
            case 1:
                showingCheatCodes = true;
                showingAbilities = false;
                break;
            case 2:
                showingAbilities = true;
                showingCheatCodes = false;
                break;
            case 3:
                gameOver = true;
                controller.giveUp();
                break;
        }
    }

        private void checkPauseMenuClick(int screenX, int screenY) {

        if (showingCheatCodes || showingAbilities) {

            Vector3 touchPoint = new Vector3(screenX, screenY, 0);
            uiViewport.unproject(touchPoint);


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


        Vector3 touchPoint = new Vector3(screenX, screenY, 0);
        uiViewport.unproject(touchPoint);


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


        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();


        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pixelTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);


        if (showingCheatCodes) {
            renderCheatCodes();
        }

        else if (showingAbilities) {
            renderAbilitiesList();
        }

        else {
            renderMainPauseMenu();
        }

        batch.end();
    }

        private void renderMainPauseMenu() {

        float menuWidth = 400;
        float menuHeight = 300;
        float menuX = WORLD_WIDTH / 2 - menuWidth / 2;
        float menuY = WORLD_HEIGHT / 2 - menuHeight / 2;


        float scale = Math.min(1, menuAnimationTime * 3);
        float actualMenuWidth = menuWidth * scale;
        float actualMenuHeight = menuHeight * scale + 110;
        float actualMenuX = WORLD_WIDTH / 2 - actualMenuWidth / 2;
        float actualMenuY = WORLD_HEIGHT / 2 - actualMenuHeight / 2;



        batch.setColor(0.1f, 0.1f, 0.2f, 0.9f);
        batch.draw(pixelTexture, actualMenuX, actualMenuY, actualMenuWidth, actualMenuHeight);


        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        float borderThickness = 2;

        batch.draw(pixelTexture, actualMenuX, actualMenuY + actualMenuHeight - borderThickness, actualMenuWidth, borderThickness);

        batch.draw(pixelTexture, actualMenuX, actualMenuY, actualMenuWidth, borderThickness);

        batch.draw(pixelTexture, actualMenuX, actualMenuY, borderThickness, actualMenuHeight);

        batch.draw(pixelTexture, actualMenuX + actualMenuWidth - borderThickness, actualMenuY, borderThickness, actualMenuHeight);


        if (scale < 1) {
            return;
        }


        titleFont.setColor(MENU_TITLE_COLOR);
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "GAME PAUSED");
        titleFont.draw(batch, titleLayout,
            menuX + menuWidth / 2 - titleLayout.width / 2,
            menuY + menuHeight - 30);


        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        batch.draw(pixelTexture, menuX + 20, menuY + menuHeight - 70, menuWidth - 40, 1);


        float optionHeight = 70;
        float optionSpacing = 10;
        float optionsStartY = menuY + menuHeight - 80;

        for (int i = 0; i < pauseMenuOptions.length; i++) {
            float optionY = optionsStartY - i * (optionHeight + optionSpacing);


            if (i == pauseMenuSelectedOption) {

                batch.setColor(0.2f, 0.3f, 0.5f, 0.7f);
                batch.draw(pixelTexture, menuX + 20, optionY - optionHeight, menuWidth - 40, optionHeight);


                batch.setColor(0.4f, 0.6f, 1f, 1);

                batch.draw(pixelTexture, menuX + 20, optionY, menuWidth - 40, 1);

                batch.draw(pixelTexture, menuX + 20, optionY - optionHeight, menuWidth - 40, 1);

                batch.draw(pixelTexture, menuX + 20, optionY - optionHeight, 1, optionHeight);

                batch.draw(pixelTexture, menuX + menuWidth - 20 - 1, optionY - optionHeight, 1, optionHeight);
            }


            optionFont.setColor(i == pauseMenuSelectedOption ? Color.WHITE : MENU_OPTION_COLOR);
            optionFont.draw(batch, pauseMenuOptions[i], menuX + 40, optionY - 15);


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


        descriptionFont.setColor(Color.LIGHT_GRAY);
        descriptionFont.draw(batch, "Use arrow keys to navigate and Enter to select",
            menuX + 20, menuY - 70);
    }

        private void renderCheatCodes() {

        float menuWidth = 500;
        float menuHeight = 500;
        float menuX = WORLD_WIDTH / 2 - menuWidth / 2;
        float menuY = WORLD_HEIGHT / 2 - menuHeight / 2;


        batch.setColor(0.1f, 0.1f, 0.2f, 0.9f);
        batch.draw(pixelTexture, menuX, menuY, menuWidth, menuHeight);


        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        float borderThickness = 2;

        batch.draw(pixelTexture, menuX, menuY + menuHeight - borderThickness, menuWidth, borderThickness);

        batch.draw(pixelTexture, menuX, menuY, menuWidth, borderThickness);

        batch.draw(pixelTexture, menuX, menuY, borderThickness, menuHeight);

        batch.draw(pixelTexture, menuX + menuWidth - borderThickness, menuY, borderThickness, menuHeight);


        BitmapFont font = new BitmapFont();
        font.setColor(1, 0.8f, 0.2f, 1);
        font.getData().setScale(2.0f);

        String title = "CHEAT CODES";
        GlyphLayout titleLayout = new GlyphLayout(font, title);
        font.draw(batch, title,
            menuX + menuWidth / 2 - titleLayout.width / 2,
            menuY + menuHeight - 30);


        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        batch.draw(pixelTexture, menuX + 20, menuY + menuHeight - 70, menuWidth - 40, 1);


        font.getData().setScale(1.2f);


        float textY = menuY + menuHeight - 100;
        float lineHeight = 35;


        font.setColor(1, 1, 0, 1);
        font.draw(batch, "Press the number key during gameplay to activate a cheat:",
            menuX + 30, textY);
        textY -= lineHeight * 1.5f;


        for (CheatCode cheat : CheatCode.values()) {

            font.setColor(1, 1, 1, 1);
            font.draw(batch, "Key " + cheat.getKeyCode() + ":", menuX + 30, textY);


            font.setColor(0, 1, 1, 1);
            font.draw(batch, cheat.getName(), menuX + 100, textY);


            font.setColor(0.8f, 0.8f, 0.8f, 1);
            font.draw(batch, cheat.getDescription(), menuX + 30, textY - 20);

            textY -= lineHeight * 2.0f;
        }


        batch.setColor(0.3f, 0.3f, 0.5f, 0.9f);
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 80, 40);


        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);

        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50 + 40, 80, 1);

        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 80, 1);

        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 1, 40);

        batch.draw(pixelTexture, WORLD_WIDTH - 100 + 80 - 1, 50, 1, 40);


        font.setColor(1, 1, 1, 1);
        font.getData().setScale(1.5f);
        String backText = "Back";
        GlyphLayout backLayout = new GlyphLayout(font, backText);
        font.draw(batch, backText,
            WORLD_WIDTH - 100 + 40 - backLayout.width / 2,
            50 + 25);


        font.getData().setScale(1.0f);
    }

        private void renderAbilitiesList() {

        float menuWidth = 500;
        float menuHeight = 400;
        float menuX = WORLD_WIDTH / 2 - menuWidth / 2;
        float menuY = WORLD_HEIGHT / 2 - menuHeight / 2;


        batch.setColor(0.1f, 0.1f, 0.2f, 0.9f);
        batch.draw(pixelTexture, menuX, menuY, menuWidth, menuHeight);


        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        float borderThickness = 2;

        batch.draw(pixelTexture, menuX, menuY + menuHeight - borderThickness, menuWidth, borderThickness);

        batch.draw(pixelTexture, menuX, menuY, menuWidth, borderThickness);

        batch.draw(pixelTexture, menuX, menuY, borderThickness, menuHeight);

        batch.draw(pixelTexture, menuX + menuWidth - borderThickness, menuY, borderThickness, menuHeight);


        titleFont.setColor(MENU_TITLE_COLOR);
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "YOUR ABILITIES");
        titleFont.draw(batch, titleLayout,
            menuX + menuWidth / 2 - titleLayout.width / 2,
            menuY + menuHeight - 30);


        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);
        batch.draw(pixelTexture, menuX + 20, menuY + menuHeight - 70, menuWidth - 40, 1);


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


                batch.setColor(ability.getCategoryColor());
                batch.draw(pixelTexture, menuX + 20, cardY - abilityCardHeight, abilityCardWidth, abilityCardHeight);


                TextureRegion icon = ability.getTextureRegion();
                if (icon != null) {
                    batch.setColor(Color.WHITE);
                    batch.draw(icon, menuX + 30, cardY - abilityCardHeight + 10, 40, 40);
                }


                optionFont.setColor(Color.WHITE);
                optionFont.draw(batch, ability.getName(), menuX + 80, cardY - 20);


                descriptionFont.setColor(Color.LIGHT_GRAY);
                descriptionFont.draw(batch, ability.getEnglishDescription(),
                    menuX + 80, cardY - 40, 380, -1, true);
            }
        }


        batch.setColor(0.3f, 0.3f, 0.5f, 0.9f);
        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 80, 40);


        batch.setColor(0.5f, 0.5f, 0.8f, 0.9f);

        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50 + 40, 80, 1);

        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 80, 1);

        batch.draw(pixelTexture, WORLD_WIDTH - 100, 50, 1, 40);

        batch.draw(pixelTexture, WORLD_WIDTH - 100 + 80 - 1, 50, 1, 40);

        optionFont.setColor(Color.WHITE);
        GlyphLayout backLayout = new GlyphLayout(optionFont, "Back");
        optionFont.draw(batch, backLayout,
            WORLD_WIDTH - 100 + 40 - backLayout.width / 2,
            50 + 25);
    }

    private void checkAbilityClick(int screenX, int screenY) {

        Vector3 touchPoint = new Vector3(screenX, screenY, 0);
        uiViewport.unproject(touchPoint);


        float cardWidth = 200;
        float cardHeight = 150;
        float startX = WORLD_WIDTH / 2 - (abilityChoices.length * cardWidth + (abilityChoices.length - 1) * 20) / 2;
        float startY = WORLD_HEIGHT / 2 - cardHeight / 2;


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


        if (!autoAim) {

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


        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        updateCamera();
        camera.update();


        updateMousePosition();


        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        batch.setColor(Color.WHITE);


        drawRepeatingBackground();


        if (lightTexture != null) {
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
            float lightSize = lightRadius * 2;
            batch.draw(lightTexture,
                playerPosition.x - lightSize/2,
                playerPosition.y - lightSize/2,
                lightSize, lightSize);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }


        enemyManager.render(batch);


        if (invincibilityTimer <= 0 || Math.sin(stateTime * 20) > 0) {
            batch.draw(currentPlayerFrame,
                playerPosition.x - PLAYER_WIDTH/2,
                playerPosition.y - PLAYER_HEIGHT/2,
                PLAYER_WIDTH,
                PLAYER_HEIGHT);
        }


        if (currentWeapon != null) {
            currentWeapon.render(batch);
        }


        cheatManager.render(batch);


        batch.end();


        drawUI();

        if (autoAim) {
            Enemy target = findNearestEnemy();
            if (target != null) {
                drawTargetIndicator(target);
            }
        }


        if (showAbilitySelection) {
            renderAbilitySelection();
        }


        if (isPaused) {
            renderPauseMenu();
        }
    }

    private void drawTargetIndicator(Enemy target) {

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        batch.setColor(1, 0, 0, 0.7f);
        Vector2 pos = target.getPosition();
        float size = 15;


        if (pixelTexture != null) {
            batch.draw(pixelTexture,
                pos.x - size/2, pos.y - size/2,
                size, size);
        }


        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void updateMousePosition() {
        mousePosition.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePosition);
    }


    private void drawRepeatingBackground() {

        float startX = camera.position.x - camera.viewportWidth/2 * camera.zoom;
        float startY = camera.position.y - camera.viewportHeight/2 * camera.zoom;
        float endX = camera.position.x + camera.viewportWidth/2 * camera.zoom;
        float endY = camera.position.y + camera.viewportHeight/2 * camera.zoom;


        int bgWidth = backgroundTexture.getWidth();
        int bgHeight = backgroundTexture.getHeight();


        int startTileX = (int)(startX / bgWidth) - 1;
        int startTileY = (int)(startY / bgHeight) - 1;
        int endTileX = (int)(endX / bgWidth) + 1;
        int endTileY = (int)(endY / bgHeight) + 1;


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

        camera.position.x = playerPosition.x;
        camera.position.y = playerPosition.y;
    }

    private void update(float delta) {
        if (gameOver) return;

        if (isPaused) {
            menuAnimationTime += delta;
            return;
        }


        if (showAbilitySelection) {
            return;
        }

        cheatManager.update(delta);



        gameTimeElapsed += delta;
        if (gameTimeElapsed >= gameTimeMinutes * 60) {
            gameOver = true;
            controller.endGame();
            return;
        }


        if (invincibilityTimer > 0) {
            invincibilityTimer -= delta;
        }


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


                Vector3 screenPos = new Vector3(targetX, targetY, 0);
                camera.project(screenPos);
                Gdx.input.setCursorPosition((int)screenPos.x, (int)screenPos.y);
            }
        }



        if (currentWeapon != null) {
            currentWeapon.update(delta, playerPosition, targetX, targetY);


            if (mouseLeft || (autoAim && nearestEnemy != null)) {

                boolean shotFired = currentWeapon.shoot(playerPosition, targetX, targetY, cheatManager.isInfiniteShootingEnabled());

                if (shotFired && damageMultiplier > 1.0f) {

                    for (Bullet bullet : currentWeapon.getBullets()) {
                        bullet.setDamageMultiplier(damageMultiplier);
                    }
                }
            }


            if (keyR && !cheatManager.isInfiniteShootingEnabled()) {
                currentWeapon.startReload();
            }
        }


        enemyManager.update(delta, playerPosition);


        if (currentWeapon != null) {
            enemyManager.checkBulletCollisions(currentWeapon.getBullets(), this);
        }


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


        if (invincibilityTimer <= 0) {
            if (enemyManager.checkPlayerCollisions(playerBounds)) {

                takeDamage(10);
            }
        }


        Array<Item> collectedItems = enemyManager.checkItemCollisions(playerBounds);
        for (Item item : collectedItems) {
            applyItemEffect(item);
        }


        updatePlayerVelocity();


        if (playerVelocity.len() > 0) {

            playerVelocity.nor().scl(playerSpeed * speedMultiplier * delta);
            playerPosition.add(playerVelocity);
            isPlayerMoving = true;


            playerBounds.setPosition(
                playerPosition.x - PLAYER_WIDTH/2,
                playerPosition.y - PLAYER_HEIGHT/2
            );
        } else {
            isPlayerMoving = false;
        }


        stateTime += delta;
        if (isPlayerMoving && walkAnimation != null) {
            currentPlayerFrame = walkAnimation.getKeyFrame(stateTime, true);
        }


        updatePlayerDirection();
    }

    private void takeDamage(float amount) {

        if (invincibilityTimer > 0) {
            return;
        }


        playerHealth -= amount;


        invincibilityTimer = INVINCIBILITY_DURATION;


        if (playerHealth <= 0) {
            playerHealth = 0;
            gameOver = true;
            controller.endGame();
        }
    }

    private void updatePlayerSpeed() {

        playerSpeed = basePlayerSpeed;


        if (hasAbility(AbilityType.SPEEDY)) {
            playerSpeed *= 1.2f;
        }


        playerSpeed *= speedMultiplier;
    }

    private boolean hasAbility(AbilityType type) {
        return playerAbilities.contains(type, true);
    }


    private Enemy findNearestEnemy() {
        Enemy nearest = null;
        float minDistanceSquared = Float.MAX_VALUE;
        float maxRange = 800f;

        for (Enemy enemy : enemyManager.getEnemies()) {
            if (enemy.isAlive()) {
                Vector2 enemyPos = enemy.getPosition();
                float dx = enemyPos.x - playerPosition.x;
                float dy = enemyPos.y - playerPosition.y;
                float distanceSquared = dx * dx + dy * dy;


                if (distanceSquared < maxRange * maxRange && distanceSquared < minDistanceSquared) {
                    minDistanceSquared = distanceSquared;
                    nearest = enemy;
                }
            }
        }

        return nearest;
    }

    private void renderAbilitySelection() {

        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();


        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(pixelTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        batch.setColor(Color.WHITE);


        titleFont.setColor(Color.GOLD);
        String title = "LEVEL UP! Choose an ability:";
        GlyphLayout layout = new GlyphLayout(titleFont, title);
        titleFont.draw(batch, title, WORLD_WIDTH / 2 - layout.width / 2, WORLD_HEIGHT - 100);


        if (abilityChoices != null) {
            float cardWidth = 200;
            float cardHeight = 150;
            float startX = WORLD_WIDTH / 2 - (abilityChoices.length * cardWidth + (abilityChoices.length - 1) * 20) / 2;
            float startY = WORLD_HEIGHT / 2 - cardHeight / 2;

            for (int i = 0; i < abilityChoices.length; i++) {
                AbilityType ability = abilityChoices[i];
                float x = startX + i * (cardWidth + 20);
                float y = startY;


                batch.setColor(ability.getCategoryColor());
                batch.draw(pixelTexture, x, y, cardWidth, cardHeight);


                batch.setColor(Color.WHITE);
                float borderThickness = 1;

                batch.draw(pixelTexture, x, y + cardHeight - borderThickness, cardWidth, borderThickness);

                batch.draw(pixelTexture, x, y, cardWidth, borderThickness);

                batch.draw(pixelTexture, x, y, borderThickness, cardHeight);

                batch.draw(pixelTexture, x + cardWidth - borderThickness, y, borderThickness, cardHeight);

                batch.setColor(Color.WHITE);


                TextureRegion icon = ability.getTextureRegion();
                if (icon != null) {
                    batch.draw(icon, x + 10, y + cardHeight - 60, 40, 40);
                }


                BitmapFont font = new BitmapFont();
                font.getData().setScale(1.2f);
                font.setColor(Color.WHITE);
                font.draw(batch, ability.getName(), x + 60, y + cardHeight - 30);


                font.setColor(Color.LIGHT_GRAY);
                font.draw(batch, ability.getEnglishDescription(), x + 10, y + cardHeight - 70, cardWidth - 20, -1, true);


                font.setColor(Color.YELLOW);
                font.draw(batch, "Press " + (i + 1) + " to select", x + 10, y + 30);
            }
        }

        batch.end();
    }

    private void applyItemEffect(Item item) {
        switch (item.getType()) {
            case HEALTH:

                playerHealth = Math.min(playerHealth + 25, playerMaxHealth);
                break;

            case AMMO:

                if (currentWeapon != null) {
                    currentWeapon.addAmmo(10);
                }
                break;

            case SPEED_BOOST:

                speedMultiplier = 1.5f;
                playerSpeed = basePlayerSpeed * speedMultiplier;
                speedBoostTimer = 10;
                break;

            case DAMAGE_BOOST:

                damageMultiplier = 2.0f;
                damageBoostTimer = 10;
                break;

            case EXPERIENCE:

                addXP(3);
                break;
        }
    }

    private void addXP(int amount) {
        playerXP += amount;


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


        xpToNextLevel = 20 * playerLevel;


        showAbilitySelection = true;


        abilityChoices = getRandomAbilities(3);


        controller.pauseGame();
    }

    private AbilityType[] getRandomAbilities(int count) {
        AbilityType[] allTypes = AbilityType.values();


        if (count > allTypes.length) {
            count = allTypes.length;
        }


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


        if (GameSettings.getInstance().isUsingWASD()) {

            if (keyW) playerVelocity.y += 1;
            if (keyS) playerVelocity.y -= 1;
            if (keyA) playerVelocity.x -= 1;
            if (keyD) playerVelocity.x += 1;
        } else {

            if (keyUp) playerVelocity.y += 1;
            if (keyDown) playerVelocity.y -= 1;
            if (keyLeft) playerVelocity.x -= 1;
            if (keyRight) playerVelocity.x += 1;
        }
    }

    private void updatePlayerDirection() {

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

        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();


        int remainingSeconds = (int)(gameTimeMinutes * 60 - gameTimeElapsed);
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;


        BitmapFont font = new BitmapFont();
        font.setColor(1, 1, 1, 1);
        font.draw(batch, String.format("Time: %02d:%02d", minutes, seconds),
            20, WORLD_HEIGHT - 20);


        if (currentWeapon != null) {
            if (cheatManager.isInfiniteShootingEnabled()) {

                font.draw(batch, String.format("Weapon: %s | Ammo: infinity",
                        currentWeapon.getType().getName()),
                    WORLD_WIDTH - 300, WORLD_HEIGHT - 20);
            } else {

                font.draw(batch, String.format("Weapon: %s | Ammo: %d/%d",
                        currentWeapon.getType().getName(),
                        currentWeapon.getCurrentAmmo(),
                        currentWeapon.getType().getMaxAmmo()),
                    WORLD_WIDTH - 300, WORLD_HEIGHT - 20);
            }


            if (!cheatManager.isInfiniteShootingEnabled() && currentWeapon.isReloading()) {
                font.draw(batch, "Reloading... " +
                        (int)(currentWeapon.getReloadProgress() * 100) + "%",
                    WORLD_WIDTH - 300, WORLD_HEIGHT - 40);
            }
        }


        font.draw(batch, String.format("Health: %.0f/%.0f", playerHealth, playerMaxHealth),
            20, WORLD_HEIGHT - 40);

        font.draw(batch, String.format("Kills: %d", playerKills),
            20, WORLD_HEIGHT - 60);


        font.draw(batch, String.format("Level: %d | XP: %d/%d",
                playerLevel, playerXP, xpToNextLevel),
            20, WORLD_HEIGHT - 80);

        float xpBarWidth = 150;
        float xpBarHeight = 10;
        float xpBarX = 120;
        float xpBarY = WORLD_HEIGHT - 85;


        batch.setColor(0.3f, 0.3f, 0.3f, 1);
        batch.draw(pixelTexture, xpBarX + 30, xpBarY - 5, xpBarWidth, xpBarHeight);


        float fillWidth = (float)playerXP / xpToNextLevel * xpBarWidth;
        batch.setColor(0.2f, 0.7f, 1f, 1);
        batch.draw(pixelTexture, xpBarX + 30, xpBarY - 5, fillWidth, xpBarHeight);


        font.setColor(1, 1, 1, 1);
        font.draw(batch, String.format("XP: %d/%d", playerXP, xpToNextLevel),
            xpBarX + xpBarWidth + 40, xpBarY + xpBarHeight - 2);


        String autoAimStatus = autoAim ? "ON" : "OFF";
        font.draw(batch, "Auto-Aim: " + autoAimStatus, 20, WORLD_HEIGHT - 100);


        if (damageBoostTimer > 0) {
            font.draw(batch, String.format("Damage Boost: %.1fs", damageBoostTimer),
                20, WORLD_HEIGHT - 120);
        }

        if (speedBoostTimer > 0) {
            font.draw(batch, String.format("Speed Boost: %.1fs", speedBoostTimer),
                20, WORLD_HEIGHT - 140);
        }


        font.setColor(Color.CYAN);
        font.draw(batch, "Abilities:", WORLD_WIDTH - 300, WORLD_HEIGHT - 60);


        float abilityIconSize = 20;
        float abilitySpacing = 5;
        float startX = WORLD_WIDTH - 280;
        float startY = WORLD_HEIGHT - 100;

        for (int i = 0; i < playerAbilities.size; i++) {
            AbilityType ability = playerAbilities.get(i);
            float x = startX;
            float y = startY - (i * (abilityIconSize + abilitySpacing));


            batch.setColor(ability.getCategoryColor());
            batch.draw(pixelTexture, x, y, abilityIconSize, abilityIconSize);


            TextureRegion icon = ability.getTextureRegion();
            if (icon != null) {
                batch.setColor(Color.WHITE);
                batch.draw(icon, x, y, abilityIconSize, abilityIconSize);
            }


            batch.setColor(Color.WHITE);
            font.draw(batch, ability.getName(), x + abilityIconSize + 5, y + abilityIconSize - 2);
        }


        font.setColor(Color.WHITE);
        if (selectedHero != null) {
            font.draw(batch, "Hero: " + selectedHero.getName(),
                WORLD_WIDTH - 300, WORLD_HEIGHT - 100 - (Math.max(playerAbilities.size, 0) * (abilityIconSize + abilitySpacing)));
        }

        font.setColor(Color.LIGHT_GRAY);
        if (GameSettings.getInstance().isUsingWASD()) {
            font.draw(batch, "WASD: Move | Mouse: Aim | Click: Shoot | R: Reload | Space: Auto-Aim",
                WORLD_WIDTH/2 - 200, 20);
        } else {
            font.draw(batch, "Arrow Keys: Move | Mouse: Aim | Click: Shoot | R: Reload | Space: Auto-Aim",
                WORLD_WIDTH/2 - 200, 20);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        uiViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

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

        if (cheatManager != null) {
            cheatManager.dispose();
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


    public Vector2 getPlayerPosition() {
        return playerPosition;
    }

    public Rectangle getPlayerBounds() {
        return playerBounds;
    }

    public boolean isGameOver() {
        return gameOver;
    }


    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }


    public void setWeapon(WeaponType weaponType) {
        if (currentWeapon != null) {
            currentWeapon.dispose();
        }
        currentWeapon = new Weapon(weaponType);
    }


    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public boolean isTimeUp() {
        return gameTimeElapsed >= gameTimeMinutes * 60;
    }

    public boolean isPlayerDead() {
        return playerHealth <= 0;
    }

    public int getPlayerKills() {
        return playerKills;
    }

    public float getSurvivalTime() {
        return gameTimeElapsed;
    }

    public boolean decreaseGameTime(float seconds) {

        float remainingTime = gameTimeMinutes * 60 - gameTimeElapsed;
        if (remainingTime > seconds) {
            gameTimeElapsed += seconds;
            return true;
        }
        return false;
    }

        public void forceLevelUp() {
        playerLevel++;

        xpToNextLevel = 20 * playerLevel;


        showAbilitySelection = true;


        abilityChoices = getRandomAbilities(3);


        controller.pauseGame();
    }

        public void refillPlayerHealth() {
        playerHealth = playerMaxHealth;
    }

        public float getPlayerHealth() {
        return playerHealth;
    }

        public float getPlayerMaxHealth() {
        return playerMaxHealth;
    }

        public float getRemainingGameTime() {
        return gameTimeMinutes * 60 - gameTimeElapsed;
    }

        public EnemyManager getEnemyManager() {
        return enemyManager;
    }

}
