package com.etcxc.android.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.TimeCount;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.myTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

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
    private String loginSmsUrl = "http://192.168.6.58/register/register/register/";
    private String smsCodeUrl = "http://192.168.6.58/register/reg_sms/smsreport/tel/";
    private AutoCompleteTextView mPhoneNumberEdit;
    private EditText mSmsCodeEdit, mPswEdit;
    private Button mRegistButton, mVerificodeButton;
    private ImageView mPhonenumberDelete, mEye, mPwdDeleteBtn;
    private Boolean flag = false;
    SharedPreferences sPUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_regist);
        sPUser = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
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
        initAutoComplete(this, "history", mPhoneNumberEdit);
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
                String phoneNum2 = mPhoneNumberEdit.getText().toString();
                if (!isMobileNO(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                } else if (TextUtils.isEmpty(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                    return;
                }
                UIUtils.saveHistory(UIUtils.getContext(), "history", phoneNum2);
                TimeCount time = new TimeCount(mVerificodeButton,60000, 1000);
                time.start();
                getSmsCode(smsCodeUrl + phoneNum2);
                break;
            case R.id.phonenumber_delete://置空手机号
                mPhoneNumberEdit.setText("");
                break;
            case R.id.eye://
                UIUtils.isLook(mPswEdit, mEye, R.drawable.vd_close_eyes, R.drawable.vd_open_eyes);
        }
    }

    private void phoneRegistButton() {
        ArrayList<String> list = new ArrayList<>();
        String smsID = PrefUtils.getString(App.get(), "pr_sms_id", null);
        String phoneNum = mPhoneNumberEdit.getText().toString();
        String passWord = mPswEdit.getText().toString().trim();
        String smsCode = mSmsCodeEdit.getText().toString().trim();
        String pwd = Md5Utils.encryptpwd(passWord);
        //tel/'tel'/reg_sms_code/'reg_sms_code'/pwd/'pwd'/sms_id/'sms_id'
        String data = "tel/" + phoneNum +
                "/reg_sms_code/" + smsCode +
                "/pwd/" + pwd +
                "/sms_id/" + smsID;
        if (phoneNum.isEmpty()) {
            ToastUtils.showToast(R.string.phone_isempty);
            return;
        } else if (!isMobileNO(phoneNum)) {
            ToastUtils.showToast(R.string.please_input_correct_phone_number);
            return;
        } else if (passWord.isEmpty()) {
            ToastUtils.showToast(R.string.password_isempty);
            return;
        } else if (passWord.length() < 6) {
            ToastUtils.showToast(R.string.password_isshort);
            return;
        } else if (smsCode.isEmpty()) {
            ToastUtils.showToast(R.string.set_verifycodes);
            return;
        }
        saveHistory(this, "history", phoneNum);
        loginUUrl(loginSmsUrl + data);
    }

    public void loginUUrl(String url) {
        showProgressDialog(getString(R.string.registing));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = get(url, new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        parseJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.intenet_err);
                    }
                });
    }

    private void parseJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            String code = jsonObject.getString("code");
            if (code.equals("s_ok")) {
                //请求成功
                JSONObject varJson = jsonObject.getJSONObject("var");
                String tel = varJson.getString("tel");
                String pwd = varJson.getString("pwd");
                String regTime = varJson.getString("reg_time");
                String nickName = varJson.getString("nick_name");
                //  todo   保存用户信息到本地  通过eventbus 把手机号码传递到Mine界面
                SharedPreferences.Editor editor = sPUser.edit();
                editor.putString("telphone", tel);
                editor.putString("password", pwd);
                editor.putString("regtime", regTime);
                editor.putString("nickname", nickName);
                editor.commit();
                closeProgressDialog();
                ToastUtils.showToast(R.string.registcomlete);
                finish();
            }
            if (code.equals("err")) {
                String returnMsg = jsonObject.getString("message");//返回的信息
                if (returnMsg.equals("sms_code_error")) {
                    closeProgressDialog();
                    ToastUtils.showToast(R.string.smscodeerr);
                } else if (returnMsg.equals("telphone_unregistered")) {
                    closeProgressDialog();
                    ToastUtils.showToast(R.string.telphoneunregistered);
                } else if (returnMsg.equals("err_password")) {
                    closeProgressDialog();
                    ToastUtils.showToast(R.string.passworderr);
                } else if (returnMsg.equals("telphoner_has_been_registered")) {
                    closeProgressDialog();
                    ToastUtils.showToast(R.string.isregist);
                } else {
                    closeProgressDialog();
                    ToastUtils.showToast(returnMsg);
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            closeProgressDialog();
            ToastUtils.showToast(R.string.para_err);
        }
    }

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
                                String smsID = jsonvar.getString("sms_id");
                                PrefUtils.setString(App.get(), "pr_sms_id", smsID);//rp_sms_id
                                ToastUtils.showToast(R.string.send_success);
                            }
                            if (code.equals("err")) {
                                // String msg = object.getString("");
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
