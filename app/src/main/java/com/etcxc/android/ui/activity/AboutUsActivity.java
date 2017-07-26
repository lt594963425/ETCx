package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.helper.VersionUpdateHelper;
import com.etcxc.android.net.download.DownloadOptions;
import com.etcxc.android.ui.view.ColorCircle;
import com.etcxc.android.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 关于我们
 * Created by xwpeng on 2017/6/30.
 */

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {
    private TextView mVersionCodeTextView;
    private View mCheckUpdateTextView;
    private VersionUpdateHelper mHelper;
    private ColorCircle mUpdateDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
        mHelper = new VersionUpdateHelper(this);
    }

    public void initView() {
        setTitle(R.string.about_us);
        mVersionCodeTextView = find(R.id.about_us_versioncode);
        mCheckUpdateTextView = find(R.id.about_us_check_update);
        mCheckUpdateTextView.setOnClickListener(this);
        String versionName = BuildConfig.VERSION_NAME;
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        mVersionCodeTextView.setText(String.valueOf("V" + versionName));
        find(R.id.about_us_test_json_api).setOnClickListener(this);
        mUpdateDot = find(R.id.update_dot);
        mUpdateDot.setRadius(UIUtils.dip2Px(5));
        mUpdateDot.setColor(getResources().getColor(R.color.update_dot));
//        if (PublicSPUtil.getInstance().getInt("check_version_code", 0) > BuildConfig.VERSION_CODE)
        mUpdateDot.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_us_check_update:
                mHelper.checkVersion();
                break;
            case R.id.about_us_test_json_api:
                startActivity(new Intent(this, TestJsonApiActivity.class));
                break;
        }
    }

    @Override
    protected void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadOptions options) {
        mHelper.downloadPd(options);
    }
}
