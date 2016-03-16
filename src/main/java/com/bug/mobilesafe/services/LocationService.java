package com.bug.mobilesafe.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import com.bug.mobilesafe.utils.ShareUtil;

/**
 * 获取位置的服务
 * Created by saqra on 2016/2/4.
 */
public class LocationService extends Service {
    Intent intent = null;
    private MyLocationListener myLocationListener;
    private LocationManager location;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        location = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//花费,走流量
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//准确程度
        String bestProvider = location.getBestProvider(criteria, true);


        //判断权限是否开启
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myLocationListener = new MyLocationListener();
        location.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);

    }

    @Override
    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("check error!");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        System.out.println("check success");
        location.removeUpdates(myLocationListener);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(android.location.Location location) {
            System.out.println("check!!");
            SharedPreferences config = getSharedPreferences(ShareUtil.CONFIG, MODE_PRIVATE);

            config.edit().putString(ShareUtil.LOCATION,"Location:"+location.getLongitude()+":"+location.getLatitude()).commit();
//            String phone = config.getString("phone", "");
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(phone, null,"Location:"+location.getLongitude()+":"+location.getLatitude(),
//                    null, null);
            System.out.println("check1!!");
            stopSelf();
            System.out.println("check2!!");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }


        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}


