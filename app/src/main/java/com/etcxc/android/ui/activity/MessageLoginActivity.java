package com.etcxc.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.bean.MessageEvent;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.TimeCount;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.mTextWatcher;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.net.FUNC.LOGIN_SMS;
import static com.etcxc.android.net.FUNC.SMSREPORT;
import static com.etcxc.android.net.NetConfig.JSON;
import static com.etcxc.android.utils.UIUtils.initAutoComplete;
import static com.etcxc.android.utils.UIUtils.isMobileNO;
import static com.etcxc.android.utils.UIUtils.saveHistory;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * 短信登录
 */

public class MessageLoginActivity extends BaseActivity implements View.OnClickListener {
    private AutoCompleteTextView mMPhoneNumberEdt;
    private ImageView mMPhoneNumberDelete, mMPicCodeIV, mMRefrshCodeIv;
    private EditText mMVeriFicodeEdt;
    private Button mGetMsgVeriFicodeButton;
    private Button mMLoginButton;
    private RelativeLayout mMsgLVLayout;
    private EditText mMPicCodeEdt;
    private RelativeLayout mMsgVodeLayout;
    private String mSMSID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_login);
        initView();
    }

    private void initView() {//message_login_toolbar
        setTitle(R.string.messagelogin);
        mMPhoneNumberEdt = find(R.id.message_phonenumber_edt);
        mMPhoneNumberDelete = find(R.id.message_phonenumber_delete);
        mMVeriFicodeEdt = find(R.id.message_verificode_edt);
        mGetMsgVeriFicodeButton = find(R.id.get_msg_sms_code_button);
        mMLoginButton = find(R.id.message_login_button);
        mMsgVodeLayout = find(R.id.message_verificode_layout);
        mMPhoneNumberDelete.setOnClickListener(this);
        mGetMsgVeriFicodeButton.setOnClickListener(this);
        mMLoginButton.setOnClickListener(this);
        mMsgLVLayout = find(R.id.message_login_verificode_layout);
        mMPicCodeEdt = find(R.id.message_login_verificode_edt); //输入图形验证码message_login_image_verificode
        mMPicCodeIV = find(R.id.message_login_image_verificode); //图形验证码 message_login_image_verificode
        mMRefrshCodeIv = find(R.id.message_login_fresh_verification); //刷新图形验证码 message_login_fresh_verificatio
        addIcon(mMPicCodeEdt, R.drawable.vd_regist_captcha);
        addIcon(mMPhoneNumberEdt, R.drawable.vd_my);
        addIcon(mMVeriFicodeEdt, R.drawable.vd_regist_captcha);
        mMPhoneNumberEdt.addTextChangedListener(new mTextWatcher(mMPhoneNumberEdt, mMPhoneNumberDelete));
        initAutoComplete("history", mMPhoneNumberEdt);
        long timeDef = 60000 - (System.currentTimeMillis() - PublicSPUtil.getInstance().getLong("timeMsgLog", 0));
        if (timeDef > 0) new TimeCount(mGetMsgVeriFicodeButton, timeDef, 1000).start();

    }


    public void addIcon(TextView view, int resId) {
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), resId, null);
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        view.setCompoundDrawablePadding(UIUtils.dip2Px(16));
    }

    private ArrayList<String> list;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.message_phonenumber_delete:
                mMPhoneNumberEdt.setText("");
                break;
            case R.id.get_msg_sms_code_button://获取短信验证码 //http://192.169.6.119/login/sms/smsreport/tel/'tel'
                requestSMSCode();
                break;
            case R.id.message_login_button://登录
                String smsid = PublicSPUtil.getInstance().getString("sms_id", null);
                String smsCode = mMVeriFicodeEdt.getText().toString().trim();//短信验证码
                String phoneNum = mMPhoneNumberEdt.getText().toString().trim();//手机号码
                //判断
                if (phoneNum.isEmpty()) {
                    ToastUtils.showToast(R.string.phone_isempty);
                    return;
                } else if (!isMobileNO(phoneNum)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                } else if (smsCode.isEmpty()) {
                    ToastUtils.showToast(R.string.please_input_smscode);
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tel", phoneNum)
                            .put("sms_code", smsCode)
                            .put("sms_id", smsid)
                            .put(Constants.ORIENTION_KEY, Constants.ORIENTION_VALUE);

                    requestSMSLogin(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void requestSMSCode() {
        String phoneNum2 = mMPhoneNumberEdt.getText().toString().trim();
        if (!isMobileNO(phoneNum2)) {
            ToastUtils.showToast(R.string.please_input_correct_phone_number);
            return;
        } else if (TextUtils.isEmpty(phoneNum2)) {
            ToastUtils.showToast(R.string.please_input_phonenumber);
            return;
        }
        saveHistory("history", phoneNum2);
        getSmsCode(phoneNum2);
    }

    public void getSmsCode(String phonenum) {
        JSONObject jsonObject = new JSONObject();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                jsonObject.put("tel", phonenum);

                e.onNext(OkHttpUtils
                        .postString()
                        .url(NetConfig.HOST + SMSREPORT)
                        .content(String.valueOf(jsonObject))
                        .mediaType(JSON)
                        .build()
                        .execute().body().string());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, s);
                        JSONObject object = new JSONObject(s);
                        String code = object.getString("code");
                        if ("s_ok".equals(code)) {
                            mSMSID = object.getString("sms_id");
                            PublicSPUtil.getInstance().putString("sms_id", mSMSID);
                            ToastUtils.showToast(R.string.send_success);
                            PublicSPUtil.getInstance().putLong("timeMsgLog", System.currentTimeMillis());
                            new TimeCount(mGetMsgVeriFicodeButton, 60000, 1000).start();
                        }
                        if ("error".equals(code)) {
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

    public void requestSMSLogin(JSONObject jsonObject) {
        Log.e(TAG, jsonObject.toString());
        showProgressDialog(getString(R.string.logining));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkHttpUtils
                        .postString()
                        .url(NetConfig.HOST + LOGIN_SMS)
                        .content(String.valueOf(jsonObject))
                        .mediaType(JSON)
                        .build()
                        .execute().body().string());
            }
        })
                .compose(RxUtil.io()).compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
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
            Log.e(TAG, s);
            if ("s_ok".equals(code)) {
                Log.e(TAG, "登录成功" + s);
                JSONObject varJson = jsonObject.getJSONObject("var");
                String token = varJson.getString("token");
                JSONObject userJson = varJson.getJSONObject("user");
                String uid = userJson.getString("uid");
                String nickName = userJson.getString("nick_name");
                String tel = userJson.getString("tel");
                String pwd = userJson.getString("pwd");
                //String head_image = varJson.getString("head_image");
                PublicSPUtil.getInstance().putString("tel", tel);
                PublicSPUtil.getInstance().putString("pwd", pwd);
                PublicSPUtil.getInstance().putString("nickname", nickName);
                Log.e(TAG, nickName + ":" + nickName + ":" + token);
                EventBus.getDefault().post(new MessageEvent(nickName));
                MeManager.setUid(uid);   //todo
                MeManager.setPhone(tel);
                MeManager.setName(nickName);
                MeManager.setToken(token);
                MeManager.setIsLgon(true);
                MeManager.setPWD(pwd);
                ToastUtils.showToast(R.string.login_success);
                App.onProfileSignIn("mLoginSMS");//帐号登录统计
                closeProgressDialog();
                openActivity(MainActivity.class);
                finish();
            }
            if ("error".equals(code)) {
                String returnMsg = jsonObject.getString("message");
                if ("telphone_unregistered".equals(returnMsg)) {
                        closeProgressDialog();
                    ToastUtils.showToast(R.string.telphoneunregistered);
                    finish();
                } else {
                    closeProgressDialog();
                    ToastUtils.showToast(R.string.login_failed + returnMsg);
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showToast(R.string.para_err);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
