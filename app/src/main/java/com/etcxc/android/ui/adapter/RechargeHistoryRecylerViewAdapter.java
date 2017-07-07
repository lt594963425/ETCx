package com.etcxc.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.bean.OrderRechargeInfo;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by 刘涛 on 2017/7/6 0006.
 */

public class RechargeHistoryRecylerViewAdapter extends RecyclerView.Adapter<RechargeHistoryRecylerViewAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<OrderRechargeInfo> list  = null ;
    private Context con;
    private OnItemRechargeHistoryClickListener mOnItemRechargeHistoryClickListener = null;

    @Override
    public void onClick(View v) {
        if (mOnItemRechargeHistoryClickListener != null) {
            mOnItemRechargeHistoryClickListener.onItemHistoryRechargeClick(v, (int) v.getTag());
        }
    }

    public void setmOnItemRechargeHistoryClickListener(OnItemRechargeHistoryClickListener listener) {
        this.mOnItemRechargeHistoryClickListener = listener;
    }

    public interface OnItemRechargeHistoryClickListener {
        void onItemHistoryRechargeClick(View view, int position);
    }
    public RechargeHistoryRecylerViewAdapter(Context con, ArrayList<OrderRechargeInfo> list) {
            this.con = con;
            this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_recylerview, parent, false);
        ViewHolder holer = new ViewHolder(view);
        holer.itemView.setOnClickListener(this);
        return holer;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderRechargeInfo info = list.get(position);
        if(info != null){
            holder.username.setText(info.getRechargename());
            holder.carnumber.setText(info.getCarnumber());
            holder.etccard.setText(info.getEtccarnumber());
            holder.itemView.setTag(position);
        }
    }
    @Override
    public int getItemCount() {
        if (list != null && list.size() > 0){
            return list.size();
        }
            return 0;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView carnumber;
        public TextView etccard;


        public ViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.item_card_name);
            carnumber = (TextView) view.findViewById(R.id.item_car_number);
            etccard = (TextView) view.findViewById(R.id.item_recharge_card_number);

        }
    }

    public void addData(OrderRechargeInfo ord,int poasation) {
        if (poasation == 0) {
            list = new ArrayList<>();
            list.add(0, ord);
            UIUtils.saveInfoList(App.get(), list);
        } else {
            list.add(0, ord);
            UIUtils.saveInfoList(App.get(), list);
        }
        notifyItemInserted(0);
        notifyItemRangeChanged(0, list.size());
        ToastUtils.showToast(R.string.add_success);
    }

    public void removeData(int position) {
        if (list.size() < 1 && list.size() != 0) {
            list.remove(0);
            notifyDataSetChanged();
        } else if (list.size() == 0) {//当列表没有数据提示用户，免得造成系统崩溃
            Toast.makeText(con, R.string.nothing_isempty, Toast.LENGTH_SHORT).show();
        } else {//更新列表
            list.remove(position);
            notifyDataSetChanged();
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());

        }
    }
}
