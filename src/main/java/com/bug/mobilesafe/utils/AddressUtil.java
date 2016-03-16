package com.bug.mobilesafe.utils;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by saqra on 2016/2/15.
 */
public class AddressUtil extends Activity {
    static String path = "data/data/com.bug.mobilesafe/files/address.db";

    public static String getAddress(String number) {
//        System.out.println(999);
        String location = "未知号码";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                path, null, SQLiteDatabase.OPEN_READONLY);
        if (number.matches("^1[3-8]\\d{9}$")) { //匹配手机号码
            Cursor cursor = db.rawQuery("select location from data2 where id=" +
                            "(select outkey from data1 where id=?)",
                    new String[]{number.substring(0, 7)});
            if (cursor.moveToNext()) {
                location = cursor.getString(0);
            }
            cursor.close();

        } else if (number.matches("^\\d+$")) {//匹配数字
            switch (number.length()) {
                case 3:
                    if (number.startsWith("1"))
                    location = "报警电话";
                    break;
                case 4:
                    location = "模拟器";
                    break;
                case 5:
                    if (number.startsWith("1"))
                    location = "客服电话";
                    break;
                case 7:
                case 8:
                    location = "本地通话";
                    break;
                default:
                    if (number.startsWith("0") && number.length() >10) {//长途区号
                        Cursor cursor = db.rawQuery("select location from data2 where area=?",
                                new String[]{number.substring(1, 4)});
                        if (cursor.moveToNext()) {
                            String string = cursor.getString(0);
                            int length = string.length();
                            location = string.substring(0, length - 2);
                        }else {
                            cursor.close();
                            cursor=db.rawQuery("select location from data2 where area=?",
                                    new String[]{number.substring(1,3)});
                            if (cursor.moveToNext()){
                                String string = cursor.getString(0);
                                int length = string.length();
                                location = string.substring(0, length - 2);
                            }
                        }
                        cursor.close();
                    }
                    break;
            }
        }

        return location;
    }

}
