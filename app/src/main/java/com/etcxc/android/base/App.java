package com.etcxc.android.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.etcxc.android.util.LogUtil;

/**
 * App基础类
 * Created by xwpeng on 2017/5/25.
 */
public class App extends Application {
    private static Context mContext;
    private static Handler mMainThreadHandler;
    private static int mMainThreadId;
    private static final String TAG = "App";
    private static App sInstance = null;
    public App() {
        sInstance = this;
    }
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
        LogUtil.d(TAG, "App Application onCreate");
    }


    public synchronized static App get() {
        if (sInstance == null) {
            throw new RuntimeException("App is null or dead.");
        }
        return sInstance;
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