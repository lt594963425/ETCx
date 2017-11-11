package com.etcxc.android.ui.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.view.View;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.nfc.CmdHandler;
import com.etcxc.android.net.nfc.bean.Card;
import com.etcxc.android.utils.LogUtil;
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
 * start此Activity，如果是本App其他页面跳转的请传fromSelf=true
 * NFC 圈存
 */
public class NFCStoreActivity extends BaseActivity implements View.OnClickListener {
    private boolean mIsFromSelf;//是否是本应用启动的,判断回到更换设备的方式
    private NfcAdapter mNfcAdapter;
    private static String[][] TECHS;
    private static IntentFilter[] TAG_FILTERS;

    static {
        TECHS = new String[][]{{IsoDep.class.getName()}, {NfcF.class.getName()},};
        try {
            TAG_FILTERS = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED, "*/*")};
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreader);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            ToastUtils.showToast(getString(R.string.phone_not_support_nfc));
            finish();
            return;
        }
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        initView();
        onNewIntent(getIntent());
    }

    private void initView() {
        setTitle(R.string.read_card);
        findViewById(btn_replace_device).setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        mIsFromSelf = intent.getBooleanExtra("fromSelf", false);
        showProgressDialog(getString(R.string.storing));
        circleSave(intent);
        closeProgressDialog();
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
     * 圈存
     */
    public void circleSave(Intent intent) {
        final Tag tag = intent.getParcelableExtra(EXTRA_TAG);
        if (tag == null) {
            ToastUtils.showToast(getString(R.string.no_know_card));
            return;
        }
        Observable.create(new ObservableOnSubscribe<Card>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Card> e) throws Exception {
                final IsoDep isodep = IsoDep.get(tag);
                if (isodep == null) e.onError(new Throwable("isoDep null"));
//                Card card = CmdHandler.readCard(isodep);
//                e.onNext(card);
                String cardId = CmdHandler.getCardId(isodep);
                String mac1 = CmdHandler.storeMac1(isodep);
                Card card = new Card();
                card.blance = "100";
                card.owerName = "mac1: " + mac1;
                card.carCardId = "....";
                card.cardId = cardId;
                e.onNext(card);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Card>() {
                    @Override
                    public void accept(@NonNull Card card) throws Exception {
                        if (!card.isAvailable()) {
                            ToastUtils.showToast(R.string.no_know_card);
                            return;
                        }
                        Intent intent = new Intent(NFCStoreActivity.this, StoreSuccessActivity.class);
                        intent.putExtra("card", card);
                        startActivity(intent);
                        openAnimator(NFCStoreActivity.this);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(R.string.no_know_card);
                        LogUtil.e(TAG, "nfc store", throwable);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mNfcAdapter.isEnabled()) ToastUtils.showToast(getString(R.string.please_open_NFC_function));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, TAG_FILTERS, TECHS);
    }
}
