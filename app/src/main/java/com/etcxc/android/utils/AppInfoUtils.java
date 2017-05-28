package com.etcxc.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.pm.PackageManager.GET_SIGNATURES;

/**
 * Created by 刘涛 on 2017/5/27 0027.
 */

public class AppInfoUtils {
    /**
     * 得到当前应用的签名
     *
     * @param context
     * @return
     */
    public static String getSign(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.fanggroup.redboymail", GET_SIGNATURES);
            return packageInfo.signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 得到手机客户端的唯一标识
     *
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();
        return szImei;
    }

    /**
     * 获取当前应用版本号
     *
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }
}
