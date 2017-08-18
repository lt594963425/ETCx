package com.etcxc.android.ui.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.FUNC;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.TimeCount;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.myTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.net.OkClient.get;
import static com.etcxc.android.utils.UIUtils.LEFT;
import static com.etcxc.android.utils.UIUtils.isMobileNO;
import static com.etcxc.android.utils.UIUtils.saveHistory;

/**
 * 手机短信注册页面
 * Created by 刘涛 on 2017/6/9 0009.
 */

public class PhoneRegistActivity extends BaseActivity implements View.OnClickListener {
    private AutoCompleteTextView mPhoneNumberEdit;
    private EditText mSmsCodeEdit, mPswEdit;
    private Button mRegistBtn, mVerifiCodeBtn;
    private ImageView mPhoneDelIv, mEyeIv, mPwdDelIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_regist);
        initView();
        setListener();
        UIUtils.initAutoTextView("histroy_user", mPhoneNumberEdit);
    }

    private void initView() {
        setTitle(R.string.regist);
        mPhoneNumberEdit = find(R.id.phonenumber_edt);
        mPswEdit = find(R.id.password_edt);
        mSmsCodeEdit = find(R.id.verificode_edt);
        mRegistBtn = find(R.id.regist_button);
        mVerifiCodeBtn = find(R.id.telregist_get_verificode_button);
        mPhoneDelIv = find(R.id.phonenumber_delete);
        mEyeIv = find(R.id.eye);
        mPwdDelIv = find(R.id.iv_regist_password_delete);
        UIUtils.addIcon(mPhoneNumberEdit, R.drawable.vd_my, LEFT);
        UIUtils.addIcon(mPswEdit, R.drawable.vd_regist_password, LEFT);
        UIUtils.addIcon(mSmsCodeEdit, R.drawable.vd_regist_captcha, LEFT);
    }

    private void setListener() {
        mRegistBtn.setOnClickListener(this);
        mVerifiCodeBtn.setOnClickListener(this);
        mPhoneDelIv.setOnClickListener(this);
        mEyeIv.setOnClickListener(this);
        mPwdDelIv.setOnClickListener(this);
        mPhoneNumberEdit.addTextChangedListener(new myTextWatcher(mPhoneNumberEdit, mPhoneDelIv));
        mPswEdit.addTextChangedListener(new myTextWatcher(mPswEdit, mPwdDelIv));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_regist_password_delete:
                mPswEdit.setText("");
                break;
            case R.id.regist_button:
                phoneRegistButton();
                break;
            case R.id.telregist_get_verificode_button:
                getSMSCode();
                break;
            case R.id.phonenumber_delete:
                mPhoneNumberEdit.setText("");
                break;
            case R.id.eye:
                UIUtils.isLook(mPswEdit, mEyeIv, R.drawable.vd_close_eyes, R.drawable.vd_open_eyes);
        }
    }

    private void getSMSCode() {
        String phoneNum = mPhoneNumberEdit.getText().toString();
        phoneNum = SystemUtil.verifyPhoneNumber(phoneNum);
        if (TextUtils.isEmpty(phoneNum)) return;
        getSmsCode(phoneNum);
        UIUtils.saveHistory("history_user", phoneNum);
        TimeCount time = new TimeCount(mVerifiCodeBtn, 60000, 1000);
        time.start();
    }

    private void phoneRegistButton() {
        ArrayList<String> list = new ArrayList<>();
        String smsID = PrefUtils.getString(App.get(), "pr_sms_id", null);
        String phoneNum = mPhoneNumberEdit.getText().toString();
        String passWord = mPswEdit.getText().toString().trim();
        String smsCode = mSmsCodeEdit.getText().toString().trim();
        //tel/'tel'/reg_sms_code/'reg_sms_code'/pwd/'pwd'/sms_id/'sms_id'
        String data = "tel/" + phoneNum +
                "/reg_sms_code/" + smsCode +
                "/pwd/" + passWord +
                "/sms_id/" + smsID;
        if (phoneNum.isEmpty()) {
            ToastUtils.showToast(R.string.phone_isempty);
            return;
        } else if (!isMobileNO(phoneNum)) {
            ToastUtils.showToast(R.string.please_input_correct_phone_number);
            return;
        } else if (passWord.isEmpty()) {
            ToastUtils.showToast(R.string.password_isempty);
            return;
        } else if (passWord.length() < 6) {
            ToastUtils.showToast(R.string.password_isshort);
            return;
        } else if (smsCode.isEmpty()) {
            ToastUtils.showToast(R.string.set_verifycodes);
            return;
        }
        saveHistory("history", phoneNum);
        //todo 接口调整
//        loginUUrl(loginSmsUrl + data);
    }

    public void loginUUrl(String url) {
        showProgressDialog(getString(R.string.registing));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = get(url, new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        parseJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.intenet_err);
                    }
                });

    }

    private void parseJson(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            String code = jsonObject.getString("code");
            requestSuccess(jsonObject, code);
            requestError(jsonObject, code);
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showToast(R.string.para_err);
        }
    }

    private void requestError(JSONObject jsonObject, String code) throws JSONException {
        if (code.equals("err")) {
            String returnMsg = jsonObject.getString("message");//返回的信息
            switch (returnMsg) {
                case "sms_code_error":
                    ToastUtils.showToast(R.string.smscodeerr);
                    break;
                case "telphone_unregistered":
                    ToastUtils.showToast(R.string.telphoneunregistered);
                    break;
                case "err_password":
                    ToastUtils.showToast(R.string.passworderr);
                    break;
                case "telphoner_has_been_registered":
                    ToastUtils.showToast(R.string.isregist);
                    break;
            }
        }
    }

    private void requestSuccess(JSONObject jsonObject, String code) throws JSONException {
        if (code.equals("s_ok")) {
            //请求成功
            JSONObject varJson = jsonObject.getJSONObject("var");
            String tel = varJson.getString("tel");
            String pwd = varJson.getString("pwd");
            String regTime = varJson.getString("reg_time");
            String nickName = varJson.getString("nick_name");
      /*      //  todo   保存用户信息到本地  通过eventbus 把手机号码传递到Mine界面
            SharedPreferences.Editor editor = sPUser.edit();
            editor.putString("telphone", tel);
            editor.putString("password", pwd);
            editor.putString("regtime", regTime);
            editor.putString("nickname", nickName);
            editor.commit();*/
            closeProgressDialog();
            ToastUtils.showToast(R.string.registcomlete);
            finish();
        }
    }

    public void getSmsCode(String tel) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tel", tel);
                e.onNext(get(NetConfig.consistUrl(FUNC.SEND_SMS), jsonObject));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        try {
                            JSONObject object = new JSONObject(s);
                            String code = object.getString("code");
                            if (code.equals("s_ok")) {
                                JSONObject jsonvar = object.getJSONObject("var");
                                String smsID = jsonvar.getString("sms_id");
                                PrefUtils.setString(App.get(), "pr_sms_id", smsID);//rp_sms_id
                                ToastUtils.showToast(R.string.send_success);
                            }
                            if (code.equals("err")) {
                                // String msg = object.getString("");
                                ToastUtils.showToast(R.string.request_failed);
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showToast(R.string.request_failed);
                            return;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(R.string.send_faid);
                        return;
                    }
                });
    }


}
