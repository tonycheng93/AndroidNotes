package com.sky.androidnotes.imageloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jakewharton.disklrucache.DiskLruCache;
import com.sky.androidnotes.R;

public class ImageLoaderActivity extends AppCompatActivity {

    private Button mBtnLoadImg;
    private ImageView mImageView;

    private DiskLruCache mDiskLruCache;

//    private static final String URL = "https://cdn.dribbble.com/users/1313925/screenshots/3982240/one.png";

//    private static final String URL = "http://img.gank.io/c2797609-468f-45bd-b7dc-8296d10a1423";

    private static final String URL = "http://img.my.csdn.net/uploads/201309/01/1378037235_7476.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader);

        mBtnLoadImg = (Button) findViewById(R.id.btn_download);
        mImageView = (ImageView) findViewById(R.id.iv_img);

        final ImageLoader imageLoader = ImageLoader.build(this);

        mBtnLoadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLoader.bindBitmap(URL, mImageView, 100, 100);
            }
        });
    }

}
