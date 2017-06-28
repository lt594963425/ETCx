package com.etcxc.android.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;

/**
 * Created by 刘涛 on 2017/6/27 0027.
 */

    public class MyGridViewAdapter extends BaseAdapter {
    private int [] image ;
    private String [] title ;
    private Context context;
    public MyGridViewAdapter(int[] image, String[] title, Context context) {
        this.image = image;
        this.title = title;
        this.context =context;
    }

    @Override
    public int getCount() {
        return image.length;
    }
    @Override
    public Object getItem(int item) {
        return null;
    }
    @Override
    public long getItemId(int id) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewGV = LayoutInflater.from(context).inflate( R.layout.item_home_gridview,parent,false);
        ImageView iv_home_gn = (ImageView) viewGV.findViewById(R.id.item_home_gv_iv);
        TextView tv_item_title = (TextView) viewGV.findViewById(R.id.item_home_gv_tv);
        iv_home_gn.setImageResource(image[position]);
        tv_item_title.setText(title[position]);
        return viewGV;
    }
}
