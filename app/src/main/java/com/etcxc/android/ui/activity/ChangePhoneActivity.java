package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.TimeCount;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.myTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.OkClient.get;
import static com.etcxc.android.utils.UIUtils.initAutoComplete;
import static com.etcxc.android.utils.UIUtils.isMobileNO;

/**
 * Created by 刘涛 on 2017/7/4 0004.
 */

public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {
    private AutoCompleteTextView mOldPhoneEdt, mNewPhoneEdt;
    private EditText mNewCaptchaEdt;
    private TextView mGetCaptcha;
    private Button mSavePhoneBtn;
    private ImageView mOldPhoneDle, mNewPhoneDle;

    private String smsCodeUrl = NetConfig.HOST + "/login/tel_change_sms/smsReport/new_tel/";
    private String telChangeUrl = "/login/login/telchange";
    private String smsCode;//验证码
    private String smsID;//验证码id
    private String phone;//返回的手机号码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        initView();

    }

    private void initView() {
        setTitle(R.string.changephone);
//        mOldPhoneEdt = find(R.id.old_phone_edt);
//        mOldPhoneDle = find(R.id.old_phone_delete);
        mNewPhoneDle = find(R.id.new_phone_delete);
        mGetCaptcha = find(R.id.get_captcha);
        mNewPhoneEdt = find(R.id.new_phone_edt);
        mNewCaptchaEdt = find(R.id.new_captcha_edt);
        mSavePhoneBtn = find(R.id.save_phone_button);
//        mOldPhoneDle.setOnClickListener(this);
        mNewPhoneDle.setOnClickListener(this);
        mGetCaptcha.setOnClickListener(this);
        mSavePhoneBtn.setOnClickListener(this);
//        mOldPhoneEdt.addTextChangedListener(new myTextWatcher(mOldPhoneEdt, mOldPhoneDle));
        mNewPhoneEdt.addTextChangedListener(new myTextWatcher(mNewPhoneEdt, mNewPhoneDle));
//        initAutoComplete(this,"history",mOldPhoneEdt);
        initAutoComplete(this, "history", mNewPhoneEdt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.old_phone_delete:
//                mOldPhoneEdt.setText("");
//                break;
            case R.id.new_phone_delete:
                mNewPhoneEdt.setText("");
                break;

            case R.id.get_captcha://获取短信验证码
                String phoneNum = mNewPhoneEdt.getText().toString();
                if (!isMobileNO(phoneNum)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                } else if (TextUtils.isEmpty(phoneNum)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                } else if (phoneNum.equals(MeManager.getSid())) {
                    ToastUtils.showToast(R.string.phone_issame);
                } else {
                    UIUtils.saveHistory(UIUtils.getContext(), "history", phoneNum);
                    TimeCount time = new TimeCount(mGetCaptcha, 60000, 1000);
                    time.start();
                    getSmsCode(smsCodeUrl + phoneNum);
                }
                break;
            case R.id.save_phone_button://保存
                smsCode = mNewCaptchaEdt.getText().toString().trim();
                if (!isMobileNO(phone)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                } else if (TextUtils.isEmpty(phone)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                } else if (phone.equals(MeManager.getSid())) {
                    ToastUtils.showToast(R.string.phone_issame);
                } else if (TextUtils.isEmpty(smsCode)) {
                    ToastUtils.showToast(R.string.please_input_smscode);
                } else {
                    Map<String, String> params = new HashMap<>();
                    params.put("tel", MeManager.getSid());
                    params.put("new_tel", phone);
                    params.put("tel_change_code", smsCode);
                    params.put("sms_id", smsID);
                    telChange(params);
                }
                break;
        }
    }


    /**
     * 获取验证码
     * @param url
     */
    public void getSmsCode(String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = get(url, new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        try {
                            JSONObject object = new JSONObject(s);
                            String code = object.getString("code");
                            if (code.equals("s_ok")) {
                                JSONObject jsonvar = object.getJSONObject("var");
                                smsID = jsonvar.getString("sms_id");
                                phone = jsonvar.getString("tel");
                                PrefUtils.setString(App.get(), "pr_sms_id", smsID);//rp_sms_id
                                ToastUtils.showToast(R.string.send_success);
                            }
                            if (code.equals("err")) {
                                ToastUtils.showToast(R.string.isregist);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast(R.string.request_failed);
                            return;
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
     * 修改手机号
     * @param params
     */
    private void telChange(Map<String, String> params) {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                Log.d(TAG, "subscribe: " + NetConfig.consistUrl(telChangeUrl, params));
                e.onNext(OkClient.get(NetConfig.consistUrl(telChangeUrl, params), new JSONObject()));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");
                        if ("s_ok".equals(code)) {
                            ToastUtils.showToast(R.string.finish);
                            MeManager.setSid(phone);
                            finish();
                        } else {
                            ToastUtils.showToast(R.string.smscodeerr);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "net", throwable);
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.request_failed);
                    }
                });
    }

}