package com.etcxc.android.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.bean.OrderRechargeInfo;
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
import com.etcxc.android.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.Call;

import static com.etcxc.android.net.NetConfig.HOST;
import static com.etcxc.android.net.OkHttpUtils.JSON;


/**
 * @author 刘涛
 * @date 2017/7/5 0005
 * 充值
 */

public class ETCPayActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ETCPayActivity";
    private RadioButton mAlipay, mWechat;
    private List<OrderRechargeInfo> mData;
    private double mAllMoney;

    private class RechargeStringCallBack extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            closeProgressDialog();
            if(e.toString().contains("closed")){
                ToastUtils.showToast(R.string.cancel_request);
            }else {
                ToastUtils.showToast(R.string.request_failed);
            }
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
                        PublicSPUtil.getInstance().putString("ETC_FLAGE", Constants.ETC_RECHARGE);
                    }
                    break;
                case 2:
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if ("s_ok".equals(jsonObject.getString("code"))) {
                            payToOrderService(jsonObject.getString("ali_pay"));
                        } else {
                            ToastUtils.showToast("支付失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            UIUtils.saveInfoList(mData);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payways);
        mData = (List<OrderRechargeInfo>) getIntent().getSerializableExtra("orderData");
        mAllMoney = getIntent().getIntExtra("allMoney", 0);
        LogUtil.e(TAG, mAllMoney + ":元" + ":::" + mData.size());
        init();
    }

    private void init() {
        setTitle(R.string.please_select);
        mAlipay = (RadioButton) findViewById(R.id.pay_alipay);
        mWechat = (RadioButton) findViewById(R.id.pay_wechat);
        Button mEtcPay = (Button) findViewById(R.id.etc_pay);
        mEtcPay.setOnClickListener(this);
    }

    // 支付
    @Override
    public void onClick(View v) {
        try {
            JSONObject params = addPayParams();
            if (mAlipay.isChecked()) {   //支付宝
                MobclickAgent.onEvent(this, "AliPay");
                showProgressDialog(getString(R.string.loading));
                params.put("way", "ali");
                aliPay(params);
            }
            if (mWechat.isChecked()) {  //微信支付
                MobclickAgent.onEvent(this, "WXPay");
                showProgressDialog(getString(R.string.loading));
                params.put("way", "wx");
                wxPay(params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 充值参数  json串
     */
    public JSONObject addPayParams() throws JSONException {
        JSONObject params = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObjectOrder = new JSONObject();
        for (int i = 0; i < mData.size(); i++) {
            int money = mData.get(i).getRechargemoney();
            jsonObjectOrder.put("etc_card", mData.get(i).getEtccarnumber());
            jsonObjectOrder.put("money", money );
            jsonArray.put(jsonObjectOrder);
        }
        params.put("order", jsonArray);
        params.put("tote_money", mAllMoney );
        params.put("client_type", "Android");
        params.put("way", "wx");
        LogUtil.e("params:", params + "");
        return params;
    }

    private void wxPay(JSONObject jsonObject) {
        OkHttpUtils.postString()
                .url(HOST + FUNC.PAY)
                .content(String.valueOf(jsonObject))
                .tag(this)
                .id(1)
                .mediaType(JSON).build()
                .execute(new RechargeStringCallBack());
    }
    private void aliPay(JSONObject jsonObject) {
        OkHttpUtils.postString()
                .url(HOST + FUNC.PAY)
                .content(String.valueOf(jsonObject))
                .tag(this)
                .id(2)
                .mediaType(JSON).build()
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
                PayTask payTask = new PayTask(ETCPayActivity.this);
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
                        String resultInfo = payResult.getResult();
                        String resultStatus = payResult.getResultStatus();
                        switch (resultStatus){
                            case  AliPay.PAY_OK:
                                showInfoDialog();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.cancelTag(this);
    }
}