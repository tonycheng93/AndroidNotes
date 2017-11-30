package com.sky.androidnotes.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;
import com.sky.androidnotes.R;
import com.sky.androidnotes.https.TlsOnlySocketFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by tonycheng on 2017/11/29.
 */

public class ImageLoader {

    private static final String TAG = "ImageLoader";

    private Context mContext;
    private LruCache<String, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;
    private boolean mIsDiskLruCacheCreated = false;
    private ImageResizer mImageResizer;
    private UIHandler mUIHandler;
    /**
     * Maximum disk cache size
     */
    private static final int DISK_CACHE_SIZE = 50;

    private static final int DISK_CACHE_INDEX = 0;

    private static final int TAG_KEY_URL = R.string.tag_key_url;

    private static final int IO_BUFFER_SIZE = 1024 * 8;

    private static final int MESSAGE_POST_RESULT = 0x10;

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

        mImageResizer = new ImageResizer();
        mUIHandler = new UIHandler();
    }

    /**
     * Build a new instance of ImageLoader
     *
     * @param context {@link Context}
     * @return return a new instance of ImageLoader
     */
    public static ImageLoader build(Context context) {
        return new ImageLoader(context);
    }

    public void bindBitmap(final String url, final ImageView imageView) {
        bindBitmap(url, imageView, 0, 0);
    }

    public void bindBitmap(final String url, final ImageView imageView, final int reqWidth, final int reqHeight) {
        imageView.setTag(TAG_KEY_URL, url);
        final Bitmap bitmap = getBitmapFromMemoryCache(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }

        Runnable loadBitmapTask = new Runnable() {
            @Override
            public void run() {
                final Bitmap loadBitmap = loadBitmap(url, reqWidth, reqHeight);
                if (loadBitmap != null) {
                    LoaderResult result = new LoaderResult(imageView, url, loadBitmap);
                    mUIHandler.obtainMessage(MESSAGE_POST_RESULT, result)
                            .sendToTarget();
                }
            }
        };

        THREAD_POOL_EXECUTOR.execute(loadBitmapTask);
    }

    private Bitmap loadBitmap(String url, int reqWidth, int reqHeight) {
        Bitmap bitmap = loadBitmapFromMemoryCache(url);
        if (bitmap != null) {
            Log.d(TAG, "load bitmap from memory cache: " + url);
            return bitmap;
        }

        bitmap = loadBitmapFromDiskCache(url, reqWidth, reqHeight);
        if (bitmap != null) {
            Log.d(TAG, "load bitmap from disk cache: " + url);
            return bitmap;
        }

        try {
            bitmap = loadBitmapFromHttp(url, reqWidth, reqHeight);
            Log.d(TAG, "load bitmap from network: " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bitmap == null && !mIsDiskLruCacheCreated) {
            Log.d(TAG, "encounter error,DiskLruCache is not created.");
            bitmap = downloadBitmapFromUrl(url);
        }
        return bitmap;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public Bitmap loadBitmapFromMemoryCache(String url) {
        final String key = hashKeyFromUrl(url);
        Bitmap bitmap = getBitmapFromMemoryCache(key);
        return bitmap;
    }

    private Bitmap loadBitmapFromDiskCache(String url, int reqWidth, int reqHeight) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w(TAG, "load bitmap from UI thread is not recommend !");
        }
        if (mDiskLruCache == null) {
            return null;
        }

        Bitmap bitmap = null;
        String key = hashKeyFromUrl(url);
        Log.d(TAG, "loadBitmapFromDiskCache: key = " + key);
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot != null) {
                FileInputStream fileInputStream = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                FileDescriptor fileDescriptor = fileInputStream.getFD();
                bitmap = mImageResizer.decodeSampledBitmapFromFileDescriptor(fileDescriptor, reqWidth, reqHeight);
                if (bitmap != null) {
                    addBitmapToMemoryCache(key, bitmap);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap loadBitmapFromHttp(String url, int reqWidth, int reqHeight) throws IOException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("can not visit network from UI thread.");
        }
        if (mDiskLruCache == null) {
            return null;
        }

        String key = hashKeyFromUrl(url);
        Log.d(TAG, "loadBitmapFromHttp: key = " + key);
        DiskLruCache.Editor editor = mDiskLruCache.edit(key);
        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
            if (downloadUrlToStream(url, outputStream)) {
                editor.commit();
            } else {
                editor.abort();
            }
        }
        mDiskLruCache.flush();
        return loadBitmapFromDiskCache(url, reqWidth, reqHeight);
    }

    private boolean downloadUrlToStream(String imageUrl, OutputStream outputStream) {
//        HttpsURLConnection connection = null;
        HttpURLConnection connection = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();

            SSLSocketFactory noSSLv3Factory = null;
            X509TrustManager x509TrustManager = null;

            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
                x509TrustManager = (X509TrustManager) trustManagers[0];
            } catch (NoSuchAlgorithmException | KeyStoreException e) {
                Log.e(TAG, "ImageLoader: ", e);
                e.printStackTrace();
            }

            try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1");
                sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
                noSSLv3Factory = new TlsOnlySocketFactory(sslContext.getSocketFactory());
                HttpsURLConnection.setDefaultSSLSocketFactory(noSSLv3Factory);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                Log.e(TAG, "HttpsClient: ", e);
                e.printStackTrace();
            }

