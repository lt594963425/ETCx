package com.etcxc.android.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.etcxc.android.utils.LogUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * App基础类
 * Created by xwpeng on 2017/5/25.
 */
public class App extends Application {

    public  static Boolean  userTag = false;

    private static final String TAG = "App";
    private static App sInstance = null;
    public App() {
        sInstance = this;
    }
    public  static final String WX_APP_ID = "wx21d6d90cd6a3a206";
    public static final String WX_APP_SECRET = "2e08ae5ae947e7bb99bfd32e24e1e7cd";
    public static IWXAPI WXapi;

    @Override
    public void onCreate() {
        WXapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        WXapi.registerApp(WX_APP_ID);
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