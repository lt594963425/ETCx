package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.modle.sp.PublicSPUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        long time = System.currentTimeMillis();
        time += 2*24*60*60*1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        Date dt = new Date(time);
        Log.e(TAG,"time:"+ sdf.format(dt));
        mCheckingHintTextView = find(R.id.issue_checking_hint_textview);
        mTimeHintTextView = find(R.id.issue_checkfinish_time_textview);
        mCheckingHintTextView.setText(getString(R.string.checking_hint,PublicSPUtil.getInstance().getString("issueContactTel", MeManager.getPhone())));
        mTimeHintTextView.setText(getString(R.string.checking_finish_time_hint, sdf.format(dt)));
        SpannableStringBuilder builder = new SpannableStringBuilder(mTimeHintTextView.getText());
        builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.issue_finish_time)), 7, 7 +  sdf.format(dt).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTimeHintTextView.setText(builder);
        find(R.id.commit_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                openActivity(MainActivity.class);
                break;
        }
    }
}
