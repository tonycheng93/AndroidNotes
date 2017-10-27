package com.sky.androidnotes.thread;

import android.util.Log;

/**
 * Created by tonycheng on 2017/7/1.
 */

public class SnowThread {

    private static final String TAG = "xxx";

    private volatile boolean finished = false;

    private boolean isDrawing = true;

    int count = 0;

    public void start() {
        Log.d(TAG, "start thread...");
        new Thread() {
            @Override
            public void run() {
                while (count < 300) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "run: is running.");
                    count++;
                }
                Log.d(TAG, "stop thread.");
                isDrawing = false;
                count = 0;
            }
        }.start();
    }

    public boolean isDrawing() {
        return isDrawing;
    }
}
