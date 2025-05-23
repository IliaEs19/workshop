package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GameSettings {
    private static final String PREFS_NAME = "tilldawn_settings";
    private static final String CONTROL_TYPE_KEY = "control_type";

    // انواع کنترل
    public enum ControlType {
        WASD, ARROWS
    }

    private static GameSettings instance;
    private Preferences prefs;
    private ControlType controlType;

    private GameSettings() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        // بارگذاری تنظیمات ذخیره شده یا استفاده از مقادیر پیش‌فرض
        String controlTypeStr = prefs.getString(CONTROL_TYPE_KEY, ControlType.WASD.name());
        try {
            controlType = ControlType.valueOf(controlTypeStr);
        } catch (Exception e) {
            controlType = ControlType.WASD; // مقدار پیش‌فرض
        }
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public ControlType getControlType() {
        return controlType;
    }

    public void setControlType(ControlType controlType) {
        this.controlType = controlType;
        prefs.putString(CONTROL_TYPE_KEY, controlType.name());
        prefs.flush(); // ذخیره تغییرات
    }

    public boolean isUsingWASD() {
        return controlType == ControlType.WASD;
    }

    public boolean isUsingArrows() {
        return controlType == ControlType.ARROWS;
    }
}
