package com.bug.mobilesafe.activity;

import android.app.Activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.bean.ProcessInfo;
import com.bug.mobilesafe.engine.ProcessProvider;
import com.bug.mobilesafe.utils.MemoryUtil;
import com.bug.mobilesafe.utils.PageUtils;
import com.bug.mobilesafe.utils.ProcessUtil;
import com.bug.mobilesafe.utils.SmsUtil;
import com.bug.mobilesafe.utils.UIUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ProcessActivity extends BaseActivity {
    @ViewInject(R.id.tv_process)
    TextView tv_process;

    @ViewInject(R.id.tv_memory)
    TextView tv_memory;

    @ViewInject(R.id.lv_process)
    ListView lv_process;
    private List<ProcessInfo> processes;
    private List<ProcessInfo> userPro;
    private List<ProcessInfo> sysPro;
    private ProgressDialog progressDialog;
    private ProAdapter proAdapter;
    private long availMem;
    private int progressCount;
    private long totalMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        ViewUtils.inject(this);

        initUI();

        initList();

    }

    @Override
    public void showNextPage() {

    }

    @Override
    public void showPreviousPage() {
        PageUtils.previousPage(this,HomeActivity.class);
    }

    private void initList() {

        new Thread() {
            @Override
            public void run() {

                processes = ProcessProvider.getProcesses(ProcessActivity.this);

                userPro = new ArrayList<>();
                sysPro = new ArrayList<>();

                for (ProcessInfo info :
                        processes) {
                    if (info.isUserPro()) {
                        userPro.add(info);
                    } else {
                        sysPro.add(info);
                    }

                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        proAdapter = new ProAdapter();

                        lv_process.setAdapter(proAdapter);

                        progressDialog.dismiss();

                    }
                });

            }
        }.start();

    }

    private void initUI() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载");
        progressDialog.show();

        progressCount = ProcessUtil.getProCount(this);

        tv_process.setText("运行" + String.valueOf(progressCount) + "进程运行");

        availMem = MemoryUtil.getAvailMem(this);

        totalMem = MemoryUtil.getTotalMem();

//        long totalMem = memoryInfo.totalMem;//这个不支持低版本手机

        tv_memory.setText("剩余/内存:" + Formatter.formatFileSize(this, availMem) +
                "/" + Formatter.formatFileSize(this, totalMem));
//        System.out.println("haha");

        /**
         * 必需设置CheckBoX 无焦点,无单击监听
         */
        lv_process.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                System.out.println("1");
                Object obj = lv_process.getItemAtPosition(position);

                if (obj != null && obj instanceof ProcessInfo) {
//                    System.out.println("2");

                    ProcessInfo processInfo = (ProcessInfo) obj;

                    //把自己去掉
                    if (processInfo.getPackageName().equals(getPackageName())){
                        return;
                    }


                    ViewHolder holder = (ViewHolder) view.getTag();
                    if (processInfo.isChecked()) {
                        processInfo.setIsChecked(false);
                        holder.cb_process.setChecked(false);
                    } else {
//                        System.out.println("3");
                        processInfo.setIsChecked(true);
                        holder.cb_process.setChecked(true);
                    }
                }
            }
        });

    }

    private class ProAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userPro.size() + sysPro.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userPro.size() + 1) {
                return null;
            }

            if (position < userPro.size() + 1) {
                return userPro.get(position - 1);
            } else {
                return sysPro.get(position - userPro.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                //用户应用
                TextView tv = new TextView(ProcessActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xFF888888);
                tv.setText("用户应用:" + userPro.size() + "个");
                return tv;
            } else if (position == userPro.size() + 1) {
                //系统应用
                TextView tv = new TextView(ProcessActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xFF888888);
                tv.setText("系统应用:" + sysPro.size() + "个");
                return tv;
            }

            ViewHolder holder;

            if (convertView == null || convertView instanceof TextView) {
                convertView = View.inflate(ProcessActivity.this, R.layout.item_process, null);
                holder = new ViewHolder();
                holder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                holder.cb_process = (CheckBox) convertView.findViewById(R.id.cb_process);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position < userPro.size() + 1) {
                ProcessInfo processInfo=userPro.get(position - 1);
                holder.iv_app_icon.setImageDrawable(processInfo.getIcon());
                holder.tv_app_name.setText(processInfo.getAppName());
                holder.tv_size.setText(Formatter.formatFileSize(ProcessActivity.this, processInfo.getSize()));

                if (processInfo.getPackageName().equals(getPackageName())){
                    holder.cb_process.setChecked(false);
                    holder.cb_process.setVisibility(View.INVISIBLE);
//                    System.out.println("隐藏");
                } else{
                    holder.cb_process.setChecked(processInfo.isChecked());
                    holder.cb_process.setVisibility(View.VISIBLE);
                }

            } else {
                holder.iv_app_icon.setImageDrawable(sysPro.get(position - userPro.size() - 2).getIcon());
                holder.tv_app_name.setText(sysPro.get(position - userPro.size() - 2).getAppName());
                holder.tv_size.setText(Formatter.formatFileSize(ProcessActivity.this, sysPro.get(position - userPro.size() - 2).getSize()));
                holder.cb_process.setChecked(sysPro.get(position - userPro.size() - 2).isChecked());
                holder.cb_process.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_size;
        CheckBox cb_process;
    }


    public void selectAll(View view){
        for (ProcessInfo info :
                userPro) {

            if (info.getPackageName().equals(getPackageName())){
                continue;
            }

            info.setIsChecked(true);
        }

        for (ProcessInfo info :
                sysPro) {
            info.setIsChecked(true);
        }

        proAdapter.notifyDataSetChanged();
    }

    public void selectOppsing(View view){
        for (ProcessInfo info :
                userPro) {

            if (info.getPackageName().equals(getPackageName())){
                continue;
            }

            if (info.isChecked()){
                info.setIsChecked(false);
            }else {
                info.setIsChecked(true);
            }
        }

        for (ProcessInfo info :
                sysPro) {
            if (info.isChecked()) {
                info.setIsChecked(false);
            }else {
                info.setIsChecked(true);
            }
        }

        proAdapter.notifyDataSetChanged();
    }

    public void clear(View view){
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<ProcessInfo> killInfo=new ArrayList<>();
        for (ProcessInfo info :
                userPro) {
            if (info.getPackageName().equals(getPackageName())){
                continue;
            }

            if (info.isChecked()){
                killInfo.add(info);
            }
        }

        for (ProcessInfo info:
                sysPro){
            if (info.isChecked()){
                killInfo.add(info);
            }
        }


        int count=0;
        long killMem=0;
        for (ProcessInfo info :
                killInfo) {

            System.err.println("清理");
            killMem +=info.getSize();
            activityManager.killBackgroundProcesses(info.getPackageName());
            count++;

//            try {
//                Method method=Class.forName("android.app.ActivityManager").getMethod("forceStopPackage",String.class);
//                method.invoke(activityManager,info.getPackageName());
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
        }

        userPro.removeAll(killInfo);
        sysPro.removeAll(killInfo);

        proAdapter.notifyDataSetChanged();

        UIUtils.toast(ProcessActivity.this, "共杀死了" + count + "进程,清理了"
                + Formatter.formatFileSize(ProcessActivity.this, killMem) + "内存");

        tv_process.setText("运行" + String.valueOf(progressCount - count) + "进程运行");
        tv_memory.setText("剩余/内存:" + Formatter.formatFileSize(this, availMem - killMem) +
                "/" + Formatter.formatFileSize(this, totalMem));
    }

    public void setting(View view){
        PageUtils.nextPage(this,ProcessSettingActivity.class);
    }
}
