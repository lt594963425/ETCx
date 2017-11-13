package com.etcxc.android.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.etcxc.android.R;
import com.etcxc.android.utils.ToastUtils;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import static com.etcxc.android.base.Constants.WX_APP_ID;


/**
 * Created by ${caoyu} on 2017/7/8.
 */


public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    private IWXAPI WXapi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        WXapi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        WXapi.registerApp(WX_APP_ID);
        setContentView(R.layout.activity_wxentry);
        WXapi.handleIntent(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
     WXapi.handleIntent(intent,this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "onReq: ");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.d(TAG, "onResp: "+baseResp.errCode);
        String result = null;
        switch (baseResp.errCode) { //根据需要的情况进行处理
            case BaseResp.ErrCode.ERR_OK:
                //正确返回
                result = getString(R.string.share_complete);
                MobclickAgent.onEvent(this, "WXShare" );
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                result = getString(R.string.share_cancel);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //认证被否决
                result = "认证被否决";
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                //发送失败
                result = getString(R.string.share_error);
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                //不支持错误
                result = "不支持错误";
                break;
            case BaseResp.ErrCode.ERR_COMM:
                //一般错误
                result = "一般错误";
                break;
            default:
                //其他不可名状的情况
                result = "其他不可名状的情况";
                break;
        }
        ToastUtils.showToast(result);
        finish();
    }
}
