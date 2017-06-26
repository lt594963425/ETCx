package com.etcxc.android.ui.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.DialogUtils;
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * 找回密码
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private String smsUrl ="http://192.168.6.58/user_information_modify/inf_modify_sms/smsreport/tel/";
    private String resetPwdUrl ="http://192.168.6.58/user_information_modify/user_information_modify/informationmodify/";
    private EditText mPhoneNumberEdit, mResetPwd, mVerifiCodeEdit;
    private Button mRegistButton, mVerificodeButton;
    private ImageView mPhonenumberDelete, mEye,mResetPwdDelete;
    private SharedPreferences sPUser;
    private Dialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        sPUser =getSharedPreferences("user_info",MODE_PRIVATE);
        initView();
    }
    //verificode_edt
    private void initView() {
        Toolbar mToolbar = find(R.id.reset_password_toolbar);
        mToolbar.setTitle(R.string.retrieve_password);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mPhoneNumberEdit =  find(R.id.reset_phonenumber_edt);//手机号码
        mPhonenumberDelete =  find(R.id.reset_phonenumber_delete);//清空
        mResetPwd =  find(R.id.reset_password_edt);
        mResetPwdDelete= find(R.id.reset_password_delete);
        mEye =  find(R.id.reset_eye);
        mVerifiCodeEdit = find(R.id.reset_verificode_edt);
        mVerificodeButton = find(R.id.get_reset_verificode_button);
        mRegistButton =  find(R.id.reset_button);
        addIcon(mPhoneNumberEdit, R.drawable.vd_my);
        addIcon(mResetPwd, R.drawable.vd_regist_password);
        addIcon(mVerifiCodeEdit, R.drawable.vd_regist_captcha);
        mRegistButton.setOnClickListener(this);
        mVerificodeButton.setOnClickListener(this);
        mPhonenumberDelete.setOnClickListener(this);
        mEye.setOnClickListener(this);
        mResetPwdDelete.setOnClickListener(this);
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mPhoneNumberEdit.addTextChangedListener(myTextWatcher);
        mResetPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !mResetPwd.getText().toString().isEmpty()) {
                    mResetPwdDelete.setVisibility(View.VISIBLE);
                } else {
                    mResetPwdDelete.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }
    public void addIcon(TextView view, int resId) {
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), resId, null);
        //drawable.setTint(Color.BLACK);
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        view.setCompoundDrawablePadding(UIUtils.dip2Px(16));
    }
    /**
     * 判断手机号码是否正确
     */
    public boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        String regExp ="((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$" ;//;
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    private Boolean flag = false;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset_password_delete:
                mResetPwd.setText("");
                break;
            case R.id.reset_button://找回密码完成，跳回登录界面
                String smsID = PrefUtils.getString(App.get(),"rp_sms_id",null);
                //tel/15974255013/inf_modify_sms_code/190881/pwd/lt767435/sms_id/90743520170623203308
                //data = /tel/'tel'/inf_modify_sms_code/'inf_modify_sms_code'/pwd/'pwd'/sms_id/'sms_id'
                String phoneNum = mPhoneNumberEdit.getText().toString();
                String passWord = mResetPwd.getText().toString().trim();
                String pwd = Md5Utils.encryptpwd(passWord);
                String veriFicode = mVerifiCodeEdit.getText().toString().trim();
                String data = "tel/" + phoneNum + "/inf_modify_sms_code/"+veriFicode+"/pwd/" + pwd+"/sms_id/"+smsID;
                if(phoneNum.isEmpty()){
                    ToastUtils.showToast(R.string.phone_isempty);
                    return;
                }else if (!isMobileNO(phoneNum)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                }else if(veriFicode.isEmpty()){
                    ToastUtils.showToast(R.string.set_verifycodes);
                    return;
                }else if(passWord.isEmpty()){
                    ToastUtils.showToast(R.string.password_isempty);
                    return;
                }else if(passWord.length() < 6){
                    ToastUtils.showToast(R.string.password_isshort);
                    return;
                }
                   mDialog = DialogUtils.createLoadingDialog(this,getString(R.string.loading));
                    ResetPwdUrl(resetPwdUrl+data);
                break;
            case R.id.get_reset_verificode_button://获取短息验证码
                String phoneNum2 = mPhoneNumberEdit.getText().toString();
                if (!isMobileNO(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_correct_phone_number);
                    return;
                } else if (TextUtils.isEmpty(phoneNum2)) {
                    ToastUtils.showToast(R.string.please_input_phonenumber);
                    return;
                }
                TimeCount time = new TimeCount(60000, 1000);
                time.start();
                //todo：向后端请求获取短信验证码
                try {
                    getSmsCode(smsUrl+phoneNum2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.reset_phonenumber_delete://置空手机号
                mPhoneNumberEdit.setText("");
                break;
            case R.id.reset_eye:
                mResetPwd.setHorizontallyScrolling(true);//不可换行
                if (flag == true) {
                    mResetPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag = false;
                    mEye.setImageResource(R.drawable.vd_close_eyes);
                } else {
                    mResetPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag = true;
                    mEye.setImageResource(R.drawable.vd_open_eyes);
                }
        }
    }
    public void ResetPwdUrl(String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(url,new JSONObject());
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
                        ToastUtils.showToast(R.string.find_faid);
                        return;
                    }
                });
    }

    private void parseJson(@NonNull String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject==null) return;
            String code = jsonObject.getString("code");
            if ( code.equals("s_ok")) {
                JSONObject varJson = jsonObject.getJSONObject("var");
                String tel = varJson.getString("tel");
                String pwd = varJson.getString("pwd");
                String lastmodifyTime = varJson.getString("last_modify_time");//标准的时间格式
                String nickName = varJson.getString("nick_name");
                SharedPreferences.Editor editor = sPUser.edit();
                editor.putString("telphone", tel);
                editor.putString("password", pwd);
                editor.putString("lastmodifyTime",lastmodifyTime);
                editor.putString("nickname",nickName);
                editor.commit();
                DialogUtils.closeDialog(mDialog);
                ToastUtils.showToast(R.string.find_success);
                finish();
            }
            if (code.equals("err")) {
                String returnMsg = jsonObject.getString("message");
                if(returnMsg.equals("telphone_unregistered")){
                    DialogUtils.closeDialog(mDialog);
                    ToastUtils.showToast(R.string.telphoneunregistered);
                } else if (returnMsg.equals("sms_code_error")){
                    DialogUtils.closeDialog(mDialog);
                    ToastUtils.showToast(R.string.smscodeerr);
                }else if (returnMsg.equals("err_password")){
                    DialogUtils.closeDialog(mDialog);
                    ToastUtils.showToast(R.string.passworderr);//
                }else {
                    DialogUtils.closeDialog(mDialog);
                    ToastUtils.showToast(returnMsg);
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DialogUtils.closeDialog(mDialog);
            ToastUtils.showToast(R.string.find_faid);// todo 返回数据待改进
            return;
        }
    }

    public void getSmsCode(String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(url,new JSONObject());
                e.onNext(result);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        parseSmsCodeJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                        ToastUtils.showToast(R.string.send_faid);
                    }
                });
    }

    private void parseSmsCodeJson(@NonNull String s) {
        try {
            JSONObject object = new JSONObject(s);
            String code = object.getString("code");
            if(code.equals("s_ok")){//返回tel,sms_id
                JSONObject  jsonvar =object.getJSONObject("var");
                String smsID = jsonvar.getString("sms_id");
                PrefUtils.setString(App.get(),"rp_sms_id",smsID);//rp_sms_id
                ToastUtils.showToast(R.string.send_success);
            }
            if(code.equals("err")){
                String msg =object.getString("message");
                if(msg.equals("password_too_easy")){
                    ToastUtils.showToast(R.string.pwd_easy);
                }else if(msg.equals("telphone_unregistered")){
                    ToastUtils.showToast(R.string.telphoneunregistered);
                }
                else {
                    ToastUtils.showToast(R.string.send_faid);
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtils.showToast(R.string.send_faid);
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
            if (temp.length() > 0 && !mPhoneNumberEdit.getText().toString().isEmpty()) {
                mPhonenumberDelete.setVisibility(View.VISIBLE);
                temp = "";
            } else {
                mPhonenumberDelete.setVisibility(View.INVISIBLE);
            }
        }
    }
    /**
     * 倒计时
     */
    public class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {//R.color.colorAccent #B6B6D8
            mVerificodeButton.setBackgroundColor(UIUtils.getColor(R.color.colorGray));
            mVerificodeButton.setClickable(false);
            mVerificodeButton.setText("(" + millisUntilFinished / 1000 + ")" + getString(R.string.timeLate));
        }
        @Override
        public void onFinish() {
            mVerificodeButton.setText(getString(R.string.reStartGetCode));
            mVerificodeButton.setClickable(true);
            mVerificodeButton.setBackgroundColor(UIUtils.getColor(R.color.colorGreen));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag =false;
    }
}

