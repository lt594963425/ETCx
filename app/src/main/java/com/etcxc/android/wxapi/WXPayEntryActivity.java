package com.etcxc.android.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.etcxc.android.R;
import com.etcxc.android.base.Constants;
import com.etcxc.android.ui.activity.ETCRechargeActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.etcxc.android.base.App.WXapi;
import static com.etcxc.android.utils.UIUtils.clearDetialData;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

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
            clearDetialData(this);
            startActivity(new Intent(this, ETCRechargeActivity.class));
        }
        finish();
    }
}