package com.bug.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.services.LockAppService;
import com.bug.mobilesafe.utils.MD5Utils;
import com.bug.mobilesafe.utils.ShareUtil;

/**
 * Created by saqra on 2016/3/9.
 */
public class PwdActivity extends BaseActivity {

    private SharedPreferences sPre;
    private String packageName;

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        sPre = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);
        packageName = getIntent().getStringExtra("packageName");
        showDialog();
    }

    private void showDialog() {
        String sGuardPwd = sPre.getString("guardPwd", "");

        if (TextUtils.isEmpty(sGuardPwd)) {  //判断是否已经设置密码
            final View setGuardPwdItem = View.inflate(this, R.layout.item_set_guard_pwd, null);
            final AlertDialog.Builder guardPwd = new AlertDialog.Builder(this);
            final AlertDialog dialog = guardPwd.create();

            //guardPwd.setView(setGuardPwdItem);

            //兼容2.x的版本
            dialog.setView(setGuardPwdItem, 0, 0, 0, 0);

            dialog.show();
            Button btn_ok = (Button) setGuardPwdItem.findViewById(R.id.btn_ok);
            Button btn_cancel = (Button) setGuardPwdItem.findViewById(R.id.btn_cancel);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText etGuardPwd = (EditText) setGuardPwdItem.findViewById(R.id.et_guardPwd);
                    EditText etConfirmGuardPwd = (EditText) setGuardPwdItem.findViewById(R.id.et_confirmGuardPwd);
                    String mGuardPwd = etGuardPwd.getText().toString();
                    String mConfirmGuardPwd = etConfirmGuardPwd.getText().toString();
                    if (TextUtils.isEmpty(mGuardPwd) || TextUtils.isEmpty(mConfirmGuardPwd)) {
                        Toast.makeText(PwdActivity.this, "密码不能为空,请重新输入!", Toast.LENGTH_SHORT).show();
                    } else if (!mGuardPwd.equals(mConfirmGuardPwd)) {
                        Toast.makeText(PwdActivity.this, "两次密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = sPre.edit();
                        editor.putBoolean(ShareUtil.IS_SET_GUARD_PWD, true);
                        editor.putString("guardPwd", MD5Utils.encode(mGuardPwd));
                        editor.commit();

                        Toast.makeText(PwdActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.setAction("com.bug.mobilesafe.lockedapp");
                        intent.putExtra("packageName", packageName);
                        dialog.dismiss();
                        PwdActivity.this.finish();
                        sendBroadcast(intent);

                    }

                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        } else {
            final View enterGuardPwdItem = View.inflate(this, R.layout.item_enter_guard_pwd, null);
            final AlertDialog.Builder brEnterGuardPwd = new AlertDialog.Builder(this);
            final AlertDialog dgEnterGuardPwd = brEnterGuardPwd.create();
//            dgEnterGuardPwd.setView(enterGuardPwdItem); Android2.x以上的使用此方法
            dgEnterGuardPwd.setView(enterGuardPwdItem, 0, 0, 0, 0);
            dgEnterGuardPwd.show();
            Button btnOk = (Button) enterGuardPwdItem.findViewById(R.id.btn_ok);
            Button btnCancel = (Button) enterGuardPwdItem.findViewById(R.id.btn_cancel);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText etGuardPwd = (EditText) enterGuardPwdItem.findViewById(R.id.et_guardPwd);
                    String sGuardPwd = etGuardPwd.getText().toString();

                    //判断密码
                    if ((!TextUtils.isEmpty(sGuardPwd)) && (MD5Utils.encode(sGuardPwd)
                            .equals(sPre.getString("guardPwd", null)))) {
                        Intent intent = new Intent();
                        intent.setAction("com.bug.mobilesafe.lockedapp");
                        intent.putExtra("packageName", packageName);
                        intent.putExtra("status", 0);
                        dgEnterGuardPwd.dismiss();
                        PwdActivity.this.finish();
                        sendBroadcast(intent);

                    } else {
                        Toast.makeText(PwdActivity.this, "您输入的密码错误,请重新输入", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dgEnterGuardPwd.dismiss();
                }
            });
        }
    }

    public void pwd(View view) {

        showDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent();
        intent.setAction("com.bug.mobilesafe.lockedapp");
        intent.putExtra("status", 1);
        sendBroadcast(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.err.println("onPause");
    }
}
