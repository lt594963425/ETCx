package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.bean.OrderRechargeInfo;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 刘涛 on 2017/7/5 0005.
 */

public class SelectPayWaysActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SelectPayWaysActivity";
    private Button mEtcPay;
    private RadioGroup mPayWayRedioGroup;
    private RadioButton mPayAlipay, mPayWechat;
    private OkHttpClient client;
    private IWXAPI mWxApi;
    private String url = "http://192.168.6.126:9999/pay/pay/payment/pay_message/";
    private String urls;
    public StringBuilder mStrBuilder ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payways);

        init();
    }

    private void init() {
        client = new OkHttpClient();
        setTitle(R.string.please_select);
        mPayWayRedioGroup = (RadioGroup) findViewById(R.id.payways_rediogroup);
        mPayAlipay = (RadioButton) findViewById(R.id.pay_alipay);
        mPayWechat = (RadioButton) findViewById(R.id.pay_wechat);
        mEtcPay = (Button) findViewById(R.id.etc_pay);
        mEtcPay.setOnClickListener(this);
    }

    // 支付
    @Override
    public void onClick(View v) {
        mStrBuilder = new StringBuilder();
        if (setData()) return;
        if (mPayAlipay.isChecked()) {   //支付宝
            ToastUtils.showToast(R.string.alipay);
        }
        if (mPayWechat.isChecked()) {  //微信支付
            showProgressDialog(getString(R.string.wx_pay_loading));
            wxPay(urls);

        }
    }

    private boolean setData() {
        ArrayList<OrderRechargeInfo> list = UIUtils.getInfoList(this);
        if (list == null && list.size() < 1) {
            ToastUtils.showToast(R.string.add_recharge_info);
            return true;
        }
        if (list.size() == 1) {
            mStrBuilder.append("\"face_card_num\"").append("=>")
                    .append("\"" + list.get(0).getEtccarnumber() + "\"")
                    .append(",").append("\"fee\"").append("=>")
                    .append("\"" + (int) (Double.parseDouble(list.get(0).getRechargemoney()) * 100) + "\"")
                    .append("/total_fee/")
                    .append((int) (Double.parseDouble(list.get(0).getAlloney())))
                    .append("/singular/1");
        }
        if (list.size() > 1) {

            for (int i = 0; i < list.size(); i++) {
                if (i == list.size() - 1) {
                    mStrBuilder.append("\"face_card_num\"")
                    .append("=>")
                    .append("\"" + list.get(i).getEtccarnumber() + "\"").append(",")
                    .append("\"fee\"").append("=>")
                    .append("\"" + (int) (Double.parseDouble(list.get(i).getRechargemoney()) * 100) + "\"")
                    .append("/total_fee/")
                    .append((int) (Double.parseDouble(list.get(0).getAlloney())))
                    .append("/singular/").append(list.size());
                    break;
                }
                mStrBuilder.append("\"face_card_num\"").append("=>")
                .append("\"" + list.get(0).getEtccarnumber() + "\"")
                .append(",").append("\"fee\"").append("=>")
                .append("\"" + (int) (Double.parseDouble(list.get(0).getRechargemoney()) * 100) + "\"")
                .append(";");
            }
        }
        urls = url + mStrBuilder.toString();
        Log.e(TAG, urls + ",list.length:" + list.size());
        return false;
    }

    private void wxPay(String url) {
        Request requst = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(requst).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//mLoginVerificodeEdt
                        ToastUtils.showToast(R.string.request_failed);
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                Log.e(TAG, "" + s);
                parseJsonResult(s);
            }
        });
    }

    private void parseJsonResult(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject == null) {
                return;
            }
            String code = jsonObject.getString("code");
            if (code.equals("s_ok")) {
                JSONObject varObject = jsonObject.getJSONObject("var");
                mWxApi = WXAPIFactory.createWXAPI(SelectPayWaysActivity.this, Constants.WX_APP_ID, true);
                mWxApi.registerApp(Constants.WX_APP_ID);
                PayReq req = new PayReq();
                req.appId = Constants.WX_APP_ID;
                req.partnerId = Constants.WX_PARTNER_ID;
                req.sign = varObject.getString("sign");
                req.prepayId = varObject.getString("prepay_id");
                req.nonceStr = varObject.getString("nonce_str");
                req.timeStamp = String.valueOf(varObject.getInt("time_start"));
                req.packageValue = "Sign=WXPay";
                Boolean b = mWxApi.sendReq(req);
                Log.e(TAG, getString(R.string.pay_result_log) + b+"，appId=" + req.appId + ",partnerId=" + req.partnerId + ",prepayId=" + req.prepayId +
                        ",time_start" + req.timeStamp + ",sign" + req.sign + ",nonce_str" + req.nonceStr);
                if(b){
                    closeProgressDialog();
                }
                finish();
            }
            if (code.equals("err")) {
                String returnMsg = jsonObject.getString("message");
                closeProgressDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//mLoginVerificodeEdt
                        ToastUtils.showToast(returnMsg);
                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();

            closeProgressDialog();
        }
    }
}