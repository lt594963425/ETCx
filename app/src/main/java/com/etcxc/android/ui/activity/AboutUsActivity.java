package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

/**
 * 关于我们
 * Created by xwpeng on 2017/6/30.
 */

public class AboutUsActivity extends BaseActivity implements View.OnClickListener{
    private TextView mLogoTextView, mVersionCodeTextView,mCheckUpdateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
    }
    private void initView() {
        setTitle(R.string.about_us);
        mLogoTextView = find(R.id.about_us_logo);
        mVersionCodeTextView = find(R.id.about_us_versioncode);
        mCheckUpdateTextView = find(R.id.about_us_check_update);
        mCheckUpdateTextView.setOnClickListener(this);
        String versionName = BuildConfig.VERSION_NAME;
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        mVersionCodeTextView.setText("V" + versionName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_us_check_update:

                break;
        }
    }
}
