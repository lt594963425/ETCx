package com.etcxc.android.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.widget.TextView;

import com.etcxc.android.base.App;

/**
 * Created by 刘涛  on 2017/5/27 0027.
 *  封装和ui相关的操作
 */
public class UIUtils {
    /**
     * 得到上下文
     */
    public static Context getContext() {
        return App.get();
    }

    /**
     * 得到Resource对象
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 得到String.xml中的字符串信息
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 得到String.xml中的字符串数组信息
     */
    public static String[] getStrings(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 得到Color.xml中的颜色信息
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /**
     * 得到应用程序包名
     */
    public static String getPackageName() {
        return getContext().getPackageName();
    }
    /**
     * dip-->px
     */
    public static int dip2Px(int dip) {
        // px/dp = density
        //取得当前手机px和dp的倍数关系
        float density = getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + .5f);
        return px;
    }

    public static int px2Dip(int px) {
        // px/dp = density
        //取得当前手机px和dp的倍数关系
        float density = getResources().getDisplayMetrics().density;
        int dip = (int) (px / density + .5f);
        return dip;
    }


    public final static int LEFT = 0, RIGHT = 1, TOP = 3, BOTTOM = 4 ;
    public static void addIcon(TextView view, int resId, int orientation) {
        if (view == null ) return;
        VectorDrawableCompat drawable = VectorDrawableCompat.create(App.get().getResources(), resId, null);
        switch (orientation) {
            case LEFT:
                view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                break;
            case RIGHT:
                view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                break;
            case TOP:
                view.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                break;
            case BOTTOM:
                view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                break;
        }
        view.setCompoundDrawablePadding(UIUtils.dip2Px(16));
    }
}
