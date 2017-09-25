package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.view.View;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.nfc.NfcManager;
import com.etcxc.android.net.nfc.ReaderListener;
import com.etcxc.android.net.nfc.SPEC;
import com.etcxc.android.net.nfc.StandardPboc;
import com.etcxc.android.net.nfc.bean.Card;
import com.etcxc.android.utils.ToastUtils;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.nfc.NfcAdapter.EXTRA_TAG;
import static com.etcxc.android.R.id.btn_replace_device;
import static com.etcxc.android.utils.UIUtils.openAnimator;

/**
 * start此Activity，如果是本应用请传fromSelf=true
 * NFC 圈存
 */
public class NFCStoreActivity extends BaseActivity implements View.OnClickListener, ReaderListener {
    private NfcManager mNFCManger ;
    private boolean mIsFromSelf;//是否是本应用启动的
    private boolean readFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreader);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            ToastUtils.showToast(getString(R.string.phone_not_support_nfc));
            finish();
            return;
        }
        initView();

        mNFCManger = new NfcManager(this);
        if (mNFCManger != null)
            onNewIntent(getIntent());
    }

    private void initView() {
        setTitle(R.string.read_card);
        findViewById(btn_replace_device).setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mIsFromSelf = intent.getBooleanExtra("fromSelf", false);
        //读卡
        if (mIsFromSelf)
            mNFCManger.readCard(intent, this);
           //circleSave(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case btn_replace_device:
                if (!mIsFromSelf) startActivity(new Intent(this, StoreActivity.class));
                finish();
                break;
        }
    }
    /**
     *    圈存
     */

    public boolean circleSave(Intent intent) {
        showProgressDialog(getString(R.string.storing));
        final Tag tag = intent.getParcelableExtra(EXTRA_TAG);
        if (tag != null) {
            Observable.create(new ObservableOnSubscribe<Card>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Card> e) throws Exception {
                    Card card = new Card();
                    final IsoDep isodep = IsoDep.get(tag);
                    if (isodep != null) {
                        card = StandardPboc.readCard(isodep);
                    }
                    Thread.sleep(2000);
                    e.onNext(card);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Card>() {
                        @Override
                        public void accept(@NonNull Card card) throws Exception {
                            readFlag = true;
                            closeProgressDialog();
                            if (card.isAvailable()) {
                                Intent intent = new Intent(NFCStoreActivity.this, StoreSuccessActivity.class);
                                intent.putExtra("card", card);
                                startActivity(intent);
                                openAnimator(NFCStoreActivity.this);
                            } else {
                                ToastUtils.showToast(getString(R.string.no_know_card));
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(@NonNull Throwable throwable) throws Exception {
                            readFlag = false;
                            ToastUtils.showToast(getString(R.string.read_card_info_fault));
                            closeProgressDialog();

                        }
                    });
            return readFlag;
        }
        return readFlag;
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
                    overridePendingTransition(R.anim.zoom_enter, R.anim.no_anim);
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
