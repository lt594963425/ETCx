package com.etcxc.android;

import android.app.Application;

import com.etcxc.android.util.LogUtil;

/**
 * App基础类
 * Created by xwpeng on 2017/5/25.
 */
public class CApp extends Application {
    private static final String TAG = "CApp";
    private static CApp sInstance = null;
    public CApp() {
        sInstance = this;
    }

    public synchronized static CApp get() {
        if (sInstance == null) {
            throw new RuntimeException("CApp is null or dead.");
        }
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "CApp Application onCreate");
    }
}
