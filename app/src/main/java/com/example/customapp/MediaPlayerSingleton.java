package com.example.customapp;

import android.media.MediaPlayer;

// Singleton to manage MediaPlayer instances
class MediaPlayerSingleton {
    private static MediaPlayer mediaPlayer;

    static MediaPlayer getInstance(){
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        return mediaPlayer;
    }

    static void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    static boolean isPlayingSong() {
        return mediaPlayer.isPlaying();
    }

    static void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

}
