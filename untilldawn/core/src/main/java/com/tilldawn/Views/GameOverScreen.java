package com.tilldawn.Views;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tilldawn.Controllers.GameController;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.SaveData;

public class GameOverScreen implements Screen {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;

    private final GameController controller;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;


    private boolean isVictory;
    private int playerKills;
    private float survivalTimeSeconds;
    private int playerScore;
    private String resultMessage;


    private Texture backgroundTexture;
    private Texture pixelTexture;
    private TextureRegion trophyTexture;
    private TextureRegion skullTexture;
    private BitmapFont titleFont;
    private BitmapFont subtitleFont;
    private BitmapFont regularFont;
    private BitmapFont buttonFont;


    private float animationTime = 0;
    private float[] itemAnimationDelays = {0.2f, 0.4f, 0.6f, 0.8f, 1.0f, 1.2f};
    private float buttonAnimationTime = 0;


    private float buttonX;
    private float buttonY;
    private float buttonWidth;
    private float buttonHeight;
    private boolean buttonHovered = false;

    public GameOverScreen(GameController controller, boolean isVictory, int kills, float survivalTime) {
        this.controller = controller;
        this.isVictory = isVictory;
        this.playerKills = kills;
        this.survivalTimeSeconds = survivalTime;


        this.playerScore = controller.getScore();


        if (isVictory) {
            resultMessage = "VICTORY!";
        } else {
            resultMessage = "GAME OVER";
        }


        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);


        buttonWidth = 250;
        buttonHeight = 60;
        buttonX = WORLD_WIDTH / 2 - buttonWidth / 2;
        buttonY = 50;

