package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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

import static com.etcxc.android.R.id.login_phonenumber_delete;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText mLoginPhonenumberEdt; // 手机号码
    private ImageView mLoginPhonenumberDelete;//   删除手机号码
    private EditText mLoginPasswordEdt;//密码
    private ImageView mLoginEye; //可见与不可见
    private EditText mLoginVerificodeEdt;//输入图形验证码
    private ImageView mLoginImageVerificode;//图形取验证码
    private ImageView mLoginFreshVerification;//刷新验证码
    private TextView mLoginMessage;//短信验证码登录
    private TextView mLoginFast;//快速注册
    private TextView mForgetPassword;//忘记密码
    private Button mLoginButton;//  登录
    private Toolbar mToobar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        initView();
    }

    private void initView() {
        Toolbar mToolbar = find(R.id.login_toolbar);
        mToolbar.setTitle(getString(R.string.login));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mLoginPhonenumberEdt = find(R.id.login_phonenumber_edt);
        mLoginPhonenumberDelete = find(login_phonenumber_delete);
        mLoginPasswordEdt = find(R.id.login_password_edt);
        mLoginEye = find(R.id.login_eye);
        mLoginVerificodeEdt = find(R.id.login_verificode_edt);
        mLoginImageVerificode = find(R.id.login_image_verificode);
        mLoginFreshVerification = find(R.id.login_fresh_verification);
        mLoginMessage = find(R.id.login_message);
        mLoginFast = find(R.id.login_fast);
        mForgetPassword = find(R.id.forget_password);
        mLoginButton = find(R.id.login_button);
        addIcon(mLoginPhonenumberEdt, R.drawable.vd_my);
        addIcon(mLoginPasswordEdt, R.drawable.vd_regist_password);
        addIcon(mLoginVerificodeEdt, R.drawable.vd_regist_captcha);
        init();
    }
    private void init() {
        mLoginPhonenumberDelete.setOnClickListener(this);
        mLoginFreshVerification.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mLoginEye.setOnClickListener(this);
        mLoginMessage.setOnClickListener(this);
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mLoginPhonenumberEdt.addTextChangedListener(myTextWatcher);
        // ToDo 向后台请求初始化图形验证码
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
        String regExp = "((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";//;
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobiles);
        return m.matches();
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
            if (temp.length() > 0 && !mLoginPhonenumberEdt.getText().toString().isEmpty()) {
                mLoginPhonenumberDelete.setVisibility(View.VISIBLE);
                temp = "";
            } else {
                mLoginPhonenumberDelete.setVisibility(View.INVISIBLE);
            }
        }
    }

    private Boolean flag = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_phonenumber_edt:
                break;
            case R.id.login_phonenumber_delete:
                mLoginPhonenumberEdt.setText("");
                break;
            case R.id.login_password_edt:
                break;
            case R.id.login_eye:
                mLoginPasswordEdt.setHorizontallyScrolling(true);//不可换行
                if (flag == true) {
                    mLoginPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag = false;
                    mLoginEye.setImageResource(R.drawable.vd_close_eyes);
                } else {
                    mLoginPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                    flag = true;
                    mLoginEye.setImageResource(R.drawable.vd_open_eyes);
                }
                break;
            case R.id.login_verificode_edt:
                break;
            case R.id.login_image_verificode:
                break;
            case R.id.login_fresh_verification:
                startRotateAnimation(mLoginFreshVerification, R.anim.login_code_rotate);
                // ToDo 向后台请求更换验证码，同时更新验证码图片

                break;
            case R.id.login_message:  //短信验证码登录
                Intent intentMsg = new Intent(this,MessageLoginActivity.class);
                startActivity(intentMsg);
                break;
            case R.id.login_fast:
                break;
            case R.id.forget_password:
                break;
            case R.id.login_more_type:
                break;
            case R.id.login_button:  //登录
                String phoneNum = mLoginPhonenumberEdt.getText().toString().trim();
                String passWord = mLoginPasswordEdt.getText().toString().trim();
                String  veriFicodem = mLoginVerificodeEdt.getText().toString().trim();//验证码
                //判断
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
                }else if(veriFicodem.isEmpty()){
                    ToastUtils.showToast(R.string.please_input_verificodem);
                    return;
                }
                //todo: 后台校验验证码的正确性
                //todo : 请求后台 校验用户登录信息，成功则返回我的界面，并保存用户信息

                ToastUtils.showToast(R.string.login);
               break;
        }
    }

    /**
     * 旋转动画
     * @param  view
     * @param setid
     */
    public void startRotateAnimation(View view, int setid) {
        Animation rotateAnim = AnimationUtils.loadAnimation(this, setid);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnim.setInterpolator(lin);
        if (rotateAnim != null) {
            view.startAnimation(rotateAnim);
        }
    }
     /*
       停止旋转
      */
    public void stopRotateAnimation(View v) {
        v.clearAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRotateAnimation(mLoginFreshVerification);
    }
}
