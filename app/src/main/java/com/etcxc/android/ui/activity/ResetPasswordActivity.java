package com.etcxc.android.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.TimeCount;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.myTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.R.drawable.vd_close_eyes;
import static com.etcxc.android.R.drawable.vd_open_eyes;
import static com.etcxc.android.net.FUNC.INFORMATIONMODIFY;
import static com.etcxc.android.net.FUNC.SMSREPORT;
import static com.etcxc.android.utils.UIUtils.LEFT;
import static com.etcxc.android.utils.UIUtils.addIcon;
import static com.etcxc.android.utils.UIUtils.initAutoComplete;
import static com.etcxc.android.utils.UIUtils.isLook;
import static com.etcxc.android.utils.UIUtils.isMobileNO;
import static com.etcxc.android.utils.UIUtils.saveHistory;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * 找回密码
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private EditText mResetPwd, mVerifiCodeEdit;
    private AutoCompleteTextView mPhoneNumberEdit;
    private Button mRegistButton, mVerificodeButton;
    private ImageView mPhonenumberDelete, mEye, mResetPwdDelete;
    private SharedPreferences sPUser;
    private String phoneNum, passWord, pwd, veriFicode, smsID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        sPUser = getSharedPreferences("user_info", MODE_PRIVATE);
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
            }
        });
        mPhoneNumberEdit = find(R.id.reset_phonenumber_edt);//手机号码
        mPhonenumberDelete = find(R.id.reset_phonenumber_delete);//清空
        mResetPwd = find(R.id.reset_password_edt);
        mResetPwdDelete = find(R.id.reset_password_delete);
        mEye = find(R.id.reset_eye);
        mVerifiCodeEdit = find(R.id.reset_verificode_edt);
        mVerificodeButton = find(R.id.get_reset_verificode_button);
        mRegistButton = find(R.id.reset_button);
        addIcon(mPhoneNumberEdit, R.drawable.vd_my, LEFT);
        addIcon(mResetPwd, R.drawable.vd_regist_password, LEFT);
        addIcon(mVerifiCodeEdit, R.drawable.vd_regist_captcha, LEFT);
        mRegistButton.setOnClickListener(this);
        mVerificodeButton.setOnClickListener(this);
        mPhonenumberDelete.setOnClickListener(this);
        mEye.setOnClickListener(this);
        mResetPwdDelete.setOnClickListener(this);
        mPhoneNumberEdit.addTextChangedListener(new myTextWatcher(mPhoneNumberEdit, mPhonenumberDelete));
        mResetPwd.addTextChangedListener(new myTextWatcher(mResetPwd, mResetPwdDelete));  // mResetPwd  mResetPwdDelete
        initAutoComplete(this, "history", mPhoneNumberEdit);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_password_delete:
                mResetPwd.setText("");
                break;
            case R.id.reset_button://找回密码完成，跳回登录界面
                smsID = PrefUtils.getString(App.get(), "rp_sms_id", null);
                //tel/15974255013/inf_modify_sms_code/190881/pwd/lt767435/sms_id/90743520170623203308
                //data = /tel/'tel'/inf_modify_sms_code/'inf_modify_sms_code'/pwd/'pwd'/sms_id/'sms_id'
                phoneNum = mPhoneNumberEdit.getText().toString();
                passWord = mResetPwd.getText().toString().trim();
                pwd = Md5Utils.encryptpwd(passWord);
                veriFicode = mVerifiCodeEdit.getText().toString().trim();
                if (phoneNum.isEmpty()) {
                    ToastUtils.showToast(R.string.phone_isempty);
                    return;
                } else if (!isMobileNO(phoneNum)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                } else if (veriFicode.isEmpty()) {
                    ToastUtils.showToast(R.string.set_verifycodes);
                    return;
                } else if (passWord.isEmpty()) {
                    ToastUtils.showToast(R.string.password_isempty);
                    return;
                } else if (passWord.length() < 6) {
                    ToastUtils.showToast(R.string.password_isshort);
                    return;
                }
                ResetPwdUrl();
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
                saveHistory(this, "history", phoneNum2);
                //todo：向后端请求获取短信验证码
                getSmsCode(SMSREPORT + phoneNum2);
                break;
            case R.id.reset_phonenumber_delete://置空手机号
                mPhoneNumberEdit.setText("");
                break;
            case R.id.reset_eye:
                isLook(mResetPwd, mEye, vd_close_eyes, vd_open_eyes);
        }
    }

    public void ResetPwdUrl() {
        showProgressDialog(getString(R.string.loading));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tel", phoneNum);
                jsonObject.put("pwd", pwd);
                jsonObject.put("inf_modify_sms_code", veriFicode);
                jsonObject.put("sms_id", smsID);
                String result = OkClient.get(NetConfig.consistUrl(INFORMATIONMODIFY), new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<String>() {

                    @Override
                    public void accept(@NonNull String s) throws Exception {
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
                JSONObject varJson = jsonObject.getJSONObject("var");
                String tel = varJson.getString("tel");
                String pwd = varJson.getString("pwd");
                String lastmodifyTime = varJson.getString("last_modify_time");//标准的时间格式
                String nickName = varJson.getString("nick_name");
                SharedPreferences.Editor editor = sPUser.edit();
                editor.putString("telphone", tel);
                editor.putString("password", pwd);
                editor.putString("lastmodifyTime", lastmodifyTime);
                editor.putString("nickname", nickName);
                editor.commit();
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
            ToastUtils.showToast(R.string.find_faid);// todo 返回数据待改进
            return;
        }
    }

    public void getSmsCode(String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(url, new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        parseSmsCodeJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                        ToastUtils.showToast(R.string.send_faid);
                    }
                });
    }

    private void parseSmsCodeJson(@NonNull String s) {
        try {
            JSONObject object = new JSONObject(s);
            String code = object.getString("code");
            if (code.equals("s_ok")) {//返回tel,sms_id
                JSONObject jsonvar = object.getJSONObject("var");
                String smsID = jsonvar.getString("sms_id");
                PrefUtils.setString(App.get(), "rp_sms_id", smsID);//rp_sms_id
                ToastUtils.showToast(R.string.send_success);
                TimeCount time = new TimeCount(mVerificodeButton, 60000, 1000);
                time.start();
            }
            if (code.equals("err")) {
                String msg = object.getString("message");
                if (msg.equals("password_too_easy")) {
                    ToastUtils.showToast(R.string.pwd_easy);
                } else if (msg.equals("telphone_unregistered")) {
                    ToastUtils.showToast(R.string.telphoneunregistered);
                } else {
                    ToastUtils.showToast(R.string.send_faid);
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showToast(R.string.send_faid);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

