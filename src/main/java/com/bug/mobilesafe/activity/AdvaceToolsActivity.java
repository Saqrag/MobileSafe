package com.bug.mobilesafe.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.services.LockAppService;
import com.bug.mobilesafe.utils.MD5Utils;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ShareUtil;
import com.bug.mobilesafe.utils.SmsUtil;
import com.bug.mobilesafe.utils.UIUtils;
import com.bug.mobilesafe.views.CheckBoxItem;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by saqra on 2016/2/10.
 */
public class AdvaceToolsActivity extends BaseActivity {



    private SharedPreferences sPre;

    @ViewInject(R.id.cbi_app_lock)
    CheckBoxItem cbi_app_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_tools);
        ViewUtils.inject(this);

        sPre = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);

        initUI();
    }

    private void initUI() {
        cbi_app_lock.setChecked(sPre.getBoolean(ShareUtil.IS_APP_LOCK, false));
    }

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this, HomeActivity.class);
    }

    /**
     * 电话归属地查询
     *
     * @param view
     */
    public void attribution(View view) {

        PageUtils.nextPage(this, AttributionActivity.class);
    }

    /**
     * 单击短信备份
     *
     * @param view
     */
    public void backUpSms(View view) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("备份进度");
        progressDialog.setMessage("正在备份不要着急!!!");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

//        final ProgressBar pb_backup= (ProgressBar) findViewById(R.id.pb_backup);
        new Thread() {
            @Override
            public void run() {

                boolean isSuccess = SmsUtil.backUpSms(AdvaceToolsActivity.this, new SmsUtil.Progress() {
                    @Override
                    public void maxCount(int maxCount) {
//                        pb_backup.setMax(maxCount);
                        progressDialog.setMax(maxCount);
                    }

                    @Override
                    public void updateProgress(int progress) {
//                        pb_backup.setProgress(progress);
                        progressDialog.setProgress(progress);
                    }
                });

                progressDialog.dismiss();

                if (isSuccess) {
                    UIUtils.toast(AdvaceToolsActivity.this, "备份完成");
                } else {
                    UIUtils.toast(AdvaceToolsActivity.this, "备份失败");
                }

            }
        }.start();

    }

    /**
     * 添加锁定应用
     * @param view
     */
    public void addAppList(View view) {

        String sGuardPwd = sPre.getString("guardPwd", "");

        if (TextUtils.isEmpty(sGuardPwd)) {  //判断是否已经设置密码
            final View setGuardPwdItem = View.inflate(this, R.layout.item_set_guard_pwd, null);
            final AlertDialog.Builder guardPwd = new AlertDialog.Builder(this);
            final AlertDialog dialog = guardPwd.create();

            //guardPwd.setView(setGuardPwdItem);

            //兼容2.x的版本
            dialog.setView(setGuardPwdItem,0,0,0,0);

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
                        Toast.makeText(AdvaceToolsActivity.this, "密码不能为空,请重新输入!", Toast.LENGTH_SHORT).show();
                    } else if (!mGuardPwd.equals(mConfirmGuardPwd)) {
                        Toast.makeText(AdvaceToolsActivity.this, "两次密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = sPre.edit();
                        editor.putBoolean(ShareUtil.IS_SET_GUARD_PWD, true);
                        editor.putString("guardPwd", MD5Utils.encode(mGuardPwd));
                        editor.commit();

                        Toast.makeText(AdvaceToolsActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        PageUtils.nextPage(AdvaceToolsActivity.this,
                                LockAppActivity.class);
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

                            dgEnterGuardPwd.dismiss();
                            PageUtils.nextPage(AdvaceToolsActivity.this,
                                    LockAppActivity.class);

                    }else{
                        Toast.makeText(AdvaceToolsActivity.this, "您输入的密码错误,请重新输入", Toast.LENGTH_SHORT).show();
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

    /**
     * 程序锁
     * @param view
     */
    public void appLock(View view){
        SharedPreferences.Editor edit = sPre.edit();
        if (cbi_app_lock.isChecked()){
            cbi_app_lock.setChecked(false);
            edit.putBoolean(ShareUtil.IS_APP_LOCK, false);
            stopService(new Intent(this, LockAppService.class));
        }else {
            cbi_app_lock.setChecked(true);
            edit.putBoolean(ShareUtil.IS_APP_LOCK, true);
            startService(new Intent(this,LockAppService.class));
        }
        edit.commit();
    }

}
