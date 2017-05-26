package com.itheima.googleplay.base;

import android.app.Application;
import android.content.Context;
import android.os.*;

/**
 *  全局单例，注意需要在清单文件里面进行配置
 */
public class MyApplication extends Application {

    private static Context mContext;
    private static Handler mMainThreadHandler;
    private static int mMainThreadId;

    /**
     *  程序的入口方法
     */
    @Override
    public void onCreate() {
        //上下文
        mContext = getApplicationContext();

        //主线程的Handler
        mMainThreadHandler = new Handler();

        /**
         * 获取主线程的线程id
         * myTid:Thread
         * myPid:Process
         * myUid:User
         */
        mMainThreadId = android.os.Process.myTid();
        super.onCreate();
    }

    /**
     * 得到上下文
     */
    public static Context getContext() {
        return mContext;
    }

    /**
     * 得到主线程里面的创建的一个hanlder
     */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    /**
     * 得到主线程的线程id
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }
}
