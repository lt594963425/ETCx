package com.etcxc.android.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.OkHttpUtils;
import com.etcxc.android.ui.view.xccamera.CameraConfig;
import com.etcxc.android.ui.view.xccamera.CropActivity;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
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
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.etcxc.android.net.FUNC.UPLOAD_FUNC;
import static com.etcxc.android.net.NetConfig.HOST;
import static com.etcxc.android.utils.FileUtils.getCachePath;
import static com.etcxc.android.utils.FileUtils.getImageDegree;
import static com.etcxc.android.utils.FileUtils.rotateBitmapByDegree;
import static com.etcxc.android.utils.UIUtils.openAnimator;

/**
 * 上传证件信息
 * fixme:涉及到图片文件操作，有优化空间
 * 删除拍照图片或者放到外部目录共享
 * FileProvider无法获取外置SD卡问题解决方案 | Failed to find configured root that contains
 * 但路径都为内部路径要好处理点
 * Created by xwpeng on 2017/6/20.
 */

public class UploadLicenseActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = UploadLicenseActivity.class.getSimpleName();
    private final static int REQUEST_CAMERA = 1;
    private final static int REQUEST_ALBUM = 2;
    private boolean mIsOrg;//是组织用户吗？
    private ImageView mFristImageView, mFristCamera, mSecondImageView, mSecondCamera, mDriveImageView, mDriveCamera;
    private int mClickFlag;
    private Uri uri;
    private final static String IMAGE_IDCARD = "idcard.png";
    private final static String IMAGE_ORG = "org_license.png";
    private final static String IMAGE_DRIVEN = "driven_license.png";

    public final static String CROP_IDCARD = "crop_idcard.png";
    private final static String CROP_ORG = "crop_org_license.png";
    private final static String CROP_DRIVEN = "crop_driven_license.png";
    private String mCachePath;
    private final static int CLICK_IDCARD = 1;
    private final static int CLICK_ORG = 2;
    private final static int CLICK_DRIVEN = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_license);
        mCachePath = getCachePath(this);
        initData();
        initView();
        setListener();
    }

    private void initData() {
        Intent intent = getIntent();
        mIsOrg = intent.getBooleanExtra("isOrg", false);
    }

    private void initView() {
        setTitle(R.string.upload_license);
        ((TextView) find(R.id.upload_license_hint_textview)).setText(mIsOrg ? R.string.org_upload_license_hint : R.string.person_upload_license_hint);
        mFristImageView = find(R.id.fisrt_license_imageview);
        mFristCamera = find(R.id.first_camera_imageview);
        mSecondImageView = find(R.id.second_license_imageview);
        mSecondCamera = find(R.id.second_camera_imageview);
        mDriveImageView = find(R.id.drive_license_imageview);
        mDriveCamera = find(R.id.drive_license_camera_image);
        if (mIsOrg) {
            mFristImageView.setScaleType(ImageView.ScaleType.CENTER);
            mFristImageView.setImageResource(R.mipmap.ic_org_license);
            find(R.id.second_license_layout).setVisibility(View.VISIBLE);
        }
        showOldImage();
    }

    /**
     * 显示以前选择的图片
     * fixme:加载太卡，考虑延迟加载
     */
    private void showOldImage() {
        if (cropExists(CROP_DRIVEN))
            setImageFromUri(mDriveImageView, Uri.fromFile(new File(mCachePath + File.separator + CROP_DRIVEN)));
        if (cropExists(CROP_ORG))
            setImageFromUri(mFristImageView, Uri.fromFile(new File(mCachePath + File.separator + CROP_ORG)));
        if (cropExists(CROP_IDCARD))
            setImageFromUri(mIsOrg ? mSecondImageView : mFristImageView, Uri.fromFile(new File(mCachePath + File.separator + CROP_IDCARD)));
    }

    private void setListener() {
        mFristImageView.setOnClickListener(this);
        mFristCamera.setOnClickListener(this);
        if (mIsOrg) {
            mSecondImageView.setOnClickListener(this);
            mSecondCamera.setOnClickListener(this);
        }
        mDriveImageView.setOnClickListener(this);
        mDriveCamera.setOnClickListener(this);
        find(R.id.commit_button).setOnClickListener(this);
    }

    private boolean cropExists(String cropName) {
        File file = new File(mCachePath, cropName);
        return file.exists() && file.length() > 0;
    }

    private void upload() {
        showProgressDialog(getString(R.string.uploading));
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                File idCardFile = new File(mCachePath, CROP_IDCARD);
                File drivenFile = new File(mCachePath, CROP_DRIVEN);
                Map<String, String> params = new HashMap<>();
                params.put("licensePlate", PublicSPUtil.getInstance().getString("carCard", ""));
                params.put("plateColor", PublicSPUtil.getInstance().getString("carCardColor", ""));
                if (mIsOrg) {
                    File orgFile = new File(mCachePath, CROP_ORG);
                    e.onNext(OkHttpUtils.post()
                            .addFile("image_id_card", CROP_IDCARD, idCardFile)//参数 1
                            .addFile("image_driven_license", CROP_DRIVEN, drivenFile)//参数2
                            .addFile("image_org_license", CROP_ORG, orgFile)  //参数3
                            .url(HOST + UPLOAD_FUNC)
                            .params(params)
                            .build().execute().body().string());
                } else {
                    e.onNext(OkHttpUtils.post()
                            .addFile("image_id_card", CROP_IDCARD, idCardFile)//参数 1
                            .addFile("image_driven_license", CROP_DRIVEN, drivenFile)//参数 2
                            .url(HOST + UPLOAD_FUNC)
                            .params(params)
                            .build().execute().body().string());
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        Log.e(TAG, s);
                        closeProgressDialog();
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");
                        if ("s_ok".equals(code)) {
                            openActivity(ContactPhoneActivity.class);
                        } else if ("error".equals(code)) {
                            String message = jsonObject.getString("message");
                            ToastUtils.showToast(message);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        closeProgressDialog();
                        ToastUtils.showToast(R.string.request_failed);
                    }
                });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                boolean drivenOK = cropExists(CROP_DRIVEN);
                boolean orgOK = cropExists(CROP_ORG);
                boolean idCradOk = cropExists(CROP_IDCARD);
                if (drivenOK && (mIsOrg ? idCradOk && orgOK : idCradOk)) {
                    upload();
                } else {
                    int toastStr = drivenOK
                            ? idCradOk ? R.string.please_upload_org_license : R.string.please_upload_idcard
                            : R.string.please_upload_driven_license;
                    ToastUtils.showToast(toastStr);
                }
                break;
            case R.id.first_camera_imageview:
                mClickFlag = mIsOrg ? CLICK_ORG : CLICK_IDCARD;
                showDialog();
                break;
            case R.id.second_camera_imageview:
                mClickFlag = CLICK_IDCARD;
                showDialog();
                break;
            case R.id.drive_license_camera_image:
                mClickFlag = CLICK_DRIVEN;
                showDialog();
                break;
            case R.id.fisrt_license_imageview:
                String name = mIsOrg ? CROP_ORG : CROP_IDCARD;
                previewLargeImage(name);
                break;
            case R.id.second_license_imageview:
                previewLargeImage(CROP_IDCARD);
                break;
            case R.id.drive_license_imageview:
                previewLargeImage(CROP_DRIVEN);
                break;
        }
    }

    private void previewLargeImage(String fileName) {
        if (cropExists(fileName)) {
            Intent i = new Intent(this, LargeImageActivity.class);
            i.putExtra("path", mCachePath + File.separator + fileName);
            startActivity(i);
            openAnimator(this);
        }
    }

    private void showDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_get_photo, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final Dialog dialog = builder.show();
        view.findViewById(R.id.get_photo_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.get_photo_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlbum();
                dialog.dismiss();
            }
        });
    }

    protected void startAlbum() {
        PermissionUtil.requestPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionUtil.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) startAlbum2();
            }
        });
    }

    private void startAlbum2() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_ALBUM);
    }

    private void startCamera() {
        if (!SystemUtil.hasCamera()) {
            ToastUtils.showToast(getString(R.string.camera_not_found));
            return;
        }
        PermissionUtil.requestPermissions(this, Manifest.permission.CAMERA, new PermissionUtil.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    takePhoto();
//                    startCamera2();
            }
        });
    }

    private void startCamera2() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(mCachePath, (mClickFlag & CLICK_IDCARD) != 0 ? IMAGE_IDCARD : (mClickFlag & CLICK_ORG) != 0 ? IMAGE_ORG : IMAGE_DRIVEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(App.get(), BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//这个权限要添加
        } else uri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    public void takePhoto() {
        Intent intent = new Intent(UploadLicenseActivity.this, CropActivity.class);
        intent.putExtra(CameraConfig.RATIO_WIDTH, 855);
        intent.putExtra(CameraConfig.RATIO_HEIGHT, 541);
        intent.putExtra(CameraConfig.PERCENT_WIDTH, 0.6f);
        intent.putExtra(CameraConfig.MASK_COLOR, 0x2f000000);
        intent.putExtra(CameraConfig.RECT_CORNER_COLOR, 0xff00ff00);
        intent.putExtra(CameraConfig.TEXT_COLOR, 0xffffffff);
        intent.putExtra(CameraConfig.HINT_TEXT, "请将方框对准证件拍照");
        intent.putExtra(CameraConfig.IMAGE_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/CameraCardCrop/" + System.currentTimeMillis() + ".jpg");
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    File idCardFile;
    File drivenFile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            LogUtil.e(TAG, "crop", cropError);
        }
        if (RESULT_OK != resultCode) return;
        switch (requestCode) {
            case UCrop.REQUEST_CROP:
                ImageView imageView = (mClickFlag & CLICK_DRIVEN) != 0 ? mDriveImageView : (mClickFlag & CLICK_ORG) != 0 ? mFristImageView : mIsOrg ? mSecondImageView : mFristImageView;
                setImageFromUri(imageView, UCrop.getOutput(data));
                break;
            case REQUEST_CAMERA:
                String path = data.getStringExtra(CameraConfig.IMAGE_PATH);
                if (mClickFlag == CLICK_DRIVEN) {
                    mDriveImageView.setImageURI(Uri.parse(path));
                    idCardFile = new File(path);
                } else {
                    mFristImageView.setImageURI(Uri.parse(path));
                    drivenFile = new File(path);
                }
//                File file =FileUtils.getFileByUri(this,Uri.parse(path));
//                        new File(mCachePath, (mClickFlag & CLICK_IDCARD) != 0 ? CROP_IDCARD : (mClickFlag & CLICK_ORG) != 0 ? CROP_ORG : CROP_DRIVEN);
//                if (file.exists()) file.delete();
//                boolean isFile = file.isFile();
//                if (isFile) UCrop.of(uri, Uri.fromFile(file)).start(this);
                break;
            case REQUEST_ALBUM:
                if (data == null) return;
                File file1 = new File(mCachePath, (mClickFlag & CLICK_IDCARD) != 0 ? CROP_IDCARD : (mClickFlag & CLICK_ORG) != 0 ? CROP_ORG : CROP_DRIVEN);
                if (file1.exists()) file1.delete();
                try {
                    boolean success = file1.createNewFile();
                    if (success) UCrop.of(data.getData(), Uri.fromFile(file1)).start(this);
                } catch (IOException e) {
                    LogUtil.e(TAG, "result_album", e);
                }
                break;
        }
    }

    // TODO: 2017/6/23 此处有压缩策略
    private void setImageFromUri(ImageView imageView, Uri uri) {
        if (imageView == null || uri == null) return;
        try {
            String cropName = (mClickFlag & CLICK_IDCARD) != 0 ? CROP_IDCARD : (mClickFlag & CLICK_ORG) != 0 ? CROP_ORG : CROP_DRIVEN;
            int degree = getImageDegree(mCachePath + File.separator + cropName);
            imageView.setImageBitmap(rotateBitmapByDegree(loadBitmap(uri), degree));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadBitmap(Uri uri) throws FileNotFoundException {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inSampleSize = 2;
//        MediaStore.Images.Media.getBitmap(getContentResolver(), uri)
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opt);
    }

}
