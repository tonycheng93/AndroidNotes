package com.sky.androidnotes.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sky.androidnotes.R;
import com.sky.androidnotes.timer.SimpleTimerTask;
import com.sky.androidnotes.timer.SimpleTimerTaskHandler;

/**
 * Created by tonycheng on 2017/6/9.
 */

public class ErrorView extends LinearLayout {

    private static final String TAG = "ErrorView";

    private Button message;
    private Button btnNormal;
    private Button btnDelay;
    private Button btnTimer;

    public ErrorView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setBackgroundColor(Color.parseColor("#6ba5ce"));

        message = new Button(getContext());
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher_round);
        drawable.setBounds(20, 20, 20, 20);
        message.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        message.setText("Test");
        message.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(200,
                80);
        messageParams.gravity = Gravity.CENTER;
        addView(message, messageParams);

        final SimpleTimerTask task = new SimpleTimerTask(2000) {
            @Override
            public void run() {
                Log.d(TAG, "run: loop.");
            }
        };

        btnNormal = new Button(getContext());
        btnNormal.setText("发送即时任务");
        addView(btnNormal, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        btnNormal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimerTaskHandler.getInstance().sendTask(0, task);
            }
        });

        btnDelay = new Button(getContext());
        btnDelay.setText("发送延时任务");
        addView(btnDelay, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        btnDelay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimerTaskHandler.getInstance().removeTask(0,task);
            }
        });

        btnTimer = new Button(getContext());
        btnTimer.setText("发送定时任务");
        addView(btnTimer, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));
        btnTimer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimerTaskHandler.getInstance().sendTimerTask(2, task, 14, 25);
            }
        });
    }
}
