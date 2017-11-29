package com.sky.androidnotes.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tonycheng on 2017/11/29.
 */

public class ImageLoader {

    private static final String TAG = "ImageLoader";

    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;
    private boolean mIsDiskLruCacheCreated = false;
    /**
     * Maximum disk cache size
     */
    private static final int DISK_CACHE_SIZE = 50;

    private static final int DISK_CACHE_INDEX = 0;

    public ImageLoader(Context context) {
        mContext = context.getApplicationContext();
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
        File diskCacheDir = getDiskCacheDir(mContext, "bitmap");
        if (!diskCacheDir.exists()) {
            final boolean mkdirs = diskCacheDir.mkdirs();
            Log.d(TAG, "make disk cache dir success = " + mkdirs);
        }

        if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                mIsDiskLruCacheCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network from UI thread.");
        }
        if (mDiskLruCache == null) {
            return null;
        }

        String key = hashKeyFromUrl(url);
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if (editor != null){
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);

        }
    }

    private String hashKeyFromUrl(String url) {
        return null;
    }

    private File getDiskCacheDir(Context context, String fileName) {
        File diskCacheDir = null;
        final String externalStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
            diskCacheDir = new File(context.getCacheDir() + fileName);
        }
        return diskCacheDir;
    }

    private int getUsableSpace(@NonNull File file) {
        final long usableSpace = file.getUsableSpace();
        return (int) (usableSpace / 1024 / 1024);
    }
}
