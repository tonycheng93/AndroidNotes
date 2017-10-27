package com.sky.androidnotes.view.recyclerview;

import com.google.gson.Gson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.sky.androidnotes.R;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private List<Result> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        new Thread(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }).start();


        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://gank.io/api/data/Android/10/1")
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.body() == null) {
                            return;
                        }
                        String result = response.body().string();
                        Log.d(TAG, "onResponse: " + result);
                        Gank gank = new Gson().fromJson(result, Gank.class);
                        if (gank != null) {
                            mDatas = gank.getResults();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               mAdapter.setData(mDatas);
                            }
                        });
                    }
                });
    }
}
