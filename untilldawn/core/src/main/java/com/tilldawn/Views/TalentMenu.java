package com.tilldawn.Views;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.HeroType;

public class TalentMenu implements Screen {
    private Stage stage;
    private Table mainTable;
    private Table heroListTable;
    private Table heroDetailsTable;
    private ScrollPane heroListScrollPane;
    private final TalentMenuController controller;
    private HeroType selectedHero;

    // UI Components for hero details
    private Image heroImage;
    private Label heroNameLabel;
    private Label heroDescriptionLabel;
    private ProgressBar healthBar;
    private ProgressBar speedBar;
    private Label heroHealthLabel;
    private Label heroSpeedLabel;

    // Constants for styling
    private static final float PADDING = 25f;
    private static final float HERO_IMAGE_SIZE = 1200f;
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.2f, 1f);
    private static final Color TITLE_COLOR = new Color(0.9f, 0.7f, 0.2f, 1f); // Gold
    private static final Color SELECTED_COLOR = new Color(0.3f, 0.6f, 0.9f, 1f); // Blue
    private static final Color TEXT_COLOR = new Color(0.9f, 0.9f, 0.9f, 1f); // White
    private static final Color STAT_LABEL_COLOR = new Color(0.2f, 0.8f, 0.2f, 1f); // Green
    private static final Color HEALTH_BAR_COLOR = new Color(0.8f, 0.2f, 0.2f, 1f); // Red
    private static final Color SPEED_BAR_COLOR = new Color(0.2f, 0.6f, 0.8f, 1f); // Blue

    public TalentMenu(TalentMenuController controller) {
        this.controller = controller;
        controller.setView(this);
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
        Label titleLabel = new Label("TALENT MENU", GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setFontScale(2.2f);
        titleLabel.setColor(TITLE_COLOR);
        titleLabel.setAlignment(Align.center);

        // Add glowing animation to title
        titleLabel.addAction(Actions.forever(Actions.sequence(
            Actions.color(new Color(TITLE_COLOR.r, TITLE_COLOR.g, TITLE_COLOR.b, 0.8f), 1.5f),
            Actions.color(TITLE_COLOR, 1.5f)
        )));

        // Create hero list section
        createHeroListSection();

        // Create hero details section with enhanced visuals
        createHeroDetailsSection();

        // Create tabs for future sections
        HorizontalGroup tabsGroup = createTabsSection();

        // Add components to main table
        mainTable.add(titleLabel).colspan(2).expandX().fillX().pad(PADDING).row();
        mainTable.add(tabsGroup).colspan(2).expandX().fillX().pad(PADDING).row();
        mainTable.add(heroListScrollPane).width(300).expandY().fillY().pad(PADDING);
        mainTable.add(heroDetailsTable).expand().fill().pad(PADDING);

        stage.addActor(mainTable);

        // Select the first hero by default
        if (HeroType.values().length > 0) {
            selectHero(HeroType.values()[0]);
        }
    }

    private void createHeroListSection() {
        heroListTable = new Table();
        heroListTable.setBackground(createGradientBackground(
            new Color(0.15f, 0.15f, 0.25f, 0.9f),
            new Color(0.1f, 0.1f, 0.2f, 0.9f)
        ));
        heroListTable.pad(15);

        // Create stylish hero list title
        Label heroListTitle = new Label("HEROES", GameAssetManager.getGameAssetManager().getSkin());
        heroListTitle.setFontScale(1.5f);
        heroListTitle.setColor(TITLE_COLOR);
        heroListTitle.setAlignment(Align.center);

        // Add decorative line under title
        Image titleUnderline = createHorizontalLine(new Color(TITLE_COLOR.r, TITLE_COLOR.g, TITLE_COLOR.b, 0.7f), 200, 2);

        heroListTable.add(heroListTitle).expandX().fillX().padBottom(5).row();
        heroListTable.add(titleUnderline).expandX().fillX().padBottom(20).row();

        // Add hero buttons with enhanced styling
        for (final HeroType hero : HeroType.values()) {
            Table buttonContainer = new Table();
            buttonContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
            buttonContainer.pad(5);

            TextButton heroButton = new TextButton(hero.getName(), GameAssetManager.getGameAssetManager().getSkin());
            styleHeroButton(heroButton, hero);

            heroButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectHero(hero);
                }
            });

            buttonContainer.add(heroButton).expandX().fillX();
            heroListTable.add(buttonContainer).expandX().fillX().pad(5).row();
        }

        heroListScrollPane = new ScrollPane(heroListTable, GameAssetManager.getGameAssetManager().getSkin());
        heroListScrollPane.setScrollingDisabled(true, false);
        heroListScrollPane.setFadeScrollBars(false);

        // Add shadow effect to scrollpane
        heroListScrollPane.addAction(Actions.forever(Actions.sequence(
            Actions.delay(0.1f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    heroListScrollPane.setScrollY(heroListScrollPane.getScrollY());
                }
            })
        )));
    }

    private void createHeroDetailsSection() {
        heroDetailsTable = new Table();
        heroDetailsTable.setBackground(createGradientBackground(
            new Color(0.15f, 0.15f, 0.25f, 0.9f),
            new Color(0.1f, 0.1f, 0.2f, 0.9f)
        ));
        heroDetailsTable.pad(25);

        // Hero name with enhanced styling
        heroNameLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        heroNameLabel.setFontScale(2.0f);
        heroNameLabel.setColor(TITLE_COLOR);
        heroNameLabel.setAlignment(Align.center);
        heroNameLabel.setWrap(true);

        // Decorative elements
        Image topDivider = createHorizontalLine(new Color(0.5f, 0.5f, 0.7f, 0.6f), 400, 2);

        // Hero image with frame
        Table imageContainer = new Table();
        imageContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.7f)));
        imageContainer.pad(10);

        heroImage = new Image();
        imageContainer.add(heroImage).size(HERO_IMAGE_SIZE).pad(5);

        // Hero description with styled container
        Table descriptionContainer = new Table();
        descriptionContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
        descriptionContainer.pad(15);

        heroDescriptionLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        heroDescriptionLabel.setFontScale(1.1f);
        heroDescriptionLabel.setColor(TEXT_COLOR);
        heroDescriptionLabel.setWrap(true);
        descriptionContainer.add(heroDescriptionLabel).expandX().fillX().padLeft(130);;

        // Stats section with visual enhancements
        Table statsContainer = new Table();
        statsContainer.setBackground(createPanelBackground(new Color(0.18f, 0.18f, 0.28f, 0.7f)));
        statsContainer.pad(15);

        // Stats title
        Label statsTitleLabel = new Label("HERO STATS", GameAssetManager.getGameAssetManager().getSkin());
        statsTitleLabel.setFontScale(1.4f);
        statsTitleLabel.setColor(new Color(0.9f, 0.9f, 0.6f, 1f));

        // HP stat with progress bar
        Label hpTitleLabel = new Label("HP", GameAssetManager.getGameAssetManager().getSkin());
        hpTitleLabel.setFontScale(1.2f);
        hpTitleLabel.setColor(STAT_LABEL_COLOR);

        heroHealthLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        heroHealthLabel.setFontScale(1.2f);
        heroHealthLabel.setColor(TEXT_COLOR);

        // Create custom progress bar style for HP
        ProgressBar.ProgressBarStyle hpBarStyle = new ProgressBar.ProgressBarStyle();
        hpBarStyle.background = createColorDrawable(new Color(0.3f, 0.3f, 0.3f, 0.6f));
        hpBarStyle.knob = createColorDrawable(HEALTH_BAR_COLOR);
        hpBarStyle.knobBefore = createColorDrawable(HEALTH_BAR_COLOR);

        healthBar = new ProgressBar(0, 10, 0.1f, false, hpBarStyle);
        healthBar.setAnimateDuration(0.5f);

        // Speed stat with progress bar
        Label speedTitleLabel = new Label("SPEED", GameAssetManager.getGameAssetManager().getSkin());
        speedTitleLabel.setFontScale(1.2f);
        speedTitleLabel.setColor(STAT_LABEL_COLOR);

        heroSpeedLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        heroSpeedLabel.setFontScale(1.0f);
        heroSpeedLabel.setColor(TEXT_COLOR);

        // Create custom progress bar style for Speed
        ProgressBar.ProgressBarStyle speedBarStyle = new ProgressBar.ProgressBarStyle();
        speedBarStyle.background = createColorDrawable(new Color(0.3f, 0.3f, 0.3f, 0.6f));
        speedBarStyle.knob = createColorDrawable(SPEED_BAR_COLOR);
        speedBarStyle.knobBefore = createColorDrawable(SPEED_BAR_COLOR);

        speedBar = new ProgressBar(0, 10, 0.1f, false, speedBarStyle);
        speedBar.setAnimateDuration(0.5f);

        // Organize stats in a table
        Table statsTable = new Table();
        statsTable.pad(10);

        statsTable.add(statsTitleLabel).colspan(3).padBottom(15).row();

        // HP row with icon, label and progress bar
        Image hpIcon = createStatIcon(HEALTH_BAR_COLOR);
        statsTable.add(hpIcon).size(25).padRight(10);
        statsTable.add(hpTitleLabel).width(80).left();
        statsTable.add(healthBar).width(200).height(20).padRight(10);
        statsTable.add(heroHealthLabel).width(30).left().row();

        // Speed row with icon, label and progress bar
        Image speedIcon = createStatIcon(SPEED_BAR_COLOR);
        statsTable.add(speedIcon).size(25).padRight(10).padTop(15);
        statsTable.add(speedTitleLabel).width(80).left().padTop(15);
        statsTable.add(speedBar).width(200).height(20).padRight(10).padTop(15);
        statsTable.add(heroSpeedLabel).width(30).left().padTop(15).row();

        statsContainer.add(statsTable).expand().fill();

        // Add all components to hero details table
        heroDetailsTable.add(heroNameLabel).colspan(2).expandX().fillX().padBottom(10).row();
        heroDetailsTable.add(topDivider).colspan(2).expandX().fillX().padBottom(20).row();

        // Add image and description side by side
        heroDetailsTable.add(imageContainer).size(HERO_IMAGE_SIZE + 90).padRight(40).top();
        heroDetailsTable.add(descriptionContainer).expand().fill().row();

        // Add stats container at the bottom
        heroDetailsTable.add(new Image()).height(20).row(); // Spacer
        heroDetailsTable.add(statsContainer).colspan(2).expandX().fillX().height(160).row();
    }

    private HorizontalGroup createTabsSection() {
        HorizontalGroup tabsGroup = new HorizontalGroup();
        tabsGroup.space(15);

        // Heroes tab (active)
        TextButton heroesTabButton = new TextButton("HEROES", GameAssetManager.getGameAssetManager().getSkin());
        heroesTabButton.setColor(SELECTED_COLOR);

        // Placeholder tabs for future sections
        TextButton skillsTabButton = new TextButton("SKILLS", GameAssetManager.getGameAssetManager().getSkin());
        TextButton upgradesTabButton = new TextButton("UPGRADES", GameAssetManager.getGameAssetManager().getSkin());
        TextButton achievementsTabButton = new TextButton("ACHIEVEMENTS", GameAssetManager.getGameAssetManager().getSkin());

        // Add enhanced tab button styling
        styleTabButton(heroesTabButton, SELECTED_COLOR, true);
        styleTabButton(skillsTabButton, new Color(0.5f, 0.5f, 0.5f, 1f), false);
        styleTabButton(upgradesTabButton, new Color(0.5f, 0.5f, 0.5f, 1f), false);
        styleTabButton(achievementsTabButton, new Color(0.5f, 0.5f, 0.5f, 1f), false);

        tabsGroup.addActor(heroesTabButton);
        tabsGroup.addActor(skillsTabButton);
        tabsGroup.addActor(upgradesTabButton);
        tabsGroup.addActor(achievementsTabButton);

        return tabsGroup;
    }

    private void selectHero(HeroType hero) {
        this.selectedHero = hero;

        // Update hero image with animation
        if (hero.getTextureRegion() != null) {
            heroImage.setDrawable(new TextureRegionDrawable(hero.getTextureRegion()));
        } else {
            // Set default image if texture is not available
            heroImage.setDrawable(createDefaultImage());
        }

        // Apply zoom in/out animation to image
        heroImage.addAction(Actions.sequence(
            Actions.scaleTo(0.9f, 0.9f, 0.1f),
            Actions.scaleTo(1.1f, 1.1f, 0.2f),
            Actions.scaleTo(1.0f, 1.0f, 0.1f)
        ));

        // Update hero details with animation
        heroNameLabel.setText(hero.getName());
        heroDescriptionLabel.setText(hero.getDescription());
        heroHealthLabel.setText(String.valueOf(hero.getHealthPoints()));
        heroSpeedLabel.setText(String.valueOf(hero.getSpeed()));

        // Animate progress bars
        healthBar.setValue(0);
        speedBar.setValue(0);

        healthBar.addAction(Actions.delay(0.3f, Actions.run(new Runnable() {
            @Override
            public void run() {
                healthBar.setValue(hero.getHealthPoints());
            }
        })));

        speedBar.addAction(Actions.delay(0.5f, Actions.run(new Runnable() {
            @Override
            public void run() {
                speedBar.setValue(hero.getSpeed());
            }
        })));

        // Add fade-in animation effect to the whole details table
        heroDetailsTable.addAction(Actions.sequence(
            Actions.alpha(0.5f),
            Actions.alpha(1f, 0.4f)
        ));
    }

    private void styleHeroButton(TextButton button, final HeroType hero) {
        button.pad(12);
        button.getLabel().setFontScale(1.2f);
        button.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));

        // Add enhanced hover and click effects
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.sequence(
                    Actions.color(SELECTED_COLOR, 0.2f),
                    Actions.scaleTo(1.05f, 1.05f, 0.1f)
                ));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (selectedHero != hero) {
                    button.addAction(Actions.parallel(
                        Actions.color(new Color(0.6f, 0.6f, 0.7f, 1f), 0.2f),
                        Actions.scaleTo(1f, 1f, 0.1f)
                    ));
                } else {
                    button.addAction(Actions.scaleTo(1f, 1f, 0.1f));
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Update all buttons to default color
                for (Actor actor : heroListTable.getChildren()) {
                    if (actor instanceof Table) {
                        Actor[] children = ((Table) actor).getChildren().toArray();
                        for (Actor child : children) {
                            if (child instanceof TextButton && child != button) {
                                ((TextButton) child).setColor(new Color(0.6f, 0.6f, 0.7f, 1f));
                            }
                        }
                    }
                }
                // Set selected button color with pulse effect
                button.clearActions();
                button.addAction(Actions.sequence(
                    Actions.color(new Color(SELECTED_COLOR.r + 0.2f, SELECTED_COLOR.g + 0.2f, SELECTED_COLOR.b + 0.2f, 1f), 0.2f),
                    Actions.color(SELECTED_COLOR, 0.2f)
                ));
            }
        });
    }

    private void styleTabButton(final TextButton button, final Color normalColor, final boolean isSelected) {
        button.pad(12);
        button.getLabel().setFontScale(1.3f);

        if (isSelected) {
            // Create underline for selected tab
            final Table tabContainer = new Table();
            tabContainer.add(button).row();

            Image underline = createHorizontalLine(SELECTED_COLOR, button.getWidth(), 2);
            tabContainer.add(underline).padTop(2).width(button.getWidth());

            // Add pulsing effect to selected tab
            button.addAction(Actions.forever(Actions.sequence(
                Actions.color(new Color(SELECTED_COLOR.r - 0.1f, SELECTED_COLOR.g - 0.1f, SELECTED_COLOR.b + 0.1f, 1f), 1.0f),
                Actions.color(SELECTED_COLOR, 1.0f)
            )));
        }

        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!isSelected) {
                    button.addAction(Actions.sequence(
                        Actions.color(new Color(0.7f, 0.7f, 0.8f, 1f), 0.2f),
                        Actions.scaleTo(1.05f, 1.05f, 0.1f)
                    ));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!isSelected) {
                    button.addAction(Actions.parallel(
                        Actions.color(normalColor, 0.2f),
                        Actions.scaleTo(1f, 1f, 0.1f)
                    ));
                }
            }
        });
    }

    // Helper methods for creating UI elements with enhanced visuals

    private TextureRegionDrawable createBackground(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        return drawable;
    }

    private TextureRegionDrawable createGradientBackground(Color topColor, Color bottomColor) {
        Pixmap pixmap = new Pixmap(1, 100, Pixmap.Format.RGBA8888);

        for (int y = 0; y < 100; y++) {
            float ratio = (float)y / 99f;
            Color blendedColor = new Color(
                topColor.r * (1 - ratio) + bottomColor.r * ratio,
                topColor.g * (1 - ratio) + bottomColor.g * ratio,
                topColor.b * (1 - ratio) + bottomColor.b * ratio,
                topColor.a * (1 - ratio) + bottomColor.a * ratio
            );
            pixmap.setColor(blendedColor);
            pixmap.drawLine(0, y, 0, y);
        }

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

    private TextureRegionDrawable createColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        return drawable;
    }

    private Image createHorizontalLine(Color color, float width, int height) {
        Pixmap pixmap = new Pixmap((int)width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        // Add subtle gradient
        for (int y = 0; y < height; y++) {
            float alpha = 0.7f + (float)y / height * 0.3f;
            pixmap.setColor(new Color(color.r, color.g, color.b, alpha * color.a));
            pixmap.drawLine(0, y, (int)width - 1, y);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new Image(texture);
    }

    private Image createStatIcon(Color color) {
        Pixmap pixmap = new Pixmap(24, 24, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.2f, 0.2f, 0.2f, 0.6f));
        pixmap.fillCircle(12, 12, 12);

        pixmap.setColor(color);
        pixmap.fillCircle(12, 12, 9);

        pixmap.setColor(new Color(1f, 1f, 1f, 0.9f));
        pixmap.fillCircle(8, 8, 3);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new Image(texture);
    }

    private TextureRegionDrawable createDefaultImage() {
        Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.3f, 0.3f, 0.4f, 1f));
        pixmap.fill();

        // Add a more interesting default image pattern
        pixmap.setColor(new Color(0.4f, 0.4f, 0.5f, 1f));
        for (int i = 0; i < 100; i += 10) {
            pixmap.drawLine(0, i, i, 0);
            pixmap.drawLine(100-i, 0, 100, i);
            pixmap.drawLine(0, 100-i, i, 100);
            pixmap.drawLine(100-i, 100, 100, 100-i);
        }

        pixmap.setColor(Color.WHITE);
        pixmap.drawRectangle(0, 0, 99, 99);
        pixmap.drawLine(0, 0, 99, 99);
        pixmap.drawLine(99, 0, 0, 99);

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
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        HeroType.disposeAllTextures();
    }

    // Getter methods for controller access if needed
    public HeroType getSelectedHero() {
        return selectedHero;
    }
}
