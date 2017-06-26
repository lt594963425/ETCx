package com.etcxc.android.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.base.App;

import java.io.File;
import java.util.List;

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

    /**
     * 判断系统中是否存在可以启动的相机应用
     *
     * @return 存在返回true，不存在返回false
     */
    public static boolean hasCamera() {
        PackageManager packageManager = App.get().getPackageManager();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, File file) {
        if (file == null) {
            LogUtil.w(TAG, "installApk: file is null.");
            return;
        }
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri uri = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    ? FileProvider.getUriForFile(App.get(), BuildConfig.APPLICATION_ID + ".fileprovider", file)
                    : Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } else {
            LogUtil.w(TAG, "installApk: failed, path=" + file.getAbsolutePath());
        }
    }

    public static final File downloadDir() {
        File f1 = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            f1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (f1.exists()) {
                f1 = new File(f1, "XC");
                if (!f1.exists()) {
                    f1.mkdir();
                }
            }
        }
        return f1;
    }

}
