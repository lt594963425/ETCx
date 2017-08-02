package com.etcxc.android.wxapi;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.activity.StoreActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.etcxc.android.base.App.WXapi;
import static com.etcxc.android.utils.UIUtils.clearDetialData;


public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        WXapi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        WXapi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        WXapi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "onPayFinish, errCode = " + resp.errCode);
        if (resp.errCode == 0) {
            showInfoDialog();
            clearDetialData(this);
        }
        finish();
    }
    private void showInfoDialog() {
        View longinDialogView = LayoutInflater.from(this).inflate(R.layout.recharge_info_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView creditForLoad = (TextView) longinDialogView.findViewById(R.id.to_CreditForLoad);
        TextView confirm = (TextView) longinDialogView.findViewById(R.id.dialog_confirm);
        builder.setView(longinDialogView);
        final Dialog dialog = builder.show();
        creditForLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(StoreActivity.class);
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(MainActivity.class);
                dialog.dismiss();
            }
        });
    }
}