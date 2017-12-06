package com.etcxc.android.test;

import com.etcxc.android.R;
import com.etcxc.android.net.FUNC;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.pay.WXPay.WXPay;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;



/**
 * 用来测试支付接口
 * Created by xwpeng on 2017/10/24.
 */

public class PayTest {
    public  void wxPay() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("licensePlate", "湘A12345")
                        .put("plateColor", "黄底黑字")
                        .put("total_fee", "1")
                        .put("way", "wx")
                        .put("client_type", "Android");
                e.onNext(OkClient.get(NetConfig.HOST + FUNC.PAY_ISSUE, jsonObject));
            }

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        boolean b = WXPay.TuneUpWxPay(s);
                            ToastUtils.showToast(R.string.request_failed);


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(R.string.pay_faid);
                    }
                });
    }
}
