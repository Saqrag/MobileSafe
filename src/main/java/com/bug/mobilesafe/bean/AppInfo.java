package com.bug.mobilesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by saqra on 2016/2/21.
 */
public class AppInfo {

    private String appName;

    private String packageName;

    private Drawable appIcon;

    private long appSpace;

    private boolean isUserApp;

    private boolean isInnerStorage;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public long getAppSpace() {
        return appSpace;
    }

    public void setAppSpace(long appSpace) {
        this.appSpace = appSpace;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setIsUserApp(boolean isUserApp) {
        this.isUserApp = isUserApp;
    }

    public boolean isInnerStorage() {
        return isInnerStorage;
    }

    public void setIsInnerStorage(boolean isInnerStorage) {
        this.isInnerStorage = isInnerStorage;
    }
}
