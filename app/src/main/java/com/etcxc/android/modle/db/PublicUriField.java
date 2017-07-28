package com.etcxc.android.modle.db;

import android.net.Uri;

import com.etcxc.android.BuildConfig;

/**
 * 数据库操作权限
 * Created by xwpeng on 2017/5/25.
 */
public class PublicUriField {

    /**
     * provider权限，与AndroidManifest.xml中的provider中的配置一致
     */
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".public";

    public static final Uri API_RECORD_URI = Uri.parse("content://" + AUTHORITY
            + "/" + UriQueryPath.API_RECORD);

    public interface UriQueryPath {
        String API_RECORD = "apiRecord";
    }
}
