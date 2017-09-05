package com.etcxc.android.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.etcxc.android.base.App;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;

/**
 * 网络相关的一些配置
 * Created by xwpeng on 2017/5/28.
 */

public class NetConfig {
    public static final String HTTP_PREFIX = "http://";
    public static final MediaType JSON
            = MediaType.parse("text/x-json;charset=UTF-8");
    /**
     * 错误提示
     */
    public static final String ERROR_PARAMS = "params error";//传参错误
    public static final String ERROR_DB= "db error";//数据库操作错误
    public static final String ERROR_SMS = "sms_error";//短信验证失败
    public static final String ERROR_AUTH = "auth failed";//凭证认证失败。修改密码，其他手机登录等会造成这个错误，需要手动输入密码
    public static final String ERROR_TOKEN = "auth token no active";    //凭证过期，需要执行重登

    public static final String CODE_FA_INVALID_SESSION = "FA_INVALID_SESSION";
    public static final String CODE_FA_SECURITY = "FA_SECURITY";
    //    public final static String HOST = "http://192.168.6.58";
    //    public final static String HOST = "http://192.168.6.50:8080";
    //    public final static String HOST = "http://46080450.nat123.net";
    public static final String HOST = "http://192.168.6.50";

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

    /**
     * 20170726 by xwpeng
     * 后端接口改成params用json传输，所以params一般传空
     */
    public static String consistUrl(@NonNull String func, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(HOST).append(func);
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(File.separator).append(entry.getKey()).append(File.separator).append(entry.getValue());
            }
        }
        return builder.toString();
    }

    public static String consistUrl(@NonNull String func) {
        return consistUrl(func, null);
    }
}
