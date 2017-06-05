package com.etcxc.android.base;

import android.app.Application;

import com.etcxc.android.utils.LogUtil;

/**
 * App基础类
 * Created by xwpeng on 2017/5/25.
 */
public class App extends Application {
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
        /**
         * 获取主线程的线程id
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
     * 得到主线程的线程id
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

}