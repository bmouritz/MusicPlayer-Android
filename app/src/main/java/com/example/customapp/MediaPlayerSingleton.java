package com.example.customapp;

import android.media.MediaPlayer;
import android.widget.Button;

// Singleton to manage MediaPlayer
class MediaPlayerSingleton {
    private static MediaPlayer mediaPlayer;

    static MediaPlayer getInstance(){
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        return mediaPlayer;
    }

    public static void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public static void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

}
