package com.bug.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bug.mobilesafe.activity.AdvaceToolsActivity;

/**
 * Created by saqra on 2016/2/26.
 */
public class UIUtils {

    public static void toast(final Activity context, final String text) {

        if ((Thread.currentThread().getName()).equals("main")){
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
