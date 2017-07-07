package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;

/**
 * Created by 刘涛 on 2017/7/5 0005.
 */

public class SelectPayWaysActivity extends BaseActivity implements View.OnClickListener {
    private Button mEtcPay;
    private RadioGroup mPayWayRedioGroup;
    private RadioButton mPayAlipay, mPayWechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payways);
        init();
    }

    private void init() {
        setTitle(R.string.please_select);
        mPayWayRedioGroup = find(R.id.payways_rediogroup);
        mPayAlipay = find(R.id.pay_alipay);
        mPayWechat = find(R.id.pay_wechat);
        mEtcPay = find(R.id.etc_pay);
        mEtcPay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mPayAlipay.isChecked()) {   //支付宝
            ToastUtils.showToast(R.string.alipay);
        } else if (mPayWechat.isChecked()) {  //微信支付
            ToastUtils.showToast(R.string.wechat_pay);
        }
        // 支付
    }

}