        loadAssets();
    }

    private void loadAssets() {

        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();


        try {
            if (isVictory) {
                backgroundTexture = new Texture(Gdx.files.internal("backgrounds/YOUWIN.png"));
            } else {
                backgroundTexture = new Texture(Gdx.files.internal("backgrounds/YOULOSE.png"));
            }
        } catch (Exception e) {

            Gdx.app.error("GameOverScreen", "Error loading background texture: " + e.getMessage());
        }


        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixelTexture = new Texture(pixmap);
        pixmap.dispose();


        try {
            Texture iconSheet = new Texture(Gdx.files.internal("heroes/character1.png"));
            trophyTexture = new TextureRegion(iconSheet, 0, 0, 64, 64);
            skullTexture = new TextureRegion(iconSheet, 64, 0, 64, 64);
        } catch (Exception e) {

            trophyTexture = new TextureRegion(pixelTexture);
            skullTexture = new TextureRegion(pixelTexture);
            Gdx.app.error("GameOverScreen", "Error loading icon textures: " + e.getMessage());
        }



            titleFont = new BitmapFont();
            titleFont.getData().setScale(3);

            subtitleFont = new BitmapFont();
            subtitleFont.getData().setScale(2);

            regularFont = new BitmapFont();
            regularFont.getData().setScale(1.5f);

            buttonFont = new BitmapFont();
            buttonFont.getData().setScale(2);


    }
























































    @Override
    public void show() {

        if (isVictory) {


        } else {


        }
    }

    @Override
    public void render(float delta) {

        updateAnimations(delta);


        checkButtonInteraction();


        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        } else {

            drawGradientBackground();
        }


        float titleY = WORLD_HEIGHT - 80;
        float itemSpacing = 60;


        if (animationTime > itemAnimationDelays[0]) {
            float alpha = Math.min(1, (animationTime - itemAnimationDelays[0]) * 2);
            Color titleColor = isVictory ? Color.GOLD : Color.FIREBRICK;
            titleFont.setColor(titleColor.r, titleColor.g, titleColor.b, alpha);


            TextureRegion icon = isVictory ? trophyTexture : skullTexture;
            float iconSize = 64;
            float bounce = (float) Math.sin(animationTime * 5) * 5;

            GlyphLayout titleLayout = new GlyphLayout(titleFont, resultMessage);
            float titleX = WORLD_WIDTH / 2 - titleLayout.width / 2;

            batch.setColor(1, 1, 1, alpha);
            batch.draw(icon, titleX - iconSize - 20, titleY - iconSize / 2 + bounce, iconSize, iconSize);
            batch.draw(icon, titleX + titleLayout.width + 20, titleY - iconSize / 2 + bounce, iconSize, iconSize);
            batch.setColor(1, 1, 1, 1);

            titleFont.draw(batch, resultMessage, titleX, titleY);
        }


        if (animationTime > itemAnimationDelays[1]) {
            float alpha = Math.min(1, (animationTime - itemAnimationDelays[1]) * 2);
            subtitleFont.setColor(subtitleFont.getColor().r, subtitleFont.getColor().g, subtitleFont.getColor().b, alpha);

            int minutes = (int) (survivalTimeSeconds / 60);
            int seconds = (int) (survivalTimeSeconds % 60);
            String timeText = String.format("Survival Time: %02d:%02d", minutes, seconds);

            GlyphLayout layout = new GlyphLayout(subtitleFont, timeText);
            subtitleFont.draw(batch, timeText, WORLD_WIDTH / 2 - layout.width / 2, titleY - itemSpacing);
        }


        if (animationTime > itemAnimationDelays[2]) {
            float alpha = Math.min(1, (animationTime - itemAnimationDelays[2]) * 2);
            subtitleFont.setColor(subtitleFont.getColor().r, subtitleFont.getColor().g, subtitleFont.getColor().b, alpha);

            String killsText = "Total Kills: " + playerKills;
            GlyphLayout layout = new GlyphLayout(subtitleFont, killsText);
            subtitleFont.draw(batch, killsText, WORLD_WIDTH / 2 - layout.width / 2, titleY - itemSpacing * 2);
        }


        if (animationTime > itemAnimationDelays[3]) {
            float alpha = Math.min(1, (animationTime - itemAnimationDelays[3]) * 2);
            subtitleFont.setColor(subtitleFont.getColor().r, subtitleFont.getColor().g, subtitleFont.getColor().b, alpha);

            String scoreText = "Score: " + playerScore;
            GlyphLayout layout = new GlyphLayout(subtitleFont, scoreText);
            subtitleFont.draw(batch, scoreText, WORLD_WIDTH / 2 - layout.width / 2, titleY - itemSpacing * 3);


            regularFont.setColor(regularFont.getColor().r, regularFont.getColor().g, regularFont.getColor().b, alpha * 0.8f);
            String formulaText = "Score = Survival Time (seconds) * Kills";
            GlyphLayout formulaLayout = new GlyphLayout(regularFont, formulaText);
            regularFont.draw(batch, formulaText, WORLD_WIDTH / 2 - formulaLayout.width / 2, titleY - itemSpacing * 3 - 30);
        }


        if (animationTime > itemAnimationDelays[4]) {
            float alpha = Math.min(1, (animationTime - itemAnimationDelays[4]) * 2);
            regularFont.setColor(regularFont.getColor().r, regularFont.getColor().g, regularFont.getColor().b, alpha);

            String finalMessage = isVictory ?
                "Congratulations! You survived till the end!" :
                "Better luck next time, warrior!\n";
            finalMessage = finalMessage + "USERNAME: ";
            finalMessage = finalMessage + (SaveData.getCurrentUser() != null ? SaveData.getCurrentUser().getUserName() : "guest");
            GlyphLayout layout = new GlyphLayout(regularFont, finalMessage);
            regularFont.draw(batch, finalMessage, WORLD_WIDTH / 2 - layout.width / 2, titleY - itemSpacing * 4);
        }


        if (animationTime > itemAnimationDelays[5]) {
            drawBackToMenuButton();
        }

        batch.end();
    }

    private void drawGradientBackground() {

        Color topColor = isVictory ? new Color(0.2f, 0.4f, 0.6f, 1) : new Color(0.4f, 0.1f, 0.1f, 1);
        Color bottomColor = isVictory ? new Color(0.1f, 0.2f, 0.4f, 1) : new Color(0.2f, 0.05f, 0.05f, 1);


        batch.setColor(topColor);
        batch.draw(pixelTexture, 0, WORLD_HEIGHT / 2, WORLD_WIDTH, WORLD_HEIGHT / 2);


        batch.setColor(bottomColor);
        batch.draw(pixelTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT / 2);


        if (isVictory) {
            drawLightEffects();
        } else {
            drawDarkEffects();
        }

        batch.setColor(Color.WHITE);
    }

    private void drawLightEffects() {

        for (int i = 0; i < 5; i++) {
            float x = WORLD_WIDTH * (0.2f + 0.15f * i);
            float y = WORLD_HEIGHT * 0.7f;
            float size = 200 + (float)Math.sin(animationTime * 0.5f + i) * 50;
            float alpha = 0.1f + (float)Math.sin(animationTime * 0.3f + i * 0.5f) * 0.05f;

            batch.setColor(1, 1, 0.8f, alpha);
            batch.draw(pixelTexture, x - size/2, y - size/2, size, size);
        }
    }

    private void drawDarkEffects() {

        for (int i = 0; i < 5; i++) {
            float x = WORLD_WIDTH * (0.2f + 0.15f * i);
            float y = WORLD_HEIGHT * 0.3f;
            float size = 150 + (float)Math.sin(animationTime * 0.3f + i) * 30;
            float alpha = 0.2f + (float)Math.sin(animationTime * 0.2f + i * 0.5f) * 0.1f;

            batch.setColor(0.5f, 0, 0, alpha);
            batch.draw(pixelTexture, x - size/2, y - size/2, size, size);
        }
    }

    private void drawBackToMenuButton() {

        float pulseScale = 1 + (float)Math.sin(buttonAnimationTime * 3) * 0.03f;
        float actualButtonWidth = buttonWidth * pulseScale + 60;
        float actualButtonHeight = buttonHeight * pulseScale;
        float actualButtonX = buttonX + (buttonWidth - actualButtonWidth) / 2;
        float actualButtonY = buttonY + (buttonHeight - actualButtonHeight) / 2;


        if (buttonHovered) {
            batch.setColor(isVictory ? new Color(0.3f, 0.5f, 0.9f, 0.9f) : new Color(0.7f, 0.2f, 0.2f, 0.9f));
        } else {
            batch.setColor(isVictory ? new Color(0.2f, 0.4f, 0.8f, 0.8f) : new Color(0.6f, 0.1f, 0.1f, 0.8f));
        }
        batch.draw(pixelTexture, actualButtonX, actualButtonY, actualButtonWidth, actualButtonHeight);


        batch.setColor(isVictory ? Color.CYAN : Color.ORANGE);
        float borderThickness = 2;


        batch.draw(pixelTexture, actualButtonX, actualButtonY + actualButtonHeight - borderThickness, actualButtonWidth, borderThickness);

        batch.draw(pixelTexture, actualButtonX, actualButtonY, actualButtonWidth, borderThickness);

        batch.draw(pixelTexture, actualButtonX, actualButtonY, borderThickness, actualButtonHeight);

        batch.draw(pixelTexture, actualButtonX + actualButtonWidth - borderThickness, actualButtonY, borderThickness, actualButtonHeight);


        String buttonText = "BACK TO MAIN MENU";
        GlyphLayout layout = new GlyphLayout(buttonFont, buttonText);
        buttonFont.setColor(Color.WHITE);
        buttonFont.draw(batch, buttonText,
            actualButtonX + actualButtonWidth / 2 - layout.width / 2,
            actualButtonY + actualButtonHeight / 2 + layout.height / 2);
    }

    private void updateAnimations(float delta) {
        animationTime += delta;
        buttonAnimationTime += delta;
    }

    private void checkButtonInteraction() {

        if (animationTime <= itemAnimationDelays[5]) {
            return;
        }


        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mousePos);


        buttonHovered = (mousePos.x >= buttonX && mousePos.x <= buttonX + buttonWidth &&
            mousePos.y >= buttonY && mousePos.y <= buttonY + buttonHeight);


        if (buttonHovered && Gdx.input.justTouched()) {

            controller.returnToMainMenu();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();

        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }

        if (pixelTexture != null) {
            pixelTexture.dispose();
        }

        if (titleFont != null) {
            titleFont.dispose();
        }

        if (subtitleFont != null) {
            subtitleFont.dispose();
        }

        if (regularFont != null) {
            regularFont.dispose();
        }

        if (buttonFont != null) {
            buttonFont.dispose();
        }
    }
}
