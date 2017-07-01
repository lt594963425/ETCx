package com.etcxc.android.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.MessageEvent;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.base.App.isLogin;
import static com.etcxc.android.net.OkClient.get;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * 短信登录
 */

public class MessageLoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText mMPhoneNumberEdt;
    private ImageView mMPhoneNumberDelete, mMPicCodeIV, mMRefrshCodeIv;
    private EditText mMVeriFicodeEdt;
    private Button mGetMsgVeriFicodeButton;
    private Button mMLoginButton;
    private RelativeLayout mMsgLVLayout;
    private EditText mMPicCodeEdt;
    private RelativeLayout mMsgVodeLayout;//图形验证码http://192.168.6.58/login/login/login/
    String loginServerUrl = "http://192.168.6.58/login/login/login/";//登录的url
    private String smsUrl = "http://192.168.6.58/login/sms/smsreport/tel/";//短信url

    private Dialog mDialog;

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
        mGetMsgVeriFicodeButton = find(R.id.get_msg_verificode_button);
        mMLoginButton = find(R.id.message_login_button);
        mMsgVodeLayout = find(R.id.message_verificode_layout);
        mMPhoneNumberDelete.setOnClickListener(this);
        mGetMsgVeriFicodeButton.setOnClickListener(this);
        mMLoginButton.setOnClickListener(this);
        //todo 输入的次数超过3次要求输入图形验证码 显示mMsgLVLayout 控件
        mMsgLVLayout = find(R.id.message_login_verificode_layout);
        mMPicCodeEdt = find(R.id.message_login_verificode_edt); //输入图形验证码message_login_image_verificode
        mMPicCodeIV = find(R.id.message_login_image_verificode); //图形验证码 message_login_image_verificode
        mMRefrshCodeIv = find(R.id.message_login_fresh_verification); //刷新图形验证码 message_login_fresh_verificatio
        addIcon(mMPicCodeEdt, R.drawable.vd_regist_captcha);
        addIcon(mMPhoneNumberEdt, R.drawable.vd_my);
        addIcon(mMVeriFicodeEdt, R.drawable.vd_regist_captcha);
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mMPhoneNumberEdt.addTextChangedListener(myTextWatcher);

    }

    public void addIcon(TextView view, int resId) {
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), resId, null);
        //drawable.setTint(Color.BLACK);
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        view.setCompoundDrawablePadding(UIUtils.dip2Px(16));
    }

    //({R.id.message_phonenumber_delete, R.id.get_msg_verificode_button, R.id.message_login_button})
    private boolean isUser = false;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.message_phonenumber_delete:
                mMPhoneNumberEdt.setText("");
                break;
            case R.id.get_msg_verificode_button://获取短信验证码 //http://192.169.6.119/login/sms/smsreport/tel/'tel'
                String phoneNum2 = mMPhoneNumberEdt.getText().toString().trim();
                if (!isMobileNO(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                } else if (TextUtils.isEmpty(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                    return;
                }
                getSmsCodeBtn(smsUrl + phoneNum2);
                TimeCount time = new TimeCount(60000, 1000);
                time.start();
                break;
            case R.id.message_login_button://登录
                String smsid = PrefUtils.getString(App.get(), "ml_sms_id", null);
                //loginRun("http://wthrcdn.etouch.cn/weather_mini?city=%E6%B7%B1%E5%9C%B3");
                String smsCode = mMVeriFicodeEdt.getText().toString().trim();//短信验证码
                String phoneNum = mMPhoneNumberEdt.getText().toString().trim();//手机号码
                String data = "tel/" + phoneNum + "/sms_code/" + smsCode + "/sms_id/" + smsid;
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
                loginUUrl(loginServerUrl + data);
                break;
        }
    }

    public void getSmsCodeBtn(String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = get(url, new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        JSONObject object = new JSONObject(s);
                        String code = object.getString("code");
                        if (code.equals("s_ok")) {//返回tel,sms_id
                            JSONObject jsonVar = object.getJSONObject("var");
                            String smstel = jsonVar.getString("tel");
                            String smsID = jsonVar.getString("sms_id");
                            PrefUtils.setString(App.get(), "ml_tel", smstel);
                            PrefUtils.setString(App.get(), "ml_sms_id", smsID);
                            ToastUtils.showToast(R.string.send_success);
                        }
                        if (code.equals("err")) {//返回失败原因
                            String msg = object.getString("messsage");
                            ToastUtils.showToast(R.string.send_faid + ":" + msg);
                            return;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(R.string.send_faid);
                    }
                });
    }

    public void loginUUrl(String url) {
        showProgressDialog(getString(R.string.logining));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(url, new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
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
            if (code.equals("s_ok")) {
                //请求成功
                JSONObject varJson = jsonObject.getJSONObject("var");
                String tel = varJson.getString("tel");
                String pwd = varJson.getString("pwd");
                String loginTime = varJson.getString("login_time");
                String nickName = varJson.getString("nick_name");
                EventBus.getDefault().post(new MessageEvent(tel));
                isLogin = true;//登录成功
                MeManager.setSid(tel);
                MeManager.setName(nickName);
                MeManager.setIsLgon(isLogin);
                closeProgressDialog();
                ToastUtils.showToast(R.string.login_success);
                finish();
            }
            if (code.equals("err")) {
                String returnMsg = jsonObject.getString("message");
                if (returnMsg.equals("telphone_unregistered")) {
                    closeProgressDialog();
                    ToastUtils.showToast(R.string.telphoneunregistered);
                    finish();
                } else {
                    closeProgressDialog();
                    ToastUtils.showToast(R.string.login_failed);
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showToast(R.string.para_err);
        }
    }

    /**
     * 监听手机号码的长度
     */
    CharSequence temp;

    public class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() > 0 && !mMPhoneNumberEdt.getText().toString().isEmpty()) {
                mMPhoneNumberDelete.setVisibility(View.VISIBLE);
                temp = "";
            } else {
                mMPhoneNumberDelete.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 倒计时获取验证码
     */
    public class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {//R.color.colorAccent #B6B6D8
            mGetMsgVeriFicodeButton.setBackgroundColor(UIUtils.getColor(R.color.colorGray));
            mGetMsgVeriFicodeButton.setClickable(false);
            mGetMsgVeriFicodeButton.setText("(" + millisUntilFinished / 1000 + ")" + getString(R.string.timeLate));
        }

        @Override
        public void onFinish() {
            mGetMsgVeriFicodeButton.setText(getString(R.string.reStartGetCode));
            mGetMsgVeriFicodeButton.setClickable(true);
            mGetMsgVeriFicodeButton.setBackgroundColor(UIUtils.getColor(R.color.colorGreen));
        }
    }

    /**
     * 判断手机号码是否正确
     */
    public boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        String regExp = "((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
