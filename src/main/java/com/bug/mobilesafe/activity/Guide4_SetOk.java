package com.bug.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ShareUtil;

/**
 * Created by saqra on 2016/1/31.
 */
public class Guide4_SetOk extends BaseActivity {


    CheckBox cbGuard;
    private SharedPreferences sPre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide4_setok);
        sPre = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);
        intiCBGuard();
    }

    private void intiCBGuard() {
        cbGuard= (CheckBox) findViewById(R.id.cb_guard);
        boolean isGuard = sPre.getBoolean(ShareUtil.IS_GUARD, false);
        if (isGuard){
            cbGuard.setText("您已经开启防盗保护");
            cbGuard.setChecked(true);
        }else {
            cbGuard.setText("您没有开启防盗保护");
            cbGuard.setChecked(false);
        }
        cbGuard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sPre.edit();
                if (cbGuard.isChecked()){
                    cbGuard.setText("您已经开启防盗保护");
                    editor.putBoolean(ShareUtil.IS_GUARD,true).commit();
                }else {
                    cbGuard.setText("您没有开启防盗保护");
                    editor.putBoolean(ShareUtil.IS_GUARD,false).commit();
                }
            }
        });
    }

    /**
     * 进入下一个Activity
     */
    @Override
    public void showNextPage() {
        SharedPreferences sPre=getSharedPreferences(ShareUtil.CONFIG,MODE_PRIVATE);
        SharedPreferences.Editor edit = sPre.edit();
        edit.putBoolean(ShareUtil.IS_CONFIG,true).commit();//设置过向导页
        PageUtils.nextPage(Guide4_SetOk.this, GuardActivity.class);
    }

    /**
     * 返回上一个Activity
     */
    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(Guide4_SetOk.this, Guide3_SafeNum.class);
    }

}
