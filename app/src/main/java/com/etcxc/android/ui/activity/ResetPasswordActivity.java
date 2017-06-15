package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.graphics.drawable.VectorDrawableCompat;
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
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * 找回密码
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText mPhoneNumberEdit, mResetPwd, mVerifiCodeEdit;
    private Button mRegistButton, mVerificodeButton;
    private ImageView mPhonenumberDelete, mEye;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
    }
    //verificode_edt
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
        mPhoneNumberEdit =  find(R.id.reset_phonenumber_edt);//手机号码
        mPhonenumberDelete =  find(R.id.reset_phonenumber_delete);//清空
        mResetPwd =  find(R.id.reset_password_edt);
        mEye =  find(R.id.reset_eye);
        mVerifiCodeEdit = find(R.id.reset_verificode_edt);
        mVerificodeButton = find(R.id.get_reset_verificode_button);
        mRegistButton =  find(R.id.reset_button);
        addIcon(mPhoneNumberEdit, R.drawable.vd_my);
        addIcon(mResetPwd, R.drawable.vd_regist_password);
        addIcon(mVerifiCodeEdit, R.drawable.vd_regist_captcha);
        mRegistButton.setOnClickListener(this);
        mVerificodeButton.setOnClickListener(this);
        mPhonenumberDelete.setOnClickListener(this);
        mEye.setOnClickListener(this);
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mPhoneNumberEdit.addTextChangedListener(myTextWatcher);
    }
    public void addIcon(TextView view, int resId) {
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), resId, null);
        //drawable.setTint(Color.BLACK);
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        view.setCompoundDrawablePadding(UIUtils.dip2Px(16));
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
    private Boolean flag = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.reset_button://找回密码完成，跳回登录界面
                String phoneNum = mPhoneNumberEdit.getText().toString();
                String passWord = mResetPwd.getText().toString().trim();
                String veriFicode = mVerifiCodeEdit.getText().toString().trim();
                if(phoneNum.isEmpty()){
                    ToastUtils.showToast(R.string.phone_isempty);
                    return;
                }else if (!isMobileNO(phoneNum)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                }else if(veriFicode.isEmpty()){
                    ToastUtils.showToast(R.string.please_input_verificodem);
                    return;
                }else if(passWord.isEmpty()){
                    ToastUtils.showToast(R.string.password_isempty);
                    return;
                }else if(passWord.length() < 6){
                    ToastUtils.showToast(R.string.password_isshort);
                    return;
                }
                ToastUtils.showToast(R.string.regist);
                //todo: 密码强弱长短校验
                //todo:发验证码后端校验
                //todo :修改密码成功，跳回重新登录界面


                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.get_reset_verificode_button://获取验证码
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

            case R.id.reset_phonenumber_delete://置空手机号
                mPhoneNumberEdit.setText("");
                break;
            case R.id.reset_eye:
                mResetPwd.setHorizontallyScrolling(true);//不可换行
                if (flag == true) {
                    mResetPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag = false;
                    mEye.setImageResource(R.drawable.vd_close_eyes);
                } else {
                    mResetPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag = true;
                    mEye.setImageResource(R.drawable.vd_open_eyes);
                }
        }
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

