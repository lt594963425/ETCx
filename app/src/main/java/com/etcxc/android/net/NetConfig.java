package com.etcxc.android.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.etcxc.android.base.App;

/**
 * 网络相关的一些配置
 * Created by xwpeng on 2017/5/28.
 */

public class NetConfig {
    public static final String CODE_FA_INVALID_SESSION = "FA_INVALID_SESSION";
    public static final String CODE_FA_SECURITY = "FA_SECURITY";

    /**
     * @return 当前网络状态，详见{@link NetworkInfo}
     */
    public static NetworkInfo getNetworkInfo() {
        ConnectivityManager connectivity = (ConnectivityManager) App
                .get().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            return connectivity.getActiveNetworkInfo();
        }
        return null;
    }

    /**
     * @return true，如果当前所处是wifi环境
     */
    public static boolean isWifi() {
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo != null
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 网络是否可用
     *
     * @return
     */
    public static boolean isAvailable() {
        NetworkInfo info = getNetworkInfo();
        return info != null && info.isAvailable();
    }
}