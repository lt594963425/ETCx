package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.FUNC;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.net.callback.StringCallback;
import com.etcxc.android.pay.WXPay.WXPay;
import com.etcxc.android.pay.alipay.AliPay;
import com.etcxc.android.pay.alipay.PayResult;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static com.etcxc.android.net.NetConfig.HOST;

/**
 * 发行支付
 * Created by xwpeng on 2017/6/20.
 */

public class IssuePayActivity extends BaseActivity implements View.OnClickListener {
    private TextView mSumPayTextView;
    private EditText mRechargeEdittext;
    private RadioButton mPayAlipay;
    private RadioButton mPayWechat;

    private class RechargeStringCallBack extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            ToastUtils.showToast(R.string.request_failed);
            LogUtil.e(TAG, "请求失败");
            closeProgressDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            closeProgressDialog();
            switch (id) {
                case 1:
                    boolean b = WXPay.TuneUpWxPay(response);
                    if (!b) {
                        ToastUtils.showToast(R.string.request_failed);
                    } else {
                        PublicSPUtil.getInstance().putString("ETC_FLAGE", Constants.ETC_ISSUE);
                    }
                    break;
                case 2:
                    payToOrderService(response);
                    break;
            }
        }
    }

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
        if (TextUtils.isEmpty(s)) {
            return 0;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            LogUtil.e(TAG, "strToInt", e);
        }
        return 0;
    }

    @Override
    public void onClick(View v) {   //PAY_ISSUE
        try {
            showProgressDialog(getString(R.string.loading));
            JSONObject jsonObject = getIssuePayParams();
            if (mPayAlipay.isChecked()) {   //支付宝
                MobclickAgent.onEvent(this, "AliPay");
                jsonObject.put("way", "ali");
                aliPay(jsonObject);

            }
            if (mPayWechat.isChecked()) {  //微信支付
                jsonObject.put("way", "wx");
                MobclickAgent.onEvent(this, "WXPay");
                showProgressDialog(getString(R.string.loading));
                wxPay(jsonObject);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Nullable
    private JSONObject getIssuePayParams() throws JSONException {
        String editMoney = mRechargeEdittext.getText().toString().trim();
        if (editMoney.isEmpty()) {
            ToastUtils.showToast(R.string.money_isempty);
            return null;
        }

        int moneyDb = (200 + Integer.valueOf(editMoney)) * 100;
        JSONObject jsonObject = new JSONObject();
        String plateLicense = PublicSPUtil.getInstance().getString("carCard", "");
        String plateColor = PublicSPUtil.getInstance().getString("carCardColor", "");
        jsonObject.put("licensePlate", plateLicense)
                .put("plateColor", plateColor)
                .put("total_fee", moneyDb)
                .put("client_type", "Android");
        Log.e(TAG, jsonObject.toString());
        return jsonObject;
    }

    private void wxPay(JSONObject jsonObject) {
        OkHttpUtils.postString()
                .url(HOST + FUNC.PAY_ISSUE)
                .content(String.valueOf(jsonObject))
                .tag(this)
                .id(1)
                .mediaType(OkHttpUtils.JSON).build()
                .execute(new RechargeStringCallBack());
    }

    private void aliPay(JSONObject jsonObject) {
        OkHttpUtils.postString()
                .url(HOST + FUNC.PAY_ISSUE)
                .content(String.valueOf(jsonObject))
                .tag(this)
                .id(2)
                .mediaType(OkHttpUtils.JSON).build()
                .execute(new RechargeStringCallBack());
    }

    /**
     * 支付宝进行请求
     */
    private void payToOrderService(String payString) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                String orderInfo = payString.replace("amp;", "");
                PayTask payTask = new PayTask(IssuePayActivity.this);
                Map<String, String> result = payTask.payV2(orderInfo, true);
                e.onNext(result);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        closeProgressDialog();
                        PayResult payResult = new PayResult((Map<String, String>) o);
                        /**
                         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                         */
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        switch (resultStatus) {
                            case AliPay.PAY_OK:
                                openActivity(IssueFinishActivity.class);
                                finish();
                                break;
                            case AliPay.PAY_FAIL:
                                ToastUtils.showToast("支付失败");
                                break;
                            case AliPay.PAY_CANCEL:
                                ToastUtils.showToast(payResult.getMemo());
                                break;
                            case AliPay.PAY_NET_ERROR:
                                ToastUtils.showToast(payResult.getMemo());
                                break;
                            case AliPay.PAY_REPEAT:
                                ToastUtils.showToast(payResult.getMemo());
                                break;
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.cancelTag(this);
    }
}