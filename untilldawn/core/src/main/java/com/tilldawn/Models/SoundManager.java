package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import com.badlogic.gdx.scenes.scene2d.ui.Button;


public class SoundManager implements Disposable {
    private static SoundManager instance;

    private ObjectMap<String, Sound> sounds;
    private boolean soundEnabled = true;
    private float volume = 0.4f;

    public static final String BUTTON_CLICK = "Music/button_click.wav";
    public static final String WEAPON_FIRE = "weapon_fire";
    public static final String WEAPON_RELOAD = "weapon_reload";
    public static final String PLAYER_HURT = "player_hurt";
    public static final String PLAYER_DEATH = "player_death";
    public static final String ENEMY_HURT = "enemy_hurt";
    public static final String ENEMY_DEATH = "enemy_death";
    public static final String LEVEL_UP = "level_up";
    public static final String GAME_START = "game_start";
    public static final String YOU_LOSE = "you_lose";
    public static final String YOU_WIN = "you_win";

    private SoundManager() {
        sounds = new ObjectMap<>();
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSounds() {
        try {
            sounds.put(BUTTON_CLICK, Gdx.audio.newSound(Gdx.files.internal("Music/button_click.wav")));
            sounds.put(WEAPON_FIRE, Gdx.audio.newSound(Gdx.files.internal("Music/weapon_fire.wav")));
            sounds.put(WEAPON_RELOAD, Gdx.audio.newSound(Gdx.files.internal("Music/weapon_reload.wav")));
            sounds.put(PLAYER_HURT, Gdx.audio.newSound(Gdx.files.internal("Music/Blood_Splash_Quick_01.wav")));
//            sounds.put(PLAYER_DEATH, Gdx.audio.newSound(Gdx.files.internal("sounds/player_death.wav")));
//            sounds.put(ENEMY_HURT, Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_hurt.wav")));
//            sounds.put(ENEMY_DEATH, Gdx.audio.newSound(Gdx.files.internal("sounds/enemy_death.wav")));
            sounds.put(LEVEL_UP, Gdx.audio.newSound(Gdx.files.internal("Music/Special & Powerup (13).wav")));
//            sounds.put(GAME_START, Gdx.audio.newSound(Gdx.files.internal("sounds/game_start.wav")));
            sounds.put(YOU_LOSE, Gdx.audio.newSound(Gdx.files.internal("Music/You Lose (4).wav")));
            sounds.put(YOU_WIN, Gdx.audio.newSound(Gdx.files.internal("Music/You Win (2).wav")));
        } catch (Exception e) {
            Gdx.app.error("SoundManager", "Error loading sounds", e);
        }
    }

    public void play(String soundName) {
        if (soundEnabled && sounds.containsKey(soundName)) {
            sounds.get(soundName).play(volume);
        }
    }

    public void play(String soundName, float volume) {
        if (soundEnabled && sounds.containsKey(soundName)) {
            sounds.get(soundName).play(this.volume * volume);
        }
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public float getVolume() {
        return volume;
    }

    public static void addSoundToButton(Button button) {
        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int buttonCode) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int buttonCode) {
                getInstance().play(BUTTON_CLICK);
            }
        });
    }


    @Override
    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
        instance = null;
    }
}
