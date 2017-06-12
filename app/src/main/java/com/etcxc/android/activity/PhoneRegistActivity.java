package com.etcxc.android.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机注册页面
 * Created by 刘涛 on 2017/6/9 0009.
 */

public class PhoneRegistActivity extends BaseActivity implements View.OnClickListener {
    private EditText mPhoneNumberEdit, mPswEdit, mVerifiCodeEdit;
    private Button mRegistButton, mVerificodeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_regist);
        initView();
    }

    private void initView() {
        mPhoneNumberEdit = find(R.id.phonenumber_edt);
        mPswEdit = find(R.id.password_edt);
        mVerifiCodeEdit = find(R.id.verificode_edt);
        mRegistButton = find(R.id.regist_button);
        mVerificodeButton = find(R.id.get_verificode_button);
        mRegistButton.setOnClickListener(this);
        mVerificodeButton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regist_button:
              String phoneNum = mPhoneNumberEdit.getText().toString();
                if (!isMobileNO(phoneNum)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                }
                //todo: 密码强弱长短校验
                //todo:发验证码后端校验
                break;
            case R.id.get_verificode_button:
                String phoneNum2 = mPhoneNumberEdit.getText().toString();
                if(!isMobileNO(phoneNum2)){
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                }else if(TextUtils.isEmpty(phoneNum2)){
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                    return;
                }
                //todo：向后端请求获取短信验证码
                break;
        }
    }
}
