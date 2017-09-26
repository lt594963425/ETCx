package com.etcxc.android.pay.WXPay;

import com.etcxc.android.base.App;
import com.etcxc.android.bean.WxPayRecharge;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import static com.etcxc.android.base.Constants.WX_APP_ID;

/**
 * 微信支付
 * Created by ${LiuTao} on 2017/9/26/026.
 */
public class WXPay {
    private static IWXAPI mWxApi = WXAPIFactory.createWXAPI(App.get(), WX_APP_ID, true);

    /**
     * 调起微信支付
     *
     * @param s
     * @return b
     */
    public static boolean TuneUpWxPay(String s) {
        Gson gson = new Gson();
        WxPayRecharge wxPayRecharge = gson.fromJson(s, WxPayRecharge.class);
        if ( !wxPayRecharge.getCode().equals("s_ok")) return false;
        return wxpay(wxPayRecharge.getVar());
    }

    private static boolean wxpay(WxPayRecharge.VarBean varBean) {
        mWxApi.registerApp(WX_APP_ID);
        PayReq req = new PayReq();
        req.appId = WX_APP_ID;
        req.partnerId = varBean.getPartnerid();
        req.sign = varBean.getSign();
        req.prepayId = varBean.getPrepayid();
        req.nonceStr = varBean.getNoncestr();
        req.timeStamp = String.valueOf(varBean.getTimestamp());
        req.packageValue = varBean.getPackageX();//"Sign=WXPay"
        return mWxApi.sendReq(req);
    }
}
