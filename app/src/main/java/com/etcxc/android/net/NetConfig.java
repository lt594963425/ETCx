package com.etcxc.android.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.etcxc.android.base.App;

import java.io.File;
import java.util.Map;

/**
 * 网络相关的一些配置
 * Created by xwpeng on 2017/5/28.
 */

public class NetConfig {
    public static final String CODE_FA_INVALID_SESSION = "FA_INVALID_SESSION";
    public static final String CODE_FA_SECURITY = "FA_SECURITY";
//    public final static String HOST = "http://192.168.6.58";
    public final static String HOST = "http://192.168.6.126:9999";
    //微信充值下单
    public final static  String wxOrders ="https://api.mch.weixin.qq.com/pay/unifiedorder";

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

    public static String consistUrl(@NonNull String func, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(HOST).append(func);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                 builder.append(File.separator).append(entry.getKey()).append(File.separator).append(entry.getValue());
            }
        }
        return builder.toString();
    }
}
