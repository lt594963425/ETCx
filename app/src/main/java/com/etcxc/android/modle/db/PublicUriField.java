package com.etcxc.android.modle.db;

import android.net.Uri;

/**
 * PublicUriField
 * Created by xwpeng on 2017/5/25.
 */
public class PublicUriField {

    /**
     * 系统数据库操作权限/provider权限，与AndroidManifest.xml中的provider中的配置一致
     */
    public static final String AUTHORITY = DbUtil.publicUriAuthority();

    public static final Uri INTERFACE1_URI = Uri.parse("content://" + AUTHORITY
            + "/" + UriQueryPath.INTERFACE1);

    public interface UriQueryPath {
        String INTERFACE1 = "interface1";
    }
}
