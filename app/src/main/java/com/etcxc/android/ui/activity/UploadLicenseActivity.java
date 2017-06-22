package com.etcxc.android.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.CropUtils;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.UIUtils;

import java.io.File;
import java.io.IOException;

/**
 * 上传证件信息
 * Created by xwpeng on 2017/6/20.
 */

public class UploadLicenseActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = UploadLicenseActivity.class.getSimpleName();
    private boolean mIsOrg = true;//是组织用户吗？
    private TextView mUploadHintTextView;
    private ImageView mFristImageView, mFristCamera, mSecondImageView, mSecondCamera, mDriveImageView, mDriveCamera;
    private int selectedFlag;
    private final static int REQUEST_CAMERA = 1;
    private final static int REQUEST_ALBUM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_license);
        initView();
        setListener();
    }

    private void initView() {
        setTitle(R.string.upload_license);
        mUploadHintTextView = find(R.id.upload_license_hint_textview);
        mUploadHintTextView.setText(mIsOrg ? R.string.org_upload_license_hint : R.string.person_upload_license_hint);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit_button:
                startActivity(new Intent(this, ContactPhoneActivity.class));
                break;
            case R.id.first_camera_imageview:
                showDialog();
                break;
            case R.id.second_camera_imageview:
                showDialog();
                break;
            case R.id.drive_license_camera_image:
                showDialog();
                break;
            case R.id.fisrt_license_imageview:
                break;
            case R.id.second_license_imageview:
                break;
            case R.id.drive_license_imageview:
                break;
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
        PermissionUtil.requestPermissions(this, Manifest.permission.CAMERA, new PermissionUtil.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) startCamera2();

            }
        });
    }

    private Uri uri;

    private void startCamera2() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(FileUtils.getCachePath(this), FileUtils.getTempPictureFileName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//这个权限要添加
        } else uri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) return;
        if (requestCode == REQUEST_CAMERA) {
            setImageFromUri(mDriveImageView, uri);
        } else if (requestCode == REQUEST_ALBUM && data != null) {
            Uri newUri = Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                    ? Uri.parse("file:///" + CropUtils.getPath(this, data.getData()))
                    : data.getData();
            setImageFromUri(mFristImageView, newUri);
        }
    }

    private void setImageFromUri(ImageView imageView, Uri uri) {
        if (imageView == null || uri == null) return;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
