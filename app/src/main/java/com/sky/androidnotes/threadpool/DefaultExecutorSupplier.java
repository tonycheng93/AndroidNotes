package com.sky.androidnotes.threadpool;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tonycheng on 2017/6/20.
 */


/**
 * Singleton class for default executor supplier
 */
public class DefaultExecutorSupplier {

    /**
     * Number of cores to decide the number of threads
     */
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    /**
     * thread pool executor for background tasks
     */
    private final ThreadPoolExecutor mForBackgroundTasks;

    /**
     * thread pool executor for light weight background tasks
     */
    private final ThreadPoolExecutor mForLightWeightBackgroundTasks;

    /**
     * thread pool for main thread tasks
     */
    private final Executor mMainThreadExecutor;

    /**
     * return the instance of DefaultExecutorSupplier
     */
    public static DefaultExecutorSupplier getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * constructor for  DefaultExecutorSupplier
     */
    private DefaultExecutorSupplier() {
        //setting the thread factory
        ThreadFactory backgroundPriorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_BACKGROUND);

        //setting the thread pool executor for mBackgroundTasks
        mForBackgroundTasks = new ThreadPoolExecutor(
                2,
                2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                backgroundPriorityThreadFactory
        );

        //setting the thread pool executor for mForLightWeightBackgroundTasks
        mForLightWeightBackgroundTasks = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                backgroundPriorityThreadFactory
        );

        //setting the thread pool executor for mMainThreadExecutor
        mMainThreadExecutor = new MainThreadExecutor();
    }

    private static class SingletonHolder {
        private static final DefaultExecutorSupplier INSTANCE = new DefaultExecutorSupplier();
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
     * remove a {@link Runnable} task from the ThreadPoolExecutor
     *
     * Notice:this method does not check the task{@link Runnable} whether it is null,
     * otherwise it properly occur {@link NullPointerException},so you must check it.
     *
     * @param task {@link Runnable}
     * @return true remove task successfully;false remove task failed.
     */
    public boolean removeBackgroundTask(@NonNull Runnable task) {
        return mForBackgroundTasks.remove(task);
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
     * remove a {@link Runnable} task from the ThreadPoolExecutor
     *
     * Notice:this method does not check the task{@link Runnable} whether it is null,
     * otherwise it properly occur {@link NullPointerException},so you must check it.
     *
     * @param task {@link Runnable}
     * @return true remove task successfully;false remove task failed.
     */
    public boolean removeLightWeightBackgroundTask(@NonNull Runnable task) {
        return mForLightWeightBackgroundTasks.remove(task);
    }

    /**
     * return the thread pool executor for main thread task
     */
    public Executor getMainThreadExecutor() {
        return mMainThreadExecutor;
    }
}
