package com.etcxc.android.ui.activity;

import android.content.Intent;
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
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

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
                String veriFicode = mVerifiCodeEdit.getText().toString().trim();
                String data = "tel/" + phoneNum + "/inf_modify_sms_code/"+veriFicode+"/pwd/" + passWord+"/sms_id/"+smsID;
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
                try {
                    ResetPwdUrl(resetPwdUrl+data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        Request requst = new Request.Builder()
                .url(url)//http://192.169.6.119/login/login/login/tel/15974255013/pwd/123456/code/wrty
                .build();
        client.newCall(requst).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                ResetPasswordActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("网络不佳，登录失败");
                        return;
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string().trim();
                // ToaltsThreadUIshow(s);// todo 返回数据待改进
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if(jsonObject==null) return;
                    String code = jsonObject.getString("code");
                    if ( code.equals("s_ok")) {
                        //请求成功
                        JSONObject varJson = jsonObject.getJSONObject("var");
                        String tel = varJson.getString("tel");
                        String pwd = varJson.getString("pwd");
                        String lastmodifyTime = varJson.getString("last_modify_time");//标准的时间格式
                        String nickName = varJson.getString("nick_name");
                        //  todo   保存用户信息到本地  通过eventbus 把手机号码传递到Mine界面
                        SharedPreferences.Editor editor = sPUser.edit();
                        editor.putString("telphone", tel);
                        editor.putString("password", Md5Utils.encryptpwd(pwd));
                        editor.putString("lastmodifyTime",lastmodifyTime);
                        editor.putString("nickname",nickName);
                        editor.commit();
                        ToaltsThreadUIshow("找回密码成功");// todo 返回数据待改进
                        Intent intent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    if (code.equals("err")) {
                        String returnMsg = jsonObject.getString("message");//返回的信息
                        if(returnMsg.equals("telphone_unregistered")){
                            ToaltsThreadUIshow("手机尚未注册");
                        } else if (returnMsg.equals("sms_code_error")){
                            ToaltsThreadUIshow("输入验证码错误");
                        }else if (returnMsg.equals("err_password")){
                            ToaltsThreadUIshow("密码错误");//
                        }else {
                            ToaltsThreadUIshow(returnMsg);//
                        }
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToaltsThreadUIshow("登录失败JSONException");// todo 返回数据待改进
                    return;
                }

            }
        });
    }
    public void getSmsCode(String url) {
        Request requst = new Request.Builder()
                .url(url)
                .build();
        client.newCall(requst).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ResetPasswordActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("网络不佳，注册失败");
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string().trim();
                try {
                    JSONObject object = new JSONObject(s);
                    String code = object.getString("code");
                    if(code.equals("s_ok")){//返回tel,sms_id
                        JSONObject  jsonvar =object.getJSONObject("var");
                        String smsID = jsonvar.getString("sms_id");
                        PrefUtils.setString(App.get(),"rp_sms_id",smsID);//rp_sms_id
                        ToaltsThreadUIshow("发送成功");
                    }
                    if(code.equals("err")){//返回失败原因
                        String msg =object.getString("message");
                        if(msg.equals("password_too_easy")){
                            ToaltsThreadUIshow("密码太简单");
                        }else if(msg.equals("telphone_unregistered")){
                            ToaltsThreadUIshow("手机尚未注册");
                        }
                        else {
                            ToaltsThreadUIshow("发送失败");
                        }
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToaltsThreadUIshow("发送失败");
                }
            }
        });
    }
    private void ToaltsThreadUIshow(Object s) {
        ResetPasswordActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {//mLoginVerificodeEdt
                ToastUtils.showToast(s+"");
            }
        });
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

