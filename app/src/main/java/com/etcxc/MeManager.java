package com.etcxc;

import com.etcxc.android.base.App;
import com.etcxc.android.modle.db.PrivateUriField;
import com.etcxc.android.modle.sp.PublicSPUtil;

/**
 * Created by xwpeng on 2017/5/26.<br/>
 * 记录个人相关的数据，包括登录后的各种信息(init配置)
 */
public class MeManager {
    private static final String TAG = "MeManager";
    public static final String KEY_Sid = "me_sid";
    public static final String KEY_Uid = "me_uid";
    public static final String KEY_Name = "me_name";
    public static final String KEY_Phone = "me_phone";
    private static final String KEY_Token = "me_token";
    private static final String KEY_PWD = "me_pwd";
    private static final String KEY_IsLogin = "me_islogin";

    private static final int clear_Uid = 1;
    private static final int clear_Sid = 2;
    private static final int clear_Token = 3;
    private static final int clear_Name = 4;
    private static final int clear_Phone = 5;
    private static final int clear_pwd = 6;
    private static final int clear_all = 0;
    public static String getSid() {
        return PublicSPUtil.getInstance().getString(KEY_Sid, "");
    }

    public static String getUid() {
        return PublicSPUtil.getInstance().getString(KEY_Uid, "");
    }

    public static Boolean getIsLogin() {
        return PublicSPUtil.getInstance().getBoolean(KEY_IsLogin, false);
    }

    public static String getName() {
        return PublicSPUtil.getInstance().getString(KEY_Name, "ETC");
    }

    public static String getPhone() {
        return PublicSPUtil.getInstance().getString(KEY_Phone, "");
    }

    public static String getToken() {
        return PublicSPUtil.getInstance().getString(KEY_Token, "");
    }
    public static String getPWD() {
        return PublicSPUtil.getInstance().getString(KEY_PWD, "");
    }
    public static void setSid(String sid) {
        PublicSPUtil.getInstance().putString(KEY_Sid, sid);
    }

    public static void setUid(String uid) { //
        PublicSPUtil.getInstance().putString(KEY_Uid, uid);
        //fixme:创建私有数据库，可以考虑放到登录成功
        App.get().getContentResolver().query(PrivateUriField.CONVERT_DB_URI, null, null, null, null);
    }

    public static void setIsLgon(Boolean islogin) {
        PublicSPUtil.getInstance().putBoolean(KEY_IsLogin, islogin);
    }

    public static void setToken(String token) {
        PublicSPUtil.getInstance().putString(KEY_Token, token);
    }

    public static void setPhone(String phone) {
        PublicSPUtil.getInstance().putString(KEY_Phone, phone);
    }

    public static void setName(String name) {
        PublicSPUtil.getInstance().putString(KEY_Name, name);
    }

    public static void setPWD(String pwd) {
        PublicSPUtil.getInstance().putString(KEY_PWD, pwd);
    }

    public static void deleteinfo(String str) {
        PublicSPUtil.getInstance().delete(str);
    }

    /**
     * todo：分情况对个人配置信息进行清除
     */
    private static void clear(int cause) {
        switch (cause) {
            case clear_all:
                clearUid();
                clearSid();
                clearName();
                clearToken();
                clearPhone();
                clearPWD();
                break;
            case clear_Uid:
                deleteinfo(KEY_Uid);
                break;
            case clear_Sid://删除sid
                deleteinfo(KEY_Sid);
                break;
            case clear_Token:
                deleteinfo(KEY_Token);
                break;
            case clear_Name:
                deleteinfo(KEY_Name);
                break;
            case clear_Phone:
                deleteinfo(KEY_Phone);
                break;
            case clear_pwd:
                deleteinfo(KEY_PWD);
                break;

        }
    }

    public static void clearUid() {
        clear(clear_Uid);
    }

    public static void clearSid() {
        clear(clear_Sid);
    }

    public static void clearName() {
        clear(clear_Name);
    }

    public static void clearToken() {
        clear(clear_Token);
    }

    public static void clearPhone() {
        clear(clear_Phone);
    }
    public static void clearPWD() {
        clear(clear_pwd);
    }
    public static void clearAll() {
        clear(clear_all);
    }

}
