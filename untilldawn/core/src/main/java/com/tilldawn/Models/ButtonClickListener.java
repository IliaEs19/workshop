package com.tilldawn.Models;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;


public class ButtonClickListener extends ClickListener {
    private Runnable action;

    public ButtonClickListener() {
        this(null);
    }

    public ButtonClickListener(Runnable action) {
        this.action = action;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {




        if (action != null) {
            action.run();
        }
    }
}
