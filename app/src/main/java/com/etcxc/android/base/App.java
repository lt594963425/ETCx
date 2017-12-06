package com.etcxc.android.base;

import android.app.Application;
import android.os.Process;

import com.bumptech.glide.Glide;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.net.cookie.CookieJarImpl;
import com.etcxc.android.net.cookie.store.SPCookieStore;
import com.etcxc.android.net.https.HttpsUtils;
import com.etcxc.android.net.log.LoggerInterceptor;
import com.etcxc.android.utils.LogUtil;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;

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
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, (X509TrustManager) null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(50000L, TimeUnit.MILLISECONDS)
                .readTimeout(50000L, TimeUnit.MILLISECONDS)
                .writeTimeout(50000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .cookieJar(new CookieJarImpl(new SPCookieStore(this)))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //https
                .build();
        OkHttpUtils.initClient(okHttpClient);
        Observable.timer(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        MobclickAgent.setDebugMode(true);
                        WXapi = WXAPIFactory.createWXAPI(get(), WX_APP_ID, true);
                        WXapi.registerApp(WX_APP_ID);
                        LogUtil.d(TAG, "App Application onCreate");
                        if (LeakCanary.isInAnalyzerProcess(get())) {
                            return;
                        }
                        LeakCanary.install(get());
                    }
                });

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