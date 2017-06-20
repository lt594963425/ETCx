package com.etcxc.android.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.net.Actions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xwpeng on 2017/6/15.<br/>
 * 申请权限的辅助类
 */
public class PermissionUtil {
    public static final int REQUEST_SHOWCAMERA = 0;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int REQUEST_RECORD_AUDIO = 2;
    public static final int REQUEST_CONTACTS = 3;
    public static final int REQUEST_LOCATION = 4;
    private static final SimpleArrayMap<String, Integer> MIN_SDK_PERMISSIONS;
    static {
        MIN_SDK_PERMISSIONS = new SimpleArrayMap<>(8);
        MIN_SDK_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", 14);
        MIN_SDK_PERMISSIONS.put("android.permission.BODY_SENSORS", 20);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.USE_SIP", 9);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16);
        MIN_SDK_PERMISSIONS.put("android.permission.SYSTEM_ALERT_WINDOW", 23);
        MIN_SDK_PERMISSIONS.put("android.permission.WRITE_SETTINGS", 23);
    }
    private static final String TAG = "PermissionUtil";

    public static void requestPermissions(Activity activity, String[] permissions, OnRequestPermissionsResultCallback callback) {
        if (permissions == null || permissions.length == 0) throw new IllegalArgumentException("Permissions is null");
        List<String> ps = new ArrayList<>();//包含未被授权的权限
        for (String p : permissions) {
            if (!hasSelfPermission(activity, p)) ps.add(p);
        }
        if (ps.isEmpty()) {
            if (callback != null) callback.onRequestPermissionsResult(permissions, grantedArray(permissions.length));
        } else {
            String[] arr = new String[ps.size()];//未被授权的权限放在这个数据
            ps.toArray(arr);
            registerCallback(callback);
            Intent intent = new Intent(Actions.ACTION_PAGE_REQUEST_PERMISSIONS);
            intent.putExtra("permissions", arr);
            activity.startActivity(intent);
        }
    }

    public static void requestPermissions(Activity activity, String permission, OnRequestPermissionsResultCallback callback) {
        if (TextUtils.isEmpty(permission)) throw new IllegalArgumentException("Permissions is null");
        requestPermissions(activity, new String[]{permission}, callback);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return activity.shouldShowRequestPermissionRationale(permission);
    }

    public static boolean hasSelfPermission(Context context, String permission) {
        return !isM() || context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean isM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public interface OnRequestPermissionsResultCallback {
        void onRequestPermissionsResult(String[] permissions, int[] grantResults);
    }

    private static List<OnRequestPermissionsResultCallback> mRequestbacks;

    private static void registerCallback(OnRequestPermissionsResultCallback callback) {
        if (callback != null) {
            mRequestbacks = new ArrayList<>();
            mRequestbacks.add(callback);
        }
    }

    public static void setResults(Activity activity, String[] permissions, int[] grantResults) {
        if (mRequestbacks != null) {
            for (OnRequestPermissionsResultCallback callback : mRequestbacks) {
                if (callback != null) {
                    callback.onRequestPermissionsResult(permissions, grantResults);
                }
            }
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, R.string.reject_permission, Toast.LENGTH_SHORT).show();
            }
            mRequestbacks = null;
        }
    }

    private static int[] grantedArray(int length) {
        int[] results = new int[length];
        for (int i = 0; i < length; i++) {
            results[i] = PackageManager.PERMISSION_GRANTED;
        }
        return results;
    }
    private static boolean permissionExists(String permission) {
        Integer minVersion = MIN_SDK_PERMISSIONS.get(permission);
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion;
    }
    public static boolean hasCameraPermission(Activity activity){
        int hasPermission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA);
        if(!permissionExists(Manifest.permission.CAMERA)){
            Log.e("permission","your system does not suppport"+ Manifest.permission.CAMERA+" permission");
            return false;
        }
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    PermissionUtil.REQUEST_SHOWCAMERA);
            return false;
        }
        return true;
    }
}
