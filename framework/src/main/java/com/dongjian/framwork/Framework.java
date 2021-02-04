package com.dongjian.framwork;

/**
 * Framework入口--单例设计模式：DCL
 */
public class Framework {

    private volatile static Framework mFramwork;

    private Framework(){

    }

    public static Framework getFramwork(){
        if(mFramwork == null){
            synchronized (Framework.class){
                if(mFramwork == null){
                    mFramwork = new Framework();
                }
            }
        }
        return mFramwork;
    }

}
