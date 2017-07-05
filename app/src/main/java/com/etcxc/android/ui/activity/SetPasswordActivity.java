package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;

import static com.etcxc.android.utils.UIUtils.isLook;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * 设置密码
 */

public class SetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText mSetPassWordEdt;
    private ImageView mSetPassWordEye;
    private Button mSetAccomplishBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.setpassword));
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
               isLook(mSetPassWordEdt,mSetPassWordEye,R.drawable.vd_close_eyes,R.drawable.vd_open_eyes);
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
                finish();
                break;
        }
    }
}
