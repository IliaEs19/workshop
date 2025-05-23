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
import com.tilldawn.Controllers.LoginMenuController;
import com.tilldawn.Controllers.MainMenuController;
import com.tilldawn.Controllers.TalentMenuController;
import com.tilldawn.Main;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.Hero.AbilityType;
import com.tilldawn.Models.Hero.HeroType;

public class TalentMenu implements Screen {
    private Stage stage;
    private Table mainTable;
    private Table heroListTable;
    private Table heroDetailsTable;
    private ScrollPane heroListScrollPane;
    private final TalentMenuController controller;
    private HeroType selectedHero;


    private Image heroImage;
    private Label heroNameLabel;
    private Label heroDescriptionLabel;
    private ProgressBar healthBar;
    private ProgressBar speedBar;
    private Label heroHealthLabel;
    private Label heroSpeedLabel;


    private enum TabType { HEROES, SKILLS }
    private TabType currentTab = TabType.HEROES;


    private Table heroesContent;
    private Table skillsContent;


    private Table abilitiesListTable;
    private Table abilityDetailsTable;
    private ScrollPane abilitiesListScrollPane;
    private AbilityType selectedAbility;
    private Image abilityImage;
    private Label abilityNameLabel;
    private Label abilityDescriptionLabel;
    private Label abilityCategoryLabel;


