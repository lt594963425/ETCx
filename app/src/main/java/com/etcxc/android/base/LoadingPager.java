package com.etcxc.android.base;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.etcxc.android.R;
import com.etcxc.android.activity.ProductListActivity;
import com.etcxc.android.factory.ThreadPoolProxyFactory;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.proxy.ThreadPoolProxy;

/**
 * Created by 刘涛 on 2017/5/28 0028.
 */

public abstract class LoadingPager extends FrameLayout{
    private View mEmptyView;
    private View mErrorView;
    private View mLoadingView;

    public final static int STATE_LOADING = 0;
    public final static int STATE_EMPTY = 1;
    public final static int STATE_ERROR = 2;
    public final static int STATE_SUCCESS = 3;
    private Button mGoShopping;

    /**
     * 加载结果的枚举类
     */
    public enum LoadedResult {
        SUCCESS(STATE_SUCCESS), ERROR(STATE_ERROR), EMPTY(STATE_EMPTY);

        private int state;

        public int getState() {
            return state;
        }

        LoadedResult(int state) {
            this.state = state;
        }
    }

    private int curState = STATE_LOADING;
    private View mSuccessView;
    private LoadTask mLoadTask;
    private Button mButton;

    //只在代码里使用
    public LoadingPager(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        //加载页
        mLoadingView = View.inflate(getContext(), R.layout.pager_loading, null);
        this.addView(mLoadingView);
        //空白页
        mEmptyView = View.inflate(getContext(), R.layout.pager_empty, null);
        mGoShopping = (Button) mEmptyView.findViewById(R.id.shopcar_toBuy_text);
        mGoShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UIUtils.getContext(), ProductListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                UIUtils.getContext().startActivity(intent);
            }
        });
        this.addView(mEmptyView);
        //错误页
        mErrorView = View.inflate(getContext(), R.layout.pager_error, null);
        mButton = (Button) mErrorView.findViewById(R.id.error_btn_retry);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                triggerLoadData();
            }
        });
        this.addView(mErrorView);

        refreshViewByState();
    }

    private void refreshViewByState() {
        //等待视图
        if (curState == STATE_LOADING) {
            mLoadingView.setVisibility(VISIBLE);
        } else {
            mLoadingView.setVisibility(GONE);
        }
        //空视图
        if (curState == STATE_EMPTY) {
            mEmptyView.setVisibility(VISIBLE);
        } else {
            mEmptyView.setVisibility(GONE);
        }
        //错误视图
        if (curState == STATE_ERROR) {
            mErrorView.setVisibility(VISIBLE);
        } else {
            mErrorView.setVisibility(GONE);
        }
        //成功视图
        if (curState == STATE_SUCCESS) {
            mSuccessView = initSuccessView();
            this.addView(mSuccessView);
        }
        if (mSuccessView != null) {
            if (curState == STATE_SUCCESS) {
                mSuccessView.setVisibility(VISIBLE);
            } else {
                mSuccessView.setVisibility(GONE);
            }
        }
    }

    /**
     * 触发加载数据
     */
    public void triggerLoadData() {
        if (curState != STATE_SUCCESS) {
            //避免新建任务重新加载
            if (mLoadTask == null) {
                curState = STATE_LOADING;
                refreshViewByState();

                mLoadTask = new LoadTask();
                ThreadPoolProxy normalThreadPoolProxy = ThreadPoolProxyFactory.getNormalThreadPoolProxy();
                normalThreadPoolProxy.submit(mLoadTask);
            }
        }
    }

    /**
     * 不论是否是成功视图都重新加载
     * @param b
     */
    public void triggerLoadData(boolean b) {
        if (mLoadTask == null) {
            curState = STATE_LOADING;
            refreshViewByState();

            mLoadTask = new LoadTask();
            ThreadPoolProxy normalThreadPoolProxy = ThreadPoolProxyFactory.getNormalThreadPoolProxy();
            normalThreadPoolProxy.submit(mLoadTask);
        }
    }

    private class LoadTask implements Runnable {

        @Override
        public void run() {
            LoadedResult loadedResult = initData();

            curState = loadedResult.getState();

            App.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    refreshViewByState();
                }
            });
            mLoadTask = null;
        }
    }

    /**
     * 接收/加载数据
     *
     * @return
     */
    public abstract LoadedResult initData();

    /**
     * 得到成功视图
     *
     * @return
     */
    public abstract View initSuccessView();
}
