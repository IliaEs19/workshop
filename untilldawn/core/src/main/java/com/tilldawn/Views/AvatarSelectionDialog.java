package com.tilldawn.Views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.tilldawn.Models.GameAssetManager;
import com.tilldawn.Models.SaveData;
import com.tilldawn.Models.User;


public class AvatarSelectionDialog extends Dialog {

    private static final float DIALOG_WIDTH = 2000;
    private static final float DIALOG_HEIGHT = 1600;
    private static final float AVATAR_SIZE = 400;
    private static final float AVATAR_FRAME_PADDING = 10;

    private final Stage stage;
    private final String username;
    private final Runnable onAvatarChanged;

    private int selectedAvatarIndex = -1;
    private String customAvatarPath = null;

    private static final String[] DEFAULT_AVATAR_PATHS = {
        "avatars/character1.jpg",
        "avatars/character2.jpg",
        "avatars/character3.jpg",
        "avatars/character4.jpg"
    };

    private Array<Table> avatarContainers = new Array<>();
    private Array<Image> selectionIndicators = new Array<>();

    public AvatarSelectionDialog(Stage stage, String username, Runnable onAvatarChanged) {
        super("Select Avatar", GameAssetManager.getGameAssetManager().getSkin());
        this.stage = stage;
        this.username = username;
        this.onAvatarChanged = onAvatarChanged;

        setModal(true);
        setMovable(false);
        setResizable(false);

        createContent();

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setPosition(
            (stage.getWidth() - DIALOG_WIDTH) / 2,
            (stage.getHeight() - DIALOG_HEIGHT) / 2
        );
    }

