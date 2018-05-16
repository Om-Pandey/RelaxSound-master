package com.example.mypc.relaxsound.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.example.mypc.relaxsound.RelaxActivity;
import com.example.mypc.relaxsound.controls.Controls;
import com.example.mypc.relaxsound.service.SongService;
import com.example.mypc.relaxsound.util.PlayerConstants;

/**
 * Created by MyPC on 20/05/2016.
 */
public class NotificationBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if (!PlayerConstants.SONG_PAUSED) {
                        Controls.pauseControl(context);
                    } else {
                        Controls.playControl(context);
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    break;
            }
        } else {
            if (intent.getAction().equals(SongService.NOTIFY_PLAY)) {
                Controls.playControl(context);
            } else if (intent.getAction().equals(SongService.NOTIFY_PAUSE)) {
                Controls.pauseControl(context);
            } else if (intent.getAction().equals(SongService.NOTIFY_DELETE)) {
                Intent i = new Intent(context, SongService.class);
                context.stopService(i);
                Intent in = new Intent(context, RelaxActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
            }
        }
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}
