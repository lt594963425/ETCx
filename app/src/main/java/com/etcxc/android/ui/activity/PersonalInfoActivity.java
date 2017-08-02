package com.etcxc.android.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.base.Constants;
import com.etcxc.android.bean.MessageEvent;
import com.etcxc.android.ui.view.GlideCircleTransform;
import com.etcxc.android.utils.CropUtils;
import com.etcxc.android.utils.DialogPermission;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.SharedPreferenceMark;
import com.etcxc.android.utils.ToastUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import static com.etcxc.MeManager.setIsLgon;
import static com.etcxc.android.base.App.isLogin;

/**
 * 个人信息界面（通过登录界面拆分）
 * Created by caoyu on 2017/8/2
 */
public class PersonalInfoActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener,View.OnClickListener{

    // 用户信息操作界面
    private ImageView mPersonHead;
    private TextView mPersonName;
    /*头像名称*/
    private File mFile;
    private Uri uri;
    /* 请求码*/
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_ALBUM = 2;
    private static final int REQUEST_CODE_CROUP_PHOTO = 3;
    private Toolbar mToolbar2;
    private Button mExitLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalinfo);
        EventBus.getDefault().register(this);
        initUserInfoView();
    }

    private void initUserInfoView() {
        //登录之后显示的页面info_page
        mToolbar2 = (Toolbar) findViewById(R.id.person_toolbar);
        setSupportActionBar(mToolbar2);
        mToolbar2.setTitle(R.string.personinfo);
        mToolbar2.inflateMenu(R.menu.menu);
        setBarBack(mToolbar2);
        mPersonHead = (ImageView) findViewById(R.id.person_userhead);
        mPersonName = (TextView) findViewById(R.id.person_username);
        mExitLogin = (Button) findViewById(R.id.exit_login_btn);
        mPersonHead.setOnClickListener(this);
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
        isLogin = MeManager.getIsLogin();
        if (isLogin) {
            String name = MeManager.getUid();
            mPersonName.setText(name);
            //适配7.0以上和以下的手机
            mFile = new File(FileUtils.getCachePath(this), "user-avatar.jpg");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                uri = Uri.fromFile(mFile);
            } else {
                //通过FileProvider创建一个content类型的Uri(android 7.0需要这样的方法跨应用访问)
                uri = FileProvider.getUriForFile(App.get(), "com.etcxc.useravatar", mFile);
            }
            if (mFile.exists()) {
                getImageToView();//初始化
            }
        } else {
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
            mPersonHead.setImageDrawable(drawable);
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
                if (PermissionUtil.hasCameraPermission(PersonalInfoActivity.this)) {
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
                Toast.makeText(this, "没有得到相册图片", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {//相机
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
            Intent intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent2.setData(uri);
            this.sendBroadcast(intent2);
        }
    }

    /**
     * 裁剪拍照
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Uri newuri = Uri.fromFile(mFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("output", newuri);
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
        final File cover = FileUtils.getSmallBitmap(mFile.getPath());
        Uri uri = Uri.fromFile(cover);
        Log.e(TAG, "#############################路径uri：" + uri);
        //通知相册更新
        // File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //   Uri uri = Uri.fromFile(file);
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
        switch (v.getId()){
            case R.id.person_userhead://头像
                if (!isLogin) {
                    ToastUtils.showToast(R.string.nologin);
                    return;
                }
                show2Dialog();
                break;
            case R.id.exit_login_btn://退出登录
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
}
