package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

/**
 * 收货地址
 * Created by xwpeng on 2017/6/20.
 */

public class PostAddressActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_post_address);
         initView();
    }

    private void initView() {
        find(R.id.commit_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                startActivity(new Intent(this, IssuePayActivity.class));
                break;
        }
    }
}
