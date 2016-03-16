package com.bug.mobilesafe.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;

import com.bug.mobilesafe.activity.PwdActivity;
import com.bug.mobilesafe.database.LockAppDB;

import java.util.List;

public class LockAppService extends Service {

    private boolean isStart;
    private ActivityManager activityManager;
    private LockAppDB lockAppDB;
    private List<String> packageNames;
    private MyReceiver myReceiver;
    private String stopApp = "";
    private String packageName = "";
    private boolean firsttime = false;
    private boolean isScreenOff = false;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        lockAppDB = new LockAppDB(this);
        packageNames = lockAppDB.getLockedApps();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.bug.mobilesafe.lockedapp");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, filter);
        startWatchApp();
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//            System.err.println("-------------------------------------");
            if (action.equals("com.bug.mobilesafe.lockedapp")) {
                if (intent.getIntExtra("status", -1) == 0) {
                    stopApp = intent.getStringExtra("packageName");
                    firsttime = true;
                } else if (intent.getIntExtra("status", -1) == 1) {
                    if (!isScreenOff) {
                        startWatchApp();
                    }
                }
            }

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
//                System.err.println("off");
                isStart = false;
                isScreenOff=true;
            }

            if (action.equals(Intent.ACTION_SCREEN_ON)) {
//                System.err.println("on");
                isScreenOff=false;
                if (isStart == false) {
                    startWatchApp();
                }
            }
        }
    }

    private void startWatchApp() {
        AsyncTask<Object, Object, Object> task = new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {

                isStart = true;
                while (isStart) {
//                    System.err.println("isStart");
                    //当输入完密码后，输密码框没有从栈顶消失就进入循环，因此每次都是密码框栈顶，所以sleep
                    if (firsttime) {
                        firsttime = false;
                        SystemClock.sleep(3000);
                    }

                    SystemClock.sleep(30);
                    List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    packageName = runningTaskInfo.topActivity.getPackageName();
//                    System.err.println("pcka;"+packageName);
                    if (packageNames.contains(packageName)) {
//                        System.err.println(packageName+"===="+stopApp);
//                        System.err.println("first:"+firsttime);
                        if (!stopApp.equals(packageName)) {
                            isStart = false;
                        }
                    }
                }

                return null;
            }


            @Override
            protected void onPostExecute(Object o) {
                inputPwd();
            }
        };

        task.execute();
    }

    private void inputPwd() {
        Intent intent = new Intent(this, PwdActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", packageName);
        packageName = "";
        startActivity(intent);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStart = false;
        unregisterReceiver(myReceiver);
    }
}
