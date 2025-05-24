package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class MusicManager implements Disposable {
    private static MusicManager instance;

    private Music currentMusic;
    private Music mainTheme;
    private Music secondaryTheme;
    private float volume = 0.5f;
    private boolean musicEnabled = true;

    private MusicManager() {

        mainTheme = Gdx.audio.newMusic(Gdx.files.internal("Music/music2.mp3"));
        secondaryTheme = Gdx.audio.newMusic(Gdx.files.internal("Music/music1.mp3"));


        mainTheme.setLooping(true);
        secondaryTheme.setLooping(true);


        mainTheme.setVolume(volume);
        secondaryTheme.setVolume(volume);


        currentMusic = mainTheme;
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

        public void playDefaultMusic() {
        if (musicEnabled && currentMusic != null) {
            currentMusic.play();
        }
    }

        public void stopMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
    }

        public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

        public void resumeMusic() {
        if (musicEnabled && currentMusic != null) {
            currentMusic.play();
        }
    }

        public void switchToMainTheme() {
        switchMusic(mainTheme);
    }

        public void switchToSecondaryTheme() {
        switchMusic(secondaryTheme);
    }

        private void switchMusic(Music newMusic) {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }

        currentMusic = newMusic;

        if (musicEnabled && currentMusic != null) {
            currentMusic.setVolume(volume);
            currentMusic.play();
        }
    }

        public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));

        if (currentMusic != null) {
            currentMusic.setVolume(this.volume);
        }
    }

        public float getVolume() {
        return volume;
    }

        public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;

        if (currentMusic != null) {
            if (enabled) {
                if (!currentMusic.isPlaying()) {
                    currentMusic.play();
                }
            } else {
                if (currentMusic.isPlaying()) {
                    currentMusic.pause();
                }
            }
        }
    }

        public boolean isMusicEnabled() {
        return musicEnabled;
    }

        public boolean isPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    public boolean isMainThemePlaying() {
        return currentMusic == mainTheme && currentMusic.isPlaying();
    }

        public boolean isSecondaryThemePlaying() {
        return currentMusic == secondaryTheme && currentMusic.isPlaying();
    }

        @Override
    public void dispose() {
        if (mainTheme != null) {
            mainTheme.dispose();
        }
        if (secondaryTheme != null) {
            secondaryTheme.dispose();
        }
    }
}
