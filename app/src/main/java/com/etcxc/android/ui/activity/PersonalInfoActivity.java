package com.etcxc.android.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.etcxc.MeManager;
import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.bean.MessageEvent;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkClient;
import com.etcxc.android.ui.view.CircleImageView;
import com.etcxc.android.utils.DialogPermission;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.LoadImageHeapler;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.SharedPreferenceMark;
import com.etcxc.android.utils.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.etcxc.android.net.FUNC.HEAD_CHANGE;
import static com.etcxc.android.net.FUNC.LOGIN_OUT;
import static com.etcxc.android.utils.FileUtils.getCachePath;

/**
 * 个人信息界面（通过登录界面拆分）
 * Created by caoyu on 2017/8/2
 */
public class PersonalInfoActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    protected final String TAG = "PersonalInfoActivity";
    //保存头像的uri
    private Uri uri;
    /* 请求码*/
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_ALBUM = 2;
    private Toolbar mToolbar2;
    private Button mExitLogin;
    private TextView mUserName, mUserPhone, mUserSex;
    private CircleImageView mUserHead;
    private final static String IMAGE_HEAD = "head.jpg";
    private String CROP_HEAD = "user_head.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        EventBus.getDefault().register(this);
        initUserInfoView();
    }

    private void initUserInfoView() {
        Resources r =this.getResources();
        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + r.getResourcePackageName(R.drawable.vd_head) + "/"
                + r.getResourceTypeName(R.drawable.vd_head) + "/"
                + r.getResourceEntryName(R.drawable.vd_head));
        mToolbar2 = find(R.id.person_toolbar);
        setSupportActionBar(mToolbar2);
        mToolbar2.setTitle(R.string.personinfo);
        mToolbar2.inflateMenu(R.menu.menu);
        setBarBack(mToolbar2);
        find(R.id.info_head_layout).setOnClickListener(this);
        find(R.id.info_name_layout).setOnClickListener(this);
        find(R.id.info_phone_layout).setOnClickListener(this);
        find(R.id.info_sex_layout).setOnClickListener(this);
        mUserHead = find(R.id.user_head);
        mUserName = find(R.id.user_name);
        mUserPhone = find(R.id.user_phone);
        mUserSex = find(R.id.user_sex);

        mExitLogin = find(R.id.exit_login_btn);


        mExitLogin.setOnClickListener(this);
        mToolbar2.setOnMenuItemClickListener(this);
        mUserHead.setOnClickListener(this);
        mUserHead.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                File file = new File(FileUtils.getCachePath(PersonalInfoActivity.this), CROP_HEAD);
                if (file.exists())
                    uri = Uri.fromFile(file);
                    FileUtils.showBigImageView(PersonalInfoActivity.this, uri);
                return false;
            }
        });
        setstatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head:
                show2Dialog();
                break;
            case R.id.info_head_layout:
                show2Dialog();
                break;
            case R.id.info_name_layout:
                openActivity(ChangeNickNameActivity.class);
                break;
            case R.id.info_phone_layout:
                openActivity(ChangePhoneActivity.class);
                break;
            case R.id.info_sex_layout:
                View view = LayoutInflater.from(this).inflate(R.layout.select_sex, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(view);
                Dialog dialog = builder.show();
                view.findViewById(R.id.select_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (((RadioButton) view.findViewById(R.id.select_man)).isChecked()) {
                            mUserSex.setText("男");
                        } else mUserSex.setText("女");
                        dialog.dismiss();
                    }
                });

                break;
            case R.id.exit_login_btn://退出登录
                requestLoginOut();
                break;
        }
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

    private boolean cropExists(String cropName) {
        File file = new File(FileUtils.getCachePath(this), cropName);
        return file.exists() && file.length() > 0;
    }

    private void setstatus() {
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head, null);
        mUserHead.setImageDrawable(drawable);
        LoadImageHeapler headLoader = new LoadImageHeapler(this, CROP_HEAD);
        if (MeManager.getIsLogin()) {
            if (NetConfig.isAvailable()) {
                mUserName.setText(MeManager.getName());
                mUserPhone.setText(MeManager.getPhone());

                headLoader.loadUserHead(new LoadImageHeapler.ImageLoadListener() {
                    @Override
                    public void loadImage(Bitmap bmp) {
                        mUserHead.setImageBitmap(bmp);
                    }
                });
            } else {
                if (cropExists(CROP_HEAD)) {
                    setImageFromUri(mUserHead, Uri.fromFile(new File(FileUtils.getCachePath(this) + File.separator + CROP_HEAD)));
                }
                mUserName.setText(MeManager.getName());
                mUserPhone.setText(MeManager.getPhone());
            }
        }

    }

    private void show2Dialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.cameral_album, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        Dialog dialog = builder.show();
        /*
        相机
         */
        view.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
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
        view.findViewById(R.id.select_photo).setOnClickListener(new View.OnClickListener() {
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
        if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            LogUtil.e(TAG, "crop", cropError);
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_ALBUM && data != null) {//相册
            if (data == null) return;
            File file1 = new File(FileUtils.getCachePath(this), CROP_HEAD);
            if (file1.exists()) file1.delete();
            try {
                boolean success = file1.createNewFile();
                if (success)
                    UCrop.of(data.getData(), Uri.fromFile(file1)).withAspectRatio(1, 1).start(this);
            } catch (IOException e) {
                LogUtil.e(TAG, "result_album", e);
            }

        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {//相机
            File file = new File(FileUtils.getCachePath(this), CROP_HEAD);
            if (file.exists())
                file.delete();
            try {
                boolean success = file.createNewFile();
                if (success) UCrop.of(uri, Uri.fromFile(file)).withAspectRatio(1, 1).start(this);
            } catch (IOException e) {
                LogUtil.e(TAG, "result_camera", e);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            updateHeadToServer(data);
        }
    }

    private void setImageFromUri(ImageView imageView, Uri uri) {
        if (imageView == null || uri == null) return;
        try {
            imageView.setImageBitmap(loadBitmap(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //reduce
    private Bitmap loadBitmap(Uri uri) throws FileNotFoundException {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inSampleSize = FileUtils.calculateInSampleSize(opt, 200, 300);
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opt);
    }

    /**
     * camera,相机
     */
    private void uploadAvatarFromPhotoRequest() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(FileUtils.getCachePath(this), IMAGE_HEAD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(App.get(), BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//这个权限要添加
        } else uri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
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
                        Log.e(TAG,s);
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
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head, null);
            mUserHead.setImageDrawable(drawable);
        }
    }

    /**
     * 提交头像之服务器
     */
    private void updateHeadToServer(Intent data) {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<String> e) throws Exception {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image"), new File(getCachePath(PersonalInfoActivity.this), CROP_HEAD));
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image[]", CROP_HEAD, fileBody)
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
                e.onNext(client.newCall(request).execute().body().string());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                        Log.e(TAG, "修改头像:" + s);
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");
                        if (code.equals("s_ok")) {
                            closeProgressDialog();
                            ToastUtils.showToast(R.string.change_head_success);
                            // mUserHead.setImageBitmap(bitmap);
                            setImageFromUri(mUserHead, UCrop.getOutput(data));

                        } else {
                            closeProgressDialog();
                            ToastUtils.showToast(R.string.request_failed);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "uploadMultiFile() e=" + throwable);
                        ToastUtils.showToast(R.string.request_failed);
                        closeProgressDialog();
                    }
                });
    }
}
