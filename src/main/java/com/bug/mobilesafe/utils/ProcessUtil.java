package com.bug.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by saqra on 2016/2/27.
 */
public class ProcessUtil {

    public static int getProCount(Context context){

        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        //get Progress count
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();

        return runningAppProcesses.size();
    }
}
