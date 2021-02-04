package com.dongjian.framwork.utils;

import android.text.TextUtils;
import android.util.Log;

import net.dongjian.framework.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 封装LOG日志打印----打印日志 + 记录日志
 */
public class LogUtils {

    public static SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH-MM-SS");

    public static void i(String text) {
        if (BuildConfig.LOG_DEBUG) {
            if (!TextUtils.isEmpty(text)) {
                Log.i(BuildConfig.LOG_TAG, text);
                writeToFile(text);
            }
        }
    }

    public static void e(String text) {
        if (BuildConfig.LOG_DEBUG) {
            if (!TextUtils.isEmpty(text)) {
                Log.e(BuildConfig.LOG_TAG, text);
                writeToFile(text);
            }
        }
    }

    /**
     * 记录日志的时间和内容
     * todo:版本不一样，好像有权限申请问题---无法找到sdcard里的meet文件夹
     * @param text  需要记录的内容
     */
    private static void writeToFile(String text){
        //1、定义文件路径
        String fileName = "/sdcard/Meet/Meet.log";
        //2、检查父路径是否正确--若无则创建
        File fileGroup = new File("/sdcard/Meet/");
        if(!fileGroup.exists()){
            fileGroup.mkdirs();
        }
        //3、记录时间和内容
        String log = mSimpleDateFormat.format(new Date()) + " " + text + "\n";
        //4、开始写入
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = new FileOutputStream(fileName,true);
            //这里有编码问题， GBK 保证正确存入中文
            bufferedWriter = new BufferedWriter(
                        new OutputStreamWriter(fileOutputStream, Charset.forName("gbk")) );
            bufferedWriter.write(log);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            if(bufferedWriter != null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
