package com.bug.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by saqra on 2016/2/27.
 */
public class MemoryUtil {

    public static long getAvailMem(Context context){

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        am.getMemoryInfo(memoryInfo);

        return memoryInfo.availMem;

    }


    public static long getTotalMem(){

        //get totalMemory
        try {

            //"/proc/meminfo"这里保存着手机的相关信息
            FileInputStream fis = new FileInputStream(new File("/proc/meminfo"));

            BufferedReader buf = new BufferedReader(new InputStreamReader(fis));

            String s = buf.readLine();

            StringBuffer sb = new StringBuffer();

            for (char c :
                    s.toCharArray()) {

                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }

            }

            return Long.parseLong(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;

    }
}
