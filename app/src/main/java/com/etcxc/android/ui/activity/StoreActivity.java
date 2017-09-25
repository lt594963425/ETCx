package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;

import static com.etcxc.android.utils.UIUtils.openAnimator;

/**
 * ETC圈存
 */
public class StoreActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout load_nfc,load_bluetooth_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_for_load);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.home_etccircle));
        load_nfc = (RelativeLayout) findViewById(R.id.load_nfc);
        load_bluetooth_box = (RelativeLayout) findViewById(R.id.load_bluetooth_box);
        load_nfc.setOnClickListener(this);
        load_bluetooth_box.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.load_nfc://nfc圈存
                Intent i = new Intent(this,NFCStoreActivity.class);
                i.putExtra("fromSelf", true);
                startActivity(i);
                openAnimator(this);
            break;
            case R.id.load_bluetooth_box://蓝牙盒子圈存
                openActivity(BleStoreActivity.class);
                break;
        }
    }


}
