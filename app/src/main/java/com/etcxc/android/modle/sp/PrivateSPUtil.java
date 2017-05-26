package com.etcxc.android.modle.sp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.etcxc.MeManager;
import com.etcxc.android.CApp;
import com.etcxc.android.util.Base64Util;

/**
 * fixme:单例写法
 * 账号登录后创建的{@link android.content.SharedPreferences}工具类
 * Created by xwpeng on 2107/5/26.<br/>
 */
public class PrivateSPUtil extends BasicSPUtil {
    private String mUid;

    private PrivateSPUtil(@Nullable String uid) {
            super(CApp.get());
            this.mUid = Base64Util.encode(uid);
            initSP();
    }

    @NonNull
    public static PrivateSPUtil newInstance() {
        String uid = MeManager.getUid();
        return PrivateSPUtil.newInstance(uid);
    }

    @NonNull
    private static PrivateSPUtil newInstance(@Nullable String uid) {
        return new PrivateSPUtil(uid);
    }

    @Override
    protected String spFileName() {
        return "sp_" + mUid;
    }
}
