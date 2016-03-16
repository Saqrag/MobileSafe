package com.bug.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by saqra on 2016/2/27.
 */
public class ProcessInfo {

    private Drawable icon;

    private String appName;

    private String packageName;

    private long size;

    private boolean isUserPro;

    private boolean isChecked=false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isUserPro() {
        return isUserPro;
    }

    public void setIsUserPro(boolean isUserPro) {
        this.isUserPro = isUserPro;
    }
}
