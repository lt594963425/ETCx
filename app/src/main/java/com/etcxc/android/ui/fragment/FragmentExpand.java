package com.etcxc.android.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.MainActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by 刘涛 on 2017/6/17 0017.
 */

public class FragmentExpand extends BaseFragment {
    private MainActivity mActivity;
    private RecyclerView mRecyclerview;
    private MyRecylerAdapter mAdapter;
    private ArrayList<String> mDatas;
    private Handler mHandler = new Handler();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_expand, null);
        mRecyclerview = (RecyclerView) view.findViewById(R.id.expand_recyclerView);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerview.setHasFixedSize(true);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerview);
        initData();
        mHandler.postDelayed(LOAD_DATA,500);
        return view;

    }

    private Runnable LOAD_DATA = new Runnable() {
        @Override
        public void run() {
            mAdapter = new MyRecylerAdapter(mDatas);
            mRecyclerview.setAdapter(mAdapter);
        }
    };
    protected void initData() {
        mDatas = new ArrayList<String>();
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
            View view = LayoutInflater.from(mActivity).inflate( R.layout.item_expand_recylerview,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.mTextView.setText("迅畅在想你" + ":" + datas.get(position));
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser){
            mHandler.removeCallbacks(LOAD_DATA);
        }
    }

    @Override
    public void onResume() {
        mRecyclerview.smoothScrollToPosition(0);
        MobclickAgent.onPageStart("FragmentExpand");
        super.onResume();
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FragmentExpand");
    }
}

