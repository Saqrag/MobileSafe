package com.bug.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ShareUtil;

/**
 * Created by saqra on 2016/1/31.
 */
public class Guide3_SafeNum extends BaseActivity {
    EditText etContact=null;
    SharedPreferences sPre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide3_safenum);
        sPre=getSharedPreferences(ShareUtil.CONFIG,MODE_PRIVATE);
        etContact= (EditText) findViewById(R.id.et_safeNum);
        initETContact();
    }

    private void initETContact() {
        String phone = sPre.getString(ShareUtil.PHONE, "");
        if (!TextUtils.isEmpty(phone)){
            etContact.setText(phone);
        }
    }

    /**
     * 进入下一个Activity
     */
    @Override
    public void showNextPage() {
        String phone = etContact.getText().toString().trim();
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(Guide3_SafeNum.this, "安全号码不能为空!", Toast.LENGTH_SHORT).show();
        }else {
            SharedPreferences.Editor editor = sPre.edit();
            editor.putString(ShareUtil.PHONE,phone).commit();
            PageUtils.nextPage(this, Guide4_SetOk.class);
        }
    }

    /**
     * 返回上一个Activity
     */
    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this, Guide2_BindSIM.class);
    }

    /**
     * selectContent button's onClick
     *
     * @param view
     */
    public void selectContent(View view) {
//        System.out.println("selectContent");
        Intent intent = new Intent(this, ContactActivity.class);
        startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode== Activity.RESULT_OK){
            String phone = data.getExtras().getString("phone");
            if (etContact!=null){
                etContact.setText(phone);
            }

        }
    }
}
