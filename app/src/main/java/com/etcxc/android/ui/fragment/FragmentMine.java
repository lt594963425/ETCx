package com.etcxc.android.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.LoginActivity;
import com.etcxc.android.ui.activity.PersonalInfoAvtivity;
import com.etcxc.android.ui.activity.PhoneRegistActivity;


/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class FragmentMine extends BaseFragment implements View.OnClickListener {
    private Button bt_f2_rg;
    private Button bt_login_rg;
    private ImageView userHead;
    private TextView username;
    private FrameLayout mMinewLauout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment_mine, null);
        mMinewLauout    = (FrameLayout) view.findViewById(R.id.mine_layout);
        userHead = (ImageView) view.findViewById(R.id.userhead);
        username = (TextView) view.findViewById(R.id.username);
        bt_f2_rg = (Button) view.findViewById(R.id.bt_f2_rg);
        bt_login_rg = (Button) view.findViewById(R.id.bt_login_rg);
        initView();
        return view;
    }

    private void initView() {
        bt_f2_rg.setOnClickListener(this);
        userHead.setOnClickListener(this);
        mMinewLauout.setOnClickListener(this);
        bt_login_rg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_layout:  //用户信息展示页面
                Intent intent1 = new Intent(getActivity(), PersonalInfoAvtivity.class);
                startActivity(intent1);
                break;
            case R.id.bt_f2_rg:
                Intent intent2 = new Intent(getActivity(), PhoneRegistActivity.class);
                startActivity(intent2);
                break;
            case R.id.bt_login_rg:
                Intent intent3 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent3);
            case R.id.userhead: //头像

                break;
            case R.id.username:

                break;
        }
    }
}
