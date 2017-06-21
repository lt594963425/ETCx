package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.UIUtils;

/**
 * 收货地址
 * Created by xwpeng on 2017/6/20.
 */

public class PostAddressActivity extends BaseActivity implements View.OnClickListener {
    private TextView mRegionResultTextView, mStreetResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_address);
        setTitle(R.string.edit_address);
        initView();
    }

    private void initView() {
        mRegionResultTextView =find(R.id.post_address_region_result);
        mStreetResultTextView =find(R.id.post_address_street_result);
        UIUtils.addIcon(mRegionResultTextView, R.drawable.vd_right_arrow, UIUtils.RIGHT);
        UIUtils.addIcon(mStreetResultTextView, R.drawable.vd_right_arrow, UIUtils.RIGHT);
        find(R.id.post_address_street_layout).setOnClickListener(this);
        find(R.id.post_address_region_layout).setOnClickListener(this);
        find(R.id.commit_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                startActivity(new Intent(this, IssuePayActivity.class));
                break;
            case R.id.post_address_region_layout:
                startActivity(new Intent(this, SelectRegionActivity.class));
                break;
            case R.id.post_address_street_layout:
                break;
        }
    }
}
