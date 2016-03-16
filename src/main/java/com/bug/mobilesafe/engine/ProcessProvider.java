package com.bug.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.bean.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saqra on 2016/2/27.
 */
public class ProcessProvider {

    public static List<ProcessInfo> getProcesses(Context context) {
        List<ProcessInfo> processInfos =new ArrayList<>();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        PackageManager packageManager = context.getPackageManager();

        for (ActivityManager.RunningAppProcessInfo info :
                runningAppProcesses) {

            ProcessInfo processInfo = new ProcessInfo();

            String packageName = info.processName;

            processInfo.setPackageName(packageName);

            Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{info.pid});

            long totalPrivateDirty = processMemoryInfo[0].getTotalPrivateDirty() * 1024;

            processInfo.setSize(totalPrivateDirty);

            try {


                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);

                int flags = applicationInfo.flags;

                if ((flags & ApplicationInfo.FLAG_SYSTEM )!= 0){

                    //系统应用
                    processInfo.setIsUserPro(false);

                }else {

                    processInfo.setIsUserPro(true);
                }
                String appName = (String) applicationInfo.loadLabel(packageManager);

                processInfo.setAppName(appName);

                Drawable icon = applicationInfo.loadIcon(packageManager);

                processInfo.setIcon(icon);



            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

                //系统核心应用没有图标
                processInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));

                processInfo.setAppName(packageName);

                processInfo.setIsUserPro(false);
            }

//            System.out.println("============================");
//            System.out.println(totalPrivateDirty);
            processInfos.add(processInfo);

        }


        return processInfos;

    }

}
