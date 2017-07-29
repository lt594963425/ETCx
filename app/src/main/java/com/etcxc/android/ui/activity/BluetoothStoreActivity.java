package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

/**
 * 蓝牙圈存
 */
public class BluetoothStoreActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_read_card,btn_replace_device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_store);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.read_card));
        btn_read_card = (Button) findViewById(R.id.btn_read_card);
        btn_replace_device = (Button) findViewById(R.id.btn_replace_device);
        btn_read_card.setOnClickListener(this);
        btn_replace_device.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_read_card:
                startActivity(new Intent(this,StoreSuccessActivity.class));
                break;
            case R.id.btn_replace_device:
                break;
        }
    }
}
