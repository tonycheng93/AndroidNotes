package com.sky.androidnotes.https;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sky.androidnotes.R;

import java.io.IOException;

import okhttp3.Response;

public class HttpsActivity extends AppCompatActivity {

    private static final String TAG = "HttpsActivity";

    private static final String IMG_URL = "https://cdn.dribbble.com/users/24974/screenshots/3898939/tomato_sticker_pack_brown_1x.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_https);

        Button button = (Button) findViewById(R.id.btn_load);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpsClient.getInstance().load(IMG_URL, new ResponseCallback() {
                    @Override
                    public void onSuccess(Response response) {
                        Log.d(TAG, "onSuccess: " + response.toString());
                    }

                    @Override
                    public void onFail(IOException e) {
                        Log.e(TAG, "onFail: ", e);
//                        javax.net.ssl.SSLHandshakeException: javax.net.ssl.SSLProtocolException: SSL handshake aborted: ssl=0x52fb82e8: Failure in SSL library, usually a protocol error
//                        error:1407742E:SSL routines:SSL23_GET_SERVER_HELLO:tlsv1 alert protocol version (external/openssl/ssl/s23_clnt.c:741 0x53124ce4:0x00000000)
//                        at com.android.org.conscrypt.OpenSSLSocketImpl.startHandshake(OpenSSLSocketImpl.java:448)
//                        at okhttp3.internal.connection.RealConnection.connectTls(RealConnection.java:281)
//                        at okhttp3.internal.connection.RealConnection.establishProtocol(RealConnection.java:251)
//                        at okhttp3.internal.connection.RealConnection.connect(RealConnection.java:151)
//                        at okhttp3.internal.connection.StreamAllocation.findConnection(StreamAllocation.java:195)
//                        at okhttp3.internal.connection.StreamAllocation.findHealthyConnection(StreamAllocation.java:121)
//                        at okhttp3.internal.connection.StreamAllocation.newStream(StreamAllocation.java:100)
//                        at okhttp3.internal.connection.ConnectInterceptor.intercept(ConnectInterceptor.java:42)
//                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
//                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:67)
//                        at okhttp3.internal.cache.CacheInterceptor.intercept(CacheInterceptor.java:93)
//                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
//                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:67)
//                        at okhttp3.internal.http.BridgeInterceptor.intercept(BridgeInterceptor.java:93)
//                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
//                        at okhttp3.internal.http.RetryAndFollowUpInterceptor.intercept(RetryAndFollowUpInterceptor.java:120)
//                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:92)
//                        at okhttp3.internal.http.RealInterceptorChain.proceed(RealInterceptorChain.java:67)
//                        at okhttp3.RealCall.getResponseWithInterceptorChain(RealCall.java:185)
//                        at okhttp3.RealCall$AsyncCall.execute(RealCall.java:135)
//                        at okhttp3.internal.NamedRunnable.run(NamedRunnable.java:32)
//                        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1112)
//                        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:587)
//                        at java.lang.Thread.run(Thread.java:841)
//                        Caused by: javax.net.ssl.SSLProtocolException: SSL handshake aborted: ssl=0x52fb82e8: Failure in SSL library, usually a protocol error
//                        error:1407742E:SSL routines:SSL23_GET_SERVER_HELLO:tlsv1 alert protocol version (external/openssl/ssl/s23_clnt.c:741 0x53124ce4:0x00000000)
//                        at com.android.org.conscrypt.NativeCrypto.SSL_do_handshake(Native Method)
//                        at com.android.org.conscrypt.OpenSSLSocketImpl.startHandshake(OpenSSLSocketImpl.java:405)
//                                                                         	... 23 more
                    }
                });
            }
        });
    }
}
