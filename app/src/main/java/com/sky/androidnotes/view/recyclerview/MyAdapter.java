package com.sky.androidnotes.view.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sky.androidnotes.R;

import java.util.List;

/**
 * Created by tonycheng on 2017/8/3.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Result> mDatas;

    public MyAdapter() {
    }

    public void setData(List<Result> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent
                , false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Result result = mDatas.get(position);
        if (result != null && !TextUtils.isEmpty(result.getDesc())) {
            holder.mTextView.setText(result.getDesc());
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text);
        }
    }
}
