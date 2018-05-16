package com.example.mypc.relaxsound.util;

import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.example.mypc.relaxsound.R;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MyPC on 20/05/2016.
 */
public class UtilFunctions {
    public final static int[] icons = {R.drawable.icon1, R.drawable.icon2,
            R.drawable.icon3, R.drawable.icon4, R.drawable.icon5,
            R.drawable.icon6, R.drawable.icon7, R.drawable.icon8,
            R.drawable.icon9, R.drawable.icon10, R.drawable.icon11,
            R.drawable.icon12, R.drawable.icon13, R.drawable.icon14
            , R.drawable.icon15, R.drawable.icon16, R.drawable.icon17
            , R.drawable.icon18, R.drawable.icon19, R.drawable.icon20};

    public final static int[] mp3 = {R.raw.music1, R.raw.music2, R.raw.music3,
            R.raw.music4, R.raw.music5, R.raw.music6, R.raw.music7,
            R.raw.music8, R.raw.music9, R.raw.music10, R.raw.music11,
            R.raw.music12, R.raw.music13, R.raw.music14
            , R.raw.music15, R.raw.music16, R.raw.music17, R.raw.music18
            , R.raw.music19, R.raw.music20};

    public final static String[] title = {"air fan","at night","cafe","campus library",
            "couttry side","fire","forest","guitar",
            "leaves","ocean waves","piano","rain",
            "river","road","snow storm","stars ship",
            "thunder","train","wind chime","wind",};


    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<MediaItem> listOfSongs(Context context) {
        ArrayList<MediaItem> list = new ArrayList<MediaItem>();

        for (int i = 0; i < icons.length; i++) {
            MediaItem im = new MediaItem(icons[i], mp3[i],title[i], context
            );
            list.add(im);
        }
        return list;
    }

    public static List<ImageView> getListBtn(Context context) {
        List<ImageView> listBtn = new ArrayList<ImageView>();
        for (int i = 0; i < icons.length; i++) {
            ImageView button = new ImageView(context);
            button.setImageResource(icons[i]);
            if (PlayerConstants.SONGS_LIST.get(i).isplay() == true) {
                button.setBackgroundResource(R.drawable.bg_soundstop);
            } else button.setBackgroundResource(R.drawable.bg_soundplay);
            button.setTag(i);
            listBtn.add(button);
        }
        return listBtn;
    }

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        return false;
    }

    public static boolean currentVersionSupportLockScreenControls() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        if (sdkVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return true;
        }
        return false;
    }
}
