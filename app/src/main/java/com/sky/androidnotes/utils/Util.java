package com.sky.androidnotes.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Created by yellowlgx on 2015/7/21.
 */
public class Util
{
    private static Util mUtil;

    private static DisplayMetrics dm;

    private static float mDiv = 1.0f;
    private static float mDpi = 1.0f;

    public static Util instence(Context context)
    {
        if (mUtil == null)
            mUtil = new Util();
        if (dm == null)
            dm = context.getResources().getDisplayMetrics();
        mDiv = (float) dm.widthPixels / 1920.0f;
        mDpi = mDiv / dm.density;
        return mUtil;
    }

    public static Util instance(){
        return mUtil;
    }

    public int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public DisplayMetrics getDm()
    {
        return dm;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue)
    {
        final float scale = dm.density;
        return (int) (dpValue * scale + 0.5f);//最后的0.5f只是为了四舍五入
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue)
    {
        final float scale = dm.density;
        return (int) (pxValue / scale + 0.5f);//最后的0.5f只是为了四舍五入
    }

    public static int Div(int x) {
        return (int) (x * mDiv + 0.5f);
    }

    public static int Dpi(int x) {
        return (int) (x * mDpi + 0.5f);
    }

    /**
     * findViewById方法，取消强制转换步骤
     */
    public <T extends View> T $(Context context, int id)
    {
        return (T) ((Activity) context).findViewById(id);
    }

    /**
     * findViewById方法，取消强制转换步骤
     */
    public <T extends View> T $(View view, int id)
    {
        return (T) view.findViewById(id);
    }

    /**
     * 根据view的宽，获取缩放比例，暂时只用于6.0主页
     */
    public static float getScaleRatio(int w){
        return w <= Div(500)?1.08F:(float)((double)(w + Div(40)) * 1.0D / (double)w);
    }

//    public static void focusAnimate(View view, boolean hasFocus){
//        if(view != null){
//            float scale;
//            if(hasFocus){
//                if(view.animate() != null)
//                    view.animate().cancel();
//                scale = getScaleRatio(Div(view.getWidth()));
//                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1){
//                    AnimatorSet set = new AnimatorSet();
//                    ObjectAnimator a1 = ObjectAnimator.ofFloat(view,"scaleX",1,scale);
//                    ObjectAnimator a2 = ObjectAnimator.ofFloat(view,"scaleY",1,scale);
//                    set.setDuration(350);
//                    set.setInterpolator(new OvershootInterpolator());
//                    set.playTogether(a1,a2);
//                    set.start();
//                    a1.addUpdateListener(new UiCompat.CompatAnimatorUpdateListener());
//                }else{
//                    view.animate().scaleX(scale).scaleY(scale).setDuration(350L).setInterpolator(new OvershootInterpolator());
//                }
//            }else {
//                scale = 1.0F;
//                if(view.animate() != null)
//                    view.animate().cancel();
//                view.animate().scaleX(scale).scaleY(scale).setDuration(300L).setInterpolator(new OvershootInterpolator());
//            }
//        }
//    }

    public static float getDiv()
    {
        return mDiv;
    }

    public static float getDpi()
    {
        return mDpi;
    }
}
