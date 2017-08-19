package com.etcxc.android.base;

import android.app.Application;

import com.etcxc.android.crash.CrashHandler;
import com.etcxc.android.utils.LogUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import static com.etcxc.android.base.Constants.WX_APP_ID;

/**
 * App基础类
 * Created by xwpeng on 2017/5/25.
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static App sInstance = null;
    public static IWXAPI WXapi;

    public App() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MobclickAgent.setDebugMode(true);//日志加密传输
        WXapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        WXapi.registerApp(WX_APP_ID);
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext());
        LogUtil.d(TAG, "App Application onCreate");
    }

    public synchronized static App get() {
        if (sInstance == null) {
            throw new RuntimeException("App is null or dead.");
        }
        return sInstance;
    }

    public static void onProfileSignIn(String ID) {
        MobclickAgent.onProfileSignIn(ID);
    }
}