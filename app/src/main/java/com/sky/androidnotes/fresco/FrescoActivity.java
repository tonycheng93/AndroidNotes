package com.sky.androidnotes.fresco;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sky.androidnotes.R;
import com.sky.androidnotes.threadpool.DefaultExecutorSupplier;
import com.sky.androidnotes.utils.BitmapUtils;

import java.io.File;

public class FrescoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FrescoActivity";

    private Button mBtnLoadImg;
    private ImageView mSimpleDraweeView;

    private static final String iconUrl = "http://pic.qqtn.com/up/2015-12/2015122909594896195.gif";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresco);

        mBtnLoadImg = (Button) findViewById(R.id.btn_load_img);
        mBtnLoadImg.setOnClickListener(this);
        mSimpleDraweeView = (ImageView) findViewById(R.id.sdv_image);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnLoadImg) {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(iconUrl));
            final ImageRequest imageRequest = builder.build();
            final DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, FrescoActivity.this);
            DataSubscriber<CloseableReference<CloseableImage>> dataSubscriber = new BaseBitmapDataSubscriber() {
                @Override
                protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                    Log.d(TAG, "onNewResultImpl: bitmap = " + bitmap);
                    if (bitmap == null) {
                        final File fileFromDiskCache = FileUtils.getFileFromDiskCache(iconUrl);
                        Log.d(TAG, "onNewResultImpl: fileFromDiskCache = " + fileFromDiskCache);
                        if (fileFromDiskCache != null && fileFromDiskCache.exists()) {
                            final String fileType = FileUtils.getFileRealType(fileFromDiskCache.getAbsolutePath());
                            Log.d(TAG, "fileType: " + fileType);
//                            BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inJustDecodeBounds = true;
//                            BitmapFactory.decodeFile(fileFromDiskCache.getAbsolutePath(), options);
//                            options.inSampleSize = 2;
//                            options.inJustDecodeBounds = false;
                            final Bitmap bmp = BitmapFactory.decodeFile(fileFromDiskCache.getAbsolutePath());
                            Log.d(TAG, "onNewResultImpl: bmp = " + bmp);
                            final Bitmap roundBitmap = BitmapUtils.getRoundBitmap(bmp);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mSimpleDraweeView.setImageBitmap(roundBitmap);

                                }
                            });
                        }
                    }
                }

                @Override
                protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                }
            };
            dataSource.subscribe(dataSubscriber, DefaultExecutorSupplier.getInstance().getForLightWeightBackgroundTasks());
        }
    }
}
