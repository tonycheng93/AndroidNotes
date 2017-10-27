package com.sky.androidnotes.threadpool;

/**
 * Created by tonycheng on 2017/7/13.
 */

import android.os.Process;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Singleton class for default executor supplier
 */
public class PriorityExecutorSupplier {

    /**
     * Number of cores to decide the number of threads
     */
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * thread pool executor for background tasks
     */
    private final PriorityThreadPoolExecutor mForBackgroundTasks;

    /**
     * thread pool executor for light weight background tasks
     */
    private final PriorityThreadPoolExecutor mForLightWeightBackgroundTasks;

    /**
     * thread pool for main thread tasks
     */
    private final Executor mMainThreadExecutor;

    /**
     * return the instance of DefaultExecutorSupplier
     */
    public static PriorityExecutorSupplier getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * constructor for  DefaultExecutorSupplier
     */
    private PriorityExecutorSupplier() {
        //setting the thread factory
        ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);

        //setting the thread pool executor for mBackgroundTasks
        mForBackgroundTasks = new PriorityThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                backgroundPriorityThreadFactory
        );

        //setting the thread pool executor for mForLightWeightBackgroundTasks
        mForLightWeightBackgroundTasks = new PriorityThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                backgroundPriorityThreadFactory
        );

        //setting the thread pool executor for mMainThreadExecutor
        mMainThreadExecutor = new MainThreadExecutor();
    }

    private static class SingletonHolder {
        private static final PriorityExecutorSupplier INSTANCE = new PriorityExecutorSupplier();
    }

    /**
     * return the thread pool executor for background task
     *
     * @return mForBackgroundTasks
     */
    public ThreadPoolExecutor getForBackgroundTasks() {
        return mForBackgroundTasks;
    }

    /**
     * return the thread pool executor for light weight background task
     *
     * @return mForLightWeightBackgroundTasks
     */
    public ThreadPoolExecutor getForLightWeightBackgroundTasks() {
        return mForLightWeightBackgroundTasks;
    }

    /**
     * return the thread pool executor for main thread task
     */
    public Executor getMainThreadExecutor() {
        return mMainThreadExecutor;
    }
}
