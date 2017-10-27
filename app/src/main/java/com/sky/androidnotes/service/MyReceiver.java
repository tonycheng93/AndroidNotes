package com.sky.androidnotes.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";

    private Handler mHandler = new Handler();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "onReceive: context = " + context + ",intent = " + intent);
        if (intent != null) {
            context.startService(new Intent(context, LocalService.class));
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    context.startService(new Intent(context, RemoteService.class));
//                }
//            }, 3000);
        }
    }
}
