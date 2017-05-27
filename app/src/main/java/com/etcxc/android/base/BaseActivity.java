package com.etcxc.android.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.etcxc.android.util.LogUtil;

/**
 * 封装Activity公共的操作
 */
@SuppressWarnings("ResourceType")
public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = ((Object) this).getClass().getSimpleName();

    protected <T extends View> T find(int id) {
        return (T) super.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "----------onNewIntent----------");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "----------onPause----------");
        if (isFinishing()) {
            LogUtil.i(TAG, "----------onPausem, finishing----------");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "----------onStop----------");
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "----------onDestroy----------");
        super.onDestroy();
    }

    @Override
    public void setContentView(@IdRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.i(TAG, "----------onNewIntent----------");
    }

    /**
     * 隐藏输入法
     *
     * @param view
     */
    protected void hideInputMethod(TextView view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 显示输入法
     *
     * @param view
     */
    protected void showInputMethod(TextView view) {
        if (view == null) {
            return;
        }
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager m = (InputMethodManager) getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        m.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        // m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        // (这个方法可以实现输入法在窗口上切换显示，如果输入法在窗口上已经显示，则隐藏，如果隐藏，则显示输入法到窗口上)
    }

    /**
     * 获取系统attr值
     *
     * @param resId
     * @return
     */
    protected int getAttrValue(int resId) {
        TypedValue value = new TypedValue();
        getTheme().resolveAttribute(resId, value, true);
        return value.data;
    }
}
