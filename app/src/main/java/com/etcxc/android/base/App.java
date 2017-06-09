package com.etcxc.android.base;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;

import com.etcxc.android.utils.LogUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * App基础类
 * Created by xwpeng on 2017/5/25.
 */
public class App extends Application {
   public  static Boolean  userTag = false;
    public  static final String WX_APP_ID = "wx21d6d90cd6a3a206";
    public static final String WX_APP_SECRET = "2e08ae5ae947e7bb99bfd32e24e1e7cd";
    public static IWXAPI WXapi;
    private static Context mContext;
    private static Handler mMainThreadHandler;
    private static int mMainThreadId;
    private static final String TAG = "App";
    private static App sInstance = null;
    //Boolean static Boolean userTag = false;
    public App() {
        sInstance = this;
    }
    /**
     * 协议的内存缓存集合
     */
    private Map<String, String> protocolCacheMemory = new HashMap<>();
    //购物车的请求参数
    public static SkuListInfo mSkuListInfo;
    /**
     *  程序的入口方法
     */

    @Override
    public void onCreate() {
        //api注册
        WXapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        //api.registerApp(WX_APP_ID);
        WXapi.registerApp(WX_APP_ID);


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


    public Map<String, String> getProtocolCacheMemory() {
        return protocolCacheMemory;
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

    /**
     *判断Apk是不是debug，一些日志只有debug模式才会打印
     */
    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {

        }
        return false;
    }
}