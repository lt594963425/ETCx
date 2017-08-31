package com.etcxc.android.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.bean.OrderRechargeInfo;

import java.util.ArrayList;
import java.util.List;

import static com.etcxc.android.utils.UIUtils.saveInfoList;

/**
 * 订单列表
 * Created by 刘涛 on 2017/7/6 0006.
 */

public class RechargeOrderFormAdapter extends BaseSelectAdapter {
    private List<OrderRechargeInfo> mData;
    private Context con;
    public TextView username;
    public TextView carnumber;
    public TextView etccard;
    public TextView moneynumber;
    public ImageView deletebtn;
    private OnItemRechargeClickListener mOnItemRechargeClickListener = null;
    public RechargeOrderFormAdapter(Context con, List<OrderRechargeInfo> list) {
        this.con = con;
        this.mData = list;
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    @Override
    public void getInitView(View view) {
        username = (TextView) view.findViewById(R.id.item_card_name);
        carnumber = (TextView) view.findViewById(R.id.item_car_number);
        etccard = (TextView) view.findViewById(R.id.item_recharge_card_number);
        moneynumber = (TextView) view.findViewById(R.id.item_money_number);
        deletebtn = (ImageView) view.findViewById(R.id.item_etc_delete_img);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_recharge_form_recylerview;
    }
    @Override
    public void getBindView(ViewHolder viewHolder, int position) {
        OrderRechargeInfo info = mData.get(position);
        if(info != null){
           username.setText(info.getRechargename());
           carnumber.setText(info.getCarnumber());
           etccard.setText(info.getEtccarnumber());
           moneynumber.setText(info.getRechargemoney());
           deletebtn.setTag(position);
        }
    }

    @Override
    public void setItemOnclickListener() {
      deletebtn.setOnClickListener(this);
    }

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

    public void addData(OrderRechargeInfo ord, int poasation, TextView edt) {
        if (poasation == 0) {
            mData = new ArrayList<>();
            mData.add(0, ord);
            saveInfoList(App.get(), mData);
            edt.setText(mData.size()+"");
        } else {
            mData.add(0, ord);
            saveInfoList(App.get(), mData);
            edt.setText(mData.size()+"");
        }
        notifyItemInserted(0);
        notifyItemRangeChanged(0, mData.size());
    }

    public void removeData(int position) {
        if (mData.size() < 1) {
            mData.remove(0);
            notifyDataSetChanged();
        } else if (mData.size() == 0) {
            Toast.makeText(con, R.string.nothing_isempty, Toast.LENGTH_SHORT).show();
        } else {//更新列表
            mData.remove(position);
   //         notifyDataSetChanged();
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mData.size());

        }
    }
}
