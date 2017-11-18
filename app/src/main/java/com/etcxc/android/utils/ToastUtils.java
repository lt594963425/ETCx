package com.etcxc.android.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.etcxc.android.base.App;

/**
 * toast封装，还可以改进
 * Created by 刘涛 on 2017/5/27 0027.
 */

public class ToastUtils {
    private static Toast toast;

    public static void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(App.get(), msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(int resId) {
       showToast(App.get().getString(resId));
    }



}
