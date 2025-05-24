package com.tilldawn.Models;

public enum CheatCode {
    DECREASE_TIME(4, "Decrease Time", "Reduces the remaining game time by one minute"),
    LEVEL_UP(5, "Level Up", "Increases player level and grants access to new abilities"),
    REFILL_HEALTH(6, "Refill Health", "Restores player health to maximum (only if below 50%)"),
    BOSS_FIGHT(7, "Boss Fight", "Starts a battle with the Elder boss"),
    INFINITE_SHOOTING(8, "Infinite Shooting", "Enables infinite shooting without need to reload");

    private final int keyCode;
    private final String name;
    private final String description;

        CheatCode(int keyCode, String name, String description) {
        this.keyCode = keyCode;
        this.name = name;
        this.description = description;
    }

        public int getKeyCode() {
        return keyCode;
    }

        public String getName() {
        return name;
    }

        public String getDescription() {
        return description;
    }

        public static CheatCode getByKeyCode(int keyCode) {
        for (CheatCode cheat : values()) {
            if (cheat.keyCode == keyCode) {
                return cheat;
            }
        }
        return null;
    }

        public static boolean isValidCheatKeyCode(int keyCode) {
        return getByKeyCode(keyCode) != null;
    }

        public String getMenuText() {
        return String.format("Key %d: %s - %s", keyCode, name, description);
    }

        public String getShortText() {
        return String.format("%d: %s", keyCode, name);
    }
}
