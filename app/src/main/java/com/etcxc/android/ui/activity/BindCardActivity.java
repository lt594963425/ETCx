package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

/**
 * 绑定湘通卡
 * Created by LiuTao on 2017/8/29 0029.
 */

public class BindCardActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_card);
        initView();
    }

    private void initView() {
        setTitle("绑卡");
        find(R.id.to_bind_card_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.to_bind_card_btn:
                openActivity(AddXCardActivity.class);
                break;
        }
    }
}
