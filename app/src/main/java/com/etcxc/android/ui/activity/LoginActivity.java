package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

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
import static com.etcxc.android.net.OkClient.get;

/**
 * Created by 刘涛 on 2017/6/14 0014.
 *
 * 登录页面
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    protected final String TAG = ((Object) this).getClass().getSimpleName();
    private final OkHttpClient client = new OkHttpClient();
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;
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
    String pictureCodeUrl = "http://192.169.6.119/index.php/captcha";  //更换图形验证码url
    String loginServerUrl = "http://192.169.6.119/login/login/login/";//登录的url
    private boolean isShow = false;
  private  SharedPreferences sPUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        setTitle(R.string.login);
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
        setPicCode(pictureCodeUrl);//初始化图形验证码
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
            case R.id.login_fresh_verification:
                startRotateAnimation(mLoginFreshVerification, R.anim.login_code_rotate);
                setPicCode(pictureCodeUrl);
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
            case R.id.login_button:  // 登录 tel/15974255013/pwd/123456/code/wrty/sms_code/123456

                loginRun("http://wthrcdn.etouch.cn/weather_mini?city=%E6%B7%B1%E5%9C%B3");
                String phoneNum = mLoginPhonenumberEdt.getText().toString().trim();
                String passWord = mLoginPasswordEdt.getText().toString().trim();
                String veriFicodem= mLoginVerificodeEdt.getText().toString().trim();//验证码
                String data = "tel/" + phoneNum + "/pwd/" + passWord;
                //判断
                if (LocalThrough(phoneNum, passWord, veriFicodem)) return;
                //String str2 = "tel/15974255013/pwd/123456/code/wrty/sms_code/123456";
                loginUUrl(loginServerUrl+data);
                break;
        }
        this.overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }
    /**
     * 正则判断手机号码是否正确
     */
    public boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) return false;
        String regExp = "((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";//;
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(mobiles);
        return m.matches();
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
        } else if (isShow) {
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
                e.onNext(get(url, new JSONObject()));
            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this))
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        //JSONStringer js = new JSONStringer(s);
                        JSONObject jsonObject = new JSONObject(s);
                        ToastUtils.showToast("----" + jsonObject + "---------");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                });
    }

    String str1 = "http://wthrcdn.etouch.cn/weather_mini?city=%E6%B7%B1%E5%9C%B3";


    public void loginUUrl(String url) {
        Request requst = new Request.Builder()
                .url(url)//http://192.169.6.119/login/login/login/tel/15974255013/pwd/123456/code/wrty
                .build();
        client.newCall(requst).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("网络不佳，登录失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string().trim();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String login = jsonObject.getString("login");
                    if ( login.equals("s_ok")) {
                        //请求成功
                        JSONObject varJson = jsonObject.getJSONObject("var");
                        String tel = varJson.getString("tel").toString();
                        String pwd = varJson.get("pwd").toString();
                        String loginTime = varJson.get("login_time").toString();
                        String nickName = varJson.get("nick_name").toString();
                        //  todo   保存用户信息到本地  通过eventbus 把手机号码传递到Mine界面
                        Editor editor = sPUser.edit();
                        editor.putString("telphone", tel);
                        editor.putString("password", Md5Utils.encryptpwd(pwd));
                        editor.putString("logintime",loginTime);
                        editor.putString("nickname",nickName);
                        editor.commit();

                    }
                    if (login.equals("err")) {
                             String returnMsg = jsonObject.getString("message");//返回的信息
                        if (returnMsg.equals("need_code")){
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mPictureCodeLayout.setVisibility(View.VISIBLE);
                                    setPicCode(pictureCodeUrl);
                                }
                            });

                        }else {
                            ToaltsThreadUIshow("测试:"+returnMsg);// todo 返回数据待改进
                        }
                        return;
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }


            }
        });
    }

    private void ToaltsThreadUIshow(Object s) {
        LoginActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {//mLoginVerificodeEdt
                ToastUtils.showToast(s+"");
            }
        });
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
                        LogUtil.v(TAG, "-----------更新验证码成功--------");
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
        stopRotateAnimation(mLoginFreshVerification);
        finish();
    }

    //将map型转为请求参数型
    public static String urlencode(Map<String,String> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue()+"","UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
