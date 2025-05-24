package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class DialogManager {

    private static final float ANIMATION_DURATION = 0.3f;

        public static void showErrorDialog(Stage stage, String title, String message, final Runnable onDismiss) {

        final Dialog dialog = new Dialog("", GameAssetManager.getGameAssetManager().getSkin()) {
            @Override
            protected void result(Object object) {
                if (object instanceof Boolean && (Boolean) object) {
                    hide();
                    remove();
                    if (onDismiss != null) {
                        onDismiss.run();
                    }
                }
            }
        };


        dialog.getTitleTable().clear();


        Label titleLabel = new Label(title, GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setColor(Color.FIREBRICK);
        titleLabel.setFontScale(1.2f);
        dialog.getTitleTable().add(titleLabel).expandX().center().padTop(15);


        TextButton closeButton = new TextButton("X", GameAssetManager.getGameAssetManager().getSkin());
        closeButton.setColor(Color.FIREBRICK);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                dialog.remove();
                if (onDismiss != null) {
                    onDismiss.run();
                }
            }
        });
        dialog.getTitleTable().add(closeButton).padRight(10).padTop(5).size(30, 30);


        Table contentTable = new Table();
        contentTable.pad(20);


        Image errorIcon = createColorCircle(Color.RED, 30);
        contentTable.add(errorIcon).size(48, 48).padRight(20);


        Label messageLabel = new Label(message, GameAssetManager.getGameAssetManager().getSkin());
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.left);
        messageLabel.setColor(Color.RED);
        contentTable.add(messageLabel).width(350).expandX().fillX();

        dialog.getContentTable().add(contentTable).pad(20);


        TextButton okButton = new TextButton("OK", GameAssetManager.getGameAssetManager().getSkin());
        okButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1));


        okButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                okButton.setColor(new Color(1f, 0.3f, 0.3f, 1));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                okButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1));
            }
        });

        dialog.button(okButton, true);
        dialog.getButtonTable().padBottom(15);


        dialog.setModal(true);
        dialog.setMovable(false);


        dialog.show(stage);
        dialog.setOrigin(Align.center);
        dialog.setScale(0.1f);
        dialog.getColor().a = 0;
        dialog.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(ANIMATION_DURATION),
                Actions.scaleTo(1, 1, ANIMATION_DURATION, Interpolation.swingOut)
            )
        ));


        try {
            Window dialogWindow = (Window) dialog.getChildren().get(0);
            dialogWindow.setColor(new Color(1f, 0.9f, 0.9f, 0.95f));
        } catch (Exception e) {
            Gdx.app.log("DialogManager", "Could not change background color");
        }
    }

        public static void showSuccessDialog(Stage stage, String title, String message, final Runnable onDismiss) {

        final Dialog dialog = new Dialog("", GameAssetManager.getGameAssetManager().getSkin()) {
            @Override
            protected void result(Object object) {
                if (object instanceof Boolean && (Boolean) object) {
                    hide();
                    remove();
                    if (onDismiss != null) {
                        onDismiss.run();
                    }
                }
            }
        };


        dialog.getTitleTable().clear();


        Label titleLabel = new Label(title, GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setColor(Color.FOREST);
        titleLabel.setFontScale(1.2f);
        dialog.getTitleTable().add(titleLabel).expandX().center().padTop(15);


        TextButton closeButton = new TextButton("X", GameAssetManager.getGameAssetManager().getSkin());
        closeButton.setColor(Color.FOREST);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
                dialog.remove();
                if (onDismiss != null) {
                    onDismiss.run();
                }
            }
        });
        dialog.getTitleTable().add(closeButton).padRight(10).padTop(5).size(30, 30);


        Table contentTable = new Table();
        contentTable.pad(20);


        Image successIcon = createColorCircle(Color.GREEN, 30);
        contentTable.add(successIcon).size(48, 48).padRight(20);


        Label messageLabel = new Label(message, GameAssetManager.getGameAssetManager().getSkin());
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.left);
        messageLabel.setColor(Color.GREEN);
        contentTable.add(messageLabel).width(350).expandX().fillX();

        dialog.getContentTable().add(contentTable).pad(20);


        TextButton okButton = new TextButton("OK", GameAssetManager.getGameAssetManager().getSkin());
        okButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1));


        okButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                okButton.setColor(new Color(0.3f, 1f, 0.3f, 1));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                okButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1));
            }
        });

        dialog.button(okButton, true);
        dialog.getButtonTable().padBottom(15);


        dialog.setModal(true);
        dialog.setMovable(false);


        dialog.show(stage);
        dialog.setOrigin(Align.center);
        dialog.setScale(0.1f);
        dialog.getColor().a = 0;
        dialog.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(ANIMATION_DURATION),
                Actions.scaleTo(1, 1, ANIMATION_DURATION, Interpolation.swingOut)
            )
        ));


        try {
            Window dialogWindow = (Window) dialog.getChildren().get(0);
            dialogWindow.setColor(new Color(0.9f, 1f, 0.9f, 0.95f));
        } catch (Exception e) {
            Gdx.app.log("DialogManager", "Could not change background color");
        }
    }

        public static void showConfirmDialog(Stage stage, String title, String message, final Runnable onYes, final Runnable onNo) {

        final Dialog dialog = new Dialog("", GameAssetManager.getGameAssetManager().getSkin()) {
            @Override
            protected void result(Object object) {
                hide();
                remove();
                if (object instanceof Boolean) {
                    boolean result = (Boolean) object;
                    if (result && onYes != null) {
                        onYes.run();
                    } else if (!result && onNo != null) {
                        onNo.run();
                    }
                }
            }
        };


        dialog.getTitleTable().clear();


        Label titleLabel = new Label(title, GameAssetManager.getGameAssetManager().getSkin());
        titleLabel.setColor(Color.ROYAL);
        titleLabel.setFontScale(1.2f);
        dialog.getTitleTable().add(titleLabel).expandX().center().padTop(15);


        Table contentTable = new Table();
        contentTable.pad(20);


        Image questionIcon = createColorCircle(Color.ROYAL, 48);
        contentTable.add(questionIcon).size(48, 48).padRight(20);


        Label messageLabel = new Label(message, GameAssetManager.getGameAssetManager().getSkin());
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.left);
        messageLabel.setColor(Color.DARK_GRAY);
        contentTable.add(messageLabel).width(350).expandX().fillX();

        dialog.getContentTable().add(contentTable).pad(20);


        TextButton noButton = new TextButton("No", GameAssetManager.getGameAssetManager().getSkin());
        noButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1));


        noButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                noButton.setColor(new Color(1f, 0.3f, 0.3f, 1));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                noButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1));
            }
        });


        TextButton yesButton = new TextButton("Yes", GameAssetManager.getGameAssetManager().getSkin());
        yesButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1));


        yesButton.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                yesButton.setColor(new Color(0.3f, 1f, 0.3f, 1));
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                yesButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1));
            }
        });


        dialog.button(noButton, false);
        dialog.button(yesButton, true);
        dialog.getButtonTable().padBottom(15);


        dialog.setModal(true);
        dialog.setMovable(false);


        dialog.show(stage);
        dialog.setOrigin(Align.center);
        dialog.setScale(0.1f);
        dialog.getColor().a = 0;
        dialog.addAction(Actions.sequence(
            Actions.parallel(
                Actions.fadeIn(ANIMATION_DURATION),
                Actions.scaleTo(1, 1, ANIMATION_DURATION, Interpolation.swingOut)
            )
        ));


        try {
            Window dialogWindow = (Window) dialog.getChildren().get(0);
            dialogWindow.setColor(new Color(0.9f, 0.95f, 1f, 0.95f));
        } catch (Exception e) {
            Gdx.app.log("DialogManager", "Could not change background color");
        }
    }

        private static Image createColorCircle(Color color, int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(size / 2, size / 2, size / 2 - 1);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new Image(texture);
    }
}
