package com.etcxc.android.holdler;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 刘涛 on 2017/5/27 0027.
 */

public class LoadMoreHolder  extends BaseHolder<Integer>{
    public static final int LOADMORE_LOADING = 0;//正在加载更多
    public static final int LOADMORE_ERROR = 1;//加载更多失败,点击重试
    public static final int LOADMORE_NONE = 2;//没有加载更多

    @BindView(R.id.item_loadmore_container_loading)
    LinearLayout mItemLoadmoreContainerLoading;
    @BindView(R.id.item_loadmore_tv_retry)
    TextView mItemLoadmoreTvRetry;
    @BindView(R.id.item_loadmore_container_retry)
    LinearLayout mItemLoadmoreContainerRetry;

    @Override
    public View initHolderView() {
        View holderView = View.inflate(UIUtils.getContext(), R.layout.item_loadmore, null);
        ButterKnife.bind(this, holderView);
        return holderView;
    }

    /**
     * 刷新UI
     * 传递进来的数据类型有什么用?-->决定ui的具体展现
     */
    @Override
    public void refreshHolderView(Integer curState) {
        //首先隐藏所有的视图
        mItemLoadmoreContainerLoading.setVisibility(View.GONE);
        mItemLoadmoreContainerRetry.setVisibility(View.GONE);
        switch (curState) {
            case LOADMORE_LOADING:
                mItemLoadmoreContainerLoading.setVisibility(View.VISIBLE);
                break;
            case LOADMORE_ERROR:
                mItemLoadmoreContainerRetry.setVisibility(View.VISIBLE);
                break;
            case LOADMORE_NONE:
                break;
            default:
                break;
        }
    }
}
