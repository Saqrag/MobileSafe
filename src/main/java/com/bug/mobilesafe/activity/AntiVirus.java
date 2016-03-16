package com.bug.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.database.AntiVirusDB;
import com.bug.mobilesafe.utils.MD5Utils;
import com.bug.mobilesafe.utils.PageUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saqra on 2016/2/29.
 */
public class AntiVirus extends BaseActivity {

    private static final int START = 0;
    private static final int SCANING = 1;
    private static final int FINISH = 2;

    List<AppInfo> viruses=new ArrayList<>();
    @ViewInject(R.id.iv_scan)
    ImageView iv_scan;

    @ViewInject(R.id.tv_status)
    TextView tv_status;

    @ViewInject(R.id.ll_anti_virus)
    LinearLayout ll_anti_virus;

    @ViewInject(R.id.progressBar)
    ProgressBar progressBar;

    @ViewInject(R.id.sv_antivirus)
    ScrollView sv_antivirus;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case START:
                    tv_status.setText("正在初始化8核处理器");
                    break;

                case SCANING:
                    tv_status.setText("正在8核查杀");
                    TextView textView=new TextView(AntiVirus.this);
                    AppInfo appInfo = (AppInfo) msg.obj;
                    if (appInfo.isVirus){
                        textView.setText("病毒 "+appInfo.appName);
                        textView.setTextColor(Color.RED);
                        viruses.add(appInfo);
                    }else {
                        textView.setText("安全 "+appInfo.appName);
                        textView.setTextColor(Color.BLUE);
                    }

                    ll_anti_virus.addView(textView);

                    sv_antivirus.fullScroll(View.FOCUS_DOWN);
                    break;

                case FINISH:
                    if (viruses.size()==0) {
                        tv_status.setTextColor(Color.BLACK);
                        tv_status.setText("查杀完成,您的手机安全");
                    }else {
                        tv_status.setTextColor(Color.RED);
                        tv_status.setText("共查到"+viruses.size()+"个病毒,请您马上卸载");
                    }
                    break;
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        ViewUtils.inject(this);

        initUI();
        antiVirus();
    }

    private void antiVirus() {
        new Thread(){
            @Override
            public void run() {

                Message msg = Message.obtain();

                //Start
                msg.what = START;
                handler.sendMessage(msg);

                /**
                 * 必杀技
                 */
                SystemClock.sleep(1000);

                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                progressBar.setMax(installedPackages.size());
                int progress=0;
                for (PackageInfo info :
                        installedPackages) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.appName = (String) info.applicationInfo.loadLabel(packageManager);
                    appInfo.packageName = info.applicationInfo.packageName;
                    String sourceDir = info.applicationInfo.sourceDir;
                    String md5 = MD5Utils.getAppMd5(sourceDir);
                    System.err.println("md5:"+md5);
                    String desc = AntiVirusDB.getDesc(AntiVirus.this, md5);

                    if (TextUtils.isEmpty(desc.trim())) {
                        appInfo.isVirus = false;
                    } else {
                        appInfo.isVirus = true;
                    }

                    progress++;
                    progressBar.setProgress(progress);

                    //Scanning
                    msg = Message.obtain();
                    msg.obj = appInfo;
                    msg.what = SCANING;
                    handler.sendMessage(msg);

                }

                //finish
                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);

            }
        }.start();



    }

    class AppInfo {
        public String packageName;
        public String appName;
        public boolean isVirus;
    }

    private void initUI() {

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);

        //设置无限次数重复
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        iv_scan.setAnimation(rotateAnimation);


    }


    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this, HomeActivity.class);
    }
}
