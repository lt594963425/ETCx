package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.net.callback.StringCallback;

import okhttp3.Call;


/**
 * 我的卡选项
 * Created by LiuTao on 2017/8/29 0029.
 */

public class MineCardActivity extends BaseActivity implements View.OnClickListener {
    public   String URL_POSTs = "http://192.168.6.46/improve/version_manager/version_update ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_card);
        initView();
    }

    private void initView() {
        setTitle(R.string.my_card);
        find(R.id.onlian_apply_schedule_toright).setOnClickListener(this);
        find(R.id.etc_card_status_toright).setOnClickListener(this);
        find(R.id.binding_card_toright).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.onlian_apply_schedule_toright:
                OkHttpUtils
                        .post()
                        .url(URL_POSTs)
                        .addParams("version_code", "3")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e(TAG,response);

                            }
                        });
                break;
            case R.id.etc_card_status_toright:
                openActivity(UserCardActivity.class);
                break;
            case R.id.binding_card_toright:
                openActivity(BindCardActivity.class);
                break;
                default:
                    break;
        }

    }
}
