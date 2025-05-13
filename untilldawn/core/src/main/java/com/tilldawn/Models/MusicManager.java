package com.tilldawn.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

public class MusicManager implements Disposable {
    private static MusicManager instance;

    private Music currentMusic;
    private Music mainTheme;
    private Music secondaryTheme;
    private float volume = 0.5f; // حجم صدای پیش‌فرض (0 تا 1)
    private boolean musicEnabled = true; // فعال/غیرفعال بودن موزیک

    private MusicManager() {
        // بارگذاری موزیک‌ها
        mainTheme = Gdx.audio.newMusic(Gdx.files.internal("Music/music2.mp3"));
        secondaryTheme = Gdx.audio.newMusic(Gdx.files.internal("Music/music1.mp3"));

        // تنظیم ویژگی‌های موزیک‌ها
        mainTheme.setLooping(true); // تکرار خودکار
        secondaryTheme.setLooping(true);

        // تنظیم حجم صدا
        mainTheme.setVolume(volume);
        secondaryTheme.setVolume(volume);

        // تنظیم موزیک فعلی به موزیک اصلی
        currentMusic = mainTheme;
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    /**
     * شروع پخش موزیک پیش‌فرض
     */
    public void playDefaultMusic() {
        if (musicEnabled && currentMusic != null) {
            currentMusic.play();
        }
    }

    /**
     * توقف پخش موزیک فعلی
     */
    public void stopMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.stop();
        }
    }

    /**
     * مکث پخش موزیک فعلی
     */
    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

    /**
     * ادامه پخش موزیک فعلی (اگر در حالت مکث باشد)
     */
    public void resumeMusic() {
        if (musicEnabled && currentMusic != null) {
            currentMusic.play();
        }
    }

    /**
     * تغییر به موزیک اصلی
     */
    public void switchToMainTheme() {
        switchMusic(mainTheme);
    }

    /**
     * تغییر به موزیک ثانویه
     */
    public void switchToSecondaryTheme() {
        switchMusic(secondaryTheme);
    }

    /**
     * تغییر موزیک فعلی به موزیک جدید
     */
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

    /**
     * تنظیم حجم صدای موزیک
     * @param volume مقدار بین 0 (بی‌صدا) تا 1 (حداکثر صدا)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume)); // محدود کردن بین 0 و 1

        if (currentMusic != null) {
            currentMusic.setVolume(this.volume);
        }
    }

    /**
     * دریافت حجم صدای فعلی
     */
    public float getVolume() {
        return volume;
    }

    /**
     * فعال/غیرفعال کردن موزیک
     */
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

    /**
     * بررسی فعال بودن موزیک
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * بررسی در حال پخش بودن موزیک
     */
    public boolean isPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    public boolean isMainThemePlaying() {
        return currentMusic == mainTheme && currentMusic.isPlaying();
    }

    /**
     * بررسی اینکه آیا موزیک ثانویه در حال پخش است
     */
    public boolean isSecondaryThemePlaying() {
        return currentMusic == secondaryTheme && currentMusic.isPlaying();
    }

    /**
     * آزادسازی منابع
     */
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
