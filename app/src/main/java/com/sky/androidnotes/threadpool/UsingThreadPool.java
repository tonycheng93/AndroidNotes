package com.sky.androidnotes.threadpool;

import java.util.concurrent.Future;

/**
 * Created by tonycheng on 2017/6/20.
 */

public class UsingThreadPool {

    public void doSomeBackgroundWork() {
        DefaultExecutorSupplier.getInstance()
                .getForBackgroundTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        //do some background work here.
                    }
                });
    }

    public void doSomeLightWeightBackgroundWork() {
        DefaultExecutorSupplier.getInstance()
                .getForLightWeightBackgroundTasks()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        //do some light-weight background work here.
                    }
                });
    }

    public void doSomeMainThreadWork() {
        DefaultExecutorSupplier.getInstance()
                .getMainThreadExecutor()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        //do dome main thread work here.
                    }
                });
    }

    public void doSomePriorityBackgroundWork() {
        PriorityExecutorSupplier.getInstance()
                .getForBackgroundTasks()
                .submit(new PriorityRunnable(Priority.HIGH) {
                    @Override
                    public void run() {
                        // do some background work here at high priority.
                    }
                });
    }

    /**
     * 要取消一个任务，你需要得到那个任务的future。所以你需要用submit方法而不是execute，
     * 它可以返回一 个future对象。你就可以通过这个future对象来取消那个任务。
     */
    private Future mFuture = null;

    public Future doBackgroundWork() {
        mFuture = DefaultExecutorSupplier.getInstance()
                .getForBackgroundTasks()
                .submit(new Runnable() {
                    @Override
                    public void run() {
                        //do some background work here.
                    }
                });
        return mFuture;
    }

    public void canceltask() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

}
