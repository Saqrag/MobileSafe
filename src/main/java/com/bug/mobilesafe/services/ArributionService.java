package com.bug.mobilesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.bug.mobilesafe.R;
import com.bug.mobilesafe.utils.AddressUtil;
import com.bug.mobilesafe.utils.ShareUtil;

/**
 * 归属地显示服务
 * Created by saqra on 2016/2/16.
 */
public class ArributionService extends Service {
    private View view;
    private WindowManager mWM;
    private TelephonyManager tm;
    private MyListen myListen;
    private OutCallReceiver receiver;
    private SharedPreferences sPre;
    private int startY;
    private int startX;
    private WindowManager.LayoutParams params;

    private int mWidth;
    private int mHeight;
    private boolean isCentre;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sPre = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);
        //来电监视
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myListen = new MyListen();
        tm.listen(myListen, PhoneStateListener.LISTEN_CALL_STATE);

        //去电监视
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);
    }

    class MyListen extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://电话声音响了
                    String address = AddressUtil.getAddress(incomingNumber.trim());
//                    Toast.makeText(ArributionService.this, address, Toast.LENGTH_LONG).show();
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://正常情况
                    if (mWM != null && view != null) {
                        mWM.removeView(view);
                        view = null;
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        tm.listen(myListen, PhoneStateListener.LISTEN_NONE);//停止listen

        unregisterReceiver(receiver);//注销广播
        if (mWM!=null&&view!=null){
            mWM.removeView(view);
            view=null;
        }
    }

    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

//            System.out.println("去电广播响应了");
            String number = getResultData();

            String address = AddressUtil.getAddress(number.trim());
            showToast(address);
        }
    }

    /**
     * 自定义toast,显示归属地
     *
     * @param address
     */
    private void showToast(String address) {
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

        /**
         * 判断存的位置是否居中
         */
        isCentre = sPre.getBoolean(ShareUtil.IS_TOAST_CENTRE, false);
        if (isCentre) {
            params.gravity = Gravity.CENTER;
        } else {
            params.gravity = Gravity.LEFT + Gravity.TOP;
            //设置初始位置
            int lastX = sPre.getInt(ShareUtil.LAST_X, 0);
            int lastY = sPre.getInt(ShareUtil.LAST_Y, 0);
            params.x = lastX;
            params.y = lastY;
        }


        view = View.inflate(this, R.layout.toast, null);
        int[] bgs = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange, R.drawable.call_locate_blue,
                R.drawable.call_locate_gray, R.drawable.call_locate_green};


        int style = sPre.getInt(ShareUtil.ADDRESS_STYLE, 0);
        view.setBackgroundResource(bgs[style]);

        //设置拖拽监听
        view.setOnTouchListener(new MyOnTouchListener());

        TextView tvToast = (TextView) view.findViewById(R.id.tv_toast);
        tvToast.setText(address);
        mWM.addView(view, params);
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
//            System.out.println("拖拽实现了");

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

                    if (isCentre) {//一开始居中的话,以view的左上角坐标原点
                        if (params.x<-mWidth/2){
                            params.x=-mWidth/2;
                        }else if (params.y<-mHeight/2){
                            params.y=-mHeight/2;
                        }else if (params.x+view.getWidth()>mWidth/2+view.getWidth()/2){
                            params.x=mWidth/2-view.getWidth()/2;
                        }else if (params.y+view.getHeight()>mHeight/2+view.getHeight()/2-40){
                            params.y=mHeight/2-view.getHeight()/2-40;
                        }
                    } else {//一开始没有居中的话,以屏幕左上角为坐标原点
                        //限制屏幕之内拖动
                        if (params.x < 0) {
                            params.x = 0;
                        } else if (params.y < 0) {
                            params.y = 0;
                        } else if (params.x + view.getWidth() > mWidth) {
                            params.x = mWidth - view.getWidth();
                        } else if (params.y + view.getHeight() > mHeight - 40) {
                            params.y = mHeight - 40 - view.getHeight();
                        }
                    }


                    //更新位置
                    mWM.updateViewLayout(view, params);

                    //重新初始化开始位置
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:

                    //保存位置
                    SharedPreferences.Editor edit = sPre.edit();

                    if (isCentre){//一开始居中的话,以view的左上角坐标原点
                        edit.putInt(ShareUtil.LAST_X, params.x+mWidth/2-view.getWidth()/2);
                        edit.putInt(ShareUtil.LAST_Y, params.y+mHeight/2-view.getHeight()/2);
                    }else {//一开始没有居中的话,以屏幕左上角为坐标原点
                        edit.putInt(ShareUtil.LAST_X, params.x);
                        edit.putInt(ShareUtil.LAST_Y, params.y);
                    }
                    edit.putBoolean(ShareUtil.IS_TOAST_CENTRE, false);
                    edit.commit();
                    break;

            }
            return true;//截断事件的的传播,onclick无效
        }
    }
}
