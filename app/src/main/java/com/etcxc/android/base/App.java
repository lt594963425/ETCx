package com.etcxc.android.base;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.etcxc.android.crash.CrashHandler;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.net.cookie.CookieJarImpl;
import com.etcxc.android.net.cookie.store.SPCookieStore;
import com.etcxc.android.net.https.HttpsUtils;
import com.etcxc.android.net.log.LoggerInterceptor;
import com.etcxc.android.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * App基础类
 * Created by xwpeng on 2017/5/25.
 */
public class App extends Application {
    private static final String TAG = App.class.getSimpleName();
    private static App sInstance = null;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public App() {
        sInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //封装okhttp的初始化配置
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, (X509TrustManager) null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //全局的连接超时时间
                .connectTimeout(50000L, TimeUnit.MILLISECONDS)
                //全局的读取超时时间
                .readTimeout(50000L, TimeUnit.MILLISECONDS)
                //全局的写入超时时间
                .writeTimeout(50000L,TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .cookieJar(new CookieJarImpl(new SPCookieStore(this)))
                //缓存大小20M
                //.cache(new Cache(App.get().getCacheDir(),20*1024*1024))
                //.cache(new Cache(new File(FileUtils.getCachePath(this), "okhttpCache"), 10 * 1024 * 1024))
                //.cache(new Cache(new File(FileUtils.getCachePath(this), "okhttpCache"), 10 * 1024 * 1024))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //https
                .build();
        OkHttpUtils.initClient(okHttpClient);
        //友盟日志加密传输
        MobclickAgent.setDebugMode(true);
        //微信

        //异常扑捉初始化
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
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

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).trimMemory(level);
        }
        Glide.get(this).clearMemory();
    }
}