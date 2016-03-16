package com.bug.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.bug.mobilesafe.rocketutil.RocketService;
import com.bug.mobilesafe.services.ArributionService;
import com.bug.mobilesafe.services.CallSafeService;
import com.bug.mobilesafe.utils.ShareUtil;

/**
 * Created by saqra on 2016/2/2.
 */
public class BootReceiver extends BroadcastReceiver {
    SharedPreferences sPre;
    Context mContect;
    @Override
    public void onReceive(Context context, Intent intent) {
        sPre = context.getSharedPreferences(ShareUtil.CONFIG, context.MODE_PRIVATE);
        this.mContect=context;
        initGuard();
        initAttribution();
        initCallSafe();
        initRocket();

    }

    private void initRocket() {
        boolean isRocket = sPre.getBoolean(ShareUtil.IS_ROCKET, false);
        if (isRocket){
            mContect.startService(new Intent(mContect, RocketService.class));
        }
    }

    /**
     * 初始化电话拦截
     */
    private void initCallSafe() {
        boolean isCallSafe = sPre.getBoolean(ShareUtil.IS_BLACK_LIST, false);
        if (isCallSafe){
            mContect.startService(new Intent(mContect, CallSafeService.class));
        }
    }

    /**
     * 显示归属地
     */
    private void initAttribution() {
        boolean isLocation = sPre.getBoolean(ShareUtil.IS_LOCATION, false);
//        System.out.println("归属地的开机广播");
        if (isLocation){
            if (mContect==null){
                return;
            }

            //startService方法,如果服务之前已经运行,保持原样
            mContect.startService(new Intent(mContect, ArributionService.class));
        }else {
            if (mContect!=null){
                return;
            }

            //stopService方法,如果服务之前已经销毁,保持原样
            mContect.stopService(new Intent(mContect, ArributionService.class));

        }
    }

    /**
     * 发送防盗短信
     */
    private void initGuard() {
        boolean isGuard = sPre.getBoolean(ShareUtil.IS_GUARD, false);

        //判断手机防盗功能是否开启
        if (isGuard) {
            String sim = sPre.getString(ShareUtil.SIM, "");

            //判断sim卡信息是否正确
            if (!TextUtils.isEmpty(sim)) {
                String phone = sPre.getString(ShareUtil.PHONE, "");

                //判断安全号码是否为空
                if (!TextUtils.isEmpty(phone)) {
                    if (mContect==null){
                        return;
                    }
                    //获得目前的sim卡号码
                    TelephonyManager tm = (TelephonyManager) mContect.getSystemService(mContect.TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();

                    if (!simSerialNumber.equals(sim)) {
                        //发短信
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phone, null, "sim卡变化了", null, null);
                    }
                }
            }

        }
    }
}
