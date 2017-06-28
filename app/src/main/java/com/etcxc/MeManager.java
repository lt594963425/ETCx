package com.etcxc;

import com.etcxc.android.modle.sp.PublicSPUtil;

/**
 * Created by xwpeng on 2017/5/26.<br/>
 * 记录个人相关的数据，包括登录后的各种信息(init配置)
 */
public class MeManager {
    private static final String TAG = "MeManager";
    public static final String KEY_Sid = "me_sid";
    public static final String KEY_Uid = "me_uid";
    public static  final String KEY_Name ="me_name";
    private static final String KEY_IsLogin ="me_islogin";
    public static final String KEY_TrueName = "me_true_name";

    public static String getSid() {
        return PublicSPUtil.getInstance().getString(KEY_Sid, null);
    }

    public static String getUid() {
        return PublicSPUtil.getInstance().getString(KEY_Uid, null);
    }
    public static String getName() {
        return PublicSPUtil.getInstance().getString(KEY_Name, null);
    }
    public static Boolean getIsLogin() {
        return PublicSPUtil.getInstance().getBoolean(KEY_IsLogin, false);
    }
    public static void setSid(String sid) {
        PublicSPUtil.getInstance().putString(KEY_Sid, sid);
    }

    public static void setUid(String uid) {
        PublicSPUtil.getInstance().putString(KEY_Uid, uid);
    }
    public static void setName(String name) {
        PublicSPUtil.getInstance().putString(KEY_Name, name);
    }
    public static void setIsLgon(Boolean islogin) {
        PublicSPUtil.getInstance().putBoolean(KEY_IsLogin, islogin);
    }
    private static final int clear_crash = 1;
    private static final int clear_logout = 2;
    public static void deleteinfo(String str) {
        PublicSPUtil.getInstance().delete(str);
    }
    /**
     * todo：分情况对个人配置信息进行清除
     */
    private static void clear(int cause) {
        switch (cause){
            case clear_crash:
                deleteinfo(KEY_Uid);
                break;
            case clear_logout://删除sid
                deleteinfo(KEY_Sid);
                break;
        }
    }

    public static void crashClear() {
        clear(clear_crash);
    }

    public static void logoutClear() {
        clear(clear_logout);
    }
}
