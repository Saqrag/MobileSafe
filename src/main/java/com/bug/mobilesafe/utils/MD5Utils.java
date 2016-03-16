package com.bug.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.Buffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    /**
     * md5加密
     *
     * @param password
     * @return
     */
    public static String encode(String password) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");// 获取MD5算法对象
            byte[] digest = instance.digest(password.getBytes());// 对字符串加密,返回字节数组

            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xff;// 获取字节的低八位有效值
                String hexString = Integer.toHexString(i);// 将整数转为16进制

                if (hexString.length() < 2) {
                    hexString = "0" + hexString;// 如果是1位的话,补0
                }

                sb.append(hexString);
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // 没有该算法时,抛出异常, 不会走到这里
        }

        return "";
    }

    public static String getAppMd5(String sourceDir) {
        String md5 = "";
        FileInputStream fis=null;

        try {
            fis = new FileInputStream(new File(sourceDir));

            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            int len = -1;
            byte[] buffer=new byte[1024];
            while ((len = fis.read(buffer))!=-1){
                messageDigest.update(buffer, 0, len);
            }
            byte[] digest = messageDigest.digest();

            StringBuffer sb=new StringBuffer();
            for (byte b :
                    digest) {
                int i = b & 0xff;
                String s = Integer.toHexString(i);
                if (s.length()<2){
                    sb.append("0"+s);
                }else {
                    sb.append(s);
                }
            }

            md5=sb.toString();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("not found App's sourceDir");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.err.println("not found md5");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return md5;
    }
}
