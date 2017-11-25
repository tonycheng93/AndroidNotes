package com.sky.androidnotes.https;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.sky.androidnotes.R;
import com.sky.androidnotes.utils.Util;

import org.reactivestreams.Publisher;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class HttpsActivity extends AppCompatActivity {

    private static final String TAG = "HttpsActivity";

    private static final String IMG_URL = "https://cdn.dribbble.com/users/24974/screenshots/3898939/tomato_sticker_pack_brown_1x.jpg";

    private static final String SERVICE_NAME = "com.sky.androidnotes.https.MyService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.instence(this);
        FrameLayout mainLayout = new FrameLayout(this);
        mainLayout.setBackgroundColor(Color.BLACK);
        setContentView(mainLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        FrameLayout focusView = new FrameLayout(this);
        focusView.setBackgroundResource(R.drawable.sky_tv_start_search_button);
        FrameLayout.LayoutParams focusParams = new FrameLayout.LayoutParams(Util.Div(320), Util.Div(135));
        focusParams.gravity = Gravity.CENTER;
        focusParams.topMargin = Util.Div(-4);
        focusParams.leftMargin = Util.Div(-30);
        mainLayout.addView(focusView, focusParams);

        Button button = new Button(this);
        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(Util.Div(260), Util.Div(76));
        btnParams.gravity = Gravity.CENTER;
        button.setText("去搜台");
        focusView.addView(button, btnParams);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Flowable<List<String>> listFlowable =
                        getTasks()
                                .flatMap(new Function<List<String>, Publisher<List<String>>>() {
                                    @Override
                                    public Publisher<List<String>> apply(@NonNull List<String> strings) throws Exception {
                                        return Flowable.fromIterable(strings)
                                                .doOnNext(new Consumer<String>() {
                                                    @Override
                                                    public void accept(String s) throws Exception {
                                                        Log.d(TAG, "accept: " + s);
                                                    }
                                                })
                                                .toList()
                                                .toFlowable();
                                    }
                                })
                                .doOnComplete(new Action() {
                                    @Override
                                    public void run() throws Exception {
                                        Log.d(TAG, "run: doOnComplete.");
                                    }
                                });
                final List<String> list = listFlowable.blockingFirst();
                Log.d(TAG, "onClick: list = " + list.toString());
            }
        });
//        setContentView(R.layout.activity_https);

//        final Button startService = (Button) findViewById(R.id.btn_start_service);
//        startService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HttpsActivity.this, MyService.class);
//                startService(intent);
//                if (Android.isServiceRunning(HttpsActivity.this, SERVICE_NAME)) {
//                    Log.d(TAG, "onClick: ");
//                }
//            }
//        });
//
//        final CustomProgressBar progressBar = (CustomProgressBar) findViewById(R.id.progress_bar);
//
//        Button button = (Button) findViewById(R.id.btn_load);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                for (int i = 0; i < 100; i++) {
//                    progressBar.setProgress(i);
//                }
////                HttpsClient.getInstance().load(IMG_URL, new ResponseCallback() {
////                    @Override
////                    public void onSuccess(Response response) {
////                        Log.d(TAG, "onSuccess: " + response.toString());
////                    }
////
////                    @Override
////                    public void onFail(IOException e) {
////                        Log.e(TAG, "onFail: ", e);
//////                        javax.net.ssl.SSLHandshakeException: javax.net.ssl.SSLProtocolException: SSL handshake aborted: ssl=0x52fb82e8: Failure in SSL library, usually a protocol error
//////                        error:1407742E:SSL routines:SSL23_GET_SERVER_HELLO:tlsv1 alert protocol version (external/openssl/ssl/s23_clnt.c:741 0x53124ce4:0x00000000)
//////                        at com.android.org.conscrypt.OpenSSLSocketImpl.startHandshake(OpenSSLSocketImpl.java:448)
//////                        at okhttp3.internal.connection.RealConnection.connectTls(RealConnection.java:281)
//////                        at okhttp3.internal.connection.RealConnection.establishProtocol(RealConnection.java:251)
//////                        at okhttp3.internal.connection.RealConnection.connect(RealConnection.java:151)
//////                        at okhttp3.internal.connection.StreamAllocation.findConnection(StreamAllocation.java:195)
//////                        at okhttp3.internal.connection.StreamAllocation.findHealthyConnection(StreamAllocation.java:121)
//////                        at okhttp3.internal.connection.StreamAllocation.newStream(StreamAllocation.java:100)
//////                        at okhttp3.internal.connection.ConnectInterceptor.intercept(ConnectInterceptor.java:42)
//////                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
//////                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:67)
//////                        at okhttp3.internal.cache.CacheInterceptor.intercept(CacheInterceptor.java:93)
//////                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
//////                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:67)
//////                        at okhttp3.internal.http.BridgeInterceptor.intercept(BridgeInterceptor.java:93)
//////                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
//////                        at okhttp3.internal.http.RetryAndFollowUpInterceptor.intercept(RetryAndFollowUpInterceptor.java:120)
//////                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
//////                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:67)
//////                        at okhttp3.RealCall.getResponseWithInterceptorChain(RealCall.java:185)
//////                        at okhttp3.RealCall$AsyncCall.execute(RealCall.java:135)
//////                        at okhttp3.internal.NamedRunnable.run(NamedRunnable.java:32)
//////                        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1112)
//////                        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:587)
//////                        at java.lang.Thread.run(Thread.java:841)
//////                        Caused by: javax.net.ssl.SSLProtocolException: SSL handshake aborted: ssl=0x52fb82e8: Failure in SSL library, usually a protocol error
//////                        error:1407742E:SSL routines:SSL23_GET_SERVER_HELLO:tlsv1 alert protocol version (external/openssl/ssl/s23_clnt.c:741 0x53124ce4:0x00000000)
//////                        at com.android.org.conscrypt.NativeCrypto.SSL_do_handshake(Native Method)
//////                        at com.android.org.conscrypt.OpenSSLSocketImpl.startHandshake(OpenSSLSocketImpl.java:405)
//////                                                                         	... 23 more
////                    }
////                });
//            }
//        });
    }

    private Flowable<List<String>> getTasks() {
        final List<String> list = new LinkedList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        return Flowable.create(new FlowableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<String>> e) throws Exception {
                e.onNext(list);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

}
