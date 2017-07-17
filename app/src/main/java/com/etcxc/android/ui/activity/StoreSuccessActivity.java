package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.nfc.bean.Card;

/**
 * 卡信息
 */
public class StoreSuccessActivity extends BaseActivity implements View.OnClickListener{
    private TextView mOwnerNameTv, mCardIdTv,mCarIdTv,mStoreMoneyTv,mBalanceTv;
    private Card mCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_success);
        mCard = getIntent().getParcelableExtra("card");
        if (mCard == null) {
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.set_accomplish));
        find(R.id.return_index_btn).setOnClickListener(this);
        mOwnerNameTv = find(R.id.owner_name_tv);
        mCardIdTv = find(R.id.card_id_tv);
        mCarIdTv = find(R.id.car_id_tv);
        mStoreMoneyTv = find(R.id.store_money_tv);
        mBalanceTv = find(R.id.balance_tv);
        mOwnerNameTv.setText(getString(R.string.owner_name, mCard.owerName));
        mCardIdTv.setText(getString(R.string.card_id, mCard.cardId));
        mCarIdTv.setText(getString(R.string.car_id, mCard.carCardId));
        mStoreMoneyTv.setText(getString(R.string.store_money, "0"));
        mBalanceTv.setText(getString(R.string.balance, mCard.blance));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_index_btn:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
    }
}
