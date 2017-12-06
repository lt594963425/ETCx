package com.etcxc.android.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;

/**
 * Created by ${liuTao} on 2017/8/31/031.
 */

public class HomeRecyclerAdapter extends BaseSelectAdapter {
    private String [] mData;
    private int [] mMages;
    private TextView mTextView;
    private ImageView mImageView;
    public HomeRecyclerAdapter(String [] datas,int[] image){
         this.mData =datas;
         this.mMages =image ;
     }
    @Override
    public void getInitView(View view) {
        mImageView = (ImageView) view.findViewById(R.id.item_home_gv_iv);
        mTextView = (TextView) view.findViewById(R.id.item_home_gv_tv);
    }
    @Override
    public int getItemCount() {
        return mData.length;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_home_gridview;
    }

    @Override
    public void getBindView(BaseSelectAdapter.ViewHolder viewHolder, int position) {
        mTextView.setText(mData[position]);
        mImageView.setImageResource(mMages[position]);
    }
}