    private void createContent() {
        getTitleLabel().setAlignment(Align.center);
        getTitleLabel().setFontScale(1.5f);
        getTitleLabel().setColor(Color.GOLD);

        Table contentTable = new Table();
        contentTable.setFillParent(true);
        contentTable.pad(40);

        Label presetLabel = new Label("PRESET AVATARS", GameAssetManager.getGameAssetManager().getSkin());
        presetLabel.setFontScale(1.2f);
        presetLabel.setColor(Color.CYAN);
        contentTable.add(presetLabel).colspan(4).padBottom(20).row();

        Table presetAvatarsTable = new Table();

        User user = SaveData.getInstance().getUser(username);
        String currentAvatarPath = (user != null) ? user.getAvatarPath() : "";

        for (int i = 0; i < DEFAULT_AVATAR_PATHS.length; i++) {
            final int avatarIndex = i;
            Table avatarContainer = createAvatarContainer(DEFAULT_AVATAR_PATHS[i], i);

            Image selectionIndicator = createSelectionIndicator();
            avatarContainer.addActor(selectionIndicator);
            selectionIndicator.setPosition(
                (avatarContainer.getWidth() - selectionIndicator.getWidth()) / 2,
                (avatarContainer.getHeight() - selectionIndicator.getHeight()) / 2
            );
            selectionIndicator.setVisible(false);

            selectionIndicators.add(selectionIndicator);

            avatarContainer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectPresetAvatar(avatarIndex);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    avatarContainer.addAction(Actions.color(new Color(1.2f, 1.2f, 1.2f, 1f), 0.2f));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    avatarContainer.addAction(Actions.color(Color.WHITE, 0.2f));
                }
            });

            presetAvatarsTable.add(avatarContainer).size(AVATAR_SIZE + AVATAR_FRAME_PADDING * 2 + 20).pad(10);
            avatarContainers.add(avatarContainer);

            if (currentAvatarPath.equals(DEFAULT_AVATAR_PATHS[i])) {
                selectPresetAvatar(i);
            }
        }

        contentTable.add(presetAvatarsTable).colspan(4).padBottom(30).row();

        Image separator = new Image(createColorDrawable(Color.GRAY));
        contentTable.add(separator).colspan(4).height(2).expandX().fillX().padBottom(20).row();

        Label customLabel = new Label("CUSTOM AVATAR", GameAssetManager.getGameAssetManager().getSkin());
        customLabel.setFontScale(1.2f);
        customLabel.setColor(Color.CYAN);
        contentTable.add(customLabel).colspan(4).padBottom(20).row();

        Table dropZone = createDropZone();
        contentTable.add(dropZone).colspan(4).width(400).height(200).padBottom(20).row();

        Table buttonTable = new Table();

        TextButton saveButton = new TextButton("SAVE", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(saveButton, new Color(0.2f, 0.8f, 0.2f, 1f), new Color(0.3f, 0.9f, 0.3f, 1f));
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSelectedAvatar();
            }
        });

        TextButton cancelButton = new TextButton("CANCEL", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(cancelButton, new Color(0.8f, 0.2f, 0.2f, 1f), new Color(0.9f, 0.3f, 0.3f, 1f));
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        buttonTable.add(cancelButton).width(300).height(100).pad(10);
        buttonTable.add(saveButton).width(300).height(100).pad(10);

        contentTable.add(buttonTable).colspan(4).expandX().fillX();
        getContentTable().add(contentTable).expand().fill();
    }

    private Table createAvatarContainer(String avatarPath, int index) {
        Table container = new Table();
        container.setBackground(createPanelBackground(new Color(0.15f, 0.15f, 0.25f, 0.7f)));
        container.setName("avatarContainer" + index);

        Image frame = createAvatarFrame(AVATAR_SIZE + AVATAR_FRAME_PADDING * 2);
        frame.setName("avatarFrame" + index);

        try {
            Texture avatarTexture = new Texture(Gdx.files.internal(avatarPath));
            avatarTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            Image avatarImage = new Image(avatarTexture);
            avatarImage.setSize(AVATAR_SIZE, AVATAR_SIZE);
            avatarImage.setName("avatarImage" + index);

            container.add(frame).size(AVATAR_SIZE + AVATAR_FRAME_PADDING * 2);
            container.addActor(avatarImage);
            avatarImage.setPosition(AVATAR_FRAME_PADDING, AVATAR_FRAME_PADDING);

            Label indexLabel = new Label("#" + (index + 1), GameAssetManager.getGameAssetManager().getSkin());
            indexLabel.setFontScale(0.8f);
            indexLabel.setColor(Color.WHITE);
            container.add(indexLabel).padTop(5).row();

        } catch (Exception e) {
            Gdx.app.error("AvatarSelectionDialog", "Error loading avatar: " + e.getMessage());

            Image emptyImage = new Image(createColorDrawable(new Color(0.3f, 0.3f, 0.5f, 1)));
            emptyImage.setSize(AVATAR_SIZE, AVATAR_SIZE);

            container.add(frame).size(AVATAR_SIZE + AVATAR_FRAME_PADDING * 2);
            container.addActor(emptyImage);
            emptyImage.setPosition(AVATAR_FRAME_PADDING, AVATAR_FRAME_PADDING);
        }

        container.setTouchable(Touchable.enabled);

        return container;
    }

    private Image createSelectionIndicator() {
        Pixmap pixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);

        pixmap.setColor(new Color(0.2f, 0.8f, 0.2f, 0.9f));
        pixmap.fillCircle(25, 25, 25);

        pixmap.setColor(Color.WHITE);
        pixmap.fillTriangle(15, 25, 22, 32, 35, 15);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new Image(texture);
    }

    private Table createDropZone() {
        Table dropZone = new Table();
        dropZone.setName("dropZone");
        dropZone.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
        dropZone.pad(20);

        Image uploadIcon = createUploadIcon();

        Label dropLabel = new Label("Drag & Drop Image Here\nor", GameAssetManager.getGameAssetManager().getSkin());
        dropLabel.setFontScale(1.1f);
        dropLabel.setAlignment(Align.center);
        dropLabel.setColor(Color.WHITE);

        TextButton browseButton = new TextButton("BROWSE FILES", GameAssetManager.getGameAssetManager().getSkin());
        styleButton(browseButton, new Color(0.2f, 0.6f, 0.9f, 1f), new Color(0.3f, 0.7f, 1f, 1f));
        browseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

            }
        });

        dropZone.add(uploadIcon).size(60, 60).padBottom(10).row();
        dropZone.add(dropLabel).padBottom(15).row();
        dropZone.add(browseButton).width(400).height(110);

        setupDragAndDrop(dropZone);

        return dropZone;
    }

    private Image createUploadIcon() {
        Pixmap pixmap = new Pixmap(60, 60, Pixmap.Format.RGBA8888);

        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();

        pixmap.setColor(Color.WHITE);

        pixmap.fillRectangle(15, 40, 30, 5);

        pixmap.fillRectangle(27, 15, 6, 25);

        pixmap.fillTriangle(15, 20, 45, 20, 30, 5);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new Image(texture);
    }

    private void setupDragAndDrop(final Table dropZone) {
        dropZone.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                dropZone.setBackground(createPanelBackground(new Color(0.25f, 0.25f, 0.35f, 0.6f)));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                dropZone.setBackground(createPanelBackground(new Color(0.2f, 0.2f, 0.3f, 0.5f)));
            }
        });














    }

    private void selectPresetAvatar(int index) {
        selectedAvatarIndex = index;
        customAvatarPath = null;

        updateSelectionIndicators(index);

        Table selectedContainer = avatarContainers.get(index);
        selectedContainer.addAction(Actions.sequence(
            Actions.scaleTo(1.1f, 1.1f, 0.1f),
            Actions.scaleTo(1.0f, 1.0f, 0.1f)
        ));
    }

    private void updateSelectionIndicators(int selectedIndex) {
        for (int i = 0; i < selectionIndicators.size; i++) {
            selectionIndicators.get(i).setVisible(false);
        }

        if (selectedIndex >= 0 && selectedIndex < selectionIndicators.size) {
            selectionIndicators.get(selectedIndex).setVisible(true);

            selectionIndicators.get(selectedIndex).addAction(Actions.sequence(
                Actions.alpha(0),
                Actions.visible(true),
                Actions.alpha(1, 0.3f)
            ));
        }
    }

    private void saveSelectedAvatar() {
        boolean success = false;

        if (selectedAvatarIndex >= 0 && selectedAvatarIndex < DEFAULT_AVATAR_PATHS.length) {
            success = SaveData.getInstance().saveUserAvatar(username, DEFAULT_AVATAR_PATHS[selectedAvatarIndex]);
        } else if (customAvatarPath != null && !customAvatarPath.isEmpty()) {
            success = SaveData.getInstance().saveUserAvatar(username, customAvatarPath);
        } else {
            showMessage("Please select an avatar first!");
            return;
        }

        if (success) {
            if (onAvatarChanged != null) {
                onAvatarChanged.run();
            }

            hide();
        } else {
            showMessage("Failed to save avatar. Please try again.");
        }
    }

    private void showMessage(String message) {
        Dialog messageDialog = new Dialog("", GameAssetManager.getGameAssetManager().getSkin());
        messageDialog.text(message);
        messageDialog.button("OK");
        messageDialog.show(stage);
    }

    private Image createAvatarFrame(float size) {
        Pixmap pixmap = new Pixmap((int)size, (int)size, Pixmap.Format.RGBA8888);

        pixmap.setColor(new Color(0.2f, 0.2f, 0.3f, 1));
        pixmap.fill();

        pixmap.setColor(Color.WHITE);

        int borderThickness = 3;
        for (int i = 0; i < borderThickness; i++) {
            pixmap.drawRectangle(i, i, (int)size - i * 2, (int)size - i * 2);
        }

        Texture frameTexture = new Texture(pixmap);
        pixmap.dispose();

        Image frame = new Image(frameTexture);
        frame.setSize(size, size);

        return frame;
    }

    private TextureRegionDrawable createPanelBackground(Color color) {
        Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private TextureRegionDrawable createColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private void styleButton(final TextButton button, final Color normalColor, final Color hoverColor) {
        button.setColor(normalColor);
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.setColor(hoverColor);
                button.addAction(Actions.scaleTo(1.05f, 1.05f, 0.1f));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.setColor(normalColor);
                button.addAction(Actions.scaleTo(1f, 1f, 0.1f));
            }
        });
    }
}
