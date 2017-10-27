package com.sky.androidnotes.threadpool;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 * Created by tonycheng on 2017/6/20.
 */

/**
 * 支持设置优先级
 */
public class PriorityThreadFactory implements ThreadFactory {

    private final int mThreadPriority;

    public PriorityThreadFactory(int threadPriority) {
        mThreadPriority = threadPriority;
    }

    @Override
    public Thread newThread(@NonNull final Runnable runnable) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(mThreadPriority);
                } catch (IllegalArgumentException | SecurityException e) {
                    e.printStackTrace();
                }
                runnable.run();
            }
        };
        return new Thread(wrapperRunnable);
    }
}
