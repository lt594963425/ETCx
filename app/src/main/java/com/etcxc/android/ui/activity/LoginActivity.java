package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.MessageEvent;
import com.etcxc.android.net.FUNC;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.myTextWatcher;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static com.etcxc.MeManager.setIsLgon;
import static com.etcxc.android.R.id.login_phonenumber_delete;
import static com.etcxc.android.base.App.isLogin;
import static com.etcxc.android.base.App.onProfileSignIn;
import static com.etcxc.android.utils.UIUtils.LEFT;
import static com.etcxc.android.utils.UIUtils.addIcon;
import static com.etcxc.android.utils.UIUtils.initAutoComplete;
import static com.etcxc.android.utils.UIUtils.isMobileNO;
import static com.etcxc.android.utils.UIUtils.saveHistory;

/**
 * 用户信息页面
 * Created by 刘涛 on 2017/6/17 0017.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    //登录信息操作界面
    protected final String TAG = ((Object) this).getClass().getSimpleName();
    private AutoCompleteTextView mLoginPhonenumberEdt;
    private EditText mLoginVerificodeEdt, mLoginPasswordEdt; // 手机号码,密码 ,输入图形验证码
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

    private boolean isShowPictureCode = false;

    private Toolbar mToolbar1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initLoginView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initLoginView() {
        mToolbar1 = find(R.id.login_toolbar);
        setTitle(R.string.login);
        setBarBack(mToolbar1);
        mLoginPhonenumberEdt = find(R.id.login_phonenumber_edt);//
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
        addIcon(mLoginPhonenumberEdt, R.drawable.vd_my, LEFT);
        addIcon(mLoginPasswordEdt, R.drawable.vd_regist_password, LEFT);
        addIcon(mLoginVerificodeEdt, R.drawable.vd_regist_captcha, LEFT);
        initAutoComplete(this, "history", mLoginPhonenumberEdt);
        init();
    }

    private void init() {
        mLoginPhonenumberDelete.setOnClickListener(this);
        mLoginPasswordDelete.setOnClickListener(this);
        mLoginFreshVerification.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mLoginEye.setOnClickListener(this);
        mLoginMessage.setOnClickListener(this);
        mLoginFast.setOnClickListener(this);
        mForgetPassword.setOnClickListener(this);
        mLoginPhonenumberEdt.addTextChangedListener(new myTextWatcher(mLoginPhonenumberEdt, mLoginPhonenumberDelete));
        mLoginPasswordEdt.addTextChangedListener(new myTextWatcher(mLoginPasswordEdt, mLoginPasswordDelete));

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.login_phonenumber_delete:
                mLoginPhonenumberEdt.setText("");
                break;
            case R.id.login_password_delete:
                mLoginPasswordEdt.setText("");
                break;
            case R.id.login_eye:
                UIUtils.isLook(mLoginPasswordEdt, mLoginEye, R.drawable.vd_close_eyes, R.drawable.vd_open_eyes);
                break;
            case R.id.login_fresh_verification://图形验证码
                long longTime = System.currentTimeMillis();
                timeStr = String.valueOf(longTime);
                PrefUtils.setString(App.get(), "code_key", timeStr);
                startRotateAnimation(mLoginFreshVerification, R.anim.login_code_rotate);
                setPicCode(NetConfig.consistUrl(FUNC.VIRIFY_CODE));
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
                MobclickAgent.onEvent(this, "LoginClick");
                if (startUserLoging()) return;
                break;
        }
        this.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    private boolean startUserLoging() {
        String key2 = PrefUtils.getString(App.get(), "code_key", null);
        String phoneNum = mLoginPhonenumberEdt.getText().toString().trim();
        String passWord = mLoginPasswordEdt.getText().toString().trim();
        String pwd = Md5Utils.encryptpwd(passWord);
        String veriFicodem = mLoginVerificodeEdt.getText().toString().trim();//验证码
        //定义一个JSON，用于向服务器提交数据
        JSONObject data = new JSONObject();
        try {
            if (veriFicodem.isEmpty()) {
                data.put("tel", phoneNum).put("pwd", pwd);
            } else {
                data.put("tel", phoneNum)
                        .put("pwd", pwd)
                        .put("code", veriFicodem)
                        .put("code_key", key2);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "startUserLoging", e);
        }
        if (LocalThrough(phoneNum, passWord, veriFicodem)) return true;
        saveHistory(this, "history", phoneNum);
        loginRun(data);
        return false;
    }

    private void setBarBack(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private boolean LocalThrough(String phoneNum, String passWord, String veriFicodem) {
        if (phoneNum.isEmpty()) {
            ToastUtils.showToast(R.string.phone_isempty);
            return true;
        } else if (!isMobileNO(phoneNum)) {
            ToastUtils.showToast(R.string.please_input_correct_phone_number);
            return true;
        } else if (TextUtils.isEmpty(passWord)) {
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

    private void loginRun(JSONObject jsonObject) {
        showProgressDialog(getString(R.string.logining));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                e.onNext(OkClient.get(NetConfig.consistUrl(FUNC.LOGIN_PWD), jsonObject));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        parseResultJson(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.login_failed);
                        LogUtil.e(TAG, "loginRun", throwable);
                    }
                });
    }

    private void parseResultJson(@NonNull String s) throws JSONException {
        JSONObject jsonObject = new JSONObject(s);
        String code = jsonObject.getString("code");
        if ("s_ok".equals(code)) {
            JSONObject varJson = jsonObject.getJSONObject("var");
            String tel = varJson.getString("tel");
            String nickName = varJson.getString("nick_name");
            //  todo   保存用户信息到本地  通过eventbus 把手机号码传递到Mine界面
            EventBus.getDefault().post(new MessageEvent(tel));
            isLogin = true;
            MeManager.setUid(tel);
            MeManager.setName(nickName);
            MeManager.setIsLgon(isLogin);
            ToastUtils.showToast(R.string.login_success);
            onProfileSignIn("mLoginPhone");//帐号登录统计
            finish();
        }
        if (code.equals("err")) {
            String returnMsg = jsonObject.getString("message");//返回的信息
            if (returnMsg.equals("telphone_unregistered")) {
                closeProgressDialog();
                ToastUtils.showToast(R.string.telphoneunregistered);
            } else if (returnMsg.equals("need_captcha")) {
                long longTime = System.currentTimeMillis();
                timeStr = String.valueOf(longTime);
                PrefUtils.setString(App.get(), "code_key", timeStr);
                setPicCode(NetConfig.consistUrl(FUNC.VIRIFY_CODE));
                mPictureCodeLayout.setVisibility(View.VISIBLE);
                closeProgressDialog();
                ToastUtils.showToast(R.string.input_pwd_ismore);
                isShowPictureCode = true;
            } else if (returnMsg.equals("err_password")) {
                closeProgressDialog();
                ToastUtils.showToast(R.string.passworderr);//
            } else if (returnMsg.equals("err_captcha")) {
                closeProgressDialog();
                ToastUtils.showToast(R.string.err_captcha);
            } else {
                closeProgressDialog();
                ToastUtils.showToast(returnMsg);
            }
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
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(R.string.request_failed);
                        mLoginFreshVerification.clearAnimation();
                    }
                });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        isShowPictureCode = false;
        stopRotateAnimation(mLoginFreshVerification);
        finish();
    }
}
