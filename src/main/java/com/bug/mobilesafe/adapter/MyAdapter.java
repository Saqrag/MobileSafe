package com.bug.mobilesafe.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by saqra on 2016/2/20.
 */
public abstract class MyAdapter<T> extends BaseAdapter {
    public List<T> list;
    public MyAdapter(List<T> list){
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
