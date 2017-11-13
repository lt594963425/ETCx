package com.etcxc.android.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.ui.activity.IssueFinishActivity;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.activity.StoreActivity;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.ToastUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.etcxc.android.utils.UIUtils.clearDetialData;

/**
 * 微信支付
 * @author LiuTao
 * @date 2017/7/2 0002
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private LinearLayout mRehcargeResult;
    private   IWXAPI WXapi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        setTitle("充值结果");
        mRehcargeResult = find(R.id.recharge_payresult_layout);

        WXapi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        WXapi.handleIntent(getIntent(), this);
        findViewById(R.id.to_CreditForLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(StoreActivity.class);
                finish();
            }
        });
        findViewById(R.id.dialog_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(MainActivity.class);
                finish();
            }
        });
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
     *
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "onPayFinish, errCode = " + resp.errCode + "type:" + resp.getType());
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String flag = PublicSPUtil.getInstance().getString("ETC_FLAGE", "");
                if (Constants.ETC_ISSUE.equals(flag)) {
                    PublicSPUtil.getInstance().putString("ETC_FLAGE", "");
                    openActivity(IssueFinishActivity.class);
                    LogUtil.e(TAG, "完成在线申请");
                    finish();
                } else if (Constants.ETC_RECHARGE.equals(flag)) {
                    PublicSPUtil.getInstance().putString("ETC_FLAGE", "");
                    LogUtil.e(TAG, "ETC充值成功");
                    mRehcargeResult.setVisibility(View.VISIBLE);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}