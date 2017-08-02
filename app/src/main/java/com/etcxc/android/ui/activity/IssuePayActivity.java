package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.LogUtil;

/**
 * 发行支付
 * Created by xwpeng on 2017/6/20.
 */

public class IssuePayActivity extends BaseActivity implements View.OnClickListener{
    private TextView mSumPayTextView;
    private EditText mRechargeEdittext;
//    private boolean mIsTruck = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_pay);
        initView();
    }

    private void initView() {
        setTitle(R.string.pay);
        mSumPayTextView = find(R.id.issue_pay_amount_text);
        mRechargeEdittext = find(R.id.recharge_amount_edittext);
        String str = mRechargeEdittext.getText().toString();
        mSumPayTextView.setText(getString(R.string.sum_pay, strToInt(str) + (200)));
        mRechargeEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSumPayTextView.setText(getString(R.string.sum_pay, strToInt(str) + 200));
            }
        });
        find(R.id.commit_button).setOnClickListener(this);
    /*    if (mIsTruck) {
            find(R.id.obu_price_textview).setVisibility(View.GONE);
            find(R.id.issue_pay_hint_textView).setVisibility(View.GONE);
        }*/
    }

    private int strToInt(String s) {
        if (TextUtils.isEmpty(s)) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            LogUtil.e(TAG, "strToInt", e);
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                openActivity(IssueFinishActivity.class);
                break;
        }
    }
}
