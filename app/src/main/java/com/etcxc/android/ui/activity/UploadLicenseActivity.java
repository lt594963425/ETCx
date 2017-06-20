package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.UIUtils;

/**
 * 上传证件信息
 * Created by xwpeng on 2017/6/20.
 */

public class UploadLicenseActivity  extends BaseActivity implements View.OnClickListener {
    private final static String TAG = UploadLicenseActivity.class.getSimpleName();
    private  boolean mIsOrg = false;//是组织用户吗？
    private TextView mUploadHintTextView;
    private RelativeLayout mFristLicenseLayout;
    private ImageView mIdcardImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_license);
         initView();
    }

    private void initView() {
        mUploadHintTextView = find(R.id.upload_license_hint_textview);
        mUploadHintTextView.setText(mIsOrg ? R.string.org_upload_license_hint : R.string.person_upload_license_hint);
        mIdcardImageView = find(R.id.id_card_imageview);
        find(R.id.commit_button).setOnClickListener(this);
        if (mIsOrg) {
            RelativeLayout fristLicenseLayout = find(R.id.first_license_layout);
            ViewGroup.LayoutParams params = fristLicenseLayout.getLayoutParams();
            params.width = UIUtils.dip2Px(98);
            params.height = UIUtils.dip2Px(139);
            fristLicenseLayout.setLayoutParams(params);
            mIdcardImageView.setImageResource(R.mipmap.ic_org_license);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                startActivity(new Intent(this, ContactPhoneActivity.class));
                break;
        }
    }
}