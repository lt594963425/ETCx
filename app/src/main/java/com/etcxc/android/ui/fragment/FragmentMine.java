package com.etcxc.android.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.android.R;
import com.etcxc.android.ui.activity.LoginActivity;
import com.etcxc.android.ui.activity.PersonalInfoAvtivity;
import com.etcxc.android.ui.activity.PhoneRegistActivity;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.io.File;


/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class FragmentMine extends RxFragment implements View.OnClickListener {
    /*头像名称*/
    private static final String IMAGE_FILE_NAME = "faceImage.jpg";
    private File file;
    private Uri uri;
    /* 请求码*/
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_ALBUM = 2;
    private static final int REQUEST_CODE_CROUP_PHOTO = 3;
    private Button bt_f2_rg;
    private Button bt_login_rg;
    private ImageView userHead;
    private TextView username;
    private FrameLayout mMinewLauout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fargment_mine, null);
        mMinewLauout    = (FrameLayout) view.findViewById(R.id.mine_layout);
        userHead = (ImageView) view.findViewById(R.id.userhead);
        username = (TextView) view.findViewById(R.id.username);
        bt_f2_rg = (Button) view.findViewById(R.id.bt_f2_rg);
        bt_login_rg = (Button) view.findViewById(R.id.bt_login_rg);
        initView();
        return view;
    }

    private void initView() {
        bt_f2_rg.setOnClickListener(this);
        userHead.setOnClickListener(this);
        mMinewLauout.setOnClickListener(this);
        bt_login_rg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_layout:  //用户信息展示页面
                Intent intent1 = new Intent(getActivity(), PersonalInfoAvtivity.class);
                startActivity(intent1);
                break;
            case R.id.bt_f2_rg:
                Intent intent2 = new Intent(getActivity(), PhoneRegistActivity.class);
                startActivity(intent2);
                break;
            case R.id.bt_login_rg:
                Intent intent3 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent3);
            case R.id.userhead: //头像

                break;
            case R.id.username:

                break;
        }
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
}
