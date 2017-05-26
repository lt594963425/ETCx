package com.itheima.googleplay.base;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * 创建者     伍碧林
 * 版权       传智播客.黑马程序员
 * 描述	      针对BaseAdapter简单封装,针对的是其中的3个方法(getCount,getItem,getItemId)
 */
public abstract class MyBaseAdapter<ITEMBEANTYPE> extends BaseAdapter {
    private List<ITEMBEANTYPE> mDataSets;

    public MyBaseAdapter(List<ITEMBEANTYPE> dataSets) {
        mDataSets = dataSets;
    }

    @Override
    public int getCount() {
        if (mDataSets != null) {
            return mDataSets.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mDataSets != null) {
            return mDataSets.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
