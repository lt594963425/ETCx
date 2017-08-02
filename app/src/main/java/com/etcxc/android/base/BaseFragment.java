package com.etcxc.android.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;

import com.etcxc.android.R;
import com.trello.rxlifecycle2.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * fragment封装
 * Created by xwpeng on 2017/6/21
 */
public abstract class BaseFragment extends RxFragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected BaseActivity mActivity = (BaseActivity) getActivity();

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
    @SuppressWarnings("unchecked")
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
    protected void openActivity(Class<?> pClass) {
        Intent mIntent = new Intent(getActivity(), pClass);
        this.startActivity(mIntent);
        getActivity().overridePendingTransition(R.anim.zoom_enter,R.anim.no_anim);
    }

    protected void openActivityForResult(Class<?> pClass, int i) {
        Intent mIntent = new Intent(getActivity(), pClass);
        this.startActivityForResult(mIntent, i);
        getActivity().overridePendingTransition(R.anim.zoom_enter,R.anim.no_anim);
    }
}
