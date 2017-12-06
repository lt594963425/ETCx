package com.etcxc.android.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
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
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.ui.view.CircleImageView;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.LoadImageHeapler;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.net.FUNC.HEAD_CHANGE;
import static com.etcxc.android.net.FUNC.LOGIN_OUT;
import static com.etcxc.android.net.NetConfig.HOST;
import static com.etcxc.android.net.NetConfig.JSON;
import static com.etcxc.android.utils.FileUtils.getCachePath;
import static com.etcxc.android.utils.UIUtils.closeAnimator;

/**
 * 个人信息界面（通过登录界面拆分）
 * Created by caoyu on 2017/8/2
 */
public class PersonalInfoActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    protected final String TAG = "PersonalInfoActivity";
    private Uri uri;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_ALBUM = 2;
    private Toolbar mToolbar2;
    private Button mExitLogin;
    private TextView mUserName, mUserPhone, mUserSex;
    public CircleImageView mUserHead;
    private String IMAGE_HEAD = MeManager.getToken() + "_head.jpg";
    private String CROP_HEAD = MeManager.getToken() + "_crop.jpg";
    private LoadImageHeapler mHeadLoader;
    protected  String IMAGE_TAG = "PE_LOAD_IMAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        initUserInfoView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_head:
                File file = new File(FileUtils.getCachePath(PersonalInfoActivity.this), CROP_HEAD);
                if (file.exists()) {
                    uri = Uri.fromFile(file);
                }
                FileUtils.showBigImageView(PersonalInfoActivity.this, uri);
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
                modifySex();
                break;
            //退出登录
            case R.id.exit_login_btn:
                requestLoginOut();
                break;
            default:
                break;
        }
    }
    private void initUserInfoView() {
        Resources r = this.getResources();
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
        initData();
    }



    private void modifySex() {
        View view = LayoutInflater.from(this).inflate(R.layout.select_sex, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        Dialog dialog = builder.show();
        view.findViewById(R.id.select_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((RadioButton) view.findViewById(R.id.select_man)).isChecked()) {
                    mUserSex.setText("男");
                } else {
                    mUserSex.setText("女");
                }
                dialog.dismiss();
            }
        });
    }

    private void setBarBack(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                closeAnimator(PersonalInfoActivity.this);
            }
        });
    }

    private void initData() {
        VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head, null);
        mUserHead.setImageDrawable(drawable);
        if (MeManager.getIsLogin()) {
            mUserName.setText(MeManager.getName());
            mUserPhone.setText(MeManager.getPhone());
            File file = new File(getCachePath(this), CROP_HEAD);
            if (file.exists()) {
                LogUtil.e(TAG, "本地加载头像");
                Glide.with(this).load(file).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mUserHead);
            } else {
                if (mHeadLoader == null) {
                    mHeadLoader = new LoadImageHeapler(CROP_HEAD,IMAGE_TAG);
                }
                mHeadLoader.loadUserHead(new LoadImageHeapler.ImageLoadListener() {
                    @Override
                    public void loadImage(Bitmap bmp) {
                        mUserHead.setImageBitmap(bmp);
                    }
                });
            }
        }

    }

    private void show2Dialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.cameral_album, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.show();
        view.findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //申请相机权限
                startCamera();
                dialog.dismiss();
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

    private void startCamera() {
        if (!SystemUtil.hasCamera()) {
            ToastUtils.showToast(getString(R.string.camera_not_found));
            return;
        }
        PermissionUtil.requestPermissions(this, Manifest.permission.CAMERA, new PermissionUtil.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    camera();
                }
            }
        });
    }

    /**
     * 拍照
     */
    private void camera() {
        File file = new File(getCachePath(this), System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Android7.0以上URI
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //通过FileProvider创建一个content类型的Uri
            uri = FileProvider.getUriForFile(this, "com.etcxc.android.fileprovider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        try {
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        } catch (ActivityNotFoundException anf) {
            ToastUtils.showToast("摄像头未准备好！");
        }
    }

    /**
     * album,相册
     */
    private void uploadAvatarFromAlbumRequest() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_PICK
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_ALBUM);
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
            if (data == null) {
                return;
            }
            File file1 = new File(FileUtils.getCachePath(this), IMAGE_HEAD);
            if (file1.exists()) {
                file1.delete();
            }
            try {
                boolean success = file1.createNewFile();
                if (success) {
                    UCrop.of(data.getData(), Uri.fromFile(file1))
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(400, 800)
                            .start(this);
                }
            } catch (IOException e) {
                LogUtil.e(TAG, "result_album", e);
            }

        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {//相机
            File file = new File(FileUtils.getCachePath(this), IMAGE_HEAD);
            if (file.exists()) {
                file.delete();
            }
            try {
                boolean success = file.createNewFile();
                if (success) {
                    UCrop.of(uri, Uri.fromFile(file))
                            .withAspectRatio(1, 1)
                            .withMaxResultSize(400, 400)
                            .start(this);
                }
            } catch (IOException e) {
                LogUtil.e(TAG, "result_camera", e);
            }
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Log.i(TAG, "onActivityResult: " + UCrop.getOutput(data));
            try {
                Bitmap bitmap = loadBitmap(UCrop.getOutput(data));
                updateHeadToServer(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    //reduce
    private Bitmap loadBitmap(Uri uri) throws FileNotFoundException {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inSampleSize = FileUtils.calculateInSampleSize(opt, 200, 400);
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opt);
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
            default:
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
        } else {
            ToastUtils.showToast("用户未安装微信");
        }
    }


    private void requestLoginOut() {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("uid", MeManager.getUid())
                        .put("token", MeManager.getToken());
                Log.e(TAG, jsonObject.toString());
                e.onNext(OkHttpUtils
                        .postString()
                        .url(NetConfig.HOST + LOGIN_OUT)
                        .content(String.valueOf(jsonObject))
                        .mediaType(JSON)
                        .build()
                        .execute().body().string());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, s);
                        JSONObject result = new JSONObject(s);
                        String code = result.getString("code");
                        if ("s_ok".equals(code)) {
                            closeProgressDialog();
                            MeManager.clearAll();
                            MeManager.setIsLgon(false);
                            ToastUtils.showToast(R.string.exitlogin);
                            openActivity(LoginActivity.class);
                            finish();
                        }
                        if ("error".equals(code)) {
                            closeProgressDialog();
                            String msg = result.getString("message");
                            if (NetConfig.ERROR_TOKEN.equals(msg)) {
                                MeManager.setIsLgon(false);
                                openActivity(LoginActivity.class);
                            } else {
                                ToastUtils.showToast(msg);
                            }
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


    /**
     * 提交头像之服务器
     */
    private void updateHeadToServer(Bitmap bitmap) {
        showProgressDialog(R.string.loading);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull ObservableEmitter<String> e) throws Exception {
                File file = new File(getCachePath(PersonalInfoActivity.this), IMAGE_HEAD);
                LogUtil.e(TAG, "file的大小：" + String.valueOf(file.length()) + "字节");
                Map<String, String> params = new HashMap<>();
                params.put("uid", MeManager.getUid());
                params.put("token", MeManager.getToken());
                e.onNext(OkHttpUtils
                        .post()
                        .addFile("image[]", IMAGE_HEAD, file)
                        .url(HOST + HEAD_CHANGE)
                        .params(params)
                        .build().execute().body().string());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");
                        if ("s_ok".equals(code)) {
                            closeProgressDialog();
                            ToastUtils.showToast(R.string.change_head_success);
                            mUserHead.setImageBitmap(bitmap);
                            FileUtils.saveToSDCard(CROP_HEAD, bitmap);
                        }
                        if ("error".equals(code)) {
                            String msg = jsonObject.getString("message");
                            if (NetConfig.ERROR_TOKEN.equals(msg)) {
                                MeManager.setIsLgon(false);
                                openActivity(LoginActivity.class);
                                finish();
                            } else {
                                closeProgressDialog();
                                ToastUtils.showToast(msg);
                            }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHeadLoader != null) {
            mHeadLoader.CancleNet(IMAGE_TAG);
            mHeadLoader = null;

        }
        finish();
    }


}
