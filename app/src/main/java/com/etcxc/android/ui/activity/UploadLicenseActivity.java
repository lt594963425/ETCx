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
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etcxc.android.BuildConfig;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.modle.sp.PublicSPUtil;
import com.etcxc.android.net.NetConfig;
import com.etcxc.android.net.upload.UploadTask;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.RxUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;
import com.etcxc.android.utils.UIUtils;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static com.etcxc.android.utils.FileUtils.getImageDegree;
import static com.etcxc.android.utils.FileUtils.rotateBitmapByDegree;

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
    private final static String IMAGENAME_IDCARD = "idcard.png";
    private final static String IMAGENAME_ORG = "org_license.png";
    private final static String IMAGENAME_DRIVEN = "driven_license.png";
    private final static String CROPENAME_IDCARD = "crop_idcard.png";
    private final static String CROPENAME_ORG = "crop_org_license.png";
    private final static String CROPENAME_DRIVEN = "crop_driven_license.png";
    private String mCachePath;
    private final static int CLICK_IDCARD = 1;
    private final static int CLICK_ORG = 2;
    private final static int CLICK_DRIVEN = 4;
    private final static String FUNC = "/transaction/transaction/upload";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_license);
        mCachePath = FileUtils.getCachePath(this);
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
            RelativeLayout fristLicenseLayout = find(R.id.first_license_layout);
            ViewGroup.LayoutParams params = fristLicenseLayout.getLayoutParams();
            params.width = UIUtils.dip2Px(152);
            params.height = UIUtils.dip2Px(217);
            fristLicenseLayout.setLayoutParams(params);
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
        if (cropExists(CROPENAME_DRIVEN))
            setImageFromUri(mDriveImageView, Uri.fromFile(new File(mCachePath + File.separator + CROPENAME_DRIVEN)));
        if (cropExists(CROPENAME_ORG))
            setImageFromUri(mFristImageView, Uri.fromFile(new File(mCachePath + File.separator + CROPENAME_ORG)));
        if (cropExists(CROPENAME_IDCARD))
            setImageFromUri(mIsOrg ? mSecondImageView : mFristImageView, Uri.fromFile(new File(mCachePath + File.separator + CROPENAME_IDCARD)));
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
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                UploadTask u = new UploadTask();
          /*      e.onNext(u.doUpload(UPLOAD_PATH, new File(mCachePath, CROPENAME_IDCARD), ""));
                e.onNext(u.doUpload(UPLOAD_PATH, new File(mCachePath, CROPENAME_DRIVEN), ""));
                if (mIsOrg)
                    e.onNext(u.doUpload(UPLOAD_PATH, new File(mCachePath, CROPENAME_ORG), ""));
            }*/
                List<File> files = new ArrayList<>();
                files.add(new File(mCachePath, CROPENAME_IDCARD));
                files.add(new File(mCachePath, CROPENAME_DRIVEN));
                if (mIsOrg) files.add(new File(mCachePath, CROPENAME_ORG));
                StringBuilder urlBuilder = new StringBuilder(NetConfig.HOST).append(FUNC)
                        .append(File.separator).append("veh_code").append(File.separator).append(PublicSPUtil.getInstance().getString("carCard", ""))
                        .append(File.separator).append("veh_code_colour").append(File.separator).append(PublicSPUtil.getInstance().getString("carCardColor", ""));
                e.onNext(UploadTask.getUploadCall(urlBuilder.toString(), "", files).execute().body().string());
            }
        }).compose(RxUtil.activityLifecycle(this))
                .compose(RxUtil.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String s) throws Exception {
                        JSONObject jsonObject = new JSONObject(s);
                        String code = jsonObject.getString("code");
                        if ("s_ok".equals(code)) startActivity(new Intent(UploadLicenseActivity.this, ContactPhoneActivity.class));
                        else ToastUtils.showToast(R.string.request_failed);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "upload", throwable);
                        ToastUtils.showToast(R.string.request_failed);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                boolean drivenOK = cropExists(CROPENAME_DRIVEN);
                boolean orgOK = cropExists(CROPENAME_ORG);
                boolean idCradOk = cropExists(CROPENAME_IDCARD);
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
                String name = mIsOrg ? CROPENAME_ORG : CROPENAME_IDCARD;
                previewLargeImage(name);
                break;
            case R.id.second_license_imageview:
                previewLargeImage(CROPENAME_IDCARD);
                break;
            case R.id.drive_license_imageview:
                previewLargeImage(CROPENAME_DRIVEN);
                break;
        }
    }

    private void previewLargeImage(String fileName) {
        if (cropExists(fileName)) {
            Intent i = new Intent(this, LargeImageActivity.class);
            i.putExtra("path", mCachePath + File.separator + fileName);
            startActivity(i);
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
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) startCamera2();
            }
        });
    }

    private void startCamera2() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(mCachePath, (mClickFlag & CLICK_IDCARD) != 0 ? IMAGENAME_IDCARD : (mClickFlag & CLICK_ORG) != 0 ? IMAGENAME_ORG : IMAGENAME_DRIVEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(App.get(), BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//这个权限要添加
        } else uri = Uri.fromFile(imageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

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
                ImageView imageView = (mClickFlag & CLICK_DRIVEN) != 0
                        ? mDriveImageView
                        : (mClickFlag & CLICK_ORG) != 0 ? mFristImageView
                        : mIsOrg ? mSecondImageView : mFristImageView;
                setImageFromUri(imageView, UCrop.getOutput(data));
                break;
            case REQUEST_CAMERA:
                File file = new File(mCachePath, (mClickFlag & CLICK_IDCARD) != 0 ? CROPENAME_IDCARD : (mClickFlag & CLICK_ORG) != 0 ? CROPENAME_ORG : CROPENAME_DRIVEN);
                if (file.exists()) file.delete();
                try {
                    boolean success = file.createNewFile();
                    if (success) UCrop.of(uri, Uri.fromFile(file)).start(this);
                } catch (IOException e) {
                    LogUtil.e(TAG, "result_camera", e);
                }
                break;
            case REQUEST_ALBUM:
                if (data == null) return;
                File file1 = new File(mCachePath, (mClickFlag & CLICK_IDCARD) != 0 ? CROPENAME_IDCARD : (mClickFlag & CLICK_ORG) != 0 ? CROPENAME_ORG : CROPENAME_DRIVEN);
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
            String cropName = (mClickFlag & CLICK_IDCARD) != 0 ? CROPENAME_IDCARD : (mClickFlag & CLICK_ORG) != 0 ? CROPENAME_ORG : CROPENAME_DRIVEN;
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
