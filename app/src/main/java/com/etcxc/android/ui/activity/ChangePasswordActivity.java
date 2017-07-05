package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.myTextWatcher;

import static com.etcxc.android.utils.UIUtils.isLook;

/**
 * Created by 刘涛 on 2017/7/4 0004.
 */

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText mOldPwdEdt,mNewPwdEdt;
    private ImageView mOldPwdDte,mNewPwdSee,mOldPwdSee,mNewPwdDte;
    private Button mSavePwdBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
     initView();
    }

    private void initView() {
        setTitle(R.string.changepassword);
        mOldPwdEdt = find(R.id.old_password_edt);
        mOldPwdDte = find(R.id.old_password_delete);
        mNewPwdEdt = find(R.id.new_password_edt);
        mNewPwdDte =find(R.id.new_password_delete);
        mNewPwdSee = find(R.id.password_see_iv);
        mSavePwdBtn = find(R.id.password_save_button);
        mOldPwdSee = find(R.id.oldpassword_see_iv);
        mOldPwdDte.setOnClickListener(this);
        mNewPwdDte.setOnClickListener(this);
        mNewPwdSee.setOnClickListener(this);
        mSavePwdBtn.setOnClickListener(this);
        mOldPwdSee.setOnClickListener(this);
        mOldPwdEdt.addTextChangedListener(new myTextWatcher(mOldPwdEdt,mOldPwdDte));
        mNewPwdEdt.addTextChangedListener(new myTextWatcher(mNewPwdEdt,mNewPwdDte));
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()){
          case R.id.old_password_delete:
              mOldPwdEdt.setText("");
              break;
          case R.id.new_password_delete:
              mNewPwdEdt.setText("");
              break;
          case R.id.oldpassword_see_iv:
             isLook(mOldPwdEdt,mOldPwdSee,R.drawable.vd_close_eyes_black,R.drawable.vd_open_eyes_black);
              break;
          case R.id.password_see_iv:
              isLook(mNewPwdEdt,mNewPwdSee,R.drawable.vd_close_eyes_black,R.drawable.vd_open_eyes_black);
              break;

          case R.id.password_save_button:  //保存
              mOldPwdEdt.getText().toString().trim();
              mNewPwdEdt.getText().toString().trim();
              break;
      }
    }
}
