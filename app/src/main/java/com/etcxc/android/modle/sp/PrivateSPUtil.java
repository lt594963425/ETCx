package com.etcxc.android.modle.sp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.etcxc.MeManager;
import com.etcxc.android.base.App;
import com.etcxc.android.utils.Base64Util;

/**
 * 账号登录后创建的{@link android.content.SharedPreferences}工具类
 * Created by xwpeng on 2107/5/26.<br/>
 */
public class PrivateSPUtil extends BasicSPUtil {
    private String mUid;

    private PrivateSPUtil(@Nullable String uid) {
        super(App.get());
        this.mUid = Base64Util.encode(uid);
    }

    @NonNull
    public static PrivateSPUtil newInstance() {
        return InstanceInner.instance;
    }

    @NonNull
    private static PrivateSPUtil newInstance(@Nullable String uid) {
        return new PrivateSPUtil(uid);
    }

    @Override
    protected String spFileName() {
        return "sp_" + mUid;
    }

    private static class InstanceInner {
        private static PrivateSPUtil instance = new PrivateSPUtil(MeManager.getUid());
    }
}
