package com.bug.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.services.ArributionService;
import com.bug.mobilesafe.services.CallSafeService;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ServiceStatusUtil;
import com.bug.mobilesafe.utils.ShareUtil;
import com.bug.mobilesafe.views.CheckBoxItem;
import com.bug.mobilesafe.views.ClickItem;

/**
 * Created by saqra on 2016/1/29.
 */
public class SettingActivity extends BaseActivity {
    CheckBoxItem cbiAutoUpdate;

    SharedPreferences mSP;
    private CheckBoxItem cbiLocation;
    private ClickItem ciStyle;
    private final String[] items = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    private ClickItem ciLocation;
    private CheckBoxItem cbiBlackList;

    public SettingActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mSP = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);
        initView();
        addOnClickListener();
    }

    private void initView() {
        cbiAutoUpdate = (CheckBoxItem) findViewById(R.id.cbi_autoupdate);
        cbiLocation = (CheckBoxItem) findViewById(R.id.cbi_location);
        ciStyle = (ClickItem) findViewById(R.id.ci_style);
        ciLocation = (ClickItem) findViewById(R.id.ci_location);
        cbiBlackList = (CheckBoxItem) findViewById(R.id.cbi_BlackList);

        Boolean isUp = mSP.getBoolean(ShareUtil.IS_UPDATE, true);
        cbiAutoUpdate.setChecked(isUp);


        Boolean isLocation = mSP.getBoolean(ShareUtil.IS_LOCATION, false);
        Boolean isLocationRunning = ServiceStatusUtil.isRunning(this, "com.bug.mobilesafe.services.ArributionService");
        Boolean isCallSafe = mSP.getBoolean(ShareUtil.IS_BLACK_LIST, false);
        Boolean isCallSafeRunning = ServiceStatusUtil.isRunning(this, "com.bug.mobilesafe.services.CallSafeService");
        if ((isCallSafe&&(!isCallSafeRunning))&&(isLocation&&(!isLocationRunning))){
            Toast.makeText(this, "由于权限问题,黑名单拦截和归属地服务被杀死,请手动重新打开归属地设置", Toast.LENGTH_LONG).show();
            cbiBlackList.setChecked(false);
            cbiLocation.setChecked(false);
        }else if (isLocation && (!isLocationRunning)) {
            Toast.makeText(this, "由于权限问题,归属地服务被杀死,请手动重新打开归属地设置", Toast.LENGTH_LONG).show();
            cbiLocation.setChecked(false);
            cbiBlackList.setChecked(isCallSafe);
        }else if (isCallSafe&&(!isCallSafeRunning)){
            Toast.makeText(this, "由于权限问题,黑名单拦截服务被杀死,请手动重新打开归属地设置", Toast.LENGTH_LONG).show();
            cbiBlackList.setChecked(false);
            cbiLocation.setChecked(isLocation);
        } else {
            cbiLocation.setChecked(isLocation);
            cbiBlackList.setChecked(isCallSafe);
        }

        int style = mSP.getInt(ShareUtil.ADDRESS_STYLE, 0);
        ciStyle.setDesc(items[style]);
    }

    /**
     * 进入下一个Activity
     */
    @Override
    public void showNextPage() {

    }

    /**
     * 返回上一个Activity
     */
    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this, HomeActivity.class);
    }


    private void addOnClickListener() {
        MyOnClickListener myOnClickListener = new MyOnClickListener();
        cbiAutoUpdate.setOnClickListener(myOnClickListener);
        cbiLocation.setOnClickListener(myOnClickListener);
        ciStyle.setOnClickListener(myOnClickListener);
        ciLocation.setOnClickListener(myOnClickListener);
        cbiBlackList.setOnClickListener(myOnClickListener);
    }


    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.cbi_autoupdate://自动更新
                    SharedPreferences.Editor editor = mSP.edit();
                    if (cbiAutoUpdate.isChecked()) {
                        cbiAutoUpdate.setChecked(false);
                        editor.putBoolean(ShareUtil.IS_UPDATE, false);
                    } else {
                        cbiAutoUpdate.setChecked(true);
                        editor.putBoolean(ShareUtil.IS_UPDATE, true);
                    }
                    editor.commit();
                    break;

                case R.id.cbi_location://归属地
                    SharedPreferences.Editor edit = mSP.edit();
                    if (cbiLocation.isChecked()) {
                        cbiLocation.setChecked(false);
                        edit.putBoolean(ShareUtil.IS_LOCATION, false);
                        stopService(new Intent(SettingActivity.this, ArributionService.class));
                    } else {
                        cbiLocation.setChecked(true);
                        edit.putBoolean(ShareUtil.IS_LOCATION, true);
                        startService(new Intent(SettingActivity.this, ArributionService.class));
                    }
                    edit.commit();
                    break;

                case R.id.ci_style://归属地风格
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                    builder.setTitle("选择归属地风格");
                    int anInt = mSP.getInt(ShareUtil.ADDRESS_STYLE, 0);
                    builder.setSingleChoiceItems(items, anInt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mSP.edit().putInt(ShareUtil.ADDRESS_STYLE, which).commit();
                            ciStyle.setDesc(items[which]);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                    break;

                //归属地提示框位置设置
                case R.id.ci_location:
                    PageUtils.nextPage(SettingActivity.this, DragViewActivity.class);
                    break;
                case R.id.cbi_BlackList:
                    SharedPreferences.Editor edit1 = mSP.edit();
                    if (cbiBlackList.isChecked()) {
                        cbiBlackList.setChecked(false);
                        edit1.putBoolean(ShareUtil.IS_BLACK_LIST, false);
                        Intent intent=new Intent(SettingActivity.this,CallSafeService.class);
                        stopService(intent);
                    } else {
                        cbiBlackList.setChecked(true);
                        edit1.putBoolean(ShareUtil.IS_BLACK_LIST, true);
                        Intent intent=new Intent(SettingActivity.this,CallSafeService.class);
                        startService(intent);
                    }
                    edit1.commit();
                    break;
            }
        }
    }
}