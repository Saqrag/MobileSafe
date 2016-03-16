package com.bug.mobilesafe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saqra on 2016/3/8.
 */
public class LockAppDB {

    private final DBhelper dBhelper;

    public LockAppDB(Context context) {
        dBhelper = new DBhelper(context);
    }

    public List<String> getLockedApps() {
        List<String> list = new ArrayList<>();

        SQLiteDatabase db = dBhelper.getReadableDatabase();
        Cursor cursor = db.query(DBhelper.TABLE_LOCKE_APP,
                new String[]{DBhelper.ROW_PACKAGE_NAME},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            String packageName = cursor.getString(0);
            list.add(packageName);
        }

        cursor.close();
        db.close();
        return list;
    }

    public boolean deleteApp(String packageName) {
        SQLiteDatabase db = dBhelper.getWritableDatabase();
        int delete = db.delete(DBhelper.TABLE_LOCKE_APP, "packageName=?", new String[]{packageName});
        db.close();

        if (delete > 0) {
            return true;
        } else {
            return false;
        }

    }

    public boolean lockApp(String packageName) {
        SQLiteDatabase db = dBhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBhelper.ROW_PACKAGE_NAME, packageName);
        long insert = db.insert(DBhelper.TABLE_LOCKE_APP, null, values);
        db.close();

        if (insert == -1) {
            return false;
        } else {
            return true;
        }

    }
}
