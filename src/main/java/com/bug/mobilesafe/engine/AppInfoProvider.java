package com.bug.mobilesafe.engine;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.bug.mobilesafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saqra on 2016/2/21.
 */
public class AppInfoProvider {

    /**
     * 获得应用信息集合
     * @param context
     * @return 应用信息集合
     */
    public static List<AppInfo> getAppInfos(Context context){
        List<AppInfo> appInfos =new ArrayList<>();

        PackageManager pm = context.getPackageManager();

        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);

        for (PackageInfo packageInfo :
                packageInfos) {

            AppInfo appInfo = new AppInfo();

            appInfo.setPackageName(packageInfo.packageName);

            appInfo.setAppName((String) packageInfo.applicationInfo.loadLabel(pm));

            appInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));

            //获取App占用rom
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file=new File(sourceDir);
            long length = file.length();
            appInfo.setAppSpace(length);


            int flags = packageInfo.applicationInfo.flags;

            //判断系统应用
            if ((flags & ApplicationInfo.FLAG_SYSTEM) !=0){

                //系统应用
                appInfo.setIsUserApp(false);

            }else {

                //用户应用
                appInfo.setIsUserApp(true);

            }

            //判断内存上还是sd卡上
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){

                //存在sd卡
                appInfo.setIsInnerStorage(false);

            }else {

                //手机内存
                appInfo.setIsInnerStorage(true);
            }

            appInfos.add(appInfo);

        }

        return appInfos;
    }
}
