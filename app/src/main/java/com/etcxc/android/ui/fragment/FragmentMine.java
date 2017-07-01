package com.etcxc.android.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.etcxc.MeManager;
import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.base.BaseFragment;
import com.etcxc.android.ui.activity.AboutUsActivity;
import com.etcxc.android.ui.activity.LargeImageActivity;
import com.etcxc.android.ui.activity.MainActivity;
import com.etcxc.android.ui.activity.PersonalInfoAvtivity;
import com.etcxc.android.utils.FileUtils;
import com.etcxc.android.utils.PrefUtils;
import com.etcxc.android.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

import static com.etcxc.android.base.App.isLogin;
import static com.etcxc.android.utils.FileUtils.getCachePath;


/**
 * Created by 刘涛 on 2017/6/2 0002.
 */

public class FragmentMine extends BaseFragment implements View.OnClickListener {
    private static int[] image = {
            R.drawable.vd_mine_harvestaddress,
            R.drawable.vd_mine_recommendfriend,
            R.drawable.vd_mine_changepassword,
            R.drawable.vd_mine_changephone,
            R.drawable.vd_mine_networktelephone,};
    private  String[] title = {
            App.get().getString(R.string.harvestaddress),App.get().getString(R.string.recommendfriend)
            ,App.get().getString(R.string.changepassword), App.get().getString(R.string.changephone),
            App.get().getString(R.string.networktelephone)};
    private File file;
    private Uri uri;
    private ImageView userHead;
    private TextView username,mAboutUsView;
    private FrameLayout mMinewLauout;
    private Handler mHandler = new Handler();
    private MainActivity mActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity =(MainActivity) getActivity();
         return inflater.inflate(R.layout.fargment_mine, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mMinewLauout = find(R.id.mine_layout);
        userHead = find(R.id.userhead);
        username = find(R.id.username);
        mAboutUsView = find(R.id.mine_about_us);
        isLogin = MeManager.getIsLogin();
        userHead.setOnClickListener(this);
        mMinewLauout.setOnClickListener(this);
        mAboutUsView.setOnClickListener(this);
        mHandler.postDelayed(LOAD_DATA,500);
    }

    /**
     * 启动延时加载
     */
    private Runnable LOAD_DATA = new Runnable() {
        @Override
        public void run() {
            //从本地加载个人信
            if (isLogin) {
                username.setText(MeManager.getSid());
                initData();
            } else {
                username.setText(R.string.now_login);
            }

        }
    };

    /**
     * 初始数据
     */
    public void initData() {
        file = new File(getCachePath(mActivity), "user-avatar.jpg");

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(App.get(), "com.etcxc.useravatar", file);
        }
        if (file.exists()) {
            getImageToView();//初始化
        } else {
            VectorDrawableCompat drawable = VectorDrawableCompat.create(getResources(), R.drawable.vd_head2, null);
            userHead.setImageDrawable(drawable);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            mHandler.removeCallbacks(LOAD_DATA);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_layout:  //用户信息页面
                Intent intent1 = new Intent(mActivity, PersonalInfoAvtivity.class);
                startActivityForResult(intent1, 0);
                break;
            case R.id.userhead: //头像
                if (!isLogin) {
                    ToastUtils.showToast(R.string.nologin);
                    return;
                }
                String path = FileUtils.getCachePath(mActivity) + File.separator + "user-avatar.jpg";
                File file = new File(path);
                if (!file.exists()) {
                    ToastUtils.showToast(R.string.nosethead);
                    return;
                }
                PrefUtils.setBoolean(mActivity, "isScal", true);
                //加载本地图片
                Intent intent4 = new Intent(mActivity, LargeImageActivity.class);
                intent4.putExtra("path", FileUtils.getCachePath(mActivity) + File.separator + "user-avatar.jpg");
                startActivity(intent4);
                mActivity.overridePendingTransition(R.anim.zoom_enter, R.anim.anim_out);
                break;
            case R.id.username:
                break;
            case R.id.mine_about_us:
                startActivity(new Intent(mActivity, AboutUsActivity.class));
                break;
        }

    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param uri
     */
    private Bitmap userBitmap;

    private void getImageToView() {
        //加载本地图片
        final File cover = FileUtils.getSmallBitmap(mActivity, file.getPath());
        Uri uri = Uri.fromFile(cover);
        try {
            userBitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (userBitmap != null) {
            userHead.setImageBitmap(FileUtils.toRoundBitmap(userBitmap));
        }
    }

    /**
     * 回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                mHandler.postDelayed(LOAD_DATA, 200);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
