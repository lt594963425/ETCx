package com.etcxc.android.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.etcxc.MeManager;
import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.bean.MessageEvent;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.view.CircleImageView;
import com.etcxc.android.ui.view.GlideCircleTransform;
import com.etcxc.android.utils.CropUtils;
import com.etcxc.android.utils.DialogPermission;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.LoadImageHeapler;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.SharedPreferenceMark;
import com.etcxc.android.utils.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.etcxc.android.net.FUNC.HEAD_CHANGE;
import static com.etcxc.android.net.FUNC.LOGIN_OUT;
import static com.etcxc.android.utils.FileUtils.getCachePath;

/**
 * 个人信息界面（通过登录界面拆分）
 * Created by caoyu on 2017/8/2
 */
public class PersonalInfoActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    protected final String TAG = "PersonalInfoActivity";
    /*头像名称*/
    private File mFile;
    private Uri uri;
    /* 请求码*/
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_ALBUM = 2;
    private static final int REQUEST_CODE_CROUP_PHOTO = 3;
    private Toolbar mToolbar2;
    private Button mExitLogin;
    private TextView mUserName, mUserPhone;
    private CircleImageView mUserHead;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        EventBus.getDefault().register(this);
        initUserInfoView();
    }

    private void initUserInfoView() {
        //登录之后显示的页面info_page
        mToolbar2 = find(R.id.person_toolbar);
        setSupportActionBar(mToolbar2);
        mToolbar2.setTitle(R.string.personinfo);
        mToolbar2.inflateMenu(R.menu.menu);
        setBarBack(mToolbar2);
        RelativeLayout mHeadLayout = find(R.id.info_head_layout);
        RelativeLayout mNameLayout = find(R.id.info_name_layout);
        RelativeLayout mPhoneLayout = find(R.id.info_phone_layout);
        mUserHead = find(R.id.user_head);
        mUserName = find(R.id.user_name);
        mUserPhone = find(R.id.user_phone);

        mExitLogin = find(R.id.exit_login_btn);
        mHeadLayout.setOnClickListener(this);
        mNameLayout.setOnClickListener(this);
        mPhoneLayout.setOnClickListener(this);

        mExitLogin.setOnClickListener(this);
        mToolbar2.setOnMenuItemClickListener(this);
        setstatus();
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

    //用户信息操作界面
    private void setstatus() {
        //适配7.0以上和以下的手机
        mFile = new File(getCachePath(this), "user-avatar.jpg");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(mFile);
        } else {
            //通过FileProvider创建一个content类型的Uri(android 7.0需要这样的方法跨应用访问)
            uri = FileProvider.getUriForFile(App.get(), BuildConfig.APPLICATION_ID + ".fileprovider", mFile);
        }
        LoadImageHeapler headLoader = new LoadImageHeapler(this, "user-avatar.jpg");
        if (MeManager.getIsLogin()) {
            if (NetConfig.isAvailable()) {
                mUserName.setText(MeManager.getName());
                mUserPhone.setText(MeManager.getPhone());
                if (mFile.exists()) {
                    Glide.with(this.getApplicationContext())
                            .load(uri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .error(R.drawable.vd_head2)
                            .dontAnimate()
                            .transform(new GlideCircleTransform(this))
                            .into(mUserHead);
                } else {
                    headLoader.loadUserHead(new LoadImageHeapler.ImageLoadListener() {
                        @Override
                        public void loadImage(Bitmap bmp) {
                            mUserHead.setImageBitmap(bmp);
                        }
                    });
                }
            } else {
                Glide.with(this.getApplicationContext())
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .error(R.drawable.vd_head2)
                        .dontAnimate()
                        .transform(new GlideCircleTransform(this))
                        .into(mUserHead);
                mUserName.setText(MeManager.getName());
                mUserPhone.setText(MeManager.getPhone());
            }
        } else {
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
            mUserHead.setImageDrawable(drawable);
        }

    }

    private void show2Dialog() {
        //动态加载布局生成View对象
        View longinDialogView = LayoutInflater.from(this).inflate(R.layout.cameral_album, null);
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
            public void onClick(View v) {  //申请相机权限
                if (!NetConfig.isAvailable()) {
                    ToastUtils.showToast(R.string.network_isdown);
                    return;
                }
                if (PermissionUtil.hasCameraPermission(PersonalInfoActivity.this)) {
                    uploadAvatarFromPhotoRequest();
                    dialog.dismiss();
                }

            }
        });
        /*
        相册
         */
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetConfig.isAvailable()) {
                    ToastUtils.showToast(R.string.network_isdown);
                    return;
                }
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
        if (requestCode == REQUEST_CODE_ALBUM && data != null) {//相册
            Uri newUri;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                newUri = Uri.parse("file:///" + CropUtils.getPath(this, data.getData()));
            } else {
                newUri = data.getData();
            }
            if (newUri != null) {
                startPhotoZoom(newUri);
            } else {
                ToastUtils.showToast(R.string.not_get_image);
            }
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {//相机
            File cover = FileUtils.getSmallBitmap(mFile.getPath());
            Uri newuri = Uri.fromFile(cover);
            startPhotoZoom(newuri);
        } else if (requestCode == REQUEST_CODE_CROUP_PHOTO) {
            Uri.fromFile(FileUtils.getSmallBitmap(mFile.getPath()));
            Log.e(TAG, "################提交服务器################");
            updateHeadToServer();
        }
    }

    /**
     * 裁剪拍照
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Uri newuri = Uri.fromFile(FileUtils.getSmallBitmap(mFile.getPath()));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra("crop", "true")// crop=true 有这句才能出来最后的裁剪页面.
                .putExtra("aspectX", 1)// 这两项为裁剪框的比例.
                .putExtra("aspectY", 1)// x:y=1:1
                .putExtra("output", newuri)
                .putExtra("outputFormat", "JPEG");// 返回格式
        startActivityForResult(intent, REQUEST_CODE_CROUP_PHOTO);
    }

    /**
     * camera,相机
     */
    private void uploadAvatarFromPhotoRequest() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .putExtra(MediaStore.Images.Media.ORIENTATION, 0)
                .putExtra(MediaStore.EXTRA_OUTPUT, uri);
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
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    private void getImageToView() {
        Log.e(TAG, "#############################路径uri：" + uri);
        Glide.with(this.getApplicationContext())
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.drawable.vd_head2)
                .dontAnimate()
                .transform(new GlideCircleTransform(this))
                .into(mUserHead);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.jy:
                WXLogin();
                break;
        }
        return false;
    }

    private String WEIXIN_SCOPE = "snsapi_userinfo";// 用于请求用户信息的作用域
    private String WEIXIN_STATE = "login_state"; // 自定义

    private void WXLogin() {
        IWXAPI WXapi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, true);
        WXapi.registerApp(Constants.WX_APP_ID);
        if (WXapi != null && WXapi.isWXAppInstalled()) {
            SendAuth.Req req;
            req = new SendAuth.Req();
            req.scope = WEIXIN_SCOPE;
            req.state = WEIXIN_STATE;
            WXapi.sendReq(req);
            Log.i(TAG, "。。。。。。。。。。。。WxLogin()，微信登录。。。。。。。");
            ToastUtils.showToast("请稍后");
        } else
            ToastUtils.showToast("用户未安装微信");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_head_layout:
                show2Dialog();
                break;
            case R.id.info_name_layout:
                openActivity(ChangeNickNameActivity.class);
                break;
            case R.id.info_phone_layout:
                openActivity(ChangePhoneActivity.class);
                break;
            case R.id.exit_login_btn://退出登录
                requestLoginOut();
                break;
        }
    }

    private void requestLoginOut() {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", MeManager.getUid());
                jsonObject.put("token", MeManager.getToken());
                Log.e(TAG, jsonObject.toString());
                e.onNext(OkClient.get(NetConfig.consistUrl(LOGIN_OUT), jsonObject));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        JSONObject result = new JSONObject(s);
                        String code = result.getString("code");
                        if (code.equals("s_ok")) {
                            closeProgressDialog();
                            MeManager.clearAll();
                            MeManager.setIsLgon(false);
                            ToastUtils.showToast(R.string.exitlogin);
                            finish();
                        } else {
                            closeProgressDialog();
                            ToastUtils.showToast(R.string.request_failed);
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        ToastUtils.showToast(R.string.request_failed);
                        closeProgressDialog();
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent messageEvent) {
        mUserName.setText(messageEvent.message);
        if (!MeManager.getIsLogin()) {
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
            mUserHead.setImageDrawable(drawable);
        }
    }

    /**
     * 提交头像之服务器
     */
    private void updateHeadToServer() {
        showProgressDialog(R.string.loading);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image"), new File(getCachePath(this), "user-avatar.jpg"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image[]", "user-avatar.jpg", fileBody)
                .addFormDataPart("uid", MeManager.getUid())
                .addFormDataPart("token", MeManager.getToken())
                .build();
        Request request = new Request.Builder()
                .url(NetConfig.HOST + HEAD_CHANGE)
                .post(requestBody)
                .build();
        final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        OkHttpClient client = httpBuilder
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "uploadMultiFile() e=" + e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(R.string.request_failed);
                    }
                });
                closeProgressDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    Log.e(TAG, "修改头像:" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.getString("code");
                    if (code.equals("s_ok")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(R.string.change_head_success);
                                getImageToView();
                                PublicSPUtil.getInstance().putBoolean("CHANGE_INFO", true);

                            }
                        });
                        closeProgressDialog();
                    } else {
                        String error = jsonObject.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(R.string.request_failed + error);
                            }
                        });
                        closeProgressDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    closeProgressDialog();
                }

            }
        });
    }
}
