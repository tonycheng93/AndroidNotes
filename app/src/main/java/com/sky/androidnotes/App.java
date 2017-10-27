package com.sky.androidnotes;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.sky.androidnotes.service.LocalService;
import com.sky.androidnotes.service.RemoteService;

/**
 * Created by tonycheng on 2017/10/9.
 */

public class App extends Application {

    private static final String TAG = "App";

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        Fresco.initialize(this);
    }

    private void startService(Context context){
        context.startService(new Intent(context,LocalService.class));
        context.startService(new Intent(context,RemoteService.class));
    }
}
