package com.etcxc.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.base.App;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 刘涛  on 2017/5/27 0027.
 * 封装和ui相关的操作
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


    public final static int LEFT = 0, RIGHT = 1, TOP = 3, BOTTOM = 4;

    public static void addIcon(TextView view, int resId, int orientation) {
        addIcon(view, resId, orientation, 16);
    }

    public static void addIcon(TextView view, int resId, int orientation, int padding) {
        if (view == null) return;
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
        view.setCompoundDrawablePadding(UIUtils.dip2Px(padding));
    }

    /**
     * @param v1 你要控制显示和隐藏内容的View
     * @param v2  你所点击的可见与不可见的View
     * @param sid1 不可见View
     * @param sid2 可见View
     */
    private static boolean flag;

    public static void isLook(TextView v1, ImageView v2, int sid1, int sid2) {
        v1.setHorizontallyScrolling(true);//不可换行
        if (flag == true) {
            v1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            flag = false;
            v2.setImageResource(sid1);
        } else {
            v1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            flag = true;
            v2.setImageResource(sid2);
        }
    }

    /**
     * 保存历史号码
     * @param context
     * @param field
     * @param text
     */
    public static void saveHistory(Context context,String field, String text) {
        SharedPreferences sp = context.getSharedPreferences("phone_history", 0);
        String phonehistory = sp.getString(field, "nothing");
        if (!phonehistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(phonehistory);
            sb.insert(0, text + ",");
            sp.edit().putString("history", sb.toString()).commit();
        }
    }
    public static void initAutoComplete(Context context,String field,AutoCompleteTextView auto) {
        SharedPreferences sp = context.getSharedPreferences("phone_history", 0);
        String longhistory = sp.getString(field, "");
        String[]  hisArrays = longhistory.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, hisArrays);
        if(hisArrays.length > 50){
            String[] newArrays = new String[50];
            System.arraycopy(hisArrays, 0, newArrays, 0, 50);
            adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, newArrays);
        }
        auto.setAdapter(adapter);
        auto.setDropDownHeight(350);
        auto.setThreshold(2);
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView  view = (AutoCompleteTextView ) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
    }
    /**
     * 判断手机号码是否正确
     */
    public static boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        String regExp = "((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(15([0-3]|[5-9]))|(18[0-9]))\\d{8}$";//;
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}