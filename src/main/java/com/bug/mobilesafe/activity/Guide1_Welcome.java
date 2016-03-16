package com.bug.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.utils.PageUtils;

/**
 * Created by saqra on 2016/1/30.
 */
public class Guide1_Welcome extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide1_welcome);
    }

    /**
     * 进入下一个Activity
     */
    @Override
    public void showNextPage() {
        PageUtils.nextPage(this,Guide2_BindSIM.class);
//        Intent intGuide2 = new Intent(Guide1_Welcome.this, Guide2_BindSIM.class);
//        startActivity(intGuide2);
//        finish();
//        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    /**
     * 返回上一个Activity
     */
    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this,HomeActivity.class);
    }
}
