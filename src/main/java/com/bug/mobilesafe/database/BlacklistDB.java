package com.bug.mobilesafe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.bug.mobilesafe.bean.BlackListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saqra on 2016/2/19.
 */
public class BlacklistDB {

    private final DBhelper DBhelper;
    private int count;

    public BlacklistDB(Context context) {
        DBhelper = new DBhelper(context);
    }

    public boolean add(String number, String name, String mode) {
        String name1 = "未知联系人";
        if (!TextUtils.isEmpty(name)) {
            name1 = name;
        }
        SQLiteDatabase db = DBhelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBhelper.ROW_NUMBER, number);
        values.put(DBhelper.ROW_NAME, name1);
        values.put(DBhelper.ROW_MODE, mode);
        long insert = db.insert(DBhelper.TABLE_BALCKLIST, null, values);
        db.close();
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public int delete(String number) {
        SQLiteDatabase db = DBhelper.getReadableDatabase();
        int delete = db.delete(DBhelper.TABLE_BALCKLIST, "number=?", new String[]{number});
        db.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return delete;
    }

    public String query(String number) {
        String mode = "";
        SQLiteDatabase db = DBhelper.getReadableDatabase();
        Cursor query = db.query(DBhelper.TABLE_BALCKLIST, new String[]{DBhelper.ROW_MODE}, "number=?", new String[]{number}, null, null, null);
        if (query.moveToNext()) {
            mode = query.getString(0);
        }
        query.close();
        db.close();
        return mode;
    }

    public List<BlackListBean> queryAll() {
        SQLiteDatabase db = DBhelper.getReadableDatabase();
        Cursor query = db.query(DBhelper.TABLE_BALCKLIST, null, null, null, null, null, null);
        List<BlackListBean> list = new ArrayList<>();
        while (query.moveToNext()) {
            String name = query.getString(query.getColumnIndex(DBhelper.ROW_NAME));
            String number = query.getString(query.getColumnIndex(DBhelper.ROW_NUMBER));
            String mode = query.getString(query.getColumnIndex(DBhelper.ROW_MODE));
            list.add(new BlackListBean(name, number, mode));
        }
        query.close();
        db.close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<BlackListBean> queryPage(int maxCount, int start) {
        SQLiteDatabase db = DBhelper.getReadableDatabase();
        List<BlackListBean> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from Blacklist limit ? offset ?", new String[]{maxCount + "", start + ""});
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DBhelper.ROW_NAME));
            String number = cursor.getString(cursor.getColumnIndex(DBhelper.ROW_NUMBER));
            String mode = cursor.getString(cursor.getColumnIndex(DBhelper.ROW_MODE));
            list.add(new BlackListBean(name, number, mode));
        }

        cursor.close();
        db.close();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getToalCount() {
        int count = 0;
        SQLiteDatabase db = DBhelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from Blacklist", null);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        return count;
    }

    public boolean exit(String number){
        boolean isExit=false;
        SQLiteDatabase db = DBhelper.getReadableDatabase();
        Cursor query = db.query(DBhelper.TABLE_BALCKLIST, new String[]{DBhelper.ROW_MODE}, "number=?", new String[]{number}, null, null, null);
        if (query.moveToNext()) {
            isExit=true;
        }
        query.close();
        db.close();
        return isExit;
    }
}
