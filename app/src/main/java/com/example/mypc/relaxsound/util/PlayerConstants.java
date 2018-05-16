package com.example.mypc.relaxsound.util;

import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by MyPC on 20/05/2016.
 */
public class PlayerConstants {
    public static ArrayList<MediaItem> SONGS_LIST = new ArrayList<MediaItem>();
    public static int TIME= 0;
    public static boolean SONG_PAUSED = true;
    public static Handler PLAY_PAUSE_HANDLER;
    public static Handler SONG_CHANGE_HANDLER ;
    public static Handler PROGRESSBAR_HANDLER;
}
