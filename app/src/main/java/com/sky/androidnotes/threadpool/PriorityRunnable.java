package com.sky.androidnotes.threadpool;

/**
 * Created by tonycheng on 2017/6/20.
 */

public class PriorityRunnable implements Runnable {

    private final Priority mPriority;

    public PriorityRunnable(Priority priority) {
        mPriority = priority;
    }

    @Override
    public void run() {
        //do nothing here.
    }

    public Priority getPriority() {
        return mPriority;
    }
}
