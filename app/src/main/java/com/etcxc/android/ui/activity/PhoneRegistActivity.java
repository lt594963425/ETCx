package com.etcxc.android.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.DialogUtils;
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.etcxc.android.utils.UIUtils.LEFT;

/**
 * 手机短信注册页面
 * Created by 刘涛 on 2017/6/9 0009.
 */

public class PhoneRegistActivity extends BaseActivity implements View.OnClickListener {
    private String loginSmsUrl = "http://192.168.6.58/register/register/register/";
    private String smsCodeUrl = "http://192.168.6.58/register/reg_sms/smsreport/tel/";
    private EditText mPhoneNumberEdit, mPswEdit, mSmsCodeEdit;
    private Button mRegistButton, mVerificodeButton;
    private ImageView mPhonenumberDelete, mEye, mPwdDeleteBtn;
    private Boolean flag = false;
    SharedPreferences sPUser;
    private Dialog mDialog;

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
        mVerificodeButton = find(R.id.get_verificode_button);
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
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mPhoneNumberEdit.addTextChangedListener(myTextWatcher);
        mPswEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !mPswEdit.getText().toString().isEmpty()) {
                    mPwdDeleteBtn.setVisibility(View.VISIBLE);
                } else {
                    mPwdDeleteBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    /**
     * 判断手机号码是否正确
     */
    public boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        String regExp = "((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";//;
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobiles);
        return m.matches();
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
            case R.id.get_verificode_button://获取验证码
                String phoneNum2 = mPhoneNumberEdit.getText().toString();
                if (!isMobileNO(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                } else if (TextUtils.isEmpty(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                    return;
                }
                TimeCount time = new TimeCount(60000, 1000);
                time.start();
                //todo：向后端请求获取短信验证码
                getSmsCode(smsCodeUrl + phoneNum2);
                break;
            case R.id.phonenumber_delete://置空手机号
                mPhoneNumberEdit.setText("");
                break;
            case R.id.eye:
                mPswEdit.setHorizontallyScrolling(true);//不可换行
                if (flag == true) {
                    mPswEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag = false;
                    mEye.setImageResource(R.drawable.vd_close_eyes);
                } else {
                    mPswEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag = true;
                    mEye.setImageResource(R.drawable.vd_open_eyes);
                }
        }
    }

    private void phoneRegistButton() {
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
        mDialog = DialogUtils.createLoadingDialog(PhoneRegistActivity.this, getString(R.string.registing));
        loginUUrl(loginSmsUrl + data);
    }

    public void loginUUrl(String url) {
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
                        parseJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        DialogUtils.closeDialog(mDialog);
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
                DialogUtils.closeDialog(mDialog);
                ToaltsThreadUIshow("注册成功，请登录");
                finish();
            }
            if (code.equals("err")) {
                String returnMsg = jsonObject.getString("message");//返回的信息
                // todo 返回数据待改进
                if (returnMsg.equals("sms_code_error")) {
                    DialogUtils.closeDialog(mDialog);
                    ToastUtils.showToast(R.string.smscodeerr);
                } else if (returnMsg.equals("telphone_unregistered")) {
                    DialogUtils.closeDialog(mDialog);
                    ToastUtils.showToast(R.string.telphoneunregistered);
                } else if (returnMsg.equals("err_password")) {
                    DialogUtils.closeDialog(mDialog);
                    ToastUtils.showToast(R.string.passworderr);
                } else if (returnMsg.equals("telphoner_has_been_registered")) {
                    DialogUtils.closeDialog(mDialog);
                    ToastUtils.showToast("手机号码已经注册");
                }else {
                    DialogUtils.closeDialog(mDialog);
                    ToastUtils.showToast(returnMsg);
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogUtils.closeDialog(mDialog);
            ToastUtils.showToast(R.string.para_err);
        }
    }

    public void getSmsCode(String url) {
        Request requst = new Request.Builder()
                .url(url)//http://192.169.6.119/login/login/login/tel/15974255013/pwd/123456/code/wrty
                .build();
        client.newCall(requst).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                PhoneRegistActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("网络不佳，获取短信失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string().trim();
                try {
                    JSONObject object = new JSONObject(s);
                    String code = object.getString("code");
                    if (code.equals("s_ok")) {
                        JSONObject jsonvar = object.getJSONObject("var");
                        String smsID = jsonvar.getString("sms_id");
                        PrefUtils.setString(App.get(), "pr_sms_id", smsID);//rp_sms_id
                        ToaltsThreadUIshow("发送成功");
                    }
                    if (code.equals("err")) {
                        // String msg = object.getString("");
                        ToaltsThreadUIshow("手机已经注册或者网络不佳");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToaltsThreadUIshow("测试测试+发送失败" + R.string.jsonexception);
                    return;
                }
            }
        });
    }

    private void ToaltsThreadUIshow(Object s) {
        PhoneRegistActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {//mLoginVerificodeEdt
                ToastUtils.showToast(s + "");
            }
        });
    }

    /**
     * 监听手机号码的长度
     */
    CharSequence temp;

    public class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() > 0 && !mPhoneNumberEdit.getText().toString().isEmpty()) {
                mPhonenumberDelete.setVisibility(View.VISIBLE);
                temp = "";
            } else {
                mPhonenumberDelete.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 倒计时
     */
    public class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {//R.color.colorAccent #B6B6D8
            mVerificodeButton.setBackgroundColor(UIUtils.getColor(R.color.colorGray));
            mVerificodeButton.setClickable(false);
            mVerificodeButton.setText("(" + millisUntilFinished / 1000 + ")" + getString(R.string.timeLate));
        }

        @Override
        public void onFinish() {
            mVerificodeButton.setText(getString(R.string.reStartGetCode));
            mVerificodeButton.setClickable(true);
            mVerificodeButton.setBackgroundColor(UIUtils.getColor(R.color.colorGreen));
        }
    }

}
