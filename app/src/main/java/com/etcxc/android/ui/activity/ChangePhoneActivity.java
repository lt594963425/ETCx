package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.TimeCount;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.myTextWatcher;

import static com.etcxc.android.utils.UIUtils.initAutoComplete;

/**
 * Created by 刘涛 on 2017/7/4 0004.
 */

public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {
    private AutoCompleteTextView mOldPhoneEdt, mNewPhoneEdt;
    private EditText mNewCaptchaEdt;
    private TextView mGetCaptcha;
    private Button mSavePhoneBtn;
    private ImageView mOldPhoneDle, mNewPhoneDle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        initView();

    }

    private void initView() {
        setTitle(R.string.changephone);
        mOldPhoneEdt = find(R.id.old_phone_edt);
        mOldPhoneDle = find(R.id.old_phone_delete);
        mNewPhoneDle = find(R.id.new_phone_delete);
        mGetCaptcha = find(R.id.get_captcha);
        mNewPhoneEdt = find(R.id.new_phone_edt);
        mNewCaptchaEdt = find(R.id.new_captcha_edt);
        mSavePhoneBtn = find(R.id.save_phone_button);
        mOldPhoneDle.setOnClickListener(this);
        mNewPhoneDle.setOnClickListener(this);
        mGetCaptcha.setOnClickListener(this);
        mSavePhoneBtn.setOnClickListener(this);
        mOldPhoneEdt.addTextChangedListener(new myTextWatcher(mOldPhoneEdt, mOldPhoneDle));
        mNewPhoneEdt.addTextChangedListener(new myTextWatcher(mNewPhoneEdt, mNewPhoneDle));
        initAutoComplete(this,"history",mOldPhoneEdt);
        initAutoComplete(this,"history",mNewPhoneEdt);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.old_phone_delete:
                mOldPhoneEdt.setText("");
                break;
            case R.id.new_phone_delete:
                mNewPhoneEdt.setText("");
                break;

            case R.id.get_captcha://获取短信验证码
                String  phone =mNewPhoneEdt.getText().toString().trim();
                UIUtils.saveHistory(UIUtils.getContext(),"history",phone);
                TimeCount time = new TimeCount(mGetCaptcha,60000, 1000);
                time.start();
                break;
            case R.id.save_phone_button://保存
                mNewCaptchaEdt.getText().toString().trim();
                String newphone = mNewPhoneEdt.getText().toString().trim();
                break;
        }
    }


}