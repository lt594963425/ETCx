package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.FUNC;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.TimeCount;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.myTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.net.OkClient.get;
import static com.etcxc.android.utils.UIUtils.LEFT;
import static com.etcxc.android.utils.UIUtils.initAutoComplete;
import static com.etcxc.android.utils.UIUtils.isMobileNO;
import static com.etcxc.android.utils.UIUtils.saveHistory;

/**
 * 手机短信注册页面
 * Created by 刘涛 on 2017/6/9 0009.
 */

public class PhoneRegistActivity extends BaseActivity implements View.OnClickListener {
    private AutoCompleteTextView mPhoneNumberEdit;
    private EditText mSmsCodeEdit, mPswEdit;
    private Button mRegistButton, mVerificodeButton;
    private ImageView mPhonenumberDelete, mEye, mPwdDeleteBtn;
    private String mPhoneNum;
    private String mPassWord;
    private String mSMSCode;
    private String mSMSID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_regist);
        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        setTitle(R.string.regist);
        mPhoneNumberEdit = find(R.id.phonenumber_edt);//手机号码
        mPswEdit = find(R.id.password_edt);
        mSmsCodeEdit = find(R.id.verificode_edt);
        mRegistButton = find(R.id.regist_button);
        mVerificodeButton = find(R.id.telregist_get_verificode_button);
        mPhonenumberDelete = find(R.id.phonenumber_delete);//清空
        mEye = find(R.id.eye);
        mPwdDeleteBtn = find(R.id.iv_regist_password_delete);
        UIUtils.addIcon(mPhoneNumberEdit, R.drawable.vd_my, LEFT);
        UIUtils.addIcon(mPswEdit, R.drawable.vd_regist_password, LEFT);
        UIUtils.addIcon(mSmsCodeEdit, R.drawable.vd_regist_captcha, LEFT);
        mRegistButton.setOnClickListener(this);
        mVerificodeButton.setOnClickListener(this);
        mPhonenumberDelete.setOnClickListener(this);
        mEye.setOnClickListener(this);
        mPwdDeleteBtn.setOnClickListener(this);
        mPhoneNumberEdit.addTextChangedListener(new myTextWatcher(mPhoneNumberEdit, mPhonenumberDelete));
        mPswEdit.addTextChangedListener(new myTextWatcher(mPswEdit, mPwdDeleteBtn));
        initAutoComplete("history", mPhoneNumberEdit);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_regist_password_delete:
                mPswEdit.setText("");
                break;
            case R.id.regist_button://注册
                phoneRegistButton();
                break;
            case R.id.telregist_get_verificode_button://获取验证码
                getSMSPhoneCode();
                break;
            case R.id.phonenumber_delete:
                mPhoneNumberEdit.setText("");
                break;
            case R.id.eye://
                UIUtils.isLook(mPswEdit, mEye, R.drawable.vd_close_eyes, R.drawable.vd_open_eyes);
        }
    }

    private void getSMSPhoneCode() {
        String phoneNum2 = mPhoneNumberEdit.getText().toString();
        if (!isMobileNO(phoneNum2)) {
            ToastUtils.showToast(R.string.please_input_correct_phone_number);
            return;
        } else if (TextUtils.isEmpty(phoneNum2)) {
            ToastUtils.showToast(R.string.please_input_phonenumber);
            return;
        }

        UIUtils.saveHistory("history", phoneNum2);
         new TimeCount(mVerificodeButton, 60000, 1000).start();
         getSmsCode(phoneNum2);
    }

    @Override
    public String toString() {
        return "PhoneRegistActivity{" +
                "mPhoneNum='" + mPhoneNum + '\'' +
                ", mPassWord='" + mPassWord + '\'' +
                ", mSMSCode='" + mSMSCode + '\'' +
                ", mSMSID='" + mSMSID + '\'' +
                '}';
    }

    private void phoneRegistButton() {
        mPhoneNum = mPhoneNumberEdit.getText().toString();
        mPassWord = mPswEdit.getText().toString().trim();
        mSMSCode = mSmsCodeEdit.getText().toString().trim();
        Log.e(TAG,"PhoneRegistActivity:"+toString());
        if (checkInfo(mPhoneNum, mPassWord, mSMSCode)) return;
        saveHistory("history", mPhoneNum);
        //todo 接口调整
        loginUUrl();
    }

    private boolean checkInfo(String phoneNum, String passWord, String smsCode) {
        if (phoneNum.isEmpty()) {
            ToastUtils.showToast(R.string.phone_isempty);
            return true;
        } else if (!isMobileNO(phoneNum)) {
            ToastUtils.showToast(R.string.please_input_correct_phone_number);
            return true;
        } else if (passWord.isEmpty()) {
            ToastUtils.showToast(R.string.password_isempty);
            return true;
        } else if (passWord.length() < 6) {
            ToastUtils.showToast(R.string.password_isshort);
            return true;
        } else if (smsCode.isEmpty()) {
            ToastUtils.showToast(R.string.set_verifycodes);
            return true;
        }
        return false;
    }

    public void loginUUrl() {
        JSONObject jsonObject = new JSONObject();
        showProgressDialog(getString(R.string.registing));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                jsonObject.put("tel", mPhoneNum);
                jsonObject.put("pwd", mPassWord);
                jsonObject.put("sms_code", mSMSCode);
                jsonObject.put("sms_id", mSMSID);
                e.onNext(get(NetConfig.consistUrl(FUNC.REGISTER), jsonObject));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        Log.e(TAG, "phoneregist:" + s);
                        parseJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.regist_error);
                    }
                });

    }

    private void parseJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            String code = jsonObject.getString("code");
            requestSuccess(jsonObject, code);
            requestError(jsonObject, code);
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showToast(R.string.para_err);
        }
    }

    private void requestError(JSONObject jsonObject, String code) throws JSONException {
        if (code.equals("error")) {
            String returnMsg = jsonObject.getString("message");//返回的信息
            switch (returnMsg) {
                case "sms_code_error":
                    ToastUtils.showToast(R.string.smscodeerr);
                    break;
                case "telphone_unregistered":
                    ToastUtils.showToast(R.string.telphoneunregistered);
                    break;
                case "err_password":
                    ToastUtils.showToast(R.string.passworderr);
                    break;
                case "telphoner_has_been_registered":
                    ToastUtils.showToast(R.string.isregist);
                default:
                    ToastUtils.showToast(R.string.regist_error+returnMsg);
                    break;
            }
        }
    }

    private void requestSuccess(JSONObject jsonObject, String code) throws JSONException {
        if (code.equals("s_ok")) {
            closeProgressDialog();
            ToastUtils.showToast(R.string.registcomlete);
            PublicSPUtil.getInstance().putBoolean("IS_REGIST",true);
            PublicSPUtil.getInstance().putString("tel",mPhoneNum);
            PublicSPUtil.getInstance().putString("pwd",mPassWord);
            openActivity(LoginActivity.class);
            finish();
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
                        try {
                            Log.e(TAG, s);
                            JSONObject object = new JSONObject(s);
                            String code = object.getString("code");
                            if (code.equals("s_ok")) {
                                mSMSID= object.getString("sms_id");
                                PublicSPUtil.getInstance().putString("pr_sms_id", mSMSID);
                                ToastUtils.showToast(R.string.send_success);
                            }
                            if (code.equals("error")) {
                                ToastUtils.showToast(R.string.request_failed);

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


}
