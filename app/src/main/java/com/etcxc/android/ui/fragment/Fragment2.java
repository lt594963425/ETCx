package com.etcxc.android.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.ui.activity.LoginActivity;
import com.etcxc.android.ui.activity.PhoneRegistActivity;
import com.trello.rxlifecycle2.components.support.RxFragment;


/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class Fragment2 extends RxFragment implements View.OnClickListener {
    private Button bt_f2_rg;
    private ImageView head;
    private TextView username;
    private RelativeLayout rl_login;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment2_item1, null);
        head = (ImageView) view.findViewById(R.id.f2_uhead);
        username = (TextView) view.findViewById(R.id.f2_uid);
        rl_login = (RelativeLayout) view.findViewById(R.id.rl_login);
        bt_f2_rg = (Button) view.findViewById(R.id.bt_f2_rg);
        initView();
        return view;
    }

    private void initView() {
        rl_login.setOnClickListener(this);
        bt_f2_rg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_login:
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
                break;
            case R.id.bt_f2_rg:
                Intent intent2 = new Intent(getActivity(), PhoneRegistActivity.class);
                startActivity(intent2);
                break;
        }
    }
}
