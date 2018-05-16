package com.example.mypc.relaxsound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypc.relaxsound.controls.Controls;
import com.example.mypc.relaxsound.service.SongService;
import com.example.mypc.relaxsound.util.PlayerConstants;
import com.example.mypc.relaxsound.util.UtilFunctions;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class RelaxActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    static Button btnPause, btnPlay, btnStop;
    static TextView tvTime;
    static LinearLayout layoutControl;
    static Context context;
    static SeekBar bar;
    static ProgressBar sbar;
    static GridLayout gridLayout;
    static int w, h;
    static List<ImageView> listBtn;
    ScrollView scroll;
    static int location = -1;


    static Object objectTime;
    static int time;

    ImageView img_settime, img_share,img_s2b;
    LinearLayout root;

    static class ThreadTime {
        public ThreadTime() {
            Time.start();
        }

        Thread Time = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    synchronized (objectTime) {
                        while (PlayerConstants.SONG_PAUSED) {
                            try {
                                objectTime.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Time.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message message = hTime.obtainMessage();
                    message.arg1 = 1;
                    hTime.sendMessage(message);
                }
            }
        });

        Handler hTime = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                time--;
                sbar.setProgress(time);
                if (time == 0 && PlayerConstants.SONG_PAUSED == false) {
                    layoutControl.setVisibility(View.INVISIBLE);
                    PlayerConstants.TIME = 0;
                    tvTime.setText(settime(0));
                    bar.setVisibility(View.INVISIBLE);
                    Intent i = new Intent(context, SongService.class);
                    context.stopService(i);
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setFinishOnTouchOutside(false);
        setContentView(R.layout.relax_activity);
        context = RelaxActivity.this;
        objectTime = new Object();
        init();
    }

    private void init() {
        getViews();
        layoutControl.setVisibility(View.INVISIBLE);
        if (PlayerConstants.SONGS_LIST.size() <= 0) {
            PlayerConstants.SONGS_LIST = UtilFunctions.listOfSongs(getApplicationContext());
        } else {
            if (SongService.checkplay() > 0) layoutControl.setVisibility(View.VISIBLE);
        }
        setListItems();
        setListeners();
    }
    private static void setListItems() {
        listBtn = UtilFunctions.getListBtn(context);
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(2);
        int size = h / 9;
        for (ImageView i : listBtn) {
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.setMargins(70, 15, 70, 15);
            layoutParams.width = size;
            layoutParams.height = size;
            i.setLayoutParams(layoutParams);
            gridLayout.addView(i);
            i.setOnClickListener(Click);
        }
    }
    static View.OnClickListener Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            if (PlayerConstants.SONGS_LIST.get(position).isplay()) {
                location = -1;
                PlayerConstants.SONGS_LIST.get(position).setIsplay(false);
                bar.setVisibility(View.INVISIBLE);
                PlayerConstants.SONGS_LIST.get(position).pause();
            } else {
                location = position;
                PlayerConstants.SONGS_LIST.get(position).setIsplay(true);
                layoutControl.setVisibility(View.VISIBLE);
                if(PlayerConstants.TIME>0){
                    time = PlayerConstants.TIME;
                    PlayerConstants.SONG_PAUSED=false;
                    new ThreadTime();
                }
            }
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), context);
            if (!isServiceRunning) {
                Intent i = new Intent(context, SongService.class);
                context.startService(i);
            } else {
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }
            if (SongService.checkplay() == -1) {
                layoutControl.setVisibility(View.INVISIBLE);
                PlayerConstants.TIME = 0;
                tvTime.setText(settime(0));
                bar.setVisibility(View.INVISIBLE);
                Intent i = new Intent(context, SongService.class);
                context.stopService(i);
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            boolean isServiceRunning = UtilFunctions.isServiceRunning(SongService.class.getName(), getApplicationContext());
            if (isServiceRunning) {
            } else {

                bar.setVisibility(View.INVISIBLE);

                if (PlayerConstants.TIME > 0) {
                    tvTime.setText(settime(PlayerConstants.TIME));
                    setProgessBar(PlayerConstants.TIME);
                }

            }
            changeUI();
            tvTime.setText(PlayerConstants.TIME);
        } catch (Exception e) {
        }
    }

    public static void setProgessBar(int tmp) {
        sbar.setProgress(0);
        sbar.setMax(tmp);
    }

    private void getViews() {
        btnPause = (Button) findViewById(R.id.button_pause);
        btnPlay = (Button) findViewById(R.id.button_start);
        btnStop = (Button) findViewById(R.id.button_stop);
        scroll = (ScrollView) findViewById(R.id.scroll);
        bar = (SeekBar) findViewById(R.id.sbar);
        tvTime = (TextView) findViewById(R.id.tv_time);
        gridLayout = (GridLayout) findViewById(R.id.grid);
        layoutControl = (LinearLayout) findViewById(R.id.layout_control);
        sbar = (ProgressBar) findViewById(R.id.sbars);
        sbar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        bar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Display display = getWindowManager().getDefaultDisplay();
        Point s = new Point();
        display.getSize(s);
        w = display.getWidth();
        h = display.getHeight();
        bar.setVisibility(View.INVISIBLE);
        bar.setOnSeekBarChangeListener(this);
        Typeface font_text = Typeface.createFromAsset(this.getAssets(), "fontchu.otf");
        tvTime.setTypeface(font_text);
        img_settime = (ImageView) findViewById(R.id.img_settime);
        img_share = (ImageView) findViewById(R.id.img_share);
        img_s2b = (ImageView) findViewById(R.id.img_s2b);


        root = (LinearLayout) findViewById(R.id.root);
        img_settime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settime_Dialog endDialog = new Settime_Dialog(context, PlayerConstants.TIME, handler);
                endDialog.show();
            }
        });

        img_s2b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/developer?id=S2B+Game+Studio");
                Toast.makeText(context,"s2b",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap b = getBitmap(root);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), b, "Title", null);
                Uri imageUri = Uri.parse(path);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/*");
                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                context.startActivity(Intent.createChooser(share, "Select"));
            }
        });
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int time = msg.arg1;
            tvTime.setText(settime(time));
            PlayerConstants.TIME = time;
            Toast.makeText(context,"time"+time,Toast.LENGTH_SHORT).show();
            setProgessBar(time);
            if(PlayerConstants.SONG_PAUSED==false){
                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
            }
        }
    };

    public static String settime(int x) {
        x=x/60;
        if (x == 0) return ".. : ..";
        int h = x / 60;
        int m = x % 60;
        String s = "";
        if (h < 10) s = s + "0" + h + ":";
        else s = s + "" + h + ":";
        if (m < 10) s = s + "0" + m;
        else s = s + "" + m;
        return s;
    }

    private Bitmap getBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private static void setListeners() {
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                synchronized (objectTime) {
                    objectTime.notifyAll();
                }
                Controls.playControl(context);
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.INVISIBLE);
                synchronized (objectTime) {
                    objectTime.notifyAll();
                }
                Controls.pauseControl(context);
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutControl.setVisibility(View.INVISIBLE);
                PlayerConstants.TIME = 0;
                tvTime.setText(settime(0));
                setProgessBar(PlayerConstants.TIME);
                sbar.setProgress(PlayerConstants.TIME);
                Intent i = new Intent(context, SongService.class);
                context.stopService(i);
            }
        });
    }

    public static void changeButton() {
        if (PlayerConstants.SONG_PAUSED) {
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
        } else {
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
        }
    }
    public static void changeUI() {
        if (location >= 0&&PlayerConstants.SONG_PAUSED==false) {
            bar.setProgress(PlayerConstants.SONGS_LIST.get(location).getVolume());
            bar.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bar.setVisibility(View.INVISIBLE);
                }
            }, 5000);
        }
        setListItems();
        changeButton();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (location >= 0) {
            PlayerConstants.SONGS_LIST.get(location).setVolume(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
