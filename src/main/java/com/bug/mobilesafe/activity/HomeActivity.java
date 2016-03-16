package com.bug.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.services.LocationService;
import com.bug.mobilesafe.utils.MD5Utils;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ShareUtil;

/**
 * Created by saqra on 2016/1/28.
 */
public class HomeActivity extends Activity {
    GridView gvHome;

    String mFuctionName[] = {"", "", "",
            "", "","",
            "", "", ""};
    int mFuctionIcon[] = {R.drawable.home_safe, R.drawable.home_callmsgsafe,
            R.drawable.home_apps, R.drawable.home_taskmanager,
            R.drawable.home_netmanager, R.drawable.home_trojan,
            R.drawable.home_sysoptimize, R.drawable.home_tools,
            R.drawable.home_settings};
    private SharedPreferences sPre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new MyAdapter());
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view, int position, long id) {
                switch (position) {
                    //手机防盗
                    case 0:
                        initGuard();
                        break;

                    //通讯卫士
                    case 1:
                        PageUtils.nextPage(HomeActivity.this,CallSafeActivity.class);
                        break;

                    //软件管理
                    case 2:
                        PageUtils.nextPage(HomeActivity.this,SoftActivity.class);
                        break;

                    //进程管理
                    case 3:

                        PageUtils.nextPage(HomeActivity.this,ProcessActivity.class);

                        break;
                    case 4:
                        break;

                    //手机杀毒
                    case 5:
                        PageUtils.nextPage(HomeActivity.this,AntiVirus.class);
                        break;
                    case 6:

                        //
                        PageUtils.nextPage(HomeActivity.this,CacheActivity.class);

                        break;
                    case 7:

                        //go to AdvanceActivity
                        PageUtils.nextPage(HomeActivity.this,AdvaceToolsActivity.class);
                        break;
                    case 8:
                        PageUtils.nextPage(HomeActivity.this, SettingActivity.class);
                        break;
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFuctionName.length;
        }

        @Override
        public Object getItem(int position) {
            return mFuctionIcon[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = View.inflate(HomeActivity.this, R.layout.item_gv_home, null);
            ImageView ivItemGV = (ImageView) v.findViewById(R.id.iv_item_gv);
            AssetManager assets = getAssets();
            Typeface typeface = Typeface.createFromAsset(assets, "fonts/MenksoftQagan.ttf");

            TextView tvItemGV = (TextView) v.findViewById(R.id.tv_item_gv);
            tvItemGV.setTypeface(typeface);
            tvItemGV.setText(mFuctionName[position]);
            ivItemGV.setImageResource(mFuctionIcon[position]);
            return v;
        }
    }

    /**
     * 跳转手机防盗Activity及之前的密码设置方法
     */
    private void initGuard() {
        sPre = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);
        String sGuardPwd = sPre.getString("guardPwd", "");

        if (TextUtils.isEmpty(sGuardPwd)) {  //判断是否已经设置密码
            final View setGuardPwdItem = View.inflate(HomeActivity.this, R.layout.item_set_guard_pwd, null);
            final AlertDialog.Builder guardPwd = new AlertDialog.Builder(HomeActivity.this);
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
                        Toast.makeText(HomeActivity.this, "密码不能为空,请重新输入!", Toast.LENGTH_SHORT).show();
                    } else if (!mGuardPwd.equals(mConfirmGuardPwd)) {
                        Toast.makeText(HomeActivity.this, "两次密码不一致,请重新输入", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences.Editor editor = sPre.edit();
                        editor.putBoolean(ShareUtil.IS_SET_GUARD_PWD, true);
                        editor.putString("guardPwd", MD5Utils.encode(mGuardPwd));
                        editor.commit();

                        Toast.makeText(HomeActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        PageUtils.nextPage(HomeActivity.this,
                                Guide1_Welcome.class);
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
            final View enterGuardPwdItem = View.inflate(HomeActivity.this, R.layout.item_enter_guard_pwd, null);
            final AlertDialog.Builder brEnterGuardPwd = new AlertDialog.Builder(HomeActivity.this);
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

                        //判断是否进入向导页
                        if(!sPre.getBoolean(ShareUtil.IS_CONFIG,false)) {
                            dgEnterGuardPwd.dismiss();
                            PageUtils.nextPage(HomeActivity.this,
                                    Guide1_Welcome.class);
                        }else {
                            dgEnterGuardPwd.dismiss();
                            PageUtils.nextPage(HomeActivity.this,
                                    GuardActivity.class);
                        }
                    }else{
                        Toast.makeText(HomeActivity.this, "您输入的密码错误,请重新输入", Toast.LENGTH_SHORT).show();
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
}
