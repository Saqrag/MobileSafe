package com.bug.mobilesafe.fragment;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.activity.LockAppActivity;
import com.bug.mobilesafe.bean.AppInfo;
import com.bug.mobilesafe.database.LockAppDB;

/**
 * Created by saqra on 2016/3/7.
 */
public class LockedAppFra extends android.support.v4.app.Fragment {

    private LockAppActivity mActivity;
    private MyAdapter myAdapter;
    private ListView lv_no_add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fra_noadded,null);

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
        lv_no_add = (ListView) getView().findViewById(R.id.lv_add);
        myAdapter = new MyAdapter();
        lv_no_add.setAdapter(myAdapter);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mActivity.lockedSystemApps.size() +
                    mActivity.lockedUsesApps.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            if (position<mActivity.lockedUsesApps.size()+1){
                return mActivity.lockedUsesApps.get(position-1);
            }else {
                return mActivity.lockedSystemApps
                        .get(position - mActivity.lockedUsesApps.size() - 2);
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
                textView.setText("用户应用：" + mActivity.lockedUsesApps.size() + "个");
                textView.setBackgroundColor(Color.BLACK);
                textView.setTextColor(Color.WHITE);
                return textView;
            }

            if (position == mActivity.lockedUsesApps.size() + 1) {
                TextView textView = new TextView(mActivity);
                textView.setText("系统应用：" + mActivity.lockedSystemApps.size() + "个");
                textView.setBackgroundColor(Color.BLACK);
                textView.setTextColor(Color.WHITE);
                return textView;
            }

           final View view;
            Holder holder;
            if (convertView == null || convertView instanceof TextView) {
                holder = new Holder();
                view = View.inflate(mActivity, R.layout.item_locked_app, null);
                holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.iv_lock_app = (ImageView) view.findViewById(R.id.iv_unlock_app);
                view.setTag(holder);
            } else {
                view = convertView;
            }

            holder = (Holder) view.getTag();

            if (position < mActivity.lockedUsesApps.size() + 1) {
                AppInfo appInfo = mActivity.lockedUsesApps.get(position - 1);
                holder.iv_app_icon.setBackgroundDrawable(appInfo.getAppIcon());
                holder.tv_app_name.setText(appInfo.getAppName());
            } else {
                AppInfo appInfo = mActivity.lockedSystemApps.get(position - mActivity.lockedUsesApps.size() - 2);
                holder.iv_app_icon.setBackgroundDrawable(appInfo.getAppIcon());
                holder.tv_app_name.setText(appInfo.getAppName());
            }

            //单击事件
            holder.iv_lock_app.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TranslateAnimation animation=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,-1f,
                            Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                    animation.setDuration(1000);

                    view.startAnimation(animation);

                    AsyncTask<Object,Object,Object> task = new AsyncTask<Object,Object,Object>() {

                        @Override
                        protected Object doInBackground(Object[] params) {
                            SystemClock.sleep(1000);
                            AppInfo item = (AppInfo) myAdapter.getItem(position);
                            if (position<mActivity.lockedUsesApps.size()+1){
                                mActivity.lockedUsesApps.remove(item);
                                mActivity.unLockedUsesApps.add(0,item);
                            }else {
                                mActivity.lockedSystemApps.remove(item);
                                mActivity.unLockedSystemApps.add(0,item);
                            }

                            LockAppDB lockAppDB = new LockAppDB(mActivity);
                            lockAppDB.deleteApp(item.getPackageName());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            myAdapter.notifyDataSetChanged();
                        }
                    };

                    task.execute();
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
