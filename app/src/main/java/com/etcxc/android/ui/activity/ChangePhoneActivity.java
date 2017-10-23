package com.etcxc.android.ui.activity;

import android.os.Bundle;
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
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.FUNC;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.TimeCount;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.mTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.FUNC.TELCHANGE;
import static com.etcxc.android.net.NetConfig.HOST;
import static com.etcxc.android.net.NetConfig.JSON;
import static com.etcxc.android.utils.UIUtils.initAutoComplete;
import static com.etcxc.android.utils.UIUtils.isMobileNO;

/**
 * 改手机号码
 * Created by 刘涛 on 2017/7/4 0004.
 */

public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {
    private AutoCompleteTextView mNewPhoneEdt;
    private EditText mNewCaptchaEdt;
    private TextView mGetCaptcha;
    private Button mSavePhoneBtn;
    private ImageView mNewPhoneDle;

    private String smsCode;//验证码
    private String mPhoneNum;
    private String mNewPhone;
    private String mSMSID;
    private String CP_SMS_COUNT_DOWN = "timeChPh";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        initView();

    }

    private void initView() {
        setTitle(R.string.changephone);
        mNewPhoneDle = find(R.id.new_phone_delete);
        mGetCaptcha = find(R.id.get_captcha);
        mNewPhoneEdt = find(R.id.new_phone_edt);
        mNewCaptchaEdt = find(R.id.new_captcha_edt);
        mSavePhoneBtn = find(R.id.save_phone_button);

        mNewPhoneDle.setOnClickListener(this);
        mGetCaptcha.setOnClickListener(this);
        mSavePhoneBtn.setOnClickListener(this);

        mNewPhoneEdt.addTextChangedListener(new mTextWatcher(mNewPhoneEdt, mNewPhoneDle));
        initAutoComplete("history", mNewPhoneEdt);
        long timeDef = 60000 - (System.currentTimeMillis() - PublicSPUtil.getInstance().getLong(CP_SMS_COUNT_DOWN, 0));
        if (timeDef > 0) new TimeCount(mGetCaptcha, timeDef, 1000).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_phone_delete:
                mNewPhoneEdt.setText("");
                break;

            case R.id.get_captcha://获取短信验证码
                mPhoneNum = mNewPhoneEdt.getText().toString();
                if (!isMobileNO(mPhoneNum)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                } else if (TextUtils.isEmpty(mPhoneNum)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                } else if (mPhoneNum.equals(MeManager.getUid())) {
                    ToastUtils.showToast(R.string.phone_issame);
                } else {
                    getSmsCode(FUNC.SMSREPORT);
                }
                break;
            case R.id.save_phone_button://保存
                mNewPhone = mNewPhoneEdt.getText().toString().trim();
                smsCode = mNewCaptchaEdt.getText().toString().trim();
                if (!isMobileNO(mNewPhone)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                } else if (TextUtils.isEmpty(mNewPhone)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                } else if (mNewPhone.equals(MeManager.getUid())) {
                    ToastUtils.showToast(R.string.phone_issame);
                } else if (TextUtils.isEmpty(smsCode)) {
                    ToastUtils.showToast(R.string.please_input_smscode);
                } else {
                    telChange();
                }
                break;
        }
    }


    /**
     * 获取验证码
     *
     * @param url
     */
    public void getSmsCode(String url) {
        JSONObject jsonObject = new JSONObject();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                jsonObject.put("tel", mPhoneNum);
                e.onNext(
                        OkHttpUtils
                                .postString()
                                .url(NetConfig.HOST + url)
                                .content(String.valueOf(jsonObject))
                                .mediaType(JSON)
                                .build()
                                .execute().body().string());

            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        try {
                            JSONObject object = new JSONObject(s);
                            String code = object.getString("code");

                            if ("s_ok".equals(code)) {
                                mSMSID = object.getString("sms_id");

                                ToastUtils.showToast(R.string.send_success);
                                UIUtils.saveHistory("history", mPhoneNum);
                                PublicSPUtil.getInstance().putLong(CP_SMS_COUNT_DOWN, System.currentTimeMillis());
                                new TimeCount(mGetCaptcha, 60000, 1000).start();
                            }
                            if ("error".equals(code)) {
                                String returnMsg = object.getString("message");//返回的信息
                                if (NetConfig.ERROR_TOKEN.equals(returnMsg)) {
                                    MeManager.setIsLgon(false);
                                    openActivity(LoginActivity.class);
                                    finish();
                                } else
                                    ToastUtils.showToast(R.string.unregist);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast(R.string.request_failed);

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
     */
    private void telChange() {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject params = new JSONObject();
                params.put("uid", MeManager.getUid())
                        .put("new_tel", mNewPhone)
                        .put("sms_code", smsCode)
                        .put("sms_id", mSMSID)
                        .put("token", PublicSPUtil.getInstance().getString("token", ""));
                Log.e(TAG, params.toString());
                e.onNext(OkHttpUtils
                        .postString()
                        .url(HOST + TELCHANGE)
                        .content(String.valueOf(params))
                        .mediaType(JSON)
                        .build()
                        .execute().body().string());
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");
                        Log.e(TAG, s);
                        if ("s_ok".equals(code)) {
                            ToastUtils.showToast(R.string.finish);
                            MeManager.clearPhone();
                            MeManager.setPhone(mNewPhone);
                            openActivity(MainActivity.class);
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