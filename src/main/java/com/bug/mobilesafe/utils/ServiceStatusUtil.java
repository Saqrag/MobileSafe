package com.bug.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by saqra on 2016/2/16.
 */
public class ServiceStatusUtil {
    public static Boolean isRunning(Context context,String packageName){
        ActivityManager activityManager= (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningService: runningServices) {
            String className = runningService.service.getClassName();
            if (className.equals(packageName)){
                return true;
            }
        }


        return false;
    }
}
