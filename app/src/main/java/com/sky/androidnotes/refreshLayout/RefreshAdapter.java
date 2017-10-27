package com.sky.androidnotes.refreshLayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sky.androidnotes.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by tonycheng on 2017/9/20.
 */

public class RefreshAdapter extends RecyclerView.Adapter<RefreshAdapter.RefreshViewHolder> {

    private List<String> mValues;

    public RefreshAdapter() {
        mValues = Collections.emptyList();
    }

    public void setData(@NonNull List<String> dataList) {
        mValues = dataList;
    }

    @Override
    public RefreshViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        return new RefreshViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RefreshViewHolder holder, int position) {
        if (mValues == null || mValues.isEmpty()) {
            return;
        }
        final String content = mValues.get(position);
        if (!TextUtils.isEmpty(content)) {
            holder.mTextView.setText(content);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class RefreshViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        RefreshViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
