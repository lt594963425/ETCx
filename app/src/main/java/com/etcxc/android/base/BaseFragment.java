package com.etcxc.android.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import com.etcxc.android.net.OkHttpUtils;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * fragment封装
 * Created by xwpeng on 2017/6/21
 */
public abstract class BaseFragment extends RxFragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected BaseActivity mActivity; //在类实例化的时候，Activity是null

    protected void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    protected void setTitle(CharSequence title) {
        if (isUnavailable()) return;
        mActivity.setTitle(title);
    }

    protected boolean isUnavailable() {
        return isRemoving() || isDetached() || getActivity() == null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mActivity.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        super.onDetach();
        OkHttpUtils.cancelTag(this);
    }

    @Override
    public void onDestroy() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    //	加载对话框相关
    private ProgressDialog mProgressDialog;

    protected ProgressDialog getProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
        } else {
            mProgressDialog = new ProgressDialog(mActivity);
        }
        return mProgressDialog;
    }

    protected void showProgressDialog(CharSequence message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mActivity);
        }
        mProgressDialog.setMessage(message);
        if (!isUnavailable() && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    protected void showProgressDialog(CharSequence message, boolean cancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mActivity);
        }
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setMessage(message);
        if (!isUnavailable() && !mProgressDialog.isShowing()) {
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


    private View mView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mView = view;
        super.onViewCreated(view, savedInstanceState);
    }

    protected <T extends View> T find(int id) {
        return (T) mView.findViewById(id);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    protected void openActivity(Class<?> c) {
        if (mActivity != null && c != null) {
            mActivity.openActivity(c);
        }
    }

    protected void openActivityForResult(Class<?> c, int i) {
        if (mActivity != null && c != null) {
            mActivity.openActivityForResult(c, i);
        }
    }

}
