package com.sky.androidnotes.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by tonycheng on 2017/10/27.
 */

public class Android {

    /**
     * judge the service is running or not.
     *
     * @param context   {@link Context}
     * @param className the service class name
     * @return if true the service is running else the service is not existed.
     */
    public static boolean isServiceRunning(@NonNull Context context, @NonNull String className) {
        if (TextUtils.isEmpty(className)) {
            throw new IllegalArgumentException("the class name can not be null or empty.");
        }
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);
        if (serviceList == null) {
            return false;
        }
        if (serviceList.isEmpty()) {
            return false;
        }
        for (int i = 0, count = serviceList.size(); i < count; i++) {
            if (className.equals(serviceList.get(i).service.getClassName())) {
                Log.d("MyService", "isServiceRunning: " + serviceList.get(i).service.getClassName());
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
