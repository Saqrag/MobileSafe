package com.bug.mobilesafe.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bug.mobilesafe.R;

/**
 * 播放报警音乐服务
 * Created by saqra on 2016/2/4.
 */
public class AlarmService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sing);
        mediaPlayer.setVolume(1f,1f);
        mediaPlayer.setLooping(true);//单曲播放
        mediaPlayer.start();
    }
}
