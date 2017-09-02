package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.FUNC;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.R.drawable.vd_close_eyes;
import static com.etcxc.android.R.drawable.vd_open_eyes;
import static com.etcxc.android.net.FUNC.RESET_PWD;
import static com.etcxc.android.utils.UIUtils.closeAnimator;
import static com.etcxc.android.utils.UIUtils.initAutoComplete;
import static com.etcxc.android.utils.UIUtils.isLook;
import static com.etcxc.android.utils.UIUtils.isMobileNO;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * 找回密码
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText mResetPwd, mVerifiCodeEdit;
    private AutoCompleteTextView mPhoneNumberEdit;
    private Button mVerificodeButton;
    private ImageView mPhonenumberDelete, mEye, mResetPwdDelete;
    private String mPhoneNum, mPassWord;
    private String mSMSID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
    }

    private void initView() {
        Toolbar mToolbar = find(R.id.reset_password_toolbar);
        mToolbar.setTitle(R.string.retrieve_password);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                closeAnimator(ResetPasswordActivity.this);
            }
        });
        mPhoneNumberEdit = find(R.id.reset_phonenumber_edt);//手机号码
        mPhonenumberDelete = find(R.id.reset_phonenumber_delete);//清空
        mResetPwd = find(R.id.reset_password_edt);
        mResetPwdDelete = find(R.id.reset_password_delete);
        mEye = find(R.id.reset_eye);
        mVerifiCodeEdit = find(R.id.reset_verificode_edt);
        mVerificodeButton = find(R.id.get_reset_verificode_button);
        find(R.id.reset_button).setOnClickListener(this);
        mVerificodeButton.setOnClickListener(this);
        mPhonenumberDelete.setOnClickListener(this);
        mEye.setOnClickListener(this);
        mResetPwdDelete.setOnClickListener(this);
        mPhoneNumberEdit.addTextChangedListener(new mTextWatcher(mPhoneNumberEdit, mPhonenumberDelete));
        mResetPwd.addTextChangedListener(new mTextWatcher(mResetPwd, mResetPwdDelete));  // mResetPwd  mResetPwdDelete
        UIUtils.addIcon(mPhoneNumberEdit, R.drawable.vd_my, UIUtils.LEFT);
        UIUtils.addIcon(mResetPwd, R.drawable.vd_regist_password, UIUtils.LEFT);
        UIUtils.addIcon(mVerifiCodeEdit, R.drawable.vd_regist_captcha, UIUtils.LEFT);
        initAutoComplete("history", mPhoneNumberEdit);
        long timeDef = 60000 - (System.currentTimeMillis() - PublicSPUtil.getInstance().getLong("timeReset", 0));
        if (timeDef > 0) new TimeCount(mVerificodeButton, timeDef, 1000).start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_password_delete:
                mResetPwd.setText("");
                break;
            case R.id.reset_button://找回密码完成，跳回登录界面
                RequstResetPwd();
                break;
            case R.id.get_reset_verificode_button://获取短息验证码
                String phoneNum2 = mPhoneNumberEdit.getText().toString();
                if (!isMobileNO(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                } else if (TextUtils.isEmpty(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                    return;
                }
                getSmsCode(phoneNum2);
                break;
            case R.id.reset_phonenumber_delete://置空手机号
                mPhoneNumberEdit.setText("");
                break;
            case R.id.reset_eye:
                isLook(mResetPwd, mEye, vd_close_eyes, vd_open_eyes);
        }
    }

    private void RequstResetPwd() {
        mPhoneNum = mPhoneNumberEdit.getText().toString();
        mPassWord = mResetPwd.getText().toString().trim();
        String veriFicode = mVerifiCodeEdit.getText().toString().trim();
        if (mPhoneNum.isEmpty()) {
            ToastUtils.showToast(R.string.phone_isempty);
            return;
        } else if (!isMobileNO(mPhoneNum)) {
            ToastUtils.showToast(R.string.please_input_correct_phone_number);
            return;
        } else if (veriFicode.isEmpty()) {
            ToastUtils.showToast(R.string.set_verifycodes);
            return;
        } else if (mPassWord.isEmpty()) {
            ToastUtils.showToast(R.string.password_isempty);
            return;
        } else if (mPassWord.length() < 6) {
            ToastUtils.showToast(R.string.password_isshort);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tel", mPhoneNum);
            jsonObject.put("pwd", mPassWord);
            jsonObject.put("sms_code", veriFicode);
            jsonObject.put("sms_id", mSMSID);
            jsonObject.put(Constants.ORIENTION_KEY, Constants.ORIENTION_VALUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ResetPwdNet(jsonObject);
    }

    public void ResetPwdNet(JSONObject jsonObject) {
        Log.e(TAG,jsonObject.toString());
        showProgressDialog(getString(R.string.loading));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(NetConfig.consistUrl(RESET_PWD), jsonObject);
                e.onNext(result);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<String>() {

                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG,s);
                        closeProgressDialog();
                        parseJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.find_faid);
                        return;
                    }
                });
    }

    private void parseJson(@NonNull String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject == null) return;
            String code = jsonObject.getString("code");
            if (code.equals("s_ok")) {
                MeManager.setPhone(mPhoneNum);
                MeManager.setPWD(mPassWord);
                MeManager.clearToken();
                openActivity(LoginActivity.class);
                ToastUtils.showToast(R.string.find_success);
                finish();
            }
            if (code.equals("err")) {
                String returnMsg = jsonObject.getString("message");
                if (returnMsg.equals("telphone_unregistered")) {
                    ToastUtils.showToast(R.string.telphoneunregistered);
                } else if (returnMsg.equals("sms_code_error")) {
                    ToastUtils.showToast(R.string.smscodeerr);
                } else if (returnMsg.equals("err_password")) {
                    ToastUtils.showToast(R.string.passworderr);//
                } else {
                    ToastUtils.showToast(returnMsg);
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showToast(R.string.find_faid);
            return;
        }
    }

    public void getSmsCode(String phonenum) {
        JSONObject jsonObject = new JSONObject();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                jsonObject.put("tel", phonenum);

                String result = OkClient.get(NetConfig.consistUrl(FUNC.SMSREPORT), jsonObject);
                e.onNext(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, s);
                        JSONObject object = new JSONObject(s);
                        String code = object.getString("code");
                        if (code.equals("s_ok")) {
                            mSMSID = object.getString("sms_id");
                            ToastUtils.showToast(R.string.send_success);
                            PublicSPUtil.getInstance().putLong("timeReset", System.currentTimeMillis());
                            new TimeCount(mVerificodeButton, 60000, 1000).start();
                        }
                        if (code.equals("error")) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

