package com.example.mypc.relaxsound.controls;

import android.content.Context;

import com.example.mypc.relaxsound.R;
import com.example.mypc.relaxsound.util.PlayerConstants;

/**
 * Created by MyPC on 20/05/2016.
 */
public class Controls {
    static String LOG_CLASS = "Controls";

    public static void playControl(Context context) {
        sendMessage(context.getResources().getString(R.string.play));
    }

    public static void pauseControl(Context context) {
        sendMessage(context.getResources().getString(R.string.pause));
    }

    private static void sendMessage(String message) {
        try {
            PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0, message));
        } catch (Exception e) {
        }
    }
}
