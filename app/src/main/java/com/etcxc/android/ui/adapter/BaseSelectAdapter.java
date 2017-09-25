package com.etcxc.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 刘涛 on 2017/7/6 0006.
 */
public abstract class BaseSelectAdapter extends RecyclerView.Adapter<BaseSelectAdapter.ViewHolder> implements View.OnClickListener {

    private OnItemClickListener mOnItemClickListener = null;
    private View mView;
    //获取数据的数量
    @Override
    public abstract int getItemCount();
    public abstract void  getInitView(View view);

    public abstract int getLayoutResId();

    public abstract void getBindView(ViewHolder viewHolder, int position);

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mView = LayoutInflater.from(viewGroup.getContext()).inflate(getLayoutResId(), viewGroup, false);
        ViewHolder viewHolder =new ViewHolder(mView);
        setItemOnclickListener();
        return  viewHolder;
    }

    public  void setItemOnclickListener() {
        mView.setOnClickListener(this);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.itemView.setTag(position);
        getBindView(viewHolder, position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            getInitView(view);
        }
    }

}
