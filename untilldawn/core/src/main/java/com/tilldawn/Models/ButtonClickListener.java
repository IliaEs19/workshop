package com.tilldawn.Models;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
//import com.tilldawn.Models.SoundManager;

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
        // پخش صدای کلیک
        //SoundManager.getInstance().playButtonClick();

        // اجرای عملیات تعیین شده
        if (action != null) {
            action.run();
        }
    }
}
