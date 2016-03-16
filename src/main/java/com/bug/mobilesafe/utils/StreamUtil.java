package com.bug.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by saqra on 2016/1/27.
 */
public class StreamUtil {
    public static String getStream(InputStream inputStream){
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer stringBuffer=new StringBuffer();
        String line="";
        try {
            while((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
