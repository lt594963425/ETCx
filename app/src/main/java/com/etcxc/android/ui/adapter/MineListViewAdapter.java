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
 * Created by 刘涛 on 2017/7/1 0001.
 */

public class MineListViewAdapter extends BaseAdapter {
    private int [] image ;
    private String [] title ;
    private Context context;
    public MineListViewAdapter( Context context,int[] image, String[] title) {
        this.image = image;
        this.title = title;
        this.context =context;
    }
    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate( R.layout.item_mine_listview,parent,false);
        ImageView iv = (ImageView) view.findViewById(R.id.mine_lv_iv);
        TextView tv = (TextView) view.findViewById(R.id.mine_lv_tv);
        iv.setImageResource(image[position]);
        tv.setText(title[position]);
        return view;
    }

}
