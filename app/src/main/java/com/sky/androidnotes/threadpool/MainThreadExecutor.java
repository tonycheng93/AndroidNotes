package com.sky.androidnotes.threadpool;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by tonycheng on 2017/6/20.
 */

public class MainThreadExecutor implements Executor {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(@NonNull Runnable runnable) {
        mHandler.post(runnable);
    }
}
