package com.etcxc.android.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.OrderRechargeInfo;
import com.etcxc.android.ui.adapter.RechargeHistoryRecylerViewAdapter;
import com.etcxc.android.utils.UIUtils;

import java.util.ArrayList;

/**
 * 充值之后的ETC卡记录
 * Created by 刘涛 on 2017/7/6 0006.
 */

public class HistoryRechargeCardActivity extends BaseActivity implements RechargeHistoryRecylerViewAdapter.OnItemRechargeHistoryClickListener{
    private ArrayList<OrderRechargeInfo> mInfoList;
    private RecyclerView mHistoryCardRecylerView;
    private RechargeHistoryRecylerViewAdapter myRechaergeRecylerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_recharge_card);
        initView();
    }
    private void initView() {
        setTitle(R.string.select_recharge_card);
        mHistoryCardRecylerView = find(R.id.hist_recylerview);
        FrameLayout fram_nothing = find(R.id.framlayout_nothing);
        FrameLayout fram_more = find(R.id.framlayout_more);
        mInfoList = new ArrayList<>();
        mInfoList = UIUtils.getInfoList(this);
        if(mInfoList != null&&mInfoList.size()>0){
            fram_nothing.setVisibility(View.INVISIBLE);
            fram_more.setVisibility(View.VISIBLE);
            mHistoryCardRecylerView.setLayoutManager(new LinearLayoutManager(this));
            myRechaergeRecylerViewAdapter = new RechargeHistoryRecylerViewAdapter(this, mInfoList);
            mHistoryCardRecylerView.setAdapter(myRechaergeRecylerViewAdapter);
            myRechaergeRecylerViewAdapter.setmOnItemRechargeHistoryClickListener(this);
        }else {
            fram_nothing.setVisibility(View.VISIBLE);
            fram_more.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemHistoryRechargeClick(View view, int position) {
        TextView textview = (TextView) view.findViewById(R.id.item_recharge_card_number);
        String number = textview.getText().toString();
        Intent intent  = new Intent();
        intent.putExtra("number",number);
        setResult(5,intent);
        finish();
    }
}
