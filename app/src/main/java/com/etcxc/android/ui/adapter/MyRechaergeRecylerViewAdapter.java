package com.etcxc.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.bean.OrderRechargeInfo;

import java.util.ArrayList;

import static com.etcxc.android.utils.UIUtils.saveInfoList;

/**
 * Created by 刘涛 on 2017/7/6 0006.
 */

public class MyRechaergeRecylerViewAdapter extends RecyclerView.Adapter<MyRechaergeRecylerViewAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<OrderRechargeInfo> list  = null ;
    private Context con;
    private OnItemRechargeClickListener mOnItemRechargeClickListener = null;

    @Override
    public void onClick(View v) {
        if (mOnItemRechargeClickListener != null) {
            mOnItemRechargeClickListener.onItemRechargeClick((ImageView) v, (Integer) v.getTag());
        }
    }

    public void setmOnItemRechargeClickListener(OnItemRechargeClickListener listener) {
        this.mOnItemRechargeClickListener = listener;
    }

    public interface OnItemRechargeClickListener {
        void onItemRechargeClick(ImageView view, int position);
    }

    public MyRechaergeRecylerViewAdapter(Context con, ArrayList<OrderRechargeInfo> list) {
            this.con = con;
            this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recharge_form_recylerview, parent, false);
        ViewHolder holer = new ViewHolder(view);
        holer.deletebtn.setOnClickListener(this);
        return holer;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OrderRechargeInfo info = list.get(position);
        if(info != null){
            holder.username.setText(info.getRechargename());
            holder.carnumber.setText(info.getCarnumber());
            holder.etccard.setText(info.getEtccarnumber());
            holder.moneynumber.setText(info.getRechargemoney());
            holder.deletebtn.setTag(position);
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

    public void addData(OrderRechargeInfo ord, int poasation, TextView edt) {
        if (poasation == 0) {
            list = new ArrayList<>();
            list.add(0, ord);
            saveInfoList(App.get(), list);
            edt.setText(list.size()+"");
        } else {
            list.add(0, ord);
            saveInfoList(App.get(), list);
            edt.setText(list.size()+"");
        }
        notifyItemInserted(0);
        notifyItemRangeChanged(0, list.size());
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