///            connection.setSSLSocketFactory(noSSLv3Factory);

            in = new BufferedInputStream(connection.getInputStream(), IO_BUFFER_SIZE);
            out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadUrlToStream: MalformedURLException ", e);
        } catch (IOException e) {
            Log.e(TAG, "downloadUrlToStream: IOException ", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return false;
    }

    private Bitmap downloadBitmapFromUrl(String imageUrl) {
        Bitmap bitmap = null;
        ///HttpsURLConnection connection = null;
        HttpURLConnection connection = null;
        BufferedInputStream in = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();

            SSLSocketFactory noSSLv3Factory = null;
            X509TrustManager x509TrustManager = null;

            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
                x509TrustManager = (X509TrustManager) trustManagers[0];
            } catch (NoSuchAlgorithmException | KeyStoreException e) {
                Log.e(TAG, "ImageLoader: ", e);
                e.printStackTrace();
            }

            try {
                SSLContext sslContext = SSLContext.getInstance("TLSv1");
                sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
                noSSLv3Factory = new TlsOnlySocketFactory(sslContext.getSocketFactory());
                HttpsURLConnection.setDefaultSSLSocketFactory(noSSLv3Factory);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                Log.e(TAG, "HttpsClient: ", e);
                e.printStackTrace();
            }

            ///connection.setSSLSocketFactory(noSSLv3Factory);

            in = new BufferedInputStream(connection.getInputStream(), IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadBitmapFromUrl: error MalformedURLException", e);
        } catch (IOException e) {
            Log.e(TAG, "downloadBitmapFromUrl: error IOException", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }

    private String hashKeyFromUrl(String url) {
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

    private String byteToHexString(byte[] bytes) {
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

    private File getDiskCacheDir(Context context, String fileName) {
        File diskCacheDir = null;
        final String externalStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
            diskCacheDir = new File(context.getExternalCacheDir().getPath() + File.separator + fileName);
        } else {
            diskCacheDir = new File(context.getCacheDir().getPath() + File.separator + fileName);
        }
        return diskCacheDir;
    }

    private int getUsableSpace(@NonNull File file) {
        final long usableSpace = file.getUsableSpace();
        return (int) (usableSpace / 1024 / 1024);
    }


   /* -----------------------ThreadPool------------------------------------*/

    private static final class NamedThreadFactory implements ThreadFactory {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "new thread #" + mCount.getAndIncrement());
        }
    }

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**
     * We want at least 2 threads and at most 4 threads in the core pool,
     * preferring to have 1 less than the CPU count to avoid saturating
     * the CPU with background work
     **/
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final Executor THREAD_POOL_EXECUTOR;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(128), new NamedThreadFactory());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }


   /* --------------------------------Handler---------------------------------*/

    private static class UIHandler extends Handler {

        public UIHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            LoaderResult result = (LoaderResult) msg.obj;
            final ImageView imageView = result.mImageView;
            imageView.setImageBitmap(result.mBitmap);
            String url = (String) imageView.getTag(TAG_KEY_URL);
            if (url.equals(result.mUrl)) {
                imageView.setImageBitmap(result.mBitmap);
            } else {
                Log.w(TAG, "handleMessage: set image bitmap,but url has changed,ignored!");
            }
        }
    }
}
