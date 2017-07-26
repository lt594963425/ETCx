package com.etcxc.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.bean.OrderRechargeInfo;

import java.util.List;

/**
 * 充值历史条目Adapter
 * Created by 刘涛 on 2017/7/6 0006.
 */

public class RechargeHistoryAdapter extends RecyclerView.Adapter {
    private List<OrderRechargeInfo> mData;
    private CallBack mCallBack;

    public RechargeHistoryAdapter(List<OrderRechargeInfo> data, CallBack callBack) {
        this.mData = data;
        this.mCallBack = callBack;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_recylerview, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderRechargeInfo info = mData.get(position);
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.userName.setText(info.getRechargename());
        viewHolder.carNumber.setText(info.getCarnumber());
        viewHolder.etcCard.setText(info.getEtccarnumber());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onItemClick(info.getCarnumber());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView carNumber;
        TextView etcCard;
        View itemView;

        public ItemViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.item_card_name);
            carNumber = (TextView) view.findViewById(R.id.item_car_number);
            etcCard = (TextView) view.findViewById(R.id.item_recharge_card_number);
            itemView = view;
        }
    }

    public interface CallBack {
        void onItemClick(String number);
    }
}
