package com.etcxc.android.base;

import android.app.Application;

import com.etcxc.android.utils.LogUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

import static com.etcxc.android.base.Constants.QQ_APP_ID;
import static com.etcxc.android.base.Constants.WX_APP_ID;

/**
 * App基础类
 * Created by xwpeng on 2017/5/25.
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    public static Boolean isLogin = false;//未登录状态false ，登录状态true
    private static App sInstance = null;
    public static IWXAPI WXapi;
    public static Tencent mTencent;// 新建Tencent实例用于调用分享方法

    public App() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        MobclickAgent.setDebugMode(true);//日志加密传输
        WXapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        WXapi.registerApp(WX_APP_ID);
        mTencent = Tencent.createInstance(QQ_APP_ID, this);
        super.onCreate();
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