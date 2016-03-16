package com.bug.mobilesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.bean.AppInfo;
import com.bug.mobilesafe.engine.AppInfoProvider;
import com.bug.mobilesafe.utils.PageUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by saqra on 2016/2/21.
 */
public class SoftActivity extends BaseActivity implements View.OnClickListener{

    @ViewInject(R.id.tv_rom)
    TextView tv_rom;
    @ViewInject(R.id.tv_sdcard)
    TextView tv_sdcard;
    @ViewInject(R.id.lv_soft)
    ListView lv_soft;
    @ViewInject(R.id.tv_is_user_app)
    TextView tv_is_user_app;
    @ViewInject(R.id.rl_pb)
    RelativeLayout rl_pb;
    private List<AppInfo> appInfos;
    private ArrayList<AppInfo> userApps;
    private ArrayList<AppInfo> systemApps;
    private PopupWindow popupWindow;
    private MySoftAdapter mySoftAdapter;
    private AppInfo clickAppInfo;
    private UninstallReceiver recevier;

    /**
     * 进入下一页,监听手机左滑
     */
    @Override
    public void showNextPage() {

    }


    /**
     * 返回前一页,监听手机右滑和返回键
     */
    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this, HomeActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soft);
        ViewUtils.inject(this);

        initUI();

        initData();

        setListener();

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void setListener() {

        //滑动监听
        lv_soft.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                dismisPop();

                if (userApps != null) {
                    if (firstVisibleItem > userApps.size()) {
                        tv_is_user_app.setText("系统应用:" + systemApps.size() + "个");

                    } else {
                        tv_is_user_app.setText("用户应用:" + userApps.size() + "个");
                    }
                }

            }
        });


        //listview点击监听
        lv_soft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                dismisPop();

                Object obj = mySoftAdapter.getItem(position);
                if (obj != null && obj instanceof AppInfo) {
                    clickAppInfo = (AppInfo) obj;

                View contentview = View.inflate(SoftActivity.this, R.layout.item_pop, null);

                //设置监听
                LinearLayout ll_delete = (LinearLayout) contentview.findViewById(R.id.ll_delete);
                LinearLayout ll_run = (LinearLayout) contentview.findViewById(R.id.ll_run);
                LinearLayout ll_message = (LinearLayout) contentview.findViewById(R.id.ll_message);
                LinearLayout ll_share = (LinearLayout) contentview.findViewById(R.id.ll_share);
                ll_delete.setOnClickListener(SoftActivity.this);
                ll_run.setOnClickListener(SoftActivity.this);
                ll_message.setOnClickListener(SoftActivity.this);
                ll_share.setOnClickListener(SoftActivity.this);

                popupWindow = new PopupWindow(contentview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                //设置动画,必需设置背景才可以,所以设置透明背景
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(view, Gravity.LEFT + Gravity.TOP, 100, location[1]);

                //设置动画,从小到大
                ScaleAnimation sa = new ScaleAnimation(0.5f, 1f, 0.5f, 1f);
                sa.setDuration(200);
                contentview.setAnimation(sa);
                }

            }


        });

    }

    @Override
    public void onClick(View v) {
//        System.out.println("单击事件生效了");

        switch (v.getId()){

            //卸载
            case R.id.ll_delete:
                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + clickAppInfo.getPackageName()));
                startActivity(uninstall_localIntent);

                //register Uninstall App receiver
                recevier = new UninstallReceiver();
                IntentFilter intentFilter=new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
                intentFilter.addDataScheme("package");
                registerReceiver(recevier, intentFilter);
                dismisPop();
                break;

            //运行
            case R.id.ll_run:
                Intent start_localIntent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getPackageName());
                this.startActivity(start_localIntent);
                dismisPop();
                break;

            //详情
            case R.id.ll_message:
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + clickAppInfo.getPackageName()));
                startActivity(detail_intent);
                dismisPop();
                break;

            //分享
            case R.id.ll_share:
                Intent share_localIntent = new Intent("android.intent.action.SEND");
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                share_localIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickAppInfo.getAppName()+"下载地址:"+"https://play.google.com/store/apps/details?id="+clickAppInfo.getPackageName());
                this.startActivity(Intent.createChooser(share_localIntent, "分享"));
                dismisPop();
                break;

        }

    }

    private void dismisPop() {
        if (popupWindow!=null){
            popupWindow.dismiss();
        }
    }


    /**
     * 获取应用信息
     */
    private void initData() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                appInfos = AppInfoProvider.getAppInfos(SoftActivity.this);
                userApps=new ArrayList<>();
                systemApps=new ArrayList<>();

                for (AppInfo appInfo : appInfos) {

                    //判断是否用户应用
                    if (appInfo.isUserApp()) {
                        userApps.add(appInfo);
                    }else {
                        systemApps.add(appInfo);
                    }
                }


                handler.sendEmptyMessage(0);
            }
        }.start();
    }



    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mySoftAdapter = new MySoftAdapter();
            lv_soft.setAdapter(mySoftAdapter);
            rl_pb.setVisibility(View.GONE);
            tv_is_user_app.setVisibility(View.VISIBLE);
        }
    };



    private class MySoftAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return appInfos.size();
        }

        @Override
        public Object getItem(int position) {

            if (position==0){
                return null;
            }else if (position==userApps.size()+1){
                return null;
            }

            if (position<userApps.size()+1){
                return userApps.get(position-1);
            }else {
                return systemApps.get(position-userApps.size()-2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (position==0){
                //用户应用
                TextView tv=new TextView(SoftActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xFF888888);
                tv.setText("用户应用:"+userApps.size()+"个");
                return tv;
            }else if (position==userApps.size()+1){
                //系统应用
                TextView tv=new TextView(SoftActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xFF888888);
                tv.setText("系统应用:"+systemApps.size()+"个");
                return tv;
            }

            if (convertView==null||convertView instanceof TextView){

                view=View.inflate(SoftActivity.this, R.layout.item_soft,null);
                Holder holder=new Holder();
                holder.iv_app_icon= (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_app_name= (TextView) view.findViewById(R.id.tv_app_name);
                holder.tv_is_inner_space= (TextView) view.findViewById(R.id.tv_is_inner_space);
                holder.tv_app_size= (TextView) view.findViewById(R.id.tv_app_size);
                view.setTag(holder);

            }else {

                view = convertView;

            }

            AppInfo appInfo;
            if (position>userApps.size()+1) {
                appInfo = systemApps.get(position - userApps.size() - 2);
            }else {
                appInfo=userApps.get(position-1);
            }
            Holder holder = (Holder) view.getTag();

            //设置背景,版本要判断
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.iv_app_icon.setBackground(appInfo.getAppIcon());
            }else {
                holder.iv_app_icon.setBackgroundDrawable(appInfo.getAppIcon());
            }

            holder.tv_app_name.setText(appInfo.getAppName());

            if (appInfo.isInnerStorage()){
                holder.tv_is_inner_space.setText("手机内存");
            }else{
                holder.tv_is_inner_space.setText("sd卡");
            }

            holder.tv_app_size.setText(Formatter.formatFileSize(SoftActivity.this, appInfo.getAppSpace()));

            return view;
        }

        class Holder{
            ImageView iv_app_icon;
            TextView tv_app_name;
            TextView tv_is_inner_space;
            TextView tv_app_size;
        }
    }

    private void initUI() {

        rl_pb.setVisibility(View.VISIBLE);
        tv_is_user_app.setVisibility(View.INVISIBLE);

        //获得rom余量
        long romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        //获得rom总空间
        long romTotalSpace = Environment.getDataDirectory().getTotalSpace();
//        System.out.println("romFreeSpace"+romFreeSpace);
//        System.out.println("romTotalSpace" + romTotalSpace);
        tv_rom.setText("内存:"+Formatter.formatFileSize(this, romFreeSpace));

        //获得sd卡余量
        long sdFreeSpace = Environment.getExternalStorageDirectory().getFreeSpace();
        //获得sd卡总量
        long sdTotalSpace = Environment.getExternalStorageDirectory().getTotalSpace();
        tv_sdcard.setText("sd卡:"+Formatter.formatFileSize(this, sdFreeSpace));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismisPop();
    }

    private class UninstallReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getDataString();

            if (packageName.equals("package:"+clickAppInfo.getPackageName())) {
                userApps.remove(clickAppInfo);
                mySoftAdapter.notifyDataSetChanged();
                context.unregisterReceiver(recevier);
            }
        }
    }
}