    private static final float PADDING = 25f;
    private static final float HERO_IMAGE_SIZE = 1200f;
    private static final Color BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.2f, 1f);
    private static final Color TITLE_COLOR = new Color(0.9f, 0.7f, 0.2f, 1f);
    private static final Color SELECTED_COLOR = new Color(0.3f, 0.6f, 0.9f, 1f);
    private static final Color TEXT_COLOR = new Color(0.9f, 0.9f, 0.9f, 1f);
    private static final Color STAT_LABEL_COLOR = new Color(0.2f, 0.8f, 0.2f, 1f);
    private static final Color HEALTH_BAR_COLOR = new Color(0.8f, 0.2f, 0.2f, 1f);
    private static final Color SPEED_BAR_COLOR = new Color(0.2f, 0.6f, 0.8f, 1f);

    public TalentMenu(TalentMenuController controller) {
        this.controller = controller;
        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);


        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(createBackground(BACKGROUND_COLOR));
        mainTable.pad(PADDING);


        Label titleLabel = new Label("TALENT MENU", GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setFontScale(2.2f);
        titleLabel.setColor(TITLE_COLOR);
        titleLabel.setAlignment(Align.center);


        titleLabel.addAction(Actions.forever(Actions.sequence(
            Actions.color(new Color(TITLE_COLOR.r, TITLE_COLOR.g, TITLE_COLOR.b, 0.8f), 1.5f),
            Actions.color(TITLE_COLOR, 1.5f)
        )));


        mainTable.add(titleLabel).colspan(2).expandX().fillX().pad(PADDING).row();


        Table tabsTable = createTabsSection();
        mainTable.add(tabsTable).colspan(2).expandX().fillX().pad(PADDING).row();



        Table contentContainer = new Table();
        contentContainer.setFillParent(false);
        contentContainer.setBackground(createBackground(new Color(0.12f, 0.12f, 0.22f, 1f)));


        heroesContent = new Table();
        createHeroListSection();
        createHeroDetailsSection();
        heroesContent.add(heroListScrollPane).width(800).expandY().fillY().pad(PADDING).padLeft(200);
        heroesContent.add(heroDetailsTable).expand().fill().pad(PADDING).width(600);


        skillsContent = new Table();
        createSkillsSection();


        contentContainer.add(heroesContent).expand().fill();
        contentContainer.add(skillsContent).expand().fill();
        skillsContent.setVisible(false);


        mainTable.add(contentContainer).colspan(2).expand().fill();

        stage.addActor(mainTable);


        if (HeroType.values().length > 0) {
            selectHero(HeroType.values()[0]);
        }


        switchTab(TabType.HEROES);
    }

    private void updateTabUnderlines(Table underlineTable, int selectedIndex) {
        underlineTable.clear();

        for (int i = 0; i < 4; i++) {
            Color color = i == selectedIndex ? SELECTED_COLOR : new Color(0.3f, 0.3f, 0.4f, 0f);
            Image underline = createHorizontalLine(color, 100, 2);
            underlineTable.add(underline).width(100).padRight(i < 3 ? 15 : 0);
        }
    }

    private void addHoverEffect(final TextButton button) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (!button.getColor().equals(SELECTED_COLOR)) {
                    button.addAction(Actions.sequence(
                        Actions.color(new Color(0.7f, 0.7f, 0.8f, 1f), 0.2f),
                        Actions.scaleTo(1.05f, 1.05f, 0.1f)
                    ));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (!button.getColor().equals(SELECTED_COLOR)) {
                    button.addAction(Actions.parallel(
                        Actions.color(new Color(0.5f, 0.5f, 0.5f, 1f), 0.2f),
                        Actions.scaleTo(1f, 1f, 0.1f)
                    ));
                } else {
                    button.addAction(Actions.scaleTo(1f, 1f, 0.1f));
                }
            }
        });
    }


    private void createHeroListSection() {
        heroListTable = new Table();




        heroListTable.pad(15).padLeft(500);


        Label heroListTitle = new Label("HEROES", GameAssetManager.getGameAssetManager().getSkin());
        heroListTitle.setFontScale(1.5f);
        heroListTitle.setColor(TITLE_COLOR);
        heroListTitle.setAlignment(Align.center);


        Image titleUnderline = createHorizontalLine(new Color(TITLE_COLOR.r, TITLE_COLOR.g, TITLE_COLOR.b, 0.7f), 200, 2);

        heroListTable.add(heroListTitle).expandX().fillX().padBottom(5).row();
        heroListTable.add(titleUnderline).expandX().fillX().padBottom(20).row();


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




        heroDetailsTable.pad(25).padLeft(1300).padBottom(100);


        heroNameLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        heroNameLabel.setFontScale(2.0f);
        heroNameLabel.setColor(TITLE_COLOR);
        heroNameLabel.setAlignment(Align.center);
        heroNameLabel.setWrap(true);


        Image topDivider = createHorizontalLine(new Color(0.5f, 0.5f, 0.7f, 0.6f), 400, 2);


        Table imageContainer = new Table();
        imageContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.7f)));
        imageContainer.pad(10);


        heroImage = new Image();
        imageContainer.add(heroImage).size(1300).pad(5);


        Table descriptionContainer = new Table();

        descriptionContainer.pad(25);



        heroDescriptionLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        heroDescriptionLabel.setFontScale(1.1f);
        heroDescriptionLabel.setColor(TEXT_COLOR);
        heroDescriptionLabel.setWrap(true);
        heroDescriptionLabel.setAlignment(Align.left);


        descriptionContainer.add(heroDescriptionLabel).expandX().fillX().width(450).padLeft(60);


        Table statsContainer = new Table();
        statsContainer.setBackground(createPanelBackground(new Color(0.18f, 0.18f, 0.28f, 0.7f)));
        statsContainer.pad(15);


        Label statsTitleLabel = new Label("HERO STATS", GameAssetManager.getGameAssetManager().getSkin());
        statsTitleLabel.setFontScale(1.4f);
        statsTitleLabel.setColor(new Color(0.9f, 0.9f, 0.6f, 1f));


        Label hpTitleLabel = new Label("HP", GameAssetManager.getGameAssetManager().getSkin());
        hpTitleLabel.setFontScale(1.2f);
        hpTitleLabel.setColor(STAT_LABEL_COLOR);

        heroHealthLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        heroHealthLabel.setFontScale(1.2f);
        heroHealthLabel.setColor(TEXT_COLOR);


        ProgressBar.ProgressBarStyle hpBarStyle = new ProgressBar.ProgressBarStyle();
        hpBarStyle.background = createColorDrawable(new Color(0.3f, 0.3f, 0.3f, 0.6f));
        hpBarStyle.knob = createColorDrawable(HEALTH_BAR_COLOR);
        hpBarStyle.knobBefore = createColorDrawable(HEALTH_BAR_COLOR);

        healthBar = new ProgressBar(0, 10, 0.1f, false, hpBarStyle);
        healthBar.setAnimateDuration(0.5f);


        Label speedTitleLabel = new Label("SPEED", GameAssetManager.getGameAssetManager().getSkin());
        speedTitleLabel.setFontScale(1.2f);
        speedTitleLabel.setColor(STAT_LABEL_COLOR);

        heroSpeedLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        heroSpeedLabel.setFontScale(1.0f);
        heroSpeedLabel.setColor(TEXT_COLOR);


        ProgressBar.ProgressBarStyle speedBarStyle = new ProgressBar.ProgressBarStyle();
        speedBarStyle.background = createColorDrawable(new Color(0.3f, 0.3f, 0.3f, 0.6f));
        speedBarStyle.knob = createColorDrawable(SPEED_BAR_COLOR);
        speedBarStyle.knobBefore = createColorDrawable(SPEED_BAR_COLOR);

        speedBar = new ProgressBar(0, 10, 0.1f, false, speedBarStyle);
        speedBar.setAnimateDuration(0.5f);


        Table statsTable = new Table();
        statsTable.pad(10);

        statsTable.add(statsTitleLabel).colspan(3).padBottom(15).row();


        Image hpIcon = createStatIcon(HEALTH_BAR_COLOR);
        statsTable.add(hpIcon).size(25).padRight(10);
        statsTable.add(hpTitleLabel).width(80).left();
        statsTable.add(healthBar).width(200).height(20).padRight(10);
        statsTable.add(heroHealthLabel).width(30).left().row();


        Image speedIcon = createStatIcon(SPEED_BAR_COLOR);
        statsTable.add(speedIcon).size(25).padRight(10).padTop(15);
        statsTable.add(speedTitleLabel).width(80).left().padTop(15);
        statsTable.add(speedBar).width(200).height(20).padRight(10).padTop(15);
        statsTable.add(heroSpeedLabel).width(30).left().padTop(15).row();

        statsContainer.add(statsTable).expand().fill();


        heroDetailsTable.add(heroNameLabel).colspan(2).expandX().fillX().padBottom(10).row();
        heroDetailsTable.add(topDivider).colspan(2).expandX().fillX().padBottom(20).row();


        Table contentTable = new Table();
        contentTable.add(imageContainer).padRight(20);
        contentTable.add(descriptionContainer).width(400).fillY();


        heroDetailsTable.add(contentTable).expandX().fillX().row();


        heroDetailsTable.add(new Image()).height(20).row();
        heroDetailsTable.add(statsContainer).expandX().fillX().height(160).row();
    }

    private void selectHero(HeroType hero) {
        this.selectedHero = hero;


        if (hero.getTextureRegion() != null) {
            heroImage.setDrawable(new TextureRegionDrawable(hero.getTextureRegion()));
        } else {

            heroImage.setDrawable(createDefaultImage());
        }


        heroImage.addAction(Actions.sequence(
            Actions.scaleTo(0.9f, 0.9f, 0.1f),
            Actions.scaleTo(1.1f, 1.1f, 0.2f),
            Actions.scaleTo(1.0f, 1.0f, 0.1f)
        ));


        heroNameLabel.setText(hero.getName());
        heroDescriptionLabel.setText(hero.getDescription());
        heroHealthLabel.setText(String.valueOf(hero.getHealthPoints()));
        heroSpeedLabel.setText(String.valueOf(hero.getSpeed()));


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


        heroDetailsTable.addAction(Actions.sequence(
            Actions.alpha(0.5f),
            Actions.alpha(1f, 0.4f)
        ));
    }

    private void styleHeroButton(TextButton button, final HeroType hero) {
        button.pad(12);
        button.getLabel().setFontScale(1.2f);
        button.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));


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

                button.clearActions();
                button.addAction(Actions.sequence(
                    Actions.color(new Color(SELECTED_COLOR.r + 0.2f, SELECTED_COLOR.g + 0.2f, SELECTED_COLOR.b + 0.2f, 1f), 0.2f),
                    Actions.color(SELECTED_COLOR, 0.2f)
                ));
            }
        });
    }


    private void switchTab(TabType tabType) {
        currentTab = tabType;


        switch (tabType) {
            case HEROES:
                heroesContent.setVisible(true);
                skillsContent.setVisible(false);
                break;
            case SKILLS:
                heroesContent.setVisible(false);
                skillsContent.setVisible(true);


                if (selectedAbility == null && AbilityType.values().length > 0) {
                    selectAbility(AbilityType.values()[0]);
                }
                break;
        }
    }

    private void createSkillsSection() {

        createAbilitiesListSection();


        createAbilityDetailsSection();


        skillsContent.add(abilitiesListScrollPane).width(600).expandY().fillY().pad(PADDING);
        skillsContent.add(abilityDetailsTable).expand().fill().pad(PADDING);
    }

    private Table createAbilityCategoryTable(String categoryName, AbilityType.AbilityCategory category) {
        Table categoryTable = new Table();
        categoryTable.setBackground(createPanelBackground(new Color(
            category.getColor().r * 0.5f,
            category.getColor().g * 0.5f,
            category.getColor().b * 0.5f,
            0.7f
        )));
        categoryTable.pad(100);


        Label categoryLabel = new Label(categoryName, GameAssetManager.getGameAssetManager().getSkin());
        categoryLabel.setFontScale(1.2f);
        categoryLabel.setColor(category.getColor());

        categoryTable.add(categoryLabel).expandX().fillX().padBottom(10).row();


        Table abilitiesTable = new Table();

        boolean hasAbilities = false;
        for (final AbilityType ability : AbilityType.values()) {
            if (ability.getCategory() == category) {
                hasAbilities = true;
                Table abilityButton = createAbilityButton(ability);
                abilitiesTable.add(abilityButton).expandX().fillX().pad(5).row();
            }
        }


        if (!hasAbilities) {
            Label emptyLabel = new Label("No abilities available", GameAssetManager.getGameAssetManager().getSkin());
            emptyLabel.setColor(Color.GRAY);
            abilitiesTable.add(emptyLabel).pad(10);
        }

        categoryTable.add(abilitiesTable).expandX().fillX();

        return categoryTable;
    }

    private Table createAbilityButton(final AbilityType ability) {
        Table buttonContainer = new Table();
        buttonContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.6f)));
        buttonContainer.pad(8);


        Image abilityIcon = new Image();
        if (ability.getTextureRegion() != null) {
            abilityIcon.setDrawable(new TextureRegionDrawable(ability.getTextureRegion()));
        } else {
            abilityIcon.setDrawable(createDefaultImage());
        }


        Label nameLabel = new Label(ability.getName(), GameAssetManager.getGameAssetManager().getSkin());
        nameLabel.setFontScale(1.1f);
        nameLabel.setColor(Color.WHITE);

        buttonContainer.add(abilityIcon).size(32).padRight(10);
        buttonContainer.add(nameLabel).expandX().fillX().left();


        buttonContainer.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                buttonContainer.setBackground(createPanelBackground(new Color(
                    ability.getCategory().getColor().r * 0.3f,
                    ability.getCategory().getColor().g * 0.3f,
                    ability.getCategory().getColor().b * 0.3f,
                    0.8f
                )));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (selectedAbility != ability) {
                    buttonContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.6f)));
                }
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                selectAbility(ability);
            }
        });

        return buttonContainer;
    }

    private void createAbilitiesListSection() {
        abilitiesListTable = new Table();




        abilitiesListTable.pad(15);


        Label abilitiesListTitle = new Label("ABILITIES", GameAssetManager.getGameAssetManager().getSkin());
        abilitiesListTitle.setFontScale(1.5f);
        abilitiesListTitle.setColor(TITLE_COLOR);
        abilitiesListTitle.setAlignment(Align.center);


        Image titleUnderline = createHorizontalLine(new Color(TITLE_COLOR.r, TITLE_COLOR.g, TITLE_COLOR.b, 0.7f), 200, 2);

        abilitiesListTable.add(abilitiesListTitle).expandX().fillX().padBottom(5).row();
        abilitiesListTable.add(titleUnderline).expandX().fillX().padBottom(20).row();


        Table attackTable = createAbilityCategoryTable("ATTACK", AbilityType.AbilityCategory.ATTACK);
        Table defenseTable = createAbilityCategoryTable("DEFENSE", AbilityType.AbilityCategory.DEFENSE);
        Table movementTable = createAbilityCategoryTable("MOVEMENT", AbilityType.AbilityCategory.MOVEMENT);
        Table utilityTable = createAbilityCategoryTable("UTILITY", AbilityType.AbilityCategory.UTILITY);


        abilitiesListTable.add(attackTable).expandX().fillX().padBottom(15).row();
        abilitiesListTable.add(defenseTable).expandX().fillX().padBottom(15).row();
        abilitiesListTable.add(movementTable).expandX().fillX().padBottom(15).row();
        abilitiesListTable.add(utilityTable).expandX().fillX().row();


        abilitiesListScrollPane = new ScrollPane(abilitiesListTable, GameAssetManager.getGameAssetManager().getSkin());
        abilitiesListScrollPane.setScrollingDisabled(true, false);
        abilitiesListScrollPane.setFadeScrollBars(false);


        abilitiesListScrollPane.addAction(Actions.forever(Actions.sequence(
            Actions.delay(0.1f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    abilitiesListScrollPane.setScrollY(abilitiesListScrollPane.getScrollY());
                }
            })
        )));
    }

    private void createAbilityDetailsSection() {
        abilityDetailsTable = new Table();
        abilityDetailsTable.setBackground(createGradientBackground(
            new Color(0.15f, 0.15f, 0.25f, 0.9f),
            new Color(0.1f, 0.1f, 0.2f, 0.9f)
        ));
        abilityDetailsTable.pad(25).padRight(700);


        abilityNameLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        abilityNameLabel.setFontScale(2.0f);
        abilityNameLabel.setColor(TITLE_COLOR);
        abilityNameLabel.setAlignment(Align.center);


        abilityCategoryLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        abilityCategoryLabel.setFontScale(1.2f);
        abilityCategoryLabel.setAlignment(Align.center);


        Image topDivider = createHorizontalLine(new Color(0.5f, 0.5f, 0.7f, 0.6f), 400, 2);


        Table imageContainer = new Table();
        imageContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.7f)));
        imageContainer.pad(15);

        abilityImage = new Image();
        imageContainer.add(abilityImage).size(380).pad(5);


        Table descriptionContainer = new Table();
        descriptionContainer.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
        descriptionContainer.pad(20);














        Label englishTitleLabel = new Label("English:", GameAssetManager.getGameAssetManager().getSkin());
        englishTitleLabel.setFontScale(1.1f);
        englishTitleLabel.setColor(new Color(0.7f, 0.7f, 0.9f, 1f));

        abilityDescriptionLabel = new Label("", GameAssetManager.getGameAssetManager().getSkin());
        abilityDescriptionLabel.setFontScale(1.2f);
        abilityDescriptionLabel.setColor(TEXT_COLOR);
        abilityDescriptionLabel.setWrap(true);


        Table descriptionsTable = new Table();


        descriptionsTable.add(englishTitleLabel).left().padBottom(5).padLeft(100).row();
        descriptionsTable.add(abilityDescriptionLabel).expandX().fillX().row();

        descriptionContainer.add(descriptionsTable).size(300).expand().fill();


        abilityDetailsTable.add(abilityNameLabel).colspan(2).expandX().fillX().row();
        abilityDetailsTable.add(abilityCategoryLabel).colspan(2).padBottom(10).row();
        abilityDetailsTable.add(topDivider).colspan(2).expandX().fillX().padBottom(20).row();
        abilityDetailsTable.add(imageContainer).size(400).padRight(20).top();
        abilityDetailsTable.add(descriptionContainer).size(500).expand().fill().row();


        Table effectsContainer = new Table();
        effectsContainer.setBackground(createPanelBackground(new Color(0.18f, 0.18f, 0.28f, 0.7f)));
        effectsContainer.pad(15);

        Label effectsTitleLabel = new Label("EFFECTS", GameAssetManager.getGameAssetManager().getSkin());
        effectsTitleLabel.setFontScale(1.3f);
        effectsTitleLabel.setColor(new Color(0.9f, 0.9f, 0.6f, 1f));


        Table effectsTable = new Table();
        effectsTable.setName("effectsTable");
        effectsTable.add(effectsTitleLabel).expandX().center().padBottom(15).row();

        effectsContainer.add(effectsTable).expand().fill();

        abilityDetailsTable.add(new Image()).height(20).row();
        abilityDetailsTable.add(effectsContainer).colspan(2).expandX().fillX().height(120).width(900).padBottom(100).row();
    }

    private void selectAbility(AbilityType ability) {
        this.selectedAbility = ability;


        if (ability.getTextureRegion() != null) {
            abilityImage.setDrawable(new TextureRegionDrawable(ability.getTextureRegion()));
        } else {

            abilityImage.setDrawable(createDefaultImage());
        }


        abilityImage.addAction(Actions.sequence(
            Actions.scaleTo(0.9f, 0.9f, 0.1f),
            Actions.scaleTo(1.1f, 1.1f, 0.2f),
            Actions.scaleTo(1.0f, 1.0f, 0.1f)
        ));


        abilityNameLabel.setText(ability.getName());
        abilityDescriptionLabel.setText(ability.getEnglishDescription());


        Actor persianDescLabel = abilityDetailsTable.findActor("persianDescLabel");
        if (persianDescLabel != null && persianDescLabel instanceof Label) {
            ((Label) persianDescLabel).setText(ability.getPersianDescription());
        }


        abilityCategoryLabel.setText(ability.getCategory().name());
        abilityCategoryLabel.setColor(ability.getCategoryColor());


        updateEffectsSection(ability);


        abilityDetailsTable.addAction(Actions.sequence(
            Actions.alpha(0.5f),
            Actions.alpha(1f, 0.4f)
        ));
    }

    private void updateEffectsSection(AbilityType ability) {

        Table effectsTable = (Table) abilityDetailsTable.findActor("effectsTable");
        if (effectsTable != null) {
            effectsTable.clear();

            Label effectsTitleLabel = new Label("EFFECTS", GameAssetManager.getGameAssetManager().getSkin());
            effectsTitleLabel.setFontScale(1.3f);
            effectsTitleLabel.setColor(new Color(0.9f, 0.9f, 0.6f, 1f));
            effectsTable.add(effectsTitleLabel).expandX().center().padBottom(15).row();


            switch (ability) {
                case VITALITY:
                    addEffectBar(effectsTable, "HP Boost", 1, 10, new Color(0.8f, 0.2f, 0.2f, 1f));
                    break;
                case DAMAGER:
                    addEffectBar(effectsTable, "Damage Boost", 25, 100, new Color(0.9f, 0.4f, 0.1f, 1f));
                    addEffectDuration(effectsTable, 10);
                    break;
                case PROCREASE:
                    addEffectBar(effectsTable, "Projectile Count", 1, 5, new Color(0.2f, 0.6f, 0.9f, 1f));
                    break;
                case AMOCREASE:
                    addEffectBar(effectsTable, "Ammo Capacity", 5, 30, new Color(0.9f, 0.7f, 0.2f, 1f));
                    break;
                case SPEEDY:
                    addEffectBar(effectsTable, "Speed Boost", 100, 100, new Color(0.3f, 0.9f, 0.3f, 1f));
                    addEffectDuration(effectsTable, 10);
                    break;
            }
        }
    }

    private void addEffectBar(Table container, String label, int value, int maxValue, Color barColor) {
        Label titleLabel = new Label(label + ": +" + value, GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setFontScale(1.1f);
        titleLabel.setColor(Color.WHITE);


        ProgressBar.ProgressBarStyle barStyle = new ProgressBar.ProgressBarStyle();
        barStyle.background = createColorDrawable(new Color(0.3f, 0.3f, 0.3f, 0.6f));
        barStyle.knob = createColorDrawable(barColor);
        barStyle.knobBefore = createColorDrawable(barColor);

        ProgressBar effectBar = new ProgressBar(0, maxValue, 0.1f, false, barStyle);
        effectBar.setValue(value);
        effectBar.setAnimateDuration(0.8f);

        container.add(titleLabel).left().padBottom(5).row();
        container.add(effectBar).width(300).height(20).padBottom(10).row();
    }

    private Table createTabsSection() {
        Table tabsTable = new Table();


        final TextButton heroesTabButton = new TextButton("HEROES", GameAssetManager.getGameAssetManager().getSkin());
        heroesTabButton.pad(12);
        heroesTabButton.getLabel().setFontScale(1.3f);
        heroesTabButton.setColor(SELECTED_COLOR);


        final TextButton skillsTabButton = new TextButton("SKILLS", GameAssetManager.getGameAssetManager().getSkin());
        skillsTabButton.pad(12);
        skillsTabButton.getLabel().setFontScale(1.3f);
        skillsTabButton.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));


        final TextButton upgradesTabButton = new TextButton("KEYS", GameAssetManager.getGameAssetManager().getSkin());
        upgradesTabButton.pad(12);
        upgradesTabButton.getLabel().setFontScale(1.3f);
        upgradesTabButton.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));

        final TextButton achievementsTabButton = new TextButton("CHEAT CODES", GameAssetManager.getGameAssetManager().getSkin());
        achievementsTabButton.pad(12);
        achievementsTabButton.getLabel().setFontScale(1.3f);
        achievementsTabButton.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));

        final TextButton backButton = new TextButton("BACK", GameAssetManager.getGameAssetManager().getSkin());
        backButton.pad(12);
        backButton.getLabel().setFontScale(1.3f);
        backButton.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));


        Table buttonsTable = new Table();



        buttonsTable.add(heroesTabButton).padRight(15);
        buttonsTable.add(skillsTabButton).padRight(15);
        buttonsTable.add(upgradesTabButton).padRight(15);
        buttonsTable.add(achievementsTabButton).padRight(15);

        buttonsTable.add(backButton).right();

        tabsTable.add(buttonsTable).expandX().fillX().row();


        Table underlineTable = new Table();
        underlineTable.setName("underlineTable");


        Image heroesUnderline = createHorizontalLine(SELECTED_COLOR, 100, 2);
        Image skillsUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);
        Image upgradesUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);
        Image achievementsUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);
        Image backUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);


        underlineTable.add().expandX().fillX();
        underlineTable.add(heroesUnderline).width(heroesTabButton.getWidth()).padRight(15);
        underlineTable.add(skillsUnderline).width(skillsTabButton.getWidth()).padRight(15);
        underlineTable.add(upgradesUnderline).width(upgradesTabButton.getWidth()).padRight(15);
        underlineTable.add(achievementsUnderline).width(achievementsTabButton.getWidth()).padRight(15);
        underlineTable.add(backUnderline).width(backButton.getWidth()).right();

        tabsTable.add(underlineTable).expandX().fillX().padTop(2);


        heroesTabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchTab(TabType.HEROES);


                heroesTabButton.setColor(SELECTED_COLOR);
                skillsTabButton.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));


                Table underlineTable = (Table) tabsTable.findActor("underlineTable");
                if (underlineTable != null) {
                    underlineTable.clear();


                    Image heroesUnderline = createHorizontalLine(SELECTED_COLOR, 100, 2);
                    Image skillsUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);
                    Image upgradesUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);
                    Image achievementsUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);
                    Image backUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);


                    underlineTable.add().expandX().fillX();
                    underlineTable.add(heroesUnderline).width(heroesTabButton.getWidth()).padRight(15);
                    underlineTable.add(skillsUnderline).width(skillsTabButton.getWidth()).padRight(15);
                    underlineTable.add(upgradesUnderline).width(upgradesTabButton.getWidth()).padRight(15);
                    underlineTable.add(achievementsUnderline).width(achievementsTabButton.getWidth()).padRight(15);
                    underlineTable.add(backUnderline).width(backButton.getWidth()).right();
                }


                heroesTabButton.clearActions();
                heroesTabButton.addAction(Actions.forever(Actions.sequence(
                    Actions.color(new Color(SELECTED_COLOR.r - 0.1f, SELECTED_COLOR.g - 0.1f, SELECTED_COLOR.b + 0.1f, 1f), 1.0f),
                    Actions.color(SELECTED_COLOR, 1.0f)
                )));
                skillsTabButton.clearActions();
            }
        });

        achievementsTabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                Main.getMain().setScreen(new CheatCodesScreen(controller));
            }
        });

        skillsTabButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchTab(TabType.SKILLS);


                skillsTabButton.setColor(SELECTED_COLOR);
                heroesTabButton.setColor(new Color(0.5f, 0.5f, 0.5f, 1f));


                Table underlineTable = (Table) tabsTable.findActor("underlineTable");
                if (underlineTable != null) {
                    underlineTable.clear();


                    Image heroesUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);
                    Image skillsUnderline = createHorizontalLine(SELECTED_COLOR, 100, 2);
                    Image upgradesUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);
                    Image achievementsUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);
                    Image backUnderline = createHorizontalLine(new Color(0.3f, 0.3f, 0.4f, 0f), 100, 2);


                    underlineTable.add().expandX().fillX();
                    underlineTable.add(heroesUnderline).width(heroesTabButton.getWidth()).padRight(15);
                    underlineTable.add(skillsUnderline).width(skillsTabButton.getWidth()).padRight(15);
                    underlineTable.add(upgradesUnderline).width(upgradesTabButton.getWidth()).padRight(15);
                    underlineTable.add(achievementsUnderline).width(achievementsTabButton.getWidth()).padRight(15);
                    underlineTable.add(backUnderline).width(backButton.getWidth()).right();
                }


                skillsTabButton.clearActions();
                skillsTabButton.addAction(Actions.forever(Actions.sequence(
                    Actions.color(new Color(SELECTED_COLOR.r - 0.1f, SELECTED_COLOR.g - 0.1f, SELECTED_COLOR.b + 0.1f, 1f), 1.0f),
                    Actions.color(SELECTED_COLOR, 1.0f)
                )));
                heroesTabButton.clearActions();
            }
        });

        backButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                Main.getMain().setScreen(new MainMenu(
                    new MainMenuController(),
                    GameAssetManager.getGameAssetManager().getSkin()));
            }
        });


        addHoverEffect(heroesTabButton);
        addHoverEffect(skillsTabButton);
        addHoverEffect(upgradesTabButton);
        addHoverEffect(achievementsTabButton);
        addHoverEffect(backButton);

        return tabsTable;
    }

    private void addEffectDuration(Table container, int seconds) {
        Label durationLabel = new Label("Duration: " + seconds + " seconds", GameAssetManager.getGameAssetManager().getSkin());
        durationLabel.setFontScale(1.1f);
        durationLabel.setColor(new Color(0.7f, 0.7f, 0.9f, 1f));

        container.add(durationLabel).left().padTop(5).row();
    }


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
        AbilityType.disposeAllTextures();
    }


    public HeroType getSelectedHero() {
        return selectedHero;
    }

    public AbilityType getSelectedAbility() {
        return selectedAbility;
    }
}
