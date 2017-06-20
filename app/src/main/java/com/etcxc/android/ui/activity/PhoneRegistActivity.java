package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
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
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.etcxc.android.utils.UIUtils.LEFT;

/**
 * 手机注册页面
 * Created by 刘涛 on 2017/6/9 0009.
 */

public class PhoneRegistActivity extends BaseActivity implements View.OnClickListener {
    private EditText mPhoneNumberEdit, mPswEdit, mVerifiCodeEdit;
    private Button mRegistButton, mVerificodeButton;
    private ImageView mPhonenumberDelete, mEye,mPwdDeleteBtn;
    private Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_regist);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        initView();
    }

    //verificode_edt
    private void initView() {
        Toolbar mToolbar = (Toolbar) find(R.id.regist_toolbar);
        mToolbar.setTitle(R.string.regist);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mPhoneNumberEdit =  find(R.id.phonenumber_edt);//手机号码
        mPswEdit =  find(R.id.password_edt);
        mVerifiCodeEdit = find(R.id.verificode_edt);
        mRegistButton =  find(R.id.regist_button);
        mVerificodeButton = find(R.id.get_verificode_button);
        mPhonenumberDelete =  find(R.id.phonenumber_delete);//清空
        mEye =  find(R.id.eye);
        mPwdDeleteBtn = find(R.id.iv_regist_password_delete);
        UIUtils.addIcon(mPhoneNumberEdit, R.drawable.vd_my, LEFT);
        UIUtils.addIcon(mPswEdit, R.drawable.vd_regist_password, LEFT);
        UIUtils.addIcon(mVerifiCodeEdit, R.drawable.vd_regist_captcha, LEFT);
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
        String regExp ="((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$" ;//;
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
                String phoneNum = mPhoneNumberEdit.getText().toString();
                String passWord = mPswEdit.getText().toString().trim();
                String veriFicode = mVerifiCodeEdit.getText().toString().trim();
                if(phoneNum.isEmpty()){
                    ToastUtils.showToast(R.string.phone_isempty);
                    return;
                }else if (!isMobileNO(phoneNum)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                }else if(passWord.isEmpty()){
                    ToastUtils.showToast(R.string.password_isempty);
                    return;
                }else if(passWord.length() < 6){
                    ToastUtils.showToast(R.string.password_isshort);
                    return;
                }else if(veriFicode.isEmpty()){
                    ToastUtils.showToast(R.string.please_input_verificodem);
                    return;
                }
                ToastUtils.showToast(R.string.regist);
                //todo: 密码强弱长短校验
                //todo:发验证码后端校验
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

    /**
     * 监听手机号码的长度
     */
    CharSequence temp;
    public class MyTextWatcher implements TextWatcher{
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
