package com.bug.mobilesafe.rocketutil;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.bean.ProcessInfo;
import com.bug.mobilesafe.engine.ProcessProvider;
import com.bug.mobilesafe.utils.UIUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundActivity extends Activity {

    private ImageView ivSmokeBotton;
    private ImageView ivSmokeTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        ivSmokeBotton = (ImageView) findViewById(R.id.iv_smoke_botton);
        ivSmokeTop = (ImageView) findViewById(R.id.iv_smoke_top);

        AlphaAnimation animation=new AlphaAnimation(0,1);
        animation.setDuration(1000);
        animation.setFillAfter(true);

        ivSmokeBotton.setAnimation(animation);
        ivSmokeTop.setAnimation(animation);

        new Thread(){
            @Override
            public void run() {
                super.run();

                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ProcessInfo> processes = ProcessProvider.getProcesses(BackgroundActivity.this);
                int count = 0;
                long killMem = 0;
                for (ProcessInfo info : processes) {
                    count++;
                    killMem += info.getSize();
                    activityManager.killBackgroundProcesses(info.getPackageName());
                }

                UIUtils.toast(BackgroundActivity.this, "共杀死了" + count + "进程,清除了" +
                        Formatter.formatFileSize(BackgroundActivity.this, killMem) +
                        "内存");

                finish();


            }
        }.start();

//        Timer timer = new Timer();
//
//		TimerTask task = new TimerTask() {
//
//			@Override
//			public void run() {
//				// 写我们的业务逻辑
//				ivSmokeBotton.setVisibility(View.INVISIBLE);
//                ivSmokeTop.setVisibility(View.INVISIBLE);
//			}
//		};
//		//进行定时调度
//		/**
//		 * 第一个参数  表示用那个类进行调度
//		 *
//		 * 第二个参数表示时间
//		 */
//		timer.schedule(task, 0,100);

//        startActivity(new Intent(this,AssistActivity.class));

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        },1000);


    }
}
