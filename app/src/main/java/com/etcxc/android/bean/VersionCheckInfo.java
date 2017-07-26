package com.etcxc.android.bean;

import android.util.Log;

import com.etcxc.android.utils.SystemUtil;

import org.json.JSONObject;

import java.io.File;

import static com.etcxc.android.net.OkClient.HTTP_PREFIX;

/**
 * 检测更新信息
 * Created by xwpeng on 2017/7/26.
 */

public class VersionCheckInfo {
    private final static String TAG = VersionCheckInfo.class.getSimpleName();
    public boolean focrceUpdate;
    public String versionName;
    public  String apkUrl;
    public String description;
    public String downloadPath;


    public static VersionCheckInfo parse(JSONObject jsonObject) {
        VersionCheckInfo info = new VersionCheckInfo();
        try {
            info.focrceUpdate = jsonObject.getBoolean("force");
            info.versionName = jsonObject.getString("version_num");
            info.apkUrl = HTTP_PREFIX + jsonObject.getString("version_url");
            info.description = jsonObject.getString("version_content");
            info.downloadPath = SystemUtil.downloadDir() + File.separator + info.versionName+ ".apk";
        } catch (Exception e) {
            Log.e(TAG, "parse", e);
        }
        return info;
    }



}
