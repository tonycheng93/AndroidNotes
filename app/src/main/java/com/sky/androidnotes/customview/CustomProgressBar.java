package com.sky.androidnotes.customview;

import android.content.Context;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sky.androidnotes.R;
import com.sky.androidnotes.utils.Util;

/**
 * @author tonycheng on 2017/11/8.
 */

/**
 * 思路分析：一张 .9 图片当作进度条，不断地改变它的宽度来实现进度的变化，
 * 另外一张 .9 图片当作背景
 */

public class CustomProgressBar extends FrameLayout {

    private static final String TAG = "CustomProgressBar";
    /**
     * 进度条
     */
    private ImageView mBarImage = null;
    /**
     * ProgressBar背景，显示进度条的轮廓
     */
    private FrameLayout mBarBgImage = null;

    private int mMin = 0;
    private int mMax = 100;
    private int mProgress = 0;
    private FrameLayout.LayoutParams mLayoutParams = null;
    private int mFirstWidth = 15;
    private int mBarBgWidth = 0;
    private int mBarBgHeight = 0;
    private int mBarWidth = 0;
    private int mBarHeight = 0;

    private int mBarResId = 0;
    private int mBarBgResId = 0;


    public CustomProgressBar(@NonNull Context context) {
        super(context);

        initView(context);
    }

    private void initView(Context context) {
        mBarBgImage = new FrameLayout(context);
        mBarBgImage.setBackgroundResource(getBarBgResId());
        LayoutParams params = new LayoutParams(getBarBgWidth(), getBarBgHeight());
        params.gravity = Gravity.CENTER_VERTICAL;
        addView(mBarBgImage, params);

        mBarImage = new ImageView(context);
        mBarImage.setBackgroundResource(getBarResId());

        mLayoutParams = new LayoutParams(getBarWidth(), getBarResId());
        mLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        mBarBgImage.addView(mBarImage, mLayoutParams);
    }

    /**
     * 暴露给外面调用来实时更新进度
     *
     * @param progress int
     */
    public void setProgress(int progress) {
        if (progress >= mMin && progress <= mMax) {
            mProgress = progress;
            refreshProgress();
        }
    }

    /**
     * 更新进度，其实本质就是不断改变.9图片的宽度来更新进度显示的
     */
    private void refreshProgress() {
        if (mProgress == 0) {
            mLayoutParams.width = 0;
        } else if (mProgress == 1) {
            mLayoutParams.width = mFirstWidth;
        } else if (mProgress > 1) {
            float percent = (float) (mProgress - mMin - 1) / (float) (mMax - 1 - mMin);
            mLayoutParams.width = mFirstWidth + (int)(percent * (getBarWidth() - mFirstWidth));
        }
        mBarBgImage.updateViewLayout(mBarImage, mLayoutParams);
    }

    public int getBarBgWidth() {
        return mBarBgWidth;
    }

    public void setBarBgWidth(int barBgWidth) {
        mBarBgWidth = barBgWidth;
    }

    public int getBarBgHeight() {
        return mBarBgHeight;
    }

    public void setBarBgHeight(int barBgHeight) {
        mBarBgHeight = barBgHeight;
    }

    public int getBarWidth() {
        return mBarWidth;
    }

    public void setBarWidth(int barWidth) {
        mBarWidth = barWidth;
    }

    public int getBarHeight() {
        return mBarHeight;
    }

    public void setBarHeight(int barHeight) {
        mBarHeight = barHeight;
    }

    public int getBarResId() {
        return mBarResId;
    }

    public void setBarResId(int barResId) {
        mBarResId = barResId;
    }

    public int getBarBgResId() {
        return mBarBgResId;
    }

    public void setBarBgResId(int barBgResId) {
        mBarBgResId = barBgResId;
    }
}
