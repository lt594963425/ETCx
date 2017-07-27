package com.etcxc.android.modle.sp;

import android.content.Context;
import android.content.SharedPreferences;

import com.etcxc.android.utils.Base64Util;

/**
 * 封装SPUtil的基础类
 * 只适用于单进程
 * Created by xwpeng on 2107/5/26.<br/>
 */
public abstract class BasicSPUtil {

    protected final String TAG = ((Object) this).getClass().getSimpleName();

    private SharedPreferences mSP;
    private Context mContext;

    public BasicSPUtil(Context context) {
        this.mContext = context;
    }

    protected void initSP() {
        mSP = mContext.getSharedPreferences(spFileName(), Context.MODE_PRIVATE);
    }

    protected abstract String spFileName();

    public void  delete (String key){
        mSP.edit().remove(Base64Util.encode(key)).commit();
    }

    public void putBoolean(String key, boolean value) {
        mSP.edit().putBoolean(Base64Util.encode(key), value).commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSP.getBoolean(Base64Util.encode(key), defValue);
    }

    public void putFloat(String key, float value) {
        mSP.edit().putFloat(Base64Util.encode(key), value).commit();
    }

    public float getFloat(String key, float defValue) {
        return mSP.getFloat(Base64Util.encode(key), defValue);
    }

    public void putInt(String key, int value) {
        mSP.edit().putInt(Base64Util.encode(key), value).commit();
    }

    public int getInt(String key, int defValue) {
        return mSP.getInt(Base64Util.encode(key), defValue);
    }

    public void putLong(String key, long value) {
        mSP.edit().putLong(Base64Util.encode(key), value).commit();
    }

    public long getLong(String key, long defValue) {
        return mSP.getLong(Base64Util.encode(key), defValue);
    }

    public void putString(String key, String value) {
        mSP.edit().putString(Base64Util.encode(key), value).commit();
    }

    public String getString(String key, String defValue) {
        return mSP.getString(Base64Util.encode(key), defValue);
    }
}
