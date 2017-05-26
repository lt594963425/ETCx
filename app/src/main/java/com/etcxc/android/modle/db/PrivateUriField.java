package com.etcxc.android.modle.db;

import android.net.Uri;

/**
 * Created by wb on 2015/7/15.
 */
public class PrivateUriField {

    /**
     * 系统数据库操作权限/provider权限，与AndroidManifest.xml中的provider中的配置一致
     */
    public static final String AUTHORITY = DbUtil.privateUriAuthority();

    public static final Uri CONVERT_DB_URL = Uri.parse("content://" + AUTHORITY
            + "/" + UriQueryPath.CONVERT_DB);

    public static final Uri CONTACT1_URI = Uri.parse("content://" + AUTHORITY
            + "/" + UriQueryPath.CONTACT1);

    public interface UriQueryPath {
        /**
         * 好友列表
         */
        String CONTACT1 = "contact1";

        String CONVERT_DB = "convert_db";
    }
}
