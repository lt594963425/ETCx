package com.etcxc.android.base;

import android.view.MenuItem;

import com.trello.rxlifecycle2.components.support.RxFragment;

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

}
