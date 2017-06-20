package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.UIUtils;

/**
 * 联系手机验证录入
 * Created by xwpeng on 2017/6/20.
 */

public class ContactPhoneActivity extends BaseActivity implements View.OnClickListener{
    private final static String TAG = ContactPhoneActivity.class.getSimpleName();
    private EditText mEditText;
    private Button mCommitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_phone);
        mEditText = find(R.id.phone_number_edittext);
        UIUtils.addIcon(mEditText, R.drawable.vd_contact_phone_delete, UIUtils.RIGHT);
        find(R.id.commit_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                startActivity(new Intent(this, PostAddressActivity.class));
                break;
        }
    }
}
