package com.tilldawn.Controllers;

import com.tilldawn.Models.GameSettings;
import com.tilldawn.Views.SettingMenu;

public class SettingMenuController {
    private SettingMenu view;

    public void setView(SettingMenu view) {
        this.view = view;
    }

    // متد جدید برای دریافت نوع کنترل فعلی
    public GameSettings.ControlType getControlType() {
        return GameSettings.getInstance().getControlType();
    }

    // متد جدید برای تغییر نوع کنترل
    public void setControlType(GameSettings.ControlType controlType) {
        GameSettings.getInstance().setControlType(controlType);
    }
}
