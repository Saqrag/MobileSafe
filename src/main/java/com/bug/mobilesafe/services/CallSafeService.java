package com.bug.mobilesafe.services;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.bug.mobilesafe.database.BlacklistDB;

import java.lang.reflect.Method;

/**
 * 电话拦截服务
 */

public class CallSafeService extends Service {

    private TelephonyManager tm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        System.out.println("电话拦截服务开启了");

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        MyListener myListener=new MyListener();
        tm.listen(myListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    BlacklistDB db=new BlacklistDB(CallSafeService.this);
                    String mode = db.query(incomingNumber);
                    if (mode.equals("0")||mode.equals("1")){
//                        System.out.println("拦截电话");

                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler(),incomingNumber));

                        endCall();
                    }
                    break;
            }
        }
    }

    private void endCall() {
        try {
//            System.out.println("endCall方法");
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getMethod("getService", String.class);
            IBinder invoke = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(invoke);
            iTelephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyContentObserver extends ContentObserver{

        private final Handler mHandler;
        private final String mIncomingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String incomingNumber) {
            super(handler);
            mHandler = handler;
            mIncomingNumber = incomingNumber;

        }

        /**
         * 观察通话记录的改变
         * @param selfChange
         */
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getContentResolver().unregisterContentObserver(this);

            deleteData(mIncomingNumber);
        }

        /**
         * 删除通讯录
         * @param mIncomingNumber
         */
        private void deleteData(String mIncomingNumber) {
            Uri uri = Uri.parse("content://call_log/calls");
            getContentResolver().delete(uri,"number=?",new String[]{mIncomingNumber});
        }
    }
}
