package com.etcxc.android.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by 刘涛 on 2017/5/27 0027.
 */

public class ToastUtils {
    private static Toast toast;

    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}
