package com.etcxc.android.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.bean.MessageEvent;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.view.GlideCircleTransform;
import com.etcxc.android.utils.CropUtils;
import com.etcxc.android.utils.DialogPermission;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.Md5Utils;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SharedPreferenceMark;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.etcxc.MeManager.setIsLgon;
import static com.etcxc.android.R.id.login_phonenumber_delete;
import static com.etcxc.android.base.App.isLogin;

/**
 * 用户信息页面
 * Created by 刘涛 on 2017/6/17 0017.
 */

public class PersonalInfoAvtivity extends BaseActivity implements View.OnClickListener {
    //登录信息操作界面
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
    // 用户信息操作界面
    private ImageView mPersonHead;
    private TextView mPersonName;
    /*头像名称*/
    private File file;
    private Uri uri;
    /* 请求码*/
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_ALBUM = 2;
    private static final int REQUEST_CODE_CROUP_PHOTO = 3;
    private Toolbar mToolbar1, mToolbar2;
    private Button mExitLogin;
    //登录和用户界面切换layout
    private LinearLayout mLoginPaget, mInfoPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        mLoginPaget = (LinearLayout) findViewById(R.id.login_page);
        mInfoPager = (LinearLayout) findViewById(R.id.info_page);
        if(MeManager.getIsLogin()){//登录成功
            mLoginPaget.setVisibility(View.INVISIBLE);
            mInfoPager.setVisibility(View.VISIBLE);
        }else {//未登录
            mInfoPager.setVisibility(View.INVISIBLE);
            mLoginPaget.setVisibility(View.VISIBLE);
        }
        initLoginView();
        initUserInfoView();
    }

    private void initLoginView() {
        mToolbar1 = find(R.id.login_toolbar);
        setTitle(R.string.login);
        setBarBack(mToolbar1);
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


    private void initUserInfoView() {
        //登录之后显示的页面info_page
        mToolbar2 = (Toolbar) findViewById(R.id.person_toolbar);
        mToolbar2.setTitle(R.string.personinfo);
        setBarBack(mToolbar2);
        mPersonHead = (ImageView) findViewById(R.id.person_userhead);
        mPersonName = (TextView) findViewById(R.id.person_username);
        mExitLogin = (Button) findViewById(R.id.exit_login_btn);
        mPersonHead.setOnClickListener(this);
        mExitLogin.setOnClickListener(this);
        setstatus();
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
                if (LocalThrough(phoneNum, passWord, veriFicodem)) return;
                loginRun(loginServerUrl + data);
                break;
            case R.id.person_userhead:
                if (!isLogin) {
                    ToastUtils.showToast(R.string.nologin);
                    return;
                }
                show2Dialog();
                break;
            case R.id.exit_login_btn:
                if (!isLogin) {
                    ToastUtils.showToast(R.string.nologin);
                    return;
                }
                MeManager.logoutClear();
                MeManager.loginClear();
                setIsLgon(false);
                ToastUtils.showToast(R.string.exitlogin);
                finish();
                break;
        }
        this.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
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

    private void loginRun(String url) {
        showProgressDialog(getString(R.string.logining));
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
                        closeProgressDialog();
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
            closeProgressDialog();
            ToastUtils.showToast("登录成功");
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
                setPicCode(pictureCodeUrl + timeStr);
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
            }else {
                closeProgressDialog();
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
               PersonalInfoAvtivity.this.runOnUiThread(new Runnable() {
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
                PersonalInfoAvtivity.this.runOnUiThread(new Runnable() {
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

    //用户信息操作界面
    private void setstatus() {
        isLogin = MeManager.getIsLogin();
        if (isLogin) {
            String name = MeManager.getSid();
            mPersonName.setText(name);
            //适配7.0以上和以下的手机
            file = new File(FileUtils.getCachePath(this), "user-avatar.jpg");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                uri = Uri.fromFile(file);
            } else {
                //通过FileProvider创建一个content类型的Uri(android 7.0需要这样的方法跨应用访问)
                uri = FileProvider.getUriForFile(App.get(), "com.etcxc.useravatar", file);
            }
            if (file.exists()) {
                getImageToView();//初始化
            }
        } else {
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
            mPersonHead.setImageDrawable(drawable);
        }

    }
    private void show2Dialog() {
        //动态加载布局生成View对象
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View longinDialogView = layoutInflater.inflate(R.layout.cameral_album, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView takePicture = (TextView) longinDialogView.findViewById(R.id.take_picture);
        TextView selectPhoto = (TextView) longinDialogView.findViewById(R.id.select_photo);
        builder.setView(longinDialogView);
        final Dialog dialog = builder.show();
        /*
        相机
         */
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtil.hasCameraPermission(PersonalInfoAvtivity.this)) {
                    uploadAvatarFromPhotoRequest();
                }
                dialog.dismiss();
            }
        });
        /*
        相册
         */
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAvatarFromAlbumRequest();
                dialog.dismiss();
            }
        });
    }

    /**
     * 回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) {
            return;
        }
        if (requestCode == REQUEST_CODE_ALBUM && data != null) {
            Uri newUri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                newUri = Uri.parse("file:///" + CropUtils.getPath(this, data.getData()));
            } else {
                newUri = data.getData();
            }
            if (newUri != null) {
                startPhotoZoom(newUri);
            } else {
                Toast.makeText(this, "没有得到相册图片", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {

            startPhotoZoom(uri);
        } else if (requestCode == REQUEST_CODE_CROUP_PHOTO) {
            Glide.with(this.getApplicationContext())
                    .load(uri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.vd_head2)
                    .dontAnimate()
                    .transform(new GlideCircleTransform(this))
                    .into(mPersonHead);
        }
    }

    /**
     * 裁剪拍照
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("output", Uri.fromFile(file));
        intent.putExtra("outputFormat", "JPEG");// 返回格式
        startActivityForResult(intent, REQUEST_CODE_CROUP_PHOTO);
    }

    /**
     * camera,相机
     */
    private void uploadAvatarFromPhotoRequest() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    /**
     * album,相册
     */
    private void uploadAvatarFromAlbumRequest() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_PICK
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_ALBUM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionUtil.REQUEST_SHOWCAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    uploadAvatarFromPhotoRequest();

                } else {
                    if (!SharedPreferenceMark.getHasShowCamera()) {
                        SharedPreferenceMark.setHasShowCamera(true);
                        new DialogPermission(this, "关闭摄像头权限影响扫描功能");

                    } else {
                        Toast.makeText(this, "未获取摄像头权限", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 圆形
     *
     * @param bitmap
     * @return
     */
    public Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if (width > height) {
            r = height;
        } else {
            r = width;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param uri
     */
    private Bitmap userBitmap;

    private void getImageToView() {
        //加载本地图片
        final File cover = FileUtils.getSmallBitmap(this, file.getPath());
        Uri uri = Uri.fromFile(cover);
        //通知相册更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        this.sendBroadcast(intent);
        try {
            userBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //userHead.setImageURI(uri);
        mPersonHead.setImageBitmap(toRoundBitmap(userBitmap));
        // todo 上传图片到服务器
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        mPersonName.setText(messageEvent.message);
        Boolean isLogin = MeManager.getIsLogin();
        if (!isLogin) {
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
            mPersonHead.setImageDrawable(drawable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setstatus();
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
