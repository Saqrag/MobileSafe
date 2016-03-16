package com.bug.mobilesafe.activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.bean.AppInfo;
import com.bug.mobilesafe.engine.AppInfoProvider;
import com.bug.mobilesafe.utils.PageUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saqra on 2016/3/14.
 */
public class CacheActivity extends BaseActivity {
    @ViewInject(R.id.lv_cache)
    ListView lv_cache;

    int scanCount = 0;
    int appCount = 0;

    List<CacheInfo> cacheInfos = new ArrayList<>();
    private ProgressDialog progress;
    private ClearCacheObserver mClearCacheObserver;
    private PackageManager pm;
    private List<CacheInfo> clearCaches;

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        ViewUtils.inject(this);
        initData();
        setListener();

    }

    private void setListener() {
        lv_cache.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CacheInfo cacheInfo = cacheInfos.get(position);
                if (cacheInfo.isChecked) {
                    cacheInfo.setIsChecked(false);
                } else {
                    cacheInfo.setIsChecked(true);
                }

                cacheAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {

        progress = new ProgressDialog(CacheActivity.this);
        progress.setMessage("正在加载。。。。");
        progress.show();

        new Thread() {
            @Override
            public void run() {
                pm = getPackageManager();
                List<AppInfo> appInfos = AppInfoProvider.getAppInfos(CacheActivity.this);
                appCount = appInfos.size();
                for (AppInfo appInfo :
                        appInfos) {
                    try {
                        Method method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
                        method.invoke(pm, appInfo.getPackageName(), new MyObserver(appInfo));
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }




            }
        }.start();

    }

    private CacheAdapter cacheAdapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                scanCount++;
                if (scanCount >= appCount) {

                    cacheAdapter = new CacheAdapter();
                    lv_cache.setAdapter(cacheAdapter);
                    progress.dismiss();
                }
            } else if (msg.what == 1) {
                scanCount++;
                if (scanCount >= appCount) {
                    cacheAdapter.notifyDataSetChanged();
                    progress.dismiss();
                }
            }
        }
    };

    class CacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cacheInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                view = View.inflate(CacheActivity.this, R.layout.item_cache, null);
                holder = new ViewHolder();
                holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.tv_size = (TextView) view.findViewById(R.id.tv_size);
                holder.cb_process = (CheckBox) view.findViewById(R.id.cb_process);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            CacheInfo cacheInfo = cacheInfos.get(position);
            holder.iv_app_icon.setImageDrawable(cacheInfo.getIcon());
            holder.tv_app_name.setText(cacheInfo.getAppName());
            holder.tv_size.setText(Formatter.formatFileSize(CacheActivity.this, cacheInfo.getCacheSize()));
            holder.cb_process.setChecked(cacheInfo.isChecked);


            return view;
        }
    }

    class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_size;
        CheckBox cb_process;
    }

    class MyObserver extends IPackageStatsObserver.Stub {
        AppInfo mAppInfo;

        public MyObserver(AppInfo appInfo) {
            mAppInfo = appInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
//            if (pStats.cacheSize > 0) {
//                System.err.println("AppName:" + mAppInfo.getAppName() + "-----Size:" +
//                        Formatter.formatFileSize(CacheActivity.this, pStats.cacheSize));
            CacheInfo cacheInfo = new CacheInfo();
            cacheInfo.setAppName(mAppInfo.getAppName());
            cacheInfo.setPackageName(mAppInfo.getPackageName());
            cacheInfo.setIcon(mAppInfo.getAppIcon());
            cacheInfo.setCacheSize(pStats.cacheSize);

            cacheInfos.add(cacheInfo);

            handler.sendEmptyMessage(0);
//            }
        }
    }


    private class CacheInfo {
        private String appName = "默认";
        private String packageName = "默认";
        private Drawable icon;
        private boolean isChecked = false;

        public boolean isChecked() {
            return isChecked;
        }

        public void setIsChecked(boolean isChecked) {
            this.isChecked = isChecked;
        }

        long cacheSize = 0;

        public long getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(long cacheSize) {
            this.cacheSize = cacheSize;
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

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }


    }

    class ClearCacheObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            System.err.println("清除");
            handler.sendEmptyMessage(1);

        }
    }

    public void clear(View view) {
        progress.setMessage("正在清除");
        progress.show();
        scanCount = 0;
        new Thread() {
            @Override
            public void run() {
                clearCaches = new ArrayList<>();
                for (CacheInfo cacheInfo :
                        cacheInfos) {
                    if (cacheInfo.isChecked) {
                        clearCaches.add(cacheInfo);
                    }

                }

                appCount=clearCaches.size();

                for (CacheInfo cacheInfo :
                        clearCaches) {

                    if (mClearCacheObserver == null) {
                        mClearCacheObserver = new ClearCacheObserver();
                    }

                    try {
                        Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                        method.invoke(pm, cacheInfo.getPackageName(), mClearCacheObserver);
                        cacheInfos.remove(cacheInfo);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                clearCaches.clear();

            }



    }.start();
}

}

