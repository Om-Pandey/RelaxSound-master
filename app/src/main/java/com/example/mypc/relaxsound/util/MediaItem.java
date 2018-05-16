package com.example.mypc.relaxsound.util;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Created by MyPC on 20/05/2016.
 */
public class MediaItem {
    String title;
    int maxVolume = 20;
    int bg;
    int mp3;
    int volume;
    MediaPlayer media;
    Context context;

    boolean isplay;

    public boolean isplay() {
        return isplay;
    }

    public MediaItem(int bg, int mp3, String title, Context context) {

        isplay = false;
        this.bg = bg;
        this.title = title;
        this.mp3 = mp3;
        this.bg = bg;
        volume =10;
        this.context = context;
        media = MediaPlayer.create(context,mp3);
        media.setLooping(true);
        setVolume(volume);

    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBg() {
        return bg;
    }
    public int getVolume() {
        return volume;
    }



    public void setVolume(int soundVolume) {
        this.volume = soundVolume;
        float x = (float) (1 - (Math.log(maxVolume - soundVolume) / Math.log(maxVolume)));
        if(media.isPlaying())media.setVolume(x, x);
    }
    public void setIsplay(boolean isplay) {
        this.isplay = isplay;

    }
    public void play(){
        if(isplay==true){
            media.release();
            media = MediaPlayer.create(context,mp3);
            media.setLooping(true);
            setVolume(volume);
            media.start();
        }
        else {
            media.release();
        }
    }
    public void pause(){
        media.release();
    }

}
