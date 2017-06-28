package com.etcxc.android.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.etcxc.android.R;
import com.etcxc.android.ui.view.XToolbar;
import com.etcxc.android.utils.LogUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import okhttp3.OkHttpClient;


/**
 * 封装Activity公共的操作
 */
@SuppressWarnings("ResourceType")
public abstract class BaseActivity extends RxAppCompatActivity {
    protected final OkHttpClient client = new OkHttpClient();
    protected final String TAG = ((Object) this).getClass().getSimpleName();
    private XToolbar mXToolbar;
    private Dialog mDialog;
    protected XToolbar getToolbar() {
        return mXToolbar;
    }

    private void initToolbar() {
        mXToolbar = find(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (mXToolbar != null) setSupportActionBar(mXToolbar);
    }

    protected void setToolbarBack(boolean showBack) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(showBack);
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

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        LogUtil.i(TAG, "----------onCreate----------");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "----------onPause----------");
        if (isFinishing()) LogUtil.i(TAG, "----------onPausem, finishing----------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i(TAG, "----------onStop----------");
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "----------onDestroy----------");
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.i(TAG, "----------onNewIntent----------");
    }

    /** 沉浸式状态栏*/
    protected void initState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    //	加载对话框相关
    private ProgressDialog mProgressDialog;

    protected ProgressDialog getProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        } else {
            mProgressDialog = new ProgressDialog(this);
        }
        return mProgressDialog;
    }

    protected void showProgressDialog(CharSequence message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(message);
        if (!isFinishing() && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void showProgressDialog(CharSequence message, boolean cancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setMessage(message);
        if (!isFinishing() && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void showProgressDialog(int message) {
        showProgressDialog(getText(message));
    }

    protected void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

}
