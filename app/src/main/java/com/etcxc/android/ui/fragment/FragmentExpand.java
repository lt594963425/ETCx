package com.etcxc.android.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.proxy.ThreadPoolProxyFactory;
import com.etcxc.android.ui.view.ItemOffsetDecoration;
import com.etcxc.android.utils.LogUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * 拓展
 * Created by LiuTao on 2017/6/17 0017.
 */

public class FragmentExpand extends BaseFragment {
    private RecyclerView mRecyclerview;
    private ArrayList<String> mDatas;
    private Future mFuture;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expand, null);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.expand_recyclerView);
        final SwipeRefreshLayout mRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_expand);
        ThreadPoolProxyFactory.getNormalThreadPoolProxy().submit(LOAD_DATA);
        return view;
    }
    private Runnable LOAD_DATA = new Runnable() {
        @Override
        public void run() {
            initData();
            setupRecyclerView();
            runLayoutAnimation(mRecyclerview);
        }
    };
    private void setupRecyclerView() {
        MyRecylerAdapter mAdapter = new MyRecylerAdapter(mDatas);
        mRecyclerview.setAdapter(mAdapter);
        mRecyclerview.addItemDecoration(new ItemOffsetDecoration(getResources().getDimensionPixelOffset(R.dimen.default_spacing_small)));
    }
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_animation_from_bottom);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    protected void initData() {
        mDatas = new ArrayList<>();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add("" + (char) i);
        }
    }

    class MyRecylerAdapter extends RecyclerView.Adapter<MyRecylerAdapter.ViewHolder> {
        public ArrayList<String> datas = null;

        public MyRecylerAdapter(ArrayList<String> datas) {
            this.datas = datas;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_expand_recylerview, parent, false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText("迅畅在线" + ":" + datas.get(position));
        }


        @Override
        public int getItemCount() {
            return datas.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mTextView = (TextView) view.findViewById(R.id.item_recylear_iv);
            }
        }
    }
    
    @Override
    public void onResume() {
        LogUtil.e(TAG,"onResume");
        mRecyclerview.smoothScrollToPosition(0);
        MobclickAgent.onPageStart("FragmentExpand");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentExpand");
    }


}

