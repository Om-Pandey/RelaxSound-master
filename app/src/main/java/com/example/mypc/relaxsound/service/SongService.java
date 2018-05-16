package com.example.mypc.relaxsound.service;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.example.mypc.relaxsound.R;
import com.example.mypc.relaxsound.RelaxActivity;
import com.example.mypc.relaxsound.receiver.NotificationBroadcast;
import com.example.mypc.relaxsound.util.PlayerConstants;
import com.example.mypc.relaxsound.util.UtilFunctions;

/**
 * Created by MyPC on 20/05/2016.
 */
public class SongService extends Service implements AudioManager.OnAudioFocusChangeListener {

    String LOG_CLASS = "SongService";
    int NOTIFICATION_ID = 1111;
    public static final String NOTIFY_DELETE = "com.example.mypc.relaxsound.delete";
    public static final String NOTIFY_PAUSE = "com.example.mypc.relaxsound.pause";
    public static final String NOTIFY_PLAY = "com.example.mypc.relaxsound.play";
    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    AudioManager audioManager;
    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;


    @Override
    public void onCreate() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVersionSupportBigNotification = UtilFunctions.currentVersionSupportBigNotification();
        currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
        super.onCreate();
    }


    public void stopMusic() {
        for (int i = 0; i < PlayerConstants.SONGS_LIST.size(); i++) {
            if (PlayerConstants.SONGS_LIST.get(i).isplay() == true) {
                PlayerConstants.SONGS_LIST.get(i).setIsplay(false);
                PlayerConstants.SONGS_LIST.get(i).play();
            }
        }
    }


    public static int checkplay() {
        for (int i = 0; i < PlayerConstants.SONGS_LIST.size(); i++) {
            if (PlayerConstants.SONGS_LIST.get(i).isplay() == true) {
                return i;
            }
        }
        return -1;
    }

    public void pauseMusic() {

        for (int i = 0; i < PlayerConstants.SONGS_LIST.size(); i++) {
            PlayerConstants.SONGS_LIST.get(i).pause();
        }
    }

    public void playMusic() {
        for (int i = 0; i < PlayerConstants.SONGS_LIST.size(); i++) {
            PlayerConstants.SONGS_LIST.get(i).play();
        }
    }
    public String gettitle() {
        String s = "list : ";
        for (int i = 0; i < PlayerConstants.SONGS_LIST.size(); i++) {
            if (PlayerConstants.SONGS_LIST.get(i).isplay() == true) {
                s = s + " " + PlayerConstants.SONGS_LIST.get(i).getTitle();
            }
        }
        return s;
    }

    @Override
    public void onDestroy() {
        stopMusic();
        PlayerConstants.SONGS_LIST = UtilFunctions.listOfSongs(getApplicationContext());
        PlayerConstants.SONG_PAUSED = false;
        RelaxActivity.changeUI();
        super.onDestroy();
    }


    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (PlayerConstants.SONGS_LIST.size() <= 0) {
                PlayerConstants.SONGS_LIST = UtilFunctions.listOfSongs(getApplicationContext());
            }
            if (currentVersionSupportLockScreenControls) {
                RegisterRemoteClient();
            }
            if (checkplay() >= 0) {
                PlayerConstants.SONG_PAUSED = false;
                playMusic();
            }
            RelaxActivity.changeUI();
            newNotification();

            PlayerConstants.SONG_CHANGE_HANDLER = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {

                    if (checkplay() >= 0) PlayerConstants.SONG_PAUSED = false;
                    else PlayerConstants.SONG_PAUSED = true;
                    newNotification();
                    playSong();
                    RelaxActivity.changeUI();
                    return false;
                }
            });

            PlayerConstants.PLAY_PAUSE_HANDLER = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String message = (String) msg.obj;

                    if (message.equalsIgnoreCase(getResources().getString(R.string.play))) {
                        PlayerConstants.SONG_PAUSED = false;

                        if (currentVersionSupportLockScreenControls) {
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        playMusic();
                    } else if (message.equalsIgnoreCase(getResources().getString(R.string.pause))) {
                        PlayerConstants.SONG_PAUSED = true;
                        if (currentVersionSupportLockScreenControls) {
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                        }
                        pauseMusic();
                    }
                    newNotification();
                    RelaxActivity.changeButton();
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    public void setListeners(RemoteViews view) {
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent play = new Intent(NOTIFY_PLAY);
        Intent intent = new Intent(this, RelaxActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.bg_noti, pendingIntent);
        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);
        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);
        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);
    }

    @SuppressLint("NewApi")
    private void newNotification() {
        String songName = gettitle();
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);

        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_music)
                .setContentTitle(songName).build();

        setListeners(simpleContentView);
        setListeners(expandedView);

        notification.contentView = simpleContentView;
        if (currentVersionSupportBigNotification) {
            notification.bigContentView = expandedView;
        }

        try {
            int albumId = R.drawable.logo;
            if (checkplay() >= 0) {
                albumId = PlayerConstants.SONGS_LIST.get(checkplay()).getBg();
            }
            notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, albumId);
            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, albumId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (PlayerConstants.SONG_PAUSED) {
            notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
            }
        } else {
            notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
            }
        }

        notification.contentView.setTextViewText(R.id.textSongName, songName);


        if (currentVersionSupportBigNotification) {
            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
        }
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_ID, notification);
    }

    @SuppressLint("NewApi")
    private void playSong() {
        if (currentVersionSupportLockScreenControls) {
            UpdateMetadata();
            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
        }
        playMusic();
    }

    @SuppressLint("NewApi")
    private void UpdateMetadata() {
        if (remoteControlClient == null) return;
        MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
        metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, gettitle());

        int albumId = R.drawable.logo;
        if (checkplay() >= 0) {
            albumId = PlayerConstants.SONGS_LIST.get(checkplay()).getBg();
        }
        Bitmap mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), albumId);
        metadataEditor.putBitmap(RemoteControlClient.MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
        metadataEditor.apply();
        audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    @SuppressLint("NewApi")
    private void RegisterRemoteClient() {
        remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
        try {
            if (remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP);

        } catch (Exception ex) {
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
