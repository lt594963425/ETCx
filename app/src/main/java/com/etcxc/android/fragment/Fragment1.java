package com.etcxc.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.utils.UIUtils;

/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class Fragment1 extends Fragment implements View.OnClickListener {
    private TextView textView1;
    private TextView textView2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item1, null);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        initView();
        return view;
    }

    private void initView() {
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView1: //注册
                Toast.makeText(UIUtils.getContext(), "注册....", Toast.LENGTH_SHORT).show();
                break;
        }
        switch (v.getId()) {
            case R.id.textView2: //激活
                Toast.makeText(UIUtils.getContext(), "激活....", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
