package com.etcxc.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.bean.AddressBean;
import com.etcxc.android.utils.UIUtils;

import java.util.List;

/**
 * 地区选择适配器
 * Created by xwpeng on 2017/6/21.
 */

public class SelectRegionAdapter extends RecyclerView.Adapter {
    private List<AddressBean> mDatas;
    private CallBack mCallback;

    public SelectRegionAdapter(List<AddressBean> datas, CallBack callBack) {
        this.mDatas = datas;
        this.mCallback = callBack;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_region, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        AddressBean addressBean = new AddressBean();
        addressBean = mDatas.get(position);
        String region = addressBean.getName();
        String code = addressBean.getCode();
        UIUtils.addIcon(viewHolder.regionView, R.drawable.vd_right_arrow, UIUtils.RIGHT);
        viewHolder.regionView.setText(region);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onItemClick(code,region);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView regionView;
        View itemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            regionView = (TextView) itemView.findViewById(R.id.item_region_textview);
            this.itemView = itemView;
        }
    }

    public interface CallBack {
        void onItemClick(String code,String content);
    }

}
