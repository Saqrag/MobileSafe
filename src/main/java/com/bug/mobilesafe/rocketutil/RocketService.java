package com.bug.mobilesafe.rocketutil;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.bean.ProcessInfo;
import com.bug.mobilesafe.engine.ProcessProvider;
import com.bug.mobilesafe.utils.UIUtils;

import java.util.List;

/**
 * Created by saqra on 2016/2/19.
 */
public class RocketService extends Service {

    private WindowManager mWM;
    private WindowManager.LayoutParams params;
    private int mWidth;
    private int mHeight;
    private View view;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mWM.updateViewLayout(view, params);
            } else if (msg.what == 1) {
                int count = msg.arg1;
                long killMem = (long) msg.obj;
                Toast.makeText(RocketService.this,
                        "共杀死了" + count + "进程,清除了" +
                                Formatter.formatFileSize(RocketService.this, killMem) + "内存", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Nullable
    @Override


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initView();
        setTouchListener();


    }

    private void setTouchListener() {
        view.setOnTouchListener(new MyTouckListener());
    }

    private void initView() {
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        //获得屏幕的宽高
        mWidth = mWM.getDefaultDisplay().getWidth();
        mHeight = mWM.getDefaultDisplay().getHeight();
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;//设置为与打电话的窗口一样的权限,需要权限android.permission.SYSTEM_ALERT_WINDOW
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT + Gravity.TOP;

        view = View.inflate(this, R.layout.rocket, null);

        ImageView ivRocket = (ImageView) view.findViewById(R.id.iv_rocket);
        AnimationDrawable animation = (AnimationDrawable) ivRocket.getBackground();
        animation.start();
        mWM.addView(view, params);
    }

    @Override
    public void onDestroy() {
        if (mWM != null && view != null) {
            mWM.removeView(view);
            view = null;
        }

//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        List<ProcessInfo> processes = ProcessProvider.getProcesses(RocketService.this);
//        int count = 0;
//        long killMem = 0;
//        for (ProcessInfo info : processes) {
//            count++;
//            killMem += info.getSize();
//            activityManager.killBackgroundProcesses(info.getPackageName());
//        }
//
//        Toast.makeText(RocketService.this,
//                "共杀死了" + count + "进程,清除了" +
//                        Formatter.formatFileSize(RocketService.this, killMem) + "内存", Toast.LENGTH_SHORT).show();
//

//        Message msg = Message.obtain();
//        msg.what = 1;
//        msg.arg1 = count;
//        msg.obj = killMem;
//        handler.sendMessage(msg);
    }
//        }.start();
//    }

    private class MyTouckListener implements View.OnTouchListener {

        private int startX;
        private int startY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN://按下屏幕

                    //记录开始位置
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:

                    //记录最终位置
                    int endX = (int) event.getRawX();
                    int endY = (int) event.getRawY();

                    //计算差值
                    int dx = endX - startX;
                    int dy = endY - startY;

                    //更新距离
//                    params.gravity = Gravity.LEFT + Gravity.TOP;
                    params.x += dx;
                    params.y += dy;

                    if (params.x < 0) {
                        params.x = 0;
                    } else if (params.y < 0) {
                        params.y = 0;
                    } else if (params.x + view.getWidth() > mWidth) {
                        params.x = mWidth - view.getWidth();
                    } else if (params.y + view.getHeight() > mHeight - 40) {
                        params.y = mHeight - 40 - view.getHeight();
                    }


                    //更新位置
                    mWM.updateViewLayout(view, params);

                    //重新初始化开始位置
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (params.x > 0 && params.x < mWidth && params.y > (mHeight - 400) && params.y < mHeight) {
                        params.x = mWidth / 2 - view.getWidth() / 2;
                        Intent intent = new Intent(RocketService.this, BackgroundActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();

                                for (int i = 0; i <= 10; i++) {
                                    try {
                                        Thread.sleep(40);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    params.y -= (mHeight - view.getHeight() - 150) / 10;
                                    handler.sendEmptyMessage(0);

                                }
                                stopSelf();

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startService(new Intent(RocketService.this,RocketService.class));
                            }
                        }.start();


                    }
                    break;

            }
            return true;
        }
    }

}
