package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.nfc.NfcManager;
import com.etcxc.android.net.nfc.ReaderListener;
import com.etcxc.android.net.nfc.SPEC;
import com.etcxc.android.net.nfc.bean.Card;
import com.etcxc.android.utils.ToastUtils;

import static com.etcxc.android.R.id.btn_replace_device;

/**
 * NFC 圈存
 */
public class NFCStoreActivity extends BaseActivity implements View.OnClickListener, ReaderListener {
    private NfcManager mNFCManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreader);
        if (!getPackageManager().hasSystemFeature(PackageManager. FEATURE_NFC)) {
            ToastUtils.showToast(getString(R.string.phone_not_support_nfc));
            finish();
            return;
        }
        initView();
        mNFCManger = new NfcManager(this);
        onNewIntent(getIntent());
    }

    private void initView() {
        setTitle(R.string.read_card);
        findViewById(btn_replace_device).setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
       mNFCManger.readCard(intent, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case btn_replace_device:
                finish();
                break;
        }
    }

    @Override
    public void onReadEvent(int status, Object... obj) {
        switch (status) {
            case SPEC.START:
                showProgressDialog(getString(R.string.storing));
                break;
            case SPEC.READING:
                break;
            case SPEC.FINISHED:
                Card card = (Card) obj[0];
                closeProgressDialog();
                if (card.isAvailable()) {
                    Intent intent = new Intent(this, StoreSuccessActivity.class);
                    intent.putExtra("card", card);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(getString(R.string.no_know_card));
                }
                break;
           default:
                ToastUtils.showToast(getString(R.string.read_card_info_fault));
                closeProgressDialog();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNFCManger.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNFCManger.onResume();
    }
}
