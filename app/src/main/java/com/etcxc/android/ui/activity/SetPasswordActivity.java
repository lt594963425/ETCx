package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * 设置密码
 */

public class SetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText mSetPassWordEdt;
    private ImageView mSetPassWordEye;
    private Button mSetAccomplishBtn;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        initView();
    }

    private void initView() {
        mToolbar = find(R.id.set_password_toolbar);
        mToolbar.setTitle(R.string.setpassword);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setTitle(getString(R.string.setpassword));
        mSetPassWordEdt = find(R.id.set_password_edt);
        mSetPassWordEye = find(R.id.set_password_eye);
        mSetAccomplishBtn = find(R.id.set_accomplish_button);
        mSetPassWordEye.setOnClickListener(this);
        mSetAccomplishBtn.setOnClickListener(this);
    }

    Boolean flag = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_password_eye:
                mSetPassWordEdt.setHorizontallyScrolling(true);//不可换行
                if (flag == true) {
                    mSetPassWordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag = false;
                    mSetPassWordEye.setImageResource(R.drawable.vd_close_eyes);
                } else {
                    mSetPassWordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                    flag = true;
                    mSetPassWordEye.setImageResource(R.drawable.vd_open_eyes);
                }
                break;
            case R.id.set_accomplish_button:  //完成
               String pwd = mSetPassWordEdt.getText().toString().trim();
                if(pwd.isEmpty()){
                    ToastUtils.showToast(R.string.password_isempty);
                    return;
                }else if(pwd.length()< 6){
                    ToastUtils.showToast(R.string.password_isshort);
                    return;
                }
                //todo 设置密码完成 提交后台,保存用户登录信息，返回

                finish();
                break;
        }
    }
}
