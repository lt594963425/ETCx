package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.ToastUtils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.net.FUNC.WX_PAY_ISSUE;

/**
 * 发行支付
 * Created by xwpeng on 2017/6/20.
 */

public class IssuePayActivity extends BaseActivity implements View.OnClickListener {
    private TextView mSumPayTextView;
    private EditText mRechargeEdittext;
    private RadioButton mPayAlipay;
    private RadioButton mPayWechat;

    //    private boolean mIsTruck = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_pay);
        initView();
    }

    private void initView() {
        setTitle(R.string.pay);
        mSumPayTextView = find(R.id.issue_pay_amount_text);
        mRechargeEdittext = find(R.id.recharge_amount_edittext);
        mPayAlipay = find(R.id.alipay_radiobutton);
        mPayWechat = find(R.id.wechat_pay_radiobutton);
        String str = mRechargeEdittext.getText().toString();
        mSumPayTextView.setText(getString(R.string.sum_pay, strToInt(str) + (200)));
        mRechargeEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mSumPayTextView.setText(getString(R.string.sum_pay, strToInt(str) + 200));
            }
        });
        find(R.id.commit_button).setOnClickListener(this);
    }

    private int strToInt(String s) {
        if (TextUtils.isEmpty(s)) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            LogUtil.e(TAG, "strToInt", e);
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        String editMoney = mRechargeEdittext.getText().toString().trim();
        DecimalFormat df = new DecimalFormat("0.00");
        if (editMoney.isEmpty()) {
            ToastUtils.showToast(R.string.money_isempty);
            return;
        }
        int moneyInt = Integer.valueOf(editMoney);//元

        Double moneyDb = Double.valueOf(df.format((200 + moneyInt)*100)); //分;
        JSONObject jsonObject = new JSONObject();
        try {
            //PublicSPUtil.getInstance().getString("carCard", "")
            //PublicSPUtil.getInstance().getString("carCardColor", "")
            jsonObject.put("licensePlate", "湘A12345")
                    .put("plateColor", "黄底黑字")
                    .put("total_fee", "1");
            Log.e(TAG, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mPayAlipay.isChecked()) {   //支付宝
            MobclickAgent.onEvent(this, "AliPay");
            ToastUtils.showToast(R.string.alipay);
        }
        if (mPayWechat.isChecked()) {  //微信支付
            MobclickAgent.onEvent(this, "WXPay");
            showProgressDialog(getString(R.string.wx_pay_loading));
            wxPay(jsonObject);
            //openActivity(IssueFinishActivity.class);
        }
    }

    private void wxPay(JSONObject jsonObject) {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkClient.get(NetConfig.consistUrl(WX_PAY_ISSUE), jsonObject));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        JSONObject js = new JSONObject(s);
                        Log.e(TAG, "js:" + js);
                        String code = js.getString("code");
                        if (code.equals("s_ok")) {
                            closeProgressDialog();
                            JSONObject varObject = js.getJSONObject("val");
                            TuneUpWxPay(varObject);
                        } else {
                            Log.e(TAG,"error");
                            closeProgressDialog();
                            ToastUtils.showToast(R.string.request_failed);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });
    }

    private void TuneUpWxPay(JSONObject varObject) throws JSONException {
        Log.e(TAG, String.valueOf(varObject));
        IWXAPI mWxApi = WXAPIFactory.createWXAPI(IssuePayActivity.this, Constants.WX_APP_ID, true);
        mWxApi.registerApp(Constants.WX_APP_ID);
        PayReq req = new PayReq();
        req.appId = Constants.WX_APP_ID;
        req.partnerId = Constants.MCH_ID;
        req.sign = varObject.getString("sign");
        req.prepayId = varObject.getString("prepayid");
        req.nonceStr = varObject.getString("noncestr");
        req.timeStamp = String.valueOf(varObject.getInt("timestamp"));
        req.packageValue = "Sign=WXPay";
        Boolean b = mWxApi.sendReq(req);
        Constants.ETC_ISSUE = true;
        Log.e(TAG, getString(R.string.pay_result_log) + b + "，appId=" + req.appId + ",partnerId=" + req.partnerId + ",prepayId=" + req.prepayId +
                ",time_start=" + req.timeStamp + ",sign=" + req.sign + ",nonce_str=" + req.nonceStr);
        PublicSPUtil.getInstance().putBoolean("ETC_ISSUE",b);
        closeProgressDialog();
    }
}
