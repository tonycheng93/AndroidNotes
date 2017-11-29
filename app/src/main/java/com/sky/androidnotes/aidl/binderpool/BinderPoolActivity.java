package com.sky.androidnotes.aidl.binderpool;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sky.androidnotes.BuildConfig;
import com.sky.androidnotes.R;
import com.sky.androidnotes.aidl.binderpool.impl.ComputeImpl;
import com.sky.androidnotes.aidl.binderpool.impl.SecurityCenterImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * @author tonycheng
 */
public class BinderPoolActivity extends AppCompatActivity {

    private static final String TAG = "BinderPoolActivity";

    private ISecurityCenter mSecurityCenter;
    private ICompute mCompute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_pool);

        getSingleThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        });

        String[] arr = new String[]{"A", "B", "C"};

        final PublishSubject<String> stringPublishSubject = PublishSubject.create();
        stringPublishSubject.doOnNext(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                if ("B".equals(s)) {
                    throw new RuntimeException("for test");
                }
            }
        }).onExceptionResumeNext(stringPublishSubject)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        Log.d(TAG, "onNext: s = " + s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        Observable.fromArray(arr)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        stringPublishSubject.onNext(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        stringPublishSubject.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        Log.d(TAG, "onCreate: debug = " + BuildConfig.DEBUG);
    }

    private void doWork() {
        BinderPool binderPool = BinderPool.getInstance(BinderPoolActivity.this);
        IBinder securityCenterBinder = binderPool.queryBinder(BinderPool.BINDER_SECURITY_CENTER);
        mSecurityCenter = (ISecurityCenter) SecurityCenterImpl.asInterface(securityCenterBinder);
        Log.d(TAG, "doWork: visit ISecurityCenter");
        String message = "Hello World";
        Log.d(TAG, "doWork: content = " + message);
        try {
            final String encryptPwd = mSecurityCenter.encrypt(message);
            Log.d(TAG, "doWork: encrypt password = " + encryptPwd);
            final String decryptPwd = mSecurityCenter.decrypt(encryptPwd);
            Log.d(TAG, "doWork: decrypt password = " + decryptPwd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "doWork: visit ICompute");
        IBinder computeBinder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
        mCompute = ComputeImpl.asInterface(computeBinder);
        try {
            final int result = mCompute.add(3, 5);
            Log.d(TAG, "doWork: add result = " + result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private ExecutorService getSingleThreadPool() {
        ExecutorService singleThreadPool = null;
        singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024),
                new NamedThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        return singleThreadPool;
    }

    private static final class NamedThreadFactory implements ThreadFactory {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@android.support.annotation.NonNull Runnable r) {
            return new Thread(r, "new thread #" + mCount.getAndIncrement());
        }
    }
}
