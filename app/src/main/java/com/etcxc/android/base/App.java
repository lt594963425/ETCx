package com.etcxc.android.base;

import android.app.Application;

import com.etcxc.android.utils.LogUtil;

/**
 * App基础类
 * Created by xwpeng on 2017/5/25.
 */
public class App extends Application {
    public static  Boolean isLogin =false;//未登录状态false ，登录状态true
    private static final String TAG = "App";
    private static App sInstance = null;

    public App() {
        sInstance = this;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "App Application onCreate");
    }


    public synchronized static App get() {
        if (sInstance == null) {
            throw new RuntimeException("App is null or dead.");
        }
        return sInstance;
    }
}