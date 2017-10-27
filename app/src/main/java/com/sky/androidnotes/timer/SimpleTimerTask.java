package com.sky.androidnotes.timer;

/**
 * Created by tonycheng on 2017/8/4.
 */

public abstract class SimpleTimerTask implements Runnable{

    /**
     * 判断是否为循环任务
     */
    private boolean isLoop;

    /**
     * 循环间隔
     * 单位：毫秒
     */
    private long mLoopInterval;

    /**
     * 普通任务
     * 默认循环标志 isLoop = false
     * 默认循环间隔 mLoopInterval = 0
     */
    public SimpleTimerTask() {

    }

    /**
     * 循环任务构造
     *
     * @param loopInterval 循环间隔
     */
    public SimpleTimerTask(long loopInterval) {
        isLoop = true;
        mLoopInterval = Math.abs(loopInterval);
    }

    /**
     * 将要执行的任务
     */
    public abstract void run();

    /**
     * @return 是否为循环任务的标志
     */
    public boolean isLoop() {
        return isLoop;
    }

    /**
     * @return 循环间隔
     */
    public long getLoopInterval() {
        return mLoopInterval;
    }
}
