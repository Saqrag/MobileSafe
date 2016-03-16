package com.bug.mobilesafe.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by saqra on 2016/2/19.
 */
public class DBhelper extends SQLiteOpenHelper{
    public static final String TABLE_BALCKLIST="Blacklist";
    public static final String TABLE_LOCKE_APP="LockedApp";
    public static final String ROW_PACKAGE_NAME="packageName";
    public static final String ROW_NUMBER="number";
    public static final String ROW_NAME="name";
    public static final String ROW_MODE="mode";


    public DBhelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Blacklist (_id integer primary key autoincrement,number varchar(12),name varchar(10),mode varchar(2))");
        db.execSQL("create table LockedApp (_id integer primary key autoincrement,packageName varchar(30))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
