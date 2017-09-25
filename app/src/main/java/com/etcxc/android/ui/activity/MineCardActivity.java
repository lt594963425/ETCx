package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;


/**
 * 我的卡选项
 * Created by LiuTao on 2017/8/29 0029.
 */

public class MineCardActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_card);
        initView();
    }

    private void initView() {
        setTitle(R.string.my_card);
        find(R.id.onlian_apply_schedule_toright).setOnClickListener(this);
        find(R.id.etc_card_status_toright).setOnClickListener(this);
        find(R.id.binding_card_toright).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.onlian_apply_schedule_toright:
                break;
            case R.id.etc_card_status_toright:
                openActivity(UserCardActivity.class);
                break;
            case R.id.binding_card_toright:
                openActivity(BindCardActivity.class);
                break;
        }

    }
}
