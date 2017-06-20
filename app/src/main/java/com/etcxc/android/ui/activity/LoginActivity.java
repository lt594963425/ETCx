package com.etcxc.android.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.Toolbar;
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
import com.etcxc.android.net.OkClient;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
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
    private EditText mLoginPhonenumberEdt,mLoginPasswordEdt,mLoginVerificodeEdt; // 手机号码,密码 ,输入图形验证码
    private ImageView mLoginPhonenumberDelete,mLoginPasswordDelete;//   删除
    private ImageView mLoginEye; //可见与不可见
    private ImageView mLoginImageVerificode;//图形取验证码
    private ImageView mLoginFreshVerification;//刷新验证码
    private TextView mLoginMessage;//短信验证码登录
    private TextView mLoginFast;//快速注册
    private TextView mForgetPassword;//忘记密码
    private Button mLoginButton;//  登录
    private RelativeLayout mPictureCodeLayout;
    String pictureCodeUrl = "http://192.169.6.119/index.php/captcha";  //更换图形验证码url
    String loginPwdUrl  = "http://192.169.6.119/login/login/login/tel/15974255013/pwd/123456/code/wrty/sms_code/123456";//登录的url
    private boolean isShow = false;
    //
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        initView();
    }

    private void initView() {
        Toolbar mToolbar = find(R.id.login_toolbar);
        mToolbar.setTitle(getString(R.string.login));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                if(s.length() > 0 && !mLoginPasswordEdt.getText().toString().isEmpty()){
                    mLoginPasswordDelete.setVisibility(View.VISIBLE);
                    //temp = "";
                }else {
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
                // ToDo 向后台请求更换验证码，同时更新验证码图片服务器返回的图形验证码 URL
               // SetPicCode(pictureCodeUrl);
                run(pictureCodeUrl);
                break;
            case R.id.login_message:  //短信验证码登录
                Intent intentMsg = new Intent(this, MessageLoginActivity.class);
                startActivity(intentMsg);
                break;

            case R.id.login_fast:
                Intent intentFast = new Intent(this, PhoneRegistActivity.class);
                startActivity(intentFast);
                break;
            case R.id.forget_password:
                Intent intentForget = new Intent(this, ResetPasswordActivity.class);
                startActivity(intentForget);
                break;
            case R.id.login_button:  // 登录
                String phoneNum = mLoginPhonenumberEdt.getText().toString().trim();
                String passWord = mLoginPasswordEdt.getText().toString().trim();
                String veriFicodem = mLoginVerificodeEdt.getText().toString().trim();//验证码
                //登录https://192.169.6.119/index.php/login/loginpwd/login/tel/'tel'/pwd/'pwd'
                // "http://192.169.6.119/index.php/login/login_pwd/login/tel/'tel'/pwd/'pwd'/code/'code'"
                //192.169.6.119/index.php/login/loginpwd/session/
                data = "tel/'" + phoneNum + "'/pwd/'" + passWord+"'/code/'"+veriFicodem+"'";
                //判断
                if (LocalThrough(phoneNum, passWord, veriFicodem)) return;
                //todo  访问服务器 请求输入的次数
               // getSession(sessionUrl);

                break;
        }
    }
    /**
     * 判断手机号码是否正确
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
    int num = 0;
    private Boolean flag = false;
    private boolean LocalThrough(String phoneNum, String passWord, String veriFicodem) {
        if(phoneNum.isEmpty()){
            ToastUtils.showToast(R.string.phone_isempty);
            return true;
        }else if (!isMobileNO(phoneNum)) {
            ToastUtils.showToast(R.string.please_input_correct_phone_number);
            return true;
        }else if(passWord.isEmpty()){
            ToastUtils.showToast(R.string.password_isempty);
            return true;
        }else if(passWord.length() < 6){
            ToastUtils.showToast(R.string.password_isshort);
            return true;
        }else  if(isShow){
            if(veriFicodem.isEmpty()){
                ToastUtils.showToast(R.string.set_picture_verifycodes);
                return true;
            }
        }
        return false;
    }

    private void   getSession(String Url) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                String result = OkClient.get(Url, new JSONObject());
                JSONObject jsonObject = new JSONObject(result);
                String str =  jsonObject.getString("login_num").toString();
                int count = Integer.parseInt(str);
                e.onNext(count);
            }
        }).compose(RxUtil.activityLifecycle(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer s) throws Exception {
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {

            }
        });
    }


    private void login () {

    }

    private void getVerifiCode(){

    }
    private void run(String url) {
        Observable.create(new ObservableOnSubscribe<String >() {
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
                       // ToastUtils.showToast("----"+count+"---------");
                    }
                });


/*        final Request request = new Request.Builder().url(s).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showToast("登录失败");
                return;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) ;
                 String responss =  response.body().string();
                JSONObject jsonObjL = null;
                try {
                    jsonObjL = new JSONObject(responss);
                    String code = jsonObjL.getString("login").toString();
                    String codepwd = jsonObjL.getString("pwd").toString();
                    String codemsg = jsonObjL.getString("message").toString();
                    String codepic = jsonObjL.getString("code").toString();
                    if(code.equals("s_ok")){
                        //请求成功
                        JSONObject varJson = jsonObjL.getJSONObject("var");
                        String tel =varJson.getString("tel").toString();
                        String pwd =varJson.get("pwd").toString();
                        String login_time = varJson.get("login_time").toString();
                        String nick_name = varJson.get("nick_name").toString();
                        //  todo   保存用户信息到本地
                        ToastUtils.showToast("登录成功");
                        finish();
                    }else if(codepwd.equals("err")){
                        ToastUtils.showToast(codemsg+"====失败 1");
                    }else if(codepic.equals("err")){
                        ToastUtils.showToast(codemsg+"====失败 2");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });*/
    }

    Bitmap bitmap;
    private Bitmap SetPicCode(final String url) {
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
                                LogUtil.v(TAG,"-----------更新验证码成功--------");
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
     * @param  view
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
