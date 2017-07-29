package com.etcxc.android.modle.db;

import android.net.Uri;

import com.etcxc.android.BuildConfig;

/**
 * private数据库操作权限
 * Created by xwpeng on 2017/7/28.
 */
public class PrivateUriField {
    /**
     * /provider权限，与AndroidManifest.xml中的provider中的配置一致
     */
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".private";

    public static final Uri CONVERT_DB_URI = Uri.parse("content://" + AUTHORITY
            + "/" + UriQueryPath.CONVERT_DB);

    public interface UriQueryPath {
        String CONVERT_DB = "convert_db";
    }
}
