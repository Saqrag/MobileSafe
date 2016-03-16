package com.bug.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.bug.mobilesafe.R;

/**
 * Created by saqra on 2016/2/1.
 */
public class PageUtils {

    /**
     * 无数据传递的启动下一个Activity
     * @param activity
     * @param cls
     */
    public static void nextPage(Activity activity,Class<?> cls){
        Intent intent = new Intent(activity,cls);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.trans_in, R.anim.trans_out);//动画
    }

    /**
     * 无数据传递的启动下一个Activity
     * @param activity
     * @param cls
     */
    public static void previousPage(Activity activity,Class<?> cls){
        Intent intent = new Intent(activity,cls);
        activity.startActivity(intent);
        activity.finish();
        activity.overridePendingTransition(R.anim.previous_in, R.anim.previous_out);//动画
    }
}
