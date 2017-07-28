package com.etcxc.android.modle.sp;

import android.content.Context;

import com.etcxc.android.base.App;

/**
 * 未登录账号使用的SharedPreferences工具类
 * Created by xwpeng on 2107/5/26.<br/>
 */
public class PublicSPUtil extends BasicSPUtil {
    private PublicSPUtil(Context context) {
        super(context);
        initSP();
    }

    @Override
    protected String spFileName() {
        return "sp_public";
    }

    public static PublicSPUtil getInstance() {
        return InstanceInner.instance;
    }

    private static class InstanceInner {
        private static PublicSPUtil instance = new PublicSPUtil(App.get());
    }
}
