package com.bug.mobilesafe.rocketutil;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.text.format.Formatter;

import com.bug.mobilesafe.bean.ProcessInfo;
import com.bug.mobilesafe.engine.ProcessProvider;
import com.bug.mobilesafe.utils.UIUtils;

import java.util.List;

/**
 * Created by saqra on 2016/2/28.
 */
public class AssistActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(){
            @Override
            public void run() {
                super.run();

                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ProcessInfo> processes = ProcessProvider.getProcesses(AssistActivity.this);
                int count = 0;
                long killMem = 0;
                for (ProcessInfo info : processes) {
                    count++;
                    killMem += info.getSize();
                    activityManager.killBackgroundProcesses(info.getPackageName());
                }

                UIUtils.toast(AssistActivity.this, "共杀死了" + count + "进程,清除了" +
                        Formatter.formatFileSize(AssistActivity.this, killMem) +
                        "内存");

                finish();


            }
        }.start();
    }
}
