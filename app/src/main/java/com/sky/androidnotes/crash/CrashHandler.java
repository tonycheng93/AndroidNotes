package com.sky.androidnotes.crash;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 * Created by cheng on 2017/10/17.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static volatile CrashHandler sInstance = null;

    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
    private Context mContext;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        synchronized (CrashHandler.class) {
            if (sInstance == null) {
                synchronized (CrashHandler.class) {
                    sInstance = new CrashHandler();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.d("bc", "uncaughtException");
        ex.printStackTrace();
        if (mDefaultExceptionHandler != null) {
            mDefaultExceptionHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private String getProcessName() {
        String processName = "";
        ActivityManager activityManager
                = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) {
            return processName;
        }
        final List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                activityManager.getRunningAppProcesses();
        if (runningAppProcesses == null) {
            return processName;
        }
        int pid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses) {
            if (processInfo.pid == pid) {
                processName = processInfo.processName;
                break;
            }
        }
        return processName;
    }

    private String getCrashLog(Throwable throwable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            throwable.printStackTrace(new PrintStream(baos));
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toString();
    }
}
