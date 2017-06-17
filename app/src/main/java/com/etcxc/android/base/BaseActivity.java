package com.etcxc.android.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.etcxc.android.ui.view.XToolbar;
import com.etcxc.android.utils.LogUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.List;


/**
 * 封装Activity公共的操作
 */
@SuppressWarnings("ResourceType")
public abstract class BaseActivity extends RxAppCompatActivity {
    protected final String TAG = ((Object) this).getClass().getSimpleName();
    private XToolbar mXToolbar;

    protected XToolbar getToolbar() {
        return mXToolbar;
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        if (mXToolbar != null) {
            mXToolbar.setTitle(titleId);
        }
    }
    @Override
    public void setTitle(CharSequence title) {
        if (mXToolbar != null) {
            mXToolbar.setTitle(title);
        }
    }

    public void setSubtitle(@StringRes int subtitleId) {
        if (mXToolbar != null) {
            mXToolbar.setSubtitle(subtitleId);
        }
    }

    public void setSubtitle(CharSequence subtitle) {
        if (mXToolbar != null) {
            mXToolbar.setSubtitle(subtitle);
        }
    }

    protected void setTitleView(View view) {
        if (view != null && mXToolbar != null) mXToolbar.setView(view);
    }

    protected void setToolbarClickEventActivated(boolean activated) {
        if (mXToolbar != null) mXToolbar.setClickEventActivated(activated);
    }

    private void initToolbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION这行加上去NoticesFragment不能滚到最底(差一点)，这行去掉带来的问题：导航栏不能半透明 at 20150928
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);//TODO 待改 20150930
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.my));
//            window.setNavigationBarColor(getResources().getColor(R.color.my));
        }

       // mXToolbar = find(R.id.toolbar);
        if (mXToolbar != null) {
            setSupportActionBar(mXToolbar);
            mXToolbar.setActionBar(getSupportActionBar());
            mXToolbar.setOnToolbarTitleClickListener(new XToolbar.OnToolbarTitleClickListener() {
                @Override
                public void onToolbarTitleClick() {
                    onTitleClick();
                }
            });
            mXToolbar.setOnToolbarTitleDoubleClickListener(new XToolbar.OnToolbarTitleDoubleClickListener() {
                @Override
                public void onToolbarTitleDoubleClick() {
                    onTitleDoubleClick();
                }
            });

        }
    }

    protected void onTitleClick() {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        if (list == null || list.isEmpty()) {
            return;
        }
        for (Fragment f : list) {
            if (f instanceof BaseFragment) {
                //todo :调好fragment再弄
            }
        }
    }

    protected void onTitleDoubleClick() {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        if (list == null || list.isEmpty()) {
            return;
        }
        for (Fragment f : list) {
            if (f instanceof BaseFragment) {
                //todo :调好fragment再弄
            }
        }
    }

    protected void setToolbarBack(boolean showBack) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(showBack);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected <T extends View> T find(int id) {
        return (T) super.findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        //initState();
        LogUtil.i(TAG, "----------onNewIntent----------");
    }
    /**
     * 沉浸式状态栏
     */
    private void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
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
        initToolbar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initToolbar();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initToolbar();
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
