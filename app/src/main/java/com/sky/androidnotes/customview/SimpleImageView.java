package com.sky.androidnotes.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sky.androidnotes.R;
import com.sky.androidnotes.utils.BitmapUtils;

/**
 * Created by tonycheng on 2017/10/20.
 */

public class SimpleImageView extends View {

    private Paint mBitmapPaint = null;
    private Drawable mDrawable = null;
    private int mWidth;
    private int mHeight;


    public SimpleImageView(Context context) {
        this(context, null);
    }

    public SimpleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
    }

    /**
     * initialize attrs
     *
     * @param context {@link Context}
     * @param attrs   {@link AttributeSet}
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.SimpleImageView);
            mDrawable = a.getDrawable(R.styleable.SimpleImageView_src);
            measureDrawable(mDrawable);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }

    }

    /**
     * measure drawable width and height
     *
     * @param drawable {@link Drawable}
     */
    private void measureDrawable(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalStateException("you must set src attr for SimpleImageView");
        }
        mWidth = drawable.getIntrinsicWidth();
        mHeight = drawable.getIntrinsicHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawable == null){
            return;
        }
        canvas.drawBitmap(BitmapUtils.toBitmap(mDrawable),getLeft(),getTop(),mBitmapPaint);
    }
}
