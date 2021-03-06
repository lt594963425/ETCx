package com.etcxc.android.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.etcxc.android.R;

import static com.etcxc.android.utils.UIUtils.getResources;
import static com.etcxc.android.utils.UIUtils.getString;

/**
 * 倒计时
 * Created by 刘涛 on 2017/7/5 0005.
 */

public class TimeCount extends CountDownTimer {
    private TextView view;

    public TimeCount(TextView view, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.view = view;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        view.setBackgroundResource(R.drawable.bg_gray);
        view.setClickable(false);
        view.setTextColor(getResources().getColor(R.color.black));
        view.setText("(" + millisUntilFinished / 1000 + ")" + getString(R.string.timeLate));
        view.setTextSize(UIUtils.px2Dip(39));
    }

    @Override
    public void onFinish() {
        view.setText(getString(R.string.reStartGetCode));
        view.setTextSize(UIUtils.px2Dip(39));
        view.setClickable(true);
        view.setBackgroundResource(R.drawable.bg_sms_button);
    }
}
