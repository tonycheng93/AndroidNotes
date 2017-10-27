package com.sky.androidnotes.timer;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

/**
 * Created by tonycheng on 2017/8/4.
 */

public class SimpleTimerTaskHandler extends Handler {

    private static final String TAG = "SimpleTimerTaskHandler";

    /**
     * 存放需要执行的任务
     */
    private SparseArray<SimpleTimerTask> mTasks = new SparseArray<>();

    private SimpleTimerTaskHandler() {
        super(Looper.getMainLooper());
    }

    private static class Singleton {
        private static final SimpleTimerTaskHandler INSTANCE = new SimpleTimerTaskHandler();
    }

    public static SimpleTimerTaskHandler getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public void handleMessage(Message msg) {
        int index = msg.what;
        Log.d(TAG, "handleMessage: the task is " + index);
        final SimpleTimerTask currentTask = mTasks.get(index);
        if (null == currentTask) {
            Log.d(TAG, "handleMessage: no task of " + index + ", please create it first !");
        } else {
            currentTask.run();
            if (currentTask.isLoop()) {
                long loopInterval = currentTask.getLoopInterval();
                sendEmptyMessageDelayed(index, loopInterval);
                Log.d(TAG, "handleMessage: the task of " + index + " is a loop task , and it will be executed after " + loopInterval + " milli");
            } else {
                mTasks.remove(index);
            }
        }
    }

    /**
     * 即时发送任务
     *
     * @param taskNum 任务编号
     * @param task    任务
     */
    public void sendTask(int taskNum, SimpleTimerTask task) {
        int index = saveTask(taskNum, task);
        sendEmptyMessage(index);
        Log.d(TAG, "sendTask: 当前任务类型为即时任务，取绝对值后的任务编号为 " + index);
    }

    /**
     * 延时发送任务
     *
     * @param taskNum     任务编号
     * @param task        任务
     * @param delayMillis 延时时间，单位：毫秒
     */
    public void sendTaskDelayed(int taskNum, SimpleTimerTask task, long delayMillis) {
        int index = saveTask(taskNum, task);
        sendEmptyMessageDelayed(index, delayMillis);
        Log.d(TAG, "sendTaskDelayed: 当前任务类型为延时任务，取绝对值后的任务编号为 " + index);
    }

    /**
     * 保存要执行的任务
     *
     * @param taskNum 任务编号
     * @param task    任务
     * @return 任务编号
     */
    private int saveTask(int taskNum, SimpleTimerTask task) {
        Log.d(TAG, "saveTask: 保存要执行的任务编号 " + taskNum);
        int index = Math.abs(taskNum);
        mTasks.put(index, task);
        return index;
    }

    /**
     * 定时发送任务，精确到小时
     *
     * @param taskNum 任务编号
     * @param task    任务
     * @param hour    指定小时
     */
    public void sendTimerTask(int taskNum, SimpleTimerTask task, int hour) {
        int index = saveTask(taskNum, task);
        sendEmptyMessageDelayed(index, TimeUtils.getFirstSendTaskDelayedTime(hour));
        Log.d(TAG, "sendTimerTask: 当前任务类型为定时任务（精确到小时），取绝对值后的任务编号为" + index + "，hour = " + hour);
    }

    /**
     * 定时发送任务，精确到分
     *
     * @param taskNum 任务编号
     * @param task    任务
     * @param hour    指定小时
     * @param minute  指定分钟
     */
    public void sendTimerTask(int taskNum, SimpleTimerTask task, int hour, int minute) {
        int index = saveTask(taskNum, task);
        sendEmptyMessageDelayed(index, TimeUtils.getFirstSendTaskDelayedTime(hour, minute));
        Log.d(TAG, "sendTimerTask: 当前任务类型为定时任务（精确到分），取绝对值后的任务编号为" + index + "，hour = " + hour + "，minute = " + minute);
    }

    /**
     * 定时发送任务，精确到秒
     *
     * @param taskNum 任务编号
     * @param task    任务
     * @param hour    指定小时
     * @param minute  指定分钟
     * @param second  指定秒数
     */
    public void sendTimerTask(int taskNum, SimpleTimerTask task, int hour, int minute, int second) {
        int index = saveTask(taskNum, task);
        sendEmptyMessageDelayed(index, TimeUtils.getFirstSendTaskDelayedTime(hour, minute, second));
        Log.d(TAG, "sendTimerTask: 当前任务类型为定时任务（精确到秒），取绝对值后的任务编号为" + index + "，hour = " + hour + "，minute = " + minute + "，second = " + second);
    }

    public void removeTask(int taskNum, SimpleTimerTask task) {
        mTasks.remove(taskNum);
        removeCallbacks(task);
    }
}
