package com.dongjian.framwork.utils;

/**
 * 时间转换类
 */
public class TimeUtils {

    /**
     * 转换毫秒格式 HH:mm:ss
     * @param ms 传入毫秒数
     */
    public static String formatDuring(long ms){
        //毫秒数 取余一天后 除一小时，算出等于多少小时
        long hours = (ms % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        //毫秒数 取余一小时后 除一分钟，算出等于多少分钟
        long minutes = (ms % (1000 * 60 * 60)) / (1000 * 60);
        //毫秒数 取余一分钟后 除一秒，算出 等于多少秒
        long seconds = (ms % (1000 * 60)) / 1000;

        //当前时间是个位数的时候，给时间补零
        String h = hours + "";
        String m = minutes + "";
        String s = seconds + "";
        if(hours < 10){
            h = "0" + h;
        }
        if(minutes < 10){
            m = "0" + m;
        }
        if(seconds < 10){
            s = "0" + s;
        }

        return h + ":" + m + ":" + s;
    }
}
