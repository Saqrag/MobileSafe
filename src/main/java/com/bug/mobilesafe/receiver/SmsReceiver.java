package com.bug.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.bug.mobilesafe.database.BlacklistDB;
import com.bug.mobilesafe.services.AlarmService;
import com.bug.mobilesafe.services.LocationService;
import com.bug.mobilesafe.utils.ShareUtil;

/**
 * Created by saqra on 2016/2/2.
 */
public class SmsReceiver extends BroadcastReceiver {

    private SharedPreferences config;
    private DevicePolicyManager mDPM;
    private ComponentName componentName;

    @Override
    public void onReceive(Context context, Intent intent) {

        BlacklistDB db=new BlacklistDB(context);

        //获取设备策略服务
        mDPM = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
        //获得管理组件
        componentName = new ComponentName(context, AdminReceiver.class);


        config = context.getSharedPreferences(ShareUtil.CONFIG, Context.MODE_PRIVATE);
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object object :
                pdus) {
            SmsMessage fromPdu = SmsMessage.createFromPdu((byte[]) object);
            String address = fromPdu.getOriginatingAddress();
            String body = fromPdu.getMessageBody();
            String phone = config.getString(ShareUtil.PHONE, "");
//            System.out.println("短信拦截2");
//            System.out.println("address:" + address);
//            System.out.println("phone:" + phone);
            if (address.equals(phone)) {
                if ("#*alarm*#".equals(body)) {//播放报警音乐

                   System.out.println("报警音乐");
                    Intent intentAlarm = new Intent(context, AlarmService.class);
                    context.startService(intentAlarm);
                    abortBroadcast();//垄断广播
                } else if ("#*location*#".equals(body)) {
                    System.out.println("gps定位");
                    context.startService(new Intent(context, LocationService.class));
                    String location = config.getString(ShareUtil.LOCATION, "正在处理数据,稍等一小会儿,再次发送指令!!!");
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phone, null, location, null, null);
                    abortBroadcast();//垄断广播
                } else if ("#*wipedata*#".equals(body)) {
                    System.out.println("远程删除数据");
                    if (mDPM.isAdminActive(componentName)) {//是否管理员身份
                        mDPM.wipeData(0);
                    }
                    abortBroadcast();
                } else if ("#*lockscreen*#".equals(body)) {
                    System.out.println("远程锁屏");
                    if (mDPM.isAdminActive(componentName)) {//是否管理员身份

                        //生成随机密码
                        String pwd = (int)(1000 + Math.random() * (9999 - 1000 + 1))+"";
                        mDPM.resetPassword(pwd + "", 0);
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phone, null, "锁屏密码是:" + (pwd + ""),null, null);
                        mDPM.lockNow();

                    }
                    abortBroadcast();
                }
            }
//            System.out.println("短信拦截1");
            if (db.exit(address)){
                if (config.getBoolean(ShareUtil.IS_BLACK_LIST, false)){
                    System.out.println("短信拦截");
                    abortBroadcast();
                }

            }
        }

    }
}
