package com.bug.mobilesafe.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.rocketutil.RocketService;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ServiceStatusUtil;
import com.bug.mobilesafe.utils.ShareUtil;
import com.bug.mobilesafe.views.CheckBoxItem;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by saqra on 2016/2/28.
 */
public class ProcessSettingActivity extends BaseActivity{

    @ViewInject(R.id.cbi_rocket)
    CheckBoxItem cbi_rocket;
    private SharedPreferences spre;

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this, ProcessActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        ViewUtils.inject(this);

        spre = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);
        boolean isRunning = spre.getBoolean(ShareUtil.IS_ROCKET, false);
        if (!ServiceStatusUtil.isRunning(this,"com.bug.mobilesafe.rocketutil.RocketService")){
            if (isRunning){
                startService(new Intent(ProcessSettingActivity.this, RocketService.class));

            }
        }


        cbi_rocket.setChecked(isRunning);



        cbi_rocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = spre.edit();
                if (cbi_rocket.isChecked()) {
                    cbi_rocket.setChecked(false);
                    edit.putBoolean(ShareUtil.IS_ROCKET, false);
                    stopService(new Intent(ProcessSettingActivity.this, RocketService.class));
                } else {
                    cbi_rocket.setChecked(true);
                    edit.putBoolean(ShareUtil.IS_ROCKET, true);
                    startService(new Intent(ProcessSettingActivity.this, RocketService.class));
                }

                edit.commit();

            }
        });
    }
}
