package com.sky.androidnotes.refreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sky.androidnotes.R;

import java.util.ArrayList;
import java.util.List;

public class RefreshActivity extends AppCompatActivity implements OnRefreshListener,
        OnLoadmoreListener {

    private static final String TAG = "RefreshActivity";

    private RefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private List<String> mDataList = new ArrayList<>();
    private Handler mHandler;
    private RefreshAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        mRefreshLayout.setRefreshHeader(new MaterialHeader(this)).setEnableHeaderTranslationContent(false);
        mRefreshLayout.setRefreshFooter(new ClassicsFooter(this)).setEnableFooterTranslationContent(false);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadmoreListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RefreshAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mHandler = new Handler();
        initData();
        mAdapter.setData(mDataList);
        mAdapter.notifyDataSetChanged();
    }

    private void initData() {
        List<String> tempList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tempList.add("index " + i);
        }
        mDataList.addAll(tempList);
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        Log.d(TAG, "onRefresh: ");
        onrefresh();
    }

    private void onrefresh() {
        final List<String> tempList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tempList.add("onRefresh " + SystemClock.elapsedRealtime());
        }
        mDataList.addAll(0, tempList);
        mRefreshLayout.finishRefresh();
//        mAdapter.notifyItemRangeInserted(0, tempList.size());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        Log.d(TAG, "onLoadmore: ");
        fetchData();
    }

    private void fetchData() {
        final List<String> tempList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            tempList.add("loadMore " + SystemClock.elapsedRealtime());
        }
        mDataList.addAll(tempList);
        mRefreshLayout.finishLoadmore();
//        mAdapter.notifyItemRangeInserted(mDataList.size() - tempList.size() - 1, tempList.size());
        mAdapter.notifyDataSetChanged();
    }
}
