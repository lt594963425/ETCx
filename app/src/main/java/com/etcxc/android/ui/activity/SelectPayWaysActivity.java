package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.alipay.sdk.app.PayTask;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.bean.OrderRechargeInfo;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.pay.WXPay.WXPay;
import com.etcxc.android.pay.alipay.AliPay;
import com.etcxc.android.pay.alipay.PayResult;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.net.FUNC.WXORDER;

/**
 * Created by 刘涛 on 2017/7/5 0005.
 * 微信和支付宝支付
 */

public class SelectPayWaysActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SelectPayWaysActivity";
    private RadioButton mPayAlipay, mPayWechat;
    private String urls;
    public StringBuilder mStrBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payways);

        init();
    }

    private void init() {
        setTitle(R.string.please_select);
        mPayAlipay = (RadioButton) findViewById(R.id.pay_alipay);
        mPayWechat = (RadioButton) findViewById(R.id.pay_wechat);
        Button mEtcPay = (Button) findViewById(R.id.etc_pay);
        mEtcPay.setOnClickListener(this);
    }

    // 支付
    @Override
    public void onClick(View v) {

        mStrBuilder = new StringBuilder();
        if (setData()) return;
        if (mPayAlipay.isChecked()) {   //支付宝
            MobclickAgent.onEvent(this, "AliPay");
            showProgressDialog(getString(R.string.wx_pay_loading));
            payToOrderService("test");
            ToastUtils.showToast(R.string.alipay);
        }
        if (mPayWechat.isChecked()) {  //微信支付
            MobclickAgent.onEvent(this, "WXPay" );
            showProgressDialog(getString(R.string.wx_pay_loading));
            wxPay(urls);

        }
    }

    private boolean setData() {
        List<OrderRechargeInfo> list = UIUtils.getInfoList(this);
        if (list == null && list.size() < 1) {
            ToastUtils.showToast(R.string.add_recharge_info);
            return true;
        }
        if (list.size() == 1) {
            mStrBuilder.append("\"face_card_num\"").append("=>").append("\"").append(list.get(0).getEtccarnumber()).append("\"")
                    .append(",").append("\"fee\"").append("=>").append("\"").append((int) (Double.parseDouble(list.get(0).getRechargemoney()) * 100)).append("\"")
                    .append("/total_fee/")
                    .append((int) (Double.parseDouble(list.get(0).getAlloney())))
                    .append("/singular/1");
        }
        if (list.size() > 1) {
            for (int i = 0; i < list.size(); i++) {
                if (i == list.size() - 1) {
                    mStrBuilder.append("\"face_card_num\"")
                            .append("=>").append("\"").append(list.get(i).getEtccarnumber()).append("\"").append(",")
                            .append("\"fee\"").append("=>").append("\"").append((int) (Double.parseDouble(list.get(i).getRechargemoney()) * 100)).append("\"")
                            .append("/total_fee/")
                            .append((int) (Double.parseDouble(list.get(0).getAlloney())))
                            .append("/singular/").append(list.size());
                    break;
                }
                mStrBuilder.append("\"face_card_num\"")
                        .append("=>").append("\"").append(list.get(i).getEtccarnumber()).append("\"")
                        .append(",").append("\"fee\"").append("=>").append("\"").append((int) (Double.parseDouble(list.get(i).getRechargemoney()) * 100)).append("\"")
                        .append(";");
            }
        }
        urls = WXORDER + mStrBuilder.toString();
        Log.e(TAG, urls + ",list.length:" + list.size());
        return false;
    }

    private void wxPay(String url) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkHttpUtils
                        .post()
                        .url(url)
                        .build()
                        .execute().body().string());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        boolean b = WXPay.TuneUpWxPay(s);
                        if (!b) {
                            ToastUtils.showToast(R.string.request_failed);
                        } else {
                            Constants.ETC_RECHARGE = b;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(R.string.send_faid);
                        return;
                    }
                });
    }

    /**
     * 支付宝进行请求
     * @param signInfo
     */
    private void payToOrderService(String signInfo) {

      Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                PayTask payTask = new PayTask(SelectPayWaysActivity.this);
                Map<String, String> result = payTask.payV2(signInfo, true);
                e.onNext(result);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        closeProgressDialog();
                        PayResult payResult = null;
                        try {

                            payResult = new PayResult((Map<String, String>) o);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /**
                         对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                         */
                        String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为9000则代表支付成功
                        if (TextUtils.equals(resultStatus, AliPay.PAY_OK)) {//--------->支付成功
                            finish();
                        } else if (TextUtils.equals(resultStatus, AliPay.PAY_FAIL)) {//--------->支付失败
                            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                            ToastUtils.showToast(payResult.getMemo());
                        } else if (TextUtils.equals(resultStatus, AliPay.PAY_CANCEL)) {//--------->交易取消
                            ToastUtils.showToast(payResult.getMemo());
                        } else if (TextUtils.equals(resultStatus, AliPay.PAY_NET_ERROR)) {//---------->网络出现错误
                            ToastUtils.showToast(payResult.getMemo());
                        } else if (TextUtils.equals(resultStatus, AliPay.PAY_REPEAT)) {//------>交易重复
                        }
                    }
                });
    }

}