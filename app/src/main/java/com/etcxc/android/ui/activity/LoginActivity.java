package com.etcxc.android.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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
import com.etcxc.android.utils.DialogUtils;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.etcxc.android.R.id.login_phonenumber_delete;
import static com.etcxc.android.R.string.login;
import static com.etcxc.android.base.App.isLogin;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 * <p>
 * 登录页面
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    protected final String TAG = ((Object) this).getClass().getSimpleName();
    private final OkHttpClient client = new OkHttpClient();
    private EditText mLoginPhonenumberEdt, mLoginPasswordEdt, mLoginVerificodeEdt; // 手机号码,密码 ,输入图形验证码
    private ImageView mLoginPhonenumberDelete, mLoginPasswordDelete;//   删除
    private ImageView mLoginEye; //可见与不可见
    private ImageView mLoginImageVerificode;//图形取验证码
    private ImageView mLoginFreshVerification;//刷新验证码
    private TextView mLoginMessage;//短信验证码登录
    private TextView mLoginFast;//快速注册
    private TextView mForgetPassword;//忘记密码
    private Button mLoginButton;//  登录
    private RelativeLayout mPictureCodeLayout;
    private String timeStr;

    String pictureCodeUrl = "http://192.168.6.58/login/login/captcha/code_key/";  //更换图形验证码url
    String loginServerUrl = "http://192.168.6.58/login/login/login/";//登录的url
    private boolean isShowPictureCode = false;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        setTitle(login);
        mLoginPhonenumberEdt = find(R.id.login_phonenumber_edt);
        mLoginPhonenumberDelete = find(login_phonenumber_delete);
        mLoginPasswordEdt = find(R.id.login_password_edt);
        mLoginPasswordDelete = find(R.id.login_password_delete);
        mLoginEye = find(R.id.login_eye);
        mLoginVerificodeEdt = find(R.id.login_verificode_edt);
        mLoginImageVerificode = find(R.id.login_image_verificode);
        mLoginFreshVerification = find(R.id.login_fresh_verification);
        mLoginMessage = find(R.id.login_message);
        mLoginFast = find(R.id.login_fast);
        mForgetPassword = find(R.id.forget_password);
        mLoginButton = find(R.id.login_button);
        // todo 密码输入超过三次增加图形验证码 校验 mPictureCodeLayout
        mPictureCodeLayout = find(R.id.login_verificode_layout);
        addIcon(mLoginPhonenumberEdt, R.drawable.vd_my);
        addIcon(mLoginPasswordEdt, R.drawable.vd_regist_password);
        addIcon(mLoginVerificodeEdt, R.drawable.vd_regist_captcha);
        init();
    }
    private void init() {
        //ToastUtils.showToast(timeStr);
        mLoginPhonenumberDelete.setOnClickListener(this);
        mLoginPasswordDelete.setOnClickListener(this);
        mLoginFreshVerification.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mLoginEye.setOnClickListener(this);
        mLoginMessage.setOnClickListener(this);
        mLoginFast.setOnClickListener(this);
        mForgetPassword.setOnClickListener(this);
        MyTextWatcher myTextWatcher = new MyTextWatcher();
        mLoginPhonenumberEdt.addTextChangedListener(myTextWatcher);
        mLoginPasswordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !mLoginPasswordEdt.getText().toString().isEmpty()) {
                    mLoginPasswordDelete.setVisibility(View.VISIBLE);
                    //temp = "";
                } else {
                    mLoginPasswordDelete.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    public void addIcon(TextView view, int resId) {
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), resId, null);
        //drawable.setTint(Color.BLACK);
        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        view.setCompoundDrawablePadding(UIUtils.dip2Px(16));
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.login_phonenumber_delete:
                mLoginPhonenumberEdt.setText("");
                break;
            case R.id.login_password_delete:
                mLoginPasswordEdt.setText("");
                break;
            case R.id.login_eye:
                isLook();
                break;
            case R.id.login_fresh_verification://图形验证码
                long longTime = System.currentTimeMillis();
                timeStr = String.valueOf(longTime);
                PrefUtils.setString(App.get(), "code_key", timeStr);
                startRotateAnimation(mLoginFreshVerification, R.anim.login_code_rotate);
                setPicCode(pictureCodeUrl + timeStr);
                break;
            case R.id.login_message:  //短信验证码登录
                intent = new Intent(this, MessageLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.login_fast:
                intent = new Intent(this, PhoneRegistActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.forget_password:
                intent = new Intent(this, ResetPasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.login_button:  // 登录
                String data;
                String key2 = PrefUtils.getString(App.get(), "code_key", null);
                String phoneNum = mLoginPhonenumberEdt.getText().toString().trim();
                String passWord = mLoginPasswordEdt.getText().toString().trim();
                String pwd = Md5Utils.encryptpwd(passWord);
                String veriFicodem = mLoginVerificodeEdt.getText().toString().trim();//验证码
                if (veriFicodem.isEmpty()) {
                    data = "tel/" + phoneNum +
                            "/pwd/" + pwd;
                } else {
                    data = "tel/" + phoneNum +
                            "/pwd/" + pwd +
                            "/code/" + veriFicodem +
                            "/code_key/" + key2;
                }
                //判断tel/'tel'/pwd/'pwd'/code/'code'/code_key/'code_key''
                if (LocalThrough(phoneNum, passWord, veriFicodem)) return;
                mDialog = DialogUtils.createLoadingDialog(LoginActivity.this,  getString(R.string.logining));
                loginRun(loginServerUrl + data);
                break;
        }
        this.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
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
            if (temp.length() > 0 && !mLoginPhonenumberEdt.getText().toString().isEmpty()) {
                mLoginPhonenumberDelete.setVisibility(View.VISIBLE);
                temp = "";
            } else {
                mLoginPhonenumberDelete.setVisibility(View.INVISIBLE);
            }
        }
    }

    private Boolean flag = false;

    private boolean LocalThrough(String phoneNum, String passWord, String veriFicodem) {
        if (phoneNum.isEmpty()) {
            ToastUtils.showToast(R.string.phone_isempty);
            return true;
        } else if (!isMobileNO(phoneNum)) {
            ToastUtils.showToast(R.string.please_input_correct_phone_number);
            return true;
        } else if (passWord.isEmpty()) {
            ToastUtils.showToast(R.string.password_isempty);
            return true;
        } else if (passWord.length() < 6) {
            ToastUtils.showToast(R.string.password_isshort);
            return true;
        } else if (isShowPictureCode) {
            if (veriFicodem.isEmpty()) {
                ToastUtils.showToast(R.string.set_picture_verifycodes);
                return true;
            }
        }
        return false;
    }

    private void loginRun(String url) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(url, new JSONObject());
                e.onNext(result);

            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        parseResultJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        DialogUtils.closeDialog(mDialog);
                        ToastUtils.showToast(R.string.login_failed);
                        LogUtil.e(TAG, "loginRun", throwable);
                    }
                });
    }

    private void parseResultJson(@NonNull String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        if (jsonObject == null) return;
        String code = jsonObject.getString("code");
        if (code.equals("s_ok")) {
            //请求成功
            JSONObject varJson = jsonObject.getJSONObject("var");
            String tel = varJson.getString("tel");
            String pwd = varJson.getString("pwd");
            String loginTime = varJson.getString("login_time");
            String nickName = varJson.getString("nick_name");
            EventBus.getDefault().post(new MessageEvent(tel));
            isLogin = true;
            //  todo   保存用户信息到本地  通过eventbus 把手机号码传递到Mine界面
            MeManager.setSid(tel);
            MeManager.setName(nickName);
            MeManager.setIsLgon(isLogin);
            DialogUtils.closeDialog(mDialog);
            ToastUtils.showToast("登录成功");
            finish();
        }
        if (code.equals("err")) {
            String returnMsg = jsonObject.getString("message");//返回的信息
            if (returnMsg.equals("telphone_unregistered")) {
                DialogUtils.closeDialog(mDialog);
                ToastUtils.showToast(R.string.telphoneunregistered);
            } else if (returnMsg.equals("need_captcha")) {
                long longTime = System.currentTimeMillis();
                timeStr = String.valueOf(longTime);
                PrefUtils.setString(App.get(), "code_key", timeStr);
                setPicCode(pictureCodeUrl + timeStr);
                mPictureCodeLayout.setVisibility(View.VISIBLE);
                DialogUtils.closeDialog(mDialog);
                ToastUtils.showToast(R.string.input_pwd_ismore);
                isShowPictureCode = true;
            } else if (returnMsg.equals("err_password")) {
                DialogUtils.closeDialog(mDialog);
                ToastUtils.showToast(R.string.passworderr);//
            } else if (returnMsg.equals("err_captcha")) {
                DialogUtils.closeDialog(mDialog);
                ToastUtils.showToast(R.string.err_captcha);
            }else {
                DialogUtils.closeDialog(mDialog);
                ToastUtils.showToast(returnMsg);
            }
            return;
        }
    }

    Bitmap bitmap;
    private Bitmap setPicCode(final String url) {
        Request requst = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(requst).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = response.body().byteStream();//字节流
                bitmap = BitmapFactory.decodeStream(is);
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//mLoginVerificodeEdt
                        mLoginImageVerificode.setImageBitmap(bitmap);
                        mLoginFreshVerification.clearAnimation();
                    }
                });
            }
        });
        return bitmap;
    }

    private void isLook() {
        mLoginPasswordEdt.setHorizontallyScrolling(true);//不可换行
        if (flag == true) {
            mLoginPasswordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            flag = false;
            mLoginEye.setImageResource(R.drawable.vd_close_eyes);
        } else {
            mLoginPasswordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

            flag = true;
            mLoginEye.setImageResource(R.drawable.vd_open_eyes);
        }
    }


    /**
     * 旋转动画
     *
     * @param view
     * @param setid
     */
    public void startRotateAnimation(View view, int setid) {
        Animation rotateAnim = AnimationUtils.loadAnimation(this, setid);
        LinearInterpolator lin = new LinearInterpolator();
        rotateAnim.setInterpolator(lin);
        if (rotateAnim != null) {
            view.startAnimation(rotateAnim);
        }
    }

    /*
      停止旋转
     */
    public void stopRotateAnimation(View v) {
        v.clearAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShowPictureCode = false;
        stopRotateAnimation(mLoginFreshVerification);
        finish();
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    /**
     * 判断手机号码是否正确
     */
    public boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        String regExp = "((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[3,7])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
