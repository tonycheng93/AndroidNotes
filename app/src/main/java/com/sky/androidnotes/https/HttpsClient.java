package com.sky.androidnotes.https;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by tonycheng on 2017/10/26.
 *
 * @author cheng
 */

final class HttpsClient {

    private static final String TAG = "HttpsClient";

    private static final int READ_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;
    private static final int CONNECT_TIMEOUT = 10;

    private final OkHttpClient mOkHttpClient;

    private HttpsClient() {
        SSLSocketFactory noSSLv3Factory = null;
        X509TrustManager x509TrustManager = null;

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            x509TrustManager = (X509TrustManager) trustManagers[0];
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            Log.e(TAG, "HttpsClient: ", e);
            e.printStackTrace();
        }

        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
            noSSLv3Factory = new TlsOnlySocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultSSLSocketFactory(noSSLv3Factory);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Log.e(TAG, "HttpsClient: ", e);
            e.printStackTrace();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        if (noSSLv3Factory != null && x509TrustManager != null) {
            builder.sslSocketFactory(noSSLv3Factory, x509TrustManager);
        }
        mOkHttpClient = builder.build();
    }

    private static class SingletonHolder {
        private static final HttpsClient INSTANCE = new HttpsClient();
    }

    public static HttpsClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    void load(@NonNull String url, @NonNull final ResponseCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        callback.onSuccess(response);
                    }
                });
    }
}
