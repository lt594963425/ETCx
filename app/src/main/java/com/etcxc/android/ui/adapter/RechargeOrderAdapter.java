package com.etcxc.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.bean.OrderRechargeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 刘涛
 * @date 2017/7/6 0006
 */
public class RechargeOrderAdapter extends RecyclerView.Adapter<RechargeOrderAdapter.ViewHolder> implements View.OnClickListener {
    private List<OrderRechargeInfo> mData;
    private OnItemDeleteClickListener mOnItemDeleteClickListener = null;
    private View mView;
    private static final String TAG = "RechargeOrderAdapter";

    public RechargeOrderAdapter() {
        this.mData = new ArrayList<>();
    }

    public void updateData(List<OrderRechargeInfo> data) {
        this.mData.clear();
        this.setData(data);
        this.notifyDataSetChanged();
    }

    public void setData(List<OrderRechargeInfo> data) {
        if (data != null) {
            this.mData.addAll(data);
        }
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItemOnclickListener() {
        ViewHolder viewHolder = new ViewHolder(mView);
        viewHolder.deletebtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemDeleteClickListener != null) {
            mOnItemDeleteClickListener.onItemDeleteClick((ImageView) v, (Integer) v.getTag());
        }
    }

    public void setmOnItemDeleteClickListener(RechargeOrderAdapter.OnItemDeleteClickListener listener) {
        this.mOnItemDeleteClickListener = listener;
    }

    public interface OnItemDeleteClickListener {
        void onItemDeleteClick(ImageView view, int position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recharge_form_recylerview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(mView);
        setItemOnclickListener();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.itemView.setTag(position);
        OrderRechargeInfo info = mData.get(position);
        if (info != null) {
            viewHolder.username.setText(info.getRechargename());
            viewHolder.carnumber.setText(info.getLicenseplate());
            viewHolder.etccard.setText("卡号:" + info.getEtccarnumber());
            viewHolder.moneynumber.setText(info.getRechargemoney() / 100.00 + "");
            viewHolder.deletebtn.setTag(position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView carnumber;
        public TextView etccard;
        public TextView moneynumber;
        public ImageView deletebtn;

        public ViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.item_card_name);
            carnumber = (TextView) view.findViewById(R.id.item_car_number);
            etccard = (TextView) view.findViewById(R.id.item_recharge_card_number);
            moneynumber = (TextView) view.findViewById(R.id.item_money_number);
            deletebtn = (ImageView) view.findViewById(R.id.item_etc_delete_img);

        }
    }

}
