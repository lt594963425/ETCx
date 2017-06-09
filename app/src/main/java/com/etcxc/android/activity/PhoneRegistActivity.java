package com.etcxc.android.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.LogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 刘涛 on 2017/6/9 0009.
 */

public class PhoneRegistActivity extends BaseActivity implements View.OnClickListener {
    private EditText phoneEditText;     //设置手机号码
    @BindView(R.id.bt_rg)
    Button registButton;            //注册
    @BindView(R.id.et_setpwd)
    EditText passWordEdit;      //设置密码
    @BindView(R.id.et_yanzhen)
    EditText yanzhenEdit;     //验证码
    @BindView(R.id.bt_getyanzhen)
    Button btGetYanzhen;    //获取验证码

    private String phoneNum;
    private TextWatcher textWatcher;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_regist);
        phoneEditText = (EditText) findViewById(R.id.et_setpnum);//设置手机号码
        passWordEdit = (EditText) findViewById(R.id.et_setpwd);//设置密码
        yanzhenEdit = (EditText) findViewById(R.id.et_yanzhen);//验证码
        btGetYanzhen = (Button) findViewById(R.id.bt_getyanzhen);//获取验证码
        registButton = (Button) findViewById(R.id.bt_rg);//注册
        passWordEdit.setEnabled(false);
        yanzhenEdit.setEnabled(false);
        ButterKnife.bind(this);
        init();
    }

    private CharSequence temp;

    private void init() {
        registButton.setOnClickListener(this);
        btGetYanzhen.setOnClickListener(this);

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneNum = phoneEditText.getText().toString();
                if (temp.length() < 11) {
                    passWordEdit.setEnabled(false);
                    yanzhenEdit.setEnabled(false);
                    return;
                }
                if (temp.length() == 11 && !isMobileNO(phoneNum)) {
                    Toast.makeText(App.getContext(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    passWordEdit.setEnabled(false);
                    yanzhenEdit.setEnabled(false);
                    return;
                } else {
                    passWordEdit.setEnabled(true);
                    yanzhenEdit.setEnabled(true);
                }
            }
        });
    }

    /**
     * 判断手机号码是否正确
     *
     * @param mobiles
     * @return
     */
    public boolean isMobileNO(String mobiles) {
        String regExp = "((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobiles);
        return m.matches();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_rg://注册
                phoneNum = phoneEditText.getText().toString();
                if (!isMobileNO(phoneNum)) {
                    Toast.makeText(App.getContext(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(App.getContext(), "注册", Toast.LENGTH_SHORT).show();
                LogUtil.i(TAG, "------PhoneRegistActivity------注册----------");
                break;
            case R.id.bt_getyanzhen://获取验证码

                phoneNum = phoneEditText.getText().toString();
                if(!isMobileNO(phoneNum)){
                    Toast.makeText(App.getContext(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }else if(phoneNum.isEmpty()){
                    Toast.makeText(App.getContext(), "请输入手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                //调用第三方接口  给手机号码发送短信
                Toast.makeText(App.getContext(), "获取验证码", Toast.LENGTH_SHORT).show();

                break;
        }

    }
}
