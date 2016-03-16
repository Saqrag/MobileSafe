package com.bug.mobilesafe.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by saqra on 2016/2/26.
 */
public class SmsUtil {


    public static boolean backUpSms(final Context context, Progress pro) {
        boolean isSuccess = false;

        FileOutputStream os = null;

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }

        Uri smsUri = Uri.parse("content://sms");


        try {

            os = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "BackupSms.xml"));

            Cursor smss = context.getContentResolver().query(smsUri,
                    new String[]{"address", "date", "type", "body"}, null, null, null);

            pro.maxCount(smss.getCount());

            XmlSerializer xm = Xml.newSerializer();

            xm.setOutput(os, "utf-8");

            xm.startDocument("utf-8", true);

            xm.startTag(null, "smss");

            int progress=0;

            while (smss.moveToNext()) {

//                System.out.println(smss.getString(0));
//                System.out.println(smss.getString(3));
                String address = smss.getString(smss.getColumnIndex("address"));
                String date = smss.getString(smss.getColumnIndex("date"));
                String type = smss.getString(smss.getColumnIndex("type"));
                String body = smss.getString(smss.getColumnIndex("body"));

                //防止内容为空,抛异常
                if (TextUtils.isEmpty(address)
                        ||TextUtils.isEmpty(date)
                        ||TextUtils.isEmpty(type)
                        ||TextUtils.isEmpty(body)){

                    continue;
                }

                xm.startTag(null, "sms");

                xm.startTag(null, "address");
                xm.text(address);
                xm.endTag(null, "address");

                xm.startTag(null, "date");
                xm.text(date);
                xm.endTag(null, "date");

                xm.startTag(null, "type");
                xm.text(type);
                xm.endTag(null, "type");

                xm.startTag(null, "body");


                //加密
                xm.text(Crypto.encrypt("12345",body));
                xm.endTag(null, "body");

                xm.endTag(null, "sms");

                progress++;
                pro.updateProgress(progress);

                 isSuccess= true;
            }



            xm.endTag(null, "smss");

            xm.endDocument();

            smss.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return isSuccess;
    }

    public interface Progress{

        /**
         * 设置最大数
         * @param maxCount
         */
        void maxCount(int maxCount);

        /**
         * 更新进度
         * @param progress
         */
        void updateProgress(int progress);
    }

}
