package com.etcxc.android.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.compat.BuildConfig;
import android.text.TextUtils;

import com.etcxc.android.base.App;

/**
 * 系统级工具类
 * Created by xwpeng on 2017/6/15.
 */

public class SystemUtil {
    private static String TAG = "SystemUtil";

    public static String getVersionName() {
        String name = BuildConfig.VERSION_NAME;
        if (TextUtils.isEmpty(name)) {
            LogUtil.w(TAG, "BuildConfig.VERSION_NAME is null or empty. So, get it from PackageInfo.");
            try {
                PackageInfo info = App.get().getPackageManager().getPackageInfo(App.get().getPackageName(), 0);
                name = info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                LogUtil.e(TAG, "getVersionName", e);
            }
        }
        return name == null ? "" : name;
    }

    public static String getVersionName4CheckUpdate() {
        return getVersionNameByLength(4);
    }

    private static String getVersionNameByLength(int length) {
        String name = getVersionName();
        if (TextUtils.isEmpty(name)) return "";
        String[] names = name.split("[.]");
        if (names.length >= length) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(names[i]);
                if (i < length - 1) {
                    builder.append(".");
                }
            }
            name = builder.toString();
        }
        return name;
    }
}
