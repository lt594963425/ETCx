package com.etcxc.android.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by ${caoyu} on 2017/7/8.
 */

public class WXEntryActivity extends AppCompatActivity  implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";


    /**分享到微信接口**/
    private IWXAPI mWxApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        Log.d(TAG, "onCreate: ");

        mWxApi = WXAPIFactory.createWXAPI(this, App.WX_APP_ID, false);
        mWxApi.registerApp(App.WX_APP_ID);
        mWxApi.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWxApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "onReq: "+baseReq.getType());
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.d(TAG, "onResp: "+baseResp.errCode);
        //在这个方法中处理微信传回的数据
        //形参resp 有下面两个个属性比较重要
        //1.resp.errCode
        //2.resp.transaction则是在分享数据的时候手动指定的字符创,用来分辨是那次分享(参照4.中req.transaction)
        switch (baseResp.errCode) { //根据需要的情况进行处理
            case BaseResp.ErrCode.ERR_OK:
                //正确返回
                ToastUtils.showToast("正确返回");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //用户取消
                ToastUtils.showToast("用户取消");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //认证被否决
                ToastUtils.showToast("认证被否决");
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                //发送失败
                ToastUtils.showToast("发送失败");
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                //不支持错误
                ToastUtils.showToast("不支持错误");
                break;
            case BaseResp.ErrCode.ERR_COMM:
                //一般错误
                ToastUtils.showToast("一般错误");
                break;
            default:
                //其他不可名状的情况
                ToastUtils.showToast("其他不可名状的情况");
                break;
        }
        finish();
    }

}
