package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.TimeCount;
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.FUNC.OWNERPHONE_VERIFY;
import static com.etcxc.android.net.FUNC.SMSREPORT;
import static com.etcxc.android.utils.UIUtils.saveHistory;

/**
 * 联系手机验证录入
 * Created by xwpeng on 2017/6/20.
 */

public class ContactPhoneActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = ContactPhoneActivity.class.getSimpleName();
    private EditText mPhoneEditText;

    private String mSmsId;
    private Button mGetVerifyCodeButton;
    private ImageView mDeleteImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_phone);
        setTitle(R.string.post_address_contact_phone);
        mPhoneEditText = find(R.id.phone_number_edittext);
        String tel = MeManager.getPhone();
        if (!TextUtils.isEmpty(tel)) mPhoneEditText.setText(tel);
        mGetVerifyCodeButton = find(R.id.get_verificode_button);
        mDeleteImageView = find(R.id.phone_number_delete);
        setListener();
    }

    private void setListener() {
        find(R.id.commit_button).setOnClickListener(this);
        mGetVerifyCodeButton.setOnClickListener(this);
        mDeleteImageView.setOnClickListener(this);
        mPhoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mDeleteImageView.setVisibility(TextUtils.isEmpty(s.toString()) ? View.GONE : View.VISIBLE);
            }
        });
        long timeDef =60000-(System.currentTimeMillis()-PublicSPUtil.getInstance().getLong("timeContact",0));
        if (timeDef>0) new TimeCount(mGetVerifyCodeButton,timeDef , 1000).start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button: {
                String tel = mPhoneEditText.getText().toString();
                tel = SystemUtil.verifyPhoneNumber(tel);
                if (!TextUtils.isEmpty(tel)) {
                    EditText verifyEdit = find(R.id.verificode_edittext);
                    String verifyCode = verifyEdit.getText().toString();
                    if (TextUtils.isEmpty(verifyCode))
                        ToastUtils.showToast(R.string.set_verifycodes);
                    else commitNet(tel, verifyCode);
                }
            }
            break;
            case R.id.get_verificode_button:
                String tel = mPhoneEditText.getText().toString();
                tel = SystemUtil.verifyPhoneNumber(tel);
                if (!TextUtils.isEmpty(tel)) sendCode(tel);
                saveHistory("history", tel);
                break;
            case R.id.phone_number_delete:
                mPhoneEditText.setText("");
                break;
        }
    }

    private void sendCode(String tel) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tel", tel);
                e.onNext(OkClient.get(NetConfig.consistUrl(SMSREPORT), jsonObject));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                JSONObject jsonObject = new JSONObject(s);
                String code = jsonObject.getString("code");
                if ("s_ok".equals(code)) {
                    mSmsId = jsonObject.getString("sms_id");
                    saveHistory("history",tel);
                    PublicSPUtil.getInstance().putLong("timeContact",System.currentTimeMillis());
                    new TimeCount(mGetVerifyCodeButton, 60000, 1000).start();
                } else ToastUtils.showToast(R.string.request_failed);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                LogUtil.e(TAG, "net", throwable);
                ToastUtils.showToast(R.string.request_failed);
            }
        });
    }

    private void commitNet(String tel, String verifyCode) {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("licensePlate", PublicSPUtil.getInstance().getString("carCard", ""))
                        .put("plateColor", PublicSPUtil.getInstance().getString("carCardColor", ""))
                        .put("tel", tel)
                        .put("sms_code", verifyCode)
                        .put("sms_id", mSmsId);
                Log.e(TAG, jsonObject.toString());

                e.onNext(OkClient.get(NetConfig.consistUrl(OWNERPHONE_VERIFY), jsonObject));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.e(TAG, s);
                JSONObject jsonObject = new JSONObject(s);
                String code = jsonObject.getString("code");
                if ("s_ok".equals(code)) {
                    PublicSPUtil.getInstance().putString("issueContactTel",tel);
                    openActivity(PostAddressActivity.class);
                }
                else ToastUtils.showToast(R.string.request_failed);
                closeProgressDialog();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                LogUtil.e(TAG, "net", throwable);
                ToastUtils.showToast(R.string.request_failed);
                closeProgressDialog();
            }
        });
    }
}
