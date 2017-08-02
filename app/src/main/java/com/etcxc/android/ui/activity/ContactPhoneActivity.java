package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.etcxc.android.utils.ToastUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.regex.Matcher;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.FUNC.FUNC_COMMIT_CONTACT_PHONE;
import static com.etcxc.android.net.FUNC.SMSREPORT;

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
        String uid = MeManager.getUid();
        if (!TextUtils.isEmpty(uid)) mPhoneEditText.setText(uid);
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
    }


    private String verifyPhoneEdit() {
        String tel1 = mPhoneEditText.getText().toString();
        if (TextUtils.isEmpty(tel1)) {
            ToastUtils.showToast(getString(R.string.phone_number_notallow_empty));
            return "";
        }
        Matcher m = SystemUtil.phonePattern.matcher(tel1);
        if (m.matches()) {
            return tel1;
        } else ToastUtils.showToast(R.string.please_input_correct_phone_number);
        return "";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                String tel1 = verifyPhoneEdit();
                if (!TextUtils.isEmpty(tel1)) {
                    EditText verifyEdit = find(R.id.verificode_edittext);
                    String verifyCode = verifyEdit.getText().toString();
                    if (TextUtils.isEmpty(verifyCode))
                        ToastUtils.showToast(R.string.set_verifycodes);
                    else commitNet(tel1, verifyCode);
                }
                break;
            case R.id.get_verificode_button:
                String tel = verifyPhoneEdit();
                if (!TextUtils.isEmpty(tel)) sendCode(tel);
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
                StringBuilder urlBUilder = new StringBuilder(NetConfig.HOST).append(SMSREPORT)
                        .append(File.separator).append("tel").append(File.separator).append(tel);
                e.onNext(OkClient.get(urlBUilder.toString(), new JSONObject()));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                JSONObject jsonObject = new JSONObject(s);
                String code = jsonObject.getString("code");
                if ("s_ok".equals(code)) {
                    jsonObject = jsonObject.getJSONObject("var");
                    mSmsId = jsonObject.getString("tran_sms_id");
                    mGetVerifyCodeButton.setEnabled(false);
                    CountDownTimer ct = new CountDownTimer(60000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            mGetVerifyCodeButton.setText(millisUntilFinished / 1000 + "S后再试");
                        }

                        @Override
                        public void onFinish() {
                            mGetVerifyCodeButton.setText(R.string.reget_verify_code);
                            mGetVerifyCodeButton.setEnabled(true);
                        }
                    };
                    ct.start();
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
                StringBuilder urlBUilder = new StringBuilder(NetConfig.HOST).append(FUNC_COMMIT_CONTACT_PHONE)
                        .append(File.separator).append("tran_sms_code").append(File.separator).append(verifyCode)
                        .append(File.separator).append("tran_sms_id").append(File.separator).append(mSmsId)
                        .append(File.separator).append("veh_plate_code").append(File.separator).append(PublicSPUtil.getInstance().getString("carCard", ""))
                        .append(File.separator).append("veh_plate_colour").append(File.separator).append(PublicSPUtil.getInstance().getString("carCardColor", ""))
                        .append(File.separator).append("tran_tel").append(File.separator).append(tel);
                e.onNext(OkClient.get(urlBUilder.toString(), new JSONObject()));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                JSONObject jsonObject = new JSONObject(s);
                String code = jsonObject.getString("code");
                if ("s_ok".equals(code))
                    startActivity(new Intent(ContactPhoneActivity.this, PostAddressActivity.class));
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
