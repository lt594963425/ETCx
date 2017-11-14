package com.etcxc.android.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.OrderRechargeInfo;
import com.etcxc.android.ui.adapter.RechargeHistoryAdapter;
import com.etcxc.android.utils.UIUtils;

import java.util.ArrayList;

/**
 * 充值之后的ETC卡记录
 * @author 刘涛
 * @date 2017/7/6 0006
 */

public class HistoryRechargeCardActivity extends BaseActivity implements RechargeHistoryAdapter.CallBack {
    private ArrayList<OrderRechargeInfo> mData = new ArrayList<>();
    private RecyclerView mRecylerView;
    private RechargeHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_recharge_card);
        initView();
    }

    private void initView() {
        setTitle(R.string.select_recharge_card);
        mRecylerView = find(R.id.hist_recylerview);
        FrameLayout fram_nothing = find(R.id.framlayout_nothing);
        FrameLayout fram_more = find(R.id.framlayout_more);
        mData.addAll(UIUtils.getInfoList(this));
        if (mData.size() > 0) {
            fram_nothing.setVisibility(View.INVISIBLE);
            fram_more.setVisibility(View.VISIBLE);
            mRecylerView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new RechargeHistoryAdapter(mData, this);
            mRecylerView.setAdapter(mAdapter);
        } else {
            fram_nothing.setVisibility(View.VISIBLE);
            fram_more.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(String number) {
        Intent intent = new Intent();
        intent.putExtra("number", number);
        setResult(5, intent);
        finish();
    }
}
