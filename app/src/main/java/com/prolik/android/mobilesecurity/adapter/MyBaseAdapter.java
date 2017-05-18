package com.prolik.android.mobilesecurity.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by ProLik on 2016/2/19.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    public List<T> mList;
    public Context mContext;

    protected MyBaseAdapter(List<T> list, Context context) {
        mList = list;
        mContext = context;
    }

    protected MyBaseAdapter() {
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
