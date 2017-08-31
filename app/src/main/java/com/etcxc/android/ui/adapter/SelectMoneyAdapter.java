package com.etcxc.android.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.etcxc.android.R;

/**
 * 充值金额选择
 * Created by 刘涛 on 2017/7/6 0006.
 */
public class SelectMoneyAdapter extends BaseSelectAdapter{
    private TextView mTextView;
    private  String[]  mDatas;

    public SelectMoneyAdapter(String[] datas) {
        this.mDatas = datas;
    }

    @Override
    public int getItemCount() {
        return mDatas.length;
    }

    @Override
    public void getInitView(View view) {
        mTextView = (TextView) view.findViewById(R.id.item_select_money_tv);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_etc_rechargemoney_recylerview;
    }

    @Override
    public void getBindView(ViewHolder viewHolder, int position) {
        mTextView.setText(mDatas[position]);

    }

}
