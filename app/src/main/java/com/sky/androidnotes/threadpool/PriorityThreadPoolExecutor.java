package com.sky.androidnotes.threadpool;

import android.support.annotation.NonNull;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by tonycheng on 2017/7/13.
 */

public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {
    public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                      TimeUnit unit, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityBlockingQueue<Runnable>(), threadFactory);
    }

    @NonNull
    @Override
    public Future<?> submit(@NonNull Runnable task) {
        PriorityFutureTask futureTask = new PriorityFutureTask((PriorityRunnable) task);
        execute(futureTask);
        return futureTask;
    }

    private static final class PriorityFutureTask extends FutureTask<PriorityRunnable>
            implements Comparable<PriorityFutureTask> {

        private final PriorityRunnable mPriorityRunnable;

        PriorityFutureTask(@NonNull PriorityRunnable priorityRunnable) {
            super(priorityRunnable, null);
            mPriorityRunnable = priorityRunnable;
        }

        /**
         * compareTo() method is defined in interface java.lang.Comparable and it is used
         * to implement natural sorting on java classes. natural sorting means the the sort
         * order which naturally applies on object e.g. lexical order for String, numeric
         * order for Integer or Sorting employee by there ID etc. most of the java core
         * classes including String and Integer implements CompareTo() method and provide
         * natural sorting.
         */
        @Override
        public int compareTo(@NonNull PriorityFutureTask other) {
            Priority priority1 = mPriorityRunnable.getPriority();
            Priority priority2 = other.mPriorityRunnable.getPriority();
            return priority2.ordinal() - priority1.ordinal();
        }
    }
}
