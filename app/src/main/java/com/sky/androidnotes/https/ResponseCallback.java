package com.sky.androidnotes.https;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by tonycheng on 2017/10/26.
 */

public interface ResponseCallback {

    void onSuccess(Response response);

    void onFail(IOException e);
}
