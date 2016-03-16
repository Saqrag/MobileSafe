package com.bug.mobilesafe.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.database.AntiVirusDB;
import com.bug.mobilesafe.receiver.AdminReceiver;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.StreamUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


/**
 * Created by saqra on 2016/1/27.
 */
public class SplashActivity extends Activity {
    TextView tvVersion;



    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
                    break;
            }
        }
    };
    private DevicePolicyManager mDPM;
    private ComponentName componentName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //获取设备策略服务
        mDPM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        //获得管理组件
        componentName = new ComponentName(this, AdminReceiver.class);

        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("Version:" + getVersionName());

        createShotCut();//创建快捷方式
        copyDB("antivirus.db");
        copyDB("address.db");
//        AntiVirusDB.addVirus(this, "2ce4d54c1b3f98d6b59926f14bac461d", "病毒快删除");
//        AntiVirusDB.addVirus(this, "eb96be2bba633058087aab5393eb1174", "病毒快删除");
//        AntiVirusDB.addVirus(this, "80f90ebd6cace4a7586448978866410b", "病毒快删除");
//        AntiVirusDB.addVirus(this, "3140542ac4f091a125ad6d728e64b5d9", "病毒快删除");

        PageUtils.nextPage(this, HomeActivity.class);


    }

    private void createShotCut() {

        /**
         * com.android.launcher.action.INSTALL_SHORTCUT
         * Intent.EXTRA_SHORTCUT_NAME
         * Intent.EXTRA_SHORTCUT_INTENT
         */

        Intent data = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        data.putExtra(Intent.EXTRA_SHORTCUT_NAME,"萨卫士");
        data.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));

        //只能创建一个
        data.putExtra("duplicate",false);

        Intent intent=new Intent("com.bug.home_activity");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        data.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);
        sendBroadcast(data);

    }

    private void checkDefultSms() {
        final String myPackageName = getPackageName();
        if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) { // App is not default. // Show the "not currently set as the default SMS app" interface
            initPolicyManager();
            return;

        }

        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
        startActivityForResult(intent, 1);
    }

    private void initPolicyManager() {
        if (mDPM.isAdminActive(componentName)) {
            checkUpdate();
            return;
        }

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "您必须手动激活管理员权限");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (mDPM.isAdminActive(componentName)) {
                checkUpdate();
                return;
            }

            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "您必须手动激活管理员权限");
            startActivityForResult(intent, 0);
        } else if (requestCode == 1) {
            final String myPackageName = getPackageName();
            if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) { // App is not default. // Show the "not currently set as the default SMS app" interface
                initPolicyManager();
                return;
            }
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
            startActivityForResult(intent, 0);
        }

    }

    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager
                    .getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Not found";
    }

    private void checkUpdate() {
        copyDB("address.db");
        final Message msg = new Message();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                }
//                try {
//                    URL url = new URL("http://10.0.2.2:8080/update.json");
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setConnectTimeout(5000);
//                    conn.setReadTimeout(5000);
//                    conn.connect();
//
//
//                    int responseCode = conn.getResponseCode();
//                    if (responseCode == 200) {
//                        InputStream inputStream = conn.getInputStream();
//                        String versionMessage = StreamUtil.getStream(inputStream);
//                        JSONObject js = new JSONObject(versionMessage);
//                        int mVersionCode = js.getInt("versionCode");
//                        String mVersionName = js.getString("versionName");
//                        String mDiscription = js.getString("discription");
//                        String mDownloadUrl = js.getString("downloadUrl");
//                        System.out.println(mVersionName + ";" +
//                                mDiscription + ";" + mDownloadUrl + ";" + mVersionCode);
//                    }
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (ProtocolException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }


        }.start();

    }

    private void copyDB(String dbName) {
        File dbAdress = new File(getFilesDir(), dbName);
        if (dbAdress.exists()) {
            System.out.println("this file exist");
            return;
        }
        InputStream in = null;
        FileOutputStream out = null;

        try {
            in = getAssets().open(dbName);
            out = new FileOutputStream(dbAdress);

            byte[] buffer = new byte[1024];

            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
