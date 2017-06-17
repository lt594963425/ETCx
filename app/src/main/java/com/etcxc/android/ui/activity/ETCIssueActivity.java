package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * etc发行Activity
 * Created by xwpeng on 2017/6/17.
 */

public class ETCIssueActivity extends BaseActivity implements View.OnClickListener{
    private final static String TAG = ETCIssueActivity.class.getSimpleName();
    private RadioGroup mUserTypeGroup;
    private EditText mCarCardEdit;
    private Spinner mCardColorSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etc_issue);
        initView();
    }

    private void initView() {
        mCarCardEdit = find(R.id.car_card_number_edittext);
        mCardColorSpinner = find(R.id.car_card_color_spinner);
        List ls = new ArrayList<String>();
        ls.add("黄底黑字");
        ls.add("蓝底白字");
        ls.add("黑底白字");
        ls.add("白底黑字");
        ls.add("绿底白字");
        ArrayAdapter<String> arr_adapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ls);
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCardColorSpinner.setAdapter(arr_adapter);
        mUserTypeGroup = find(R.id.user_type_radiogroup);

    }

    @Override
    public void onClick(View v) {

    }
}
