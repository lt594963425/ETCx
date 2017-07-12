package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.BottomDialog;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.WXShareUtils;

public class ShareActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_share;
    private TextView tv_wechat,tv_wechat_timeline,tv_qq,tv_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        initView();
    }

    private void initView() {
        setTitle(getString(R.string.my_share));
        btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_share://分享按钮
                BottomDialog.create(getSupportFragmentManager())
                        .setViewListener(new BottomDialog.ViewListener() {
                            @Override
                            public void bindView(View v) {
                                // // You can do any of the necessary the operation with the view
                                initDialogView(v);

                            }
                        })
                        .setLayoutRes(R.layout.share_dialog_layout)
                        .setDimAmount(0.5f)
                        .setCancelOutside(true)
                        .show();
                break;
            case R.id.tv_wechat:
//                String content = getString(R.string.sharecontent) + "http://www.xckjetc.com/";
                String content = "hello";
                WXShareUtils.shareText(App.get(),content,true);
                break;
            case R.id.tv_wechat_timeline:
                String content1 = getString(R.string.sharecontent) + "http://www.xckjetc.com/";
                WXShareUtils.shareText(App.get(),content1,false);
                break;
            case R.id.tv_qq:
                ToastUtils.showToast("qq");
                break;
            case R.id.tv_sms:
                ToastUtils.showToast("短信");
                break;
        }

    }

    private void initDialogView(View v) {
        tv_wechat = (TextView) v.findViewById(R.id.tv_wechat);
        tv_wechat_timeline = (TextView) v.findViewById(R.id.tv_wechat_timeline);
        tv_qq = (TextView) v.findViewById(R.id.tv_qq);
        tv_sms = (TextView) v.findViewById(R.id.tv_sms);
        tv_wechat.setOnClickListener(this);
        tv_wechat_timeline.setOnClickListener(this);
        tv_qq.setOnClickListener(this);
        tv_sms.setOnClickListener(this);
    }
}
