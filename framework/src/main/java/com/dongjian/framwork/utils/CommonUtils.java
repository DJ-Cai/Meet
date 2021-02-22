package com.dongjian.framwork.utils;

import java.util.List;

/**
 * FileName: CommonUtils
 * Founder: LiuGuiLin
 * Profile: 通用方法
 */
public class CommonUtils {

    /**
     * 检查List是否可用
     * @param list
     * @return
     */
    public static boolean isNotEmpty(List list){
        return list != null && list.size() > 0;
    }
}
