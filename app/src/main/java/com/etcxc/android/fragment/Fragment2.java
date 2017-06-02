package com.etcxc.android.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.android.R;
import com.etcxc.android.utils.UIUtils;

/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class Fragment2 extends Fragment implements View.OnClickListener {
    private EditText editText1;
    private EditText editText2;
    private TextView textView3;
    public String userName;
    public String passWord;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item2, null);
        editText1 = (EditText) view.findViewById(R.id.editText1);//用户名
        editText2 = (EditText) view.findViewById(R.id.editText2);//密码
        textView3 =(TextView) view.findViewById(R.id.textView3);//登录
        initView();
        return view;
    }

    private void initView() {
        textView3.setOnClickListener(this);
        userName = editText1.getText().toString();
        passWord = editText2.getText().toString();
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(UIUtils.getContext(), "登录成功", Toast.LENGTH_SHORT).show();

    }
}
