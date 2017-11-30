package com.sky.androidnotes.imageloader;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by tonycheng on 2017/11/30.
 */

public final class LoaderResult {
    public ImageView mImageView;
    public String mUrl;
    public Bitmap mBitmap;

    public LoaderResult(ImageView imageView, String url, Bitmap bitmap) {
        mImageView = imageView;
        mUrl = url;
        mBitmap = bitmap;
    }
}
