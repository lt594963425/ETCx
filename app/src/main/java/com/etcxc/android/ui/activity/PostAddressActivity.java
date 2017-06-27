package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

/**
 * 收货地址
 * Created by xwpeng on 2017/6/20.
 */

public class PostAddressActivity extends BaseActivity implements View.OnClickListener {
    private TextView mRegionResultTextView, mStreetResultTextView;
    public final static int REQUEST_REGION = 1;
    public final static int REQUEST_STREET = 2;
    private final static String FUNC_POSTADDRESS = "/transaction/transaction/transactionmail";


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
                startActivityForResult(new Intent(this, SelectRegionActivity.class), REQUEST_REGION);
                break;
            case R.id.post_address_street_layout:
                String county = mRegionResultTextView.getText().toString();
                if (TextUtils.isEmpty(county)) {
                    ToastUtils.showToast(R.string.please_select_region);
                    return;
                }
                county = county.split(" ")[2];
                Intent intent = new Intent(this, SelectRegionActivity.class);
                intent.putExtra("county", county);
                startActivityForResult(intent, REQUEST_STREET);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_REGION:
                    mRegionResultTextView.setText(data.getStringExtra("region"));
                    break;
                case REQUEST_STREET:
                    mStreetResultTextView.setText(data.getStringExtra("street"));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
