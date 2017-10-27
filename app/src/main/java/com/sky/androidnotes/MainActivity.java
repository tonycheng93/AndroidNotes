package com.sky.androidnotes;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sky.androidnotes.parcelable.Person;
import com.sky.androidnotes.thread.SnowThread;
import com.sky.androidnotes.threadpool.DefaultExecutorSupplier;
import com.sky.androidnotes.threadpool.Priority;
import com.sky.androidnotes.threadpool.PriorityRunnable;
import com.sky.androidnotes.utils.BitmapUtils;

import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "xxx";
    private final String url = "http://pic.qqtn.com/up/2015-12/2015122909594896195.gif";

    private Handler mHandler = new Handler();

    private Future mFuture = null;

    private int startIndex = 0;
    private int currentIndex = 0;

    private Button message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SnowThread thread = new SnowThread();
        thread.start();

//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                thread.stop();
//            }
//        }, 300);

        Button btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean drawing = thread.isDrawing();
                Log.d(TAG, "drawing = " + drawing);
                if (drawing) {
                    Log.d(TAG, "drawing is on process,no need to start another thread.");
                } else {
                    Log.d(TAG, "drawing is not on process,start a new thread.");
                    thread.start();
                }
            }
        });

        Button btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
            }
        });

        final Person person = new Person();
        person.name = "tony";
        person.age = 12;

        final SparseIntArray mDatas = null;
//        mDatas.get(startIndex);
        Button btnTest = (Button) findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIndex++;
                currentIndex = startIndex % mDatas.size();
                mDatas.get(currentIndex);
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex == 0) {
                    currentIndex = mDatas.size() - 1;
                } else {
                    currentIndex--;
                }
                mDatas.get(currentIndex);
            }
        });

        final Runnable backgroundTask = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.d(TAG, "run: thread name = " + Thread.currentThread().getName());
                }
            }
        };

        Button btnStartThread = (Button) findViewById(R.id.btn_start_thread);
        btnStartThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mFuture = DefaultExecutorSupplier.getInstance()
//                        .getForLightWeightBackgroundTasks()
//                        .submit(new Runnable() {
//                            @Override
//                            public void run() {
//                                while (true){
//                                    while (!Thread.currentThread().isInterrupted()){
                Log.d(TAG, "run: thread name = " + Thread.currentThread().getName());
//                                        SystemClock.sleep(3000);
//                                        boolean interrupted = false;
//                                        try {
//                                            Thread.sleep(3000);
//                                        } catch (InterruptedException e) {
//                                            interrupted = true;
//                                        }
//                                        if (interrupted){
//                                            Thread.currentThread().interrupt();
//                                        }
//                                    }
//                                }
//                            }
//                        });
                DefaultExecutorSupplier.getInstance()
                        .getForBackgroundTasks()
                        .execute(new PriorityRunnable(Priority.IMMEDIATE) {
                            @Override
                            public void run() {
                                int i = 0;
                                while (i < 10) {
                                    SystemClock.sleep(3000);
                                    Log.d(TAG, "run: Priority.IMMEDIATE");
                                    i++;
                                }
                            }
                        });
                DefaultExecutorSupplier.getInstance()
                        .getForBackgroundTasks()
                        .execute(new PriorityRunnable(Priority.MEDIUM) {
                            @Override
                            public void run() {
                                int i = 0;
                                while (i < 10) {
                                    SystemClock.sleep(3000);
                                    Log.d(TAG, "run: Priority.MEDIUM");
                                    i++;
                                }
                            }
                        });
                DefaultExecutorSupplier.getInstance()
                        .getForBackgroundTasks()
                        .execute(new PriorityRunnable(Priority.LOW) {
                            @Override
                            public void run() {
                                int i = 0;
                                while (i < 10) {
                                    SystemClock.sleep(3000);
                                    Log.d(TAG, "run: Priority.LOW");
                                    i++;
                                }
                            }
                        });
                DefaultExecutorSupplier.getInstance()
                        .getForBackgroundTasks()
                        .execute(new PriorityRunnable(Priority.HIGH) {
                            @Override
                            public void run() {
                                int i = 0;
                                while (i < 10) {
                                    SystemClock.sleep(3000);
                                    Log.d(TAG, "run: Priority.HIGH");
                                    i++;
                                }
                            }
                        });
            }
        });

        Button btnStopThread = (Button) findViewById(R.id.btn_stop_thread);
        btnStopThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFuture != null && !mFuture.isCancelled()) {
                    Log.d(TAG, "onClick: cancel thread.");
                    final boolean cancel = mFuture.cancel(true);
                    Log.d(TAG, "onClick: cancel = " + cancel);
                    mFuture = null;
                    DefaultExecutorSupplier.getInstance()
                            .removeBackgroundTask(backgroundTask);
                }
            }
        });

        final Button BtnHide = (Button) findViewById(R.id.btn_hide);
        BtnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int width = BtnHide.getWidth() / 2;
                int height = BtnHide.getHeight() / 2;
                float radius = BtnHide.getWidth();
                Animator animator = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    animator = ViewAnimationUtils.createCircularReveal(BtnHide, width, height, radius, 0);
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            BtnHide.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                } else {
                    BtnHide.setVisibility(View.INVISIBLE);
                }
            }
        });
        Fresco.initialize(this);
        Button loadImageView = (Button) findViewById(R.id.bnt_load_image);
        final ImageView imageView = (ImageView) findViewById(R.id.iv_image);
        final SimpleDraweeView draweeView = (SimpleDraweeView) findViewById(R.id.drawee_view);
        loadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(url))
                        .setAutoPlayAnimations(true)
                        .build();
                draweeView.setController(controller);

//                loadGif(new OnBitmapLoadedListener() {
//                    @Override
//                    public void onLoadSuccess(Bitmap bitmap) {
//                        Log.d(TAG, "onLoadSuccess: bitmap = " + bitmap);
//                        if (bitmap == null){
//                            return;
//                        }
//                        final Bitmap roundBitmap = BitmapUtils.getRoundBitmap(Bitmap.createBitmap(bitmap));
//                        imageView.setImageBitmap(roundBitmap);
//                    }
//
//                    @Override
//                    public void onLoadFailed() {
//
//                    }
//                });
            }
        });
    }

    interface OnBitmapLoadedListener {

        void onLoadSuccess(Bitmap bitmap);

        void onLoadFailed();
    }

    private void loadGif(final OnBitmapLoadedListener listener) {

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));
        builder.setResizeOptions(new ResizeOptions(100, 100));
        ImageRequest imageRequest = builder.build();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
        DataSubscriber dataSubscriber = new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(@Nullable Bitmap bitmap) {
                listener.onLoadSuccess(bitmap);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                listener.onLoadFailed();
            }
        };
        dataSource.subscribe(dataSubscriber, DefaultExecutorSupplier.getInstance().getMainThreadExecutor());
    }
}
