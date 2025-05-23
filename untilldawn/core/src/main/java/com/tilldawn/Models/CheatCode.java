package com.tilldawn.Models;

public enum CheatCode {
    DECREASE_TIME(4, "Decrease Time", "Reduces the remaining game time by one minute"),
    LEVEL_UP(5, "Level Up", "Increases player level and grants access to new abilities"),
    REFILL_HEALTH(6, "Refill Health", "Restores player health to maximum (only if below 50%)"),
    BOSS_FIGHT(7, "Boss Fight", "Starts a battle with the Elder boss"),
    INFINITE_SHOOTING(8, "Infinite Shooting", "Enables infinite shooting without need to reload");

    private final int keyCode; // شماره کلید مربوط به چیت
    private final String name; // نام چیت
    private final String description; // توضیحات چیت

    /**
     * سازنده شمارشگر
     * @param keyCode شماره کلید
     * @param name نام چیت
     * @param description توضیحات چیت
     */
    CheatCode(int keyCode, String name, String description) {
        this.keyCode = keyCode;
        this.name = name;
        this.description = description;
    }

    /**
     * دریافت شماره کلید چیت
     * @return شماره کلید
     */
    public int getKeyCode() {
        return keyCode;
    }

    /**
     * دریافت نام چیت
     * @return نام چیت
     */
    public String getName() {
        return name;
    }

    /**
     * دریافت توضیحات چیت
     * @return توضیحات چیت
     */
    public String getDescription() {
        return description;
    }

    /**
     * دریافت چیت‌کد بر اساس شماره کلید
     * @param keyCode شماره کلید
     * @return چیت‌کد مربوطه یا null اگر چیت‌کدی با این شماره وجود نداشته باشد
     */
    public static CheatCode getByKeyCode(int keyCode) {
        for (CheatCode cheat : values()) {
            if (cheat.keyCode == keyCode) {
                return cheat;
            }
        }
        return null;
    }

    /**
     * بررسی وجود چیت‌کد با شماره کلید مشخص
     * @param keyCode شماره کلید
     * @return true اگر چیت‌کدی با این شماره وجود داشته باشد
     */
    public static boolean isValidCheatKeyCode(int keyCode) {
        return getByKeyCode(keyCode) != null;
    }

    /**
     * دریافت متن راهنمای چیت‌کد برای نمایش در منو
     * @return متن راهنما
     */
    public String getMenuText() {
        return String.format("Key %d: %s - %s", keyCode, name, description);
    }

    /**
     * دریافت متن کوتاه چیت‌کد برای نمایش در UI
     * @return متن کوتاه
     */
    public String getShortText() {
        return String.format("%d: %s", keyCode, name);
    }
}
