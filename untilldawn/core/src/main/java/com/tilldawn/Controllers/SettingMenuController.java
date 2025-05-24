package com.tilldawn.Controllers;

import com.tilldawn.Models.GameSettings;
import com.tilldawn.Views.SettingMenu;

public class SettingMenuController {
    private SettingMenu view;

    public void setView(SettingMenu view) {
        this.view = view;
    }

    public GameSettings.ControlType getControlType() {
        return GameSettings.getInstance().getControlType();
    }

    public void setControlType(GameSettings.ControlType controlType) {
        GameSettings.getInstance().setControlType(controlType);
    }
}
