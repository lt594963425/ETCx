package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

/**
 * 发行流程完成
 * Created by xwpeng on 2017/6/20.
 */

public class IssueFinishActivity extends BaseActivity implements View.OnClickListener{
    private TextView mCheckingHintTextView, mTimeHintTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isssue_finish);
        initView();
    }

    private void initView() {
        setTitle(R.string.finish);
        mCheckingHintTextView = find(R.id.issue_checking_hint_textview);
        mTimeHintTextView = find(R.id.issue_checkfinish_time_textview);
        mCheckingHintTextView.setText(getString(R.string.checking_hint, "15512345678"));
        String timeStr = "2017年9月1号14:15";
        mTimeHintTextView.setText(getString(R.string.checking_finish_time_hint, "2017年9月1号14:15"));
        SpannableStringBuilder builder = new SpannableStringBuilder(mTimeHintTextView.getText());
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.issue_finish_time)), 7, 7 + timeStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTimeHintTextView.setText(builder);
        find(R.id.commit_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
