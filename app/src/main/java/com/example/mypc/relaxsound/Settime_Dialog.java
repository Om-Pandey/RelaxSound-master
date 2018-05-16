package com.example.mypc.relaxsound;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

/**
 * Created by MyPC on 28/05/2016.
 */
public class Settime_Dialog extends Dialog {
    int[] times = {15, 30, 45, 60, 120, 180};
    TextView tv_custime, et_custime;
    ImageView bt_timepicker;
    ArrayList<ImageView> list_time;
    Context mcontext;
    int time;
    public Settime_Dialog(final Context context, final int timesx, final Handler handler) {
        super(context);
        this.setCancelable(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settime_activity);
        getWindow().setBackgroundDrawableResource(R.color.trongsuot);
        mcontext = context;
        this.time = timesx;
        tv_custime = (TextView) findViewById(R.id.tv_custime);
        et_custime = (TextView) findViewById(R.id.et_custume);
        bt_timepicker = (ImageView) findViewById(R.id.bt_timepicker);

        et_custime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < list_time.size(); i++) {
                        list_time.get(i).setBackgroundResource(R.drawable.bg_time);
                }
                TimePickerDialog tpd = new TimePickerDialog(mcontext,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String s = "";
                                if (hourOfDay < 10) s = s + "0" + hourOfDay + ":";
                                else s = s + "" + hourOfDay + ":";
                                if (minute < 10) s = s + "0" + minute;
                                else s = s + "" + minute;
                                et_custime.setText(s);
                                time = hourOfDay * 60 + minute;
                            }
                        }, 0, 0,true);
                tpd.show();
            }
        });
        bt_timepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settime_Dialog.this.dismiss();
            }
        });

        ImageView img_time15, img_time30, img_time45, img_time1h, img_time2h, img_time3h;
        img_time15 = (ImageView) findViewById(R.id.img_time15);
        img_time30 = (ImageView) findViewById(R.id.img_time30);
        img_time45 = (ImageView) findViewById(R.id.img_time45);
        img_time1h = (ImageView) findViewById(R.id.img_time1h);
        img_time2h = (ImageView) findViewById(R.id.img_time2h);
        img_time3h = (ImageView) findViewById(R.id.img_time3h);


        list_time = new ArrayList<>();
        list_time.add(img_time15);
        list_time.add(img_time30);
        list_time.add(img_time45);
        list_time.add(img_time1h);
        list_time.add(img_time2h);
        list_time.add(img_time3h);
        for (int i = 0; i < list_time.size(); i++) {
            if (time == times[i]) list_time.get(i).setBackgroundResource(R.drawable.bg_timec);
            list_time.get(i).setTag(times[i]);
            list_time.get(i).setOnClickListener(onclick);
        }
        Typeface font_text = Typeface.createFromAsset(mcontext.getAssets(), "fontchu.otf");
        et_custime.setTypeface(font_text);
        tv_custime.setTypeface(font_text);

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Settime_Dialog.this.dismiss();
                }
                return false;
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Message message = handler.obtainMessage();
                time = time *60; // doi ra s
                message.arg1 = time;
                handler.sendMessage(message);
            }
        });
    }

    View.OnClickListener onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < list_time.size(); i++) {
                if (list_time.get(i).getTag() == v.getTag()) {
                    time = (int) v.getTag();
                    list_time.get(i).setBackgroundResource(R.drawable.bg_timec);
                } else
                    list_time.get(i).setBackgroundResource(R.drawable.bg_time);
            }
            Settime_Dialog.this.dismiss();

        }
    };

}
