package com.sky.androidnotes.aidl.book;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by tonycheng on 2017/11/23.
 */

public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";

    /**
     * CopyOnWriteArrayList 支持并发读写
     */
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private RemoteCallbackList<IOnNewBookArrivedListener> mListeners = new RemoteCallbackList<>();

    private static final String PERMISSION = "com.sky.androidnotes.aidl.book.ACCESS_BOOK_MANAGER_SERVICE";

    private static final String PACKAGE_NAME_PREFIX = "com.sky.androidnotes";

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.register(listener);
            Log.d(TAG, "registerListener: listener size = " + mListeners.getRegisteredCallbackCount());
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListeners.unregister(listener);
            Log.d(TAG, "unRegisterListener: listener size = " + mListeners.getRegisteredCallbackCount());
        }

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            final int permission = checkCallingOrSelfPermission(PERMISSION);
            if (permission == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            String packageName = "";
            final String[] packagesForUid = getPackageManager().getPackagesForUid(getCallingUid());
            if (packagesForUid != null && packagesForUid.length > 0) {
                packageName = packagesForUid[0];
            }
            if (!packageName.startsWith(PACKAGE_NAME_PREFIX)) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    private class ServiceRunnable implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookNo = mBookList.size() + 1;
                Book book = new Book(bookNo, "new Book#" + bookNo);
                try {
                    onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(0, "Android"));
        mBookList.add(new Book(1, "iOS"));
        Log.d(TAG, "onCreate: BookManagerService thread name = " + Thread.currentThread().getName());

//        while (!mIsServiceDestroyed.get()) {
//            getSingleThreadPool().execute(new ServiceRunnable());
//        }
    }

    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        Log.d(TAG, "onNewBookArrived: book list = " + mBookList.size());
        for (int i = 0, length = mListeners.beginBroadcast(); i < length; i++) {
            final IOnNewBookArrivedListener listener = mListeners.getBroadcastItem(i);
            Log.d(TAG, "onNewBookArrived: notify listener = " + listener);
            if (listener != null) {
                listener.onNewBookArrived(book);
            }
            mListeners.finishBroadcast();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        final int permission = checkCallingOrSelfPermission(PERMISSION);
        Log.d(TAG, "onBind: permission = " + permission);
        if (permission == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "onServiceConnected: sorry,you can not connect remote service," +
                    "please ensure you have the right permission.");
            return null;
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    private ExecutorService getSingleThreadPool() {
        ThreadFactory namedThreadFactory = Executors.defaultThreadFactory();
        ExecutorService singleThreadPool = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        return singleThreadPool;
    }
}
