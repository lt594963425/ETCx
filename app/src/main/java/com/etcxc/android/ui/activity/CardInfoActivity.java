package com.etcxc.android.ui.activity;

import android.os.Bundle;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

/**
 * 卡信息
 */
public class CardInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.card_info));
    }
}
