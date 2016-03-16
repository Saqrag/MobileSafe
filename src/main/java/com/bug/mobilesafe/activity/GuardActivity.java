package com.bug.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ShareUtil;

/**
 * Created by saqra on 2016/1/30.
 */
public class GuardActivity extends BaseActivity {

    private SharedPreferences sPre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard);
        sPre = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);
        initTVSafeNum();
        initIVGuardState();
        initRLReturnGuide();
    }

    private void initIVGuardState() {
        ImageView ivGuardState= (ImageView) findViewById(R.id.ivGuardState);
        boolean configed = sPre.getBoolean(ShareUtil.IS_GUARD, false);
        if (configed){
            ivGuardState.setBackgroundResource(R.drawable.lock);
        }else {
            ivGuardState.setBackgroundResource(R.drawable.unlock);
        }
    }

    private void initTVSafeNum() {
        String phone = sPre.getString(ShareUtil.PHONE, "");
        TextView tvSafeNum= (TextView) findViewById(R.id.tvSafeNum);
        if (!TextUtils.isEmpty(phone)){
            tvSafeNum.setText(phone);
        }else {
            tvSafeNum.setText("无");
        }

    }

    private void initRLReturnGuide() {
        RelativeLayout rlReturnGuide= (RelativeLayout) findViewById(R.id.rl_return_guide);
        rlReturnGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageUtils.nextPage(GuardActivity.this,Guide1_Welcome.class);
            }
        });
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
        PageUtils.previousPage(this,HomeActivity.class);
    }
}
