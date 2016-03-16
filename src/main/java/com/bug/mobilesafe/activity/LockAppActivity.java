package com.bug.mobilesafe.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.bean.AppInfo;
import com.bug.mobilesafe.database.LockAppDB;
import com.bug.mobilesafe.engine.AppInfoProvider;
import com.bug.mobilesafe.fragment.UnLockedAppFra;
import com.bug.mobilesafe.fragment.LockedAppFra;
import com.bug.mobilesafe.utils.PageUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saqra on 2016/3/1.
 */
public class LockAppActivity extends BaseActivity {

    //实例化View
    @ViewInject(R.id.btn_add)
    Button btn_add;
    @ViewInject(R.id.btn_no_add)
    Button btn_no_add;


    public List<AppInfo> unLockedSystemApps;
    public List<AppInfo> lockedSystemApps;
    public List<AppInfo> unLockedUsesApps;
    public List<AppInfo> lockedUsesApps;

    private Fragment addedFragment;
    private Fragment noAddFragment;
    private ProgressDialog progressDialog;

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0:

                    progressDialog.dismiss();
                    initView();

                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_app);

        ViewUtils.inject(this);

        initData();
    }

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this,AdvaceToolsActivity.class);
    }

    private void initData() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("请稍后........");
        progressDialog.show();
        new Thread(){
            @Override
            public void run() {
                List<AppInfo> appInfos;
                appInfos = AppInfoProvider.getAppInfos(LockAppActivity.this);
                LockAppDB lockAppDB = new LockAppDB(LockAppActivity.this);
                List<String> lockedApps = lockAppDB.getLockedApps();
                unLockedSystemApps = new ArrayList<>();
                lockedSystemApps = new ArrayList<>();
                unLockedUsesApps = new ArrayList<>();
                lockedUsesApps = new ArrayList<>();
                for (AppInfo info : appInfos) {
                    boolean isLocked=false;

                    for (String lockedApp : lockedApps) {
                        if (info.getPackageName().equals(lockedApp)){
                            if (info.isUserApp()){
                                lockedUsesApps.add(info);
                            }else {
                                lockedSystemApps.add(info);
                            }
                            isLocked=true;
                            break;
                        }
                    }

                    if (!isLocked==true){
                        if (info.isUserApp()){
                            unLockedUsesApps.add(info);
                        }else {
                            unLockedSystemApps.add(info);
                        }
                    }
                }

                handler.sendEmptyMessage(0);

            }
        }.start();
    }

    private void initView() {

        addedFragment = new UnLockedAppFra();
        noAddFragment = new LockedAppFra();

        btn_add.setBackgroundResource(R.drawable.tab_right_default);
        btn_no_add.setBackgroundResource(R.drawable.tab_left_pressed);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_app_list, addedFragment);
        transaction.commit();

        MyListener myListener = new MyListener();
        btn_add.setOnClickListener(myListener);
        btn_no_add.setOnClickListener(myListener);

    }


    private class MyListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            switch (v.getId()) {


                case R.id.btn_no_add:
                    btn_add.setBackgroundResource(R.drawable.tab_right_default);
                    btn_no_add.setBackgroundResource(R.drawable.tab_left_pressed);

                    transaction.replace(R.id.fl_app_list, addedFragment);
                    transaction.commit();
                    break;

                case R.id.btn_add:
                    btn_add.setBackgroundResource(R.drawable.tab_right_pressed);
                    btn_no_add.setBackgroundResource(R.drawable.tab_left_default);

                    transaction.replace(R.id.fl_app_list, noAddFragment);
                    transaction.commit();
                    break;

            }

        }
    }
}
