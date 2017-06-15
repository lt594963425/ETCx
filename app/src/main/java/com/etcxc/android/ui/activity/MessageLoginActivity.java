package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * 短信登录
 */

public class MessageLoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mMPhoneNumberEdt;
    private ImageView mMPhoneNumberDelete;
    private EditText mMVeriFicodeEdt;
    private Button mGetMsgVeriFicodeButton;
    private Button mMLoginButton;
    private RelativeLayout mMsgLVLayout;
    private EditText mMPicCodeEdt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {//message_login_toolbar
        Toolbar mToolbar = find(R.id.message_login_toolbar);
        mToolbar.setTitle(R.string.messagelogin);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mMPhoneNumberEdt = find(R.id.message_phonenumber_edt);
        mMPhoneNumberDelete = find(R.id.message_phonenumber_delete);
        mMVeriFicodeEdt = find(R.id.message_verificode_edt);
        mGetMsgVeriFicodeButton = find(R.id.get_msg_verificode_button);
        mMLoginButton = find(R.id.message_login_button);
        mMPhoneNumberDelete.setOnClickListener(this);
        mGetMsgVeriFicodeButton.setOnClickListener(this);
        mMLoginButton.setOnClickListener(this);
        //todo 输入的次数超过3次要求输入图形验证码 显示mMsgLVLayout 控件
        mMsgLVLayout=find(R.id.message_login_verificode_layout);
        mMPicCodeEdt = find (R.id.message_login_verificode_edt); //图形验证码message_login_image_verificode
        mMPicCodeEdt = find (R.id.message_login_image_verificode); //图形验证码 message_login_image_verificode
        mMPicCodeEdt = find (R.id.message_login_fresh_verification); //刷新图形验证码 message_login_fresh_verification

        addIcon(mMPicCodeEdt,R.drawable.vd_regist_captcha);
        addIcon(mMPhoneNumberEdt, R.drawable.vd_my);
        addIcon(mMVeriFicodeEdt, R.drawable.vd_regist_captcha);
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mMPhoneNumberEdt.addTextChangedListener(myTextWatcher);

    }

    public void addIcon(TextView view, int resId) {
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), resId, null);
        //drawable.setTint(Color.BLACK);
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        view.setCompoundDrawablePadding(UIUtils.dip2Px(16));
    }

      //({R.id.message_phonenumber_delete, R.id.get_msg_verificode_button, R.id.message_login_button})
    private boolean isUser = false ;
    @Override
    public void  onClick(View view) {
        switch (view.getId()) {
            case R.id.message_phonenumber_delete:
                mMPhoneNumberEdt.setText("");
                break;
            case R.id.get_msg_verificode_button://获取验证码
                String phoneNum2 = mMPhoneNumberEdt.getText().toString().trim();
                if (!isMobileNO(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                } else if (TextUtils.isEmpty(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                    return;
                }
                TimeCount time = new TimeCount(60000, 1000);
                time.start();
                //todo 向后台发送请求获取新的验证码

                break;
            case R.id.message_login_button://登录
                String phoneNum = mMPhoneNumberEdt.getText().toString().trim();
                String veriFicodem = mMVeriFicodeEdt.getText().toString().trim();//验证码
                //判断
                if (phoneNum.isEmpty()) {
                    ToastUtils.showToast(R.string.phone_isempty);
                    return;
                } else if (!isMobileNO(phoneNum)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                } else if (veriFicodem.isEmpty()) {
                    ToastUtils.showToast(R.string.please_input_verificodem);
                    return;
                }
                //todo: 后台校验验证码的正确性
                //todo : 校验用户登录信息
                //todo : 判断用户是否已经注册 1， 如果没有注册 则跳到密码设置界面 2.若已经注册，则进入我的界面

               if(!isUser){
                   isUser =true;
                   Intent setpsd = new Intent(this,SetPasswordActivity.class);
                   startActivity(setpsd);
                   finish();
               }else {
                   // todo 从后台得到用户信息,并保存到本地,跳转到我的界面
               }
                finish();
                break;
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
            if (temp.length() > 0 && !mMPhoneNumberEdt.getText().toString().isEmpty()) {
                mMPhoneNumberDelete.setVisibility(View.VISIBLE);
                temp = "";
            } else {
                mMPhoneNumberDelete.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 倒计时获取验证码
     */
    public class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {//R.color.colorAccent #B6B6D8
            mGetMsgVeriFicodeButton.setBackgroundColor(UIUtils.getColor(R.color.colorGray));
            mGetMsgVeriFicodeButton.setClickable(false);
            mGetMsgVeriFicodeButton.setText("(" + millisUntilFinished / 1000 + ")" + getString(R.string.timeLate));
        }

        @Override
        public void onFinish() {
            mGetMsgVeriFicodeButton.setText(getString(R.string.reStartGetCode));
            mGetMsgVeriFicodeButton.setClickable(true);
            mGetMsgVeriFicodeButton.setBackgroundColor(UIUtils.getColor(R.color.colorGreen));
        }
    }

    /**
     * 判断手机号码是否正确
     */
    public boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        String regExp = "((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
