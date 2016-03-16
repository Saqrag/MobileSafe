package com.bug.mobilesafe.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.activity.LockAppActivity;
import com.bug.mobilesafe.bean.AppInfo;
import com.bug.mobilesafe.database.LockAppDB;

import java.util.Objects;


/**
 * Created by saqra on 2016/3/7.
 */
public class UnLockedAppFra extends android.support.v4.app.Fragment {
    private LockAppActivity mActivity;
    private MyAdapter myAdapter;
    private ListView lv_add;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_added, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        myAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        mActivity = (LockAppActivity) getActivity();
        initView();
    }

    private void initView() {
        lv_add = (ListView) getView().findViewById(R.id.lv_add);
        myAdapter = new MyAdapter();
        lv_add.setAdapter(myAdapter);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mActivity.unLockedSystemApps.size() +
                    mActivity.unLockedUsesApps.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            if (position<mActivity.unLockedUsesApps.size()+1){
                return mActivity.unLockedUsesApps.get(position-1);
            }else {
                return mActivity.unLockedSystemApps
                        .get(position - mActivity.unLockedUsesApps.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                TextView textView = new TextView(mActivity);
                textView.setText("用户应用：" + mActivity.unLockedUsesApps.size() + "个");
                textView.setBackgroundColor(Color.BLACK);
                textView.setTextColor(Color.WHITE);
                return textView;
            }

            if (position == mActivity.unLockedUsesApps.size() + 1) {
                TextView textView = new TextView(mActivity);
                textView.setText("系统应用：" + mActivity.unLockedSystemApps.size() + "个");
                textView.setBackgroundColor(Color.BLACK);
                textView.setTextColor(Color.WHITE);
                return textView;
            }

            final View view;
            Holder holder;
            if (convertView == null || convertView instanceof TextView) {
                holder = new Holder();
                view = View.inflate(mActivity, R.layout.item_unlocked_app, null);
                holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.iv_lock_app = (ImageView) view.findViewById(R.id.iv_lock_app);
                view.setTag(holder);
            } else {
                view = convertView;
            }

            holder = (Holder) view.getTag();

            if (position < mActivity.unLockedUsesApps.size() + 1) {
                AppInfo appInfo = mActivity.unLockedUsesApps.get(position - 1);
                holder.iv_app_icon.setBackgroundDrawable(appInfo.getAppIcon());
                holder.tv_app_name.setText(appInfo.getAppName());
            } else {
                AppInfo appInfo = mActivity.unLockedSystemApps.get(position - mActivity.unLockedUsesApps.size() - 2);
                holder.iv_app_icon.setBackgroundDrawable(appInfo.getAppIcon());
                holder.tv_app_name.setText(appInfo.getAppName());
            }

            //单击事件
            holder.iv_lock_app.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TranslateAnimation animation=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,1f,
                            Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                    animation.setDuration(1000);

                    view.startAnimation(animation);

                    AsyncTask<Object,Object,Object> task = new AsyncTask<Object,Object,Object>() {



                        @Override
                        protected Object doInBackground(Object[] params) {

                            SystemClock.sleep(1000);
                            System.err.println("doInBackground:1");
                            AppInfo item = (AppInfo) myAdapter.getItem(position);
                            if (position<mActivity.unLockedUsesApps.size()+1){
                                System.err.println("doInBackground:2");
                                mActivity.unLockedUsesApps.remove(item);
                                mActivity.lockedUsesApps.add(0, item);
                                System.err.println("doInBackground:3");
                            }else {
                                System.err.println("doInBackground:4");
                                mActivity.unLockedSystemApps.remove(item);
                                mActivity.lockedSystemApps.add(0, item);
                                System.err.println("doInBackground:5");
                            }

                            LockAppDB lockAppDB = new LockAppDB(mActivity);
                            lockAppDB.lockApp(item.getPackageName());
                            System.err.println("doInBackground:6");
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            System.err.println("onPostExecute");
                            myAdapter.notifyDataSetChanged();
                        }
                    };

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }else {
                        task.execute();
                    }
                }
            });

            return view;
        }
    }

    class Holder {
        public ImageView iv_app_icon;
        public TextView tv_app_name;
        public ImageView iv_lock_app;
    }

}
