package com.etcxc.android.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.bean.MessageEvent;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.FUNC;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.etcxc.android.utils.mTextWatcher;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.etcxc.android.R.id.login_phonenumber_delete;
import static com.etcxc.android.base.App.onProfileSignIn;
import static com.etcxc.android.net.FUNC.VIRIFY_CODE;
import static com.etcxc.android.net.NetConfig.consistUrl;
import static com.etcxc.android.utils.UIUtils.LEFT;
import static com.etcxc.android.utils.UIUtils.addIcon;
import static com.etcxc.android.utils.UIUtils.closeAnimator;
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
    private RelativeLayout mPictureCodeLayout;
    private String timeStr;

    private boolean isShowPictureCode = false;

    private Toolbar mToolbar1;
    String s;
    private String mVerfiyToken;

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
        mPictureCodeLayout = find(R.id.login_verificode_layout);
        find(R.id.forget_password).setOnClickListener(this);
        find(R.id.login_fast).setOnClickListener(this);
        find(R.id.login_message).setOnClickListener(this);
        find(R.id.login_button).setOnClickListener(this);
        addIcon(mLoginPhonenumberEdt, R.drawable.vd_my, LEFT);
        addIcon(mLoginPasswordEdt, R.drawable.vd_regist_password, LEFT);
        addIcon(mLoginVerificodeEdt, R.drawable.vd_regist_captcha, LEFT);
        initAutoComplete("history", mLoginPhonenumberEdt);
        init();
    }

    private void init() {
        mLoginPhonenumberDelete.setOnClickListener(this);
        mLoginPasswordDelete.setOnClickListener(this);
        mLoginFreshVerification.setOnClickListener(this);
        mLoginEye.setOnClickListener(this);
        mLoginPhonenumberEdt.addTextChangedListener(new mTextWatcher(mLoginPhonenumberEdt, mLoginPhonenumberDelete));
        mLoginPasswordEdt.addTextChangedListener(new mTextWatcher(mLoginPasswordEdt, mLoginPasswordDelete));
        mLoginPhonenumberEdt.setText(MeManager.getPhone());
        mLoginPasswordEdt.setText(MeManager.getPWD());
        autoLogin();
    }

    private void autoLogin() {
        boolean b = PublicSPUtil.getInstance().getBoolean("IS_REGIST", false);
        if (b) {
            JSONObject premes = new JSONObject();
            try {
                mLoginPhonenumberEdt.setText(PublicSPUtil.getInstance().getString("tel", null));
                mLoginPasswordEdt.setText(PublicSPUtil.getInstance().getString("pwd", null));
                PublicSPUtil.getInstance().putBoolean("IS_REGIST", false);
                premes.put("tel", PublicSPUtil.getInstance().getString("tel", null));
                premes.put("pwd", PublicSPUtil.getInstance().getString("pwd", null));
                premes.put(Constants.ORIENTION_KEY, Constants.ORIENTION_VALUE);

                loginRun(premes);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {
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
                refrePictureCode();
                break;
            case R.id.login_message:  //短信验证码登录
                openActivity(MessageLoginActivity.class);
                finish();
                break;
            case R.id.login_fast:
                openActivity(PhoneRegistActivity.class);
                finish();
                break;
            case R.id.forget_password:
                openActivity(ResetPasswordActivity.class);
                finish();
                break;
            case R.id.login_button:  // 登录
                MobclickAgent.onEvent(this, "LoginClick");
                if (startUserLoging()) return;
                break;
        }
    }

    private void refrePictureCode() {
        PublicSPUtil.getInstance().getString("verfiy_token", mVerfiyToken);
        startRotateAnimation(mLoginFreshVerification, R.anim.login_code_rotate);
        requstVerfiyCode(mVerfiyToken);
    }

    private boolean startUserLoging() {
        String code_key = PublicSPUtil.getInstance().getString("verfiy_token", null);
        String phoneNum = mLoginPhonenumberEdt.getText().toString().trim();
        String passWord = mLoginPasswordEdt.getText().toString().trim();
        String veriFicodem = mLoginVerificodeEdt.getText().toString().trim();//验证码
        //定义一个JSON，用于向服务器提交数据
        JSONObject data = new JSONObject();
        try {
            if (veriFicodem.isEmpty()) {
                data.put("tel", phoneNum)
                        .put("pwd", passWord)
                        .put("f", 1);
            } else {
                data.put("tel", phoneNum)
                        .put("pwd", passWord)
                        .put("code", veriFicodem)
                        .put("token", code_key)
                        .put("f", 1);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "startUserLoging", e);
        }
        if (LocalThrough(phoneNum, passWord, veriFicodem)) return true;
        saveHistory("history", phoneNum);
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
                closeAnimator(LoginActivity.this);
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
        Log.e(TAG, jsonObject.toString());
        showProgressDialog(getString(R.string.logining));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                String result = OkClient.get(consistUrl(FUNC.LOGIN_PWD), jsonObject);
                e.onNext(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        closeProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            Log.e(TAG, jsonObject.toString());
                            String code = jsonObject.getString("code");
                            if ("s_ok".equals(code)) {
                                successResult(jsonObject);
                            }
                            if (code.equals("error")) {
                                errorResult(jsonObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.login_failed);
                        Log.e(TAG, "++++++++++++++登录失败+2++++++++++++++", throwable);
                    }
                });
    }


    private void successResult(JSONObject jsonObject) throws JSONException {
        Log.e(TAG, "登录成功" + s);
        JSONObject varJson = jsonObject.getJSONObject("var");
        String token = varJson.getString("token");
        JSONObject userJson = varJson.getJSONObject("user");
        MeManager.clearToken();
        String uid = userJson.getString("uid");
        String nickName = userJson.getString("nick_name");
        String tel = userJson.getString("tel");
        String pwd = userJson.getString("pwd");
        //String head_image = varJson.getString("head_image");
        PublicSPUtil.getInstance().putString("tel", tel);
        PublicSPUtil.getInstance().putString("pwd", pwd);
        PublicSPUtil.getInstance().putString("nickname", nickName);
        Log.e(TAG, uid + ":" + nickName + ":" + token + ":" + uid);
        EventBus.getDefault().post(new MessageEvent(nickName));
        isShowPictureCode = false;
        MeManager.setUid(uid);   //todo
        MeManager.setPhone(tel);
        MeManager.setName(nickName);
        MeManager.setToken(token);
        MeManager.setIsLgon(true);
        MeManager.setPWD(pwd);
        ToastUtils.showToast(R.string.login_success);
        onProfileSignIn("mLoginPhone");//帐号登录统计
        closeProgressDialog();
        openActivity(MainActivity.class);
    }

    private void errorResult(JSONObject jsonObject) throws JSONException {
        Log.e(TAG, "++++++++++++++登录失败++3+++++++++++++");
        String returnMsg = jsonObject.getString("message");//返回的信息
        if (returnMsg.equals("telphone_unregistered")) {
            closeProgressDialog();
            ToastUtils.showToast(R.string.telphoneunregistered);
        } else if (returnMsg.equals("verifycode error")) {
            mVerfiyToken = jsonObject.getString("token");
            PublicSPUtil.getInstance().putString("verfiy_token", mVerfiyToken);
            jsonObject.put("token", mVerfiyToken);
            Log.e(TAG, "mVerfiyToken:" + mVerfiyToken);
            requstVerfiyCode(mVerfiyToken);
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
            ToastUtils.showToast(R.string.tel_and_pwd_err);
        }
    }

    private void requstVerfiyCode(String mVerfiytoken) {
        Observable.create(new ObservableOnSubscribe<InputStream>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<InputStream> e) throws Exception {
                OkHttpClient client = OkClient.rightClient(NetConfig.consistUrl(VIRIFY_CODE));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("token", mVerfiytoken);
                Request request = OkClient.initRequest(NetConfig.consistUrl(VIRIFY_CODE), null, jsonObject.toString());
                Response response = client.newCall(request).execute();
                e.onNext(response.body().byteStream());
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<InputStream>() {
            @Override
            public void accept(@NonNull InputStream s) throws Exception {
                Log.e(TAG, "验证码图片：" + s.toString());
                Bitmap bitmap = BitmapFactory.decodeStream(s);
                mLoginImageVerificode.setImageBitmap(bitmap);
                stopRotateAnimation(mLoginFreshVerification);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                LogUtil.e(TAG, "net", throwable);
                stopRotateAnimation(mLoginFreshVerification);
                ToastUtils.showToast(R.string.request_failed);
            }
        });
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
        EventBus.getDefault().unregister(this);
        isShowPictureCode = false;
        stopRotateAnimation(mLoginFreshVerification);
        finish();
        UIUtils.closeAnimator(LoginActivity.this);
    }
}
