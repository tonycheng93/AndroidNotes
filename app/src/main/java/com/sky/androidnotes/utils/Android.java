package com.sky.androidnotes.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    /**
     * 更加高效的读取进程名方法
     *
     * @return 进程名
     */
    public static String getProcessName() {
        String processName = "";
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            processName = reader.readLine().trim();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return processName;
        } catch (IOException e) {
            e.printStackTrace();
            return processName;
        }
        return processName;
    }

    public static String hashKeyFromUrl(String url) {
        String cacheKey;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            cacheKey = byteToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(url.hashCode());
        }
        return cacheKey;
    }

    private static String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = bytes.length; i < length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
