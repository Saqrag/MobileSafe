package com.bug.mobilesafe.activity;

import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.utils.AddressUtil;
import com.bug.mobilesafe.utils.PageUtils;

/**
 * Created by saqra on 2016/2/12.
 */
public class AttributionActivity extends BaseActivity {

    private EditText etAttribution;
    private TextView tvAttribution;

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this,AdvaceToolsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribution);
        initView();
    }

    private void initView() {
        etAttribution = (EditText) findViewById(R.id.et_attribution);
        tvAttribution = (TextView) findViewById(R.id.tv_attribution);
        etAttribution.addTextChangedListener(new MyTextChage());
    }



    public void select(View view) {
        String mNum = etAttribution.getText().toString().trim();
        String location = AddressUtil.getAddress(mNum);
//        System.out.println(location);
        tvAttribution.setText(location);
        if (location.equals("未知号码")) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            etAttribution.startAnimation(shake);
            vibrate();
        }
    }

    private void vibrate() {
        Vibrator vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0,200,200,300},-1);// 先等待0秒,再震动2秒,再等待2秒,再震动3秒,
                                                       // 参2等于-1表示只执行一次,不循环,
                                                       // 参2等于0表示从头循环,
                                                       // 参2表示从第几个位置开始循环
    }


    private class MyTextChage implements TextWatcher {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String mNum = etAttribution.getText().toString().trim();
            String location = AddressUtil.getAddress(mNum);
            tvAttribution.setText(location);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
