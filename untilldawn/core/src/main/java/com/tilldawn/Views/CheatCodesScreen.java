package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tilldawn.Controllers.TalentMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.CheatCode;
import com.tilldawn.Models.GameAssetManager;

public class CheatCodesScreen implements Screen {
    private Stage stage;
    private Table mainTable;
    private final TalentMenuController controller;

    // Constants for styling
    private static final float PADDING = 25f;
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.2f, 1f);
    private static final Color TITLE_COLOR = new Color(0.9f, 0.7f, 0.2f, 1f); // Gold
    private static final Color TEXT_COLOR = new Color(0.9f, 0.9f, 0.9f, 1f); // White

    public CheatCodesScreen(TalentMenuController controller) {
        this.controller = controller;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create main layout
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(createBackground(BACKGROUND_COLOR));
        mainTable.pad(PADDING);

        // Create title with glowing effect
        Label titleLabel = new Label("CHEAT CODES", GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setFontScale(2.2f);
        titleLabel.setColor(TITLE_COLOR);
        titleLabel.setAlignment(Align.center);

        // Add glowing animation to title
        titleLabel.addAction(Actions.forever(Actions.sequence(
            Actions.color(new Color(TITLE_COLOR.r, TITLE_COLOR.g, TITLE_COLOR.b, 0.8f), 1.5f),
            Actions.color(TITLE_COLOR, 1.5f)
        )));

        // Add title to main table
        mainTable.add(titleLabel).colspan(2).expandX().fillX().pad(PADDING).row();

        // Create description text
        Label descriptionLabel = new Label(
            "Use these cheat codes during gameplay to gain advantages or test game features.\n" +
                "Press the corresponding number key to activate each cheat.",
            GameAssetManager.getGameAssetManager().getSkin()
        );
        descriptionLabel.setFontScale(1.2f);
        descriptionLabel.setColor(TEXT_COLOR);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.center);

        // Add description to main table
        mainTable.add(descriptionLabel).colspan(2).width(800).pad(PADDING).row();

        // Create cheat codes table
        Table cheatCodesTable = createCheatCodesTable();

        // Add cheat codes table to main table
        mainTable.add(cheatCodesTable).colspan(2).expand().fill().pad(PADDING).row();

        // Create back button
        TextButton backButton = new TextButton("BACK", GameAssetManager.getGameAssetManager().getSkin());
        backButton.pad(15);
        backButton.getLabel().setFontScale(1.3f);

        // Add click listener for back button
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Return to talent menu
                goBackToTalentMenu();
            }
        });

        // Add back button to main table
        mainTable.add(backButton).colspan(2).width(200).padTop(PADDING).row();

        // Add main table to stage
        stage.addActor(mainTable);
    }

    private Table createCheatCodesTable() {
        Table container = new Table();
        container.setBackground(createPanelBackground(new Color(0.15f, 0.15f, 0.25f, 0.8f)));
        container.pad(20);

        // Create header for the table
        Table headerTable = new Table();
        headerTable.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.35f, 1f)));
        headerTable.pad(10);

        Label keyHeader = new Label("KEY", GameAssetManager.getGameAssetManager().getSkin());
        keyHeader.setFontScale(1.3f);
        keyHeader.setColor(TITLE_COLOR);

        Label nameHeader = new Label("NAME", GameAssetManager.getGameAssetManager().getSkin());
        nameHeader.setFontScale(1.3f);
        nameHeader.setColor(TITLE_COLOR);

        Label descHeader = new Label("DESCRIPTION", GameAssetManager.getGameAssetManager().getSkin());
        descHeader.setFontScale(1.3f);
        descHeader.setColor(TITLE_COLOR);

        headerTable.add(keyHeader).width(80).padRight(20);
        headerTable.add(nameHeader).width(200).padRight(20);
        headerTable.add(descHeader).width(400).expandX().fillX();

        // Add header to container
        container.add(headerTable).expandX().fillX().padBottom(15).row();

        // Create table for cheat code rows
        Table cheatCodesTable = new Table();

        // Add cheat codes from enum
        CheatCode[] cheatCodes = CheatCode.values();
        if (cheatCodes != null && cheatCodes.length > 0) {
            for (CheatCode cheatCode : cheatCodes) {
                addCheatCodeRow(cheatCodesTable, cheatCode.getKeyCode(), cheatCode.getName(), cheatCode.getDescription());
            }
        } else {
            // Show placeholder if no cheat codes found
            Label noCodesLabel = new Label("No cheat codes available", GameAssetManager.getGameAssetManager().getSkin());
            noCodesLabel.setFontScale(1.2f);
            noCodesLabel.setColor(Color.GRAY);
            cheatCodesTable.add(noCodesLabel).pad(20).row();

            // Add some placeholder cheat codes for demonstration
            addCheatCodeRow(cheatCodesTable, 4, "Decrease Time", "Reduces the remaining game time by one minute");
            addCheatCodeRow(cheatCodesTable, 5, "Level Up", "Increases player level and grants access to new abilities");
            addCheatCodeRow(cheatCodesTable, 6, "Refill Health", "Restores player health to maximum (only if below 50%)");
            addCheatCodeRow(cheatCodesTable, 7, "Boss Fight", "Starts a battle with the Elder boss");
            addCheatCodeRow(cheatCodesTable, 8, "Infinite Shooting", "Enables infinite shooting without need to reload");
        }

        // Add cheat codes table to a scroll pane
        ScrollPane scrollPane = new ScrollPane(cheatCodesTable, GameAssetManager.getGameAssetManager().getSkin());
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        // Add scroll pane to container
        container.add(scrollPane).expand().fill();

        return container;
    }

    private void addCheatCodeRow(Table table, int keyCode, String name, String description) {
        Table rowTable = new Table();
        rowTable.setBackground(createPanelBackground(new Color(0.18f, 0.18f, 0.28f, 0.7f)));
        rowTable.pad(10);

        // Key number with background
        Table keyContainer = new Table();
        keyContainer.setBackground(createPanelBackground(new Color(0.3f, 0.3f, 0.5f, 1f)));
        keyContainer.pad(8);

        Label keyLabel = new Label(String.valueOf(keyCode), GameAssetManager.getGameAssetManager().getSkin());
        keyLabel.setFontScale(1.2f);
        keyLabel.setColor(Color.WHITE);
        keyLabel.setAlignment(Align.center);

        keyContainer.add(keyLabel).width(30).height(30);

        // Cheat name
        Label nameLabel = new Label(name, GameAssetManager.getGameAssetManager().getSkin());
        nameLabel.setFontScale(1.1f);
        nameLabel.setColor(new Color(0.2f, 0.7f, 1f, 1f));

        // Cheat description
        Label descLabel = new Label(description, GameAssetManager.getGameAssetManager().getSkin());
        descLabel.setFontScale(1.0f);
        descLabel.setColor(TEXT_COLOR);
        descLabel.setWrap(true);

        rowTable.add(keyContainer).width(80).padRight(20);
        rowTable.add(nameLabel).width(200).padRight(20).left();
        rowTable.add(descLabel).width(400).expandX().fillX().left();

        table.add(rowTable).expandX().fillX().padBottom(10).row();
    }

    private void goBackToTalentMenu() {
        // Navigate back to talent menu
        Main.getMain().setScreen(new TalentMenu(controller));
    }

    private TextureRegionDrawable createBackground(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        return drawable;
    }

    private TextureRegionDrawable createPanelBackground(Color color) {
        Pixmap pixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        // Add subtle border
        pixmap.setColor(new Color(color.r + 0.1f, color.g + 0.1f, color.b + 0.1f, color.a));
        for (int i = 0; i < 2; i++) {
            pixmap.drawRectangle(i, i, pixmap.getWidth() - i*2, pixmap.getHeight() - i*2);
        }

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        return drawable;
    }

    @Override
    public void render(float delta) {
        // Add subtle color pulsing to background
        float t = (System.currentTimeMillis() % 10000) / 10000f;
        float r = 0.05f + 0.02f * (float)Math.sin(t * Math.PI * 2);
        float g = 0.05f + 0.01f * (float)Math.sin((t + 0.33f) * Math.PI * 2);
        float b = 0.1f + 0.03f * (float)Math.sin((t + 0.66f) * Math.PI * 2);

        Gdx.gl.glClearColor(r, g, b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
