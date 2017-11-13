package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.helper.VersionUpdateHelper;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.net.download.DownloadOptions;
import com.etcxc.android.pay.alipay.AliPay;
import com.etcxc.android.pay.alipay.PayResult;
import com.etcxc.android.ui.view.ColorCircle;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * 关于我们
 * Created by xwpeng on 2017/6/30.
 */

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {
    private TextView mVersionCodeTextView;
    private View mCheckUpdateTextView;
    private VersionUpdateHelper mHelper;
    private ColorCircle mUpdateDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
        mHelper = new VersionUpdateHelper(this);
    }

    public void initView() {
        setTitle(R.string.about_us);
        mVersionCodeTextView = find(R.id.about_us_versioncode);
        mCheckUpdateTextView = find(R.id.about_us_check_update);
        mCheckUpdateTextView.setOnClickListener(this);
        String versionName = BuildConfig.VERSION_NAME;
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        versionName = versionName.substring(0, versionName.lastIndexOf("."));
        mVersionCodeTextView.setText(String.valueOf("V" + versionName));
        find(R.id.about_us_test_json_api).setOnClickListener(this);
        mUpdateDot = find(R.id.update_dot);
        mUpdateDot.setRadius(UIUtils.dip2Px(5));
        mUpdateDot.setColor(getResources().getColor(R.color.update_dot));
        if (PublicSPUtil.getInstance().getInt("check_version_code", 0) > BuildConfig.VERSION_CODE)
            mUpdateDot.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_us_check_update:
                mHelper.checkVersion();
                break;
            case R.id.about_us_test_json_api:
//
                String strURL = "http://192.168.6.46/xczx/issue/pay";
                aliPay(strURL);
                break;
        }
    }

    @Override
    protected void onStart() {
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DownloadOptions options) {
        mHelper.downloadPd(options);
    }

    /**
     * 支付宝
     *
     * @param strURL
     */
    private void aliPay(String strURL) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {
                String orderInfo = null;
                try {
                    JSONObject js = new JSONObject();
                    js.put("licensePlate","冀D56565");
                    js.put("plateColor","蓝底白字");
                    js.put("total_fee","1");
                    js.put("way","ali");
                    js.put("client_type","Android");
                    orderInfo = OkClient.get(strURL, js);
                    orderInfo = orderInfo.replace("amp;", "");
                    PayTask payTask = new PayTask(AboutUsActivity.this);
                    Map<String, String> result = payTask.payV2(orderInfo, true);
                    e.onNext(result);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

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
