package com.etcxc.android.pay.wxapi;

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
import com.etcxc.android.ui.activity.IssueFinishActivity;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.activity.StoreActivity;
import com.etcxc.android.utils.ToastUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.etcxc.android.base.App.WXapi;
import static com.etcxc.android.utils.UIUtils.clearDetialData;

/**
 * 微信支付
 * Created by LiuTao on 2017/7/2 0002.
 */
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

    /**
     * int ERR_OK = 0;
     * int ERR_COMM = -1;
     * int ERR_USER_CANCEL = -2;
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "onPayFinish, errCode = " + resp.errCode + "type:" + resp.getType());
        switch (resp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                if (Constants.ETC_ISSUE) {
                    Constants.ETC_ISSUE = false;
                    openActivity(IssueFinishActivity.class);
                    Log.e(TAG, "完成在线申请");
                } else {
                    Log.e(TAG, "ETC充值成功");
                    showInfoDialog();
                    clearDetialData(this);
                }
                break;
            case BaseResp.ErrCode.ERR_COMM:
                ToastUtils.showToast(R.string.pay_failed);
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ToastUtils.showToast(R.string.pay_cancle);
                finish();
                break;

        }

    }
    private void showInfoDialog(){
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