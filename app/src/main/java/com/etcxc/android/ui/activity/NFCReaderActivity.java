package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

/**
 * NFC 读卡
 */
public class NFCReaderActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_replace_device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreader);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.read_card));
        btn_replace_device = (Button) findViewById(R.id.btn_replace_device);
        btn_replace_device.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
