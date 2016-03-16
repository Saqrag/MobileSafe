package com.bug.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ShareUtil;
import com.bug.mobilesafe.views.CheckBoxItem;

/**
 * Created by saqra on 2016/1/30.
 */
public class Guide2_BindSIM extends BaseActivity {

    private CheckBoxItem cbiBindSim;
    private SharedPreferences sPre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide2_bindsim);
        sPre = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);
        initCBI_BindSim();

    }

    /**
     * init View of cbiBindSim
     */
    private void initCBI_BindSim() {
        cbiBindSim = (CheckBoxItem) findViewById(R.id.cbiBindCard);

        if (!TextUtils.isEmpty(sPre.getString(ShareUtil.SIM, ""))) {
            cbiBindSim.setChecked(true);
        } else {
            cbiBindSim.setChecked(false);
        }

        cbiBindSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sPre.edit();
                if (cbiBindSim.isChecked()) {
                    cbiBindSim.setChecked(false);
                    editor.remove(ShareUtil.SIM).commit();
                } else {
                    cbiBindSim.setChecked(true);

                    //获得sim卡序列号
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();
//                    System.out.println("simSerialNumber:" + simSerialNumber);
                    editor.putString(ShareUtil.SIM, simSerialNumber).commit();
                }
            }
        });
    }


    /**
     * 进入下一个Activity
     */
    @Override
    public void showNextPage() {
        if (!TextUtils.isEmpty(sPre.getString(ShareUtil.SIM, ""))) {
            PageUtils.nextPage(Guide2_BindSIM.this, Guide3_SafeNum.class);
        } else {
            Toast.makeText(Guide2_BindSIM.this, "you must to check the BindSim!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 返回上一个Activity
     */
    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(Guide2_BindSIM.this, Guide1_Welcome.class);
    }
}
