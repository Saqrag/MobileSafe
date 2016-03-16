package com.bug.mobilesafe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by saqra on 2016/2/29.
 */
public class AntiVirusDB {

    public static String getDesc(Context context,String md5){
        String desc="";
        SQLiteDatabase db=SQLiteDatabase.openDatabase
                (context.getFilesDir().getPath()+"/antivirus.db",null ,SQLiteDatabase.OPEN_READWRITE);
        Cursor datable = db.query("datable", new String[]{"desc"}, "md5=?",
                new String[]{md5}, null, null, null);

        if (datable.moveToNext()){
            desc=datable.getString(0);
        }
    return desc;
    }

    public static void addVirus(Context context,String md5,String desc){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                context.getFilesDir().getPath()+"/antivirus.db", null,
                SQLiteDatabase.OPEN_READWRITE);

        ContentValues values = new ContentValues();

        values.put("md5", md5);

        values.put("type", 6);

        values.put("name", "Android.Troj.AirAD.a");

        values.put("desc", desc);

        db.insert("datable", null, values);


        db.close();
    }
}
